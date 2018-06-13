package net.minecraft.server;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.concurrent.Immutable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <b>Akarin Changes Note</b><br>
 * <br>
 * 1) Add OBFHELPER<br>
 * @author cakoyo
 */
@Immutable
public class BlockPosition extends BaseBlockPosition {

    private static final Logger b = LogManager.getLogger();
    public static final BlockPosition ZERO = new BlockPosition(0, 0, 0);
    private static final int c = 1 + MathHelper.e(MathHelper.c(30000000));
    private static final int d = BlockPosition.c;
    private static final int f = 64 - BlockPosition.c - BlockPosition.d;
    private static final int g = 0 + BlockPosition.d;
    private static final int h = BlockPosition.g + BlockPosition.f;
    private static final long i = (1L << BlockPosition.c) - 1L;
    private static final long j = (1L << BlockPosition.f) - 1L;
    private static final long k = (1L << BlockPosition.d) - 1L;

    public BlockPosition(int i, int j, int k) {
        super(i, j, k);
    }

    public BlockPosition(double d0, double d1, double d2) {
        super(d0, d1, d2);
    }

    public BlockPosition(Entity entity) {
        this(entity.locX, entity.locY, entity.locZ);
    }

    public BlockPosition(Vec3D vec3d) {
        this(vec3d.x, vec3d.y, vec3d.z);
    }

    public BlockPosition(BaseBlockPosition baseblockposition) {
        this(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
    }

    public BlockPosition add(double x, double y, double z) { return this.a(x, y, z); } // Paper - OBFHELPER
    public BlockPosition a(double d0, double d1, double d2) {
        return d0 == 0.0D && d1 == 0.0D && d2 == 0.0D ? this : new BlockPosition(this.getX() + d0, this.getY() + d1, this.getZ() + d2);
    }

    public BlockPosition a(int i, int j, int k) {
        return i == 0 && j == 0 && k == 0 ? this : new BlockPosition(this.getX() + i, this.getY() + j, this.getZ() + k);
    }

    public BlockPosition add(BaseBlockPosition baseblockposition) { return this.a(baseblockposition); } // Akarin - OBFHELPER
    public BlockPosition a(BaseBlockPosition baseblockposition) {
        return this.a(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
    }

    public BlockPosition b(BaseBlockPosition baseblockposition) {
        return this.a(-baseblockposition.getX(), -baseblockposition.getY(), -baseblockposition.getZ());
    }

    public BlockPosition up() {
        return this.up(1);
    }

    public BlockPosition up(int i) {
        return this.shift(EnumDirection.UP, i);
    }

    public BlockPosition down() {
        return this.down(1);
    }

    public BlockPosition down(int i) {
        return this.shift(EnumDirection.DOWN, i);
    }

    public BlockPosition north() {
        return this.north(1);
    }

    public BlockPosition north(int i) {
        return this.shift(EnumDirection.NORTH, i);
    }

    public BlockPosition south() {
        return this.south(1);
    }

    public BlockPosition south(int i) {
        return this.shift(EnumDirection.SOUTH, i);
    }

    public BlockPosition west() {
        return this.west(1);
    }

    public BlockPosition west(int i) {
        return this.shift(EnumDirection.WEST, i);
    }

    public BlockPosition east() {
        return this.east(1);
    }

    public BlockPosition east(int i) {
        return this.shift(EnumDirection.EAST, i);
    }

    public BlockPosition shift(EnumDirection enumdirection) {
        return this.shift(enumdirection, 1);
    }

    public BlockPosition shift(EnumDirection enumdirection, int i) {
        return i == 0 ? this : new BlockPosition(this.getX() + enumdirection.getAdjacentX() * i, this.getY() + enumdirection.getAdjacentY() * i, this.getZ() + enumdirection.getAdjacentZ() * i);
    }

    public BlockPosition a(EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
        case NONE:
        default:
            return this;

        case CLOCKWISE_90:
            return new BlockPosition(-this.getZ(), this.getY(), this.getX());

        case CLOCKWISE_180:
            return new BlockPosition(-this.getX(), this.getY(), -this.getZ());

        case COUNTERCLOCKWISE_90:
            return new BlockPosition(this.getZ(), this.getY(), -this.getX());
        }
    }

    public BlockPosition c(BaseBlockPosition baseblockposition) {
        return new BlockPosition(this.getY() * baseblockposition.getZ() - this.getZ() * baseblockposition.getY(), this.getZ() * baseblockposition.getX() - this.getX() * baseblockposition.getZ(), this.getX() * baseblockposition.getY() - this.getY() * baseblockposition.getX());
    }

    public long asLong() {
        return (this.getX() & BlockPosition.i) << BlockPosition.h | (this.getY() & BlockPosition.j) << BlockPosition.g | (this.getZ() & BlockPosition.k) << 0;
    }

    public static BlockPosition fromLong(long i) {
        int j = (int) (i << 64 - BlockPosition.h - BlockPosition.c >> 64 - BlockPosition.c);
        int k = (int) (i << 64 - BlockPosition.g - BlockPosition.f >> 64 - BlockPosition.f);
        int l = (int) (i << 64 - BlockPosition.d >> 64 - BlockPosition.d);

        return new BlockPosition(j, k, l);
    }

    public static Iterable<BlockPosition> a(BlockPosition blockposition, BlockPosition blockposition1) {
        return a(Math.min(blockposition.getX(), blockposition1.getX()), Math.min(blockposition.getY(), blockposition1.getY()), Math.min(blockposition.getZ(), blockposition1.getZ()), Math.max(blockposition.getX(), blockposition1.getX()), Math.max(blockposition.getY(), blockposition1.getY()), Math.max(blockposition.getZ(), blockposition1.getZ()));
    }

    public static Iterable<BlockPosition> a(final int i, final int j, final int k, final int l, final int i1, final int j1) {
        return new Iterable() {
            @Override
            public Iterator<BlockPosition> iterator() {
                return new AbstractIterator() {
                    private boolean b = true;
                    private int c;
                    private int d;
                    private int e;

                    protected BlockPosition a() {
                        if (this.b) {
                            this.b = false;
                            this.c = i;
                            this.d = j;
                            this.e = k;
                            return new BlockPosition(i, j, k);
                        } else if (this.c == l && this.d == i1 && this.e == j1) {
                            return (BlockPosition) this.endOfData();
                        } else {
                            if (this.c < l) {
                                ++this.c;
                            } else if (this.d < i1) {
                                this.c = i;
                                ++this.d;
                            } else if (this.e < j1) {
                                this.c = i;
                                this.d = j;
                                ++this.e;
                            }

                            return new BlockPosition(this.c, this.d, this.e);
                        }
                    }

                    @Override
                    protected Object computeNext() {
                        return this.a();
                    }
                };
            }
        };
    }

    public BlockPosition h() {
        return this;
    }

    public static Iterable<BlockPosition.MutableBlockPosition> b(BlockPosition blockposition, BlockPosition blockposition1) {
        return b(Math.min(blockposition.getX(), blockposition1.getX()), Math.min(blockposition.getY(), blockposition1.getY()), Math.min(blockposition.getZ(), blockposition1.getZ()), Math.max(blockposition.getX(), blockposition1.getX()), Math.max(blockposition.getY(), blockposition1.getY()), Math.max(blockposition.getZ(), blockposition1.getZ()));
    }

    public static Iterable<BlockPosition.MutableBlockPosition> b(final int i, final int j, final int k, final int l, final int i1, final int j1) {
        return new Iterable() {
            @Override
            public Iterator<BlockPosition.MutableBlockPosition> iterator() {
                return new AbstractIterator() {
                    private BlockPosition.MutableBlockPosition b;

                    protected BlockPosition.MutableBlockPosition a() {
                        if (this.b == null) {
                            this.b = new BlockPosition.MutableBlockPosition(i, j, k);
                            return this.b;
                        // Paper start - b, c, d, refer to x, y, z, and as such, a, b, c of BaseBlockPosition
                        } else if (((BaseBlockPosition)this.b).a == l && ((BaseBlockPosition)this.b).b == i1 && ((BaseBlockPosition)this.b).c == j1) {
                            return (BlockPosition.MutableBlockPosition) this.endOfData();
                        } else {
                            if (((BaseBlockPosition) this.b).a < l) {
                                ++((BaseBlockPosition) this.b).a;
                            } else if (((BaseBlockPosition) this.b).b < i1) {
                                ((BaseBlockPosition) this.b).a = i;
                                ++((BaseBlockPosition) this.b).b;
                            } else if (((BaseBlockPosition) this.b).c < j1) {
                                ((BaseBlockPosition) this.b).a = i;
                                ((BaseBlockPosition) this.b).b = j;
                                ++((BaseBlockPosition) this.b).c;
                            }
                            // Paper end

                            return this.b;
                        }
                    }

                    @Override
                    protected Object computeNext() {
                        return this.a();
                    }
                };
            }
        };
    }

    @Override
    public BaseBlockPosition d(BaseBlockPosition baseblockposition) {
        return this.c(baseblockposition);
    }

    public static final class PooledBlockPosition extends BlockPosition.MutableBlockPosition {

        private boolean f;
        private static final List<BlockPosition.PooledBlockPosition> g = Lists.newArrayList();

        private PooledBlockPosition(int i, int j, int k) {
            super(i, j, k);
        }

        public static BlockPosition.PooledBlockPosition aquire() { return s(); } // Paper - OBFHELPER
        public static BlockPosition.PooledBlockPosition s() {
            return e(0, 0, 0);
        }

        public static BlockPosition.PooledBlockPosition d(double d0, double d1, double d2) {
            return e(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2));
        }

        public static BlockPosition.PooledBlockPosition e(int i, int j, int k) {
            List list = BlockPosition.PooledBlockPosition.g;

            synchronized (BlockPosition.PooledBlockPosition.g) {
                if (!BlockPosition.PooledBlockPosition.g.isEmpty()) {
                    BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.g.remove(BlockPosition.PooledBlockPosition.g.size() - 1);

                    if (blockposition_pooledblockposition != null && blockposition_pooledblockposition.f) {
                        blockposition_pooledblockposition.f = false;
                        blockposition_pooledblockposition.f(i, j, k);
                        return blockposition_pooledblockposition;
                    }
                }
            }

            return new BlockPosition.PooledBlockPosition(i, j, k);
        }

        public void free() { t(); } // Paper - OBFHELPER
        public void t() {
            List list = BlockPosition.PooledBlockPosition.g;

            synchronized (BlockPosition.PooledBlockPosition.g) {
                if (BlockPosition.PooledBlockPosition.g.size() < 100) {
                    BlockPosition.PooledBlockPosition.g.add(this);
                }

                this.f = true;
            }
        }

        public BlockPosition.PooledBlockPosition f(int i, int j, int k) {
            if (this.f) {
                BlockPosition.b.error("PooledMutableBlockPosition modified after it was released.", new Throwable());
                this.f = false;
            }

            return (BlockPosition.PooledBlockPosition) super.c(i, j, k);
        }

        public BlockPosition.PooledBlockPosition e(double d0, double d1, double d2) {
            return (BlockPosition.PooledBlockPosition) super.c(d0, d1, d2);
        }

        public BlockPosition.PooledBlockPosition j(BaseBlockPosition baseblockposition) {
            return (BlockPosition.PooledBlockPosition) super.g(baseblockposition);
        }

        public BlockPosition.PooledBlockPosition d(EnumDirection enumdirection) {
            return (BlockPosition.PooledBlockPosition) super.c(enumdirection);
        }

        public BlockPosition.PooledBlockPosition d(EnumDirection enumdirection, int i) {
            return (BlockPosition.PooledBlockPosition) super.c(enumdirection, i);
        }

        @Override
        public BlockPosition.MutableBlockPosition c(EnumDirection enumdirection, int i) {
            return this.d(enumdirection, i);
        }

        @Override
        public BlockPosition.MutableBlockPosition c(EnumDirection enumdirection) {
            return this.d(enumdirection);
        }

        @Override
        public BlockPosition.MutableBlockPosition g(BaseBlockPosition baseblockposition) {
            return this.j(baseblockposition);
        }

        @Override
        public BlockPosition.MutableBlockPosition c(double d0, double d1, double d2) {
            return this.e(d0, d1, d2);
        }

        @Override
        public BlockPosition.MutableBlockPosition c(int i, int j, int k) {
            return this.f(i, j, k);
        }
    }

    public static class MutableBlockPosition extends BlockPosition {

        // Paper start - Remove variables
        /*
        protected int b;
        protected int c;
        protected int d;
        // Paper start
        @Override
        public boolean isValidLocation() {
            return b >= -30000000 && d >= -30000000 && b < 30000000 && d < 30000000 && c >= 0 && c < 256;
        }
        @Override
        public boolean isInvalidYLocation() {
            return c < 0 || c >= 256;
        }
        */
        // Paper end

        public MutableBlockPosition() {
            this(0, 0, 0);
        }

        public MutableBlockPosition(BlockPosition blockposition) {
            this(blockposition.getX(), blockposition.getY(), blockposition.getZ());
        }

        public MutableBlockPosition(int i, int j, int k) {
            super(0, 0, 0);
            // Paper start - Modify base position variables
            ((BaseBlockPosition) this).a = i;
            ((BaseBlockPosition) this).b = j;
            ((BaseBlockPosition) this).c = k;
            // Paper end
        }

        @Override
        public BlockPosition a(double d0, double d1, double d2) {
            return super.a(d0, d1, d2).h();
        }

        @Override
        public BlockPosition a(int i, int j, int k) {
            return super.a(i, j, k).h();
        }

        @Override
        public BlockPosition shift(EnumDirection enumdirection, int i) {
            return super.shift(enumdirection, i).h();
        }

        @Override
        public BlockPosition a(EnumBlockRotation enumblockrotation) {
            return super.a(enumblockrotation).h();
        }

        // Paper start - Use superclass methods
        /*
        public int getX() {
            return this.b;
        }

        public int getY() {
            return this.c;
        }

        public int getZ() {
            return this.d;
        }
        */
        // Paper end

        public void setValues(int x, int y, int z) { c(x, y, z); } // Paper - OBFHELPER
        public BlockPosition.MutableBlockPosition c(int i, int j, int k) {
            // Paper start - Modify base position variables
            ((BaseBlockPosition) this).a = i;
            ((BaseBlockPosition) this).b = j;
            ((BaseBlockPosition) this).c = k;
            // Paper end
            return this;
        }

        public BlockPosition.MutableBlockPosition c(double d0, double d1, double d2) {
            return this.c(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2));
        }

        public BlockPosition.MutableBlockPosition g(BaseBlockPosition baseblockposition) {
            return this.c(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
        }

        public BlockPosition.MutableBlockPosition c(EnumDirection enumdirection) {
            return this.c(enumdirection, 1);
        }

        public BlockPosition.MutableBlockPosition c(EnumDirection enumdirection, int i) {
            return this.c(this.getX() + enumdirection.getAdjacentX() * i, this.getY() + enumdirection.getAdjacentY() * i, this.getZ() + enumdirection.getAdjacentZ() * i); // Paper - USE THE BLEEPING GETTERS
        }

        public void p(int i) {
            ((BaseBlockPosition) this).b = i; // Paper - Modify base variable
        }

        @Override
        public BlockPosition h() {
            return new BlockPosition(this);
        }

        @Override
        public BaseBlockPosition d(BaseBlockPosition baseblockposition) {
            return super.c(baseblockposition);
        }
    }
}
