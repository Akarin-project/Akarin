package net.minecraft.server;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class World implements IEntityAccess, GeneratorAccess, IIBlockAccess, AutoCloseable {

    protected static final Logger e = LogManager.getLogger();
    private static final EnumDirection[] a = EnumDirection.values();
    private int b = 63;
    public final List<Entity> entityList = Lists.newArrayList();
    protected final List<Entity> g = Lists.newArrayList();
    public final List<TileEntity> tileEntityList = Lists.newArrayList();
    public final List<TileEntity> tileEntityListTick = Lists.newArrayList();
    private final List<TileEntity> c = Lists.newArrayList();
    private final List<TileEntity> tileEntityListUnload = Lists.newArrayList();
    public final List<EntityHuman> players = Lists.newArrayList();
    public final List<Entity> k = Lists.newArrayList();
    protected final IntHashMap<Entity> entitiesById = new IntHashMap<>();
    private final long F = 16777215L;
    private int G;
    protected int m = (new Random()).nextInt();
    protected final int n = 1013904223;
    protected float o;
    protected float p;
    protected float q;
    protected float r;
    private int H;
    public final Random random = new Random();
    public WorldProvider worldProvider;
    protected NavigationListener u = new NavigationListener();
    protected List<IWorldAccess> v;
    protected IChunkProvider chunkProvider;
    protected final IDataManager dataManager;
    public WorldData worldData;
    @Nullable
    public final PersistentCollection worldMaps;
    protected PersistentVillage villages;
    public final MethodProfiler methodProfiler;
    public final boolean isClientSide;
    public boolean allowMonsters;
    public boolean allowAnimals;
    private boolean J;
    private final WorldBorder K;
    int[] E;

    protected World(IDataManager idatamanager, @Nullable PersistentCollection persistentcollection, WorldData worlddata, WorldProvider worldprovider, MethodProfiler methodprofiler, boolean flag) {
        this.v = Lists.newArrayList(new IWorldAccess[] { this.u});
        this.allowMonsters = true;
        this.allowAnimals = true;
        this.E = new int['\u8000'];
        this.dataManager = idatamanager;
        this.worldMaps = persistentcollection;
        this.methodProfiler = methodprofiler;
        this.worldData = worlddata;
        this.worldProvider = worldprovider;
        this.isClientSide = flag;
        this.K = worldprovider.getWorldBorder();
    }

    public BiomeBase getBiome(BlockPosition blockposition) {
        if (this.isLoaded(blockposition)) {
            Chunk chunk = this.getChunkAtWorldCoords(blockposition);

            try {
                return chunk.getBiome(blockposition);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Getting biome");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Coordinates of biome request");

                crashreportsystemdetails.a("Location", () -> {
                    return CrashReportSystemDetails.a(blockposition);
                });
                throw new ReportedException(crashreport);
            }
        } else {
            return this.chunkProvider.getChunkGenerator().getWorldChunkManager().getBiome(blockposition, Biomes.PLAINS);
        }
    }

    protected abstract IChunkProvider r();

    public void a(WorldSettings worldsettings) {
        this.worldData.d(true);
    }

    public boolean e() {
        return this.isClientSide;
    }

    @Nullable
    public MinecraftServer getMinecraftServer() {
        return null;
    }

    public IBlockData i(BlockPosition blockposition) {
        BlockPosition blockposition1;

        for (blockposition1 = new BlockPosition(blockposition.getX(), this.getSeaLevel(), blockposition.getZ()); !this.isEmpty(blockposition1.up()); blockposition1 = blockposition1.up()) {
            ;
        }

        return this.getType(blockposition1);
    }

    public static boolean isValidLocation(BlockPosition blockposition) {
        return !k(blockposition) && blockposition.getX() >= -30000000 && blockposition.getZ() >= -30000000 && blockposition.getX() < 30000000 && blockposition.getZ() < 30000000;
    }

    public static boolean k(BlockPosition blockposition) {
        return blockposition.getY() < 0 || blockposition.getY() >= 256;
    }

    public boolean isEmpty(BlockPosition blockposition) {
        return this.getType(blockposition).isAir();
    }

    public Chunk getChunkAtWorldCoords(BlockPosition blockposition) {
        return this.getChunkAt(blockposition.getX() >> 4, blockposition.getZ() >> 4);
    }

    public Chunk getChunkAt(int i, int j) {
        Chunk chunk = this.chunkProvider.getChunkAt(i, j, true, true);

        if (chunk == null) {
            throw new IllegalStateException("Should always be able to create a chunk!");
        } else {
            return chunk;
        }
    }

    public boolean setTypeAndData(BlockPosition blockposition, IBlockData iblockdata, int i) {
        if (k(blockposition)) {
            return false;
        } else if (!this.isClientSide && this.worldData.getType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
            return false;
        } else {
            Chunk chunk = this.getChunkAtWorldCoords(blockposition);
            Block block = iblockdata.getBlock();
            IBlockData iblockdata1 = chunk.setType(blockposition, iblockdata, (i & 64) != 0);

            if (iblockdata1 == null) {
                return false;
            } else {
                IBlockData iblockdata2 = this.getType(blockposition);

                if (iblockdata2.b(this, blockposition) != iblockdata1.b(this, blockposition) || iblockdata2.e() != iblockdata1.e()) {
                    this.methodProfiler.enter("checkLight");
                    this.r(blockposition);
                    this.methodProfiler.exit();
                }

                if (iblockdata2 == iblockdata) {
                    if (iblockdata1 != iblockdata2) {
                        this.a(blockposition, blockposition);
                    }

                    if ((i & 2) != 0 && (!this.isClientSide || (i & 4) == 0) && chunk.isReady()) {
                        this.notify(blockposition, iblockdata1, iblockdata, i);
                    }

                    if (!this.isClientSide && (i & 1) != 0) {
                        this.update(blockposition, iblockdata1.getBlock());
                        if (iblockdata.isComplexRedstone()) {
                            this.updateAdjacentComparators(blockposition, block);
                        }
                    }

                    if ((i & 16) == 0) {
                        int j = i & -2;

                        iblockdata1.b(this, blockposition, j);
                        iblockdata.a((GeneratorAccess) this, blockposition, j);
                        iblockdata.b(this, blockposition, j);
                    }
                }

                return true;
            }
        }
    }

    public boolean setAir(BlockPosition blockposition) {
        Fluid fluid = this.getFluid(blockposition);

        return this.setTypeAndData(blockposition, fluid.i(), 3);
    }

    public boolean setAir(BlockPosition blockposition, boolean flag) {
        IBlockData iblockdata = this.getType(blockposition);

        if (iblockdata.isAir()) {
            return false;
        } else {
            Fluid fluid = this.getFluid(blockposition);

            this.triggerEffect(2001, blockposition, Block.getCombinedId(iblockdata));
            if (flag) {
                iblockdata.a(this, blockposition, 0);
            }

            return this.setTypeAndData(blockposition, fluid.i(), 3);
        }
    }

    public boolean setTypeUpdate(BlockPosition blockposition, IBlockData iblockdata) {
        return this.setTypeAndData(blockposition, iblockdata, 3);
    }

    public void notify(BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, int i) {
        for (int j = 0; j < this.v.size(); ++j) {
            ((IWorldAccess) this.v.get(j)).a(this, blockposition, iblockdata, iblockdata1, i);
        }

    }

    public void update(BlockPosition blockposition, Block block) {
        if (this.worldData.getType() != WorldType.DEBUG_ALL_BLOCK_STATES) {
            this.applyPhysics(blockposition, block);
        }

    }

    public void a(int i, int j, int k, int l) {
        int i1;

        if (k > l) {
            i1 = l;
            l = k;
            k = i1;
        }

        if (this.worldProvider.g()) {
            for (i1 = k; i1 <= l; ++i1) {
                this.c(EnumSkyBlock.SKY, new BlockPosition(i, i1, j));
            }
        }

        this.a(i, k, j, i, l, j);
    }

    public void a(BlockPosition blockposition, BlockPosition blockposition1) {
        this.a(blockposition.getX(), blockposition.getY(), blockposition.getZ(), blockposition1.getX(), blockposition1.getY(), blockposition1.getZ());
    }

    public void a(int i, int j, int k, int l, int i1, int j1) {
        for (int k1 = 0; k1 < this.v.size(); ++k1) {
            ((IWorldAccess) this.v.get(k1)).a(i, j, k, l, i1, j1);
        }

    }

    public void applyPhysics(BlockPosition blockposition, Block block) {
        this.a(blockposition.west(), block, blockposition);
        this.a(blockposition.east(), block, blockposition);
        this.a(blockposition.down(), block, blockposition);
        this.a(blockposition.up(), block, blockposition);
        this.a(blockposition.north(), block, blockposition);
        this.a(blockposition.south(), block, blockposition);
    }

    public void a(BlockPosition blockposition, Block block, EnumDirection enumdirection) {
        if (enumdirection != EnumDirection.WEST) {
            this.a(blockposition.west(), block, blockposition);
        }

        if (enumdirection != EnumDirection.EAST) {
            this.a(blockposition.east(), block, blockposition);
        }

        if (enumdirection != EnumDirection.DOWN) {
            this.a(blockposition.down(), block, blockposition);
        }

        if (enumdirection != EnumDirection.UP) {
            this.a(blockposition.up(), block, blockposition);
        }

        if (enumdirection != EnumDirection.NORTH) {
            this.a(blockposition.north(), block, blockposition);
        }

        if (enumdirection != EnumDirection.SOUTH) {
            this.a(blockposition.south(), block, blockposition);
        }

    }

    public void a(BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!this.isClientSide) {
            IBlockData iblockdata = this.getType(blockposition);

            try {
                iblockdata.doPhysics(this, blockposition, block, blockposition1);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Exception while updating neighbours");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being updated");

                crashreportsystemdetails.a("Source block type", () -> {
                    try {
                        return String.format("ID #%s (%s // %s)", IRegistry.BLOCK.getKey(block), block.m(), block.getClass().getCanonicalName());
                    } catch (Throwable throwable1) {
                        return "ID #" + IRegistry.BLOCK.getKey(block);
                    }
                });
                CrashReportSystemDetails.a(crashreportsystemdetails, blockposition, iblockdata);
                throw new ReportedException(crashreport);
            }
        }
    }

    public boolean e(BlockPosition blockposition) {
        return this.getChunkAtWorldCoords(blockposition).c(blockposition);
    }

    public int getLightLevel(BlockPosition blockposition, int i) {
        if (blockposition.getX() >= -30000000 && blockposition.getZ() >= -30000000 && blockposition.getX() < 30000000 && blockposition.getZ() < 30000000) {
            if (blockposition.getY() < 0) {
                return 0;
            } else {
                if (blockposition.getY() >= 256) {
                    blockposition = new BlockPosition(blockposition.getX(), 255, blockposition.getZ());
                }

                return this.getChunkAtWorldCoords(blockposition).a(blockposition, i);
            }
        } else {
            return 15;
        }
    }

    public int a(HeightMap.Type heightmap_type, int i, int j) {
        int k;

        if (i >= -30000000 && j >= -30000000 && i < 30000000 && j < 30000000) {
            if (this.isChunkLoaded(i >> 4, j >> 4, true)) {
                k = this.getChunkAt(i >> 4, j >> 4).a(heightmap_type, i & 15, j & 15) + 1;
            } else {
                k = 0;
            }
        } else {
            k = this.getSeaLevel() + 1;
        }

        return k;
    }

    @Deprecated
    public int d(int i, int j) {
        if (i >= -30000000 && j >= -30000000 && i < 30000000 && j < 30000000) {
            if (!this.isChunkLoaded(i >> 4, j >> 4, true)) {
                return 0;
            } else {
                Chunk chunk = this.getChunkAt(i >> 4, j >> 4);

                return chunk.D();
            }
        } else {
            return this.getSeaLevel() + 1;
        }
    }

    public int getBrightness(EnumSkyBlock enumskyblock, BlockPosition blockposition) {
        if (blockposition.getY() < 0) {
            blockposition = new BlockPosition(blockposition.getX(), 0, blockposition.getZ());
        }

        return !isValidLocation(blockposition) ? enumskyblock.c : (!this.isLoaded(blockposition) ? enumskyblock.c : this.getChunkAtWorldCoords(blockposition).getBrightness(enumskyblock, blockposition));
    }

    public void a(EnumSkyBlock enumskyblock, BlockPosition blockposition, int i) {
        if (isValidLocation(blockposition)) {
            if (this.isLoaded(blockposition)) {
                this.getChunkAtWorldCoords(blockposition).a(enumskyblock, blockposition, i);
                this.m(blockposition);
            }
        }
    }

    public void m(BlockPosition blockposition) {
        for (int i = 0; i < this.v.size(); ++i) {
            ((IWorldAccess) this.v.get(i)).a(blockposition);
        }

    }

    public IBlockData getType(BlockPosition blockposition) {
        if (k(blockposition)) {
            return Blocks.VOID_AIR.getBlockData();
        } else {
            Chunk chunk = this.getChunkAtWorldCoords(blockposition);

            return chunk.getType(blockposition);
        }
    }

    public Fluid getFluid(BlockPosition blockposition) {
        if (k(blockposition)) {
            return FluidTypes.EMPTY.i();
        } else {
            Chunk chunk = this.getChunkAtWorldCoords(blockposition);

            return chunk.getFluid(blockposition);
        }
    }

    public boolean L() {
        return this.G < 4;
    }

    @Nullable
    public MovingObjectPosition rayTrace(Vec3D vec3d, Vec3D vec3d1) {
        return this.rayTrace(vec3d, vec3d1, FluidCollisionOption.NEVER, false, false);
    }

    @Nullable
    public MovingObjectPosition rayTrace(Vec3D vec3d, Vec3D vec3d1, FluidCollisionOption fluidcollisionoption) {
        return this.rayTrace(vec3d, vec3d1, fluidcollisionoption, false, false);
    }

    @Nullable
    public MovingObjectPosition rayTrace(Vec3D vec3d, Vec3D vec3d1, FluidCollisionOption fluidcollisionoption, boolean flag, boolean flag1) {
        double d0 = vec3d.x;
        double d1 = vec3d.y;
        double d2 = vec3d.z;

        if (!Double.isNaN(d0) && !Double.isNaN(d1) && !Double.isNaN(d2)) {
            if (!Double.isNaN(vec3d1.x) && !Double.isNaN(vec3d1.y) && !Double.isNaN(vec3d1.z)) {
                int i = MathHelper.floor(vec3d1.x);
                int j = MathHelper.floor(vec3d1.y);
                int k = MathHelper.floor(vec3d1.z);
                int l = MathHelper.floor(d0);
                int i1 = MathHelper.floor(d1);
                int j1 = MathHelper.floor(d2);
                BlockPosition blockposition = new BlockPosition(l, i1, j1);
                IBlockData iblockdata = this.getType(blockposition);
                Fluid fluid = this.getFluid(blockposition);
                boolean flag2;
                boolean flag3;

                if (!flag || !iblockdata.getCollisionShape(this, blockposition).isEmpty()) {
                    flag2 = iblockdata.getBlock().isCollidable(iblockdata);
                    flag3 = fluidcollisionoption.predicate.test(fluid);
                    if (flag2 || flag3) {
                        MovingObjectPosition movingobjectposition = null;

                        if (flag2) {
                            movingobjectposition = Block.rayTrace(iblockdata, this, blockposition, vec3d, vec3d1);
                        }

                        if (movingobjectposition == null && flag3) {
                            movingobjectposition = VoxelShapes.create(0.0D, 0.0D, 0.0D, 1.0D, (double) fluid.getHeight(), 1.0D).rayTrace(vec3d, vec3d1, blockposition);
                        }

                        if (movingobjectposition != null) {
                            return movingobjectposition;
                        }
                    }
                }

                MovingObjectPosition movingobjectposition1 = null;
                int k1 = 200;

                while (k1-- >= 0) {
                    if (Double.isNaN(d0) || Double.isNaN(d1) || Double.isNaN(d2)) {
                        return null;
                    }

                    if (l == i && i1 == j && j1 == k) {
                        return flag1 ? movingobjectposition1 : null;
                    }

                    flag2 = true;
                    flag3 = true;
                    boolean flag4 = true;
                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;

                    if (i > l) {
                        d3 = (double) l + 1.0D;
                    } else if (i < l) {
                        d3 = (double) l + 0.0D;
                    } else {
                        flag2 = false;
                    }

                    if (j > i1) {
                        d4 = (double) i1 + 1.0D;
                    } else if (j < i1) {
                        d4 = (double) i1 + 0.0D;
                    } else {
                        flag3 = false;
                    }

                    if (k > j1) {
                        d5 = (double) j1 + 1.0D;
                    } else if (k < j1) {
                        d5 = (double) j1 + 0.0D;
                    } else {
                        flag4 = false;
                    }

                    double d6 = 999.0D;
                    double d7 = 999.0D;
                    double d8 = 999.0D;
                    double d9 = vec3d1.x - d0;
                    double d10 = vec3d1.y - d1;
                    double d11 = vec3d1.z - d2;

                    if (flag2) {
                        d6 = (d3 - d0) / d9;
                    }

                    if (flag3) {
                        d7 = (d4 - d1) / d10;
                    }

                    if (flag4) {
                        d8 = (d5 - d2) / d11;
                    }

                    if (d6 == -0.0D) {
                        d6 = -1.0E-4D;
                    }

                    if (d7 == -0.0D) {
                        d7 = -1.0E-4D;
                    }

                    if (d8 == -0.0D) {
                        d8 = -1.0E-4D;
                    }

                    EnumDirection enumdirection;

                    if (d6 < d7 && d6 < d8) {
                        enumdirection = i > l ? EnumDirection.WEST : EnumDirection.EAST;
                        d0 = d3;
                        d1 += d10 * d6;
                        d2 += d11 * d6;
                    } else if (d7 < d8) {
                        enumdirection = j > i1 ? EnumDirection.DOWN : EnumDirection.UP;
                        d0 += d9 * d7;
                        d1 = d4;
                        d2 += d11 * d7;
                    } else {
                        enumdirection = k > j1 ? EnumDirection.NORTH : EnumDirection.SOUTH;
                        d0 += d9 * d8;
                        d1 += d10 * d8;
                        d2 = d5;
                    }

                    l = MathHelper.floor(d0) - (enumdirection == EnumDirection.EAST ? 1 : 0);
                    i1 = MathHelper.floor(d1) - (enumdirection == EnumDirection.UP ? 1 : 0);
                    j1 = MathHelper.floor(d2) - (enumdirection == EnumDirection.SOUTH ? 1 : 0);
                    blockposition = new BlockPosition(l, i1, j1);
                    IBlockData iblockdata1 = this.getType(blockposition);
                    Fluid fluid1 = this.getFluid(blockposition);

                    if (!flag || iblockdata1.getMaterial() == Material.PORTAL || !iblockdata1.getCollisionShape(this, blockposition).isEmpty()) {
                        boolean flag5 = iblockdata1.getBlock().isCollidable(iblockdata1);
                        boolean flag6 = fluidcollisionoption.predicate.test(fluid1);

                        if (!flag5 && !flag6) {
                            movingobjectposition1 = new MovingObjectPosition(MovingObjectPosition.EnumMovingObjectType.MISS, new Vec3D(d0, d1, d2), enumdirection, blockposition);
                        } else {
                            MovingObjectPosition movingobjectposition2 = null;

                            if (flag5) {
                                movingobjectposition2 = Block.rayTrace(iblockdata1, this, blockposition, vec3d, vec3d1);
                            }

                            if (movingobjectposition2 == null && flag6) {
                                movingobjectposition2 = VoxelShapes.create(0.0D, 0.0D, 0.0D, 1.0D, (double) fluid1.getHeight(), 1.0D).rayTrace(vec3d, vec3d1, blockposition);
                            }

                            if (movingobjectposition2 != null) {
                                return movingobjectposition2;
                            }
                        }
                    }
                }

                return flag1 ? movingobjectposition1 : null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void a(@Nullable EntityHuman entityhuman, BlockPosition blockposition, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1) {
        this.a(entityhuman, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, soundeffect, soundcategory, f, f1);
    }

    public void a(@Nullable EntityHuman entityhuman, double d0, double d1, double d2, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1) {
        for (int i = 0; i < this.v.size(); ++i) {
            ((IWorldAccess) this.v.get(i)).a(entityhuman, soundeffect, soundcategory, d0, d1, d2, f, f1);
        }

    }

    public void a(double d0, double d1, double d2, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1, boolean flag) {}

    public void a(BlockPosition blockposition, @Nullable SoundEffect soundeffect) {
        for (int i = 0; i < this.v.size(); ++i) {
            ((IWorldAccess) this.v.get(i)).a(soundeffect, blockposition);
        }

    }

    public void addParticle(ParticleParam particleparam, double d0, double d1, double d2, double d3, double d4, double d5) {
        for (int i = 0; i < this.v.size(); ++i) {
            ((IWorldAccess) this.v.get(i)).a(particleparam, particleparam.b().e(), d0, d1, d2, d3, d4, d5);
        }

    }

    public void b(ParticleParam particleparam, double d0, double d1, double d2, double d3, double d4, double d5) {
        for (int i = 0; i < this.v.size(); ++i) {
            ((IWorldAccess) this.v.get(i)).a(particleparam, false, true, d0, d1, d2, d3, d4, d5);
        }

    }

    public boolean strikeLightning(Entity entity) {
        this.k.add(entity);
        return true;
    }

    public boolean addEntity(Entity entity) {
        int i = MathHelper.floor(entity.locX / 16.0D);
        int j = MathHelper.floor(entity.locZ / 16.0D);
        boolean flag = entity.attachedToPlayer;

        if (entity instanceof EntityHuman) {
            flag = true;
        }

        if (!flag && !this.isChunkLoaded(i, j, false)) {
            return false;
        } else {
            if (entity instanceof EntityHuman) {
                EntityHuman entityhuman = (EntityHuman) entity;

                this.players.add(entityhuman);
                this.everyoneSleeping();
            }

            this.getChunkAt(i, j).a(entity);
            this.entityList.add(entity);
            this.b(entity);
            return true;
        }
    }

    protected void b(Entity entity) {
        for (int i = 0; i < this.v.size(); ++i) {
            ((IWorldAccess) this.v.get(i)).a(entity);
        }

    }

    protected void c(Entity entity) {
        for (int i = 0; i < this.v.size(); ++i) {
            ((IWorldAccess) this.v.get(i)).b(entity);
        }

    }

    public void kill(Entity entity) {
        if (entity.isVehicle()) {
            entity.ejectPassengers();
        }

        if (entity.isPassenger()) {
            entity.stopRiding();
        }

        entity.die();
        if (entity instanceof EntityHuman) {
            this.players.remove(entity);
            this.everyoneSleeping();
            this.c(entity);
        }

    }

    public void removeEntity(Entity entity) {
        entity.b(false);
        entity.die();
        if (entity instanceof EntityHuman) {
            this.players.remove(entity);
            this.everyoneSleeping();
        }

        int i = entity.chunkX;
        int j = entity.chunkZ;

        if (entity.inChunk && this.isChunkLoaded(i, j, true)) {
            this.getChunkAt(i, j).b(entity);
        }

        this.entityList.remove(entity);
        this.c(entity);
    }

    public void addIWorldAccess(IWorldAccess iworldaccess) {
        this.v.add(iworldaccess);
    }

    public int a(float f) {
        float f1 = this.k(f);
        float f2 = 1.0F - (MathHelper.cos(f1 * 6.2831855F) * 2.0F + 0.5F);

        f2 = MathHelper.a(f2, 0.0F, 1.0F);
        f2 = 1.0F - f2;
        f2 = (float) ((double) f2 * (1.0D - (double) (this.i(f) * 5.0F) / 16.0D));
        f2 = (float) ((double) f2 * (1.0D - (double) (this.g(f) * 5.0F) / 16.0D));
        f2 = 1.0F - f2;
        return (int) (f2 * 11.0F);
    }

    public float c(float f) {
        float f1 = this.k(f);

        return f1 * 6.2831855F;
    }

    public void tickEntities() {
        this.methodProfiler.enter("entities");
        this.methodProfiler.enter("global");

        Entity entity;
        int i;

        for (i = 0; i < this.k.size(); ++i) {
            entity = (Entity) this.k.get(i);

            try {
                ++entity.ticksLived;
                entity.tick();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Ticking entity");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being ticked");

                if (entity == null) {
                    crashreportsystemdetails.a("Entity", (Object) "~~NULL~~");
                } else {
                    entity.appendEntityCrashDetails(crashreportsystemdetails);
                }

                throw new ReportedException(crashreport);
            }

            if (entity.dead) {
                this.k.remove(i--);
            }
        }

        this.methodProfiler.exitEnter("remove");
        this.entityList.removeAll(this.g);

        int j;

        for (i = 0; i < this.g.size(); ++i) {
            entity = (Entity) this.g.get(i);
            int k = entity.chunkX;

            j = entity.chunkZ;
            if (entity.inChunk && this.isChunkLoaded(k, j, true)) {
                this.getChunkAt(k, j).b(entity);
            }
        }

        for (i = 0; i < this.g.size(); ++i) {
            this.c((Entity) this.g.get(i));
        }

        this.g.clear();
        this.p_();
        this.methodProfiler.exitEnter("regular");

        CrashReport crashreport1;
        CrashReportSystemDetails crashreportsystemdetails1;

        for (i = 0; i < this.entityList.size(); ++i) {
            entity = (Entity) this.entityList.get(i);
            Entity entity1 = entity.getVehicle();

            if (entity1 != null) {
                if (!entity1.dead && entity1.w(entity)) {
                    continue;
                }

                entity.stopRiding();
            }

            this.methodProfiler.enter("tick");
            if (!entity.dead && !(entity instanceof EntityPlayer)) {
                try {
                    this.g(entity);
                } catch (Throwable throwable1) {
                    crashreport1 = CrashReport.a(throwable1, "Ticking entity");
                    crashreportsystemdetails1 = crashreport1.a("Entity being ticked");
                    entity.appendEntityCrashDetails(crashreportsystemdetails1);
                    throw new ReportedException(crashreport1);
                }
            }

            this.methodProfiler.exit();
            this.methodProfiler.enter("remove");
            if (entity.dead) {
                j = entity.chunkX;
                int l = entity.chunkZ;

                if (entity.inChunk && this.isChunkLoaded(j, l, true)) {
                    this.getChunkAt(j, l).b(entity);
                }

                this.entityList.remove(i--);
                this.c(entity);
            }

            this.methodProfiler.exit();
        }

        this.methodProfiler.exitEnter("blockEntities");
        if (!this.tileEntityListUnload.isEmpty()) {
            this.tileEntityListTick.removeAll(this.tileEntityListUnload);
            this.tileEntityList.removeAll(this.tileEntityListUnload);
            this.tileEntityListUnload.clear();
        }

        this.J = true;
        Iterator iterator = this.tileEntityListTick.iterator();

        while (iterator.hasNext()) {
            TileEntity tileentity = (TileEntity) iterator.next();

            if (!tileentity.x() && tileentity.hasWorld()) {
                BlockPosition blockposition = tileentity.getPosition();

                if (this.isLoaded(blockposition) && this.K.a(blockposition)) {
                    try {
                        this.methodProfiler.a(() -> {
                            return String.valueOf(TileEntityTypes.a(tileentity.C()));
                        });
                        ((ITickable) tileentity).tick();
                        this.methodProfiler.exit();
                    } catch (Throwable throwable2) {
                        crashreport1 = CrashReport.a(throwable2, "Ticking block entity");
                        crashreportsystemdetails1 = crashreport1.a("Block entity being ticked");
                        tileentity.a(crashreportsystemdetails1);
                        throw new ReportedException(crashreport1);
                    }
                }
            }

            if (tileentity.x()) {
                iterator.remove();
                this.tileEntityList.remove(tileentity);
                if (this.isLoaded(tileentity.getPosition())) {
                    this.getChunkAtWorldCoords(tileentity.getPosition()).d(tileentity.getPosition());
                }
            }
        }

        this.J = false;
        this.methodProfiler.exitEnter("pendingBlockEntities");
        if (!this.c.isEmpty()) {
            for (int i1 = 0; i1 < this.c.size(); ++i1) {
                TileEntity tileentity1 = (TileEntity) this.c.get(i1);

                if (!tileentity1.x()) {
                    if (!this.tileEntityList.contains(tileentity1)) {
                        this.a(tileentity1);
                    }

                    if (this.isLoaded(tileentity1.getPosition())) {
                        Chunk chunk = this.getChunkAtWorldCoords(tileentity1.getPosition());
                        IBlockData iblockdata = chunk.getType(tileentity1.getPosition());

                        chunk.a(tileentity1.getPosition(), tileentity1);
                        this.notify(tileentity1.getPosition(), iblockdata, iblockdata, 3);
                    }
                }
            }

            this.c.clear();
        }

        this.methodProfiler.exit();
        this.methodProfiler.exit();
    }

    protected void p_() {}

    public boolean a(TileEntity tileentity) {
        boolean flag = this.tileEntityList.add(tileentity);

        if (flag && tileentity instanceof ITickable) {
            this.tileEntityListTick.add(tileentity);
        }

        if (this.isClientSide) {
            BlockPosition blockposition = tileentity.getPosition();
            IBlockData iblockdata = this.getType(blockposition);

            this.notify(blockposition, iblockdata, iblockdata, 2);
        }

        return flag;
    }

    public void a(Collection<TileEntity> collection) {
        if (this.J) {
            this.c.addAll(collection);
        } else {
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                TileEntity tileentity = (TileEntity) iterator.next();

                this.a(tileentity);
            }
        }

    }

    public void g(Entity entity) {
        this.entityJoinedWorld(entity, true);
    }

    public void entityJoinedWorld(Entity entity, boolean flag) {
        int i;
        int j;

        if (!(entity instanceof EntityHuman)) {
            i = MathHelper.floor(entity.locX);
            j = MathHelper.floor(entity.locZ);
            boolean flag1 = true;

            if (flag && !this.isAreaLoaded(i - 32, 0, j - 32, i + 32, 0, j + 32, true)) {
                return;
            }
        }

        entity.N = entity.locX;
        entity.O = entity.locY;
        entity.P = entity.locZ;
        entity.lastYaw = entity.yaw;
        entity.lastPitch = entity.pitch;
        if (flag && entity.inChunk) {
            ++entity.ticksLived;
            if (entity.isPassenger()) {
                entity.aH();
            } else {
                this.methodProfiler.a(() -> {
                    return IRegistry.ENTITY_TYPE.getKey(entity.P()).toString();
                });
                entity.tick();
                this.methodProfiler.exit();
            }
        }

        this.methodProfiler.enter("chunkCheck");
        if (Double.isNaN(entity.locX) || Double.isInfinite(entity.locX)) {
            entity.locX = entity.N;
        }

        if (Double.isNaN(entity.locY) || Double.isInfinite(entity.locY)) {
            entity.locY = entity.O;
        }

        if (Double.isNaN(entity.locZ) || Double.isInfinite(entity.locZ)) {
            entity.locZ = entity.P;
        }

        if (Double.isNaN((double) entity.pitch) || Double.isInfinite((double) entity.pitch)) {
            entity.pitch = entity.lastPitch;
        }

        if (Double.isNaN((double) entity.yaw) || Double.isInfinite((double) entity.yaw)) {
            entity.yaw = entity.lastYaw;
        }

        i = MathHelper.floor(entity.locX / 16.0D);
        j = MathHelper.floor(entity.locY / 16.0D);
        int k = MathHelper.floor(entity.locZ / 16.0D);

        if (!entity.inChunk || entity.chunkX != i || entity.chunkY != j || entity.chunkZ != k) {
            if (entity.inChunk && this.isChunkLoaded(entity.chunkX, entity.chunkZ, true)) {
                this.getChunkAt(entity.chunkX, entity.chunkZ).a(entity, entity.chunkY);
            }

            if (!entity.bN() && !this.isChunkLoaded(i, k, true)) {
                entity.inChunk = false;
            } else {
                this.getChunkAt(i, k).a(entity);
            }
        }

        this.methodProfiler.exit();
        if (flag && entity.inChunk) {
            Iterator iterator = entity.bP().iterator();

            while (iterator.hasNext()) {
                Entity entity1 = (Entity) iterator.next();

                if (!entity1.dead && entity1.getVehicle() == entity) {
                    this.g(entity1);
                } else {
                    entity1.stopRiding();
                }
            }
        }

    }

    public boolean a(@Nullable Entity entity, VoxelShape voxelshape) {
        if (voxelshape.isEmpty()) {
            return true;
        } else {
            List<Entity> list = this.getEntities((Entity) null, voxelshape.getBoundingBox());

            for (int i = 0; i < list.size(); ++i) {
                Entity entity1 = (Entity) list.get(i);

                if (!entity1.dead && entity1.j && entity1 != entity && (entity == null || !entity1.x(entity)) && VoxelShapes.c(voxelshape, VoxelShapes.a(entity1.getBoundingBox()), OperatorBoolean.AND)) {
                    return false;
                }
            }

            return true;
        }
    }

    public boolean a(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.f(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.f(axisalignedbb.maxY);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.f(axisalignedbb.maxZ);
        BlockPosition.b blockposition_b = BlockPosition.b.r();
        Throwable throwable = null;

        try {
            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        IBlockData iblockdata = this.getType(blockposition_b.c(k1, l1, i2));

                        if (!iblockdata.isAir()) {
                            boolean flag = true;

                            return flag;
                        }
                    }
                }
            }

            return false;
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_b != null) {
                if (throwable != null) {
                    try {
                        blockposition_b.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_b.close();
                }
            }

        }
    }

    public boolean b(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.f(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.f(axisalignedbb.maxY);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.f(axisalignedbb.maxZ);

        if (this.isAreaLoaded(i, k, i1, j, l, j1, true)) {
            BlockPosition.b blockposition_b = BlockPosition.b.r();
            Throwable throwable = null;

            try {
                for (int k1 = i; k1 < j; ++k1) {
                    for (int l1 = k; l1 < l; ++l1) {
                        for (int i2 = i1; i2 < j1; ++i2) {
                            Block block = this.getType(blockposition_b.c(k1, l1, i2)).getBlock();

                            if (block == Blocks.FIRE || block == Blocks.LAVA) {
                                boolean flag = true;

                                return flag;
                            }
                        }
                    }
                }

                return false;
            } catch (Throwable throwable1) {
                throwable = throwable1;
                throw throwable1;
            } finally {
                if (blockposition_b != null) {
                    if (throwable != null) {
                        try {
                            blockposition_b.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        blockposition_b.close();
                    }
                }

            }
        } else {
            return false;
        }
    }

    @Nullable
    public IBlockData a(AxisAlignedBB axisalignedbb, Block block) {
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.f(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.f(axisalignedbb.maxY);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.f(axisalignedbb.maxZ);

        if (this.isAreaLoaded(i, k, i1, j, l, j1, true)) {
            BlockPosition.b blockposition_b = BlockPosition.b.r();
            Throwable throwable = null;

            try {
                for (int k1 = i; k1 < j; ++k1) {
                    for (int l1 = k; l1 < l; ++l1) {
                        for (int i2 = i1; i2 < j1; ++i2) {
                            IBlockData iblockdata = this.getType(blockposition_b.c(k1, l1, i2));

                            if (iblockdata.getBlock() == block) {
                                IBlockData iblockdata1 = iblockdata;

                                return iblockdata1;
                            }
                        }
                    }
                }

                return null;
            } catch (Throwable throwable1) {
                throwable = throwable1;
                throw throwable1;
            } finally {
                if (blockposition_b != null) {
                    if (throwable != null) {
                        try {
                            blockposition_b.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        blockposition_b.close();
                    }
                }

            }
        } else {
            return null;
        }
    }

    public boolean a(AxisAlignedBB axisalignedbb, Material material) {
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.f(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.f(axisalignedbb.maxY);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.f(axisalignedbb.maxZ);
        MaterialPredicate materialpredicate = MaterialPredicate.a(material);
        BlockPosition.b blockposition_b = BlockPosition.b.r();
        Throwable throwable = null;

        try {
            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        if (materialpredicate.test(this.getType(blockposition_b.c(k1, l1, i2)))) {
                            boolean flag = true;

                            return flag;
                        }
                    }
                }
            }

            return false;
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_b != null) {
                if (throwable != null) {
                    try {
                        blockposition_b.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_b.close();
                }
            }

        }
    }

    public Explosion explode(@Nullable Entity entity, double d0, double d1, double d2, float f, boolean flag) {
        return this.createExplosion(entity, (DamageSource) null, d0, d1, d2, f, false, flag);
    }

    public Explosion createExplosion(@Nullable Entity entity, double d0, double d1, double d2, float f, boolean flag, boolean flag1) {
        return this.createExplosion(entity, (DamageSource) null, d0, d1, d2, f, flag, flag1);
    }

    public Explosion createExplosion(@Nullable Entity entity, @Nullable DamageSource damagesource, double d0, double d1, double d2, float f, boolean flag, boolean flag1) {
        Explosion explosion = new Explosion(this, entity, d0, d1, d2, f, flag, flag1);

        if (damagesource != null) {
            explosion.a(damagesource);
        }

        explosion.a();
        explosion.a(true);
        return explosion;
    }

    public float a(Vec3D vec3d, AxisAlignedBB axisalignedbb) {
        double d0 = 1.0D / ((axisalignedbb.maxX - axisalignedbb.minX) * 2.0D + 1.0D);
        double d1 = 1.0D / ((axisalignedbb.maxY - axisalignedbb.minY) * 2.0D + 1.0D);
        double d2 = 1.0D / ((axisalignedbb.maxZ - axisalignedbb.minZ) * 2.0D + 1.0D);
        double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
        double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;

        if (d0 >= 0.0D && d1 >= 0.0D && d2 >= 0.0D) {
            int i = 0;
            int j = 0;

            for (float f = 0.0F; f <= 1.0F; f = (float) ((double) f + d0)) {
                for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float) ((double) f1 + d1)) {
                    for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float) ((double) f2 + d2)) {
                        double d5 = axisalignedbb.minX + (axisalignedbb.maxX - axisalignedbb.minX) * (double) f;
                        double d6 = axisalignedbb.minY + (axisalignedbb.maxY - axisalignedbb.minY) * (double) f1;
                        double d7 = axisalignedbb.minZ + (axisalignedbb.maxZ - axisalignedbb.minZ) * (double) f2;

                        if (this.rayTrace(new Vec3D(d5 + d3, d6, d7 + d4), vec3d) == null) {
                            ++i;
                        }

                        ++j;
                    }
                }
            }

            return (float) i / (float) j;
        } else {
            return 0.0F;
        }
    }

    public boolean douseFire(@Nullable EntityHuman entityhuman, BlockPosition blockposition, EnumDirection enumdirection) {
        blockposition = blockposition.shift(enumdirection);
        if (this.getType(blockposition).getBlock() == Blocks.FIRE) {
            this.a(entityhuman, 1009, blockposition, 0);
            this.setAir(blockposition);
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public TileEntity getTileEntity(BlockPosition blockposition) {
        if (k(blockposition)) {
            return null;
        } else {
            TileEntity tileentity = null;

            if (this.J) {
                tileentity = this.E(blockposition);
            }

            if (tileentity == null) {
                tileentity = this.getChunkAtWorldCoords(blockposition).a(blockposition, Chunk.EnumTileEntityState.IMMEDIATE);
            }

            if (tileentity == null) {
                tileentity = this.E(blockposition);
            }

            return tileentity;
        }
    }

    @Nullable
    private TileEntity E(BlockPosition blockposition) {
        for (int i = 0; i < this.c.size(); ++i) {
            TileEntity tileentity = (TileEntity) this.c.get(i);

            if (!tileentity.x() && tileentity.getPosition().equals(blockposition)) {
                return tileentity;
            }
        }

        return null;
    }

    public void setTileEntity(BlockPosition blockposition, @Nullable TileEntity tileentity) {
        if (!k(blockposition)) {
            if (tileentity != null && !tileentity.x()) {
                if (this.J) {
                    tileentity.setPosition(blockposition);
                    Iterator iterator = this.c.iterator();

                    while (iterator.hasNext()) {
                        TileEntity tileentity1 = (TileEntity) iterator.next();

                        if (tileentity1.getPosition().equals(blockposition)) {
                            tileentity1.y();
                            iterator.remove();
                        }
                    }

                    this.c.add(tileentity);
                } else {
                    this.getChunkAtWorldCoords(blockposition).a(blockposition, tileentity);
                    this.a(tileentity);
                }
            }

        }
    }

    public void n(BlockPosition blockposition) {
        TileEntity tileentity = this.getTileEntity(blockposition);

        if (tileentity != null && this.J) {
            tileentity.y();
            this.c.remove(tileentity);
        } else {
            if (tileentity != null) {
                this.c.remove(tileentity);
                this.tileEntityList.remove(tileentity);
                this.tileEntityListTick.remove(tileentity);
            }

            this.getChunkAtWorldCoords(blockposition).d(blockposition);
        }

    }

    public void b(TileEntity tileentity) {
        this.tileEntityListUnload.add(tileentity);
    }

    public boolean o(BlockPosition blockposition) {
        return Block.a(this.getType(blockposition).getCollisionShape(this, blockposition));
    }

    public boolean p(BlockPosition blockposition) {
        if (k(blockposition)) {
            return false;
        } else {
            Chunk chunk = this.chunkProvider.getChunkAt(blockposition.getX() >> 4, blockposition.getZ() >> 4, false, false);

            return chunk != null && !chunk.isEmpty();
        }
    }

    public boolean q(BlockPosition blockposition) {
        return this.p(blockposition) && this.getType(blockposition).q();
    }

    public void P() {
        int i = this.a(1.0F);

        if (i != this.G) {
            this.G = i;
        }

    }

    public void setSpawnFlags(boolean flag, boolean flag1) {
        this.allowMonsters = flag;
        this.allowAnimals = flag1;
    }

    public void doTick(BooleanSupplier booleansupplier) {
        this.K.r();
        this.w();
    }

    protected void Q() {
        if (this.worldData.hasStorm()) {
            this.p = 1.0F;
            if (this.worldData.isThundering()) {
                this.r = 1.0F;
            }
        }

    }

    public void close() {
        this.chunkProvider.close();
    }

    protected void w() {
        if (this.worldProvider.g()) {
            if (!this.isClientSide) {
                boolean flag = this.getGameRules().getBoolean("doWeatherCycle");

                if (flag) {
                    int i = this.worldData.z();

                    if (i > 0) {
                        --i;
                        this.worldData.g(i);
                        this.worldData.setThunderDuration(this.worldData.isThundering() ? 1 : 2);
                        this.worldData.setWeatherDuration(this.worldData.hasStorm() ? 1 : 2);
                    }

                    int j = this.worldData.getThunderDuration();

                    if (j <= 0) {
                        if (this.worldData.isThundering()) {
                            this.worldData.setThunderDuration(this.random.nextInt(12000) + 3600);
                        } else {
                            this.worldData.setThunderDuration(this.random.nextInt(168000) + 12000);
                        }
                    } else {
                        --j;
                        this.worldData.setThunderDuration(j);
                        if (j <= 0) {
                            this.worldData.setThundering(!this.worldData.isThundering());
                        }
                    }

                    int k = this.worldData.getWeatherDuration();

                    if (k <= 0) {
                        if (this.worldData.hasStorm()) {
                            this.worldData.setWeatherDuration(this.random.nextInt(12000) + 12000);
                        } else {
                            this.worldData.setWeatherDuration(this.random.nextInt(168000) + 12000);
                        }
                    } else {
                        --k;
                        this.worldData.setWeatherDuration(k);
                        if (k <= 0) {
                            this.worldData.setStorm(!this.worldData.hasStorm());
                        }
                    }
                }

                this.q = this.r;
                if (this.worldData.isThundering()) {
                    this.r = (float) ((double) this.r + 0.01D);
                } else {
                    this.r = (float) ((double) this.r - 0.01D);
                }

                this.r = MathHelper.a(this.r, 0.0F, 1.0F);
                this.o = this.p;
                if (this.worldData.hasStorm()) {
                    this.p = (float) ((double) this.p + 0.01D);
                } else {
                    this.p = (float) ((double) this.p - 0.01D);
                }

                this.p = MathHelper.a(this.p, 0.0F, 1.0F);
            }
        }
    }

    protected void n_() {}

    public boolean r(BlockPosition blockposition) {
        boolean flag = false;

        if (this.worldProvider.g()) {
            flag |= this.c(EnumSkyBlock.SKY, blockposition);
        }

        flag |= this.c(EnumSkyBlock.BLOCK, blockposition);
        return flag;
    }

    private int a(BlockPosition blockposition, EnumSkyBlock enumskyblock) {
        if (enumskyblock == EnumSkyBlock.SKY && this.e(blockposition)) {
            return 15;
        } else {
            IBlockData iblockdata = this.getType(blockposition);
            int i = enumskyblock == EnumSkyBlock.SKY ? 0 : iblockdata.e();
            int j = iblockdata.b(this, blockposition);

            if (j >= 15 && iblockdata.e() > 0) {
                j = 1;
            }

            if (j < 1) {
                j = 1;
            }

            if (j >= 15) {
                return 0;
            } else if (i >= 14) {
                return i;
            } else {
                BlockPosition.b blockposition_b = BlockPosition.b.r();
                Throwable throwable = null;

                try {
                    EnumDirection[] aenumdirection = World.a;
                    int k = aenumdirection.length;

                    for (int l = 0; l < k; ++l) {
                        EnumDirection enumdirection = aenumdirection[l];

                        blockposition_b.g(blockposition).c(enumdirection);
                        int i1 = this.getBrightness(enumskyblock, blockposition_b) - j;

                        if (i1 > i) {
                            i = i1;
                        }

                        if (i >= 14) {
                            int j1 = i;

                            return j1;
                        }
                    }

                    return i;
                } catch (Throwable throwable1) {
                    throwable = throwable1;
                    throw throwable1;
                } finally {
                    if (blockposition_b != null) {
                        if (throwable != null) {
                            try {
                                blockposition_b.close();
                            } catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        } else {
                            blockposition_b.close();
                        }
                    }

                }
            }
        }
    }

    public boolean c(EnumSkyBlock enumskyblock, BlockPosition blockposition) {
        if (!this.areChunksLoaded(blockposition, 17, false)) {
            return false;
        } else {
            int i = 0;
            int j = 0;

            this.methodProfiler.enter("getBrightness");
            int k = this.getBrightness(enumskyblock, blockposition);
            int l = this.a(blockposition, enumskyblock);
            int i1 = blockposition.getX();
            int j1 = blockposition.getY();
            int k1 = blockposition.getZ();
            int l1;
            int i2;
            int j2;
            int k2;
            int l2;
            int i3;
            int j3;
            int k3;

            if (l > k) {
                this.E[j++] = 133152;
            } else if (l < k) {
                this.E[j++] = 133152 | k << 18;

                while (i < j) {
                    l1 = this.E[i++];
                    i2 = (l1 & 63) - 32 + i1;
                    j2 = (l1 >> 6 & 63) - 32 + j1;
                    k2 = (l1 >> 12 & 63) - 32 + k1;
                    int l3 = l1 >> 18 & 15;
                    BlockPosition blockposition1 = new BlockPosition(i2, j2, k2);

                    l2 = this.getBrightness(enumskyblock, blockposition1);
                    if (l2 == l3) {
                        this.a(enumskyblock, blockposition1, 0);
                        if (l3 > 0) {
                            i3 = MathHelper.a(i2 - i1);
                            j3 = MathHelper.a(j2 - j1);
                            k3 = MathHelper.a(k2 - k1);
                            if (i3 + j3 + k3 < 17) {
                                BlockPosition.b blockposition_b = BlockPosition.b.r();
                                Throwable throwable = null;

                                try {
                                    EnumDirection[] aenumdirection = World.a;
                                    int i4 = aenumdirection.length;

                                    for (int j4 = 0; j4 < i4; ++j4) {
                                        EnumDirection enumdirection = aenumdirection[j4];
                                        int k4 = i2 + enumdirection.getAdjacentX();
                                        int l4 = j2 + enumdirection.getAdjacentY();
                                        int i5 = k2 + enumdirection.getAdjacentZ();

                                        blockposition_b.c(k4, l4, i5);
                                        int j5 = Math.max(1, this.getType(blockposition_b).b(this, blockposition_b));

                                        l2 = this.getBrightness(enumskyblock, blockposition_b);
                                        if (l2 == l3 - j5 && j < this.E.length) {
                                            this.E[j++] = k4 - i1 + 32 | l4 - j1 + 32 << 6 | i5 - k1 + 32 << 12 | l3 - j5 << 18;
                                        }
                                    }
                                } catch (Throwable throwable1) {
                                    throwable = throwable1;
                                    throw throwable1;
                                } finally {
                                    if (blockposition_b != null) {
                                        if (throwable != null) {
                                            try {
                                                blockposition_b.close();
                                            } catch (Throwable throwable2) {
                                                throwable.addSuppressed(throwable2);
                                            }
                                        } else {
                                            blockposition_b.close();
                                        }
                                    }

                                }
                            }
                        }
                    }
                }

                i = 0;
            }

            this.methodProfiler.exit();
            this.methodProfiler.enter("checkedPosition < toCheckCount");

            while (i < j) {
                l1 = this.E[i++];
                i2 = (l1 & 63) - 32 + i1;
                j2 = (l1 >> 6 & 63) - 32 + j1;
                k2 = (l1 >> 12 & 63) - 32 + k1;
                BlockPosition blockposition2 = new BlockPosition(i2, j2, k2);
                int k5 = this.getBrightness(enumskyblock, blockposition2);

                l2 = this.a(blockposition2, enumskyblock);
                if (l2 != k5) {
                    this.a(enumskyblock, blockposition2, l2);
                    if (l2 > k5) {
                        i3 = Math.abs(i2 - i1);
                        j3 = Math.abs(j2 - j1);
                        k3 = Math.abs(k2 - k1);
                        boolean flag = j < this.E.length - 6;

                        if (i3 + j3 + k3 < 17 && flag) {
                            if (this.getBrightness(enumskyblock, blockposition2.west()) < l2) {
                                this.E[j++] = i2 - 1 - i1 + 32 + (j2 - j1 + 32 << 6) + (k2 - k1 + 32 << 12);
                            }

                            if (this.getBrightness(enumskyblock, blockposition2.east()) < l2) {
                                this.E[j++] = i2 + 1 - i1 + 32 + (j2 - j1 + 32 << 6) + (k2 - k1 + 32 << 12);
                            }

                            if (this.getBrightness(enumskyblock, blockposition2.down()) < l2) {
                                this.E[j++] = i2 - i1 + 32 + (j2 - 1 - j1 + 32 << 6) + (k2 - k1 + 32 << 12);
                            }

                            if (this.getBrightness(enumskyblock, blockposition2.up()) < l2) {
                                this.E[j++] = i2 - i1 + 32 + (j2 + 1 - j1 + 32 << 6) + (k2 - k1 + 32 << 12);
                            }

                            if (this.getBrightness(enumskyblock, blockposition2.north()) < l2) {
                                this.E[j++] = i2 - i1 + 32 + (j2 - j1 + 32 << 6) + (k2 - 1 - k1 + 32 << 12);
                            }

                            if (this.getBrightness(enumskyblock, blockposition2.south()) < l2) {
                                this.E[j++] = i2 - i1 + 32 + (j2 - j1 + 32 << 6) + (k2 + 1 - k1 + 32 << 12);
                            }
                        }
                    }
                }
            }

            this.methodProfiler.exit();
            return true;
        }
    }

    public Stream<VoxelShape> a(@Nullable Entity entity, VoxelShape voxelshape, VoxelShape voxelshape1, Set<Entity> set) {
        Stream<VoxelShape> stream = GeneratorAccess.super.a(entity, voxelshape, voxelshape1, set);

        return entity == null ? stream : Stream.concat(stream, this.a(entity, voxelshape, set));
    }

    public List<Entity> getEntities(@Nullable Entity entity, AxisAlignedBB axisalignedbb, @Nullable Predicate<? super Entity> predicate) {
        List<Entity> list = Lists.newArrayList();
        int i = MathHelper.floor((axisalignedbb.minX - 2.0D) / 16.0D);
        int j = MathHelper.floor((axisalignedbb.maxX + 2.0D) / 16.0D);
        int k = MathHelper.floor((axisalignedbb.minZ - 2.0D) / 16.0D);
        int l = MathHelper.floor((axisalignedbb.maxZ + 2.0D) / 16.0D);

        for (int i1 = i; i1 <= j; ++i1) {
            for (int j1 = k; j1 <= l; ++j1) {
                if (this.isChunkLoaded(i1, j1, true)) {
                    this.getChunkAt(i1, j1).a(entity, axisalignedbb, list, predicate);
                }
            }
        }

        return list;
    }

    public <T extends Entity> List<T> a(Class<? extends T> oclass, Predicate<? super T> predicate) {
        List<T> list = Lists.newArrayList();
        Iterator iterator = this.entityList.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (oclass.isAssignableFrom(entity.getClass()) && predicate.test(entity)) {
                list.add(entity);
            }
        }

        return list;
    }

    public <T extends Entity> List<T> b(Class<? extends T> oclass, Predicate<? super T> predicate) {
        List<T> list = Lists.newArrayList();
        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (oclass.isAssignableFrom(entity.getClass()) && predicate.test(entity)) {
                list.add(entity);
            }
        }

        return list;
    }

    public <T extends Entity> List<T> a(Class<? extends T> oclass, AxisAlignedBB axisalignedbb) {
        return this.a(oclass, axisalignedbb, IEntitySelector.f);
    }

    public <T extends Entity> List<T> a(Class<? extends T> oclass, AxisAlignedBB axisalignedbb, @Nullable Predicate<? super T> predicate) {
        int i = MathHelper.floor((axisalignedbb.minX - 2.0D) / 16.0D);
        int j = MathHelper.f((axisalignedbb.maxX + 2.0D) / 16.0D);
        int k = MathHelper.floor((axisalignedbb.minZ - 2.0D) / 16.0D);
        int l = MathHelper.f((axisalignedbb.maxZ + 2.0D) / 16.0D);
        List<T> list = Lists.newArrayList();

        for (int i1 = i; i1 < j; ++i1) {
            for (int j1 = k; j1 < l; ++j1) {
                if (this.isChunkLoaded(i1, j1, true)) {
                    this.getChunkAt(i1, j1).a(oclass, axisalignedbb, list, predicate);
                }
            }
        }

        return list;
    }

    @Nullable
    public <T extends Entity> T a(Class<? extends T> oclass, AxisAlignedBB axisalignedbb, T t0) {
        List<T> list = this.a(oclass, axisalignedbb);
        T t1 = null;
        double d0 = Double.MAX_VALUE;

        for (int i = 0; i < list.size(); ++i) {
            T t2 = (Entity) list.get(i);

            if (t2 != t0 && IEntitySelector.f.test(t2)) {
                double d1 = t0.h(t2);

                if (d1 <= d0) {
                    t1 = t2;
                    d0 = d1;
                }
            }
        }

        return t1;
    }

    @Nullable
    public Entity getEntity(int i) {
        return (Entity) this.entitiesById.get(i);
    }

    public void b(BlockPosition blockposition, TileEntity tileentity) {
        if (this.isLoaded(blockposition)) {
            this.getChunkAtWorldCoords(blockposition).markDirty();
        }

    }

    public int a(Class<?> oclass, int i) {
        int j = 0;
        Iterator iterator = this.entityList.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (!(entity instanceof EntityInsentient) || !((EntityInsentient) entity).isPersistent()) {
                if (oclass.isAssignableFrom(entity.getClass())) {
                    ++j;
                }

                if (j > i) {
                    return j;
                }
            }
        }

        return j;
    }

    public void a(Stream<Entity> stream) {
        stream.forEach((entity) -> {
            this.entityList.add(entity);
            this.b(entity);
        });
    }

    public void b(Collection<Entity> collection) {
        this.g.addAll(collection);
    }

    public int getSeaLevel() {
        return this.b;
    }

    public World getMinecraftWorld() {
        return this;
    }

    public void b(int i) {
        this.b = i;
    }

    public int a(BlockPosition blockposition, EnumDirection enumdirection) {
        return this.getType(blockposition).b((IBlockAccess) this, blockposition, enumdirection);
    }

    public WorldType S() {
        return this.worldData.getType();
    }

    public int getBlockPower(BlockPosition blockposition) {
        byte b0 = 0;
        int i = Math.max(b0, this.a(blockposition.down(), EnumDirection.DOWN));

        if (i >= 15) {
            return i;
        } else {
            i = Math.max(i, this.a(blockposition.up(), EnumDirection.UP));
            if (i >= 15) {
                return i;
            } else {
                i = Math.max(i, this.a(blockposition.north(), EnumDirection.NORTH));
                if (i >= 15) {
                    return i;
                } else {
                    i = Math.max(i, this.a(blockposition.south(), EnumDirection.SOUTH));
                    if (i >= 15) {
                        return i;
                    } else {
                        i = Math.max(i, this.a(blockposition.west(), EnumDirection.WEST));
                        if (i >= 15) {
                            return i;
                        } else {
                            i = Math.max(i, this.a(blockposition.east(), EnumDirection.EAST));
                            return i >= 15 ? i : i;
                        }
                    }
                }
            }
        }
    }

    public boolean isBlockFacePowered(BlockPosition blockposition, EnumDirection enumdirection) {
        return this.getBlockFacePower(blockposition, enumdirection) > 0;
    }

    public int getBlockFacePower(BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = this.getType(blockposition);

        return iblockdata.isOccluding() ? this.getBlockPower(blockposition) : iblockdata.a((IBlockAccess) this, blockposition, enumdirection);
    }

    public boolean isBlockIndirectlyPowered(BlockPosition blockposition) {
        return this.getBlockFacePower(blockposition.down(), EnumDirection.DOWN) > 0 ? true : (this.getBlockFacePower(blockposition.up(), EnumDirection.UP) > 0 ? true : (this.getBlockFacePower(blockposition.north(), EnumDirection.NORTH) > 0 ? true : (this.getBlockFacePower(blockposition.south(), EnumDirection.SOUTH) > 0 ? true : (this.getBlockFacePower(blockposition.west(), EnumDirection.WEST) > 0 ? true : this.getBlockFacePower(blockposition.east(), EnumDirection.EAST) > 0))));
    }

    public int u(BlockPosition blockposition) {
        int i = 0;
        EnumDirection[] aenumdirection = World.a;
        int j = aenumdirection.length;

        for (int k = 0; k < j; ++k) {
            EnumDirection enumdirection = aenumdirection[k];
            int l = this.getBlockFacePower(blockposition.shift(enumdirection), enumdirection);

            if (l >= 15) {
                return 15;
            }

            if (l > i) {
                i = l;
            }
        }

        return i;
    }

    @Nullable
    public EntityHuman a(double d0, double d1, double d2, double d3, Predicate<Entity> predicate) {
        double d4 = -1.0D;
        EntityHuman entityhuman = null;

        for (int i = 0; i < this.players.size(); ++i) {
            EntityHuman entityhuman1 = (EntityHuman) this.players.get(i);

            if (predicate.test(entityhuman1)) {
                double d5 = entityhuman1.d(d0, d1, d2);

                if ((d3 < 0.0D || d5 < d3 * d3) && (d4 == -1.0D || d5 < d4)) {
                    d4 = d5;
                    entityhuman = entityhuman1;
                }
            }
        }

        return entityhuman;
    }

    public boolean isPlayerNearby(double d0, double d1, double d2, double d3) {
        for (int i = 0; i < this.players.size(); ++i) {
            EntityHuman entityhuman = (EntityHuman) this.players.get(i);

            if (IEntitySelector.f.test(entityhuman)) {
                double d4 = entityhuman.d(d0, d1, d2);

                if (d3 < 0.0D || d4 < d3 * d3) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean b(double d0, double d1, double d2, double d3) {
        Iterator iterator = this.players.iterator();

        double d4;

        do {
            EntityHuman entityhuman;

            do {
                do {
                    if (!iterator.hasNext()) {
                        return false;
                    }

                    entityhuman = (EntityHuman) iterator.next();
                } while (!IEntitySelector.f.test(entityhuman));
            } while (!IEntitySelector.b.test(entityhuman));

            d4 = entityhuman.d(d0, d1, d2);
        } while (d3 >= 0.0D && d4 >= d3 * d3);

        return true;
    }

    @Nullable
    public EntityHuman a(double d0, double d1, double d2) {
        double d3 = -1.0D;
        EntityHuman entityhuman = null;

        for (int i = 0; i < this.players.size(); ++i) {
            EntityHuman entityhuman1 = (EntityHuman) this.players.get(i);

            if (IEntitySelector.f.test(entityhuman1)) {
                double d4 = entityhuman1.d(d0, entityhuman1.locY, d1);

                if ((d2 < 0.0D || d4 < d2 * d2) && (d3 == -1.0D || d4 < d3)) {
                    d3 = d4;
                    entityhuman = entityhuman1;
                }
            }
        }

        return entityhuman;
    }

    @Nullable
    public EntityHuman a(Entity entity, double d0, double d1) {
        return this.a(entity.locX, entity.locY, entity.locZ, d0, d1, (Function) null, (Predicate) null);
    }

    @Nullable
    public EntityHuman a(BlockPosition blockposition, double d0, double d1) {
        return this.a((double) ((float) blockposition.getX() + 0.5F), (double) ((float) blockposition.getY() + 0.5F), (double) ((float) blockposition.getZ() + 0.5F), d0, d1, (Function) null, (Predicate) null);
    }

    @Nullable
    public EntityHuman a(double d0, double d1, double d2, double d3, double d4, @Nullable Function<EntityHuman, Double> function, @Nullable Predicate<EntityHuman> predicate) {
        double d5 = -1.0D;
        EntityHuman entityhuman = null;

        for (int i = 0; i < this.players.size(); ++i) {
            EntityHuman entityhuman1 = (EntityHuman) this.players.get(i);

            if (!entityhuman1.abilities.isInvulnerable && entityhuman1.isAlive() && !entityhuman1.isSpectator() && (predicate == null || predicate.test(entityhuman1))) {
                double d6 = entityhuman1.d(d0, entityhuman1.locY, d2);
                double d7 = d3;

                if (entityhuman1.isSneaking()) {
                    d7 = d3 * 0.800000011920929D;
                }

                if (entityhuman1.isInvisible()) {
                    float f = entityhuman1.dk();

                    if (f < 0.1F) {
                        f = 0.1F;
                    }

                    d7 *= (double) (0.7F * f);
                }

                if (function != null) {
                    d7 *= (Double) MoreObjects.firstNonNull(function.apply(entityhuman1), 1.0D);
                }

                if ((d4 < 0.0D || Math.abs(entityhuman1.locY - d1) < d4 * d4) && (d3 < 0.0D || d6 < d7 * d7) && (d5 == -1.0D || d6 < d5)) {
                    d5 = d6;
                    entityhuman = entityhuman1;
                }
            }
        }

        return entityhuman;
    }

    @Nullable
    public EntityHuman a(String s) {
        for (int i = 0; i < this.players.size(); ++i) {
            EntityHuman entityhuman = (EntityHuman) this.players.get(i);

            if (s.equals(entityhuman.getDisplayName().getString())) {
                return entityhuman;
            }
        }

        return null;
    }

    @Nullable
    public EntityHuman b(UUID uuid) {
        for (int i = 0; i < this.players.size(); ++i) {
            EntityHuman entityhuman = (EntityHuman) this.players.get(i);

            if (uuid.equals(entityhuman.getUniqueID())) {
                return entityhuman;
            }
        }

        return null;
    }

    public void checkSession() throws ExceptionWorldConflict {
        this.dataManager.checkSession();
    }

    public long getSeed() {
        return this.worldData.getSeed();
    }

    public long getTime() {
        return this.worldData.getTime();
    }

    public long getDayTime() {
        return this.worldData.getDayTime();
    }

    public void setDayTime(long i) {
        this.worldData.setDayTime(i);
    }

    public BlockPosition getSpawn() {
        BlockPosition blockposition = new BlockPosition(this.worldData.b(), this.worldData.c(), this.worldData.d());

        if (!this.getWorldBorder().a(blockposition)) {
            blockposition = this.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, new BlockPosition(this.getWorldBorder().getCenterX(), 0.0D, this.getWorldBorder().getCenterZ()));
        }

        return blockposition;
    }

    public void v(BlockPosition blockposition) {
        this.worldData.setSpawn(blockposition);
    }

    public boolean a(EntityHuman entityhuman, BlockPosition blockposition) {
        return true;
    }

    public void broadcastEntityEffect(Entity entity, byte b0) {}

    public IChunkProvider getChunkProvider() {
        return this.chunkProvider;
    }

    public void playBlockAction(BlockPosition blockposition, Block block, int i, int j) {
        this.getType(blockposition).a(this, blockposition, i, j);
    }

    public IDataManager getDataManager() {
        return this.dataManager;
    }

    public WorldData getWorldData() {
        return this.worldData;
    }

    public GameRules getGameRules() {
        return this.worldData.w();
    }

    public void everyoneSleeping() {}

    public float g(float f) {
        return (this.q + (this.r - this.q) * f) * this.i(f);
    }

    public float i(float f) {
        return this.o + (this.p - this.o) * f;
    }

    public boolean Y() {
        return this.worldProvider.g() && !this.worldProvider.h() ? (double) this.g(1.0F) > 0.9D : false;
    }

    public boolean isRaining() {
        return (double) this.i(1.0F) > 0.2D;
    }

    public boolean isRainingAt(BlockPosition blockposition) {
        return !this.isRaining() ? false : (!this.e(blockposition) ? false : (this.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, blockposition).getY() > blockposition.getY() ? false : this.getBiome(blockposition).c() == BiomeBase.Precipitation.RAIN));
    }

    public boolean x(BlockPosition blockposition) {
        BiomeBase biomebase = this.getBiome(blockposition);

        return biomebase.d();
    }

    @Nullable
    public PersistentCollection h() {
        return this.worldMaps;
    }

    public void a(int i, BlockPosition blockposition, int j) {
        for (int k = 0; k < this.v.size(); ++k) {
            ((IWorldAccess) this.v.get(k)).a(i, blockposition, j);
        }

    }

    public void triggerEffect(int i, BlockPosition blockposition, int j) {
        this.a((EntityHuman) null, i, blockposition, j);
    }

    public void a(@Nullable EntityHuman entityhuman, int i, BlockPosition blockposition, int j) {
        try {
            for (int k = 0; k < this.v.size(); ++k) {
                ((IWorldAccess) this.v.get(k)).a(entityhuman, i, blockposition, j);
            }

        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Playing level event");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Level event being played");

            crashreportsystemdetails.a("Block coordinates", (Object) CrashReportSystemDetails.a(blockposition));
            crashreportsystemdetails.a("Event source", (Object) entityhuman);
            crashreportsystemdetails.a("Event type", (Object) i);
            crashreportsystemdetails.a("Event data", (Object) j);
            throw new ReportedException(crashreport);
        }
    }

    public int getHeight() {
        return 256;
    }

    public int ab() {
        return this.worldProvider.h() ? 128 : 256;
    }

    public CrashReportSystemDetails a(CrashReport crashreport) {
        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Affected level", 1);

        crashreportsystemdetails.a("Level name", (Object) (this.worldData == null ? "????" : this.worldData.getName()));
        crashreportsystemdetails.a("All players", () -> {
            return this.players.size() + " total; " + this.players;
        });
        crashreportsystemdetails.a("Chunk stats", () -> {
            return this.chunkProvider.getName();
        });

        try {
            this.worldData.a(crashreportsystemdetails);
        } catch (Throwable throwable) {
            crashreportsystemdetails.a("Level Data Unobtainable", throwable);
        }

        return crashreportsystemdetails;
    }

    public void c(int i, BlockPosition blockposition, int j) {
        for (int k = 0; k < this.v.size(); ++k) {
            IWorldAccess iworldaccess = (IWorldAccess) this.v.get(k);

            iworldaccess.b(i, blockposition, j);
        }

    }

    public abstract Scoreboard getScoreboard();

    public void updateAdjacentComparators(BlockPosition blockposition, Block block) {
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.shift(enumdirection);

            if (this.isLoaded(blockposition1)) {
                IBlockData iblockdata = this.getType(blockposition1);

                if (iblockdata.getBlock() == Blocks.COMPARATOR) {
                    iblockdata.doPhysics(this, blockposition1, block, blockposition);
                } else if (iblockdata.isOccluding()) {
                    blockposition1 = blockposition1.shift(enumdirection);
                    iblockdata = this.getType(blockposition1);
                    if (iblockdata.getBlock() == Blocks.COMPARATOR) {
                        iblockdata.doPhysics(this, blockposition1, block, blockposition);
                    }
                }
            }
        }

    }

    public DifficultyDamageScaler getDamageScaler(BlockPosition blockposition) {
        long i = 0L;
        float f = 0.0F;

        if (this.isLoaded(blockposition)) {
            f = this.ah();
            i = this.getChunkAtWorldCoords(blockposition).m();
        }

        return new DifficultyDamageScaler(this.getDifficulty(), this.getDayTime(), i, f);
    }

    public int c() {
        return this.G;
    }

    public void c(int i) {
        this.G = i;
    }

    public void d(int i) {
        this.H = i;
    }

    public PersistentVillage af() {
        return this.villages;
    }

    public WorldBorder getWorldBorder() {
        return this.K;
    }

    public boolean e(int i, int j) {
        BlockPosition blockposition = this.getSpawn();
        int k = i * 16 + 8 - blockposition.getX();
        int l = j * 16 + 8 - blockposition.getZ();
        boolean flag = true;

        return k >= -128 && k <= 128 && l >= -128 && l <= 128;
    }

    public LongSet ag() {
        ForcedChunk forcedchunk = (ForcedChunk) this.a(this.worldProvider.getDimensionManager(), ForcedChunk::new, "chunks");

        return (LongSet) (forcedchunk != null ? LongSets.unmodifiable(forcedchunk.a()) : LongSets.EMPTY_SET);
    }

    public boolean isForceLoaded(int i, int j) {
        ForcedChunk forcedchunk = (ForcedChunk) this.a(this.worldProvider.getDimensionManager(), ForcedChunk::new, "chunks");

        return forcedchunk != null && forcedchunk.a().contains(ChunkCoordIntPair.a(i, j));
    }

    public boolean setForceLoaded(int i, int j, boolean flag) {
        String s = "chunks";
        ForcedChunk forcedchunk = (ForcedChunk) this.a(this.worldProvider.getDimensionManager(), ForcedChunk::new, "chunks");

        if (forcedchunk == null) {
            forcedchunk = new ForcedChunk("chunks");
            this.a(this.worldProvider.getDimensionManager(), "chunks", (PersistentBase) forcedchunk);
        }

        long k = ChunkCoordIntPair.a(i, j);
        boolean flag1;

        if (flag) {
            flag1 = forcedchunk.a().add(k);
            if (flag1) {
                this.getChunkAt(i, j);
            }
        } else {
            flag1 = forcedchunk.a().remove(k);
        }

        forcedchunk.a(flag1);
        return flag1;
    }

    public void a(Packet<?> packet) {
        throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
    }

    @Nullable
    public BlockPosition a(String s, BlockPosition blockposition, int i, boolean flag) {
        return null;
    }

    public WorldProvider o() {
        return this.worldProvider;
    }

    public Random m() {
        return this.random;
    }

    public abstract CraftingManager getCraftingManager();

    public abstract TagRegistry F();
}
