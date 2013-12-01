package net.minecraft.server;

public class WorldGenFeatureDesertPyramid extends WorldGenFeatureRandomScattered<WorldGenFeatureDesertPyramidConfiguration> {

    public WorldGenFeatureDesertPyramid() {}

    protected String a() {
        return "Desert_Pyramid";
    }

    public int b() {
        return 3;
    }

    protected StructureStart a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j) {
        BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9), Biomes.PLAINS);

        return new WorldGenFeatureDesertPyramid.a(generatoraccess, seededrandom, i, j, biomebase);
    }

    protected int c() {
        return 14357617;
    }

    public static class a extends StructureStart {

        public a() {}

        public a(GeneratorAccess generatoraccess, SeededRandom seededrandom, int i, int j, BiomeBase biomebase) {
            super(i, j, biomebase, seededrandom, generatoraccess.getSeed());
            WorldGenDesertPyramidPiece worldgendesertpyramidpiece = new WorldGenDesertPyramidPiece(seededrandom, i * 16, j * 16);

            this.a.add(worldgendesertpyramidpiece);
            this.a((IBlockAccess) generatoraccess);
        }
    }
}
