package net.minecraft.server;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
// Paper start
import com.destroystokyo.paper.event.entity.SlimeChangeDirectionEvent;
import com.destroystokyo.paper.event.entity.SlimeSwimEvent;
import com.destroystokyo.paper.event.entity.SlimeTargetLivingEntityEvent;
import com.destroystokyo.paper.event.entity.SlimeWanderEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
// Paper end
// CraftBukkit start
import java.util.ArrayList;
import java.util.List;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
// CraftBukkit end

public class EntitySlime extends EntityInsentient implements IMonster {

    private static final DataWatcherObject<Integer> bz = DataWatcher.a(EntitySlime.class, DataWatcherRegistry.b);
    public float b;
    public float c;
    public float d;
    private boolean bA;

    public EntitySlime(EntityTypes<? extends EntitySlime> entitytypes, World world) {
        super(entitytypes, world);
        this.moveController = new EntitySlime.ControllerMoveSlime(this);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(1, new EntitySlime.PathfinderGoalSlimeRandomJump(this));
        this.goalSelector.a(2, new EntitySlime.PathfinderGoalSlimeNearestPlayer(this));
        this.goalSelector.a(3, new EntitySlime.PathfinderGoalSlimeRandomDirection(this));
        this.goalSelector.a(5, new EntitySlime.PathfinderGoalSlimeIdle(this));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, (entityliving) -> {
            return Math.abs(entityliving.locY - this.locY) <= 4.0D;
        }));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntitySlime.bz, 1);
    }

    public void setSize(int i, boolean flag) {
        this.datawatcher.set(EntitySlime.bz, i);
        this.setPosition(this.locX, this.locY, this.locZ);
        this.updateSize();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue((double) (i * i));
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue((double) (0.2F + 0.1F * (float) i));
        if (flag) {
            this.setHealth(this.getMaxHealth());
        }

        this.f = i;
    }

    public int getSize() {
        return (Integer) this.datawatcher.get(EntitySlime.bz);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Size", this.getSize() - 1);
        nbttagcompound.setBoolean("wasOnGround", this.bA);
        nbttagcompound.setBoolean("Paper.canWander", this.canWander); // Paper
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        int i = nbttagcompound.getInt("Size");

        if (i < 0) {
            i = 0;
        }

        this.setSize(i + 1, false);
        this.bA = nbttagcompound.getBoolean("wasOnGround");
        // Paper start - check exists before loading or this will be loaded as false
        if (nbttagcompound.hasKey("Paper.canWander")) {
            this.canWander = nbttagcompound.getBoolean("Paper.canWander");
        }
        // Paper end
    }

    public boolean ea() {
        return this.getSize() <= 1;
    }

    protected ParticleParam l() {
        return Particles.ITEM_SLIME;
    }

    @Override
    public void tick() {
        if (!this.world.isClientSide && this.world.getDifficulty() == EnumDifficulty.PEACEFUL && this.getSize() > 0) {
            this.dead = true;
        }

        this.c += (this.b - this.c) * 0.5F;
        this.d = this.c;
        super.tick();
        if (this.onGround && !this.bA) {
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

            this.a(this.getSoundSquish(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            this.b = -0.5F;
        } else if (!this.onGround && this.bA) {
            this.b = 1.0F;
        }

        this.bA = this.onGround;
        this.dU();
    }

    protected void dU() {
        this.b *= 0.6F;
    }

    protected int dT() {
        return this.random.nextInt(20) + 10;
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntitySlime.bz.equals(datawatcherobject)) {
            this.updateSize();
            this.yaw = this.aM;
            this.aK = this.aM;
            if (this.isInWater() && this.random.nextInt(20) == 0) {
                this.az();
            }
        }

        super.a(datawatcherobject);
    }

    @Override
    public EntityTypes<? extends EntitySlime> getEntityType() {
        return (EntityTypes<? extends EntitySlime>) super.getEntityType(); // CraftBukkit - decompile error
    }

    @Override
    public void die() {
        int i = this.getSize();

        if (!this.world.isClientSide && i > 1 && this.getHealth() <= 0.0F) {
            int j = 2 + this.random.nextInt(3);

            // CraftBukkit start
            SlimeSplitEvent event = new SlimeSplitEvent((org.bukkit.entity.Slime) this.getBukkitEntity(), j);
            this.world.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled() && event.getCount() > 0) {
                j = event.getCount();
            } else {
                super.die();
                return;
            }
            List<EntityLiving> slimes = new ArrayList<>(j);
            // CraftBukkit end

            for (int k = 0; k < j; ++k) {
                float f = ((float) (k % 2) - 0.5F) * (float) i / 4.0F;
                float f1 = ((float) (k / 2) - 0.5F) * (float) i / 4.0F;
                EntitySlime entityslime = (EntitySlime) this.getEntityType().a(this.world);

                if (this.hasCustomName()) {
                    entityslime.setCustomName(this.getCustomName());
                }

                if (this.isPersistent()) {
                    entityslime.setPersistent();
                }

                entityslime.setSize(i / 2, true);
                entityslime.setPositionRotation(this.locX + (double) f, this.locY + 0.5D, this.locZ + (double) f1, this.random.nextFloat() * 360.0F, 0.0F);

                slimes.add(entityslime); // CraftBukkit
            }

            // CraftBukkit start
            if (CraftEventFactory.callEntityTransformEvent(this, slimes, EntityTransformEvent.TransformReason.SPLIT).isCancelled()) {
                return;
            }
            for (EntityLiving living : slimes) {
                this.world.addEntity(living, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SLIME_SPLIT); // CraftBukkit - SpawnReason
            }
            // CraftBukkit end
        }

        super.die();
    }

    @Override
    public void collide(Entity entity) {
        super.collide(entity);
        if (entity instanceof EntityIronGolem && this.dV()) {
            this.h((EntityLiving) entity);
        }

    }

    @Override
    public void pickup(EntityHuman entityhuman) {
        if (this.dV()) {
            this.h((EntityLiving) entityhuman);
        }

    }

    protected void h(EntityLiving entityliving) {
        if (this.isAlive()) {
            int i = this.getSize();

            if (this.h((Entity) entityliving) < 0.6D * (double) i * 0.6D * (double) i && this.hasLineOfSight(entityliving) && entityliving.damageEntity(DamageSource.mobAttack(this), (float) this.dW())) {
                this.a(SoundEffects.ENTITY_SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.a((EntityLiving) this, (Entity) entityliving);
            }
        }

    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 0.625F * entitysize.height;
    }

    protected boolean dV() {
        return !this.ea() && this.df();
    }

    protected int dW() {
        return this.getSize();
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return this.ea() ? SoundEffects.ENTITY_SLIME_HURT_SMALL : SoundEffects.ENTITY_SLIME_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return this.ea() ? SoundEffects.ENTITY_SLIME_DEATH_SMALL : SoundEffects.ENTITY_SLIME_DEATH;
    }

    protected SoundEffect getSoundSquish() {
        return this.ea() ? SoundEffects.ENTITY_SLIME_SQUISH_SMALL : SoundEffects.ENTITY_SLIME_SQUISH;
    }

    @Override
    protected MinecraftKey getDefaultLootTable() {
        return this.getSize() == 1 ? this.getEntityType().h() : LootTables.a;
    }

    public static boolean c(EntityTypes<EntitySlime> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        if (generatoraccess.getWorldData().getType() == WorldType.FLAT && random.nextInt(4) != 1) {
            return false;
        } else {
            if (generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL) {
                BiomeBase biomebase = generatoraccess.getBiome(blockposition);

                if (biomebase == Biomes.SWAMP && blockposition.getY() > 50 && blockposition.getY() < 70 && random.nextFloat() < 0.5F && random.nextFloat() < generatoraccess.aa() && generatoraccess.getLightLevel(blockposition) <= random.nextInt(8)) {
                    return a(entitytypes, generatoraccess, enummobspawn, blockposition, random);
                }

                ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(blockposition);
                boolean flag = generatoraccess.getMinecraftWorld().paperConfig.allChunksAreSlimeChunks || SeededRandom.a(chunkcoordintpair.x, chunkcoordintpair.z, generatoraccess.getSeed(), generatoraccess.getMinecraftWorld().spigotConfig.slimeSeed).nextInt(10) == 0; // Spigot // Paper

                if (random.nextInt(10) == 0 && flag && blockposition.getY() < 40) {
                    return a(entitytypes, generatoraccess, enummobspawn, blockposition, random);
                }
            }

            return false;
        }
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F * (float) this.getSize();
    }

    @Override
    public int M() {
        return 0;
    }

    protected boolean eb() {
        return this.getSize() > 0;
    }

    @Override
    protected void jump() {
        Vec3D vec3d = this.getMot();

        this.setMot(vec3d.x, 0.41999998688697815D, vec3d.z);
        this.impulse = true;
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        int i = this.random.nextInt(3);

        if (i < 2 && this.random.nextFloat() < 0.5F * difficultydamagescaler.d()) {
            ++i;
        }

        int j = 1 << i;

        this.setSize(j, true);
        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    protected SoundEffect getSoundJump() {
        return this.ea() ? SoundEffects.ENTITY_SLIME_JUMP_SMALL : SoundEffects.ENTITY_SLIME_JUMP;
    }

    @Override
    public EntitySize a(EntityPose entitypose) {
        return super.a(entitypose).a(0.255F * (float) this.getSize());
    }

    static class PathfinderGoalSlimeIdle extends PathfinderGoal {

        private final EntitySlime a;

        public PathfinderGoalSlimeIdle(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            return !this.a.isPassenger() && this.a.canWander && new SlimeWanderEvent((Slime) this.a.getBukkitEntity()).callEvent(); // Paper
        }

        @Override
        public void e() {
            ((EntitySlime.ControllerMoveSlime) this.a.getControllerMove()).a(1.0D);
        }
    }

    static class PathfinderGoalSlimeRandomJump extends PathfinderGoal {

        private final EntitySlime a;

        public PathfinderGoalSlimeRandomJump(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
            entityslime.getNavigation().d(true);
        }

        @Override
        public boolean a() {
            return (this.a.isInWater() || this.a.aD()) && this.a.getControllerMove() instanceof EntitySlime.ControllerMoveSlime && this.a.canWander && new SlimeSwimEvent((Slime) this.a.getBukkitEntity()).callEvent(); // Paper
        }

        @Override
        public void e() {
            if (this.a.getRandom().nextFloat() < 0.8F) {
                this.a.getControllerJump().jump();
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
            this.a(EnumSet.of(PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            return this.a.canWander && this.a.getGoalTarget() == null && (this.a.onGround || this.a.isInWater() || this.a.aD() || this.a.hasEffect(MobEffects.LEVITATION)) && this.a.getControllerMove() instanceof EntitySlime.ControllerMoveSlime;
        }

        @Override
        public void e() {
            if (--this.c <= 0) {
                this.c = 40 + this.a.getRandom().nextInt(60);
                // Paper start
                SlimeChangeDirectionEvent event = new SlimeChangeDirectionEvent((Slime) this.a.getBukkitEntity(), (float) this.a.getRandom().nextInt(360));
                if (!this.a.canWander || !event.callEvent()) return;
                this.b = event.getNewYaw();
                // Paper end
            }

            ((EntitySlime.ControllerMoveSlime) this.a.getControllerMove()).a(this.b, false);
        }
    }

    static class PathfinderGoalSlimeNearestPlayer extends PathfinderGoal {

        private final EntitySlime a;
        private int b;

        public PathfinderGoalSlimeNearestPlayer(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(EnumSet.of(PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            EntityLiving entityliving = this.a.getGoalTarget();

            // Paper start
            if (entityliving == null || !entityliving.isAlive()) {
                return false;
            }
            if (entityliving instanceof EntityHuman && ((EntityHuman) entityliving).abilities.isInvulnerable) {
                return false;
            }
            return this.a.getControllerMove() instanceof EntitySlime.ControllerMoveSlime && this.a.canWander && new SlimeTargetLivingEntityEvent((Slime) this.a.getBukkitEntity(), (LivingEntity) entityliving.getBukkitEntity()).callEvent();
            // Paper end
        }

        @Override
        public void c() {
            this.b = 300;
            super.c();
        }

        @Override
        public boolean b() {
            EntityLiving entityliving = this.a.getGoalTarget();

            // Paper start
            if (entityliving == null || !entityliving.isAlive()) {
                return false;
            }
            if (entityliving instanceof EntityHuman && ((EntityHuman) entityliving).abilities.isInvulnerable) {
                return false;
            }
            return --this.b > 0 && this.a.canWander && new SlimeTargetLivingEntityEvent((Slime) this.a.getBukkitEntity(), (LivingEntity) entityliving.getBukkitEntity()).callEvent();
            // Paper end
        }

        @Override
        public void e() {
            this.a.a((Entity) this.a.getGoalTarget(), 10.0F, 10.0F);
            ((EntitySlime.ControllerMoveSlime) this.a.getControllerMove()).a(this.a.yaw, this.a.dV());
        }

        // Paper start - clear timer and target when goal resets
        public void d() {
            this.b = 0;
            this.a.setGoalTarget(null);
        }
        // Paper end
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

        @Override
        public void a() {
            this.a.yaw = this.a(this.a.yaw, this.i, 90.0F);
            this.a.aM = this.a.yaw;
            this.a.aK = this.a.yaw;
            if (this.h != ControllerMove.Operation.MOVE_TO) {
                this.a.r(0.0F);
            } else {
                this.h = ControllerMove.Operation.WAIT;
                if (this.a.onGround) {
                    this.a.o((float) (this.e * this.a.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue()));
                    if (this.j-- <= 0) {
                        this.j = this.k.dT();
                        if (this.l) {
                            this.j /= 3;
                        }

                        this.k.getControllerJump().jump();
                        if (this.k.eb()) {
                            this.k.a(this.k.getSoundJump(), this.k.getSoundVolume(), ((this.k.getRandom().nextFloat() - this.k.getRandom().nextFloat()) * 0.2F + 1.0F) * 0.8F);
                        }
                    } else {
                        this.k.bb = 0.0F;
                        this.k.bd = 0.0F;
                        this.a.o(0.0F);
                    }
                } else {
                    this.a.o((float) (this.e * this.a.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue()));
                }

            }
        }
    }

    // Paper start
    private boolean canWander = true;
    public boolean canWander() {
        return canWander;
    }

    public void setWander(boolean canWander) {
        this.canWander = canWander;
    }
    // Paper end
}
