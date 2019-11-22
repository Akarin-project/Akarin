package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class WorldGenMonument extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final List<BiomeBase.BiomeMeta> a = Lists.newArrayList(new BiomeBase.BiomeMeta[]{new BiomeBase.BiomeMeta(EntityTypes.GUARDIAN, 1, 2, 4)});

    public WorldGenMonument(Function<Dynamic<?>, ? extends WorldGenFeatureEmptyConfiguration> function) {
        super(function);
    }

    @Override
    protected ChunkCoordIntPair a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j, int k, int l) {
        int i1 = chunkgenerator.getSettings().c();
        int j1 = chunkgenerator.getSettings().d();
        int k1 = i + i1 * k;
        int l1 = j + i1 * l;
        int i2 = k1 < 0 ? k1 - i1 + 1 : k1;
        int j2 = l1 < 0 ? l1 - i1 + 1 : l1;
        int k2 = i2 / i1;
        int l2 = j2 / i1;

        ((SeededRandom) random).a(chunkgenerator.getSeed(), k2, l2, chunkgenerator.getWorld().spigotConfig.monumentSeed); // Spigot
        k2 *= i1;
        l2 *= i1;
        k2 += (random.nextInt(i1 - j1) + random.nextInt(i1 - j1)) / 2;
        l2 += (random.nextInt(i1 - j1) + random.nextInt(i1 - j1)) / 2;
        return new ChunkCoordIntPair(k2, l2);
    }

    @Override
    public boolean a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j) {
        ChunkCoordIntPair chunkcoordintpair = this.a(chunkgenerator, random, i, j, 0, 0);

        if (i == chunkcoordintpair.x && j == chunkcoordintpair.z) {
            Set<BiomeBase> set = chunkgenerator.getWorldChunkManager().a(i * 16 + 9, j * 16 + 9, 16);
            Iterator iterator = set.iterator();

            BiomeBase biomebase;

            do {
                if (!iterator.hasNext()) {
                    Set<BiomeBase> set1 = chunkgenerator.getWorldChunkManager().a(i * 16 + 9, j * 16 + 9, 29);
                    Iterator iterator1 = set1.iterator();

                    BiomeBase biomebase1;

                    do {
                        if (!iterator1.hasNext()) {
                            return true;
                        }

                        biomebase1 = (BiomeBase) iterator1.next();
                    } while (biomebase1.o() == BiomeBase.Geography.OCEAN || biomebase1.o() == BiomeBase.Geography.RIVER);

                    return false;
                }

                biomebase = (BiomeBase) iterator.next();
            } while (chunkgenerator.canSpawnStructure(biomebase, WorldGenerator.OCEAN_MONUMENT));

            return false;
        } else {
            return false;
        }
    }

    @Override
    public StructureGenerator.a a() {
        return WorldGenMonument.a::new;
    }

    @Override
    public String b() {
        return "Monument";
    }

    @Override
    public int c() {
        return 8;
    }

    @Override
    public List<BiomeBase.BiomeMeta> e() {
        return WorldGenMonument.a;
    }

    public static class a extends StructureStart {

        private boolean e;

        public a(StructureGenerator<?> structuregenerator, int i, int j, BiomeBase biomebase, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, biomebase, structureboundingbox, k, l);
        }

        @Override
        public void a(ChunkGenerator<?> chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase) {
            this.b(i, j);
        }

        private void b(int i, int j) {
            int k = i * 16 - 29;
            int l = j * 16 - 29;
            EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(this.d);

            this.b.add(new WorldGenMonumentPieces.WorldGenMonumentPiece1(this.d, k, l, enumdirection));
            this.b();
            this.e = true;
        }

        @Override
        public void a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            if (!this.e) {
                this.b.clear();
                this.b(this.f(), this.g());
            }

            super.a(generatoraccess, random, structureboundingbox, chunkcoordintpair);
        }
    }
}
