package net.minecraft.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.googlecode.concurentlocks.ReentrantReadWriteUpdateLock;

import io.akarin.api.internal.utils.CheckedConcurrentLinkedQueue;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.SocketAddress;
import java.util.Queue;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * Akarin Changes Note
 * 2) Expose private members (nsc)
 * 3) Changes lock type to updatable lock (compatibility)
 * 4) Removes unneed array creation (performance)
 */
public class NetworkManager extends SimpleChannelInboundHandler<Packet<?>> {

    private static final Logger g = LogManager.getLogger();
    public static final Marker a = MarkerManager.getMarker("NETWORK");
    public static final Marker b = MarkerManager.getMarker("NETWORK_PACKETS", NetworkManager.a);
    public static final AttributeKey<EnumProtocol> c = AttributeKey.valueOf("protocol");
    public static final LazyInitVar<NioEventLoopGroup> d = new LazyInitVar() {
        protected NioEventLoopGroup a() {
            return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
        }

        @Override
        protected Object init() {
            return this.a();
        }
    };
    public static final LazyInitVar<EpollEventLoopGroup> e = new LazyInitVar() {
        protected EpollEventLoopGroup a() {
            return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
        }

        @Override
        protected Object init() {
            return this.a();
        }
    };
    public static final LazyInitVar<LocalEventLoopGroup> f = new LazyInitVar() {
        protected LocalEventLoopGroup a() {
            return new LocalEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
        }

        @Override
        protected Object init() {
            return this.a();
        }
    };
    private final EnumProtocolDirection h;
    private final Queue<NetworkManager.QueuedPacket> i = new CheckedConcurrentLinkedQueue<NetworkManager.QueuedPacket>(); private final Queue<NetworkManager.QueuedPacket> getPacketQueue() { return this.i; } // Paper - Anti-Xray - OBFHELPER // Akarin
    private final ReentrantReadWriteUpdateLock j = new ReentrantReadWriteUpdateLock(); // Akarin - use update lock
    public Channel channel;
    // Spigot Start // PAIL
    public SocketAddress l;
    public java.util.UUID spoofedUUID;
    public com.mojang.authlib.properties.Property[] spoofedProfile;
    public boolean preparing = true;
    // Spigot End
    private PacketListener m;
    private IChatBaseComponent n;
    private boolean o;
    private boolean p;
    // Paper start - NetworkClient implementation
    public int protocolVersion;
    public java.net.InetSocketAddress virtualHost;
    private static boolean enableExplicitFlush = Boolean.getBoolean("paper.explicit-flush");
    // Paper end

    public NetworkManager(EnumProtocolDirection enumprotocoldirection) {
        this.h = enumprotocoldirection;
    }

    @Override
    public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {
        super.channelActive(channelhandlercontext);
        this.channel = channelhandlercontext.channel();
        this.l = this.channel.remoteAddress();
        // Spigot Start
        this.preparing = false;
        // Spigot End

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

    @Override
    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.close(new ChatMessage("disconnect.endOfStream", new Object[0]));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception {
        ChatMessage chatmessage;

        if (throwable instanceof TimeoutException) {
            chatmessage = new ChatMessage("disconnect.timeout", new Object[0]);
        } else {
            chatmessage = new ChatMessage("disconnect.genericReason", new Object[] { "Internal Exception: " + throwable});
        }

        NetworkManager.g.debug(chatmessage.toPlainText(), throwable);
        this.close(chatmessage);
        if (MinecraftServer.getServer().isDebugging()) throwable.printStackTrace(); // Spigot
    }

    protected void a(ChannelHandlerContext channelhandlercontext, Packet<?> packet) throws Exception {
        if (this.channel.isOpen()) {
            try {
                ((Packet) packet).a(this.m); // CraftBukkit - decompile error
            } catch (CancelledPacketHandleException cancelledpackethandleexception) {
                ;
            }
        }

    }

    public void setPacketListener(PacketListener packetlistener) {
        Validate.notNull(packetlistener, "packetListener", new Object[0]);
        NetworkManager.g.debug("Set listener of {} to {}", this, packetlistener);
        this.m = packetlistener;
    }

    public void sendPacket(Packet<?> packet) {
        if (this.isConnected() && this.trySendQueue() && !(packet instanceof PacketPlayOutMapChunk && !((PacketPlayOutMapChunk) packet).isReady())) { // Paper - Async-Anti-Xray - Add chunk packets which are not ready or all packets if the queue contains chunk packets which are not ready to the queue and send the packets later in the right order
            //this.m(); // Paper - Async-Anti-Xray - Move to if statement (this.trySendQueue())
            this.a(packet, (GenericFutureListener[]) null);
        } else {
            this.j.writeLock().lock();

            try {
                this.i.add(new NetworkManager.QueuedPacket(packet)); // Akarin - remove fake listener creation
            } finally {
                this.j.writeLock().unlock();
            }
        }

    }

    public void sendPacket(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> genericfuturelistener, GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
        if (this.isConnected() && this.trySendQueue() && !(packet instanceof PacketPlayOutMapChunk && !((PacketPlayOutMapChunk) packet).isReady())) { // Paper - Async-Anti-Xray - Add chunk packets which are not ready or all packets if the queue contains chunk packets which are not ready to the queue and send the packets later in the right order
            //this.m(); // Paper - Async-Anti-Xray - Move to if statement (this.trySendQueue())
            this.a(packet, ArrayUtils.add(agenericfuturelistener, 0, genericfuturelistener));
        } else {
            this.j.writeLock().lock();

            try {
                this.i.add(new NetworkManager.QueuedPacket(packet, ArrayUtils.add(agenericfuturelistener, 0, genericfuturelistener)));
            } finally {
                this.j.writeLock().unlock();
            }
        }

    }

    private void dispatchPacket(final Packet<?> packet, @Nullable final GenericFutureListener<? extends Future<? super Void>>[] genericFutureListeners) { this.a(packet, genericFutureListeners); } // Paper - Anti-Xray - OBFHELPER
    private void a(final Packet<?> packet, @Nullable final GenericFutureListener<? extends Future<? super Void>>[] agenericfuturelistener) {
        final EnumProtocol enumprotocol = EnumProtocol.a(packet);
        final EnumProtocol enumprotocol1 = this.channel.attr(NetworkManager.c).get();

        if (enumprotocol1 != enumprotocol) {
            NetworkManager.g.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }

        if (this.channel.eventLoop().inEventLoop()) {
            if (enumprotocol != enumprotocol1) {
                this.setProtocol(enumprotocol);
            }

            ChannelFuture channelfuture = this.channel.writeAndFlush(packet);

            if (agenericfuturelistener != null) {
                channelfuture.addListeners(agenericfuturelistener);
            }

            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            this.channel.eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    if (enumprotocol != enumprotocol1) {
                        NetworkManager.this.setProtocol(enumprotocol);
                    }

                    ChannelFuture channelfuture = NetworkManager.this.channel.writeAndFlush(packet);

                    if (agenericfuturelistener != null) {
                        channelfuture.addListeners(agenericfuturelistener);
                    }

                    channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                }
            });
        }

    }

    // Paper start - Async-Anti-Xray - Stop dispatching further packets and return false if the peeked packet is a chunk packet which is not ready
    private boolean trySendQueue() { return this.m(); } // OBFHELPER
    private boolean m() { // void -> boolean
        if (this.channel != null && this.channel.isOpen()) {
            if (this.i.isEmpty()) { // return if the packet queue is empty so that the write lock by Anti-Xray doesn't affect the vanilla performance at all
                return true;
            }

            this.j.writeLock().lock(); // readLock -> writeLock (because of race condition between peek and poll)

            try {
                while (!this.i.isEmpty()) {
                    NetworkManager.QueuedPacket networkmanager_queuedpacket = this.getPacketQueue().peek(); // poll -> peek

                    if (networkmanager_queuedpacket != null) { // Fix NPE (Spigot bug caused by handleDisconnection())
                        if (networkmanager_queuedpacket.getPacket() instanceof PacketPlayOutMapChunk && !((PacketPlayOutMapChunk) networkmanager_queuedpacket.getPacket()).isReady()) { // Check if the peeked packet is a chunk packet which is not ready
                            return false; // Return false if the peeked packet is a chunk packet which is not ready
                        } else {
                            this.getPacketQueue().poll(); // poll here
                            this.dispatchPacket(networkmanager_queuedpacket.getPacket(), networkmanager_queuedpacket.getGenericFutureListeners()); // dispatch the packet
                        }
                    }
                }
            } finally {
                this.j.writeLock().unlock(); // readLock -> writeLock (because of race condition between peek and poll)
            }

        }

        return true; // Return true if all packets were dispatched
    }
    // Paper end

    public void a() {
        this.m();
        if (this.m instanceof ITickable) {
            ((ITickable) this.m).e();
        }

        if (this.channel != null) {
            if (enableExplicitFlush) this.channel.eventLoop().execute(() -> this.channel.flush()); // Paper - we don't need to explicit flush here, but allow opt in incase issues are found to a better version
        }

    }

    public SocketAddress getSocketAddress() {
        return this.l;
    }

    public void close(IChatBaseComponent ichatbasecomponent) {
        // Spigot Start
        this.preparing = false;
        // Spigot End
        if (this.channel.isOpen()) {
            this.channel.close(); // We can't wait as this may be called from an event loop.
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
        return this.m;
    }

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
                this.i.clear(); // Free up packet queue.
            }

        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelhandlercontext, Packet object) throws Exception { // CraftBukkit - fix decompile error
        this.a(channelhandlercontext, object);
    }

    public static class QueuedPacket { // Akarin - default -> public

        private final Packet<?> a; public final Packet<?> getPacket() { return this.a; } // Paper - Anti-Xray - OBFHELPER // Akarin - private -> public
        private final GenericFutureListener<? extends Future<? super Void>>[] b; public final GenericFutureListener<? extends Future<? super Void>>[] getGenericFutureListeners() { return this.b; } // Paper - Anti-Xray - OBFHELPER // Akarin - private -> public

        public QueuedPacket(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
            this.a = packet;
            this.b = agenericfuturelistener;
        }
    }

    // Spigot Start
    public SocketAddress getRawAddress()
    {
        return this.channel.remoteAddress();
    }
    // Spigot End
}
