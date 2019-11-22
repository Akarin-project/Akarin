package net.minecraft.server;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Iterator;
import java.util.Random;

public abstract class ChunkGeneratorAbstract<T extends GeneratorSettingsDefault> extends ChunkGenerator<T> {

    private static final float[] h = (float[]) SystemUtils.a((new float[13824]), (afloat) -> {
        for (int i = 0; i < 24; ++i) {
            for (int j = 0; j < 24; ++j) {
                for (int k = 0; k < 24; ++k) {
                    afloat[i * 24 * 24 + j * 24 + k] = (float) b(j - 12, k - 12, i - 12);
                }
            }
        }

    });
    private static final IBlockData i = Blocks.AIR.getBlockData();
    private final int j;
    private final int k;
    private final int l;
    private final int m;
    private final int n;
    protected final SeededRandom e;
    private final NoiseGeneratorOctaves o;
    private final NoiseGeneratorOctaves p;
    private final NoiseGeneratorOctaves q;
    private final NoiseGenerator r;
    protected final IBlockData f;
    protected final IBlockData g;

    public ChunkGeneratorAbstract(GeneratorAccess generatoraccess, WorldChunkManager worldchunkmanager, int i, int j, int k, T t0, boolean flag) {
        super(generatoraccess, worldchunkmanager, t0);
        this.j = j;
        this.k = i;
        this.f = t0.r();
        this.g = t0.s();
        this.l = 16 / this.k;
        this.m = k / this.j;
        this.n = 16 / this.k;
        this.e = new SeededRandom(this.seed);
        this.o = new NoiseGeneratorOctaves(this.e, 16);
        this.p = new NoiseGeneratorOctaves(this.e, 16);
        this.q = new NoiseGeneratorOctaves(this.e, 8);
        this.r = (NoiseGenerator) (flag ? new NoiseGenerator3(this.e, 4) : new NoiseGeneratorOctaves(this.e, 4));
    }

    private double a(int i, int j, int k, double d0, double d1, double d2, double d3) {
        double d4 = 0.0D;
        double d5 = 0.0D;
        double d6 = 0.0D;
        double d7 = 1.0D;

        for (int l = 0; l < 16; ++l) {
            double d8 = NoiseGeneratorOctaves.a((double) i * d0 * d7);
            double d9 = NoiseGeneratorOctaves.a((double) j * d1 * d7);
            double d10 = NoiseGeneratorOctaves.a((double) k * d0 * d7);
            double d11 = d1 * d7;

            d4 += this.o.a(l).a(d8, d9, d10, d11, (double) j * d11) / d7;
            d5 += this.p.a(l).a(d8, d9, d10, d11, (double) j * d11) / d7;
            if (l < 8) {
                d6 += this.q.a(l).a(NoiseGeneratorOctaves.a((double) i * d2 * d7), NoiseGeneratorOctaves.a((double) j * d3 * d7), NoiseGeneratorOctaves.a((double) k * d2 * d7), d3 * d7, (double) j * d3 * d7) / d7;
            }

            d7 /= 2.0D;
        }

        return MathHelper.b(d4 / 512.0D, d5 / 512.0D, (d6 / 10.0D + 1.0D) / 2.0D);
    }

    protected double[] b(int i, int j) {
        double[] adouble = new double[this.m + 1];

        this.a(adouble, i, j);
        return adouble;
    }

    protected void a(double[] adouble, int i, int j, double d0, double d1, double d2, double d3, int k, int l) {
        double[] adouble1 = this.a(i, j);
        double d4 = adouble1[0];
        double d5 = adouble1[1];
        double d6 = this.g();
        double d7 = this.h();

        for (int i1 = 0; i1 < this.i(); ++i1) {
            double d8 = this.a(i, i1, j, d0, d1, d2, d3);

            d8 -= this.a(d4, d5, i1);
            if ((double) i1 > d6) {
                d8 = MathHelper.b(d8, (double) l, ((double) i1 - d6) / (double) k);
            } else if ((double) i1 < d7) {
                d8 = MathHelper.b(d8, -30.0D, (d7 - (double) i1) / (d7 - 1.0D));
            }

            adouble[i1] = d8;
        }

    }

    protected abstract double[] a(int i, int j);

    protected abstract double a(double d0, double d1, int i);

    protected double g() {
        return (double) (this.i() - 4);
    }

    protected double h() {
        return 0.0D;
    }

    @Override
    public int getBaseHeight(int i, int j, HeightMap.Type heightmap_type) {
        int k = Math.floorDiv(i, this.k);
        int l = Math.floorDiv(j, this.k);
        int i1 = Math.floorMod(i, this.k);
        int j1 = Math.floorMod(j, this.k);
        double d0 = (double) i1 / (double) this.k;
        double d1 = (double) j1 / (double) this.k;
        double[][] adouble = new double[][]{this.b(k, l), this.b(k, l + 1), this.b(k + 1, l), this.b(k + 1, l + 1)};
        int k1 = this.getSeaLevel();

        for (int l1 = this.m - 1; l1 >= 0; --l1) {
            double d2 = adouble[0][l1];
            double d3 = adouble[1][l1];
            double d4 = adouble[2][l1];
            double d5 = adouble[3][l1];
            double d6 = adouble[0][l1 + 1];
            double d7 = adouble[1][l1 + 1];
            double d8 = adouble[2][l1 + 1];
            double d9 = adouble[3][l1 + 1];

            for (int i2 = this.j - 1; i2 >= 0; --i2) {
                double d10 = (double) i2 / (double) this.j;
                double d11 = MathHelper.a(d10, d0, d1, d2, d6, d4, d8, d3, d7, d5, d9);
                int j2 = l1 * this.j + i2;

                if (d11 > 0.0D || j2 < k1) {
                    IBlockData iblockdata;

                    if (d11 > 0.0D) {
                        iblockdata = this.f;
                    } else {
                        iblockdata = this.g;
                    }

                    if (heightmap_type.d().test(iblockdata)) {
                        return j2 + 1;
                    }
                }
            }
        }

        return 0;
    }

    protected abstract void a(double[] adouble, int i, int j);

    public int i() {
        return this.m + 1;
    }

    @Override
    public void buildBase(IChunkAccess ichunkaccess) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int i = chunkcoordintpair.x;
        int j = chunkcoordintpair.z;
        SeededRandom seededrandom = new SeededRandom();

        seededrandom.a(i, j);
        ChunkCoordIntPair chunkcoordintpair1 = ichunkaccess.getPos();
        int k = chunkcoordintpair1.d();
        int l = chunkcoordintpair1.e();
        double d0 = 0.0625D;
        BiomeBase[] abiomebase = ichunkaccess.getBiomeIndex();

        for (int i1 = 0; i1 < 16; ++i1) {
            for (int j1 = 0; j1 < 16; ++j1) {
                int k1 = k + i1;
                int l1 = l + j1;
                int i2 = ichunkaccess.a(HeightMap.Type.WORLD_SURFACE_WG, i1, j1) + 1;
                double d1 = this.r.a((double) k1 * 0.0625D, (double) l1 * 0.0625D, 0.0625D, (double) i1 * 0.0625D);

                abiomebase[j1 * 16 + i1].a(seededrandom, ichunkaccess, k1, l1, i2, d1, this.getSettings().r(), this.getSettings().s(), this.getSeaLevel(), this.a.getSeed());
            }
        }

        this.a(ichunkaccess, seededrandom);
    }

    protected void a(IChunkAccess ichunkaccess, Random random) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int i = ichunkaccess.getPos().d();
        int j = ichunkaccess.getPos().e();
        T t0 = this.getSettings();
        int k = t0.u(); final int floorHeight = k; // Paper
        int l = t0.t(); final int roofHeight = l; // Paper
        Iterator iterator = BlockPosition.b(i, 0, j, i + 15, 0, j + 15).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();
            int i1;

            if (l > 0) {
                for (i1 = l; i1 >= l - 4; --i1) {
                    if (i1 >= (getWorld().paperConfig.generateFlatBedrock ? roofHeight : l - random.nextInt(5))) { // Paper - Configurable flat bedrock roof
                        ichunkaccess.setType(blockposition_mutableblockposition.d(blockposition.getX(), i1, blockposition.getZ()), Blocks.BEDROCK.getBlockData(), false);
                    }
                }
            }

            if (k < 256) {
                for (i1 = k + 4; i1 >= k; --i1) {
                    if (i1 <= (getWorld().paperConfig.generateFlatBedrock ? floorHeight : k + random.nextInt(5))) { // Paper - Configurable flat bedrock floor
                        ichunkaccess.setType(blockposition_mutableblockposition.d(blockposition.getX(), i1, blockposition.getZ()), Blocks.BEDROCK.getBlockData(), false);
                    }
                }
            }
        }

    }

    @Override
    public void buildNoise(GeneratorAccess generatoraccess, IChunkAccess ichunkaccess) {
        int i = this.getSeaLevel();
        ObjectList<WorldGenFeaturePillagerOutpostPoolPiece> objectlist = new ObjectArrayList(10);
        ObjectList<WorldGenFeatureDefinedStructureJigsawJunction> objectlist1 = new ObjectArrayList(32);
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int j = chunkcoordintpair.x;
        int k = chunkcoordintpair.z;
        int l = j << 4;
        int i1 = k << 4;
        Iterator iterator = WorldGenerator.aQ.iterator();

        while (iterator.hasNext()) {
            StructureGenerator<?> structuregenerator = (StructureGenerator) iterator.next();
            String s = structuregenerator.b();
            LongIterator longiterator = ichunkaccess.b(s).iterator();

            while (longiterator.hasNext()) {
                long j1 = longiterator.nextLong();
                ChunkCoordIntPair chunkcoordintpair1 = new ChunkCoordIntPair(j1);
                IChunkAccess ichunkaccess1 = generatoraccess.getChunkAt(chunkcoordintpair1.x, chunkcoordintpair1.z);
                StructureStart structurestart = ichunkaccess1.a(s);

                if (structurestart != null && structurestart.e()) {
                    Iterator iterator1 = structurestart.d().iterator();

                    while (iterator1.hasNext()) {
                        StructurePiece structurepiece = (StructurePiece) iterator1.next();

                        if (structurepiece.a(chunkcoordintpair, 12) && structurepiece instanceof WorldGenFeaturePillagerOutpostPoolPiece) {
                            WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece = (WorldGenFeaturePillagerOutpostPoolPiece) structurepiece;
                            WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching = worldgenfeaturepillageroutpostpoolpiece.b().c();

                            if (worldgenfeaturedefinedstructurepooltemplate_matching == WorldGenFeatureDefinedStructurePoolTemplate.Matching.RIGID) {
                                objectlist.add(worldgenfeaturepillageroutpostpoolpiece);
                            }

                            Iterator iterator2 = worldgenfeaturepillageroutpostpoolpiece.e().iterator();

                            while (iterator2.hasNext()) {
                                WorldGenFeatureDefinedStructureJigsawJunction worldgenfeaturedefinedstructurejigsawjunction = (WorldGenFeatureDefinedStructureJigsawJunction) iterator2.next();
                                int k1 = worldgenfeaturedefinedstructurejigsawjunction.a();
                                int l1 = worldgenfeaturedefinedstructurejigsawjunction.c();

                                if (k1 > l - 12 && l1 > i1 - 12 && k1 < l + 15 + 12 && l1 < i1 + 15 + 12) {
                                    objectlist1.add(worldgenfeaturedefinedstructurejigsawjunction);
                                }
                            }
                        }
                    }
                }
            }
        }

        double[][][] adouble = new double[2][this.n + 1][this.m + 1];

        for (int i2 = 0; i2 < this.n + 1; ++i2) {
            adouble[0][i2] = new double[this.m + 1];
            this.a(adouble[0][i2], j * this.l, k * this.n + i2);
            adouble[1][i2] = new double[this.m + 1];
        }

        ProtoChunk protochunk = (ProtoChunk) ichunkaccess;
        HeightMap heightmap = protochunk.b(HeightMap.Type.OCEAN_FLOOR_WG);
        HeightMap heightmap1 = protochunk.b(HeightMap.Type.WORLD_SURFACE_WG);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        ObjectListIterator<WorldGenFeaturePillagerOutpostPoolPiece> objectlistiterator = objectlist.iterator();
        ObjectListIterator<WorldGenFeatureDefinedStructureJigsawJunction> objectlistiterator1 = objectlist1.iterator();

        for (int j2 = 0; j2 < this.l; ++j2) {
            int k2;

            for (k2 = 0; k2 < this.n + 1; ++k2) {
                this.a(adouble[1][k2], j * this.l + j2 + 1, k * this.n + k2);
            }

            for (k2 = 0; k2 < this.n; ++k2) {
                ChunkSection chunksection = protochunk.a(15);

                chunksection.a();

                for (int l2 = this.m - 1; l2 >= 0; --l2) {
                    double d0 = adouble[0][k2][l2];
                    double d1 = adouble[0][k2 + 1][l2];
                    double d2 = adouble[1][k2][l2];
                    double d3 = adouble[1][k2 + 1][l2];
                    double d4 = adouble[0][k2][l2 + 1];
                    double d5 = adouble[0][k2 + 1][l2 + 1];
                    double d6 = adouble[1][k2][l2 + 1];
                    double d7 = adouble[1][k2 + 1][l2 + 1];

                    for (int i3 = this.j - 1; i3 >= 0; --i3) {
                        int j3 = l2 * this.j + i3;
                        int k3 = j3 & 15;
                        int l3 = j3 >> 4;

                        if (chunksection.getYPosition() >> 4 != l3) {
                            chunksection.b();
                            chunksection = protochunk.a(l3);
                            chunksection.a();
                        }

                        double d8 = (double) i3 / (double) this.j;
                        double d9 = MathHelper.d(d8, d0, d4);
                        double d10 = MathHelper.d(d8, d2, d6);
                        double d11 = MathHelper.d(d8, d1, d5);
                        double d12 = MathHelper.d(d8, d3, d7);

                        for (int i4 = 0; i4 < this.k; ++i4) {
                            int j4 = l + j2 * this.k + i4;
                            int k4 = j4 & 15;
                            double d13 = (double) i4 / (double) this.k;
                            double d14 = MathHelper.d(d13, d9, d10);
                            double d15 = MathHelper.d(d13, d11, d12);

                            for (int l4 = 0; l4 < this.k; ++l4) {
                                int i5 = i1 + k2 * this.k + l4;
                                int j5 = i5 & 15;
                                double d16 = (double) l4 / (double) this.k;
                                double d17 = MathHelper.d(d16, d14, d15);
                                double d18 = MathHelper.a(d17 / 200.0D, -1.0D, 1.0D);

                                int k5;
                                int l5;
                                int i6;

                                for (d18 = d18 / 2.0D - d18 * d18 * d18 / 24.0D; objectlistiterator.hasNext(); d18 += a(k5, l5, i6) * 0.8D) {
                                    WorldGenFeaturePillagerOutpostPoolPiece worldgenfeaturepillageroutpostpoolpiece1 = (WorldGenFeaturePillagerOutpostPoolPiece) objectlistiterator.next();
                                    StructureBoundingBox structureboundingbox = worldgenfeaturepillageroutpostpoolpiece1.g();

                                    k5 = Math.max(0, Math.max(structureboundingbox.a - j4, j4 - structureboundingbox.d));
                                    l5 = j3 - (structureboundingbox.b + worldgenfeaturepillageroutpostpoolpiece1.d());
                                    i6 = Math.max(0, Math.max(structureboundingbox.c - i5, i5 - structureboundingbox.f));
                                }

                                objectlistiterator.back(objectlist.size());

                                while (objectlistiterator1.hasNext()) {
                                    WorldGenFeatureDefinedStructureJigsawJunction worldgenfeaturedefinedstructurejigsawjunction1 = (WorldGenFeatureDefinedStructureJigsawJunction) objectlistiterator1.next();
                                    int j6 = j4 - worldgenfeaturedefinedstructurejigsawjunction1.a();

                                    k5 = j3 - worldgenfeaturedefinedstructurejigsawjunction1.b();
                                    l5 = i5 - worldgenfeaturedefinedstructurejigsawjunction1.c();
                                    d18 += a(j6, k5, l5) * 0.4D;
                                }

                                objectlistiterator1.back(objectlist1.size());
                                IBlockData iblockdata;

                                if (d18 > 0.0D) {
                                    iblockdata = this.f;
                                } else if (j3 < i) {
                                    iblockdata = this.g;
                                } else {
                                    iblockdata = ChunkGeneratorAbstract.i;
                                }

                                if (iblockdata != ChunkGeneratorAbstract.i) {
                                    if (iblockdata.h() != 0) {
                                        blockposition_mutableblockposition.d(j4, j3, i5);
                                        protochunk.k(blockposition_mutableblockposition);
                                    }

                                    chunksection.setType(k4, k3, j5, iblockdata, false);
                                    heightmap.a(k4, j3, j5, iblockdata);
                                    heightmap1.a(k4, j3, j5, iblockdata);
                                }
                            }
                        }
                    }
                }

                chunksection.b();
            }

            double[][] adouble1 = adouble[0];

            adouble[0] = adouble[1];
            adouble[1] = adouble1;
        }

    }

    private static double a(int i, int j, int k) {
        int l = i + 12;
        int i1 = j + 12;
        int j1 = k + 12;

        return l >= 0 && l < 24 ? (i1 >= 0 && i1 < 24 ? (j1 >= 0 && j1 < 24 ? (double) ChunkGeneratorAbstract.h[j1 * 24 * 24 + l * 24 + i1] : 0.0D) : 0.0D) : 0.0D;
    }

    private static double b(int i, int j, int k) {
        double d0 = (double) (i * i + k * k);
        double d1 = (double) j + 0.5D;
        double d2 = d1 * d1;
        double d3 = Math.pow(2.718281828459045D, -(d2 / 16.0D + d0 / 16.0D));
        double d4 = -d1 * MathHelper.i(d2 / 2.0D + d0 / 2.0D) / 2.0D;

        return d4 * d3;
    }
}
