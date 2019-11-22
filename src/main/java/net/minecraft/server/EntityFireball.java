package net.minecraft.server;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public abstract class EntityFireball extends Entity {

    public EntityLiving shooter;
    private int f;
    private int g;
    public double dirX;
    public double dirY;
    public double dirZ;
    public float bukkitYield = 1; // CraftBukkit
    public boolean isIncendiary = true; // CraftBukkit

    protected EntityFireball(EntityTypes<? extends EntityFireball> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityFireball(EntityTypes<? extends EntityFireball> entitytypes, double d0, double d1, double d2, double d3, double d4, double d5, World world) {
        this(entitytypes, world);
        this.setPositionRotation(d0, d1, d2, this.yaw, this.pitch);
        this.setPosition(d0, d1, d2);
        double d6 = (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

        this.dirX = d3 / d6 * 0.1D;
        this.dirY = d4 / d6 * 0.1D;
        this.dirZ = d5 / d6 * 0.1D;
    }

    public EntityFireball(EntityTypes<? extends EntityFireball> entitytypes, EntityLiving entityliving, double d0, double d1, double d2, World world) {
        this(entitytypes, world);
        this.shooter = entityliving;
        this.projectileSource = (org.bukkit.entity.LivingEntity) entityliving.getBukkitEntity(); // CraftBukkit
        this.setPositionRotation(entityliving.locX, entityliving.locY, entityliving.locZ, entityliving.yaw, entityliving.pitch);
        this.setPosition(this.locX, this.locY, this.locZ);
        this.setMot(Vec3D.a);
        // CraftBukkit start - Added setDirection method
        this.setDirection(d0, d1, d2);
    }

    public void setDirection(double d0, double d1, double d2) {
        // CraftBukkit end
        d0 += this.random.nextGaussian() * 0.4D;
        d1 += this.random.nextGaussian() * 0.4D;
        d2 += this.random.nextGaussian() * 0.4D;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

        this.dirX = d0 / d3 * 0.1D;
        this.dirY = d1 / d3 * 0.1D;
        this.dirZ = d2 / d3 * 0.1D;
    }

    @Override
    protected void initDatawatcher() {}

    @Override
    public void tick() {
        if (!this.world.isClientSide && (this.shooter != null && this.shooter.dead || !this.world.isLoaded(new BlockPosition(this)))) {
            this.die();
        } else {
            super.tick();
            if (this.K_()) {
                this.setOnFire(1);
            }

            ++this.g;
            MovingObjectPosition movingobjectposition = ProjectileHelper.a(this, true, this.g >= 25, this.shooter, RayTrace.BlockCollisionOption.COLLIDER);

            // Paper start - Call ProjectileCollideEvent
            if (movingobjectposition instanceof MovingObjectPositionEntity) {
                com.destroystokyo.paper.event.entity.ProjectileCollideEvent event = CraftEventFactory.callProjectileCollideEvent(this, (MovingObjectPositionEntity)movingobjectposition);
                if (event.isCancelled()) {
                    movingobjectposition = null;
                }
            }
            // Paper end

            if (movingobjectposition != null && movingobjectposition.getType() != MovingObjectPosition.EnumMovingObjectType.MISS) { // Paper - add null check in case cancelled
                this.a(movingobjectposition);

                // CraftBukkit start - Fire ProjectileHitEvent
                if (this.dead) {
                    CraftEventFactory.callProjectileHitEvent(this, movingobjectposition);
                }
                // CraftBukkit end
            }

            Vec3D vec3d = this.getMot();

            this.locX += vec3d.x;
            this.locY += vec3d.y;
            this.locZ += vec3d.z;
            ProjectileHelper.a(this, 0.2F);
            float f = this.k();

            if (this.isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    float f1 = 0.25F;

                    this.world.addParticle(Particles.BUBBLE, this.locX - vec3d.x * 0.25D, this.locY - vec3d.y * 0.25D, this.locZ - vec3d.z * 0.25D, vec3d.x, vec3d.y, vec3d.z);
                }

                f = 0.8F;
            }

            this.setMot(vec3d.add(this.dirX, this.dirY, this.dirZ).a((double) f));
            this.world.addParticle(this.i(), this.locX, this.locY + 0.5D, this.locZ, 0.0D, 0.0D, 0.0D);
            this.setPosition(this.locX, this.locY, this.locZ);
        }
    }

    protected boolean K_() {
        return true;
    }

    protected ParticleParam i() {
        return Particles.SMOKE;
    }

    protected float k() {
        return 0.95F;
    }

    protected abstract void a(MovingObjectPosition movingobjectposition);

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        Vec3D vec3d = this.getMot();

        nbttagcompound.set("direction", this.a(new double[]{vec3d.x, vec3d.y, vec3d.z}));
        nbttagcompound.set("power", this.a(new double[]{this.dirX, this.dirY, this.dirZ}));
        nbttagcompound.setInt("life", this.f);
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        NBTTagList nbttaglist;

        if (nbttagcompound.hasKeyOfType("power", 9)) {
            nbttaglist = nbttagcompound.getList("power", 6);
            if (nbttaglist.size() == 3) {
                this.dirX = nbttaglist.h(0);
                this.dirY = nbttaglist.h(1);
                this.dirZ = nbttaglist.h(2);
            }
        }

        this.f = nbttagcompound.getInt("life");
        if (nbttagcompound.hasKeyOfType("direction", 9) && nbttagcompound.getList("direction", 6).size() == 3) {
            nbttaglist = nbttagcompound.getList("direction", 6);
            this.setMot(nbttaglist.h(0), nbttaglist.h(1), nbttaglist.h(2));
        } else {
            this.die();
        }

    }

    @Override
    public boolean isInteractable() {
        return true;
    }

    @Override
    public float aS() {
        return 1.0F;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            this.velocityChanged();
            if (damagesource.getEntity() != null) {
                // CraftBukkit start
                if (CraftEventFactory.handleNonLivingEntityDamageEvent(this, damagesource, f)) {
                    return false;
                }
                // CraftBukkit end
                Vec3D vec3d = damagesource.getEntity().getLookDirection();

                this.setMot(vec3d);
                this.dirX = vec3d.x * 0.1D;
                this.dirY = vec3d.y * 0.1D;
                this.dirZ = vec3d.z * 0.1D;
                if (damagesource.getEntity() instanceof EntityLiving) {
                    this.shooter = (EntityLiving) damagesource.getEntity();
                    this.projectileSource = (org.bukkit.projectiles.ProjectileSource) this.shooter.getBukkitEntity();
                }

                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public float aF() {
        return 1.0F;
    }

    @Override
    public Packet<?> N() {
        int i = this.shooter == null ? 0 : this.shooter.getId();

        return new PacketPlayOutSpawnEntity(this.getId(), this.getUniqueID(), this.locX, this.locY, this.locZ, this.pitch, this.yaw, this.getEntityType(), i, new Vec3D(this.dirX, this.dirY, this.dirZ));
    }
}
