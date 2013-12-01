package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.List;

public class WorldGenFeatureSwampHut extends WorldGenFeatureRandomScattered<WorldGenFeatureSwampHutConfiguration> {

    private static final List<BiomeBase.BiomeMeta> b = Lists.newArrayList(new BiomeBase.BiomeMeta[] { new BiomeBase.BiomeMeta(EntityTypes.WITCH, 1, 1, 1)});

    public WorldGenFeatureSwampHut() {}

    protected String a() {
        return "Swamp_Hut";
    }

    public int b() {
        return 3;
    }

    protected StructureStart a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j) {
        BiomeBase biomebase = chunkgenerator.getWorldChunkManager().getBiome(new BlockPosition((i << 4) + 9, 0, (j << 4) + 9), Biomes.PLAINS);

        return new WorldGenFeatureSwampHut.a(generatoraccess, seededrandom, i, j, biomebase);
    }

    protected int c() {
        return 14357620;
    }

    public List<BiomeBase.BiomeMeta> d() {
        return WorldGenFeatureSwampHut.b;
    }

    public boolean d(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        StructureStart structurestart = this.a(generatoraccess, blockposition);

        if (structurestart != WorldGenFeatureSwampHut.a && structurestart instanceof WorldGenFeatureSwampHut.a && !structurestart.d().isEmpty()) {
            StructurePiece structurepiece = (StructurePiece) structurestart.d().get(0);

            return structurepiece instanceof WorldGenWitchHut;
        } else {
            return false;
        }
    }

    public static class a extends StructureStart {

        public a() {}

        public a(GeneratorAccess generatoraccess, SeededRandom seededrandom, int i, int j, BiomeBase biomebase) {
            super(i, j, biomebase, seededrandom, generatoraccess.getSeed());
            WorldGenWitchHut worldgenwitchhut = new WorldGenWitchHut(seededrandom, i * 16, j * 16);

            this.a.add(worldgenwitchhut);
            this.a((IBlockAccess) generatoraccess);
        }
    }
}
