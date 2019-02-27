package net.minecraft.server;

public abstract class PathfinderGoalGotoTarget extends PathfinderGoal {

    private final EntityCreature f;
    public double a;
    protected int b;
    protected int c;
    private int g;
    protected BlockPosition d;
    private boolean h;
    private final int i;
    private final int j;
    public int e;

    public PathfinderGoalGotoTarget(EntityCreature entitycreature, double d0, int i) {
        this(entitycreature, d0, i, 1);
    }

    public PathfinderGoalGotoTarget(EntityCreature entitycreature, double d0, int i, int j) {
        this.d = BlockPosition.ZERO;
        this.f = entitycreature;
        this.a = d0;
        this.i = i;
        this.e = 0;
        this.j = j;
        this.a(5);
    }

    public boolean a() {
        if (this.b > 0) {
            --this.b;
            return false;
        } else {
            this.b = this.a(this.f);
            return this.l();
        }
    }

    protected int a(EntityCreature entitycreature) {
        return 200 + entitycreature.getRandom().nextInt(200);
    }

    public boolean b() {
        return this.c >= -this.g && this.c <= 1200 && this.a(this.f.world, this.d);
    }

    public void c() {
        this.f.getNavigation().a((double) ((float) this.d.getX()) + 0.5D, (double) (this.d.getY() + 1), (double) ((float) this.d.getZ()) + 0.5D, this.a);
        this.c = 0;
        this.g = this.f.getRandom().nextInt(this.f.getRandom().nextInt(1200) + 1200) + 1200;
    }

    public double g() {
        return 1.0D;
    }

    public void e() {
        if (this.f.d(this.d.up()) > this.g()) {
            this.h = false;
            ++this.c;
            if (this.i()) {
                this.f.getNavigation().a((double) ((float) this.d.getX()) + 0.5D, (double) (this.d.getY() + this.j()), (double) ((float) this.d.getZ()) + 0.5D, this.a);
            }
        } else {
            this.h = true;
            --this.c;
        }

    }

    public boolean i() {
        return this.c % 40 == 0;
    }

    public int j() {
        return 1;
    }

    protected boolean k() {
        return this.h;
    }

    private boolean l() {
        int i = this.i;
        int j = this.j;
        BlockPosition blockposition = new BlockPosition(this.f);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int k = this.e; k <= j; k = k > 0 ? -k : 1 - k) {
            for (int l = 0; l < i; ++l) {
                for (int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1) {
                    for (int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1) {
                        blockposition_mutableblockposition.g(blockposition).d(i1, k - 1, j1);
                        if (this.f.f((BlockPosition) blockposition_mutableblockposition) && this.a(this.f.world, blockposition_mutableblockposition)) {
                            this.d = blockposition_mutableblockposition;
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    protected abstract boolean a(IWorldReader iworldreader, BlockPosition blockposition);
}
