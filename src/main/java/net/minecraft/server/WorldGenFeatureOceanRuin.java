package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WorldGenFeatureOceanRuin extends WorldGenFeatureRandomScattered<WorldGenFeatureOceanRuinConfiguration> {

    public WorldGenFeatureOceanRuin(Function<Dynamic<?>, ? extends WorldGenFeatureOceanRuinConfiguration> function) {
        super(function);
    }

    @Override
    public String b() {
        return "Ocean_Ruin";
    }

    @Override
    public int c() {
        return 3;
    }

    @Override
    protected int a(ChunkGenerator<?> chunkgenerator) {
        return chunkgenerator.getSettings().l();
    }

    @Override
    protected int b(ChunkGenerator<?> chunkgenerator) {
        return chunkgenerator.getSettings().m();
    }

    @Override
    public StructureGenerator.a a() {
        return WorldGenFeatureOceanRuin.a::new;
    }

    @Override
    // Spigot start
    protected int getSeed(World world) {
        return world.spigotConfig.oceanSeed;
        // Spigot end
    }

    public static enum Temperature {

        WARM("warm"), COLD("cold");

        private static final Map<String, WorldGenFeatureOceanRuin.Temperature> c = (Map) Arrays.stream(values()).collect(Collectors.toMap(WorldGenFeatureOceanRuin.Temperature::a, (worldgenfeatureoceanruin_temperature) -> {
            return worldgenfeatureoceanruin_temperature;
        }));
        private final String d;

        private Temperature(String s) {
            this.d = s;
        }

        public String a() {
            return this.d;
        }

        public static WorldGenFeatureOceanRuin.Temperature a(String s) {
            return (WorldGenFeatureOceanRuin.Temperature) WorldGenFeatureOceanRuin.Temperature.c.get(s);
        }
    }

    public static class a extends StructureStart {

        public a(StructureGenerator<?> structuregenerator, int i, int j, BiomeBase biomebase, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, biomebase, structureboundingbox, k, l);
        }

        @Override
        public void a(ChunkGenerator<?> chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase) {
            WorldGenFeatureOceanRuinConfiguration worldgenfeatureoceanruinconfiguration = (WorldGenFeatureOceanRuinConfiguration) chunkgenerator.getFeatureConfiguration(biomebase, WorldGenerator.OCEAN_RUIN);
            int k = i * 16;
            int l = j * 16;
            BlockPosition blockposition = new BlockPosition(k, 90, l);
            EnumBlockRotation enumblockrotation = EnumBlockRotation.values()[this.d.nextInt(EnumBlockRotation.values().length)];

            WorldGenFeatureOceanRuinPieces.a(definedstructuremanager, blockposition, enumblockrotation, this.b, (Random) this.d, worldgenfeatureoceanruinconfiguration);
            this.b();
        }
    }
}
