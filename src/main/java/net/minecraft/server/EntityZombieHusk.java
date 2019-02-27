package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityZombieHusk extends EntityZombie {

    public EntityZombieHusk(World world) {
        super(EntityTypes.HUSK, world);
    }

    public boolean a(GeneratorAccess generatoraccess, boolean flag) {
        return super.a(generatoraccess, flag) && (flag || generatoraccess.e(new BlockPosition(this)));
    }

    protected boolean L_() {
        return false;
    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_HUSK_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_HUSK_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_HUSK_DEATH;
    }

    protected SoundEffect dA() {
        return SoundEffects.ENTITY_HUSK_STEP;
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.ay;
    }

    public boolean B(Entity entity) {
        boolean flag = super.B(entity);

        if (flag && this.getItemInMainHand().isEmpty() && entity instanceof EntityLiving) {
            float f = this.world.getDamageScaler(new BlockPosition(this)).b();

            ((EntityLiving) entity).addEffect(new MobEffect(MobEffects.HUNGER, 140 * (int) f), org.bukkit.event.entity.EntityPotionEffectEvent.Cause.ATTACK); // CraftBukkit
        }

        return flag;
    }

    protected boolean dC() {
        return true;
    }

    protected void dE() {
        this.a(new EntityZombie(this.world));
        this.world.a((EntityHuman) null, 1041, new BlockPosition((int) this.locX, (int) this.locY, (int) this.locZ), 0);
    }

    protected ItemStack dB() {
        return ItemStack.a;
    }
}
