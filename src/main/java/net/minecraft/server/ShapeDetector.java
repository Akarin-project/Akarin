package net.minecraft.server;

import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Iterator;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class ShapeDetector {

    private final Predicate<ShapeDetectorBlock>[][][] a;
    private final int b;
    private final int c;
    private final int d;

    public ShapeDetector(Predicate<ShapeDetectorBlock>[][][] apredicate) {
        this.a = apredicate;
        this.b = apredicate.length;
        if (this.b > 0) {
            this.c = apredicate[0].length;
            if (this.c > 0) {
                this.d = apredicate[0][0].length;
            } else {
                this.d = 0;
            }
        } else {
            this.c = 0;
            this.d = 0;
        }

    }

    public int a() {
        return this.b;
    }

    public int b() {
        return this.c;
    }

    public int c() {
        return this.d;
    }

    @Nullable
    private ShapeDetector.ShapeDetectorCollection a(BlockPosition blockposition, EnumDirection enumdirection, EnumDirection enumdirection1, LoadingCache<BlockPosition, ShapeDetectorBlock> loadingcache) {
        for (int i = 0; i < this.d; ++i) {
            for (int j = 0; j < this.c; ++j) {
                for (int k = 0; k < this.b; ++k) {
                    if (!this.a[k][j][i].test(loadingcache.getUnchecked(a(blockposition, enumdirection, enumdirection1, i, j, k)))) {
                        return null;
                    }
                }
            }
        }

        return new ShapeDetector.ShapeDetectorCollection(blockposition, enumdirection, enumdirection1, loadingcache, this.d, this.c, this.b);
    }

    @Nullable
    public ShapeDetector.ShapeDetectorCollection a(IWorldReader iworldreader, BlockPosition blockposition) {
        LoadingCache<BlockPosition, ShapeDetectorBlock> loadingcache = a(iworldreader, false);
        int i = Math.max(Math.max(this.d, this.c), this.b);
        Iterator iterator = BlockPosition.a(blockposition, blockposition.a(i - 1, i - 1, i - 1)).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();
            EnumDirection[] aenumdirection = EnumDirection.values();
            int j = aenumdirection.length;

            for (int k = 0; k < j; ++k) {
                EnumDirection enumdirection = aenumdirection[k];
                EnumDirection[] aenumdirection1 = EnumDirection.values();
                int l = aenumdirection1.length;

                for (int i1 = 0; i1 < l; ++i1) {
                    EnumDirection enumdirection1 = aenumdirection1[i1];

                    if (enumdirection1 != enumdirection && enumdirection1 != enumdirection.opposite()) {
                        ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = this.a(blockposition1, enumdirection, enumdirection1, loadingcache);

                        if (shapedetector_shapedetectorcollection != null) {
                            return shapedetector_shapedetectorcollection;
                        }
                    }
                }
            }
        }

        return null;
    }

    public static LoadingCache<BlockPosition, ShapeDetectorBlock> a(IWorldReader iworldreader, boolean flag) {
        return CacheBuilder.newBuilder().build(new ShapeDetector.BlockLoader(iworldreader, flag));
    }

    protected static BlockPosition a(BlockPosition blockposition, EnumDirection enumdirection, EnumDirection enumdirection1, int i, int j, int k) {
        if (enumdirection != enumdirection1 && enumdirection != enumdirection1.opposite()) {
            BaseBlockPosition baseblockposition = new BaseBlockPosition(enumdirection.getAdjacentX(), enumdirection.getAdjacentY(), enumdirection.getAdjacentZ());
            BaseBlockPosition baseblockposition1 = new BaseBlockPosition(enumdirection1.getAdjacentX(), enumdirection1.getAdjacentY(), enumdirection1.getAdjacentZ());
            BaseBlockPosition baseblockposition2 = baseblockposition.d(baseblockposition1);

            return blockposition.a(baseblockposition1.getX() * -j + baseblockposition2.getX() * i + baseblockposition.getX() * k, baseblockposition1.getY() * -j + baseblockposition2.getY() * i + baseblockposition.getY() * k, baseblockposition1.getZ() * -j + baseblockposition2.getZ() * i + baseblockposition.getZ() * k);
        } else {
            throw new IllegalArgumentException("Invalid forwards & up combination");
        }
    }

    public static class ShapeDetectorCollection {

        private final BlockPosition a;
        private final EnumDirection b;
        private final EnumDirection c;
        private final LoadingCache<BlockPosition, ShapeDetectorBlock> d;
        private final int e;
        private final int f;
        private final int g;

        public ShapeDetectorCollection(BlockPosition blockposition, EnumDirection enumdirection, EnumDirection enumdirection1, LoadingCache<BlockPosition, ShapeDetectorBlock> loadingcache, int i, int j, int k) {
            this.a = blockposition;
            this.b = enumdirection;
            this.c = enumdirection1;
            this.d = loadingcache;
            this.e = i;
            this.f = j;
            this.g = k;
        }

        public BlockPosition a() {
            return this.a;
        }

        public EnumDirection getFacing() {
            return this.b;
        }

        public EnumDirection c() {
            return this.c;
        }

        public int d() {
            return this.e;
        }

        public int e() {
            return this.f;
        }

        public ShapeDetectorBlock a(int i, int j, int k) {
            return (ShapeDetectorBlock) this.d.getUnchecked(ShapeDetector.a(this.a, this.getFacing(), this.c(), i, j, k));
        }

        public String toString() {
            return MoreObjects.toStringHelper(this).add("up", this.c).add("forwards", this.b).add("frontTopLeft", this.a).toString();
        }
    }

    static class BlockLoader extends CacheLoader<BlockPosition, ShapeDetectorBlock> {

        private final IWorldReader a;
        private final boolean b;

        public BlockLoader(IWorldReader iworldreader, boolean flag) {
            this.a = iworldreader;
            this.b = flag;
        }

        public ShapeDetectorBlock load(BlockPosition blockposition) throws Exception {
            return new ShapeDetectorBlock(this.a, blockposition, this.b);
        }
    }
}
