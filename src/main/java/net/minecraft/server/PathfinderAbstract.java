package net.minecraft.server;

public abstract class PathfinderAbstract {

    protected IBlockAccess a;
    protected EntityInsentient b;
    protected final IntHashMap<PathPoint> c = new IntHashMap<>();
    protected int d;
    protected int e;
    protected int f;
    protected boolean g;
    protected boolean h;
    protected boolean i;

    public PathfinderAbstract() {}

    public void a(IBlockAccess iblockaccess, EntityInsentient entityinsentient) {
        this.a = iblockaccess;
        this.b = entityinsentient;
        this.c.c();
        this.d = MathHelper.d(entityinsentient.width + 1.0F);
        this.e = MathHelper.d(entityinsentient.length + 1.0F);
        this.f = MathHelper.d(entityinsentient.width + 1.0F);
    }

    public void a() {
        this.a = null;
        this.b = null;
    }

    protected PathPoint a(int i, int j, int k) {
        int l = PathPoint.b(i, j, k);
        PathPoint pathpoint = (PathPoint) this.c.get(l);

        if (pathpoint == null) {
            pathpoint = new PathPoint(i, j, k);
            this.c.a(l, pathpoint);
        }

        return pathpoint;
    }

    public abstract PathPoint b();

    public abstract PathPoint a(double d0, double d1, double d2);

    public abstract int a(PathPoint[] apathpoint, PathPoint pathpoint, PathPoint pathpoint1, float f);

    public abstract PathType a(IBlockAccess iblockaccess, int i, int j, int k, EntityInsentient entityinsentient, int l, int i1, int j1, boolean flag, boolean flag1);

    public abstract PathType a(IBlockAccess iblockaccess, int i, int j, int k);

    public void a(boolean flag) {
        this.g = flag;
    }

    public void b(boolean flag) {
        this.h = flag;
    }

    public void c(boolean flag) {
        this.i = flag;
    }

    public boolean c() {
        return this.g;
    }

    public boolean d() {
        return this.h;
    }

    public boolean e() {
        return this.i;
    }
}
