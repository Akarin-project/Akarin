package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class WorldGenFeatureIgloo extends WorldGenFeatureRandomScattered<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureIgloo(Function<Dynamic<?>, ? extends WorldGenFeatureEmptyConfiguration> function) {
        super(function);
    }

    @Override
    public String b() {
        return "Igloo";
    }

    @Override
    public int c() {
        return 3;
    }

    @Override
    public StructureGenerator.a a() {
        return WorldGenFeatureIgloo.a::new;
    }

    @Override
    // Spigot start
    protected int getSeed(World world) {
        return world.spigotConfig.iglooSeed;
        // Spigot end
    }

    public static class a extends StructureStart {

        public a(StructureGenerator<?> structuregenerator, int i, int j, BiomeBase biomebase, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, biomebase, structureboundingbox, k, l);
        }

        @Override
        public void a(ChunkGenerator<?> chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase) {
            WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration = (WorldGenFeatureEmptyConfiguration) chunkgenerator.getFeatureConfiguration(biomebase, WorldGenerator.IGLOO);
            int k = i * 16;
            int l = j * 16;
            BlockPosition blockposition = new BlockPosition(k, 90, l);
            EnumBlockRotation enumblockrotation = EnumBlockRotation.values()[this.d.nextInt(EnumBlockRotation.values().length)];

            WorldGenIglooPiece.a(definedstructuremanager, blockposition, enumblockrotation, this.b, this.d, worldgenfeatureemptyconfiguration);
            this.b();
        }
    }
}
