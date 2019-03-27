package net.minecraft.server;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;

public class BiomeCache {

    private final WorldChunkManager a;
    private final com.github.benmanes.caffeine.cache.LoadingCache<ChunkCoordIntPair, net.minecraft.server.BiomeCache.a> b; // Akarin - caffeine

    public BiomeCache(WorldChunkManager worldchunkmanager) {
        // Akarin start - caffeine
        this.b = com.github.benmanes.caffeine.cache.Caffeine.newBuilder().expireAfterAccess(30L, TimeUnit.SECONDS).build(new com.github.benmanes.caffeine.cache.CacheLoader<ChunkCoordIntPair, BiomeCache.a>() {
            @Override
            public BiomeCache.a load(ChunkCoordIntPair chunkcoordintpair) throws Exception {
                return BiomeCache.this.new a(chunkcoordintpair.x, chunkcoordintpair.z);
            }
        });
        // Akarin end
        this.a = worldchunkmanager;
    }

    public BiomeCache.a a(int i, int j) {
        i >>= 4;
        j >>= 4;
        return (BiomeCache.a) this.b.get(new ChunkCoordIntPair(i, j)); // Akarin - caffeine
    }

    public BiomeBase a(int i, int j, BiomeBase biomebase) {
        BiomeBase biomebase1 = this.a(i, j).a(i, j);

        return biomebase1 == null ? biomebase : biomebase1;
    }

    public void a() {}

    public BiomeBase[] b(int i, int j) {
        return this.a(i, j).b;
    }

    public class a {

        private final BiomeBase[] b;

        public a(int i, int j) {
            this.b = BiomeCache.this.a.a(i << 4, j << 4, 16, 16, false);
        }

        public BiomeBase a(int i, int j) {
            return this.b[i & 15 | (j & 15) << 4];
        }
    }
}
