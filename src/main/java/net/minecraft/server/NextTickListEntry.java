package net.minecraft.server;

import java.util.Comparator;

public class NextTickListEntry<T> {

    private static final java.util.concurrent.atomic.AtomicLong COUNTER = new java.util.concurrent.atomic.AtomicLong(); // Paper - async chunk loading
    private final T e;
    public final BlockPosition a;
    public final long b;
    public final TickListPriority c;
    private final long f;

    public NextTickListEntry(BlockPosition blockposition, T t0) {
        this(blockposition, t0, 0L, TickListPriority.NORMAL);
    }

    public NextTickListEntry(BlockPosition blockposition, T t0, long i, TickListPriority ticklistpriority) {
        this.f = (long) (NextTickListEntry.COUNTER.getAndIncrement()); // Paper - async chunk loading
        this.a = blockposition.immutableCopy();
        this.e = t0;
        this.b = i;
        this.c = ticklistpriority;
    }

    public boolean equals(Object object) {
        if (!(object instanceof NextTickListEntry)) {
            return false;
        } else {
            NextTickListEntry<?> nextticklistentry = (NextTickListEntry) object;

            return this.a.equals(nextticklistentry.a) && this.e == nextticklistentry.e;
        }
    }

    public int hashCode() {
        return this.a.hashCode();
    }

    public static <T> Comparator<NextTickListEntry<T>> a() {
        return (nextticklistentry, nextticklistentry1) -> {
            int i = Long.compare(nextticklistentry.b, nextticklistentry1.b);

            if (i != 0) {
                return i;
            } else {
                i = nextticklistentry.c.compareTo(nextticklistentry1.c);
                return i != 0 ? i : Long.compare(nextticklistentry.f, nextticklistentry1.f);
            }
        };
    }

    public String toString() {
        return this.e + ": " + this.a + ", " + this.b + ", " + this.c + ", " + this.f;
    }

    public T b() {
        return this.e;
    }
}
