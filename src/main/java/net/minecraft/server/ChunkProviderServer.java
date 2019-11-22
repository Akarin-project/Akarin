package net.minecraft.server;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.destroystokyo.paper.exception.ServerInternalException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderServer extends IChunkProvider {

    private static final int b = (int) Math.pow(17.0D, 2.0D);
    private static final List<ChunkStatus> c = ChunkStatus.a(); static final List<ChunkStatus> getPossibleChunkStatuses() { return ChunkProviderServer.c; } // Paper - OBFHELPER
    private final ChunkMapDistance chunkMapDistance;
    public final ChunkGenerator<?> chunkGenerator;
    private final WorldServer world;
    private final Thread serverThread;
    private final LightEngineThreaded lightEngine;
    public final ChunkProviderServer.a serverThreadQueue; // Paper private -> public
    public final PlayerChunkMap playerChunkMap;
    private final WorldPersistentData worldPersistentData;
    private long lastTickTime;
    public boolean allowMonsters = true;
    public boolean allowAnimals = true;
    private final long[] cachePos = new long[4];
    private final ChunkStatus[] cacheStatus = new ChunkStatus[4];
    private final IChunkAccess[] cacheChunk = new IChunkAccess[4];

    public ChunkProviderServer(WorldServer worldserver, File file, DataFixer datafixer, DefinedStructureManager definedstructuremanager, Executor executor, ChunkGenerator<?> chunkgenerator, int i, WorldLoadListener worldloadlistener, Supplier<WorldPersistentData> supplier) {
        this.world = worldserver;
        this.serverThreadQueue = new ChunkProviderServer.a(worldserver);
        this.chunkGenerator = chunkgenerator;
        this.serverThread = Thread.currentThread();
        File file1 = worldserver.getWorldProvider().getDimensionManager().a(file);
        File file2 = new File(file1, "data");

        file2.mkdirs();
        this.worldPersistentData = new WorldPersistentData(file2, datafixer);
        this.playerChunkMap = new PlayerChunkMap(worldserver, file, datafixer, definedstructuremanager, executor, this.serverThreadQueue, this, this.getChunkGenerator(), worldloadlistener, supplier, i);
        this.lightEngine = this.playerChunkMap.a();
        this.chunkMapDistance = this.playerChunkMap.e();
        this.clearCache();
    }

    @Override
    public LightEngineThreaded getLightEngine() {
        return this.lightEngine;
    }

    @Nullable
    private PlayerChunk getChunk(long i) {
        return this.playerChunkMap.getVisibleChunk(i);
    }

    public int b() {
        return this.playerChunkMap.c();
    }

    private void a(long i, IChunkAccess ichunkaccess, ChunkStatus chunkstatus) {
        for (int j = 3; j > 0; --j) {
            this.cachePos[j] = this.cachePos[j - 1];
            this.cacheStatus[j] = this.cacheStatus[j - 1];
            this.cacheChunk[j] = this.cacheChunk[j - 1];
        }

        this.cachePos[0] = i;
        this.cacheStatus[0] = chunkstatus;
        this.cacheChunk[0] = ichunkaccess;
    }

    // Paper start - "real" get chunk if loaded
    // Note: Partially copied from the getChunkAt method below
    @Nullable
    public Chunk getChunkAtIfCachedImmediately(int x, int z) {
        long k = ChunkCoordIntPair.pair(x, z);

        // Note: Bypass cache to make this MT-Safe

        PlayerChunk playerChunk = this.getChunk(k);
        if (playerChunk == null) {
            return null;
        }

        return playerChunk.getFullChunkIfCached();
    }

    @Nullable
    public Chunk getChunkAtIfLoadedImmediately(int x, int z) {
        long k = ChunkCoordIntPair.pair(x, z);

        // Note: Bypass cache since we need to check ticket level, and to make this MT-Safe

        PlayerChunk playerChunk = this.getChunk(k);
        if (playerChunk == null) {
            return null;
        }

        return playerChunk.getFullChunk();
    }

    @Nullable
    public IChunkAccess getChunkAtImmediately(int x, int z) {
        long k = ChunkCoordIntPair.pair(x, z);

        // Note: Bypass cache to make this MT-Safe

        PlayerChunk playerChunk = this.getChunk(k);
        if (playerChunk == null) {
            return null;
        }

        return playerChunk.getAvailableChunkNow();

    }

    private long asyncLoadSeqCounter;

    public void getChunkAtAsynchronously(int x, int z, boolean gen, java.util.function.Consumer<Chunk> onComplete) {
        if (Thread.currentThread() != this.serverThread) {
            this.serverThreadQueue.execute(() -> {
                this.getChunkAtAsynchronously(x, z, gen, onComplete);
            });
            return;
        }

        long k = ChunkCoordIntPair.pair(x, z);
        ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(x, z);

        IChunkAccess ichunkaccess;

        // try cache
        for (int l = 0; l < 4; ++l) {
            if (k == this.cachePos[l] && ChunkStatus.FULL == this.cacheStatus[l]) {
                ichunkaccess = this.cacheChunk[l];
                if (ichunkaccess != null) { // CraftBukkit - the chunk can become accessible in the meantime TODO for non-null chunks it might also make sense to check that the chunk's state hasn't changed in the meantime

                    // move to first in cache

                    for (int i1 = 3; i1 > 0; --i1) {
                        this.cachePos[i1] = this.cachePos[i1 - 1];
                        this.cacheStatus[i1] = this.cacheStatus[i1 - 1];
                        this.cacheChunk[i1] = this.cacheChunk[i1 - 1];
                    }

                    this.cachePos[0] = k;
                    this.cacheStatus[0] = ChunkStatus.FULL;
                    this.cacheChunk[0] = ichunkaccess;

                    onComplete.accept((Chunk)ichunkaccess);

                    return;
                }
            }
        }

        if (gen) {
            this.bringToFullStatusAsync(x, z, chunkPos, onComplete);
            return;
        }

        IChunkAccess current = this.getChunkAtImmediately(x, z); // we want to bypass ticket restrictions
        if (current != null) {
            if (!(current instanceof ProtoChunkExtension) && !(current instanceof net.minecraft.server.Chunk)) {
                onComplete.accept(null); // the chunk is not gen'd
                return;
            }
            // we know the chunk is at full status here (either in read-only mode or the real thing)
            this.bringToFullStatusAsync(x, z, chunkPos, onComplete);
            return;
        }

        ChunkStatus status = world.getChunkProvider().playerChunkMap.getStatusOnDiskNoLoad(x, z);

        if (status != null && status != ChunkStatus.FULL) {
            // does not exist on disk
            onComplete.accept(null);
            return;
        }

        if (status == ChunkStatus.FULL) {
            this.bringToFullStatusAsync(x, z, chunkPos, onComplete);
            return;
        }

        // status is null here

        // here we don't know what status it is and we're not supposed to generate
        // so we asynchronously load empty status

        this.bringToStatusAsync(x, z, chunkPos, ChunkStatus.EMPTY, (IChunkAccess chunk) -> {
            if (!(chunk instanceof ProtoChunkExtension) && !(chunk instanceof net.minecraft.server.Chunk)) {
                // the chunk on disk was not a full status chunk
                onComplete.accept(null);
                return;
            }
            this.bringToFullStatusAsync(x, z, chunkPos, onComplete); // bring to full status if required
        });
    }

    private void bringToFullStatusAsync(int x, int z, ChunkCoordIntPair chunkPos, java.util.function.Consumer<Chunk> onComplete) {
        this.bringToStatusAsync(x, z, chunkPos, ChunkStatus.FULL, (java.util.function.Consumer)onComplete);
    }

    private void bringToStatusAsync(int x, int z, ChunkCoordIntPair chunkPos, ChunkStatus status, java.util.function.Consumer<IChunkAccess> onComplete) {
        CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> future = this.getChunkFutureMainThread(x, z, status, true);
        Long identifier = Long.valueOf(this.asyncLoadSeqCounter++);
        int ticketLevel = MCUtil.getTicketLevelFor(status);
        this.addTicketAtLevel(TicketType.ASYNC_LOAD, chunkPos, ticketLevel, identifier);

        future.whenCompleteAsync((Either<IChunkAccess, PlayerChunk.Failure> either, Throwable throwable) -> {
            // either left -> success
            // either right -> failure

            if (throwable != null) {
                throw new RuntimeException(throwable);
            }

            this.removeTicketAtLevel(TicketType.ASYNC_LOAD, chunkPos, ticketLevel, identifier);
            this.addTicketAtLevel(TicketType.UNKNOWN, chunkPos, ticketLevel, chunkPos); // allow unloading

            Optional<PlayerChunk.Failure> failure = either.right();

            if (failure.isPresent()) {
                // failure
                throw new IllegalStateException("Chunk failed to load: " + failure.get().toString());
            }

            onComplete.accept(either.left().get());

        }, this.serverThreadQueue);
    }

    public <T> void addTicketAtLevel(TicketType<T> ticketType, ChunkCoordIntPair chunkPos, int ticketLevel, T identifier) {
        this.chunkMapDistance.addTicketAtLevel(ticketType, chunkPos, ticketLevel, identifier);
    }

    public <T> void removeTicketAtLevel(TicketType<T> ticketType, ChunkCoordIntPair chunkPos, int ticketLevel, T identifier) {
        this.chunkMapDistance.removeTicketAtLevel(ticketType, chunkPos, ticketLevel, identifier);
    }
    // Paper end

    @Nullable
    @Override
    public IChunkAccess getChunkAt(int i, int j, ChunkStatus chunkstatus, boolean flag) {
        final int x = i; final int z = j; // Paper - conflict on variable change
        if (Thread.currentThread() != this.serverThread) {
            return (IChunkAccess) CompletableFuture.supplyAsync(() -> {
                return this.getChunkAt(i, j, chunkstatus, flag);
            }, this.serverThreadQueue).join();
        } else {
            long k = ChunkCoordIntPair.pair(i, j);

            IChunkAccess ichunkaccess;

            for (int l = 0; l < 4; ++l) {
                if (k == this.cachePos[l] && chunkstatus == this.cacheStatus[l]) {
                    ichunkaccess = this.cacheChunk[l];
                    if (ichunkaccess != null) { // CraftBukkit - the chunk can become accessible in the meantime TODO for non-null chunks it might also make sense to check that the chunk's state hasn't changed in the meantime
                        return ichunkaccess;
                    }
                }
            }

            CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> completablefuture = this.getChunkFutureMainThread(i, j, chunkstatus, flag);

            if (!completablefuture.isDone()) { // Paper
                // Paper start - async chunk io/loading
                this.world.asyncChunkTaskManager.raisePriority(x, z, com.destroystokyo.paper.io.PrioritizedTaskQueue.HIGHEST_PRIORITY);
                com.destroystokyo.paper.io.chunk.ChunkTaskManager.pushChunkWait(this.world, x, z);
                // Paper end
                com.destroystokyo.paper.io.SyncLoadFinder.logSyncLoad(this.world, x, z); // Paper - sync load info
                this.world.timings.chunkAwait.startTiming(); // Paper
            this.serverThreadQueue.awaitTasks(completablefuture::isDone);
                com.destroystokyo.paper.io.chunk.ChunkTaskManager.popChunkWait(); // Paper - async chunk debug
                this.world.timings.chunkAwait.stopTiming(); // Paper
            } // Paper
            ichunkaccess = (IChunkAccess) ((Either) completablefuture.join()).map((ichunkaccess1) -> {
                return ichunkaccess1;
            }, (playerchunk_failure) -> {
                if (flag) {
                    throw new IllegalStateException("Chunk not there when requested: " + playerchunk_failure);
                } else {
                    return null;
                }
            });
            this.a(k, ichunkaccess, chunkstatus);
            return ichunkaccess;
        }
    }

    @Nullable
    @Override
    public Chunk a(int i, int j) {
        if (Thread.currentThread() != this.serverThread) {
            return null;
        } else {
            long k = ChunkCoordIntPair.pair(i, j);

            for (int l = 0; l < 4; ++l) {
                if (k == this.cachePos[l] && this.cacheStatus[l] == ChunkStatus.FULL) {
                    IChunkAccess ichunkaccess = this.cacheChunk[l];

                    return ichunkaccess instanceof Chunk ? (Chunk) ichunkaccess : null;
                }
            }

            PlayerChunk playerchunk = this.getChunk(k);

            if (playerchunk == null) {
                return null;
            } else {
                Either<IChunkAccess, PlayerChunk.Failure> either = (Either) playerchunk.b(ChunkStatus.FULL).getNow(null); // Craftbukkit - decompile error

                if (either == null) {
                    return null;
                } else {
                    IChunkAccess ichunkaccess1 = (IChunkAccess) either.left().orElse(null); // Craftbukkit - decompile error

                    if (ichunkaccess1 != null) {
                        this.a(k, ichunkaccess1, ChunkStatus.FULL);
                        if (ichunkaccess1 instanceof Chunk) {
                            return (Chunk) ichunkaccess1;
                        }
                    }

                    return null;
                }
            }
        }
    }

    private void clearCache() {
        Arrays.fill(this.cachePos, ChunkCoordIntPair.a);
        Arrays.fill(this.cacheStatus, (Object) null);
        Arrays.fill(this.cacheChunk, (Object) null);
    }

    private CompletableFuture<Either<IChunkAccess, PlayerChunk.Failure>> getChunkFutureMainThread(int i, int j, ChunkStatus chunkstatus, boolean flag) {
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i, j);
        long k = chunkcoordintpair.pair();
        int l = 33 + ChunkStatus.a(chunkstatus);
        PlayerChunk playerchunk = this.getChunk(k);

        // CraftBukkit start - don't add new ticket for currently unloading chunk
        boolean currentlyUnloading = false;
        if (playerchunk != null) {
            PlayerChunk.State oldChunkState = PlayerChunk.getChunkState(playerchunk.oldTicketLevel);
            PlayerChunk.State currentChunkState = PlayerChunk.getChunkState(playerchunk.getTicketLevel());
            currentlyUnloading = (oldChunkState.isAtLeast(PlayerChunk.State.BORDER) && !currentChunkState.isAtLeast(PlayerChunk.State.BORDER));
        }
        if (flag && !currentlyUnloading) {
            // CraftBukkit end
            this.chunkMapDistance.a(TicketType.UNKNOWN, chunkcoordintpair, l, chunkcoordintpair);
            if (this.a(playerchunk, l)) {
                GameProfilerFiller gameprofilerfiller = this.world.getMethodProfiler();

                gameprofilerfiller.enter("chunkLoad");
                this.tickDistanceManager();
                playerchunk = this.getChunk(k);
                gameprofilerfiller.exit();
                if (this.a(playerchunk, l)) {
                    throw new IllegalStateException("No chunk holder after ticket has been added");
                }
            }
        }

        return this.a(playerchunk, l) ? PlayerChunk.UNLOADED_CHUNK_ACCESS_FUTURE : playerchunk.a(chunkstatus, this.playerChunkMap);
    }

    private boolean a(@Nullable PlayerChunk playerchunk, int i) {
        return playerchunk == null || playerchunk.oldTicketLevel > i; // CraftBukkit using oldTicketLevel for isLoaded checks
    }

    public boolean isLoaded(int i, int j) {
        PlayerChunk playerchunk = this.getChunk((new ChunkCoordIntPair(i, j)).pair());
        int k = 33 + ChunkStatus.a(ChunkStatus.FULL);

        return !this.a(playerchunk, k);
    }

    @Override
    public IBlockAccess c(int i, int j) {
        long k = ChunkCoordIntPair.pair(i, j);
        PlayerChunk playerchunk = this.getChunk(k);

        if (playerchunk == null) {
            return null;
        } else {
            int l = ChunkProviderServer.c.size() - 1;

            while (true) {
                ChunkStatus chunkstatus = (ChunkStatus) ChunkProviderServer.c.get(l);
                Optional<IChunkAccess> optional = ((Either) playerchunk.getStatusFutureUnchecked(chunkstatus).getNow(PlayerChunk.UNLOADED_CHUNK_ACCESS)).left();

                if (optional.isPresent()) {
                    return (IBlockAccess) optional.get();
                }

                if (chunkstatus == ChunkStatus.LIGHT.e()) {
                    return null;
                }

                --l;
            }
        }
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    public boolean runTasks() {
        return this.serverThreadQueue.executeNext();
    }

    private boolean tickDistanceManager() {
        boolean flag = this.chunkMapDistance.a(this.playerChunkMap);
        boolean flag1 = this.playerChunkMap.b();

        if (!flag && !flag1) {
            return false;
        } else {
            this.clearCache();
            return true;
        }
    }

    @Override
    public boolean a(Entity entity) {
        long i = ChunkCoordIntPair.pair(MathHelper.floor(entity.locX) >> 4, MathHelper.floor(entity.locZ) >> 4);

        return this.a(i, PlayerChunk::b);
    }

    @Override
    public boolean a(ChunkCoordIntPair chunkcoordintpair) {
        return this.a(chunkcoordintpair.pair(), PlayerChunk::b);
    }

    @Override
    public boolean a(BlockPosition blockposition) {
        long i = ChunkCoordIntPair.pair(blockposition.getX() >> 4, blockposition.getZ() >> 4);

        return this.a(i, PlayerChunk::a);
    }

    public boolean b(Entity entity) {
        long i = ChunkCoordIntPair.pair(MathHelper.floor(entity.locX) >> 4, MathHelper.floor(entity.locZ) >> 4);

        return this.a(i, PlayerChunk::c);
    }

    private boolean a(long i, Function<PlayerChunk, CompletableFuture<Either<Chunk, PlayerChunk.Failure>>> function) {
        PlayerChunk playerchunk = this.getChunk(i);

        if (playerchunk == null) {
            return false;
        } else {
            Either<Chunk, PlayerChunk.Failure> either = (Either) ((CompletableFuture) function.apply(playerchunk)).getNow(PlayerChunk.UNLOADED_CHUNK);

            return either.left().isPresent();
        }
    }

    public void save(boolean flag) {
        this.tickDistanceManager();
        try (co.aikar.timings.Timing timed = world.timings.chunkSaveData.startTiming()) { // Paper - Timings
        this.playerChunkMap.save(flag);
        } // Paper - Timings
    }

    // Paper start - duplicate save, but call incremental
    public void saveIncrementally() {
        this.tickDistanceManager();
        try (co.aikar.timings.Timing timed = world.timings.chunkSaveData.startTiming()) { // Paper - Timings
            this.playerChunkMap.saveIncrementally();
        } // Paper - Timings
    }
    // Paper end

    @Override
    public void close() throws IOException {
        // CraftBukkit start
        close(true);
    }

    public void close(boolean save) throws IOException {
        if (save) {
            this.save(true);
        }
        // CraftBukkit end
        this.lightEngine.close();
        this.playerChunkMap.close();
    }

    // CraftBukkit start - modelled on below
    public void purgeUnload() {
        this.world.getMethodProfiler().enter("purge");
        this.chunkMapDistance.purgeTickets();
        this.tickDistanceManager();
        this.world.getMethodProfiler().exitEnter("unload");
        this.playerChunkMap.unloadChunks(() -> true);
        this.world.getMethodProfiler().exit();
        this.clearCache();
    }
    // CraftBukkit end

    public void tick(BooleanSupplier booleansupplier) {
        this.world.getMethodProfiler().enter("purge");
        this.world.timings.doChunkMap.startTiming(); // Spigot
        this.chunkMapDistance.purgeTickets();
        this.tickDistanceManager();
        this.world.timings.doChunkMap.stopTiming(); // Spigot
        this.world.getMethodProfiler().exitEnter("chunks");
        this.world.timings.chunks.startTiming(); // Paper - timings
        this.tickChunks();
        this.world.timings.chunks.stopTiming(); // Paper - timings
        this.world.timings.doChunkUnload.startTiming(); // Spigot
        this.world.getMethodProfiler().exitEnter("unload");
        this.playerChunkMap.unloadChunks(booleansupplier);
        this.world.timings.doChunkUnload.stopTiming(); // Spigot
        this.world.getMethodProfiler().exit();
        this.clearCache();
    }

    private void tickChunks() {
        long i = this.world.getTime();
        long j = i - this.lastTickTime;

        this.lastTickTime = i;
        WorldData worlddata = this.world.getWorldData();
        boolean flag = worlddata.getType() == WorldType.DEBUG_ALL_BLOCK_STATES;
        boolean flag1 = this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && !world.getPlayers().isEmpty(); // CraftBukkit

        if (!flag) {
            this.world.getMethodProfiler().enter("pollingChunks");
            int k = this.world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
            BlockPosition blockposition = this.world.getSpawn();
            boolean flag2 = world.ticksPerAnimalSpawns != 0L && worlddata.getTime() % world.ticksPerAnimalSpawns == 0L; // CraftBukkit // PAIL: TODO monster ticks

            this.world.getMethodProfiler().enter("naturalSpawnCount");
            this.world.timings.countNaturalMobs.startTiming(); // Paper - timings
            int l = this.chunkMapDistance.b();
            EnumCreatureType[] aenumcreaturetype = EnumCreatureType.values();
            // Paper start - per player mob spawning
            int[] worldMobCount;
            if (this.playerChunkMap.playerMobDistanceMap != null) {
                // update distance map
                this.world.timings.playerMobDistanceMapUpdate.startTiming();
                this.playerChunkMap.playerMobDistanceMap.update(this.world.players, this.playerChunkMap.viewDistance);
                this.world.timings.playerMobDistanceMapUpdate.stopTiming();
                // re-set mob counts
                for (EntityPlayer player : this.world.players) {
                    Arrays.fill(player.mobCounts, 0);
                }
                worldMobCount = this.world.countMobs(true);
            } else {
                worldMobCount = this.world.countMobs(false);
            }
            // Paper end

            this.world.timings.countNaturalMobs.stopTiming(); // Paper - timings
            this.world.getMethodProfiler().exit();
            this.playerChunkMap.f().forEach((playerchunk) -> {
                Optional<Chunk> optional = ((Either) playerchunk.b().getNow(PlayerChunk.UNLOADED_CHUNK)).left();

                if (optional.isPresent()) {
                    Chunk chunk = (Chunk) optional.get();

                    this.world.getMethodProfiler().enter("broadcast");
                    this.world.timings.broadcastChunkUpdates.startTiming(); // Paper - timings
                    playerchunk.a(chunk);
                    this.world.timings.broadcastChunkUpdates.stopTiming(); // Paper - timings
                    this.world.getMethodProfiler().exit();
                    ChunkCoordIntPair chunkcoordintpair = playerchunk.i();

                    // Paper start - timings
                    this.world.timings.chunkRangeCheckBig.startTiming();
                    // note: this is just a copy of the expression in the if
                    boolean bigRadiusOutsideRange = !this.playerChunkMap.isOutsideOfRange(chunkcoordintpair);
                    this.world.timings.chunkRangeCheckBig.stopTiming();
                    if (bigRadiusOutsideRange) {
                        // Paper end
                        chunk.b(chunk.q() + j);
                        // Paper start - timings
                        this.world.timings.chunkRangeCheckSmall.startTiming();
                        // note: this is just a copy of the expression in the if
                        boolean smallRadiusOutsideRange = flag1 && (this.allowMonsters || this.allowAnimals) && this.world.getWorldBorder().isInBounds(chunk.getPos()) && !this.playerChunkMap.isOutsideOfRange(chunkcoordintpair, true);
                        this.world.timings.chunkRangeCheckSmall.stopTiming();
                        if (smallRadiusOutsideRange) { // Spigot
                            // Paper end
                            this.world.getMethodProfiler().enter("spawner");
                            this.world.timings.mobSpawn.startTiming(); // Spigot
                            EnumCreatureType[] aenumcreaturetype1 = aenumcreaturetype;
                            int i1 = aenumcreaturetype.length;

                            for (int j1 = 0; j1 < i1; ++j1) {
                                EnumCreatureType enumcreaturetype = aenumcreaturetype1[j1];

                                // CraftBukkit start - Use per-world spawn limits
                                int limit = enumcreaturetype.b();
                                switch (enumcreaturetype) {
                                    case MONSTER:
                                        limit = world.getWorld().getMonsterSpawnLimit();
                                        break;
                                    case CREATURE:
                                        limit = world.getWorld().getAnimalSpawnLimit();
                                        break;
                                    case WATER_CREATURE:
                                        limit = world.getWorld().getWaterAnimalSpawnLimit();
                                        break;
                                    case AMBIENT:
                                        limit = world.getWorld().getAmbientSpawnLimit();
                                        break;
                                }

                                if (limit == 0) {
                                    continue;
                                }
                                // CraftBukkit end

                                if (enumcreaturetype != EnumCreatureType.MISC && (!enumcreaturetype.c() || this.allowAnimals) && (enumcreaturetype.c() || this.allowMonsters) && (!enumcreaturetype.d() || flag2)) {
                                    int k1 = limit * l / ChunkProviderServer.b; // CraftBukkit - use per-world limits

                                    // Paper start - only allow spawns upto the limit per chunk and update count afterwards
                                    int currEntityCount = worldMobCount[enumcreaturetype.ordinal()];
                                    int difference = k1 - currEntityCount;

                                    if (this.world.paperConfig.perPlayerMobSpawns) {
                                        int minDiff = Integer.MAX_VALUE;
                                        for (EntityPlayer entityplayer : this.playerChunkMap.playerMobDistanceMap.getPlayersInRange(chunk.getPos())) {
                                            minDiff = Math.min(limit - this.playerChunkMap.getMobCountNear(entityplayer, enumcreaturetype), minDiff);
                                        }
                                        difference = (minDiff == Integer.MAX_VALUE) ? 0 : minDiff;
                                    }

                                    if (difference > 0) {
                                        int spawnCount = SpawnerCreature.spawnMobs(enumcreaturetype, this.world, chunk, blockposition, difference,
                                            this.world.paperConfig.perPlayerMobSpawns ? this.playerChunkMap::updatePlayerMobTypeMap : null);
                                        worldMobCount[enumcreaturetype.ordinal()] += spawnCount;
                                        // Paper end
                                    }
                                }
                            }

                            this.world.timings.mobSpawn.stopTiming(); // Spigot
                            this.world.getMethodProfiler().exit();
                        }

                        this.world.timings.chunkTicks.startTiming(); // Spigot // Paper
                        this.world.a(chunk, k);
                        this.world.timings.chunkTicks.stopTiming(); // Spigot // Paper
                    }
                }
            });
            this.world.getMethodProfiler().enter("customSpawners");
            if (flag1) {
                try (co.aikar.timings.Timing ignored = this.world.timings.miscMobSpawning.startTiming()) { // Paper - timings
                this.chunkGenerator.doMobSpawning(this.world, this.allowMonsters, this.allowAnimals);
                } // Paper - timings
            }

            this.world.getMethodProfiler().exit();
            this.world.getMethodProfiler().exit();
        }

        this.playerChunkMap.g();
    }

    @Override
    public String getName() {
        return "ServerChunkCache: " + this.h();
    }

    @VisibleForTesting
    public int f() {
        return this.serverThreadQueue.be();
    }

    @Override
    public ChunkGenerator<?> getChunkGenerator() {
        return this.chunkGenerator;
    }

    public int h() {
        return this.playerChunkMap.d();
    }

    public void flagDirty(BlockPosition blockposition) {
        int i = blockposition.getX() >> 4;
        int j = blockposition.getZ() >> 4;
        PlayerChunk playerchunk = this.getChunk(ChunkCoordIntPair.pair(i, j));

        if (playerchunk != null) {
            playerchunk.a(blockposition.getX() & 15, blockposition.getY(), blockposition.getZ() & 15);
        }

    }

    @Override
    public void a(EnumSkyBlock enumskyblock, SectionPosition sectionposition) {
        this.serverThreadQueue.execute(() -> {
            PlayerChunk playerchunk = this.getChunk(sectionposition.u().pair());

            if (playerchunk != null) {
                playerchunk.a(enumskyblock, sectionposition.b());
            }

        });
    }

    public <T> void addTicket(TicketType<T> tickettype, ChunkCoordIntPair chunkcoordintpair, int i, T t0) {
        this.chunkMapDistance.addTicket(tickettype, chunkcoordintpair, i, t0);
    }

    public <T> void removeTicket(TicketType<T> tickettype, ChunkCoordIntPair chunkcoordintpair, int i, T t0) {
        this.chunkMapDistance.removeTicket(tickettype, chunkcoordintpair, i, t0);
    }

    @Override
    public void a(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        this.chunkMapDistance.a(chunkcoordintpair, flag);
    }

    public void movePlayer(EntityPlayer entityplayer) {
        this.playerChunkMap.movePlayer(entityplayer);
    }

    public void removeEntity(Entity entity) {
        this.playerChunkMap.removeEntity(entity);
    }

    public void addEntity(Entity entity) {
        this.playerChunkMap.addEntity(entity);
    }

    public void broadcastIncludingSelf(Entity entity, Packet<?> packet) {
        this.playerChunkMap.broadcastIncludingSelf(entity, packet);
    }

    public void broadcast(Entity entity, Packet<?> packet) {
        this.playerChunkMap.broadcast(entity, packet);
    }

    public void setViewDistance(int i) {
        this.playerChunkMap.setViewDistance(i);
    }

    @Override
    public void a(boolean flag, boolean flag1) {
        this.allowMonsters = flag;
        this.allowAnimals = flag1;
    }

    public WorldPersistentData getWorldPersistentData() {
        return this.worldPersistentData;
    }

    public VillagePlace j() {
        return this.playerChunkMap.h();
    }

    final class a extends IAsyncTaskHandler<Runnable> {

        private a(World world) {
            super("Chunk source main thread executor for " + IRegistry.DIMENSION_TYPE.getKey(world.getWorldProvider().getDimensionManager()));
        }

        @Override
        protected Runnable postToMainThread(Runnable runnable) {
            return runnable;
        }

        @Override
        protected boolean canExecute(Runnable runnable) {
            return true;
        }

        @Override
        protected boolean isNotMainThread() {
            return true;
        }

        @Override
        protected Thread getThread() {
            return ChunkProviderServer.this.serverThread;
        }

        @Override
        protected boolean executeNext() {
        // CraftBukkit start - process pending Chunk loadCallback() and unloadCallback() after each run task
        try {
            boolean execChunkTask = com.destroystokyo.paper.io.chunk.ChunkTaskManager.pollChunkWaitQueue() || ChunkProviderServer.this.world.asyncChunkTaskManager.pollNextChunkTask(); // Paper
            if (ChunkProviderServer.this.tickDistanceManager()) {
                return true;
            } else {
                ChunkProviderServer.this.lightEngine.queueUpdate();
                return super.executeNext() || execChunkTask; // Paper
            }
        } finally {
            playerChunkMap.callbackExecutor.run();
        }
        // CraftBukkit end
        }
    }
}
