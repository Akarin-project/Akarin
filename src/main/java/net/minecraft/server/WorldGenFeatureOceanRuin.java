package net.minecraft.server;

import java.util.Random;

public class WorldGenFeatureOceanRuin extends WorldGenFeatureRandomScattered<WorldGenFeatureOceanRuinConfiguration> {

    public WorldGenFeatureOceanRuin() {}

    public String a() {
        return "Ocean_Ruin";
    }

    public int b() {
        return 3;
    }

    protected int a(ChunkGenerator<?> chunkgenerator) {
        return chunkgenerator.getSettings().l();
    }

    protected int b(ChunkGenerator<?> chunkgenerator) {
        return chunkgenerator.getSettings().m();
    }

    protected StructureStart a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j) {
        BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9), (BiomeBase) null);

        return new WorldGenFeatureOceanRuin.a(generatoraccess, chunkgenerator, seededrandom, i, j, biomebase);
    }

    protected int c() {
        return 14357621;
    }

    public static enum Temperature {

        WARM, COLD;

        private Temperature() {}
    }

    public static class a extends StructureStart {

        public a() {}

        public a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j, BiomeBase biomebase) {
            super(i, j, biomebase, seededrandom, generatoraccess.getSeed());
            WorldGenFeatureOceanRuinConfiguration worldgenfeatureoceanruinconfiguration = (WorldGenFeatureOceanRuinConfiguration) chunkgenerator.getFeatureConfiguration(biomebase, WorldGenerator.o);
            int k = i * 16;
            int l = j * 16;
            BlockPosition blockposition = new BlockPosition(k, 90, l);
            EnumBlockRotation enumblockrotation = EnumBlockRotation.values()[seededrandom.nextInt(EnumBlockRotation.values().length)];
            DefinedStructureManager definedstructuremanager = generatoraccess.getDataManager().h();

            WorldGenFeatureOceanRuinPieces.a(definedstructuremanager, blockposition, enumblockrotation, this.a, (Random) seededrandom, worldgenfeatureoceanruinconfiguration);
            this.a((IBlockAccess) generatoraccess);
        }
    }
}
