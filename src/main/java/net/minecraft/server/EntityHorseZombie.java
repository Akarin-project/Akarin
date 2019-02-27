package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityHorseZombie extends EntityHorseAbstract {

    public EntityHorseZombie(World world) {
        super(EntityTypes.ZOMBIE_HORSE, world);
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(15.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.20000000298023224D);
        this.getAttributeInstance(EntityHorseZombie.attributeJumpStrength).setValue(this.ed());
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    protected SoundEffect D() {
        super.D();
        return SoundEffects.ENTITY_ZOMBIE_HORSE_AMBIENT;
    }

    protected SoundEffect cs() {
        super.cs();
        return SoundEffects.ENTITY_ZOMBIE_HORSE_DEATH;
    }

    protected SoundEffect d(DamageSource damagesource) {
        super.d(damagesource);
        return SoundEffects.ENTITY_ZOMBIE_HORSE_HURT;
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.Q;
    }

    @Nullable
    public EntityAgeable createChild(EntityAgeable entityageable) {
        return new EntityHorseZombie(this.world);
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
                if (!this.dV() && itemstack.getItem() == Items.SADDLE) {
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

    protected void dI() {}
}
