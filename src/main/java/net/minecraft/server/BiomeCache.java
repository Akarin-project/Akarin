package net.minecraft.server;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;

public class BiomeCache {

    private final WorldChunkManager a;
    private final LoadingCache<ChunkCoordIntPair, BiomeCache.a> b;

    public BiomeCache(WorldChunkManager worldchunkmanager) {
        this.b = CacheBuilder.newBuilder().expireAfterAccess(30000L, TimeUnit.MILLISECONDS).build(new CacheLoader<ChunkCoordIntPair, BiomeCache.a>() {
            public BiomeCache.a load(ChunkCoordIntPair chunkcoordintpair) throws Exception {
                return BiomeCache.this.new a(chunkcoordintpair.x, chunkcoordintpair.z);
            }
        });
        this.a = worldchunkmanager;
    }

    public BiomeCache.a a(int i, int j) {
        i >>= 4;
        j >>= 4;
        return (BiomeCache.a) this.b.getUnchecked(new ChunkCoordIntPair(i, j));
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
