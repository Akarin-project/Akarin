package io.akarin.server.mixin.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spigotmc.SpigotConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Lists;
import io.akarin.api.LocalAddress;
import io.akarin.api.WrappedCollections;
import io.akarin.server.core.AkarinGlobalConfig;
import io.akarin.server.core.ChannelAdapter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.server.ChatComponentText;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.PacketPlayOutKickDisconnect;
import net.minecraft.server.ServerConnection;

@Mixin(value = ServerConnection.class, remap = false)
public class NonblockingServerConnection {
    private final static Logger logger = LogManager.getLogger("NSC");
    
    /**
     * Contains all endpoints added to this NetworkSystem
     */
    @Shadow @Mutable @Final private List<ChannelFuture> g;
    /**
     * A list containing all NetworkManager instances of all endpoints
     */
    @Shadow @Mutable @Final private List<NetworkManager> h;
    
    private final List<NetworkManager> pending = WrappedCollections.wrappedList(Collections.synchronizedList(Lists.newLinkedList()));
    
    @Overwrite
    private void addPending() {} // just keep compatibility
    
    /**
     * Removes all pending endpoints from global NetworkManager list
     */
    private void removePending() {
        synchronized (pending) {
            h.removeAll(pending);
            pending.clear();
        }
    }
    
    @Shadow @Final private MinecraftServer f;
    
    /**
     * Adds channels (endpoint) that listens on publicly accessible network ports
     */
    @Overwrite
    public void a(InetAddress address, int port) throws IOException {
        registerChannels(Lists.newArrayList(LocalAddress.create(address, port)));
    }
    
    private boolean needDeployList = true;
    
    public void registerChannels(Collection<LocalAddress> data) throws IOException {
        Class<? extends ServerChannel> channelClass;
        EventLoopGroup loopGroup;
        
        if (Epoll.isAvailable() && this.f.af()) { // PAIL: MinecraftServer::useNativeTransport
            channelClass = EpollServerSocketChannel.class;
            loopGroup = ServerConnection.b.c();
            logger.info("Using epoll channel type");
        } else {
            channelClass = NioServerSocketChannel.class;
            loopGroup = ServerConnection.a.c();
            logger.info("Using nio channel type");
        }
        
        // Since we cannot overwrite the initializer, here is the best chance to handle it
        if (needDeployList) {
            h = WrappedCollections.wrappedList(Lists.newCopyOnWriteArrayList());
            needDeployList = false;
        }
        
        ServerBootstrap bootstrap = new ServerBootstrap().channel(channelClass).childHandler(ChannelAdapter.create(h)).group(loopGroup);
        synchronized (g) {
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
            data.forEach(address -> g.add(bootstrap.localAddress(address.host(), address.port()).bind().syncUninterruptibly())); // supports multi-port bind
        }
    }
    
    @Shadow public volatile boolean d; // PAIL: neverTerminate
    /**
     * Shuts down all open endpoints
     */
    public void b() {
        this.d = false;
        try {
            synchronized (g) { // safe fixes
                for (ChannelFuture channel : g) channel.channel().close().sync();
            }
        } catch (InterruptedException ex) {
            logger.error("Interrupted whilst closing channel");
        }
    }
    
    /**
     * Will try to process the packets received by each NetworkManager, gracefully manage processing failures and cleans up dead connections (tick)
     */
    @Overwrite
    public void c() {
        // Spigot - This prevents players from 'gaming' the server, and strategically relogging to increase their position in the tick order
        if (SpigotConfig.playerShuffle > 0 && MinecraftServer.currentTick % SpigotConfig.playerShuffle == 0) {
            Collections.shuffle(h);
        }
        boolean needRemoval = false;
        
        for (NetworkManager manager : h) {
            if (manager.h()) continue; // PAIL: NetworkManager::hasNoChannel
            
            if (manager.isConnected()) {
                try {
                    manager.a(); // PAIL: NetworkManager::processReceivedPackets
                } catch (Exception ex) {
                    logger.warn("Failed to handle packet for {}", new Object[] { manager.getSocketAddress(), ex });
                    final ChatComponentText kick = new ChatComponentText("Internal server error");
                    
                    manager.sendPacket(new PacketPlayOutKickDisconnect(kick), new GenericFutureListener<Future<? super Void>>() {
                        @Override
                        public void operationComplete(Future<? super Void> future) throws Exception {
                            manager.close(kick);
                        }
                    }, new GenericFutureListener[0]);
                    manager.stopReading();
                }
            } else {
                // Spigot - Fix a race condition where a NetworkManager could be unregistered just before connection.
                if (manager.preparing) continue;
                
                needRemoval = true;
                synchronized (pending) {
                    pending.add(manager);
                }
                
                manager.handleDisconnection();
            }
        }
        
        if (needRemoval) removePending();
    }
}

