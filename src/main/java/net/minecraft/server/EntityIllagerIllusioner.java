package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityIllagerIllusioner extends EntityIllagerWizard implements IRangedEntity {

    private int bz;
    private final Vec3D[][] bA;

    public EntityIllagerIllusioner(EntityTypes<? extends EntityIllagerIllusioner> entitytypes, World world) {
        super(entitytypes, world);
        this.f = 5;
        this.bA = new Vec3D[2][4];

        for (int i = 0; i < 4; ++i) {
            this.bA[0][i] = new Vec3D(0.0D, 0.0D, 0.0D);
            this.bA[1][i] = new Vec3D(0.0D, 0.0D, 0.0D);
        }

    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new EntityIllagerWizard.b());
        this.goalSelector.a(4, new EntityIllagerIllusioner.b());
        this.goalSelector.a(5, new EntityIllagerIllusioner.a());
        this.goalSelector.a(6, new PathfinderGoalBowShoot<>(this, 0.5D, 20, 15.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.a(9, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 3.0F, 1.0F));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityRaider.class})).a(new Class[0])); // CraftBukkit - decompile error
        this.targetSelector.a(2, (new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true)).a(300));
        this.targetSelector.a(3, (new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, false)).a(300));
        this.targetSelector.a(3, (new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, false)).a(300));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.5D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(18.0D);
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(32.0D);
    }

    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.BOW));
        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (this.world.isClientSide && this.isInvisible()) {
            --this.bz;
            if (this.bz < 0) {
                this.bz = 0;
            }

            if (this.hurtTicks != 1 && this.ticksLived % 1200 != 0) {
                if (this.hurtTicks == this.hurtDuration - 1) {
                    this.bz = 3;

                    for (int i = 0; i < 4; ++i) {
                        this.bA[0][i] = this.bA[1][i];
                        this.bA[1][i] = new Vec3D(0.0D, 0.0D, 0.0D);
                    }
                }
            } else {
                this.bz = 3;
                float f = -6.0F;
                boolean flag = true;

                int j;

                for (j = 0; j < 4; ++j) {
                    this.bA[0][j] = this.bA[1][j];
                    this.bA[1][j] = new Vec3D((double) (-6.0F + (float) this.random.nextInt(13)) * 0.5D, (double) Math.max(0, this.random.nextInt(6) - 4), (double) (-6.0F + (float) this.random.nextInt(13)) * 0.5D);
                }

                for (j = 0; j < 16; ++j) {
                    this.world.addParticle(Particles.CLOUD, this.locX + (this.random.nextDouble() - 0.5D) * (double) this.getWidth(), this.locY + this.random.nextDouble() * (double) this.getHeight(), this.locZ + (this.random.nextDouble() - 0.5D) * (double) this.getWidth(), 0.0D, 0.0D, 0.0D);
                }

                this.world.a(this.locX, this.locY, this.locZ, SoundEffects.ENTITY_ILLUSIONER_MIRROR_MOVE, this.getSoundCategory(), 1.0F, 1.0F, false);
            }
        }

    }

    @Override
    public SoundEffect dV() {
        return SoundEffects.ENTITY_ILLUSIONER_AMBIENT;
    }

    @Override
    public boolean r(Entity entity) {
        return super.r(entity) ? true : (entity instanceof EntityLiving && ((EntityLiving) entity).getMonsterType() == EnumMonsterType.ILLAGER ? this.getScoreboardTeam() == null && entity.getScoreboardTeam() == null : false);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_ILLUSIONER_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_ILLUSIONER_DEATH;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_ILLUSIONER_HURT;
    }

    @Override
    protected SoundEffect getSoundCastSpell() {
        return SoundEffects.ENTITY_ILLUSIONER_CAST_SPELL;
    }

    @Override
    public void a(int i, boolean flag) {}

    @Override
    public void a(EntityLiving entityliving, float f) {
        ItemStack itemstack = this.f(this.b(ProjectileHelper.a(this, Items.BOW)));
        EntityArrow entityarrow = ProjectileHelper.a(this, itemstack, f);
        double d0 = entityliving.locX - this.locX;
        double d1 = entityliving.getBoundingBox().minY + (double) (entityliving.getHeight() / 3.0F) - entityarrow.locY;
        double d2 = entityliving.locZ - this.locZ;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);

        entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float) (14 - this.world.getDifficulty().a() * 4));
        // Paper start
        org.bukkit.event.entity.EntityShootBowEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callEntityShootBowEvent(this, this.getItemInMainHand(), this.getItemInOffHand(), entityarrow,0.8F);
        if (event.isCancelled()) {
            event.getProjectile().remove();
            return;
        }

        if (event.getProjectile() == entityarrow.getBukkitEntity()) {
            this.world.addEntity(entityarrow);
        }
        this.a(SoundEffects.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        // Paper end
    }

    class a extends EntityIllagerWizard.c {

        private int e;

        private a() {
            super();
        }

        @Override
        public boolean a() {
            return !super.a() ? false : (EntityIllagerIllusioner.this.getGoalTarget() == null ? false : (EntityIllagerIllusioner.this.getGoalTarget().getId() == this.e ? false : EntityIllagerIllusioner.this.world.getDamageScaler(new BlockPosition(EntityIllagerIllusioner.this)).a((float) EnumDifficulty.NORMAL.ordinal())));
        }

        @Override
        public void c() {
            super.c();
            this.e = EntityIllagerIllusioner.this.getGoalTarget().getId();
        }

        @Override
        protected int g() {
            return 20;
        }

        @Override
        protected int h() {
            return 180;
        }

        @Override
        protected void j() {
            EntityIllagerIllusioner.this.getGoalTarget().addEffect(new MobEffect(MobEffects.BLINDNESS, 400), org.bukkit.event.entity.EntityPotionEffectEvent.Cause.ATTACK); // CraftBukkit
        }

        @Override
        protected SoundEffect k() {
            return SoundEffects.ENTITY_ILLUSIONER_PREPARE_BLINDNESS;
        }

        @Override
        protected EntityIllagerWizard.Spell l() {
            return EntityIllagerWizard.Spell.BLINDNESS;
        }
    }

    class b extends EntityIllagerWizard.c {

        private b() {
            super();
        }

        @Override
        public boolean a() {
            return !super.a() ? false : !EntityIllagerIllusioner.this.hasEffect(MobEffects.INVISIBILITY);
        }

        @Override
        protected int g() {
            return 20;
        }

        @Override
        protected int h() {
            return 340;
        }

        @Override
        protected void j() {
            EntityIllagerIllusioner.this.addEffect(new MobEffect(MobEffects.INVISIBILITY, 1200), org.bukkit.event.entity.EntityPotionEffectEvent.Cause.ILLUSION); // CraftBukkit
        }

        @Nullable
        @Override
        protected SoundEffect k() {
            return SoundEffects.ENTITY_ILLUSIONER_PREPARE_MIRROR;
        }

        @Override
        protected EntityIllagerWizard.Spell l() {
            return EntityIllagerWizard.Spell.DISAPPEAR;
        }
    }
}
