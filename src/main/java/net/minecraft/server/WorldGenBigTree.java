package net.minecraft.server;

import java.util.Random;

import org.bukkit.BlockChangeDelegate; // CraftBukkit

public class WorldGenBigTree extends WorldGenerator {

    static final byte[] a = new byte[] { (byte) 2, (byte) 0, (byte) 0, (byte) 1, (byte) 2, (byte) 1};
    Random b = new Random();
    BlockChangeDelegate c; // CraftBukkit
    int[] d = new int[] { 0, 0, 0};
    int e = 0;
    int f;
    double g = 0.618D;
    double h = 1.0D;
    double i = 0.381D;
    double j = 1.0D;
    double k = 1.0D;
    int l = 1;
    int m = 12;
    int n = 4;
    int[][] o;

    public WorldGenBigTree() {}

    void a() {
        this.f = (int) ((double) this.e * this.g);
        if (this.f >= this.e) {
            this.f = this.e - 1;
        }

        int i = (int) (1.382D + Math.pow(this.k * (double) this.e / 13.0D, 2.0D));

        if (i < 1) {
            i = 1;
        }

        int[][] aint = new int[i * this.e][4];
        int j = this.d[1] + this.e - this.n;
        int k = 1;
        int l = this.d[1] + this.f;
        int i1 = j - this.d[1];

        aint[0][0] = this.d[0];
        aint[0][1] = j;
        aint[0][2] = this.d[2];
        aint[0][3] = l;
        --j;

        while (i1 >= 0) {
            int j1 = 0;
            float f = this.a(i1);

            if (f < 0.0F) {
                --j;
                --i1;
            } else {
                for (double d0 = 0.5D; j1 < i; ++j1) {
                    double d1 = this.j * (double) f * ((double) this.b.nextFloat() + 0.328D);
                    double d2 = (double) this.b.nextFloat() * 2.0D * 3.14159D;
                    int k1 = MathHelper.floor(d1 * Math.sin(d2) + (double) this.d[0] + d0);
                    int l1 = MathHelper.floor(d1 * Math.cos(d2) + (double) this.d[2] + d0);
                    int[] aint1 = new int[] { k1, j, l1};
                    int[] aint2 = new int[] { k1, j + this.n, l1};

                    if (this.a(aint1, aint2) == -1) {
                        int[] aint3 = new int[] { this.d[0], this.d[1], this.d[2]};
                        double d3 = Math.sqrt(Math.pow((double) Math.abs(this.d[0] - aint1[0]), 2.0D) + Math.pow((double) Math.abs(this.d[2] - aint1[2]), 2.0D));
                        double d4 = d3 * this.i;

                        if ((double) aint1[1] - d4 > (double) l) {
                            aint3[1] = l;
                        } else {
                            aint3[1] = (int) ((double) aint1[1] - d4);
                        }

                        if (this.a(aint3, aint1) == -1) {
                            aint[k][0] = k1;
                            aint[k][1] = j;
                            aint[k][2] = l1;
                            aint[k][3] = aint3[1];
                            ++k;
                        }
                    }
                }

                --j;
                --i1;
            }
        }

        this.o = new int[k][4];
        System.arraycopy(aint, 0, this.o, 0, k);
    }

    void a(int i, int j, int k, float f, byte b0, int l) {
        int i1 = (int) ((double) f + 0.618D);
        byte b1 = a[b0];
        byte b2 = a[b0 + 3];
        int[] aint = new int[] { i, j, k};
        int[] aint1 = new int[] { 0, 0, 0};
        int j1 = -i1;
        int k1 = -i1;

        for (aint1[b0] = aint[b0]; j1 <= i1; ++j1) {
            aint1[b1] = aint[b1] + j1;
            k1 = -i1;

            while (k1 <= i1) {
                double d0 = Math.sqrt(Math.pow((double) Math.abs(j1) + 0.5D, 2.0D) + Math.pow((double) Math.abs(k1) + 0.5D, 2.0D));

                if (d0 > (double) f) {
                    ++k1;
                } else {
                    aint1[b2] = aint[b2] + k1;
                    int l1 = this.c.getTypeId(aint1[0], aint1[1], aint1[2]);

                    if (l1 != 0 && l1 != 18) {
                        ++k1;
                    } else {
                        this.c.setRawTypeId(aint1[0], aint1[1], aint1[2], l);
                        ++k1;
                    }
                }
            }
        }
    }

    float a(int i) {
        if ((double) i < (double) ((float) this.e) * 0.3D) {
            return -1.618F;
        } else {
            float f = (float) this.e / 2.0F;
            float f1 = (float) this.e / 2.0F - (float) i;
            float f2;

            if (f1 == 0.0F) {
                f2 = f;
            } else if (Math.abs(f1) >= f) {
                f2 = 0.0F;
            } else {
                f2 = (float) Math.sqrt(Math.pow((double) Math.abs(f), 2.0D) - Math.pow((double) Math.abs(f1), 2.0D));
            }

            f2 *= 0.5F;
            return f2;
        }
    }

    float b(int i) {
        return i >= 0 && i < this.n ? (i != 0 && i != this.n - 1 ? 3.0F : 2.0F) : -1.0F;
    }

    void a(int i, int j, int k) {
        int l = j;

        for (int i1 = j + this.n; l < i1; ++l) {
            float f = this.b(l - j);

            this.a(i, l, k, f, (byte) 1, 18);
        }
    }

    void a(int[] aint, int[] aint1, int i) {
        int[] aint2 = new int[] { 0, 0, 0};
        byte b0 = 0;

        byte b1;

        for (b1 = 0; b0 < 3; ++b0) {
            aint2[b0] = aint1[b0] - aint[b0];
            if (Math.abs(aint2[b0]) > Math.abs(aint2[b1])) {
                b1 = b0;
            }
        }

        if (aint2[b1] != 0) {
            byte b2 = a[b1];
            byte b3 = a[b1 + 3];
            byte b4;

            if (aint2[b1] > 0) {
                b4 = 1;
            } else {
                b4 = -1;
            }

            double d0 = (double) aint2[b2] / (double) aint2[b1];
            double d1 = (double) aint2[b3] / (double) aint2[b1];
            int[] aint3 = new int[] { 0, 0, 0};
            int j = 0;

            for (int k = aint2[b1] + b4; j != k; j += b4) {
                aint3[b1] = MathHelper.floor((double) (aint[b1] + j) + 0.5D);
                aint3[b2] = MathHelper.floor((double) aint[b2] + (double) j * d0 + 0.5D);
                aint3[b3] = MathHelper.floor((double) aint[b3] + (double) j * d1 + 0.5D);
                this.c.setRawTypeId(aint3[0], aint3[1], aint3[2], i);
            }
        }
    }

    void b() {
        int i = 0;

        for (int j = this.o.length; i < j; ++i) {
            int k = this.o[i][0];
            int l = this.o[i][1];
            int i1 = this.o[i][2];

            this.a(k, l, i1);
        }
    }

    boolean c(int i) {
        return (double) i >= (double) this.e * 0.2D;
    }

    void c() {
        int i = this.d[0];
        int j = this.d[1];
        int k = this.d[1] + this.f;
        int l = this.d[2];
        int[] aint = new int[] { i, j, l};
        int[] aint1 = new int[] { i, k, l};

        this.a(aint, aint1, 17);
        if (this.l == 2) {
            ++aint[0];
            ++aint1[0];
            this.a(aint, aint1, 17);
            ++aint[2];
            ++aint1[2];
            this.a(aint, aint1, 17);
            aint[0] += -1;
            aint1[0] += -1;
            this.a(aint, aint1, 17);
        }
    }

    void d() {
        int i = 0;
        int j = this.o.length;

        for (int[] aint = new int[] { this.d[0], this.d[1], this.d[2]}; i < j; ++i) {
            int[] aint1 = this.o[i];
            int[] aint2 = new int[] { aint1[0], aint1[1], aint1[2]};

            aint[1] = aint1[3];
            int k = aint[1] - this.d[1];

            if (this.c(k)) {
                this.a(aint, aint2, 17);
            }
        }
    }

    int a(int[] aint, int[] aint1) {
        int[] aint2 = new int[] { 0, 0, 0};
        byte b0 = 0;

        byte b1;

        for (b1 = 0; b0 < 3; ++b0) {
            aint2[b0] = aint1[b0] - aint[b0];
            if (Math.abs(aint2[b0]) > Math.abs(aint2[b1])) {
                b1 = b0;
            }
        }

        if (aint2[b1] == 0) {
            return -1;
        } else {
            byte b2 = a[b1];
            byte b3 = a[b1 + 3];
            byte b4;

            if (aint2[b1] > 0) {
                b4 = 1;
            } else {
                b4 = -1;
            }

            double d0 = (double) aint2[b2] / (double) aint2[b1];
            double d1 = (double) aint2[b3] / (double) aint2[b1];
            int[] aint3 = new int[] { 0, 0, 0};
            int i = 0;

            int j;

            for (j = aint2[b1] + b4; i != j; i += b4) {
                aint3[b1] = aint[b1] + i;
                aint3[b2] = MathHelper.floor((double) aint[b2] + (double) i * d0);
                aint3[b3] = MathHelper.floor((double) aint[b3] + (double) i * d1);
                int k = this.c.getTypeId(aint3[0], aint3[1], aint3[2]);

                if (k != 0 && k != 18) {
                    break;
                }
            }

            return i == j ? -1 : Math.abs(i);
        }
    }

    boolean e() {
        int[] aint = new int[] { this.d[0], this.d[1], this.d[2]};
        int[] aint1 = new int[] { this.d[0], this.d[1] + this.e - 1, this.d[2]};
        int i = this.c.getTypeId(this.d[0], this.d[1] - 1, this.d[2]);

        if (i != 2 && i != 3) {
            return false;
        } else {
            int j = this.a(aint, aint1);

            if (j == -1) {
                return true;
            } else if (j < 6) {
                return false;
            } else {
                this.e = j;
                return true;
            }
        }
    }

    public void a(double d0, double d1, double d2) {
        this.m = (int) (d0 * 12.0D);
        if (d0 > 0.5D) {
            this.n = 5;
        }

        this.j = d1;
        this.k = d2;
    }

    public boolean a(World world, Random random, int i, int j, int k) {
        // CraftBukkit start
        // sk: The idea is to have (our) WorldServer implement
        // BlockChangeDelegate and then we can implicitly cast World to
        // WorldServer (a safe cast, AFAIK) and no code will be broken. This
        // then allows plugins to catch manually-invoked generation events
        return this.generate((BlockChangeDelegate) world, random, i, j, k);
    }

    public boolean generate(BlockChangeDelegate world, Random random, int i, int j, int k) {
        // CraftBukkit end
        this.c = world;
        long l = random.nextLong();

        this.b.setSeed(l);
        this.d[0] = i;
        this.d[1] = j;
        this.d[2] = k;
        if (this.e == 0) {
            this.e = 5 + this.b.nextInt(this.m);
        }

        if (!this.e()) {
            return false;
        } else {
            this.a();
            this.b();
            this.c();
            this.d();
            return true;
        }
    }
}
