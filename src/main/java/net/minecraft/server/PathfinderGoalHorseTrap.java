package net.minecraft.server;

public class PathfinderGoalHorseTrap extends PathfinderGoal {

    private final EntityHorseSkeleton a;

    public PathfinderGoalHorseTrap(EntityHorseSkeleton entityhorseskeleton) {
        this.a = entityhorseskeleton;
    }

    public boolean a() {
        return this.a.world.isPlayerNearby(this.a.locX, this.a.locY, this.a.locZ, 10.0D);
    }

    public void e() {
        DifficultyDamageScaler difficultydamagescaler = this.a.world.getDamageScaler(new BlockPosition(this.a));

        this.a.s(false);
        this.a.setTamed(true);
        this.a.setAgeRaw(0);
        this.a.world.strikeLightning(new EntityLightning(this.a.world, this.a.locX, this.a.locY, this.a.locZ, true));
        EntitySkeleton entityskeleton = this.a(difficultydamagescaler, this.a);

        entityskeleton.startRiding(this.a);

        for (int i = 0; i < 3; ++i) {
            EntityHorseAbstract entityhorseabstract = this.a(difficultydamagescaler);
            EntitySkeleton entityskeleton1 = this.a(difficultydamagescaler, entityhorseabstract);

            entityskeleton1.startRiding(entityhorseabstract);
            entityhorseabstract.f(this.a.getRandom().nextGaussian() * 0.5D, 0.0D, this.a.getRandom().nextGaussian() * 0.5D);
        }

    }

    private EntityHorseAbstract a(DifficultyDamageScaler difficultydamagescaler) {
        EntityHorseSkeleton entityhorseskeleton = new EntityHorseSkeleton(this.a.world);

        entityhorseskeleton.prepare(difficultydamagescaler, (GroupDataEntity) null, (NBTTagCompound) null);
        entityhorseskeleton.setPosition(this.a.locX, this.a.locY, this.a.locZ);
        entityhorseskeleton.noDamageTicks = 60;
        entityhorseskeleton.di();
        entityhorseskeleton.setTamed(true);
        entityhorseskeleton.setAgeRaw(0);
        entityhorseskeleton.world.addEntity(entityhorseskeleton);
        return entityhorseskeleton;
    }

    private EntitySkeleton a(DifficultyDamageScaler difficultydamagescaler, EntityHorseAbstract entityhorseabstract) {
        EntitySkeleton entityskeleton = new EntitySkeleton(entityhorseabstract.world);

        entityskeleton.prepare(difficultydamagescaler, (GroupDataEntity) null, (NBTTagCompound) null);
        entityskeleton.setPosition(entityhorseabstract.locX, entityhorseabstract.locY, entityhorseabstract.locZ);
        entityskeleton.noDamageTicks = 60;
        entityskeleton.di();
        if (entityskeleton.getEquipment(EnumItemSlot.HEAD).isEmpty()) {
            entityskeleton.setSlot(EnumItemSlot.HEAD, new ItemStack(Items.IRON_HELMET));
        }

        entityskeleton.setSlot(EnumItemSlot.MAINHAND, EnchantmentManager.a(entityskeleton.getRandom(), entityskeleton.getItemInMainHand(), (int) (5.0F + difficultydamagescaler.d() * (float) entityskeleton.getRandom().nextInt(18)), false));
        entityskeleton.setSlot(EnumItemSlot.HEAD, EnchantmentManager.a(entityskeleton.getRandom(), entityskeleton.getEquipment(EnumItemSlot.HEAD), (int) (5.0F + difficultydamagescaler.d() * (float) entityskeleton.getRandom().nextInt(18)), false));
        entityskeleton.world.addEntity(entityskeleton);
        return entityskeleton;
    }
}
