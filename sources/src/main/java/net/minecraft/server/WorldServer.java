package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import java.util.logging.Level;

import org.bukkit.WeatherType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.weather.LightningStrikeEvent;
// CraftBukkit end

/**
 * Akarin Changes Note
 * 1) Expose private members (core, safety issue)
 * 2) Removed hardcore difficulty codes (slack service, feature)
 */
public class WorldServer extends World implements IAsyncTaskHandler {

    private static final Logger a = LogManager.getLogger();
    boolean stopPhysicsEvent = false; // Paper
    private final MinecraftServer server;
    public EntityTracker tracker;
    public final PlayerChunkMap manager; // Akarin - private -> public
    public final Map<UUID, Entity> entitiesByUUID = Maps.newHashMap(); // Paper
    public boolean savingDisabled;
    private boolean K;
    private int emptyTime;
    private final PortalTravelAgent portalTravelAgent;
    private final SpawnerCreature spawnerCreature = new SpawnerCreature();
    private final TickListServer<Block> nextTickListBlock;
    private final TickListServer<FluidType> nextTickListFluid;
    protected final VillageSiege siegeManager;
    ObjectLinkedOpenHashSet<BlockActionData> d;
    private boolean Q;

    // CraftBukkit start
    private static final boolean DEBUG_ENTITIES = Boolean.getBoolean("debug.entities"); // Paper
    private static Throwable getAddToWorldStackTrace(Entity entity) {
        return new Throwable(entity + " Added to world at " + new java.util.Date());
    }
    public final int dimension;

    // Add env and gen to constructor
    public WorldServer(MinecraftServer minecraftserver, IDataManager idatamanager, WorldData worlddata, int i, MethodProfiler methodprofiler, org.bukkit.World.Environment env, org.bukkit.generator.ChunkGenerator gen) {
        super(idatamanager, worlddata, DimensionManager.a(env.getId()).d(), methodprofiler, false, gen, env);
        this.dimension = i;
        this.pvpMode = minecraftserver.getPVP();
        worlddata.world = this;
        // CraftBukkit end
        Predicate<Block> predicate = (block) -> { // CraftBukkit - decompile error
            return block == null || block.getBlockData().isAir();
        };
        RegistryBlocks registryblocks = Block.REGISTRY;

        Block.REGISTRY.getClass();
        Function function = registryblocks::b;
        RegistryBlocks<MinecraftKey, Block> registryblocks1 = Block.REGISTRY; // CraftBukkit - decompile error

        Block.REGISTRY.getClass();
        this.nextTickListBlock = new TickListServer<>(this, predicate, function, key -> registryblocks1.get((MinecraftKey) key), e -> b((NextTickListEntry) e), "Blocks"); // CraftBukkit - decompile error // Paper - Timings v2 // Akarin - decompile error
        Predicate<FluidType> predicate2 = (fluidtype) -> {
            return fluidtype == null || fluidtype == FluidTypes.a;
        };
        registryblocks = FluidType.c;
        FluidType.c.getClass();
        function = registryblocks::b;
        RegistryBlocks<MinecraftKey, FluidType> registryblocks2 = FluidType.c; // CraftBukkit - decompile error
        FluidType.c.getClass();
        this.nextTickListFluid = new TickListServer<>(this, predicate2, function, registryblocks2::get, e -> a((NextTickListEntry) e), "Fluids"); // CraftBukkit - decompile error // Paper - Timings v2 // Akarin - decompile error
        this.siegeManager = new VillageSiege(this);
        this.d = new ObjectLinkedOpenHashSet();
        this.server = minecraftserver;
        this.tracker = new EntityTracker(this);
        this.manager = new PlayerChunkMap(this);
        this.worldProvider.a((World) this);
        this.chunkProvider = this.q();
        this.portalTravelAgent = new org.bukkit.craftbukkit.CraftTravelAgent(this); // CraftBukkit
        this.O();
        this.P();
        this.getWorldBorder().a(minecraftserver.aw());
    }

    public GeneratorAccess b() {
        this.worldMaps = new PersistentCollection(this.dataManager);
        String s = PersistentVillage.a(this.worldProvider);
        PersistentVillage persistentvillage = (PersistentVillage) this.worldMaps.get(PersistentVillage::new, s);

        if (persistentvillage == null) {
            this.villages = new PersistentVillage(this);
            this.worldMaps.a(s, this.villages);
        } else {
            this.villages = persistentvillage;
            this.villages.a((World) this);
        }

        if (getServer().getScoreboardManager() == null) { // CraftBukkit
        PersistentScoreboard persistentscoreboard = (PersistentScoreboard) this.worldMaps.get(PersistentScoreboard::new, "scoreboard");

        if (persistentscoreboard == null) {
            persistentscoreboard = new PersistentScoreboard();
            this.worldMaps.a("scoreboard", persistentscoreboard);
        }

        persistentscoreboard.a((Scoreboard) this.server.getScoreboard());
        this.server.getScoreboard().a((Runnable) (new RunnableSaveScoreboard(persistentscoreboard)));
        } // CraftBukkit
        this.getWorldBorder().setCenter(this.worldData.B(), this.worldData.C());
        this.getWorldBorder().setDamageAmount(this.worldData.H());
        this.getWorldBorder().setDamageBuffer(this.worldData.G());
        this.getWorldBorder().setWarningDistance(this.worldData.I());
        this.getWorldBorder().setWarningTime(this.worldData.J());
        if (this.worldData.E() > 0L) {
            this.getWorldBorder().transitionSizeBetween(this.worldData.D(), this.worldData.F(), this.worldData.E());
        } else {
            this.getWorldBorder().setSize(this.worldData.D());
        }

        // CraftBukkit start
        if (generator != null) {
            getWorld().getPopulators().addAll(generator.getDefaultPopulators(getWorld()));
        }
        // CraftBukkit end

        return this;
    }

    // CraftBukkit start
    @Override
    public TileEntity getTileEntity(BlockPosition pos) {
        TileEntity result = super.getTileEntity(pos);
        Block type = getType(pos).getBlock();

        if (type == Blocks.CHEST || type == Blocks.TRAPPED_CHEST) { // Spigot
            if (!(result instanceof TileEntityChest)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.FURNACE) {
            if (!(result instanceof TileEntityFurnace)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.DROPPER) {
            if (!(result instanceof TileEntityDropper)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.DISPENSER) {
            if (!(result instanceof TileEntityDispenser)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.JUKEBOX) {
            if (!(result instanceof TileEntityJukeBox)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.SPAWNER) {
            if (!(result instanceof TileEntityMobSpawner)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if ((type == Blocks.SIGN) || (type == Blocks.WALL_SIGN)) {
            if (!(result instanceof TileEntitySign)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.ENDER_CHEST) {
            if (!(result instanceof TileEntityEnderChest)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.BREWING_STAND) {
            if (!(result instanceof TileEntityBrewingStand)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.BEACON) {
            if (!(result instanceof TileEntityBeacon)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.HOPPER) {
            if (!(result instanceof TileEntityHopper)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.ENCHANTING_TABLE) {
            if (!(result instanceof TileEntityEnchantTable)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.END_PORTAL) {
            if (!(result instanceof TileEntityEnderPortal)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type instanceof BlockSkullAbstract) {
            if (!(result instanceof TileEntitySkull)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.DAYLIGHT_DETECTOR) {
            if (!(result instanceof TileEntityLightDetector)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.COMPARATOR) {
            if (!(result instanceof TileEntityComparator)) {
                result = fixTileEntity(pos, type, result);
            }
        }else if (type instanceof BlockBannerAbstract) {
            if (!(result instanceof TileEntityBanner)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.STRUCTURE_BLOCK) {
            if (!(result instanceof TileEntityStructure)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.END_GATEWAY) {
            if (!(result instanceof TileEntityEndGateway)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.COMMAND_BLOCK) {
            if (!(result instanceof TileEntityCommand)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type == Blocks.STRUCTURE_BLOCK) {
            if (!(result instanceof TileEntityStructure)) {
                result = fixTileEntity(pos, type, result);
            }
        } else if (type instanceof BlockBed) {
            if (!(result instanceof TileEntityBed)) {
                result = fixTileEntity(pos, type, result);
            }
        }
        // Paper Start - add TE fix checks for shulkers, see nms.BlockShulkerBox
        else if (type instanceof BlockShulkerBox) {
            if (!(result instanceof TileEntityShulkerBox)) {
                result = fixTileEntity(pos, type, result);
            }
        }
        // Paper end

        return result;
    }

    private TileEntity fixTileEntity(BlockPosition pos, Block type, TileEntity found) {
        this.getServer().getLogger().log(Level.SEVERE, "Block at {0},{1},{2} is {3} but has {4}" + ". "
                + "Bukkit will attempt to fix this, but there may be additional damage that we cannot recover.", new Object[]{pos.getX(), pos.getY(), pos.getZ(), type, found});

        if (type instanceof ITileEntity) {
            TileEntity replacement = ((ITileEntity) type).a(this);
            replacement.world = this;
            this.setTileEntity(pos, replacement);
            return replacement;
        } else {
            this.getServer().getLogger().severe("Don't know how to fix for this type... Can't do anything! :(");
            return found;
        }
    }
    // CraftBukkit end

    public void doTick() {
        this.Q = true;
        super.doTick();
        // Akarin start - goes to slack service
        /*
        if (this.getWorldData().isHardcore() && this.getDifficulty() != EnumDifficulty.HARD) {
            this.getWorldData().setDifficulty(EnumDifficulty.HARD);
        }
        */
        // Akarin end

        this.chunkProvider.getChunkGenerator().getWorldChunkManager().Y_();
        if (this.everyoneDeeplySleeping()) {
            if (this.getGameRules().getBoolean("doDaylightCycle")) {
                long i = this.worldData.getDayTime() + 24000L;

                this.worldData.setDayTime(i - i % 24000L);
            }

            this.h();
        }

        this.methodProfiler.a("spawner");
        // CraftBukkit start - Only call spawner if we have players online and the world allows for mobs or animals
        long time = this.worldData.getTime();
        if (this.getGameRules().getBoolean("doMobSpawning") && this.worldData.getType() != WorldType.DEBUG_ALL_BLOCK_STATES && (this.allowMonsters || this.allowAnimals) && (this instanceof WorldServer && this.players.size() > 0)) {
            timings.mobSpawn.startTiming(); // Spigot
            this.spawnerCreature.a(this, this.allowMonsters && (this.ticksPerMonsterSpawns != 0 && time % this.ticksPerMonsterSpawns == 0L), this.allowAnimals && (this.ticksPerAnimalSpawns != 0 && time % this.ticksPerAnimalSpawns == 0L), this.worldData.getTime() % 400L == 0L);
            this.getChunkProviderServer().a(this, this.allowMonsters && (this.ticksPerMonsterSpawns != 0 && time % this.ticksPerMonsterSpawns == 0L), this.allowAnimals && (this.ticksPerAnimalSpawns != 0 && time % this.ticksPerAnimalSpawns == 0L));
            timings.mobSpawn.stopTiming(); // Spigot
            // CraftBukkit end
        }

        timings.doChunkUnload.startTiming(); // Spigot
        this.methodProfiler.c("chunkSource");
        this.chunkProvider.unloadChunks();
        int j = this.a(1.0F);

        if (j != this.c()) {
            this.c(j);
        }

        this.worldData.setTime(this.worldData.getTime() + 1L);
        if (this.getGameRules().getBoolean("doDaylightCycle")) {
            this.worldData.setDayTime(this.worldData.getDayTime() + 1L);
        }

        timings.doChunkUnload.stopTiming(); // Spigot
        this.methodProfiler.c("tickPending");
        timings.scheduledBlocks.startTiming(); // Paper
        this.p();
        timings.scheduledBlocks.stopTiming(); // Paper
        this.methodProfiler.c("tickBlocks");
        timings.chunkTicks.startTiming(); // Paper
        this.l();
        timings.chunkTicks.stopTiming(); // Paper
        this.methodProfiler.c("chunkMap");
        timings.doChunkMap.startTiming(); // Spigot
        this.manager.flush();
        timings.doChunkMap.stopTiming(); // Spigot
        this.methodProfiler.c("village");
        timings.doVillages.startTiming(); // Spigot
        this.villages.tick();
        this.siegeManager.a();
        timings.doVillages.stopTiming(); // Spigot
        this.methodProfiler.c("portalForcer");
        timings.doPortalForcer.startTiming(); // Spigot
        this.portalTravelAgent.a(this.getTime());
        timings.doPortalForcer.stopTiming(); // Spigot
        this.methodProfiler.e();
        timings.doSounds.startTiming(); // Spigot
        this.am();
        timings.doSounds.stopTiming(); // Spigot
        this.Q = false;

        timings.doChunkGC.startTiming();// Spigot
        this.getWorld().processChunkGC(); // CraftBukkit
        timings.doChunkGC.stopTiming(); // Spigot
    }

    public boolean j_() {
        return this.Q;
    }

    @Nullable
    public BiomeBase.BiomeMeta a(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        List list = this.getChunkProviderServer().a(enumcreaturetype, blockposition);

        return list.isEmpty() ? null : (BiomeBase.BiomeMeta) WeightedRandom.a(this.random, list);
    }

    public boolean a(EnumCreatureType enumcreaturetype, BiomeBase.BiomeMeta biomebase_biomemeta, BlockPosition blockposition) {
        List list = this.getChunkProviderServer().a(enumcreaturetype, blockposition);

        return list != null && !list.isEmpty() ? list.contains(biomebase_biomemeta) : false;
    }

    public void everyoneSleeping() {
        this.K = false;
        if (!this.players.isEmpty()) {
            int i = 0;
            int j = 0;
            Iterator iterator = this.players.iterator();

            while (iterator.hasNext()) {
                EntityHuman entityhuman = (EntityHuman) iterator.next();

                if (entityhuman.isSpectator()) {
                    ++i;
                } else if (entityhuman.isSleeping() || entityhuman.fauxSleeping) {
                    ++j;
                }
            }

            this.K = j > 0 && j >= this.players.size() - i;
        }

    }

    public ScoreboardServer l_() {
        return this.server.getScoreboard();
    }

    protected void h() {
        this.K = false;
        List list = (List) this.players.stream().filter(EntityHuman::isSleeping).collect(Collectors.toList());
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityHuman entityhuman = (EntityHuman) iterator.next();

            entityhuman.a(false, false, true);
        }

        if (this.getGameRules().getBoolean("doWeatherCycle")) {
            this.ai();
        }

    }

    private void ai() {
        // CraftBukkit start
        this.worldData.setStorm(false);
        // If we stop due to everyone sleeping we should reset the weather duration to some other random value.
        // Not that everyone ever manages to get the whole server to sleep at the same time....
        if (!this.worldData.hasStorm()) {
            this.worldData.setWeatherDuration(0);
        }
        // CraftBukkit end
        this.worldData.setThundering(false);
        // CraftBukkit start
        // If we stop due to everyone sleeping we should reset the weather duration to some other random value.
        // Not that everyone ever manages to get the whole server to sleep at the same time....
        if (!this.worldData.isThundering()) {
            this.worldData.setThunderDuration(0);
        }
        // CraftBukkit end
    }

    public boolean everyoneDeeplySleeping() {
        if (this.K && !this.isClientSide) {
            Iterator iterator = this.players.iterator();

            // CraftBukkit - This allows us to assume that some people are in bed but not really, allowing time to pass in spite of AFKers
            boolean foundActualSleepers = false;

            EntityHuman entityhuman;

            do {
                if (!iterator.hasNext()) {
                    return foundActualSleepers;
                }

                entityhuman = (EntityHuman) iterator.next();

                // CraftBukkit start
                if (entityhuman.isDeeplySleeping()) {
                    foundActualSleepers = true;
                }
            } while (!entityhuman.isSpectator() || entityhuman.isDeeplySleeping() || entityhuman.fauxSleeping);
            // CraftBukkit end

            return false;
        } else {
            return false;
        }
    }

    public boolean isChunkLoaded(int i, int j, boolean flag) {
        return this.a(i, j);
    }

    public boolean a(int i, int j) {
        return this.getChunkProviderServer().isLoaded(i, j);
    }

    protected void n_() {
        this.methodProfiler.a("playerCheckLight");
        if (spigotConfig.randomLightUpdates && !this.players.isEmpty()) { // Spigot
            int i = this.random.nextInt(this.players.size());
            EntityHuman entityhuman = (EntityHuman) this.players.get(i);
            int j = MathHelper.floor(entityhuman.locX) + this.random.nextInt(11) - 5;
            int k = MathHelper.floor(entityhuman.locY) + this.random.nextInt(11) - 5;
            int l = MathHelper.floor(entityhuman.locZ) + this.random.nextInt(11) - 5;

            this.r(new BlockPosition(j, k, l));
        }

        this.methodProfiler.e();
    }

    protected void l() {
        this.n_();
        if (this.worldData.getType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
            Iterator iterator = this.manager.b();

            while (iterator.hasNext()) {
                ((Chunk) iterator.next()).d(false);
            }

        } else {
            int i = this.getGameRules().c("randomTickSpeed");
            boolean flag = this.isRaining();
            boolean flag1 = this.X();

            this.methodProfiler.a("pollingChunks");

            for (Iterator iterator1 = this.manager.b(); iterator1.hasNext(); this.methodProfiler.e()) {
                this.methodProfiler.a("getChunk");
                Chunk chunk = (Chunk) iterator1.next();
                int j = chunk.locX * 16;
                int k = chunk.locZ * 16;

                this.methodProfiler.c("checkNextLight");
                chunk.x();
                this.methodProfiler.c("tickChunk");
                chunk.d(false);
                if ( !chunk.areNeighborsLoaded( 1 ) ) continue; // Spigot
                this.methodProfiler.c("thunder");
                int l;
                BlockPosition blockposition;

                if (!this.paperConfig.disableThunder && flag && flag1 && this.random.nextInt(100000) == 0) { // Paper - Disable thunder
                    this.m = this.m * 3 + 1013904223;
                    l = this.m >> 2;
                    blockposition = this.a(new BlockPosition(j + (l & 15), 0, k + (l >> 8 & 15)));
                    if (this.isRainingAt(blockposition)) {
                        DifficultyDamageScaler difficultydamagescaler = this.getDamageScaler(blockposition);

                        if (this.getGameRules().getBoolean("doMobSpawning") && this.random.nextDouble() < (double) difficultydamagescaler.b() * paperConfig.skeleHorseSpawnChance) {
                            EntityHorseSkeleton entityhorseskeleton = new EntityHorseSkeleton(this);

                            entityhorseskeleton.s(true);
                            entityhorseskeleton.setAgeRaw(0);
                            entityhorseskeleton.setPosition((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
                            this.addEntity(entityhorseskeleton, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.LIGHTNING); // CraftBukkit
                            this.strikeLightning(new EntityLightning(this, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), true));
                        } else {
                            this.strikeLightning(new EntityLightning(this, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), false));
                        }
                    }
                }

                this.methodProfiler.c("iceandsnow");
                if (!this.paperConfig.disableIceAndSnow && this.random.nextInt(16) == 0) { // Paper - Disable ice and snow
                    this.m = this.m * 3 + 1013904223;
                    l = this.m >> 2;
                    blockposition = this.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, new BlockPosition(j + (l & 15), 0, k + (l >> 8 & 15)));
                    BlockPosition blockposition1 = blockposition.down();
                    BiomeBase biomebase = this.getBiome(blockposition);

                    if (biomebase.a((IWorldReader) this, blockposition1)) {
                        org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockFormEvent(this, blockposition1, Blocks.ICE.getBlockData(), null); // CraftBukkit
                    }

                    if (flag && biomebase.b(this, blockposition)) {
                        org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockFormEvent(this, blockposition, Blocks.SNOW.getBlockData(), null); // CraftBukkit
                    }

                    if (flag && this.getBiome(blockposition1).c() == BiomeBase.Precipitation.RAIN) {
                        this.getType(blockposition1).getBlock().c(this, blockposition1);
                    }
                }

                timings.chunkTicksBlocks.startTiming(); // Paper
                if (i > 0) {
                    ChunkSection[] achunksection = chunk.getSections();
                    int i1 = achunksection.length;

                    for (int j1 = 0; j1 < i1; ++j1) {
                        ChunkSection chunksection = achunksection[j1];

                        if (chunksection != Chunk.a && chunksection.b()) {
                            for (int k1 = 0; k1 < i; ++k1) {
                                this.m = this.m * 3 + 1013904223;
                                int l1 = this.m >> 2;
                                int i2 = l1 & 15;
                                int j2 = l1 >> 8 & 15;
                                int k2 = l1 >> 16 & 15;
                                IBlockData iblockdata = chunksection.getType(i2, k2, j2);
                                Fluid fluid = chunksection.b(i2, k2, j2);

                                this.methodProfiler.a("randomTick");
                                if (iblockdata.t()) {
                                    iblockdata.b((World) this, new BlockPosition(i2 + j, k2 + chunksection.getYPosition(), j2 + k), this.random);
                                }

                                if (fluid.h()) {
                                    fluid.b(this, new BlockPosition(i2 + j, k2 + chunksection.getYPosition(), j2 + k), this.random);
                                }

                                this.methodProfiler.e();
                            }
                        }
                    }
                }
                timings.chunkTicksBlocks.stopTiming(); // Paper
            }

            this.methodProfiler.e();
        }
    }

    protected BlockPosition a(BlockPosition blockposition) {
        BlockPosition blockposition1 = this.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, blockposition);
        AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockposition1, new BlockPosition(blockposition1.getX(), this.getHeight(), blockposition1.getZ()))).g(3.0D);
        List list = this.a(EntityLiving.class, axisalignedbb, (Predicate<EntityLiving>) (entityliving) -> { // CraftBukkit - decompile error
            return entityliving != null && entityliving.isAlive() && this.e(entityliving.getChunkCoordinates());
        });

        if (!list.isEmpty()) {
            return ((EntityLiving) list.get(this.random.nextInt(list.size()))).getChunkCoordinates();
        } else {
            if (blockposition1.getY() == -1) {
                blockposition1 = blockposition1.up(2);
            }

            return blockposition1;
        }
    }

    public void tickEntities() {
        if (false && this.players.isEmpty()) { // CraftBukkit - this prevents entity cleanup, other issues on servers with no players
            if (this.emptyTime++ >= 300) {
                return;
            }
        } else {
            this.q_();
        }

        this.worldProvider.l();
        super.tickEntities();
        spigotConfig.currentPrimedTnt = 0; // Spigot
    }

    protected void p_() {
        super.p_();
        this.methodProfiler.c("players");

        for (int i = 0; i < this.players.size(); ++i) {
            Entity entity = (Entity) this.players.get(i);
            Entity entity1 = entity.getVehicle();

            if (entity1 != null) {
                if (!entity1.dead && entity1.w(entity)) {
                    continue;
                }

                entity.stopRiding();
            }

            this.methodProfiler.a("tick");
            if (!entity.dead) {
                try {
                    this.g(entity);
                } catch (Throwable throwable) {
                    CrashReport crashreport = CrashReport.a(throwable, "Ticking player");
                    CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Player being ticked");

                    entity.appendEntityCrashDetails(crashreportsystemdetails);
                    throw new ReportedException(crashreport);
                }
            }

            this.methodProfiler.e();
            this.methodProfiler.a("remove");
            if (entity.dead) {
                int j = entity.ae;
                int k = entity.ag;

                if (entity.inChunk && this.isChunkLoaded(j, k, true)) {
                    this.getChunkAt(j, k).b(entity);
                }

                this.entityList.remove(entity);
                this.c(entity);
            }

            this.methodProfiler.e();
        }

    }

    public void q_() {
        this.emptyTime = 0;
    }

    public void p() {
        if (this.worldData.getType() != WorldType.DEBUG_ALL_BLOCK_STATES) {
            this.nextTickListBlock.a();
            this.nextTickListFluid.a();
        }
    }

    private void a(NextTickListEntry<FluidType> nextticklistentry) {
        Fluid fluid = this.b(nextticklistentry.a);

        if (fluid.c() == nextticklistentry.a()) {
            fluid.a((World) this, nextticklistentry.a);
        }

    }

    private void b(NextTickListEntry<Block> nextticklistentry) {
        IBlockData iblockdata = this.getType(nextticklistentry.a);

        if (iblockdata.getBlock() == nextticklistentry.a()) {
            stopPhysicsEvent = !paperConfig.firePhysicsEventForRedstone && (iblockdata.getBlock() instanceof BlockDiodeAbstract || iblockdata.getBlock() instanceof BlockRedstoneTorch); // Paper
            iblockdata.a((World) this, nextticklistentry.a, this.random);
        }

    }

    /* CraftBukkit start - We prevent spawning in general, so this butchering is not needed
    public void entityJoinedWorld(Entity entity, boolean flag) {
        if (!this.getSpawnAnimals() && (entity instanceof EntityAnimal || entity instanceof EntityWaterAnimal)) {
            entity.die();
        }

        if (!this.getSpawnNPCs() && entity instanceof NPC) {
            entity.die();
        }

        super.entityJoinedWorld(entity, flag);
    }
    // CraftBukkit end */

    private boolean getSpawnNPCs() {
        return this.server.getSpawnNPCs();
    }

    private boolean getSpawnAnimals() {
        return this.server.getSpawnAnimals();
    }

    protected IChunkProvider q() {
        IChunkLoader ichunkloader = this.dataManager.createChunkLoader(this.worldProvider);

        // CraftBukkit start
        org.bukkit.craftbukkit.generator.InternalChunkGenerator gen;

        if (this.generator != null) {
            gen = new org.bukkit.craftbukkit.generator.CustomChunkGenerator(this, this.getSeed(), this.generator);
        } else if (this.worldProvider instanceof WorldProviderHell) {
            gen = new org.bukkit.craftbukkit.generator.NetherChunkGenerator(this, this.getSeed());
        } else if (this.worldProvider instanceof WorldProviderTheEnd) {
            gen = new org.bukkit.craftbukkit.generator.SkyLandsChunkGenerator(this, this.getSeed());
        } else {
            gen = new org.bukkit.craftbukkit.generator.NormalChunkGenerator(this, this.getSeed());
        }

        return new ChunkProviderServer(this, ichunkloader, new co.aikar.timings.TimedChunkGenerator(this, gen), this.server); // Paper
        // CraftBukkit end
    }

    public boolean a(EntityHuman entityhuman, BlockPosition blockposition) {
        return !this.server.a(this, blockposition, entityhuman) && this.getWorldBorder().a(blockposition);
    }

    public void a(WorldSettings worldsettings) {
        if (!this.worldData.v()) {
            try {
                this.b(worldsettings);
                if (this.worldData.getType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
                    this.al();
                }

                super.a(worldsettings);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Exception initializing level");

                try {
                    this.a(crashreport);
                } catch (Throwable throwable1) {
                    ;
                }

                throw new ReportedException(crashreport);
            }

            this.worldData.d(true);
        }

    }

    private void al() {
        this.worldData.f(false);
        this.worldData.c(true);
        this.worldData.setStorm(false);
        this.worldData.setThundering(false);
        this.worldData.g(1000000000);
        this.worldData.setDayTime(6000L);
        this.worldData.setGameType(EnumGamemode.SPECTATOR);
        this.worldData.g(false);
        this.worldData.setDifficulty(EnumDifficulty.PEACEFUL);
        this.worldData.e(true);
        this.getGameRules().set("doDaylightCycle", "false", this.server);
    }

    private void b(WorldSettings worldsettings) {
        if (!this.worldProvider.p()) {
            this.worldData.setSpawn(BlockPosition.ZERO.up(this.chunkProvider.getChunkGenerator().getSpawnHeight()));
        } else if (this.worldData.getType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
            this.worldData.setSpawn(BlockPosition.ZERO.up());
        } else {
            WorldChunkManager worldchunkmanager = this.chunkProvider.getChunkGenerator().getWorldChunkManager();
            List list = worldchunkmanager.a();
            Random random = new Random(this.getSeed());
            BlockPosition blockposition = worldchunkmanager.a(0, 0, 256, list, random);
            ChunkCoordIntPair chunkcoordintpair = blockposition == null ? new ChunkCoordIntPair(0, 0) : new ChunkCoordIntPair(blockposition);

            // CraftBukkit start
            if (this.generator != null) {
                Random rand = new Random(this.getSeed());
                org.bukkit.Location spawn = this.generator.getFixedSpawnLocation(((WorldServer) this).getWorld(), rand);

                if (spawn != null) {
                    if (spawn.getWorld() != ((WorldServer) this).getWorld()) {
                        throw new IllegalStateException("Cannot set spawn point for " + this.worldData.getName() + " to be in another world (" + spawn.getWorld().getName() + ")");
                    } else {
                        this.worldData.setSpawn(new BlockPosition(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()));
                        return;
                    }
                }
            }
            // CraftBukkit end

            if (blockposition == null) {
                WorldServer.a.warn("Unable to find spawn biome");
            }

            boolean flag = false;
            Iterator iterator = TagsBlock.I.a().iterator();

            while (iterator.hasNext()) {
                Block block = (Block) iterator.next();

                if (worldchunkmanager.b().contains(block.getBlockData())) {
                    flag = true;
                    break;
                }
            }

            this.worldData.setSpawn(chunkcoordintpair.h().a(8, this.chunkProvider.getChunkGenerator().getSpawnHeight(), 8));
            int i = 0;
            int j = 0;
            int k = 0;
            int l = -1;
            boolean flag1 = true;

            for (int i1 = 0; i1 < 1024; ++i1) {
                if (i > -16 && i <= 16 && j > -16 && j <= 16) {
                    BlockPosition blockposition1 = this.worldProvider.a(new ChunkCoordIntPair(chunkcoordintpair.x + i, chunkcoordintpair.z + j), flag);

                    if (blockposition1 != null) {
                        this.worldData.setSpawn(blockposition1);
                        break;
                    }
                }

                if (i == j || i < 0 && i == -j || i > 0 && i == 1 - j) {
                    int j1 = k;

                    k = -l;
                    l = j1;
                }

                i += k;
                j += l;
            }

            if (worldsettings.c()) {
                this.r();
            }

        }
    }

    protected void r() {
        WorldGenBonusChest worldgenbonuschest = new WorldGenBonusChest();

        for (int i = 0; i < 10; ++i) {
            int j = this.worldData.b() + this.random.nextInt(6) - this.random.nextInt(6);
            int k = this.worldData.d() + this.random.nextInt(6) - this.random.nextInt(6);
            BlockPosition blockposition = this.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPosition(j, 0, k)).up();

            if (worldgenbonuschest.a(this, this.chunkProvider.getChunkGenerator(), this.random, blockposition, WorldGenFeatureConfiguration.e)) {
                break;
            }
        }

    }

    @Nullable
    public BlockPosition getDimensionSpawn() {
        return this.worldProvider.d();
    }

    public void save(boolean flag, @Nullable IProgressUpdate iprogressupdate) throws ExceptionWorldConflict {
        ChunkProviderServer chunkproviderserver = this.getChunkProviderServer();

        if (chunkproviderserver.e()) {
            if (flag) org.bukkit.Bukkit.getPluginManager().callEvent(new org.bukkit.event.world.WorldSaveEvent(getWorld())); // CraftBukkit // Paper - Incremental Auto Saving - Only fire event on full save
            timings.worldSave.startTiming(); // Paper
            if (flag || server.serverAutoSave) { // Paper
            if (iprogressupdate != null) {
                iprogressupdate.a(new ChatMessage("menu.savingLevel", new Object[0]));
            }

            this.a();
            if (iprogressupdate != null) {
                iprogressupdate.c(new ChatMessage("menu.savingChunks", new Object[0]));
            }
            } // Paper

            timings.worldSaveChunks.startTiming(); // Paper
            chunkproviderserver.a(flag);
            timings.worldSaveChunks.stopTiming(); // Paper
            // CraftBukkit - ArrayList -> Collection
            /* //Paper start Collection arraylist = chunkproviderserver.a();
            Iterator iterator = arraylist.iterator();

            while (iterator.hasNext()) {
                Chunk chunk = (Chunk) iterator.next();

                if (chunk != null && !this.manager.a(chunk.locX, chunk.locZ)) {
                    chunkproviderserver.unload(chunk);
                }
            }*/
            // Paper end
            timings.worldSave.stopTiming(); // Paper
        }
    }

    public void flushSave() {
        ChunkProviderServer chunkproviderserver = this.getChunkProviderServer();

        if (chunkproviderserver.e()) {
            chunkproviderserver.c();
        }
    }

    protected void a() throws ExceptionWorldConflict {
        timings.worldSaveLevel.startTiming(); // Paper
        this.checkSession();
        WorldServer[] aworldserver = this.server.worldServer;
        int i = aworldserver.length;

        for (int j = 0; j < i; ++j) {
            WorldServer worldserver = aworldserver[j];

            if (worldserver instanceof SecondaryWorldServer) {
                ((SecondaryWorldServer) worldserver).t_();
            }
        }

        // CraftBukkit start - Save secondary data for nether/end
        if (this instanceof SecondaryWorldServer) {
            ((SecondaryWorldServer) this).c(); // As above
        }
        // CraftBukkit end

        this.worldData.a(this.getWorldBorder().getSize());
        this.worldData.d(this.getWorldBorder().getCenterX());
        this.worldData.c(this.getWorldBorder().getCenterZ());
        this.worldData.e(this.getWorldBorder().getDamageBuffer());
        this.worldData.f(this.getWorldBorder().getDamageAmount());
        this.worldData.h(this.getWorldBorder().getWarningDistance());
        this.worldData.i(this.getWorldBorder().getWarningTime());
        this.worldData.b(this.getWorldBorder().j());
        this.worldData.c(this.getWorldBorder().i());
        this.worldData.c(this.server.aR().c());
        this.dataManager.saveWorldData(this.worldData, this.server.getPlayerList().t());
        this.worldMaps.a();
        timings.worldSaveLevel.stopTiming(); // Paper
    }

    // CraftBukkit start
    public boolean addEntity(Entity entity, SpawnReason spawnReason) { // Changed signature, added SpawnReason
        // World.addEntity(Entity) will call this, and we still want to perform
        // existing entity checking when it's called with a SpawnReason
        return this.j(entity) ? super.addEntity(entity, spawnReason) : false;
    }
    // CraftBukkit end

    public void a(Collection<Entity> collection) {
        ArrayList arraylist = Lists.newArrayList(collection);
        Iterator iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (this.j(entity)) {
                this.entityList.add(entity);
                this.b(entity);
            }
        }

    }

    private boolean j(Entity entity) {
        if (entity.dead) {
            WorldServer.a.warn("Tried to add entity {} but it was marked as removed already: " + entity); // CraftBukkit // Paper
            if (DEBUG_ENTITIES) getAddToWorldStackTrace(entity).printStackTrace();
            return false;
        } else {
            UUID uuid = entity.getUniqueID();

            if (this.entitiesByUUID.containsKey(uuid)) {
                Entity entity1 = (Entity) this.entitiesByUUID.get(uuid);

                if (this.g.contains(entity1) || entity1.dead) { // Paper - if dupe is dead, overwrite
                    this.g.remove(entity1);
                } else {
                    if (!(entity instanceof EntityHuman)) {
                        if (entity.world.paperConfig.duplicateUUIDMode != com.destroystokyo.paper.PaperWorldConfig.DuplicateUUIDMode.NOTHING) {
                            WorldServer.a.error("Keeping entity {} that already exists with UUID {}", entity1, uuid.toString()); // CraftBukkit // Paper
                            WorldServer.a.error("Duplicate entity {} will not be added to the world. See paper.yml duplicate-uuid-resolver and set this to either regen, delete or nothing to get rid of this message", entity); // Paper
                            if (DEBUG_ENTITIES) {
                                if (entity1.addedToWorldStack != null) {
                                    entity1.addedToWorldStack.printStackTrace();
                                }
                                getAddToWorldStackTrace(entity).printStackTrace();
                            }
                        }

                        return false;
                    }

                    WorldServer.a.warn("Force-added player with duplicate UUID {}", uuid.toString());
                }

                this.removeEntity(entity1);
            }

            return true;
        }
    }

    protected void b(Entity entity) {
        super.b(entity);
        this.entitiesById.a(entity.getId(), entity);
        // Paper start
        if (DEBUG_ENTITIES) {
            entity.addedToWorldStack = getAddToWorldStackTrace(entity);
        }

        Entity old = this.entitiesByUUID.put(entity.getUniqueID(), entity);
        if (old != null && old.getId() != entity.getId() && old.valid && entity.world.paperConfig.duplicateUUIDMode != com.destroystokyo.paper.PaperWorldConfig.DuplicateUUIDMode.NOTHING) {
            Logger logger = LogManager.getLogger();
            logger.error("Overwrote an existing entity " + old + " with " + entity);
            if (DEBUG_ENTITIES) {
                if (old.addedToWorldStack != null) {
                    old.addedToWorldStack.printStackTrace();
                } else {
                    logger.error("Oddly, the old entity was not added to the world in the normal way. Plugins?");
                }
                entity.addedToWorldStack.printStackTrace();
            }
        }
        // Paper end
        Entity[] aentity = entity.bi();

        if (aentity != null) {
            Entity[] aentity1 = aentity;
            int i = aentity.length;

            for (int j = 0; j < i; ++j) {
                Entity entity1 = aentity1[j];

                this.entitiesById.a(entity1.getId(), entity1);
            }
        }

    }

    protected void c(Entity entity) {
        if (!this.entitiesByUUID.containsKey(entity.getUniqueID()) && !entity.valid) return; // Paper - Already removed, dont fire twice - this looks like it can happen even without our changes
        super.c(entity);
        this.entitiesById.d(entity.getId());
        this.entitiesByUUID.remove(entity.getUniqueID());
        Entity[] aentity = entity.bi();

        if (aentity != null) {
            Entity[] aentity1 = aentity;
            int i = aentity.length;

            for (int j = 0; j < i; ++j) {
                Entity entity1 = aentity1[j];

                this.entitiesById.d(entity1.getId());
            }
        }

    }

    public boolean strikeLightning(Entity entity) {
        // CraftBukkit start
        LightningStrikeEvent lightning = new LightningStrikeEvent(this.getWorld(), (org.bukkit.entity.LightningStrike) entity.getBukkitEntity());
        this.getServer().getPluginManager().callEvent(lightning);

        if (lightning.isCancelled()) {
            return false;
        }
        // CraftBukkit end
        if (super.strikeLightning(entity)) {
            this.server.getPlayerList().sendPacketNearby((EntityHuman) null, entity.locX, entity.locY, entity.locZ, 512.0D, this, new PacketPlayOutSpawnEntityWeather(entity)); // CraftBukkit - Use dimension, // Paper - use world instead of dimension
            return true;
        } else {
            return false;
        }
    }

    public void broadcastEntityEffect(Entity entity, byte b0) {
        this.getTracker().sendPacketToEntity(entity, new PacketPlayOutEntityStatus(entity, b0));
    }

    public ChunkProviderServer getChunkProviderServer() {
        return (ChunkProviderServer) super.getChunkProvider();
    }

    public Explosion createExplosion(@Nullable Entity entity, DamageSource damagesource, double d0, double d1, double d2, float f, boolean flag, boolean flag1) {
        // CraftBukkit start
        Explosion explosion = super.createExplosion(entity, damagesource, d0, d1, d2, f, flag, flag1);

        if (explosion.wasCanceled) {
            return explosion;
        }

        /* Remove
        Explosion explosion = new Explosion(this, entity, d0, d1, d2, f, flag, flag1);

        if (damagesource != null) {
            explosion.a(damagesource);
        }

        explosion.a();
        explosion.a(false);
        */
        // CraftBukkit end - TODO: Check if explosions are still properly implemented
        if (!flag1) {
            explosion.clearBlocks();
        }

        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            EntityHuman entityhuman = (EntityHuman) iterator.next();

            if (entityhuman.d(d0, d1, d2) < 4096.0D) {
                ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutExplosion(d0, d1, d2, f, explosion.getBlocks(), (Vec3D) explosion.c().get(entityhuman)));
            }
        }

        return explosion;
    }

    public void playBlockAction(BlockPosition blockposition, Block block, int i, int j) {
        this.d.add(new BlockActionData(blockposition, block, i, j));
    }

    private void am() {
        while (!this.d.isEmpty()) {
            BlockActionData blockactiondata = (BlockActionData) this.d.removeFirst();

            if (this.a(blockactiondata)) {
                // CraftBukkit - this.worldProvider.dimension -> this.dimension, // Paper - dimension -> world
                this.server.getPlayerList().sendPacketNearby((EntityHuman) null, (double) blockactiondata.a().getX(), (double) blockactiondata.a().getY(), (double) blockactiondata.a().getZ(), 64.0D, this, new PacketPlayOutBlockAction(blockactiondata.a(), blockactiondata.b(), blockactiondata.c(), blockactiondata.d()));
            }
        }

    }

    private boolean a(BlockActionData blockactiondata) {
        IBlockData iblockdata = this.getType(blockactiondata.a());

        return iblockdata.getBlock() == blockactiondata.b() ? iblockdata.a(this, blockactiondata.a(), blockactiondata.c(), blockactiondata.d()) : false;
    }

    public void close() {
        this.dataManager.a();
        super.close();
    }

    protected void v() {
        boolean flag = this.isRaining();

        super.v();
        /* CraftBukkit start
        if (this.o != this.p) {
            this.server.getPlayerList().a((Packet) (new PacketPlayOutGameStateChange(7, this.p)), this.worldProvider.getDimensionManager().getDimensionID());
        }

        if (this.q != this.r) {
            this.server.getPlayerList().a((Packet) (new PacketPlayOutGameStateChange(8, this.r)), this.worldProvider.getDimensionManager().getDimensionID());
        }

        if (flag != this.isRaining()) {
            if (flag) {
                this.server.getPlayerList().sendAll(new PacketPlayOutGameStateChange(2, 0.0F));
            } else {
                this.server.getPlayerList().sendAll(new PacketPlayOutGameStateChange(1, 0.0F));
            }

            this.server.getPlayerList().sendAll(new PacketPlayOutGameStateChange(7, this.p));
            this.server.getPlayerList().sendAll(new PacketPlayOutGameStateChange(8, this.r));
        }
        // */
        if (flag != this.isRaining()) {
            // Only send weather packets to those affected
            for (int i = 0; i < this.players.size(); ++i) {
                if (((EntityPlayer) this.players.get(i)).world == this) {
                    ((EntityPlayer) this.players.get(i)).setPlayerWeather((!flag ? WeatherType.DOWNFALL : WeatherType.CLEAR), false);
                }
            }
        }
        for (int i = 0; i < this.players.size(); ++i) {
            if (((EntityPlayer) this.players.get(i)).world == this) {
                ((EntityPlayer) this.players.get(i)).updateWeather(this.o, this.p, this.q, this.r);
            }
        }
        // CraftBukkit end

    }

    public TickListServer<Block> w() {
        return this.nextTickListBlock;
    }

    public TickListServer<FluidType> x() {
        return this.nextTickListFluid;
    }

    @Nonnull
    public MinecraftServer getMinecraftServer() {
        return this.server;
    }

    public EntityTracker getTracker() {
        return this.tracker;
    }

    public PlayerChunkMap getPlayerChunkMap() {
        return this.manager;
    }

    public PortalTravelAgent getTravelAgent() {
        return this.portalTravelAgent;
    }

    public DefinedStructureManager C() {
        return this.dataManager.h();
    }

    public <T extends ParticleParam> int a(T t0, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6) {
        // CraftBukkit - visibility api support
        return sendParticles(null, t0, d0, d1, d2, i, d3, d4, d5, d6);
    }
    // Paper start - Particle API Expansion
    public <T extends ParticleParam> int sendParticles(EntityPlayer sender, T t0, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6) {
        return sendParticles(this.players, sender, t0, false, d0, d1, d2, i, d3, d5, d5, d6);
    }

    public <T extends ParticleParam> int sendParticles(List<EntityHuman> receivers, EntityPlayer sender, T t0, boolean force, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6) {
        // CraftBukkit end
        PacketPlayOutWorldParticles packetplayoutworldparticles = new PacketPlayOutWorldParticles(t0, force, (float) d0, (float) d1, (float) d2, (float) d3, (float) d4, (float) d5, (float) d6, i);
        // Paper end
        int j = 0;

        for (EntityHuman entityhuman : receivers) { // Paper - Particle API Expansion
            EntityPlayer entityplayer = (EntityPlayer) entityhuman; // Paper - Particle API Expansion
            if (sender != null && !entityplayer.getBukkitEntity().canSee(sender.getBukkitEntity())) continue; // CraftBukkit

            if (this.a(entityplayer, false, d0, d1, d2, packetplayoutworldparticles)) {
                ++j;
            }
        }

        return j;
    }

    public <T extends ParticleParam> boolean a(EntityPlayer entityplayer, T t0, boolean flag, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6) {
        PacketPlayOutWorldParticles packetplayoutworldparticles = new PacketPlayOutWorldParticles(t0, flag, (float) d0, (float) d1, (float) d2, (float) d3, (float) d4, (float) d5, (float) d6, i);

        return this.a(entityplayer, flag, d0, d1, d2, packetplayoutworldparticles);
    }

    private boolean a(EntityPlayer entityplayer, boolean flag, double d0, double d1, double d2, Packet<?> packet) {
        if (entityplayer.getWorldServer() != this) {
            return false;
        } else {
            BlockPosition blockposition = entityplayer.getChunkCoordinates();
            double d3 = blockposition.distanceSquared(d0, d1, d2);

            if (d3 > 1024.0D && (!flag || d3 > 262144.0D)) {
                return false;
            } else {
                entityplayer.playerConnection.sendPacket(packet);
                return true;
            }
        }
    }

    @Nullable
    public Entity getEntity(UUID uuid) {
        return (Entity) this.entitiesByUUID.get(uuid);
    }

    public ListenableFuture<Object> postToMainThread(Runnable runnable) {
        return this.server.postToMainThread(runnable);
    }

    public boolean isMainThread() {
        return this.server.isMainThread();
    }

    @Nullable
    public BlockPosition a(String s, BlockPosition blockposition, int i) {
        return this.getChunkProviderServer().a(this, s, blockposition, i);
    }

    public CraftingManager D() {
        return this.server.getCraftingManager();
    }

    public TagRegistry E() {
        return this.server.getTagRegistry();
    }

    public Scoreboard getScoreboard() {
        return this.l_();
    }

    public IChunkProvider getChunkProvider() {
        return this.getChunkProviderServer();
    }

    public TickList H() {
        return this.x();
    }

    public TickList I() {
        return this.w();
    }
}
