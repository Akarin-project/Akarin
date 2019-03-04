package net.minecraft.server;

import javax.annotation.Nullable;

public class EntitySkeletonWither extends EntitySkeletonAbstract {

    public EntitySkeletonWither(World world) {
        super(EntityTypes.WITHER_SKELETON, world);
        this.setSize(0.7F, 2.4F);
        this.fireProof = true;
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.aw;
    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_WITHER_SKELETON_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_WITHER_SKELETON_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_WITHER_SKELETON_DEATH;
    }

    SoundEffect l() {
        return SoundEffects.ENTITY_WITHER_SKELETON_STEP;
    }

    public void die(DamageSource damagesource) {
        // super.die(damagesource); // CraftBukkit
        if (damagesource.getEntity() instanceof EntityCreeper) {
            EntityCreeper entitycreeper = (EntityCreeper) damagesource.getEntity();

            if (entitycreeper.isPowered() && entitycreeper.canCauseHeadDrop()) {
                entitycreeper.setCausedHeadDrop();
                this.a((IMaterial) Items.WITHER_SKELETON_SKULL);
            }
        }
        super.die(damagesource); // CraftBukkit - moved from above

    }

    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
    }

    protected void b(DifficultyDamageScaler difficultydamagescaler) {}

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        GroupDataEntity groupdataentity1 = super.prepare(difficultydamagescaler, groupdataentity, nbttagcompound);

        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(4.0D);
        this.dz();
        return groupdataentity1;
    }

    public float getHeadHeight() {
        return 2.1F;
    }

    public boolean B(Entity entity) {
        if (!super.B(entity)) {
            return false;
        } else {
            if (entity instanceof EntityLiving) {
                ((EntityLiving) entity).addEffect(new MobEffect(MobEffects.WITHER, 200), org.bukkit.event.entity.EntityPotionEffectEvent.Cause.ATTACK); // CraftBukkit
            }

            return true;
        }
    }

    protected EntityArrow a(float f) {
        EntityArrow entityarrow = super.a(f);

        entityarrow.setOnFire(100);
        return entityarrow;
    }
}
