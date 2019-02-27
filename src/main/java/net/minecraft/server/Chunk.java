package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk implements IChunkAccess {

    private static final Logger d = LogManager.getLogger();
    public static final ChunkSection a = null;
    private final ChunkSection[] sections;
    private final BiomeBase[] f;
    private final boolean[] g;
    private final Map<BlockPosition, NBTTagCompound> h;
    private boolean i;
    public final World world;
    public final Map<HeightMap.Type, HeightMap> heightMap;
    public final int locX;
    public final int locZ;
    private boolean l;
    private final ChunkConverter m;
    public final Map<BlockPosition, TileEntity> tileEntities;
    public final EntitySlice<Entity>[] entitySlices;
    private final Map<String, StructureStart> p;
    private final Map<String, LongSet> q;
    private final ShortList[] r;
    private final TickList<Block> s;
    private final TickList<FluidType> t;
    private boolean u;
    private boolean v;
    private long lastSaved;
    private boolean x;
    private int y;
    private long z;
    private int A;
    private final ConcurrentLinkedQueue<BlockPosition> B;
    private ChunkStatus C;
    private int D;
    private final AtomicInteger E;
    private final ChunkCoordIntPair F;

    public Chunk(World world, int i, int j, BiomeBase[] abiomebase, ChunkConverter chunkconverter, TickList<Block> ticklist, TickList<FluidType> ticklist1, long k) {
        this.sections = new ChunkSection[16];
        this.g = new boolean[256];
        this.h = Maps.newHashMap();
        this.heightMap = Maps.newEnumMap(HeightMap.Type.class);
        this.tileEntities = Maps.newHashMap();
        this.p = Maps.newHashMap();
        this.q = Maps.newHashMap();
        this.r = new ShortList[16];
        this.A = 4096;
        this.B = Queues.newConcurrentLinkedQueue();
        this.C = ChunkStatus.EMPTY;
        this.E = new AtomicInteger();
        this.entitySlices = (EntitySlice[]) (new EntitySlice[16]);
        this.world = world;
        this.locX = i;
        this.locZ = j;
        this.F = new ChunkCoordIntPair(i, j);
        this.m = chunkconverter;
        HeightMap.Type[] aheightmap_type = HeightMap.Type.values();
        int l = aheightmap_type.length;

        for (int i1 = 0; i1 < l; ++i1) {
            HeightMap.Type heightmap_type = aheightmap_type[i1];

            if (heightmap_type.c() == HeightMap.Use.LIVE_WORLD) {
                this.heightMap.put(heightmap_type, new HeightMap(this, heightmap_type));
            }
        }

        for (int j1 = 0; j1 < this.entitySlices.length; ++j1) {
            this.entitySlices[j1] = new EntitySlice<>(Entity.class);
        }

        this.f = abiomebase;
        this.s = ticklist;
        this.t = ticklist1;
        this.z = k;
    }

    public Chunk(World world, ProtoChunk protochunk, int i, int j) {
        this(world, i, j, protochunk.getBiomeIndex(), protochunk.v(), protochunk.k(), protochunk.l(), protochunk.m());

        int k;

        for (k = 0; k < this.sections.length; ++k) {
            this.sections[k] = protochunk.getSections()[k];
        }

        Iterator iterator = protochunk.s().iterator();

        while (iterator.hasNext()) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) iterator.next();

            ChunkRegionLoader.a(nbttagcompound, world, this);
        }

        iterator = protochunk.r().values().iterator();

        while (iterator.hasNext()) {
            TileEntity tileentity = (TileEntity) iterator.next();

            this.a(tileentity);
        }

        this.h.putAll(protochunk.w());

        for (k = 0; k < protochunk.u().length; ++k) {
            this.r[k] = protochunk.u()[k];
        }

        this.a(protochunk.e());
        this.b(protochunk.f());
        iterator = protochunk.t().iterator();

        while (iterator.hasNext()) {
            HeightMap.Type heightmap_type = (HeightMap.Type) iterator.next();

            if (heightmap_type.c() == HeightMap.Use.LIVE_WORLD) {
                ((HeightMap) this.heightMap.computeIfAbsent(heightmap_type, (heightmap_type1) -> {
                    return new HeightMap(this, heightmap_type1);
                })).a(protochunk.b(heightmap_type).b());
            }
        }

        this.x = true;
        this.a(ChunkStatus.FULLCHUNK);
    }

    public Set<BlockPosition> t() {
        Set<BlockPosition> set = Sets.newHashSet(this.h.keySet());

        set.addAll(this.tileEntities.keySet());
        return set;
    }

    public boolean a(int i, int j) {
        return i == this.locX && j == this.locZ;
    }

    public ChunkSection[] getSections() {
        return this.sections;
    }

    public void initLighting() {
        int i = this.b();

        this.y = Integer.MAX_VALUE;
        Iterator iterator = this.heightMap.values().iterator();

        while (iterator.hasNext()) {
            HeightMap heightmap = (HeightMap) iterator.next();

            heightmap.a();
        }

        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                if (this.world.worldProvider.g()) {
                    int l = 15;
                    int i1 = i + 16 - 1;

                    do {
                        int j1 = this.d(j, i1, k);

                        if (j1 == 0 && l != 15) {
                            j1 = 1;
                        }

                        l -= j1;
                        if (l > 0) {
                            ChunkSection chunksection = this.sections[i1 >> 4];

                            if (chunksection != Chunk.a) {
                                chunksection.a(j, i1 & 15, k, l);
                                this.world.m(new BlockPosition((this.locX << 4) + j, i1, (this.locZ << 4) + k));
                            }
                        }

                        --i1;
                    } while (i1 > 0 && l > 0);
                }
            }
        }

        this.x = true;
    }

    private void c(int i, int j) {
        this.g[i + j * 16] = true;
        this.l = true;
    }

    private void g(boolean flag) {
        this.world.methodProfiler.enter("recheckGaps");
        if (this.world.areChunksLoaded(new BlockPosition(this.locX * 16 + 8, 0, this.locZ * 16 + 8), 16)) {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (this.g[i + j * 16]) {
                        this.g[i + j * 16] = false;
                        int k = this.a(HeightMap.Type.LIGHT_BLOCKING, i, j);
                        int l = this.locX * 16 + i;
                        int i1 = this.locZ * 16 + j;
                        int j1 = Integer.MAX_VALUE;

                        EnumDirection enumdirection;
                        Iterator iterator;

                        for (iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator(); iterator.hasNext(); j1 = Math.min(j1, this.world.d(l + enumdirection.getAdjacentX(), i1 + enumdirection.getAdjacentZ()))) {
                            enumdirection = (EnumDirection) iterator.next();
                        }

                        this.c(l, i1, j1);
                        iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                        while (iterator.hasNext()) {
                            enumdirection = (EnumDirection) iterator.next();
                            this.c(l + enumdirection.getAdjacentX(), i1 + enumdirection.getAdjacentZ(), k);
                        }

                        if (flag) {
                            this.world.methodProfiler.exit();
                            return;
                        }
                    }
                }
            }

            this.l = false;
        }

        this.world.methodProfiler.exit();
    }

    private void c(int i, int j, int k) {
        int l = this.world.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, new BlockPosition(i, 0, j)).getY();

        if (l > k) {
            this.a(i, j, k, l + 1);
        } else if (l < k) {
            this.a(i, j, l, k + 1);
        }

    }

    private void a(int i, int j, int k, int l) {
        if (l > k && this.world.areChunksLoaded(new BlockPosition(i, 0, j), 16)) {
            for (int i1 = k; i1 < l; ++i1) {
                this.world.c(EnumSkyBlock.SKY, new BlockPosition(i, i1, j));
            }

            this.x = true;
        }

    }

    private void a(int i, int j, int k, IBlockData iblockdata) {
        HeightMap heightmap = (HeightMap) this.heightMap.get(HeightMap.Type.LIGHT_BLOCKING);
        int l = heightmap.a(i & 15, k & 15) & 255;

        if (heightmap.a(i, j, k, iblockdata)) {
            int i1 = heightmap.a(i & 15, k & 15);
            int j1 = this.locX * 16 + i;
            int k1 = this.locZ * 16 + k;

            this.world.a(j1, k1, i1, l);
            int l1;
            int i2;
            int j2;

            if (this.world.worldProvider.g()) {
                l1 = Math.min(l, i1);
                i2 = Math.max(l, i1);
                j2 = i1 < l ? 15 : 0;

                int k2;

                for (k2 = l1; k2 < i2; ++k2) {
                    ChunkSection chunksection = this.sections[k2 >> 4];

                    if (chunksection != Chunk.a) {
                        chunksection.a(i, k2 & 15, k, j2);
                        this.world.m(new BlockPosition((this.locX << 4) + i, k2, (this.locZ << 4) + k));
                    }
                }

                k2 = 15;

                while (i1 > 0 && k2 > 0) {
                    --i1;
                    int l2 = this.d(i, i1, k);

                    l2 = l2 == 0 ? 1 : l2;
                    k2 -= l2;
                    k2 = Math.max(0, k2);
                    ChunkSection chunksection1 = this.sections[i1 >> 4];

                    if (chunksection1 != Chunk.a) {
                        chunksection1.a(i, i1 & 15, k, k2);
                    }
                }
            }

            if (i1 < this.y) {
                this.y = i1;
            }

            if (this.world.worldProvider.g()) {
                l1 = heightmap.a(i & 15, k & 15);
                i2 = Math.min(l, l1);
                j2 = Math.max(l, l1);
                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                while (iterator.hasNext()) {
                    EnumDirection enumdirection = (EnumDirection) iterator.next();

                    this.a(j1 + enumdirection.getAdjacentX(), k1 + enumdirection.getAdjacentZ(), i2, j2);
                }

                this.a(j1, k1, i2, j2);
            }

            this.x = true;
        }
    }

    private int d(int i, int j, int k) {
        return this.getBlockData(i, j, k).b(this.world, new BlockPosition(i, j, k));
    }

    public IBlockData getType(BlockPosition blockposition) {
        return this.getBlockData(blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    public IBlockData getBlockData(int i, int j, int k) {
        if (this.world.S() == WorldType.DEBUG_ALL_BLOCK_STATES) {
            IBlockData iblockdata = null;

            if (j == 60) {
                iblockdata = Blocks.BARRIER.getBlockData();
            }

            if (j == 70) {
                iblockdata = ChunkProviderDebug.b(i, k);
            }

            return iblockdata == null ? Blocks.AIR.getBlockData() : iblockdata;
        } else {
            try {
                if (j >= 0 && j >> 4 < this.sections.length) {
                    ChunkSection chunksection = this.sections[j >> 4];

                    if (chunksection != Chunk.a) {
                        return chunksection.getType(i & 15, j & 15, k & 15);
                    }
                }

                return Blocks.AIR.getBlockData();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Getting block state");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being got");

                crashreportsystemdetails.a("Location", () -> {
                    return CrashReportSystemDetails.a(i, j, k);
                });
                throw new ReportedException(crashreport);
            }
        }
    }

    public Fluid getFluid(BlockPosition blockposition) {
        return this.b(blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    public Fluid b(int i, int j, int k) {
        try {
            if (j >= 0 && j >> 4 < this.sections.length) {
                ChunkSection chunksection = this.sections[j >> 4];

                if (chunksection != Chunk.a) {
                    return chunksection.b(i & 15, j & 15, k & 15);
                }
            }

            return FluidTypes.EMPTY.i();
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Getting fluid state");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being got");

            crashreportsystemdetails.a("Location", () -> {
                return CrashReportSystemDetails.a(i, j, k);
            });
            throw new ReportedException(crashreport);
        }
    }

    @Nullable
    public IBlockData setType(BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getY();
        int k = blockposition.getZ() & 15;
        int l = ((HeightMap) this.heightMap.get(HeightMap.Type.LIGHT_BLOCKING)).a(i, k);
        IBlockData iblockdata1 = this.getType(blockposition);

        if (iblockdata1 == iblockdata) {
            return null;
        } else {
            Block block = iblockdata.getBlock();
            Block block1 = iblockdata1.getBlock();
            ChunkSection chunksection = this.sections[j >> 4];
            boolean flag1 = false;

            if (chunksection == Chunk.a) {
                if (iblockdata.isAir()) {
                    return null;
                }

                chunksection = new ChunkSection(j >> 4 << 4, this.world.worldProvider.g());
                this.sections[j >> 4] = chunksection;
                flag1 = j >= l;
            }

            chunksection.setType(i, j & 15, k, iblockdata);
            ((HeightMap) this.heightMap.get(HeightMap.Type.MOTION_BLOCKING)).a(i, j, k, iblockdata);
            ((HeightMap) this.heightMap.get(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES)).a(i, j, k, iblockdata);
            ((HeightMap) this.heightMap.get(HeightMap.Type.OCEAN_FLOOR)).a(i, j, k, iblockdata);
            ((HeightMap) this.heightMap.get(HeightMap.Type.WORLD_SURFACE)).a(i, j, k, iblockdata);
            if (!this.world.isClientSide) {
                iblockdata1.remove(this.world, blockposition, iblockdata, flag);
            } else if (block1 != block && block1 instanceof ITileEntity) {
                this.world.n(blockposition);
            }

            if (chunksection.getType(i, j & 15, k).getBlock() != block) {
                return null;
            } else {
                if (flag1) {
                    this.initLighting();
                } else {
                    int i1 = iblockdata.b(this.world, blockposition);
                    int j1 = iblockdata1.b(this.world, blockposition);

                    this.a(i, j, k, iblockdata);
                    if (i1 != j1 && (i1 < j1 || this.getBrightness(EnumSkyBlock.SKY, blockposition) > 0 || this.getBrightness(EnumSkyBlock.BLOCK, blockposition) > 0)) {
                        this.c(i, k);
                    }
                }

                TileEntity tileentity;

                if (block1 instanceof ITileEntity) {
                    tileentity = this.a(blockposition, Chunk.EnumTileEntityState.CHECK);
                    if (tileentity != null) {
                        tileentity.invalidateBlockCache();
                    }
                }

                if (!this.world.isClientSide) {
                    iblockdata.onPlace(this.world, blockposition, iblockdata1);
                }

                if (block instanceof ITileEntity) {
                    tileentity = this.a(blockposition, Chunk.EnumTileEntityState.CHECK);
                    if (tileentity == null) {
                        tileentity = ((ITileEntity) block).a(this.world);
                        this.world.setTileEntity(blockposition, tileentity);
                    } else {
                        tileentity.invalidateBlockCache();
                    }
                }

                this.x = true;
                return iblockdata1;
            }
        }
    }

    public int getBrightness(EnumSkyBlock enumskyblock, BlockPosition blockposition) {
        return this.a(enumskyblock, blockposition, this.world.o().g());
    }

    public int a(EnumSkyBlock enumskyblock, BlockPosition blockposition, boolean flag) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getY();
        int k = blockposition.getZ() & 15;
        int l = j >> 4;

        if (l >= 0 && l <= this.sections.length - 1) {
            ChunkSection chunksection = this.sections[l];

            return chunksection == Chunk.a ? (this.c(blockposition) ? enumskyblock.c : 0) : (enumskyblock == EnumSkyBlock.SKY ? (!flag ? 0 : chunksection.c(i, j & 15, k)) : (enumskyblock == EnumSkyBlock.BLOCK ? chunksection.d(i, j & 15, k) : enumskyblock.c));
        } else {
            return (enumskyblock != EnumSkyBlock.SKY || !flag) && enumskyblock != EnumSkyBlock.BLOCK ? 0 : enumskyblock.c;
        }
    }

    public void a(EnumSkyBlock enumskyblock, BlockPosition blockposition, int i) {
        this.a(enumskyblock, this.world.o().g(), blockposition, i);
    }

    public void a(EnumSkyBlock enumskyblock, boolean flag, BlockPosition blockposition, int i) {
        int j = blockposition.getX() & 15;
        int k = blockposition.getY();
        int l = blockposition.getZ() & 15;
        int i1 = k >> 4;

        if (i1 < 16 && i1 >= 0) {
            ChunkSection chunksection = this.sections[i1];

            if (chunksection == Chunk.a) {
                if (i == enumskyblock.c) {
                    return;
                }

                chunksection = new ChunkSection(i1 << 4, flag);
                this.sections[i1] = chunksection;
                this.initLighting();
            }

            if (enumskyblock == EnumSkyBlock.SKY) {
                if (this.world.worldProvider.g()) {
                    chunksection.a(j, k & 15, l, i);
                }
            } else if (enumskyblock == EnumSkyBlock.BLOCK) {
                chunksection.b(j, k & 15, l, i);
            }

            this.x = true;
        }
    }

    public int a(BlockPosition blockposition, int i) {
        return this.a(blockposition, i, this.world.o().g());
    }

    public int a(BlockPosition blockposition, int i, boolean flag) {
        int j = blockposition.getX() & 15;
        int k = blockposition.getY();
        int l = blockposition.getZ() & 15;
        int i1 = k >> 4;

        if (i1 >= 0 && i1 <= this.sections.length - 1) {
            ChunkSection chunksection = this.sections[i1];

            if (chunksection == Chunk.a) {
                return flag && i < EnumSkyBlock.SKY.c ? EnumSkyBlock.SKY.c - i : 0;
            } else {
                int j1 = flag ? chunksection.c(j, k & 15, l) : 0;

                j1 -= i;
                int k1 = chunksection.d(j, k & 15, l);

                if (k1 > j1) {
                    j1 = k1;
                }

                return j1;
            }
        } else {
            return 0;
        }
    }

    public void a(Entity entity) {
        this.v = true;
        int i = MathHelper.floor(entity.locX / 16.0D);
        int j = MathHelper.floor(entity.locZ / 16.0D);

        if (i != this.locX || j != this.locZ) {
            Chunk.d.warn("Wrong location! ({}, {}) should be ({}, {}), {}", i, j, this.locX, this.locZ, entity);
            entity.die();
        }

        int k = MathHelper.floor(entity.locY / 16.0D);

        if (k < 0) {
            k = 0;
        }

        if (k >= this.entitySlices.length) {
            k = this.entitySlices.length - 1;
        }

        entity.inChunk = true;
        entity.chunkX = this.locX;
        entity.chunkY = k;
        entity.chunkZ = this.locZ;
        this.entitySlices[k].add(entity);
    }

    public void a(HeightMap.Type heightmap_type, long[] along) {
        ((HeightMap) this.heightMap.get(heightmap_type)).a(along);
    }

    public void b(Entity entity) {
        this.a(entity, entity.chunkY);
    }

    public void a(Entity entity, int i) {
        if (i < 0) {
            i = 0;
        }

        if (i >= this.entitySlices.length) {
            i = this.entitySlices.length - 1;
        }

        this.entitySlices[i].remove(entity);
    }

    public boolean c(BlockPosition blockposition) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getY();
        int k = blockposition.getZ() & 15;

        return j >= ((HeightMap) this.heightMap.get(HeightMap.Type.LIGHT_BLOCKING)).a(i, k);
    }

    public int a(HeightMap.Type heightmap_type, int i, int j) {
        return ((HeightMap) this.heightMap.get(heightmap_type)).a(i & 15, j & 15) - 1;
    }

    @Nullable
    private TileEntity j(BlockPosition blockposition) {
        IBlockData iblockdata = this.getType(blockposition);
        Block block = iblockdata.getBlock();

        return !block.isTileEntity() ? null : ((ITileEntity) block).a(this.world);
    }

    @Nullable
    public TileEntity getTileEntity(BlockPosition blockposition) {
        return this.a(blockposition, Chunk.EnumTileEntityState.CHECK);
    }

    @Nullable
    public TileEntity a(BlockPosition blockposition, Chunk.EnumTileEntityState chunk_enumtileentitystate) {
        TileEntity tileentity = (TileEntity) this.tileEntities.get(blockposition);

        if (tileentity == null) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) this.h.remove(blockposition);

            if (nbttagcompound != null) {
                TileEntity tileentity1 = this.a(blockposition, nbttagcompound);

                if (tileentity1 != null) {
                    return tileentity1;
                }
            }
        }

        if (tileentity == null) {
            if (chunk_enumtileentitystate == Chunk.EnumTileEntityState.IMMEDIATE) {
                tileentity = this.j(blockposition);
                this.world.setTileEntity(blockposition, tileentity);
            } else if (chunk_enumtileentitystate == Chunk.EnumTileEntityState.QUEUED) {
                this.B.add(blockposition);
            }
        } else if (tileentity.x()) {
            this.tileEntities.remove(blockposition);
            return null;
        }

        return tileentity;
    }

    public void a(TileEntity tileentity) {
        this.a(tileentity.getPosition(), tileentity);
        if (this.i) {
            this.world.a(tileentity);
        }

    }

    public void a(BlockPosition blockposition, TileEntity tileentity) {
        tileentity.setWorld(this.world);
        tileentity.setPosition(blockposition);
        if (this.getType(blockposition).getBlock() instanceof ITileEntity) {
            if (this.tileEntities.containsKey(blockposition)) {
                ((TileEntity) this.tileEntities.get(blockposition)).y();
            }

            tileentity.z();
            this.tileEntities.put(blockposition.h(), tileentity);
        }
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.h.put(new BlockPosition(nbttagcompound.getInt("x"), nbttagcompound.getInt("y"), nbttagcompound.getInt("z")), nbttagcompound);
    }

    public void d(BlockPosition blockposition) {
        if (this.i) {
            TileEntity tileentity = (TileEntity) this.tileEntities.remove(blockposition);

            if (tileentity != null) {
                tileentity.y();
            }
        }

    }

    public void addEntities() {
        this.i = true;
        this.world.a(this.tileEntities.values());
        EntitySlice[] aentityslice = this.entitySlices;
        int i = aentityslice.length;

        for (int j = 0; j < i; ++j) {
            EntitySlice<Entity> entityslice = aentityslice[j];

            this.world.a(entityslice.stream().filter((entity) -> {
                return !(entity instanceof EntityHuman);
            }));
        }

    }

    public void removeEntities() {
        this.i = false;
        Iterator iterator = this.tileEntities.values().iterator();

        while (iterator.hasNext()) {
            TileEntity tileentity = (TileEntity) iterator.next();

            this.world.b(tileentity);
        }

        EntitySlice[] aentityslice = this.entitySlices;
        int i = aentityslice.length;

        for (int j = 0; j < i; ++j) {
            EntitySlice<Entity> entityslice = aentityslice[j];

            this.world.b((Collection) entityslice);
        }

    }

    public void markDirty() {
        this.x = true;
    }

    public void a(@Nullable Entity entity, AxisAlignedBB axisalignedbb, List<Entity> list, Predicate<? super Entity> predicate) {
        int i = MathHelper.floor((axisalignedbb.minY - 2.0D) / 16.0D);
        int j = MathHelper.floor((axisalignedbb.maxY + 2.0D) / 16.0D);

        i = MathHelper.clamp(i, 0, this.entitySlices.length - 1);
        j = MathHelper.clamp(j, 0, this.entitySlices.length - 1);

        for (int k = i; k <= j; ++k) {
            if (!this.entitySlices[k].isEmpty()) {
                Iterator iterator = this.entitySlices[k].iterator();

                while (iterator.hasNext()) {
                    Entity entity1 = (Entity) iterator.next();

                    if (entity1.getBoundingBox().c(axisalignedbb) && entity1 != entity) {
                        if (predicate == null || predicate.test(entity1)) {
                            list.add(entity1);
                        }

                        Entity[] aentity = entity1.bi();

                        if (aentity != null) {
                            Entity[] aentity1 = aentity;
                            int l = aentity.length;

                            for (int i1 = 0; i1 < l; ++i1) {
                                Entity entity2 = aentity1[i1];

                                if (entity2 != entity && entity2.getBoundingBox().c(axisalignedbb) && (predicate == null || predicate.test(entity2))) {
                                    list.add(entity2);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public <T extends Entity> void a(Class<? extends T> oclass, AxisAlignedBB axisalignedbb, List<T> list, @Nullable Predicate<? super T> predicate) {
        int i = MathHelper.floor((axisalignedbb.minY - 2.0D) / 16.0D);
        int j = MathHelper.floor((axisalignedbb.maxY + 2.0D) / 16.0D);

        i = MathHelper.clamp(i, 0, this.entitySlices.length - 1);
        j = MathHelper.clamp(j, 0, this.entitySlices.length - 1);

        for (int k = i; k <= j; ++k) {
            Iterator iterator = this.entitySlices[k].c(oclass).iterator();

            while (iterator.hasNext()) {
                T t0 = (Entity) iterator.next();

                if (t0.getBoundingBox().c(axisalignedbb) && (predicate == null || predicate.test(t0))) {
                    list.add(t0);
                }
            }
        }

    }

    public boolean c(boolean flag) {
        if (flag) {
            if (this.v && this.world.getTime() != this.lastSaved || this.x) {
                return true;
            }
        } else if (this.v && this.world.getTime() >= this.lastSaved + 600L) {
            return true;
        }

        return this.x;
    }

    public boolean isEmpty() {
        return false;
    }

    public void d(boolean flag) {
        if (this.l && this.world.worldProvider.g() && !flag) {
            this.g(this.world.isClientSide);
        }

        this.u = true;

        while (!this.B.isEmpty()) {
            BlockPosition blockposition = (BlockPosition) this.B.poll();

            if (this.a(blockposition, Chunk.EnumTileEntityState.CHECK) == null && this.getType(blockposition).getBlock().isTileEntity()) {
                TileEntity tileentity = this.j(blockposition);

                this.world.setTileEntity(blockposition, tileentity);
                this.world.a(blockposition, blockposition);
            }
        }

    }

    public boolean isReady() {
        return this.C.a(ChunkStatus.POSTPROCESSED);
    }

    public boolean v() {
        return this.u;
    }

    public ChunkCoordIntPair getPos() {
        return this.F;
    }

    public boolean b(int i, int j) {
        if (i < 0) {
            i = 0;
        }

        if (j >= 256) {
            j = 255;
        }

        for (int k = i; k <= j; k += 16) {
            ChunkSection chunksection = this.sections[k >> 4];

            if (chunksection != Chunk.a && !chunksection.a()) {
                return false;
            }
        }

        return true;
    }

    public void a(ChunkSection[] achunksection) {
        if (this.sections.length != achunksection.length) {
            Chunk.d.warn("Could not set level chunk sections, array length is {} instead of {}", achunksection.length, this.sections.length);
        } else {
            System.arraycopy(achunksection, 0, this.sections, 0, this.sections.length);
        }
    }

    public BiomeBase getBiome(BlockPosition blockposition) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getZ() & 15;

        return this.f[j << 4 | i];
    }

    public BiomeBase[] getBiomeIndex() {
        return this.f;
    }

    public void x() {
        if (this.A < 4096) {
            BlockPosition blockposition = new BlockPosition(this.locX << 4, 0, this.locZ << 4);

            for (int i = 0; i < 8; ++i) {
                if (this.A >= 4096) {
                    return;
                }

                int j = this.A % 16;
                int k = this.A / 16 % 16;
                int l = this.A / 256;

                ++this.A;

                for (int i1 = 0; i1 < 16; ++i1) {
                    BlockPosition blockposition1 = blockposition.a(k, (j << 4) + i1, l);
                    boolean flag = i1 == 0 || i1 == 15 || k == 0 || k == 15 || l == 0 || l == 15;

                    if (this.sections[j] == Chunk.a && flag || this.sections[j] != Chunk.a && this.sections[j].getType(k, i1, l).isAir()) {
                        EnumDirection[] aenumdirection = EnumDirection.values();
                        int j1 = aenumdirection.length;

                        for (int k1 = 0; k1 < j1; ++k1) {
                            EnumDirection enumdirection = aenumdirection[k1];
                            BlockPosition blockposition2 = blockposition1.shift(enumdirection);

                            if (this.world.getType(blockposition2).e() > 0) {
                                this.world.r(blockposition2);
                            }
                        }

                        this.world.r(blockposition1);
                    }
                }
            }

        }
    }

    public boolean y() {
        return this.i;
    }

    public World getWorld() {
        return this.world;
    }

    public Set<HeightMap.Type> A() {
        return this.heightMap.keySet();
    }

    public HeightMap b(HeightMap.Type heightmap_type) {
        return (HeightMap) this.heightMap.get(heightmap_type);
    }

    public Map<BlockPosition, TileEntity> getTileEntities() {
        return this.tileEntities;
    }

    public EntitySlice<Entity>[] getEntitySlices() {
        return this.entitySlices;
    }

    public NBTTagCompound g(BlockPosition blockposition) {
        return (NBTTagCompound) this.h.get(blockposition);
    }

    public TickList<Block> k() {
        return this.s;
    }

    public TickList<FluidType> l() {
        return this.t;
    }

    public BitSet a(WorldGenStage.Features worldgenstage_features) {
        throw new RuntimeException("Not yet implemented");
    }

    public void a(boolean flag) {
        this.x = flag;
    }

    public void f(boolean flag) {
        this.v = flag;
    }

    public void setLastSaved(long i) {
        this.lastSaved = i;
    }

    @Nullable
    public StructureStart a(String s) {
        return (StructureStart) this.p.get(s);
    }

    public void a(String s, StructureStart structurestart) {
        this.p.put(s, structurestart);
    }

    public Map<String, StructureStart> e() {
        return this.p;
    }

    public void a(Map<String, StructureStart> map) {
        this.p.clear();
        this.p.putAll(map);
    }

    @Nullable
    public LongSet b(String s) {
        return (LongSet) this.q.computeIfAbsent(s, (s1) -> {
            return new LongOpenHashSet();
        });
    }

    public void a(String s, long i) {
        ((LongSet) this.q.computeIfAbsent(s, (s1) -> {
            return new LongOpenHashSet();
        })).add(i);
    }

    public Map<String, LongSet> f() {
        return this.q;
    }

    public void b(Map<String, LongSet> map) {
        this.q.clear();
        this.q.putAll(map);
    }

    public int D() {
        return this.y;
    }

    public long m() {
        return this.z;
    }

    public void b(long i) {
        this.z = i;
    }

    public void E() {
        if (!this.C.a(ChunkStatus.POSTPROCESSED) && this.D == 8) {
            ChunkCoordIntPair chunkcoordintpair = this.getPos();

            for (int i = 0; i < this.r.length; ++i) {
                if (this.r[i] != null) {
                    ShortListIterator shortlistiterator = this.r[i].iterator();

                    while (shortlistiterator.hasNext()) {
                        Short oshort = (Short) shortlistiterator.next();
                        BlockPosition blockposition = ProtoChunk.a(oshort, i, chunkcoordintpair);
                        IBlockData iblockdata = this.world.getType(blockposition);
                        IBlockData iblockdata1 = Block.b(iblockdata, this.world, blockposition);

                        this.world.setTypeAndData(blockposition, iblockdata1, 20);
                    }

                    this.r[i].clear();
                }
            }

            if (this.s instanceof ProtoChunkTickList) {
                ((ProtoChunkTickList) this.s).a(this.world.getBlockTickList(), (blockposition1) -> {
                    return this.world.getType(blockposition1).getBlock();
                });
            }

            if (this.t instanceof ProtoChunkTickList) {
                ((ProtoChunkTickList) this.t).a(this.world.getFluidTickList(), (blockposition1) -> {
                    return this.world.getFluid(blockposition1).c();
                });
            }

            Iterator iterator = (new HashSet(this.h.keySet())).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();

                this.getTileEntity(blockposition1);
            }

            this.h.clear();
            this.a(ChunkStatus.POSTPROCESSED);
            this.m.a(this);
        }
    }

    @Nullable
    private TileEntity a(BlockPosition blockposition, NBTTagCompound nbttagcompound) {
        TileEntity tileentity;

        if ("DUMMY".equals(nbttagcompound.getString("id"))) {
            Block block = this.getType(blockposition).getBlock();

            if (block instanceof ITileEntity) {
                tileentity = ((ITileEntity) block).a(this.world);
            } else {
                tileentity = null;
                Chunk.d.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", blockposition, this.getType(blockposition));
            }
        } else {
            tileentity = TileEntity.create(nbttagcompound);
        }

        if (tileentity != null) {
            tileentity.setPosition(blockposition);
            this.a(tileentity);
        } else {
            Chunk.d.warn("Tried to load a block entity for block {} but failed at location {}", this.getType(blockposition), blockposition);
        }

        return tileentity;
    }

    public ChunkConverter F() {
        return this.m;
    }

    public ShortList[] G() {
        return this.r;
    }

    public void a(short short0, int i) {
        ProtoChunk.a(this.r, i).add(short0);
    }

    public ChunkStatus i() {
        return this.C;
    }

    public void a(ChunkStatus chunkstatus) {
        this.C = chunkstatus;
    }

    public void c(String s) {
        this.a(ChunkStatus.a(s));
    }

    public void H() {
        ++this.D;
        if (this.D > 8) {
            throw new RuntimeException("Error while adding chunk to cache. Too many neighbors");
        } else {
            if (this.J()) {
                ((IAsyncTaskHandler) this.world).postToMainThread(this::E);
            }

        }
    }

    public void I() {
        --this.D;
        if (this.D < 0) {
            throw new RuntimeException("Error while removing chunk from cache. Not enough neighbors");
        }
    }

    public boolean J() {
        return this.D == 8;
    }

    public static enum EnumTileEntityState {

        IMMEDIATE, QUEUED, CHECK;

        private EnumTileEntityState() {}
    }
}
