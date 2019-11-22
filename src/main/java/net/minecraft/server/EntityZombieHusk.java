package net.minecraft.server;

import java.util.Random;

public class EntityZombieHusk extends EntityZombie {

    public EntityZombieHusk(EntityTypes<? extends EntityZombieHusk> entitytypes, World world) {
        super(entitytypes, world);
    }

    public static boolean b(EntityTypes<EntityZombieHusk> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return c(entitytypes, generatoraccess, enummobspawn, blockposition, random) && (enummobspawn == EnumMobSpawn.SPAWNER || generatoraccess.f(blockposition));
    }

    @Override
    protected boolean I_() {
        return false;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_HUSK_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_HUSK_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_HUSK_DEATH;
    }

    @Override
    protected SoundEffect getSoundStep() {
        return SoundEffects.ENTITY_HUSK_STEP;
    }

    @Override
    public boolean C(Entity entity) {
        boolean flag = super.C(entity);

        if (flag && this.getItemInMainHand().isEmpty() && entity instanceof EntityLiving) {
            float f = this.world.getDamageScaler(new BlockPosition(this)).b();

            ((EntityLiving) entity).addEffect(new MobEffect(MobEffects.HUNGER, 140 * (int) f), org.bukkit.event.entity.EntityPotionEffectEvent.Cause.ATTACK); // CraftBukkit
        }

        return flag;
    }

    @Override
    protected boolean dY() {
        return true;
    }

    @Override
    protected void ea() {
        this.b(EntityTypes.ZOMBIE);
        this.world.a((EntityHuman) null, 1041, new BlockPosition(this), 0);
    }

    @Override
    protected ItemStack dX() {
        return ItemStack.a;
    }
}
