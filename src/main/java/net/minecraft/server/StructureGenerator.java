package net.minecraft.server;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class StructureGenerator<C extends WorldGenFeatureConfiguration> extends WorldGenerator<C> {

    private static final Logger b = LogManager.getLogger();
    public static final StructureStart a = new StructureStart() {
        public boolean b() {
            return false;
        }
    };

    public StructureGenerator() {}

    public boolean generate(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, C c0) {
        if (!this.a(generatoraccess)) {
            return false;
        } else {
            int i = this.b();
            int j = blockposition.getX() >> 4;
            int k = blockposition.getZ() >> 4;
            int l = j << 4;
            int i1 = k << 4;
            long j1 = ChunkCoordIntPair.a(j, k);
            boolean flag = false;

            for (int k1 = j - i; k1 <= j + i; ++k1) {
                for (int l1 = k - i; l1 <= k + i; ++l1) {
                    long i2 = ChunkCoordIntPair.a(k1, l1);
                    StructureStart structurestart = this.a(generatoraccess, chunkgenerator, (SeededRandom) random, i2);

                    if (structurestart != StructureGenerator.a && structurestart.c().a(l, i1, l + 15, i1 + 15)) {
                        ((LongSet) chunkgenerator.getStructureCache(this).computeIfAbsent(j1, (j2) -> {
                            return new LongOpenHashSet();
                        })).add(i2);
                        generatoraccess.getChunkProvider().a(j, k, true).a(this.a(), i2);
                        structurestart.a(generatoraccess, random, new StructureBoundingBox(l, i1, l + 15, i1 + 15), new ChunkCoordIntPair(j, k));
                        structurestart.b(new ChunkCoordIntPair(j, k));
                        flag = true;
                    }
                }
            }

            return flag;
        }
    }

    protected StructureStart a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        List<StructureStart> list = this.a(generatoraccess, blockposition.getX() >> 4, blockposition.getZ() >> 4);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            StructureStart structurestart = (StructureStart) iterator.next();

            if (structurestart.b() && structurestart.c().b((BaseBlockPosition) blockposition)) {
                Iterator iterator1 = structurestart.d().iterator();

                while (iterator1.hasNext()) {
                    StructurePiece structurepiece = (StructurePiece) iterator1.next();

                    if (structurepiece.d().b((BaseBlockPosition) blockposition)) {
                        return structurestart;
                    }
                }
            }
        }

        return StructureGenerator.a;
    }

    public boolean b(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        List<StructureStart> list = this.a(generatoraccess, blockposition.getX() >> 4, blockposition.getZ() >> 4);
        Iterator iterator = list.iterator();

        StructureStart structurestart;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            structurestart = (StructureStart) iterator.next();
        } while (!structurestart.b() || !structurestart.c().b((BaseBlockPosition) blockposition));

        return true;
    }

    public boolean c(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        return this.a(generatoraccess, blockposition).b();
    }

    @Nullable
    public BlockPosition getNearestGeneratedFeature(World world, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, BlockPosition blockposition, int i, boolean flag) {
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
                                StructureStart structurestart = this.a(world, chunkgenerator, seededrandom, chunkcoordintpair.a());

                                if (structurestart != StructureGenerator.a) {
                                    if (flag && structurestart.g()) {
                                        structurestart.h();
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
        Long2ObjectMap<StructureStart> long2objectmap = generatoraccess.getChunkProvider().getChunkGenerator().getStructureStartCache(this);
        Long2ObjectMap<LongSet> long2objectmap1 = generatoraccess.getChunkProvider().getChunkGenerator().getStructureCache(this);
        long k = ChunkCoordIntPair.a(i, j);
        LongSet longset = (LongSet) long2objectmap1.get(k);

        if (longset == null) {
            longset = generatoraccess.getChunkProvider().a(i, j, true).b(this.a());
            long2objectmap1.put(k, longset);
        }

        LongIterator longiterator = longset.iterator();

        while (longiterator.hasNext()) {
            Long olong = (Long) longiterator.next();
            StructureStart structurestart = (StructureStart) long2objectmap.get(olong);

            if (structurestart != null) {
                list.add(structurestart);
            } else {
                ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(olong);
                IChunkAccess ichunkaccess = generatoraccess.getChunkProvider().a(chunkcoordintpair.x, chunkcoordintpair.z, true);

                structurestart = ichunkaccess.a(this.a());
                if (structurestart != null) {
                    long2objectmap.put(olong, structurestart);
                    list.add(structurestart);
                }
            }
        }

        return list;
    }

    private StructureStart a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, SeededRandom seededrandom, long i) {
        if (!chunkgenerator.getWorldChunkManager().a(this)) {
            return StructureGenerator.a;
        } else {
            Long2ObjectMap<StructureStart> long2objectmap = chunkgenerator.getStructureStartCache(this);
            StructureStart structurestart = (StructureStart) long2objectmap.get(i);

            if (structurestart != null) {
                return structurestart;
            } else {
                ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i);
                IChunkAccess ichunkaccess = generatoraccess.getChunkProvider().getChunkAt(chunkcoordintpair.x, chunkcoordintpair.z, false, false); // CraftBukkit - don't load chunks

                if (ichunkaccess != null) {
                    structurestart = ichunkaccess.a(this.a());
                    if (structurestart != null) {
                        long2objectmap.put(i, structurestart);
                        return structurestart;
                    }
                }

                if (this.a(chunkgenerator, seededrandom, chunkcoordintpair.x, chunkcoordintpair.z)) {
                    StructureStart structurestart1 = this.a(generatoraccess, chunkgenerator, seededrandom, chunkcoordintpair.x, chunkcoordintpair.z);

                    structurestart = structurestart1.b() ? structurestart1 : StructureGenerator.a;
                } else {
                    structurestart = StructureGenerator.a;
                }

                if (structurestart.b()) {
                    generatoraccess.getChunkProvider().a(chunkcoordintpair.x, chunkcoordintpair.z, true).a(this.a(), structurestart);
                }

                long2objectmap.put(i, structurestart);
                return structurestart;
            }
        }
    }

    protected ChunkCoordIntPair a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j, int k, int l) {
        return new ChunkCoordIntPair(i + k, j + l);
    }

    protected abstract boolean a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j);

    protected abstract boolean a(GeneratorAccess generatoraccess);

    protected abstract StructureStart a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, SeededRandom seededrandom, int i, int j);

    protected abstract String a();

    public abstract int b();
}
