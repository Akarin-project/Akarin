package net.minecraft.server;

import java.util.function.IntConsumer;
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
        //Validate.inclusiveBetween(1L, 32L, (long) i); // Paper
        this.d = j;
        this.b = i;
        this.a = along;
        this.c = (1L << i) - 1L;
        int k = MathHelper.c(j * i, 64) / 64;

        if (along.length != k) {
            throw new RuntimeException("Invalid length given for storage, got: " + along.length + " but expected: " + k);
        }
    }

    public int a(int i, int j) {
        //Validate.inclusiveBetween(0L, (long) (this.d - 1), (long) i); // Paper
        //Validate.inclusiveBetween(0L, this.c, (long) j); // Paper
        int k = i * this.b;
        int l = k >> 6;
        int i1 = (i + 1) * this.b - 1 >> 6;
        int j1 = k ^ l << 6;
        byte b0 = 0;
        int k1 = b0 | (int) (this.a[l] >>> j1 & this.c);

        this.a[l] = this.a[l] & ~(this.c << j1) | ((long) j & this.c) << j1;
        if (l != i1) {
            int l1 = 64 - j1;
            int i2 = this.b - l1;

            k1 |= (int) (this.a[i1] << l1 & this.c);
            this.a[i1] = this.a[i1] >>> i2 << i2 | ((long) j & this.c) >> l1;
        }

        return k1;
    }

    public void b(int i, int j) {
        //Validate.inclusiveBetween(0L, (long) (this.d - 1), (long) i); // Paper
        //Validate.inclusiveBetween(0L, this.c, (long) j); // Paper
        int k = i * this.b;
        int l = k >> 6;
        int i1 = (i + 1) * this.b - 1 >> 6;
        int j1 = k ^ l << 6;

        this.a[l] = this.a[l] & ~(this.c << j1) | ((long) j & this.c) << j1;
        if (l != i1) {
            int k1 = 64 - j1;
            int l1 = this.b - k1;

            this.a[i1] = this.a[i1] >>> l1 << l1 | ((long) j & this.c) >> k1;
        }

    }

    public int a(int i) {
        //Validate.inclusiveBetween(0L, (long) (this.d - 1), (long) i); // Paper
        int j = i * this.b;
        int k = j >> 6;
        int l = (i + 1) * this.b - 1 >> 6;
        int i1 = j ^ k << 6;

        if (k == l) {
            return (int) (this.a[k] >>> i1 & this.c);
        } else {
            int j1 = 64 - i1;

            return (int) ((this.a[k] >>> i1 | this.a[l] << j1) & this.c);
        }
    }

    public long[] getDataBits() { return this.a(); } // Paper - OBFHELPER
    public long[] a() {
        return this.a;
    }

    public int b() {
        return this.d;
    }

    public int c() {
        return this.b;
    }

    public void a(IntConsumer intconsumer) {
        int i = this.a.length;

        if (i != 0) {
            int j = 0;
            long k = this.a[0];
            long l = i > 1 ? this.a[1] : 0L;

            for (int i1 = 0; i1 < this.d; ++i1) {
                int j1 = i1 * this.b;
                int k1 = j1 >> 6;
                int l1 = (i1 + 1) * this.b - 1 >> 6;
                int i2 = j1 ^ k1 << 6;

                if (k1 != j) {
                    k = l;
                    l = k1 + 1 < i ? this.a[k1 + 1] : 0L;
                    j = k1;
                }

                if (k1 == l1) {
                    intconsumer.accept((int) (k >>> i2 & this.c));
                } else {
                    int j2 = 64 - i2;

                    intconsumer.accept((int) ((k >>> i2 | l << j2) & this.c));
                }
            }

        }
    }
}
