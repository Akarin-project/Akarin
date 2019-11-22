package net.minecraft.server;

import java.util.List;

public class ChunkProviderGenerate extends ChunkGeneratorAbstract<GeneratorSettingsOverworld> {

    private static final float[] h = (float[]) SystemUtils.a((new float[25]), (afloat) -> { // CraftBukkit - decompile error
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f = 10.0F / MathHelper.c((float) (i * i + j * j) + 0.2F);

                afloat[i + 2 + (j + 2) * 5] = f;
            }
        }

    });
    private final NoiseGeneratorOctaves i;
    private final boolean j;
    private final MobSpawnerPhantom k = new MobSpawnerPhantom();
    private final MobSpawnerPatrol l = new MobSpawnerPatrol();
    private final MobSpawnerCat m = new MobSpawnerCat();
    private final VillageSiege n = new VillageSiege();

    public ChunkProviderGenerate(GeneratorAccess generatoraccess, WorldChunkManager worldchunkmanager, GeneratorSettingsOverworld generatorsettingsoverworld) {
        super(generatoraccess, worldchunkmanager, 4, 8, 256, generatorsettingsoverworld, true);
        this.e.a(2620);
        this.i = new NoiseGeneratorOctaves(this.e, 16);
        this.j = generatoraccess.getWorldData().getType() == WorldType.AMPLIFIED;
    }

    @Override
    public void addMobs(RegionLimitedWorldAccess regionlimitedworldaccess) {
        int i = regionlimitedworldaccess.a();
        int j = regionlimitedworldaccess.b();
        BiomeBase biomebase = regionlimitedworldaccess.getChunkAt(i, j).getBiomeIndex()[0];
        SeededRandom seededrandom = new SeededRandom();

        seededrandom.a(regionlimitedworldaccess.getSeed(), i << 4, j << 4);
        SpawnerCreature.a(regionlimitedworldaccess, biomebase, i, j, seededrandom);
    }

    @Override
    protected void a(double[] adouble, int i, int j) {
        double d0 = 684.4119873046875D;
        double d1 = 684.4119873046875D;
        double d2 = 8.555149841308594D;
        double d3 = 4.277574920654297D;
        boolean flag = true;
        boolean flag1 = true;

        this.a(adouble, i, j, 684.4119873046875D, 684.4119873046875D, 8.555149841308594D, 4.277574920654297D, 3, -10);
    }

    @Override
    protected double a(double d0, double d1, int i) {
        double d2 = 8.5D;
        double d3 = ((double) i - (8.5D + d0 * 8.5D / 8.0D * 4.0D)) * 12.0D * 128.0D / 256.0D / d1;

        if (d3 < 0.0D) {
            d3 *= 4.0D;
        }

        return d3;
    }

    @Override
    protected double[] a(int i, int j) {
        double[] adouble = new double[2];
        float f = 0.0F;
        float f1 = 0.0F;
        float f2 = 0.0F;
        boolean flag = true;
        float f3 = this.c.b(i, j).g();

        for (int k = -2; k <= 2; ++k) {
            for (int l = -2; l <= 2; ++l) {
                BiomeBase biomebase = this.c.b(i + k, j + l);
                float f4 = biomebase.g();
                float f5 = biomebase.k();

                if (this.j && f4 > 0.0F) {
                    f4 = 1.0F + f4 * 2.0F;
                    f5 = 1.0F + f5 * 4.0F;
                }
                // CraftBukkit start - fix MC-54738
                if (f4 < -1.8F) {
                    f4 = -1.8F;
                }
                // CraftBukkit end

                float f6 = ChunkProviderGenerate.h[k + 2 + (l + 2) * 5] / (f4 + 2.0F);

                if (biomebase.g() > f3) {
                    f6 /= 2.0F;
                }

                f += f5 * f6;
                f1 += f4 * f6;
                f2 += f6;
            }
        }

        f /= f2;
        f1 /= f2;
        f = f * 0.9F + 0.1F;
        f1 = (f1 * 4.0F - 1.0F) / 8.0F;
        adouble[0] = (double) f1 + this.c(i, j);
        adouble[1] = (double) f;
        return adouble;
    }

    private double c(int i, int j) {
        double d0 = this.i.a((double) (i * 200), 10.0D, (double) (j * 200), 1.0D, 0.0D, true) / 8000.0D;

        if (d0 < 0.0D) {
            d0 = -d0 * 0.3D;
        }

        d0 = d0 * 3.0D - 2.0D;
        if (d0 < 0.0D) {
            d0 /= 28.0D;
        } else {
            if (d0 > 1.0D) {
                d0 = 1.0D;
            }

            d0 /= 40.0D;
        }

        return d0;
    }

    @Override
    public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        if (WorldGenerator.SWAMP_HUT.c(this.a, blockposition)) {
            if (enumcreaturetype == EnumCreatureType.MONSTER) {
                return WorldGenerator.SWAMP_HUT.e();
            }

            if (enumcreaturetype == EnumCreatureType.CREATURE) {
                return WorldGenerator.SWAMP_HUT.f();
            }
        } else if (enumcreaturetype == EnumCreatureType.MONSTER) {
            if (WorldGenerator.PILLAGER_OUTPOST.a(this.a, blockposition)) {
                return WorldGenerator.PILLAGER_OUTPOST.e();
            }

            if (WorldGenerator.OCEAN_MONUMENT.a(this.a, blockposition)) {
                return WorldGenerator.OCEAN_MONUMENT.e();
            }
        }

        return super.getMobsFor(enumcreaturetype, blockposition);
    }

    @Override
    public void doMobSpawning(WorldServer worldserver, boolean flag, boolean flag1) {
        this.k.a(worldserver, flag, flag1);
        this.l.a(worldserver, flag, flag1);
        this.m.a(worldserver, flag, flag1);
        this.n.a(worldserver, flag, flag1);
    }

    @Override
    public int getSpawnHeight() {
        return this.a.getSeaLevel() + 1;
    }

    @Override
    public int getSeaLevel() {
        return 63;
    }
}
