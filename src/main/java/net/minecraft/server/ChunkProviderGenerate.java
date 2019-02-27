package net.minecraft.server;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderGenerate extends ChunkGeneratorAbstract<GeneratorSettingsOverworld> {

    private static final Logger f = LogManager.getLogger();
    private final NoiseGeneratorOctaves g;
    private final NoiseGeneratorOctaves h;
    private final NoiseGeneratorOctaves i;
    private final NoiseGenerator3 j;
    private final GeneratorSettingsOverworld k;
    private final NoiseGeneratorOctaves l;
    private final NoiseGeneratorOctaves m;
    private final WorldType n;
    private final float[] o;
    private final MobSpawnerPhantom p = new MobSpawnerPhantom();
    private final IBlockData q;
    private final IBlockData r;

    public ChunkProviderGenerate(GeneratorAccess generatoraccess, WorldChunkManager worldchunkmanager, GeneratorSettingsOverworld generatorsettingsoverworld) {
        super(generatoraccess, worldchunkmanager);
        this.n = generatoraccess.getWorldData().getType();
        SeededRandom seededrandom = new SeededRandom(this.b);

        this.g = new NoiseGeneratorOctaves(seededrandom, 16);
        this.h = new NoiseGeneratorOctaves(seededrandom, 16);
        this.i = new NoiseGeneratorOctaves(seededrandom, 8);
        this.j = new NoiseGenerator3(seededrandom, 4);
        this.l = new NoiseGeneratorOctaves(seededrandom, 10);
        this.m = new NoiseGeneratorOctaves(seededrandom, 16);
        this.o = new float[25];

        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f = 10.0F / MathHelper.c((float) (i * i + j * j) + 0.2F);

                this.o[i + 2 + (j + 2) * 5] = f;
            }
        }

        this.k = generatorsettingsoverworld;
        this.q = this.k.r();
        this.r = this.k.s();
    }

    public void createChunk(IChunkAccess ichunkaccess) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int i = chunkcoordintpair.x;
        int j = chunkcoordintpair.z;
        SeededRandom seededrandom = new SeededRandom();

        seededrandom.a(i, j);
        BiomeBase[] abiomebase = this.c.getBiomeBlock(i * 16, j * 16, 16, 16);

        ichunkaccess.a(abiomebase);
        this.a(i, j, ichunkaccess);
        ichunkaccess.a(HeightMap.Type.WORLD_SURFACE_WG, HeightMap.Type.OCEAN_FLOOR_WG);
        this.a(ichunkaccess, abiomebase, seededrandom, this.a.getSeaLevel());
        this.a(ichunkaccess, seededrandom);
        ichunkaccess.a(HeightMap.Type.WORLD_SURFACE_WG, HeightMap.Type.OCEAN_FLOOR_WG);
        ichunkaccess.a(ChunkStatus.BASE);
    }

    public void addMobs(RegionLimitedWorldAccess regionlimitedworldaccess) {
        int i = regionlimitedworldaccess.a();
        int j = regionlimitedworldaccess.b();
        BiomeBase biomebase = regionlimitedworldaccess.getChunkAt(i, j).getBiomeIndex()[0];
        SeededRandom seededrandom = new SeededRandom();

        seededrandom.a(regionlimitedworldaccess.getSeed(), i << 4, j << 4);
        SpawnerCreature.a(regionlimitedworldaccess, biomebase, i, j, seededrandom);
    }

    public void a(int i, int j, IChunkAccess ichunkaccess) {
        BiomeBase[] abiomebase = this.c.getBiomes(ichunkaccess.getPos().x * 4 - 2, ichunkaccess.getPos().z * 4 - 2, 10, 10);
        double[] adouble = new double[825];

        this.a(abiomebase, ichunkaccess.getPos().x * 4, 0, ichunkaccess.getPos().z * 4, adouble);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int k = 0; k < 4; ++k) {
            int l = k * 5;
            int i1 = (k + 1) * 5;

            for (int j1 = 0; j1 < 4; ++j1) {
                int k1 = (l + j1) * 33;
                int l1 = (l + j1 + 1) * 33;
                int i2 = (i1 + j1) * 33;
                int j2 = (i1 + j1 + 1) * 33;

                for (int k2 = 0; k2 < 32; ++k2) {
                    double d0 = 0.125D;
                    double d1 = adouble[k1 + k2];
                    double d2 = adouble[l1 + k2];
                    double d3 = adouble[i2 + k2];
                    double d4 = adouble[j2 + k2];
                    double d5 = (adouble[k1 + k2 + 1] - d1) * 0.125D;
                    double d6 = (adouble[l1 + k2 + 1] - d2) * 0.125D;
                    double d7 = (adouble[i2 + k2 + 1] - d3) * 0.125D;
                    double d8 = (adouble[j2 + k2 + 1] - d4) * 0.125D;

                    for (int l2 = 0; l2 < 8; ++l2) {
                        double d9 = 0.25D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * 0.25D;
                        double d13 = (d4 - d2) * 0.25D;

                        for (int i3 = 0; i3 < 4; ++i3) {
                            double d14 = 0.25D;
                            double d15 = (d11 - d10) * 0.25D;
                            double d16 = d10 - d15;

                            for (int j3 = 0; j3 < 4; ++j3) {
                                blockposition_mutableblockposition.c(k * 4 + i3, k2 * 8 + l2, j1 * 4 + j3);
                                if ((d16 += d15) > 0.0D) {
                                    ichunkaccess.setType(blockposition_mutableblockposition, this.q, false);
                                } else if (k2 * 8 + l2 < this.k.w()) {
                                    ichunkaccess.setType(blockposition_mutableblockposition, this.r, false);
                                }
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }

    }

    private void a(BiomeBase[] abiomebase, int i, int j, int k, double[] adouble) {
        double[] adouble1 = this.m.a(i, k, 5, 5, this.k.x(), this.k.y(), this.k.z());
        float f = this.k.A();
        float f1 = this.k.B();
        double[] adouble2 = this.i.a(i, j, k, 5, 33, 5, (double) (f / this.k.C()), (double) (f1 / this.k.D()), (double) (f / this.k.E()));
        double[] adouble3 = this.g.a(i, j, k, 5, 33, 5, (double) f, (double) f1, (double) f);
        double[] adouble4 = this.h.a(i, j, k, 5, 33, 5, (double) f, (double) f1, (double) f);
        int l = 0;
        int i1 = 0;

        for (int j1 = 0; j1 < 5; ++j1) {
            for (int k1 = 0; k1 < 5; ++k1) {
                float f2 = 0.0F;
                float f3 = 0.0F;
                float f4 = 0.0F;
                boolean flag = true;
                BiomeBase biomebase = abiomebase[j1 + 2 + (k1 + 2) * 10];

                for (int l1 = -2; l1 <= 2; ++l1) {
                    for (int i2 = -2; i2 <= 2; ++i2) {
                        BiomeBase biomebase1 = abiomebase[j1 + l1 + 2 + (k1 + i2 + 2) * 10];
                        float f5 = this.k.F() + biomebase1.h() * this.k.G();
                        float f6 = this.k.H() + biomebase1.l() * this.k.I();

                        if (this.n == WorldType.AMPLIFIED && f5 > 0.0F) {
                            f5 = 1.0F + f5 * 2.0F;
                            f6 = 1.0F + f6 * 4.0F;
                        }

                        float f7 = this.o[l1 + 2 + (i2 + 2) * 5] / (f5 + 2.0F);

                        if (biomebase1.h() > biomebase.h()) {
                            f7 /= 2.0F;
                        }

                        f2 += f6 * f7;
                        f3 += f5 * f7;
                        f4 += f7;
                    }
                }

                f2 /= f4;
                f3 /= f4;
                f2 = f2 * 0.9F + 0.1F;
                f3 = (f3 * 4.0F - 1.0F) / 8.0F;
                double d0 = adouble1[i1] / 8000.0D;

                if (d0 < 0.0D) {
                    d0 = -d0 * 0.3D;
                }

                d0 = d0 * 3.0D - 2.0D;
                if (d0 < 0.0D) {
                    d0 /= 2.0D;
                    if (d0 < -1.0D) {
                        d0 = -1.0D;
                    }

                    d0 /= 1.4D;
                    d0 /= 2.0D;
                } else {
                    if (d0 > 1.0D) {
                        d0 = 1.0D;
                    }

                    d0 /= 8.0D;
                }

                ++i1;
                double d1 = (double) f3;
                double d2 = (double) f2;

                d1 += d0 * 0.2D;
                d1 = d1 * this.k.J() / 8.0D;
                double d3 = this.k.J() + d1 * 4.0D;

                for (int j2 = 0; j2 < 33; ++j2) {
                    double d4 = ((double) j2 - d3) * this.k.K() * 128.0D / 256.0D / d2;

                    if (d4 < 0.0D) {
                        d4 *= 4.0D;
                    }

                    double d5 = adouble3[l] / this.k.L();
                    double d6 = adouble4[l] / this.k.M();
                    double d7 = (adouble2[l] / 10.0D + 1.0D) / 2.0D;
                    double d8 = MathHelper.b(d5, d6, d7) - d4;

                    if (j2 > 29) {
                        double d9 = (double) ((float) (j2 - 29) / 3.0F);

                        d8 = d8 * (1.0D - d9) - 10.0D * d9;
                    }

                    adouble[l] = d8;
                    ++l;
                }
            }
        }

    }

    public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        BiomeBase biomebase = this.a.getBiome(blockposition);

        return enumcreaturetype == EnumCreatureType.MONSTER && ((WorldGenFeatureSwampHut) WorldGenerator.l).d(this.a, blockposition) ? WorldGenerator.l.d() : (enumcreaturetype == EnumCreatureType.MONSTER && WorldGenerator.n.b(this.a, blockposition) ? WorldGenerator.n.d() : biomebase.getMobs(enumcreaturetype));
    }

    public int a(World world, boolean flag, boolean flag1) {
        byte b0 = 0;
        int i = b0 + this.p.a(world, flag, flag1);

        return i;
    }

    public GeneratorSettingsOverworld getSettings() {
        return this.k;
    }

    public double[] a(int i, int j) {
        double d0 = 0.03125D;

        return this.j.a((double) (i << 4), (double) (j << 4), 16, 16, 0.0625D, 0.0625D, 1.0D);
    }

    public int getSpawnHeight() {
        return this.a.getSeaLevel() + 1;
    }
}
