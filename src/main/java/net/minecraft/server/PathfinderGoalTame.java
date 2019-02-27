package net.minecraft.server;

public class PathfinderGoalTame extends PathfinderGoal {

    private final EntityHorseAbstract entity;
    private final double b;
    private double c;
    private double d;
    private double e;

    public PathfinderGoalTame(EntityHorseAbstract entityhorseabstract, double d0) {
        this.entity = entityhorseabstract;
        this.b = d0;
        this.a(1);
    }

    public boolean a() {
        if (!this.entity.isTamed() && this.entity.isVehicle()) {
            Vec3D vec3d = RandomPositionGenerator.a(this.entity, 5, 4);

            if (vec3d == null) {
                return false;
            } else {
                this.c = vec3d.x;
                this.d = vec3d.y;
                this.e = vec3d.z;
                return true;
            }
        } else {
            return false;
        }
    }

    public void c() {
        this.entity.getNavigation().a(this.c, this.d, this.e, this.b);
    }

    public boolean b() {
        return !this.entity.isTamed() && !this.entity.getNavigation().p() && this.entity.isVehicle();
    }

    public void e() {
        if (!this.entity.isTamed() && this.entity.getRandom().nextInt(50) == 0) {
            Entity entity = (Entity) this.entity.bP().get(0);

            if (entity == null) {
                return;
            }

            if (entity instanceof EntityHuman) {
                int i = this.entity.getTemper();
                int j = this.entity.getMaxDomestication();

                // CraftBukkit - fire EntityTameEvent
                if (j > 0 && this.entity.getRandom().nextInt(j) < i && !org.bukkit.craftbukkit.event.CraftEventFactory.callEntityTameEvent(this.entity, ((org.bukkit.craftbukkit.entity.CraftHumanEntity) this.entity.getBukkitEntity().getPassenger()).getHandle()).isCancelled()) {
                    this.entity.h((EntityHuman) entity);
                    return;
                }

                this.entity.r(5);
            }

            this.entity.ejectPassengers();
            this.entity.dZ();
            this.entity.world.broadcastEntityEffect(this.entity, (byte) 6);
        }

    }
}
