package net.minecraft.server;

import javax.annotation.Nullable;

import com.koloboke.collect.map.hash.HashIntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class IntHashMap<V> {

    private final HashIntObjMap<V> map = HashIntObjMaps.newMutableMap(); // Akarin
    @Deprecated private transient IntHashMap.IntHashMapEntry<V>[] a = (IntHashMap.IntHashMapEntry[]) (new IntHashMap.IntHashMapEntry[16]); // Akarin
    @Deprecated private transient int b; // Akarin
    @Deprecated private int c = 12; // Akarin
    private final float d = 0.75F;

    public IntHashMap() {}

    @Deprecated // Akarin
    private static int g(int i) {
        i ^= i >>> 20 ^ i >>> 12;
        return i ^ i >>> 7 ^ i >>> 4;
    }

    @Deprecated // Akarin
    private static int a(int i, int j) {
        return i & j - 1;
    }

    @Nullable
    public V get(int i) {
        // Akarin start
        /*
        int j = g(i);

        for (IntHashMap.IntHashMapEntry inthashmap_inthashmapentry = this.a[a(j, this.a.length)]; inthashmap_inthashmapentry != null; inthashmap_inthashmapentry = inthashmap_inthashmapentry.c) {
            if (inthashmap_inthashmapentry.a == i) {
                return (V) inthashmap_inthashmapentry.b;
            }
        }

        return null;
        */
        return map.get(i);
        // Akarin end
    }

    public boolean b(int i) {
        return map.containsKey(i); // Akarin
    }

    @Nullable
    @Deprecated // Akarin
    final IntHashMap.IntHashMapEntry<V> c(int i) {
        int j = g(i);

        for (IntHashMap.IntHashMapEntry inthashmap_inthashmapentry = this.a[a(j, this.a.length)]; inthashmap_inthashmapentry != null; inthashmap_inthashmapentry = inthashmap_inthashmapentry.c) {
            if (inthashmap_inthashmapentry.a == i) {
                return inthashmap_inthashmapentry;
            }
        }

        return null;
    }

    public void a(int i, V v0) {
        // Akarin start
        /*
        int j = g(i);
        int k = a(j, this.a.length);

        for (IntHashMap.IntHashMapEntry inthashmap_inthashmapentry = this.a[k]; inthashmap_inthashmapentry != null; inthashmap_inthashmapentry = inthashmap_inthashmapentry.c) {
            if (inthashmap_inthashmapentry.a == i) {
                inthashmap_inthashmapentry.b = v0;
                return;
            }
        }

        this.a(j, i, v0, k);
        */
        map.put(i, v0);
     // Akarin end
    }

    @Deprecated // Akarin
    private void h(int i) {
        IntHashMap.IntHashMapEntry<V>[] ainthashmap_inthashmapentry = this.a;
        int j = ainthashmap_inthashmapentry.length;

        if (j == 1073741824) {
            this.c = Integer.MAX_VALUE;
        } else {
            IntHashMap.IntHashMapEntry<V>[] ainthashmap_inthashmapentry1 = (IntHashMap.IntHashMapEntry[]) (new IntHashMap.IntHashMapEntry[i]);

            this.a(ainthashmap_inthashmapentry1);
            this.a = ainthashmap_inthashmapentry1;
            this.c = (int) ((float) i * this.d);
        }
    }

    @Deprecated // Akarin
    private void a(IntHashMap.IntHashMapEntry<V>[] ainthashmap_inthashmapentry) {
        IntHashMap.IntHashMapEntry<V>[] ainthashmap_inthashmapentry1 = this.a;
        int i = ainthashmap_inthashmapentry.length;

        for (int j = 0; j < ainthashmap_inthashmapentry1.length; ++j) {
            IntHashMap.IntHashMapEntry<V> inthashmap_inthashmapentry = ainthashmap_inthashmapentry1[j];

            if (inthashmap_inthashmapentry != null) {
                ainthashmap_inthashmapentry1[j] = null;

                IntHashMap.IntHashMapEntry inthashmap_inthashmapentry1;

                do {
                    inthashmap_inthashmapentry1 = inthashmap_inthashmapentry.c;
                    int k = a(inthashmap_inthashmapentry.d, i);

                    inthashmap_inthashmapentry.c = ainthashmap_inthashmapentry[k];
                    ainthashmap_inthashmapentry[k] = inthashmap_inthashmapentry;
                    inthashmap_inthashmapentry = inthashmap_inthashmapentry1;
                } while (inthashmap_inthashmapentry1 != null);
            }
        }

    }

    @Nullable
    public V d(int i) {
        // Akarin start
        /*
        IntHashMap.IntHashMapEntry<V> inthashmap_inthashmapentry = this.e(i);

        return inthashmap_inthashmapentry == null ? null : inthashmap_inthashmapentry.b;
        */
        return map.get(i);
        // Akarin end
    }

    @Nullable
    @Deprecated // Akarin
    final IntHashMap.IntHashMapEntry<V> e(int i) {
        int j = g(i);
        int k = a(j, this.a.length);
        IntHashMap.IntHashMapEntry<V> inthashmap_inthashmapentry = this.a[k];

        IntHashMap.IntHashMapEntry inthashmap_inthashmapentry1;
        IntHashMap.IntHashMapEntry inthashmap_inthashmapentry2;

        for (inthashmap_inthashmapentry2 = inthashmap_inthashmapentry; inthashmap_inthashmapentry2 != null; inthashmap_inthashmapentry2 = inthashmap_inthashmapentry1) {
            inthashmap_inthashmapentry1 = inthashmap_inthashmapentry2.c;
            if (inthashmap_inthashmapentry2.a == i) {
                --this.b;
                if (inthashmap_inthashmapentry == inthashmap_inthashmapentry2) {
                    this.a[k] = inthashmap_inthashmapentry1;
                } else {
                    inthashmap_inthashmapentry.c = inthashmap_inthashmapentry1;
                }

                return inthashmap_inthashmapentry2;
            }

            inthashmap_inthashmapentry = inthashmap_inthashmapentry2;
        }

        return inthashmap_inthashmapentry2;
    }

    public void c() {
        // Akarin start
        /*
        IntHashMap.IntHashMapEntry<V>[] ainthashmap_inthashmapentry = this.a;

        for (int i = 0; i < ainthashmap_inthashmapentry.length; ++i) {
            ainthashmap_inthashmapentry[i] = null;
        }

        this.b = 0;
        */
        map.clear();
        // Akarin end
    }

    @Deprecated // Akarin
    private void a(int i, int j, V v0, int k) {
        IntHashMap.IntHashMapEntry<V> inthashmap_inthashmapentry = this.a[k];

        this.a[k] = new IntHashMap.IntHashMapEntry<>(i, j, v0, inthashmap_inthashmapentry);
        if (this.b++ >= this.c) {
            this.h(2 * this.a.length);
        }

    }

    @Deprecated // Akarin
    static class IntHashMapEntry<V> {

        private final int a;
        private V b;
        private IntHashMap.IntHashMapEntry<V> c;
        private final int d;

        IntHashMapEntry(int i, int j, V v0, IntHashMap.IntHashMapEntry<V> inthashmap_inthashmapentry) {
            this.b = v0;
            this.c = inthashmap_inthashmapentry;
            this.a = j;
            this.d = i;
        }

        public final int a() {
            return this.a;
        }

        public final V b() {
            return this.b;
        }

        public final boolean equals(Object object) {
            if (!(object instanceof IntHashMap.IntHashMapEntry)) {
                return false;
            } else {
                IntHashMap.IntHashMapEntry<V> inthashmap_inthashmapentry = (IntHashMap.IntHashMapEntry) object;

                if (this.a == inthashmap_inthashmapentry.a) {
                    Object object1 = this.b();
                    Object object2 = inthashmap_inthashmapentry.b();

                    if (object1 == object2 || object1 != null && object1.equals(object2)) {
                        return true;
                    }
                }

                return false;
            }
        }

        public final int hashCode() {
            return IntHashMap.g(this.a);
        }

        public final String toString() {
            return this.a() + "=" + this.b();
        }
    }
}
