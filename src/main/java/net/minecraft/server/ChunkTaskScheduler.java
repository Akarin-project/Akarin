package net.minecraft.server;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkTaskScheduler extends Scheduler<ChunkCoordIntPair, ChunkStatus, ProtoChunk> {

    private static final Logger b = LogManager.getLogger();
    private final World c; private final World getWorld() { return this.c; } // Paper - OBFHELPER
    private final ChunkGenerator<?> d;
    private final IChunkLoader e;
    private final IAsyncTaskHandler f;
    protected final Long2ObjectMap<Scheduler<ChunkCoordIntPair, ChunkStatus, ProtoChunk>.a> progressCache = new ExpiringMap<Scheduler<ChunkCoordIntPair, ChunkStatus, ProtoChunk>.a>(8192, 5000) { // Paper - protected
        protected boolean a(Scheduler<ChunkCoordIntPair, ChunkStatus, ProtoChunk>.a scheduler_a) {
            ProtoChunk protochunk = (ProtoChunk) scheduler_a.a();

            return !protochunk.ab_() /*&& !protochunk.h()*/; // Paper
        }
    };
    private final Long2ObjectMap<java.util.concurrent.CompletableFuture<Scheduler.a>> pendingSchedulers = new it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap<>(); // Paper

    public ChunkTaskScheduler(int i, World world, ChunkGenerator<?> chunkgenerator, IChunkLoader ichunkloader, IAsyncTaskHandler iasynctaskhandler) {
        super("WorldGen", i, ChunkStatus.FINALIZED, () -> {
            return new EnumMap(ChunkStatus.class);
        }, () -> {
            return new EnumMap(ChunkStatus.class);
        });
        this.c = world;
        this.d = chunkgenerator;
        this.e = ichunkloader;
        this.f = iasynctaskhandler;
    }

    // CraftBukkit start
    public void forcePolluteCache(ChunkCoordIntPair chunkcoordintpair) {
        this.progressCache.put(chunkcoordintpair.a(), new Scheduler.a(chunkcoordintpair, new ProtoChunk(chunkcoordintpair, ChunkConverter.a, this.getWorld()), ChunkStatus.EMPTY)); // Paper - Anti-Xray
    }
    // CraftBukkit end

    @Nullable
    protected Scheduler<ChunkCoordIntPair, ChunkStatus, ProtoChunk>.a a(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        IChunkLoader ichunkloader = this.e;

        // Paper start - refactor a lot of this - avoid generating a chunk while holding lock on expiring map
        java.util.concurrent.CompletableFuture<Scheduler.a> pending = null;
        boolean created = false;
        long key = chunkcoordintpair.a();
        synchronized (pendingSchedulers) {
            Scheduler.a existing = this.progressCache.get(key);
            if (existing != null) {
                return existing;
            }
            pending = this.pendingSchedulers.get(key);
            if (pending == null) {
                if (!flag) {
                    return null;
                }
                created = true;
                pending = new java.util.concurrent.CompletableFuture<>();
                pendingSchedulers.put(key, pending);
            }
        }
        if (created) {
            java.util.function.Function<Long, Scheduler.a> get = (i) -> {
                // Paper end
                ProtoChunk protochunk;

                try {
                    protochunk = this.e.b(this.c, chunkcoordintpair.x, chunkcoordintpair.z, (ichunkaccess) -> {
                    });
                } catch (ReportedException reportedexception) {
                    throw reportedexception;
                } catch (Exception exception) {
                    ChunkTaskScheduler.b.error("Couldn't load protochunk", exception);
                    protochunk = null;
                }

                if (protochunk != null) {
                    protochunk.setLastSaved(this.c.getTime());
                    return new Scheduler.a(chunkcoordintpair, protochunk, protochunk.i());
                } else {
                    return new Scheduler.a(chunkcoordintpair, new ProtoChunk(chunkcoordintpair, ChunkConverter.a, this.getWorld()), ChunkStatus.EMPTY); // Paper - Anti-Xray
                }
                // Paper start
            };
            Scheduler.a scheduler = get.apply(key);
            progressCache.put(key, scheduler);
            pending.complete(scheduler);
            synchronized (pendingSchedulers) {
                pendingSchedulers.remove(key);
            }
            return scheduler;
        }
        return pending.join();
        // Paper end
    }

    protected ProtoChunk a(ChunkCoordIntPair chunkcoordintpair, ChunkStatus chunkstatus, Map<ChunkCoordIntPair, ProtoChunk> map) {
        return chunkstatus.a(this.c, this.d, map, chunkcoordintpair.x, chunkcoordintpair.z);
    }

    protected Scheduler<ChunkCoordIntPair, ChunkStatus, ProtoChunk>.a a(ChunkCoordIntPair chunkcoordintpair, Scheduler<ChunkCoordIntPair, ChunkStatus, ProtoChunk>.a scheduler_a) {
        ((ProtoChunk) scheduler_a.a()).a(1);
        return scheduler_a;
    }

    protected void b(ChunkCoordIntPair chunkcoordintpair, Scheduler<ChunkCoordIntPair, ChunkStatus, ProtoChunk>.a scheduler_a) {
        ((ProtoChunk) scheduler_a.a()).a(-1);
    }

    public void a(BooleanSupplier booleansupplier) {
        if (true) return; // Paper - we don't save proto chunks, and don't want to block thread
        IChunkLoader ichunkloader = this.e;

        synchronized (this.e) {
            ObjectIterator objectiterator = this.progressCache.values().iterator();

            do {
                if (!objectiterator.hasNext()) {
                    return;
                }

                Scheduler<ChunkCoordIntPair, ChunkStatus, ProtoChunk>.a scheduler_a = (Scheduler.a) objectiterator.next();
                ProtoChunk protochunk = (ProtoChunk) scheduler_a.a();

                if (protochunk.h() && protochunk.i().d() == ChunkStatus.Type.PROTOCHUNK) {
                    try {
                        protochunk.setLastSaved(this.c.getTime());
                        this.e.saveChunk(this.c, protochunk);
                        protochunk.a(false);
                    } catch (IOException ioexception) {
                        ChunkTaskScheduler.b.error("Couldn't save chunk", ioexception);
                    } catch (ExceptionWorldConflict exceptionworldconflict) {
                        ChunkTaskScheduler.b.error("Couldn't save chunk; already in use by another instance of Minecraft?", exceptionworldconflict);
                    }
                }
            } while (booleansupplier.getAsBoolean());

        }
    }
}
