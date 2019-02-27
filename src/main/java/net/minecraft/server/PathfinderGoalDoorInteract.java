package net.minecraft.server;

public abstract class PathfinderGoalDoorInteract extends PathfinderGoal {

    protected EntityInsentient a;
    protected BlockPosition b;
    protected boolean c;
    private boolean d;
    private float e;
    private float f;

    public PathfinderGoalDoorInteract(EntityInsentient entityinsentient) {
        this.b = BlockPosition.ZERO;
        this.a = entityinsentient;
        if (!(entityinsentient.getNavigation() instanceof Navigation)) {
            throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
        }
    }

    protected boolean g() {
        if (!this.c) {
            return false;
        } else {
            IBlockData iblockdata = this.a.world.getType(this.b);

            if (!(iblockdata.getBlock() instanceof BlockDoor)) {
                this.c = false;
                return false;
            } else {
                return (Boolean) iblockdata.get(BlockDoor.OPEN);
            }
        }
    }

    protected void a(boolean flag) {
        if (this.c) {
            IBlockData iblockdata = this.a.world.getType(this.b);

            if (iblockdata.getBlock() instanceof BlockDoor) {
                ((BlockDoor) iblockdata.getBlock()).setDoor(this.a.world, this.b, flag);
            }
        }

    }

    public boolean a() {
        if (!this.a.positionChanged) {
            return false;
        } else {
            Navigation navigation = (Navigation) this.a.getNavigation();
            PathEntity pathentity = navigation.m();

            if (pathentity != null && !pathentity.b() && navigation.g()) {
                for (int i = 0; i < Math.min(pathentity.e() + 2, pathentity.d()); ++i) {
                    PathPoint pathpoint = pathentity.a(i);

                    this.b = new BlockPosition(pathpoint.a, pathpoint.b + 1, pathpoint.c);
                    if (this.a.d((double) this.b.getX(), this.a.locY, (double) this.b.getZ()) <= 2.25D) {
                        this.c = this.a(this.b);
                        if (this.c) {
                            return true;
                        }
                    }
                }

                this.b = (new BlockPosition(this.a)).up();
                this.c = this.a(this.b);
                return this.c;
            } else {
                return false;
            }
        }
    }

    public boolean b() {
        return !this.d;
    }

    public void c() {
        this.d = false;
        this.e = (float) ((double) ((float) this.b.getX() + 0.5F) - this.a.locX);
        this.f = (float) ((double) ((float) this.b.getZ() + 0.5F) - this.a.locZ);
    }

    public void e() {
        float f = (float) ((double) ((float) this.b.getX() + 0.5F) - this.a.locX);
        float f1 = (float) ((double) ((float) this.b.getZ() + 0.5F) - this.a.locZ);
        float f2 = this.e * f + this.f * f1;

        if (f2 < 0.0F) {
            this.d = true;
        }

    }

    private boolean a(BlockPosition blockposition) {
        IBlockData iblockdata = this.a.world.getType(blockposition);

        return iblockdata.getBlock() instanceof BlockDoor && iblockdata.getMaterial() == Material.WOOD;
    }
}
