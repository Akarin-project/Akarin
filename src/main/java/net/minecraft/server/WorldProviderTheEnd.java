package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class WorldProviderTheEnd extends WorldProvider {

    public static final BlockPosition f = new BlockPosition(100, 50, 0);
    private final EnderDragonBattle g;

    public WorldProviderTheEnd(World world, DimensionManager dimensionmanager) {
        super(world, dimensionmanager);
        NBTTagCompound nbttagcompound = world.getWorldData().a(DimensionManager.THE_END);

        this.g = world instanceof WorldServer ? new EnderDragonBattle((WorldServer) world, nbttagcompound.getCompound("DragonFight")) : null;
    }

    @Override
    public ChunkGenerator<?> getChunkGenerator() {
        GeneratorSettingsEnd generatorsettingsend = (GeneratorSettingsEnd) ChunkGeneratorType.c.a();

        generatorsettingsend.a(Blocks.END_STONE.getBlockData());
        generatorsettingsend.b(Blocks.AIR.getBlockData());
        generatorsettingsend.a(this.d());
        return ChunkGeneratorType.c.create(this.b, BiomeLayout.d.a(((BiomeLayoutTheEndConfiguration) BiomeLayout.d.a()).a(this.b.getSeed())), generatorsettingsend);
    }

    @Override
    public float a(long i, float f) {
        return 0.5F; // Paper - fix MC-93764
    }

    @Override
    public boolean canRespawn() {
        return false;
    }

    @Override
    public boolean isOverworld() {
        return false;
    }

    @Nullable
    @Override
    public BlockPosition a(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        Random random = new Random(this.b.getSeed());
        BlockPosition blockposition = new BlockPosition(chunkcoordintpair.d() + random.nextInt(15), 0, chunkcoordintpair.g() + random.nextInt(15));

        return this.b.i(blockposition).getMaterial().isSolid() ? blockposition : null;
    }

    @Override
    public BlockPosition d() {
        return WorldProviderTheEnd.f;
    }

    @Nullable
    @Override
    public BlockPosition a(int i, int j, boolean flag) {
        return this.a(new ChunkCoordIntPair(i >> 4, j >> 4), flag);
    }

    // CraftBukkit start
    /*
    @Override
    public DimensionManager getDimensionManager() {
        return DimensionManager.THE_END;
    }
    */
    // CraftBukkit end

    @Override
    public void k() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        if (this.g != null) {
            nbttagcompound.set("DragonFight", this.g.a());
        }

        this.b.getWorldData().a(DimensionManager.THE_END, nbttagcompound);
    }

    @Override
    public void l() {
        if (this.g != null) {
            this.g.b();
        }

    }

    @Nullable
    public EnderDragonBattle q() {
        return this.g;
    }
}
