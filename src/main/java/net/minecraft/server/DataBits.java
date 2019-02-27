package net.minecraft.server;

import org.apache.commons.lang3.Validate;

public class DataBits {

    private final long[] a;
    private final int b;
    private final long c;
    private final int d;

    public DataBits(int i, int j) {
        this(i, j, new long[MathHelper.c(j * i, 64) / 64]);
    }

    public DataBits(int i, int j, long[] along) {
        Validate.inclusiveBetween(1L, 32L, (long) i);
        this.d = j;
        this.b = i;
        this.a = along;
        this.c = (1L << i) - 1L;
        int k = MathHelper.c(j * i, 64) / 64;

        if (along.length != k) {
            throw new RuntimeException("Invalid length given for storage, got: " + along.length + " but expected: " + k);
        }
    }

    public void a(int i, int j) {
        Validate.inclusiveBetween(0L, (long) (this.d - 1), (long) i);
        Validate.inclusiveBetween(0L, this.c, (long) j);
        int k = i * this.b;
        int l = k / 64;
        int i1 = ((i + 1) * this.b - 1) / 64;
        int j1 = k % 64;

        this.a[l] = this.a[l] & ~(this.c << j1) | ((long) j & this.c) << j1;
        if (l != i1) {
            int k1 = 64 - j1;
            int l1 = this.b - k1;

            this.a[i1] = this.a[i1] >>> l1 << l1 | ((long) j & this.c) >> k1;
        }

    }

    public int a(int i) {
        Validate.inclusiveBetween(0L, (long) (this.d - 1), (long) i);
        int j = i * this.b;
        int k = j / 64;
        int l = ((i + 1) * this.b - 1) / 64;
        int i1 = j % 64;

        if (k == l) {
            return (int) (this.a[k] >>> i1 & this.c);
        } else {
            int j1 = 64 - i1;

            return (int) ((this.a[k] >>> i1 | this.a[l] << j1) & this.c);
        }
    }

    public long[] a() {
        return this.a;
    }

    public int b() {
        return this.d;
    }

    public int c() {
        return this.b;
    }
}
