package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class EntitySpider extends EntityMonster {

    private static final DataWatcherObject<Byte> b = DataWatcher.a(EntitySpider.class, DataWatcherRegistry.a);

    public EntitySpider(EntityTypes<? extends EntitySpider> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(3, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.a(4, new EntitySpider.PathfinderGoalSpiderMeleeAttack(this));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 0.8D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.a(2, new EntitySpider.PathfinderGoalSpiderNearestAttackableTarget<>(this, EntityHuman.class));
        this.targetSelector.a(3, new EntitySpider.PathfinderGoalSpiderNearestAttackableTarget<>(this, EntityIronGolem.class));
    }

    @Override
    public double aP() {
        return (double) (this.getHeight() * 0.5F);
    }

    @Override
    protected NavigationAbstract b(World world) {
        return new NavigationSpider(this, world);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntitySpider.b, (byte) 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClientSide) {
            this.r(this.positionChanged);
        }

    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(16.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30000001192092896D);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_SPIDER_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_SPIDER_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_SPIDER_DEATH;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean isClimbing() {
        return this.l();
    }

    @Override
    public void a(IBlockData iblockdata, Vec3D vec3d) {
        if (iblockdata.getBlock() != Blocks.COBWEB) {
            super.a(iblockdata, vec3d);
        }

    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.ARTHROPOD;
    }

    @Override
    public boolean d(MobEffect mobeffect) {
        return mobeffect.getMobEffect() == MobEffects.POISON ? false : super.d(mobeffect);
    }

    public boolean l() {
        return ((Byte) this.datawatcher.get(EntitySpider.b) & 1) != 0;
    }

    public void r(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntitySpider.b);

        if (flag) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 &= -2;
        }

        this.datawatcher.set(EntitySpider.b, b0);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        Object object = super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);

        if (generatoraccess.getRandom().nextInt(100) == 0) {
            EntitySkeleton entityskeleton = (EntitySkeleton) EntityTypes.SKELETON.a(this.world);

            entityskeleton.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, 0.0F);
            entityskeleton.prepare(generatoraccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) null, (NBTTagCompound) null);
            generatoraccess.addEntity(entityskeleton, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.JOCKEY); // CraftBukkit - add SpawnReason
            entityskeleton.startRiding(this);
        }

        if (object == null) {
            object = new EntitySpider.GroupDataSpider();
            if (generatoraccess.getDifficulty() == EnumDifficulty.HARD && generatoraccess.getRandom().nextFloat() < 0.1F * difficultydamagescaler.d()) {
                ((EntitySpider.GroupDataSpider) object).a(generatoraccess.getRandom());
            }
        }

        if (object instanceof EntitySpider.GroupDataSpider) {
            MobEffectList mobeffectlist = ((EntitySpider.GroupDataSpider) object).a;

            if (mobeffectlist != null) {
                this.addEffect(new MobEffect(mobeffectlist, Integer.MAX_VALUE), org.bukkit.event.entity.EntityPotionEffectEvent.Cause.SPIDER_SPAWN); // CraftBukkit
            }
        }

        return (GroupDataEntity) object;
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 0.65F;
    }

    static class PathfinderGoalSpiderNearestAttackableTarget<T extends EntityLiving> extends PathfinderGoalNearestAttackableTarget<T> {

        public PathfinderGoalSpiderNearestAttackableTarget(EntitySpider entityspider, Class<T> oclass) {
            super(entityspider, oclass, true);
        }

        @Override
        public boolean a() {
            float f = this.e.aF();

            return f >= 0.5F ? false : super.a();
        }
    }

    static class PathfinderGoalSpiderMeleeAttack extends PathfinderGoalMeleeAttack {

        public PathfinderGoalSpiderMeleeAttack(EntitySpider entityspider) {
            super(entityspider, 1.0D, true);
        }

        @Override
        public boolean a() {
            return super.a() && !this.a.isVehicle();
        }

        @Override
        public boolean b() {
            float f = this.a.aF();

            if (f >= 0.5F && this.a.getRandom().nextInt(100) == 0) {
                this.a.setGoalTarget((EntityLiving) null);
                return false;
            } else {
                return super.b();
            }
        }

        @Override
        protected double a(EntityLiving entityliving) {
            return (double) (4.0F + entityliving.getWidth());
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
