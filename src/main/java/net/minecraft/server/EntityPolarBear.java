package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityPolarBear extends EntityAnimal {

    private static final DataWatcherObject<Boolean> bC = DataWatcher.a(EntityPolarBear.class, DataWatcherRegistry.i);
    private float bD;
    private float bE;
    private int bG;

    public EntityPolarBear(World world) {
        super(EntityTypes.POLAR_BEAR, world);
        this.setSize(1.3F, 1.4F);
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        return new EntityPolarBear(this.world);
    }

    public boolean f(ItemStack itemstack) {
        return false;
    }

    protected void n() {
        super.n();
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new EntityPolarBear.d());
        this.goalSelector.a(1, new EntityPolarBear.e());
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.25D));
        this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new EntityPolarBear.c());
        this.targetSelector.a(2, new EntityPolarBear.a());
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(30.0D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(20.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(6.0D);
    }

    public boolean a(GeneratorAccess generatoraccess, boolean flag) {
        int i = MathHelper.floor(this.locX);
        int j = MathHelper.floor(this.getBoundingBox().minY);
        int k = MathHelper.floor(this.locZ);
        BlockPosition blockposition = new BlockPosition(i, j, k);
        BiomeBase biomebase = generatoraccess.getBiome(blockposition);

        return biomebase != Biomes.FROZEN_OCEAN && biomebase != Biomes.DEEP_FROZEN_OCEAN ? super.a(generatoraccess, flag) : generatoraccess.getLightLevel(blockposition, 0) > 8 && generatoraccess.getType(blockposition.down()).getBlock() == Blocks.ICE;
    }

    protected SoundEffect D() {
        return this.isBaby() ? SoundEffects.ENTITY_POLAR_BEAR_AMBIENT_BABY : SoundEffects.ENTITY_POLAR_BEAR_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_POLAR_BEAR_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_POLAR_BEAR_DEATH;
    }

    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_POLAR_BEAR_STEP, 0.15F, 1.0F);
    }

    protected void dy() {
        if (this.bG <= 0) {
            this.a(SoundEffects.ENTITY_POLAR_BEAR_WARNING, 1.0F, 1.0F);
            this.bG = 40;
        }

    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.M;
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityPolarBear.bC, false);
    }

    public void tick() {
        super.tick();
        if (this.world.isClientSide) {
            this.bD = this.bE;
            if (this.dz()) {
                this.bE = MathHelper.a(this.bE + 1.0F, 0.0F, 6.0F);
            } else {
                this.bE = MathHelper.a(this.bE - 1.0F, 0.0F, 6.0F);
            }
        }

        if (this.bG > 0) {
            --this.bG;
        }

    }

    public boolean B(Entity entity) {
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), (float) ((int) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue()));

        if (flag) {
            this.a((EntityLiving) this, entity);
        }

        return flag;
    }

    public boolean dz() {
        return (Boolean) this.datawatcher.get(EntityPolarBear.bC);
    }

    public void s(boolean flag) {
        this.datawatcher.set(EntityPolarBear.bC, flag);
    }

    protected float cJ() {
        return 0.98F;
    }

    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (groupdataentity instanceof EntityPolarBear.b) {
            if (((EntityPolarBear.b) groupdataentity).a) {
                this.setAgeRaw(-24000);
            }
        } else {
            EntityPolarBear.b entitypolarbear_b = new EntityPolarBear.b();

            entitypolarbear_b.a = true;
            groupdataentity = entitypolarbear_b;
        }

        return (GroupDataEntity) groupdataentity;
    }

    class e extends PathfinderGoalPanic {

        public e() {
            super(EntityPolarBear.this, 2.0D);
        }

        public boolean a() {
            return !EntityPolarBear.this.isBaby() && !EntityPolarBear.this.isBurning() ? false : super.a();
        }
    }

    class d extends PathfinderGoalMeleeAttack {

        public d() {
            super(EntityPolarBear.this, 1.25D, true);
        }

        protected void a(EntityLiving entityliving, double d0) {
            double d1 = this.a(entityliving);

            if (d0 <= d1 && this.b <= 0) {
                this.b = 20;
                this.a.B(entityliving);
                EntityPolarBear.this.s(false);
            } else if (d0 <= d1 * 2.0D) {
                if (this.b <= 0) {
                    EntityPolarBear.this.s(false);
                    this.b = 20;
                }

                if (this.b <= 10) {
                    EntityPolarBear.this.s(true);
                    EntityPolarBear.this.dy();
                }
            } else {
                this.b = 20;
                EntityPolarBear.this.s(false);
            }

        }

        public void d() {
            EntityPolarBear.this.s(false);
            super.d();
        }

        protected double a(EntityLiving entityliving) {
            return (double) (4.0F + entityliving.width);
        }
    }

    class a extends PathfinderGoalNearestAttackableTarget<EntityHuman> {

        public a() {
            super(EntityPolarBear.this, EntityHuman.class, 20, true, true, (Predicate) null);
        }

        public boolean a() {
            if (EntityPolarBear.this.isBaby()) {
                return false;
            } else {
                if (super.a()) {
                    List<EntityPolarBear> list = EntityPolarBear.this.world.a(EntityPolarBear.class, EntityPolarBear.this.getBoundingBox().grow(8.0D, 4.0D, 8.0D));
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        EntityPolarBear entitypolarbear = (EntityPolarBear) iterator.next();

                        if (entitypolarbear.isBaby()) {
                            return true;
                        }
                    }
                }

                EntityPolarBear.this.setGoalTarget((EntityLiving) null);
                return false;
            }
        }

        protected double i() {
            return super.i() * 0.5D;
        }
    }

    class c extends PathfinderGoalHurtByTarget {

        public c() {
            super(EntityPolarBear.this, false);
        }

        public void c() {
            super.c();
            if (EntityPolarBear.this.isBaby()) {
                this.g();
                this.d();
            }

        }

        protected void a(EntityCreature entitycreature, EntityLiving entityliving) {
            if (entitycreature instanceof EntityPolarBear && !entitycreature.isBaby()) {
                super.a(entitycreature, entityliving);
            }

        }
    }

    static class b implements GroupDataEntity {

        public boolean a;

        private b() {}
    }
}
