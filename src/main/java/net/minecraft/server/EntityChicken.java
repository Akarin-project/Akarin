package net.minecraft.server;

public class EntityChicken extends EntityAnimal {

    private static final RecipeItemStack bG = RecipeItemStack.a(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
    public float bz;
    public float bA;
    public float bB;
    public float bC;
    public float bD = 1.0F;
    public int eggLayTime;
    public boolean bF;

    public EntityChicken(EntityTypes<? extends EntityChicken> entitytypes, World world) {
        super(entitytypes, world);
        this.eggLayTime = this.random.nextInt(6000) + 6000;
        this.a(PathType.WATER, 0.0F);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.4D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.0D, false, EntityChicken.bG));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.1D));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return this.isBaby() ? entitysize.height * 0.85F : entitysize.height * 0.92F;
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(4.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    @Override
    public void movementTick() {
        // CraftBukkit start
        if (this.isChickenJockey()) {
            this.persistent = !this.isTypeNotPersistent(0);
        }
        // CraftBukkit end
        super.movementTick();
        this.bC = this.bz;
        this.bB = this.bA;
        this.bA = (float) ((double) this.bA + (double) (this.onGround ? -1 : 4) * 0.3D);
        this.bA = MathHelper.a(this.bA, 0.0F, 1.0F);
        if (!this.onGround && this.bD < 1.0F) {
            this.bD = 1.0F;
        }

        this.bD = (float) ((double) this.bD * 0.9D);
        Vec3D vec3d = this.getMot();

        if (!this.onGround && vec3d.y < 0.0D) {
            this.setMot(vec3d.d(1.0D, 0.6D, 1.0D));
        }

        this.bz += this.bD * 2.0F;
        if (!this.world.isClientSide && this.isAlive() && !this.isBaby() && !this.isChickenJockey() && --this.eggLayTime <= 0) {
            this.a(SoundEffects.ENTITY_CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.forceDrops = true; // CraftBukkit
            this.a((IMaterial) Items.EGG);
            this.forceDrops = false; // CraftBukkit
            this.eggLayTime = this.random.nextInt(6000) + 6000;
        }

    }

    @Override
    public void b(float f, float f1) {}

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_CHICKEN_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_CHICKEN_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_CHICKEN_DEATH;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_CHICKEN_STEP, 0.15F, 1.0F);
    }

    @Override
    public EntityChicken createChild(EntityAgeable entityageable) {
        return (EntityChicken) EntityTypes.CHICKEN.a(this.world);
    }

    @Override
    public boolean i(ItemStack itemstack) {
        return EntityChicken.bG.test(itemstack);
    }

    @Override
    protected int getExpValue(EntityHuman entityhuman) {
        return this.isChickenJockey() ? 10 : super.getExpValue(entityhuman);
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.bF = nbttagcompound.getBoolean("IsChickenJockey");
        if (nbttagcompound.hasKey("EggLayTime")) {
            this.eggLayTime = nbttagcompound.getInt("EggLayTime");
        }

    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("IsChickenJockey", this.bF);
        nbttagcompound.setInt("EggLayTime", this.eggLayTime);
    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return this.isChickenJockey() && !this.isVehicle();
    }

    @Override
    public void k(Entity entity) {
        super.k(entity);
        float f = MathHelper.sin(this.aK * 0.017453292F);
        float f1 = MathHelper.cos(this.aK * 0.017453292F);
        float f2 = 0.1F;
        float f3 = 0.0F;

        entity.setPosition(this.locX + (double) (0.1F * f), this.locY + (double) (this.getHeight() * 0.5F) + entity.aO() + 0.0D, this.locZ - (double) (0.1F * f1));
        if (entity instanceof EntityLiving) {
            ((EntityLiving) entity).aK = this.aK;
        }

    }

    public boolean isChickenJockey() {
        return this.bF;
    }

    public void r(boolean flag) {
        this.bF = flag;
    }
}
