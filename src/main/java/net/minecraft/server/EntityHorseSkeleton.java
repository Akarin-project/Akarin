package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityHorseSkeleton extends EntityHorseAbstract {

    private final PathfinderGoalHorseTrap bM = new PathfinderGoalHorseTrap(this);
    private boolean bN;
    private int bO;

    public EntityHorseSkeleton(World world) {
        super(EntityTypes.SKELETON_HORSE, world);
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(15.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.20000000298023224D);
        this.getAttributeInstance(EntityHorseSkeleton.attributeJumpStrength).setValue(this.ed());
    }

    protected void dI() {}

    protected SoundEffect D() {
        super.D();
        return this.a(TagsFluid.WATER) ? SoundEffects.ENTITY_SKELETON_HORSE_AMBIENT_WATER : SoundEffects.ENTITY_SKELETON_HORSE_AMBIENT;
    }

    protected SoundEffect cs() {
        super.cs();
        return SoundEffects.ENTITY_SKELETON_HORSE_DEATH;
    }

    protected SoundEffect d(DamageSource damagesource) {
        super.d(damagesource);
        return SoundEffects.ENTITY_SKELETON_HORSE_HURT;
    }

    protected SoundEffect ad() {
        if (this.onGround) {
            if (!this.isVehicle()) {
                return SoundEffects.ENTITY_SKELETON_HORSE_STEP_WATER;
            }

            ++this.bL;
            if (this.bL > 5 && this.bL % 3 == 0) {
                return SoundEffects.ENTITY_SKELETON_HORSE_GALLOP_WATER;
            }

            if (this.bL <= 5) {
                return SoundEffects.ENTITY_SKELETON_HORSE_STEP_WATER;
            }
        }

        return SoundEffects.ENTITY_SKELETON_HORSE_SWIM;
    }

    protected void d(float f) {
        if (this.onGround) {
            super.d(0.3F);
        } else {
            super.d(Math.min(0.1F, f * 25.0F));
        }

    }

    protected void ea() {
        if (this.isInWater()) {
            this.a(SoundEffects.ENTITY_SKELETON_HORSE_JUMP_WATER, 0.4F, 1.0F);
        } else {
            super.ea();
        }

    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    public double aJ() {
        return super.aJ() - 0.1875D;
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.R;
    }

    public void movementTick() {
        super.movementTick();
        if (this.dy() && this.bO++ >= 18000) {
            this.die();
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("SkeletonTrap", this.dy());
        nbttagcompound.setInt("SkeletonTrapTime", this.bO);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.s(nbttagcompound.getBoolean("SkeletonTrap"));
        this.bO = nbttagcompound.getInt("SkeletonTrapTime");
    }

    public boolean aY() {
        return true;
    }

    protected float cJ() {
        return 0.96F;
    }

    public boolean dy() {
        return this.bN;
    }

    public void s(boolean flag) {
        if (flag != this.bN) {
            this.bN = flag;
            if (flag) {
                this.goalSelector.a(1, this.bM);
            } else {
                this.goalSelector.a((PathfinderGoal) this.bM);
            }

        }
    }

    @Nullable
    public EntityAgeable createChild(EntityAgeable entityageable) {
        return new EntityHorseSkeleton(this.world);
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() instanceof ItemMonsterEgg) {
            return super.a(entityhuman, enumhand);
        } else if (!this.isTamed()) {
            return false;
        } else if (this.isBaby()) {
            return super.a(entityhuman, enumhand);
        } else if (entityhuman.isSneaking()) {
            this.c(entityhuman);
            return true;
        } else if (this.isVehicle()) {
            return super.a(entityhuman, enumhand);
        } else {
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == Items.SADDLE && !this.dV()) {
                    this.c(entityhuman);
                    return true;
                }

                if (itemstack.a(entityhuman, (EntityLiving) this, enumhand)) {
                    return true;
                }
            }

            this.g(entityhuman);
            return true;
        }
    }
}
