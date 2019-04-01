package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.akarin.server.core.PacketType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
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
import io.netty.util.concurrent.PromiseNotifier;

import java.net.SocketAddress;
import java.nio.channels.Channels;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private final ConcurrentLinkedQueue<NetworkManager.QueuedPacket> packetQueue = new ConcurrentLinkedQueue<NetworkManager.QueuedPacket>();  private final Queue<NetworkManager.QueuedPacket> getPacketQueue() { return this.packetQueue; } // Paper - OBFHELPER // Akarin
    private final Queue<PacketPlayOutMapChunk> pendingChunkQueue = Lists.newLinkedList(); // Akarin - remove packet queue
    private final ReentrantReadWriteLock j = new ReentrantReadWriteLock();
    public Channel channel;
    public SocketAddress socketAddress; public void setSpoofedRemoteAddress(SocketAddress address) { this.socketAddress = address; } // Paper - OBFHELPER
    // Spigot Start
    public java.util.UUID spoofedUUID;
    public com.mojang.authlib.properties.Property[] spoofedProfile;
    public boolean preparing = true;
    // Spigot End
    private PacketListener packetListener;
    private IChatBaseComponent n;
    private boolean o;
    private AtomicBoolean p = new AtomicBoolean(false); // Akarin - atomic
    private int q;
    private int r;
    private float s;
    private float t;
    private int u;
    private boolean v;
    // Paper start - NetworkClient implementation
    public int protocolVersion;
    public java.net.InetSocketAddress virtualHost;
    private static boolean enableExplicitFlush = Boolean.getBoolean("paper.explicit-flush");
    // Paper end

    public NetworkManager(EnumProtocolDirection enumprotocoldirection) {
        this.h = enumprotocoldirection;
    }

    public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {
        super.channelActive(channelhandlercontext);
        this.channel = channelhandlercontext.channel();
        this.socketAddress = this.channel.remoteAddress();
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

    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.close(new ChatMessage("disconnect.endOfStream", new Object[0]));
    }

    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) {
        // Paper start
        if (throwable instanceof io.netty.handler.codec.EncoderException && throwable.getCause() instanceof PacketEncoder.PacketTooLargeException) {
            if (((PacketEncoder.PacketTooLargeException) throwable.getCause()).getPacket().packetTooLarge(this)) {
                return;
            } else {
                throwable = throwable.getCause();
            }
        }
        // Paper end
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
        if (MinecraftServer.getServer().isDebugging()) throwable.printStackTrace(); // Spigot
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
        packet.a((T) packetlistener); // CraftBukkit - decompile error
    }

    public void setPacketListener(PacketListener packetlistener) {
        Validate.notNull(packetlistener, "packetListener", new Object[0]);
        NetworkManager.g.debug("Set listener of {} to {}", this, packetlistener);
        this.packetListener = packetlistener;
    }

    public void sendPacket(Packet<?> packet) {
        this.sendPacket(packet, (GenericFutureListener) null);
    }

    // Akarin start
    public final void sendPackets(Packet<?> packet0, Packet<?> packet1) {
        if (this.isConnected() && this.channel.isRegistered()) { // why send packet to whom not connected?
            //this.j.readLock().lock();
            //try {
                // Queue new packets
                this.dispatchPacket(packet0, null);
                this.dispatchPacket(packet1, null);
            //} finally {
            //    this.j.readLock().unlock();
            //}
        }
    }

    public final void sendPackets(Packet<?> packet0, Packet<?> packet1, Packet<?> packet2) {
        if (this.isConnected() && this.channel.isRegistered()) { // why send packet to whom not connected?
            //this.j.readLock().lock();
            //try {
                // Queue new packets
                this.dispatchPacket(packet0, null);
                this.dispatchPacket(packet1, null);
                this.dispatchPacket(packet2, null);
            //} finally {
            //this.j.readLock().unlock();
            //}
        }
    }

    public final void sendPackets(Packet<?> packet0, Packet<?> packet1, Packet<?> packet2, Packet<?> packet3, Packet<?> packet4, Packet<?> packet5, Packet<?> packet6) {
        if (this.isConnected() && this.channel.isRegistered()) { // why send packet to whom not connected?
            //this.j.readLock().lock();
            //try {
                // Queue new packets
                this.dispatchPacket(packet0, null);
                this.dispatchPacket(packet1, null);
                this.dispatchPacket(packet2, null);
                this.dispatchPacket(packet3, null);
                this.dispatchPacket(packet4, null);
                this.dispatchPacket(packet5, null);
                this.dispatchPacket(packet6, null);
            //} finally {
            //this.j.readLock().unlock();
            //}
        }
    }
    // Akarin end
    public void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
        if (this.isConnected() && this.channel.isRegistered() /*&& this.sendPacketQueue() && !(packet instanceof PacketPlayOutMapChunk && !((PacketPlayOutMapChunk) packet).isReady())*/) { // Paper - Async-Anti-Xray - Add chunk packets which are not ready or all packets if the packet queue contains chunk packets which are not ready to the packet queue and send the packets later in the right order // Akarin
            //this.o(); // Paper - Async-Anti-Xray - Move to if statement (this.sendPacketQueue())
            // Akarin start
            //this.j.readLock().lock();
            //try {
                // Dispatch or queue new packets
                this.dispatchPacket(packet, genericfuturelistener);
            //} finally {
            //    this.j.readLock().unlock();
            //}
        } else if (false) {
            // Akarin end
            this.j.writeLock().lock();

            try {
                this.packetQueue.add(new NetworkManager.QueuedPacket(packet, genericfuturelistener));
            } finally {
                this.j.writeLock().unlock();
            }
        }

    }

    // Akarin start
    private final void dispatchPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) { this.b(packet, genericFutureListener); } // Paper - OBFHELPER
    private final void b(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
        if (!packet.canDispatchImmediately())
            this.pendingChunkQueue.add((PacketPlayOutMapChunk) packet);
        // Akarin end
        EnumProtocol enumprotocol = EnumProtocol.a(packet);
        EnumProtocol enumprotocol1 = (EnumProtocol) this.channel.attr(NetworkManager.c).get();

        //++this.r; // Akarin - unused
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

        // Paper start
        java.util.List<Packet> extraPackets = packet.getExtraPackets();
        if (extraPackets != null && !extraPackets.isEmpty()) {
            for (Packet extraPacket : extraPackets) {
                this.dispatchPacket(extraPacket, genericfuturelistener);
            }
        }
        // Paper end

    }

    // Paper start - Async-Anti-Xray - Stop dispatching further packets and return false if the peeked packet is a chunk packet which is not ready
    public boolean sendPacketQueue() { return this.o(); } // OBFHELPER // void -> boolean // Akarin - public
    private boolean o() { // void -> boolean
        if (this.channel != null && this.channel.isOpen() && this.channel.isRegistered() && !this.pendingChunkQueue.isEmpty()) {
            // Akarin start
            Iterator<PacketPlayOutMapChunk> iterator = this.pendingChunkQueue.iterator();
            while (iterator.hasNext()) {
                PacketPlayOutMapChunk packet = iterator.next();
                if (packet.isReady()) {
                    this.dispatchPacket(packet, null);
                    iterator.remove();
                }
            }
            /*
            if (this.packetQueue.isEmpty()) { // return if the packet queue is empty so that the write lock by Anti-Xray doesn't affect the vanilla performance at all
                return true;
            }

            this.j.writeLock().lock(); // readLock -> writeLock (because of race condition between peek and poll)

            try {
                while (!this.packetQueue.isEmpty()) {
                    NetworkManager.QueuedPacket networkmanager_queuedpacket = (NetworkManager.QueuedPacket) this.getPacketQueue().peek(); // poll -> peek

                    if (networkmanager_queuedpacket != null) { // Fix NPE (Spigot bug caused by handleDisconnection())
                        if (networkmanager_queuedpacket.getPacket() instanceof PacketPlayOutMapChunk && !((PacketPlayOutMapChunk) networkmanager_queuedpacket.getPacket()).isReady()) { // Check if the peeked packet is a chunk packet which is not ready
                            return false; // Return false if the peeked packet is a chunk packet which is not ready
                        } else {
                            this.getPacketQueue().poll(); // poll here
                            this.dispatchPacket(networkmanager_queuedpacket.getPacket(), networkmanager_queuedpacket.getGenericFutureListener()); // dispatch the packet
                        }
                    }
                }
            } finally {
                this.j.writeLock().unlock(); // readLock -> writeLock (because of race condition between peek and poll)
            }
            */
            // Akarin end

        }

        return true; // Return true if all packets were dispatched
    }
    // Paper end

    public void a() {
        //this.o(); // Akarin - move to scheduler
        if (this.packetListener instanceof ITickable) {
            ((ITickable) this.packetListener).tick();
        }

        if (this.channel != null) {
            if (enableExplicitFlush) this.channel.eventLoop().execute(() -> this.channel.flush()); // Paper - we don't need to explicit flush here, but allow opt in incase issues are found to a better version
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
            if (!this.p.compareAndSet(false, true)) { // Akarin
                //NetworkManager.g.warn("handleDisconnection() called twice"); // Akarin
            } else {
                //this.p = true; // Akarin
                if (this.j() != null) {
                    this.i().a(this.j());
                } else if (this.i() != null) {
                    this.i().a(new ChatMessage("multiplayer.disconnect.generic", new Object[0]));
                }
                this.packetQueue.clear(); // Free up packet queue.
                // Paper start - Add PlayerConnectionCloseEvent
                final PacketListener packetListener = this.i();
                if (packetListener instanceof PlayerConnection) {
                    /* Player was logged in */
                    final PlayerConnection playerConnection = (PlayerConnection) packetListener;
                    new com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent(playerConnection.player.uniqueID,
                        playerConnection.player.getName(), ((java.net.InetSocketAddress)socketAddress).getAddress(), false).callEvent();
                } else if (packetListener instanceof LoginListener) {
                    /* Player is login stage */
                    final LoginListener loginListener = (LoginListener) packetListener;
                    switch (loginListener.getLoginState()) {
                        case READY_TO_ACCEPT:
                        case DELAY_ACCEPT:
                        case ACCEPTED:
                            final com.mojang.authlib.GameProfile profile = loginListener.getGameProfile(); /* Should be non-null at this stage */
                            new com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent(profile.getId(), profile.getName(),
                                ((java.net.InetSocketAddress)socketAddress).getAddress(), false).callEvent();
                    }
                }
                // Paper end
            }

        }
    }

    static class QueuedPacket {

        private final Packet<?> a; private final Packet<?> getPacket() { return this.a; } // Paper - OBFHELPER
        @Nullable
        private final GenericFutureListener<? extends Future<? super Void>> b; private final GenericFutureListener<? extends Future<? super Void>> getGenericFutureListener() { return this.b; } // Paper - OBFHELPER

        public QueuedPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
            this.a = packet;
            this.b = genericfuturelistener;
        }
    }

    // Spigot Start
    public SocketAddress getRawAddress()
    {
        return this.channel.remoteAddress();
    }
    // Spigot End
}
