package net.minecraft.server;

import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityOcelot extends EntityTameableAnimal {

    private static final RecipeItemStack bG = RecipeItemStack.a(Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH);
    private static final DataWatcherObject<Integer> bH = DataWatcher.a(EntityOcelot.class, DataWatcherRegistry.b);
    private static final MinecraftKey bI = new MinecraftKey("cat");
    private PathfinderGoalAvoidTarget<EntityHuman> bJ;
    private PathfinderGoalTempt bK;

    public EntityOcelot(World world) {
        super(EntityTypes.OCELOT, world);
        this.setSize(0.6F, 0.7F);
    }

    protected void n() {
        this.goalSit = new PathfinderGoalSit(this);
        this.bK = new PathfinderGoalTempt(this, 0.6D, EntityOcelot.bG, true);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, this.goalSit);
        this.goalSelector.a(3, this.bK);
        this.goalSelector.a(5, new PathfinderGoalFollowOwner(this, 1.0D, 10.0F, 5.0F));
        this.goalSelector.a(6, new PathfinderGoalJumpOnBlock(this, 0.8D));
        this.goalSelector.a(7, new PathfinderGoalLeapAtTarget(this, 0.3F));
        this.goalSelector.a(8, new PathfinderGoalOcelotAttack(this));
        this.goalSelector.a(9, new PathfinderGoalBreed(this, 0.8D));
        this.goalSelector.a(10, new PathfinderGoalRandomStrollLand(this, 0.8D, 1.0000001E-5F));
        this.goalSelector.a(11, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 10.0F));
        this.targetSelector.a(1, new PathfinderGoalRandomTargetNonTamed<>(this, EntityChicken.class, false, (Predicate) null));
        this.targetSelector.a(1, new PathfinderGoalRandomTargetNonTamed<>(this, EntityTurtle.class, false, EntityTurtle.bC));
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityOcelot.bH, 0);
    }

    public void mobTick() {
        if (this.getControllerMove().b()) {
            double d0 = this.getControllerMove().c();

            if (d0 == 0.6D) {
                this.setSneaking(true);
                this.setSprinting(false);
            } else if (d0 == 1.33D) {
                this.setSneaking(false);
                this.setSprinting(true);
            } else {
                this.setSneaking(false);
                this.setSprinting(false);
            }
        } else {
            this.setSneaking(false);
            this.setSprinting(false);
        }

    }

    public boolean isTypeNotPersistent() {
        return !this.isTamed() && this.ticksLived > 2400;
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30000001192092896D);
    }

    public void c(float f, float f1) {}

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("CatType", this.getCatType());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setCatType(nbttagcompound.getInt("CatType"));
    }

    @Nullable
    protected SoundEffect D() {
        return this.isTamed() ? (this.isInLove() ? SoundEffects.ENTITY_CAT_PURR : (this.random.nextInt(4) == 0 ? SoundEffects.ENTITY_CAT_PURREOW : SoundEffects.ENTITY_CAT_AMBIENT)) : null;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_CAT_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_CAT_DEATH;
    }

    protected float cD() {
        return 0.4F;
    }

    public boolean B(Entity entity) {
        return entity.damageEntity(DamageSource.mobAttack(this), 3.0F);
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            if (this.goalSit != null) {
                this.goalSit.setSitting(false);
            }

            return super.damageEntity(damagesource, f);
        }
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.V;
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (this.isTamed()) {
            if (this.f((EntityLiving) entityhuman) && !this.world.isClientSide && !this.f(itemstack)) {
                this.goalSit.setSitting(!this.isSitting());
            }
        } else if ((this.bK == null || this.bK.g()) && EntityOcelot.bG.test(itemstack) && entityhuman.h(this) < 9.0D) {
            if (!entityhuman.abilities.canInstantlyBuild) {
                itemstack.subtract(1);
            }

            if (!this.world.isClientSide) {
                if (this.random.nextInt(3) == 0) {
                    this.c(entityhuman);
                    this.setCatType(1 + this.world.random.nextInt(3));
                    this.s(true);
                    this.goalSit.setSitting(true);
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

    public EntityOcelot createChild(EntityAgeable entityageable) {
        EntityOcelot entityocelot = new EntityOcelot(this.world);

        if (this.isTamed()) {
            entityocelot.setOwnerUUID(this.getOwnerUUID());
            entityocelot.setTamed(true);
            entityocelot.setCatType(this.getCatType());
        }

        return entityocelot;
    }

    public boolean f(ItemStack itemstack) {
        return EntityOcelot.bG.test(itemstack);
    }

    public boolean mate(EntityAnimal entityanimal) {
        if (entityanimal == this) {
            return false;
        } else if (!this.isTamed()) {
            return false;
        } else if (!(entityanimal instanceof EntityOcelot)) {
            return false;
        } else {
            EntityOcelot entityocelot = (EntityOcelot) entityanimal;

            return !entityocelot.isTamed() ? false : this.isInLove() && entityocelot.isInLove();
        }
    }

    public int getCatType() {
        return (Integer) this.datawatcher.get(EntityOcelot.bH);
    }

    public void setCatType(int i) {
        this.datawatcher.set(EntityOcelot.bH, i);
    }

    public boolean a(GeneratorAccess generatoraccess, boolean flag) {
        return this.random.nextInt(3) != 0;
    }

    public boolean a(IWorldReader iworldreader) {
        if (iworldreader.a_(this, this.getBoundingBox()) && iworldreader.getCubes(this, this.getBoundingBox()) && !iworldreader.containsLiquid(this.getBoundingBox())) {
            BlockPosition blockposition = new BlockPosition(this.locX, this.getBoundingBox().minY, this.locZ);

            if (blockposition.getY() < iworldreader.getSeaLevel()) {
                return false;
            }

            IBlockData iblockdata = iworldreader.getType(blockposition.down());
            Block block = iblockdata.getBlock();

            if (block == Blocks.GRASS_BLOCK || iblockdata.a(TagsBlock.LEAVES)) {
                return true;
            }
        }

        return false;
    }

    public IChatBaseComponent getDisplayName() {
        IChatBaseComponent ichatbasecomponent = this.getCustomName();

        return (IChatBaseComponent) (ichatbasecomponent != null ? ichatbasecomponent : (this.isTamed() ? new ChatMessage(SystemUtils.a("entity", EntityOcelot.bI), new Object[0]) : super.getDisplayName()));
    }

    protected void dz() {
        if (this.bJ == null) {
            this.bJ = new PathfinderGoalAvoidTarget<>(this, EntityHuman.class, 16.0F, 0.8D, 1.33D);
        }

        this.goalSelector.a((PathfinderGoal) this.bJ);
        if (!this.isTamed()) {
            this.goalSelector.a(4, this.bJ);
        }

    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        groupdataentity = super.prepare(difficultydamagescaler, groupdataentity, nbttagcompound);
        if (this.getCatType() == 0 && this.world.random.nextInt(7) == 0) {
            for (int i = 0; i < 2; ++i) {
                EntityOcelot entityocelot = new EntityOcelot(this.world);

                entityocelot.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, 0.0F);
                entityocelot.setAgeRaw(-24000);
                this.world.addEntity(entityocelot);
            }
        }

        return groupdataentity;
    }
}
