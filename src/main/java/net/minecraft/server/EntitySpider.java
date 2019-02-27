package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class EntitySpider extends EntityMonster {

    private static final DataWatcherObject<Byte> a = DataWatcher.a(EntitySpider.class, DataWatcherRegistry.a);

    protected EntitySpider(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.setSize(1.4F, 0.9F);
    }

    public EntitySpider(World world) {
        this(EntityTypes.SPIDER, world);
    }

    protected void n() {
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(3, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.a(4, new EntitySpider.PathfinderGoalSpiderMeleeAttack(this));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 0.8D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
        this.targetSelector.a(2, new EntitySpider.PathfinderGoalSpiderNearestAttackableTarget<>(this, EntityHuman.class));
        this.targetSelector.a(3, new EntitySpider.PathfinderGoalSpiderNearestAttackableTarget<>(this, EntityIronGolem.class));
    }

    public double aJ() {
        return (double) (this.length * 0.5F);
    }

    protected NavigationAbstract b(World world) {
        return new NavigationSpider(this, world);
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntitySpider.a, (byte) 0);
    }

    public void tick() {
        super.tick();
        if (!this.world.isClientSide) {
            this.a(this.positionChanged);
        }

    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(16.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30000001192092896D);
    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_SPIDER_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_SPIDER_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_SPIDER_DEATH;
    }

    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.y;
    }

    public boolean z_() {
        return this.l();
    }

    public void bh() {}

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.ARTHROPOD;
    }

    public boolean d(MobEffect mobeffect) {
        return mobeffect.getMobEffect() == MobEffects.POISON ? false : super.d(mobeffect);
    }

    public boolean l() {
        return ((Byte) this.datawatcher.get(EntitySpider.a) & 1) != 0;
    }

    public void a(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntitySpider.a);

        if (flag) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 &= -2;
        }

        this.datawatcher.set(EntitySpider.a, b0);
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        Object object = super.prepare(difficultydamagescaler, groupdataentity, nbttagcompound);

        if (this.world.random.nextInt(100) == 0) {
            EntitySkeleton entityskeleton = new EntitySkeleton(this.world);

            entityskeleton.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, 0.0F);
            entityskeleton.prepare(difficultydamagescaler, (GroupDataEntity) null, (NBTTagCompound) null);
            this.world.addEntity(entityskeleton);
            entityskeleton.startRiding(this);
        }

        if (object == null) {
            object = new EntitySpider.GroupDataSpider();
            if (this.world.getDifficulty() == EnumDifficulty.HARD && this.world.random.nextFloat() < 0.1F * difficultydamagescaler.d()) {
                ((EntitySpider.GroupDataSpider) object).a(this.world.random);
            }
        }

        if (object instanceof EntitySpider.GroupDataSpider) {
            MobEffectList mobeffectlist = ((EntitySpider.GroupDataSpider) object).a;

            if (mobeffectlist != null) {
                this.addEffect(new MobEffect(mobeffectlist, Integer.MAX_VALUE));
            }
        }

        return (GroupDataEntity) object;
    }

    public float getHeadHeight() {
        return 0.65F;
    }

    static class PathfinderGoalSpiderNearestAttackableTarget<T extends EntityLiving> extends PathfinderGoalNearestAttackableTarget<T> {

        public PathfinderGoalSpiderNearestAttackableTarget(EntitySpider entityspider, Class<T> oclass) {
            super(entityspider, oclass, true);
        }

        public boolean a() {
            float f = this.e.az();

            return f >= 0.5F ? false : super.a();
        }
    }

    static class PathfinderGoalSpiderMeleeAttack extends PathfinderGoalMeleeAttack {

        public PathfinderGoalSpiderMeleeAttack(EntitySpider entityspider) {
            super(entityspider, 1.0D, true);
        }

        public boolean b() {
            float f = this.a.az();

            if (f >= 0.5F && this.a.getRandom().nextInt(100) == 0) {
                this.a.setGoalTarget((EntityLiving) null);
                return false;
            } else {
                return super.b();
            }
        }

        protected double a(EntityLiving entityliving) {
            return (double) (4.0F + entityliving.width);
        }
    }

    public static class GroupDataSpider implements GroupDataEntity {

        public MobEffectList a;

        public GroupDataSpider() {}

        public void a(Random random) {
            int i = random.nextInt(5);

            if (i <= 1) {
                this.a = MobEffects.FASTER_MOVEMENT;
            } else if (i <= 2) {
                this.a = MobEffects.INCREASE_DAMAGE;
            } else if (i <= 3) {
                this.a = MobEffects.REGENERATION;
            } else if (i <= 4) {
                this.a = MobEffects.INVISIBILITY;
            }

        }
    }
}
