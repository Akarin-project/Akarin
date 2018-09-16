package net.minecraft.server;

import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.LongFunction;

public class ExpiringMap<T> extends Long2ObjectMaps.SynchronizedMap<T> { // paper - synchronize accesss
    private final int a;
    private final Long2LongMap ttl = new Long2LongLinkedOpenHashMap(); // Paper
    private static final boolean DEBUG_EXPIRING_MAP = Boolean.getBoolean("debug.expiringmap");

    public ExpiringMap(int i, int j) {
        super(new Long2ObjectOpenHashMap<>(i)); // Paper
        this.a = j;
    }

    // Paper start
    private void setAccess(long i) { a(i); } // Paper - OBFHELPER
    private void a(long i) {
        synchronized (this.sync) {
            long j = System.currentTimeMillis(); // Paper
            this.ttl.put(i, j);
            if (!registered) {
                registered = true;
                MinecraftServer.getServer().expiringMaps.add(this);
            }
        }
    }

    @Override
    public T compute(long l, BiFunction<? super Long, ? super T, ? extends T> biFunction) {
        setAccess(l);
        return super.compute(l, biFunction);
    }

    @Override
    public T putIfAbsent(long l, T t) {
        setAccess(l);
        return super.putIfAbsent(l, t);
    }

    @Override
    public T computeIfPresent(long l, BiFunction<? super Long, ? super T, ? extends T> biFunction) {
        setAccess(l);
        return super.computeIfPresent(l, biFunction);
    }

    @Override
    public T computeIfAbsent(long l, LongFunction<? extends T> longFunction) {
        setAccess(l);
        return super.computeIfAbsent(l, longFunction);
    }


    @Override
    public boolean replace(long l, T t, T v1) {
        setAccess(l);
        return super.replace(l, t, v1);
    }

    @Override
    public T replace(long l, T t) {
        setAccess(l);
        return super.replace(l, t);
    }

    @Override
    public T putIfAbsent(Long aLong, T t) {
        setAccess(aLong);
        return super.putIfAbsent(aLong, t);
    }

    @Override
    public boolean replace(Long aLong, T t, T v1) {
        setAccess(aLong);
        return super.replace(aLong, t, v1);
    }

    @Override
    public T replace(Long aLong, T t) {
        setAccess(aLong);
        return super.replace(aLong, t);
    }

    @Override
    public T computeIfAbsent(Long aLong, Function<? super Long, ? extends T> function) {
        setAccess(aLong);
        return super.computeIfAbsent(aLong, function);
    }

    @Override
    public T computeIfPresent(Long aLong, BiFunction<? super Long, ? super T, ? extends T> biFunction) {
        setAccess(aLong);
        return super.computeIfPresent(aLong, biFunction);
    }

    @Override
    public T compute(Long aLong, BiFunction<? super Long, ? super T, ? extends T> biFunction) {
        setAccess(aLong);
        return super.compute(aLong, biFunction);
    }

    @Override
    public void clear() {
        synchronized (this.sync) {
            ttl.clear();
            super.clear();
        }
    }

    private boolean registered = false;

    // Break clean to its own method to be ticked
    boolean clean() {
        synchronized (this.sync) {
            long now = System.currentTimeMillis();
            ObjectIterator<Long2LongMap.Entry> objectiterator = this.ttl.long2LongEntrySet().iterator(); // Paper

            while (objectiterator.hasNext()) {
                Long2LongMap.Entry entry = objectiterator.next(); // Paper
                T object = super.get(entry.getLongKey()); // Paper
                if (now - entry.getLongValue() <= (long) this.a) {
                    break;
                }

                if (object != null && this.a(object)) {
                    super.remove(entry.getLongKey());
                    objectiterator.remove();
                }
            }
            int ttlSize = this.ttl.size();
            int thisSize = this.size();
            if (ttlSize < thisSize) {
                if (DEBUG_EXPIRING_MAP) {
                    MinecraftServer.LOGGER.warn("WARNING: ExpiringMap desync (ttl:" + ttlSize + " < actual:" + thisSize + ")");
                }
                try {
                    for (Entry<T> entry : this.long2ObjectEntrySet()) {
                        ttl.putIfAbsent(entry.getLongKey(), now);
                    }
                } catch (Exception ignored) {
                } // Ignore any como's
            } else if (ttlSize > this.size()) {
                if (DEBUG_EXPIRING_MAP) {
                    MinecraftServer.LOGGER.warn("WARNING: ExpiringMap desync (ttl:" + ttlSize + " > actual:" + thisSize + ")");
                }
                try {
                    this.ttl.long2LongEntrySet().removeIf(entry -> !this.containsKey(entry.getLongKey()));
                } catch (Exception ignored) {
                } // Ignore any como's
            }
            if (isEmpty()) {
                registered = false;
                return true;
            }
            return false;
        }
        // Paper end
    }

    protected boolean a(T var1) {
        return true;
    }

    public T put(long i, T object) {
        this.a(i);
        return (T)super.put(i, object);
    }

    public T put(Long olong, T object) {
        this.a(olong);
        return (T)super.put(olong, object);
    }

    public T get(long i) {
        // Paper start - don't setAccess unless a hit
        T t = super.get(i);
        if (t != null) {
            this.setAccess(i);
        }
        return t;
        // Paper end
    }

    public void putAll(Map<? extends Long, ? extends T> var1) {
        throw new RuntimeException("Not implemented");
    }

    public T remove(long var1) {
        throw new RuntimeException("Not implemented");
    }

    public T remove(Object var1) {
        throw new RuntimeException("Not implemented");
    }

    // Paper start
    /*
    // CraftBukkit start
    @Override
    public T computeIfAbsent(long l, LongFunction<? extends T> lf) {
        this.ttl.put(l, SystemUtils.getMonotonicMillis()); // Paper
        return super.computeIfAbsent(l, lf);
    }

    @Override
    public ObjectCollection<T> values() {
        cleanup();
        return super.values();
    }
    // CraftBukkit end
    */ // Paper end
}
