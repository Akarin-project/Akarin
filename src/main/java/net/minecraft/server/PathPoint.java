package net.minecraft.server;

public class PathPoint {

    public final int a;
    public final int b;
    public final int c;
    private final int n;
    public int d = -1;
    public float e;
    public float f;
    public float g;
    public PathPoint h;
    public boolean i;
    public float j;
    public float k;
    public float l;
    public PathType m;

    public PathPoint(int i, int j, int k) {
        this.m = PathType.BLOCKED;
        this.a = i;
        this.b = j;
        this.c = k;
        this.n = b(i, j, k);
    }

    public PathPoint a(int i, int j, int k) {
        PathPoint pathpoint = new PathPoint(i, j, k);

        pathpoint.d = this.d;
        pathpoint.e = this.e;
        pathpoint.f = this.f;
        pathpoint.g = this.g;
        pathpoint.h = this.h;
        pathpoint.i = this.i;
        pathpoint.j = this.j;
        pathpoint.k = this.k;
        pathpoint.l = this.l;
        pathpoint.m = this.m;
        return pathpoint;
    }

    public static int b(int i, int j, int k) {
        return j & 255 | (i & 32767) << 8 | (k & 32767) << 24 | (i < 0 ? Integer.MIN_VALUE : 0) | (k < 0 ? '\u8000' : 0);
    }

    public float a(PathPoint pathpoint) {
        float f = (float) (pathpoint.a - this.a);
        float f1 = (float) (pathpoint.b - this.b);
        float f2 = (float) (pathpoint.c - this.c);

        return MathHelper.c(f * f + f1 * f1 + f2 * f2);
    }

    public float b(PathPoint pathpoint) {
        float f = (float) (pathpoint.a - this.a);
        float f1 = (float) (pathpoint.b - this.b);
        float f2 = (float) (pathpoint.c - this.c);

        return f * f + f1 * f1 + f2 * f2;
    }

    public float c(PathPoint pathpoint) {
        float f = (float) Math.abs(pathpoint.a - this.a);
        float f1 = (float) Math.abs(pathpoint.b - this.b);
        float f2 = (float) Math.abs(pathpoint.c - this.c);

        return f + f1 + f2;
    }

    public boolean equals(Object object) {
        if (!(object instanceof PathPoint)) {
            return false;
        } else {
            PathPoint pathpoint = (PathPoint) object;

            return this.n == pathpoint.n && this.a == pathpoint.a && this.b == pathpoint.b && this.c == pathpoint.c;
        }
    }

    public int hashCode() {
        return this.n;
    }

    public boolean a() {
        return this.d >= 0;
    }

    public String toString() {
        return this.a + ", " + this.b + ", " + this.c;
    }
}
