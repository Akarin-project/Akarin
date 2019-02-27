package net.minecraft.server;

import javax.annotation.Nullable;

public class EntitySkeleton extends EntitySkeletonAbstract {

    public EntitySkeleton(World world) {
        super(EntityTypes.SKELETON, world);
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.av;
    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_SKELETON_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_SKELETON_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_SKELETON_DEATH;
    }

    SoundEffect l() {
        return SoundEffects.ENTITY_SKELETON_STEP;
    }

    public void die(DamageSource damagesource) {
        // super.die(damagesource); // CraftBukkit
        if (damagesource.getEntity() instanceof EntityCreeper) {
            EntityCreeper entitycreeper = (EntityCreeper) damagesource.getEntity();

            if (entitycreeper.isPowered() && entitycreeper.canCauseHeadDrop()) {
                entitycreeper.setCausedHeadDrop();
                this.a((IMaterial) Items.SKELETON_SKULL);
            }
        }
        super.die(damagesource); // CraftBukkit - moved from above

    }

    protected EntityArrow a(float f) {
        ItemStack itemstack = this.getEquipment(EnumItemSlot.OFFHAND);

        if (itemstack.getItem() == Items.SPECTRAL_ARROW) {
            EntitySpectralArrow entityspectralarrow = new EntitySpectralArrow(this.world, this);

            entityspectralarrow.a((EntityLiving) this, f);
            return entityspectralarrow;
        } else {
            EntityArrow entityarrow = super.a(f);

            if (itemstack.getItem() == Items.TIPPED_ARROW && entityarrow instanceof EntityTippedArrow) {
                ((EntityTippedArrow) entityarrow).b(itemstack);
            }

            return entityarrow;
        }
    }
}
