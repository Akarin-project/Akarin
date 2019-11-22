package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class EntityFireworks extends Entity implements IProjectile {

    public static final DataWatcherObject<ItemStack> FIREWORK_ITEM = DataWatcher.a(EntityFireworks.class, DataWatcherRegistry.g);
    private static final DataWatcherObject<OptionalInt> c = DataWatcher.a(EntityFireworks.class, DataWatcherRegistry.r);
    public static final DataWatcherObject<Boolean> d = DataWatcher.a(EntityFireworks.class, DataWatcherRegistry.i); // PAIL
    private int ticksFlown;
    public int expectedLifespan;
    private EntityLiving ridingEntity; public final EntityLiving getBoostedEntity() { return this.ridingEntity; } // Paper - OBFHELPER
    public UUID spawningEntity; // Paper

    public EntityFireworks(EntityTypes<? extends EntityFireworks> entitytypes, World world) {
        super(entitytypes, world);
    }

    // Spigot Start - copied from tick
    @Override
    public void inactiveTick() {
        this.ticksFlown += 1;

        if (!this.world.isClientSide && this.ticksFlown > this.expectedLifespan) {
            // CraftBukkit start
            if (!org.bukkit.craftbukkit.event.CraftEventFactory.callFireworkExplodeEvent(this).isCancelled()) {
                this.explode();
            }
            // CraftBukkit end
        }
        super.inactiveTick();
    }
    // Spigot End

    @Override
    protected void initDatawatcher() {
        this.datawatcher.register(EntityFireworks.FIREWORK_ITEM, ItemStack.a);
        this.datawatcher.register(EntityFireworks.c, OptionalInt.empty());
        this.datawatcher.register(EntityFireworks.d, false);
    }

    public EntityFireworks(World world, double d0, double d1, double d2, ItemStack itemstack) {
        super(EntityTypes.FIREWORK_ROCKET, world);
        this.ticksFlown = 0;
        this.setPosition(d0, d1, d2);
        int i = 1;

        if (!itemstack.isEmpty() && itemstack.hasTag()) {
            this.datawatcher.set(EntityFireworks.FIREWORK_ITEM, itemstack.cloneItemStack());
            i += itemstack.a("Fireworks").getByte("Flight");
        }

        this.setMot(this.random.nextGaussian() * 0.001D, 0.05D, this.random.nextGaussian() * 0.001D);
        this.expectedLifespan = 10 * i + this.random.nextInt(6) + this.random.nextInt(7);
    }

    public EntityFireworks(World world, ItemStack itemstack, EntityLiving entityliving) {
        this(world, entityliving.locX, entityliving.locY, entityliving.locZ, itemstack);
        this.datawatcher.set(EntityFireworks.c, OptionalInt.of(entityliving.getId()));
        this.ridingEntity = entityliving;
    }

    public EntityFireworks(World world, ItemStack itemstack, double d0, double d1, double d2, boolean flag) {
        this(world, d0, d1, d2, itemstack);
        this.datawatcher.set(EntityFireworks.d, flag);
    }

    @Override
    public void tick() {
        this.H = this.locX;
        this.I = this.locY;
        this.J = this.locZ;
        super.tick();
        Vec3D vec3d;

        if (this.n()) {
            if (this.ridingEntity == null) {
                ((OptionalInt) this.datawatcher.get(EntityFireworks.c)).ifPresent((i) -> {
                    Entity entity = this.world.getEntity(i);

                    if (entity instanceof EntityLiving) {
                        this.ridingEntity = (EntityLiving) entity;
                    }

                });
            }

            if (this.ridingEntity != null) {
                if (this.ridingEntity.isGliding()) {
                    vec3d = this.ridingEntity.getLookDirection();
                    double d0 = 1.5D;
                    double d1 = 0.1D;
                    Vec3D vec3d1 = this.ridingEntity.getMot();

                    this.ridingEntity.setMot(vec3d1.add(vec3d.x * 0.1D + (vec3d.x * 1.5D - vec3d1.x) * 0.5D, vec3d.y * 0.1D + (vec3d.y * 1.5D - vec3d1.y) * 0.5D, vec3d.z * 0.1D + (vec3d.z * 1.5D - vec3d1.z) * 0.5D));
                }

                this.setPosition(this.ridingEntity.locX, this.ridingEntity.locY, this.ridingEntity.locZ);
                this.setMot(this.ridingEntity.getMot());
            }
        } else {
            if (!this.i()) {
                this.setMot(this.getMot().d(1.15D, 1.0D, 1.15D).add(0.0D, 0.04D, 0.0D));
            }

            this.move(EnumMoveType.SELF, this.getMot());
        }

        vec3d = this.getMot();
        MovingObjectPosition movingobjectposition = ProjectileHelper.a(this, this.getBoundingBox().a(vec3d).g(1.0D), (entity) -> {
            return !entity.isSpectator() && entity.isAlive() && entity.isInteractable();
        }, RayTrace.BlockCollisionOption.COLLIDER, true);

        if (!this.noclip) {
            this.a(movingobjectposition);
            this.impulse = true;
        }

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
        if (this.ticksFlown == 0 && !this.isSilent()) {
            this.world.playSound((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0F, 1.0F);
        }

        ++this.ticksFlown;
        if (this.world.isClientSide && this.ticksFlown % 2 < 2) {
            this.world.addParticle(Particles.FIREWORK, this.locX, this.locY - 0.3D, this.locZ, this.random.nextGaussian() * 0.05D, -this.getMot().y * 0.5D, this.random.nextGaussian() * 0.05D);
        }

        if (!this.world.isClientSide && this.ticksFlown > this.expectedLifespan) {
            // CraftBukkit start
            if (!org.bukkit.craftbukkit.event.CraftEventFactory.callFireworkExplodeEvent(this).isCancelled()) {
                this.explode();
            }
            // CraftBukkit end
        }

    }

    private void explode() {
        this.world.broadcastEntityEffect(this, (byte) 17);
        this.m();
        this.die();
    }

    protected void a(MovingObjectPosition movingobjectposition) {
        if (movingobjectposition.getType() == MovingObjectPosition.EnumMovingObjectType.ENTITY && !this.world.isClientSide) {
            // CraftBukkit start
            if (!org.bukkit.craftbukkit.event.CraftEventFactory.callFireworkExplodeEvent(this).isCancelled()) {
                this.explode();
            }
            // CraftBukkit end
        } else if (this.z) {
            BlockPosition blockposition;

            if (movingobjectposition.getType() == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
                blockposition = new BlockPosition(((MovingObjectPositionBlock) movingobjectposition).getBlockPosition());
            } else {
                blockposition = new BlockPosition(this);
            }

            this.world.getType(blockposition).a(this.world, blockposition, (Entity) this);
            if (this.l()) {
                // CraftBukkit start
                if (!org.bukkit.craftbukkit.event.CraftEventFactory.callFireworkExplodeEvent(this).isCancelled()) {
                    this.explode();
                }
                // CraftBukkit end
            }
        }

    }

    private boolean l() {
        ItemStack itemstack = (ItemStack) this.datawatcher.get(EntityFireworks.FIREWORK_ITEM);
        NBTTagCompound nbttagcompound = itemstack.isEmpty() ? null : itemstack.b("Fireworks");
        NBTTagList nbttaglist = nbttagcompound != null ? nbttagcompound.getList("Explosions", 10) : null;

        return nbttaglist != null && !nbttaglist.isEmpty();
    }

    private void m() {
        float f = 0.0F;
        ItemStack itemstack = (ItemStack) this.datawatcher.get(EntityFireworks.FIREWORK_ITEM);
        NBTTagCompound nbttagcompound = itemstack.isEmpty() ? null : itemstack.b("Fireworks");
        NBTTagList nbttaglist = nbttagcompound != null ? nbttagcompound.getList("Explosions", 10) : null;

        if (nbttaglist != null && !nbttaglist.isEmpty()) {
            f = 5.0F + (float) (nbttaglist.size() * 2);
        }

        if (f > 0.0F) {
            if (this.ridingEntity != null) {
                CraftEventFactory.entityDamage = this; // CraftBukkit
                this.ridingEntity.damageEntity(DamageSource.FIREWORKS, 5.0F + (float) (nbttaglist.size() * 2));
                CraftEventFactory.entityDamage = null; // CraftBukkit
            }

            double d0 = 5.0D;
            Vec3D vec3d = new Vec3D(this.locX, this.locY, this.locZ);
            List<EntityLiving> list = this.world.a(EntityLiving.class, this.getBoundingBox().g(5.0D));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityLiving entityliving = (EntityLiving) iterator.next();

                if (entityliving != this.ridingEntity && this.h(entityliving) <= 25.0D) {
                    boolean flag = false;

                    for (int i = 0; i < 2; ++i) {
                        Vec3D vec3d1 = new Vec3D(entityliving.locX, entityliving.locY + (double) entityliving.getHeight() * 0.5D * (double) i, entityliving.locZ);
                        MovingObjectPositionBlock movingobjectpositionblock = this.world.rayTrace(new RayTrace(vec3d, vec3d1, RayTrace.BlockCollisionOption.COLLIDER, RayTrace.FluidCollisionOption.NONE, this));

                        if (movingobjectpositionblock.getType() == MovingObjectPosition.EnumMovingObjectType.MISS) {
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

    private boolean n() {
        return ((OptionalInt) this.datawatcher.get(EntityFireworks.c)).isPresent();
    }

    public boolean i() {
        return (Boolean) this.datawatcher.get(EntityFireworks.d);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("Life", this.ticksFlown);
        nbttagcompound.setInt("LifeTime", this.expectedLifespan);
        ItemStack itemstack = (ItemStack) this.datawatcher.get(EntityFireworks.FIREWORK_ITEM);

        if (!itemstack.isEmpty()) {
            nbttagcompound.set("FireworksItem", itemstack.save(new NBTTagCompound()));
        }

        nbttagcompound.setBoolean("ShotAtAngle", (Boolean) this.datawatcher.get(EntityFireworks.d));
        // Paper start
        if (this.spawningEntity != null) {
            nbttagcompound.setUUID("SpawningEntity", this.spawningEntity);
        }
        // Paper end
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        this.ticksFlown = nbttagcompound.getInt("Life");
        this.expectedLifespan = nbttagcompound.getInt("LifeTime");
        ItemStack itemstack = ItemStack.a(nbttagcompound.getCompound("FireworksItem"));

        if (!itemstack.isEmpty()) {
            this.datawatcher.set(EntityFireworks.FIREWORK_ITEM, itemstack);
        }

        if (nbttagcompound.hasKey("ShotAtAngle")) {
            this.datawatcher.set(EntityFireworks.d, nbttagcompound.getBoolean("ShotAtAngle"));
        }
        // Paper start
        if (nbttagcompound.hasUUID("SpawningEntity")) {
            this.spawningEntity = nbttagcompound.getUUID("SpawningEntity");
        }
        // Paper end
    }

    @Override
    public boolean bs() {
        return false;
    }

    @Override
    public Packet<?> N() {
        return new PacketPlayOutSpawnEntity(this);
    }

    @Override
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
        this.setMot(d0, d1, d2);
    }
}
