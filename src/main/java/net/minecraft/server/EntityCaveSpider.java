package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityCaveSpider extends EntitySpider {

    public EntityCaveSpider(World world) {
        super(EntityTypes.CAVE_SPIDER, world);
        this.setSize(0.7F, 0.5F);
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(12.0D);
    }

    public boolean B(Entity entity) {
        if (super.B(entity)) {
            if (entity instanceof EntityLiving) {
                byte b0 = 0;

                if (this.world.getDifficulty() == EnumDifficulty.NORMAL) {
                    b0 = 7;
                } else if (this.world.getDifficulty() == EnumDifficulty.HARD) {
                    b0 = 15;
                }

                if (b0 > 0) {
                    ((EntityLiving) entity).addEffect(new MobEffect(MobEffects.POISON, b0 * 20, 0));
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        return groupdataentity;
    }

    public float getHeadHeight() {
        return 0.45F;
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.z;
    }
}
