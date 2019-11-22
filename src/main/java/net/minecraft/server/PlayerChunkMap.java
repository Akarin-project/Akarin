package net.minecraft.server;

import co.aikar.timings.Timing; // Paper
import com.destroystokyo.paper.PaperWorldConfig; // Paper
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.ComparisonChain; // Paper
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap; // Paper
import java.util.Iterator;
import java.util.List;
import java.util.Map; // Paper
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.UUID; // Paper
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player; // CraftBukkit

public class PlayerChunkMap extends IChunkLoader implements PlayerChunk.d {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final int GOLDEN_TICKET = 33 + ChunkStatus.b();
    public final Long2ObjectLinkedOpenHashMap<PlayerChunk> updatingChunks = new Long2ObjectLinkedOpenHashMap();
    public volatile Long2ObjectLinkedOpenHashMap<PlayerChunk> visibleChunks;
    private final Long2ObjectLinkedOpenHashMap<PlayerChunk> pendingUnload;
    final LongSet loadedChunks; // Paper - private -> package
    public final WorldServer world;
    private final LightEngineThreaded lightEngine;
    private final IAsyncTaskHandler<Runnable> executor;
    public final ChunkGenerator<?> chunkGenerator;
    private final Supplier<WorldPersistentData> m; public final Supplier<WorldPersistentData> getWorldPersistentDataSupplier() { return this.m; } // Paper - OBFHELPER
    private final VillagePlace n;
    public final LongSet unloadQueue;
    private boolean updatingChunksModified;
    private final ChunkTaskQueueSorter q;
    private final Mailbox<ChunkTaskQueueSorter.a<Runnable>> mailboxWorldGen;
    private final Mailbox<ChunkTaskQueueSorter.a<Runnable>> mailboxMain;
    public final WorldLoadListener worldLoadListener;
    public final PlayerChunkMap.a chunkDistanceManager; public final PlayerChunkMap.a getChunkMapDistanceManager() { return this.chunkDistanceManager; } // Paper - OBFHELPER
    private final AtomicInteger v;
    public final DefinedStructureManager definedStructureManager; // Paper - private -> public
    private final File x;
    private final PlayerMap playerMap;
    public final Int2ObjectMap<PlayerChunkMap.EntityTracker> trackedEntities;
    private final Queue<Runnable> A;
    int viewDistance; // Paper - private -> package private
    public final com.destroystokyo.paper.util.PlayerMobDistanceMap playerMobDistanceMap; // Paper

    // CraftBukkit start - recursion-safe executor for Chunk loadCallback() and unloadCallback()
    public final CallbackExecutor callbackExecutor = new CallbackExecutor();
    public static final class CallbackExecutor implements java.util.concurrent.Executor, Runnable {

        private Runnable queued;

        @Override
        public void execute(Runnable runnable) {
            if (queued != null) {
                throw new IllegalStateException("Already queued");
            }
            queued = runnable;
        }

        @Override
        public void run() {
            Runnable task = queued;
            queued = null;
            if (task != null) {
                task.run();
            }
        }
    };
    // CraftBukkit end

    public PlayerChunkMap(WorldServer worldserver, File file, DataFixer datafixer, DefinedStructureManager definedstructuremanager, Executor executor, IAsyncTaskHandler<Runnable> iasynctaskhandler, ILightAccess ilightaccess, ChunkGenerator<?> chunkgenerator, WorldLoadListener worldloadlistener, Supplier<WorldPersistentData> supplier, int i) {
        super(new File(worldserver.getWorldProvider().getDimensionManager().a(file), "region"), datafixer);
        this.visibleChunks = this.updatingChunks.clone();
        this.pendingUnload = new Long2ObjectLinkedOpenHashMap();
        this.loadedChunks = new LongOpenHashSet();
        this.unloadQueue = new LongOpenHashSet();
        this.v = new AtomicInteger();
        this.playerMap = new PlayerMap();
        this.trackedEntities = new Int2ObjectOpenHashMap();
        this.A = new com.destroystokyo.paper.utils.CachedSizeConcurrentLinkedQueue<>(); // Paper
        this.definedStructureManager = definedstructuremanager;
        this.x = worldserver.getWorldProvider().getDimensionManager().a(file);
        this.world = worldserver;
        this.chunkGenerator = chunkgenerator;
        this.executor = iasynctaskhandler;
        ThreadedMailbox<Runnable> threadedmailbox = ThreadedMailbox.a(executor, "worldgen");

        iasynctaskhandler.getClass();
        Mailbox<Runnable> mailbox = Mailbox.a("main", iasynctaskhandler::a);

        this.worldLoadListener = worldloadlistener;
        ThreadedMailbox<Runnable> threadedmailbox1 = ThreadedMailbox.a(executor, "light");

        this.q = new ChunkTaskQueueSorter(ImmutableList.of(threadedmailbox, mailbox, threadedmailbox1), executor, Integer.MAX_VALUE);
        this.mailboxWorldGen = this.q.a(threadedmailbox, false);
        this.mailboxMain = this.q.a(mailbox, false);
        this.lightEngine = new LightEngineThreaded(ilightaccess, this, this.world.getWorldProvider().g(), threadedmailbox1, this.q.a(threadedmailbox1, false));
        this.chunkDistanceManager = new PlayerChunkMap.a(executor, iasynctaskhandler);
        this.m = supplier;
        this.n = new VillagePlace(new File(this.x, "poi"), datafixer, this.world); // Paper
        this.setViewDistance(i);
        this.playerMobDistanceMap = this.world.paperConfig.perPlayerMobSpawns ? new com.destroystokyo.paper.util.PlayerMobDistanceMap() : null; // Paper
    }

    public void updatePlayerMobTypeMap(Entity entity) {
        if (!this.world.paperConfig.perPlayerMobSpawns) {
            return;
        }
        int chunkX = (int)Math.floor(entity.locX) >> 4;
        int chunkZ = (int)Math.floor(entity.locZ) >> 4;
        int index = entity.getEntityType().getEnumCreatureType().ordinal();

        for (EntityPlayer player : this.playerMobDistanceMap.getPlayersInRange(chunkX, chunkZ)) {
            ++player.mobCounts[index];
        }
    }

    public int getMobCountNear(EntityPlayer entityPlayer, EnumCreatureType enumCreatureType) {
        return entityPlayer.mobCounts[enumCreatureType.ordinal()];
    }

    private static double a(ChunkCoordIntPair chunkcoordintpair, Entity entity) {
        double d0 = (double) (chunkcoordintpair.x * 16 + 8);
        double d1 = (double) (chunkcoordintpair.z * 16 + 8);
        double d2 = d0 - entity.locX;
        double d3 = d1 - entity.locZ;

        return d2 * d2 + d3 * d3;
    }

    private static int b(ChunkCoordIntPair chunkcoordintpair, EntityPlayer entityplayer, boolean flag) {
        int i;
        int j;

        if (flag) {
            SectionPosition sectionposition = entityplayer.M();

            i = sectionposition.a();
            j = sectionposition.c();
        } else {
            i = MathHelper.floor(entityplayer.locX / 16.0D);
            j = MathHelper.floor(entityplayer.locZ / 16.0D);
        }

        return a(chunkcoordintpair, i, j);
    }

    private static int a(ChunkCoordIntPair chunkcoordintpair, int i, int j) {
        int k = chunkcoordintpair.x - i;
        int l = chunkcoordintpair.z - j;

        return Math.max(Math.abs(k), Math.abs(l));
    }

    protected LightEngineThreaded a() {
        return this.lightEngine;
    }

    @Nullable
    protected PlayerChunk getUpdatingChunk(long i) {
        return (PlayerChunk) this.updatingChunks.get(i);
    }

    @Nullable
    public PlayerChunk getVisibleChunk(long i) { // Paper - protected -> public
        return (PlayerChunk) this.visibleChunks.get(i);
    }

    protected IntSupplier c(long i) {
        return () -> {
            PlayerChunk playerchunk = this.getVisibleChunk(i);

            return playerchunk == null ? ChunkTaskQueue.a - 1 : Math.min(playerchunk.k(), ChunkTaskQueue.a - 1);
        };
    }

    private CompletableFuture<Either<List<IChunkAccess>, PlayerChunk.Failure>> a(ChunkCoordIntPair chunkcoordintpair, int i, IntFunction<ChunkStatus> intfunction) {
        List<CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>>> list = Lists.newArrayList();
        int j = chunkcoordintpair.x;
        int k = chunkcoordintpair.z;

        for (int l = -i; l <= i; ++l) {
            for (int i1 = -i; i1 <= i; ++i1) {
                int j1 = Math.max(Math.abs(i1), Math.abs(l));
                final ChunkCoordIntPair chunkcoordintpair1 = new ChunkCoordIntPair(j + i1, k + l);
                long k1 = chunkcoordintpair1.pair();
                PlayerChunk playerchunk = this.getUpdatingChunk(k1);

                if (playerchunk == null) {
                    return CompletableFuture.completedFuture(Either.right(new PlayerChunk.Failure() {
                        public String toString() {
                            return "Unloaded " + chunkcoordintpair1.toString();
                        }
                    }));
                }

                ChunkStatus chunkstatus = (ChunkStatus) intfunction.apply(j1);
                CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = playerchunk.a(chunkstatus, this);

                list.add(completablefuture);
            }
        }

        CompletableFuture<List<Either<IChunkAccess, PlayerChunk.Failure>>> completablefuture1 = SystemUtils.b(list);

        return completablefuture1.thenApply((list1) -> {
            List<IChunkAccess> list2 = Lists.newArrayList();
            // CraftBukkit start - decompile error
            int cnt = 0;

            for (Iterator iterator = list1.iterator(); iterator.hasNext(); ++cnt) {
                final int l1 = cnt;
                // CraftBukkit end
                final Either<IChunkAccess, PlayerChunk.Failure> either = (Either) iterator.next();
                Optional<IChunkAccess> optional = either.left();

                if (!optional.isPresent()) {
                    return Either.right(new PlayerChunk.Failure() {
                        public String toString() {
                            return "Unloaded " + new ChunkCoordIntPair(j + l1 % (i * 2 + 1), k + l1 / (i * 2 + 1)) + " " + ((PlayerChunk.Failure) either.right().get()).toString();
                        }
                    });
                }

                list2.add(optional.get());
            }

            return Either.left(list2);
        });
    }

    public CompletableFuture<Either<Chunk, PlayerChunk.Failure>> b(ChunkCoordIntPair chunkcoordintpair) {
        return this.a(chunkcoordintpair, 2, (i) -> {
            return ChunkStatus.FULL;
        }).thenApplyAsync((either) -> {
            return either.mapLeft((list) -> {
                return (Chunk) list.get(list.size() / 2);
            });
        }, this.executor);
    }

    @Nullable
    private PlayerChunk a(long i, int j, @Nullable PlayerChunk playerchunk, int k) {
        if (k > PlayerChunkMap.GOLDEN_TICKET && j > PlayerChunkMap.GOLDEN_TICKET) {
            return playerchunk;
        } else {
            if (playerchunk != null) {
                playerchunk.a(j);
            }

            if (playerchunk != null) {
                if (j > PlayerChunkMap.GOLDEN_TICKET) {
                    this.unloadQueue.add(i);
                } else {
                    this.unloadQueue.remove(i);
                }
            }

            if (j <= PlayerChunkMap.GOLDEN_TICKET && playerchunk == null) {
                playerchunk = (PlayerChunk) this.pendingUnload.remove(i);
                if (playerchunk != null) {
                    playerchunk.a(j);
                } else {
                    playerchunk = new PlayerChunk(new ChunkCoordIntPair(i), j, this.lightEngine, this.q, this);
                }

                this.updatingChunks.put(i, playerchunk);
                this.updatingChunksModified = true;
            }

            return playerchunk;
        }
    }

    @Override
    public void close() throws IOException {
        this.q.close();
        this.world.asyncChunkTaskManager.close(true); // Paper - Required since we're closing regionfiles in the next line
        this.n.close();
        super.close();
    }

    // Paper start - derived from below
    protected void saveIncrementally() {
        int savedThisTick = 0;
        for (PlayerChunk playerchunk : visibleChunks.values()) {
            if (playerchunk.hasBeenLoaded()) {

                IChunkAccess ichunkaccess = (IChunkAccess) playerchunk.getChunkSave().getNow(null); // CraftBukkit - decompile error


                if (ichunkaccess instanceof ProtoChunkExtension || ichunkaccess instanceof Chunk) {
                    boolean shouldSave = true;

                    if (ichunkaccess instanceof Chunk) {
                        shouldSave = ((Chunk) ichunkaccess).lastSaved + world.paperConfig.autoSavePeriod <= world.getTime();
                    }

                    if (shouldSave && this.saveChunk(ichunkaccess, true)) { // Paper - async chunk io
                        ++savedThisTick;
                        playerchunk.m();
                    }
                }

                if (savedThisTick >= world.paperConfig.maxAutoSaveChunksPerTick) {
                    return;
                }
            }
        }
    }
    // paper end

    protected void save(boolean flag) {
        if (flag) {
            List<PlayerChunk> list = (List) this.visibleChunks.values().stream().filter(PlayerChunk::hasBeenLoaded).peek(PlayerChunk::m).collect(Collectors.toList());
            MutableBoolean mutableboolean = new MutableBoolean();

            do {
                mutableboolean.setFalse();
                list.stream().map((playerchunk) -> {
                    CompletableFuture completablefuture;

                    do {
                        completablefuture = playerchunk.getChunkSave();
                        this.executor.awaitTasks(completablefuture::isDone);
                    } while (completablefuture != playerchunk.getChunkSave());

                    return (IChunkAccess) completablefuture.join();
                }).filter((ichunkaccess) -> {
                    return ichunkaccess instanceof ProtoChunkExtension || ichunkaccess instanceof Chunk;
                }).filter((chunk) -> this.saveChunk(chunk, true)).forEach((ichunkaccess) -> { // Paper - async io for chunk save
                    mutableboolean.setTrue();
                });
            } while (mutableboolean.isTrue());

            this.b(() -> {
                return true;
            });
            this.world.asyncChunkTaskManager.flush(); // Paper - flush to preserve behavior compat with pre-async behaviour
            PlayerChunkMap.LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", this.x.getName());
        } else {
            this.visibleChunks.values().stream().filter(PlayerChunk::hasBeenLoaded).forEach((playerchunk) -> {
                IChunkAccess ichunkaccess = (IChunkAccess) playerchunk.getChunkSave().getNow(null); // CraftBukkit - decompile error

                if (ichunkaccess instanceof ProtoChunkExtension || ichunkaccess instanceof Chunk) {
                    this.saveChunk(ichunkaccess, true); // Paper
                    playerchunk.m();
                }

            });
            com.destroystokyo.paper.io.PaperFileIOThread.Holder.INSTANCE.flush(); // Paper - flush to preserve behavior compat with pre-async behaviour
        }

    }

    private static final double UNLOAD_QUEUE_RESIZE_FACTOR = 0.96; // Spigot

    protected void unloadChunks(BooleanSupplier booleansupplier) {
        GameProfilerFiller gameprofilerfiller = this.world.getMethodProfiler();

        try (Timing ignored = this.world.timings.poiUnload.startTiming()) { // Paper
        gameprofilerfiller.enter("poi");
        this.n.a(booleansupplier);
        } // Paper
        gameprofilerfiller.exitEnter("chunk_unload");
        if (!this.world.isSavingDisabled()) {
            try (Timing ignored = this.world.timings.chunkUnload.startTiming()) { // Paper
            this.b(booleansupplier);
            }// Paper
        }

        gameprofilerfiller.exit();
    }

    private void b(BooleanSupplier booleansupplier) {
        LongIterator longiterator = this.unloadQueue.iterator();
        // Spigot start
        org.spigotmc.SlackActivityAccountant activityAccountant = this.world.getMinecraftServer().slackActivityAccountant;
        activityAccountant.startActivity(0.5);
        int targetSize = Math.min(this.unloadQueue.size() - 100,  (int) (this.unloadQueue.size() * UNLOAD_QUEUE_RESIZE_FACTOR)); // Paper - Make more aggressive
        // Spigot end
        while (longiterator.hasNext()) { // Spigot
            long j = longiterator.nextLong();
            longiterator.remove(); // Spigot
            PlayerChunk playerchunk = (PlayerChunk) this.updatingChunks.remove(j);

            if (playerchunk != null) {
                this.pendingUnload.put(j, playerchunk);
                this.updatingChunksModified = true;
                // Spigot start
                if (!booleansupplier.getAsBoolean() && this.unloadQueue.size() <= targetSize && activityAccountant.activityTimeIsExhausted()) {
                    break;
                }
                // Spigot end
                this.a(j, playerchunk);
            }
        }
        activityAccountant.endActivity(); // Spigot

        Runnable runnable;

        int queueTarget = Math.min(this.A.size() - 100, (int) (this.A.size() * UNLOAD_QUEUE_RESIZE_FACTOR)); // Paper - Target this queue as well

        while ((booleansupplier.getAsBoolean() || this.A.size() > queueTarget) && (runnable = (Runnable) this.A.poll()) != null) { // Paper - Target this queue as well
            runnable.run();
        }

    }

    // Paper start - async chunk save for unload
    // Note: This is very unsafe to call if the chunk is still in use.
    // This is also modeled after PlayerChunkMap#saveChunk(IChunkAccess, boolean), with the intentional difference being
    // serializing the chunk is left to a worker thread.
    private void asyncSave(IChunkAccess chunk) {
        ChunkCoordIntPair chunkPos = chunk.getPos();
        NBTTagCompound poiData;
        try (Timing ignored = this.world.timings.chunkUnloadPOISerialization.startTiming()) {
            poiData = this.getVillagePlace().getData(chunk.getPos());
        }

        com.destroystokyo.paper.io.PaperFileIOThread.Holder.INSTANCE.scheduleSave(this.world, chunkPos.x, chunkPos.z,
            poiData, null, com.destroystokyo.paper.io.PrioritizedTaskQueue.LOW_PRIORITY);

        if (!chunk.isNeedsSaving()) {
            return;
        }

        ChunkStatus chunkstatus = chunk.getChunkStatus();

        // Copied from PlayerChunkMap#saveChunk(IChunkAccess, boolean)
        if (chunkstatus.getType() != ChunkStatus.Type.LEVELCHUNK) {
            try (co.aikar.timings.Timing ignored1 = this.world.timings.chunkSaveOverwriteCheck.startTiming()) { // Paper
                // Paper start - Optimize save by using status cache
                try {
                    ChunkStatus statusOnDisk = this.getChunkStatusOnDisk(chunkPos);
                    if (statusOnDisk != null && statusOnDisk.getType() == ChunkStatus.Type.LEVELCHUNK) {
                        // Paper end
                        return;
                    }

                    if (chunkstatus == ChunkStatus.EMPTY && chunk.h().values().stream().noneMatch(StructureStart::e)) {
                        return;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return;
                }
            }
        }

        ChunkRegionLoader.AsyncSaveData asyncSaveData;
        try (Timing ignored = this.world.timings.chunkUnloadPrepareSave.startTiming()) {
            asyncSaveData = ChunkRegionLoader.getAsyncSaveData(this.world, chunk);
        }

        this.world.asyncChunkTaskManager.scheduleChunkSave(chunkPos.x, chunkPos.z, com.destroystokyo.paper.io.PrioritizedTaskQueue.LOW_PRIORITY,
            asyncSaveData, chunk);

        chunk.setLastSaved(this.world.getTime());
        chunk.setNeedsSaving(false);
    }
    // Paper end

    private void a(long i, PlayerChunk playerchunk) {
        CompletableFuture<IChunkAccess> completablefuture = playerchunk.getChunkSave();
        Consumer<IChunkAccess> consumer = (ichunkaccess) -> { // CraftBukkit - decompile error
            CompletableFuture<IChunkAccess> completablefuture1 = playerchunk.getChunkSave();

            if (completablefuture1 != completablefuture) {
                this.a(i, playerchunk);
            } else {
                if (this.pendingUnload.remove(i, playerchunk) && ichunkaccess != null) {
                    if (ichunkaccess instanceof Chunk) {
                        ((Chunk) ichunkaccess).setLoaded(false);
                    }

                    //this.saveChunk(ichunkaccess);// Paper - delay
                    if (this.loadedChunks.remove(i) && ichunkaccess instanceof Chunk) {
                        Chunk chunk = (Chunk) ichunkaccess;

                        this.world.unloadChunk(chunk);
                    }

                    try {
                        this.asyncSave(ichunkaccess); // Paper - async chunk saving
                    } catch (Throwable ex) {
                        LOGGER.fatal("Failed to prepare async save, attempting synchronous save", ex);
                        this.saveChunk(ichunkaccess, true);
                    }

                    this.lightEngine.a(ichunkaccess.getPos());
                    this.lightEngine.queueUpdate();
                    this.worldLoadListener.a(ichunkaccess.getPos(), (ChunkStatus) null);
                }

            }
        };
        Queue queue = this.A;

        this.A.getClass();
        completablefuture.thenAcceptAsync(consumer, queue::add).whenComplete((ovoid, throwable) -> {
            if (throwable != null) {
                PlayerChunkMap.LOGGER.error("Failed to save chunk " + playerchunk.i(), throwable);
            }

        });
    }

    protected boolean b() {
        if (!this.updatingChunksModified) {
            return false;
        } else {
            this.visibleChunks = this.updatingChunks.clone();
            this.updatingChunksModified = false;
            return true;
        }
    }

    public CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> a(PlayerChunk playerchunk, ChunkStatus chunkstatus) {
        ChunkCoordIntPair chunkcoordintpair = playerchunk.i();

        if (chunkstatus == ChunkStatus.EMPTY) {
            return this.f(chunkcoordintpair);
        } else {
            CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = playerchunk.a(chunkstatus.e(), this);

            return completablefuture.thenComposeAsync((either) -> {
                Optional<IChunkAccess> optional = either.left();

                if (!optional.isPresent()) {
                    return CompletableFuture.completedFuture(either);
                } else {
                    if (chunkstatus == ChunkStatus.LIGHT) {
                        this.chunkDistanceManager.a(TicketType.LIGHT, chunkcoordintpair, 33 + ChunkStatus.a(ChunkStatus.FEATURES), chunkcoordintpair);
                    }

                    IChunkAccess ichunkaccess = (IChunkAccess) optional.get();

                    if (ichunkaccess.getChunkStatus().b(chunkstatus)) {
                        CompletableFuture completablefuture1;

                        if (chunkstatus == ChunkStatus.LIGHT) {
                            completablefuture1 = this.b(playerchunk, chunkstatus);
                        } else {
                            completablefuture1 = chunkstatus.a(this.world, this.definedStructureManager, this.lightEngine, (ichunkaccess1) -> {
                                return this.c(playerchunk);
                            }, ichunkaccess);
                        }

                        this.worldLoadListener.a(chunkcoordintpair, chunkstatus);
                        return completablefuture1;
                    } else {
                        return this.b(playerchunk, chunkstatus);
                    }
                }
            }, this.executor);
        }
    }

    // Paper start - Async chunk io
    public NBTTagCompound completeChunkData(NBTTagCompound compound, ChunkCoordIntPair chunkcoordintpair) throws IOException {
        return compound == null ? null : this.getChunkData(this.world.getWorldProvider().getDimensionManager(), this.getWorldPersistentDataSupplier(), compound, chunkcoordintpair, this.world);
    }
    // Paper end

    private CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> f(ChunkCoordIntPair chunkcoordintpair) {
        // Paper start - Async chunk io
        final java.util.function.BiFunction<ChunkRegionLoader.InProgressChunkHolder, Throwable, Either<IChunkAccess, PlayerChunk.Failure>> syncLoadComplete = (chunkHolder, ioThrowable) -> {
            try (Timing ignored = this.world.timings.syncChunkLoadTimer.startTimingIfSync()) { // Paper
                if (ioThrowable != null) {
                    com.destroystokyo.paper.io.IOUtil.rethrow(ioThrowable);
                }
                this.getVillagePlace().loadInData(chunkcoordintpair, chunkHolder.poiData);
                chunkHolder.tasks.forEach(Runnable::run);
                // Paper - async load completes this
                // Paper end

                // Paper start - This is done async
                if (chunkHolder.protoChunk != null) {
                    chunkHolder.protoChunk.setLastSaved(this.world.getTime());
                    return Either.left(chunkHolder.protoChunk);
                }
                // Paper end
            } catch (ReportedException reportedexception) {
                Throwable throwable = reportedexception.getCause();

                if (!(throwable instanceof IOException)) {
                    throw reportedexception;
                }

                PlayerChunkMap.LOGGER.error("Couldn't load chunk {}", chunkcoordintpair, throwable);
            } catch (Exception exception) {
                PlayerChunkMap.LOGGER.error("Couldn't load chunk {}", chunkcoordintpair, exception);
            }

            return Either.left(new ProtoChunk(chunkcoordintpair, ChunkConverter.a, this.world)); // Paper - Anti-Xray
            // Paper start - Async chunk io
        };
        CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> ret = new CompletableFuture<>();

        Consumer<ChunkRegionLoader.InProgressChunkHolder> chunkHolderConsumer = (ChunkRegionLoader.InProgressChunkHolder holder) -> {
            PlayerChunkMap.this.executor.addTask(() -> {
                ret.complete(syncLoadComplete.apply(holder, null));
            });
        };

        CompletableFuture<NBTTagCompound> chunkSaveFuture = this.world.asyncChunkTaskManager.getChunkSaveFuture(chunkcoordintpair.x, chunkcoordintpair.z);
        if (chunkSaveFuture != null) {
            this.world.asyncChunkTaskManager.scheduleChunkLoad(chunkcoordintpair.x, chunkcoordintpair.z,
                com.destroystokyo.paper.io.PrioritizedTaskQueue.HIGH_PRIORITY, chunkHolderConsumer, false, chunkSaveFuture);
            this.world.asyncChunkTaskManager.raisePriority(chunkcoordintpair.x, chunkcoordintpair.z, com.destroystokyo.paper.io.PrioritizedTaskQueue.HIGH_PRIORITY);
        } else {
            this.world.asyncChunkTaskManager.scheduleChunkLoad(chunkcoordintpair.x, chunkcoordintpair.z,
                com.destroystokyo.paper.io.PrioritizedTaskQueue.NORMAL_PRIORITY, chunkHolderConsumer, false);
        }
        return ret;
        // Paper end
    }

    private CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> b(PlayerChunk playerchunk, ChunkStatus chunkstatus) {
        ChunkCoordIntPair chunkcoordintpair = playerchunk.i();
        CompletableFuture<Either<List<IChunkAccess>, PlayerChunk.Failure>> completablefuture = this.a(chunkcoordintpair, chunkstatus.f(), (i) -> {
            return this.a(chunkstatus, i);
        });

        return completablefuture.thenComposeAsync((either) -> {
            return either.map((list) -> { // Paper - Shut up.
                try {
                    CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture1 = chunkstatus.a(this.world, this.chunkGenerator, this.definedStructureManager, this.lightEngine, (ichunkaccess) -> {
                        return this.c(playerchunk);
                    }, list);

                    this.worldLoadListener.a(chunkcoordintpair, chunkstatus);
                    return completablefuture1;
                } catch (Exception exception) {
                    CrashReport crashreport = CrashReport.a(exception, "Exception generating new chunk");
                    CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Chunk to be generated");

                    crashreportsystemdetails.a("Location", (Object) String.format("%d,%d", chunkcoordintpair.x, chunkcoordintpair.z));
                    crashreportsystemdetails.a("Position hash", (Object) ChunkCoordIntPair.pair(chunkcoordintpair.x, chunkcoordintpair.z));
                    crashreportsystemdetails.a("Generator", (Object) this.chunkGenerator);
                    throw new ReportedException(crashreport);
                }
            }, (playerchunk_failure) -> {
                this.c(chunkcoordintpair);
                return CompletableFuture.completedFuture(Either.right(playerchunk_failure));
            });
        }, (runnable) -> {
            this.mailboxWorldGen.a(ChunkTaskQueueSorter.a(playerchunk, runnable)); // CraftBukkit - decompile error
        });
    }

    protected void c(ChunkCoordIntPair chunkcoordintpair) {
        this.executor.a(SystemUtils.a(() -> {
            this.chunkDistanceManager.b(TicketType.LIGHT, chunkcoordintpair, 33 + ChunkStatus.a(ChunkStatus.FEATURES), chunkcoordintpair);
        }, () -> {
            return "release light ticket " + chunkcoordintpair;
        }));
    }

    private ChunkStatus a(ChunkStatus chunkstatus, int i) {
        ChunkStatus chunkstatus1;

        if (i == 0) {
            chunkstatus1 = chunkstatus.e();
        } else {
            chunkstatus1 = ChunkStatus.a(ChunkStatus.a(chunkstatus) + i);
        }

        return chunkstatus1;
    }

    private CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> c(PlayerChunk playerchunk) {
        CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = playerchunk.getStatusFutureUnchecked(ChunkStatus.FULL.e());

        return completablefuture.thenApplyAsync((either) -> {
            ChunkStatus chunkstatus = PlayerChunk.getChunkStatus(playerchunk.getTicketLevel());

            return !chunkstatus.b(ChunkStatus.FULL) ? PlayerChunk.UNLOADED_CHUNK_ACCESS : either.mapLeft((ichunkaccess) -> {
            try (Timing ignored = world.timings.chunkIOStage2.startTimingIfSync()) { // Paper
                ChunkCoordIntPair chunkcoordintpair = playerchunk.i();
                Chunk chunk;

                if (ichunkaccess instanceof ProtoChunkExtension) {
                    chunk = ((ProtoChunkExtension) ichunkaccess).u();
                } else {
                    chunk = new Chunk(this.world, (ProtoChunk) ichunkaccess);
                    playerchunk.a(new ProtoChunkExtension(chunk));
                }

                chunk.a(() -> {
                    return PlayerChunk.getChunkState(playerchunk.getTicketLevel());
                });
                chunk.addEntities();
                if (this.loadedChunks.add(chunkcoordintpair.pair())) {
                    chunk.setLoaded(true);
                    this.world.a(chunk.getTileEntities().values());
                    List<Entity> list = null;
                    List<Entity>[] aentityslice = chunk.getEntitySlices(); // Spigot
                    int i = aentityslice.length;

                    for (int j = 0; j < i; ++j) {
                        List<Entity> entityslice = aentityslice[j]; // Spigot

                        // Paper start
                        PaperWorldConfig.DuplicateUUIDMode mode = world.paperConfig.duplicateUUIDMode;
                        if (mode == PaperWorldConfig.DuplicateUUIDMode.WARN || mode == PaperWorldConfig.DuplicateUUIDMode.DELETE || mode == PaperWorldConfig.DuplicateUUIDMode.SAFE_REGEN) {
                            Map<UUID, Entity> thisChunk = new HashMap<>();
                            for (Iterator<Entity> iterator = ((List<Entity>) entityslice).iterator(); iterator.hasNext(); ) {
                                Entity entity = iterator.next();
                                if (entity.dead || entity.valid) continue;
                                Entity other = ((WorldServer) world).getEntity(entity.uniqueID);
                                if (other == null || other.dead) {
                                    other = thisChunk.get(entity.uniqueID);
                                }

                                if (mode == PaperWorldConfig.DuplicateUUIDMode.SAFE_REGEN && other != null && !other.dead
                                        && java.util.Objects.equals(other.getSaveID(), entity.getSaveID())
                                        && entity.getBukkitEntity().getLocation().distance(other.getBukkitEntity().getLocation()) < world.paperConfig.duplicateUUIDDeleteRange
                                ) {
                                    if (World.DEBUG_ENTITIES) LOGGER.warn("[DUPE-UUID] Duplicate UUID found used by " + other + ", deleted entity " + entity + " because it was near the duplicate and likely an actual duplicate. See https://github.com/PaperMC/Paper/issues/1223 for discussion on what this is about.");
                                    entity.dead = true;
                                    iterator.remove();
                                    continue;
                                }
                                if (other != null && !other.dead) {
                                    switch (mode) {
                                        case SAFE_REGEN: {
                                            entity.setUUID(UUID.randomUUID());
                                            if (World.DEBUG_ENTITIES) LOGGER.warn("[DUPE-UUID] Duplicate UUID found used by " + other + ", regenerated UUID for " + entity + ". See https://github.com/PaperMC/Paper/issues/1223 for discussion on what this is about.");
                                            break;
                                        }
                                        case DELETE: {
                                            if (World.DEBUG_ENTITIES) LOGGER.warn("[DUPE-UUID] Duplicate UUID found used by " + other + ", deleted entity " + entity + ". See https://github.com/PaperMC/Paper/issues/1223 for discussion on what this is about.");
                                            entity.dead = true;
                                            iterator.remove();
                                            break;
                                        }
                                        default:
                                            if (World.DEBUG_ENTITIES) LOGGER.warn("[DUPE-UUID] Duplicate UUID found used by " + other + ", doing nothing to " + entity + ". See https://github.com/PaperMC/Paper/issues/1223 for discussion on what this is about.");
                                            break;
                                    }
                                }


                            if (!(entity instanceof EntityHuman) && (entity.dead || !this.world.addEntityChunk(entity))) { // Paper
                                if (list == null) {
                                    list = Lists.newArrayList(new Entity[]{entity});
                                } else {
                                    list.add(entity);
                                }
                            }
                        }
                        } // Paper
                    }

                    if (list != null) {
                        list.forEach(chunk::b);
                    }
                }

                return chunk;
                } // Paper
            });
        }, (runnable) -> {
            Mailbox mailbox = this.mailboxMain;
            long i = playerchunk.i().pair();

            playerchunk.getClass();
            mailbox.a(ChunkTaskQueueSorter.a(runnable, i, playerchunk::getTicketLevel)); // CraftBukkit - decompile error
        });
    }

    public CompletableFuture<Either<Chunk, PlayerChunk.Failure>> a(PlayerChunk playerchunk) {
        ChunkCoordIntPair chunkcoordintpair = playerchunk.i();
        CompletableFuture<Either<List<IChunkAccess>, PlayerChunk.Failure>> completablefuture = this.a(chunkcoordintpair, 1, (i) -> {
            return ChunkStatus.FULL;
        });
        CompletableFuture<Either<Chunk, PlayerChunk.Failure>> completablefuture1 = completablefuture.thenApplyAsync((either) -> {
            return either.flatMap((list) -> {
                Chunk chunk = (Chunk) list.get(list.size() / 2);

                chunk.A();
                return Either.left(chunk);
            });
        }, (runnable) -> {
            this.mailboxMain.a(ChunkTaskQueueSorter.a(playerchunk, runnable)); // CraftBukkit - decompile error
        });

        completablefuture1.thenAcceptAsync((either) -> {
            either.mapLeft((chunk) -> {
                this.v.getAndIncrement();
                Packet<?>[] apacket = new Packet[2];

                this.a(chunkcoordintpair, false).forEach((entityplayer) -> {
                    this.a(entityplayer, apacket, chunk);
                });
                return Either.left(chunk);
            });
        }, (runnable) -> {
            this.mailboxMain.a(ChunkTaskQueueSorter.a(playerchunk, runnable)); // CraftBukkit - decompile error
        });
        return completablefuture1;
    }

    public CompletableFuture<Either<Chunk, PlayerChunk.Failure>> b(PlayerChunk playerchunk) {
        return playerchunk.a(ChunkStatus.FULL, this).thenApplyAsync((either) -> {
            return either.mapLeft((ichunkaccess) -> {
                Chunk chunk = (Chunk) ichunkaccess;

                chunk.B();
                return chunk;
            });
        }, (runnable) -> {
            this.mailboxMain.a(ChunkTaskQueueSorter.a(playerchunk, runnable)); // CraftBukkit - decompile error
        });
    }

    public int c() {
        return this.v.get();
    }

    // Paper start - async chunk io
    private boolean writeDataAsync(ChunkCoordIntPair chunkPos, NBTTagCompound poiData, NBTTagCompound chunkData, boolean async) {
        com.destroystokyo.paper.io.PaperFileIOThread.Holder.INSTANCE.scheduleSave(this.world, chunkPos.x, chunkPos.z,
            poiData, chunkData, !async ? com.destroystokyo.paper.io.PrioritizedTaskQueue.HIGHEST_PRIORITY : com.destroystokyo.paper.io.PrioritizedTaskQueue.LOW_PRIORITY);

        if (async) {
            return true;
        }

        try (co.aikar.timings.Timing ignored = this.world.timings.chunkSaveIOWait.startTiming()) { // Paper
        Boolean successPoi = com.destroystokyo.paper.io.PaperFileIOThread.Holder.INSTANCE.waitForIOToComplete(this.world, chunkPos.x, chunkPos.z, true, true);
        Boolean successChunk = com.destroystokyo.paper.io.PaperFileIOThread.Holder.INSTANCE.waitForIOToComplete(this.world, chunkPos.x, chunkPos.z, true, false);

        if (successPoi == Boolean.FALSE || successChunk == Boolean.FALSE) {
            return false;
        }

        // null indicates no task existed, which means our write completed before we waited on it

        return true;
        } // Paper
    }
    // Paper end

    public boolean saveChunk(IChunkAccess ichunkaccess) {
        // Paper start - async param
        return this.saveChunk(ichunkaccess, false);
    }
    public boolean saveChunk(IChunkAccess ichunkaccess, boolean async) {
        try (co.aikar.timings.Timing ignored = this.world.timings.chunkSave.startTiming()) {
        NBTTagCompound poiData = this.getVillagePlace().getData(ichunkaccess.getPos()); // Paper
        //this.n.a(ichunkaccess.getPos()); // Delay
        // Paper end
        if (!ichunkaccess.isNeedsSaving()) {
            return false;
        } else {
            // Paper - The save session check is performed on the IO thread

            ichunkaccess.setLastSaved(this.world.getTime());
            ichunkaccess.setNeedsSaving(false);
            ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();

            try {
                ChunkStatus chunkstatus = ichunkaccess.getChunkStatus();
                NBTTagCompound nbttagcompound;

                if (chunkstatus.getType() != ChunkStatus.Type.LEVELCHUNK) {
                    try (co.aikar.timings.Timing ignored1 = this.world.timings.chunkSaveOverwriteCheck.startTiming()) { // Paper
                    // Paper start - Optimize save by using status cache
                    ChunkStatus statusOnDisk = this.getChunkStatusOnDisk(chunkcoordintpair);
                    if (statusOnDisk != null && statusOnDisk.getType() == ChunkStatus.Type.LEVELCHUNK) {
                        // Paper end
                        this.writeDataAsync(ichunkaccess.getPos(), poiData, null, async); // Paper - Async chunk io
                        return false;
                    }

                    if (chunkstatus == ChunkStatus.EMPTY && ichunkaccess.h().values().stream().noneMatch(StructureStart::e)) {
                        this.writeDataAsync(ichunkaccess.getPos(), poiData, null, async); // Paper - Async chunk io
                        return false;
                    }
                }
                } // Paper
                try (co.aikar.timings.Timing ignored1 = this.world.timings.chunkSaveDataSerialization.startTiming()) { // Paper
                nbttagcompound = ChunkRegionLoader.saveChunk(this.world, ichunkaccess);
                } // Paper
                return this.writeDataAsync(ichunkaccess.getPos(), poiData, nbttagcompound, async); // Paper - Async chunk io
                //return true; // Paper
            } catch (Exception exception) {
                PlayerChunkMap.LOGGER.error("Failed to save chunk {},{}", chunkcoordintpair.x, chunkcoordintpair.z, exception);
                com.destroystokyo.paper.exception.ServerInternalException.reportInternalException(exception); // Paper
                return false;
            }
        }
        } // Paper
    }

    protected void setViewDistance(int i) {
        int j = MathHelper.clamp(i + 1, 3, 33);

        if (j != this.viewDistance) {
            int k = this.viewDistance;

            this.viewDistance = j;
            this.chunkDistanceManager.a(this.viewDistance);
            ObjectIterator objectiterator = this.updatingChunks.values().iterator();

            while (objectiterator.hasNext()) {
                PlayerChunk playerchunk = (PlayerChunk) objectiterator.next();
                ChunkCoordIntPair chunkcoordintpair = playerchunk.i();
                Packet<?>[] apacket = new Packet[2];

                this.a(chunkcoordintpair, false).forEach((entityplayer) -> {
                    int l = b(chunkcoordintpair, entityplayer, true);
                    boolean flag = l <= k;
                    boolean flag1 = l <= this.viewDistance;

                    this.sendChunk(entityplayer, chunkcoordintpair, apacket, flag, flag1);
                });
            }
        }

    }

    protected void sendChunk(EntityPlayer entityplayer, ChunkCoordIntPair chunkcoordintpair, Packet<?>[] apacket, boolean flag, boolean flag1) {
        if (entityplayer.world == this.world) {
            if (flag1 && !flag) {
                PlayerChunk playerchunk = this.getVisibleChunk(chunkcoordintpair.pair());

                if (playerchunk != null) {
                    Chunk chunk = playerchunk.getChunk();

                    if (chunk != null) {
                        this.a(entityplayer, apacket, chunk);
                    }

                    PacketDebug.a(this.world, chunkcoordintpair);
                }
            }

            if (!flag1 && flag) {
                entityplayer.a(chunkcoordintpair);
            }

        }
    }

    public int d() {
        return this.visibleChunks.size();
    }

    protected PlayerChunkMap.a e() {
        return this.chunkDistanceManager;
    }

    protected Iterable<PlayerChunk> f() {
        return Iterables.unmodifiableIterable(this.visibleChunks.values());
    }

    void a(Writer writer) throws IOException {
        CSVWriter csvwriter = CSVWriter.a().a("x").a("z").a("level").a("in_memory").a("status").a("full_status").a("accessible_ready").a("ticking_ready").a("entity_ticking_ready").a("ticket").a("spawning").a("entity_count").a("block_entity_count").a(writer);
        ObjectBidirectionalIterator objectbidirectionaliterator = this.visibleChunks.long2ObjectEntrySet().iterator();

        while (objectbidirectionaliterator.hasNext()) {
            Entry<PlayerChunk> entry = (Entry) objectbidirectionaliterator.next();
            ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(entry.getLongKey());
            PlayerChunk playerchunk = (PlayerChunk) entry.getValue();
            Optional<IChunkAccess> optional = Optional.ofNullable(playerchunk.f());
            Optional<Chunk> optional1 = optional.flatMap((ichunkaccess) -> {
                return ichunkaccess instanceof Chunk ? Optional.of((Chunk) ichunkaccess) : Optional.empty();
            });

            // Craftbukkit - decompile error
            csvwriter.a(chunkcoordintpair.x, chunkcoordintpair.z, playerchunk.getTicketLevel(), optional.isPresent(), optional.map(IChunkAccess::getChunkStatus).orElse(null), optional1.map(Chunk::getState).orElse(null), a(playerchunk.c()), a(playerchunk.a()), a(playerchunk.b()), this.chunkDistanceManager.c(entry.getLongKey()), !this.isOutsideOfRange(chunkcoordintpair), optional1.map((chunk) -> {
                return Stream.of(chunk.getEntitySlices()).mapToInt(List::size).sum(); // Spigot
            }).orElse(0), optional1.map((chunk) -> {
                return chunk.getTileEntities().size();
            }).orElse(0));
        }

    }

    private static String a(CompletableFuture<Either<Chunk, PlayerChunk.Failure>> completablefuture) {
        try {
            Either<Chunk, PlayerChunk.Failure> either = (Either) completablefuture.getNow(null); // Craftbukkit - decompile error

            return either != null ? (String) either.map((chunk) -> {
                return "done";
            }, (playerchunk_failure) -> {
                return "unloaded";
            }) : "not completed";
        } catch (CompletionException completionexception) {
            return "failed " + completionexception.getCause().getMessage();
        } catch (CancellationException cancellationexception) {
            return "cancelled";
        }
    }

    // Paper start - Asynchronous chunk io
    @Nullable
    @Override
    public NBTTagCompound read(ChunkCoordIntPair chunkcoordintpair) throws IOException {
        if (Thread.currentThread() != com.destroystokyo.paper.io.PaperFileIOThread.Holder.INSTANCE) {
            NBTTagCompound ret = com.destroystokyo.paper.io.PaperFileIOThread.Holder.INSTANCE
                .loadChunkDataAsyncFuture(this.world, chunkcoordintpair.x, chunkcoordintpair.z, com.destroystokyo.paper.io.IOUtil.getPriorityForCurrentThread(),
                    false, true, true).join().chunkData;

            if (ret == com.destroystokyo.paper.io.PaperFileIOThread.FAILURE_VALUE) {
                throw new IOException("See logs for further detail");
            }
            return ret;
        }
        return super.read(chunkcoordintpair);
    }

    @Override
    public void write(ChunkCoordIntPair chunkcoordintpair, NBTTagCompound nbttagcompound) throws IOException {
        if (Thread.currentThread() != com.destroystokyo.paper.io.PaperFileIOThread.Holder.INSTANCE) {
            com.destroystokyo.paper.io.PaperFileIOThread.Holder.INSTANCE.scheduleSave(
                this.world, chunkcoordintpair.x, chunkcoordintpair.z, null, nbttagcompound,
                com.destroystokyo.paper.io.IOUtil.getPriorityForCurrentThread());

            Boolean ret = com.destroystokyo.paper.io.PaperFileIOThread.Holder.INSTANCE.waitForIOToComplete(this.world,
                chunkcoordintpair.x, chunkcoordintpair.z, true, false);

            if (ret == Boolean.FALSE) {
                throw new IOException("See logs for further detail");
            }
            return;
        }
        super.write(chunkcoordintpair, nbttagcompound);
    }
    // Paper end

    @Nullable
    public NBTTagCompound readChunkData(ChunkCoordIntPair chunkcoordintpair) throws IOException { // Paper - private -> public
        NBTTagCompound nbttagcompound = this.read(chunkcoordintpair);

        // Paper start - Cache chunk status on disk
        if (nbttagcompound == null) {
            return null;
        }

        nbttagcompound = this.getChunkData(this.world.getWorldProvider().getDimensionManager(), this.m, nbttagcompound, chunkcoordintpair, world); // CraftBukkit
        if (nbttagcompound == null) {
            return null;
        }

        this.updateChunkStatusOnDisk(chunkcoordintpair, nbttagcompound);

        return nbttagcompound;
        // Paper end
    }

    // Paper start - chunk status cache "api"
    public ChunkStatus getChunkStatusOnDiskIfCached(ChunkCoordIntPair chunkPos) {
        // Paper start - async chunk save for unload
        IChunkAccess unloadingChunk = this.world.asyncChunkTaskManager.getChunkInSaveProgress(chunkPos.x, chunkPos.z);
        if (unloadingChunk != null) {
            return unloadingChunk.getChunkStatus();
        }
        // Paper end
        // Paper start - async io
        NBTTagCompound inProgressWrite = com.destroystokyo.paper.io.PaperFileIOThread.Holder.INSTANCE
            .getPendingWrite(this.world, chunkPos.x, chunkPos.z, false);

        if (inProgressWrite != null) {
            return ChunkRegionLoader.getStatus(inProgressWrite);
        }
        // Paper end

        RegionFile regionFile = this.getRegionFileIfLoaded(chunkPos);

        return regionFile == null ? null : regionFile.getStatusIfCached(chunkPos.x, chunkPos.z);
    }

    public ChunkStatus getChunkStatusOnDisk(ChunkCoordIntPair chunkPos) throws IOException {
        // Paper start - async chunk save for unload
        IChunkAccess unloadingChunk = this.world.asyncChunkTaskManager.getChunkInSaveProgress(chunkPos.x, chunkPos.z);
        if (unloadingChunk != null) {
            return unloadingChunk.getChunkStatus();
        }
        // Paper end
        // Paper start - async io
        NBTTagCompound inProgressWrite = com.destroystokyo.paper.io.PaperFileIOThread.Holder.INSTANCE
            .getPendingWrite(this.world, chunkPos.x, chunkPos.z, false);

        if (inProgressWrite != null) {
            return ChunkRegionLoader.getStatus(inProgressWrite);
        }
        // Paper end
        synchronized (this) { // Paper - async io
        RegionFile regionFile = this.getRegionFile(chunkPos, false);

        if (!regionFile.chunkExists(chunkPos)) {
            return null;
        }

        ChunkStatus status = regionFile.getStatusIfCached(chunkPos.x, chunkPos.z);

        if (status != null) {
            return status;
        }
        // Paper start - async io
        }

        NBTTagCompound compound = this.readChunkData(chunkPos);

        return ChunkRegionLoader.getStatus(compound);
        // Paper end
    }

    public void updateChunkStatusOnDisk(ChunkCoordIntPair chunkPos, @Nullable NBTTagCompound compound) throws IOException {
        synchronized (this) { // Paper - async io
        RegionFile regionFile = this.getRegionFile(chunkPos, false);

        regionFile.setStatus(chunkPos.x, chunkPos.z, ChunkRegionLoader.getStatus(compound));
        } // Paper - async io
    }

    // Paper start - async io
    // this function will not load chunk data off disk to check for status
    // ret null for unknown, empty for empty status on disk or absent from disk
    public ChunkStatus getStatusOnDiskNoLoad(int x, int z) {
        // Paper start - async chunk save for unload
        IChunkAccess unloadingChunk = this.world.asyncChunkTaskManager.getChunkInSaveProgress(x, z);
        if (unloadingChunk != null) {
            return unloadingChunk.getChunkStatus();
        }
        // Paper end
        // Paper start - async io
        net.minecraft.server.NBTTagCompound inProgressWrite = com.destroystokyo.paper.io.PaperFileIOThread.Holder.INSTANCE
            .getPendingWrite(this.world, x, z, false);

        if (inProgressWrite != null) {
            return net.minecraft.server.ChunkRegionLoader.getStatus(inProgressWrite);
        }
        // Paper end
        // variant of PlayerChunkMap#getChunkStatusOnDisk that does not load data off disk, but loads the region file
        ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(x, z);
        synchronized (world.getChunkProvider().playerChunkMap) {
            net.minecraft.server.RegionFile file;
            try {
                file = world.getChunkProvider().playerChunkMap.getRegionFile(chunkPos, false);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            return !file.chunkExists(chunkPos) ? ChunkStatus.EMPTY : file.getStatusIfCached(x, z);
        }
    }
    // Paper end

    public IChunkAccess getUnloadingChunk(int chunkX, int chunkZ) {
        PlayerChunk chunkHolder = this.pendingUnload.get(ChunkCoordIntPair.pair(chunkX, chunkZ));
        return chunkHolder == null ? null : chunkHolder.getAvailableChunkNow();
    }
    // Paper end

    boolean isOutsideOfRange(ChunkCoordIntPair chunkcoordintpair) {
        // Spigot start
        return isOutsideOfRange(chunkcoordintpair, false);
    }

    boolean isOutsideOfRange(ChunkCoordIntPair chunkcoordintpair, boolean reducedRange) {
        int chunkRange = world.spigotConfig.mobSpawnRange;
        chunkRange = (chunkRange > world.spigotConfig.viewDistance) ? (byte) world.spigotConfig.viewDistance : chunkRange;
        chunkRange = (chunkRange > 8) ? 8 : chunkRange;

        final int finalChunkRange = chunkRange; // Paper for lambda below
        //double blockRange = (reducedRange) ? Math.pow(chunkRange << 4, 2) : 16384.0D; // Paper - use from event
        // Spigot end
        long i = chunkcoordintpair.pair();

        return !this.chunkDistanceManager.d(i) ? true : this.playerMap.a(i).noneMatch((entityplayer) -> {
            // Paper start -
            com.destroystokyo.paper.event.entity.PlayerNaturallySpawnCreaturesEvent event;
            double blockRange = 16384.0D;
            if (reducedRange) {
                event = new com.destroystokyo.paper.event.entity.PlayerNaturallySpawnCreaturesEvent(entityplayer.getBukkitEntity(), (byte) finalChunkRange);
                event.callEvent();
                blockRange = (double) ((event.getSpawnRadius() << 4) * (event.getSpawnRadius() << 4));
                if (event.isCancelled()) return true;
            }

            return (!entityplayer.isSpectator() && a(chunkcoordintpair, (Entity) entityplayer) < blockRange); // Spigot
            // Paper end
        });
    }

    private boolean b(EntityPlayer entityplayer) {
        return entityplayer.isSpectator() && !this.world.getGameRules().getBoolean(GameRules.SPECTATORS_GENERATE_CHUNKS);
    }

    void a(EntityPlayer entityplayer, boolean flag) {
        boolean flag1 = this.b(entityplayer);
        boolean flag2 = this.playerMap.c(entityplayer);
        int i = MathHelper.floor(entityplayer.locX) >> 4;
        int j = MathHelper.floor(entityplayer.locZ) >> 4;

        if (flag) {
            this.playerMap.a(ChunkCoordIntPair.pair(i, j), entityplayer, flag1);
            this.c(entityplayer);
            if (!flag1) {
                this.chunkDistanceManager.a(SectionPosition.a((Entity) entityplayer), entityplayer);
            }
        } else {
            SectionPosition sectionposition = entityplayer.M();

            this.playerMap.a(sectionposition.u().pair(), entityplayer);
            if (!flag2) {
                this.chunkDistanceManager.b(sectionposition, entityplayer);
            }
        }

        for (int k = i - this.viewDistance; k <= i + this.viewDistance; ++k) {
            for (int l = j - this.viewDistance; l <= j + this.viewDistance; ++l) {
                ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(k, l);

                this.sendChunk(entityplayer, chunkcoordintpair, new Packet[2], !flag, flag);
            }
        }

    }

    private SectionPosition c(EntityPlayer entityplayer) {
        SectionPosition sectionposition = SectionPosition.a((Entity) entityplayer);

        entityplayer.a(sectionposition);
        entityplayer.playerConnection.sendPacket(new PacketPlayOutViewCentre(sectionposition.a(), sectionposition.c()));
        return sectionposition;
    }

    public void movePlayer(EntityPlayer entityplayer) {
        ObjectIterator objectiterator = this.trackedEntities.values().iterator();

        while (objectiterator.hasNext()) {
            PlayerChunkMap.EntityTracker playerchunkmap_entitytracker = (PlayerChunkMap.EntityTracker) objectiterator.next();

            if (playerchunkmap_entitytracker.tracker == entityplayer) {
                playerchunkmap_entitytracker.track(this.world.getPlayers());
            } else {
                playerchunkmap_entitytracker.updatePlayer(entityplayer);
            }
        }

        int i = MathHelper.floor(entityplayer.locX) >> 4;
        int j = MathHelper.floor(entityplayer.locZ) >> 4;
        SectionPosition sectionposition = entityplayer.M();
        SectionPosition sectionposition1 = SectionPosition.a((Entity) entityplayer);
        long k = sectionposition.u().pair();
        long l = sectionposition1.u().pair();
        boolean flag = this.playerMap.d(entityplayer);
        boolean flag1 = this.b(entityplayer);
        boolean flag2 = sectionposition.v() != sectionposition1.v();

        if (flag2 || flag != flag1) {
            this.c(entityplayer);
            if (!flag) {
                this.chunkDistanceManager.b(sectionposition, entityplayer);
            }

            if (!flag1) {
                this.chunkDistanceManager.a(sectionposition1, entityplayer);
            }

            if (!flag && flag1) {
                this.playerMap.a(entityplayer);
            }

            if (flag && !flag1) {
                this.playerMap.b(entityplayer);
            }

            if (k != l) {
                this.playerMap.a(k, l, entityplayer);
            }
        }

        int i1 = sectionposition.a();
        int j1 = sectionposition.c();
        int k1;
        int l1;

        if (Math.abs(i1 - i) <= this.viewDistance * 2 && Math.abs(j1 - j) <= this.viewDistance * 2) {
            k1 = Math.min(i, i1) - this.viewDistance;
            l1 = Math.min(j, j1) - this.viewDistance;
            int i2 = Math.max(i, i1) + this.viewDistance;
            int j2 = Math.max(j, j1) + this.viewDistance;

            for (int k2 = k1; k2 <= i2; ++k2) {
                for (int l2 = l1; l2 <= j2; ++l2) {
                    ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(k2, l2);
                    boolean flag3 = a(chunkcoordintpair, i1, j1) <= this.viewDistance;
                    boolean flag4 = a(chunkcoordintpair, i, j) <= this.viewDistance;

                    this.sendChunk(entityplayer, chunkcoordintpair, new Packet[2], flag3, flag4);
                }
            }
        } else {
            ChunkCoordIntPair chunkcoordintpair1;
            boolean flag5;
            boolean flag6;

            for (k1 = i1 - this.viewDistance; k1 <= i1 + this.viewDistance; ++k1) {
                for (l1 = j1 - this.viewDistance; l1 <= j1 + this.viewDistance; ++l1) {
                    chunkcoordintpair1 = new ChunkCoordIntPair(k1, l1);
                    flag5 = true;
                    flag6 = false;
                    this.sendChunk(entityplayer, chunkcoordintpair1, new Packet[2], true, false);
                }
            }

            for (k1 = i - this.viewDistance; k1 <= i + this.viewDistance; ++k1) {
                for (l1 = j - this.viewDistance; l1 <= j + this.viewDistance; ++l1) {
                    chunkcoordintpair1 = new ChunkCoordIntPair(k1, l1);
                    flag5 = false;
                    flag6 = true;
                    this.sendChunk(entityplayer, chunkcoordintpair1, new Packet[2], false, true);
                }
            }
        }

    }

    @Override
    public Stream<EntityPlayer> a(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        return this.playerMap.a(chunkcoordintpair.pair()).filter((entityplayer) -> {
            int i = b(chunkcoordintpair, entityplayer, true);

            return i > this.viewDistance ? false : !flag || i == this.viewDistance;
        });
    }

    protected void addEntity(Entity entity) {
        org.spigotmc.AsyncCatcher.catchOp("entity track"); // Spigot
        if (!(entity instanceof EntityComplexPart)) {
            if (!(entity instanceof EntityLightning)) {
                EntityTypes<?> entitytypes = entity.getEntityType();
                int i = entitytypes.getChunkRange() * 16;
                i = org.spigotmc.TrackingRange.getEntityTrackingRange(entity, i); // Spigot
                int j = entitytypes.getUpdateInterval();

                if (this.trackedEntities.containsKey(entity.getId())) {
                    throw new IllegalStateException("Entity is already tracked!");
                } else {
                    PlayerChunkMap.EntityTracker playerchunkmap_entitytracker = new PlayerChunkMap.EntityTracker(entity, i, j, entitytypes.isDeltaTracking());

                    entity.tracker = playerchunkmap_entitytracker; // Paper - Fast access to tracker
                    this.trackedEntities.put(entity.getId(), playerchunkmap_entitytracker);
                    playerchunkmap_entitytracker.track(this.world.getPlayers());
                    if (entity instanceof EntityPlayer) {
                        EntityPlayer entityplayer = (EntityPlayer) entity;

                        this.a(entityplayer, true);
                        ObjectIterator objectiterator = this.trackedEntities.values().iterator();

                        while (objectiterator.hasNext()) {
                            PlayerChunkMap.EntityTracker playerchunkmap_entitytracker1 = (PlayerChunkMap.EntityTracker) objectiterator.next();

                            if (playerchunkmap_entitytracker1.tracker != entityplayer) {
                                playerchunkmap_entitytracker1.updatePlayer(entityplayer);
                            }
                        }
                    }

                }
            }
        }
    }

    protected void removeEntity(Entity entity) {
        org.spigotmc.AsyncCatcher.catchOp("entity untrack"); // Spigot
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entity;

            this.a(entityplayer, false);
            ObjectIterator objectiterator = this.trackedEntities.values().iterator();

            while (objectiterator.hasNext()) {
                PlayerChunkMap.EntityTracker playerchunkmap_entitytracker = (PlayerChunkMap.EntityTracker) objectiterator.next();

                playerchunkmap_entitytracker.clear(entityplayer);
            }
        }

        PlayerChunkMap.EntityTracker playerchunkmap_entitytracker1 = (PlayerChunkMap.EntityTracker) this.trackedEntities.remove(entity.getId());

        if (playerchunkmap_entitytracker1 != null) {
            playerchunkmap_entitytracker1.a();
        }
        entity.tracker = null; // Paper - We're no longer tracked
    }

    protected void g() {
        List<EntityPlayer> list = Lists.newArrayList();
        List<EntityPlayer> list1 = this.world.getPlayers();

        PlayerChunkMap.EntityTracker playerchunkmap_entitytracker;
        ObjectIterator objectiterator;
        world.timings.tracker1.startTiming(); // Paper

        for (objectiterator = this.trackedEntities.values().iterator(); objectiterator.hasNext(); playerchunkmap_entitytracker.trackerEntry.a()) {
            playerchunkmap_entitytracker = (PlayerChunkMap.EntityTracker) objectiterator.next();
            SectionPosition sectionposition = playerchunkmap_entitytracker.e;
            SectionPosition sectionposition1 = SectionPosition.a(playerchunkmap_entitytracker.tracker);

            if (!Objects.equals(sectionposition, sectionposition1)) {
                playerchunkmap_entitytracker.track(list1);
                Entity entity = playerchunkmap_entitytracker.tracker;

                if (entity instanceof EntityPlayer) {
                    list.add((EntityPlayer) entity);
                }

                playerchunkmap_entitytracker.e = sectionposition1;
            }
        }
        world.timings.tracker1.stopTiming(); // Paper

        objectiterator = this.trackedEntities.values().iterator();

        world.timings.tracker2.startTiming(); // Paper
        while (objectiterator.hasNext()) {
            playerchunkmap_entitytracker = (PlayerChunkMap.EntityTracker) objectiterator.next();
            playerchunkmap_entitytracker.track(list);
        }
        world.timings.tracker2.stopTiming(); // Paper

    }

    protected void broadcast(Entity entity, Packet<?> packet) {
        PlayerChunkMap.EntityTracker playerchunkmap_entitytracker = (PlayerChunkMap.EntityTracker) this.trackedEntities.get(entity.getId());

        if (playerchunkmap_entitytracker != null) {
            playerchunkmap_entitytracker.broadcast(packet);
        }

    }

    protected void broadcastIncludingSelf(Entity entity, Packet<?> packet) {
        PlayerChunkMap.EntityTracker playerchunkmap_entitytracker = (PlayerChunkMap.EntityTracker) this.trackedEntities.get(entity.getId());

        if (playerchunkmap_entitytracker != null) {
            playerchunkmap_entitytracker.broadcastIncludingSelf(packet);
        }

    }

    private void a(EntityPlayer entityplayer, Packet<?>[] apacket, Chunk chunk) {
        if (apacket[0] == null) {
            apacket[0] = new PacketPlayOutMapChunk(chunk, 65535, true); // Paper - Anti-Xray
            apacket[1] = new PacketPlayOutLightUpdate(chunk.getPos(), this.lightEngine);
        }

        entityplayer.a(chunk.getPos(), apacket[0], apacket[1]);
        PacketDebug.a(this.world, chunk.getPos());
        List<Entity> list = Lists.newArrayList();
        List<Entity> list1 = Lists.newArrayList();
        ObjectIterator objectiterator = this.trackedEntities.values().iterator();

        while (objectiterator.hasNext()) {
            PlayerChunkMap.EntityTracker playerchunkmap_entitytracker = (PlayerChunkMap.EntityTracker) objectiterator.next();
            Entity entity = playerchunkmap_entitytracker.tracker;

            if (entity != entityplayer && entity.chunkX == chunk.getPos().x && entity.chunkZ == chunk.getPos().z) {
                playerchunkmap_entitytracker.updatePlayer(entityplayer);
                if (entity instanceof EntityInsentient && ((EntityInsentient) entity).getLeashHolder() != null) {
                    list.add(entity);
                }

                if (!entity.getPassengers().isEmpty()) {
                    list1.add(entity);
                }
            }
        }

        Iterator iterator;
        Entity entity1;

        if (!list.isEmpty()) {
            iterator = list.iterator();

            while (iterator.hasNext()) {
                entity1 = (Entity) iterator.next();
                entityplayer.playerConnection.sendPacket(new PacketPlayOutAttachEntity(entity1, ((EntityInsentient) entity1).getLeashHolder()));
            }
        }

        if (!list1.isEmpty()) {
            iterator = list1.iterator();

            while (iterator.hasNext()) {
                entity1 = (Entity) iterator.next();
                entityplayer.playerConnection.sendPacket(new PacketPlayOutMount(entity1));
            }
        }

    }

    public VillagePlace getVillagePlace() { return this.h(); } // Paper - OBFHELPER
    protected VillagePlace h() {
        return this.n;
    }

    public CompletableFuture<Void> a(Chunk chunk) {
        return this.executor.e(() -> {
            chunk.a(this.world);
        });
    }

    public class EntityTracker {

        private final EntityTrackerEntry trackerEntry;
        private final Entity tracker;
        private final int trackingDistance;
        private SectionPosition e;
        // Paper start
        // Replace trackedPlayers Set with a Map. The value is true until the player receives
        // their first update (which is forced to have absolute coordinates), false afterward.
        public java.util.Map<EntityPlayer, Boolean> trackedPlayerMap = new java.util.HashMap<>();
        public Set<EntityPlayer> trackedPlayers = trackedPlayerMap.keySet();

        public EntityTracker(Entity entity, int i, int j, boolean flag) {
            this.trackerEntry = new EntityTrackerEntry(PlayerChunkMap.this.world, entity, j, flag, this::broadcast, trackedPlayerMap); // CraftBukkit // Paper
            this.tracker = entity;
            this.trackingDistance = i;
            this.e = SectionPosition.a(entity);
        }

        public boolean equals(Object object) {
            return object instanceof PlayerChunkMap.EntityTracker ? ((PlayerChunkMap.EntityTracker) object).tracker.getId() == this.tracker.getId() : false;
        }

        public int hashCode() {
            return this.tracker.getId();
        }

        public void broadcast(Packet<?> packet) {
            Iterator iterator = this.trackedPlayers.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                entityplayer.playerConnection.sendPacket(packet);
            }

        }

        public void broadcastIncludingSelf(Packet<?> packet) {
            this.broadcast(packet);
            if (this.tracker instanceof EntityPlayer) {
                ((EntityPlayer) this.tracker).playerConnection.sendPacket(packet);
            }

        }

        public void a() {
            Iterator iterator = this.trackedPlayers.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                this.trackerEntry.a(entityplayer);
            }

        }

        public void clear(EntityPlayer entityplayer) {
            org.spigotmc.AsyncCatcher.catchOp("player tracker clear"); // Spigot
            if (this.trackedPlayers.remove(entityplayer)) {
                this.trackerEntry.a(entityplayer);
            }

        }

        public void updatePlayer(EntityPlayer entityplayer) {
            org.spigotmc.AsyncCatcher.catchOp("player tracker update"); // Spigot
            if (entityplayer != this.tracker) {
                Vec3D vec3d = (new Vec3D(entityplayer.locX, entityplayer.locY, entityplayer.locZ)).d(this.tracker.getPositionVector()); // MC-155077, SPIGOT-5113
                int i = Math.min(this.trackingDistance, (PlayerChunkMap.this.viewDistance - 1) * 16);
                boolean flag = vec3d.x >= (double) (-i) && vec3d.x <= (double) i && vec3d.z >= (double) (-i) && vec3d.z <= (double) i && this.tracker.a(entityplayer);

                if (flag) {
                    boolean flag1 = this.tracker.attachedToPlayer;

                    if (!flag1) {
                        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(this.tracker.chunkX, this.tracker.chunkZ);
                        PlayerChunk playerchunk = PlayerChunkMap.this.getVisibleChunk(chunkcoordintpair.pair());

                        if (playerchunk != null && playerchunk.getChunk() != null) {
                            flag1 = PlayerChunkMap.b(chunkcoordintpair, entityplayer, false) <= PlayerChunkMap.this.viewDistance;
                        }
                    }

                    // CraftBukkit start - respect vanish API
                    if (this.tracker instanceof EntityPlayer) {
                        Player player = ((EntityPlayer) this.tracker).getBukkitEntity();
                        if (!entityplayer.getBukkitEntity().canSee(player)) {
                            flag1 = false;
                        }
                    }

                    entityplayer.removeQueue.remove(Integer.valueOf(this.tracker.getId()));
                    // CraftBukkit end

                    if (flag1 && this.trackedPlayerMap.putIfAbsent(entityplayer, true) == null) { // Paper
                        this.trackerEntry.b(entityplayer);
                    }
                } else if (this.trackedPlayers.remove(entityplayer)) {
                    this.trackerEntry.a(entityplayer);
                }

            }
        }

        public void track(List<EntityPlayer> list) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                this.updatePlayer(entityplayer);
            }

        }
    }

    class a extends ChunkMapDistance {

        protected a(Executor executor, Executor executor1) {
            super(executor, executor1);
        }

        @Override
        protected boolean a(long i) {
            return PlayerChunkMap.this.unloadQueue.contains(i);
        }

        @Nullable
        @Override
        protected PlayerChunk b(long i) {
            return PlayerChunkMap.this.getUpdatingChunk(i);
        }

        @Nullable
        @Override
        protected PlayerChunk a(long i, int j, @Nullable PlayerChunk playerchunk, int k) {
            return PlayerChunkMap.this.a(i, j, playerchunk, k);
        }
    }
}
