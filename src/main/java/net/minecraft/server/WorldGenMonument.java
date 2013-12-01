package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WorldGenMonument extends StructureGenerator<WorldGenMonumentConfiguration> {

    private static final List<BiomeBase.BiomeMeta> b = Lists.newArrayList(new BiomeBase.BiomeMeta[] { new BiomeBase.BiomeMeta(EntityTypes.GUARDIAN, 1, 2, 4)});

    public WorldGenMonument() {}

    protected ChunkCoordIntPair a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j, int k, int l) {
        int i1 = chunkgenerator.getSettings().c();
        int j1 = chunkgenerator.getSettings().d();
        int k1 = i + i1 * k;
        int l1 = j + i1 * l;
        int i2 = k1 < 0 ? k1 - i1 + 1 : k1;
        int j2 = l1 < 0 ? l1 - i1 + 1 : l1;
        int k2 = i2 / i1;
        int l2 = j2 / i1;

        ((SeededRandom) random).a(chunkgenerator.getSeed(), k2, l2, 10387313);
        k2 *= i1;
        l2 *= i1;
        k2 += (random.nextInt(i1 - j1) + random.nextInt(i1 - j1)) / 2;
        l2 += (random.nextInt(i1 - j1) + random.nextInt(i1 - j1)) / 2;
        return new ChunkCoordIntPair(k2, l2);
    }

    protected boolean a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j) {
        ChunkCoordIntPair chunkcoordintpair = this.a(chunkgenerator, random, i, j, 0, 0);

        if (i == chunkcoordintpair.x && j == chunkcoordintpair.z) {
            Set<BiomeBase> set = chunkgenerator.getWorldChunkManager().a(i * 16 + 9, j * 16 + 9, 16);
            Iterator iterator = set.iterator();

            BiomeBase biomebase;

            do {
                if (!iterator.hasNext()) {
                    Set<BiomeBase> set1 = chunkgenerator.getWorldChunkManager().a(i * 16 + 9, j * 16 + 9, 29);
                    Iterator iterator1 = set1.iterator();

                    BiomeBase biomebase1;

                    do {
                        if (!iterator1.hasNext()) {
                            return true;
                        }

                        biomebase1 = (BiomeBase) iterator1.next();
                    } while (biomebase1.p() == BiomeBase.Geography.OCEAN || biomebase1.p() == BiomeBase.Geography.RIVER);

                    return false;
                }

                biomebase = (BiomeBase) iterator.next();
            } while (chunkgenerator.canSpawnStructure(biomebase, WorldGenerator.n));

            return false;
        } else {
            return false;
        }
    }

    protected boolean a(GeneratorAccess generatoraccess) {
        return generatoraccess.getWorldData().shouldGenerateMapFeatures();
    }

    protected StructureStart a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j) {
        BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9), Biomes.b);

        return new WorldGenMonument.a(generatoraccess, seededrandom, i, j, biomebase);
    }

    protected String a() {
        return "Monument";
    }

    public int b() {
        return 8;
    }

    public List<BiomeBase.BiomeMeta> d() {
        return WorldGenMonument.b;
    }

    public static class a extends StructureStart {

        private final Set<ChunkCoordIntPair> e = Sets.newHashSet();
        private boolean f;

        public a() {}

        public a(GeneratorAccess generatoraccess, SeededRandom seededrandom, int i, int j, BiomeBase biomebase) {
            super(i, j, biomebase, seededrandom, generatoraccess.getSeed());
            this.b(generatoraccess, seededrandom, i, j);
        }

        private void b(IBlockAccess iblockaccess, Random random, int i, int j) {
            int k = i * 16 - 29;
            int l = j * 16 - 29;
            EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);

            this.a.add(new WorldGenMonumentPieces.WorldGenMonumentPiece1(random, k, l, enumdirection));
            this.a(iblockaccess);
            this.f = true;
        }

        public void a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            if (!this.f) {
                this.a.clear();
                this.b(generatoraccess, random, this.e(), this.f());
            }

            super.a(generatoraccess, random, structureboundingbox, chunkcoordintpair);
        }

        public void b(ChunkCoordIntPair chunkcoordintpair) {
            super.b(chunkcoordintpair);
            this.e.add(chunkcoordintpair);
        }

        public void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = this.e.iterator();

            while (iterator.hasNext()) {
                ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) iterator.next();
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.setInt("X", chunkcoordintpair.x);
                nbttagcompound1.setInt("Z", chunkcoordintpair.z);
                nbttaglist.add((NBTBase) nbttagcompound1);
            }

            nbttagcompound.set("Processed", nbttaglist);
        }

        public void b(NBTTagCompound nbttagcompound) {
            super.b(nbttagcompound);
            if (nbttagcompound.hasKeyOfType("Processed", 9)) {
                NBTTagList nbttaglist = nbttagcompound.getList("Processed", 10);

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);

                    this.e.add(new ChunkCoordIntPair(nbttagcompound1.getInt("X"), nbttagcompound1.getInt("Z")));
                }
            }

        }
    }
}
