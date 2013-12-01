package net.minecraft.server;

public class WorldGenFeatureShipwreck extends WorldGenFeatureRandomScattered<WorldGenFeatureShipwreckConfiguration> {

    public WorldGenFeatureShipwreck() {}

    protected String a() {
        return "Shipwreck";
    }

    public int b() {
        return 3;
    }

    protected StructureStart a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j) {
        BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9), (BiomeBase) null);

        return new WorldGenFeatureShipwreck.a(generatoraccess, chunkgenerator, seededrandom, i, j, biomebase);
    }

    protected int c() {
        return 165745295;
    }

    protected int a(ChunkGenerator<?> chunkgenerator) {
        return chunkgenerator.getSettings().j();
    }

    protected int b(ChunkGenerator<?> chunkgenerator) {
        return chunkgenerator.getSettings().k();
    }

    public static class a extends StructureStart {

        public a() {}

        public a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j, BiomeBase biomebase) {
            super(i, j, biomebase, seededrandom, generatoraccess.getSeed());
            WorldGenFeatureShipwreckConfiguration worldgenfeatureshipwreckconfiguration = (WorldGenFeatureShipwreckConfiguration) chunkgenerator.getFeatureConfiguration(biomebase, WorldGenerator.k);
            EnumBlockRotation enumblockrotation = EnumBlockRotation.values()[seededrandom.nextInt(EnumBlockRotation.values().length)];
            BlockPosition blockposition = new BlockPosition(i * 16, 90, j * 16);

            WorldGenShipwreck.a(generatoraccess.getDataManager().h(), blockposition, enumblockrotation, this.a, seededrandom, worldgenfeatureshipwreckconfiguration);
            this.a((IBlockAccess) generatoraccess);
        }
    }
}
