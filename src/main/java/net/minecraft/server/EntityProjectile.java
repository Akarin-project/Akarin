package net.minecraft.server;

import java.util.Iterator;
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
    private Entity as;
    private int at;

    protected EntityProjectile(EntityTypes<? extends EntityProjectile> entitytypes, World world) {
        super(entitytypes, world);
        this.blockX = -1;
        this.blockY = -1;
        this.blockZ = -1;
    }

    protected EntityProjectile(EntityTypes<? extends EntityProjectile> entitytypes, double d0, double d1, double d2, World world) {
        this(entitytypes, world);
        this.setPosition(d0, d1, d2);
    }

    protected EntityProjectile(EntityTypes<? extends EntityProjectile> entitytypes, EntityLiving entityliving, World world) {
        this(entitytypes, entityliving.locX, entityliving.locY + (double) entityliving.getHeadHeight() - 0.10000000149011612D, entityliving.locZ, world);
        this.shooter = entityliving;
        this.shooterId = entityliving.getUniqueID();
        this.projectileSource = (org.bukkit.entity.LivingEntity) entityliving.getBukkitEntity(); // CraftBukkit
    }

    public void a(Entity entity, float f, float f1, float f2, float f3, float f4) {
        float f5 = -MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f * 0.017453292F);
        float f6 = -MathHelper.sin((f + f2) * 0.017453292F);
        float f7 = MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f * 0.017453292F);

        this.shoot((double) f5, (double) f6, (double) f7, f3, f4);
        Vec3D vec3d = entity.getMot();

        if (!entity.world.paperConfig.disableRelativeProjectileVelocity) this.setMot(this.getMot().add(vec3d.x, entity.onGround ? 0.0D : vec3d.y, vec3d.z)); // Paper - allow disabling relative velocity
    }

    @Override
    public void shoot(double d0, double d1, double d2, float f, float f1) {
        Vec3D vec3d = (new Vec3D(d0, d1, d2)).d().add(this.random.nextGaussian() * 0.007499999832361937D * (double) f1, this.random.nextGaussian() * 0.007499999832361937D * (double) f1, this.random.nextGaussian() * 0.007499999832361937D * (double) f1).a((double) f);

        this.setMot(vec3d);
        float f2 = MathHelper.sqrt(b(vec3d));

        this.yaw = (float) (MathHelper.d(vec3d.x, vec3d.z) * 57.2957763671875D);
        this.pitch = (float) (MathHelper.d(vec3d.y, (double) f2) * 57.2957763671875D);
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
    }

    @Override
    public void tick() {
        this.H = this.locX;
        this.I = this.locY;
        this.J = this.locZ;
        super.tick();
        if (this.shake > 0) {
            --this.shake;
        }

        if (this.inGround) {
            this.inGround = false;
            this.setMot(this.getMot().d((double) (this.random.nextFloat() * 0.2F), (double) (this.random.nextFloat() * 0.2F), (double) (this.random.nextFloat() * 0.2F)));
        }

        AxisAlignedBB axisalignedbb = this.getBoundingBox().a(this.getMot()).g(1.0D);
        Iterator iterator = this.world.getEntities(this, axisalignedbb, (entity) -> {
            return !entity.isSpectator() && entity.isInteractable();
        }).iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity == this.as) {
                ++this.at;
                break;
            }

            if (this.shooter != null && this.ticksLived < 2 && this.as == null && this.shooter == entity) { // CraftBukkit - MC-88491
                this.as = entity;
                this.at = 3;
                break;
            }
        }

        MovingObjectPosition movingobjectposition = ProjectileHelper.a(this, axisalignedbb, (entity1) -> {
            return !entity1.isSpectator() && entity1.isInteractable() && entity1 != this.as;
        }, RayTrace.BlockCollisionOption.OUTLINE, true);

        if (this.as != null && this.at-- <= 0) {
            this.as = null;
        }

        // Paper start - Call ProjectileCollideEvent
        if (movingobjectposition instanceof MovingObjectPositionEntity) {
            com.destroystokyo.paper.event.entity.ProjectileCollideEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callProjectileCollideEvent(this, (MovingObjectPositionEntity)movingobjectposition);
            if (event.isCancelled()) {
                movingobjectposition = null;
            }
        }
        // Paper end

        if (movingobjectposition != null && movingobjectposition.getType() != MovingObjectPosition.EnumMovingObjectType.MISS) { // Paper - add null check in case cancelled
            if (movingobjectposition.getType() == MovingObjectPosition.EnumMovingObjectType.BLOCK && this.world.getType(((MovingObjectPositionBlock) movingobjectposition).getBlockPosition()).getBlock() == Blocks.NETHER_PORTAL) {
                this.c(((MovingObjectPositionBlock) movingobjectposition).getBlockPosition());
            } else {
                this.a(movingobjectposition);
                // CraftBukkit start
                if (this.dead) {
                    org.bukkit.craftbukkit.event.CraftEventFactory.callProjectileHitEvent(this, movingobjectposition);
                }
                // CraftBukkit end
            }
        }

        Vec3D vec3d = this.getMot();

        this.locX += vec3d.x;
        this.locY += vec3d.y;
        this.locZ += vec3d.z;
        float f = MathHelper.sqrt(b(vec3d));

        this.yaw = (float) (MathHelper.d(vec3d.x, vec3d.z) * 57.2957763671875D);

        for (this.pitch = (float) (MathHelper.d(vec3d.y, (double) f) * 57.2957763671875D); this.pitch - this.lastPitch < -180.0F; this.lastPitch -= 360.0F) {
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

        this.pitch = MathHelper.g(0.2F, this.lastPitch, this.pitch);
        this.yaw = MathHelper.g(0.2F, this.lastYaw, this.yaw);
        float f1;

        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                float f2 = 0.25F;

                this.world.addParticle(Particles.BUBBLE, this.locX - vec3d.x * 0.25D, this.locY - vec3d.y * 0.25D, this.locZ - vec3d.z * 0.25D, vec3d.x, vec3d.y, vec3d.z);
            }

            f1 = 0.8F;
        } else {
            f1 = 0.99F;
        }

        this.setMot(vec3d.a((double) f1));
        if (!this.isNoGravity()) {
            Vec3D vec3d1 = this.getMot();

            this.setMot(vec3d1.x, vec3d1.y - (double) this.l(), vec3d1.z);
        }

        this.setPosition(this.locX, this.locY, this.locZ);
    }

    protected float l() {
        return 0.03F;
    }

    protected abstract void a(MovingObjectPosition movingobjectposition);

    @Override
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

    @Override
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
        if (this instanceof EntityEnderPearl && this.world != null && this.world.paperConfig.disableEnderpearlExploit) { this.shooterId = null; } // Paper - Don't store shooter name for pearls to block enderpearl travel exploit

    }

    @Nullable
    public EntityLiving getShooter() {
        if (this.shooter == null && this.shooterId != null && this.world instanceof WorldServer) {
            Entity entity = ((WorldServer) this.world).getEntity(this.shooterId);
            // Paper start - MC-50319 - shooter might be in another world (arrows through portals)
            if (entity == null) {
                for (WorldServer world : world.getMinecraftServer().getWorlds()) {
                    entity = world.getEntity(this.shooterId);
                    if (entity != null) {
                        break;
                    }
                }
            }
            // Paper end

            if (entity instanceof EntityLiving) {
                this.shooter = (EntityLiving) entity;
            } else {
                //this.shooterId = null; // Paper - don't unset shooterId
            }
        }

        return this.shooter;
    }

    @Override
    public Packet<?> N() {
        return new PacketPlayOutSpawnEntity(this);
    }
}
