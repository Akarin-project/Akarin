package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class WorldGenFeatureJunglePyramid extends WorldGenFeatureRandomScattered<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureJunglePyramid(Function<Dynamic<?>, ? extends WorldGenFeatureEmptyConfiguration> function) {
        super(function);
    }

    @Override
    public String b() {
        return "Jungle_Pyramid";
    }

    @Override
    public int c() {
        return 3;
    }

    @Override
    public StructureGenerator.a a() {
        return WorldGenFeatureJunglePyramid.a::new;
    }

    @Override
    // Spigot start
    protected int getSeed(World world) {
        return world.spigotConfig.jungleSeed;
        // Spigot end
    }

    public static class a extends StructureStart {

        public a(StructureGenerator<?> structuregenerator, int i, int j, BiomeBase biomebase, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, biomebase, structureboundingbox, k, l);
        }

        @Override
        public void a(ChunkGenerator<?> chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase) {
            WorldGenJunglePyramidPiece worldgenjunglepyramidpiece = new WorldGenJunglePyramidPiece(this.d, i * 16, j * 16);

            this.b.add(worldgenjunglepyramidpiece);
            this.b();
        }
    }
}
