package net.minecraft.server;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

public abstract class ChunkGeneratorAbstract<C extends GeneratorSettings> implements ChunkGenerator<C> {

    protected final GeneratorAccess a;
    protected final long b;
    protected final WorldChunkManager c;
    protected final Map<StructureGenerator<? extends WorldGenFeatureConfiguration>, Long2ObjectMap<StructureStart>> d = Maps.newHashMap();
    protected final Map<StructureGenerator<? extends WorldGenFeatureConfiguration>, Long2ObjectMap<LongSet>> e = Maps.newHashMap();

    public ChunkGeneratorAbstract(GeneratorAccess generatoraccess, WorldChunkManager worldchunkmanager) {
        this.a = generatoraccess;
        this.b = generatoraccess.getSeed();
        this.c = worldchunkmanager;
    }

    public void addFeatures(RegionLimitedWorldAccess regionlimitedworldaccess, WorldGenStage.Features worldgenstage_features) {
        SeededRandom seededrandom = new SeededRandom(this.b);
        boolean flag = true;
        int i = regionlimitedworldaccess.a();
        int j = regionlimitedworldaccess.b();
        BitSet bitset = regionlimitedworldaccess.getChunkAt(i, j).a(worldgenstage_features);

        for (int k = i - 8; k <= i + 8; ++k) {
            for (int l = j - 8; l <= j + 8; ++l) {
                List<WorldGenCarverWrapper<?>> list = regionlimitedworldaccess.getChunkProvider().getChunkGenerator().getWorldChunkManager().getBiome(new BlockPosition(k * 16, 0, l * 16), (BiomeBase) null).a(worldgenstage_features);
                ListIterator listiterator = list.listIterator();

                while (listiterator.hasNext()) {
                    int i1 = listiterator.nextIndex();
                    WorldGenCarverWrapper<?> worldgencarverwrapper = (WorldGenCarverWrapper) listiterator.next();

                    seededrandom.c(regionlimitedworldaccess.getMinecraftWorld().getSeed() + (long) i1, k, l);
                    if (worldgencarverwrapper.a(regionlimitedworldaccess, seededrandom, k, l, WorldGenFeatureConfiguration.e)) {
                        worldgencarverwrapper.a(regionlimitedworldaccess, seededrandom, k, l, i, j, bitset, WorldGenFeatureConfiguration.e);
                    }
                }
            }
        }

    }

    @Nullable
    public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockposition, int i, boolean flag) {
        StructureGenerator<?> structuregenerator = (StructureGenerator) WorldGenerator.aF.get(s.toLowerCase(Locale.ROOT));

        return structuregenerator != null ? structuregenerator.getNearestGeneratedFeature(world, this, blockposition, i, flag) : null;
    }

    protected void a(IChunkAccess ichunkaccess, Random random) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int i = ichunkaccess.getPos().d();
        int j = ichunkaccess.getPos().e();
        Iterator iterator = BlockPosition.a(i, 0, j, i + 16, 0, j + 16).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();

            for (int k = 4; k >= 0; --k) {
                if (k <= random.nextInt(5)) {
                    ichunkaccess.setType(blockposition_mutableblockposition.c(blockposition.getX(), k, blockposition.getZ()), Blocks.BEDROCK.getBlockData(), false);
                }
            }
        }

    }

    public void addDecorations(RegionLimitedWorldAccess regionlimitedworldaccess) {
        BlockFalling.instaFall = true;
        int i = regionlimitedworldaccess.a();
        int j = regionlimitedworldaccess.b();
        int k = i * 16;
        int l = j * 16;
        BlockPosition blockposition = new BlockPosition(k, 0, l);
        BiomeBase biomebase = regionlimitedworldaccess.getChunkAt(i + 1, j + 1).getBiomeIndex()[0];
        SeededRandom seededrandom = new SeededRandom();
        long i1 = seededrandom.a(regionlimitedworldaccess.getSeed(), k, l);
        WorldGenStage.Decoration[] aworldgenstage_decoration = WorldGenStage.Decoration.values();
        int j1 = aworldgenstage_decoration.length;

        for (int k1 = 0; k1 < j1; ++k1) {
            WorldGenStage.Decoration worldgenstage_decoration = aworldgenstage_decoration[k1];

            biomebase.a(worldgenstage_decoration, this, regionlimitedworldaccess, i1, seededrandom, blockposition);
        }

        BlockFalling.instaFall = false;
    }

    public void a(IChunkAccess ichunkaccess, BiomeBase[] abiomebase, SeededRandom seededrandom, int i) {
        double d0 = 0.03125D;
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int j = chunkcoordintpair.d();
        int k = chunkcoordintpair.e();
        double[] adouble = this.a(chunkcoordintpair.x, chunkcoordintpair.z);

        for (int l = 0; l < 16; ++l) {
            for (int i1 = 0; i1 < 16; ++i1) {
                int j1 = j + l;
                int k1 = k + i1;
                int l1 = ichunkaccess.a(HeightMap.Type.WORLD_SURFACE_WG, l, i1) + 1;

                abiomebase[i1 * 16 + l].a(seededrandom, ichunkaccess, j1, k1, l1, adouble[i1 * 16 + l], this.getSettings().r(), this.getSettings().s(), i, this.a.getSeed());
            }
        }

    }

    public abstract C getSettings();

    public abstract double[] a(int i, int j);

    public boolean canSpawnStructure(BiomeBase biomebase, StructureGenerator<? extends WorldGenFeatureConfiguration> structuregenerator) {
        return biomebase.a(structuregenerator);
    }

    @Nullable
    public WorldGenFeatureConfiguration getFeatureConfiguration(BiomeBase biomebase, StructureGenerator<? extends WorldGenFeatureConfiguration> structuregenerator) {
        return biomebase.b(structuregenerator);
    }

    public WorldChunkManager getWorldChunkManager() {
        return this.c;
    }

    public long getSeed() {
        return this.b;
    }

    public Long2ObjectMap<StructureStart> getStructureStartCache(StructureGenerator<? extends WorldGenFeatureConfiguration> structuregenerator) {
        return (Long2ObjectMap) this.d.computeIfAbsent(structuregenerator, (structuregenerator1) -> {
            return Long2ObjectMaps.synchronize(new ExpiringMap<>(8192, 10000));
        });
    }

    public Long2ObjectMap<LongSet> getStructureCache(StructureGenerator<? extends WorldGenFeatureConfiguration> structuregenerator) {
        return (Long2ObjectMap) this.e.computeIfAbsent(structuregenerator, (structuregenerator1) -> {
            return Long2ObjectMaps.synchronize(new ExpiringMap<>(8192, 10000));
        });
    }

    public int getGenerationDepth() {
        return 256;
    }
}
