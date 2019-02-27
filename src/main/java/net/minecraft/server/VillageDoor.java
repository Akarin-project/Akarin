package net.minecraft.server;

public class VillageDoor {

    private final BlockPosition a;
    private final BlockPosition b;
    private final EnumDirection c;
    private int d;
    private boolean e;
    private int f;

    public VillageDoor(BlockPosition blockposition, int i, int j, int k) {
        this(blockposition, a(i, j), k);
    }

    private static EnumDirection a(int i, int j) {
        return i < 0 ? EnumDirection.WEST : (i > 0 ? EnumDirection.EAST : (j < 0 ? EnumDirection.NORTH : EnumDirection.SOUTH));
    }

    public VillageDoor(BlockPosition blockposition, EnumDirection enumdirection, int i) {
        this.a = blockposition.h();
        this.c = enumdirection;
        this.b = blockposition.shift(enumdirection, 2);
        this.d = i;
    }

    public int b(int i, int j, int k) {
        return (int) this.a.distanceSquared((double) i, (double) j, (double) k);
    }

    public int a(BlockPosition blockposition) {
        return (int) blockposition.n(this.d());
    }

    public int b(BlockPosition blockposition) {
        return (int) this.b.n(blockposition);
    }

    public boolean c(BlockPosition blockposition) {
        int i = blockposition.getX() - this.a.getX();
        int j = blockposition.getZ() - this.a.getY();

        return i * this.c.getAdjacentX() + j * this.c.getAdjacentZ() >= 0;
    }

    public void a() {
        this.f = 0;
    }

    public void b() {
        ++this.f;
    }

    public int c() {
        return this.f;
    }

    public BlockPosition d() {
        return this.a;
    }

    public BlockPosition e() {
        return this.b;
    }

    public int f() {
        return this.c.getAdjacentX() * 2;
    }

    public int g() {
        return this.c.getAdjacentZ() * 2;
    }

    public int h() {
        return this.d;
    }

    public void a(int i) {
        this.d = i;
    }

    public boolean i() {
        return this.e;
    }

    public void a(boolean flag) {
        this.e = flag;
    }

    public EnumDirection j() {
        return this.c;
    }
}
