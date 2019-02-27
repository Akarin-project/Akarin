package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityIllagerIllusioner extends EntityIllagerWizard implements IRangedEntity {

    private int c;
    private final Vec3D[][] bC;

    public EntityIllagerIllusioner(World world) {
        super(EntityTypes.ILLUSIONER, world);
        this.setSize(0.6F, 1.95F);
        this.b_ = 5;
        this.bC = new Vec3D[2][4];

        for (int i = 0; i < 4; ++i) {
            this.bC[0][i] = new Vec3D(0.0D, 0.0D, 0.0D);
            this.bC[1][i] = new Vec3D(0.0D, 0.0D, 0.0D);
        }

    }

    protected void n() {
        super.n();
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new EntityIllagerWizard.b());
        this.goalSelector.a(4, new EntityIllagerIllusioner.b());
        this.goalSelector.a(5, new EntityIllagerIllusioner.a());
        this.goalSelector.a(6, new PathfinderGoalBowShoot<>(this, 0.5D, 20, 15.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.a(9, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 3.0F, 1.0F));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true, new Class[] { EntityIllagerIllusioner.class}));
        this.targetSelector.a(2, (new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true)).b(300));
        this.targetSelector.a(3, (new PathfinderGoalNearestAttackableTarget<>(this, EntityVillager.class, false)).b(300));
        this.targetSelector.a(3, (new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, false)).b(300));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.5D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(18.0D);
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(32.0D);
    }

    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.BOW));
        return super.prepare(difficultydamagescaler, groupdataentity, nbttagcompound);
    }

    protected void x_() {
        super.x_();
    }

    protected MinecraftKey getDefaultLootTable() {
        return LootTables.a;
    }

    public void movementTick() {
        super.movementTick();
        if (this.world.isClientSide && this.isInvisible()) {
            --this.c;
            if (this.c < 0) {
                this.c = 0;
            }

            if (this.hurtTicks != 1 && this.ticksLived % 1200 != 0) {
                if (this.hurtTicks == this.aC - 1) {
                    this.c = 3;

                    for (int i = 0; i < 4; ++i) {
                        this.bC[0][i] = this.bC[1][i];
                        this.bC[1][i] = new Vec3D(0.0D, 0.0D, 0.0D);
                    }
                }
            } else {
                this.c = 3;
                float f = -6.0F;
                boolean flag = true;

                int j;

                for (j = 0; j < 4; ++j) {
                    this.bC[0][j] = this.bC[1][j];
                    this.bC[1][j] = new Vec3D((double) (-6.0F + (float) this.random.nextInt(13)) * 0.5D, (double) Math.max(0, this.random.nextInt(6) - 4), (double) (-6.0F + (float) this.random.nextInt(13)) * 0.5D);
                }

                for (j = 0; j < 16; ++j) {
                    this.world.addParticle(Particles.g, this.locX + (this.random.nextDouble() - 0.5D) * (double) this.width, this.locY + this.random.nextDouble() * (double) this.length, this.locZ + (this.random.nextDouble() - 0.5D) * (double) this.width, 0.0D, 0.0D, 0.0D);
                }

                this.world.a(this.locX, this.locY, this.locZ, SoundEffects.ENTITY_ILLUSIONER_MIRROR_MOVE, this.bV(), 1.0F, 1.0F, false);
            }
        }

    }

    public boolean r(Entity entity) {
        return super.r(entity) ? true : (entity instanceof EntityLiving && ((EntityLiving) entity).getMonsterType() == EnumMonsterType.ILLAGER ? this.getScoreboardTeam() == null && entity.getScoreboardTeam() == null : false);
    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_ILLUSIONER_AMBIENT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_ILLUSIONER_DEATH;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_ILLUSIONER_HURT;
    }

    protected SoundEffect dz() {
        return SoundEffects.ENTITY_ILLUSIONER_CAST_SPELL;
    }

    public void a(EntityLiving entityliving, float f) {
        EntityArrow entityarrow = this.v(f);
        double d0 = entityliving.locX - this.locX;
        double d1 = entityliving.getBoundingBox().minY + (double) (entityliving.length / 3.0F) - entityarrow.locY;
        double d2 = entityliving.locZ - this.locZ;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);

        entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float) (14 - this.world.getDifficulty().a() * 4));
        this.a(SoundEffects.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.world.addEntity(entityarrow);
    }

    protected EntityArrow v(float f) {
        EntityTippedArrow entitytippedarrow = new EntityTippedArrow(this.world, this);

        entitytippedarrow.a((EntityLiving) this, f);
        return entitytippedarrow;
    }

    public void s(boolean flag) {
        this.a(1, flag);
    }

    class a extends EntityIllagerWizard.c {

        private int e;

        private a() {
            super();
        }

        public boolean a() {
            return !super.a() ? false : (EntityIllagerIllusioner.this.getGoalTarget() == null ? false : (EntityIllagerIllusioner.this.getGoalTarget().getId() == this.e ? false : EntityIllagerIllusioner.this.world.getDamageScaler(new BlockPosition(EntityIllagerIllusioner.this)).a((float) EnumDifficulty.NORMAL.ordinal())));
        }

        public void c() {
            super.c();
            this.e = EntityIllagerIllusioner.this.getGoalTarget().getId();
        }

        protected int g() {
            return 20;
        }

        protected int i() {
            return 180;
        }

        protected void j() {
            EntityIllagerIllusioner.this.getGoalTarget().addEffect(new MobEffect(MobEffects.BLINDNESS, 400));
        }

        protected SoundEffect k() {
            return SoundEffects.ENTITY_ILLUSIONER_PREPARE_BLINDNESS;
        }

        protected EntityIllagerWizard.Spell l() {
            return EntityIllagerWizard.Spell.BLINDNESS;
        }
    }

    class b extends EntityIllagerWizard.c {

        private b() {
            super();
        }

        public boolean a() {
            return !super.a() ? false : !EntityIllagerIllusioner.this.hasEffect(MobEffects.INVISIBILITY);
        }

        protected int g() {
            return 20;
        }

        protected int i() {
            return 340;
        }

        protected void j() {
            EntityIllagerIllusioner.this.addEffect(new MobEffect(MobEffects.INVISIBILITY, 1200));
        }

        @Nullable
        protected SoundEffect k() {
            return SoundEffects.ENTITY_ILLUSIONER_PREPARE_MIRROR;
        }

        protected EntityIllagerWizard.Spell l() {
            return EntityIllagerWizard.Spell.DISAPPEAR;
        }
    }
}
