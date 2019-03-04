/*
 * This file is licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018 Daniel Ennis <http://aikar.co>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.minecraft.server;

import com.destroystokyo.paper.PaperConfig;
import com.destroystokyo.paper.util.PriorityQueuedExecutor;
import com.destroystokyo.paper.util.PriorityQueuedExecutor.Priority;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.generator.CustomChunkGenerator;
import org.bukkit.craftbukkit.generator.InternalChunkGenerator;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class PaperAsyncChunkProvider extends ChunkProviderServer {

    private static final int GEN_THREAD_PRIORITY = Integer.getInteger("paper.genThreadPriority", 3);
    private static final int LOAD_THREAD_PRIORITY = Integer.getInteger("paper.loadThreadPriority", 4);
    private static final PriorityQueuedExecutor EXECUTOR = new PriorityQueuedExecutor("PaperChunkLoader", PaperConfig.asyncChunks ? PaperConfig.asyncChunkLoadThreads : 0, LOAD_THREAD_PRIORITY);
    private static final PriorityQueuedExecutor SINGLE_GEN_EXECUTOR = new PriorityQueuedExecutor("PaperChunkGenerator", PaperConfig.asyncChunks && PaperConfig.asyncChunkGeneration && !PaperConfig.asyncChunkGenThreadPerWorld ? 1 : 0, GEN_THREAD_PRIORITY);
    private static final ConcurrentLinkedDeque<Runnable> MAIN_THREAD_QUEUE = new ConcurrentLinkedDeque<>();

    private final PriorityQueuedExecutor generationExecutor;
    //private static final PriorityQueuedExecutor generationExecutor = new PriorityQueuedExecutor("PaperChunkGen", 1);
    private final Long2ObjectMap<PendingChunk> pendingChunks = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<>());
    private final IAsyncTaskHandler asyncHandler;

    private final WorldServer world;
    private final IChunkLoader chunkLoader;
    private final MinecraftServer server;
    private final boolean shouldGenSync;

    public PaperAsyncChunkProvider(WorldServer world, IChunkLoader chunkLoader, InternalChunkGenerator generator, MinecraftServer server) {
        super(world, chunkLoader, generator, server);

        this.server = world.getMinecraftServer();
        this.world = world;
        this.asyncHandler = server;
        this.chunkLoader = chunkLoader;
        String worldName = this.world.getWorld().getName();
        this.shouldGenSync = generator instanceof CustomChunkGenerator && !(((CustomChunkGenerator) generator).asyncSupported) || !PaperConfig.asyncChunkGeneration;
        this.generationExecutor = PaperConfig.asyncChunkGenThreadPerWorld ? new PriorityQueuedExecutor("PaperChunkGen-" + worldName, shouldGenSync ? 0 : 1, GEN_THREAD_PRIORITY) : SINGLE_GEN_EXECUTOR;
    }

    private static Priority calculatePriority(boolean isBlockingMain, boolean priority) {
        if (isBlockingMain) {
            return Priority.URGENT;
        }

        if (priority) {
            return Priority.HIGH;
        }

        return Priority.NORMAL;
    }

    static void stop(MinecraftServer server) {
        for (WorldServer world : server.getWorlds()) {
            world.getPlayerChunkMap().shutdown();
        }
    }

    static void processMainThreadQueue(MinecraftServer server) {
        for (WorldServer world : server.getWorlds()) {
            processMainThreadQueue(world);
        }
    }

    static void processMainThreadQueue(World world) {
        IChunkProvider chunkProvider = world.getChunkProvider();
        if (chunkProvider instanceof PaperAsyncChunkProvider) {
            ((PaperAsyncChunkProvider) chunkProvider).processMainThreadQueue();
        }
    }

    private void processMainThreadQueue() {
        processMainThreadQueue((PendingChunk) null);
    }
    private boolean processMainThreadQueue(PendingChunk pending) {
        Runnable run;
        boolean hadLoad = false;
        while ((run = MAIN_THREAD_QUEUE.poll()) != null) {
            run.run();
            hadLoad = true;
            if (pending != null && pending.hasPosted) {
                break;
            }
        }
        return hadLoad;
    }

    @Override
    public void bumpPriority(ChunkCoordIntPair coords) {
        final PendingChunk pending = pendingChunks.get(coords.asLong());
        if (pending != null) {
            pending.bumpPriority(Priority.HIGH);
        }
    }

    @Nullable
    @Override
    public Chunk getChunkAt(int x, int z, boolean load, boolean gen) {
        return getChunkAt(x, z, load, gen, null);
    }

    @Nullable
    @Override
    public Chunk getChunkAt(int x, int z, boolean load, boolean gen, boolean priority, Consumer<Chunk> consumer) {
        final long key = ChunkCoordIntPair.asLong(x, z);
        final Chunk chunk = this.chunks.get(key);
        if (chunk != null || !load) { // return null if we aren't loading
            if (consumer != null) {
                consumer.accept(chunk);
            }
            return chunk;
        }
        return loadOrGenerateChunk(x, z, gen, priority, consumer); // Async overrides this method
    }

    private Chunk loadOrGenerateChunk(int x, int z, boolean gen, boolean priority, Consumer<Chunk> consumer) {
        return requestChunk(x, z, gen, priority, consumer).getChunk();
    }

    final PendingChunkRequest requestChunk(int x, int z, boolean gen, boolean priority, Consumer<Chunk> consumer) {
        try (co.aikar.timings.Timing timing = world.timings.syncChunkLoadTimer.startTiming()) {
            final long key = ChunkCoordIntPair.asLong(x, z);
            final boolean isChunkThread = isChunkThread();
            final boolean isBlockingMain = consumer == null && server.isMainThread();
            final boolean loadOnThisThread = isChunkThread || isBlockingMain;
            final Priority taskPriority = calculatePriority(isBlockingMain, priority);

            // Obtain a PendingChunk
            final PendingChunk pending;
            synchronized (pendingChunks) {
                PendingChunk pendingChunk = pendingChunks.get(key);
                if (pendingChunk == null) {
                    pending = new PendingChunk(x, z, key, gen, taskPriority);
                    pendingChunks.put(key, pending);
                } else if (pendingChunk.hasFinished && gen && !pendingChunk.canGenerate && pendingChunk.chunk == null) {
                    // need to overwrite the old
                    pending = new PendingChunk(x, z, key, true, taskPriority);
                    pendingChunks.put(key, pending);
                } else {
                    pending = pendingChunk;
                    if (pending.taskPriority != taskPriority) {
                        pending.bumpPriority(taskPriority);
                    }
                }
            }

            // Listen for when result is ready
            final CompletableFuture<Chunk> future = new CompletableFuture<>();
            final PendingChunkRequest request = pending.addListener(future, gen, !loadOnThisThread);

            // Chunk Generation can trigger Chunk Loading, those loads may need to convert, and could be slow
            // Give an opportunity for urgent tasks to jump in at these times
            if (isChunkThread) {
                processUrgentTasks();
            }

            if (loadOnThisThread) {
                // do loads on main if blocking, or on current if we are a load/gen thread
                // gen threads do trigger chunk loads
                pending.loadTask.run();
            }

            if (isBlockingMain) {
                while (!future.isDone()) {
                    // We aren't done, obtain lock on queue
                    synchronized (MAIN_THREAD_QUEUE) {
                        // We may of received our request now, check it
                        if (processMainThreadQueue(pending)) {
                            // If we processed SOMETHING, don't wait
                            continue;
                        }
                        try {
                            // We got nothing from the queue, wait until something has been added
                            MAIN_THREAD_QUEUE.wait(1);
                        } catch (InterruptedException ignored) {
                        }
                    }
                    // Queue has been notified or timed out, process it
                    processMainThreadQueue(pending);
                }
                // We should be done AND posted into chunk map now, return it
                request.initialReturnChunk = pending.postChunk();
            } else if (consumer == null) {
                // This is on another thread
                request.initialReturnChunk = future.join();
            } else {
                future.thenAccept((c) -> this.asyncHandler.postToMainThread(() -> consumer.accept(c)));
            }

            return request;
        }
    }

    private void processUrgentTasks() {
        final PriorityQueuedExecutor executor = PriorityQueuedExecutor.getExecutor();
        if (executor != null) {
            executor.processUrgentTasks();
        }
    }

    @Override
    public CompletableFuture<Void> loadAllChunks(Iterable<ChunkCoordIntPair> iterable, Consumer<Chunk> consumer) {
        final Iterator<ChunkCoordIntPair> iterator = iterable.iterator();

        final List<CompletableFuture<Chunk>> all = new ArrayList<>();
        while (iterator.hasNext()) {
            final ChunkCoordIntPair chunkcoordintpair = iterator.next();
            final CompletableFuture<Chunk> future = new CompletableFuture<>();
            all.add(future);
            this.getChunkAt(chunkcoordintpair.x, chunkcoordintpair.z, true, true, chunk -> {
                future.complete(chunk);
                if (consumer != null) {
                    consumer.accept(chunk);
                }
            });
        }
        return CompletableFuture.allOf(all.toArray(new CompletableFuture[0]));
    }

    boolean chunkGoingToExists(int x, int z) {
        synchronized (pendingChunks) {
            PendingChunk pendingChunk = pendingChunks.get(ChunkCoordIntPair.asLong(x, z));
            return pendingChunk != null && pendingChunk.canGenerate;
        }
    }

    private enum PendingStatus {
        /**
         * Request has just started
         */
        STARTED,
        /**
         * Chunk is attempting to be loaded from disk
         */
        LOADING,
        /**
         * Chunk must generate on main and is pending main
         */
        GENERATION_PENDING,
        /**
         * Chunk is generating
         */
        GENERATING,
        /**
         * Chunk is ready and is pending post to main
         */
        PENDING_MAIN,
        /**
         * Could not load chunk, and did not need to generat
         */
        FAIL,
        /**
         * Fully done with this request (may or may not of loaded)
         */
        DONE,
        /**
         * Chunk load was cancelled (no longer needed)
         */
        CANCELLED
    }

    public interface CancellableChunkRequest {
        void cancel();
        Chunk getChunk();
    }

    public static class PendingChunkRequest implements CancellableChunkRequest {
        private final PendingChunk pending;
        private final AtomicBoolean cancelled = new AtomicBoolean(false);
        private volatile boolean generating;
        private volatile Chunk initialReturnChunk;

        private PendingChunkRequest(PendingChunk pending) {
            this.pending = pending;
            this.cancelled.set(true);
        }

        private PendingChunkRequest(PendingChunk pending, boolean gen) {
            this.pending = pending;
            this.generating = gen;
        }

        public void cancel() {
            this.pending.cancel(this);
        }

        /**
         * Will be null on asynchronous loads
         */
        @Override @Nullable
        public Chunk getChunk() {
            return initialReturnChunk;
        }
    }

    private boolean isLoadThread() {
        return EXECUTOR.isCurrentThread();
    }

    private boolean isGenThread() {
        return generationExecutor.isCurrentThread();
    }
    private boolean isChunkThread() {
        return isLoadThread() || isGenThread();
    }

    private class PendingChunk implements Runnable {
        private final int x;
        private final int z;
        private final long key;
        private final long started = System.currentTimeMillis();
        private final CompletableFuture<Chunk> loadOnly = new CompletableFuture<>();
        private final CompletableFuture<Chunk> generate = new CompletableFuture<>();
        private final AtomicInteger requests = new AtomicInteger(0);

        private volatile PendingStatus status = PendingStatus.STARTED;
        private volatile PriorityQueuedExecutor.PendingTask<Void> loadTask;
        private volatile PriorityQueuedExecutor.PendingTask<Chunk> genTask;
        private volatile Priority taskPriority;
        private volatile boolean generating;
        private volatile boolean canGenerate;
        private volatile boolean isHighPriority;
        private volatile boolean hasPosted;
        private volatile boolean hasFinished;
        private volatile Chunk chunk;
        private volatile NBTTagCompound pendingLevel;

        PendingChunk(int x, int z, long key, boolean canGenerate, boolean priority) {
            this.x = x;
            this.z = z;
            this.key = key;
            this.canGenerate = canGenerate;
            taskPriority = priority ? Priority.HIGH : Priority.NORMAL;
        }

        PendingChunk(int x, int z, long key, boolean canGenerate, Priority taskPriority) {
            this.x = x;
            this.z = z;
            this.key = key;
            this.canGenerate = canGenerate;
            this.taskPriority = taskPriority;
        }

        private synchronized void setStatus(PendingStatus status) {
            this.status = status;
        }

        private Chunk loadChunk(int x, int z) throws IOException {
            setStatus(PendingStatus.LOADING);
            Object[] data = chunkLoader.loadChunk(world, x, z, null);
            if (data != null) {
                // Level must be loaded on main
                this.pendingLevel = ((NBTTagCompound) data[1]).getCompound("Level");
                return (Chunk) data[0];
            } else {
                return null;
            }
        }

        private Chunk generateChunk() {
            synchronized (this) {
                if (requests.get() <= 0) {
                    return null;
                }
            }

            try {
                CompletableFuture<Chunk> pending = new CompletableFuture<>();
                batchScheduler.startBatch();
                batchScheduler.add(new ChunkCoordIntPair(x, z));

                ProtoChunk protoChunk = batchScheduler.executeBatch().join();
                boolean saved = false;
                if (!Bukkit.isPrimaryThread()) {
                    // If we are async, dispatch later
                    try {
                        chunkLoader.saveChunk(world, protoChunk, true);
                        saved = true;
                    } catch (IOException | ExceptionWorldConflict e) {
                        e.printStackTrace();
                    }
                }
                Chunk chunk = new Chunk(world, protoChunk, x, z);
                if (saved) {
                    chunk.setLastSaved(world.getTime());
                }
                generateFinished(chunk);

                return chunk;
            } catch (Throwable e) {
                MinecraftServer.LOGGER.error("Couldn't generate chunk (" +world.getWorld().getName() + ":" + x + "," + z + ")", e);
                generateFinished(null);
                return null;
            }
        }

        boolean loadFinished(Chunk chunk) {
            if (chunk != null) {
                postChunkToMain(chunk);
                return false;
            }
            loadOnly.complete(null);

            synchronized (this) {
                boolean cancelled = requests.get() <= 0;
                if (!canGenerate || cancelled) {
                    if (!cancelled) {
                        setStatus(PendingStatus.FAIL);
                    }
                    this.chunk = null;
                    this.hasFinished = true;
                    pendingChunks.remove(key);
                    return false;
                } else {
                    setStatus(PendingStatus.GENERATING);
                    generating = true;
                    return true;
                }
            }
        }

        void generateFinished(Chunk chunk) {
            synchronized (this) {
                this.chunk = chunk;
                this.hasFinished = true;
            }
            if (chunk != null) {
                postChunkToMain(chunk);
            } else {
                synchronized (this) {
                    pendingChunks.remove(key);
                    completeFutures(null);
                }
            }
        }

        synchronized private void completeFutures(Chunk chunk) {
            loadOnly.complete(chunk);
            generate.complete(chunk);
        }

        private void postChunkToMain(Chunk chunk) {
            synchronized (this) {
                setStatus(PendingStatus.PENDING_MAIN);
                this.chunk = chunk;
                this.hasFinished = true;
            }

            if (server.isMainThread()) {
                postChunk();
                return;
            }

            // Don't post here, even if on main, it must enter the queue so we can exit any open batch
            // schedulers, as post stage may trigger a new generation and cause errors
            synchronized (MAIN_THREAD_QUEUE) {
                if (this.taskPriority == Priority.URGENT) {
                    MAIN_THREAD_QUEUE.addFirst(this::postChunk);
                } else {
                    MAIN_THREAD_QUEUE.addLast(this::postChunk);
                }
                MAIN_THREAD_QUEUE.notify();
            }
        }

        Chunk postChunk() {
            if (!server.isMainThread()) {
                throw new IllegalStateException("Must post from main");
            }
            synchronized (this) {
                if (hasPosted || requests.get() <= 0) { // if pending is 0, all were cancelled
                    return chunk;
                }
                hasPosted = true;
            }
            try {
                if (chunk == null) {
                    chunk = chunks.get(key);
                    completeFutures(chunk);
                    return chunk;
                }
                if (pendingLevel != null) {
                    chunkLoader.loadEntities(pendingLevel, chunk);
                    pendingLevel = null;
                }
                synchronized (chunks) {
                    final Chunk other = chunks.get(key);
                    if (other != null) {
                        this.chunk = other;
                        completeFutures(other);
                        return other;
                    }
                    if (chunk != null) {
                        chunks.put(key, chunk);
                    }
                }

                chunk.addEntities();

                completeFutures(chunk);
                return chunk;
            } finally {
                pendingChunks.remove(key);
                setStatus(PendingStatus.DONE);
            }
        }

        synchronized PendingChunkRequest addListener(CompletableFuture<Chunk> future, boolean gen, boolean autoSubmit) {
            requests.incrementAndGet();
            if (loadTask == null) {
                // Take care of a race condition in that a request could be cancelled after the synchronize
                // on pendingChunks, but before a listener is added, which would erase these pending tasks.
                genTask = generationExecutor.createPendingTask(this::generateChunk, taskPriority);
                loadTask = EXECUTOR.createPendingTask(this, taskPriority);
                if (autoSubmit) {
                    // We will execute it outside of the synchronized context immediately after
                    loadTask.submit();
                }
            }

            if (hasFinished) {
                future.complete(chunk);
                return new PendingChunkRequest(this);
            } else if (gen) {
                canGenerate = true;
                generate.thenAccept(future::complete);
            } else {
                if (generating) {
                    future.complete(null);
                    return new PendingChunkRequest(this);
                } else {
                    loadOnly.thenAccept(future::complete);
                }
            }

            return new PendingChunkRequest(this, gen);
        }

        @Override
        public void run() {
            try {
                if (!loadFinished(loadChunk(x, z))) {
                    return;
                }
            } catch (Throwable ex) {
                MinecraftServer.LOGGER.error("Couldn't load chunk (" +world.getWorld().getName() + ":" + x + "," + z + ")", ex);
                if (ex instanceof IOException) {
                    generateFinished(null);
                    return;
                }
            }

            if (shouldGenSync) {
                synchronized (this) {
                    setStatus(PendingStatus.GENERATION_PENDING);
                    if (this.taskPriority == Priority.URGENT) {
                        MAIN_THREAD_QUEUE.addFirst(() -> generateFinished(this.generateChunk()));
                    } else {
                        MAIN_THREAD_QUEUE.addLast(() -> generateFinished(this.generateChunk()));
                    }

                }
                synchronized (MAIN_THREAD_QUEUE) {
                    MAIN_THREAD_QUEUE.notify();
                }
            } else {
                if (isGenThread()) {
                    // ideally we should never run into 1 chunk generating another chunk...
                    // but if we do, let's apply same solution
                    genTask.run();
                } else {
                    genTask.submit();
                }
            }
        }

        void bumpPriority() {
            bumpPriority(Priority.HIGH);
        }

        void bumpPriority(Priority newPriority) {
            if (taskPriority.ordinal() >= newPriority.ordinal()) {
                return;
            }

            this.taskPriority = newPriority;
            PriorityQueuedExecutor.PendingTask<Void> loadTask = this.loadTask;
            PriorityQueuedExecutor.PendingTask<Chunk> genTask = this.genTask;
            if (loadTask != null) {
                loadTask.bumpPriority(newPriority);
            }
            if (genTask != null) {
                genTask.bumpPriority(newPriority);
            }
        }

        public synchronized boolean isCancelled() {
            return requests.get() <= 0;
        }

        public synchronized void cancel(PendingChunkRequest request) {
            synchronized (pendingChunks) {
                if (!request.cancelled.compareAndSet(false, true)) {
                    return;
                }

                if (requests.decrementAndGet() > 0) {
                    return;
                }

                boolean c1 = genTask.cancel();
                boolean c2 = loadTask.cancel();
                loadTask = null;
                genTask = null;
                pendingChunks.remove(key);
                setStatus(PendingStatus.CANCELLED);
            }
        }
    }

}
