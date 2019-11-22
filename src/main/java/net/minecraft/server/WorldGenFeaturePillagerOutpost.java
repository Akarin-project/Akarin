package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class WorldGenFeaturePillagerOutpost extends WorldGenFeatureRandomScattered<WorldGenFeaturePillagerOutpostConfiguration> {

    private static final List<BiomeBase.BiomeMeta> a = Lists.newArrayList(new BiomeBase.BiomeMeta[]{new BiomeBase.BiomeMeta(EntityTypes.PILLAGER, 1, 1, 1)});

    public WorldGenFeaturePillagerOutpost(Function<Dynamic<?>, ? extends WorldGenFeaturePillagerOutpostConfiguration> function) {
        super(function);
    }

    @Override
    public String b() {
        return "Pillager_Outpost";
    }

    @Override
    public int c() {
        return 3;
    }

    @Override
    public List<BiomeBase.BiomeMeta> e() {
        return WorldGenFeaturePillagerOutpost.a;
    }

    @Override
    public boolean a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j) {
        ChunkCoordIntPair chunkcoordintpair = this.a(chunkgenerator, random, i, j, 0, 0);

        if (i == chunkcoordintpair.x && j == chunkcoordintpair.z) {
            int k = i >> 4;
            int l = j >> 4;

            random.setSeed((long) (k ^ l << 4) ^ chunkgenerator.getSeed());
            random.nextInt();
            if (random.nextInt(5) != 0) {
                return false;
            }

            BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9));

            if (chunkgenerator.canSpawnStructure(biomebase, WorldGenerator.PILLAGER_OUTPOST)) {
                for (int i1 = i - 10; i1 <= i + 10; ++i1) {
                    for (int j1 = j - 10; j1 <= j + 10; ++j1) {
                        if (WorldGenerator.VILLAGE.a(chunkgenerator, random, i1, j1)) {
                            return false;
                        }
                    }
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public StructureGenerator.a a() {
        return WorldGenFeaturePillagerOutpost.a::new;
    }

    @Override
    // Spigot start
    protected int getSeed(World world) {
        return world.spigotConfig.outpostSeed;
        // Spigot end
    }

    public static class a extends StructureAbstract {

        public a(StructureGenerator<?> structuregenerator, int i, int j, BiomeBase biomebase, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, biomebase, structureboundingbox, k, l);
        }

        @Override
        public void a(ChunkGenerator<?> chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase) {
            BlockPosition blockposition = new BlockPosition(i * 16, 90, j * 16);

            WorldGenFeaturePillagerOutpostPieces.a(chunkgenerator, definedstructuremanager, blockposition, this.b, this.d);
            this.b();
        }
    }
}
