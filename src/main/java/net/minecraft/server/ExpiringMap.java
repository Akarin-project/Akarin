package net.minecraft.server;

import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;
import java.util.function.LongFunction;

public class ExpiringMap<T> extends Long2ObjectOpenHashMap<T> {

    private final int a;
    private final Long2LongMap b = new Long2LongLinkedOpenHashMap();

    public ExpiringMap(int i, int j) {
        super(i);
        this.a = j;
    }

    private void a(long i) {
        long j = SystemUtils.getMonotonicMillis();

        this.b.put(i, j);
        // CraftBukkit start
        cleanup();
    }

    public void cleanup() {
        long j = SystemUtils.getMonotonicMillis();
        // CraftBukkit end
        ObjectIterator objectiterator = this.b.long2LongEntrySet().iterator();

        while (objectiterator.hasNext()) {
            Long2LongMap.Entry entry = (Long2LongMap.Entry) objectiterator.next(); // CraftBukkit - decompile error
            T t0 = super.get(entry.getLongKey());

            if (j - entry.getLongValue() <= (long) this.a) {
                break;
            }

            if (t0 != null && this.a(t0)) {
                super.remove(entry.getLongKey());
                objectiterator.remove();
            }
        }

    }

    protected boolean a(T t0) {
        return true;
    }

    public T put(long i, T t0) {
        this.a(i);
        return super.put(i, t0);
    }

    public T put(Long olong, T t0) {
        this.a(olong);
        return super.put(olong, t0);
    }

    public T get(long i) {
        this.a(i);
        return super.get(i);
    }

    public void putAll(Map<? extends Long, ? extends T> map) {
        throw new RuntimeException("Not implemented");
    }

    public T remove(long i) {
        throw new RuntimeException("Not implemented");
    }

    public T remove(Object object) {
        throw new RuntimeException("Not implemented");
    }

    // CraftBukkit start
    @Override
    public T computeIfAbsent(long l, LongFunction<? extends T> lf) {
        this.b.put(l, SystemUtils.getMonotonicMillis());
        return super.computeIfAbsent(l, lf);
    }

    @Override
    public ObjectCollection<T> values() {
        cleanup();
        return super.values();
    }
    // CraftBukkit end
}
