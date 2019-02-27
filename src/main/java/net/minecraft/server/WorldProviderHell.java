package net.minecraft.server;

import javax.annotation.Nullable;

public class WorldProviderHell extends WorldProvider {

    public WorldProviderHell() {}

    public void m() {
        this.c = true;
        this.d = true;
        this.e = false;
    }

    protected void a() {
        float f = 0.1F;

        for (int i = 0; i <= 15; ++i) {
            float f1 = 1.0F - (float) i / 15.0F;

            this.f[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * 0.9F + 0.1F;
        }

    }

    public ChunkGenerator<?> getChunkGenerator() {
        GeneratorSettingsNether generatorsettingsnether = (GeneratorSettingsNether) ChunkGeneratorType.b.b();

        generatorsettingsnether.a(Blocks.NETHERRACK.getBlockData());
        generatorsettingsnether.b(Blocks.LAVA.getBlockData());
        return ChunkGeneratorType.b.create(this.b, BiomeLayout.b.a(((BiomeLayoutFixedConfiguration) BiomeLayout.b.b()).a(Biomes.NETHER)), generatorsettingsnether);
    }

    public boolean isOverworld() {
        return false;
    }

    @Nullable
    public BlockPosition a(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        return null;
    }

    @Nullable
    public BlockPosition a(int i, int j, boolean flag) {
        return null;
    }

    public float a(long i, float f) {
        return 0.5F;
    }

    public boolean canRespawn() {
        return false;
    }

    public WorldBorder getWorldBorder() {
        return new WorldBorder() {
            public double getCenterX() {
                return super.getCenterX(); // CraftBukkit
            }

            public double getCenterZ() {
                return super.getCenterZ(); // CraftBukkit
            }
        };
    }

    public DimensionManager getDimensionManager() {
        return DimensionManager.NETHER;
    }
}
