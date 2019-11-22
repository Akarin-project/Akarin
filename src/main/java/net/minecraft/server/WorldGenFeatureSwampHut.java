package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.function.Function;

public class WorldGenFeatureSwampHut extends WorldGenFeatureRandomScattered<WorldGenFeatureEmptyConfiguration> {

    private static final List<BiomeBase.BiomeMeta> a = Lists.newArrayList(new BiomeBase.BiomeMeta[]{new BiomeBase.BiomeMeta(EntityTypes.WITCH, 1, 1, 1)});
    private static final List<BiomeBase.BiomeMeta> aS = Lists.newArrayList(new BiomeBase.BiomeMeta[]{new BiomeBase.BiomeMeta(EntityTypes.CAT, 1, 1, 1)});

    public WorldGenFeatureSwampHut(Function<Dynamic<?>, ? extends WorldGenFeatureEmptyConfiguration> function) {
        super(function);
    }

    @Override
    public String b() {
        return "Swamp_Hut";
    }

    @Override
    public int c() {
        return 3;
    }

    @Override
    public StructureGenerator.a a() {
        return WorldGenFeatureSwampHut.a::new;
    }

    @Override
    // Spigot start
    protected int getSeed(World world) {
        return world.spigotConfig.swampSeed;
        // Spigot end
    }

    @Override
    public List<BiomeBase.BiomeMeta> e() {
        return WorldGenFeatureSwampHut.a;
    }

    @Override
    public List<BiomeBase.BiomeMeta> f() {
        return WorldGenFeatureSwampHut.aS;
    }

    public boolean c(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        StructureStart structurestart = this.a(generatoraccess, blockposition, true);

        if (structurestart != StructureStart.a && structurestart instanceof WorldGenFeatureSwampHut.a && !structurestart.d().isEmpty()) {
            StructurePiece structurepiece = (StructurePiece) structurestart.d().get(0);

            return structurepiece instanceof WorldGenWitchHut;
        } else {
            return false;
        }
    }

    public static class a extends StructureStart {

        public a(StructureGenerator<?> structuregenerator, int i, int j, BiomeBase biomebase, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, biomebase, structureboundingbox, k, l);
        }

        @Override
        public void a(ChunkGenerator<?> chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase) {
            WorldGenWitchHut worldgenwitchhut = new WorldGenWitchHut(this.d, i * 16, j * 16);

            this.b.add(worldgenwitchhut);
            this.b();
        }
    }
}
