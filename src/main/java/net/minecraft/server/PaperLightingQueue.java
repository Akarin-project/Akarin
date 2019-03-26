package net.minecraft.server;

import co.aikar.timings.Timing;
import io.akarin.server.core.AkarinGlobalConfig;

import com.destroystokyo.paper.PaperConfig;
import it.unimi.dsi.fastutil.objects.ObjectCollection;

import java.util.ArrayDeque;

class PaperLightingQueue {
    private static final long MAX_TIME = (long) (1000000 * (50 + PaperConfig.maxTickMsLostLightQueue));

    static void processQueue(long curTime) {
        final long startTime = System.nanoTime();
        final long maxTickTime = MAX_TIME - (startTime - curTime);

        if (maxTickTime <= 0) {
            return;
        }

        START:
        for (World world : MinecraftServer.getServer().getWorlds()) {
            if (!world.paperConfig.queueLightUpdates || AkarinGlobalConfig.enableAsyncLighting) { // Akarin
                continue;
            }

            ObjectCollection<Chunk> loadedChunks = ((WorldServer) world).getChunkProvider().chunks.values();
            for (Chunk chunk : loadedChunks.toArray(new Chunk[0])) {
                if (chunk.lightingQueue.processQueue(startTime, maxTickTime)) {
                    break START;
                }
            }
        }
    }

    static class LightingQueue extends ArrayDeque<Runnable> {
        final private Chunk chunk;

        LightingQueue(Chunk chunk) {
            super();
            this.chunk = chunk;
        }

        /**
         * Processes the lighting queue for this chunk
         *
         * @param startTime If start Time is 0, we will not limit execution time
         * @param maxTickTime Maximum time to spend processing lighting updates
         * @return true to abort processing furthur lighting updates
         */
        private boolean processQueue(long startTime, long maxTickTime) {
            if (this.isEmpty()) {
                return false;
            }
            if (isOutOfTime(maxTickTime, startTime)) {
                return true;
            }
            try (Timing ignored = chunk.world.timings.lightingQueueTimer.startTimingUnsafe()) {
                Runnable lightUpdate;
                while ((lightUpdate = this.poll()) != null) {
                    lightUpdate.run();
                    if (isOutOfTime(maxTickTime, startTime)) {
                        return true;
                    }
                }
            }

            return false;
        }

        /**
         * Flushes lighting updates to unload the chunk
         */
        void processUnload() {
            if (!chunk.world.paperConfig.queueLightUpdates || AkarinGlobalConfig.enableAsyncLighting) { // Akarin
                return;
            }
            processQueue(0, 0); // No timeout

            final int radius = 1;
            for (int x = chunk.locX - radius; x <= chunk.locX + radius; ++x) {
                for (int z = chunk.locZ - radius; z <= chunk.locZ + radius; ++z) {
                    if (x == chunk.locX && z == chunk.locZ) {
                        continue;
                    }

                    Chunk neighbor = chunk.world.getChunkIfLoaded(x, z);
                    if (neighbor != null) {
                        neighbor.lightingQueue.processQueue(0, 0); // No timeout
                    }
                }
            }
        }
    }

    private static boolean isOutOfTime(long maxTickTime, long startTime) {
        return startTime > 0 && System.nanoTime() - startTime > maxTickTime;
    }
}
