package io.akarin.server.mixin.cps;

import org.spigotmc.SlackActivityAccountant;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkProviderServer;
import net.minecraft.server.IChunkLoader;
import net.minecraft.server.WorldServer;

@Mixin(value = ChunkProviderServer.class, remap = false)
public abstract class MixinChunkProviderServer {
    @Shadow @Final public WorldServer world;
    @Shadow public Long2ObjectOpenHashMap<Chunk> chunks;
    
    public void unload(Chunk chunk) {
        if (this.world.worldProvider.c(chunk.locX, chunk.locZ)) {
            // Akarin - avoid using the queue and simply check the unloaded flag during unloads
            // this.unloadQueue.add(Long.valueOf(ChunkCoordIntPair.a(chunk.locX, chunk.locZ)));
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
            activityAccountant.startActivity(0.5);
            ObjectIterator<Entry<Chunk>> it = chunks.long2ObjectEntrySet().fastIterator();
            int remainingChunks = chunks.size();
            int targetSize = Math.min(remainingChunks - 100,  (int) (remainingChunks * UNLOAD_QUEUE_RESIZE_FACTOR)); // Paper - Make more aggressive
            
            while (it.hasNext()) {
                Entry<Chunk> entry = it.next();
                Chunk chunk = entry.getValue();
                if (chunk == null) continue;
                
                if (chunk.isUnloading()) {
                    if (chunk.scheduledForUnload != null) {
                        if (now - chunk.scheduledForUnload > unloadAfter) {
                            chunk.scheduledForUnload = null;
                        } else continue;
                    }
                    
                    if (!unloadChunk(chunk, true)) { // Event cancelled
                        // If a plugin cancelled it, we shouldn't trying unload it for a while
                        chunk.setShouldUnload(false);
                        continue;
                    }
                    
                    it.remove();
                    if (--remainingChunks <= targetSize || activityAccountant.activityTimeIsExhausted()) break; // more slack since the target size not work as intended
                }
            }
            activityAccountant.endActivity();
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
        return "ServerChunkCache: " + chunks.size();
    }
}
