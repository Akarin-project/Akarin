package net.minecraft.server;

import javax.annotation.Nullable;

public class PathEntity {

    private final PathPoint[] a;
    private PathPoint[] b = new PathPoint[0];
    private PathPoint[] c = new PathPoint[0];
    private PathPoint d;
    private int e;
    private int f;

    public PathEntity(PathPoint[] apathpoint) {
        this.a = apathpoint;
        this.f = apathpoint.length;
    }

    public void a() {
        ++this.e;
    }

    public boolean b() {
        return this.e >= this.f;
    }

    @Nullable
    public PathPoint c() {
        return this.f > 0 ? this.a[this.f - 1] : null;
    }

    public PathPoint a(int i) {
        return this.a[i];
    }

    public void a(int i, PathPoint pathpoint) {
        this.a[i] = pathpoint;
    }

    public int d() {
        return this.f;
    }

    public void b(int i) {
        this.f = i;
    }

    public int e() {
        return this.e;
    }

    public void c(int i) {
        this.e = i;
    }

    public Vec3D a(Entity entity, int i) {
        double d0 = (double) this.a[i].a + (double) ((int) (entity.width + 1.0F)) * 0.5D;
        double d1 = (double) this.a[i].b;
        double d2 = (double) this.a[i].c + (double) ((int) (entity.width + 1.0F)) * 0.5D;

        return new Vec3D(d0, d1, d2);
    }

    public Vec3D a(Entity entity) {
        return this.a(entity, this.e);
    }

    public Vec3D f() {
        PathPoint pathpoint = this.a[this.e];

        return new Vec3D((double) pathpoint.a, (double) pathpoint.b, (double) pathpoint.c);
    }

    public boolean a(PathEntity pathentity) {
        if (pathentity == null) {
            return false;
        } else if (pathentity.a.length != this.a.length) {
            return false;
        } else {
            for (int i = 0; i < this.a.length; ++i) {
                if (this.a[i].a != pathentity.a[i].a || this.a[i].b != pathentity.a[i].b || this.a[i].c != pathentity.a[i].c) {
                    return false;
                }
            }

            return true;
        }
    }

    @Nullable
    public PathPoint i() {
        return this.d;
    }
}
