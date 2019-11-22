package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
// CraftBukkit end

public class EntityWither extends EntityMonster implements IRangedEntity {

    private static final DataWatcherObject<Integer> b = DataWatcher.a(EntityWither.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> c = DataWatcher.a(EntityWither.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> d = DataWatcher.a(EntityWither.class, DataWatcherRegistry.b);
    private static final List<DataWatcherObject<Integer>> bz = ImmutableList.of(EntityWither.b, EntityWither.c, EntityWither.d);
    private static final DataWatcherObject<Integer> bA = DataWatcher.a(EntityWither.class, DataWatcherRegistry.b);
    private final float[] bB = new float[2];
    private final float[] bC = new float[2];
    private final float[] bD = new float[2];
    private final float[] bE = new float[2];
    private final int[] bF = new int[2];
    private final int[] bG = new int[2];
    private int bH;
    public final BossBattleServer bossBattle;
    private static final Predicate<EntityLiving> bJ = (entityliving) -> {
        return entityliving.getMonsterType() != EnumMonsterType.UNDEAD && entityliving.du();
    };
    private static final PathfinderTargetCondition bK = (new PathfinderTargetCondition()).a(20.0D).a(EntityWither.bJ);

    public EntityWither(EntityTypes<? extends EntityWither> entitytypes, World world) {
        super(entitytypes, world);
        this.bossBattle = (BossBattleServer) (new BossBattleServer(this.getScoreboardDisplayName(), BossBattle.BarColor.PURPLE, BossBattle.BarStyle.PROGRESS)).setDarkenSky(true);
        this.setHealth(this.getMaxHealth());
        this.getNavigation().d(true);
        this.f = 50;
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new EntityWither.a());
        this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0D, 40, 20.0F));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityInsentient.class, 0, false, false, EntityWither.bJ));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityWither.b, 0);
        this.datawatcher.register(EntityWither.c, 0);
        this.datawatcher.register(EntityWither.d, 0);
        this.datawatcher.register(EntityWither.bA, 0);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Invul", this.dV());
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.r(nbttagcompound.getInt("Invul"));
        if (this.hasCustomName()) {
            this.bossBattle.a(this.getScoreboardDisplayName());
        }

    }

    @Override
    public void setCustomName(@Nullable IChatBaseComponent ichatbasecomponent) {
        super.setCustomName(ichatbasecomponent);
        this.bossBattle.a(this.getScoreboardDisplayName());
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_WITHER_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_WITHER_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_WITHER_DEATH;
    }

    @Override
    public void movementTick() {
        Vec3D vec3d = this.getMot().d(1.0D, 0.6D, 1.0D);

        if (!this.world.isClientSide && this.getHeadTarget(0) > 0) {
            Entity entity = this.world.getEntity(this.getHeadTarget(0));

            if (entity != null) {
                double d0 = vec3d.y;

                if (this.locY < entity.locY || !this.dW() && this.locY < entity.locY + 5.0D) {
                    d0 = Math.max(0.0D, d0);
                    d0 += 0.3D - d0 * 0.6000000238418579D;
                }

                vec3d = new Vec3D(vec3d.x, d0, vec3d.z);
                Vec3D vec3d1 = new Vec3D(entity.locX - this.locX, 0.0D, entity.locZ - this.locZ);

                if (b(vec3d1) > 9.0D) {
                    Vec3D vec3d2 = vec3d1.d();

                    vec3d = vec3d.add(vec3d2.x * 0.3D - vec3d.x * 0.6D, 0.0D, vec3d2.z * 0.3D - vec3d.z * 0.6D);
                }
            }
        }

        this.setMot(vec3d);
        if (b(vec3d) > 0.05D) {
            this.yaw = (float) MathHelper.d(vec3d.z, vec3d.x) * 57.295776F - 90.0F;
        }

        super.movementTick();

        int i;

        for (i = 0; i < 2; ++i) {
            this.bE[i] = this.bC[i];
            this.bD[i] = this.bB[i];
        }

        int j;

        for (i = 0; i < 2; ++i) {
            j = this.getHeadTarget(i + 1);
            Entity entity1 = null;

            if (j > 0) {
                entity1 = this.world.getEntity(j);
            }

            if (entity1 != null) {
                double d1 = this.t(i + 1);
                double d2 = this.u(i + 1);
                double d3 = this.v(i + 1);
                double d4 = entity1.locX - d1;
                double d5 = entity1.locY + (double) entity1.getHeadHeight() - d2;
                double d6 = entity1.locZ - d3;
                double d7 = (double) MathHelper.sqrt(d4 * d4 + d6 * d6);
                float f = (float) (MathHelper.d(d6, d4) * 57.2957763671875D) - 90.0F;
                float f1 = (float) (-(MathHelper.d(d5, d7) * 57.2957763671875D));

                this.bB[i] = this.a(this.bB[i], f1, 40.0F);
                this.bC[i] = this.a(this.bC[i], f, 10.0F);
            } else {
                this.bC[i] = this.a(this.bC[i], this.aK, 10.0F);
            }
        }

        boolean flag = this.dW();

        for (j = 0; j < 3; ++j) {
            double d8 = this.t(j);
            double d9 = this.u(j);
            double d10 = this.v(j);

            this.world.addParticle(Particles.SMOKE, d8 + this.random.nextGaussian() * 0.30000001192092896D, d9 + this.random.nextGaussian() * 0.30000001192092896D, d10 + this.random.nextGaussian() * 0.30000001192092896D, 0.0D, 0.0D, 0.0D);
            if (flag && this.world.random.nextInt(4) == 0) {
                this.world.addParticle(Particles.ENTITY_EFFECT, d8 + this.random.nextGaussian() * 0.30000001192092896D, d9 + this.random.nextGaussian() * 0.30000001192092896D, d10 + this.random.nextGaussian() * 0.30000001192092896D, 0.699999988079071D, 0.699999988079071D, 0.5D);
            }
        }

        if (this.dV() > 0) {
            for (j = 0; j < 3; ++j) {
                this.world.addParticle(Particles.ENTITY_EFFECT, this.locX + this.random.nextGaussian(), this.locY + (double) (this.random.nextFloat() * 3.3F), this.locZ + this.random.nextGaussian(), 0.699999988079071D, 0.699999988079071D, 0.8999999761581421D);
            }
        }

    }

    @Override
    protected void mobTick() {
        int i;

        if (this.dV() > 0) {
            i = this.dV() - 1;
            if (i <= 0) {
                Explosion.Effect explosion_effect = this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) ? Explosion.Effect.DESTROY : Explosion.Effect.NONE;
                // CraftBukkit start
                // this.world.createExplosion(this, this.locX, this.locY + (double) this.getHeadHeight(), this.locZ, 7.0F, false, explosion_effect);
                ExplosionPrimeEvent event = new ExplosionPrimeEvent(this.getBukkitEntity(), 7.0F, false);
                this.world.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    this.world.createExplosion(this, this.locX, this.locY + (double) this.getHeadHeight(), this.locZ, event.getRadius(), event.getFire(), explosion_effect);
                }
                // CraftBukkit end

                // CraftBukkit start - Use relative location for far away sounds
                // this.world.b(1023, new BlockPosition(this), 0);
                // Paper start
                int viewDistance = ((WorldServer) this.world).spigotConfig.viewDistance * 16; // Paper - updated to use worlds actual view distance incase we have to uncomment this due to removal of player view distance API
                for (EntityPlayer player : ((WorldServer)world).getPlayers()) {
                    //final int viewDistance = player.getViewDistance(); // TODO apply view distance api patch
                    // Paper end
                    double deltaX = this.locX - player.locX;
                    double deltaZ = this.locZ - player.locZ;
                    double distanceSquared = deltaX * deltaX + deltaZ * deltaZ;
                    if ( world.spigotConfig.witherSpawnSoundRadius > 0 && distanceSquared > world.spigotConfig.witherSpawnSoundRadius * world.spigotConfig.witherSpawnSoundRadius ) continue; // Spigot
                    if (distanceSquared > viewDistance * viewDistance) {
                        double deltaLength = Math.sqrt(distanceSquared);
                        double relativeX = player.locX + (deltaX / deltaLength) * viewDistance;
                        double relativeZ = player.locZ + (deltaZ / deltaLength) * viewDistance;
                        player.playerConnection.sendPacket(new PacketPlayOutWorldEvent(1023, new BlockPosition((int) relativeX, (int) this.locY, (int) relativeZ), 0, true));
                    } else {
                        player.playerConnection.sendPacket(new PacketPlayOutWorldEvent(1023, new BlockPosition((int) this.locX, (int) this.locY, (int) this.locZ), 0, true));
                    }
                }
                // CraftBukkit end
            }

            this.r(i);
            if (this.ticksLived % 10 == 0) {
                this.heal(10.0F, EntityRegainHealthEvent.RegainReason.WITHER_SPAWN); // CraftBukkit
            }

        } else {
            super.mobTick();

            int j;

            for (i = 1; i < 3; ++i) {
                if (this.ticksLived >= this.bF[i - 1]) {
                    this.bF[i - 1] = this.ticksLived + 10 + this.random.nextInt(10);
                    if (this.world.getDifficulty() == EnumDifficulty.NORMAL || this.world.getDifficulty() == EnumDifficulty.HARD) {
                        int k = i - 1;
                        int l = this.bG[i - 1];

                        this.bG[k] = this.bG[i - 1] + 1;
                        if (l > 15) {
                            float f = 10.0F;
                            float f1 = 5.0F;
                            double d0 = MathHelper.a(this.random, this.locX - 10.0D, this.locX + 10.0D);
                            double d1 = MathHelper.a(this.random, this.locY - 5.0D, this.locY + 5.0D);
                            double d2 = MathHelper.a(this.random, this.locZ - 10.0D, this.locZ + 10.0D);

                            this.a(i + 1, d0, d1, d2, true);
                            this.bG[i - 1] = 0;
                        }
                    }

                    j = this.getHeadTarget(i);
                    if (j > 0) {
                        Entity entity = this.world.getEntity(j);

                        if (entity != null && entity.isAlive() && this.h(entity) <= 900.0D && this.hasLineOfSight(entity)) {
                            if (entity instanceof EntityHuman && ((EntityHuman) entity).abilities.isInvulnerable) {
                                this.setHeadTarget(i, 0);
                            } else {
                                this.a(i + 1, (EntityLiving) entity);
                                this.bF[i - 1] = this.ticksLived + 40 + this.random.nextInt(20);
                                this.bG[i - 1] = 0;
                            }
                        } else {
                            this.setHeadTarget(i, 0);
                        }
                    } else {
                        List<EntityLiving> list = this.world.a(EntityLiving.class, EntityWither.bK, this, this.getBoundingBox().grow(20.0D, 8.0D, 20.0D));

                        for (int i1 = 0; i1 < 10 && !list.isEmpty(); ++i1) {
                            EntityLiving entityliving = (EntityLiving) list.get(this.random.nextInt(list.size()));

                            if (entityliving != this && entityliving.isAlive() && this.hasLineOfSight(entityliving)) {
                                if (entityliving instanceof EntityHuman) {
                                    if (!((EntityHuman) entityliving).abilities.isInvulnerable) {
                                        if (CraftEventFactory.callEntityTargetLivingEvent(this, entityliving, EntityTargetEvent.TargetReason.CLOSEST_PLAYER).isCancelled()) continue; // CraftBukkit
                                        this.setHeadTarget(i, entityliving.getId());
                                    }
                                } else {
                                    if (CraftEventFactory.callEntityTargetLivingEvent(this, entityliving, EntityTargetEvent.TargetReason.CLOSEST_ENTITY).isCancelled()) continue; // CraftBukkit
                                    this.setHeadTarget(i, entityliving.getId());
                                }
                                break;
                            }

                            list.remove(entityliving);
                        }
                    }
                }
            }

            if (this.getGoalTarget() != null) {
                this.setHeadTarget(0, this.getGoalTarget().getId());
            } else {
                this.setHeadTarget(0, 0);
            }

            if (this.bH > 0) {
                --this.bH;
                if (this.bH == 0 && this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
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

                                if (b(iblockdata)) {
                                    // CraftBukkit start
                                    if (CraftEventFactory.callEntityChangeBlockEvent(this, blockposition, Blocks.AIR.getBlockData()).isCancelled()) {
                                        continue;
                                    }
                                    // CraftBukkit end
                                    flag = this.world.b(blockposition, true) || flag;
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
                this.heal(1.0F, EntityRegainHealthEvent.RegainReason.REGEN); // CraftBukkit
            }

            this.bossBattle.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    public static boolean b(IBlockData iblockdata) {
        return !iblockdata.isAir() && !TagsBlock.WITHER_IMMUNE.isTagged(iblockdata.getBlock());
    }

    public void l() {
        this.r(220);
        this.setHealth(this.getMaxHealth() / 3.0F);
    }

    @Override
    public void a(IBlockData iblockdata, Vec3D vec3d) {}

    @Override
    public void b(EntityPlayer entityplayer) {
        super.b(entityplayer);
        this.bossBattle.addPlayer(entityplayer);
    }

    @Override
    public void c(EntityPlayer entityplayer) {
        super.c(entityplayer);
        this.bossBattle.removePlayer(entityplayer);
    }

    private double t(int i) {
        if (i <= 0) {
            return this.locX;
        } else {
            float f = (this.aK + (float) (180 * (i - 1))) * 0.017453292F;
            float f1 = MathHelper.cos(f);

            return this.locX + (double) f1 * 1.3D;
        }
    }

    private double u(int i) {
        return i <= 0 ? this.locY + 3.0D : this.locY + 2.2D;
    }

    private double v(int i) {
        if (i <= 0) {
            return this.locZ;
        } else {
            float f = (this.aK + (float) (180 * (i - 1))) * 0.017453292F;
            float f1 = MathHelper.sin(f);

            return this.locZ + (double) f1 * 1.3D;
        }
    }

    private float a(float f, float f1, float f2) {
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
        double d3 = this.t(i);
        double d4 = this.u(i);
        double d5 = this.v(i);
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

    @Override
    public void a(EntityLiving entityliving, float f) {
        this.a(0, entityliving);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (damagesource != DamageSource.DROWN && !(damagesource.getEntity() instanceof EntityWither)) {
            if (this.dV() > 0 && damagesource != DamageSource.OUT_OF_WORLD) {
                return false;
            } else {
                Entity entity;

                if (this.dW()) {
                    entity = damagesource.j();
                    if (entity instanceof EntityArrow) {
                        return false;
                    }
                }

                entity = damagesource.getEntity();
                if (entity != null && !(entity instanceof EntityHuman) && entity instanceof EntityLiving && ((EntityLiving) entity).getMonsterType() == this.getMonsterType()) {
                    return false;
                } else {
                    if (this.bH <= 0) {
                        this.bH = 20;
                    }

                    for (int i = 0; i < this.bG.length; ++i) {
                        this.bG[i] += 3;
                    }

                    return super.damageEntity(damagesource, f);
                }
            }
        } else {
            return false;
        }
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropDeathLoot(damagesource, i, flag);
        EntityItem entityitem = this.a((IMaterial) Items.NETHER_STAR);

        if (entityitem != null) {
            entityitem.s();
        }

    }

    @Override
    protected void checkDespawn() {
        this.ticksFarFromPlayer = 0;
    }

    @Override
    public void b(float f, float f1) {}

    @Override
    public boolean addEffect(MobEffect mobeffect) {
        return false;
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(300.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.6000000238418579D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(40.0D);
        this.getAttributeInstance(GenericAttributes.ARMOR).setValue(4.0D);
    }

    public int dV() {
        return (Integer) this.datawatcher.get(EntityWither.bA);
    }

    public void r(int i) {
        this.datawatcher.set(EntityWither.bA, i);
    }

    public int getHeadTarget(int i) {
        return (Integer) this.datawatcher.get((DataWatcherObject) EntityWither.bz.get(i));
    }

    public void setHeadTarget(int i, int j) {
        this.datawatcher.set((DataWatcherObject) EntityWither.bz.get(i), j);
    }

    public boolean dW() {
        return this.getHealth() <= this.getMaxHealth() / 2.0F;
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    @Override
    protected boolean n(Entity entity) {
        return false;
    }

    @Override
    public boolean canPortal() {
        return false;
    }

    @Override
    public boolean d(MobEffect mobeffect) {
        return mobeffect.getMobEffect() == MobEffects.WITHER ? false : super.d(mobeffect);
    }

    class a extends PathfinderGoal {

        public a() {
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.JUMP, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            return EntityWither.this.dV() > 0;
        }
    }
}
