package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.TrigMath;
import org.bukkit.event.entity.EntityTargetEvent;
// CraftBukkit end

public class EntityCreature extends EntityLiving {

    public PathEntity pathEntity; // CraftBukkit - public
    public Entity target; // CraftBukkit - public
    protected boolean e = false;

    public EntityCreature(World world) {
        super(world);
    }

    protected boolean w() {
        return false;
    }

    protected void c_() {
        this.e = this.w();
        float f = 16.0F;

        if (this.target == null) {
            // CraftBukkit start
            Entity target = this.findTarget();
            if (target != null) {
                EntityTargetEvent event = new EntityTargetEvent(this.getBukkitEntity(), target.getBukkitEntity(), EntityTargetEvent.TargetReason.CLOSEST_PLAYER);
                this.world.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    if (event.getTarget() == null) {
                        this.target = null;
                    } else {
                        this.target = ((CraftEntity) event.getTarget()).getHandle();
                    }
                }
            }
            // CraftBukkit end

            if (this.target != null) {
                this.pathEntity = this.world.findPath(this, this.target, f);
            }
        } else if (!this.target.T()) {
            // CraftBukkit start
            EntityTargetEvent event = new EntityTargetEvent(this.getBukkitEntity(), null, EntityTargetEvent.TargetReason.TARGET_DIED);
            this.world.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                if (event.getTarget() == null) {
                    this.target = null;
                } else {
                    this.target = ((CraftEntity) event.getTarget()).getHandle();
                }
            }
            // CraftBukkit end
        } else {
            float f1 = this.target.f(this);

            if (this.e(this.target)) {
                this.a(this.target, f1);
            } else {
                this.b(this.target, f1);
            }
        }

        if (!this.e && this.target != null && (this.pathEntity == null || this.random.nextInt(20) == 0)) {
            this.pathEntity = this.world.findPath(this, this.target, f);
        } else if (!this.e && (this.pathEntity == null && this.random.nextInt(80) == 0 || this.random.nextInt(80) == 0)) {
            this.B();
        }

        int i = MathHelper.floor(this.boundingBox.b + 0.5D);
        boolean flag = this.ad();
        boolean flag1 = this.ae();

        this.pitch = 0.0F;
        if (this.pathEntity != null && this.random.nextInt(100) != 0) {
            Vec3D vec3d = this.pathEntity.a(this);
            double d0 = (double) (this.length * 2.0F);

            while (vec3d != null && vec3d.d(this.locX, vec3d.b, this.locZ) < d0 * d0) {
                this.pathEntity.a();
                if (this.pathEntity.b()) {
                    vec3d = null;
                    this.pathEntity = null;
                } else {
                    vec3d = this.pathEntity.a(this);
                }
            }

            this.aC = false;
            if (vec3d != null) {
                double d1 = vec3d.a - this.locX;
                double d2 = vec3d.c - this.locZ;
                double d3 = vec3d.b - (double) i;
                // CraftBukkit - Math -> TrigMath
                float f2 = (float) (TrigMath.atan2(d2, d1) * 180.0D / 3.1415927410125732D) - 90.0F;
                float f3 = f2 - this.yaw;

                for (this.aA = this.aE; f3 < -180.0F; f3 += 360.0F) {
                    ;
                }

                while (f3 >= 180.0F) {
                    f3 -= 360.0F;
                }

                if (f3 > 30.0F) {
                    f3 = 30.0F;
                }

                if (f3 < -30.0F) {
                    f3 = -30.0F;
                }

                this.yaw += f3;
                if (this.e && this.target != null) {
                    double d4 = this.target.locX - this.locX;
                    double d5 = this.target.locZ - this.locZ;
                    float f4 = this.yaw;

                    this.yaw = (float) (Math.atan2(d5, d4) * 180.0D / 3.1415927410125732D) - 90.0F;
                    f3 = (f4 - this.yaw + 90.0F) * 3.1415927F / 180.0F;
                    this.az = -MathHelper.sin(f3) * this.aA * 1.0F;
                    this.aA = MathHelper.cos(f3) * this.aA * 1.0F;
                }

                if (d3 > 0.0D) {
                    this.aC = true;
                }
            }

            if (this.target != null) {
                this.a(this.target, 30.0F, 30.0F);
            }

            if (this.positionChanged && !this.C()) {
                this.aC = true;
            }

            if (this.random.nextFloat() < 0.8F && (flag || flag1)) {
                this.aC = true;
            }
        } else {
            super.c_();
            this.pathEntity = null;
        }
    }

    protected void B() {
        boolean flag = false;
        int i = -1;
        int j = -1;
        int k = -1;
        float f = -99999.0F;

        for (int l = 0; l < 10; ++l) {
            int i1 = MathHelper.floor(this.locX + (double) this.random.nextInt(13) - 6.0D);
            int j1 = MathHelper.floor(this.locY + (double) this.random.nextInt(7) - 3.0D);
            int k1 = MathHelper.floor(this.locZ + (double) this.random.nextInt(13) - 6.0D);
            float f1 = this.a(i1, j1, k1);

            if (f1 > f) {
                f = f1;
                i = i1;
                j = j1;
                k = k1;
                flag = true;
            }
        }

        if (flag) {
            this.pathEntity = this.world.a(this, i, j, k, 10.0F);
        }
    }

    protected void a(Entity entity, float f) {}

    protected void b(Entity entity, float f) {}

    protected float a(int i, int j, int k) {
        return 0.0F;
    }

    protected Entity findTarget() {
        return null;
    }

    public boolean d() {
        int i = MathHelper.floor(this.locX);
        int j = MathHelper.floor(this.boundingBox.b);
        int k = MathHelper.floor(this.locZ);

        return super.d() && this.a(i, j, k) >= 0.0F;
    }

    public boolean C() {
        return this.pathEntity != null;
    }

    public void setPathEntity(PathEntity pathentity) {
        this.pathEntity = pathentity;
    }

    public Entity F() {
        return this.target;
    }

    public void setTarget(Entity entity) {
        this.target = entity;
    }
}
