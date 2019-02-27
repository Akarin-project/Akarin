package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProtoChunk implements IChunkAccess {

    private static final Logger a = LogManager.getLogger();
    private final ChunkCoordIntPair b;
    private boolean c;
    private final AtomicInteger d;
    private BiomeBase[] e;
    private final Map<HeightMap.Type, HeightMap> f;
    private volatile ChunkStatus g;
    private final Map<BlockPosition, TileEntity> h;
    private final Map<BlockPosition, NBTTagCompound> i;
    private final ChunkSection[] j;
    private final List<NBTTagCompound> k;
    private final List<BlockPosition> l;
    private final ShortList[] m;
    private final Map<String, StructureStart> n;
    private final Map<String, LongSet> o;
    private final ChunkConverter p;
    private final ProtoChunkTickList<Block> q;
    private final ProtoChunkTickList<FluidType> r;
    private long s;
    private final Map<WorldGenStage.Features, BitSet> t;
    private boolean u;

    public ProtoChunk(int i, int j, ChunkConverter chunkconverter) {
        this(new ChunkCoordIntPair(i, j), chunkconverter);
    }

    public ProtoChunk(ChunkCoordIntPair chunkcoordintpair, ChunkConverter chunkconverter) {
        this.d = new AtomicInteger();
        this.f = Maps.newEnumMap(HeightMap.Type.class);
        this.g = ChunkStatus.EMPTY;
        this.h = Maps.newHashMap();
        this.i = Maps.newHashMap();
        this.j = new ChunkSection[16];
        this.k = Lists.newArrayList();
        this.l = Lists.newArrayList();
        this.m = new ShortList[16];
        this.n = Maps.newHashMap();
        this.o = Maps.newHashMap();
        this.t = Maps.newHashMap();
        this.b = chunkcoordintpair;
        this.p = chunkconverter;
        this.q = new ProtoChunkTickList<>((block) -> {
            return block == null || block.getBlockData().isAir();
        }, IRegistry.BLOCK::getKey, IRegistry.BLOCK::getOrDefault, chunkcoordintpair);
        this.r = new ProtoChunkTickList<>((fluidtype) -> {
            return fluidtype == null || fluidtype == FluidTypes.EMPTY;
        }, IRegistry.FLUID::getKey, IRegistry.FLUID::getOrDefault, chunkcoordintpair);
    }

    public static ShortList a(ShortList[] ashortlist, int i) {
        if (ashortlist[i] == null) {
            ashortlist[i] = new ShortArrayList();
        }

        return ashortlist[i];
    }

    @Nullable
    public IBlockData getType(BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();

        return j >= 0 && j < 256 ? (this.j[j >> 4] == Chunk.a ? Blocks.AIR.getBlockData() : this.j[j >> 4].getType(i & 15, j & 15, k & 15)) : Blocks.VOID_AIR.getBlockData();
    }

    public Fluid getFluid(BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();

        return j >= 0 && j < 256 && this.j[j >> 4] != Chunk.a ? this.j[j >> 4].b(i & 15, j & 15, k & 15) : FluidTypes.EMPTY.i();
    }

    public List<BlockPosition> j() {
        return this.l;
    }

    public ShortList[] p() {
        ShortList[] ashortlist = new ShortList[16];
        Iterator iterator = this.l.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();

            a(ashortlist, blockposition.getY() >> 4).add(i(blockposition));
        }

        return ashortlist;
    }

    public void a(short short0, int i) {
        this.h(a(short0, i, this.b));
    }

    public void h(BlockPosition blockposition) {
        this.l.add(blockposition);
    }

    @Nullable
    public IBlockData setType(BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();

        if (j >= 0 && j < 256) {
            if (iblockdata.e() > 0) {
                this.l.add(new BlockPosition((i & 15) + this.getPos().d(), j, (k & 15) + this.getPos().e()));
            }

            if (this.j[j >> 4] == Chunk.a) {
                if (iblockdata.getBlock() == Blocks.AIR) {
                    return iblockdata;
                }

                this.j[j >> 4] = new ChunkSection(j >> 4 << 4, this.x());
            }

            IBlockData iblockdata1 = this.j[j >> 4].getType(i & 15, j & 15, k & 15);

            this.j[j >> 4].setType(i & 15, j & 15, k & 15, iblockdata);
            if (this.u) {
                this.c(HeightMap.Type.MOTION_BLOCKING).a(i & 15, j, k & 15, iblockdata);
                this.c(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES).a(i & 15, j, k & 15, iblockdata);
                this.c(HeightMap.Type.OCEAN_FLOOR).a(i & 15, j, k & 15, iblockdata);
                this.c(HeightMap.Type.WORLD_SURFACE).a(i & 15, j, k & 15, iblockdata);
            }

            return iblockdata1;
        } else {
            return Blocks.VOID_AIR.getBlockData();
        }
    }

    public void a(BlockPosition blockposition, TileEntity tileentity) {
        tileentity.setPosition(blockposition);
        this.h.put(blockposition, tileentity);
    }

    public Set<BlockPosition> q() {
        Set<BlockPosition> set = Sets.newHashSet(this.i.keySet());

        set.addAll(this.h.keySet());
        return set;
    }

    @Nullable
    public TileEntity getTileEntity(BlockPosition blockposition) {
        return (TileEntity) this.h.get(blockposition);
    }

    public Map<BlockPosition, TileEntity> r() {
        return this.h;
    }

    public void b(NBTTagCompound nbttagcompound) {
        this.k.add(nbttagcompound);
    }

    public void a(Entity entity) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        entity.d(nbttagcompound);
        this.b(nbttagcompound);
    }

    public List<NBTTagCompound> s() {
        return this.k;
    }

    public void a(BiomeBase[] abiomebase) {
        this.e = abiomebase;
    }

    public BiomeBase[] getBiomeIndex() {
        return this.e;
    }

    public void a(boolean flag) {
        this.c = flag;
    }

    public boolean h() {
        return this.c;
    }

    public ChunkStatus i() {
        return this.g;
    }

    public void a(ChunkStatus chunkstatus) {
        this.g = chunkstatus;
        this.a(true);
    }

    public void c(String s) {
        this.a(ChunkStatus.a(s));
    }

    public ChunkSection[] getSections() {
        return this.j;
    }

    public int a(EnumSkyBlock enumskyblock, BlockPosition blockposition, boolean flag) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getY();
        int k = blockposition.getZ() & 15;
        int l = j >> 4;

        if (l >= 0 && l <= this.j.length - 1) {
            ChunkSection chunksection = this.j[l];

            return chunksection == Chunk.a ? (this.c(blockposition) ? enumskyblock.c : 0) : (enumskyblock == EnumSkyBlock.SKY ? (!flag ? 0 : chunksection.c(i, j & 15, k)) : (enumskyblock == EnumSkyBlock.BLOCK ? chunksection.d(i, j & 15, k) : enumskyblock.c));
        } else {
            return 0;
        }
    }

    public int a(BlockPosition blockposition, int i, boolean flag) {
        int j = blockposition.getX() & 15;
        int k = blockposition.getY();
        int l = blockposition.getZ() & 15;
        int i1 = k >> 4;

        if (i1 >= 0 && i1 <= this.j.length - 1) {
            ChunkSection chunksection = this.j[i1];

            if (chunksection == Chunk.a) {
                return this.x() && i < EnumSkyBlock.SKY.c ? EnumSkyBlock.SKY.c - i : 0;
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

    public boolean c(BlockPosition blockposition) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getY();
        int k = blockposition.getZ() & 15;

        return j >= this.a(HeightMap.Type.MOTION_BLOCKING, i, k);
    }

    public void a(ChunkSection[] achunksection) {
        if (this.j.length != achunksection.length) {
            ProtoChunk.a.warn("Could not set level chunk sections, array length is {} instead of {}", achunksection.length, this.j.length);
        } else {
            System.arraycopy(achunksection, 0, this.j, 0, this.j.length);
        }
    }

    public Set<HeightMap.Type> t() {
        return this.f.keySet();
    }

    @Nullable
    public HeightMap b(HeightMap.Type heightmap_type) {
        return (HeightMap) this.f.get(heightmap_type);
    }

    public void a(HeightMap.Type heightmap_type, long[] along) {
        this.c(heightmap_type).a(along);
    }

    public void a(HeightMap.Type... aheightmap_type) {
        HeightMap.Type[] aheightmap_type1 = aheightmap_type;
        int i = aheightmap_type.length;

        for (int j = 0; j < i; ++j) {
            HeightMap.Type heightmap_type = aheightmap_type1[j];

            this.c(heightmap_type);
        }

    }

    private HeightMap c(HeightMap.Type heightmap_type) {
        return (HeightMap) this.f.computeIfAbsent(heightmap_type, (heightmap_type1) -> {
            HeightMap heightmap = new HeightMap(this, heightmap_type1);

            heightmap.a();
            return heightmap;
        });
    }

    public int a(HeightMap.Type heightmap_type, int i, int j) {
        HeightMap heightmap = (HeightMap) this.f.get(heightmap_type);

        if (heightmap == null) {
            this.a(heightmap_type);
            heightmap = (HeightMap) this.f.get(heightmap_type);
        }

        return heightmap.a(i & 15, j & 15) - 1;
    }

    public ChunkCoordIntPair getPos() {
        return this.b;
    }

    public void setLastSaved(long i) {}

    @Nullable
    public StructureStart a(String s) {
        return (StructureStart) this.n.get(s);
    }

    public void a(String s, StructureStart structurestart) {
        this.n.put(s, structurestart);
        this.c = true;
    }

    public Map<String, StructureStart> e() {
        return Collections.unmodifiableMap(this.n);
    }

    public void a(Map<String, StructureStart> map) {
        this.n.clear();
        this.n.putAll(map);
        this.c = true;
    }

    @Nullable
    public LongSet b(String s) {
        return (LongSet) this.o.computeIfAbsent(s, (s1) -> {
            return new LongOpenHashSet();
        });
    }

    public void a(String s, long i) {
        ((LongSet) this.o.computeIfAbsent(s, (s1) -> {
            return new LongOpenHashSet();
        })).add(i);
        this.c = true;
    }

    public Map<String, LongSet> f() {
        return Collections.unmodifiableMap(this.o);
    }

    public void b(Map<String, LongSet> map) {
        this.o.clear();
        this.o.putAll(map);
        this.c = true;
    }

    public void a(EnumSkyBlock enumskyblock, boolean flag, BlockPosition blockposition, int i) {
        int j = blockposition.getX() & 15;
        int k = blockposition.getY();
        int l = blockposition.getZ() & 15;
        int i1 = k >> 4;

        if (i1 < 16 && i1 >= 0) {
            if (this.j[i1] == Chunk.a) {
                if (i == enumskyblock.c) {
                    return;
                }

                this.j[i1] = new ChunkSection(i1 << 4, this.x());
            }

            if (enumskyblock == EnumSkyBlock.SKY) {
                if (flag) {
                    this.j[i1].a(j, k & 15, l, i);
                }
            } else if (enumskyblock == EnumSkyBlock.BLOCK) {
                this.j[i1].b(j, k & 15, l, i);
            }

        }
    }

    public static short i(BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();
        int l = i & 15;
        int i1 = j & 15;
        int j1 = k & 15;

        return (short) (l | i1 << 4 | j1 << 8);
    }

    public static BlockPosition a(short short0, int i, ChunkCoordIntPair chunkcoordintpair) {
        int j = (short0 & 15) + (chunkcoordintpair.x << 4);
        int k = (short0 >>> 4 & 15) + (i << 4);
        int l = (short0 >>> 8 & 15) + (chunkcoordintpair.z << 4);

        return new BlockPosition(j, k, l);
    }

    public void e(BlockPosition blockposition) {
        if (!World.k(blockposition)) {
            a(this.m, blockposition.getY() >> 4).add(i(blockposition));
        }

    }

    public ShortList[] u() {
        return this.m;
    }

    public void b(short short0, int i) {
        a(this.m, i).add(short0);
    }

    public ProtoChunkTickList<Block> k() {
        return this.q;
    }

    public ProtoChunkTickList<FluidType> l() {
        return this.r;
    }

    private boolean x() {
        return true;
    }

    public ChunkConverter v() {
        return this.p;
    }

    public void b(long i) {
        this.s = i;
    }

    public long m() {
        return this.s;
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.i.put(new BlockPosition(nbttagcompound.getInt("x"), nbttagcompound.getInt("y"), nbttagcompound.getInt("z")), nbttagcompound);
    }

    public Map<BlockPosition, NBTTagCompound> w() {
        return Collections.unmodifiableMap(this.i);
    }

    public NBTTagCompound g(BlockPosition blockposition) {
        return (NBTTagCompound) this.i.get(blockposition);
    }

    public void d(BlockPosition blockposition) {
        this.h.remove(blockposition);
        this.i.remove(blockposition);
    }

    public BitSet a(WorldGenStage.Features worldgenstage_features) {
        return (BitSet) this.t.computeIfAbsent(worldgenstage_features, (worldgenstage_features1) -> {
            return new BitSet(65536);
        });
    }

    public void a(WorldGenStage.Features worldgenstage_features, BitSet bitset) {
        this.t.put(worldgenstage_features, bitset);
    }

    public void a(int i) {
        this.d.addAndGet(i);
    }

    public boolean ab_() {
        return this.d.get() > 0;
    }

    public void b(boolean flag) {
        this.u = flag;
    }
}
