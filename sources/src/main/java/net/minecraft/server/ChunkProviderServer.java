package net.minecraft.server;

import com.google.common.collect.Lists;

import io.akarin.api.internal.Akari;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import com.destroystokyo.paper.exception.ServerInternalException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.bukkit.event.world.ChunkUnloadEvent;
// CraftBukkit end

/**
 * Akarin Changes Note
 * 1) Lock for event (safety issue)
 */
public class ChunkProviderServer implements IChunkProvider {

    private static final Logger a = LogManager.getLogger();
    public final LongSet unloadQueue = new LongOpenHashSet();
    public final ChunkGenerator<?> chunkGenerator;
    private final IChunkLoader chunkLoader;
    public final Long2ObjectMap<Chunk> chunks = Long2ObjectMaps.synchronize(new ChunkMap(8192));
    private final ChunkTaskScheduler f;
    private final SchedulerBatch<ChunkCoordIntPair, ChunkStatus, ProtoChunk> g;
    // Paper start - chunk save stats
    private long lastQueuedSaves = 0L; // Paper
    private long lastProcessedSaves = 0L; // Paper
    private long lastSaveStatPrinted = System.currentTimeMillis();
    // Paper end
    public final WorldServer world;

    public ChunkProviderServer(WorldServer worldserver, IChunkLoader ichunkloader, ChunkGenerator<?> chunkgenerator, IAsyncTaskHandler iasynctaskhandler) {
        this.world = worldserver;
        this.chunkLoader = ichunkloader;
        this.chunkGenerator = chunkgenerator;
        this.f = new ChunkTaskScheduler(0, worldserver, chunkgenerator, ichunkloader, iasynctaskhandler); // CraftBukkit - very buggy, broken in lots of __subtle__ ways. Same goes for async chunk loading. Also Bukkit API / plugins can't handle async events at all anyway.
        this.g = new SchedulerBatch(this.f);
    }

    public Collection<Chunk> a() {
        return this.chunks.values();
    }

    public void unload(Chunk chunk) {
        if (this.world.worldProvider.a(chunk.locX, chunk.locZ)) {
            this.unloadQueue.add(ChunkCoordIntPair.a(chunk.locX, chunk.locZ));
            chunk.d = true;
        }

    }

    public void b() {
        ObjectIterator objectiterator = this.chunks.values().iterator();

        while (objectiterator.hasNext()) {
            Chunk chunk = (Chunk) objectiterator.next();

            this.unload(chunk);
        }

    }

    @Nullable
    public Chunk getLoadedChunkAt(int i, int j) {
        long k = ChunkCoordIntPair.a(i, j);
        Chunk chunk = (Chunk) this.chunks.get(k);

        if (chunk != null) {
            chunk.d = false;
        }

        return chunk;
    }

    @Nullable
    private Chunk loadChunkAt(int i, int j) {
        try {
            // CraftBukkit - decompile error
            Chunk chunk = this.chunkLoader.a(this.world, i, j, (chunk1) -> {
                chunk1.setLastSaved(this.world.getTime());
                this.chunks.put(ChunkCoordIntPair.a(i, j), chunk1);
            });

            if (chunk != null) {
                chunk.addEntities();
            }

            return chunk;
        } catch (Exception exception) {
            ChunkProviderServer.a.error("Couldn\'t load chunk", exception);
            return null;
        }
    }

    @Nullable
    public Chunk getOrLoadChunkAt(int i, int j) {
        Long2ObjectMap long2objectmap = this.chunks;

        Akari.eventLock.lock(); // Akarin
        try { // Akarin
        synchronized (this.chunks) {
            Chunk chunk = world.paperConfig.allowPermaChunkLoaders ? getLoadedChunkAt(i, j) : getChunkIfLoaded(i, j); // Paper - Configurable perma chunk loaders

            return chunk != null ? chunk : this.loadChunkAt(i, j);
        }
        } finally { Akari.eventLock.unlock(); } // Akarin
    }

    // CraftBukkit start
    public Chunk getChunkIfLoaded(int x, int z) {
        return chunks.get(ChunkCoordIntPair.a(x, z));
    }
    // CraftBukkit end

    public Chunk getChunkAt(int i, int j) {
        Chunk chunk = this.getOrLoadChunkAt(i, j);

        if (chunk != null) {
            return chunk;
        } else {
            try (co.aikar.timings.Timing timing = world.timings.chunkGeneration.startTiming()) {
                ; // Spigot
                chunk = (Chunk) this.generateChunk(i, j).get();
                return chunk;
            } catch (ExecutionException | InterruptedException interruptedexception) {
                throw this.a(i, j, (Throwable) interruptedexception);
            }
        }
    }

    public IChunkAccess d(int i, int j) {
        Long2ObjectMap long2objectmap = this.chunks;

        synchronized (this.chunks) {
            IChunkAccess ichunkaccess = (IChunkAccess) this.chunks.get(ChunkCoordIntPair.a(i, j));

            return ichunkaccess != null ? ichunkaccess : (IChunkAccess) this.f.c((new ChunkCoordIntPair(i, j))); // CraftBukkit - decompile error
        }
    }

    public CompletableFuture<ProtoChunk> a(Iterable<ChunkCoordIntPair> iterable, Consumer<Chunk> consumer) {
        this.g.b();
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) iterator.next();
            Chunk chunk = this.getOrLoadChunkAt(chunkcoordintpair.x, chunkcoordintpair.z);

            if (chunk != null) {
                consumer.accept(chunk);
            } else {
                this.g.a(chunkcoordintpair).thenApply(this::a).thenAccept(consumer);
            }
        }

        return this.g.c();
    }

    // CraftBukkit start
    public CompletableFuture<Chunk> generateChunk(int i, int j) {
        return this.generateChunk(i, j, false);
    }

    public CompletableFuture<Chunk> generateChunk(int i, int j, boolean force) {
        this.g.b();

        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i, j);
        if (force) {
            this.f.forcePolluteCache(chunkcoordintpair);
        }
        this.g.a(chunkcoordintpair);
        // CraftBukkit end
        CompletableFuture<ProtoChunk> completablefuture = this.g.c(); // CraftBukkit - decompile error

        return completablefuture.thenApply(this::a);
    }

    private ReportedException a(int i, int j, Throwable throwable) {
        CrashReport crashreport = CrashReport.a(throwable, "Exception generating new chunk");
        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Chunk to be generated");

        crashreportsystemdetails.a("Location", (Object) String.format("%d,%d", new Object[] { Integer.valueOf(i), Integer.valueOf(j)}));
        crashreportsystemdetails.a("Position hash", (Object) Long.valueOf(ChunkCoordIntPair.a(i, j)));
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
        }

        chunk.addEntities();
        return chunk;
    }

    public void saveChunkNOP(Chunk chunk) {
        try {
            // this.chunkLoader.a(this.world, chunk); // Spigot
        } catch (Exception exception) {
            // Paper start
            String msg = "Couldn\'t save entities";
            ChunkProviderServer.a.error(msg, exception);
            ServerInternalException.reportInternalException(exception);
            // Paper end
        }

    }

    public void saveChunk(IChunkAccess ichunkaccess, boolean unloaded) { // Spigot
        try (co.aikar.timings.Timing timed = world.timings.chunkSaveData.startTiming()) { // Paper - Timings
            ichunkaccess.setLastSaved(this.world.getTime());
            this.chunkLoader.saveChunk(this.world, ichunkaccess, unloaded); // Spigot
        } catch (IOException ioexception) {
            // Paper start
            String msg = "Couldn\'t save chunk";
            ChunkProviderServer.a.error(msg, ioexception);
            ServerInternalException.reportInternalException(ioexception);
        } catch (ExceptionWorldConflict exceptionworldconflict) {
            String msg = "Couldn\'t save chunk; already in use by another instance of Minecraft?";
            ChunkProviderServer.a.error(msg, exceptionworldconflict);
            ServerInternalException.reportInternalException(exceptionworldconflict);
        }

    }

    public boolean a(boolean flag) {
        int i = 0;

        this.f.a();
        ArrayList arraylist = Lists.newArrayList(this.chunks.values());
        Iterator iterator = arraylist.iterator();

        // Paper start
        final ChunkRegionLoader chunkLoader = (ChunkRegionLoader) world.getChunkProviderServer().chunkLoader;
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

        if (queueSize > world.paperConfig.queueSizeAutoSaveThreshold){
            return false;
        }
        final int autoSaveLimit = world.paperConfig.maxAutoSaveChunksPerTick;
        // Paper end
        while (iterator.hasNext()) {
            Chunk chunk = (Chunk) iterator.next();

            if (flag) {
                this.saveChunkNOP(chunk);
            }

            if (chunk.c(flag)) {
                this.saveChunk(chunk, false); // Spigot
                chunk.a(false);
                ++i;
                if (!flag && i >= autoSaveLimit) { // Spigot - // Paper - Incremental Auto Save - cap max per tick
                    return false;
                }
            }
        }

        return true;
    }

    public void close() {
        try {
            this.g.a();
        } catch (InterruptedException interruptedexception) {
            ChunkProviderServer.a.error("Couldn\'t stop taskManager", interruptedexception);
        }

    }

    public void c() {
        this.chunkLoader.c();
    }

    private static final double UNLOAD_QUEUE_RESIZE_FACTOR = 0.96;

    public boolean unloadChunks() {
        if (!this.world.savingDisabled) {
            if (!this.unloadQueue.isEmpty()) {
                // Spigot start
                org.spigotmc.SlackActivityAccountant activityAccountant = this.world.getMinecraftServer().slackActivityAccountant;
                activityAccountant.startActivity(0.5);
                int targetSize = Math.min(this.unloadQueue.size() - 100,  (int) (this.unloadQueue.size() * UNLOAD_QUEUE_RESIZE_FACTOR)); // Paper - Make more aggressive
                // Spigot end

                LongIterator longiterator = this.unloadQueue.iterator();

                while (longiterator.hasNext()) { // Spigot
                    Long olong = (Long) longiterator.next();
                    longiterator.remove(); // Spigot
                    Chunk chunk = (Chunk) this.chunks.get(olong);

                    if (chunk != null && chunk.d) {
                        // CraftBukkit start - move unload logic to own method
                        chunk.setShouldUnload(false); // Paper
                        if (!unloadChunk(chunk, true)) {
                            continue;
                        }
                        // CraftBukkit end

                        // Spigot start
                        if (this.unloadQueue.size() <= targetSize && activityAccountant.activityTimeIsExhausted()) {
                            break;
                        }
                        // Spigot end
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

            this.f.a();
            this.chunkLoader.b();
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

                Chunk neighbor = this.getChunkIfLoaded(chunk.locX + x, chunk.locZ + z);
                if (neighbor != null) {
                    neighbor.setNeighborUnloaded(-x, -z);
                    chunk.setNeighborUnloaded(x, z);
                }
            }
        }
        // Moved from unloadChunks above
        chunk.removeEntities();
        if (save) {
            this.saveChunk(chunk, true); // Spigot
            this.saveChunkNOP(chunk);
        }
        this.chunks.remove(chunk.chunkKey);
        return true;
    }
    // CraftBukkit end

    public boolean e() {
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
    public BlockPosition a(World world, String s, BlockPosition blockposition, int i) {
        return this.chunkGenerator.findNearestMapFeature(world, s, blockposition, i);
    }

    public ChunkGenerator<?> getChunkGenerator() {
        return this.chunkGenerator;
    }

    public int h() {
        return this.chunks.size();
    }

    public boolean isLoaded(int i, int j) {
        return this.chunks.containsKey(ChunkCoordIntPair.a(i, j));
    }

    public boolean f(int i, int j) {
        return this.chunks.containsKey(ChunkCoordIntPair.a(i, j)) || this.chunkLoader.chunkExists(i, j);
    }
}
