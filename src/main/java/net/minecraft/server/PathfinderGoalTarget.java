package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class PathfinderGoalTarget extends PathfinderGoal {

    protected final EntityCreature e;
    protected boolean f;
    private final boolean a;
    private int b;
    private int c;
    private int d;
    protected EntityLiving g;
    protected int h;

    public PathfinderGoalTarget(EntityCreature entitycreature, boolean flag) {
        this(entitycreature, flag, false);
    }

    public PathfinderGoalTarget(EntityCreature entitycreature, boolean flag, boolean flag1) {
        this.h = 60;
        this.e = entitycreature;
        this.f = flag;
        this.a = flag1;
    }

    public boolean b() {
        EntityLiving entityliving = this.e.getGoalTarget();

        if (entityliving == null) {
            entityliving = this.g;
        }

        if (entityliving == null) {
            return false;
        } else if (!entityliving.isAlive()) {
            return false;
        } else {
            ScoreboardTeamBase scoreboardteambase = this.e.getScoreboardTeam();
            ScoreboardTeamBase scoreboardteambase1 = entityliving.getScoreboardTeam();

            if (scoreboardteambase != null && scoreboardteambase1 == scoreboardteambase) {
                return false;
            } else {
                double d0 = this.i();

                if (this.e.h(entityliving) > d0 * d0) {
                    return false;
                } else {
                    if (this.f) {
                        if (this.e.getEntitySenses().a(entityliving)) {
                            this.d = 0;
                        } else if (++this.d > this.h) {
                            return false;
                        }
                    }

                    if (entityliving instanceof EntityHuman && ((EntityHuman) entityliving).abilities.isInvulnerable) {
                        return false;
                    } else {
                        this.e.setGoalTarget(entityliving);
                        return true;
                    }
                }
            }
        }
    }

    protected double i() {
        AttributeInstance attributeinstance = this.e.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);

        return attributeinstance == null ? 16.0D : attributeinstance.getValue();
    }

    public void c() {
        this.b = 0;
        this.c = 0;
        this.d = 0;
    }

    public void d() {
        this.e.setGoalTarget((EntityLiving) null);
        this.g = null;
    }

    public static boolean a(EntityInsentient entityinsentient, @Nullable EntityLiving entityliving, boolean flag, boolean flag1) {
        if (entityliving == null) {
            return false;
        } else if (entityliving == entityinsentient) {
            return false;
        } else if (!entityliving.isAlive()) {
            return false;
        } else if (!entityinsentient.b(entityliving.getClass())) {
            return false;
        } else if (entityinsentient.r(entityliving)) {
            return false;
        } else {
            if (entityinsentient instanceof EntityOwnable && ((EntityOwnable) entityinsentient).getOwnerUUID() != null) {
                if (entityliving instanceof EntityOwnable && ((EntityOwnable) entityinsentient).getOwnerUUID().equals(((EntityOwnable) entityliving).getOwnerUUID())) {
                    return false;
                }

                if (entityliving == ((EntityOwnable) entityinsentient).getOwner()) {
                    return false;
                }
            } else if (entityliving instanceof EntityHuman && !flag && ((EntityHuman) entityliving).abilities.isInvulnerable) {
                return false;
            }

            return !flag1 || entityinsentient.getEntitySenses().a(entityliving);
        }
    }

    protected boolean a(@Nullable EntityLiving entityliving, boolean flag) {
        if (!a(this.e, entityliving, flag, this.f)) {
            return false;
        } else if (!this.e.f(new BlockPosition(entityliving))) {
            return false;
        } else {
            if (this.a) {
                if (--this.c <= 0) {
                    this.b = 0;
                }

                if (this.b == 0) {
                    this.b = this.a(entityliving) ? 1 : 2;
                }

                if (this.b == 2) {
                    return false;
                }
            }

            return true;
        }
    }

    private boolean a(EntityLiving entityliving) {
        this.c = 10 + this.e.getRandom().nextInt(5);
        PathEntity pathentity = this.e.getNavigation().a((Entity) entityliving);

        if (pathentity == null) {
            return false;
        } else {
            PathPoint pathpoint = pathentity.c();

            if (pathpoint == null) {
                return false;
            } else {
                int i = pathpoint.a - MathHelper.floor(entityliving.locX);
                int j = pathpoint.c - MathHelper.floor(entityliving.locZ);

                return (double) (i * i + j * j) <= 2.25D;
            }
        }
    }

    public PathfinderGoalTarget b(int i) {
        this.h = i;
        return this;
    }
}
