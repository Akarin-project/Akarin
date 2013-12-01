package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class WorldGenVillage extends StructureGenerator<WorldGenFeatureVillageConfiguration> {

    public WorldGenVillage() {}

    public String a() {
        return "Village";
    }

    public int b() {
        return 8;
    }

    protected boolean a(GeneratorAccess generatoraccess) {
        return generatoraccess.getWorldData().shouldGenerateMapFeatures();
    }

    protected ChunkCoordIntPair a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j, int k, int l) {
        int i1 = chunkgenerator.getSettings().a();
        int j1 = chunkgenerator.getSettings().b();
        int k1 = i + i1 * k;
        int l1 = j + i1 * l;
        int i2 = k1 < 0 ? k1 - i1 + 1 : k1;
        int j2 = l1 < 0 ? l1 - i1 + 1 : l1;
        int k2 = i2 / i1;
        int l2 = j2 / i1;

        ((SeededRandom) random).a(chunkgenerator.getSeed(), k2, l2, 10387312);
        k2 *= i1;
        l2 *= i1;
        k2 += random.nextInt(i1 - j1);
        l2 += random.nextInt(i1 - j1);
        return new ChunkCoordIntPair(k2, l2);
    }

    protected boolean a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j) {
        ChunkCoordIntPair chunkcoordintpair = this.a(chunkgenerator, random, i, j, 0, 0);

        if (i == chunkcoordintpair.x && j == chunkcoordintpair.z) {
            BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9), Biomes.b);

            return chunkgenerator.canSpawnStructure(biomebase, WorldGenerator.e);
        } else {
            return false;
        }
    }

    protected StructureStart a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j) {
        BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9), Biomes.b);

        return new WorldGenVillage.a(generatoraccess, chunkgenerator, seededrandom, i, j, biomebase);
    }

    public static class a extends StructureStart {

        private boolean e;

        public a() {}

        public a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j, BiomeBase biomebase) {
            super(i, j, biomebase, seededrandom, generatoraccess.getSeed());
            WorldGenFeatureVillageConfiguration worldgenfeaturevillageconfiguration = (WorldGenFeatureVillageConfiguration) chunkgenerator.getFeatureConfiguration(biomebase, WorldGenerator.e);
            List<WorldGenVillagePieces.WorldGenVillagePieceWeight> list = WorldGenVillagePieces.a(seededrandom, worldgenfeaturevillageconfiguration.a);
            WorldGenVillagePieces.WorldGenVillageStartPiece worldgenvillagepieces_worldgenvillagestartpiece = new WorldGenVillagePieces.WorldGenVillageStartPiece(0, seededrandom, (i << 4) + 2, (j << 4) + 2, list, worldgenfeaturevillageconfiguration);

            this.a.add(worldgenvillagepieces_worldgenvillagestartpiece);
            worldgenvillagepieces_worldgenvillagestartpiece.a((StructurePiece) worldgenvillagepieces_worldgenvillagestartpiece, this.a, (Random) seededrandom);
            List<StructurePiece> list1 = worldgenvillagepieces_worldgenvillagestartpiece.e;
            List list2 = worldgenvillagepieces_worldgenvillagestartpiece.d;

            int k;

            while (!list1.isEmpty() || !list2.isEmpty()) {
                StructurePiece structurepiece;

                if (list1.isEmpty()) {
                    k = seededrandom.nextInt(list2.size());
                    structurepiece = (StructurePiece) list2.remove(k);
                    structurepiece.a((StructurePiece) worldgenvillagepieces_worldgenvillagestartpiece, this.a, (Random) seededrandom);
                } else {
                    k = seededrandom.nextInt(list1.size());
                    structurepiece = (StructurePiece) list1.remove(k);
                    structurepiece.a((StructurePiece) worldgenvillagepieces_worldgenvillagestartpiece, this.a, (Random) seededrandom);
                }
            }

            this.a((IBlockAccess) generatoraccess);
            k = 0;
            Iterator iterator = this.a.iterator();

            while (iterator.hasNext()) {
                StructurePiece structurepiece1 = (StructurePiece) iterator.next();

                if (!(structurepiece1 instanceof WorldGenVillagePieces.WorldGenVillageRoadPiece)) {
                    ++k;
                }
            }

            this.e = k > 2;
        }

        public boolean b() {
            return this.e;
        }

        public void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setBoolean("Valid", this.e);
        }

        public void b(NBTTagCompound nbttagcompound) {
            super.b(nbttagcompound);
            this.e = nbttagcompound.getBoolean("Valid");
        }
    }
}
