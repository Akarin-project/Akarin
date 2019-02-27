package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class EntityFireworks extends Entity {

    public static final DataWatcherObject<ItemStack> FIREWORK_ITEM = DataWatcher.a(EntityFireworks.class, DataWatcherRegistry.g);
    private static final DataWatcherObject<Integer> b = DataWatcher.a(EntityFireworks.class, DataWatcherRegistry.b);
    private int ticksFlown;
    public int expectedLifespan;
    private EntityLiving e;

    public EntityFireworks(World world) {
        super(EntityTypes.FIREWORK_ROCKET, world);
        this.setSize(0.25F, 0.25F);
    }

    protected void x_() {
        this.datawatcher.register(EntityFireworks.FIREWORK_ITEM, ItemStack.a);
        this.datawatcher.register(EntityFireworks.b, 0);
    }

    public EntityFireworks(World world, double d0, double d1, double d2, ItemStack itemstack) {
        super(EntityTypes.FIREWORK_ROCKET, world);
        this.ticksFlown = 0;
        this.setSize(0.25F, 0.25F);
        this.setPosition(d0, d1, d2);
        int i = 1;

        if (!itemstack.isEmpty() && itemstack.hasTag()) {
            this.datawatcher.set(EntityFireworks.FIREWORK_ITEM, itemstack.cloneItemStack());
            i += itemstack.a("Fireworks").getByte("Flight");
        }

        this.motX = this.random.nextGaussian() * 0.001D;
        this.motZ = this.random.nextGaussian() * 0.001D;
        this.motY = 0.05D;
        this.expectedLifespan = 10 * i + this.random.nextInt(6) + this.random.nextInt(7);
    }

    public EntityFireworks(World world, ItemStack itemstack, EntityLiving entityliving) {
        this(world, entityliving.locX, entityliving.locY, entityliving.locZ, itemstack);
        this.datawatcher.set(EntityFireworks.b, entityliving.getId());
        this.e = entityliving;
    }

    public void tick() {
        this.N = this.locX;
        this.O = this.locY;
        this.P = this.locZ;
        super.tick();
        if (this.f()) {
            if (this.e == null) {
                Entity entity = this.world.getEntity((Integer) this.datawatcher.get(EntityFireworks.b));

                if (entity instanceof EntityLiving) {
                    this.e = (EntityLiving) entity;
                }
            }

            if (this.e != null) {
                if (this.e.dc()) {
                    Vec3D vec3d = this.e.aN();
                    double d0 = 1.5D;
                    double d1 = 0.1D;

                    this.e.motX += vec3d.x * 0.1D + (vec3d.x * 1.5D - this.e.motX) * 0.5D;
                    this.e.motY += vec3d.y * 0.1D + (vec3d.y * 1.5D - this.e.motY) * 0.5D;
                    this.e.motZ += vec3d.z * 0.1D + (vec3d.z * 1.5D - this.e.motZ) * 0.5D;
                }

                this.setPosition(this.e.locX, this.e.locY, this.e.locZ);
                this.motX = this.e.motX;
                this.motY = this.e.motY;
                this.motZ = this.e.motZ;
            }
        } else {
            this.motX *= 1.15D;
            this.motZ *= 1.15D;
            this.motY += 0.04D;
            this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
        }

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
        if (this.ticksFlown == 0 && !this.isSilent()) {
            this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0F, 1.0F);
        }

        ++this.ticksFlown;
        if (this.world.isClientSide && this.ticksFlown % 2 < 2) {
            this.world.addParticle(Particles.w, this.locX, this.locY - 0.3D, this.locZ, this.random.nextGaussian() * 0.05D, -this.motY * 0.5D, this.random.nextGaussian() * 0.05D);
        }

        if (!this.world.isClientSide && this.ticksFlown > this.expectedLifespan) {
            // CraftBukkit start
            if (!org.bukkit.craftbukkit.event.CraftEventFactory.callFireworkExplodeEvent(this).isCancelled()) {
                this.world.broadcastEntityEffect(this, (byte) 17);
                this.i();
            }
            // CraftBukkit end
            this.die();
        }

    }

    private void i() {
        float f = 0.0F;
        ItemStack itemstack = (ItemStack) this.datawatcher.get(EntityFireworks.FIREWORK_ITEM);
        NBTTagCompound nbttagcompound = itemstack.isEmpty() ? null : itemstack.b("Fireworks");
        NBTTagList nbttaglist = nbttagcompound != null ? nbttagcompound.getList("Explosions", 10) : null;

        if (nbttaglist != null && !nbttaglist.isEmpty()) {
            f = (float) (5 + nbttaglist.size() * 2);
        }

        if (f > 0.0F) {
            if (this.e != null) {
                CraftEventFactory.entityDamage = this; // CraftBukkit
                this.e.damageEntity(DamageSource.FIREWORKS, (float) (5 + nbttaglist.size() * 2));
                CraftEventFactory.entityDamage = null; // CraftBukkit
            }

            double d0 = 5.0D;
            Vec3D vec3d = new Vec3D(this.locX, this.locY, this.locZ);
            List<EntityLiving> list = this.world.a(EntityLiving.class, this.getBoundingBox().g(5.0D));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityLiving entityliving = (EntityLiving) iterator.next();

                if (entityliving != this.e && this.h(entityliving) <= 25.0D) {
                    boolean flag = false;

                    for (int i = 0; i < 2; ++i) {
                        MovingObjectPosition movingobjectposition = this.world.rayTrace(vec3d, new Vec3D(entityliving.locX, entityliving.locY + (double) entityliving.length * 0.5D * (double) i, entityliving.locZ), FluidCollisionOption.NEVER, true, false);

                        if (movingobjectposition == null || movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.MISS) {
                            flag = true;
                            break;
                        }
                    }

                    if (flag) {
                        float f1 = f * (float) Math.sqrt((5.0D - (double) this.g(entityliving)) / 5.0D);

                        CraftEventFactory.entityDamage = this; // CraftBukkit
                        entityliving.damageEntity(DamageSource.FIREWORKS, f1);
                        CraftEventFactory.entityDamage = null; // CraftBukkit
                    }
                }
            }
        }

    }

    public boolean f() {
        return (Integer) this.datawatcher.get(EntityFireworks.b) > 0;
    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("Life", this.ticksFlown);
        nbttagcompound.setInt("LifeTime", this.expectedLifespan);
        ItemStack itemstack = (ItemStack) this.datawatcher.get(EntityFireworks.FIREWORK_ITEM);

        if (!itemstack.isEmpty()) {
            nbttagcompound.set("FireworksItem", itemstack.save(new NBTTagCompound()));
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        this.ticksFlown = nbttagcompound.getInt("Life");
        this.expectedLifespan = nbttagcompound.getInt("LifeTime");
        ItemStack itemstack = ItemStack.a(nbttagcompound.getCompound("FireworksItem"));

        if (!itemstack.isEmpty()) {
            this.datawatcher.set(EntityFireworks.FIREWORK_ITEM, itemstack);
        }

    }

    public boolean bk() {
        return false;
    }
}
