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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderServer implements IChunkProvider {

    private static final Logger a = LogManager.getLogger();
    public final LongSet unloadQueue = new LongOpenHashSet();
    public final ChunkGenerator<?> chunkGenerator;
    public final IChunkLoader chunkLoader;
    public final Long2ObjectMap<Chunk> chunks = Long2ObjectMaps.synchronize(new ChunkMap(8192));
    private Chunk lastChunk;
    private final ChunkTaskScheduler chunkScheduler;
    private final SchedulerBatch<ChunkCoordIntPair, ChunkStatus, ProtoChunk> batchScheduler;
    public final WorldServer world;
    private final IAsyncTaskHandler asyncTaskHandler;

    public ChunkProviderServer(WorldServer worldserver, IChunkLoader ichunkloader, ChunkGenerator<?> chunkgenerator, IAsyncTaskHandler iasynctaskhandler) {
        this.world = worldserver;
        this.chunkLoader = ichunkloader;
        this.chunkGenerator = chunkgenerator;
        this.asyncTaskHandler = iasynctaskhandler;
        this.chunkScheduler = new ChunkTaskScheduler(2, worldserver, chunkgenerator, ichunkloader, iasynctaskhandler);
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

    @Nullable
    public Chunk getChunkAt(int i, int j, boolean flag, boolean flag1) {
        IChunkLoader ichunkloader = this.chunkLoader;
        Chunk chunk;

        synchronized (this.chunkLoader) {
            if (this.lastChunk != null && this.lastChunk.getPos().x == i && this.lastChunk.getPos().z == j) {
                return this.lastChunk;
            }

            long k = ChunkCoordIntPair.a(i, j);

            chunk = (Chunk) this.chunks.get(k);
            if (chunk != null) {
                this.lastChunk = chunk;
                return chunk;
            }

            if (flag) {
                try {
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
            try {
                this.batchScheduler.b();
                this.batchScheduler.a(new ChunkCoordIntPair(i, j));
                CompletableFuture<ProtoChunk> completablefuture = this.batchScheduler.c();

                return (Chunk) completablefuture.thenApply(this::a).join();
            } catch (RuntimeException runtimeexception) {
                throw this.a(i, j, (Throwable) runtimeexception);
            }
        } else {
            return null;
        }
    }

    public IChunkAccess a(int i, int j, boolean flag) {
        Chunk chunk = this.getChunkAt(i, j, true, false);

        return (IChunkAccess) (chunk != null ? chunk : (IChunkAccess) this.chunkScheduler.b(new ChunkCoordIntPair(i, j), flag));
    }

    public CompletableFuture<ProtoChunk> a(Iterable<ChunkCoordIntPair> iterable, Consumer<Chunk> consumer) {
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
            this.lastChunk = chunk;
        }

        this.asyncTaskHandler.postToMainThread(chunk::addEntities);
        return chunk;
    }

    public void saveChunk(IChunkAccess ichunkaccess) {
        try {
            ichunkaccess.setLastSaved(this.world.getTime());
            this.chunkLoader.saveChunk(this.world, ichunkaccess);
        } catch (IOException ioexception) {
            ChunkProviderServer.a.error("Couldn't save chunk", ioexception);
        } catch (ExceptionWorldConflict exceptionworldconflict) {
            ChunkProviderServer.a.error("Couldn't save chunk; already in use by another instance of Minecraft?", exceptionworldconflict);
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

            while (objectiterator.hasNext()) {
                Chunk chunk = (Chunk) objectiterator.next();

                if (chunk.c(flag)) {
                    this.saveChunk(chunk);
                    chunk.a(false);
                    ++i;
                    if (i == 24 && !flag) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    public void close() {
        try {
            this.batchScheduler.a();
        } catch (InterruptedException interruptedexception) {
            ChunkProviderServer.a.error("Couldn't stop taskManager", interruptedexception);
        }

    }

    public void c() {
        IChunkLoader ichunkloader = this.chunkLoader;

        synchronized (this.chunkLoader) {
            this.chunkLoader.b();
        }
    }

    public boolean unloadChunks(BooleanSupplier booleansupplier) {
        if (!this.world.savingDisabled) {
            if (!this.unloadQueue.isEmpty()) {
                Iterator<Long> iterator = this.unloadQueue.iterator();

                for (int i = 0; iterator.hasNext() && (booleansupplier.getAsBoolean() || i < 200 || this.unloadQueue.size() > 2000); iterator.remove()) {
                    Long olong = (Long) iterator.next();
                    IChunkLoader ichunkloader = this.chunkLoader;

                    synchronized (this.chunkLoader) {
                        Chunk chunk = (Chunk) this.chunks.get(olong);

                        if (chunk != null) {
                            chunk.removeEntities();
                            this.saveChunk(chunk);
                            this.chunks.remove(olong);
                            this.lastChunk = null;
                            ++i;
                        }
                    }
                }
            }

            this.chunkScheduler.a(booleansupplier);
        }

        return false;
    }

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
        return this.chunks.containsKey(ChunkCoordIntPair.a(i, j));
    }
}
