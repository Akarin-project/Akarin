package net.minecraft.server;

import javax.annotation.Nullable;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
// CraftBukkit end

public class EntityPig extends EntityAnimal {

    private static final DataWatcherObject<Boolean> bz = DataWatcher.a(EntityPig.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> bA = DataWatcher.a(EntityPig.class, DataWatcherRegistry.b);
    private static final RecipeItemStack bB = RecipeItemStack.a(Items.CARROT, Items.POTATO, Items.BEETROOT);
    private boolean bC;
    private int bD;
    private int bE;

    public EntityPig(EntityTypes<? extends EntityPig> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.25D));
        this.goalSelector.a(3, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(4, new PathfinderGoalTempt(this, 1.2D, RecipeItemStack.a(Items.CARROT_ON_A_STICK), false));
        this.goalSelector.a(4, new PathfinderGoalTempt(this, 1.2D, false, EntityPig.bB));
        this.goalSelector.a(5, new PathfinderGoalFollowParent(this, 1.1D));
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    @Nullable
    @Override
    public Entity getRidingPassenger() {
        return this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
    }

    @Override
    public boolean dD() {
        Entity entity = this.getRidingPassenger();

        if (!(entity instanceof EntityHuman)) {
            return false;
        } else {
            EntityHuman entityhuman = (EntityHuman) entity;

            return entityhuman.getItemInMainHand().getItem() == Items.CARROT_ON_A_STICK || entityhuman.getItemInOffHand().getItem() == Items.CARROT_ON_A_STICK;
        }
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityPig.bA.equals(datawatcherobject) && this.world.isClientSide) {
            this.bC = true;
            this.bD = 0;
            this.bE = (Integer) this.datawatcher.get(EntityPig.bA);
        }

        super.a(datawatcherobject);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityPig.bz, false);
        this.datawatcher.register(EntityPig.bA, 0);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("Saddle", this.hasSaddle());
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setSaddle(nbttagcompound.getBoolean("Saddle"));
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_PIG_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_PIG_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_PIG_DEATH;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_PIG_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        if (!super.a(entityhuman, enumhand)) {
            ItemStack itemstack = entityhuman.b(enumhand);

            if (itemstack.getItem() == Items.NAME_TAG) {
                itemstack.a(entityhuman, (EntityLiving) this, enumhand);
                return true;
            } else if (this.hasSaddle() && !this.isVehicle()) {
                if (!this.world.isClientSide) {
                    entityhuman.startRiding(this);
                }

                return true;
            } else if (itemstack.getItem() == Items.SADDLE) {
                itemstack.a(entityhuman, (EntityLiving) this, enumhand);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    protected void cF() {
        super.cF();
        if (this.hasSaddle()) {
            this.a((IMaterial) Items.SADDLE);
        }

    }

    public boolean hasSaddle() {
        return (Boolean) this.datawatcher.get(EntityPig.bz);
    }

    public void setSaddle(boolean flag) {
        if (flag) {
            this.datawatcher.set(EntityPig.bz, true);
        } else {
            this.datawatcher.set(EntityPig.bz, false);
        }

    }

    @Override
    public void onLightningStrike(EntityLightning entitylightning) {
        EntityPigZombie entitypigzombie = (EntityPigZombie) EntityTypes.ZOMBIE_PIGMAN.a(this.world);

        entitypigzombie.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
        entitypigzombie.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
        entitypigzombie.setNoAI(this.isNoAI());
        if (this.hasCustomName()) {
            entitypigzombie.setCustomName(this.getCustomName());
            entitypigzombie.setCustomNameVisible(this.getCustomNameVisible());
        }

        // Paper start
        if (CraftEventFactory.callEntityZapEvent(this, entitylightning, entitypigzombie).isCancelled()) {
            return;
        }
        // Paper end

        // CraftBukkit start
        if (CraftEventFactory.callPigZapEvent(this, entitylightning, entitypigzombie).isCancelled()) {
            return;
        }
        // CraftBukkit - added a reason for spawning this creature
        this.world.addEntity(entitypigzombie, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.LIGHTNING);
        // CraftBukkit end
        this.die();
    }

    @Override
    public void e(Vec3D vec3d) {
        if (this.isAlive()) {
            Entity entity = this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);

            if (this.isVehicle() && this.dD()) {
                this.yaw = entity.yaw;
                this.lastYaw = this.yaw;
                this.pitch = entity.pitch * 0.5F;
                this.setYawPitch(this.yaw, this.pitch);
                this.aK = this.yaw;
                this.aM = this.yaw;
                this.K = 1.0F;
                this.aO = this.db() * 0.1F;
                if (this.bC && this.bD++ > this.bE) {
                    this.bC = false;
                }

                if (this.ca()) {
                    float f = (float) this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue() * 0.225F;

                    if (this.bC) {
                        f += f * 1.15F * MathHelper.sin((float) this.bD / (float) this.bE * 3.1415927F);
                    }

                    this.o(f);
                    super.e(new Vec3D(0.0D, 0.0D, 1.0D));
                } else {
                    this.setMot(Vec3D.a);
                }

                this.aE = this.aF;
                double d0 = this.locX - this.lastX;
                double d1 = this.locZ - this.lastZ;
                float f1 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

                if (f1 > 1.0F) {
                    f1 = 1.0F;
                }

                this.aF += (f1 - this.aF) * 0.4F;
                this.aG += this.aF;
            } else {
                this.K = 0.5F;
                this.aO = 0.02F;
                super.e(vec3d);
            }
        }
    }

    public boolean dW() {
        if (this.bC) {
            return false;
        } else {
            this.bC = true;
            this.bD = 0;
            this.bE = this.getRandom().nextInt(841) + 140;
            this.getDataWatcher().set(EntityPig.bA, this.bE);
            return true;
        }
    }

    @Override
    public EntityPig createChild(EntityAgeable entityageable) {
        return (EntityPig) EntityTypes.PIG.a(this.world);
    }

    @Override
    public boolean i(ItemStack itemstack) {
        return EntityPig.bB.test(itemstack);
    }
}
