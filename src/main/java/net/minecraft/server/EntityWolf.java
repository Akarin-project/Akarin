package net.minecraft.server;

import java.util.UUID;
import javax.annotation.Nullable;

public class EntityWolf extends EntityTameableAnimal {

    private static final DataWatcherObject<Float> DATA_HEALTH = DataWatcher.a(EntityWolf.class, DataWatcherRegistry.c);
    private static final DataWatcherObject<Boolean> bH = DataWatcher.a(EntityWolf.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> bI = DataWatcher.a(EntityWolf.class, DataWatcherRegistry.b);
    private float bJ;
    private float bK;
    private boolean bL;
    private boolean bM;
    private float bN;
    private float bO;

    public EntityWolf(World world) {
        super(EntityTypes.WOLF, world);
        this.setSize(0.6F, 0.85F);
        this.setTamed(false);
    }

    protected void n() {
        this.goalSit = new PathfinderGoalSit(this);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, this.goalSit);
        this.goalSelector.a(3, new EntityWolf.a<>(this, EntityLlama.class, 24.0F, 1.5D, 1.5D));
        this.goalSelector.a(4, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.a(5, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.a(6, new PathfinderGoalFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.a(7, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(8, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(9, new PathfinderGoalBeg(this, 8.0F));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(10, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalOwnerHurtByTarget(this));
        this.targetSelector.a(2, new PathfinderGoalOwnerHurtTarget(this));
        this.targetSelector.a(3, new PathfinderGoalHurtByTarget(this, true, new Class[0]));
        this.targetSelector.a(4, new PathfinderGoalRandomTargetNonTamed<>(this, EntityAnimal.class, false, (entityanimal) -> {
            return entityanimal instanceof EntitySheep || entityanimal instanceof EntityRabbit;
        }));
        this.targetSelector.a(4, new PathfinderGoalRandomTargetNonTamed<>(this, EntityTurtle.class, false, EntityTurtle.bC));
        this.targetSelector.a(5, new PathfinderGoalNearestAttackableTarget<>(this, EntitySkeletonAbstract.class, false));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30000001192092896D);
        if (this.isTamed()) {
            this.getAttributeInstance(GenericAttributes.maxHealth).setValue(20.0D);
        } else {
            this.getAttributeInstance(GenericAttributes.maxHealth).setValue(8.0D);
        }

        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE).setValue(2.0D);
    }

    public void setGoalTarget(@Nullable EntityLiving entityliving) {
        super.setGoalTarget(entityliving);
        if (entityliving == null) {
            this.setAngry(false);
        } else if (!this.isTamed()) {
            this.setAngry(true);
        }

    }

    protected void mobTick() {
        this.datawatcher.set(EntityWolf.DATA_HEALTH, this.getHealth());
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityWolf.DATA_HEALTH, this.getHealth());
        this.datawatcher.register(EntityWolf.bH, false);
        this.datawatcher.register(EntityWolf.bI, EnumColor.RED.getColorIndex());
    }

    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_WOLF_STEP, 0.15F, 1.0F);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("Angry", this.isAngry());
        nbttagcompound.setByte("CollarColor", (byte) this.getCollarColor().getColorIndex());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setAngry(nbttagcompound.getBoolean("Angry"));
        if (nbttagcompound.hasKeyOfType("CollarColor", 99)) {
            this.setCollarColor(EnumColor.fromColorIndex(nbttagcompound.getInt("CollarColor")));
        }

    }

    protected SoundEffect D() {
        return this.isAngry() ? SoundEffects.ENTITY_WOLF_GROWL : (this.random.nextInt(3) == 0 ? (this.isTamed() && (Float) this.datawatcher.get(EntityWolf.DATA_HEALTH) < 10.0F ? SoundEffects.ENTITY_WOLF_WHINE : SoundEffects.ENTITY_WOLF_PANT) : SoundEffects.ENTITY_WOLF_AMBIENT);
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_WOLF_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_WOLF_DEATH;
    }

    protected float cD() {
        return 0.4F;
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.U;
    }

    public void movementTick() {
        super.movementTick();
        if (!this.world.isClientSide && this.bL && !this.bM && !this.dr() && this.onGround) {
            this.bM = true;
            this.bN = 0.0F;
            this.bO = 0.0F;
            this.world.broadcastEntityEffect(this, (byte) 8);
        }

        if (!this.world.isClientSide && this.getGoalTarget() == null && this.isAngry()) {
            this.setAngry(false);
        }

    }

    public void tick() {
        super.tick();
        this.bK = this.bJ;
        if (this.dL()) {
            this.bJ += (1.0F - this.bJ) * 0.4F;
        } else {
            this.bJ += (0.0F - this.bJ) * 0.4F;
        }

        if (this.ap()) {
            this.bL = true;
            this.bM = false;
            this.bN = 0.0F;
            this.bO = 0.0F;
        } else if ((this.bL || this.bM) && this.bM) {
            if (this.bN == 0.0F) {
                this.a(SoundEffects.ENTITY_WOLF_SHAKE, this.cD(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }

            this.bO = this.bN;
            this.bN += 0.05F;
            if (this.bO >= 2.0F) {
                this.bL = false;
                this.bM = false;
                this.bO = 0.0F;
                this.bN = 0.0F;
            }

            if (this.bN > 0.4F) {
                float f = (float) this.getBoundingBox().minY;
                int i = (int) (MathHelper.sin((this.bN - 0.4F) * 3.1415927F) * 7.0F);

                for (int j = 0; j < i; ++j) {
                    float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                    float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;

                    this.world.addParticle(Particles.R, this.locX + (double) f1, (double) (f + 0.8F), this.locZ + (double) f2, this.motX, this.motY, this.motZ);
                }
            }
        }

    }

    public float getHeadHeight() {
        return this.length * 0.8F;
    }

    public int K() {
        return this.isSitting() ? 20 : super.K();
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            Entity entity = damagesource.getEntity();

            if (this.goalSit != null) {
                this.goalSit.setSitting(false);
            }

            if (entity != null && !(entity instanceof EntityHuman) && !(entity instanceof EntityArrow)) {
                f = (f + 1.0F) / 2.0F;
            }

            return super.damageEntity(damagesource, f);
        }
    }

    public boolean B(Entity entity) {
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), (float) ((int) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue()));

        if (flag) {
            this.a((EntityLiving) this, entity);
        }

        return flag;
    }

    public void setTamed(boolean flag) {
        super.setTamed(flag);
        if (flag) {
            this.getAttributeInstance(GenericAttributes.maxHealth).setValue(20.0D);
        } else {
            this.getAttributeInstance(GenericAttributes.maxHealth).setValue(8.0D);
        }

        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(4.0D);
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        Item item = itemstack.getItem();

        if (this.isTamed()) {
            if (!itemstack.isEmpty()) {
                if (item instanceof ItemFood) {
                    ItemFood itemfood = (ItemFood) item;

                    if (itemfood.d() && (Float) this.datawatcher.get(EntityWolf.DATA_HEALTH) < 20.0F) {
                        if (!entityhuman.abilities.canInstantlyBuild) {
                            itemstack.subtract(1);
                        }

                        this.heal((float) itemfood.getNutrition(itemstack));
                        return true;
                    }
                } else if (item instanceof ItemDye) {
                    EnumColor enumcolor = ((ItemDye) item).d();

                    if (enumcolor != this.getCollarColor()) {
                        this.setCollarColor(enumcolor);
                        if (!entityhuman.abilities.canInstantlyBuild) {
                            itemstack.subtract(1);
                        }

                        return true;
                    }
                }
            }

            if (this.f((EntityLiving) entityhuman) && !this.world.isClientSide && !this.f(itemstack)) {
                this.goalSit.setSitting(!this.isSitting());
                this.bg = false;
                this.navigation.q();
                this.setGoalTarget((EntityLiving) null);
            }
        } else if (item == Items.BONE && !this.isAngry()) {
            if (!entityhuman.abilities.canInstantlyBuild) {
                itemstack.subtract(1);
            }

            if (!this.world.isClientSide) {
                if (this.random.nextInt(3) == 0) {
                    this.c(entityhuman);
                    this.navigation.q();
                    this.setGoalTarget((EntityLiving) null);
                    this.goalSit.setSitting(true);
                    this.setHealth(20.0F);
                    this.s(true);
                    this.world.broadcastEntityEffect(this, (byte) 7);
                } else {
                    this.s(false);
                    this.world.broadcastEntityEffect(this, (byte) 6);
                }
            }

            return true;
        }

        return super.a(entityhuman, enumhand);
    }

    public boolean f(ItemStack itemstack) {
        Item item = itemstack.getItem();

        return item instanceof ItemFood && ((ItemFood) item).d();
    }

    public int dg() {
        return 8;
    }

    public boolean isAngry() {
        return ((Byte) this.datawatcher.get(EntityWolf.bC) & 2) != 0;
    }

    public void setAngry(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityWolf.bC);

        if (flag) {
            this.datawatcher.set(EntityWolf.bC, (byte) (b0 | 2));
        } else {
            this.datawatcher.set(EntityWolf.bC, (byte) (b0 & -3));
        }

    }

    public EnumColor getCollarColor() {
        return EnumColor.fromColorIndex((Integer) this.datawatcher.get(EntityWolf.bI));
    }

    public void setCollarColor(EnumColor enumcolor) {
        this.datawatcher.set(EntityWolf.bI, enumcolor.getColorIndex());
    }

    public EntityWolf createChild(EntityAgeable entityageable) {
        EntityWolf entitywolf = new EntityWolf(this.world);
        UUID uuid = this.getOwnerUUID();

        if (uuid != null) {
            entitywolf.setOwnerUUID(uuid);
            entitywolf.setTamed(true);
        }

        return entitywolf;
    }

    public void w(boolean flag) {
        this.datawatcher.set(EntityWolf.bH, flag);
    }

    public boolean mate(EntityAnimal entityanimal) {
        if (entityanimal == this) {
            return false;
        } else if (!this.isTamed()) {
            return false;
        } else if (!(entityanimal instanceof EntityWolf)) {
            return false;
        } else {
            EntityWolf entitywolf = (EntityWolf) entityanimal;

            return !entitywolf.isTamed() ? false : (entitywolf.isSitting() ? false : this.isInLove() && entitywolf.isInLove());
        }
    }

    public boolean dL() {
        return (Boolean) this.datawatcher.get(EntityWolf.bH);
    }

    public boolean a(EntityLiving entityliving, EntityLiving entityliving1) {
        if (!(entityliving instanceof EntityCreeper) && !(entityliving instanceof EntityGhast)) {
            if (entityliving instanceof EntityWolf) {
                EntityWolf entitywolf = (EntityWolf) entityliving;

                if (entitywolf.isTamed() && entitywolf.getOwner() == entityliving1) {
                    return false;
                }
            }

            return entityliving instanceof EntityHuman && entityliving1 instanceof EntityHuman && !((EntityHuman) entityliving1).a((EntityHuman) entityliving) ? false : !(entityliving instanceof EntityHorseAbstract) || !((EntityHorseAbstract) entityliving).isTamed();
        } else {
            return false;
        }
    }

    public boolean a(EntityHuman entityhuman) {
        return !this.isAngry() && super.a(entityhuman);
    }

    class a<T extends Entity> extends PathfinderGoalAvoidTarget<T> {

        private final EntityWolf d;

        public a(EntityWolf entitywolf, Class oclass, float f, double d0, double d1) {
            super(entitywolf, oclass, f, d0, d1);
            this.d = entitywolf;
        }

        public boolean a() {
            return super.a() && this.b instanceof EntityLlama ? !this.d.isTamed() && this.a((EntityLlama) this.b) : false;
        }

        private boolean a(EntityLlama entityllama) {
            return entityllama.getStrength() >= EntityWolf.this.random.nextInt(5);
        }

        public void c() {
            EntityWolf.this.setGoalTarget((EntityLiving) null);
            super.c();
        }

        public void e() {
            EntityWolf.this.setGoalTarget((EntityLiving) null);
            super.e();
        }
    }
}
