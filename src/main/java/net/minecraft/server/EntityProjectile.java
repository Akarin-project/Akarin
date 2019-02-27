package net.minecraft.server;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public abstract class EntityProjectile extends Entity implements IProjectile {

    private int blockX;
    private int blockY;
    private int blockZ;
    protected boolean inGround;
    public int shake;
    public EntityLiving shooter;
    public UUID shooterId;
    public Entity d;
    private int aw;

    protected EntityProjectile(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.blockX = -1;
        this.blockY = -1;
        this.blockZ = -1;
        this.setSize(0.25F, 0.25F);
    }

    protected EntityProjectile(EntityTypes<?> entitytypes, double d0, double d1, double d2, World world) {
        this(entitytypes, world);
        this.setPosition(d0, d1, d2);
    }

    protected EntityProjectile(EntityTypes<?> entitytypes, EntityLiving entityliving, World world) {
        this(entitytypes, entityliving.locX, entityliving.locY + (double) entityliving.getHeadHeight() - 0.10000000149011612D, entityliving.locZ, world);
        this.shooter = entityliving;
        this.shooterId = entityliving.getUniqueID();
    }

    protected void x_() {}

    public void a(Entity entity, float f, float f1, float f2, float f3, float f4) {
        float f5 = -MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f * 0.017453292F);
        float f6 = -MathHelper.sin((f + f2) * 0.017453292F);
        float f7 = MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f * 0.017453292F);

        this.shoot((double) f5, (double) f6, (double) f7, f3, f4);
        this.motX += entity.motX;
        this.motZ += entity.motZ;
        if (!entity.onGround) {
            this.motY += entity.motY;
        }

    }

    public void shoot(double d0, double d1, double d2, float f, float f1) {
        float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

        d0 /= (double) f2;
        d1 /= (double) f2;
        d2 /= (double) f2;
        d0 += this.random.nextGaussian() * 0.007499999832361937D * (double) f1;
        d1 += this.random.nextGaussian() * 0.007499999832361937D * (double) f1;
        d2 += this.random.nextGaussian() * 0.007499999832361937D * (double) f1;
        d0 *= (double) f;
        d1 *= (double) f;
        d2 *= (double) f;
        this.motX = d0;
        this.motY = d1;
        this.motZ = d2;
        float f3 = MathHelper.sqrt(d0 * d0 + d2 * d2);

        this.yaw = (float) (MathHelper.c(d0, d2) * 57.2957763671875D);
        this.pitch = (float) (MathHelper.c(d1, (double) f3) * 57.2957763671875D);
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
    }

    public void tick() {
        this.N = this.locX;
        this.O = this.locY;
        this.P = this.locZ;
        super.tick();
        if (this.shake > 0) {
            --this.shake;
        }

        if (this.inGround) {
            this.inGround = false;
            this.motX *= (double) (this.random.nextFloat() * 0.2F);
            this.motY *= (double) (this.random.nextFloat() * 0.2F);
            this.motZ *= (double) (this.random.nextFloat() * 0.2F);
        }

        Vec3D vec3d = new Vec3D(this.locX, this.locY, this.locZ);
        Vec3D vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
        MovingObjectPosition movingobjectposition = this.world.rayTrace(vec3d, vec3d1);

        vec3d = new Vec3D(this.locX, this.locY, this.locZ);
        vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
        if (movingobjectposition != null) {
            vec3d1 = new Vec3D(movingobjectposition.pos.x, movingobjectposition.pos.y, movingobjectposition.pos.z);
        }

        Entity entity = null;
        List<Entity> list = this.world.getEntities(this, this.getBoundingBox().b(this.motX, this.motY, this.motZ).g(1.0D));
        double d0 = 0.0D;
        boolean flag = false;

        for (int i = 0; i < list.size(); ++i) {
            Entity entity1 = (Entity) list.get(i);

            if (entity1.isInteractable()) {
                if (entity1 == this.d) {
                    flag = true;
                } else if (this.shooter != null && this.ticksLived < 2 && this.d == null) {
                    this.d = entity1;
                    flag = true;
                } else {
                    flag = false;
                    AxisAlignedBB axisalignedbb = entity1.getBoundingBox().g(0.30000001192092896D);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb.b(vec3d, vec3d1);

                    if (movingobjectposition1 != null) {
                        double d1 = vec3d.distanceSquared(movingobjectposition1.pos);

                        if (d1 < d0 || d0 == 0.0D) {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }
        }

        if (this.d != null) {
            if (flag) {
                this.aw = 2;
            } else if (this.aw-- <= 0) {
                this.d = null;
            }
        }

        if (entity != null) {
            movingobjectposition = new MovingObjectPosition(entity);
        }

        if (movingobjectposition != null) {
            if (movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.BLOCK && this.world.getType(movingobjectposition.getBlockPosition()).getBlock() == Blocks.NETHER_PORTAL) {
                this.e(movingobjectposition.getBlockPosition());
            } else {
                this.a(movingobjectposition);
            }
        }

        this.locX += this.motX;
        this.locY += this.motY;
        this.locZ += this.motZ;
        float f = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);

        this.yaw = (float) (MathHelper.c(this.motX, this.motZ) * 57.2957763671875D);

        for (this.pitch = (float) (MathHelper.c(this.motY, (double) f) * 57.2957763671875D); this.pitch - this.lastPitch < -180.0F; this.lastPitch -= 360.0F) {
            ;
        }

        while (this.pitch - this.lastPitch >= 180.0F) {
            this.lastPitch += 360.0F;
        }

        while (this.yaw - this.lastYaw < -180.0F) {
            this.lastYaw -= 360.0F;
        }

        while (this.yaw - this.lastYaw >= 180.0F) {
            this.lastYaw += 360.0F;
        }

        this.pitch = this.lastPitch + (this.pitch - this.lastPitch) * 0.2F;
        this.yaw = this.lastYaw + (this.yaw - this.lastYaw) * 0.2F;
        float f1 = 0.99F;
        float f2 = this.f();

        if (this.isInWater()) {
            for (int j = 0; j < 4; ++j) {
                float f3 = 0.25F;

                this.world.addParticle(Particles.e, this.locX - this.motX * 0.25D, this.locY - this.motY * 0.25D, this.locZ - this.motZ * 0.25D, this.motX, this.motY, this.motZ);
            }

            f1 = 0.8F;
        }

        this.motX *= (double) f1;
        this.motY *= (double) f1;
        this.motZ *= (double) f1;
        if (!this.isNoGravity()) {
            this.motY -= (double) f2;
        }

        this.setPosition(this.locX, this.locY, this.locZ);
    }

    protected float f() {
        return 0.03F;
    }

    protected abstract void a(MovingObjectPosition movingobjectposition);

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("xTile", this.blockX);
        nbttagcompound.setInt("yTile", this.blockY);
        nbttagcompound.setInt("zTile", this.blockZ);
        nbttagcompound.setByte("shake", (byte) this.shake);
        nbttagcompound.setByte("inGround", (byte) (this.inGround ? 1 : 0));
        if (this.shooterId != null) {
            nbttagcompound.set("owner", GameProfileSerializer.a(this.shooterId));
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        this.blockX = nbttagcompound.getInt("xTile");
        this.blockY = nbttagcompound.getInt("yTile");
        this.blockZ = nbttagcompound.getInt("zTile");
        this.shake = nbttagcompound.getByte("shake") & 255;
        this.inGround = nbttagcompound.getByte("inGround") == 1;
        this.shooter = null;
        if (nbttagcompound.hasKeyOfType("owner", 10)) {
            this.shooterId = GameProfileSerializer.b(nbttagcompound.getCompound("owner"));
        }

    }

    @Nullable
    public EntityLiving getShooter() {
        if (this.shooter == null && this.shooterId != null && this.world instanceof WorldServer) {
            Entity entity = ((WorldServer) this.world).getEntity(this.shooterId);

            if (entity instanceof EntityLiving) {
                this.shooter = (EntityLiving) entity;
            } else {
                this.shooterId = null;
            }
        }

        return this.shooter;
    }
}
