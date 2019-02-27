package net.minecraft.server;

public abstract class EntityFireball extends Entity {

    public EntityLiving shooter;
    private int e;
    private int f;
    public double dirX;
    public double dirY;
    public double dirZ;

    protected EntityFireball(EntityTypes<?> entitytypes, World world, float f, float f1) {
        super(entitytypes, world);
        this.setSize(f, f1);
    }

    public EntityFireball(EntityTypes<?> entitytypes, double d0, double d1, double d2, double d3, double d4, double d5, World world, float f, float f1) {
        this(entitytypes, world, f, f1);
        this.setPositionRotation(d0, d1, d2, this.yaw, this.pitch);
        this.setPosition(d0, d1, d2);
        double d6 = (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

        this.dirX = d3 / d6 * 0.1D;
        this.dirY = d4 / d6 * 0.1D;
        this.dirZ = d5 / d6 * 0.1D;
    }

    public EntityFireball(EntityTypes<?> entitytypes, EntityLiving entityliving, double d0, double d1, double d2, World world, float f, float f1) {
        this(entitytypes, world, f, f1);
        this.shooter = entityliving;
        this.setPositionRotation(entityliving.locX, entityliving.locY, entityliving.locZ, entityliving.yaw, entityliving.pitch);
        this.setPosition(this.locX, this.locY, this.locZ);
        this.motX = 0.0D;
        this.motY = 0.0D;
        this.motZ = 0.0D;
        d0 += this.random.nextGaussian() * 0.4D;
        d1 += this.random.nextGaussian() * 0.4D;
        d2 += this.random.nextGaussian() * 0.4D;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

        this.dirX = d0 / d3 * 0.1D;
        this.dirY = d1 / d3 * 0.1D;
        this.dirZ = d2 / d3 * 0.1D;
    }

    protected void x_() {}

    public void tick() {
        if (!this.world.isClientSide && (this.shooter != null && this.shooter.dead || !this.world.isLoaded(new BlockPosition(this)))) {
            this.die();
        } else {
            super.tick();
            if (this.f()) {
                this.setOnFire(1);
            }

            ++this.f;
            MovingObjectPosition movingobjectposition = ProjectileHelper.a(this, true, this.f >= 25, this.shooter);

            if (movingobjectposition != null) {
                this.a(movingobjectposition);
            }

            this.locX += this.motX;
            this.locY += this.motY;
            this.locZ += this.motZ;
            ProjectileHelper.a(this, 0.2F);
            float f = this.k();

            if (this.isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    float f1 = 0.25F;

                    this.world.addParticle(Particles.e, this.locX - this.motX * 0.25D, this.locY - this.motY * 0.25D, this.locZ - this.motZ * 0.25D, this.motX, this.motY, this.motZ);
                }

                f = 0.8F;
            }

            this.motX += this.dirX;
            this.motY += this.dirY;
            this.motZ += this.dirZ;
            this.motX *= (double) f;
            this.motY *= (double) f;
            this.motZ *= (double) f;
            this.world.addParticle(this.i(), this.locX, this.locY + 0.5D, this.locZ, 0.0D, 0.0D, 0.0D);
            this.setPosition(this.locX, this.locY, this.locZ);
        }
    }

    protected boolean f() {
        return true;
    }

    protected ParticleParam i() {
        return Particles.M;
    }

    protected float k() {
        return 0.95F;
    }

    protected abstract void a(MovingObjectPosition movingobjectposition);

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.set("direction", this.a(new double[] { this.motX, this.motY, this.motZ}));
        nbttagcompound.set("power", this.a(new double[] { this.dirX, this.dirY, this.dirZ}));
        nbttagcompound.setInt("life", this.e);
    }

    public void a(NBTTagCompound nbttagcompound) {
        NBTTagList nbttaglist;

        if (nbttagcompound.hasKeyOfType("power", 9)) {
            nbttaglist = nbttagcompound.getList("power", 6);
            if (nbttaglist.size() == 3) {
                this.dirX = nbttaglist.k(0);
                this.dirY = nbttaglist.k(1);
                this.dirZ = nbttaglist.k(2);
            }
        }

        this.e = nbttagcompound.getInt("life");
        if (nbttagcompound.hasKeyOfType("direction", 9) && nbttagcompound.getList("direction", 6).size() == 3) {
            nbttaglist = nbttagcompound.getList("direction", 6);
            this.motX = nbttaglist.k(0);
            this.motY = nbttaglist.k(1);
            this.motZ = nbttaglist.k(2);
        } else {
            this.die();
        }

    }

    public boolean isInteractable() {
        return true;
    }

    public float aM() {
        return 1.0F;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            this.aA();
            if (damagesource.getEntity() != null) {
                Vec3D vec3d = damagesource.getEntity().aN();

                if (vec3d != null) {
                    this.motX = vec3d.x;
                    this.motY = vec3d.y;
                    this.motZ = vec3d.z;
                    this.dirX = this.motX * 0.1D;
                    this.dirY = this.motY * 0.1D;
                    this.dirZ = this.motZ * 0.1D;
                }

                if (damagesource.getEntity() instanceof EntityLiving) {
                    this.shooter = (EntityLiving) damagesource.getEntity();
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public float az() {
        return 1.0F;
    }
}
