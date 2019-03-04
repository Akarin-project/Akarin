package org.bukkit.craftbukkit.chunkio;

import java.io.IOException;

import co.aikar.timings.Timing;
import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.ChunkRegionLoader;
import net.minecraft.server.NBTTagCompound;

import org.bukkit.Server;
import org.bukkit.craftbukkit.util.AsynchronousExecutor;

import java.util.concurrent.atomic.AtomicInteger;

class ChunkIOProvider implements AsynchronousExecutor.CallBackProvider<QueuedChunk, Chunk, Runnable, RuntimeException> {
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    // async stuff
    public Chunk callStage1(QueuedChunk queuedChunk) throws RuntimeException {
        try (Timing ignored = queuedChunk.provider.world.timings.chunkIOStage1.startTimingIfSync()) { // Paper
            ChunkRegionLoader loader = queuedChunk.loader;
            Object[] data = loader.loadChunk(queuedChunk.world, queuedChunk.x, queuedChunk.z, (chunk) -> {});
            
            if (data != null) {
                queuedChunk.compound = (NBTTagCompound) data[1];
                return (Chunk) data[0];
            }

            return null;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    // sync stuff
    public void callStage2(QueuedChunk queuedChunk, Chunk chunk) throws RuntimeException {
        if (chunk == null || queuedChunk.provider.chunks.containsKey(ChunkCoordIntPair.a(queuedChunk.x, queuedChunk.z))) { // Paper - also call original if it was already loaded
            // If the chunk loading failed just do it synchronously (may generate)
            queuedChunk.provider.getChunkAt(queuedChunk.x, queuedChunk.z, true, true); // Paper - actually call original if it was already loaded
            return;
        }
        try (Timing ignored = queuedChunk.provider.world.timings.chunkIOStage2.startTimingIfSync()) { // Paper

        queuedChunk.loader.loadEntities(queuedChunk.compound.getCompound("Level"), chunk);
        chunk.setLastSaved(queuedChunk.provider.world.getTime());
        queuedChunk.provider.chunks.put(ChunkCoordIntPair.a(queuedChunk.x, queuedChunk.z), chunk);
        chunk.addEntities();
        } // Paper
    }

    public void callStage3(QueuedChunk queuedChunk, Chunk chunk, Runnable runnable) throws RuntimeException {
        runnable.run();
    }

    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, "Chunk I/O Executor Thread-" + threadNumber.getAndIncrement());
        thread.setDaemon(true);
        return thread;
    }
}
