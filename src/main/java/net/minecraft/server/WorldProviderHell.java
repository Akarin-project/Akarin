package net.minecraft.server;

import javax.annotation.Nullable;

public class WorldProviderHell extends WorldProvider {

    public WorldProviderHell(World world, DimensionManager dimensionmanager) {
        super(world, dimensionmanager);
        this.c = true;
        this.d = true;
    }

    @Override
    protected void a() {
        float f = 0.1F;

        for (int i = 0; i <= 15; ++i) {
            float f1 = 1.0F - (float) i / 15.0F;

            this.e[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * 0.9F + 0.1F;
        }

    }

    @Override
    public ChunkGenerator<?> getChunkGenerator() {
        GeneratorSettingsNether generatorsettingsnether = (GeneratorSettingsNether) ChunkGeneratorType.b.a();

        generatorsettingsnether.a(Blocks.NETHERRACK.getBlockData());
        generatorsettingsnether.b(Blocks.LAVA.getBlockData());
        return ChunkGeneratorType.b.create(this.b, BiomeLayout.b.a(((BiomeLayoutFixedConfiguration) BiomeLayout.b.a()).a(Biomes.NETHER)), generatorsettingsnether);
    }

    @Override
    public boolean isOverworld() {
        return false;
    }

    @Nullable
    @Override
    public BlockPosition a(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        return null;
    }

    @Nullable
    @Override
    public BlockPosition a(int i, int j, boolean flag) {
        return null;
    }

    @Override
    public float a(long i, float f) {
        return 0.5F;
    }

    @Override
    public boolean canRespawn() {
        return false;
    }

    @Override
    public WorldBorder getWorldBorder() {
        return new WorldBorder() {
            @Override
            public double getCenterX() {
                return super.getCenterX(); // CraftBukkit
            }

            @Override
            public double getCenterZ() {
                return super.getCenterZ(); // CraftBukkit
            }
        };
    }

    // CraftBukkit start
    /*
    @Override
    public DimensionManager getDimensionManager() {
        return DimensionManager.NETHER;
    }
    */
    // CraftBukkit end
}
