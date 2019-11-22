package net.minecraft.server;

import com.google.common.base.MoreObjects;

public abstract class BlockState<T extends Comparable<T>> implements IBlockState<T> {

    private final Class<T> a;
    private final String b;
    private Integer c;

    protected BlockState(String s, Class<T> oclass) {
        this.a = oclass;
        this.b = s;
    }

    @Override
    public String a() {
        return this.b;
    }

    @Override
    public Class<T> b() {
        return this.a;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("name", this.b).add("clazz", this.a).add("values", this.getValues()).toString();
    }

    public boolean equals(Object object) {
        return this == object; // Paper - only one instance per configuration
    }

    private static final java.util.concurrent.atomic.AtomicInteger hashId = new java.util.concurrent.atomic.AtomicInteger(1); // Paper - only one instance per configuration
    private final int hashCode = 92821 * hashId.getAndIncrement(); // Paper - only one instance per configuration
    public final int hashCode() {
        return this.hashCode; // Paper - only one instance per configuration
    }

    public int c() {
        return 31 * this.a.hashCode() + this.b.hashCode();
    }
}
