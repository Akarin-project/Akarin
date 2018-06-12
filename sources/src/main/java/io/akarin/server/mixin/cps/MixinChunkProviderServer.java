package io.akarin.server.mixin.cps;

import java.util.Iterator;

import org.spigotmc.SlackActivityAccountant;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkProviderServer;
import net.minecraft.server.IChunkLoader;
import net.minecraft.server.WorldServer;

@Mixin(value = ChunkProviderServer.class, remap = false)
public abstract class MixinChunkProviderServer {
    @Shadow @Final public WorldServer world;
    @Shadow public Long2ObjectOpenHashMap<Chunk> chunks;
    
    public int pendingUnloadChunks; // For keeping unload target-size feature
    
    public void unload(Chunk chunk) {
        if (this.world.worldProvider.c(chunk.locX, chunk.locZ)) {
            // Akarin - avoid using the queue and simply check the unloaded flag during unloads
            // this.unloadQueue.add(Long.valueOf(ChunkCoordIntPair.a(chunk.locX, chunk.locZ)));
            pendingUnloadChunks++;
            chunk.setShouldUnload(true);
        }
    }
    
    @Shadow public abstract boolean unloadChunk(Chunk chunk, boolean save);
    @Shadow @Final private IChunkLoader chunkLoader;
    @Shadow @Final private static double UNLOAD_QUEUE_RESIZE_FACTOR;
    
    @Overwrite
    public boolean unloadChunks() {
        if (!this.world.savingDisabled) {
            long now = System.currentTimeMillis();
            long unloadAfter = world.paperConfig.delayChunkUnloadsBy;
            SlackActivityAccountant activityAccountant = world.getMinecraftServer().slackActivityAccountant;
            Iterator<Chunk> it = chunks.values().iterator();
            
            while (it.hasNext()) {
                activityAccountant.startActivity(0.5);
                
                Chunk chunk = it.next();
                if (unloadAfter > 0) {
                    if (chunk.scheduledForUnload != null && now - chunk.scheduledForUnload > unloadAfter) {
                        chunk.scheduledForUnload = null;
                        unload(chunk);
                    }
                }
                int targetSize = Math.min(pendingUnloadChunks - 100,  (int) (pendingUnloadChunks * UNLOAD_QUEUE_RESIZE_FACTOR)); // Paper - Make more aggressive
                
                if (chunk != null && chunk.isUnloading()) {
                    // If a plugin cancelled it, we shouldn't trying unload it for a while
                    chunk.setShouldUnload(false); // Paper
                    
                    if (!unloadChunk(chunk, true)) continue; // Event cancelled
                    it.remove();
                    
                    if (--pendingUnloadChunks <= targetSize && activityAccountant.activityTimeIsExhausted()) break;
                }
                activityAccountant.endActivity();
            }
            this.chunkLoader.b(); // PAIL: chunkTick
        }
        return false;
    }
    
    @Redirect(method = "unloadChunk", at = @At(
            value = "INVOKE",
            target = "it/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap.remove(J)Ljava/lang/Object;"
    ))
    private Object remove(Long2ObjectOpenHashMap<Chunk> chunks, long chunkHash) {
        return null;
    }
    
    @Overwrite
    public String getName() {
        return "ServerChunkCache: " + chunks.size(); // Akarin - remove unload queue
    }
}
