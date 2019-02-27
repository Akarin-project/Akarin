package net.minecraft.server;

public class PathfinderGoalSit extends PathfinderGoal {

    private final EntityTameableAnimal entity;
    private boolean willSit;

    public PathfinderGoalSit(EntityTameableAnimal entitytameableanimal) {
        this.entity = entitytameableanimal;
        this.a(5);
    }

    public boolean a() {
        if (!this.entity.isTamed()) {
            return false;
        } else if (this.entity.aq()) {
            return false;
        } else if (!this.entity.onGround) {
            return false;
        } else {
            EntityLiving entityliving = this.entity.getOwner();

            return entityliving == null ? true : (this.entity.h(entityliving) < 144.0D && entityliving.getLastDamager() != null ? false : this.willSit);
        }
    }

    public void c() {
        this.entity.getNavigation().q();
        this.entity.setSitting(true);
    }

    public void d() {
        this.entity.setSitting(false);
    }

    public void setSitting(boolean flag) {
        this.willSit = flag;
    }
}
