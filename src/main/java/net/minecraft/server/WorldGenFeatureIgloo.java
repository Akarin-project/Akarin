package net.minecraft.server;

public class WorldGenFeatureIgloo extends WorldGenFeatureRandomScattered<WorldGenFeatureIglooConfiguration> {

    public WorldGenFeatureIgloo() {}

    protected String a() {
        return "Igloo";
    }

    public int b() {
        return 3;
    }

    protected StructureStart a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j) {
        BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9), Biomes.PLAINS);

        return new WorldGenFeatureIgloo.a(generatoraccess, chunkgenerator, seededrandom, i, j, biomebase);
    }

    protected int c() {
        return 14357618;
    }

    public static class a extends StructureStart {

        public a() {}

        public a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j, BiomeBase biomebase) {
            super(i, j, biomebase, seededrandom, generatoraccess.getSeed());
            WorldGenFeatureIglooConfiguration worldgenfeatureiglooconfiguration = (WorldGenFeatureIglooConfiguration) chunkgenerator.getFeatureConfiguration(biomebase, WorldGenerator.j);
            int k = i * 16;
            int l = j * 16;
            BlockPosition blockposition = new BlockPosition(k, 90, l);
            EnumBlockRotation enumblockrotation = EnumBlockRotation.values()[seededrandom.nextInt(EnumBlockRotation.values().length)];
            DefinedStructureManager definedstructuremanager = generatoraccess.getDataManager().h();

            WorldGenIglooPiece.a(definedstructuremanager, blockposition, enumblockrotation, this.a, seededrandom, worldgenfeatureiglooconfiguration);
            this.a((IBlockAccess) generatoraccess);
        }
    }
}
