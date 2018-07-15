package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

/**
 * Akarin Changes Note
 * 1) Add OBFHELPER (panda redstone)
 */
public enum EnumDirection implements INamable {

    DOWN(0, 1, -1, "down", EnumDirection.EnumAxisDirection.NEGATIVE, EnumDirection.EnumAxis.Y, new BaseBlockPosition(0, -1, 0)), UP(1, 0, -1, "up", EnumDirection.EnumAxisDirection.POSITIVE, EnumDirection.EnumAxis.Y, new BaseBlockPosition(0, 1, 0)), NORTH(2, 3, 2, "north", EnumDirection.EnumAxisDirection.NEGATIVE, EnumDirection.EnumAxis.Z, new BaseBlockPosition(0, 0, -1)), SOUTH(3, 2, 0, "south", EnumDirection.EnumAxisDirection.POSITIVE, EnumDirection.EnumAxis.Z, new BaseBlockPosition(0, 0, 1)), WEST(4, 5, 1, "west", EnumDirection.EnumAxisDirection.NEGATIVE, EnumDirection.EnumAxis.X, new BaseBlockPosition(-1, 0, 0)), EAST(5, 4, 3, "east", EnumDirection.EnumAxisDirection.POSITIVE, EnumDirection.EnumAxis.X, new BaseBlockPosition(1, 0, 0));

    private final int g;
    private final int h;
    private final int i;
    private final String j;
    private final EnumDirection.EnumAxis k;
    private final EnumDirection.EnumAxisDirection l;
    private final BaseBlockPosition m; public BaseBlockPosition getDirectionPosition() { return m; } // Akarin - OBFHELPER
    private static final EnumDirection[] n = new EnumDirection[6];
    private static final EnumDirection[] o = new EnumDirection[4];
    private static final Map<String, EnumDirection> p = Maps.newHashMap();

    private EnumDirection(int i, int j, int k, String s, EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection, EnumDirection.EnumAxis enumdirection_enumaxis, BaseBlockPosition baseblockposition) {
        this.g = i;
        this.i = k;
        this.h = j;
        this.j = s;
        this.k = enumdirection_enumaxis;
        this.l = enumdirection_enumaxisdirection;
        this.m = baseblockposition;
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

    public EnumDirection.EnumAxis getAxis() { return k(); } // Akarin - OBFHELPER
    public EnumDirection.EnumAxis k() {
        return this.k;
    }

    public static EnumDirection fromType1(int i) {
        return EnumDirection.n[MathHelper.a(i % EnumDirection.n.length)];
    }

    public static EnumDirection fromType2(int i) {
        return EnumDirection.o[MathHelper.a(i % EnumDirection.o.length)];
    }

    public static EnumDirection fromAngle(double d0) {
        return fromType2(MathHelper.floor(d0 / 90.0D + 0.5D) & 3);
    }

    public float l() {
        return (this.i & 3) * 90;
    }

    public static EnumDirection a(Random random) {
        return values()[random.nextInt(values().length)];
    }

    @Override
    public String toString() {
        return this.j;
    }

    @Override
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

    public static EnumDirection a(BlockPosition blockposition, EntityLiving entityliving) {
        if (Math.abs(entityliving.locX - (blockposition.getX() + 0.5F)) < 2.0D && Math.abs(entityliving.locZ - (blockposition.getZ() + 0.5F)) < 2.0D) {
            double d0 = entityliving.locY + entityliving.getHeadHeight();

            if (d0 - blockposition.getY() > 2.0D) {
                return EnumDirection.UP;
            }

            if (blockposition.getY() - d0 > 0.0D) {
                return EnumDirection.DOWN;
            }
        }

        return entityliving.getDirection().opposite();
    }

    static {
        EnumDirection[] aenumdirection = values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            EnumDirection.n[enumdirection.g] = enumdirection;
            if (enumdirection.k().c()) {
                EnumDirection.o[enumdirection.i] = enumdirection;
            }

            EnumDirection.p.put(enumdirection.j().toLowerCase(Locale.ROOT), enumdirection);
        }

    }

    public static enum EnumDirectionLimit implements Predicate<EnumDirection>, Iterable<EnumDirection> {

        HORIZONTAL, VERTICAL;

        private EnumDirectionLimit() {}

        public EnumDirection[] a() {
            switch (this) {
            case HORIZONTAL:
                return new EnumDirection[] { EnumDirection.NORTH, EnumDirection.EAST, EnumDirection.SOUTH, EnumDirection.WEST};

            case VERTICAL:
                return new EnumDirection[] { EnumDirection.UP, EnumDirection.DOWN};

            default:
                throw new Error("Someone\'s been tampering with the universe!");
            }
        }

        public EnumDirection a(Random random) {
            EnumDirection[] aenumdirection = this.a();

            return aenumdirection[random.nextInt(aenumdirection.length)];
        }

        public boolean a(@Nullable EnumDirection enumdirection) {
            return enumdirection != null && enumdirection.k().d() == this;
        }

        @Override
        public Iterator<EnumDirection> iterator() {
            return Iterators.forArray(this.a());
        }

        @Override
        public boolean apply(@Nullable EnumDirection object) {
            return this.a(object);
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

        @Override
        public String toString() {
            return this.d;
        }
    }

    public static enum EnumAxis implements Predicate<EnumDirection>, INamable {

        X("x", EnumDirection.EnumDirectionLimit.HORIZONTAL), Y("y", EnumDirection.EnumDirectionLimit.VERTICAL), Z("z", EnumDirection.EnumDirectionLimit.HORIZONTAL);

        private static final Map<String, EnumDirection.EnumAxis> d = Maps.newHashMap();
        private final String e;
        private final EnumDirection.EnumDirectionLimit f;

        private EnumAxis(String s, EnumDirection.EnumDirectionLimit enumdirection_enumdirectionlimit) {
            this.e = s;
            this.f = enumdirection_enumdirectionlimit;
        }

        public String a() {
            return this.e;
        }

        public boolean b() {
            return this.f == EnumDirection.EnumDirectionLimit.VERTICAL;
        }

        public boolean isHorizontal() { return c(); } // Akarin - OBFHELPER
        public boolean c() {
            return this.f == EnumDirection.EnumDirectionLimit.HORIZONTAL;
        }

        @Override
        public String toString() {
            return this.e;
        }

        public boolean a(@Nullable EnumDirection enumdirection) {
            return enumdirection != null && enumdirection.k() == this;
        }

        public EnumDirection.EnumDirectionLimit d() {
            return this.f;
        }

        @Override
        public String getName() {
            return this.e;
        }

        @Override
        public boolean apply(@Nullable EnumDirection object) {
            return this.a(object);
        }

        static {
            EnumDirection.EnumAxis[] aenumdirection_enumaxis = values();
            int i = aenumdirection_enumaxis.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection.EnumAxis enumdirection_enumaxis = aenumdirection_enumaxis[j];

                EnumDirection.EnumAxis.d.put(enumdirection_enumaxis.a().toLowerCase(Locale.ROOT), enumdirection_enumaxis);
            }

        }
    }
}
