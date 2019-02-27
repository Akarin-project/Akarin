package net.minecraft.server;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityDolphin extends EntityWaterAnimal {

    private static final DataWatcherObject<BlockPosition> b = DataWatcher.a(EntityDolphin.class, DataWatcherRegistry.l);
    private static final DataWatcherObject<Boolean> c = DataWatcher.a(EntityDolphin.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> bC = DataWatcher.a(EntityDolphin.class, DataWatcherRegistry.b);
    public static final Predicate<EntityItem> a = (entityitem) -> {
        return !entityitem.q() && entityitem.isAlive() && entityitem.isInWater();
    };

    public EntityDolphin(World world) {
        super(EntityTypes.DOLPHIN, world);
        this.setSize(0.9F, 0.6F);
        this.moveController = new EntityDolphin.a(this);
        this.lookController = new ControllerLookDolphin(this, 10);
        this.p(true);
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setAirTicks(this.bf());
        this.pitch = 0.0F;
        return super.prepare(difficultydamagescaler, groupdataentity, nbttagcompound);
    }

    public boolean ca() {
        return false;
    }

    protected void a(int i) {}

    public void g(BlockPosition blockposition) {
        this.datawatcher.set(EntityDolphin.b, blockposition);
    }

    public BlockPosition l() {
        return (BlockPosition) this.datawatcher.get(EntityDolphin.b);
    }

    public boolean dy() {
        return (Boolean) this.datawatcher.get(EntityDolphin.c);
    }

    public void a(boolean flag) {
        this.datawatcher.set(EntityDolphin.c, flag);
    }

    public int dz() {
        return (Integer) this.datawatcher.get(EntityDolphin.bC);
    }

    public void b(int i) {
        this.datawatcher.set(EntityDolphin.bC, i);
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityDolphin.b, BlockPosition.ZERO);
        this.datawatcher.register(EntityDolphin.c, false);
        this.datawatcher.register(EntityDolphin.bC, 2400);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("TreasurePosX", this.l().getX());
        nbttagcompound.setInt("TreasurePosY", this.l().getY());
        nbttagcompound.setInt("TreasurePosZ", this.l().getZ());
        nbttagcompound.setBoolean("GotFish", this.dy());
        nbttagcompound.setInt("Moistness", this.dz());
    }

    public void a(NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.getInt("TreasurePosX");
        int j = nbttagcompound.getInt("TreasurePosY");
        int k = nbttagcompound.getInt("TreasurePosZ");

        this.g(new BlockPosition(i, j, k));
        super.a(nbttagcompound);
        this.a(nbttagcompound.getBoolean("GotFish"));
        this.b(nbttagcompound.getInt("Moistness"));
    }

    protected void n() {
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
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true, new Class[] { EntityGuardian.class}));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(1.2000000476837158D);
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(3.0D);
    }

    protected NavigationAbstract b(World world) {
        return new NavigationGuardian(this, world);
    }

    public boolean B(Entity entity) {
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), (float) ((int) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue()));

        if (flag) {
            this.a((EntityLiving) this, entity);
            this.a(SoundEffects.ENTITY_DOLPHIN_ATTACK, 1.0F, 1.0F);
        }

        return flag;
    }

    public int bf() {
        return 4800;
    }

    protected int l(int i) {
        return this.bf();
    }

    public float getHeadHeight() {
        return 0.3F;
    }

    public int K() {
        return 1;
    }

    public int L() {
        return 1;
    }

    protected boolean n(Entity entity) {
        return true;
    }

    protected void a(EntityItem entityitem) {
        if (this.getEquipment(EnumItemSlot.MAINHAND).isEmpty()) {
            ItemStack itemstack = entityitem.getItemStack();

            if (this.d(itemstack)) {
                this.setSlot(EnumItemSlot.MAINHAND, itemstack);
                this.dropChanceHand[EnumItemSlot.MAINHAND.b()] = 2.0F;
                this.receive(entityitem, itemstack.getCount());
                entityitem.die();
            }
        }

    }

    public void tick() {
        super.tick();
        if (!this.isNoAI()) {
            if (this.ap()) {
                this.b(2400);
            } else {
                this.b(this.dz() - 1);
                if (this.dz() <= 0) {
                    this.damageEntity(DamageSource.DRYOUT, 1.0F);
                }

                if (this.onGround) {
                    this.motY += 0.5D;
                    this.motX += (double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F);
                    this.motZ += (double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F);
                    this.yaw = this.random.nextFloat() * 360.0F;
                    this.onGround = false;
                    this.impulse = true;
                }
            }

            if (this.world.isClientSide && this.isInWater() && this.motX * this.motX + this.motY * this.motY + this.motZ * this.motZ > 0.03D) {
                Vec3D vec3d = this.f(0.0F);
                float f = MathHelper.cos(this.yaw * 0.017453292F) * 0.3F;
                float f1 = MathHelper.sin(this.yaw * 0.017453292F) * 0.3F;
                float f2 = 1.2F - this.random.nextFloat() * 0.7F;

                for (int i = 0; i < 2; ++i) {
                    this.world.addParticle(Particles.X, this.locX - vec3d.x * (double) f2 + (double) f, this.locY - vec3d.y, this.locZ - vec3d.z * (double) f2 + (double) f1, 0.0D, 0.0D, 0.0D);
                    this.world.addParticle(Particles.X, this.locX - vec3d.x * (double) f2 - (double) f, this.locY - vec3d.y, this.locZ - vec3d.z * (double) f2 - (double) f1, 0.0D, 0.0D, 0.0D);
                }
            }

        }
    }

    protected boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!itemstack.isEmpty() && itemstack.getItem().a(TagsItem.FISHES)) {
            if (!this.world.isClientSide) {
                this.a(SoundEffects.ENTITY_DOLPHIN_EAT, 1.0F, 1.0F);
            }

            this.a(true);
            if (!entityhuman.abilities.canInstantlyBuild) {
                itemstack.subtract(1);
            }

            return true;
        } else {
            return super.a(entityhuman, enumhand);
        }
    }

    @Nullable
    public EntityItem f(ItemStack itemstack) {
        if (itemstack.isEmpty()) {
            return null;
        } else {
            double d0 = this.locY - 0.30000001192092896D + (double) this.getHeadHeight();
            EntityItem entityitem = new EntityItem(this.world, this.locX, d0, this.locZ, itemstack);

            entityitem.a(40);
            entityitem.c(this.getUniqueID());
            float f = 0.3F;

            entityitem.motX = (double) (-MathHelper.sin(this.yaw * 0.017453292F) * MathHelper.cos(this.pitch * 0.017453292F) * f);
            entityitem.motY = (double) (MathHelper.sin(this.pitch * 0.017453292F) * f * 1.5F);
            entityitem.motZ = (double) (MathHelper.cos(this.yaw * 0.017453292F) * MathHelper.cos(this.pitch * 0.017453292F) * f);
            float f1 = this.random.nextFloat() * 6.2831855F;

            f = 0.02F * this.random.nextFloat();
            entityitem.motX += (double) (MathHelper.cos(f1) * f);
            entityitem.motZ += (double) (MathHelper.sin(f1) * f);
            this.world.addEntity(entityitem);
            return entityitem;
        }
    }

    public boolean a(GeneratorAccess generatoraccess, boolean flag) {
        return this.locY > 45.0D && this.locY < (double) generatoraccess.getSeaLevel() && generatoraccess.getBiome(new BlockPosition(this)) != Biomes.OCEAN || generatoraccess.getBiome(new BlockPosition(this)) != Biomes.DEEP_OCEAN && super.a(generatoraccess, flag);
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_DOLPHIN_HURT;
    }

    @Nullable
    protected SoundEffect cs() {
        return SoundEffects.ENTITY_DOLPHIN_DEATH;
    }

    @Nullable
    protected SoundEffect D() {
        return this.isInWater() ? SoundEffects.ENTITY_DOLPHIN_AMBIENT_WATER : SoundEffects.ENTITY_DOLPHIN_AMBIENT;
    }

    protected SoundEffect ae() {
        return SoundEffects.ENTITY_DOLPHIN_SPLASH;
    }

    protected SoundEffect ad() {
        return SoundEffects.ENTITY_DOLPHIN_SWIM;
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.aN;
    }

    protected boolean dA() {
        BlockPosition blockposition = this.getNavigation().i();

        return blockposition != null ? this.c(blockposition) < 144.0D : false;
    }

    public void a(float f, float f1, float f2) {
        if (this.cP() && this.isInWater()) {
            this.a(f, f1, f2, this.cK());
            this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
            this.motX *= 0.8999999761581421D;
            this.motY *= 0.8999999761581421D;
            this.motZ *= 0.8999999761581421D;
            if (this.getGoalTarget() == null) {
                this.motY -= 0.005D;
            }
        } else {
            super.a(f, f1, f2);
        }

    }

    public boolean a(EntityHuman entityhuman) {
        return true;
    }

    static class b extends PathfinderGoal {

        private final EntityDolphin a;
        private boolean b;

        b(EntityDolphin entitydolphin) {
            this.a = entitydolphin;
            this.a(3);
        }

        public boolean f() {
            return false;
        }

        public boolean a() {
            return this.a.dy() && this.a.getAirTicks() >= 100;
        }

        public boolean b() {
            BlockPosition blockposition = this.a.l();

            return this.a.c(new BlockPosition((double) blockposition.getX(), this.a.locY, (double) blockposition.getZ())) > 16.0D && !this.b && this.a.getAirTicks() >= 100;
        }

        public void c() {
            this.b = false;
            this.a.getNavigation().q();
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

        public void d() {
            BlockPosition blockposition = this.a.l();

            if (this.a.c(new BlockPosition((double) blockposition.getX(), this.a.locY, (double) blockposition.getZ())) <= 16.0D || this.b) {
                this.a.a(false);
            }

        }

        public void e() {
            BlockPosition blockposition = this.a.l();
            World world = this.a.world;

            if (this.a.dA() || this.a.getNavigation().p()) {
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

                this.a.getControllerLook().a(vec3d.x, vec3d.y, vec3d.z, (float) (this.a.L() + 20), (float) this.a.K());
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
            this.a(3);
        }

        public boolean a() {
            this.c = this.a.world.findNearbyPlayer(this.a, 10.0D);
            return this.c == null ? false : this.c.isSwimming();
        }

        public boolean b() {
            return this.c != null && this.c.isSwimming() && this.a.h(this.c) < 256.0D;
        }

        public void c() {
            this.c.addEffect(new MobEffect(MobEffects.DOLPHINS_GRACE, 100));
        }

        public void d() {
            this.c = null;
            this.a.getNavigation().q();
        }

        public void e() {
            this.a.getControllerLook().a(this.c, (float) (this.a.L() + 20), (float) this.a.K());
            if (this.a.h(this.c) < 6.25D) {
                this.a.getNavigation().q();
            } else {
                this.a.getNavigation().a((Entity) this.c, this.b);
            }

            if (this.c.isSwimming() && this.c.world.random.nextInt(6) == 0) {
                this.c.addEffect(new MobEffect(MobEffects.DOLPHINS_GRACE, 100));
            }

        }
    }

    class d extends PathfinderGoal {

        private int b;

        private d() {}

        public boolean a() {
            if (this.b > EntityDolphin.this.ticksLived) {
                return false;
            } else {
                List<EntityItem> list = EntityDolphin.this.world.a(EntityItem.class, EntityDolphin.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityDolphin.a);

                return !list.isEmpty() || !EntityDolphin.this.getEquipment(EnumItemSlot.MAINHAND).isEmpty();
            }
        }

        public void c() {
            List<EntityItem> list = EntityDolphin.this.world.a(EntityItem.class, EntityDolphin.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityDolphin.a);

            if (!list.isEmpty()) {
                EntityDolphin.this.getNavigation().a((Entity) list.get(0), 1.2000000476837158D);
                EntityDolphin.this.a(SoundEffects.ENTITY_DOLPHIN_PLAY, 1.0F, 1.0F);
            }

            this.b = 0;
        }

        public void d() {
            ItemStack itemstack = EntityDolphin.this.getEquipment(EnumItemSlot.MAINHAND);

            if (!itemstack.isEmpty()) {
                EntityDolphin.this.f(itemstack);
                EntityDolphin.this.setSlot(EnumItemSlot.MAINHAND, ItemStack.a);
                this.b = EntityDolphin.this.ticksLived + EntityDolphin.this.random.nextInt(100);
            }

        }

        public void e() {
            List<EntityItem> list = EntityDolphin.this.world.a(EntityItem.class, EntityDolphin.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityDolphin.a);
            ItemStack itemstack = EntityDolphin.this.getEquipment(EnumItemSlot.MAINHAND);

            if (!itemstack.isEmpty()) {
                EntityDolphin.this.f(itemstack);
                EntityDolphin.this.setSlot(EnumItemSlot.MAINHAND, ItemStack.a);
            } else if (!list.isEmpty()) {
                EntityDolphin.this.getNavigation().a((Entity) list.get(0), 1.2000000476837158D);
            }

        }
    }

    static class a extends ControllerMove {

        private final EntityDolphin i;

        public a(EntityDolphin entitydolphin) {
            super(entitydolphin);
            this.i = entitydolphin;
        }

        public void a() {
            if (this.i.isInWater()) {
                this.i.motY += 0.005D;
            }

            if (this.h == ControllerMove.Operation.MOVE_TO && !this.i.getNavigation().p()) {
                double d0 = this.b - this.i.locX;
                double d1 = this.c - this.i.locY;
                double d2 = this.d - this.i.locZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (d3 < 2.500000277905201E-7D) {
                    this.a.r(0.0F);
                } else {
                    float f = (float) (MathHelper.c(d2, d0) * 57.2957763671875D) - 90.0F;

                    this.i.yaw = this.a(this.i.yaw, f, 10.0F);
                    this.i.aQ = this.i.yaw;
                    this.i.aS = this.i.yaw;
                    float f1 = (float) (this.e * this.i.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());

                    if (this.i.isInWater()) {
                        this.i.o(f1 * 0.02F);
                        float f2 = -((float) (MathHelper.c(d1, (double) MathHelper.sqrt(d0 * d0 + d2 * d2)) * 57.2957763671875D));

                        f2 = MathHelper.a(MathHelper.g(f2), -85.0F, 85.0F);
                        this.i.pitch = this.a(this.i.pitch, f2, 5.0F);
                        float f3 = MathHelper.cos(this.i.pitch * 0.017453292F);
                        float f4 = MathHelper.sin(this.i.pitch * 0.017453292F);

                        this.i.bj = f3 * f1;
                        this.i.bi = -f4 * f1;
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
