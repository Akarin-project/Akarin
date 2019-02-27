package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityHorseDonkey extends EntityHorseChestedAbstract {

    public EntityHorseDonkey(World world) {
        super(EntityTypes.DONKEY, world);
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.O;
    }

    protected SoundEffect D() {
        super.D();
        return SoundEffects.ENTITY_DONKEY_AMBIENT;
    }

    protected SoundEffect cs() {
        super.cs();
        return SoundEffects.ENTITY_DONKEY_DEATH;
    }

    protected SoundEffect d(DamageSource damagesource) {
        super.d(damagesource);
        return SoundEffects.ENTITY_DONKEY_HURT;
    }

    public boolean mate(EntityAnimal entityanimal) {
        return entityanimal == this ? false : (!(entityanimal instanceof EntityHorseDonkey) && !(entityanimal instanceof EntityHorse) ? false : this.eb() && ((EntityHorseAbstract) entityanimal).eb());
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        Object object = entityageable instanceof EntityHorse ? new EntityHorseMule(this.world) : new EntityHorseDonkey(this.world);

        this.a(entityageable, (EntityHorseAbstract) object);
        return (EntityAgeable) object;
    }
}
