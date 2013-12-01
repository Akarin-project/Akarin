package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerConnection {

    private static final Logger d = LogManager.getLogger();
    public static final LazyInitVar<NioEventLoopGroup> a = new LazyInitVar<>(() -> {
        return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).build());
    });
    public static final LazyInitVar<EpollEventLoopGroup> b = new LazyInitVar<>(() -> {
        return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
    });
    private final MinecraftServer e;
    public volatile boolean c;
    private final List<ChannelFuture> f = Collections.synchronizedList(Lists.newArrayList());
    private final List<NetworkManager> g = Collections.synchronizedList(Lists.newArrayList());

    public ServerConnection(MinecraftServer minecraftserver) {
        this.e = minecraftserver;
        this.c = true;
    }

    public void a(@Nullable InetAddress inetaddress, int i) throws IOException {
        List list = this.f;

        synchronized (this.f) {
            Class oclass;
            LazyInitVar lazyinitvar;

            if (Epoll.isAvailable() && this.e.V()) {
                oclass = EpollServerSocketChannel.class;
                lazyinitvar = ServerConnection.b;
                ServerConnection.d.info("Using epoll channel type");
            } else {
                oclass = NioServerSocketChannel.class;
                lazyinitvar = ServerConnection.a;
                ServerConnection.d.info("Using default channel type");
            }

            this.f.add(((ServerBootstrap) ((ServerBootstrap) (new ServerBootstrap()).channel(oclass)).childHandler(new ChannelInitializer<Channel>() {
                protected void initChannel(Channel channel) throws Exception {
                    try {
                        channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                    } catch (ChannelException channelexception) {
                        ;
                    }

                    channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("legacy_query", new LegacyPingHandler(ServerConnection.this)).addLast("splitter", new PacketSplitter()).addLast("decoder", new PacketDecoder(EnumProtocolDirection.SERVERBOUND)).addLast("prepender", new PacketPrepender()).addLast("encoder", new PacketEncoder(EnumProtocolDirection.CLIENTBOUND));
                    NetworkManager networkmanager = new NetworkManager(EnumProtocolDirection.SERVERBOUND);

                    ServerConnection.this.g.add(networkmanager);
                    channel.pipeline().addLast("packet_handler", networkmanager);
                    networkmanager.setPacketListener(new HandshakeListener(ServerConnection.this.e, networkmanager));
                }
            }).group((EventLoopGroup) lazyinitvar.a()).localAddress(inetaddress, i)).bind().syncUninterruptibly());
        }
    }

    public void b() {
        this.c = false;
        Iterator iterator = this.f.iterator();

        while (iterator.hasNext()) {
            ChannelFuture channelfuture = (ChannelFuture) iterator.next();

            try {
                channelfuture.channel().close().sync();
            } catch (InterruptedException interruptedexception) {
                ServerConnection.d.error("Interrupted whilst closing channel");
            }
        }

    }

    public void c() {
        List list = this.g;

        synchronized (this.g) {
            Iterator iterator = this.g.iterator();

            while (iterator.hasNext()) {
                NetworkManager networkmanager = (NetworkManager) iterator.next();

                if (!networkmanager.h()) {
                    if (networkmanager.isConnected()) {
                        try {
                            networkmanager.a();
                        } catch (Exception exception) {
                            if (networkmanager.isLocal()) {
                                CrashReport crashreport = CrashReport.a(exception, "Ticking memory connection");
                                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Ticking connection");

                                crashreportsystemdetails.a("Connection", networkmanager::toString);
                                throw new ReportedException(crashreport);
                            }

                            ServerConnection.d.warn("Failed to handle packet for {}", networkmanager.getSocketAddress(), exception);
                            ChatComponentText chatcomponenttext = new ChatComponentText("Internal server error");

                            networkmanager.sendPacket(new PacketPlayOutKickDisconnect(chatcomponenttext), (future) -> {
                                networkmanager.close(chatcomponenttext);
                            });
                            networkmanager.stopReading();
                        }
                    } else {
                        iterator.remove();
                        networkmanager.handleDisconnection();
                    }
                }
            }

        }
    }

    public MinecraftServer d() {
        return this.e;
    }
}
