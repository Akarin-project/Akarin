package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class StructureGenerator<C extends WorldGenFeatureConfiguration> extends WorldGenerator<C> {

    private static final Logger LOGGER = LogManager.getLogger();

    public StructureGenerator(Function<Dynamic<?>, ? extends C> function) {
        super(function, false);
    }

    @Override
    public boolean generate(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, Random random, BlockPosition blockposition, C c0) {
        if (!generatoraccess.getWorldData().shouldGenerateMapFeatures()) {
            return false;
        } else {
            int i = blockposition.getX() >> 4;
            int j = blockposition.getZ() >> 4;
            int k = i << 4;
            int l = j << 4;
            boolean flag = false;
            LongIterator longiterator = generatoraccess.getChunkAt(i, j).b(this.b()).iterator();

            while (longiterator.hasNext()) {
                Long olong = (Long) longiterator.next();
                ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(olong);
                StructureStart structurestart = generatoraccess.getChunkAt(chunkcoordintpair.x, chunkcoordintpair.z).a(this.b());

                if (structurestart != null && structurestart != StructureStart.a) {
                    structurestart.a(generatoraccess, random, new StructureBoundingBox(k, l, k + 15, l + 15), new ChunkCoordIntPair(i, j));
                    flag = true;
                }
            }

            return flag;
        }
    }

    protected StructureStart a(GeneratorAccess generatoraccess, BlockPosition blockposition, boolean flag) {
        List<StructureStart> list = this.a(generatoraccess, blockposition.getX() >> 4, blockposition.getZ() >> 4);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            StructureStart structurestart = (StructureStart) iterator.next();

            if (structurestart.e() && structurestart.c().b((BaseBlockPosition) blockposition)) {
                if (!flag) {
                    return structurestart;
                }

                Iterator iterator1 = structurestart.d().iterator();

                while (iterator1.hasNext()) {
                    StructurePiece structurepiece = (StructurePiece) iterator1.next();

                    if (structurepiece.g().b((BaseBlockPosition) blockposition)) {
                        return structurestart;
                    }
                }
            }
        }

        return StructureStart.a;
    }

    public boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        return this.a(generatoraccess, blockposition, false).e();
    }

    public boolean b(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        return this.a(generatoraccess, blockposition, true).e();
    }

    @Nullable
    public BlockPosition getNearestGeneratedFeature(World world, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, BlockPosition blockposition, int i, boolean flag) {
        if (!chunkgenerator.getWorldChunkManager().a(this)) {
            return null;
        } else {
            int j = blockposition.getX() >> 4;
            int k = blockposition.getZ() >> 4;
            int l = 0;
            SeededRandom seededrandom = new SeededRandom();

            while (l <= i) {
                int i1 = -l;

                while (true) {
                    if (i1 <= l) {
                        boolean flag1 = i1 == -l || i1 == l;

                        for (int j1 = -l; j1 <= l; ++j1) {
                            boolean flag2 = j1 == -l || j1 == l;

                            if (flag1 || flag2) {
                                ChunkCoordIntPair chunkcoordintpair = this.a(chunkgenerator, seededrandom, j, k, i1, j1);
                                if (!world.getWorldBorder().isChunkInBounds(chunkcoordintpair.x, chunkcoordintpair.z)) { continue; } // Paper
                                StructureStart structurestart = world.getChunkAt(chunkcoordintpair.x, chunkcoordintpair.z, ChunkStatus.STRUCTURE_STARTS).a(this.b());

                                if (structurestart != null && structurestart.e()) {
                                    if (flag && structurestart.h()) {
                                        structurestart.i();
                                        return structurestart.a();
                                    }

                                    if (!flag) {
                                        return structurestart.a();
                                    }
                                }

                                if (l == 0) {
                                    break;
                                }
                            }
                        }

                        if (l != 0) {
                            ++i1;
                            continue;
                        }
                    }

                    ++l;
                    break;
                }
            }

            return null;
        }
    }

    private List<StructureStart> a(GeneratorAccess generatoraccess, int i, int j) {
        List<StructureStart> list = Lists.newArrayList();
        IChunkAccess ichunkaccess = generatoraccess.getChunkAt(i, j, ChunkStatus.STRUCTURE_REFERENCES);
        LongIterator longiterator = ichunkaccess.b(this.b()).iterator();

        while (longiterator.hasNext()) {
            long k = longiterator.nextLong();
            IChunkAccess ichunkaccess1 = generatoraccess.getChunkAt(ChunkCoordIntPair.getX(k), ChunkCoordIntPair.getZ(k), ChunkStatus.STRUCTURE_STARTS, false); // CraftBukkit - don't load chunks
            StructureStart structurestart = ichunkaccess1.a(this.b());

            if (structurestart != null) {
                list.add(structurestart);
            }
        }

        return list;
    }

    protected ChunkCoordIntPair a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j, int k, int l) {
        return new ChunkCoordIntPair(i + k, j + l);
    }

    public abstract boolean a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j);

    public abstract StructureGenerator.a a();

    public abstract String b();

    public abstract int c();

    public interface a {

        StructureStart create(StructureGenerator<?> structuregenerator, int i, int j, BiomeBase biomebase, StructureBoundingBox structureboundingbox, int k, long l);
    }
}
