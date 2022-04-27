package io.akarin.server.mixin.nsc;

import java.util.Queue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.googlecode.concurentlocks.ReentrantReadWriteUpdateLock;

import io.akarin.api.internal.utils.CheckedConcurrentLinkedQueue;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.NetworkManager.QueuedPacket;
import net.minecraft.server.Packet;
import net.minecraft.server.PacketPlayOutMapChunk;

@Mixin(value = NetworkManager.class, remap = false)
public abstract class OptimisticNetworkManager {
    @Shadow public Channel channel;
    @Shadow(aliases = "i") @Final private Queue<NetworkManager.QueuedPacket> packets;
    @Shadow(aliases = "j") @Final private ReentrantReadWriteUpdateLock queueLock;
    
    @Shadow public abstract Queue<NetworkManager.QueuedPacket> getPacketQueue();
    @Shadow public abstract void dispatchPacket(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>>[] genericFutureListeners);
    
    @SuppressWarnings("unchecked")
    private static final QueuedPacket SIGNAL_PACKET = new QueuedPacket(null);
    
    @Overwrite // OBFHELPER: trySendQueue
    private boolean m() {
        if (this.channel != null && this.channel.isOpen()) {
            if (this.packets.isEmpty()) { // return if the packet queue is empty so that the write lock by Anti-Xray doesn't affect the vanilla performance at all
                return true;
            }

            this.queueLock.updateLock().lock();
            try {
                while (!this.packets.isEmpty()) {
                    NetworkManager.QueuedPacket packet = ((CheckedConcurrentLinkedQueue<QueuedPacket>) getPacketQueue()).poll(item -> {
                        return item.getPacket() instanceof PacketPlayOutMapChunk && !((PacketPlayOutMapChunk) item.getPacket()).isReady();
                    }, SIGNAL_PACKET);
                    
                    if (packet != null) { // Fix NPE (Spigot bug caused by handleDisconnection())
                        if (packet == SIGNAL_PACKET) {
                            return false; // Return false if the peeked packet is a chunk packet which is not ready
                        } else {
                            dispatchPacket(packet.getPacket(), packet.getGenericFutureListeners()); // dispatch the packet
                        }
                    }
                }
            } finally {
                this.queueLock.updateLock().unlock();
            }
            
        }
        return true; // Return true if all packets were dispatched
    }
    
}
