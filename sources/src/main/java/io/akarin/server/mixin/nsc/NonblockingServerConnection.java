package io.akarin.server.mixin.nsc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spigotmc.SpigotConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import com.google.common.collect.Lists;

import io.akarin.api.internal.LocalAddress;
import io.akarin.server.core.AkarinGlobalConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.server.ChatComponentText;
import net.minecraft.server.EnumProtocolDirection;
import net.minecraft.server.HandshakeListener;
import net.minecraft.server.LegacyPingHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.PacketDecoder;
import net.minecraft.server.PacketEncoder;
import net.minecraft.server.PacketPlayOutKickDisconnect;
import net.minecraft.server.PacketPrepender;
import net.minecraft.server.PacketSplitter;
import net.minecraft.server.ServerConnection;

@Mixin(value = ServerConnection.class, remap = false)
public abstract class NonblockingServerConnection {
    private final static Logger logger = LogManager.getLogger("NSC");
    
    /**
     * Contains all endpoints added to this NetworkSystem
     */
    @Shadow(aliases = "g") @Mutable @Final private List<ChannelFuture> endPoints;
    /**
     * A list containing all NetworkManager instances of all endpoints
     */
    @Shadow(aliases = "h") @Mutable @Final private List<NetworkManager> networkManagers;
    
    @Overwrite
    private void addPending() {} // just keep compatibility
    
    @Shadow(aliases = "f") @Final private MinecraftServer server;
    
    /**
     * Adds channels (endpoint) that listens on publicly accessible network ports
     */
    @Overwrite
    public void a(InetAddress address, int port) throws IOException {
        registerChannels(Lists.newArrayList(LocalAddress.create(address, port)));
    }
    
    public void registerChannels(Collection<LocalAddress> data) throws IOException {
        Class<? extends ServerChannel> channelClass;
        EventLoopGroup loopGroup;
        
        if (Epoll.isAvailable() && this.server.af()) { // OBFHELPER: MinecraftServer::useNativeTransport
            channelClass = EpollServerSocketChannel.class;
            loopGroup = ServerConnection.b.c();
            logger.info("Using epoll channel type");
        } else {
            channelClass = NioServerSocketChannel.class;
            loopGroup = ServerConnection.a.c();
            logger.info("Using nio channel type");
        }
        
        ServerBootstrap bootstrap = new ServerBootstrap().channel(channelClass).childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException ex) {
                    ;
                }
                channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30))
                                  .addLast("legacy_query", new LegacyPingHandler(MinecraftServer.getServer().getServerConnection()))
                                  .addLast("splitter", new PacketSplitter()).addLast("decoder", new PacketDecoder(EnumProtocolDirection.SERVERBOUND))
                                  .addLast("prepender", new PacketPrepender()).addLast("encoder", new PacketEncoder(EnumProtocolDirection.CLIENTBOUND));
                
                NetworkManager manager = new NetworkManager(EnumProtocolDirection.SERVERBOUND);
                networkManagers.add(manager);
                
                channel.pipeline().addLast("packet_handler", manager);
                manager.setPacketListener(new HandshakeListener(MinecraftServer.getServer(), manager));
            }
        }).group(loopGroup);
        synchronized (endPoints) {
            data.addAll(Lists.transform(AkarinGlobalConfig.extraAddress, s -> {
                String[] info = s.split(":");
                try {
                    logger.info("Attempt to bind server on " + s);
                    return LocalAddress.create(InetAddress.getByName(info[0]), Integer.valueOf(info[1]));
                } catch (NumberFormatException | UnknownHostException ex) {
                    logger.error("Error on lookup additional host, wrong format?", ex);
                    return null;
                }
            }));
            data.forEach(address -> endPoints.add(bootstrap.localAddress(address.host(), address.port()).bind().syncUninterruptibly())); // supports multi-port bind
        }
    }
    
    @Shadow public volatile boolean d; // OBFHELPER: neverTerminate
    /**
     * Shuts down all open endpoints
     */
    public void b() {
        this.d = false;
        try {
            synchronized (endPoints) { // safe fixes
                for (ChannelFuture channel : endPoints) channel.channel().close().sync();
            }
        } catch (InterruptedException ex) {
            logger.error("Interrupted whilst closing channel");
        }
    }
    
    public void processPackets(NetworkManager manager) {
        try {
            manager.a(); // OBFHELPER: NetworkManager::processReceivedPackets
        } catch (Exception ex) {
            logger.warn("Failed to handle packet for {}", manager.getSocketAddress(), ex);
            final ChatComponentText message = new ChatComponentText("Internal server error");
            
            manager.sendPacket(new PacketPlayOutKickDisconnect(message), new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    manager.close(message);
                }
            }, new GenericFutureListener[0]);
            manager.stopReading();
        }
    }
    
    /**
     * Will try to process the packets received by each NetworkManager, gracefully manage processing failures and cleans up dead connections (tick)
     */
    @Overwrite
    public void c() throws InterruptedException {
        synchronized (networkManagers) {
            // Spigot - This prevents players from 'gaming' the server, and strategically relogging to increase their position in the tick order
            if (SpigotConfig.playerShuffle > 0 && MinecraftServer.currentTick % SpigotConfig.playerShuffle == 0) {
                Collections.shuffle(networkManagers);
            }
            
            Iterator<NetworkManager> it = networkManagers.iterator();
            while (it.hasNext()) {
                NetworkManager manager = it.next();
                if (manager.h()) continue; // OBFHELPER: NetworkManager::hasNoChannel
                
                if (manager.isConnected()) {
                    processPackets(manager);
                } else {
                    // Spigot - Fix a race condition where a NetworkManager could be unregistered just before connection.
                    if (manager.preparing) continue;
                    
                    it.remove();
                    manager.handleDisconnection();
                }
            }
        }
    }
}

