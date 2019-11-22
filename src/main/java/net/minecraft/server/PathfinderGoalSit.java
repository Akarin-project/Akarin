package net.minecraft.server;

import java.util.EnumSet;

public class PathfinderGoalSit extends PathfinderGoal {

    private final EntityTameableAnimal entity;
    private boolean willSit;

    public PathfinderGoalSit(EntityTameableAnimal entitytameableanimal) {
        this.entity = entitytameableanimal;
        this.a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean b() {
        return this.willSit;
    }

    @Override
    public boolean a() {
        if (!this.entity.isTamed()) {
            return this.willSit && this.entity.getGoalTarget() == null; // CraftBukkit - Allow sitting for wild animals
        } else if (this.entity.av()) {
            return false;
        } else if (!this.entity.onGround) {
            return false;
        } else {
            EntityLiving entityliving = this.entity.getOwner();

            return entityliving == null ? true : (this.entity.h((Entity) entityliving) < 144.0D && entityliving.getLastDamager() != null ? false : this.willSit);
        }
    }

    @Override
    public void c() {
        this.entity.getNavigation().o();
        this.entity.setSitting(true);
    }

    @Override
    public void d() {
        this.entity.setSitting(false);
    }

    public void setSitting(boolean flag) {
        this.willSit = flag;
    }
}
