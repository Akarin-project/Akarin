package net.minecraft.server;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenStronghold extends StructureGenerator<WorldGenFeatureStrongholdConfiguration> {

    private boolean b;
    private ChunkCoordIntPair[] c;
    private long d;

    public WorldGenStronghold() {}

    protected boolean a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j) {
        if (this.d != chunkgenerator.getSeed()) {
            this.c();
        }

        if (!this.b) {
            this.a(chunkgenerator);
            this.b = true;
        }

        ChunkCoordIntPair[] achunkcoordintpair = this.c;
        int k = achunkcoordintpair.length;

        for (int l = 0; l < k; ++l) {
            ChunkCoordIntPair chunkcoordintpair = achunkcoordintpair[l];

            if (i == chunkcoordintpair.x && j == chunkcoordintpair.z) {
                return true;
            }
        }

        return false;
    }

    private void c() {
        this.b = false;
        this.c = null;
    }

    protected boolean a(GeneratorAccess generatoraccess) {
        return generatoraccess.getWorldData().shouldGenerateMapFeatures();
    }

    protected StructureStart a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j) {
        BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9), Biomes.b);
        byte b0 = 0;
        int k = b0 + 1;

        WorldGenStronghold.a worldgenstronghold_a;

        for (worldgenstronghold_a = new WorldGenStronghold.a(generatoraccess, seededrandom, i, j, biomebase, b0); worldgenstronghold_a.d().isEmpty() || ((WorldGenStrongholdPieces.WorldGenStrongholdStart) worldgenstronghold_a.d().get(0)).b == null; worldgenstronghold_a = new WorldGenStronghold.a(generatoraccess, seededrandom, i, j, biomebase, k++)) {
            ;
        }

        return worldgenstronghold_a;
    }

    protected String a() {
        return "Stronghold";
    }

    public int b() {
        return 8;
    }

    @Nullable
    public BlockPosition getNearestGeneratedFeature(World world, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, BlockPosition blockposition, int i, boolean flag) {
        if (!chunkgenerator.getWorldChunkManager().a(this)) {
            return null;
        } else {
            if (this.d != world.getSeed()) {
                this.c();
            }

            if (!this.b) {
                this.a(chunkgenerator);
                this.b = true;
            }

            BlockPosition blockposition1 = null;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(0, 0, 0);
            double d0 = Double.MAX_VALUE;
            ChunkCoordIntPair[] achunkcoordintpair = this.c;
            int j = achunkcoordintpair.length;

            for (int k = 0; k < j; ++k) {
                ChunkCoordIntPair chunkcoordintpair = achunkcoordintpair[k];

                blockposition_mutableblockposition.c((chunkcoordintpair.x << 4) + 8, 32, (chunkcoordintpair.z << 4) + 8);
                double d1 = blockposition_mutableblockposition.n(blockposition);

                if (blockposition1 == null) {
                    blockposition1 = new BlockPosition(blockposition_mutableblockposition);
                    d0 = d1;
                } else if (d1 < d0) {
                    blockposition1 = new BlockPosition(blockposition_mutableblockposition);
                    d0 = d1;
                }
            }

            return blockposition1;
        }
    }

    private void a(ChunkGenerator<?> chunkgenerator) {
        this.d = chunkgenerator.getSeed();
        List<BiomeBase> list = Lists.newArrayList();
        Iterator iterator = IRegistry.BIOME.iterator();

        while (iterator.hasNext()) {
            BiomeBase biomebase = (BiomeBase) iterator.next();

            if (biomebase != null && chunkgenerator.canSpawnStructure(biomebase, WorldGenerator.m)) {
                list.add(biomebase);
            }
        }

        int i = chunkgenerator.getSettings().e();
        int j = chunkgenerator.getSettings().f();
        int k = chunkgenerator.getSettings().g();

        this.c = new ChunkCoordIntPair[j];
        int l = 0;
        Long2ObjectMap<StructureStart> long2objectmap = chunkgenerator.getStructureStartCache(this);

        synchronized (long2objectmap) {
            ObjectIterator objectiterator = long2objectmap.values().iterator();

            while (objectiterator.hasNext()) {
                StructureStart structurestart = (StructureStart) objectiterator.next();

                if (l < this.c.length) {
                    this.c[l++] = new ChunkCoordIntPair(structurestart.e(), structurestart.f());
                }
            }
        }

        Random random = new Random();

        random.setSeed(chunkgenerator.getSeed());
        double d0 = random.nextDouble() * 3.141592653589793D * 2.0D;
        int i1 = long2objectmap.size();

        if (i1 < this.c.length) {
            int j1 = 0;
            int k1 = 0;

            for (int l1 = 0; l1 < this.c.length; ++l1) {
                double d1 = (double) (4 * i + i * k1 * 6) + (random.nextDouble() - 0.5D) * (double) i * 2.5D;
                int i2 = (int) Math.round(Math.cos(d0) * d1);
                int j2 = (int) Math.round(Math.sin(d0) * d1);
                BlockPosition blockposition = chunkgenerator.getWorldChunkManager().a((i2 << 4) + 8, (j2 << 4) + 8, 112, list, random);

                if (blockposition != null) {
                    i2 = blockposition.getX() >> 4;
                    j2 = blockposition.getZ() >> 4;
                }

                if (l1 >= i1) {
                    this.c[l1] = new ChunkCoordIntPair(i2, j2);
                }

                d0 += 6.283185307179586D / (double) k;
                ++j1;
                if (j1 == k) {
                    ++k1;
                    j1 = 0;
                    k += 2 * k / (k1 + 1);
                    k = Math.min(k, this.c.length - l1);
                    d0 += random.nextDouble() * 3.141592653589793D * 2.0D;
                }
            }
        }

    }

    public static class a extends StructureStart {

        public a() {}

        public a(GeneratorAccess generatoraccess, SeededRandom seededrandom, int i, int j, BiomeBase biomebase, int k) {
            super(i, j, biomebase, seededrandom, generatoraccess.getSeed() + (long) k);
            WorldGenStrongholdPieces.b();
            WorldGenStrongholdPieces.WorldGenStrongholdStart worldgenstrongholdpieces_worldgenstrongholdstart = new WorldGenStrongholdPieces.WorldGenStrongholdStart(0, seededrandom, (i << 4) + 2, (j << 4) + 2);

            this.a.add(worldgenstrongholdpieces_worldgenstrongholdstart);
            worldgenstrongholdpieces_worldgenstrongholdstart.a((StructurePiece) worldgenstrongholdpieces_worldgenstrongholdstart, this.a, (Random) seededrandom);
            List list = worldgenstrongholdpieces_worldgenstrongholdstart.c;

            while (!list.isEmpty()) {
                int l = seededrandom.nextInt(list.size());
                StructurePiece structurepiece = (StructurePiece) list.remove(l);

                structurepiece.a((StructurePiece) worldgenstrongholdpieces_worldgenstrongholdstart, this.a, (Random) seededrandom);
            }

            this.a((IBlockAccess) generatoraccess);
            this.a(generatoraccess, seededrandom, 10);
        }
    }
}
