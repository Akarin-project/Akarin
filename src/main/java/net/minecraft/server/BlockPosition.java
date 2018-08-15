package net.minecraft.server;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.concurrent.Immutable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Immutable
public class BlockPosition extends BaseBlockPosition {

    //private static final Logger b = LogManager.getLogger(); // Paper - variable name conflict, logger isn't used
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

    public BlockPosition a(double d0, double d1, double d2) {
        return d0 == 0.0D && d1 == 0.0D && d2 == 0.0D ? this : new BlockPosition((double) this.getX() + d0, (double) this.getY() + d1, (double) this.getZ() + d2);
    }

    public BlockPosition add(int i, int j, int k) {return a(i, j, k);} // Paper - OBFHELPER
    public BlockPosition a(int i, int j, int k) {
        return i == 0 && j == 0 && k == 0 ? this : new BlockPosition(this.getX() + i, this.getY() + j, this.getZ() + k);
    }

    public BlockPosition a(BaseBlockPosition baseblockposition) {
        return this.a(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
    }

    public BlockPosition b(BaseBlockPosition baseblockposition) {
        return this.a(-baseblockposition.getX(), -baseblockposition.getY(), -baseblockposition.getZ());
    }

    public BlockPosition up() {
        return new BlockPosition(this.getX(), this.getY() + 1, this.getZ()); // Paper - Optimize BlockPosition
    }

    public BlockPosition up(int i) {
        return i == 0 ? this : new BlockPosition(this.getX(), this.getY() + i, this.getZ()); // Paper - Optimize BlockPosition
    }

    public BlockPosition down() {
        return new BlockPosition(this.getX(), this.getY() - 1, this.getZ()); // Paper - Optimize BlockPosition
    }

    public BlockPosition down(int i) {
        return i == 0 ? this : new BlockPosition(this.getX(), this.getY() - i, this.getZ()); // Paper - Optimize BlockPosition
    }

    public BlockPosition north() {
        return new BlockPosition(this.getX(), this.getY(), this.getZ() - 1); // Paper - Optimize BlockPosition
    }

    public BlockPosition north(int i) {
        return i == 0 ? this : new BlockPosition(this.getX(), this.getY(), this.getZ() - i); // Paper - Optimize BlockPosition
    }

    public BlockPosition south() {
        return new BlockPosition(this.getX(), this.getY(), this.getZ() + 1); // Paper - Optimize BlockPosition
    }

    public BlockPosition south(int i) {
        return i == 0 ? this : new BlockPosition(this.getX(), this.getY(), this.getZ() + i); // Paper - Optimize BlockPosition
    }

    public BlockPosition west() {
        return new BlockPosition(this.getX() - 1, this.getY(), this.getZ()); // Paper - Optimize BlockPosition
    }

    public BlockPosition west(int i) {
        return i == 0 ? this : new BlockPosition(this.getX() - i, this.getY(), this.getZ()); // Paper - Optimize BlockPosition
    }

    public BlockPosition east() {
        return new BlockPosition(this.getX() + 1, this.getY(), this.getZ()); // Paper - Optimize BlockPosition
    }

    public BlockPosition east(int i) {
        return i == 0 ? this : new BlockPosition(this.getX() + i, this.getY(), this.getZ()); // Paper - Optimize BlockPosition
    }

    public BlockPosition shift(EnumDirection enumdirection) {
        // Paper Start - Optimize BlockPosition
        switch(enumdirection) {
            case UP:
                return new BlockPosition(this.getX(), this.getY() + 1, this.getZ());
            case DOWN:
                return new BlockPosition(this.getX(), this.getY() - 1, this.getZ());
            case NORTH:
                return new BlockPosition(this.getX(), this.getY(), this.getZ() - 1);
            case SOUTH:
                return new BlockPosition(this.getX(), this.getY(), this.getZ() + 1);
            case WEST:
                return new BlockPosition(this.getX() - 1, this.getY(), this.getZ());
            case EAST:
                return new BlockPosition(this.getX() + 1, this.getY(), this.getZ());
            default:
                return new BlockPosition(this.getX() + enumdirection.getAdjacentX(), this.getY() + enumdirection.getAdjacentY(), this.getZ() + enumdirection.getAdjacentZ());
        }
        // Paper End
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

    public BlockPosition d(BaseBlockPosition baseblockposition) {
        return new BlockPosition(this.getY() * baseblockposition.getZ() - this.getZ() * baseblockposition.getY(), this.getZ() * baseblockposition.getX() - this.getX() * baseblockposition.getZ(), this.getX() * baseblockposition.getY() - this.getY() * baseblockposition.getX());
    }

    public long asLong() {
        return ((long) this.getX() & BlockPosition.i) << BlockPosition.h | ((long) this.getY() & BlockPosition.j) << BlockPosition.g | ((long) this.getZ() & BlockPosition.k) << 0;
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

    public static Iterable<BlockPosition> a(int ix, int jx, int kx, int l, int i1, int j1) { // Paper - decompile fix
        return () -> {
            return new AbstractIterator<BlockPosition>() {
                private boolean g = true;
                private int h;
                private int i;
                private int j;

                protected BlockPosition computeNext() {
                    if (this.g) {
                        this.g = false;
                        this.h = ix; // Paper - decompile fix
                        this.i = jx; // Paper - decompile fix
                        this.j = kx; // Paper - decompile fix
                        return new BlockPosition(ix, jx, kx);
                    } else if (this.h == l && this.i == i1 && this.j == j1) {
                        return (BlockPosition) this.endOfData();
                    } else {
                        if (this.h < l) {
                            ++this.h;
                        } else if (this.i < i1) {
                            this.h = ix; // Paper - decompile fix
                            ++this.i;
                        } else if (this.j < j1) {
                            this.h = ix; // Paper - decompile fix
                            this.i = jx; // Paper - decompile fix
                            ++this.j;
                        }

                        return new BlockPosition(this.h, this.i, this.j);
                    }
                }
            };
        };
    }

    public BlockPosition asImmutable() { return h(); } // Paper - OBFHELPER
    public BlockPosition h() {
        return this;
    }

    public static Iterable<BlockPosition.MutableBlockPosition> b(BlockPosition blockposition, BlockPosition blockposition1) {
        return b(Math.min(blockposition.getX(), blockposition1.getX()), Math.min(blockposition.getY(), blockposition1.getY()), Math.min(blockposition.getZ(), blockposition1.getZ()), Math.max(blockposition.getX(), blockposition1.getX()), Math.max(blockposition.getY(), blockposition1.getY()), Math.max(blockposition.getZ(), blockposition1.getZ()));
    }

    public static Iterable<BlockPosition.MutableBlockPosition> b(int i, int j, int k, int l, int i1, int j1) {
        return () -> {
            return new AbstractIterator<BlockPosition.MutableBlockPosition>() {
                private BlockPosition.MutableBlockPosition g;

                protected BlockPosition.MutableBlockPosition computeNext() {
                    if (this.g == null) {
                        this.g = new BlockPosition.MutableBlockPosition(i, j, k);
                        return this.g;
                    } else if (this.g.x == l && this.g.y == i1 && this.g.z == j1) {
                        return (BlockPosition.MutableBlockPosition) this.endOfData();
                    } else {
                        if (this.g.x < l) {
                            ++this.g.x;
                        } else if (this.g.y < i1) {
                            this.g.x = i; // Paper - decompile fix Readd line removed by the decompiler
                            ++this.g.y;
                        } else if (this.g.z < j1) {
                            this.g.x = i; // Paper - decompile fix Readd line removed by the decompiler
                            this.g.y = j; // Paper - decompile fix Readd line removed by the decompiler
                            ++this.g.z;
                        }

                        return this.g;
                    }
                }
            };
        };
    }

    public static final class b extends BlockPosition.MutableBlockPosition implements AutoCloseable {

        private boolean f;
        private static final List<BlockPosition.b> g = Lists.newArrayList();

        private b(int i, int j, int k) {
            super(i, j, k);
        }

        public static BlockPosition.b r() {
            return e(0, 0, 0);
        }

        public static BlockPosition.b b(Entity entity) {
            return d(entity.locX, entity.locY, entity.locZ);
        }

        public static BlockPosition.b d(double d0, double d1, double d2) {
            return e(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2));
        }

        public static BlockPosition.b e(int i, int j, int k) {
            synchronized (BlockPosition.b.g) {
                if (!BlockPosition.b.g.isEmpty()) {
                    BlockPosition.b blockposition_b = (BlockPosition.b) BlockPosition.b.g.remove(BlockPosition.b.g.size() - 1);

                    if (blockposition_b != null && blockposition_b.f) {
                        blockposition_b.f = false;
                        blockposition_b.c(i, j, k);
                        return blockposition_b;
                    }
                }
            }

            return new BlockPosition.b(i, j, k);
        }

        public BlockPosition.b c(int i, int j, int k) {
            return (BlockPosition.b) super.c(i, j, k);
        }

        public BlockPosition.b c(double d0, double d1, double d2) {
            return (BlockPosition.b) super.c(d0, d1, d2);
        }

        public BlockPosition.b g(BaseBlockPosition baseblockposition) {
            return (BlockPosition.b) super.g(baseblockposition);
        }

        public BlockPosition.b c(EnumDirection enumdirection) {
            return (BlockPosition.b) super.c(enumdirection);
        }

        public BlockPosition.b c(EnumDirection enumdirection, int i) {
            return (BlockPosition.b) super.c(enumdirection, i);
        }

        public BlockPosition.b d(int i, int j, int k) {
            return (BlockPosition.b) super.d(i, j, k);
        }

        public void close() {
            synchronized (BlockPosition.b.g) {
                if (BlockPosition.b.g.size() < 100) {
                    BlockPosition.b.g.add(this);
                }

                this.f = true;
            }
        }
    }

    public static class MutableBlockPosition extends BlockPosition {
        // Paper start - comment out
        /*
        protected int b;
        protected int c;
        protected int d;

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
            // Paper start
            super(i, j, k);
            /*
            this.b = i;
            this.c = j;
            this.d = k;*/
            // Paper end
        }

        public BlockPosition a(double d0, double d1, double d2) {
            return super.a(d0, d1, d2).h();
        }

        public BlockPosition a(int i, int j, int k) {
            return super.a(i, j, k).h();
        }

        public BlockPosition shift(EnumDirection enumdirection, int i) {
            return super.shift(enumdirection, i).h();
        }

        public BlockPosition a(EnumBlockRotation enumblockrotation) {
            return super.a(enumblockrotation).h();
        }

        /*
        // Paper start - use parent getters
        public int getX() {
            return this.b;
        }

        public int getY() {
            return this.c;
        }

        public int getZ() {
            return this.d;
        }*/
        // Paper end

        public BlockPosition.MutableBlockPosition setValues(int i, int j, int k) { return c(i, j, k);} // Paper - OBFHELPER
        public BlockPosition.MutableBlockPosition c(int i, int j, int k) {
            // Paper start - use xyz
            this.x = i;
            this.y = j;
            this.z = k;
            // Paper end
            return this;
        }

        public BlockPosition.MutableBlockPosition setValues(double d0, double d1, double d2) { return c(d0, d1, d2);} // Paper - OBFHELPER
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
            return this.c(x + enumdirection.getAdjacentX() * i, y + enumdirection.getAdjacentY() * i, z + enumdirection.getAdjacentZ() * i); // Paper - use xyz
        }

        public BlockPosition.MutableBlockPosition d(int i, int j, int k) {
            return this.c(x + i, y + j, z + k); // Paper - use xyz
        }

        public void p(int i) {
            this.y = i; // Paper change to y
        }

        public BlockPosition toBlockPosition() { return h(); } // Paper - OBFHELPER
        public BlockPosition h() {
            return new BlockPosition(this);
        }
    }
}
