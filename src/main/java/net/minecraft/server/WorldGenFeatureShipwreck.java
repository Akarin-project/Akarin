package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class WorldGenFeatureShipwreck extends WorldGenFeatureRandomScattered<WorldGenFeatureShipwreckConfiguration> {

    public WorldGenFeatureShipwreck(Function<Dynamic<?>, ? extends WorldGenFeatureShipwreckConfiguration> function) {
        super(function);
    }

    @Override
    public String b() {
        return "Shipwreck";
    }

    @Override
    public int c() {
        return 3;
    }

    @Override
    public StructureGenerator.a a() {
        return WorldGenFeatureShipwreck.a::new;
    }

    @Override
    // Spigot start
    protected int getSeed(World world) {
        return world.spigotConfig.shipwreckSeed;
        // Spigot end
    }

    @Override
    protected int a(ChunkGenerator<?> chunkgenerator) {
        return chunkgenerator.getSettings().j();
    }

    @Override
    protected int b(ChunkGenerator<?> chunkgenerator) {
        return chunkgenerator.getSettings().k();
    }

    public static class a extends StructureStart {

        public a(StructureGenerator<?> structuregenerator, int i, int j, BiomeBase biomebase, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, biomebase, structureboundingbox, k, l);
        }

        @Override
        public void a(ChunkGenerator<?> chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase) {
            WorldGenFeatureShipwreckConfiguration worldgenfeatureshipwreckconfiguration = (WorldGenFeatureShipwreckConfiguration) chunkgenerator.getFeatureConfiguration(biomebase, WorldGenerator.SHIPWRECK);
            EnumBlockRotation enumblockrotation = EnumBlockRotation.values()[this.d.nextInt(EnumBlockRotation.values().length)];
            BlockPosition blockposition = new BlockPosition(i * 16, 90, j * 16);

            WorldGenShipwreck.a(definedstructuremanager, blockposition, enumblockrotation, this.b, this.d, worldgenfeatureshipwreckconfiguration);
            this.b();
        }
    }
}
