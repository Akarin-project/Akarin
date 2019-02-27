package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WorldGenMonumentPieces {

    public static void a() {
        WorldGenFactory.a(WorldGenMonumentPieces.WorldGenMonumentPiece1.class, "OMB");
        WorldGenFactory.a(WorldGenMonumentPieces.WorldGenMonumentPiece2.class, "OMCR");
        WorldGenFactory.a(WorldGenMonumentPieces.WorldGenMonumentPiece3.class, "OMDXR");
        WorldGenFactory.a(WorldGenMonumentPieces.WorldGenMonumentPiece4.class, "OMDXYR");
        WorldGenFactory.a(WorldGenMonumentPieces.WorldGenMonumentPiece5.class, "OMDYR");
        WorldGenFactory.a(WorldGenMonumentPieces.WorldGenMonumentPiece6.class, "OMDYZR");
        WorldGenFactory.a(WorldGenMonumentPieces.WorldGenMonumentPiece7.class, "OMDZR");
        WorldGenFactory.a(WorldGenMonumentPieces.WorldGenMonumentPieceEntry.class, "OMEntry");
        WorldGenFactory.a(WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.class, "OMPenthouse");
        WorldGenFactory.a(WorldGenMonumentPieces.WorldGenMonumentPieceSimple.class, "OMSimple");
        WorldGenFactory.a(WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.class, "OMSimpleT");
    }

    static class WorldGenMonumentPieceSelector4 implements WorldGenMonumentPieces.IWorldGenMonumentPieceSelector {

        private WorldGenMonumentPieceSelector4() {}

        public boolean a(WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            if (worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.NORTH.a()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.NORTH.a()].d && worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.UP.a()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.UP.a()].d) {
                WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker1 = worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.NORTH.a()];

                return worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.UP.a()] && !worldgenmonumentpieces_worldgenmonumentstatetracker1.b[EnumDirection.UP.a()].d;
            } else {
                return false;
            }
        }

        public WorldGenMonumentPieces.WorldGenMonumentPiece a(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            worldgenmonumentpieces_worldgenmonumentstatetracker.d = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.NORTH.a()].d = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.UP.a()].d = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.NORTH.a()].b[EnumDirection.UP.a()].d = true;
            return new WorldGenMonumentPieces.WorldGenMonumentPiece6(enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, random);
        }
    }

    static class WorldGenMonumentPieceSelector6 implements WorldGenMonumentPieces.IWorldGenMonumentPieceSelector {

        private WorldGenMonumentPieceSelector6() {}

        public boolean a(WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            if (worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.EAST.a()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.EAST.a()].d && worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.UP.a()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.UP.a()].d) {
                WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker1 = worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.EAST.a()];

                return worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.UP.a()] && !worldgenmonumentpieces_worldgenmonumentstatetracker1.b[EnumDirection.UP.a()].d;
            } else {
                return false;
            }
        }

        public WorldGenMonumentPieces.WorldGenMonumentPiece a(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            worldgenmonumentpieces_worldgenmonumentstatetracker.d = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.EAST.a()].d = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.UP.a()].d = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.EAST.a()].b[EnumDirection.UP.a()].d = true;
            return new WorldGenMonumentPieces.WorldGenMonumentPiece4(enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, random);
        }
    }

    static class WorldGenMonumentPieceSelector3 implements WorldGenMonumentPieces.IWorldGenMonumentPieceSelector {

        private WorldGenMonumentPieceSelector3() {}

        public boolean a(WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            return worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.NORTH.a()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.NORTH.a()].d;
        }

        public WorldGenMonumentPieces.WorldGenMonumentPiece a(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker1 = worldgenmonumentpieces_worldgenmonumentstatetracker;

            if (!worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.NORTH.a()] || worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.NORTH.a()].d) {
                worldgenmonumentpieces_worldgenmonumentstatetracker1 = worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.SOUTH.a()];
            }

            worldgenmonumentpieces_worldgenmonumentstatetracker1.d = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker1.b[EnumDirection.NORTH.a()].d = true;
            return new WorldGenMonumentPieces.WorldGenMonumentPiece7(enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker1, random);
        }
    }

    static class WorldGenMonumentPieceSelector7 implements WorldGenMonumentPieces.IWorldGenMonumentPieceSelector {

        private WorldGenMonumentPieceSelector7() {}

        public boolean a(WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            return worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.EAST.a()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.EAST.a()].d;
        }

        public WorldGenMonumentPieces.WorldGenMonumentPiece a(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            worldgenmonumentpieces_worldgenmonumentstatetracker.d = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.EAST.a()].d = true;
            return new WorldGenMonumentPieces.WorldGenMonumentPiece3(enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, random);
        }
    }

    static class WorldGenMonumentPieceSelector5 implements WorldGenMonumentPieces.IWorldGenMonumentPieceSelector {

        private WorldGenMonumentPieceSelector5() {}

        public boolean a(WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            return worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.UP.a()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.UP.a()].d;
        }

        public WorldGenMonumentPieces.WorldGenMonumentPiece a(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            worldgenmonumentpieces_worldgenmonumentstatetracker.d = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.UP.a()].d = true;
            return new WorldGenMonumentPieces.WorldGenMonumentPiece5(enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, random);
        }
    }

    static class WorldGenMonumentPieceSelector1 implements WorldGenMonumentPieces.IWorldGenMonumentPieceSelector {

        private WorldGenMonumentPieceSelector1() {}

        public boolean a(WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            return !worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.WEST.a()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.EAST.a()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.NORTH.a()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.SOUTH.a()] && !worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.UP.a()];
        }

        public WorldGenMonumentPieces.WorldGenMonumentPiece a(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            worldgenmonumentpieces_worldgenmonumentstatetracker.d = true;
            return new WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT(enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, random);
        }
    }

    static class WorldGenMonumentPieceSelector2 implements WorldGenMonumentPieces.IWorldGenMonumentPieceSelector {

        private WorldGenMonumentPieceSelector2() {}

        public boolean a(WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            return true;
        }

        public WorldGenMonumentPieces.WorldGenMonumentPiece a(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            worldgenmonumentpieces_worldgenmonumentstatetracker.d = true;
            return new WorldGenMonumentPieces.WorldGenMonumentPieceSimple(enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, random);
        }
    }

    interface IWorldGenMonumentPieceSelector {

        boolean a(WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker);

        WorldGenMonumentPieces.WorldGenMonumentPiece a(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random);
    }

    static class WorldGenMonumentStateTracker {

        private final int a;
        private final WorldGenMonumentPieces.WorldGenMonumentStateTracker[] b = new WorldGenMonumentPieces.WorldGenMonumentStateTracker[6];
        private final boolean[] c = new boolean[6];
        private boolean d;
        private boolean e;
        private int f;

        public WorldGenMonumentStateTracker(int i) {
            this.a = i;
        }

        public void a(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            this.b[enumdirection.a()] = worldgenmonumentpieces_worldgenmonumentstatetracker;
            worldgenmonumentpieces_worldgenmonumentstatetracker.b[enumdirection.opposite().a()] = this;
        }

        public void a() {
            for (int i = 0; i < 6; ++i) {
                this.c[i] = this.b[i] != null;
            }

        }

        public boolean a(int i) {
            if (this.e) {
                return true;
            } else {
                this.f = i;

                for (int j = 0; j < 6; ++j) {
                    if (this.b[j] != null && this.c[j] && this.b[j].f != i && this.b[j].a(i)) {
                        return true;
                    }
                }

                return false;
            }
        }

        public boolean b() {
            return this.a >= 75;
        }

        public int c() {
            int i = 0;

            for (int j = 0; j < 6; ++j) {
                if (this.c[j]) {
                    ++i;
                }
            }

            return i;
        }
    }

    public static class WorldGenMonumentPiecePenthouse extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        public WorldGenMonumentPiecePenthouse() {}

        public WorldGenMonumentPiecePenthouse(EnumDirection enumdirection, StructureBoundingBox structureboundingbox) {
            super(enumdirection, structureboundingbox);
        }

        public boolean a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            this.a(generatoraccess, structureboundingbox, 2, -1, 2, 11, -1, 11, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, false);
            this.a(generatoraccess, structureboundingbox, 0, -1, 0, 1, -1, 11, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.a, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.a, false);
            this.a(generatoraccess, structureboundingbox, 12, -1, 0, 13, -1, 11, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.a, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.a, false);
            this.a(generatoraccess, structureboundingbox, 2, -1, 0, 11, -1, 1, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.a, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.a, false);
            this.a(generatoraccess, structureboundingbox, 2, -1, 12, 11, -1, 13, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.a, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.a, false);
            this.a(generatoraccess, structureboundingbox, 0, 0, 0, 0, 0, 13, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, false);
            this.a(generatoraccess, structureboundingbox, 13, 0, 0, 13, 0, 13, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 0, 0, 12, 0, 0, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 0, 13, 12, 0, 13, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, false);

            for (int i = 2; i <= 11; i += 3) {
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.e, 0, 0, i, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.e, 13, 0, i, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.e, i, 0, 0, structureboundingbox);
            }

            this.a(generatoraccess, structureboundingbox, 2, 0, 3, 4, 0, 9, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, false);
            this.a(generatoraccess, structureboundingbox, 9, 0, 3, 11, 0, 9, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, false);
            this.a(generatoraccess, structureboundingbox, 4, 0, 9, 9, 0, 11, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, false);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, 5, 0, 8, structureboundingbox);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, 8, 0, 8, structureboundingbox);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, 10, 0, 10, structureboundingbox);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, 3, 0, 10, structureboundingbox);
            this.a(generatoraccess, structureboundingbox, 3, 0, 3, 3, 0, 7, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.c, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.c, false);
            this.a(generatoraccess, structureboundingbox, 10, 0, 3, 10, 0, 7, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.c, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.c, false);
            this.a(generatoraccess, structureboundingbox, 6, 0, 10, 7, 0, 10, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.c, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.c, false);
            byte b0 = 3;

            for (int j = 0; j < 2; ++j) {
                for (int k = 2; k <= 8; k += 3) {
                    this.a(generatoraccess, structureboundingbox, b0, 0, k, b0, 2, k, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, false);
                }

                b0 = 10;
            }

            this.a(generatoraccess, structureboundingbox, 5, 0, 10, 5, 2, 10, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, false);
            this.a(generatoraccess, structureboundingbox, 8, 0, 10, 8, 2, 10, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.b, false);
            this.a(generatoraccess, structureboundingbox, 6, -1, 7, 7, -1, 8, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.c, WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse.c, false);
            this.a(generatoraccess, structureboundingbox, 6, -1, 3, 7, -1, 4);
            this.a(generatoraccess, structureboundingbox, 6, 1, 6);
            return true;
        }
    }

    public static class WorldGenMonumentPiece8 extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        private int p;

        public WorldGenMonumentPiece8() {}

        public WorldGenMonumentPiece8(EnumDirection enumdirection, StructureBoundingBox structureboundingbox, int i) {
            super(enumdirection, structureboundingbox);
            this.p = i & 1;
        }

        public boolean a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            if (this.p == 0) {
                int i;

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccess, structureboundingbox, 10 - i, 3 - i, 20 - i, 12 + i, 3 - i, 20, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                }

                this.a(generatoraccess, structureboundingbox, 7, 0, 6, 15, 0, 16, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                this.a(generatoraccess, structureboundingbox, 6, 0, 6, 6, 3, 20, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                this.a(generatoraccess, structureboundingbox, 16, 0, 6, 16, 3, 20, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                this.a(generatoraccess, structureboundingbox, 7, 1, 7, 7, 1, 20, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                this.a(generatoraccess, structureboundingbox, 15, 1, 7, 15, 1, 20, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                this.a(generatoraccess, structureboundingbox, 7, 1, 6, 9, 3, 6, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                this.a(generatoraccess, structureboundingbox, 13, 1, 6, 15, 3, 6, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                this.a(generatoraccess, structureboundingbox, 8, 1, 7, 9, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                this.a(generatoraccess, structureboundingbox, 13, 1, 7, 14, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                this.a(generatoraccess, structureboundingbox, 9, 0, 5, 13, 0, 5, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                this.a(generatoraccess, structureboundingbox, 10, 0, 7, 12, 0, 7, WorldGenMonumentPieces.WorldGenMonumentPiece8.c, WorldGenMonumentPieces.WorldGenMonumentPiece8.c, false);
                this.a(generatoraccess, structureboundingbox, 8, 0, 10, 8, 0, 12, WorldGenMonumentPieces.WorldGenMonumentPiece8.c, WorldGenMonumentPieces.WorldGenMonumentPiece8.c, false);
                this.a(generatoraccess, structureboundingbox, 14, 0, 10, 14, 0, 12, WorldGenMonumentPieces.WorldGenMonumentPiece8.c, WorldGenMonumentPieces.WorldGenMonumentPiece8.c, false);

                for (i = 18; i >= 7; i -= 3) {
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.e, 6, 3, i, structureboundingbox);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.e, 16, 3, i, structureboundingbox);
                }

                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.e, 10, 0, 10, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.e, 12, 0, 10, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.e, 10, 0, 12, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.e, 12, 0, 12, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.e, 8, 3, 6, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.e, 14, 3, 6, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, 4, 2, 4, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.e, 4, 1, 4, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, 4, 0, 4, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, 18, 2, 4, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.e, 18, 1, 4, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, 18, 0, 4, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, 4, 2, 18, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.e, 4, 1, 18, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, 4, 0, 18, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, 18, 2, 18, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.e, 18, 1, 18, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, 18, 0, 18, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, 9, 7, 20, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, 13, 7, 20, structureboundingbox);
                this.a(generatoraccess, structureboundingbox, 6, 0, 21, 7, 4, 21, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                this.a(generatoraccess, structureboundingbox, 15, 0, 21, 16, 4, 21, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                this.a(generatoraccess, structureboundingbox, 11, 2, 16);
            } else if (this.p == 1) {
                this.a(generatoraccess, structureboundingbox, 9, 3, 18, 13, 3, 20, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                this.a(generatoraccess, structureboundingbox, 9, 0, 18, 9, 2, 18, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                this.a(generatoraccess, structureboundingbox, 13, 0, 18, 13, 2, 18, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                byte b0 = 9;
                boolean flag = true;
                boolean flag1 = true;

                int j;

                for (j = 0; j < 2; ++j) {
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, b0, 6, 20, structureboundingbox);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.e, b0, 5, 20, structureboundingbox);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, b0, 4, 20, structureboundingbox);
                    b0 = 13;
                }

                this.a(generatoraccess, structureboundingbox, 7, 3, 7, 15, 3, 14, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                b0 = 10;

                for (j = 0; j < 2; ++j) {
                    this.a(generatoraccess, structureboundingbox, b0, 0, 10, b0, 6, 10, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                    this.a(generatoraccess, structureboundingbox, b0, 0, 12, b0, 6, 12, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.e, b0, 0, 10, structureboundingbox);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.e, b0, 0, 12, structureboundingbox);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.e, b0, 4, 10, structureboundingbox);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece8.e, b0, 4, 12, structureboundingbox);
                    b0 = 12;
                }

                b0 = 8;

                for (j = 0; j < 2; ++j) {
                    this.a(generatoraccess, structureboundingbox, b0, 0, 7, b0, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                    this.a(generatoraccess, structureboundingbox, b0, 0, 14, b0, 2, 14, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, WorldGenMonumentPieces.WorldGenMonumentPiece8.b, false);
                    b0 = 14;
                }

                this.a(generatoraccess, structureboundingbox, 8, 3, 8, 8, 3, 13, WorldGenMonumentPieces.WorldGenMonumentPiece8.c, WorldGenMonumentPieces.WorldGenMonumentPiece8.c, false);
                this.a(generatoraccess, structureboundingbox, 14, 3, 8, 14, 3, 13, WorldGenMonumentPieces.WorldGenMonumentPiece8.c, WorldGenMonumentPieces.WorldGenMonumentPiece8.c, false);
                this.a(generatoraccess, structureboundingbox, 11, 5, 13);
            }

            return true;
        }
    }

    public static class WorldGenMonumentPiece2 extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        public WorldGenMonumentPiece2() {}

        public WorldGenMonumentPiece2(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            super(1, enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, 2, 2, 2);
        }

        public boolean a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            this.a(generatoraccess, structureboundingbox, 1, 8, 0, 14, 8, 14, WorldGenMonumentPieces.WorldGenMonumentPiece2.a);
            boolean flag = true;
            IBlockData iblockdata = WorldGenMonumentPieces.WorldGenMonumentPiece2.b;

            this.a(generatoraccess, structureboundingbox, 0, 7, 0, 0, 7, 15, iblockdata, iblockdata, false);
            this.a(generatoraccess, structureboundingbox, 15, 7, 0, 15, 7, 15, iblockdata, iblockdata, false);
            this.a(generatoraccess, structureboundingbox, 1, 7, 0, 15, 7, 0, iblockdata, iblockdata, false);
            this.a(generatoraccess, structureboundingbox, 1, 7, 15, 14, 7, 15, iblockdata, iblockdata, false);

            int i;

            for (i = 1; i <= 6; ++i) {
                iblockdata = WorldGenMonumentPieces.WorldGenMonumentPiece2.b;
                if (i == 2 || i == 6) {
                    iblockdata = WorldGenMonumentPieces.WorldGenMonumentPiece2.a;
                }

                for (int j = 0; j <= 15; j += 15) {
                    this.a(generatoraccess, structureboundingbox, j, i, 0, j, i, 1, iblockdata, iblockdata, false);
                    this.a(generatoraccess, structureboundingbox, j, i, 6, j, i, 9, iblockdata, iblockdata, false);
                    this.a(generatoraccess, structureboundingbox, j, i, 14, j, i, 15, iblockdata, iblockdata, false);
                }

                this.a(generatoraccess, structureboundingbox, 1, i, 0, 1, i, 0, iblockdata, iblockdata, false);
                this.a(generatoraccess, structureboundingbox, 6, i, 0, 9, i, 0, iblockdata, iblockdata, false);
                this.a(generatoraccess, structureboundingbox, 14, i, 0, 14, i, 0, iblockdata, iblockdata, false);
                this.a(generatoraccess, structureboundingbox, 1, i, 15, 14, i, 15, iblockdata, iblockdata, false);
            }

            this.a(generatoraccess, structureboundingbox, 6, 3, 6, 9, 6, 9, WorldGenMonumentPieces.WorldGenMonumentPiece2.c, WorldGenMonumentPieces.WorldGenMonumentPiece2.c, false);
            this.a(generatoraccess, structureboundingbox, 7, 4, 7, 8, 5, 8, Blocks.GOLD_BLOCK.getBlockData(), Blocks.GOLD_BLOCK.getBlockData(), false);

            for (i = 3; i <= 6; i += 3) {
                for (int k = 6; k <= 9; k += 3) {
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece2.e, k, i, 6, structureboundingbox);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece2.e, k, i, 9, structureboundingbox);
                }
            }

            this.a(generatoraccess, structureboundingbox, 5, 1, 6, 5, 2, 6, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 5, 1, 9, 5, 2, 9, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 10, 1, 6, 10, 2, 6, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 10, 1, 9, 10, 2, 9, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 6, 1, 5, 6, 2, 5, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 9, 1, 5, 9, 2, 5, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 6, 1, 10, 6, 2, 10, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 9, 1, 10, 9, 2, 10, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 5, 2, 5, 5, 6, 5, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 5, 2, 10, 5, 6, 10, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 10, 2, 5, 10, 6, 5, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 10, 2, 10, 10, 6, 10, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 5, 7, 1, 5, 7, 6, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 10, 7, 1, 10, 7, 6, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 5, 7, 9, 5, 7, 14, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 10, 7, 9, 10, 7, 14, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 7, 5, 6, 7, 5, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 7, 10, 6, 7, 10, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 9, 7, 5, 14, 7, 5, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 9, 7, 10, 14, 7, 10, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 2, 1, 2, 2, 1, 3, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 3, 1, 2, 3, 1, 2, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 13, 1, 2, 13, 1, 3, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 12, 1, 2, 12, 1, 2, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 2, 1, 12, 2, 1, 13, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 3, 1, 13, 3, 1, 13, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 13, 1, 12, 13, 1, 13, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            this.a(generatoraccess, structureboundingbox, 12, 1, 13, 12, 1, 13, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, WorldGenMonumentPieces.WorldGenMonumentPiece2.b, false);
            return true;
        }
    }

    public static class WorldGenMonumentPiece6 extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        public WorldGenMonumentPiece6() {}

        public WorldGenMonumentPiece6(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            super(1, enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, 1, 2, 2);
        }

        public boolean a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker = this.l.b[EnumDirection.NORTH.a()];
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker1 = this.l;
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker2 = worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.UP.a()];
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker3 = worldgenmonumentpieces_worldgenmonumentstatetracker1.b[EnumDirection.UP.a()];

            if (this.l.a / 25 > 0) {
                this.a(generatoraccess, structureboundingbox, 0, 8, worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.DOWN.a()]);
                this.a(generatoraccess, structureboundingbox, 0, 0, worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.DOWN.a()]);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker3.b[EnumDirection.UP.a()] == null) {
                this.a(generatoraccess, structureboundingbox, 1, 8, 1, 6, 8, 7, WorldGenMonumentPieces.WorldGenMonumentPiece6.a);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker2.b[EnumDirection.UP.a()] == null) {
                this.a(generatoraccess, structureboundingbox, 1, 8, 8, 6, 8, 14, WorldGenMonumentPieces.WorldGenMonumentPiece6.a);
            }

            IBlockData iblockdata;
            int i;

            for (i = 1; i <= 7; ++i) {
                iblockdata = WorldGenMonumentPieces.WorldGenMonumentPiece6.b;
                if (i == 2 || i == 6) {
                    iblockdata = WorldGenMonumentPieces.WorldGenMonumentPiece6.a;
                }

                this.a(generatoraccess, structureboundingbox, 0, i, 0, 0, i, 15, iblockdata, iblockdata, false);
                this.a(generatoraccess, structureboundingbox, 7, i, 0, 7, i, 15, iblockdata, iblockdata, false);
                this.a(generatoraccess, structureboundingbox, 1, i, 0, 6, i, 0, iblockdata, iblockdata, false);
                this.a(generatoraccess, structureboundingbox, 1, i, 15, 6, i, 15, iblockdata, iblockdata, false);
            }

            for (i = 1; i <= 7; ++i) {
                iblockdata = WorldGenMonumentPieces.WorldGenMonumentPiece6.c;
                if (i == 2 || i == 6) {
                    iblockdata = WorldGenMonumentPieces.WorldGenMonumentPiece6.e;
                }

                this.a(generatoraccess, structureboundingbox, 3, i, 7, 4, i, 8, iblockdata, iblockdata, false);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.SOUTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 3, 1, 0, 4, 2, 0);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.EAST.a()]) {
                this.a(generatoraccess, structureboundingbox, 7, 1, 3, 7, 2, 4);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.WEST.a()]) {
                this.a(generatoraccess, structureboundingbox, 0, 1, 3, 0, 2, 4);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.NORTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 3, 1, 15, 4, 2, 15);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.WEST.a()]) {
                this.a(generatoraccess, structureboundingbox, 0, 1, 11, 0, 2, 12);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.EAST.a()]) {
                this.a(generatoraccess, structureboundingbox, 7, 1, 11, 7, 2, 12);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker3.c[EnumDirection.SOUTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 3, 5, 0, 4, 6, 0);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker3.c[EnumDirection.EAST.a()]) {
                this.a(generatoraccess, structureboundingbox, 7, 5, 3, 7, 6, 4);
                this.a(generatoraccess, structureboundingbox, 5, 4, 2, 6, 4, 5, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, false);
                this.a(generatoraccess, structureboundingbox, 6, 1, 2, 6, 3, 2, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, false);
                this.a(generatoraccess, structureboundingbox, 6, 1, 5, 6, 3, 5, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, false);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker3.c[EnumDirection.WEST.a()]) {
                this.a(generatoraccess, structureboundingbox, 0, 5, 3, 0, 6, 4);
                this.a(generatoraccess, structureboundingbox, 1, 4, 2, 2, 4, 5, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, false);
                this.a(generatoraccess, structureboundingbox, 1, 1, 2, 1, 3, 2, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, false);
                this.a(generatoraccess, structureboundingbox, 1, 1, 5, 1, 3, 5, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, false);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker2.c[EnumDirection.NORTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 3, 5, 15, 4, 6, 15);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker2.c[EnumDirection.WEST.a()]) {
                this.a(generatoraccess, structureboundingbox, 0, 5, 11, 0, 6, 12);
                this.a(generatoraccess, structureboundingbox, 1, 4, 10, 2, 4, 13, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, false);
                this.a(generatoraccess, structureboundingbox, 1, 1, 10, 1, 3, 10, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, false);
                this.a(generatoraccess, structureboundingbox, 1, 1, 13, 1, 3, 13, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, false);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker2.c[EnumDirection.EAST.a()]) {
                this.a(generatoraccess, structureboundingbox, 7, 5, 11, 7, 6, 12);
                this.a(generatoraccess, structureboundingbox, 5, 4, 10, 6, 4, 13, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, false);
                this.a(generatoraccess, structureboundingbox, 6, 1, 10, 6, 3, 10, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, false);
                this.a(generatoraccess, structureboundingbox, 6, 1, 13, 6, 3, 13, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, WorldGenMonumentPieces.WorldGenMonumentPiece6.b, false);
            }

            return true;
        }
    }

    public static class WorldGenMonumentPiece4 extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        public WorldGenMonumentPiece4() {}

        public WorldGenMonumentPiece4(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            super(1, enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, 2, 2, 1);
        }

        public boolean a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker = this.l.b[EnumDirection.EAST.a()];
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker1 = this.l;
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker2 = worldgenmonumentpieces_worldgenmonumentstatetracker1.b[EnumDirection.UP.a()];
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker3 = worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.UP.a()];

            if (this.l.a / 25 > 0) {
                this.a(generatoraccess, structureboundingbox, 8, 0, worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.DOWN.a()]);
                this.a(generatoraccess, structureboundingbox, 0, 0, worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.DOWN.a()]);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker2.b[EnumDirection.UP.a()] == null) {
                this.a(generatoraccess, structureboundingbox, 1, 8, 1, 7, 8, 6, WorldGenMonumentPieces.WorldGenMonumentPiece4.a);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker3.b[EnumDirection.UP.a()] == null) {
                this.a(generatoraccess, structureboundingbox, 8, 8, 1, 14, 8, 6, WorldGenMonumentPieces.WorldGenMonumentPiece4.a);
            }

            for (int i = 1; i <= 7; ++i) {
                IBlockData iblockdata = WorldGenMonumentPieces.WorldGenMonumentPiece4.b;

                if (i == 2 || i == 6) {
                    iblockdata = WorldGenMonumentPieces.WorldGenMonumentPiece4.a;
                }

                this.a(generatoraccess, structureboundingbox, 0, i, 0, 0, i, 7, iblockdata, iblockdata, false);
                this.a(generatoraccess, structureboundingbox, 15, i, 0, 15, i, 7, iblockdata, iblockdata, false);
                this.a(generatoraccess, structureboundingbox, 1, i, 0, 15, i, 0, iblockdata, iblockdata, false);
                this.a(generatoraccess, structureboundingbox, 1, i, 7, 14, i, 7, iblockdata, iblockdata, false);
            }

            this.a(generatoraccess, structureboundingbox, 2, 1, 3, 2, 7, 4, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, false);
            this.a(generatoraccess, structureboundingbox, 3, 1, 2, 4, 7, 2, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, false);
            this.a(generatoraccess, structureboundingbox, 3, 1, 5, 4, 7, 5, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, false);
            this.a(generatoraccess, structureboundingbox, 13, 1, 3, 13, 7, 4, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, false);
            this.a(generatoraccess, structureboundingbox, 11, 1, 2, 12, 7, 2, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, false);
            this.a(generatoraccess, structureboundingbox, 11, 1, 5, 12, 7, 5, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, false);
            this.a(generatoraccess, structureboundingbox, 5, 1, 3, 5, 3, 4, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, false);
            this.a(generatoraccess, structureboundingbox, 10, 1, 3, 10, 3, 4, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, false);
            this.a(generatoraccess, structureboundingbox, 5, 7, 2, 10, 7, 5, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, false);
            this.a(generatoraccess, structureboundingbox, 5, 5, 2, 5, 7, 2, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, false);
            this.a(generatoraccess, structureboundingbox, 10, 5, 2, 10, 7, 2, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, false);
            this.a(generatoraccess, structureboundingbox, 5, 5, 5, 5, 7, 5, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, false);
            this.a(generatoraccess, structureboundingbox, 10, 5, 5, 10, 7, 5, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, false);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, 6, 6, 2, structureboundingbox);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, 9, 6, 2, structureboundingbox);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, 6, 6, 5, structureboundingbox);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, 9, 6, 5, structureboundingbox);
            this.a(generatoraccess, structureboundingbox, 5, 4, 3, 6, 4, 4, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, false);
            this.a(generatoraccess, structureboundingbox, 9, 4, 3, 10, 4, 4, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, WorldGenMonumentPieces.WorldGenMonumentPiece4.b, false);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece4.e, 5, 4, 2, structureboundingbox);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece4.e, 5, 4, 5, structureboundingbox);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece4.e, 10, 4, 2, structureboundingbox);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece4.e, 10, 4, 5, structureboundingbox);
            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.SOUTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 3, 1, 0, 4, 2, 0);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.NORTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 3, 1, 7, 4, 2, 7);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.WEST.a()]) {
                this.a(generatoraccess, structureboundingbox, 0, 1, 3, 0, 2, 4);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.SOUTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 11, 1, 0, 12, 2, 0);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.NORTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 11, 1, 7, 12, 2, 7);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.EAST.a()]) {
                this.a(generatoraccess, structureboundingbox, 15, 1, 3, 15, 2, 4);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker2.c[EnumDirection.SOUTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 3, 5, 0, 4, 6, 0);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker2.c[EnumDirection.NORTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 3, 5, 7, 4, 6, 7);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker2.c[EnumDirection.WEST.a()]) {
                this.a(generatoraccess, structureboundingbox, 0, 5, 3, 0, 6, 4);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker3.c[EnumDirection.SOUTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 11, 5, 0, 12, 6, 0);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker3.c[EnumDirection.NORTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 11, 5, 7, 12, 6, 7);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker3.c[EnumDirection.EAST.a()]) {
                this.a(generatoraccess, structureboundingbox, 15, 5, 3, 15, 6, 4);
            }

            return true;
        }
    }

    public static class WorldGenMonumentPiece7 extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        public WorldGenMonumentPiece7() {}

        public WorldGenMonumentPiece7(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            super(1, enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, 1, 1, 2);
        }

        public boolean a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker = this.l.b[EnumDirection.NORTH.a()];
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker1 = this.l;

            if (this.l.a / 25 > 0) {
                this.a(generatoraccess, structureboundingbox, 0, 8, worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.DOWN.a()]);
                this.a(generatoraccess, structureboundingbox, 0, 0, worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.DOWN.a()]);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.b[EnumDirection.UP.a()] == null) {
                this.a(generatoraccess, structureboundingbox, 1, 4, 1, 6, 4, 7, WorldGenMonumentPieces.WorldGenMonumentPiece7.a);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.UP.a()] == null) {
                this.a(generatoraccess, structureboundingbox, 1, 4, 8, 6, 4, 14, WorldGenMonumentPieces.WorldGenMonumentPiece7.a);
            }

            this.a(generatoraccess, structureboundingbox, 0, 3, 0, 0, 3, 15, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 7, 3, 0, 7, 3, 15, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 3, 0, 7, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 3, 15, 6, 3, 15, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 0, 2, 0, 0, 2, 15, WorldGenMonumentPieces.WorldGenMonumentPiece7.a, WorldGenMonumentPieces.WorldGenMonumentPiece7.a, false);
            this.a(generatoraccess, structureboundingbox, 7, 2, 0, 7, 2, 15, WorldGenMonumentPieces.WorldGenMonumentPiece7.a, WorldGenMonumentPieces.WorldGenMonumentPiece7.a, false);
            this.a(generatoraccess, structureboundingbox, 1, 2, 0, 7, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPiece7.a, WorldGenMonumentPieces.WorldGenMonumentPiece7.a, false);
            this.a(generatoraccess, structureboundingbox, 1, 2, 15, 6, 2, 15, WorldGenMonumentPieces.WorldGenMonumentPiece7.a, WorldGenMonumentPieces.WorldGenMonumentPiece7.a, false);
            this.a(generatoraccess, structureboundingbox, 0, 1, 0, 0, 1, 15, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 7, 1, 0, 7, 1, 15, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 1, 0, 7, 1, 0, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 1, 15, 6, 1, 15, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 1, 1, 1, 1, 2, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 6, 1, 1, 6, 1, 2, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 3, 1, 1, 3, 2, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 6, 3, 1, 6, 3, 2, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 1, 13, 1, 1, 14, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 6, 1, 13, 6, 1, 14, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 3, 13, 1, 3, 14, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 6, 3, 13, 6, 3, 14, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 2, 1, 6, 2, 3, 6, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 5, 1, 6, 5, 3, 6, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 2, 1, 9, 2, 3, 9, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 5, 1, 9, 5, 3, 9, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 3, 2, 6, 4, 2, 6, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 3, 2, 9, 4, 2, 9, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 2, 2, 7, 2, 2, 8, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, structureboundingbox, 5, 2, 7, 5, 2, 8, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, false);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece7.e, 2, 2, 5, structureboundingbox);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece7.e, 5, 2, 5, structureboundingbox);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece7.e, 2, 2, 10, structureboundingbox);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece7.e, 5, 2, 10, structureboundingbox);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, 2, 3, 5, structureboundingbox);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, 5, 3, 5, structureboundingbox);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, 2, 3, 10, structureboundingbox);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece7.b, 5, 3, 10, structureboundingbox);
            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.SOUTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 3, 1, 0, 4, 2, 0);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.EAST.a()]) {
                this.a(generatoraccess, structureboundingbox, 7, 1, 3, 7, 2, 4);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.WEST.a()]) {
                this.a(generatoraccess, structureboundingbox, 0, 1, 3, 0, 2, 4);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.NORTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 3, 1, 15, 4, 2, 15);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.WEST.a()]) {
                this.a(generatoraccess, structureboundingbox, 0, 1, 11, 0, 2, 12);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.EAST.a()]) {
                this.a(generatoraccess, structureboundingbox, 7, 1, 11, 7, 2, 12);
            }

            return true;
        }
    }

    public static class WorldGenMonumentPiece3 extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        public WorldGenMonumentPiece3() {}

        public WorldGenMonumentPiece3(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            super(1, enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, 2, 1, 1);
        }

        public boolean a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker = this.l.b[EnumDirection.EAST.a()];
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker1 = this.l;

            if (this.l.a / 25 > 0) {
                this.a(generatoraccess, structureboundingbox, 8, 0, worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.DOWN.a()]);
                this.a(generatoraccess, structureboundingbox, 0, 0, worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.DOWN.a()]);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.b[EnumDirection.UP.a()] == null) {
                this.a(generatoraccess, structureboundingbox, 1, 4, 1, 7, 4, 6, WorldGenMonumentPieces.WorldGenMonumentPiece3.a);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.UP.a()] == null) {
                this.a(generatoraccess, structureboundingbox, 8, 4, 1, 14, 4, 6, WorldGenMonumentPieces.WorldGenMonumentPiece3.a);
            }

            this.a(generatoraccess, structureboundingbox, 0, 3, 0, 0, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, false);
            this.a(generatoraccess, structureboundingbox, 15, 3, 0, 15, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 3, 0, 15, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 3, 7, 14, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, false);
            this.a(generatoraccess, structureboundingbox, 0, 2, 0, 0, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPiece3.a, WorldGenMonumentPieces.WorldGenMonumentPiece3.a, false);
            this.a(generatoraccess, structureboundingbox, 15, 2, 0, 15, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPiece3.a, WorldGenMonumentPieces.WorldGenMonumentPiece3.a, false);
            this.a(generatoraccess, structureboundingbox, 1, 2, 0, 15, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPiece3.a, WorldGenMonumentPieces.WorldGenMonumentPiece3.a, false);
            this.a(generatoraccess, structureboundingbox, 1, 2, 7, 14, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPiece3.a, WorldGenMonumentPieces.WorldGenMonumentPiece3.a, false);
            this.a(generatoraccess, structureboundingbox, 0, 1, 0, 0, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, false);
            this.a(generatoraccess, structureboundingbox, 15, 1, 0, 15, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 1, 0, 15, 1, 0, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 1, 7, 14, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, false);
            this.a(generatoraccess, structureboundingbox, 5, 1, 0, 10, 1, 4, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, false);
            this.a(generatoraccess, structureboundingbox, 6, 2, 0, 9, 2, 3, WorldGenMonumentPieces.WorldGenMonumentPiece3.a, WorldGenMonumentPieces.WorldGenMonumentPiece3.a, false);
            this.a(generatoraccess, structureboundingbox, 5, 3, 0, 10, 3, 4, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, WorldGenMonumentPieces.WorldGenMonumentPiece3.b, false);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece3.e, 6, 2, 3, structureboundingbox);
            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece3.e, 9, 2, 3, structureboundingbox);
            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.SOUTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 3, 1, 0, 4, 2, 0);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.NORTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 3, 1, 7, 4, 2, 7);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.WEST.a()]) {
                this.a(generatoraccess, structureboundingbox, 0, 1, 3, 0, 2, 4);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.SOUTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 11, 1, 0, 12, 2, 0);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.NORTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 11, 1, 7, 12, 2, 7);
            }

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.c[EnumDirection.EAST.a()]) {
                this.a(generatoraccess, structureboundingbox, 15, 1, 3, 15, 2, 4);
            }

            return true;
        }
    }

    public static class WorldGenMonumentPiece5 extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        public WorldGenMonumentPiece5() {}

        public WorldGenMonumentPiece5(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            super(1, enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, 1, 2, 1);
        }

        public boolean a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            if (this.l.a / 25 > 0) {
                this.a(generatoraccess, structureboundingbox, 0, 0, this.l.c[EnumDirection.DOWN.a()]);
            }

            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker = this.l.b[EnumDirection.UP.a()];

            if (worldgenmonumentpieces_worldgenmonumentstatetracker.b[EnumDirection.UP.a()] == null) {
                this.a(generatoraccess, structureboundingbox, 1, 8, 1, 6, 8, 6, WorldGenMonumentPieces.WorldGenMonumentPiece5.a);
            }

            this.a(generatoraccess, structureboundingbox, 0, 4, 0, 0, 4, 7, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
            this.a(generatoraccess, structureboundingbox, 7, 4, 0, 7, 4, 7, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 4, 0, 6, 4, 0, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 4, 7, 6, 4, 7, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
            this.a(generatoraccess, structureboundingbox, 2, 4, 1, 2, 4, 2, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 4, 2, 1, 4, 2, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
            this.a(generatoraccess, structureboundingbox, 5, 4, 1, 5, 4, 2, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
            this.a(generatoraccess, structureboundingbox, 6, 4, 2, 6, 4, 2, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
            this.a(generatoraccess, structureboundingbox, 2, 4, 5, 2, 4, 6, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 4, 5, 1, 4, 5, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
            this.a(generatoraccess, structureboundingbox, 5, 4, 5, 5, 4, 6, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
            this.a(generatoraccess, structureboundingbox, 6, 4, 5, 6, 4, 5, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker1 = this.l;

            for (int i = 1; i <= 5; i += 4) {
                byte b0 = 0;

                if (worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.SOUTH.a()]) {
                    this.a(generatoraccess, structureboundingbox, 2, i, b0, 2, i + 2, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
                    this.a(generatoraccess, structureboundingbox, 5, i, b0, 5, i + 2, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
                    this.a(generatoraccess, structureboundingbox, 3, i + 2, b0, 4, i + 2, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
                } else {
                    this.a(generatoraccess, structureboundingbox, 0, i, b0, 7, i + 2, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
                    this.a(generatoraccess, structureboundingbox, 0, i + 1, b0, 7, i + 1, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.a, WorldGenMonumentPieces.WorldGenMonumentPiece5.a, false);
                }

                b0 = 7;
                if (worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.NORTH.a()]) {
                    this.a(generatoraccess, structureboundingbox, 2, i, b0, 2, i + 2, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
                    this.a(generatoraccess, structureboundingbox, 5, i, b0, 5, i + 2, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
                    this.a(generatoraccess, structureboundingbox, 3, i + 2, b0, 4, i + 2, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
                } else {
                    this.a(generatoraccess, structureboundingbox, 0, i, b0, 7, i + 2, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
                    this.a(generatoraccess, structureboundingbox, 0, i + 1, b0, 7, i + 1, b0, WorldGenMonumentPieces.WorldGenMonumentPiece5.a, WorldGenMonumentPieces.WorldGenMonumentPiece5.a, false);
                }

                byte b1 = 0;

                if (worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.WEST.a()]) {
                    this.a(generatoraccess, structureboundingbox, b1, i, 2, b1, i + 2, 2, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
                    this.a(generatoraccess, structureboundingbox, b1, i, 5, b1, i + 2, 5, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
                    this.a(generatoraccess, structureboundingbox, b1, i + 2, 3, b1, i + 2, 4, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
                } else {
                    this.a(generatoraccess, structureboundingbox, b1, i, 0, b1, i + 2, 7, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
                    this.a(generatoraccess, structureboundingbox, b1, i + 1, 0, b1, i + 1, 7, WorldGenMonumentPieces.WorldGenMonumentPiece5.a, WorldGenMonumentPieces.WorldGenMonumentPiece5.a, false);
                }

                b1 = 7;
                if (worldgenmonumentpieces_worldgenmonumentstatetracker1.c[EnumDirection.EAST.a()]) {
                    this.a(generatoraccess, structureboundingbox, b1, i, 2, b1, i + 2, 2, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
                    this.a(generatoraccess, structureboundingbox, b1, i, 5, b1, i + 2, 5, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
                    this.a(generatoraccess, structureboundingbox, b1, i + 2, 3, b1, i + 2, 4, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
                } else {
                    this.a(generatoraccess, structureboundingbox, b1, i, 0, b1, i + 2, 7, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, WorldGenMonumentPieces.WorldGenMonumentPiece5.b, false);
                    this.a(generatoraccess, structureboundingbox, b1, i + 1, 0, b1, i + 1, 7, WorldGenMonumentPieces.WorldGenMonumentPiece5.a, WorldGenMonumentPieces.WorldGenMonumentPiece5.a, false);
                }

                worldgenmonumentpieces_worldgenmonumentstatetracker1 = worldgenmonumentpieces_worldgenmonumentstatetracker;
            }

            return true;
        }
    }

    public static class WorldGenMonumentPieceSimpleT extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        public WorldGenMonumentPieceSimpleT() {}

        public WorldGenMonumentPieceSimpleT(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            super(1, enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, 1, 1, 1);
        }

        public boolean a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            if (this.l.a / 25 > 0) {
                this.a(generatoraccess, structureboundingbox, 0, 0, this.l.c[EnumDirection.DOWN.a()]);
            }

            if (this.l.b[EnumDirection.UP.a()] == null) {
                this.a(generatoraccess, structureboundingbox, 1, 4, 1, 6, 4, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.a);
            }

            for (int i = 1; i <= 6; ++i) {
                for (int j = 1; j <= 6; ++j) {
                    if (random.nextInt(3) != 0) {
                        int k = 2 + (random.nextInt(4) == 0 ? 0 : 1);
                        IBlockData iblockdata = Blocks.WET_SPONGE.getBlockData();

                        this.a(generatoraccess, structureboundingbox, i, k, j, i, 3, j, iblockdata, iblockdata, false);
                    }
                }
            }

            this.a(generatoraccess, structureboundingbox, 0, 1, 0, 0, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.b, false);
            this.a(generatoraccess, structureboundingbox, 7, 1, 0, 7, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 1, 0, 6, 1, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 1, 7, 6, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.b, false);
            this.a(generatoraccess, structureboundingbox, 0, 2, 0, 0, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.c, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.c, false);
            this.a(generatoraccess, structureboundingbox, 7, 2, 0, 7, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.c, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.c, false);
            this.a(generatoraccess, structureboundingbox, 1, 2, 0, 6, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.c, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.c, false);
            this.a(generatoraccess, structureboundingbox, 1, 2, 7, 6, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.c, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.c, false);
            this.a(generatoraccess, structureboundingbox, 0, 3, 0, 0, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.b, false);
            this.a(generatoraccess, structureboundingbox, 7, 3, 0, 7, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 3, 0, 6, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 3, 7, 6, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.b, false);
            this.a(generatoraccess, structureboundingbox, 0, 1, 3, 0, 2, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.c, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.c, false);
            this.a(generatoraccess, structureboundingbox, 7, 1, 3, 7, 2, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.c, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.c, false);
            this.a(generatoraccess, structureboundingbox, 3, 1, 0, 4, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.c, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.c, false);
            this.a(generatoraccess, structureboundingbox, 3, 1, 7, 4, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.c, WorldGenMonumentPieces.WorldGenMonumentPieceSimpleT.c, false);
            if (this.l.c[EnumDirection.SOUTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 3, 1, 0, 4, 2, 0);
            }

            return true;
        }
    }

    public static class WorldGenMonumentPieceSimple extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        private int p;

        public WorldGenMonumentPieceSimple() {}

        public WorldGenMonumentPieceSimple(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, Random random) {
            super(1, enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, 1, 1, 1);
            this.p = random.nextInt(3);
        }

        public boolean a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            if (this.l.a / 25 > 0) {
                this.a(generatoraccess, structureboundingbox, 0, 0, this.l.c[EnumDirection.DOWN.a()]);
            }

            if (this.l.b[EnumDirection.UP.a()] == null) {
                this.a(generatoraccess, structureboundingbox, 1, 4, 1, 6, 4, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a);
            }

            boolean flag = this.p != 0 && random.nextBoolean() && !this.l.c[EnumDirection.DOWN.a()] && !this.l.c[EnumDirection.UP.a()] && this.l.c() > 1;

            if (this.p == 0) {
                this.a(generatoraccess, structureboundingbox, 0, 1, 0, 2, 1, 2, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 0, 3, 0, 2, 3, 2, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 0, 2, 0, 0, 2, 2, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, false);
                this.a(generatoraccess, structureboundingbox, 1, 2, 0, 2, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, false);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.e, 1, 2, 1, structureboundingbox);
                this.a(generatoraccess, structureboundingbox, 5, 1, 0, 7, 1, 2, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 5, 3, 0, 7, 3, 2, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 7, 2, 0, 7, 2, 2, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, false);
                this.a(generatoraccess, structureboundingbox, 5, 2, 0, 6, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, false);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.e, 6, 2, 1, structureboundingbox);
                this.a(generatoraccess, structureboundingbox, 0, 1, 5, 2, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 0, 3, 5, 2, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 0, 2, 5, 0, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, false);
                this.a(generatoraccess, structureboundingbox, 1, 2, 7, 2, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, false);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.e, 1, 2, 6, structureboundingbox);
                this.a(generatoraccess, structureboundingbox, 5, 1, 5, 7, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 5, 3, 5, 7, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 7, 2, 5, 7, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, false);
                this.a(generatoraccess, structureboundingbox, 5, 2, 7, 6, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, false);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.e, 6, 2, 6, structureboundingbox);
                if (this.l.c[EnumDirection.SOUTH.a()]) {
                    this.a(generatoraccess, structureboundingbox, 3, 3, 0, 4, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                } else {
                    this.a(generatoraccess, structureboundingbox, 3, 3, 0, 4, 3, 1, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                    this.a(generatoraccess, structureboundingbox, 3, 2, 0, 4, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, false);
                    this.a(generatoraccess, structureboundingbox, 3, 1, 0, 4, 1, 1, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                }

                if (this.l.c[EnumDirection.NORTH.a()]) {
                    this.a(generatoraccess, structureboundingbox, 3, 3, 7, 4, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                } else {
                    this.a(generatoraccess, structureboundingbox, 3, 3, 6, 4, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                    this.a(generatoraccess, structureboundingbox, 3, 2, 7, 4, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, false);
                    this.a(generatoraccess, structureboundingbox, 3, 1, 6, 4, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                }

                if (this.l.c[EnumDirection.WEST.a()]) {
                    this.a(generatoraccess, structureboundingbox, 0, 3, 3, 0, 3, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                } else {
                    this.a(generatoraccess, structureboundingbox, 0, 3, 3, 1, 3, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                    this.a(generatoraccess, structureboundingbox, 0, 2, 3, 0, 2, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, false);
                    this.a(generatoraccess, structureboundingbox, 0, 1, 3, 1, 1, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                }

                if (this.l.c[EnumDirection.EAST.a()]) {
                    this.a(generatoraccess, structureboundingbox, 7, 3, 3, 7, 3, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                } else {
                    this.a(generatoraccess, structureboundingbox, 6, 3, 3, 7, 3, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                    this.a(generatoraccess, structureboundingbox, 7, 2, 3, 7, 2, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, false);
                    this.a(generatoraccess, structureboundingbox, 6, 1, 3, 7, 1, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                }
            } else if (this.p == 1) {
                this.a(generatoraccess, structureboundingbox, 2, 1, 2, 2, 3, 2, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 2, 1, 5, 2, 3, 5, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 5, 1, 5, 5, 3, 5, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 5, 1, 2, 5, 3, 2, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.e, 2, 2, 2, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.e, 2, 2, 5, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.e, 5, 2, 5, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.e, 5, 2, 2, structureboundingbox);
                this.a(generatoraccess, structureboundingbox, 0, 1, 0, 1, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 0, 1, 1, 0, 3, 1, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 0, 1, 7, 1, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 0, 1, 6, 0, 3, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 6, 1, 7, 7, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 7, 1, 6, 7, 3, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 6, 1, 0, 7, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 7, 1, 1, 7, 3, 1, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, 1, 2, 0, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, 0, 2, 1, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, 1, 2, 7, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, 0, 2, 6, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, 6, 2, 7, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, 7, 2, 6, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, 6, 2, 0, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, 7, 2, 1, structureboundingbox);
                if (!this.l.c[EnumDirection.SOUTH.a()]) {
                    this.a(generatoraccess, structureboundingbox, 1, 3, 0, 6, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                    this.a(generatoraccess, structureboundingbox, 1, 2, 0, 6, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, false);
                    this.a(generatoraccess, structureboundingbox, 1, 1, 0, 6, 1, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                }

                if (!this.l.c[EnumDirection.NORTH.a()]) {
                    this.a(generatoraccess, structureboundingbox, 1, 3, 7, 6, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                    this.a(generatoraccess, structureboundingbox, 1, 2, 7, 6, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, false);
                    this.a(generatoraccess, structureboundingbox, 1, 1, 7, 6, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                }

                if (!this.l.c[EnumDirection.WEST.a()]) {
                    this.a(generatoraccess, structureboundingbox, 0, 3, 1, 0, 3, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                    this.a(generatoraccess, structureboundingbox, 0, 2, 1, 0, 2, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, false);
                    this.a(generatoraccess, structureboundingbox, 0, 1, 1, 0, 1, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                }

                if (!this.l.c[EnumDirection.EAST.a()]) {
                    this.a(generatoraccess, structureboundingbox, 7, 3, 1, 7, 3, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                    this.a(generatoraccess, structureboundingbox, 7, 2, 1, 7, 2, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, false);
                    this.a(generatoraccess, structureboundingbox, 7, 1, 1, 7, 1, 6, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                }
            } else if (this.p == 2) {
                this.a(generatoraccess, structureboundingbox, 0, 1, 0, 0, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 7, 1, 0, 7, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 1, 1, 0, 6, 1, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 1, 1, 7, 6, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 0, 2, 0, 0, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.c, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.c, false);
                this.a(generatoraccess, structureboundingbox, 7, 2, 0, 7, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.c, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.c, false);
                this.a(generatoraccess, structureboundingbox, 1, 2, 0, 6, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.c, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.c, false);
                this.a(generatoraccess, structureboundingbox, 1, 2, 7, 6, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.c, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.c, false);
                this.a(generatoraccess, structureboundingbox, 0, 3, 0, 0, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 7, 3, 0, 7, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 1, 3, 0, 6, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 1, 3, 7, 6, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 0, 1, 3, 0, 2, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.c, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.c, false);
                this.a(generatoraccess, structureboundingbox, 7, 1, 3, 7, 2, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.c, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.c, false);
                this.a(generatoraccess, structureboundingbox, 3, 1, 0, 4, 2, 0, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.c, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.c, false);
                this.a(generatoraccess, structureboundingbox, 3, 1, 7, 4, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.c, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.c, false);
                if (this.l.c[EnumDirection.SOUTH.a()]) {
                    this.a(generatoraccess, structureboundingbox, 3, 1, 0, 4, 2, 0);
                }

                if (this.l.c[EnumDirection.NORTH.a()]) {
                    this.a(generatoraccess, structureboundingbox, 3, 1, 7, 4, 2, 7);
                }

                if (this.l.c[EnumDirection.WEST.a()]) {
                    this.a(generatoraccess, structureboundingbox, 0, 1, 3, 0, 2, 4);
                }

                if (this.l.c[EnumDirection.EAST.a()]) {
                    this.a(generatoraccess, structureboundingbox, 7, 1, 3, 7, 2, 4);
                }
            }

            if (flag) {
                this.a(generatoraccess, structureboundingbox, 3, 1, 3, 4, 1, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
                this.a(generatoraccess, structureboundingbox, 3, 2, 3, 4, 2, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.a, false);
                this.a(generatoraccess, structureboundingbox, 3, 3, 3, 4, 3, 4, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, WorldGenMonumentPieces.WorldGenMonumentPieceSimple.b, false);
            }

            return true;
        }
    }

    public static class WorldGenMonumentPieceEntry extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        public WorldGenMonumentPieceEntry() {}

        public WorldGenMonumentPieceEntry(EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker) {
            super(1, enumdirection, worldgenmonumentpieces_worldgenmonumentstatetracker, 1, 1, 1);
        }

        public boolean a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            this.a(generatoraccess, structureboundingbox, 0, 3, 0, 2, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.b, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.b, false);
            this.a(generatoraccess, structureboundingbox, 5, 3, 0, 7, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.b, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.b, false);
            this.a(generatoraccess, structureboundingbox, 0, 2, 0, 1, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.b, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.b, false);
            this.a(generatoraccess, structureboundingbox, 6, 2, 0, 7, 2, 7, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.b, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.b, false);
            this.a(generatoraccess, structureboundingbox, 0, 1, 0, 0, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.b, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.b, false);
            this.a(generatoraccess, structureboundingbox, 7, 1, 0, 7, 1, 7, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.b, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.b, false);
            this.a(generatoraccess, structureboundingbox, 0, 1, 7, 7, 3, 7, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.b, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.b, false);
            this.a(generatoraccess, structureboundingbox, 1, 1, 0, 2, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.b, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.b, false);
            this.a(generatoraccess, structureboundingbox, 5, 1, 0, 6, 3, 0, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.b, WorldGenMonumentPieces.WorldGenMonumentPieceEntry.b, false);
            if (this.l.c[EnumDirection.NORTH.a()]) {
                this.a(generatoraccess, structureboundingbox, 3, 1, 7, 4, 2, 7);
            }

            if (this.l.c[EnumDirection.WEST.a()]) {
                this.a(generatoraccess, structureboundingbox, 0, 1, 3, 1, 2, 4);
            }

            if (this.l.c[EnumDirection.EAST.a()]) {
                this.a(generatoraccess, structureboundingbox, 6, 1, 3, 7, 2, 4);
            }

            return true;
        }
    }

    public static class WorldGenMonumentPiece1 extends WorldGenMonumentPieces.WorldGenMonumentPiece {

        private WorldGenMonumentPieces.WorldGenMonumentStateTracker p;
        private WorldGenMonumentPieces.WorldGenMonumentStateTracker q;
        private final List<WorldGenMonumentPieces.WorldGenMonumentPiece> r = Lists.newArrayList();

        public WorldGenMonumentPiece1() {}

        public WorldGenMonumentPiece1(Random random, int i, int j, EnumDirection enumdirection) {
            super(0);
            this.a(enumdirection);
            EnumDirection enumdirection1 = this.f();

            if (enumdirection1.k() == EnumDirection.EnumAxis.Z) {
                this.n = new StructureBoundingBox(i, 39, j, i + 58 - 1, 61, j + 58 - 1);
            } else {
                this.n = new StructureBoundingBox(i, 39, j, i + 58 - 1, 61, j + 58 - 1);
            }

            List<WorldGenMonumentPieces.WorldGenMonumentStateTracker> list = this.a(random);

            this.p.d = true;
            this.r.add(new WorldGenMonumentPieces.WorldGenMonumentPieceEntry(enumdirection1, this.p));
            this.r.add(new WorldGenMonumentPieces.WorldGenMonumentPiece2(enumdirection1, this.q, random));
            List<WorldGenMonumentPieces.IWorldGenMonumentPieceSelector> list1 = Lists.newArrayList();

            list1.add(new WorldGenMonumentPieces.WorldGenMonumentPieceSelector6());
            list1.add(new WorldGenMonumentPieces.WorldGenMonumentPieceSelector4());
            list1.add(new WorldGenMonumentPieces.WorldGenMonumentPieceSelector3());
            list1.add(new WorldGenMonumentPieces.WorldGenMonumentPieceSelector7());
            list1.add(new WorldGenMonumentPieces.WorldGenMonumentPieceSelector5());
            list1.add(new WorldGenMonumentPieces.WorldGenMonumentPieceSelector1());
            list1.add(new WorldGenMonumentPieces.WorldGenMonumentPieceSelector2());
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker = (WorldGenMonumentPieces.WorldGenMonumentStateTracker) iterator.next();

                if (!worldgenmonumentpieces_worldgenmonumentstatetracker.d && !worldgenmonumentpieces_worldgenmonumentstatetracker.b()) {
                    Iterator iterator1 = list1.iterator();

                    while (iterator1.hasNext()) {
                        WorldGenMonumentPieces.IWorldGenMonumentPieceSelector worldgenmonumentpieces_iworldgenmonumentpieceselector = (WorldGenMonumentPieces.IWorldGenMonumentPieceSelector) iterator1.next();

                        if (worldgenmonumentpieces_iworldgenmonumentpieceselector.a(worldgenmonumentpieces_worldgenmonumentstatetracker)) {
                            this.r.add(worldgenmonumentpieces_iworldgenmonumentpieceselector.a(enumdirection1, worldgenmonumentpieces_worldgenmonumentstatetracker, random));
                            break;
                        }
                    }
                }
            }

            int k = this.n.b;
            int l = this.a(9, 22);
            int i1 = this.b(9, 22);
            Iterator iterator2 = this.r.iterator();

            while (iterator2.hasNext()) {
                WorldGenMonumentPieces.WorldGenMonumentPiece worldgenmonumentpieces_worldgenmonumentpiece = (WorldGenMonumentPieces.WorldGenMonumentPiece) iterator2.next();

                worldgenmonumentpieces_worldgenmonumentpiece.d().a(l, k, i1);
            }

            StructureBoundingBox structureboundingbox = StructureBoundingBox.a(this.a(1, 1), this.d(1), this.b(1, 1), this.a(23, 21), this.d(8), this.b(23, 21));
            StructureBoundingBox structureboundingbox1 = StructureBoundingBox.a(this.a(34, 1), this.d(1), this.b(34, 1), this.a(56, 21), this.d(8), this.b(56, 21));
            StructureBoundingBox structureboundingbox2 = StructureBoundingBox.a(this.a(22, 22), this.d(13), this.b(22, 22), this.a(35, 35), this.d(17), this.b(35, 35));
            int j1 = random.nextInt();

            this.r.add(new WorldGenMonumentPieces.WorldGenMonumentPiece8(enumdirection1, structureboundingbox, j1++));
            this.r.add(new WorldGenMonumentPieces.WorldGenMonumentPiece8(enumdirection1, structureboundingbox1, j1++));
            this.r.add(new WorldGenMonumentPieces.WorldGenMonumentPiecePenthouse(enumdirection1, structureboundingbox2));
        }

        private List<WorldGenMonumentPieces.WorldGenMonumentStateTracker> a(Random random) {
            WorldGenMonumentPieces.WorldGenMonumentStateTracker[] aworldgenmonumentpieces_worldgenmonumentstatetracker = new WorldGenMonumentPieces.WorldGenMonumentStateTracker[75];

            boolean flag;
            int i;
            int j;
            int k;

            for (j = 0; j < 5; ++j) {
                for (k = 0; k < 4; ++k) {
                    flag = false;
                    i = b(j, 0, k);
                    aworldgenmonumentpieces_worldgenmonumentstatetracker[i] = new WorldGenMonumentPieces.WorldGenMonumentStateTracker(i);
                }
            }

            for (j = 0; j < 5; ++j) {
                for (k = 0; k < 4; ++k) {
                    flag = true;
                    i = b(j, 1, k);
                    aworldgenmonumentpieces_worldgenmonumentstatetracker[i] = new WorldGenMonumentPieces.WorldGenMonumentStateTracker(i);
                }
            }

            for (j = 1; j < 4; ++j) {
                for (k = 0; k < 2; ++k) {
                    flag = true;
                    i = b(j, 2, k);
                    aworldgenmonumentpieces_worldgenmonumentstatetracker[i] = new WorldGenMonumentPieces.WorldGenMonumentStateTracker(i);
                }
            }

            this.p = aworldgenmonumentpieces_worldgenmonumentstatetracker[WorldGenMonumentPieces.WorldGenMonumentPiece1.h];

            int l;
            int i1;
            int j1;
            int k1;
            int l1;

            for (j = 0; j < 5; ++j) {
                for (k = 0; k < 5; ++k) {
                    for (int i2 = 0; i2 < 3; ++i2) {
                        i = b(j, i2, k);
                        if (aworldgenmonumentpieces_worldgenmonumentstatetracker[i] != null) {
                            EnumDirection[] aenumdirection = EnumDirection.values();

                            l = aenumdirection.length;

                            for (i1 = 0; i1 < l; ++i1) {
                                EnumDirection enumdirection = aenumdirection[i1];

                                j1 = j + enumdirection.getAdjacentX();
                                k1 = i2 + enumdirection.getAdjacentY();
                                l1 = k + enumdirection.getAdjacentZ();
                                if (j1 >= 0 && j1 < 5 && l1 >= 0 && l1 < 5 && k1 >= 0 && k1 < 3) {
                                    int j2 = b(j1, k1, l1);

                                    if (aworldgenmonumentpieces_worldgenmonumentstatetracker[j2] != null) {
                                        if (l1 == k) {
                                            aworldgenmonumentpieces_worldgenmonumentstatetracker[i].a(enumdirection, aworldgenmonumentpieces_worldgenmonumentstatetracker[j2]);
                                        } else {
                                            aworldgenmonumentpieces_worldgenmonumentstatetracker[i].a(enumdirection.opposite(), aworldgenmonumentpieces_worldgenmonumentstatetracker[j2]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker = new WorldGenMonumentPieces.WorldGenMonumentStateTracker(1003);
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker1 = new WorldGenMonumentPieces.WorldGenMonumentStateTracker(1001);
            WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker2 = new WorldGenMonumentPieces.WorldGenMonumentStateTracker(1002);

            aworldgenmonumentpieces_worldgenmonumentstatetracker[WorldGenMonumentPieces.WorldGenMonumentPiece1.i].a(EnumDirection.UP, worldgenmonumentpieces_worldgenmonumentstatetracker);
            aworldgenmonumentpieces_worldgenmonumentstatetracker[WorldGenMonumentPieces.WorldGenMonumentPiece1.j].a(EnumDirection.SOUTH, worldgenmonumentpieces_worldgenmonumentstatetracker1);
            aworldgenmonumentpieces_worldgenmonumentstatetracker[WorldGenMonumentPieces.WorldGenMonumentPiece1.k].a(EnumDirection.SOUTH, worldgenmonumentpieces_worldgenmonumentstatetracker2);
            worldgenmonumentpieces_worldgenmonumentstatetracker.d = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker1.d = true;
            worldgenmonumentpieces_worldgenmonumentstatetracker2.d = true;
            this.p.e = true;
            this.q = aworldgenmonumentpieces_worldgenmonumentstatetracker[b(random.nextInt(4), 0, 2)];
            this.q.d = true;
            this.q.b[EnumDirection.EAST.a()].d = true;
            this.q.b[EnumDirection.NORTH.a()].d = true;
            this.q.b[EnumDirection.EAST.a()].b[EnumDirection.NORTH.a()].d = true;
            this.q.b[EnumDirection.UP.a()].d = true;
            this.q.b[EnumDirection.EAST.a()].b[EnumDirection.UP.a()].d = true;
            this.q.b[EnumDirection.NORTH.a()].b[EnumDirection.UP.a()].d = true;
            this.q.b[EnumDirection.EAST.a()].b[EnumDirection.NORTH.a()].b[EnumDirection.UP.a()].d = true;
            List<WorldGenMonumentPieces.WorldGenMonumentStateTracker> list = Lists.newArrayList();
            WorldGenMonumentPieces.WorldGenMonumentStateTracker[] aworldgenmonumentpieces_worldgenmonumentstatetracker1 = aworldgenmonumentpieces_worldgenmonumentstatetracker;

            l = aworldgenmonumentpieces_worldgenmonumentstatetracker.length;

            for (i1 = 0; i1 < l; ++i1) {
                WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker3 = aworldgenmonumentpieces_worldgenmonumentstatetracker1[i1];

                if (worldgenmonumentpieces_worldgenmonumentstatetracker3 != null) {
                    worldgenmonumentpieces_worldgenmonumentstatetracker3.a();
                    list.add(worldgenmonumentpieces_worldgenmonumentstatetracker3);
                }
            }

            worldgenmonumentpieces_worldgenmonumentstatetracker.a();
            Collections.shuffle(list, random);
            int k2 = 1;
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker4 = (WorldGenMonumentPieces.WorldGenMonumentStateTracker) iterator.next();
                int l2 = 0;

                j1 = 0;

                while (l2 < 2 && j1 < 5) {
                    ++j1;
                    k1 = random.nextInt(6);
                    if (worldgenmonumentpieces_worldgenmonumentstatetracker4.c[k1]) {
                        l1 = EnumDirection.fromType1(k1).opposite().a();
                        worldgenmonumentpieces_worldgenmonumentstatetracker4.c[k1] = false;
                        worldgenmonumentpieces_worldgenmonumentstatetracker4.b[k1].c[l1] = false;
                        if (worldgenmonumentpieces_worldgenmonumentstatetracker4.a(k2++) && worldgenmonumentpieces_worldgenmonumentstatetracker4.b[k1].a(k2++)) {
                            ++l2;
                        } else {
                            worldgenmonumentpieces_worldgenmonumentstatetracker4.c[k1] = true;
                            worldgenmonumentpieces_worldgenmonumentstatetracker4.b[k1].c[l1] = true;
                        }
                    }
                }
            }

            list.add(worldgenmonumentpieces_worldgenmonumentstatetracker);
            list.add(worldgenmonumentpieces_worldgenmonumentstatetracker1);
            list.add(worldgenmonumentpieces_worldgenmonumentstatetracker2);
            return list;
        }

        public boolean a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            int i = Math.max(generatoraccess.getSeaLevel(), 64) - this.n.b;

            this.a(generatoraccess, structureboundingbox, 0, 0, 0, 58, i, 58);
            this.a(false, 0, generatoraccess, random, structureboundingbox);
            this.a(true, 33, generatoraccess, random, structureboundingbox);
            this.a(generatoraccess, random, structureboundingbox);
            this.b(generatoraccess, random, structureboundingbox);
            this.c(generatoraccess, random, structureboundingbox);
            this.d(generatoraccess, random, structureboundingbox);
            this.e(generatoraccess, random, structureboundingbox);
            this.f(generatoraccess, random, structureboundingbox);

            int j;

            for (j = 0; j < 7; ++j) {
                int k = 0;

                while (k < 7) {
                    if (k == 0 && j == 3) {
                        k = 6;
                    }

                    int l = j * 9;
                    int i1 = k * 9;

                    for (int j1 = 0; j1 < 4; ++j1) {
                        for (int k1 = 0; k1 < 4; ++k1) {
                            this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, l + j1, 0, i1 + k1, structureboundingbox);
                            this.b(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, l + j1, -1, i1 + k1, structureboundingbox);
                        }
                    }

                    if (j != 0 && j != 6) {
                        k += 6;
                    } else {
                        ++k;
                    }
                }
            }

            for (j = 0; j < 5; ++j) {
                this.a(generatoraccess, structureboundingbox, -1 - j, 0 + j * 2, -1 - j, -1 - j, 23, 58 + j);
                this.a(generatoraccess, structureboundingbox, 58 + j, 0 + j * 2, -1 - j, 58 + j, 23, 58 + j);
                this.a(generatoraccess, structureboundingbox, 0 - j, 0 + j * 2, -1 - j, 57 + j, 23, -1 - j);
                this.a(generatoraccess, structureboundingbox, 0 - j, 0 + j * 2, 58 + j, 57 + j, 23, 58 + j);
            }

            Iterator iterator = this.r.iterator();

            while (iterator.hasNext()) {
                WorldGenMonumentPieces.WorldGenMonumentPiece worldgenmonumentpieces_worldgenmonumentpiece = (WorldGenMonumentPieces.WorldGenMonumentPiece) iterator.next();

                if (worldgenmonumentpieces_worldgenmonumentpiece.d().a(structureboundingbox)) {
                    worldgenmonumentpieces_worldgenmonumentpiece.a(generatoraccess, random, structureboundingbox, chunkcoordintpair);
                }
            }

            return true;
        }

        private void a(boolean flag, int i, GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox) {
            boolean flag1 = true;

            if (this.a(structureboundingbox, i, 0, i + 23, 20)) {
                this.a(generatoraccess, structureboundingbox, i + 0, 0, 0, i + 24, 0, 20, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, i + 0, 1, 0, i + 24, 10, 20);

                int j;

                for (j = 0; j < 4; ++j) {
                    this.a(generatoraccess, structureboundingbox, i + j, j + 1, j, i + j, j + 1, 20, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                    this.a(generatoraccess, structureboundingbox, i + j + 7, j + 5, j + 7, i + j + 7, j + 5, 20, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                    this.a(generatoraccess, structureboundingbox, i + 17 - j, j + 5, j + 7, i + 17 - j, j + 5, 20, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                    this.a(generatoraccess, structureboundingbox, i + 24 - j, j + 1, j, i + 24 - j, j + 1, 20, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                    this.a(generatoraccess, structureboundingbox, i + j + 1, j + 1, j, i + 23 - j, j + 1, j, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                    this.a(generatoraccess, structureboundingbox, i + j + 8, j + 5, j + 7, i + 16 - j, j + 5, j + 7, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                }

                this.a(generatoraccess, structureboundingbox, i + 4, 4, 4, i + 6, 4, 20, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, i + 7, 4, 4, i + 17, 4, 6, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, i + 18, 4, 4, i + 20, 4, 20, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, i + 11, 8, 11, i + 13, 8, 20, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, i + 12, 9, 12, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, i + 12, 9, 15, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, i + 12, 9, 18, structureboundingbox);
                j = i + (flag ? 19 : 5);
                int k = i + (flag ? 5 : 19);

                int l;

                for (l = 20; l >= 5; l -= 3) {
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, j, 5, l, structureboundingbox);
                }

                for (l = 19; l >= 7; l -= 3) {
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, k, 5, l, structureboundingbox);
                }

                for (l = 0; l < 4; ++l) {
                    int i1 = flag ? i + 24 - (17 - l * 3) : i + 17 - l * 3;

                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, i1, 5, 5, structureboundingbox);
                }

                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, k, 5, 5, structureboundingbox);
                this.a(generatoraccess, structureboundingbox, i + 11, 1, 12, i + 13, 7, 12, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, i + 12, 1, 11, i + 12, 7, 13, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
            }

        }

        private void a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox) {
            if (this.a(structureboundingbox, 22, 5, 35, 17)) {
                this.a(generatoraccess, structureboundingbox, 25, 0, 0, 32, 8, 20);

                for (int i = 0; i < 4; ++i) {
                    this.a(generatoraccess, structureboundingbox, 24, 2, 5 + i * 4, 24, 4, 5 + i * 4, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                    this.a(generatoraccess, structureboundingbox, 22, 4, 5 + i * 4, 23, 4, 5 + i * 4, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, 25, 5, 5 + i * 4, structureboundingbox);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, 26, 6, 5 + i * 4, structureboundingbox);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.e, 26, 5, 5 + i * 4, structureboundingbox);
                    this.a(generatoraccess, structureboundingbox, 33, 2, 5 + i * 4, 33, 4, 5 + i * 4, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                    this.a(generatoraccess, structureboundingbox, 34, 4, 5 + i * 4, 35, 4, 5 + i * 4, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, 32, 5, 5 + i * 4, structureboundingbox);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, 31, 6, 5 + i * 4, structureboundingbox);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.e, 31, 5, 5 + i * 4, structureboundingbox);
                    this.a(generatoraccess, structureboundingbox, 27, 6, 5 + i * 4, 30, 6, 5 + i * 4, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                }
            }

        }

        private void b(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox) {
            if (this.a(structureboundingbox, 15, 20, 42, 21)) {
                this.a(generatoraccess, structureboundingbox, 15, 0, 21, 42, 0, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 26, 1, 21, 31, 3, 21);
                this.a(generatoraccess, structureboundingbox, 21, 12, 21, 36, 12, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 17, 11, 21, 40, 11, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 16, 10, 21, 41, 10, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 15, 7, 21, 42, 9, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 16, 6, 21, 41, 6, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 17, 5, 21, 40, 5, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 21, 4, 21, 36, 4, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 22, 3, 21, 26, 3, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 31, 3, 21, 35, 3, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 23, 2, 21, 25, 2, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 32, 2, 21, 34, 2, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 28, 4, 20, 29, 4, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, 27, 3, 21, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, 30, 3, 21, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, 26, 2, 21, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, 31, 2, 21, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, 25, 1, 21, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, 32, 1, 21, structureboundingbox);

                int i;

                for (i = 0; i < 7; ++i) {
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.c, 28 - i, 6 + i, 21, structureboundingbox);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.c, 29 + i, 6 + i, 21, structureboundingbox);
                }

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.c, 28 - i, 9 + i, 21, structureboundingbox);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.c, 29 + i, 9 + i, 21, structureboundingbox);
                }

                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.c, 28, 12, 21, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.c, 29, 12, 21, structureboundingbox);

                for (i = 0; i < 3; ++i) {
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.c, 22 - i * 2, 8, 21, structureboundingbox);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.c, 22 - i * 2, 9, 21, structureboundingbox);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.c, 35 + i * 2, 8, 21, structureboundingbox);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.c, 35 + i * 2, 9, 21, structureboundingbox);
                }

                this.a(generatoraccess, structureboundingbox, 15, 13, 21, 42, 15, 21);
                this.a(generatoraccess, structureboundingbox, 15, 1, 21, 15, 6, 21);
                this.a(generatoraccess, structureboundingbox, 16, 1, 21, 16, 5, 21);
                this.a(generatoraccess, structureboundingbox, 17, 1, 21, 20, 4, 21);
                this.a(generatoraccess, structureboundingbox, 21, 1, 21, 21, 3, 21);
                this.a(generatoraccess, structureboundingbox, 22, 1, 21, 22, 2, 21);
                this.a(generatoraccess, structureboundingbox, 23, 1, 21, 24, 1, 21);
                this.a(generatoraccess, structureboundingbox, 42, 1, 21, 42, 6, 21);
                this.a(generatoraccess, structureboundingbox, 41, 1, 21, 41, 5, 21);
                this.a(generatoraccess, structureboundingbox, 37, 1, 21, 40, 4, 21);
                this.a(generatoraccess, structureboundingbox, 36, 1, 21, 36, 3, 21);
                this.a(generatoraccess, structureboundingbox, 33, 1, 21, 34, 1, 21);
                this.a(generatoraccess, structureboundingbox, 35, 1, 21, 35, 2, 21);
            }

        }

        private void c(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox) {
            if (this.a(structureboundingbox, 21, 21, 36, 36)) {
                this.a(generatoraccess, structureboundingbox, 21, 0, 22, 36, 0, 36, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 21, 1, 22, 36, 23, 36);

                for (int i = 0; i < 4; ++i) {
                    this.a(generatoraccess, structureboundingbox, 21 + i, 13 + i, 21 + i, 36 - i, 13 + i, 21 + i, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                    this.a(generatoraccess, structureboundingbox, 21 + i, 13 + i, 36 - i, 36 - i, 13 + i, 36 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                    this.a(generatoraccess, structureboundingbox, 21 + i, 13 + i, 22 + i, 21 + i, 13 + i, 35 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                    this.a(generatoraccess, structureboundingbox, 36 - i, 13 + i, 22 + i, 36 - i, 13 + i, 35 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                }

                this.a(generatoraccess, structureboundingbox, 25, 16, 25, 32, 16, 32, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 25, 17, 25, 25, 19, 25, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                this.a(generatoraccess, structureboundingbox, 32, 17, 25, 32, 19, 25, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                this.a(generatoraccess, structureboundingbox, 25, 17, 32, 25, 19, 32, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                this.a(generatoraccess, structureboundingbox, 32, 17, 32, 32, 19, 32, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, 26, 20, 26, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, 27, 21, 27, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.e, 27, 20, 27, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, 26, 20, 31, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, 27, 21, 30, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.e, 27, 20, 30, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, 31, 20, 31, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, 30, 21, 30, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.e, 30, 20, 30, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, 31, 20, 26, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, 30, 21, 27, structureboundingbox);
                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.e, 30, 20, 27, structureboundingbox);
                this.a(generatoraccess, structureboundingbox, 28, 21, 27, 29, 21, 27, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 27, 21, 28, 27, 21, 29, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 28, 21, 30, 29, 21, 30, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 30, 21, 28, 30, 21, 29, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
            }

        }

        private void d(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox) {
            int i;

            if (this.a(structureboundingbox, 0, 21, 6, 58)) {
                this.a(generatoraccess, structureboundingbox, 0, 0, 21, 6, 0, 57, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 0, 1, 21, 6, 7, 57);
                this.a(generatoraccess, structureboundingbox, 4, 4, 21, 6, 4, 53, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccess, structureboundingbox, i, i + 1, 21, i, i + 1, 57 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                }

                for (i = 23; i < 53; i += 3) {
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, 5, 5, i, structureboundingbox);
                }

                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, 5, 5, 52, structureboundingbox);

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccess, structureboundingbox, i, i + 1, 21, i, i + 1, 57 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                }

                this.a(generatoraccess, structureboundingbox, 4, 1, 52, 6, 3, 52, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 5, 1, 51, 5, 3, 53, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
            }

            if (this.a(structureboundingbox, 51, 21, 58, 58)) {
                this.a(generatoraccess, structureboundingbox, 51, 0, 21, 57, 0, 57, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 51, 1, 21, 57, 7, 57);
                this.a(generatoraccess, structureboundingbox, 51, 4, 21, 53, 4, 53, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccess, structureboundingbox, 57 - i, i + 1, 21, 57 - i, i + 1, 57 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                }

                for (i = 23; i < 53; i += 3) {
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, 52, 5, i, structureboundingbox);
                }

                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, 52, 5, 52, structureboundingbox);
                this.a(generatoraccess, structureboundingbox, 51, 1, 52, 53, 3, 52, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 52, 1, 51, 52, 3, 53, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
            }

            if (this.a(structureboundingbox, 0, 51, 57, 57)) {
                this.a(generatoraccess, structureboundingbox, 7, 0, 51, 50, 0, 57, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 7, 1, 51, 50, 10, 57);

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccess, structureboundingbox, i + 1, i + 1, 57 - i, 56 - i, i + 1, 57 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                }
            }

        }

        private void e(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox) {
            int i;

            if (this.a(structureboundingbox, 7, 21, 13, 50)) {
                this.a(generatoraccess, structureboundingbox, 7, 0, 21, 13, 0, 50, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 7, 1, 21, 13, 10, 50);
                this.a(generatoraccess, structureboundingbox, 11, 8, 21, 13, 8, 53, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccess, structureboundingbox, i + 7, i + 5, 21, i + 7, i + 5, 54, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                }

                for (i = 21; i <= 45; i += 3) {
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, 12, 9, i, structureboundingbox);
                }
            }

            if (this.a(structureboundingbox, 44, 21, 50, 54)) {
                this.a(generatoraccess, structureboundingbox, 44, 0, 21, 50, 0, 50, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 44, 1, 21, 50, 10, 50);
                this.a(generatoraccess, structureboundingbox, 44, 8, 21, 46, 8, 53, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccess, structureboundingbox, 50 - i, i + 5, 21, 50 - i, i + 5, 54, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                }

                for (i = 21; i <= 45; i += 3) {
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, 45, 9, i, structureboundingbox);
                }
            }

            if (this.a(structureboundingbox, 8, 44, 49, 54)) {
                this.a(generatoraccess, structureboundingbox, 14, 0, 44, 43, 0, 50, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 14, 1, 44, 43, 10, 50);

                for (i = 12; i <= 45; i += 3) {
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, i, 9, 45, structureboundingbox);
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, i, 9, 52, structureboundingbox);
                    if (i == 12 || i == 18 || i == 24 || i == 33 || i == 39 || i == 45) {
                        this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, i, 9, 47, structureboundingbox);
                        this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, i, 9, 50, structureboundingbox);
                        this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, i, 10, 45, structureboundingbox);
                        this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, i, 10, 46, structureboundingbox);
                        this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, i, 10, 51, structureboundingbox);
                        this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, i, 10, 52, structureboundingbox);
                        this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, i, 11, 47, structureboundingbox);
                        this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, i, 11, 50, structureboundingbox);
                        this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, i, 12, 48, structureboundingbox);
                        this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, i, 12, 49, structureboundingbox);
                    }
                }

                for (i = 0; i < 3; ++i) {
                    this.a(generatoraccess, structureboundingbox, 8 + i, 5 + i, 54, 49 - i, 5 + i, 54, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                }

                this.a(generatoraccess, structureboundingbox, 11, 8, 54, 46, 8, 54, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                this.a(generatoraccess, structureboundingbox, 14, 8, 44, 43, 8, 53, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
            }

        }

        private void f(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox) {
            int i;

            if (this.a(structureboundingbox, 14, 21, 20, 43)) {
                this.a(generatoraccess, structureboundingbox, 14, 0, 21, 20, 0, 43, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 14, 1, 22, 20, 14, 43);
                this.a(generatoraccess, structureboundingbox, 18, 12, 22, 20, 12, 39, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 18, 12, 21, 20, 12, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccess, structureboundingbox, i + 14, i + 9, 21, i + 14, i + 9, 43 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                }

                for (i = 23; i <= 39; i += 3) {
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, 19, 13, i, structureboundingbox);
                }
            }

            if (this.a(structureboundingbox, 37, 21, 43, 43)) {
                this.a(generatoraccess, structureboundingbox, 37, 0, 21, 43, 0, 43, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 37, 1, 22, 43, 14, 43);
                this.a(generatoraccess, structureboundingbox, 37, 12, 22, 39, 12, 39, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 37, 12, 21, 39, 12, 21, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccess, structureboundingbox, 43 - i, i + 9, 21, 43 - i, i + 9, 43 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                }

                for (i = 23; i <= 39; i += 3) {
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, 38, 13, i, structureboundingbox);
                }
            }

            if (this.a(structureboundingbox, 15, 37, 42, 43)) {
                this.a(generatoraccess, structureboundingbox, 21, 0, 37, 36, 0, 43, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);
                this.a(generatoraccess, structureboundingbox, 21, 1, 37, 36, 14, 43);
                this.a(generatoraccess, structureboundingbox, 21, 12, 37, 36, 12, 39, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, WorldGenMonumentPieces.WorldGenMonumentPiece1.a, false);

                for (i = 0; i < 4; ++i) {
                    this.a(generatoraccess, structureboundingbox, 15 + i, i + 9, 43 - i, 42 - i, i + 9, 43 - i, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, WorldGenMonumentPieces.WorldGenMonumentPiece1.b, false);
                }

                for (i = 21; i <= 36; i += 3) {
                    this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece1.d, i, 13, 38, structureboundingbox);
                }
            }

        }
    }

    public abstract static class WorldGenMonumentPiece extends StructurePiece {

        protected static final IBlockData a = Blocks.PRISMARINE.getBlockData();
        protected static final IBlockData b = Blocks.PRISMARINE_BRICKS.getBlockData();
        protected static final IBlockData c = Blocks.DARK_PRISMARINE.getBlockData();
        protected static final IBlockData d = WorldGenMonumentPieces.WorldGenMonumentPiece.b;
        protected static final IBlockData e = Blocks.SEA_LANTERN.getBlockData();
        protected static final IBlockData f = Blocks.WATER.getBlockData();
        protected static final Set<Block> g = ImmutableSet.builder().add(Blocks.ICE).add(Blocks.PACKED_ICE).add(Blocks.BLUE_ICE).add(WorldGenMonumentPieces.WorldGenMonumentPiece.f.getBlock()).build();
        protected static final int h = b(2, 0, 0);
        protected static final int i = b(2, 2, 0);
        protected static final int j = b(0, 1, 0);
        protected static final int k = b(4, 1, 0);
        protected WorldGenMonumentPieces.WorldGenMonumentStateTracker l;

        protected static final int b(int i, int j, int k) {
            return j * 25 + k * 5 + i;
        }

        public WorldGenMonumentPiece() {
            super(0);
        }

        public WorldGenMonumentPiece(int i) {
            super(i);
        }

        public WorldGenMonumentPiece(EnumDirection enumdirection, StructureBoundingBox structureboundingbox) {
            super(1);
            this.a(enumdirection);
            this.n = structureboundingbox;
        }

        protected WorldGenMonumentPiece(int i, EnumDirection enumdirection, WorldGenMonumentPieces.WorldGenMonumentStateTracker worldgenmonumentpieces_worldgenmonumentstatetracker, int j, int k, int l) {
            super(i);
            this.a(enumdirection);
            this.l = worldgenmonumentpieces_worldgenmonumentstatetracker;
            int i1 = worldgenmonumentpieces_worldgenmonumentstatetracker.a;
            int j1 = i1 % 5;
            int k1 = i1 / 5 % 5;
            int l1 = i1 / 25;

            if (enumdirection != EnumDirection.NORTH && enumdirection != EnumDirection.SOUTH) {
                this.n = new StructureBoundingBox(0, 0, 0, l * 8 - 1, k * 4 - 1, j * 8 - 1);
            } else {
                this.n = new StructureBoundingBox(0, 0, 0, j * 8 - 1, k * 4 - 1, l * 8 - 1);
            }

            switch (enumdirection) {
            case NORTH:
                this.n.a(j1 * 8, l1 * 4, -(k1 + l) * 8 + 1);
                break;
            case SOUTH:
                this.n.a(j1 * 8, l1 * 4, k1 * 8);
                break;
            case WEST:
                this.n.a(-(k1 + l) * 8 + 1, l1 * 4, j1 * 8);
                break;
            default:
                this.n.a(k1 * 8, l1 * 4, j1 * 8);
            }

        }

        protected void a(NBTTagCompound nbttagcompound) {}

        protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {}

        protected void a(GeneratorAccess generatoraccess, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1) {
            for (int k1 = j; k1 <= i1; ++k1) {
                for (int l1 = i; l1 <= l; ++l1) {
                    for (int i2 = k; i2 <= j1; ++i2) {
                        IBlockData iblockdata = this.a((IBlockAccess) generatoraccess, l1, k1, i2, structureboundingbox);

                        if (!WorldGenMonumentPieces.WorldGenMonumentPiece.g.contains(iblockdata.getBlock())) {
                            if (this.d(k1) >= generatoraccess.getSeaLevel() && iblockdata != WorldGenMonumentPieces.WorldGenMonumentPiece.f) {
                                this.a(generatoraccess, Blocks.AIR.getBlockData(), l1, k1, i2, structureboundingbox);
                            } else {
                                this.a(generatoraccess, WorldGenMonumentPieces.WorldGenMonumentPiece.f, l1, k1, i2, structureboundingbox);
                            }
                        }
                    }
                }
            }

        }

        protected void a(GeneratorAccess generatoraccess, StructureBoundingBox structureboundingbox, int i, int j, boolean flag) {
            if (flag) {
                this.a(generatoraccess, structureboundingbox, i + 0, 0, j + 0, i + 2, 0, j + 8 - 1, WorldGenMonumentPieces.WorldGenMonumentPiece.a, WorldGenMonumentPieces.WorldGenMonumentPiece.a, false);
                this.a(generatoraccess, structureboundingbox, i + 5, 0, j + 0, i + 8 - 1, 0, j + 8 - 1, WorldGenMonumentPieces.WorldGenMonumentPiece.a, WorldGenMonumentPieces.WorldGenMonumentPiece.a, false);
                this.a(generatoraccess, structureboundingbox, i + 3, 0, j + 0, i + 4, 0, j + 2, WorldGenMonumentPieces.WorldGenMonumentPiece.a, WorldGenMonumentPieces.WorldGenMonumentPiece.a, false);
                this.a(generatoraccess, structureboundingbox, i + 3, 0, j + 5, i + 4, 0, j + 8 - 1, WorldGenMonumentPieces.WorldGenMonumentPiece.a, WorldGenMonumentPieces.WorldGenMonumentPiece.a, false);
                this.a(generatoraccess, structureboundingbox, i + 3, 0, j + 2, i + 4, 0, j + 2, WorldGenMonumentPieces.WorldGenMonumentPiece.b, WorldGenMonumentPieces.WorldGenMonumentPiece.b, false);
                this.a(generatoraccess, structureboundingbox, i + 3, 0, j + 5, i + 4, 0, j + 5, WorldGenMonumentPieces.WorldGenMonumentPiece.b, WorldGenMonumentPieces.WorldGenMonumentPiece.b, false);
                this.a(generatoraccess, structureboundingbox, i + 2, 0, j + 3, i + 2, 0, j + 4, WorldGenMonumentPieces.WorldGenMonumentPiece.b, WorldGenMonumentPieces.WorldGenMonumentPiece.b, false);
                this.a(generatoraccess, structureboundingbox, i + 5, 0, j + 3, i + 5, 0, j + 4, WorldGenMonumentPieces.WorldGenMonumentPiece.b, WorldGenMonumentPieces.WorldGenMonumentPiece.b, false);
            } else {
                this.a(generatoraccess, structureboundingbox, i + 0, 0, j + 0, i + 8 - 1, 0, j + 8 - 1, WorldGenMonumentPieces.WorldGenMonumentPiece.a, WorldGenMonumentPieces.WorldGenMonumentPiece.a, false);
            }

        }

        protected void a(GeneratorAccess generatoraccess, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1, IBlockData iblockdata) {
            for (int k1 = j; k1 <= i1; ++k1) {
                for (int l1 = i; l1 <= l; ++l1) {
                    for (int i2 = k; i2 <= j1; ++i2) {
                        if (this.a((IBlockAccess) generatoraccess, l1, k1, i2, structureboundingbox) == WorldGenMonumentPieces.WorldGenMonumentPiece.f) {
                            this.a(generatoraccess, iblockdata, l1, k1, i2, structureboundingbox);
                        }
                    }
                }
            }

        }

        protected boolean a(StructureBoundingBox structureboundingbox, int i, int j, int k, int l) {
            int i1 = this.a(i, j);
            int j1 = this.b(i, j);
            int k1 = this.a(k, l);
            int l1 = this.b(k, l);

            return structureboundingbox.a(Math.min(i1, k1), Math.min(j1, l1), Math.max(i1, k1), Math.max(j1, l1));
        }

        protected boolean a(GeneratorAccess generatoraccess, StructureBoundingBox structureboundingbox, int i, int j, int k) {
            int l = this.a(i, k);
            int i1 = this.d(j);
            int j1 = this.b(i, k);

            if (structureboundingbox.b((BaseBlockPosition) (new BlockPosition(l, i1, j1)))) {
                EntityGuardianElder entityguardianelder = new EntityGuardianElder(generatoraccess.getMinecraftWorld());

                entityguardianelder.heal(entityguardianelder.getMaxHealth());
                entityguardianelder.setPositionRotation((double) l + 0.5D, (double) i1, (double) j1 + 0.5D, 0.0F, 0.0F);
                entityguardianelder.prepare(generatoraccess.getDamageScaler(new BlockPosition(entityguardianelder)), (GroupDataEntity) null, (NBTTagCompound) null);
                generatoraccess.addEntity(entityguardianelder);
                return true;
            } else {
                return false;
            }
        }
    }
}
