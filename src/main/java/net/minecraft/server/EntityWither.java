package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityWither extends EntityMonster implements IRangedEntity {

    private static final DataWatcherObject<Integer> a = DataWatcher.a(EntityWither.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> b = DataWatcher.a(EntityWither.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> c = DataWatcher.a(EntityWither.class, DataWatcherRegistry.b);
    private static final List<DataWatcherObject<Integer>> bC = ImmutableList.of(EntityWither.a, EntityWither.b, EntityWither.c);
    private static final DataWatcherObject<Integer> bD = DataWatcher.a(EntityWither.class, DataWatcherRegistry.b);
    private final float[] bE = new float[2];
    private final float[] bF = new float[2];
    private final float[] bG = new float[2];
    private final float[] bH = new float[2];
    private final int[] bI = new int[2];
    private final int[] bJ = new int[2];
    private int bK;
    public final BossBattleServer bossBattle;
    private static final Predicate<Entity> bM = (entity) -> {
        return entity instanceof EntityLiving && ((EntityLiving) entity).getMonsterType() != EnumMonsterType.UNDEAD && ((EntityLiving) entity).df();
    };

    public EntityWither(World world) {
        super(EntityTypes.WITHER, world);
        this.bossBattle = (BossBattleServer) (new BossBattleServer(this.getScoreboardDisplayName(), BossBattle.BarColor.PURPLE, BossBattle.BarStyle.PROGRESS)).setDarkenSky(true);
        this.setHealth(this.getMaxHealth());
        this.setSize(0.9F, 3.5F);
        this.fireProof = true;
        ((Navigation) this.getNavigation()).d(true);
        this.b_ = 50;
    }

    protected void n() {
        this.goalSelector.a(0, new EntityWither.a());
        this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0D, 40, 20.0F));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityInsentient.class, 0, false, false, EntityWither.bM));
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityWither.a, 0);
        this.datawatcher.register(EntityWither.b, 0);
        this.datawatcher.register(EntityWither.c, 0);
        this.datawatcher.register(EntityWither.bD, 0);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Invul", this.dz());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.d(nbttagcompound.getInt("Invul"));
        if (this.hasCustomName()) {
            this.bossBattle.a(this.getScoreboardDisplayName());
        }

    }

    public void setCustomName(@Nullable IChatBaseComponent ichatbasecomponent) {
        super.setCustomName(ichatbasecomponent);
        this.bossBattle.a(this.getScoreboardDisplayName());
    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_WITHER_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_WITHER_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_WITHER_DEATH;
    }

    public void movementTick() {
        this.motY *= 0.6000000238418579D;
        double d0;
        double d1;
        double d2;

        if (!this.world.isClientSide && this.p(0) > 0) {
            Entity entity = this.world.getEntity(this.p(0));

            if (entity != null) {
                if (this.locY < entity.locY || !this.dA() && this.locY < entity.locY + 5.0D) {
                    if (this.motY < 0.0D) {
                        this.motY = 0.0D;
                    }

                    this.motY += (0.5D - this.motY) * 0.6000000238418579D;
                }

                double d3 = entity.locX - this.locX;

                d0 = entity.locZ - this.locZ;
                d1 = d3 * d3 + d0 * d0;
                if (d1 > 9.0D) {
                    d2 = (double) MathHelper.sqrt(d1);
                    this.motX += (d3 / d2 * 0.5D - this.motX) * 0.6000000238418579D;
                    this.motZ += (d0 / d2 * 0.5D - this.motZ) * 0.6000000238418579D;
                }
            }
        }

        if (this.motX * this.motX + this.motZ * this.motZ > 0.05000000074505806D) {
            this.yaw = (float) MathHelper.c(this.motZ, this.motX) * 57.295776F - 90.0F;
        }

        super.movementTick();

        int i;

        for (i = 0; i < 2; ++i) {
            this.bH[i] = this.bF[i];
            this.bG[i] = this.bE[i];
        }

        int j;

        for (i = 0; i < 2; ++i) {
            j = this.p(i + 1);
            Entity entity1 = null;

            if (j > 0) {
                entity1 = this.world.getEntity(j);
            }

            if (entity1 != null) {
                d0 = this.q(i + 1);
                d1 = this.r(i + 1);
                d2 = this.s(i + 1);
                double d4 = entity1.locX - d0;
                double d5 = entity1.locY + (double) entity1.getHeadHeight() - d1;
                double d6 = entity1.locZ - d2;
                double d7 = (double) MathHelper.sqrt(d4 * d4 + d6 * d6);
                float f = (float) (MathHelper.c(d6, d4) * 57.2957763671875D) - 90.0F;
                float f1 = (float) (-(MathHelper.c(d5, d7) * 57.2957763671875D));

                this.bE[i] = this.c(this.bE[i], f1, 40.0F);
                this.bF[i] = this.c(this.bF[i], f, 10.0F);
            } else {
                this.bF[i] = this.c(this.bF[i], this.aQ, 10.0F);
            }
        }

        boolean flag = this.dA();

        for (j = 0; j < 3; ++j) {
            double d8 = this.q(j);
            double d9 = this.r(j);
            double d10 = this.s(j);

            this.world.addParticle(Particles.M, d8 + this.random.nextGaussian() * 0.30000001192092896D, d9 + this.random.nextGaussian() * 0.30000001192092896D, d10 + this.random.nextGaussian() * 0.30000001192092896D, 0.0D, 0.0D, 0.0D);
            if (flag && this.world.random.nextInt(4) == 0) {
                this.world.addParticle(Particles.s, d8 + this.random.nextGaussian() * 0.30000001192092896D, d9 + this.random.nextGaussian() * 0.30000001192092896D, d10 + this.random.nextGaussian() * 0.30000001192092896D, 0.699999988079071D, 0.699999988079071D, 0.5D);
            }
        }

        if (this.dz() > 0) {
            for (j = 0; j < 3; ++j) {
                this.world.addParticle(Particles.s, this.locX + this.random.nextGaussian(), this.locY + (double) (this.random.nextFloat() * 3.3F), this.locZ + this.random.nextGaussian(), 0.699999988079071D, 0.699999988079071D, 0.8999999761581421D);
            }
        }

    }

    protected void mobTick() {
        int i;

        if (this.dz() > 0) {
            i = this.dz() - 1;
            if (i <= 0) {
                this.world.createExplosion(this, this.locX, this.locY + (double) this.getHeadHeight(), this.locZ, 7.0F, false, this.world.getGameRules().getBoolean("mobGriefing"));
                this.world.a(1023, new BlockPosition(this), 0);
            }

            this.d(i);
            if (this.ticksLived % 10 == 0) {
                this.heal(10.0F);
            }

        } else {
            super.mobTick();

            int j;

            for (i = 1; i < 3; ++i) {
                if (this.ticksLived >= this.bI[i - 1]) {
                    this.bI[i - 1] = this.ticksLived + 10 + this.random.nextInt(10);
                    if (this.world.getDifficulty() == EnumDifficulty.NORMAL || this.world.getDifficulty() == EnumDifficulty.HARD) {
                        int k = i - 1;
                        int l = this.bJ[i - 1];

                        this.bJ[k] = this.bJ[i - 1] + 1;
                        if (l > 15) {
                            float f = 10.0F;
                            float f1 = 5.0F;
                            double d0 = MathHelper.a(this.random, this.locX - 10.0D, this.locX + 10.0D);
                            double d1 = MathHelper.a(this.random, this.locY - 5.0D, this.locY + 5.0D);
                            double d2 = MathHelper.a(this.random, this.locZ - 10.0D, this.locZ + 10.0D);

                            this.a(i + 1, d0, d1, d2, true);
                            this.bJ[i - 1] = 0;
                        }
                    }

                    j = this.p(i);
                    if (j > 0) {
                        Entity entity = this.world.getEntity(j);

                        if (entity != null && entity.isAlive() && this.h(entity) <= 900.0D && this.hasLineOfSight(entity)) {
                            if (entity instanceof EntityHuman && ((EntityHuman) entity).abilities.isInvulnerable) {
                                this.a(i, 0);
                            } else {
                                this.a(i + 1, (EntityLiving) entity);
                                this.bI[i - 1] = this.ticksLived + 40 + this.random.nextInt(20);
                                this.bJ[i - 1] = 0;
                            }
                        } else {
                            this.a(i, 0);
                        }
                    } else {
                        List<EntityLiving> list = this.world.a(EntityLiving.class, this.getBoundingBox().grow(20.0D, 8.0D, 20.0D), EntityWither.bM.and(IEntitySelector.f));

                        for (int i1 = 0; i1 < 10 && !list.isEmpty(); ++i1) {
                            EntityLiving entityliving = (EntityLiving) list.get(this.random.nextInt(list.size()));

                            if (entityliving != this && entityliving.isAlive() && this.hasLineOfSight(entityliving)) {
                                if (entityliving instanceof EntityHuman) {
                                    if (!((EntityHuman) entityliving).abilities.isInvulnerable) {
                                        this.a(i, entityliving.getId());
                                    }
                                } else {
                                    this.a(i, entityliving.getId());
                                }
                                break;
                            }

                            list.remove(entityliving);
                        }
                    }
                }
            }

            if (this.getGoalTarget() != null) {
                this.a(0, this.getGoalTarget().getId());
            } else {
                this.a(0, 0);
            }

            if (this.bK > 0) {
                --this.bK;
                if (this.bK == 0 && this.world.getGameRules().getBoolean("mobGriefing")) {
                    i = MathHelper.floor(this.locY);
                    j = MathHelper.floor(this.locX);
                    int j1 = MathHelper.floor(this.locZ);
                    boolean flag = false;

                    for (int k1 = -1; k1 <= 1; ++k1) {
                        for (int l1 = -1; l1 <= 1; ++l1) {
                            for (int i2 = 0; i2 <= 3; ++i2) {
                                int j2 = j + k1;
                                int k2 = i + i2;
                                int l2 = j1 + l1;
                                BlockPosition blockposition = new BlockPosition(j2, k2, l2);
                                IBlockData iblockdata = this.world.getType(blockposition);
                                Block block = iblockdata.getBlock();

                                if (!iblockdata.isAir() && a(block)) {
                                    flag = this.world.setAir(blockposition, true) || flag;
                                }
                            }
                        }
                    }

                    if (flag) {
                        this.world.a((EntityHuman) null, 1022, new BlockPosition(this), 0);
                    }
                }
            }

            if (this.ticksLived % 20 == 0) {
                this.heal(1.0F);
            }

            this.bossBattle.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    public static boolean a(Block block) {
        return block != Blocks.BEDROCK && block != Blocks.END_PORTAL && block != Blocks.END_PORTAL_FRAME && block != Blocks.COMMAND_BLOCK && block != Blocks.REPEATING_COMMAND_BLOCK && block != Blocks.CHAIN_COMMAND_BLOCK && block != Blocks.BARRIER && block != Blocks.STRUCTURE_BLOCK && block != Blocks.STRUCTURE_VOID && block != Blocks.MOVING_PISTON && block != Blocks.END_GATEWAY;
    }

    public void l() {
        this.d(220);
        this.setHealth(this.getMaxHealth() / 3.0F);
    }

    public void bh() {}

    public void b(EntityPlayer entityplayer) {
        super.b(entityplayer);
        this.bossBattle.addPlayer(entityplayer);
    }

    public void c(EntityPlayer entityplayer) {
        super.c(entityplayer);
        this.bossBattle.removePlayer(entityplayer);
    }

    private double q(int i) {
        if (i <= 0) {
            return this.locX;
        } else {
            float f = (this.aQ + (float) (180 * (i - 1))) * 0.017453292F;
            float f1 = MathHelper.cos(f);

            return this.locX + (double) f1 * 1.3D;
        }
    }

    private double r(int i) {
        return i <= 0 ? this.locY + 3.0D : this.locY + 2.2D;
    }

    private double s(int i) {
        if (i <= 0) {
            return this.locZ;
        } else {
            float f = (this.aQ + (float) (180 * (i - 1))) * 0.017453292F;
            float f1 = MathHelper.sin(f);

            return this.locZ + (double) f1 * 1.3D;
        }
    }

    private float c(float f, float f1, float f2) {
        float f3 = MathHelper.g(f1 - f);

        if (f3 > f2) {
            f3 = f2;
        }

        if (f3 < -f2) {
            f3 = -f2;
        }

        return f + f3;
    }

    private void a(int i, EntityLiving entityliving) {
        this.a(i, entityliving.locX, entityliving.locY + (double) entityliving.getHeadHeight() * 0.5D, entityliving.locZ, i == 0 && this.random.nextFloat() < 0.001F);
    }

    private void a(int i, double d0, double d1, double d2, boolean flag) {
        this.world.a((EntityHuman) null, 1024, new BlockPosition(this), 0);
        double d3 = this.q(i);
        double d4 = this.r(i);
        double d5 = this.s(i);
        double d6 = d0 - d3;
        double d7 = d1 - d4;
        double d8 = d2 - d5;
        EntityWitherSkull entitywitherskull = new EntityWitherSkull(this.world, this, d6, d7, d8);

        if (flag) {
            entitywitherskull.setCharged(true);
        }

        entitywitherskull.locY = d4;
        entitywitherskull.locX = d3;
        entitywitherskull.locZ = d5;
        this.world.addEntity(entitywitherskull);
    }

    public void a(EntityLiving entityliving, float f) {
        this.a(0, entityliving);
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (damagesource != DamageSource.DROWN && !(damagesource.getEntity() instanceof EntityWither)) {
            if (this.dz() > 0 && damagesource != DamageSource.OUT_OF_WORLD) {
                return false;
            } else {
                Entity entity;

                if (this.dA()) {
                    entity = damagesource.j();
                    if (entity instanceof EntityArrow) {
                        return false;
                    }
                }

                entity = damagesource.getEntity();
                if (entity != null && !(entity instanceof EntityHuman) && entity instanceof EntityLiving && ((EntityLiving) entity).getMonsterType() == this.getMonsterType()) {
                    return false;
                } else {
                    if (this.bK <= 0) {
                        this.bK = 20;
                    }

                    for (int i = 0; i < this.bJ.length; ++i) {
                        this.bJ[i] += 3;
                    }

                    return super.damageEntity(damagesource, f);
                }
            }
        } else {
            return false;
        }
    }

    protected void dropDeathLoot(boolean flag, int i) {
        EntityItem entityitem = this.a((IMaterial) Items.NETHER_STAR);

        if (entityitem != null) {
            entityitem.s();
        }

    }

    protected void I() {
        this.ticksFarFromPlayer = 0;
    }

    public void c(float f, float f1) {}

    public boolean addEffect(MobEffect mobeffect) {
        return false;
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(300.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.6000000238418579D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(40.0D);
        this.getAttributeInstance(GenericAttributes.h).setValue(4.0D);
    }

    public int dz() {
        return (Integer) this.datawatcher.get(EntityWither.bD);
    }

    public void d(int i) {
        this.datawatcher.set(EntityWither.bD, i);
    }

    public int p(int i) {
        return (Integer) this.datawatcher.get((DataWatcherObject) EntityWither.bC.get(i));
    }

    public void a(int i, int j) {
        this.datawatcher.set((DataWatcherObject) EntityWither.bC.get(i), j);
    }

    public boolean dA() {
        return this.getHealth() <= this.getMaxHealth() / 2.0F;
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    protected boolean n(Entity entity) {
        return false;
    }

    public boolean bm() {
        return false;
    }

    public void s(boolean flag) {}

    class a extends PathfinderGoal {

        public a() {
            this.a(7);
        }

        public boolean a() {
            return EntityWither.this.dz() > 0;
        }
    }
}
