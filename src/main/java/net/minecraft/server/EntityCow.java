package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityCow extends EntityAnimal {

    protected EntityCow(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.setSize(0.9F, 1.4F);
    }

    public EntityCow(World world) {
        this(EntityTypes.COW, world);
    }

    protected void n() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 2.0D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.25D, RecipeItemStack.a(Items.WHEAT), false));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.25D));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.20000000298023224D);
    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_COW_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_COW_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_COW_DEATH;
    }

    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_COW_STEP, 0.15F, 1.0F);
    }

    protected float cD() {
        return 0.4F;
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.S;
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() == Items.BUCKET && !entityhuman.abilities.canInstantlyBuild && !this.isBaby()) {
            entityhuman.a(SoundEffects.ENTITY_COW_MILK, 1.0F, 1.0F);
            itemstack.subtract(1);
            if (itemstack.isEmpty()) {
                entityhuman.a(enumhand, new ItemStack(Items.MILK_BUCKET));
            } else if (!entityhuman.inventory.pickup(new ItemStack(Items.MILK_BUCKET))) {
                entityhuman.drop(new ItemStack(Items.MILK_BUCKET), false);
            }

            return true;
        } else {
            return super.a(entityhuman, enumhand);
        }
    }

    public EntityCow createChild(EntityAgeable entityageable) {
        return new EntityCow(this.world);
    }

    public float getHeadHeight() {
        return this.isBaby() ? this.length : 1.3F;
    }
}
