package net.minecraft.server;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;

import java.util.BitSet;
import java.util.Iterator;
import javax.annotation.Nullable;

/**
 * Akarin Changes Note
 * 1) BitSet for faster access (performance)
 */
public class RegistryID<K> implements Registry {

    private static final Object a = null;
    private K[] b;
    private int[] c;
    private K[] d;
    private int e;
    private int f;
    private java.util.BitSet usedIds; // Akarin - 1.13 backport

    public RegistryID(int i) {
        i = (int) ((float) i / 0.8F);
        this.b = (K[]) (new Object[i]);
        this.c = new int[i];
        this.d = (K[]) (new Object[i]);
        this.usedIds = new BitSet(); // Akarin - 1.13 backport
    }

    public int getId(@Nullable K k0) {
        return this.c(this.b(k0, this.d(k0)));
    }

    @Nullable
    public K fromId(int i) {
        return i >= 0 && i < this.d.length ? this.d[i] : null;
    }

    private int c(int i) {
        return i == -1 ? -1 : this.c[i];
    }

    public int c(K k0) {
        int i = this.c();

        this.a(k0, i);
        return i;
    }

    private int c() {
        // Akarin start - 1.13 backport
        /*
        while (this.e < this.d.length && this.d[this.e] != null) {
            ++this.e;
        }
        */
        this.e = this.usedIds.nextClearBit(0);
        // Akarin end - 1.13 backport

        return this.e;
    }

    private void d(int i) {
        K[] aobject = this.b;
        int[] aint = this.c;

        this.b = (K[]) (new Object[i]);
        this.c = new int[i];
        this.d = (K[]) (new Object[i]);
        this.e = 0;
        this.f = 0;
        this.usedIds.clear(); // Akarin - 1.13 backport

        for (int j = 0; j < aobject.length; ++j) {
            if (aobject[j] != null) {
                this.a(aobject[j], aint[j]);
            }
        }

    }

    public void a(K k0, int i) {
        int j = Math.max(i, this.f + 1);
        int k;

        if ((float) j >= (float) this.b.length * 0.8F) {
            for (k = this.b.length << 1; k < i; k <<= 1) {
                ;
            }

            this.d(k);
        }

        k = this.e(this.d(k0));
        this.b[k] = k0;
        this.c[k] = i;
        this.d[i] = k0;
        this.usedIds.set(i); // Akarin - 1.13 backport
        ++this.f;
        if (i == this.e) {
            ++this.e;
        }

    }

    private int d(@Nullable K k0) {
        return (MathHelper.f(System.identityHashCode(k0)) & Integer.MAX_VALUE) % this.b.length;
    }

    private int b(@Nullable K k0, int i) {
        int j;

        for (j = i; j < this.b.length; ++j) {
            if (this.b[j] == k0) {
                return j;
            }

            if (this.b[j] == RegistryID.a) {
                return -1;
            }
        }

        for (j = 0; j < i; ++j) {
            if (this.b[j] == k0) {
                return j;
            }

            if (this.b[j] == RegistryID.a) {
                return -1;
            }
        }

        return -1;
    }

    private int e(int i) {
        int j;

        for (j = i; j < this.b.length; ++j) {
            if (this.b[j] == RegistryID.a) {
                return j;
            }
        }

        for (j = 0; j < i; ++j) {
            if (this.b[j] == RegistryID.a) {
                return j;
            }
        }

        throw new RuntimeException("Overflowed :(");
    }

    public Iterator<K> iterator() {
        return Iterators.filter(Iterators.forArray(this.d), Predicates.notNull());
    }

    public int b() {
        return this.f;
    }
}
