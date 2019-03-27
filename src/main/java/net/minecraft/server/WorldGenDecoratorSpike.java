package net.minecraft.server;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class WorldGenDecoratorSpike extends WorldGenDecorator<WorldGenFeatureDecoratorEmptyConfiguration> {

    private static final LoadingCache<Long, WorldGenEnder.Spike[]> a = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build(new WorldGenDecoratorSpike.a());

    public WorldGenDecoratorSpike() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureDecoratorEmptyConfiguration worldgenfeaturedecoratoremptyconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        WorldGenEnder.Spike[] aworldgenender_spike = a(generatoraccess);
        boolean flag = false;
        WorldGenEnder.Spike[] aworldgenender_spike1 = aworldgenender_spike;
        int i = aworldgenender_spike.length;

        for (int j = 0; j < i; ++j) {
            WorldGenEnder.Spike worldgenender_spike = aworldgenender_spike1[j];

            if (worldgenender_spike.a(blockposition)) {
                ((WorldGenEnder) worldgenerator).a(worldgenender_spike);
                flag |= ((WorldGenEnder) worldgenerator).a(generatoraccess, chunkgenerator, random, new BlockPosition(worldgenender_spike.a(), 45, worldgenender_spike.b()), WorldGenFeatureConfiguration.e);
            }
        }

        return flag;
    }

    public static WorldGenEnder.Spike[] a(GeneratorAccess generatoraccess) {
        Random random = new Random(generatoraccess.getSeed());
        long i = random.nextLong() & 65535L;

        return (WorldGenEnder.Spike[]) WorldGenDecoratorSpike.a.getUnchecked(i);
    }

    static class a extends CacheLoader<Long, WorldGenEnder.Spike[]> {

        private a() {}

        public WorldGenEnder.Spike[] load(Long olong) throws Exception {
            List<Integer> list = Lists.newArrayList(ContiguousSet.create(Range.closedOpen(0, 10), DiscreteDomain.integers()));

            Collections.shuffle(list, new Random(olong));
            WorldGenEnder.Spike[] aworldgenender_spike = new WorldGenEnder.Spike[10];

            for (int i = 0; i < 10; ++i) {
                int j = (int) (42.0D * Math.cos(2.0D * (-3.141592653589793D + 0.3141592653589793D * (double) i)));
                int k = (int) (42.0D * Math.sin(2.0D * (-3.141592653589793D + 0.3141592653589793D * (double) i)));
                int l = (Integer) list.get(i);
                int i1 = 2 + l / 3;
                int j1 = 76 + l * 3;
                boolean flag = l == 1 || l == 2;

                aworldgenender_spike[i] = new WorldGenEnder.Spike(j, k, i1, j1, flag);
            }

            return aworldgenender_spike;
        }
    }
}
