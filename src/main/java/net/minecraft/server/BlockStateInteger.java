package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class BlockStateInteger extends BlockState<Integer> {

    private final ImmutableSet<Integer> a;
    // CraftBukkit start
    public final int min;
    public final int max;

    protected BlockStateInteger(String s, int i, int j) {
        super(s, Integer.class);
        this.min = i;
        this.max = j;
        // CraftBukkit end
        if (i < 0) {
            throw new IllegalArgumentException("Min value of " + s + " must be 0 or greater");
        } else if (j <= i) {
            throw new IllegalArgumentException("Max value of " + s + " must be greater than min (" + i + ")");
        } else {
            Set<Integer> set = Sets.newHashSet();

            for (int k = i; k <= j; ++k) {
                set.add(k);
            }

            this.a = ImmutableSet.copyOf(set);
        }
    }

    public Collection<Integer> d() {
        return this.a;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof BlockStateInteger && super.equals(object)) {
            BlockStateInteger blockstateinteger = (BlockStateInteger) object;

            return this.a.equals(blockstateinteger.a);
        } else {
            return false;
        }
    }

    public int c() {
        return 31 * super.c() + this.a.hashCode();
    }

    public static BlockStateInteger of(String s, int i, int j) {
        return new BlockStateInteger(s, i, j);
    }

    public Optional<Integer> b(String s) {
        try {
            Integer integer = Integer.valueOf(s);

            return this.a.contains(integer) ? Optional.of(integer) : Optional.empty();
        } catch (NumberFormatException numberformatexception) {
            return Optional.empty();
        }
    }

    public String a(Integer integer) {
        return integer.toString();
    }
}
