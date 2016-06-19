package net.minecraft.server;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkMap extends Long2ObjectOpenHashMap<Chunk> {

    private static final Logger a = LogManager.getLogger();

    public ChunkMap(int i) {
        super(i);
    }

    public Chunk put(long i, Chunk chunk) {
        chunk.world.timings.syncChunkLoadPostTimer.startTiming(); // Paper
        lastChunkByPos = chunk; // Paper
        Chunk chunk1 = (Chunk) super.put(i, chunk);
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i);

        for (int j = chunkcoordintpair.x - 1; j <= chunkcoordintpair.x + 1; ++j) {
            for (int k = chunkcoordintpair.z - 1; k <= chunkcoordintpair.z + 1; ++k) {
                if (j != chunkcoordintpair.x || k != chunkcoordintpair.z) {
                    long l = ChunkCoordIntPair.a(j, k);
                    Chunk chunk2 = (Chunk) super.get(l);  // Paper - use super to avoid polluting last access cache

                    if (chunk2 != null) {
                        chunk.H();
                        chunk2.H();
                    }
                }
            }
        }

        // CraftBukkit start
        // Update neighbor counts
        for (int x = -2; x < 3; x++) {
            for (int z = -2; z < 3; z++) {
                if (x == 0 && z == 0) {
                    continue;
                }

                Chunk neighbor = super.get(ChunkCoordIntPair.a(chunkcoordintpair.x + x, chunkcoordintpair.z + z)); // Paper - use super to avoid polluting last access cache
                if (neighbor != null) {
                    neighbor.setNeighborLoaded(-x, -z);
                    chunk.setNeighborLoaded(x, z);
                }
            }
        }
        // Paper start - if this is a spare chunk (not part of any players view distance), go ahead and queue it for unload.
        if (!((WorldServer)chunk.world).getPlayerChunkMap().isChunkInUse(chunk.locX, chunk.locZ)) {
            if (chunk.world.paperConfig.delayChunkUnloadsBy > 0) {
                chunk.scheduledForUnload = System.currentTimeMillis();
            } else {
                ((WorldServer) chunk.world).getChunkProvider().unload(chunk);
            }
        }
        // Paper end
        chunk.world.timings.syncChunkLoadPostTimer.stopTiming(); // Paper
        // CraftBukkit end

        return chunk1;
    }

    public Chunk put(Long olong, Chunk chunk) {
        return this.put(olong, chunk);
    }

    public Chunk remove(long i) {
        Chunk chunk = (Chunk) super.remove(i);
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i);

        for (int j = chunkcoordintpair.x - 1; j <= chunkcoordintpair.x + 1; ++j) {
            for (int k = chunkcoordintpair.z - 1; k <= chunkcoordintpair.z + 1; ++k) {
                if (j != chunkcoordintpair.x || k != chunkcoordintpair.z) {
                    Chunk chunk1 = (Chunk) super.get(ChunkCoordIntPair.a(j, k)); // Paper - use super to avoid polluting last access cache

                    if (chunk1 != null) {
                        chunk1.I();
                    }
                }
            }
        }

        // Paper start
        if (lastChunkByPos != null && i == lastChunkByPos.chunkKey) {
            lastChunkByPos = null;
        }
        return chunk;
    }
    private Chunk lastChunkByPos = null;

    @Override
    public Chunk get(long l) {
        if (lastChunkByPos != null && l == lastChunkByPos.chunkKey) {
            return lastChunkByPos;
        }
        return lastChunkByPos = super.get(l);
    }
    // Paper end

    public Chunk remove(Object object) {
        return this.remove((Long) object);
    }

    public void putAll(Map<? extends Long, ? extends Chunk> map) {
        throw new RuntimeException("Not yet implemented");
    }

    public boolean remove(Object object, Object object1) {
        throw new RuntimeException("Not yet implemented");
    }
}
