package net.minecraft.server;

import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public enum EnumDirection implements INamable {

    DOWN(0, 1, -1, "down", EnumDirection.EnumAxisDirection.NEGATIVE, EnumDirection.EnumAxis.Y, new BaseBlockPosition(0, -1, 0)), UP(1, 0, -1, "up", EnumDirection.EnumAxisDirection.POSITIVE, EnumDirection.EnumAxis.Y, new BaseBlockPosition(0, 1, 0)), NORTH(2, 3, 2, "north", EnumDirection.EnumAxisDirection.NEGATIVE, EnumDirection.EnumAxis.Z, new BaseBlockPosition(0, 0, -1)), SOUTH(3, 2, 0, "south", EnumDirection.EnumAxisDirection.POSITIVE, EnumDirection.EnumAxis.Z, new BaseBlockPosition(0, 0, 1)), WEST(4, 5, 1, "west", EnumDirection.EnumAxisDirection.NEGATIVE, EnumDirection.EnumAxis.X, new BaseBlockPosition(-1, 0, 0)), EAST(5, 4, 3, "east", EnumDirection.EnumAxisDirection.POSITIVE, EnumDirection.EnumAxis.X, new BaseBlockPosition(1, 0, 0));

    private final int g;
    private final int h;
    private final int i;
    private final String j;
    private final EnumDirection.EnumAxis k;
    private final EnumDirection.EnumAxisDirection l;
    private final BaseBlockPosition m;
    private static final EnumDirection[] n = values();
    private static final Map<String, EnumDirection> o = (Map) Arrays.stream(EnumDirection.n).collect(Collectors.toMap(EnumDirection::j, (enumdirection) -> {
        return enumdirection;
    }));
    private static final EnumDirection[] p = (EnumDirection[]) Arrays.stream(EnumDirection.n).sorted(Comparator.comparingInt((enumdirection) -> {
        return enumdirection.g;
    })).toArray((i) -> {
        return new EnumDirection[i];
    });
    private static final EnumDirection[] q = (EnumDirection[]) Arrays.stream(EnumDirection.n).filter((enumdirection) -> {
        return enumdirection.k().c();
    }).sorted(Comparator.comparingInt((enumdirection) -> {
        return enumdirection.i;
    })).toArray((i) -> {
        return new EnumDirection[i];
    });

    private EnumDirection(int i, int j, int k, String s, EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection, EnumDirection.EnumAxis enumdirection_enumaxis, BaseBlockPosition baseblockposition) {
        this.g = i;
        this.i = k;
        this.h = j;
        this.j = s;
        this.k = enumdirection_enumaxis;
        this.l = enumdirection_enumaxisdirection;
        this.m = baseblockposition;
    }

    public static EnumDirection[] a(Entity entity) {
        float f = entity.g(1.0F) * 0.017453292F;
        float f1 = -entity.h(1.0F) * 0.017453292F;
        float f2 = MathHelper.sin(f);
        float f3 = MathHelper.cos(f);
        float f4 = MathHelper.sin(f1);
        float f5 = MathHelper.cos(f1);
        boolean flag = f4 > 0.0F;
        boolean flag1 = f2 < 0.0F;
        boolean flag2 = f5 > 0.0F;
        float f6 = flag ? f4 : -f4;
        float f7 = flag1 ? -f2 : f2;
        float f8 = flag2 ? f5 : -f5;
        float f9 = f6 * f3;
        float f10 = f8 * f3;
        EnumDirection enumdirection = flag ? EnumDirection.EAST : EnumDirection.WEST;
        EnumDirection enumdirection1 = flag1 ? EnumDirection.UP : EnumDirection.DOWN;
        EnumDirection enumdirection2 = flag2 ? EnumDirection.SOUTH : EnumDirection.NORTH;

        return f6 > f8 ? (f7 > f9 ? a(enumdirection1, enumdirection, enumdirection2) : (f10 > f7 ? a(enumdirection, enumdirection2, enumdirection1) : a(enumdirection, enumdirection1, enumdirection2))) : (f7 > f10 ? a(enumdirection1, enumdirection2, enumdirection) : (f9 > f7 ? a(enumdirection2, enumdirection, enumdirection1) : a(enumdirection2, enumdirection1, enumdirection)));
    }

    private static EnumDirection[] a(EnumDirection enumdirection, EnumDirection enumdirection1, EnumDirection enumdirection2) {
        return new EnumDirection[] { enumdirection, enumdirection1, enumdirection2, enumdirection2.opposite(), enumdirection1.opposite(), enumdirection.opposite()};
    }

    public int a() {
        return this.g;
    }

    public int get2DRotationValue() {
        return this.i;
    }

    public EnumDirection.EnumAxisDirection c() {
        return this.l;
    }

    public EnumDirection opposite() {
        return fromType1(this.h);
    }

    public EnumDirection e() {
        switch (this) {
        case NORTH:
            return EnumDirection.EAST;
        case EAST:
            return EnumDirection.SOUTH;
        case SOUTH:
            return EnumDirection.WEST;
        case WEST:
            return EnumDirection.NORTH;
        default:
            throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
        }
    }

    public EnumDirection f() {
        switch (this) {
        case NORTH:
            return EnumDirection.WEST;
        case EAST:
            return EnumDirection.NORTH;
        case SOUTH:
            return EnumDirection.EAST;
        case WEST:
            return EnumDirection.SOUTH;
        default:
            throw new IllegalStateException("Unable to get CCW facing of " + this);
        }
    }

    public int getAdjacentX() {
        return this.k == EnumDirection.EnumAxis.X ? this.l.a() : 0;
    }

    public int getAdjacentY() {
        return this.k == EnumDirection.EnumAxis.Y ? this.l.a() : 0;
    }

    public int getAdjacentZ() {
        return this.k == EnumDirection.EnumAxis.Z ? this.l.a() : 0;
    }

    public String j() {
        return this.j;
    }

    public EnumDirection.EnumAxis k() {
        return this.k;
    }

    public static EnumDirection fromType1(int i) {
        return EnumDirection.p[MathHelper.a(i % EnumDirection.p.length)];
    }

    public static EnumDirection fromType2(int i) {
        return EnumDirection.q[MathHelper.a(i % EnumDirection.q.length)];
    }

    public static EnumDirection fromAngle(double d0) {
        return fromType2(MathHelper.floor(d0 / 90.0D + 0.5D) & 3);
    }

    public static EnumDirection a(EnumDirection.EnumAxis enumdirection_enumaxis, EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection) {
        switch (enumdirection_enumaxis) {
        case X:
            return enumdirection_enumaxisdirection == EnumDirection.EnumAxisDirection.POSITIVE ? EnumDirection.EAST : EnumDirection.WEST;
        case Y:
            return enumdirection_enumaxisdirection == EnumDirection.EnumAxisDirection.POSITIVE ? EnumDirection.UP : EnumDirection.DOWN;
        case Z:
        default:
            return enumdirection_enumaxisdirection == EnumDirection.EnumAxisDirection.POSITIVE ? EnumDirection.SOUTH : EnumDirection.NORTH;
        }
    }

    public float l() {
        return (float) ((this.i & 3) * 90);
    }

    public static EnumDirection a(Random random) {
        return values()[random.nextInt(values().length)];
    }

    public static EnumDirection a(double d0, double d1, double d2) {
        return a((float) d0, (float) d1, (float) d2);
    }

    public static EnumDirection a(float f, float f1, float f2) {
        EnumDirection enumdirection = EnumDirection.NORTH;
        float f3 = Float.MIN_VALUE;
        EnumDirection[] aenumdirection = EnumDirection.n;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection1 = aenumdirection[j];
            float f4 = f * (float) enumdirection1.m.getX() + f1 * (float) enumdirection1.m.getY() + f2 * (float) enumdirection1.m.getZ();

            if (f4 > f3) {
                f3 = f4;
                enumdirection = enumdirection1;
            }
        }

        return enumdirection;
    }

    public String toString() {
        return this.j;
    }

    public String getName() {
        return this.j;
    }

    public static EnumDirection a(EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection, EnumDirection.EnumAxis enumdirection_enumaxis) {
        EnumDirection[] aenumdirection = values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (enumdirection.c() == enumdirection_enumaxisdirection && enumdirection.k() == enumdirection_enumaxis) {
                return enumdirection;
            }
        }

        throw new IllegalArgumentException("No such direction: " + enumdirection_enumaxisdirection + " " + enumdirection_enumaxis);
    }

    public static enum EnumDirectionLimit implements Iterable<EnumDirection>, Predicate<EnumDirection> {

        HORIZONTAL(new EnumDirection[] { EnumDirection.NORTH, EnumDirection.EAST, EnumDirection.SOUTH, EnumDirection.WEST}, new EnumDirection.EnumAxis[] { EnumDirection.EnumAxis.X, EnumDirection.EnumAxis.Z}), VERTICAL(new EnumDirection[] { EnumDirection.UP, EnumDirection.DOWN}, new EnumDirection.EnumAxis[] { EnumDirection.EnumAxis.Y});

        private final EnumDirection[] c;
        private final EnumDirection.EnumAxis[] d;

        private EnumDirectionLimit(EnumDirection[] aenumdirection, EnumDirection.EnumAxis[] aenumdirection_enumaxis) {
            this.c = aenumdirection;
            this.d = aenumdirection_enumaxis;
        }

        public EnumDirection a(Random random) {
            return this.c[random.nextInt(this.c.length)];
        }

        public boolean test(@Nullable EnumDirection enumdirection) {
            return enumdirection != null && enumdirection.k().d() == this;
        }

        public Iterator<EnumDirection> iterator() {
            return Iterators.forArray(this.c);
        }
    }

    public static enum EnumAxisDirection {

        POSITIVE(1, "Towards positive"), NEGATIVE(-1, "Towards negative");

        private final int c;
        private final String d;

        private EnumAxisDirection(int i, String s) {
            this.c = i;
            this.d = s;
        }

        public int a() {
            return this.c;
        }

        public String toString() {
            return this.d;
        }
    }

    public static enum EnumAxis implements Predicate<EnumDirection>, INamable {

        X("x") {
            public int a(int i, int j, int k) {
                return i;
            }

            public double a(double d0, double d1, double d2) {
                return d0;
            }
        },
        Y("y") {
            public int a(int i, int j, int k) {
                return j;
            }

            public double a(double d0, double d1, double d2) {
                return d1;
            }
        },
        Z("z") {
            public int a(int i, int j, int k) {
                return k;
            }

            public double a(double d0, double d1, double d2) {
                return d2;
            }
        };

        private static final Map<String, EnumDirection.EnumAxis> d = (Map) Arrays.stream(values()).collect(Collectors.toMap(EnumDirection.EnumAxis::a, (enumdirection_enumaxis) -> {
            return enumdirection_enumaxis;
        }));
        private final String e;

        private EnumAxis(String s) {
            this.e = s;
        }

        public String a() {
            return this.e;
        }

        public boolean b() {
            return this == EnumDirection.EnumAxis.Y;
        }

        public boolean c() {
            return this == EnumDirection.EnumAxis.X || this == EnumDirection.EnumAxis.Z;
        }

        public String toString() {
            return this.e;
        }

        public boolean test(@Nullable EnumDirection enumdirection) {
            return enumdirection != null && enumdirection.k() == this;
        }

        public EnumDirection.EnumDirectionLimit d() {
            switch (this) {
            case X:
            case Z:
                return EnumDirection.EnumDirectionLimit.HORIZONTAL;
            case Y:
                return EnumDirection.EnumDirectionLimit.VERTICAL;
            default:
                throw new Error("Someone's been tampering with the universe!");
            }
        }

        public String getName() {
            return this.e;
        }

        public abstract int a(int i, int j, int k);

        public abstract double a(double d0, double d1, double d2);
    }
}
