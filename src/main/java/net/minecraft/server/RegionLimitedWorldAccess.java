package net.minecraft.server;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionLimitedWorldAccess implements GeneratorAccess {

    private static final Logger LOGGER = LogManager.getLogger();
    private final List<IChunkAccess> b;
    private final int c;
    private final int d;
    private final int e;
    private final WorldServer f;
    private final long g;
    private final int h;
    private final WorldData i;
    private final Random j;
    private final WorldProvider k;
    private final GeneratorSettingsDefault l;
    private final TickList<Block> m = new TickListWorldGen<>((blockposition) -> {
        return this.w(blockposition).n();
    });
    private final TickList<FluidType> n = new TickListWorldGen<>((blockposition) -> {
        return this.w(blockposition).o();
    });

    public RegionLimitedWorldAccess(WorldServer worldserver, List<IChunkAccess> list) {
        int i = MathHelper.floor(Math.sqrt((double) list.size()));

        if (i * i != list.size()) {
            throw new IllegalStateException("Cache size is not a square.");
        } else {
            ChunkCoordIntPair chunkcoordintpair = ((IChunkAccess) list.get(list.size() / 2)).getPos();

            this.b = list;
            this.c = chunkcoordintpair.x;
            this.d = chunkcoordintpair.z;
            this.e = i;
            this.f = worldserver;
            this.g = worldserver.getSeed();
            this.l = worldserver.getChunkProvider().getChunkGenerator().getSettings();
            this.h = worldserver.getSeaLevel();
            this.i = worldserver.getWorldData();
            this.j = worldserver.getRandom();
            this.k = worldserver.getWorldProvider();
        }
    }

    public int a() {
        return this.c;
    }

    public int b() {
        return this.d;
    }

    @Override
    public IChunkAccess getChunkAt(int i, int j) {
        return this.getChunkAt(i, j, ChunkStatus.EMPTY);
    }

    @Nullable
    @Override
    public IChunkAccess getChunkAt(int i, int j, ChunkStatus chunkstatus, boolean flag) {
        IChunkAccess ichunkaccess;

        if (this.isChunkLoaded(i, j)) {
            ChunkCoordIntPair chunkcoordintpair = ((IChunkAccess) this.b.get(0)).getPos();
            int k = i - chunkcoordintpair.x;
            int l = j - chunkcoordintpair.z;

            ichunkaccess = (IChunkAccess) this.b.get(k + l * this.e);
            if (ichunkaccess.getChunkStatus().b(chunkstatus)) {
                return ichunkaccess;
            }
        } else {
            ichunkaccess = null;
        }

        if (!flag) {
            return null;
        } else {
            IChunkAccess ichunkaccess1 = (IChunkAccess) this.b.get(0);
            IChunkAccess ichunkaccess2 = (IChunkAccess) this.b.get(this.b.size() - 1);

            RegionLimitedWorldAccess.LOGGER.error("Requested chunk : {} {}", i, j);
            RegionLimitedWorldAccess.LOGGER.error("Region bounds : {} {} | {} {}", ichunkaccess1.getPos().x, ichunkaccess1.getPos().z, ichunkaccess2.getPos().x, ichunkaccess2.getPos().z);
            if (ichunkaccess != null) {
                throw new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", chunkstatus, ichunkaccess.getChunkStatus(), i, j));
            } else {
                throw new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", i, j));
            }
        }
    }

    @Override
    public boolean isChunkLoaded(int i, int j) {
        IChunkAccess ichunkaccess = (IChunkAccess) this.b.get(0);
        IChunkAccess ichunkaccess1 = (IChunkAccess) this.b.get(this.b.size() - 1);

        return i >= ichunkaccess.getPos().x && i <= ichunkaccess1.getPos().x && j >= ichunkaccess.getPos().z && j <= ichunkaccess1.getPos().z;
    }

    // Paper start - if loaded util
    @Nullable
    @Override
    public IChunkAccess getChunkIfLoadedImmediately(int x, int z) {
        return this.getChunkAt(x, z, ChunkStatus.FULL, false);
    }

    @Override
    public IBlockData getTypeIfLoaded(BlockPosition blockposition) {
        IChunkAccess chunk = this.getChunkIfLoadedImmediately(blockposition.getX() >> 4, blockposition.getZ() >> 4);
        return chunk == null ? null : chunk.getType(blockposition);
    }

    @Override
    public Fluid getFluidIfLoaded(BlockPosition blockposition) {
        IChunkAccess chunk = this.getChunkIfLoadedImmediately(blockposition.getX() >> 4, blockposition.getZ() >> 4);
        return chunk == null ? null : chunk.getFluid(blockposition);
    }
    // Paper end

    @Override
    public IBlockData getType(BlockPosition blockposition) {
        return this.getChunkAt(blockposition.getX() >> 4, blockposition.getZ() >> 4).getType(blockposition);
    }

    @Override
    public Fluid getFluid(BlockPosition blockposition) {
        return this.w(blockposition).getFluid(blockposition);
    }

    @Nullable
    @Override
    public EntityHuman a(double d0, double d1, double d2, double d3, Predicate<Entity> predicate) {
        return null;
    }

    @Override
    public int c() {
        return 0;
    }

    @Override
    public BiomeBase getBiome(BlockPosition blockposition) {
        BiomeBase biomebase = this.w(blockposition).getBiomeIndex()[blockposition.getX() & 15 | (blockposition.getZ() & 15) << 4];

        if (biomebase == null) {
            throw new RuntimeException(String.format("Biome is null @ %s", blockposition));
        } else {
            return biomebase;
        }
    }

    @Override
    public int getBrightness(EnumSkyBlock enumskyblock, BlockPosition blockposition) {
        return this.getChunkProvider().getLightEngine().a(enumskyblock).b(blockposition);
    }

    @Override
    public int getLightLevel(BlockPosition blockposition, int i) {
        return this.w(blockposition).a(blockposition, i, this.getWorldProvider().g());
    }

    @Override
    public boolean b(BlockPosition blockposition, boolean flag) {
        IBlockData iblockdata = this.getType(blockposition);

        if (iblockdata.isAir()) {
            return false;
        } else {
            if (flag) {
                TileEntity tileentity = iblockdata.getBlock().isTileEntity() ? this.getTileEntity(blockposition) : null;

                Block.a(iblockdata, (World) this.f, blockposition, tileentity);
            }

            return this.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 3);
        }
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPosition blockposition) {
        IChunkAccess ichunkaccess = this.w(blockposition);
        TileEntity tileentity = ichunkaccess.getTileEntity(blockposition);

        if (tileentity != null) {
            return tileentity;
        } else {
            NBTTagCompound nbttagcompound = ichunkaccess.i(blockposition);

            if (nbttagcompound != null) {
                if ("DUMMY".equals(nbttagcompound.getString("id"))) {
                    Block block = this.getType(blockposition).getBlock();

                    if (!(block instanceof ITileEntity)) {
                        return null;
                    }

                    tileentity = ((ITileEntity) block).createTile(this.f);
                } else {
                    tileentity = TileEntity.create(nbttagcompound);
                }

                if (tileentity != null) {
                    ichunkaccess.setTileEntity(blockposition, tileentity);
                    return tileentity;
                }
            }

            if (ichunkaccess.getType(blockposition).getBlock() instanceof ITileEntity) {
                RegionLimitedWorldAccess.LOGGER.warn("Tried to access a block entity before it was created. {}", blockposition);
            }

            return null;
        }
    }

    @Override
    public boolean setTypeAndData(BlockPosition blockposition, IBlockData iblockdata, int i) {
        IChunkAccess ichunkaccess = this.w(blockposition);
        IBlockData iblockdata1 = ichunkaccess.setType(blockposition, iblockdata, false);

        if (iblockdata1 != null) {
            this.f.a(blockposition, iblockdata1, iblockdata);
        }

        Block block = iblockdata.getBlock();

        if (block.isTileEntity()) {
            if (ichunkaccess.getChunkStatus().getType() == ChunkStatus.Type.LEVELCHUNK) {
                ichunkaccess.setTileEntity(blockposition, ((ITileEntity) block).createTile(this));
            } else {
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                nbttagcompound.setInt("x", blockposition.getX());
                nbttagcompound.setInt("y", blockposition.getY());
                nbttagcompound.setInt("z", blockposition.getZ());
                nbttagcompound.setString("id", "DUMMY");
                ichunkaccess.a(nbttagcompound);
            }
        } else if (iblockdata1 != null && iblockdata1.getBlock().isTileEntity()) {
            ichunkaccess.removeTileEntity(blockposition);
        }

        if (iblockdata.n(this, blockposition)) {
            this.i(blockposition);
        }

        return true;
    }

    private void i(BlockPosition blockposition) {
        this.w(blockposition).f(blockposition);
    }

    @Override
    public boolean addEntity(Entity entity) {
        // CraftBukkit start
        return addEntity(entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.DEFAULT);
    }

    @Override
    public boolean addEntity(Entity entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason) {
        // CraftBukkit end
        int i = MathHelper.floor(entity.locX / 16.0D);
        int j = MathHelper.floor(entity.locZ / 16.0D);

        this.getChunkAt(i, j).a(entity);
        return true;
    }

    @Override
    public boolean a(BlockPosition blockposition, boolean flag) {
        return this.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 3);
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.f.getWorldBorder();
    }

    @Override
    public boolean a(@Nullable Entity entity, VoxelShape voxelshape) {
        return true;
    }

    @Override
    public boolean e() {
        return false;
    }

    @Deprecated
    @Override
    public WorldServer getMinecraftWorld() {
        return this.f;
    }

    @Override
    public WorldData getWorldData() {
        return this.i;
    }

    @Override
    public DifficultyDamageScaler getDamageScaler(BlockPosition blockposition) {
        if (!this.isChunkLoaded(blockposition.getX() >> 4, blockposition.getZ() >> 4)) {
            throw new RuntimeException("We are asking a region for a chunk out of bound");
        } else {
            return new DifficultyDamageScaler(this.f.getDifficulty(), this.f.getDayTime(), 0L, this.f.aa());
        }
    }

    @Override
    public IChunkProvider getChunkProvider() {
        return this.f.getChunkProvider();
    }

    @Override
    public long getSeed() {
        return this.g;
    }

    @Override
    public TickList<Block> getBlockTickList() {
        return this.m;
    }

    @Override
    public TickList<FluidType> getFluidTickList() {
        return this.n;
    }

    @Override
    public int getSeaLevel() {
        return this.h;
    }

    @Override
    public Random getRandom() {
        return this.j;
    }

    @Override
    public void update(BlockPosition blockposition, Block block) {}

    @Override
    public int a(HeightMap.Type heightmap_type, int i, int j) {
        return this.getChunkAt(i >> 4, j >> 4).a(heightmap_type, i & 15, j & 15) + 1;
    }

    @Override
    public void playSound(@Nullable EntityHuman entityhuman, BlockPosition blockposition, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1) {}

    @Override
    public void addParticle(ParticleParam particleparam, double d0, double d1, double d2, double d3, double d4, double d5) {}

    @Override
    public void a(@Nullable EntityHuman entityhuman, int i, BlockPosition blockposition, int j) {}

    @Override
    public WorldProvider getWorldProvider() {
        return this.k;
    }

    @Override
    public boolean a(BlockPosition blockposition, Predicate<IBlockData> predicate) {
        return predicate.test(this.getType(blockposition));
    }

    @Override
    public <T extends Entity> List<T> a(Class<? extends T> oclass, AxisAlignedBB axisalignedbb, @Nullable Predicate<? super T> predicate) {
        return Collections.emptyList();
    }

    @Override
    public List<Entity> getEntities(@Nullable Entity entity, AxisAlignedBB axisalignedbb, @Nullable Predicate<? super Entity> predicate) {
        return Collections.emptyList();
    }

    @Override
    public List<EntityHuman> getPlayers() {
        return Collections.emptyList();
    }

    @Override
    public BlockPosition getHighestBlockYAt(HeightMap.Type heightmap_type, BlockPosition blockposition) {
        return new BlockPosition(blockposition.getX(), this.a(heightmap_type, blockposition.getX(), blockposition.getZ()), blockposition.getZ());
    }
}
