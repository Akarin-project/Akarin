package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityPig extends EntityAnimal {

    private static final DataWatcherObject<Boolean> bC = DataWatcher.a(EntityPig.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> bD = DataWatcher.a(EntityPig.class, DataWatcherRegistry.b);
    private static final RecipeItemStack bE = RecipeItemStack.a(Items.CARROT, Items.POTATO, Items.BEETROOT);
    private boolean bG;
    private int bH;
    private int bI;

    public EntityPig(World world) {
        super(EntityTypes.PIG, world);
        this.setSize(0.9F, 0.9F);
    }

    protected void n() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.25D));
        this.goalSelector.a(3, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(4, new PathfinderGoalTempt(this, 1.2D, RecipeItemStack.a(Items.CARROT_ON_A_STICK), false));
        this.goalSelector.a(4, new PathfinderGoalTempt(this, 1.2D, false, EntityPig.bE));
        this.goalSelector.a(5, new PathfinderGoalFollowParent(this, 1.1D));
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    @Nullable
    public Entity bO() {
        return this.bP().isEmpty() ? null : (Entity) this.bP().get(0);
    }

    public boolean dh() {
        Entity entity = this.bO();

        if (!(entity instanceof EntityHuman)) {
            return false;
        } else {
            EntityHuman entityhuman = (EntityHuman) entity;

            return entityhuman.getItemInMainHand().getItem() == Items.CARROT_ON_A_STICK || entityhuman.getItemInOffHand().getItem() == Items.CARROT_ON_A_STICK;
        }
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityPig.bD.equals(datawatcherobject) && this.world.isClientSide) {
            this.bG = true;
            this.bH = 0;
            this.bI = (Integer) this.datawatcher.get(EntityPig.bD);
        }

        super.a(datawatcherobject);
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityPig.bC, false);
        this.datawatcher.register(EntityPig.bD, 0);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("Saddle", this.hasSaddle());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setSaddle(nbttagcompound.getBoolean("Saddle"));
    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_PIG_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_PIG_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_PIG_DEATH;
    }

    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_PIG_STEP, 0.15F, 1.0F);
    }

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

    public void die(DamageSource damagesource) {
        super.die(damagesource);
        if (!this.world.isClientSide) {
            if (this.hasSaddle()) {
                this.a((IMaterial) Items.SADDLE);
            }

        }
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.L;
    }

    public boolean hasSaddle() {
        return (Boolean) this.datawatcher.get(EntityPig.bC);
    }

    public void setSaddle(boolean flag) {
        if (flag) {
            this.datawatcher.set(EntityPig.bC, true);
        } else {
            this.datawatcher.set(EntityPig.bC, false);
        }

    }

    public void onLightningStrike(EntityLightning entitylightning) {
        if (!this.world.isClientSide && !this.dead) {
            EntityPigZombie entitypigzombie = new EntityPigZombie(this.world);

            entitypigzombie.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
            entitypigzombie.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            entitypigzombie.setNoAI(this.isNoAI());
            if (this.hasCustomName()) {
                entitypigzombie.setCustomName(this.getCustomName());
                entitypigzombie.setCustomNameVisible(this.getCustomNameVisible());
            }

            this.world.addEntity(entitypigzombie);
            this.die();
        }
    }

    public void a(float f, float f1, float f2) {
        Entity entity = this.bP().isEmpty() ? null : (Entity) this.bP().get(0);

        if (this.isVehicle() && this.dh()) {
            this.yaw = entity.yaw;
            this.lastYaw = this.yaw;
            this.pitch = entity.pitch * 0.5F;
            this.setYawPitch(this.yaw, this.pitch);
            this.aQ = this.yaw;
            this.aS = this.yaw;
            this.Q = 1.0F;
            this.aU = this.cK() * 0.1F;
            if (this.bG && this.bH++ > this.bI) {
                this.bG = false;
            }

            if (this.bT()) {
                float f3 = (float) this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue() * 0.225F;

                if (this.bG) {
                    f3 += f3 * 1.15F * MathHelper.sin((float) this.bH / (float) this.bI * 3.1415927F);
                }

                this.o(f3);
                super.a(0.0F, 0.0F, 1.0F);
            } else {
                this.motX = 0.0D;
                this.motY = 0.0D;
                this.motZ = 0.0D;
            }

            this.aI = this.aJ;
            double d0 = this.locX - this.lastX;
            double d1 = this.locZ - this.lastZ;
            float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

            if (f4 > 1.0F) {
                f4 = 1.0F;
            }

            this.aJ += (f4 - this.aJ) * 0.4F;
            this.aK += this.aJ;
        } else {
            this.Q = 0.5F;
            this.aU = 0.02F;
            super.a(f, f1, f2);
        }
    }

    public boolean dz() {
        if (this.bG) {
            return false;
        } else {
            this.bG = true;
            this.bH = 0;
            this.bI = this.getRandom().nextInt(841) + 140;
            this.getDataWatcher().set(EntityPig.bD, this.bI);
            return true;
        }
    }

    public EntityPig createChild(EntityAgeable entityageable) {
        return new EntityPig(this.world);
    }

    public boolean f(ItemStack itemstack) {
        return EntityPig.bE.test(itemstack);
    }
}
