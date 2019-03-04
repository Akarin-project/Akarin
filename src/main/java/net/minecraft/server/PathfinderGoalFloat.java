package net.minecraft.server;

public class PathfinderGoalFloat extends PathfinderGoal {

    private final EntityInsentient a;

    public PathfinderGoalFloat(EntityInsentient entityinsentient) {
        this.a = entityinsentient;
        if (entityinsentient.getWorld().paperConfig.nerfedMobsShouldJump) entityinsentient.goalFloat = this; // Paper
        this.a(4);
        entityinsentient.getNavigation().d(true);
    }

    public boolean validConditions() { return this.a(); } // Paper - OBFHELPER
    public boolean a() {
        return this.a.isInWater() && this.a.bY() > 0.4D || this.a.ax();
    }

    public void update() { this.e(); } // Paper - OBFHELPER
    public void e() {
        if (this.a.getRandom().nextFloat() < 0.8F) {
            this.a.getControllerJump().a();
        }

    }
}
