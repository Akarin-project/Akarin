package net.minecraft.server;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NetworkManager extends SimpleChannelInboundHandler<Packet<?>> {

    private static final Logger g = LogManager.getLogger();
    public static final Marker a = MarkerManager.getMarker("NETWORK");
    public static final Marker b = MarkerManager.getMarker("NETWORK_PACKETS", NetworkManager.a);
    public static final AttributeKey<EnumProtocol> c = AttributeKey.valueOf("protocol");
    public static final LazyInitVar<NioEventLoopGroup> d = new LazyInitVar<>(() -> {
        return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
    });
    public static final LazyInitVar<EpollEventLoopGroup> e = new LazyInitVar<>(() -> {
        return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
    });
    public static final LazyInitVar<DefaultEventLoopGroup> f = new LazyInitVar<>(() -> {
        return new DefaultEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
    });
    private final EnumProtocolDirection h;
    private final Queue<NetworkManager.QueuedPacket> packetQueue = Queues.newConcurrentLinkedQueue();
    private final ReentrantReadWriteLock j = new ReentrantReadWriteLock();
    public Channel channel;
    public SocketAddress socketAddress;
    private PacketListener packetListener;
    private IChatBaseComponent n;
    private boolean o;
    private boolean p;
    private int q;
    private int r;
    private float s;
    private float t;
    private int u;
    private boolean v;

    public NetworkManager(EnumProtocolDirection enumprotocoldirection) {
        this.h = enumprotocoldirection;
    }

    public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {
        super.channelActive(channelhandlercontext);
        this.channel = channelhandlercontext.channel();
        this.socketAddress = this.channel.remoteAddress();

        try {
            this.setProtocol(EnumProtocol.HANDSHAKING);
        } catch (Throwable throwable) {
            NetworkManager.g.fatal(throwable);
        }

    }

    public void setProtocol(EnumProtocol enumprotocol) {
        this.channel.attr(NetworkManager.c).set(enumprotocol);
        this.channel.config().setAutoRead(true);
        NetworkManager.g.debug("Enabled auto read");
    }

    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.close(new ChatMessage("disconnect.endOfStream", new Object[0]));
    }

    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) {
        if (throwable instanceof SkipEncodeException) {
            NetworkManager.g.debug("Skipping packet due to errors", throwable.getCause());
        } else {
            boolean flag = !this.v;

            this.v = true;
            if (this.channel.isOpen()) {
                if (throwable instanceof TimeoutException) {
                    NetworkManager.g.debug("Timeout", throwable);
                    this.close(new ChatMessage("disconnect.timeout", new Object[0]));
                } else {
                    ChatMessage chatmessage = new ChatMessage("disconnect.genericReason", new Object[] { "Internal Exception: " + throwable});

                    if (flag) {
                        NetworkManager.g.debug("Failed to sent packet", throwable);
                        this.sendPacket(new PacketPlayOutKickDisconnect(chatmessage), (future) -> {
                            this.close(chatmessage);
                        });
                        this.stopReading();
                    } else {
                        NetworkManager.g.debug("Double fault", throwable);
                        this.close(chatmessage);
                    }
                }

            }
        }
    }

    protected void channelRead0(ChannelHandlerContext channelhandlercontext, Packet<?> packet) throws Exception {
        if (this.channel.isOpen()) {
            try {
                a(packet, this.packetListener);
            } catch (CancelledPacketHandleException cancelledpackethandleexception) {
                ;
            }

            ++this.q;
        }

    }

    private static <T extends PacketListener> void a(Packet<T> packet, PacketListener packetlistener) {
        packet.a(packetlistener);
    }

    public void setPacketListener(PacketListener packetlistener) {
        Validate.notNull(packetlistener, "packetListener", new Object[0]);
        NetworkManager.g.debug("Set listener of {} to {}", this, packetlistener);
        this.packetListener = packetlistener;
    }

    public void sendPacket(Packet<?> packet) {
        this.sendPacket(packet, (GenericFutureListener) null);
    }

    public void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
        if (this.isConnected()) {
            this.o();
            this.b(packet, genericfuturelistener);
        } else {
            this.j.writeLock().lock();

            try {
                this.packetQueue.add(new NetworkManager.QueuedPacket(packet, genericfuturelistener));
            } finally {
                this.j.writeLock().unlock();
            }
        }

    }

    private void b(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
        EnumProtocol enumprotocol = EnumProtocol.a(packet);
        EnumProtocol enumprotocol1 = (EnumProtocol) this.channel.attr(NetworkManager.c).get();

        ++this.r;
        if (enumprotocol1 != enumprotocol) {
            NetworkManager.g.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }

        if (this.channel.eventLoop().inEventLoop()) {
            if (enumprotocol != enumprotocol1) {
                this.setProtocol(enumprotocol);
            }

            ChannelFuture channelfuture = this.channel.writeAndFlush(packet);

            if (genericfuturelistener != null) {
                channelfuture.addListener(genericfuturelistener);
            }

            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            this.channel.eventLoop().execute(() -> {
                if (enumprotocol != enumprotocol1) {
                    this.setProtocol(enumprotocol);
                }

                ChannelFuture channelfuture1 = this.channel.writeAndFlush(packet);

                if (genericfuturelistener != null) {
                    channelfuture1.addListener(genericfuturelistener);
                }

                channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            });
        }

    }

    private void o() {
        if (this.channel != null && this.channel.isOpen()) {
            this.j.readLock().lock();

            try {
                while (!this.packetQueue.isEmpty()) {
                    NetworkManager.QueuedPacket networkmanager_queuedpacket = (NetworkManager.QueuedPacket) this.packetQueue.poll();

                    this.b(networkmanager_queuedpacket.a, networkmanager_queuedpacket.b);
                }
            } finally {
                this.j.readLock().unlock();
            }

        }
    }

    public void a() {
        this.o();
        if (this.packetListener instanceof ITickable) {
            ((ITickable) this.packetListener).tick();
        }

        if (this.channel != null) {
            this.channel.flush();
        }

        if (this.u++ % 20 == 0) {
            this.t = this.t * 0.75F + (float) this.r * 0.25F;
            this.s = this.s * 0.75F + (float) this.q * 0.25F;
            this.r = 0;
            this.q = 0;
        }

    }

    public SocketAddress getSocketAddress() {
        return this.socketAddress;
    }

    public void close(IChatBaseComponent ichatbasecomponent) {
        if (this.channel.isOpen()) {
            this.channel.close().awaitUninterruptibly();
            this.n = ichatbasecomponent;
        }

    }

    public boolean isLocal() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    public void a(SecretKey secretkey) {
        this.o = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", new PacketDecrypter(MinecraftEncryption.a(2, secretkey)));
        this.channel.pipeline().addBefore("prepender", "encrypt", new PacketEncrypter(MinecraftEncryption.a(1, secretkey)));
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean h() {
        return this.channel == null;
    }

    public PacketListener i() {
        return this.packetListener;
    }

    @Nullable
    public IChatBaseComponent j() {
        return this.n;
    }

    public void stopReading() {
        this.channel.config().setAutoRead(false);
    }

    public void setCompressionLevel(int i) {
        if (i >= 0) {
            if (this.channel.pipeline().get("decompress") instanceof PacketDecompressor) {
                ((PacketDecompressor) this.channel.pipeline().get("decompress")).a(i);
            } else {
                this.channel.pipeline().addBefore("decoder", "decompress", new PacketDecompressor(i));
            }

            if (this.channel.pipeline().get("compress") instanceof PacketCompressor) {
                ((PacketCompressor) this.channel.pipeline().get("compress")).a(i);
            } else {
                this.channel.pipeline().addBefore("encoder", "compress", new PacketCompressor(i));
            }
        } else {
            if (this.channel.pipeline().get("decompress") instanceof PacketDecompressor) {
                this.channel.pipeline().remove("decompress");
            }

            if (this.channel.pipeline().get("compress") instanceof PacketCompressor) {
                this.channel.pipeline().remove("compress");
            }
        }

    }

    public void handleDisconnection() {
        if (this.channel != null && !this.channel.isOpen()) {
            if (this.p) {
                NetworkManager.g.warn("handleDisconnection() called twice");
            } else {
                this.p = true;
                if (this.j() != null) {
                    this.i().a(this.j());
                } else if (this.i() != null) {
                    this.i().a(new ChatMessage("multiplayer.disconnect.generic", new Object[0]));
                }
            }

        }
    }

    static class QueuedPacket {

        private final Packet<?> a;
        @Nullable
        private final GenericFutureListener<? extends Future<? super Void>> b;

        public QueuedPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
            this.a = packet;
            this.b = genericfuturelistener;
        }
    }
}
