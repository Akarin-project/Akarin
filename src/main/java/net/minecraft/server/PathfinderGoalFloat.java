package net.minecraft.server;

public class PathfinderGoalFloat extends PathfinderGoal {

    private final EntityInsentient a;

    public PathfinderGoalFloat(EntityInsentient entityinsentient) {
        this.a = entityinsentient;
        this.a(4);
        entityinsentient.getNavigation().d(true);
    }

    public boolean a() {
        return this.a.isInWater() && this.a.bY() > 0.4D || this.a.ax();
    }

    public void e() {
        if (this.a.getRandom().nextFloat() < 0.8F) {
            this.a.getControllerJump().a();
        }

    }
}
