package net.minecraft.server;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionLimitedWorldAccess implements GeneratorAccess {

    private static final Logger a = LogManager.getLogger();
    private final ProtoChunk[] b;
    private final int c;
    private final int d;
    private final int e;
    private final int f;
    private final World g;
    private final long h;
    private final int i;
    private final WorldData j;
    private final Random k;
    private final WorldProvider l;
    private final GeneratorSettings m;
    private final TickList<Block> n = new TickListWorldGen<>((blockposition) -> {
        return this.y(blockposition).k();
    });
    private final TickList<FluidType> o = new TickListWorldGen<>((blockposition) -> {
        return this.y(blockposition).l();
    });

    public RegionLimitedWorldAccess(ProtoChunk[] aprotochunk, int i, int j, int k, int l, World world) {
        this.b = aprotochunk;
        this.c = k;
        this.d = l;
        this.e = i;
        this.f = j;
        this.g = world;
        this.h = world.getSeed();
        this.m = world.getChunkProvider().getChunkGenerator().getSettings();
        this.i = world.getSeaLevel();
        this.j = world.getWorldData();
        this.k = world.m();
        this.l = world.o();
    }

    public int a() {
        return this.c;
    }

    public int b() {
        return this.d;
    }

    public boolean a(int i, int j) {
        ProtoChunk protochunk = this.b[0];
        ProtoChunk protochunk1 = this.b[this.b.length - 1];

        return i >= protochunk.getPos().x && i <= protochunk1.getPos().x && j >= protochunk.getPos().z && j <= protochunk1.getPos().z;
    }

    public IChunkAccess getChunkAt(int i, int j) {
        if (this.a(i, j)) {
            int k = i - this.b[0].getPos().x;
            int l = j - this.b[0].getPos().z;

            return this.b[k + l * this.e];
        } else {
            ProtoChunk protochunk = this.b[0];
            ProtoChunk protochunk1 = this.b[this.b.length - 1];

            RegionLimitedWorldAccess.a.error("Requested chunk : {} {}", i, j);
            RegionLimitedWorldAccess.a.error("Region bounds : {} {} | {} {}", protochunk.getPos().x, protochunk.getPos().z, protochunk1.getPos().x, protochunk1.getPos().z);
            throw new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", i, j));
        }
    }

    public IBlockData getType(BlockPosition blockposition) {
        return this.y(blockposition).getType(blockposition);
    }

    public Fluid getFluid(BlockPosition blockposition) {
        return this.y(blockposition).getFluid(blockposition);
    }

    @Nullable
    public EntityHuman a(double d0, double d1, double d2, double d3, Predicate<Entity> predicate) {
        return null;
    }

    public int c() {
        return 0;
    }

    public boolean isEmpty(BlockPosition blockposition) {
        return this.getType(blockposition).isAir();
    }

    public BiomeBase getBiome(BlockPosition blockposition) {
        BiomeBase biomebase = this.y(blockposition).getBiomeIndex()[blockposition.getX() & 15 | (blockposition.getZ() & 15) << 4];

        if (biomebase == null) {
            throw new RuntimeException(String.format("Biome is null @ %s", blockposition));
        } else {
            return biomebase;
        }
    }

    public int getBrightness(EnumSkyBlock enumskyblock, BlockPosition blockposition) {
        IChunkAccess ichunkaccess = this.y(blockposition);

        return ichunkaccess.a(enumskyblock, blockposition, this.o().g());
    }

    public int getLightLevel(BlockPosition blockposition, int i) {
        return this.y(blockposition).a(blockposition, i, this.o().g());
    }

    public boolean isChunkLoaded(int i, int j, boolean flag) {
        return this.a(i, j);
    }

    public boolean setAir(BlockPosition blockposition, boolean flag) {
        IBlockData iblockdata = this.getType(blockposition);

        if (iblockdata.isAir()) {
            return false;
        } else {
            if (flag) {
                iblockdata.a(this.g, blockposition, 0);
            }

            return this.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 3);
        }
    }

    public boolean e(BlockPosition blockposition) {
        return this.y(blockposition).c(blockposition);
    }

    @Nullable
    public TileEntity getTileEntity(BlockPosition blockposition) {
        IChunkAccess ichunkaccess = this.y(blockposition);
        TileEntity tileentity = ichunkaccess.getTileEntity(blockposition);

        if (tileentity != null) {
            return tileentity;
        } else {
            NBTTagCompound nbttagcompound = ichunkaccess.g(blockposition);

            if (nbttagcompound != null) {
                if ("DUMMY".equals(nbttagcompound.getString("id"))) {
                    tileentity = ((ITileEntity) this.getType(blockposition).getBlock()).a(this.g);
                } else {
                    tileentity = TileEntity.create(nbttagcompound);
                }

                if (tileentity != null) {
                    ichunkaccess.a(blockposition, tileentity);
                    return tileentity;
                }
            }

            if (ichunkaccess.getType(blockposition).getBlock() instanceof ITileEntity) {
                RegionLimitedWorldAccess.a.warn("Tried to access a block entity before it was created. {}", blockposition);
            }

            return null;
        }
    }

    public boolean setTypeAndData(BlockPosition blockposition, IBlockData iblockdata, int i) {
        IChunkAccess ichunkaccess = this.y(blockposition);
        IBlockData iblockdata1 = ichunkaccess.setType(blockposition, iblockdata, false);
        Block block = iblockdata.getBlock();

        if (block.isTileEntity()) {
            if (ichunkaccess.i().d() == ChunkStatus.Type.LEVELCHUNK) {
                ichunkaccess.a(blockposition, ((ITileEntity) block).a(this));
            } else {
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                nbttagcompound.setInt("x", blockposition.getX());
                nbttagcompound.setInt("y", blockposition.getY());
                nbttagcompound.setInt("z", blockposition.getZ());
                nbttagcompound.setString("id", "DUMMY");
                ichunkaccess.a(nbttagcompound);
            }
        } else if (iblockdata1 != null && iblockdata1.getBlock().isTileEntity()) {
            ichunkaccess.d(blockposition);
        }

        if (iblockdata.l(this, blockposition)) {
            this.i(blockposition);
        }

        return true;
    }

    private void i(BlockPosition blockposition) {
        this.y(blockposition).e(blockposition);
    }

    public boolean addEntity(Entity entity) {
        int i = MathHelper.floor(entity.locX / 16.0D);
        int j = MathHelper.floor(entity.locZ / 16.0D);

        this.getChunkAt(i, j).a(entity);
        return true;
    }

    public boolean setAir(BlockPosition blockposition) {
        return this.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 3);
    }

    public void a(EnumSkyBlock enumskyblock, BlockPosition blockposition, int i) {
        this.y(blockposition).a(enumskyblock, this.l.g(), blockposition, i);
    }

    public WorldBorder getWorldBorder() {
        return this.g.getWorldBorder();
    }

    public boolean a(@Nullable Entity entity, VoxelShape voxelshape) {
        return true;
    }

    public int a(BlockPosition blockposition, EnumDirection enumdirection) {
        return this.getType(blockposition).b((IBlockAccess) this, blockposition, enumdirection);
    }

    public boolean e() {
        return false;
    }

    @Deprecated
    public World getMinecraftWorld() {
        return this.g;
    }

    public WorldData getWorldData() {
        return this.j;
    }

    public DifficultyDamageScaler getDamageScaler(BlockPosition blockposition) {
        if (!this.a(blockposition.getX() >> 4, blockposition.getZ() >> 4)) {
            throw new RuntimeException("We are asking a region for a chunk out of bound");
        } else {
            return new DifficultyDamageScaler(this.g.getDifficulty(), this.g.getDayTime(), 0L, this.g.ah());
        }
    }

    @Nullable
    public PersistentCollection h() {
        return this.g.h();
    }

    public IChunkProvider getChunkProvider() {
        return this.g.getChunkProvider();
    }

    public IDataManager getDataManager() {
        return this.g.getDataManager();
    }

    public long getSeed() {
        return this.h;
    }

    public TickList<Block> getBlockTickList() {
        return this.n;
    }

    public TickList<FluidType> getFluidTickList() {
        return this.o;
    }

    public int getSeaLevel() {
        return this.i;
    }

    public Random m() {
        return this.k;
    }

    public void update(BlockPosition blockposition, Block block) {}

    public int a(HeightMap.Type heightmap_type, int i, int j) {
        return this.getChunkAt(i >> 4, j >> 4).a(heightmap_type, i & 15, j & 15) + 1;
    }

    public void a(@Nullable EntityHuman entityhuman, BlockPosition blockposition, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1) {}

    public void addParticle(ParticleParam particleparam, double d0, double d1, double d2, double d3, double d4, double d5) {}

    public BlockPosition getSpawn() {
        return this.g.getSpawn();
    }

    public WorldProvider o() {
        return this.l;
    }
}
