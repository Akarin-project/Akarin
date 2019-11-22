package net.minecraft.server;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityDolphin extends EntityWaterAnimal {

    private static final DataWatcherObject<BlockPosition> c = DataWatcher.a(EntityDolphin.class, DataWatcherRegistry.l);
    private static final DataWatcherObject<Boolean> d = DataWatcher.a(EntityDolphin.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> bz = DataWatcher.a(EntityDolphin.class, DataWatcherRegistry.b);
    private static final PathfinderTargetCondition bA = (new PathfinderTargetCondition()).a(10.0D).b().a().c();
    public static final Predicate<EntityItem> b = (entityitem) -> {
        return !entityitem.q() && entityitem.isAlive() && entityitem.isInWater();
    };

    public EntityDolphin(EntityTypes<? extends EntityDolphin> entitytypes, World world) {
        super(entitytypes, world);
        this.moveController = new EntityDolphin.a(this);
        this.lookController = new ControllerLookDolphin(this, 10);
        this.setCanPickupLoot(true);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setAirTicks(this.bp());
        this.pitch = 0.0F;
        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    public boolean cm() {
        return false;
    }

    @Override
    protected void a(int i) {}

    public void g(BlockPosition blockposition) {
        this.datawatcher.set(EntityDolphin.c, blockposition);
    }

    public BlockPosition l() {
        return (BlockPosition) this.datawatcher.get(EntityDolphin.c);
    }

    public boolean dV() {
        return (Boolean) this.datawatcher.get(EntityDolphin.d);
    }

    public void r(boolean flag) {
        this.datawatcher.set(EntityDolphin.d, flag);
    }

    public int dW() {
        return (Integer) this.datawatcher.get(EntityDolphin.bz);
    }

    public void b(int i) {
        this.datawatcher.set(EntityDolphin.bz, i);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityDolphin.c, BlockPosition.ZERO);
        this.datawatcher.register(EntityDolphin.d, false);
        this.datawatcher.register(EntityDolphin.bz, 2400);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("TreasurePosX", this.l().getX());
        nbttagcompound.setInt("TreasurePosY", this.l().getY());
        nbttagcompound.setInt("TreasurePosZ", this.l().getZ());
        nbttagcompound.setBoolean("GotFish", this.dV());
        nbttagcompound.setInt("Moistness", this.dW());
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.getInt("TreasurePosX");
        int j = nbttagcompound.getInt("TreasurePosY");
        int k = nbttagcompound.getInt("TreasurePosZ");

        this.g(new BlockPosition(i, j, k));
        super.a(nbttagcompound);
        this.r(nbttagcompound.getBoolean("GotFish"));
        this.b(nbttagcompound.getInt("Moistness"));
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalBreath(this));
        this.goalSelector.a(0, new PathfinderGoalWater(this));
        this.goalSelector.a(1, new EntityDolphin.b(this));
        this.goalSelector.a(2, new EntityDolphin.c(this, 4.0D));
        this.goalSelector.a(4, new PathfinderGoalRandomSwim(this, 1.0D, 10));
        this.goalSelector.a(4, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(5, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(5, new PathfinderGoalWaterJump(this, 10));
        this.goalSelector.a(6, new PathfinderGoalMeleeAttack(this, 1.2000000476837158D, true));
        this.goalSelector.a(8, new EntityDolphin.d());
        this.goalSelector.a(8, new PathfinderGoalFollowBoat(this));
        this.goalSelector.a(9, new PathfinderGoalAvoidTarget<>(this, EntityGuardian.class, 8.0F, 1.0D, 1.0D));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityGuardian.class})).a(new Class[0])); // CraftBukkit - decompile error
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(1.2000000476837158D);
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(3.0D);
    }

    @Override
    protected NavigationAbstract b(World world) {
        return new NavigationGuardian(this, world);
    }

    @Override
    public boolean C(Entity entity) {
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), (float) ((int) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue()));

        if (flag) {
            this.a((EntityLiving) this, entity);
            this.a(SoundEffects.ENTITY_DOLPHIN_ATTACK, 1.0F, 1.0F);
        }

        return flag;
    }

    @Override
    public int bp() {
        return 4800;
    }

    @Override
    protected int m(int i) {
        return this.bp();
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 0.3F;
    }

    @Override
    public int M() {
        return 1;
    }

    @Override
    public int dA() {
        return 1;
    }

    @Override
    protected boolean n(Entity entity) {
        return true;
    }

    @Override
    public boolean e(ItemStack itemstack) {
        EnumItemSlot enumitemslot = EntityInsentient.h(itemstack);

        return !this.getEquipment(enumitemslot).isEmpty() ? false : enumitemslot == EnumItemSlot.MAINHAND && super.e(itemstack);
    }

    @Override
    protected void a(EntityItem entityitem) {
        if (this.getEquipment(EnumItemSlot.MAINHAND).isEmpty()) {
            ItemStack itemstack = entityitem.getItemStack();

            if (this.g(itemstack)) {
                // CraftBukkit start - call EntityPickupItemEvent
                if (org.bukkit.craftbukkit.event.CraftEventFactory.callEntityPickupItemEvent(this, entityitem, 0, false).isCancelled()) {
                    return;
                }
                // CraftBukkit end
                this.setSlot(EnumItemSlot.MAINHAND, itemstack);
                this.dropChanceHand[EnumItemSlot.MAINHAND.b()] = 2.0F;
                this.receive(entityitem, itemstack.getCount());
                entityitem.die();
            }
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isNoAI()) {
            if (this.au()) {
                this.b(2400);
            } else {
                this.b(this.dW() - 1);
                if (this.dW() <= 0) {
                    this.damageEntity(DamageSource.DRYOUT, 1.0F);
                }

                if (this.onGround) {
                    this.setMot(this.getMot().add((double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F), 0.5D, (double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F)));
                    this.yaw = this.random.nextFloat() * 360.0F;
                    this.onGround = false;
                    this.impulse = true;
                }
            }

            if (this.world.isClientSide && this.isInWater() && this.getMot().g() > 0.03D) {
                Vec3D vec3d = this.f(0.0F);
                float f = MathHelper.cos(this.yaw * 0.017453292F) * 0.3F;
                float f1 = MathHelper.sin(this.yaw * 0.017453292F) * 0.3F;
                float f2 = 1.2F - this.random.nextFloat() * 0.7F;

                for (int i = 0; i < 2; ++i) {
                    this.world.addParticle(Particles.DOLPHIN, this.locX - vec3d.x * (double) f2 + (double) f, this.locY - vec3d.y, this.locZ - vec3d.z * (double) f2 + (double) f1, 0.0D, 0.0D, 0.0D);
                    this.world.addParticle(Particles.DOLPHIN, this.locX - vec3d.x * (double) f2 - (double) f, this.locY - vec3d.y, this.locZ - vec3d.z * (double) f2 - (double) f1, 0.0D, 0.0D, 0.0D);
                }
            }

        }
    }

    @Override
    protected boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!itemstack.isEmpty() && itemstack.getItem().a(TagsItem.FISHES)) {
            if (!this.world.isClientSide) {
                this.a(SoundEffects.ENTITY_DOLPHIN_EAT, 1.0F, 1.0F);
            }

            this.r(true);
            if (!entityhuman.abilities.canInstantlyBuild) {
                itemstack.subtract(1);
            }

            return true;
        } else {
            return super.a(entityhuman, enumhand);
        }
    }

    public static boolean b(EntityTypes<EntityDolphin> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return blockposition.getY() > 45 && blockposition.getY() < generatoraccess.getSeaLevel() && (generatoraccess.getBiome(blockposition) != Biomes.OCEAN || generatoraccess.getBiome(blockposition) != Biomes.DEEP_OCEAN) && generatoraccess.getFluid(blockposition).a(TagsFluid.WATER);
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_DOLPHIN_HURT;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_DOLPHIN_DEATH;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        return this.isInWater() ? SoundEffects.ENTITY_DOLPHIN_AMBIENT_WATER : SoundEffects.ENTITY_DOLPHIN_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundSplash() {
        return SoundEffects.ENTITY_DOLPHIN_SPLASH;
    }

    @Override
    protected SoundEffect getSoundSwim() {
        return SoundEffects.ENTITY_DOLPHIN_SWIM;
    }

    protected boolean dX() {
        BlockPosition blockposition = this.getNavigation().h();

        return blockposition != null ? blockposition.a((IPosition) this.getPositionVector(), 12.0D) : false;
    }

    @Override
    public void e(Vec3D vec3d) {
        if (this.df() && this.isInWater()) {
            this.a(this.db(), vec3d);
            this.move(EnumMoveType.SELF, this.getMot());
            this.setMot(this.getMot().a(0.9D));
            if (this.getGoalTarget() == null) {
                this.setMot(this.getMot().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.e(vec3d);
        }

    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return true;
    }

    static class b extends PathfinderGoal {

        private final EntityDolphin a;
        private boolean b;

        b(EntityDolphin entitydolphin) {
            this.a = entitydolphin;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean C_() {
            return false;
        }

        @Override
        public boolean a() {
            return this.a.dV() && this.a.getAirTicks() >= 100;
        }

        @Override
        public boolean b() {
            BlockPosition blockposition = this.a.l();

            return !(new BlockPosition((double) blockposition.getX(), this.a.locY, (double) blockposition.getZ())).a((IPosition) this.a.getPositionVector(), 4.0D) && !this.b && this.a.getAirTicks() >= 100;
        }

        @Override
        public void c() {
            this.b = false;
            this.a.getNavigation().o();
            World world = this.a.world;
            BlockPosition blockposition = new BlockPosition(this.a);
            String s = (double) world.random.nextFloat() >= 0.5D ? "Ocean_Ruin" : "Shipwreck";
            BlockPosition blockposition1 = world.a(s, blockposition, 50, false);

            if (blockposition1 == null) {
                BlockPosition blockposition2 = world.a(s.equals("Ocean_Ruin") ? "Shipwreck" : "Ocean_Ruin", blockposition, 50, false);

                if (blockposition2 == null) {
                    this.b = true;
                    return;
                }

                this.a.g(blockposition2);
            } else {
                this.a.g(blockposition1);
            }

            world.broadcastEntityEffect(this.a, (byte) 38);
        }

        @Override
        public void d() {
            BlockPosition blockposition = this.a.l();

            if ((new BlockPosition((double) blockposition.getX(), this.a.locY, (double) blockposition.getZ())).a((IPosition) this.a.getPositionVector(), 4.0D) || this.b) {
                this.a.r(false);
            }

        }

        @Override
        public void e() {
            BlockPosition blockposition = this.a.l();
            World world = this.a.world;

            if (this.a.dX() || this.a.getNavigation().n()) {
                Vec3D vec3d = RandomPositionGenerator.a((EntityCreature) this.a, 16, 1, new Vec3D((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ()), 0.39269909262657166D);

                if (vec3d == null) {
                    vec3d = RandomPositionGenerator.a(this.a, 8, 4, new Vec3D((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ()));
                }

                if (vec3d != null) {
                    BlockPosition blockposition1 = new BlockPosition(vec3d);

                    if (!world.getFluid(blockposition1).a(TagsFluid.WATER) || !world.getType(blockposition1).a((IBlockAccess) world, blockposition1, PathMode.WATER)) {
                        vec3d = RandomPositionGenerator.a(this.a, 8, 5, new Vec3D((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ()));
                    }
                }

                if (vec3d == null) {
                    this.b = true;
                    return;
                }

                this.a.getControllerLook().a(vec3d.x, vec3d.y, vec3d.z, (float) (this.a.dA() + 20), (float) this.a.M());
                this.a.getNavigation().a(vec3d.x, vec3d.y, vec3d.z, 1.3D);
                if (world.random.nextInt(80) == 0) {
                    world.broadcastEntityEffect(this.a, (byte) 38);
                }
            }

        }
    }

    static class c extends PathfinderGoal {

        private final EntityDolphin a;
        private final double b;
        private EntityHuman c;

        c(EntityDolphin entitydolphin, double d0) {
            this.a = entitydolphin;
            this.b = d0;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            this.c = this.a.world.a(EntityDolphin.bA, (EntityLiving) this.a);
            return this.c == null ? false : this.c.isSwimming();
        }

        @Override
        public boolean b() {
            return this.c != null && this.c.isSwimming() && this.a.h((Entity) this.c) < 256.0D;
        }

        @Override
        public void c() {
            this.c.addEffect(new MobEffect(MobEffects.DOLPHINS_GRACE, 100), org.bukkit.event.entity.EntityPotionEffectEvent.Cause.DOLPHIN); // CraftBukkit
        }

        @Override
        public void d() {
            this.c = null;
            this.a.getNavigation().o();
        }

        @Override
        public void e() {
            this.a.getControllerLook().a(this.c, (float) (this.a.dA() + 20), (float) this.a.M());
            if (this.a.h((Entity) this.c) < 6.25D) {
                this.a.getNavigation().o();
            } else {
                this.a.getNavigation().a((Entity) this.c, this.b);
            }

            if (this.c.isSwimming() && this.c.world.random.nextInt(6) == 0) {
                this.c.addEffect(new MobEffect(MobEffects.DOLPHINS_GRACE, 100), org.bukkit.event.entity.EntityPotionEffectEvent.Cause.DOLPHIN); // CraftBukkit
            }

        }
    }

    class d extends PathfinderGoal {

        private int b;

        private d() {}

        @Override
        public boolean a() {
            if (this.b > EntityDolphin.this.ticksLived) {
                return false;
            } else {
                List<EntityItem> list = EntityDolphin.this.world.a(EntityItem.class, EntityDolphin.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityDolphin.b);

                return !list.isEmpty() || !EntityDolphin.this.getEquipment(EnumItemSlot.MAINHAND).isEmpty();
            }
        }

        @Override
        public void c() {
            List<EntityItem> list = EntityDolphin.this.world.a(EntityItem.class, EntityDolphin.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityDolphin.b);

            if (!list.isEmpty()) {
                EntityDolphin.this.getNavigation().a((Entity) list.get(0), 1.2000000476837158D);
                EntityDolphin.this.a(SoundEffects.ENTITY_DOLPHIN_PLAY, 1.0F, 1.0F);
            }

            this.b = 0;
        }

        @Override
        public void d() {
            ItemStack itemstack = EntityDolphin.this.getEquipment(EnumItemSlot.MAINHAND);

            if (!itemstack.isEmpty()) {
                this.a(itemstack);
                EntityDolphin.this.setSlot(EnumItemSlot.MAINHAND, ItemStack.a);
                this.b = EntityDolphin.this.ticksLived + EntityDolphin.this.random.nextInt(100);
            }

        }

        @Override
        public void e() {
            List<EntityItem> list = EntityDolphin.this.world.a(EntityItem.class, EntityDolphin.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityDolphin.b);
            ItemStack itemstack = EntityDolphin.this.getEquipment(EnumItemSlot.MAINHAND);

            if (!itemstack.isEmpty()) {
                this.a(itemstack);
                EntityDolphin.this.setSlot(EnumItemSlot.MAINHAND, ItemStack.a);
            } else if (!list.isEmpty()) {
                EntityDolphin.this.getNavigation().a((Entity) list.get(0), 1.2000000476837158D);
            }

        }

        private void a(ItemStack itemstack) {
            if (!itemstack.isEmpty()) {
                double d0 = EntityDolphin.this.locY - 0.30000001192092896D + (double) EntityDolphin.this.getHeadHeight();
                EntityItem entityitem = new EntityItem(EntityDolphin.this.world, EntityDolphin.this.locX, d0, EntityDolphin.this.locZ, itemstack);

                entityitem.setPickupDelay(40);
                entityitem.setThrower(EntityDolphin.this.getUniqueID());
                float f = 0.3F;
                float f1 = EntityDolphin.this.random.nextFloat() * 6.2831855F;
                float f2 = 0.02F * EntityDolphin.this.random.nextFloat();

                entityitem.setMot((double) (0.3F * -MathHelper.sin(EntityDolphin.this.yaw * 0.017453292F) * MathHelper.cos(EntityDolphin.this.pitch * 0.017453292F) + MathHelper.cos(f1) * f2), (double) (0.3F * MathHelper.sin(EntityDolphin.this.pitch * 0.017453292F) * 1.5F), (double) (0.3F * MathHelper.cos(EntityDolphin.this.yaw * 0.017453292F) * MathHelper.cos(EntityDolphin.this.pitch * 0.017453292F) + MathHelper.sin(f1) * f2));
                EntityDolphin.this.world.addEntity(entityitem);
            }
        }
    }

    static class a extends ControllerMove {

        private final EntityDolphin i;

        public a(EntityDolphin entitydolphin) {
            super(entitydolphin);
            this.i = entitydolphin;
        }

        @Override
        public void a() {
            if (this.i.isInWater()) {
                this.i.setMot(this.i.getMot().add(0.0D, 0.005D, 0.0D));
            }

            if (this.h == ControllerMove.Operation.MOVE_TO && !this.i.getNavigation().n()) {
                double d0 = this.b - this.i.locX;
                double d1 = this.c - this.i.locY;
                double d2 = this.d - this.i.locZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (d3 < 2.500000277905201E-7D) {
                    this.a.r(0.0F);
                } else {
                    float f = (float) (MathHelper.d(d2, d0) * 57.2957763671875D) - 90.0F;

                    this.i.yaw = this.a(this.i.yaw, f, 10.0F);
                    this.i.aK = this.i.yaw;
                    this.i.aM = this.i.yaw;
                    float f1 = (float) (this.e * this.i.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());

                    if (this.i.isInWater()) {
                        this.i.o(f1 * 0.02F);
                        float f2 = -((float) (MathHelper.d(d1, (double) MathHelper.sqrt(d0 * d0 + d2 * d2)) * 57.2957763671875D));

                        f2 = MathHelper.a(MathHelper.g(f2), -85.0F, 85.0F);
                        this.i.pitch = this.a(this.i.pitch, f2, 5.0F);
                        float f3 = MathHelper.cos(this.i.pitch * 0.017453292F);
                        float f4 = MathHelper.sin(this.i.pitch * 0.017453292F);

                        this.i.bd = f3 * f1;
                        this.i.bc = -f4 * f1;
                    } else {
                        this.i.o(f1 * 0.1F);
                    }

                }
            } else {
                this.i.o(0.0F);
                this.i.t(0.0F);
                this.i.s(0.0F);
                this.i.r(0.0F);
            }
        }
    }
}
