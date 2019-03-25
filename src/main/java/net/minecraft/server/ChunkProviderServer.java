package net.minecraft.server;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import com.destroystokyo.paper.exception.ServerInternalException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;
import org.bukkit.event.world.ChunkUnloadEvent;
// CraftBukkit end

public class ChunkProviderServer implements IChunkProvider {

    private static final Logger a = LogManager.getLogger();
    public final LongSet unloadQueue = new LongOpenHashSet();
    public final ChunkGenerator<?> chunkGenerator;
    public final IChunkLoader chunkLoader;
    // Paper start - chunk save stats
    private long lastQueuedSaves = 0L; // Paper
    private long lastProcessedSaves = 0L; // Paper
    private long lastSaveStatPrinted = System.currentTimeMillis();
    // Paper end
    public final Long2ObjectMap<Chunk> chunks = new ChunkMap(8192); // Paper - remove synchronize - we keep everything on main for manip
    private Chunk lastChunk;
    private final ChunkTaskScheduler chunkScheduler;
    final SchedulerBatch<ChunkCoordIntPair, ChunkStatus, ProtoChunk> batchScheduler; // Paper
    public final WorldServer world;
    final IAsyncTaskHandler asyncTaskHandler; // Paper

    public ChunkProviderServer(WorldServer worldserver, IChunkLoader ichunkloader, ChunkGenerator<?> chunkgenerator, IAsyncTaskHandler iasynctaskhandler) {
        this.world = worldserver;
        this.chunkLoader = ichunkloader;
        this.chunkGenerator = chunkgenerator;
        this.asyncTaskHandler = iasynctaskhandler;
        this.chunkScheduler = new ChunkTaskScheduler(0, worldserver, chunkgenerator, ichunkloader, iasynctaskhandler); // CraftBukkit - very buggy, broken in lots of __subtle__ ways. Same goes for async chunk loading. Also Bukkit API / plugins can't handle async events at all anyway.
        this.batchScheduler = new SchedulerBatch<>(this.chunkScheduler);
    }

    public Collection<Chunk> a() {
        return this.chunks.values();
    }

    public void unload(Chunk chunk) {
        if (this.world.worldProvider.a(chunk.locX, chunk.locZ)) {
            this.unloadQueue.add(ChunkCoordIntPair.a(chunk.locX, chunk.locZ));
        }

    }

    public void b() {
        ObjectIterator objectiterator = this.chunks.values().iterator();

        while (objectiterator.hasNext()) {
            Chunk chunk = (Chunk) objectiterator.next();

            this.unload(chunk);
        }

    }

    public void a(int i, int j) {
        this.unloadQueue.remove(ChunkCoordIntPair.a(i, j));
    }

    // Paper start - defaults if Async Chunks is not enabled
    boolean chunkGoingToExists(int x, int z) {
        final long k = ChunkCoordIntPair.asLong(x, z);
        return chunkScheduler.progressCache.containsKey(k);
    }
    public void bumpPriority(ChunkCoordIntPair coords) {
        // do nothing, override in async
    }

    public List<ChunkCoordIntPair> getSpiralOutChunks(BlockPosition blockposition, int radius) {
        List<ChunkCoordIntPair> list = com.google.common.collect.Lists.newArrayList();

        list.add(new ChunkCoordIntPair(blockposition.getX() >> 4, blockposition.getZ() >> 4));
        for (int r = 1; r <= radius; r++) {
            int x = -r;
            int z = r;

            // Iterates the edge of half of the box; then negates for other half.
            while (x <= r && z > -r) {
                list.add(new ChunkCoordIntPair((blockposition.getX() + (x << 4)) >> 4, (blockposition.getZ() + (z << 4)) >> 4));
                list.add(new ChunkCoordIntPair((blockposition.getX() - (x << 4)) >> 4, (blockposition.getZ() - (z << 4)) >> 4));

                if (x < r) {
                    x++;
                } else {
                    z--;
                }
            }
        }
        return list;
    }

    public Chunk getChunkAt(int x, int z, boolean load, boolean gen, Consumer<Chunk> consumer) {
        return getChunkAt(x, z, load, gen, false, consumer);
    }
    public Chunk getChunkAt(int x, int z, boolean load, boolean gen, boolean priority, Consumer<Chunk> consumer) {
        Chunk chunk = getChunkAt(x, z, load, gen);
        if (consumer != null) {
            consumer.accept(chunk);
        }
        return chunk;
    }

    PaperAsyncChunkProvider.CancellableChunkRequest requestChunk(int x, int z, boolean gen, boolean priority, Consumer<Chunk> consumer) {
        Chunk chunk = getChunkAt(x, z, gen, priority, consumer);
        return new PaperAsyncChunkProvider.CancellableChunkRequest() {
            @Override
            public void cancel() {

            }

            @Override
            public Chunk getChunk() {
                return chunk;
            }
        };
    }
    // Paper end

    @Nullable
    public Chunk getChunkAt(int i, int j, boolean flag, boolean flag1) {
        IChunkLoader ichunkloader = this.chunkLoader;
        Chunk chunk;
        // Paper start - do already loaded checks before synchronize
        long k = ChunkCoordIntPair.a(i, j);
        chunk = (Chunk) this.chunks.get(k);
        if (chunk != null) {
            //this.lastChunk = chunk; // Paper remove vanilla lastChunk
            return chunk;
        }
        // Paper end

        synchronized (this.chunkLoader) {
            // Paper start - remove vanilla lastChunk, we do it more accurately
            /* if (this.lastChunk != null && this.lastChunk.locX == i && this.lastChunk.locZ == j) {
                return this.lastChunk;
            }*/ // Paper end

            // Paper start - move up
            //long k = ChunkCoordIntPair.a(i, j);

            /*chunk = (Chunk) this.chunks.get(k);
            if (chunk != null) {
                //this.lastChunk = chunk; // Paper remove vanilla lastChunk
                return chunk;
            }*/
            // Paper end

            if (flag) {
                try (co.aikar.timings.Timing timing = world.timings.syncChunkLoadTimer.startTiming()) { // Paper
                    chunk = this.chunkLoader.a(this.world, i, j, (chunk1) -> {
                        chunk1.setLastSaved(this.world.getTime());
                        this.chunks.put(ChunkCoordIntPair.a(i, j), chunk1);
                    });
                } catch (Exception exception) {
                    ChunkProviderServer.a.error("Couldn't load chunk", exception);
                }
            }
        }

        if (chunk != null) {
            this.asyncTaskHandler.postToMainThread(chunk::addEntities);
            return chunk;
        } else if (flag1) {
            try (co.aikar.timings.Timing timing = world.timings.chunkGeneration.startTiming(true)) { // Paper // Akarin
                this.batchScheduler.b();
                this.batchScheduler.a(new ChunkCoordIntPair(i, j));
                CompletableFuture<ProtoChunk> completablefuture = this.batchScheduler.c();

                return (Chunk) completablefuture.thenApply(this::a).join();
            } catch (RuntimeException runtimeexception) {
                throw this.a(i, j, (Throwable) runtimeexception);
            }
            // finally { world.timings.syncChunkLoadTimer.stopTiming(); } // Spigot // Paper
        } else {
            return null;
        }
    }

    // CraftBukkit start
    public Chunk generateChunk(int x, int z) {
        try {
            this.batchScheduler.b();
            ChunkCoordIntPair pos = new ChunkCoordIntPair(x, z);
            this.chunkScheduler.forcePolluteCache(pos);
            ((ChunkRegionLoader) this.chunkLoader).blacklist.add(pos.a());
            this.batchScheduler.a(pos);
            CompletableFuture<ProtoChunk> completablefuture = this.batchScheduler.c();

            Chunk chunk = (Chunk) completablefuture.thenApply(this::a).join();
            ((ChunkRegionLoader) this.chunkLoader).blacklist.remove(pos.a());
            return chunk;
        } catch (RuntimeException runtimeexception) {
            throw this.a(x, z, (Throwable) runtimeexception);
        }
    }
    // CraftBukkit end

    public IChunkAccess a(int i, int j, boolean flag) {
        Chunk chunk = this.getChunkAt(i, j, true, false);

        return (IChunkAccess) (chunk != null ? chunk : (IChunkAccess) this.chunkScheduler.b(new ChunkCoordIntPair(i, j), flag));
    }

    public CompletableFuture<Void> loadAllChunks(Iterable<ChunkCoordIntPair> iterable, Consumer<Chunk> consumer) { return a(iterable, consumer).thenCompose(protoChunk -> null); } // Paper - overriden in async chunk provider
    private CompletableFuture<ProtoChunk> a(Iterable<ChunkCoordIntPair> iterable, Consumer<Chunk> consumer) { // Paper - mark private, use above method
        this.batchScheduler.b();
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) iterator.next();
            Chunk chunk = this.getChunkAt(chunkcoordintpair.x, chunkcoordintpair.z, true, false);

            if (chunk != null) {
                consumer.accept(chunk);
            } else {
                this.batchScheduler.a(chunkcoordintpair).thenApply(this::a).thenAccept(consumer);
            }
        }

        return this.batchScheduler.c();
    }

    ReportedException generateChunkError(int i, int j, Throwable throwable) { return a(i, j, throwable); } // Paper - OBFHELPER
    private ReportedException a(int i, int j, Throwable throwable) {
        CrashReport crashreport = CrashReport.a(throwable, "Exception generating new chunk");
        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Chunk to be generated");

        crashreportsystemdetails.a("Location", (Object) String.format("%d,%d", i, j));
        crashreportsystemdetails.a("Position hash", (Object) ChunkCoordIntPair.a(i, j));
        crashreportsystemdetails.a("Generator", (Object) this.chunkGenerator);
        return new ReportedException(crashreport);
    }

    private Chunk a(IChunkAccess ichunkaccess) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int i = chunkcoordintpair.x;
        int j = chunkcoordintpair.z;
        long k = ChunkCoordIntPair.a(i, j);
        Long2ObjectMap long2objectmap = this.chunks;
        Chunk chunk;

        synchronized (this.chunks) {
            Chunk chunk1 = (Chunk) this.chunks.get(k);

            if (chunk1 != null) {
                return chunk1;
            }

            if (ichunkaccess instanceof Chunk) {
                chunk = (Chunk) ichunkaccess;
            } else {
                if (!(ichunkaccess instanceof ProtoChunk)) {
                    throw new IllegalStateException();
                }

                chunk = new Chunk(this.world, (ProtoChunk) ichunkaccess, i, j);
            }

            this.chunks.put(k, chunk);
            //this.lastChunk = chunk; // Paper
        }

        this.asyncTaskHandler.postToMainThread(chunk::addEntities);
        return chunk;
    }

    public void saveChunk(IChunkAccess ichunkaccess, boolean unloaded) { // Spigot
        try (co.aikar.timings.Timing timed = world.timings.chunkSaveData.startTimingUnsafe()) { // Paper - Timings
            ichunkaccess.setLastSaved(this.world.getTime());
            this.chunkLoader.saveChunk(this.world, ichunkaccess, unloaded); // Spigot
        } catch (IOException ioexception) {
            // Paper start
            String msg = "Couldn\'t save chunk";
            ChunkProviderServer.a.error(msg, ioexception);
            ServerInternalException.reportInternalException(ioexception);
        } catch (ExceptionWorldConflict exceptionworldconflict) {
            ChunkProviderServer.a.error("Couldn\'t save chunk; already in use by another instance of Minecraft?", exceptionworldconflict);
            String msg = "Couldn\'t save chunk; already in use by another instance of Minecraft?";
            ChunkProviderServer.a.error(msg, exceptionworldconflict);
            ServerInternalException.reportInternalException(exceptionworldconflict);
            // Paper end
        }

    }

    public boolean a(boolean flag) {
        int i = 0;

        this.chunkScheduler.a(() -> {
            return true;
        });
        IChunkLoader ichunkloader = this.chunkLoader;

        synchronized (this.chunkLoader) {
            ObjectIterator objectiterator = this.chunks.values().iterator();

            // Paper start
            final ChunkRegionLoader chunkLoader = (ChunkRegionLoader) world.getChunkProvider().chunkLoader;
            final int queueSize = chunkLoader.getQueueSize();

            final long now = System.currentTimeMillis();
            final long timeSince = (now - lastSaveStatPrinted) / 1000;
            final Integer printRateSecs = Integer.getInteger("printSaveStats");
            if (printRateSecs != null && timeSince >= printRateSecs) {
                final String timeStr = "/" + timeSince  +"s";
                final long queuedSaves = chunkLoader.getQueuedSaves();
                long queuedDiff = queuedSaves - lastQueuedSaves;
                lastQueuedSaves = queuedSaves;

                final long processedSaves = chunkLoader.getProcessedSaves();
                long processedDiff = processedSaves - lastProcessedSaves;
                lastProcessedSaves = processedSaves;

                lastSaveStatPrinted = now;
                if (processedDiff > 0 || queueSize > 0 || queuedDiff > 0) {
                    System.out.println("[Chunk Save Stats] " + world.worldData.getName() +
                        " - Current: " + queueSize +
                        " - Queued: " + queuedDiff + timeStr +
                        " - Processed: " +processedDiff + timeStr
                    );
                }
            }
            if (!flag && queueSize > world.paperConfig.queueSizeAutoSaveThreshold){
                return false;
            }
            // Paper end
            while (objectiterator.hasNext()) {
                Chunk chunk = (Chunk) objectiterator.next();

                if (chunk.c(flag)) {
                    this.saveChunk(chunk, false); // Spigot
                    chunk.a(false);
                    ++i;
                    if (!flag && i >= world.paperConfig.maxAutoSaveChunksPerTick) { // Spigot - // Paper - Incremental Auto Save - cap max
                        return false;
                    }
                }
            }

            return true;
        }
    }

    public void close() {
        // Paper start - we do not need to wait for chunk generations to finish on close
        /*try {
            this.batchScheduler.a();
        } catch (InterruptedException interruptedexception) {
            ChunkProviderServer.a.error("Couldn't stop taskManager", interruptedexception);
        }*/
        // Paper end

    }

    public void c() {
        IChunkLoader ichunkloader = this.chunkLoader;

        synchronized (this.chunkLoader) {
            this.chunkLoader.b();
        }
    }

    private static final double UNLOAD_QUEUE_RESIZE_FACTOR = 0.96; // Spigot

    public boolean unloadChunks(BooleanSupplier booleansupplier) {
        if (!this.world.savingDisabled) {
            if (!this.unloadQueue.isEmpty()) {
                // Spigot start
                org.spigotmc.SlackActivityAccountant activityAccountant = this.world.getMinecraftServer().slackActivityAccountant;
                activityAccountant.startActivity(0.5);
                int targetSize = Math.min(this.unloadQueue.size() - 100,  (int) (this.unloadQueue.size() * UNLOAD_QUEUE_RESIZE_FACTOR)); // Paper - Make more aggressive
                // Spigot end
                Iterator<Long> iterator = this.unloadQueue.iterator();

                while (iterator.hasNext()) { // Spigot
                    Long olong = (Long) iterator.next();
                    iterator.remove(); // Spigot
                    IChunkLoader ichunkloader = this.chunkLoader;

                    synchronized (this.chunkLoader) {
                        Chunk chunk = (Chunk) this.chunks.get(olong);

                        if (chunk != null) {
                            // CraftBukkit start - move unload logic to own method
                            if (!unloadChunk(chunk, true)) {
                                continue;
                            }
                            // CraftBukkit end

                            // Spigot start
                            if (!booleansupplier.getAsBoolean() && this.unloadQueue.size() <= targetSize && activityAccountant.activityTimeIsExhausted()) {
                                break;
                            }
                            // Spigot end
                        }
                    }
                }
                activityAccountant.endActivity(); // Spigot
            }
            // Paper start - delayed chunk unloads
            long now = System.currentTimeMillis();
            long unloadAfter = world.paperConfig.delayChunkUnloadsBy;
            if (unloadAfter > 0) {
                //noinspection Convert2streamapi
                for (Chunk chunk : chunks.values()) {
                    if (chunk.scheduledForUnload != null && now - chunk.scheduledForUnload > unloadAfter) {
                        chunk.scheduledForUnload = null;
                        unload(chunk);
                    }
                }
            }
            // Paper end

            this.chunkScheduler.a(booleansupplier);
        }

        return false;
    }

    // CraftBukkit start
    public boolean unloadChunk(Chunk chunk, boolean save) {
        ChunkUnloadEvent event = new ChunkUnloadEvent(chunk.bukkitChunk, save);
        this.world.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        save = event.isSaveChunk();
        chunk.lightingQueue.processUnload(); // Paper

        // Update neighbor counts
        for (int x = -2; x < 3; x++) {
            for (int z = -2; z < 3; z++) {
                if (x == 0 && z == 0) {
                    continue;
                }

                Chunk neighbor = this.chunks.get(chunk.chunkKey); // Paper
                if (neighbor != null) {
                    neighbor.setNeighborUnloaded(-x, -z);
                    chunk.setNeighborUnloaded(x, z);
                }
            }
        }
        // Moved from unloadChunks above
        synchronized (this.chunkLoader) {
            chunk.removeEntities();
            if (save) {
                this.saveChunk(chunk, true); // Spigot
            }
            this.chunks.remove(chunk.chunkKey);
            // this.lastChunk = null; // Paper
        }
        return true;
    }
    // CraftBukkit end

    public boolean d() {
        return !this.world.savingDisabled;
    }

    public String getName() {
        return "ServerChunkCache: " + this.chunks.size() + " Drop: " + this.unloadQueue.size();
    }

    public List<BiomeBase.BiomeMeta> a(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        return this.chunkGenerator.getMobsFor(enumcreaturetype, blockposition);
    }

    public int a(World world, boolean flag, boolean flag1) {
        return this.chunkGenerator.a(world, flag, flag1);
    }

    @Nullable
    public BlockPosition a(World world, String s, BlockPosition blockposition, int i, boolean flag) {
        return this.chunkGenerator.findNearestMapFeature(world, s, blockposition, i, flag);
    }

    public ChunkGenerator<?> getChunkGenerator() {
        return this.chunkGenerator;
    }

    public int g() {
        return this.chunks.size();
    }

    public boolean isLoaded(int i, int j) {
        return this.chunks.get(ChunkCoordIntPair.asLong(i, j)) != null; // Paper - use get for last access
    }
}
