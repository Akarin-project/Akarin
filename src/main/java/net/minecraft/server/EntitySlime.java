package net.minecraft.server;

import javax.annotation.Nullable;

public class EntitySlime extends EntityInsentient implements IMonster {

    private static final DataWatcherObject<Integer> bC = DataWatcher.a(EntitySlime.class, DataWatcherRegistry.b);
    public float a;
    public float b;
    public float c;
    private boolean bD;

    protected EntitySlime(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.moveController = new EntitySlime.ControllerMoveSlime(this);
    }

    public EntitySlime(World world) {
        this(EntityTypes.SLIME, world);
    }

    protected void n() {
        this.goalSelector.a(1, new EntitySlime.PathfinderGoalSlimeRandomJump(this));
        this.goalSelector.a(2, new EntitySlime.PathfinderGoalSlimeNearestPlayer(this));
        this.goalSelector.a(3, new EntitySlime.PathfinderGoalSlimeRandomDirection(this));
        this.goalSelector.a(5, new EntitySlime.PathfinderGoalSlimeIdle(this));
        this.targetSelector.a(1, new PathfinderGoalTargetNearestPlayer(this));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTargetInsentient(this, EntityIronGolem.class));
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntitySlime.bC, 1);
    }

    public void setSize(int i, boolean flag) {
        this.datawatcher.set(EntitySlime.bC, i);
        this.setSize(0.51000005F * (float) i, 0.51000005F * (float) i);
        this.setPosition(this.locX, this.locY, this.locZ);
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue((double) (i * i));
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue((double) (0.2F + 0.1F * (float) i));
        if (flag) {
            this.setHealth(this.getMaxHealth());
        }

        this.b_ = i;
    }

    public int getSize() {
        return (Integer) this.datawatcher.get(EntitySlime.bC);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Size", this.getSize() - 1);
        nbttagcompound.setBoolean("wasOnGround", this.bD);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        int i = nbttagcompound.getInt("Size");

        if (i < 0) {
            i = 0;
        }

        this.setSize(i + 1, false);
        this.bD = nbttagcompound.getBoolean("wasOnGround");
    }

    public boolean dy() {
        return this.getSize() <= 1;
    }

    protected ParticleParam l() {
        return Particles.D;
    }

    public void tick() {
        if (!this.world.isClientSide && this.world.getDifficulty() == EnumDifficulty.PEACEFUL && this.getSize() > 0) {
            this.dead = true;
        }

        this.b += (this.a - this.b) * 0.5F;
        this.c = this.b;
        super.tick();
        if (this.onGround && !this.bD) {
            int i = this.getSize();

            for (int j = 0; j < i * 8; ++j) {
                float f = this.random.nextFloat() * 6.2831855F;
                float f1 = this.random.nextFloat() * 0.5F + 0.5F;
                float f2 = MathHelper.sin(f) * (float) i * 0.5F * f1;
                float f3 = MathHelper.cos(f) * (float) i * 0.5F * f1;
                World world = this.world;
                ParticleParam particleparam = this.l();
                double d0 = this.locX + (double) f2;
                double d1 = this.locZ + (double) f3;

                world.addParticle(particleparam, d0, this.getBoundingBox().minY, d1, 0.0D, 0.0D, 0.0D);
            }

            this.a(this.dv(), this.cD(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            this.a = -0.5F;
        } else if (!this.onGround && this.bD) {
            this.a = 1.0F;
        }

        this.bD = this.onGround;
        this.ds();
    }

    protected void ds() {
        this.a *= 0.6F;
    }

    protected int dr() {
        return this.random.nextInt(20) + 10;
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntitySlime.bC.equals(datawatcherobject)) {
            int i = this.getSize();

            this.setSize(0.51000005F * (float) i, 0.51000005F * (float) i);
            this.yaw = this.aS;
            this.aQ = this.aS;
            if (this.isInWater() && this.random.nextInt(20) == 0) {
                this.au();
            }
        }

        super.a(datawatcherobject);
    }

    public EntityTypes<? extends EntitySlime> P() {
        return super.P();
    }

    public void die() {
        int i = this.getSize();

        if (!this.world.isClientSide && i > 1 && this.getHealth() <= 0.0F) {
            int j = 2 + this.random.nextInt(3);

            for (int k = 0; k < j; ++k) {
                float f = ((float) (k % 2) - 0.5F) * (float) i / 4.0F;
                float f1 = ((float) (k / 2) - 0.5F) * (float) i / 4.0F;
                EntitySlime entityslime = (EntitySlime) this.P().a(this.world);

                if (this.hasCustomName()) {
                    entityslime.setCustomName(this.getCustomName());
                }

                if (this.isPersistent()) {
                    entityslime.di();
                }

                entityslime.setSize(i / 2, true);
                entityslime.setPositionRotation(this.locX + (double) f, this.locY + 0.5D, this.locZ + (double) f1, this.random.nextFloat() * 360.0F, 0.0F);
                this.world.addEntity(entityslime);
            }
        }

        super.die();
    }

    public void collide(Entity entity) {
        super.collide(entity);
        if (entity instanceof EntityIronGolem && this.dt()) {
            this.f((EntityLiving) entity);
        }

    }

    public void d(EntityHuman entityhuman) {
        if (this.dt()) {
            this.f((EntityLiving) entityhuman);
        }

    }

    protected void f(EntityLiving entityliving) {
        int i = this.getSize();

        if (this.hasLineOfSight(entityliving) && this.h(entityliving) < 0.6D * (double) i * 0.6D * (double) i && entityliving.damageEntity(DamageSource.mobAttack(this), (float) this.du())) {
            this.a(SoundEffects.ENTITY_SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.a((EntityLiving) this, (Entity) entityliving);
        }

    }

    public float getHeadHeight() {
        return 0.625F * this.length;
    }

    protected boolean dt() {
        return !this.dy() && this.cP();
    }

    protected int du() {
        return this.getSize();
    }

    protected SoundEffect d(DamageSource damagesource) {
        return this.dy() ? SoundEffects.ENTITY_SLIME_HURT_SMALL : SoundEffects.ENTITY_SLIME_HURT;
    }

    protected SoundEffect cs() {
        return this.dy() ? SoundEffects.ENTITY_SLIME_DEATH_SMALL : SoundEffects.ENTITY_SLIME_DEATH;
    }

    protected SoundEffect dv() {
        return this.dy() ? SoundEffects.ENTITY_SLIME_SQUISH_SMALL : SoundEffects.ENTITY_SLIME_SQUISH;
    }

    protected Item getLoot() {
        return this.getSize() == 1 ? Items.SLIME_BALL : null;
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return this.getSize() == 1 ? LootTables.ao : LootTables.a;
    }

    public boolean a(GeneratorAccess generatoraccess, boolean flag) {
        BlockPosition blockposition = new BlockPosition(MathHelper.floor(this.locX), 0, MathHelper.floor(this.locZ));

        if (generatoraccess.getWorldData().getType() == WorldType.FLAT && this.random.nextInt(4) != 1) {
            return false;
        } else {
            if (generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL) {
                BiomeBase biomebase = generatoraccess.getBiome(blockposition);

                if (biomebase == Biomes.SWAMP && this.locY > 50.0D && this.locY < 70.0D && this.random.nextFloat() < 0.5F && this.random.nextFloat() < generatoraccess.ah() && generatoraccess.getLightLevel(new BlockPosition(this)) <= this.random.nextInt(8)) {
                    return super.a(generatoraccess, flag);
                }

                ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(blockposition);
                boolean flag1 = SeededRandom.a(chunkcoordintpair.x, chunkcoordintpair.z, generatoraccess.getSeed(), 987234911L).nextInt(10) == 0;

                if (this.random.nextInt(10) == 0 && flag1 && this.locY < 40.0D) {
                    return super.a(generatoraccess, flag);
                }
            }

            return false;
        }
    }

    protected float cD() {
        return 0.4F * (float) this.getSize();
    }

    public int K() {
        return 0;
    }

    protected boolean dz() {
        return this.getSize() > 0;
    }

    protected void cH() {
        this.motY = 0.41999998688697815D;
        this.impulse = true;
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        int i = this.random.nextInt(3);

        if (i < 2 && this.random.nextFloat() < 0.5F * difficultydamagescaler.d()) {
            ++i;
        }

        int j = 1 << i;

        this.setSize(j, true);
        return super.prepare(difficultydamagescaler, groupdataentity, nbttagcompound);
    }

    protected SoundEffect dw() {
        return this.dy() ? SoundEffects.ENTITY_SLIME_JUMP_SMALL : SoundEffects.ENTITY_SLIME_JUMP;
    }

    static class PathfinderGoalSlimeIdle extends PathfinderGoal {

        private final EntitySlime a;

        public PathfinderGoalSlimeIdle(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(5);
        }

        public boolean a() {
            return true;
        }

        public void e() {
            ((EntitySlime.ControllerMoveSlime) this.a.getControllerMove()).a(1.0D);
        }
    }

    static class PathfinderGoalSlimeRandomJump extends PathfinderGoal {

        private final EntitySlime a;

        public PathfinderGoalSlimeRandomJump(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(5);
            ((Navigation) entityslime.getNavigation()).d(true);
        }

        public boolean a() {
            return this.a.isInWater() || this.a.ax();
        }

        public void e() {
            if (this.a.getRandom().nextFloat() < 0.8F) {
                this.a.getControllerJump().a();
            }

            ((EntitySlime.ControllerMoveSlime) this.a.getControllerMove()).a(1.2D);
        }
    }

    static class PathfinderGoalSlimeRandomDirection extends PathfinderGoal {

        private final EntitySlime a;
        private float b;
        private int c;

        public PathfinderGoalSlimeRandomDirection(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(2);
        }

        public boolean a() {
            return this.a.getGoalTarget() == null && (this.a.onGround || this.a.isInWater() || this.a.ax() || this.a.hasEffect(MobEffects.LEVITATION));
        }

        public void e() {
            if (--this.c <= 0) {
                this.c = 40 + this.a.getRandom().nextInt(60);
                this.b = (float) this.a.getRandom().nextInt(360);
            }

            ((EntitySlime.ControllerMoveSlime) this.a.getControllerMove()).a(this.b, false);
        }
    }

    static class PathfinderGoalSlimeNearestPlayer extends PathfinderGoal {

        private final EntitySlime a;
        private int b;

        public PathfinderGoalSlimeNearestPlayer(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(2);
        }

        public boolean a() {
            EntityLiving entityliving = this.a.getGoalTarget();

            return entityliving == null ? false : (!entityliving.isAlive() ? false : !(entityliving instanceof EntityHuman) || !((EntityHuman) entityliving).abilities.isInvulnerable);
        }

        public void c() {
            this.b = 300;
            super.c();
        }

        public boolean b() {
            EntityLiving entityliving = this.a.getGoalTarget();

            return entityliving == null ? false : (!entityliving.isAlive() ? false : (entityliving instanceof EntityHuman && ((EntityHuman) entityliving).abilities.isInvulnerable ? false : --this.b > 0));
        }

        public void e() {
            this.a.a((Entity) this.a.getGoalTarget(), 10.0F, 10.0F);
            ((EntitySlime.ControllerMoveSlime) this.a.getControllerMove()).a(this.a.yaw, this.a.dt());
        }
    }

    static class ControllerMoveSlime extends ControllerMove {

        private float i;
        private int j;
        private final EntitySlime k;
        private boolean l;

        public ControllerMoveSlime(EntitySlime entityslime) {
            super(entityslime);
            this.k = entityslime;
            this.i = 180.0F * entityslime.yaw / 3.1415927F;
        }

        public void a(float f, boolean flag) {
            this.i = f;
            this.l = flag;
        }

        public void a(double d0) {
            this.e = d0;
            this.h = ControllerMove.Operation.MOVE_TO;
        }

        public void a() {
            this.a.yaw = this.a(this.a.yaw, this.i, 90.0F);
            this.a.aS = this.a.yaw;
            this.a.aQ = this.a.yaw;
            if (this.h != ControllerMove.Operation.MOVE_TO) {
                this.a.r(0.0F);
            } else {
                this.h = ControllerMove.Operation.WAIT;
                if (this.a.onGround) {
                    this.a.o((float) (this.e * this.a.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue()));
                    if (this.j-- <= 0) {
                        this.j = this.k.dr();
                        if (this.l) {
                            this.j /= 3;
                        }

                        this.k.getControllerJump().a();
                        if (this.k.dz()) {
                            this.k.a(this.k.dw(), this.k.cD(), ((this.k.getRandom().nextFloat() - this.k.getRandom().nextFloat()) * 0.2F + 1.0F) * 0.8F);
                        }
                    } else {
                        this.k.bh = 0.0F;
                        this.k.bj = 0.0F;
                        this.a.o(0.0F);
                    }
                } else {
                    this.a.o((float) (this.e * this.a.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue()));
                }

            }
        }
    }
}
