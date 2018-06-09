package io.akarin.server.mixin.core;

import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.google.common.base.Predicate;

import io.akarin.api.CheckedConcurrentLinkedQueue;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.NetworkManager.QueuedPacket;
import net.minecraft.server.Packet;
import net.minecraft.server.PacketPlayOutMapChunk;

@Mixin(value = NetworkManager.class, remap = false)
public class OptimisticNetworkManager {
    @Shadow public Channel channel;
    @Shadow @Final private Queue<NetworkManager.QueuedPacket> i;
    @Shadow @Final private ReentrantReadWriteLock j;
    
    @Shadow private Queue<NetworkManager.QueuedPacket> getPacketQueue() { return null; }
    @Shadow private void dispatchPacket(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>>[] genericFutureListeners) {}
    
    private static final Predicate<QueuedPacket> IS_CHUNK_READY = new Predicate<QueuedPacket>() {
        @Override
        public boolean apply(QueuedPacket item) {
            Packet<?> packet = item.getPacket();
            return !(packet instanceof PacketPlayOutMapChunk && !((PacketPlayOutMapChunk) packet).isReady()); // Check if the peeked packet is a chunk packet which is not ready
        }
    };
    
    private static final QueuedPacket SIGNAL_PACKET = new QueuedPacket(null, null);
    
    @Overwrite
    private boolean m() {
        if (this.channel != null && this.channel.isOpen()) {
            if (this.i.isEmpty()) { // return if the packet queue is empty so that the write lock by Anti-Xray doesn't affect the vanilla performance at all
                return true;
            }

            this.j.readLock().lock();
            try {
                while (!this.i.isEmpty()) {
                    NetworkManager.QueuedPacket packet = ((CheckedConcurrentLinkedQueue<QueuedPacket>) getPacketQueue()).poll(IS_CHUNK_READY, SIGNAL_PACKET);
                    
                    if (packet != null) { // Fix NPE (Spigot bug caused by handleDisconnection())
                        if (packet == SIGNAL_PACKET) {
                            return false; // Return false if the peeked packet is a chunk packet which is not ready
                        } else {
                            dispatchPacket(packet.getPacket(), packet.getGenericFutureListeners()); // dispatch the packet
                        }
                    }
                }
            } finally {
                this.j.readLock().unlock();
            }
            
        }
        return true; // Return true if all packets were dispatched
    }
    
}
