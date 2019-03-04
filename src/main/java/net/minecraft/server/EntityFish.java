package net.minecraft.server;

public abstract class EntityFish extends EntityWaterAnimal implements IAnimal {

    private static final DataWatcherObject<Boolean> a = DataWatcher.a(EntityFish.class, DataWatcherRegistry.i);

    public EntityFish(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.moveController = new EntityFish.a(this);
    }

    public float getHeadHeight() {
        return this.length * 0.65F;
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(3.0D);
    }

    public boolean isPersistent() {
        return this.isFromBucket() || super.isPersistent();
    }

    public boolean a(GeneratorAccess generatoraccess, boolean flag) {
        BlockPosition blockposition = new BlockPosition(this);

        return generatoraccess.getType(blockposition).getBlock() == Blocks.WATER && generatoraccess.getType(blockposition.up()).getBlock() == Blocks.WATER ? super.a(generatoraccess, flag) : false;
    }

    public boolean isTypeNotPersistent() {
        return true; // CraftBukkit
    }

    public int dg() {
        return 8;
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityFish.a, false);
    }

    public boolean isFromBucket() {
        return (Boolean) this.datawatcher.get(EntityFish.a);
    }

    public void setFromBucket(boolean flag) {
        this.datawatcher.set(EntityFish.a, flag);
        this.persistent = this.isPersistent(); // CraftBukkit - SPIGOT-4106 update persistence
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("FromBucket", this.isFromBucket());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setFromBucket(nbttagcompound.getBoolean("FromBucket"));
    }

    protected void n() {
        super.n();
        this.goalSelector.a(0, new PathfinderGoalPanic(this, 1.25D));
        this.goalSelector.a(2, new PathfinderGoalAvoidTarget<>(this, EntityHuman.class, 8.0F, 1.6D, 1.4D, IEntitySelector.f));
        this.goalSelector.a(4, new EntityFish.b(this));
    }

    protected NavigationAbstract b(World world) {
        return new NavigationGuardian(this, world);
    }

    public void a(float f, float f1, float f2) {
        if (this.cP() && this.isInWater()) {
            this.a(f, f1, f2, 0.01F);
            this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
            this.motX *= 0.8999999761581421D;
            this.motY *= 0.8999999761581421D;
            this.motZ *= 0.8999999761581421D;
            if (this.getGoalTarget() == null) {
                this.motY -= 0.005D;
            }
        } else {
            super.a(f, f1, f2);
        }

    }

    public void movementTick() {
        if (!this.isInWater() && this.onGround && this.C) {
            this.motY += 0.4000000059604645D;
            this.motX += (double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F);
            this.motZ += (double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F);
            this.onGround = false;
            this.impulse = true;
            this.a(this.dz(), this.cD(), this.cE());
        }

        super.movementTick();
    }

    protected boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() == Items.WATER_BUCKET && this.isAlive()) {
            this.a(SoundEffects.ITEM_BUCKET_FILL_FISH, 1.0F, 1.0F);
            itemstack.subtract(1);
            ItemStack itemstack1 = this.l();

            this.f(itemstack1);
            if (!this.world.isClientSide) {
                CriterionTriggers.j.a((EntityPlayer) entityhuman, itemstack1);
            }

            if (itemstack.isEmpty()) {
                entityhuman.a(enumhand, itemstack1);
            } else if (!entityhuman.inventory.pickup(itemstack1)) {
                entityhuman.drop(itemstack1, false);
            }

            this.die();
            return true;
        } else {
            return super.a(entityhuman, enumhand);
        }
    }

    protected void f(ItemStack itemstack) {
        if (this.hasCustomName()) {
            itemstack.a(this.getCustomName());
        }

    }

    protected abstract ItemStack l();

    protected boolean dy() {
        return true;
    }

    protected abstract SoundEffect dz();

    protected SoundEffect ad() {
        return SoundEffects.ENTITY_FISH_SWIM;
    }

    static class a extends ControllerMove {

        private final EntityFish i;

        a(EntityFish entityfish) {
            super(entityfish);
            this.i = entityfish;
        }

        public void a() {
            if (this.i.a(TagsFluid.WATER)) {
                this.i.motY += 0.005D;
            }

            if (this.h == ControllerMove.Operation.MOVE_TO && !this.i.getNavigation().p()) {
                double d0 = this.b - this.i.locX;
                double d1 = this.c - this.i.locY;
                double d2 = this.d - this.i.locZ;
                double d3 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

                d1 /= d3;
                float f = (float) (MathHelper.c(d2, d0) * 57.2957763671875D) - 90.0F;

                this.i.yaw = this.a(this.i.yaw, f, 90.0F);
                this.i.aQ = this.i.yaw;
                float f1 = (float) (this.e * this.i.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());

                this.i.o(this.i.cK() + (f1 - this.i.cK()) * 0.125F);
                this.i.motY += (double) this.i.cK() * d1 * 0.1D;
            } else {
                this.i.o(0.0F);
            }
        }
    }

    static class b extends PathfinderGoalRandomSwim {

        private final EntityFish h;

        public b(EntityFish entityfish) {
            super(entityfish, 1.0D, 40);
            this.h = entityfish;
        }

        public boolean a() {
            return this.h.dy() && super.a();
        }
    }
}
