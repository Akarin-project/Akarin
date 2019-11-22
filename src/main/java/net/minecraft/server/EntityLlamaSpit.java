package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class EntityLlamaSpit extends Entity implements IProjectile {

    public EntityLiving shooter; // CraftBukkit - type
    private NBTTagCompound c;

    public EntityLlamaSpit(EntityTypes<? extends EntityLlamaSpit> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityLlamaSpit(World world, EntityLlama entityllama) {
        this(EntityTypes.LLAMA_SPIT, world);
        this.shooter = entityllama;
        this.setPosition(entityllama.locX - (double) (entityllama.getWidth() + 1.0F) * 0.5D * (double) MathHelper.sin(entityllama.aK * 0.017453292F), entityllama.locY + (double) entityllama.getHeadHeight() - 0.10000000149011612D, entityllama.locZ + (double) (entityllama.getWidth() + 1.0F) * 0.5D * (double) MathHelper.cos(entityllama.aK * 0.017453292F));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.c != null) {
            this.f();
        }

        Vec3D vec3d = this.getMot();
        MovingObjectPosition movingobjectposition = ProjectileHelper.a(this, this.getBoundingBox().a(vec3d).g(1.0D), (entity) -> {
            return !entity.isSpectator() && entity != this.shooter;
        }, RayTrace.BlockCollisionOption.OUTLINE, true);

        if (movingobjectposition != null) {
            this.a(movingobjectposition);
        }

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
        float f1 = 0.99F;
        float f2 = 0.06F;

        if (!this.world.a(this.getBoundingBox(), Material.AIR)) {
            this.die();
        } else if (this.av()) {
            this.die();
        } else {
            this.setMot(vec3d.a(0.9900000095367432D));
            if (!this.isNoGravity()) {
                this.setMot(this.getMot().add(0.0D, -0.05999999865889549D, 0.0D));
            }

            this.setPosition(this.locX, this.locY, this.locZ);
        }
    }

    @Override
    public void shoot(double d0, double d1, double d2, float f, float f1) {
        Vec3D vec3d = (new Vec3D(d0, d1, d2)).d().add(this.random.nextGaussian() * 0.007499999832361937D * (double) f1, this.random.nextGaussian() * 0.007499999832361937D * (double) f1, this.random.nextGaussian() * 0.007499999832361937D * (double) f1).a((double) f);

        this.setMot(vec3d);
        float f2 = MathHelper.sqrt(b(vec3d));

        this.yaw = (float) (MathHelper.d(vec3d.x, d2) * 57.2957763671875D);
        this.pitch = (float) (MathHelper.d(vec3d.y, (double) f2) * 57.2957763671875D);
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
    }

    public void a(MovingObjectPosition movingobjectposition) {
        org.bukkit.craftbukkit.event.CraftEventFactory.callProjectileHitEvent(this, movingobjectposition); // Craftbukkit - Call event
        MovingObjectPosition.EnumMovingObjectType movingobjectposition_enummovingobjecttype = movingobjectposition.getType();

        if (movingobjectposition_enummovingobjecttype == MovingObjectPosition.EnumMovingObjectType.ENTITY && this.shooter != null) {
            ((MovingObjectPositionEntity) movingobjectposition).getEntity().damageEntity(DamageSource.a(this, (EntityLiving) this.shooter).c(), 1.0F);
        } else if (movingobjectposition_enummovingobjecttype == MovingObjectPosition.EnumMovingObjectType.BLOCK && !this.world.isClientSide) {
            this.die();
        }

    }

    @Override
    protected void initDatawatcher() {}

    @Override
    protected void a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKeyOfType("Owner", 10)) {
            this.c = nbttagcompound.getCompound("Owner");
        }

    }

    @Override
    protected void b(NBTTagCompound nbttagcompound) {
        if (this.shooter != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            UUID uuid = this.shooter.getUniqueID();

            nbttagcompound1.a("OwnerUUID", uuid);
            nbttagcompound.set("Owner", nbttagcompound1);
        }

    }

    private void f() {
        if (this.c != null && this.c.b("OwnerUUID")) {
            UUID uuid = this.c.a("OwnerUUID");
            List<EntityLlama> list = this.world.a(EntityLlama.class, this.getBoundingBox().g(15.0D));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityLlama entityllama = (EntityLlama) iterator.next();

                if (entityllama.getUniqueID().equals(uuid)) {
                    this.shooter = entityllama;
                    break;
                }
            }
        }

        this.c = null;
    }

    @Override
    public Packet<?> N() {
        return new PacketPlayOutSpawnEntity(this);
    }
}
