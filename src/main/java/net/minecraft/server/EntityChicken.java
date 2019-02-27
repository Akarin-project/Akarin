package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityChicken extends EntityAnimal {

    private static final RecipeItemStack bK = RecipeItemStack.a(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
    public float bC;
    public float bD;
    public float bE;
    public float bG;
    public float bH = 1.0F;
    public int bI;
    public boolean bJ;

    public EntityChicken(World world) {
        super(EntityTypes.CHICKEN, world);
        this.setSize(0.4F, 0.7F);
        this.bI = this.random.nextInt(6000) + 6000;
        this.a(PathType.WATER, 0.0F);
    }

    protected void n() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.4D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.0D, false, EntityChicken.bK));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.1D));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    }

    public float getHeadHeight() {
        return this.length;
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(4.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    public void movementTick() {
        super.movementTick();
        this.bG = this.bC;
        this.bE = this.bD;
        this.bD = (float) ((double) this.bD + (double) (this.onGround ? -1 : 4) * 0.3D);
        this.bD = MathHelper.a(this.bD, 0.0F, 1.0F);
        if (!this.onGround && this.bH < 1.0F) {
            this.bH = 1.0F;
        }

        this.bH = (float) ((double) this.bH * 0.9D);
        if (!this.onGround && this.motY < 0.0D) {
            this.motY *= 0.6D;
        }

        this.bC += this.bH * 2.0F;
        if (!this.world.isClientSide && !this.isBaby() && !this.isChickenJockey() && --this.bI <= 0) {
            this.a(SoundEffects.ENTITY_CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.a((IMaterial) Items.EGG);
            this.bI = this.random.nextInt(6000) + 6000;
        }

    }

    public void c(float f, float f1) {}

    protected SoundEffect D() {
        return SoundEffects.ENTITY_CHICKEN_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_CHICKEN_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_CHICKEN_DEATH;
    }

    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_CHICKEN_STEP, 0.15F, 1.0F);
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.J;
    }

    public EntityChicken createChild(EntityAgeable entityageable) {
        return new EntityChicken(this.world);
    }

    public boolean f(ItemStack itemstack) {
        return EntityChicken.bK.test(itemstack);
    }

    protected int getExpValue(EntityHuman entityhuman) {
        return this.isChickenJockey() ? 10 : super.getExpValue(entityhuman);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.bJ = nbttagcompound.getBoolean("IsChickenJockey");
        if (nbttagcompound.hasKey("EggLayTime")) {
            this.bI = nbttagcompound.getInt("EggLayTime");
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("IsChickenJockey", this.bJ);
        nbttagcompound.setInt("EggLayTime", this.bI);
    }

    public boolean isTypeNotPersistent() {
        return this.isChickenJockey() && !this.isVehicle();
    }

    public void k(Entity entity) {
        super.k(entity);
        float f = MathHelper.sin(this.aQ * 0.017453292F);
        float f1 = MathHelper.cos(this.aQ * 0.017453292F);
        float f2 = 0.1F;
        float f3 = 0.0F;

        entity.setPosition(this.locX + (double) (0.1F * f), this.locY + (double) (this.length * 0.5F) + entity.aI() + 0.0D, this.locZ - (double) (0.1F * f1));
        if (entity instanceof EntityLiving) {
            ((EntityLiving) entity).aQ = this.aQ;
        }

    }

    public boolean isChickenJockey() {
        return this.bJ;
    }

    public void s(boolean flag) {
        this.bJ = flag;
    }
}
