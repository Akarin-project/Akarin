package net.minecraft.server;

public class ChunkCoordIntPair {

    public final int x;
    public final int z;

    public ChunkCoordIntPair(int i, int j) {
        this.x = i;
        this.z = j;
    }

    public ChunkCoordIntPair(BlockPosition blockposition) {
        this.x = blockposition.getX() >> 4;
        this.z = blockposition.getZ() >> 4;
    }

    public ChunkCoordIntPair(long i) {
        this.x = (int) i;
        this.z = (int) (i >> 32);
    }

    public long a() {
        return a(this.x, this.z);
    }

    public static long a(int i, int j) {
        return (long) i & 4294967295L | ((long) j & 4294967295L) << 32;
    }

    public static int a(long i) {
        return (int) (i & 4294967295L);
    }

    public static int b(long i) {
        return (int) (i >>> 32 & 4294967295L);
    }

    public int hashCode() {
        int i = 1664525 * this.x + 1013904223;
        int j = 1664525 * (this.z ^ -559038737) + 1013904223;

        return i ^ j;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChunkCoordIntPair)) {
            return false;
        } else {
            ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) object;

            return this.x == chunkcoordintpair.x && this.z == chunkcoordintpair.z;
        }
    }

    public double a(Entity entity) {
        double d0 = (double) (this.x * 16 + 8);
        double d1 = (double) (this.z * 16 + 8);
        double d2 = d0 - entity.locX;
        double d3 = d1 - entity.locZ;

        return d2 * d2 + d3 * d3;
    }

    public int d() {
        return this.x << 4;
    }

    public int e() {
        return this.z << 4;
    }

    public int f() {
        return (this.x << 4) + 15;
    }

    public int g() {
        return (this.z << 4) + 15;
    }

    public BlockPosition a(int i, int j, int k) {
        return new BlockPosition((this.x << 4) + i, j, (this.z << 4) + k);
    }

    public String toString() {
        return "[" + this.x + ", " + this.z + "]";
    }

    public BlockPosition h() {
        return new BlockPosition(this.x << 4, 0, this.z << 4);
    }
}
