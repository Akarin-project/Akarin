package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class WorldGenVillage extends StructureGenerator<WorldGenFeatureVillageConfiguration> {

    public WorldGenVillage(Function<Dynamic<?>, ? extends WorldGenFeatureVillageConfiguration> function) {
        super(function);
    }

    @Override
    protected ChunkCoordIntPair a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j, int k, int l) {
        int i1 = chunkgenerator.getSettings().a();
        int j1 = chunkgenerator.getSettings().b();
        int k1 = i + i1 * k;
        int l1 = j + i1 * l;
        int i2 = k1 < 0 ? k1 - i1 + 1 : k1;
        int j2 = l1 < 0 ? l1 - i1 + 1 : l1;
        int k2 = i2 / i1;
        int l2 = j2 / i1;

        ((SeededRandom) random).a(chunkgenerator.getSeed(), k2, l2, chunkgenerator.getWorld().spigotConfig.villageSeed); // Spigot
        k2 *= i1;
        l2 *= i1;
        k2 += random.nextInt(i1 - j1);
        l2 += random.nextInt(i1 - j1);
        return new ChunkCoordIntPair(k2, l2);
    }

    @Override
    public boolean a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j) {
        ChunkCoordIntPair chunkcoordintpair = this.a(chunkgenerator, random, i, j, 0, 0);

        if (i == chunkcoordintpair.x && j == chunkcoordintpair.z) {
            BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9));

            return chunkgenerator.canSpawnStructure(biomebase, WorldGenerator.VILLAGE);
        } else {
            return false;
        }
    }

    @Override
    public StructureGenerator.a a() {
        return WorldGenVillage.a::new;
    }

    @Override
    public String b() {
        return "Village";
    }

    @Override
    public int c() {
        return 8;
    }

    public static class a extends StructureAbstract {

        public a(StructureGenerator<?> structuregenerator, int i, int j, BiomeBase biomebase, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, biomebase, structureboundingbox, k, l);
        }

        @Override
        public void a(ChunkGenerator<?> chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase) {
            WorldGenFeatureVillageConfiguration worldgenfeaturevillageconfiguration = (WorldGenFeatureVillageConfiguration) chunkgenerator.getFeatureConfiguration(biomebase, WorldGenerator.VILLAGE);
            BlockPosition blockposition = new BlockPosition(i * 16, 0, j * 16);

            NewVillagePieces.a(chunkgenerator, definedstructuremanager, blockposition, this.b, this.d, worldgenfeaturevillageconfiguration);
            this.b();
        }
    }
}
