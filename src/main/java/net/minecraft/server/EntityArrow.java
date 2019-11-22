package net.minecraft.server;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;

// CraftBukkit start
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
// CraftBukkit end

public abstract class EntityArrow extends Entity implements IProjectile {

    private static final DataWatcherObject<Byte> ar = DataWatcher.a(EntityArrow.class, DataWatcherRegistry.a);
    protected static final DataWatcherObject<Optional<UUID>> b = DataWatcher.a(EntityArrow.class, DataWatcherRegistry.o);
    private static final DataWatcherObject<Byte> as = DataWatcher.a(EntityArrow.class, DataWatcherRegistry.a);
    @Nullable
    private IBlockData at;
    public boolean inGround;
    protected int d;
    public EntityArrow.PickupStatus fromPlayer;
    public int shake;
    public UUID shooter;
    public int despawnCounter;
    private int av;
    private double damage;
    public int knockbackStrength;
    private SoundEffect ay;
    private IntOpenHashSet az;
    private List<Entity> aA;

    // Spigot Start
    @Override
    public void inactiveTick()
    {
        if ( this.inGround )
        {
            this.despawnCounter += 1;
        }
        super.inactiveTick();
    }
    // Spigot End

    protected EntityArrow(EntityTypes<? extends EntityArrow> entitytypes, World world) {
        super(entitytypes, world);
        this.fromPlayer = EntityArrow.PickupStatus.DISALLOWED;
        this.damage = 2.0D;
        this.ay = this.k();
    }

    protected EntityArrow(EntityTypes<? extends EntityArrow> entitytypes, double d0, double d1, double d2, World world) {
        this(entitytypes, world);
        this.setPosition(d0, d1, d2);
    }

    protected EntityArrow(EntityTypes<? extends EntityArrow> entitytypes, EntityLiving entityliving, World world) {
        this(entitytypes, entityliving.locX, entityliving.locY + (double) entityliving.getHeadHeight() - 0.10000000149011612D, entityliving.locZ, world);
        this.setShooter(entityliving);
        if (entityliving instanceof EntityHuman) {
            this.fromPlayer = EntityArrow.PickupStatus.ALLOWED;
        }

    }

    public void a(SoundEffect soundeffect) {
        this.ay = soundeffect;
    }

    @Override
    protected void initDatawatcher() {
        this.datawatcher.register(EntityArrow.ar, (byte) 0);
        this.datawatcher.register(EntityArrow.b, Optional.empty());
        this.datawatcher.register(EntityArrow.as, (byte) 0);
    }

    public void a(Entity entity, float f, float f1, float f2, float f3, float f4) {
        float f5 = -MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f * 0.017453292F);
        float f6 = -MathHelper.sin(f * 0.017453292F);
        float f7 = MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f * 0.017453292F);

        this.shoot((double) f5, (double) f6, (double) f7, f3, f4);
        if (!entity.world.paperConfig.disableRelativeProjectileVelocity) this.setMot(this.getMot().add(entity.getMot().x, entity.onGround ? 0.0D : entity.getMot().y, entity.getMot().z)); // Paper - allow disabling relative velocity
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
        this.despawnCounter = 0;
    }

    @Override
    public void tick() {
        super.tick();
        boolean flag = this.v();
        Vec3D vec3d = this.getMot();

        if (this.lastPitch == 0.0F && this.lastYaw == 0.0F) {
            float f = MathHelper.sqrt(b(vec3d));

            this.yaw = (float) (MathHelper.d(vec3d.x, vec3d.z) * 57.2957763671875D);
            this.pitch = (float) (MathHelper.d(vec3d.y, (double) f) * 57.2957763671875D);
            this.lastYaw = this.yaw;
            this.lastPitch = this.pitch;
        }

        BlockPosition blockposition = new BlockPosition(this.locX, this.locY, this.locZ);
        IBlockData iblockdata = this.world.getType(blockposition);

        if (!iblockdata.isAir() && !flag) {
            VoxelShape voxelshape = iblockdata.getCollisionShape(this.world, blockposition);

            if (!voxelshape.isEmpty()) {
                Iterator iterator = voxelshape.d().iterator();

                while (iterator.hasNext()) {
                    AxisAlignedBB axisalignedbb = (AxisAlignedBB) iterator.next();

                    if (axisalignedbb.a(blockposition).c(new Vec3D(this.locX, this.locY, this.locZ))) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }

        if (this.shake > 0) {
            --this.shake;
        }

        if (this.isInWaterOrRain()) {
            this.extinguish();
        }

        if (this.inGround && !flag) {
            if (this.at != iblockdata && this.world.c(this.getBoundingBox().g(0.06D))) {
                this.inGround = false;
                this.setMot(vec3d.d((double) (this.random.nextFloat() * 0.2F), (double) (this.random.nextFloat() * 0.2F), (double) (this.random.nextFloat() * 0.2F)));
                this.despawnCounter = 0;
                this.av = 0;
            } else if (!this.world.isClientSide) {
                this.i();
            }

            ++this.d;
        } else {
            this.d = 0;
            ++this.av;
            Vec3D vec3d1 = new Vec3D(this.locX, this.locY, this.locZ);
            Vec3D vec3d2 = vec3d1.e(vec3d);
            Object object = this.world.rayTrace(new RayTrace(vec3d1, vec3d2, RayTrace.BlockCollisionOption.COLLIDER, RayTrace.FluidCollisionOption.NONE, this));

            if (((MovingObjectPosition) object).getType() != MovingObjectPosition.EnumMovingObjectType.MISS) {
                vec3d2 = ((MovingObjectPosition) object).getPos();
            }

            while (!this.dead) {
                MovingObjectPositionEntity movingobjectpositionentity = this.a(vec3d1, vec3d2);

                if (movingobjectpositionentity != null) {
                    object = movingobjectpositionentity;
                }

                if (object != null && ((MovingObjectPosition) object).getType() == MovingObjectPosition.EnumMovingObjectType.ENTITY) {
                    Entity entity = ((MovingObjectPositionEntity) object).getEntity();
                    Entity entity1 = this.getShooter();

                    if (entity instanceof EntityHuman && entity1 instanceof EntityHuman && !((EntityHuman) entity1).a((EntityHuman) entity)) {
                        object = null;
                        movingobjectpositionentity = null;
                    }
                }

                // Paper start - Call ProjectileCollideEvent
                // TODO: flag - noclip - call cancelled?
                if (object instanceof MovingObjectPositionEntity) {
                    com.destroystokyo.paper.event.entity.ProjectileCollideEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callProjectileCollideEvent(this, (MovingObjectPositionEntity)object);
                    if (event.isCancelled()) {
                        object = null;
                        movingobjectpositionentity = null;
                    }
                }
                // Paper end

                if (object != null && !flag) {
                    this.a((MovingObjectPosition) object);
                    this.impulse = true;
                }

                if (movingobjectpositionentity == null || this.getPierceLevel() <= 0) {
                    break;
                }

                object = null;
            }

            vec3d = this.getMot();
            double d0 = vec3d.x;
            double d1 = vec3d.y;
            double d2 = vec3d.z;

            if (this.isCritical()) {
                for (int i = 0; i < 4; ++i) {
                    this.world.addParticle(Particles.CRIT, this.locX + d0 * (double) i / 4.0D, this.locY + d1 * (double) i / 4.0D, this.locZ + d2 * (double) i / 4.0D, -d0, -d1 + 0.2D, -d2);
                }
            }

            this.locX += d0;
            this.locY += d1;
            this.locZ += d2;
            float f1 = MathHelper.sqrt(b(vec3d));

            if (flag) {
                this.yaw = (float) (MathHelper.d(-d0, -d2) * 57.2957763671875D);
            } else {
                this.yaw = (float) (MathHelper.d(d0, d2) * 57.2957763671875D);
            }

            for (this.pitch = (float) (MathHelper.d(d1, (double) f1) * 57.2957763671875D); this.pitch - this.lastPitch < -180.0F; this.lastPitch -= 360.0F) {
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
            float f2 = 0.99F;
            float f3 = 0.05F;

            if (this.isInWater()) {
                for (int j = 0; j < 4; ++j) {
                    float f4 = 0.25F;

                    this.world.addParticle(Particles.BUBBLE, this.locX - d0 * 0.25D, this.locY - d1 * 0.25D, this.locZ - d2 * 0.25D, d0, d1, d2);
                }

                f2 = this.u();
            }

            this.setMot(vec3d.a((double) f2));
            if (!this.isNoGravity() && !flag) {
                Vec3D vec3d3 = this.getMot();

                this.setMot(vec3d3.x, vec3d3.y - 0.05000000074505806D, vec3d3.z);
            }

            this.setPosition(this.locX, this.locY, this.locZ);
            this.checkBlockCollisions();
        }
    }

    protected void i() {
        ++this.despawnCounter;
        if (this.despawnCounter >= (fromPlayer == PickupStatus.CREATIVE_ONLY ? world.paperConfig.creativeArrowDespawnRate : (fromPlayer == PickupStatus.DISALLOWED ? world.paperConfig.nonPlayerArrowDespawnRate : world.spigotConfig.arrowDespawnRate))) { // Spigot // Paper
            this.die();
        }

    }

    protected void a(MovingObjectPosition movingobjectposition) {
        org.bukkit.craftbukkit.event.CraftEventFactory.callProjectileHitEvent(this, movingobjectposition); // CraftBukkit - Call event
        MovingObjectPosition.EnumMovingObjectType movingobjectposition_enummovingobjecttype = movingobjectposition.getType();

        if (movingobjectposition_enummovingobjecttype == MovingObjectPosition.EnumMovingObjectType.ENTITY) {
            this.a((MovingObjectPositionEntity) movingobjectposition);
        } else if (movingobjectposition_enummovingobjecttype == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
            MovingObjectPositionBlock movingobjectpositionblock = (MovingObjectPositionBlock) movingobjectposition;
            IBlockData iblockdata = this.world.getType(movingobjectpositionblock.getBlockPosition());

            this.at = iblockdata;
            Vec3D vec3d = movingobjectpositionblock.getPos().a(this.locX, this.locY, this.locZ);

            this.setMot(vec3d);
            Vec3D vec3d1 = vec3d.d().a(0.05000000074505806D);

            this.locX -= vec3d1.x;
            this.locY -= vec3d1.y;
            this.locZ -= vec3d1.z;
            this.a(this.getSoundHit(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            this.inGround = true;
            this.shake = 7;
            this.setCritical(false);
            this.setPierceLevel((byte) 0);
            this.a(SoundEffects.ENTITY_ARROW_HIT);
            this.o(false);
            this.w();
            iblockdata.a(this.world, iblockdata, movingobjectpositionblock, this);
        }

    }

    private void w() {
        if (this.aA != null) {
            this.aA.clear();
        }

        if (this.az != null) {
            this.az.clear();
        }

    }

    protected void a(MovingObjectPositionEntity movingobjectpositionentity) {
        Entity entity = movingobjectpositionentity.getEntity();
        float f = (float) this.getMot().f();
        int i = MathHelper.f(Math.max((double) f * this.damage, 0.0D));

        if (this.getPierceLevel() > 0) {
            if (this.az == null) {
                this.az = new IntOpenHashSet(5);
            }

            if (this.aA == null) {
                this.aA = Lists.newArrayListWithCapacity(5);
            }

            if (this.az.size() >= this.getPierceLevel() + 1) {
                this.die();
                return;
            }

            this.az.add(entity.getId());
        }

        if (this.isCritical()) {
            i += this.random.nextInt(i / 2 + 2);
        }

        Entity entity1 = this.getShooter();
        DamageSource damagesource;

        if (entity1 == null) {
            damagesource = DamageSource.arrow(this, this);
        } else {
            damagesource = DamageSource.arrow(this, entity1);
            if (entity1 instanceof EntityLiving) {
                ((EntityLiving) entity1).z(entity);
            }
        }

        int j = entity.ad();

        if (this.isBurning() && !(entity instanceof EntityEnderman)) {
            // CraftBukkit start
            EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), 5);
            org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);
            if (!combustEvent.isCancelled()) {
                entity.setOnFire(combustEvent.getDuration(), false);
            }
            // CraftBukkit end
        }

        if (entity.damageEntity(damagesource, (float) i)) {
            if (entity instanceof EntityLiving) {
                EntityLiving entityliving = (EntityLiving) entity;

                if (!this.world.isClientSide && this.getPierceLevel() <= 0) {
                    entityliving.setArrowCount(entityliving.getArrowCount() + 1);
                }

                if (this.knockbackStrength > 0) {
                    Vec3D vec3d = this.getMot().d(1.0D, 0.0D, 1.0D).d().a((double) this.knockbackStrength * 0.6D);

                    if (vec3d.g() > 0.0D) {
                        entityliving.f(vec3d.x, 0.1D, vec3d.z);
                    }
                }

                if (!this.world.isClientSide && entity1 instanceof EntityLiving) {
                    EnchantmentManager.a(entityliving, entity1);
                    EnchantmentManager.b((EntityLiving) entity1, (Entity) entityliving);
                }

                this.a(entityliving);
                if (entity1 != null && entityliving != entity1 && entityliving instanceof EntityHuman && entity1 instanceof EntityPlayer) {
                    ((EntityPlayer) entity1).playerConnection.sendPacket(new PacketPlayOutGameStateChange(6, 0.0F));
                }

                if (!entity.isAlive() && this.aA != null) {
                    this.aA.add(entityliving);
                }

                if (!this.world.isClientSide && entity1 instanceof EntityPlayer) {
                    EntityPlayer entityplayer = (EntityPlayer) entity1;

                    if (this.aA != null && this.r()) {
                        CriterionTriggers.G.a(entityplayer, this.aA, this.aA.size());
                    } else if (!entity.isAlive() && this.r()) {
                        CriterionTriggers.G.a(entityplayer, Arrays.asList(entity), 0);
                    }
                }
            }

            this.a(this.ay, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            if (this.getPierceLevel() <= 0 && !(entity instanceof EntityEnderman)) {
                this.die();
            }
        } else {
            entity.g(j);
            this.setMot(this.getMot().a(-0.1D));
            this.yaw += 180.0F;
            this.lastYaw += 180.0F;
            this.av = 0;
            if (!this.world.isClientSide && this.getMot().g() < 1.0E-7D) {
                if (this.fromPlayer == EntityArrow.PickupStatus.ALLOWED) {
                    this.a(this.getItemStack(), 0.1F);
                }

                this.die();
            }
        }

    }

    protected SoundEffect k() {
        return SoundEffects.ENTITY_ARROW_HIT;
    }

    protected final SoundEffect getSoundHit() {
        return this.ay;
    }

    protected void a(EntityLiving entityliving) {}

    @Nullable
    protected MovingObjectPositionEntity a(Vec3D vec3d, Vec3D vec3d1) {
        return ProjectileHelper.a(this.world, this, vec3d, vec3d1, this.getBoundingBox().a(this.getMot()).g(1.0D), (entity) -> {
            return !entity.isSpectator() && entity.isAlive() && entity.isInteractable() && (entity != this.getShooter() || this.av >= 5) && (this.az == null || !this.az.contains(entity.getId()));
        });
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("life", (short) this.despawnCounter);
        if (this.at != null) {
            nbttagcompound.set("inBlockState", GameProfileSerializer.a(this.at));
        }

        nbttagcompound.setByte("shake", (byte) this.shake);
        nbttagcompound.setByte("inGround", (byte) (this.inGround ? 1 : 0));
        nbttagcompound.setByte("pickup", (byte) this.fromPlayer.ordinal());
        nbttagcompound.setDouble("damage", this.damage);
        nbttagcompound.setBoolean("crit", this.isCritical());
        nbttagcompound.setByte("PierceLevel", this.getPierceLevel());
        if (this.shooter != null) {
            nbttagcompound.a("OwnerUUID", this.shooter);
        }

        nbttagcompound.setString("SoundEvent", IRegistry.SOUND_EVENT.getKey(this.ay).toString());
        nbttagcompound.setBoolean("ShotFromCrossbow", this.r());
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        this.despawnCounter = nbttagcompound.getShort("life");
        if (nbttagcompound.hasKeyOfType("inBlockState", 10)) {
            this.at = GameProfileSerializer.d(nbttagcompound.getCompound("inBlockState"));
        }

        this.shake = nbttagcompound.getByte("shake") & 255;
        this.inGround = nbttagcompound.getByte("inGround") == 1;
        if (nbttagcompound.hasKeyOfType("damage", 99)) {
            this.damage = nbttagcompound.getDouble("damage");
        }

        if (nbttagcompound.hasKeyOfType("pickup", 99)) {
            this.fromPlayer = EntityArrow.PickupStatus.a(nbttagcompound.getByte("pickup"));
        } else if (nbttagcompound.hasKeyOfType("player", 99)) {
            this.fromPlayer = nbttagcompound.getBoolean("player") ? EntityArrow.PickupStatus.ALLOWED : EntityArrow.PickupStatus.DISALLOWED;
        }

        this.setCritical(nbttagcompound.getBoolean("crit"));
        this.setPierceLevel(nbttagcompound.getByte("PierceLevel"));
        if (nbttagcompound.b("OwnerUUID")) {
            this.shooter = nbttagcompound.a("OwnerUUID");
        }

        if (nbttagcompound.hasKeyOfType("SoundEvent", 8)) {
            this.ay = (SoundEffect) IRegistry.SOUND_EVENT.getOptional(new MinecraftKey(nbttagcompound.getString("SoundEvent"))).orElse(this.k());
        }

        this.o(nbttagcompound.getBoolean("ShotFromCrossbow"));
    }

    public void setShooter(@Nullable Entity entity) {
        this.shooter = entity == null ? null : entity.getUniqueID();
        this.projectileSource = entity == null ? null : (LivingEntity) entity.getBukkitEntity(); // CraftBukkit
        if (entity instanceof EntityHuman) {
            this.fromPlayer = ((EntityHuman) entity).abilities.canInstantlyBuild ? EntityArrow.PickupStatus.CREATIVE_ONLY : EntityArrow.PickupStatus.ALLOWED;
        }

    }

    @Nullable
    public Entity getShooter() {
        return this.shooter != null && this.world instanceof WorldServer ? ((WorldServer) this.world).getEntity(this.shooter) : null;
    }

    @Override
    public void pickup(EntityHuman entityhuman) {
        if (!this.world.isClientSide && (this.inGround || this.v()) && this.shake <= 0) {
            // CraftBukkit start
            ItemStack itemstack = this.getItemStack();
            if (this.fromPlayer == PickupStatus.ALLOWED && !itemstack.isEmpty() && entityhuman.inventory.canHold(itemstack) > 0) {
                EntityItem item = new EntityItem(this.world, this.locX, this.locY, this.locZ, itemstack);
                PlayerPickupArrowEvent event = new PlayerPickupArrowEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), new org.bukkit.craftbukkit.entity.CraftItem(this.world.getServer(), this, item), (org.bukkit.entity.AbstractArrow) this.getBukkitEntity());
                // event.setCancelled(!entityhuman.canPickUpLoot); TODO
                this.world.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return;
                }
                itemstack = item.getItemStack();
            }
            boolean flag = this.fromPlayer == EntityArrow.PickupStatus.ALLOWED || this.fromPlayer == EntityArrow.PickupStatus.CREATIVE_ONLY && entityhuman.abilities.canInstantlyBuild || this.v() && this.getShooter().getUniqueID() == entityhuman.getUniqueID();

            if (this.fromPlayer == EntityArrow.PickupStatus.ALLOWED && !entityhuman.inventory.pickup(itemstack)) {
                // CraftBukkit end
                flag = false;
            }

            if (flag) {
                entityhuman.receive(this, 1);
                this.die();
            }

        }
    }

    protected abstract ItemStack getItemStack();

    @Override
    protected boolean playStepSound() {
        return false;
    }

    public void setDamage(double d0) {
        this.damage = d0;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setKnockbackStrength(int i) {
        this.knockbackStrength = i;
    }

    @Override
    public boolean bs() {
        return false;
    }

    @Override
    protected float getHeadHeight(EntityPose entitypose, EntitySize entitysize) {
        return 0.0F;
    }

    public void setCritical(boolean flag) {
        this.a(1, flag);
    }

    public void setPierceLevel(byte b0) {
        this.datawatcher.set(EntityArrow.as, b0);
    }

    private void a(int i, boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityArrow.ar);

        if (flag) {
            this.datawatcher.set(EntityArrow.ar, (byte) (b0 | i));
        } else {
            this.datawatcher.set(EntityArrow.ar, (byte) (b0 & ~i));
        }

    }

    public boolean isCritical() {
        byte b0 = (Byte) this.datawatcher.get(EntityArrow.ar);

        return (b0 & 1) != 0;
    }

    public boolean r() {
        byte b0 = (Byte) this.datawatcher.get(EntityArrow.ar);

        return (b0 & 4) != 0;
    }

    public byte getPierceLevel() {
        return (Byte) this.datawatcher.get(EntityArrow.as);
    }

    public void a(EntityLiving entityliving, float f) {
        int i = EnchantmentManager.a(Enchantments.ARROW_DAMAGE, entityliving);
        int j = EnchantmentManager.a(Enchantments.ARROW_KNOCKBACK, entityliving);

        this.setDamage((double) (f * 2.0F) + this.random.nextGaussian() * 0.25D + (double) ((float) this.world.getDifficulty().a() * 0.11F));
        if (i > 0) {
            this.setDamage(this.getDamage() + (double) i * 0.5D + 0.5D);
        }

        if (j > 0) {
            this.setKnockbackStrength(j);
        }

        if (EnchantmentManager.a(Enchantments.ARROW_FIRE, entityliving) > 0) {
            this.setOnFire(100);
        }

    }

    protected float u() {
        return 0.6F;
    }

    public void n(boolean flag) {
        this.noclip = flag;
        this.a(2, flag);
    }

    public boolean v() {
        return !this.world.isClientSide ? this.noclip : ((Byte) this.datawatcher.get(EntityArrow.ar) & 2) != 0;
    }

    public void o(boolean flag) {
        this.a(4, flag);
    }

    @Override
    public Packet<?> N() {
        Entity entity = this.getShooter();

        return new PacketPlayOutSpawnEntity(this, entity == null ? 0 : entity.getId());
    }

    public static enum PickupStatus {

        DISALLOWED, ALLOWED, CREATIVE_ONLY;

        private PickupStatus() {}

        public static EntityArrow.PickupStatus a(int i) {
            if (i < 0 || i > values().length) {
                i = 0;
            }

            return values()[i];
        }
    }
}
