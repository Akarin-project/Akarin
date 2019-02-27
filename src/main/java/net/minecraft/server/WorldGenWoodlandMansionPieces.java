package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class WorldGenWoodlandMansionPieces {

    public static void a() {
        WorldGenFactory.a(WorldGenWoodlandMansionPieces.i.class, "WMP");
    }

    public static void a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, List<WorldGenWoodlandMansionPieces.i> list, Random random) {
        WorldGenWoodlandMansionPieces.c worldgenwoodlandmansionpieces_c = new WorldGenWoodlandMansionPieces.c(random);
        WorldGenWoodlandMansionPieces.d worldgenwoodlandmansionpieces_d = new WorldGenWoodlandMansionPieces.d(definedstructuremanager, random);

        worldgenwoodlandmansionpieces_d.a(blockposition, enumblockrotation, list, worldgenwoodlandmansionpieces_c);
    }

    static class h extends WorldGenWoodlandMansionPieces.f {

        private h() {
            super(null);
        }
    }

    static class f extends WorldGenWoodlandMansionPieces.b {

        private f() {
            super(null);
        }

        public String a(Random random) {
            return "1x1_b" + (random.nextInt(4) + 1);
        }

        public String b(Random random) {
            return "1x1_as" + (random.nextInt(4) + 1);
        }

        public String a(Random random, boolean flag) {
            return flag ? "1x2_c_stairs" : "1x2_c" + (random.nextInt(4) + 1);
        }

        public String b(Random random, boolean flag) {
            return flag ? "1x2_d_stairs" : "1x2_d" + (random.nextInt(5) + 1);
        }

        public String c(Random random) {
            return "1x2_se" + (random.nextInt(1) + 1);
        }

        public String d(Random random) {
            return "2x2_b" + (random.nextInt(5) + 1);
        }

        public String e(Random random) {
            return "2x2_s1";
        }
    }

    static class a extends WorldGenWoodlandMansionPieces.b {

        private a() {
            super(null);
        }

        public String a(Random random) {
            return "1x1_a" + (random.nextInt(5) + 1);
        }

        public String b(Random random) {
            return "1x1_as" + (random.nextInt(4) + 1);
        }

        public String a(Random random, boolean flag) {
            return "1x2_a" + (random.nextInt(9) + 1);
        }

        public String b(Random random, boolean flag) {
            return "1x2_b" + (random.nextInt(5) + 1);
        }

        public String c(Random random) {
            return "1x2_s" + (random.nextInt(2) + 1);
        }

        public String d(Random random) {
            return "2x2_a" + (random.nextInt(4) + 1);
        }

        public String e(Random random) {
            return "2x2_s1";
        }
    }

    abstract static class b {

        private b() {}

        public abstract String a(Random random);

        public abstract String b(Random random);

        public abstract String a(Random random, boolean flag);

        public abstract String b(Random random, boolean flag);

        public abstract String c(Random random);

        public abstract String d(Random random);

        public abstract String e(Random random);
    }

    static class g {

        private final int[][] a;
        private final int b;
        private final int c;
        private final int d;

        public g(int i, int j, int k) {
            this.b = i;
            this.c = j;
            this.d = k;
            this.a = new int[i][j];
        }

        public void a(int i, int j, int k) {
            if (i >= 0 && i < this.b && j >= 0 && j < this.c) {
                this.a[i][j] = k;
            }

        }

        public void a(int i, int j, int k, int l, int i1) {
            for (int j1 = j; j1 <= l; ++j1) {
                for (int k1 = i; k1 <= k; ++k1) {
                    this.a(k1, j1, i1);
                }
            }

        }

        public int a(int i, int j) {
            return i >= 0 && i < this.b && j >= 0 && j < this.c ? this.a[i][j] : this.d;
        }

        public void a(int i, int j, int k, int l) {
            if (this.a(i, j) == k) {
                this.a(i, j, l);
            }

        }

        public boolean b(int i, int j, int k) {
            return this.a(i - 1, j) == k || this.a(i + 1, j) == k || this.a(i, j + 1) == k || this.a(i, j - 1) == k;
        }
    }

    static class c {

        private final Random a;
        private final WorldGenWoodlandMansionPieces.g b;
        private final WorldGenWoodlandMansionPieces.g c;
        private final WorldGenWoodlandMansionPieces.g[] d;
        private final int e;
        private final int f;

        public c(Random random) {
            this.a = random;
            boolean flag = true;

            this.e = 7;
            this.f = 4;
            this.b = new WorldGenWoodlandMansionPieces.g(11, 11, 5);
            this.b.a(this.e, this.f, this.e + 1, this.f + 1, 3);
            this.b.a(this.e - 1, this.f, this.e - 1, this.f + 1, 2);
            this.b.a(this.e + 2, this.f - 2, this.e + 3, this.f + 3, 5);
            this.b.a(this.e + 1, this.f - 2, this.e + 1, this.f - 1, 1);
            this.b.a(this.e + 1, this.f + 2, this.e + 1, this.f + 3, 1);
            this.b.a(this.e - 1, this.f - 1, 1);
            this.b.a(this.e - 1, this.f + 2, 1);
            this.b.a(0, 0, 11, 1, 5);
            this.b.a(0, 9, 11, 11, 5);
            this.a(this.b, this.e, this.f - 2, EnumDirection.WEST, 6);
            this.a(this.b, this.e, this.f + 3, EnumDirection.WEST, 6);
            this.a(this.b, this.e - 2, this.f - 1, EnumDirection.WEST, 3);
            this.a(this.b, this.e - 2, this.f + 2, EnumDirection.WEST, 3);

            while (this.a(this.b)) {
                ;
            }

            this.d = new WorldGenWoodlandMansionPieces.g[3];
            this.d[0] = new WorldGenWoodlandMansionPieces.g(11, 11, 5);
            this.d[1] = new WorldGenWoodlandMansionPieces.g(11, 11, 5);
            this.d[2] = new WorldGenWoodlandMansionPieces.g(11, 11, 5);
            this.a(this.b, this.d[0]);
            this.a(this.b, this.d[1]);
            this.d[0].a(this.e + 1, this.f, this.e + 1, this.f + 1, 8388608);
            this.d[1].a(this.e + 1, this.f, this.e + 1, this.f + 1, 8388608);
            this.c = new WorldGenWoodlandMansionPieces.g(this.b.b, this.b.c, 5);
            this.b();
            this.a(this.c, this.d[2]);
        }

        public static boolean a(WorldGenWoodlandMansionPieces.g worldgenwoodlandmansionpieces_g, int i, int j) {
            int k = worldgenwoodlandmansionpieces_g.a(i, j);

            return k == 1 || k == 2 || k == 3 || k == 4;
        }

        public boolean a(WorldGenWoodlandMansionPieces.g worldgenwoodlandmansionpieces_g, int i, int j, int k, int l) {
            return (this.d[k].a(i, j) & '\uffff') == l;
        }

        @Nullable
        public EnumDirection b(WorldGenWoodlandMansionPieces.g worldgenwoodlandmansionpieces_g, int i, int j, int k, int l) {
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            EnumDirection enumdirection;

            do {
                if (!iterator.hasNext()) {
                    return null;
                }

                enumdirection = (EnumDirection) iterator.next();
            } while (!this.a(worldgenwoodlandmansionpieces_g, i + enumdirection.getAdjacentX(), j + enumdirection.getAdjacentZ(), k, l));

            return enumdirection;
        }

        private void a(WorldGenWoodlandMansionPieces.g worldgenwoodlandmansionpieces_g, int i, int j, EnumDirection enumdirection, int k) {
            if (k > 0) {
                worldgenwoodlandmansionpieces_g.a(i, j, 1);
                worldgenwoodlandmansionpieces_g.a(i + enumdirection.getAdjacentX(), j + enumdirection.getAdjacentZ(), 0, 1);

                EnumDirection enumdirection1;

                for (int l = 0; l < 8; ++l) {
                    enumdirection1 = EnumDirection.fromType2(this.a.nextInt(4));
                    if (enumdirection1 != enumdirection.opposite() && (enumdirection1 != EnumDirection.EAST || !this.a.nextBoolean())) {
                        int i1 = i + enumdirection.getAdjacentX();
                        int j1 = j + enumdirection.getAdjacentZ();

                        if (worldgenwoodlandmansionpieces_g.a(i1 + enumdirection1.getAdjacentX(), j1 + enumdirection1.getAdjacentZ()) == 0 && worldgenwoodlandmansionpieces_g.a(i1 + enumdirection1.getAdjacentX() * 2, j1 + enumdirection1.getAdjacentZ() * 2) == 0) {
                            this.a(worldgenwoodlandmansionpieces_g, i + enumdirection.getAdjacentX() + enumdirection1.getAdjacentX(), j + enumdirection.getAdjacentZ() + enumdirection1.getAdjacentZ(), enumdirection1, k - 1);
                            break;
                        }
                    }
                }

                EnumDirection enumdirection2 = enumdirection.e();

                enumdirection1 = enumdirection.f();
                worldgenwoodlandmansionpieces_g.a(i + enumdirection2.getAdjacentX(), j + enumdirection2.getAdjacentZ(), 0, 2);
                worldgenwoodlandmansionpieces_g.a(i + enumdirection1.getAdjacentX(), j + enumdirection1.getAdjacentZ(), 0, 2);
                worldgenwoodlandmansionpieces_g.a(i + enumdirection.getAdjacentX() + enumdirection2.getAdjacentX(), j + enumdirection.getAdjacentZ() + enumdirection2.getAdjacentZ(), 0, 2);
                worldgenwoodlandmansionpieces_g.a(i + enumdirection.getAdjacentX() + enumdirection1.getAdjacentX(), j + enumdirection.getAdjacentZ() + enumdirection1.getAdjacentZ(), 0, 2);
                worldgenwoodlandmansionpieces_g.a(i + enumdirection.getAdjacentX() * 2, j + enumdirection.getAdjacentZ() * 2, 0, 2);
                worldgenwoodlandmansionpieces_g.a(i + enumdirection2.getAdjacentX() * 2, j + enumdirection2.getAdjacentZ() * 2, 0, 2);
                worldgenwoodlandmansionpieces_g.a(i + enumdirection1.getAdjacentX() * 2, j + enumdirection1.getAdjacentZ() * 2, 0, 2);
            }
        }

        private boolean a(WorldGenWoodlandMansionPieces.g worldgenwoodlandmansionpieces_g) {
            boolean flag = false;

            for (int i = 0; i < worldgenwoodlandmansionpieces_g.c; ++i) {
                for (int j = 0; j < worldgenwoodlandmansionpieces_g.b; ++j) {
                    if (worldgenwoodlandmansionpieces_g.a(j, i) == 0) {
                        byte b0 = 0;
                        int k = b0 + (a(worldgenwoodlandmansionpieces_g, j + 1, i) ? 1 : 0);

                        k += a(worldgenwoodlandmansionpieces_g, j - 1, i) ? 1 : 0;
                        k += a(worldgenwoodlandmansionpieces_g, j, i + 1) ? 1 : 0;
                        k += a(worldgenwoodlandmansionpieces_g, j, i - 1) ? 1 : 0;
                        if (k >= 3) {
                            worldgenwoodlandmansionpieces_g.a(j, i, 2);
                            flag = true;
                        } else if (k == 2) {
                            byte b1 = 0;
                            int l = b1 + (a(worldgenwoodlandmansionpieces_g, j + 1, i + 1) ? 1 : 0);

                            l += a(worldgenwoodlandmansionpieces_g, j - 1, i + 1) ? 1 : 0;
                            l += a(worldgenwoodlandmansionpieces_g, j + 1, i - 1) ? 1 : 0;
                            l += a(worldgenwoodlandmansionpieces_g, j - 1, i - 1) ? 1 : 0;
                            if (l <= 1) {
                                worldgenwoodlandmansionpieces_g.a(j, i, 2);
                                flag = true;
                            }
                        }
                    }
                }
            }

            return flag;
        }

        private void b() {
            List<Tuple<Integer, Integer>> list = Lists.newArrayList();
            WorldGenWoodlandMansionPieces.g worldgenwoodlandmansionpieces_g = this.d[1];

            int i;
            int j;

            for (int k = 0; k < this.c.c; ++k) {
                for (i = 0; i < this.c.b; ++i) {
                    int l = worldgenwoodlandmansionpieces_g.a(i, k);

                    j = l & 983040;
                    if (j == 131072 && (l & 2097152) == 2097152) {
                        list.add(new Tuple<>(i, k));
                    }
                }
            }

            if (list.isEmpty()) {
                this.c.a(0, 0, this.c.b, this.c.c, 5);
            } else {
                Tuple<Integer, Integer> tuple = (Tuple) list.get(this.a.nextInt(list.size()));

                i = worldgenwoodlandmansionpieces_g.a((Integer) tuple.a(), (Integer) tuple.b());
                worldgenwoodlandmansionpieces_g.a((Integer) tuple.a(), (Integer) tuple.b(), i | 4194304);
                EnumDirection enumdirection = this.b(this.b, (Integer) tuple.a(), (Integer) tuple.b(), 1, i & '\uffff');

                j = (Integer) tuple.a() + enumdirection.getAdjacentX();
                int i1 = (Integer) tuple.b() + enumdirection.getAdjacentZ();

                for (int j1 = 0; j1 < this.c.c; ++j1) {
                    for (int k1 = 0; k1 < this.c.b; ++k1) {
                        if (!a(this.b, k1, j1)) {
                            this.c.a(k1, j1, 5);
                        } else if (k1 == (Integer) tuple.a() && j1 == (Integer) tuple.b()) {
                            this.c.a(k1, j1, 3);
                        } else if (k1 == j && j1 == i1) {
                            this.c.a(k1, j1, 3);
                            this.d[2].a(k1, j1, 8388608);
                        }
                    }
                }

                List<EnumDirection> list1 = Lists.newArrayList();
                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                while (iterator.hasNext()) {
                    EnumDirection enumdirection1 = (EnumDirection) iterator.next();

                    if (this.c.a(j + enumdirection1.getAdjacentX(), i1 + enumdirection1.getAdjacentZ()) == 0) {
                        list1.add(enumdirection1);
                    }
                }

                if (list1.isEmpty()) {
                    this.c.a(0, 0, this.c.b, this.c.c, 5);
                    worldgenwoodlandmansionpieces_g.a((Integer) tuple.a(), (Integer) tuple.b(), i);
                } else {
                    EnumDirection enumdirection2 = (EnumDirection) list1.get(this.a.nextInt(list1.size()));

                    this.a(this.c, j + enumdirection2.getAdjacentX(), i1 + enumdirection2.getAdjacentZ(), enumdirection2, 4);

                    while (this.a(this.c)) {
                        ;
                    }

                }
            }
        }

        private void a(WorldGenWoodlandMansionPieces.g worldgenwoodlandmansionpieces_g, WorldGenWoodlandMansionPieces.g worldgenwoodlandmansionpieces_g1) {
            List<Tuple<Integer, Integer>> list = Lists.newArrayList();

            int i;

            for (i = 0; i < worldgenwoodlandmansionpieces_g.c; ++i) {
                for (int j = 0; j < worldgenwoodlandmansionpieces_g.b; ++j) {
                    if (worldgenwoodlandmansionpieces_g.a(j, i) == 2) {
                        list.add(new Tuple<>(j, i));
                    }
                }
            }

            Collections.shuffle(list, this.a);
            i = 10;
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Tuple<Integer, Integer> tuple = (Tuple) iterator.next();
                int k = (Integer) tuple.a();
                int l = (Integer) tuple.b();

                if (worldgenwoodlandmansionpieces_g1.a(k, l) == 0) {
                    int i1 = k;
                    int j1 = k;
                    int k1 = l;
                    int l1 = l;
                    int i2 = 65536;

                    if (worldgenwoodlandmansionpieces_g1.a(k + 1, l) == 0 && worldgenwoodlandmansionpieces_g1.a(k, l + 1) == 0 && worldgenwoodlandmansionpieces_g1.a(k + 1, l + 1) == 0 && worldgenwoodlandmansionpieces_g.a(k + 1, l) == 2 && worldgenwoodlandmansionpieces_g.a(k, l + 1) == 2 && worldgenwoodlandmansionpieces_g.a(k + 1, l + 1) == 2) {
                        j1 = k + 1;
                        l1 = l + 1;
                        i2 = 262144;
                    } else if (worldgenwoodlandmansionpieces_g1.a(k - 1, l) == 0 && worldgenwoodlandmansionpieces_g1.a(k, l + 1) == 0 && worldgenwoodlandmansionpieces_g1.a(k - 1, l + 1) == 0 && worldgenwoodlandmansionpieces_g.a(k - 1, l) == 2 && worldgenwoodlandmansionpieces_g.a(k, l + 1) == 2 && worldgenwoodlandmansionpieces_g.a(k - 1, l + 1) == 2) {
                        i1 = k - 1;
                        l1 = l + 1;
                        i2 = 262144;
                    } else if (worldgenwoodlandmansionpieces_g1.a(k - 1, l) == 0 && worldgenwoodlandmansionpieces_g1.a(k, l - 1) == 0 && worldgenwoodlandmansionpieces_g1.a(k - 1, l - 1) == 0 && worldgenwoodlandmansionpieces_g.a(k - 1, l) == 2 && worldgenwoodlandmansionpieces_g.a(k, l - 1) == 2 && worldgenwoodlandmansionpieces_g.a(k - 1, l - 1) == 2) {
                        i1 = k - 1;
                        k1 = l - 1;
                        i2 = 262144;
                    } else if (worldgenwoodlandmansionpieces_g1.a(k + 1, l) == 0 && worldgenwoodlandmansionpieces_g.a(k + 1, l) == 2) {
                        j1 = k + 1;
                        i2 = 131072;
                    } else if (worldgenwoodlandmansionpieces_g1.a(k, l + 1) == 0 && worldgenwoodlandmansionpieces_g.a(k, l + 1) == 2) {
                        l1 = l + 1;
                        i2 = 131072;
                    } else if (worldgenwoodlandmansionpieces_g1.a(k - 1, l) == 0 && worldgenwoodlandmansionpieces_g.a(k - 1, l) == 2) {
                        i1 = k - 1;
                        i2 = 131072;
                    } else if (worldgenwoodlandmansionpieces_g1.a(k, l - 1) == 0 && worldgenwoodlandmansionpieces_g.a(k, l - 1) == 2) {
                        k1 = l - 1;
                        i2 = 131072;
                    }

                    int j2 = this.a.nextBoolean() ? i1 : j1;
                    int k2 = this.a.nextBoolean() ? k1 : l1;
                    int l2 = 2097152;

                    if (!worldgenwoodlandmansionpieces_g.b(j2, k2, 1)) {
                        j2 = j2 == i1 ? j1 : i1;
                        k2 = k2 == k1 ? l1 : k1;
                        if (!worldgenwoodlandmansionpieces_g.b(j2, k2, 1)) {
                            k2 = k2 == k1 ? l1 : k1;
                            if (!worldgenwoodlandmansionpieces_g.b(j2, k2, 1)) {
                                j2 = j2 == i1 ? j1 : i1;
                                k2 = k2 == k1 ? l1 : k1;
                                if (!worldgenwoodlandmansionpieces_g.b(j2, k2, 1)) {
                                    l2 = 0;
                                    j2 = i1;
                                    k2 = k1;
                                }
                            }
                        }
                    }

                    for (int i3 = k1; i3 <= l1; ++i3) {
                        for (int j3 = i1; j3 <= j1; ++j3) {
                            if (j3 == j2 && i3 == k2) {
                                worldgenwoodlandmansionpieces_g1.a(j3, i3, 1048576 | l2 | i2 | i);
                            } else {
                                worldgenwoodlandmansionpieces_g1.a(j3, i3, i2 | i);
                            }
                        }
                    }

                    ++i;
                }
            }

        }
    }

    static class d {

        private final DefinedStructureManager a;
        private final Random b;
        private int c;
        private int d;

        public d(DefinedStructureManager definedstructuremanager, Random random) {
            this.a = definedstructuremanager;
            this.b = random;
        }

        public void a(BlockPosition blockposition, EnumBlockRotation enumblockrotation, List<WorldGenWoodlandMansionPieces.i> list, WorldGenWoodlandMansionPieces.c worldgenwoodlandmansionpieces_c) {
            WorldGenWoodlandMansionPieces.e worldgenwoodlandmansionpieces_e = new WorldGenWoodlandMansionPieces.e();

            worldgenwoodlandmansionpieces_e.b = blockposition;
            worldgenwoodlandmansionpieces_e.a = enumblockrotation;
            worldgenwoodlandmansionpieces_e.c = "wall_flat";
            WorldGenWoodlandMansionPieces.e worldgenwoodlandmansionpieces_e1 = new WorldGenWoodlandMansionPieces.e();

            this.a(list, worldgenwoodlandmansionpieces_e);
            worldgenwoodlandmansionpieces_e1.b = worldgenwoodlandmansionpieces_e.b.up(8);
            worldgenwoodlandmansionpieces_e1.a = worldgenwoodlandmansionpieces_e.a;
            worldgenwoodlandmansionpieces_e1.c = "wall_window";
            if (!list.isEmpty()) {
                ;
            }

            WorldGenWoodlandMansionPieces.g worldgenwoodlandmansionpieces_g = worldgenwoodlandmansionpieces_c.b;
            WorldGenWoodlandMansionPieces.g worldgenwoodlandmansionpieces_g1 = worldgenwoodlandmansionpieces_c.c;

            this.c = worldgenwoodlandmansionpieces_c.e + 1;
            this.d = worldgenwoodlandmansionpieces_c.f + 1;
            int i = worldgenwoodlandmansionpieces_c.e + 1;
            int j = worldgenwoodlandmansionpieces_c.f;

            this.a(list, worldgenwoodlandmansionpieces_e, worldgenwoodlandmansionpieces_g, EnumDirection.SOUTH, this.c, this.d, i, j);
            this.a(list, worldgenwoodlandmansionpieces_e1, worldgenwoodlandmansionpieces_g, EnumDirection.SOUTH, this.c, this.d, i, j);
            WorldGenWoodlandMansionPieces.e worldgenwoodlandmansionpieces_e2 = new WorldGenWoodlandMansionPieces.e();

            worldgenwoodlandmansionpieces_e2.b = worldgenwoodlandmansionpieces_e.b.up(19);
            worldgenwoodlandmansionpieces_e2.a = worldgenwoodlandmansionpieces_e.a;
            worldgenwoodlandmansionpieces_e2.c = "wall_window";
            boolean flag = false;

            int k;

            for (int l = 0; l < worldgenwoodlandmansionpieces_g1.c && !flag; ++l) {
                for (k = worldgenwoodlandmansionpieces_g1.b - 1; k >= 0 && !flag; --k) {
                    if (WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g1, k, l)) {
                        worldgenwoodlandmansionpieces_e2.b = worldgenwoodlandmansionpieces_e2.b.shift(enumblockrotation.a(EnumDirection.SOUTH), 8 + (l - this.d) * 8);
                        worldgenwoodlandmansionpieces_e2.b = worldgenwoodlandmansionpieces_e2.b.shift(enumblockrotation.a(EnumDirection.EAST), (k - this.c) * 8);
                        this.b(list, worldgenwoodlandmansionpieces_e2);
                        this.a(list, worldgenwoodlandmansionpieces_e2, worldgenwoodlandmansionpieces_g1, EnumDirection.SOUTH, k, l, k, l);
                        flag = true;
                    }
                }
            }

            this.a(list, blockposition.up(16), enumblockrotation, worldgenwoodlandmansionpieces_g, worldgenwoodlandmansionpieces_g1);
            this.a(list, blockposition.up(27), enumblockrotation, worldgenwoodlandmansionpieces_g1, (WorldGenWoodlandMansionPieces.g) null);
            if (!list.isEmpty()) {
                ;
            }

            WorldGenWoodlandMansionPieces.b[] aworldgenwoodlandmansionpieces_b = new WorldGenWoodlandMansionPieces.b[] { new WorldGenWoodlandMansionPieces.a(), new WorldGenWoodlandMansionPieces.f(), new WorldGenWoodlandMansionPieces.h()};

            for (k = 0; k < 3; ++k) {
                BlockPosition blockposition1 = blockposition.up(8 * k + (k == 2 ? 3 : 0));
                WorldGenWoodlandMansionPieces.g worldgenwoodlandmansionpieces_g2 = worldgenwoodlandmansionpieces_c.d[k];
                WorldGenWoodlandMansionPieces.g worldgenwoodlandmansionpieces_g3 = k == 2 ? worldgenwoodlandmansionpieces_g1 : worldgenwoodlandmansionpieces_g;
                String s = k == 0 ? "carpet_south_1" : "carpet_south_2";
                String s1 = k == 0 ? "carpet_west_1" : "carpet_west_2";

                for (int i1 = 0; i1 < worldgenwoodlandmansionpieces_g3.c; ++i1) {
                    for (int j1 = 0; j1 < worldgenwoodlandmansionpieces_g3.b; ++j1) {
                        if (worldgenwoodlandmansionpieces_g3.a(j1, i1) == 1) {
                            BlockPosition blockposition2 = blockposition1.shift(enumblockrotation.a(EnumDirection.SOUTH), 8 + (i1 - this.d) * 8);

                            blockposition2 = blockposition2.shift(enumblockrotation.a(EnumDirection.EAST), (j1 - this.c) * 8);
                            list.add(new WorldGenWoodlandMansionPieces.i(this.a, "corridor_floor", blockposition2, enumblockrotation));
                            if (worldgenwoodlandmansionpieces_g3.a(j1, i1 - 1) == 1 || (worldgenwoodlandmansionpieces_g2.a(j1, i1 - 1) & 8388608) == 8388608) {
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, "carpet_north", blockposition2.shift(enumblockrotation.a(EnumDirection.EAST), 1).up(), enumblockrotation));
                            }

                            if (worldgenwoodlandmansionpieces_g3.a(j1 + 1, i1) == 1 || (worldgenwoodlandmansionpieces_g2.a(j1 + 1, i1) & 8388608) == 8388608) {
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, "carpet_east", blockposition2.shift(enumblockrotation.a(EnumDirection.SOUTH), 1).shift(enumblockrotation.a(EnumDirection.EAST), 5).up(), enumblockrotation));
                            }

                            if (worldgenwoodlandmansionpieces_g3.a(j1, i1 + 1) == 1 || (worldgenwoodlandmansionpieces_g2.a(j1, i1 + 1) & 8388608) == 8388608) {
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, s, blockposition2.shift(enumblockrotation.a(EnumDirection.SOUTH), 5).shift(enumblockrotation.a(EnumDirection.WEST), 1), enumblockrotation));
                            }

                            if (worldgenwoodlandmansionpieces_g3.a(j1 - 1, i1) == 1 || (worldgenwoodlandmansionpieces_g2.a(j1 - 1, i1) & 8388608) == 8388608) {
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, s1, blockposition2.shift(enumblockrotation.a(EnumDirection.WEST), 1).shift(enumblockrotation.a(EnumDirection.NORTH), 1), enumblockrotation));
                            }
                        }
                    }
                }

                String s2 = k == 0 ? "indoors_wall_1" : "indoors_wall_2";
                String s3 = k == 0 ? "indoors_door_1" : "indoors_door_2";
                List<EnumDirection> list1 = Lists.newArrayList();

                for (int k1 = 0; k1 < worldgenwoodlandmansionpieces_g3.c; ++k1) {
                    for (int l1 = 0; l1 < worldgenwoodlandmansionpieces_g3.b; ++l1) {
                        boolean flag1 = k == 2 && worldgenwoodlandmansionpieces_g3.a(l1, k1) == 3;

                        if (worldgenwoodlandmansionpieces_g3.a(l1, k1) == 2 || flag1) {
                            int i2 = worldgenwoodlandmansionpieces_g2.a(l1, k1);
                            int j2 = i2 & 983040;
                            int k2 = i2 & '\uffff';

                            flag1 = flag1 && (i2 & 8388608) == 8388608;
                            list1.clear();
                            if ((i2 & 2097152) == 2097152) {
                                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                                while (iterator.hasNext()) {
                                    EnumDirection enumdirection = (EnumDirection) iterator.next();

                                    if (worldgenwoodlandmansionpieces_g3.a(l1 + enumdirection.getAdjacentX(), k1 + enumdirection.getAdjacentZ()) == 1) {
                                        list1.add(enumdirection);
                                    }
                                }
                            }

                            EnumDirection enumdirection1 = null;

                            if (!list1.isEmpty()) {
                                enumdirection1 = (EnumDirection) list1.get(this.b.nextInt(list1.size()));
                            } else if ((i2 & 1048576) == 1048576) {
                                enumdirection1 = EnumDirection.UP;
                            }

                            BlockPosition blockposition3 = blockposition1.shift(enumblockrotation.a(EnumDirection.SOUTH), 8 + (k1 - this.d) * 8);

                            blockposition3 = blockposition3.shift(enumblockrotation.a(EnumDirection.EAST), -1 + (l1 - this.c) * 8);
                            if (WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g3, l1 - 1, k1) && !worldgenwoodlandmansionpieces_c.a(worldgenwoodlandmansionpieces_g3, l1 - 1, k1, k, k2)) {
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, enumdirection1 == EnumDirection.WEST ? s3 : s2, blockposition3, enumblockrotation));
                            }

                            BlockPosition blockposition4;

                            if (worldgenwoodlandmansionpieces_g3.a(l1 + 1, k1) == 1 && !flag1) {
                                blockposition4 = blockposition3.shift(enumblockrotation.a(EnumDirection.EAST), 8);
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, enumdirection1 == EnumDirection.EAST ? s3 : s2, blockposition4, enumblockrotation));
                            }

                            if (WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g3, l1, k1 + 1) && !worldgenwoodlandmansionpieces_c.a(worldgenwoodlandmansionpieces_g3, l1, k1 + 1, k, k2)) {
                                blockposition4 = blockposition3.shift(enumblockrotation.a(EnumDirection.SOUTH), 7);
                                blockposition4 = blockposition4.shift(enumblockrotation.a(EnumDirection.EAST), 7);
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, enumdirection1 == EnumDirection.SOUTH ? s3 : s2, blockposition4, enumblockrotation.a(EnumBlockRotation.CLOCKWISE_90)));
                            }

                            if (worldgenwoodlandmansionpieces_g3.a(l1, k1 - 1) == 1 && !flag1) {
                                blockposition4 = blockposition3.shift(enumblockrotation.a(EnumDirection.NORTH), 1);
                                blockposition4 = blockposition4.shift(enumblockrotation.a(EnumDirection.EAST), 7);
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, enumdirection1 == EnumDirection.NORTH ? s3 : s2, blockposition4, enumblockrotation.a(EnumBlockRotation.CLOCKWISE_90)));
                            }

                            if (j2 == 65536) {
                                this.a(list, blockposition3, enumblockrotation, enumdirection1, aworldgenwoodlandmansionpieces_b[k]);
                            } else {
                                EnumDirection enumdirection2;

                                if (j2 == 131072 && enumdirection1 != null) {
                                    enumdirection2 = worldgenwoodlandmansionpieces_c.b(worldgenwoodlandmansionpieces_g3, l1, k1, k, k2);
                                    boolean flag2 = (i2 & 4194304) == 4194304;

                                    this.a(list, blockposition3, enumblockrotation, enumdirection2, enumdirection1, aworldgenwoodlandmansionpieces_b[k], flag2);
                                } else if (j2 == 262144 && enumdirection1 != null && enumdirection1 != EnumDirection.UP) {
                                    enumdirection2 = enumdirection1.e();
                                    if (!worldgenwoodlandmansionpieces_c.a(worldgenwoodlandmansionpieces_g3, l1 + enumdirection2.getAdjacentX(), k1 + enumdirection2.getAdjacentZ(), k, k2)) {
                                        enumdirection2 = enumdirection2.opposite();
                                    }

                                    this.a(list, blockposition3, enumblockrotation, enumdirection2, enumdirection1, aworldgenwoodlandmansionpieces_b[k]);
                                } else if (j2 == 262144 && enumdirection1 == EnumDirection.UP) {
                                    this.a(list, blockposition3, enumblockrotation, aworldgenwoodlandmansionpieces_b[k]);
                                }
                            }
                        }
                    }
                }
            }

        }

        private void a(List<WorldGenWoodlandMansionPieces.i> list, WorldGenWoodlandMansionPieces.e worldgenwoodlandmansionpieces_e, WorldGenWoodlandMansionPieces.g worldgenwoodlandmansionpieces_g, EnumDirection enumdirection, int i, int j, int k, int l) {
            int i1 = i;
            int j1 = j;
            EnumDirection enumdirection1 = enumdirection;

            do {
                if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, i1 + enumdirection.getAdjacentX(), j1 + enumdirection.getAdjacentZ())) {
                    this.c(list, worldgenwoodlandmansionpieces_e);
                    enumdirection = enumdirection.e();
                    if (i1 != k || j1 != l || enumdirection1 != enumdirection) {
                        this.b(list, worldgenwoodlandmansionpieces_e);
                    }
                } else if (WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, i1 + enumdirection.getAdjacentX(), j1 + enumdirection.getAdjacentZ()) && WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, i1 + enumdirection.getAdjacentX() + enumdirection.f().getAdjacentX(), j1 + enumdirection.getAdjacentZ() + enumdirection.f().getAdjacentZ())) {
                    this.d(list, worldgenwoodlandmansionpieces_e);
                    i1 += enumdirection.getAdjacentX();
                    j1 += enumdirection.getAdjacentZ();
                    enumdirection = enumdirection.f();
                } else {
                    i1 += enumdirection.getAdjacentX();
                    j1 += enumdirection.getAdjacentZ();
                    if (i1 != k || j1 != l || enumdirection1 != enumdirection) {
                        this.b(list, worldgenwoodlandmansionpieces_e);
                    }
                }
            } while (i1 != k || j1 != l || enumdirection1 != enumdirection);

        }

        private void a(List<WorldGenWoodlandMansionPieces.i> list, BlockPosition blockposition, EnumBlockRotation enumblockrotation, WorldGenWoodlandMansionPieces.g worldgenwoodlandmansionpieces_g, @Nullable WorldGenWoodlandMansionPieces.g worldgenwoodlandmansionpieces_g1) {
            BlockPosition blockposition1;
            int i;
            int j;
            boolean flag;
            BlockPosition blockposition2;

            for (i = 0; i < worldgenwoodlandmansionpieces_g.c; ++i) {
                for (j = 0; j < worldgenwoodlandmansionpieces_g.b; ++j) {
                    blockposition1 = blockposition.shift(enumblockrotation.a(EnumDirection.SOUTH), 8 + (i - this.d) * 8);
                    blockposition1 = blockposition1.shift(enumblockrotation.a(EnumDirection.EAST), (j - this.c) * 8);
                    flag = worldgenwoodlandmansionpieces_g1 != null && WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g1, j, i);
                    if (WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j, i) && !flag) {
                        list.add(new WorldGenWoodlandMansionPieces.i(this.a, "roof", blockposition1.up(3), enumblockrotation));
                        if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j + 1, i)) {
                            blockposition2 = blockposition1.shift(enumblockrotation.a(EnumDirection.EAST), 6);
                            list.add(new WorldGenWoodlandMansionPieces.i(this.a, "roof_front", blockposition2, enumblockrotation));
                        }

                        if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j - 1, i)) {
                            blockposition2 = blockposition1.shift(enumblockrotation.a(EnumDirection.EAST), 0);
                            blockposition2 = blockposition2.shift(enumblockrotation.a(EnumDirection.SOUTH), 7);
                            list.add(new WorldGenWoodlandMansionPieces.i(this.a, "roof_front", blockposition2, enumblockrotation.a(EnumBlockRotation.CLOCKWISE_180)));
                        }

                        if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j, i - 1)) {
                            blockposition2 = blockposition1.shift(enumblockrotation.a(EnumDirection.WEST), 1);
                            list.add(new WorldGenWoodlandMansionPieces.i(this.a, "roof_front", blockposition2, enumblockrotation.a(EnumBlockRotation.COUNTERCLOCKWISE_90)));
                        }

                        if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j, i + 1)) {
                            blockposition2 = blockposition1.shift(enumblockrotation.a(EnumDirection.EAST), 6);
                            blockposition2 = blockposition2.shift(enumblockrotation.a(EnumDirection.SOUTH), 6);
                            list.add(new WorldGenWoodlandMansionPieces.i(this.a, "roof_front", blockposition2, enumblockrotation.a(EnumBlockRotation.CLOCKWISE_90)));
                        }
                    }
                }
            }

            if (worldgenwoodlandmansionpieces_g1 != null) {
                for (i = 0; i < worldgenwoodlandmansionpieces_g.c; ++i) {
                    for (j = 0; j < worldgenwoodlandmansionpieces_g.b; ++j) {
                        blockposition1 = blockposition.shift(enumblockrotation.a(EnumDirection.SOUTH), 8 + (i - this.d) * 8);
                        blockposition1 = blockposition1.shift(enumblockrotation.a(EnumDirection.EAST), (j - this.c) * 8);
                        flag = WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g1, j, i);
                        if (WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j, i) && flag) {
                            if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j + 1, i)) {
                                blockposition2 = blockposition1.shift(enumblockrotation.a(EnumDirection.EAST), 7);
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, "small_wall", blockposition2, enumblockrotation));
                            }

                            if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j - 1, i)) {
                                blockposition2 = blockposition1.shift(enumblockrotation.a(EnumDirection.WEST), 1);
                                blockposition2 = blockposition2.shift(enumblockrotation.a(EnumDirection.SOUTH), 6);
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, "small_wall", blockposition2, enumblockrotation.a(EnumBlockRotation.CLOCKWISE_180)));
                            }

                            if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j, i - 1)) {
                                blockposition2 = blockposition1.shift(enumblockrotation.a(EnumDirection.WEST), 0);
                                blockposition2 = blockposition2.shift(enumblockrotation.a(EnumDirection.NORTH), 1);
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, "small_wall", blockposition2, enumblockrotation.a(EnumBlockRotation.COUNTERCLOCKWISE_90)));
                            }

                            if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j, i + 1)) {
                                blockposition2 = blockposition1.shift(enumblockrotation.a(EnumDirection.EAST), 6);
                                blockposition2 = blockposition2.shift(enumblockrotation.a(EnumDirection.SOUTH), 7);
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, "small_wall", blockposition2, enumblockrotation.a(EnumBlockRotation.CLOCKWISE_90)));
                            }

                            if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j + 1, i)) {
                                if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j, i - 1)) {
                                    blockposition2 = blockposition1.shift(enumblockrotation.a(EnumDirection.EAST), 7);
                                    blockposition2 = blockposition2.shift(enumblockrotation.a(EnumDirection.NORTH), 2);
                                    list.add(new WorldGenWoodlandMansionPieces.i(this.a, "small_wall_corner", blockposition2, enumblockrotation));
                                }

                                if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j, i + 1)) {
                                    blockposition2 = blockposition1.shift(enumblockrotation.a(EnumDirection.EAST), 8);
                                    blockposition2 = blockposition2.shift(enumblockrotation.a(EnumDirection.SOUTH), 7);
                                    list.add(new WorldGenWoodlandMansionPieces.i(this.a, "small_wall_corner", blockposition2, enumblockrotation.a(EnumBlockRotation.CLOCKWISE_90)));
                                }
                            }

                            if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j - 1, i)) {
                                if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j, i - 1)) {
                                    blockposition2 = blockposition1.shift(enumblockrotation.a(EnumDirection.WEST), 2);
                                    blockposition2 = blockposition2.shift(enumblockrotation.a(EnumDirection.NORTH), 1);
                                    list.add(new WorldGenWoodlandMansionPieces.i(this.a, "small_wall_corner", blockposition2, enumblockrotation.a(EnumBlockRotation.COUNTERCLOCKWISE_90)));
                                }

                                if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j, i + 1)) {
                                    blockposition2 = blockposition1.shift(enumblockrotation.a(EnumDirection.WEST), 1);
                                    blockposition2 = blockposition2.shift(enumblockrotation.a(EnumDirection.SOUTH), 8);
                                    list.add(new WorldGenWoodlandMansionPieces.i(this.a, "small_wall_corner", blockposition2, enumblockrotation.a(EnumBlockRotation.CLOCKWISE_180)));
                                }
                            }
                        }
                    }
                }
            }

            for (i = 0; i < worldgenwoodlandmansionpieces_g.c; ++i) {
                for (j = 0; j < worldgenwoodlandmansionpieces_g.b; ++j) {
                    blockposition1 = blockposition.shift(enumblockrotation.a(EnumDirection.SOUTH), 8 + (i - this.d) * 8);
                    blockposition1 = blockposition1.shift(enumblockrotation.a(EnumDirection.EAST), (j - this.c) * 8);
                    flag = worldgenwoodlandmansionpieces_g1 != null && WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g1, j, i);
                    if (WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j, i) && !flag) {
                        BlockPosition blockposition3;

                        if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j + 1, i)) {
                            blockposition2 = blockposition1.shift(enumblockrotation.a(EnumDirection.EAST), 6);
                            if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j, i + 1)) {
                                blockposition3 = blockposition2.shift(enumblockrotation.a(EnumDirection.SOUTH), 6);
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, "roof_corner", blockposition3, enumblockrotation));
                            } else if (WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j + 1, i + 1)) {
                                blockposition3 = blockposition2.shift(enumblockrotation.a(EnumDirection.SOUTH), 5);
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, "roof_inner_corner", blockposition3, enumblockrotation));
                            }

                            if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j, i - 1)) {
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, "roof_corner", blockposition2, enumblockrotation.a(EnumBlockRotation.COUNTERCLOCKWISE_90)));
                            } else if (WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j + 1, i - 1)) {
                                blockposition3 = blockposition1.shift(enumblockrotation.a(EnumDirection.EAST), 9);
                                blockposition3 = blockposition3.shift(enumblockrotation.a(EnumDirection.NORTH), 2);
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, "roof_inner_corner", blockposition3, enumblockrotation.a(EnumBlockRotation.CLOCKWISE_90)));
                            }
                        }

                        if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j - 1, i)) {
                            blockposition2 = blockposition1.shift(enumblockrotation.a(EnumDirection.EAST), 0);
                            blockposition2 = blockposition2.shift(enumblockrotation.a(EnumDirection.SOUTH), 0);
                            if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j, i + 1)) {
                                blockposition3 = blockposition2.shift(enumblockrotation.a(EnumDirection.SOUTH), 6);
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, "roof_corner", blockposition3, enumblockrotation.a(EnumBlockRotation.CLOCKWISE_90)));
                            } else if (WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j - 1, i + 1)) {
                                blockposition3 = blockposition2.shift(enumblockrotation.a(EnumDirection.SOUTH), 8);
                                blockposition3 = blockposition3.shift(enumblockrotation.a(EnumDirection.WEST), 3);
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, "roof_inner_corner", blockposition3, enumblockrotation.a(EnumBlockRotation.COUNTERCLOCKWISE_90)));
                            }

                            if (!WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j, i - 1)) {
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, "roof_corner", blockposition2, enumblockrotation.a(EnumBlockRotation.CLOCKWISE_180)));
                            } else if (WorldGenWoodlandMansionPieces.c.a(worldgenwoodlandmansionpieces_g, j - 1, i - 1)) {
                                blockposition3 = blockposition2.shift(enumblockrotation.a(EnumDirection.SOUTH), 1);
                                list.add(new WorldGenWoodlandMansionPieces.i(this.a, "roof_inner_corner", blockposition3, enumblockrotation.a(EnumBlockRotation.CLOCKWISE_180)));
                            }
                        }
                    }
                }
            }

        }

        private void a(List<WorldGenWoodlandMansionPieces.i> list, WorldGenWoodlandMansionPieces.e worldgenwoodlandmansionpieces_e) {
            EnumDirection enumdirection = worldgenwoodlandmansionpieces_e.a.a(EnumDirection.WEST);

            list.add(new WorldGenWoodlandMansionPieces.i(this.a, "entrance", worldgenwoodlandmansionpieces_e.b.shift(enumdirection, 9), worldgenwoodlandmansionpieces_e.a));
            worldgenwoodlandmansionpieces_e.b = worldgenwoodlandmansionpieces_e.b.shift(worldgenwoodlandmansionpieces_e.a.a(EnumDirection.SOUTH), 16);
        }

        private void b(List<WorldGenWoodlandMansionPieces.i> list, WorldGenWoodlandMansionPieces.e worldgenwoodlandmansionpieces_e) {
            list.add(new WorldGenWoodlandMansionPieces.i(this.a, worldgenwoodlandmansionpieces_e.c, worldgenwoodlandmansionpieces_e.b.shift(worldgenwoodlandmansionpieces_e.a.a(EnumDirection.EAST), 7), worldgenwoodlandmansionpieces_e.a));
            worldgenwoodlandmansionpieces_e.b = worldgenwoodlandmansionpieces_e.b.shift(worldgenwoodlandmansionpieces_e.a.a(EnumDirection.SOUTH), 8);
        }

        private void c(List<WorldGenWoodlandMansionPieces.i> list, WorldGenWoodlandMansionPieces.e worldgenwoodlandmansionpieces_e) {
            worldgenwoodlandmansionpieces_e.b = worldgenwoodlandmansionpieces_e.b.shift(worldgenwoodlandmansionpieces_e.a.a(EnumDirection.SOUTH), -1);
            list.add(new WorldGenWoodlandMansionPieces.i(this.a, "wall_corner", worldgenwoodlandmansionpieces_e.b, worldgenwoodlandmansionpieces_e.a));
            worldgenwoodlandmansionpieces_e.b = worldgenwoodlandmansionpieces_e.b.shift(worldgenwoodlandmansionpieces_e.a.a(EnumDirection.SOUTH), -7);
            worldgenwoodlandmansionpieces_e.b = worldgenwoodlandmansionpieces_e.b.shift(worldgenwoodlandmansionpieces_e.a.a(EnumDirection.WEST), -6);
            worldgenwoodlandmansionpieces_e.a = worldgenwoodlandmansionpieces_e.a.a(EnumBlockRotation.CLOCKWISE_90);
        }

        private void d(List<WorldGenWoodlandMansionPieces.i> list, WorldGenWoodlandMansionPieces.e worldgenwoodlandmansionpieces_e) {
            worldgenwoodlandmansionpieces_e.b = worldgenwoodlandmansionpieces_e.b.shift(worldgenwoodlandmansionpieces_e.a.a(EnumDirection.SOUTH), 6);
            worldgenwoodlandmansionpieces_e.b = worldgenwoodlandmansionpieces_e.b.shift(worldgenwoodlandmansionpieces_e.a.a(EnumDirection.EAST), 8);
            worldgenwoodlandmansionpieces_e.a = worldgenwoodlandmansionpieces_e.a.a(EnumBlockRotation.COUNTERCLOCKWISE_90);
        }

        private void a(List<WorldGenWoodlandMansionPieces.i> list, BlockPosition blockposition, EnumBlockRotation enumblockrotation, EnumDirection enumdirection, WorldGenWoodlandMansionPieces.b worldgenwoodlandmansionpieces_b) {
            EnumBlockRotation enumblockrotation1 = EnumBlockRotation.NONE;
            String s = worldgenwoodlandmansionpieces_b.a(this.b);

            if (enumdirection != EnumDirection.EAST) {
                if (enumdirection == EnumDirection.NORTH) {
                    enumblockrotation1 = enumblockrotation1.a(EnumBlockRotation.COUNTERCLOCKWISE_90);
                } else if (enumdirection == EnumDirection.WEST) {
                    enumblockrotation1 = enumblockrotation1.a(EnumBlockRotation.CLOCKWISE_180);
                } else if (enumdirection == EnumDirection.SOUTH) {
                    enumblockrotation1 = enumblockrotation1.a(EnumBlockRotation.CLOCKWISE_90);
                } else {
                    s = worldgenwoodlandmansionpieces_b.b(this.b);
                }
            }

            BlockPosition blockposition1 = DefinedStructure.a(new BlockPosition(1, 0, 0), EnumBlockMirror.NONE, enumblockrotation1, 7, 7);

            enumblockrotation1 = enumblockrotation1.a(enumblockrotation);
            blockposition1 = blockposition1.a(enumblockrotation);
            BlockPosition blockposition2 = blockposition.a(blockposition1.getX(), 0, blockposition1.getZ());

            list.add(new WorldGenWoodlandMansionPieces.i(this.a, s, blockposition2, enumblockrotation1));
        }

        private void a(List<WorldGenWoodlandMansionPieces.i> list, BlockPosition blockposition, EnumBlockRotation enumblockrotation, EnumDirection enumdirection, EnumDirection enumdirection1, WorldGenWoodlandMansionPieces.b worldgenwoodlandmansionpieces_b, boolean flag) {
            BlockPosition blockposition1;

            if (enumdirection1 == EnumDirection.EAST && enumdirection == EnumDirection.SOUTH) {
                blockposition1 = blockposition.shift(enumblockrotation.a(EnumDirection.EAST), 1);
                list.add(new WorldGenWoodlandMansionPieces.i(this.a, worldgenwoodlandmansionpieces_b.a(this.b, flag), blockposition1, enumblockrotation));
            } else if (enumdirection1 == EnumDirection.EAST && enumdirection == EnumDirection.NORTH) {
                blockposition1 = blockposition.shift(enumblockrotation.a(EnumDirection.EAST), 1);
                blockposition1 = blockposition1.shift(enumblockrotation.a(EnumDirection.SOUTH), 6);
                list.add(new WorldGenWoodlandMansionPieces.i(this.a, worldgenwoodlandmansionpieces_b.a(this.b, flag), blockposition1, enumblockrotation, EnumBlockMirror.LEFT_RIGHT));
            } else if (enumdirection1 == EnumDirection.WEST && enumdirection == EnumDirection.NORTH) {
                blockposition1 = blockposition.shift(enumblockrotation.a(EnumDirection.EAST), 7);
                blockposition1 = blockposition1.shift(enumblockrotation.a(EnumDirection.SOUTH), 6);
                list.add(new WorldGenWoodlandMansionPieces.i(this.a, worldgenwoodlandmansionpieces_b.a(this.b, flag), blockposition1, enumblockrotation.a(EnumBlockRotation.CLOCKWISE_180)));
            } else if (enumdirection1 == EnumDirection.WEST && enumdirection == EnumDirection.SOUTH) {
                blockposition1 = blockposition.shift(enumblockrotation.a(EnumDirection.EAST), 7);
                list.add(new WorldGenWoodlandMansionPieces.i(this.a, worldgenwoodlandmansionpieces_b.a(this.b, flag), blockposition1, enumblockrotation, EnumBlockMirror.FRONT_BACK));
            } else if (enumdirection1 == EnumDirection.SOUTH && enumdirection == EnumDirection.EAST) {
                blockposition1 = blockposition.shift(enumblockrotation.a(EnumDirection.EAST), 1);
                list.add(new WorldGenWoodlandMansionPieces.i(this.a, worldgenwoodlandmansionpieces_b.a(this.b, flag), blockposition1, enumblockrotation.a(EnumBlockRotation.CLOCKWISE_90), EnumBlockMirror.LEFT_RIGHT));
            } else if (enumdirection1 == EnumDirection.SOUTH && enumdirection == EnumDirection.WEST) {
                blockposition1 = blockposition.shift(enumblockrotation.a(EnumDirection.EAST), 7);
                list.add(new WorldGenWoodlandMansionPieces.i(this.a, worldgenwoodlandmansionpieces_b.a(this.b, flag), blockposition1, enumblockrotation.a(EnumBlockRotation.CLOCKWISE_90)));
            } else if (enumdirection1 == EnumDirection.NORTH && enumdirection == EnumDirection.WEST) {
                blockposition1 = blockposition.shift(enumblockrotation.a(EnumDirection.EAST), 7);
                blockposition1 = blockposition1.shift(enumblockrotation.a(EnumDirection.SOUTH), 6);
                list.add(new WorldGenWoodlandMansionPieces.i(this.a, worldgenwoodlandmansionpieces_b.a(this.b, flag), blockposition1, enumblockrotation.a(EnumBlockRotation.CLOCKWISE_90), EnumBlockMirror.FRONT_BACK));
            } else if (enumdirection1 == EnumDirection.NORTH && enumdirection == EnumDirection.EAST) {
                blockposition1 = blockposition.shift(enumblockrotation.a(EnumDirection.EAST), 1);
                blockposition1 = blockposition1.shift(enumblockrotation.a(EnumDirection.SOUTH), 6);
                list.add(new WorldGenWoodlandMansionPieces.i(this.a, worldgenwoodlandmansionpieces_b.a(this.b, flag), blockposition1, enumblockrotation.a(EnumBlockRotation.COUNTERCLOCKWISE_90)));
            } else if (enumdirection1 == EnumDirection.SOUTH && enumdirection == EnumDirection.NORTH) {
                blockposition1 = blockposition.shift(enumblockrotation.a(EnumDirection.EAST), 1);
                blockposition1 = blockposition1.shift(enumblockrotation.a(EnumDirection.NORTH), 8);
                list.add(new WorldGenWoodlandMansionPieces.i(this.a, worldgenwoodlandmansionpieces_b.b(this.b, flag), blockposition1, enumblockrotation));
            } else if (enumdirection1 == EnumDirection.NORTH && enumdirection == EnumDirection.SOUTH) {
                blockposition1 = blockposition.shift(enumblockrotation.a(EnumDirection.EAST), 7);
                blockposition1 = blockposition1.shift(enumblockrotation.a(EnumDirection.SOUTH), 14);
                list.add(new WorldGenWoodlandMansionPieces.i(this.a, worldgenwoodlandmansionpieces_b.b(this.b, flag), blockposition1, enumblockrotation.a(EnumBlockRotation.CLOCKWISE_180)));
            } else if (enumdirection1 == EnumDirection.WEST && enumdirection == EnumDirection.EAST) {
                blockposition1 = blockposition.shift(enumblockrotation.a(EnumDirection.EAST), 15);
                list.add(new WorldGenWoodlandMansionPieces.i(this.a, worldgenwoodlandmansionpieces_b.b(this.b, flag), blockposition1, enumblockrotation.a(EnumBlockRotation.CLOCKWISE_90)));
            } else if (enumdirection1 == EnumDirection.EAST && enumdirection == EnumDirection.WEST) {
                blockposition1 = blockposition.shift(enumblockrotation.a(EnumDirection.WEST), 7);
                blockposition1 = blockposition1.shift(enumblockrotation.a(EnumDirection.SOUTH), 6);
                list.add(new WorldGenWoodlandMansionPieces.i(this.a, worldgenwoodlandmansionpieces_b.b(this.b, flag), blockposition1, enumblockrotation.a(EnumBlockRotation.COUNTERCLOCKWISE_90)));
            } else if (enumdirection1 == EnumDirection.UP && enumdirection == EnumDirection.EAST) {
                blockposition1 = blockposition.shift(enumblockrotation.a(EnumDirection.EAST), 15);
                list.add(new WorldGenWoodlandMansionPieces.i(this.a, worldgenwoodlandmansionpieces_b.c(this.b), blockposition1, enumblockrotation.a(EnumBlockRotation.CLOCKWISE_90)));
            } else if (enumdirection1 == EnumDirection.UP && enumdirection == EnumDirection.SOUTH) {
                blockposition1 = blockposition.shift(enumblockrotation.a(EnumDirection.EAST), 1);
                blockposition1 = blockposition1.shift(enumblockrotation.a(EnumDirection.NORTH), 0);
                list.add(new WorldGenWoodlandMansionPieces.i(this.a, worldgenwoodlandmansionpieces_b.c(this.b), blockposition1, enumblockrotation));
            }

        }

        private void a(List<WorldGenWoodlandMansionPieces.i> list, BlockPosition blockposition, EnumBlockRotation enumblockrotation, EnumDirection enumdirection, EnumDirection enumdirection1, WorldGenWoodlandMansionPieces.b worldgenwoodlandmansionpieces_b) {
            byte b0 = 0;
            byte b1 = 0;
            EnumBlockRotation enumblockrotation1 = enumblockrotation;
            EnumBlockMirror enumblockmirror = EnumBlockMirror.NONE;

            if (enumdirection1 == EnumDirection.EAST && enumdirection == EnumDirection.SOUTH) {
                b0 = -7;
            } else if (enumdirection1 == EnumDirection.EAST && enumdirection == EnumDirection.NORTH) {
                b0 = -7;
                b1 = 6;
                enumblockmirror = EnumBlockMirror.LEFT_RIGHT;
            } else if (enumdirection1 == EnumDirection.NORTH && enumdirection == EnumDirection.EAST) {
                b0 = 1;
                b1 = 14;
                enumblockrotation1 = enumblockrotation.a(EnumBlockRotation.COUNTERCLOCKWISE_90);
            } else if (enumdirection1 == EnumDirection.NORTH && enumdirection == EnumDirection.WEST) {
                b0 = 7;
                b1 = 14;
                enumblockrotation1 = enumblockrotation.a(EnumBlockRotation.COUNTERCLOCKWISE_90);
                enumblockmirror = EnumBlockMirror.LEFT_RIGHT;
            } else if (enumdirection1 == EnumDirection.SOUTH && enumdirection == EnumDirection.WEST) {
                b0 = 7;
                b1 = -8;
                enumblockrotation1 = enumblockrotation.a(EnumBlockRotation.CLOCKWISE_90);
            } else if (enumdirection1 == EnumDirection.SOUTH && enumdirection == EnumDirection.EAST) {
                b0 = 1;
                b1 = -8;
                enumblockrotation1 = enumblockrotation.a(EnumBlockRotation.CLOCKWISE_90);
                enumblockmirror = EnumBlockMirror.LEFT_RIGHT;
            } else if (enumdirection1 == EnumDirection.WEST && enumdirection == EnumDirection.NORTH) {
                b0 = 15;
                b1 = 6;
                enumblockrotation1 = enumblockrotation.a(EnumBlockRotation.CLOCKWISE_180);
            } else if (enumdirection1 == EnumDirection.WEST && enumdirection == EnumDirection.SOUTH) {
                b0 = 15;
                enumblockmirror = EnumBlockMirror.FRONT_BACK;
            }

            BlockPosition blockposition1 = blockposition.shift(enumblockrotation.a(EnumDirection.EAST), b0);

            blockposition1 = blockposition1.shift(enumblockrotation.a(EnumDirection.SOUTH), b1);
            list.add(new WorldGenWoodlandMansionPieces.i(this.a, worldgenwoodlandmansionpieces_b.d(this.b), blockposition1, enumblockrotation1, enumblockmirror));
        }

        private void a(List<WorldGenWoodlandMansionPieces.i> list, BlockPosition blockposition, EnumBlockRotation enumblockrotation, WorldGenWoodlandMansionPieces.b worldgenwoodlandmansionpieces_b) {
            BlockPosition blockposition1 = blockposition.shift(enumblockrotation.a(EnumDirection.EAST), 1);

            list.add(new WorldGenWoodlandMansionPieces.i(this.a, worldgenwoodlandmansionpieces_b.e(this.b), blockposition1, enumblockrotation, EnumBlockMirror.NONE));
        }
    }

    static class e {

        public EnumBlockRotation a;
        public BlockPosition b;
        public String c;

        private e() {}
    }

    public static class i extends DefinedStructurePiece {

        private String d;
        private EnumBlockRotation e;
        private EnumBlockMirror f;

        public i() {}

        public i(DefinedStructureManager definedstructuremanager, String s, BlockPosition blockposition, EnumBlockRotation enumblockrotation) {
            this(definedstructuremanager, s, blockposition, enumblockrotation, EnumBlockMirror.NONE);
        }

        public i(DefinedStructureManager definedstructuremanager, String s, BlockPosition blockposition, EnumBlockRotation enumblockrotation, EnumBlockMirror enumblockmirror) {
            super(0);
            this.d = s;
            this.c = blockposition;
            this.e = enumblockrotation;
            this.f = enumblockmirror;
            this.a(definedstructuremanager);
        }

        private void a(DefinedStructureManager definedstructuremanager) {
            DefinedStructure definedstructure = definedstructuremanager.a(new MinecraftKey("woodland_mansion/" + this.d));
            DefinedStructureInfo definedstructureinfo = (new DefinedStructureInfo()).a(true).a(this.e).a(this.f);

            this.a(definedstructure, this.c, definedstructureinfo);
        }

        protected void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setString("Template", this.d);
            nbttagcompound.setString("Rot", this.b.c().name());
            nbttagcompound.setString("Mi", this.b.b().name());
        }

        protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {
            super.a(nbttagcompound, definedstructuremanager);
            this.d = nbttagcompound.getString("Template");
            this.e = EnumBlockRotation.valueOf(nbttagcompound.getString("Rot"));
            this.f = EnumBlockMirror.valueOf(nbttagcompound.getString("Mi"));
            this.a(definedstructuremanager);
        }

        protected void a(String s, BlockPosition blockposition, GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox) {
            if (s.startsWith("Chest")) {
                EnumBlockRotation enumblockrotation = this.b.c();
                IBlockData iblockdata = Blocks.CHEST.getBlockData();

                if ("ChestWest".equals(s)) {
                    iblockdata = (IBlockData) iblockdata.set(BlockChest.FACING, enumblockrotation.a(EnumDirection.WEST));
                } else if ("ChestEast".equals(s)) {
                    iblockdata = (IBlockData) iblockdata.set(BlockChest.FACING, enumblockrotation.a(EnumDirection.EAST));
                } else if ("ChestSouth".equals(s)) {
                    iblockdata = (IBlockData) iblockdata.set(BlockChest.FACING, enumblockrotation.a(EnumDirection.SOUTH));
                } else if ("ChestNorth".equals(s)) {
                    iblockdata = (IBlockData) iblockdata.set(BlockChest.FACING, enumblockrotation.a(EnumDirection.NORTH));
                }

                this.a(generatoraccess, structureboundingbox, random, blockposition, LootTables.o, iblockdata);
            } else if ("Mage".equals(s)) {
                EntityEvoker entityevoker = new EntityEvoker(generatoraccess.getMinecraftWorld());

                entityevoker.di();
                entityevoker.setPositionRotation(blockposition, 0.0F, 0.0F);
                generatoraccess.addEntity(entityevoker);
                generatoraccess.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 2);
            } else if ("Warrior".equals(s)) {
                EntityVindicator entityvindicator = new EntityVindicator(generatoraccess.getMinecraftWorld());

                entityvindicator.di();
                entityvindicator.setPositionRotation(blockposition, 0.0F, 0.0F);
                entityvindicator.prepare(generatoraccess.getDamageScaler(new BlockPosition(entityvindicator)), (GroupDataEntity) null, (NBTTagCompound) null);
                generatoraccess.addEntity(entityvindicator);
                generatoraccess.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 2);
            }

        }
    }
}
