package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityCaveSpider extends EntitySpider {

    public EntityCaveSpider(EntityTypes<? extends EntityCaveSpider> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(12.0D);
    }

    @Override
    public boolean C(Entity entity) {
        if (super.C(entity)) {
            if (entity instanceof EntityLiving) {
                byte b0 = 0;

                if (this.world.getDifficulty() == EnumDifficulty.NORMAL) {
                    b0 = 7;
                } else if (this.world.getDifficulty() == EnumDifficulty.HARD) {
                    b0 = 15;
                }

                if (b0 > 0) {
                    ((EntityLiving) entity).addEffect(new MobEffect(MobEffects.POISON, b0 * 20, 0), org.bukkit.event.entity.EntityPotionEffectEvent.Cause.ATTACK); // CraftBukkit
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        return groupdataentity;
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 0.45F;
    }
}
