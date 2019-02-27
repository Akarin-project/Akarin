package net.minecraft.server;

public class PathfinderGoalMakeLove extends PathfinderGoal {

    private final EntityVillager a;
    private EntityVillager b;
    private final World c;
    private int d;
    private Village e;

    public PathfinderGoalMakeLove(EntityVillager entityvillager) {
        this.a = entityvillager;
        this.c = entityvillager.world;
        this.a(3);
    }

    public boolean a() {
        if (this.a.getAge() != 0) {
            return false;
        } else if (this.a.getRandom().nextInt(500) != 0) {
            return false;
        } else {
            this.e = this.c.af().getClosestVillage(new BlockPosition(this.a), 0);
            if (this.e == null) {
                return false;
            } else if (this.g() && this.a.u(true)) {
                Entity entity = this.c.a(EntityVillager.class, this.a.getBoundingBox().grow(8.0D, 3.0D, 8.0D), (Entity) this.a);

                if (entity == null) {
                    return false;
                } else {
                    this.b = (EntityVillager) entity;
                    return this.b.getAge() == 0 && this.b.u(true);
                }
            } else {
                return false;
            }
        }
    }

    public void c() {
        this.d = 300;
        this.a.s(true);
    }

    public void d() {
        this.e = null;
        this.b = null;
        this.a.s(false);
    }

    public boolean b() {
        return this.d >= 0 && this.g() && this.a.getAge() == 0 && this.a.u(false);
    }

    public void e() {
        --this.d;
        this.a.getControllerLook().a(this.b, 10.0F, 30.0F);
        if (this.a.h(this.b) > 2.25D) {
            this.a.getNavigation().a((Entity) this.b, 0.25D);
        } else if (this.d == 0 && this.b.isInLove()) {
            this.i();
        }

        if (this.a.getRandom().nextInt(35) == 0) {
            this.c.broadcastEntityEffect(this.a, (byte) 12);
        }

    }

    private boolean g() {
        if (!this.e.i()) {
            return false;
        } else {
            int i = (int) ((double) ((float) this.e.c()) * 0.35D);

            return this.e.e() < i;
        }
    }

    private void i() {
        EntityVillager entityvillager = this.a.createChild(this.b);

        this.b.setAgeRaw(6000);
        this.a.setAgeRaw(6000);
        this.b.v(false);
        this.a.v(false);
        entityvillager.setAgeRaw(-24000);
        entityvillager.setPositionRotation(this.a.locX, this.a.locY, this.a.locZ, 0.0F, 0.0F);
        this.c.addEntity(entityvillager);
        this.c.broadcastEntityEffect(entityvillager, (byte) 12);
    }
}
