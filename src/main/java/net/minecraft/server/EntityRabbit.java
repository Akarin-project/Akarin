package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class EntityRabbit extends EntityAnimal {

    private static final DataWatcherObject<Integer> bz = DataWatcher.a(EntityRabbit.class, DataWatcherRegistry.b);
    private static final MinecraftKey bA = new MinecraftKey("killer_bunny");
    private int bB;
    private int bC;
    private boolean bD;
    private int bE;
    private int bF;

    public EntityRabbit(EntityTypes<? extends EntityRabbit> entitytypes, World world) {
        super(entitytypes, world);
        this.bt = new EntityRabbit.ControllerJumpRabbit(this);
        this.moveController = new EntityRabbit.ControllerMoveRabbit(this);
        this.initializePathFinderGoals(); // CraftBukkit - moved code
    }

    // CraftBukkit start - code from constructor
    public void initializePathFinderGoals(){
        this.d(0.0D);
    }
    // CraftBukkit end

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new EntityRabbit.PathfinderGoalRabbitPanic(this, 2.2D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 0.8D));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.0D, RecipeItemStack.a(Items.CARROT, Items.GOLDEN_CARROT, Blocks.DANDELION), false));
        this.goalSelector.a(4, new EntityRabbit.PathfinderGoalRabbitAvoidTarget<>(this, EntityHuman.class, 8.0F, 2.2D, 2.2D));
        this.goalSelector.a(4, new EntityRabbit.PathfinderGoalRabbitAvoidTarget<>(this, EntityWolf.class, 10.0F, 2.2D, 2.2D));
        this.goalSelector.a(4, new EntityRabbit.PathfinderGoalRabbitAvoidTarget<>(this, EntityMonster.class, 4.0F, 2.2D, 2.2D));
        this.goalSelector.a(5, new EntityRabbit.PathfinderGoalEatCarrots(this));
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 0.6D));
        this.goalSelector.a(11, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 10.0F));
    }

    @Override
    protected float cX() {
        if (!this.positionChanged && (!this.moveController.b() || this.moveController.e() <= this.locY + 0.5D)) {
            PathEntity pathentity = this.navigation.l();

            if (pathentity != null && pathentity.f() < pathentity.e()) {
                Vec3D vec3d = pathentity.a((Entity) this);

                if (vec3d.y > this.locY + 0.5D) {
                    return 0.5F;
                }
            }

            return this.moveController.c() <= 0.6D ? 0.2F : 0.3F;
        } else {
            return 0.5F;
        }
    }

    @Override
    protected void jump() {
        super.jump();
        double d0 = this.moveController.c();

        if (d0 > 0.0D) {
            double d1 = b(this.getMot());

            if (d1 < 0.01D) {
                this.a(0.1F, new Vec3D(0.0D, 0.0D, 1.0D));
            }
        }

        if (!this.world.isClientSide) {
            this.world.broadcastEntityEffect(this, (byte) 1);
        }

    }

    public void d(double d0) {
        this.getNavigation().a(d0);
        this.moveController.a(this.moveController.d(), this.moveController.e(), this.moveController.f(), d0);
    }

    @Override
    public void setJumping(boolean flag) {
        super.setJumping(flag);
        if (flag) {
            this.a(this.getSoundJump(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
        }

    }

    public void dV() {
        this.setJumping(true);
        this.bC = 10;
        this.bB = 0;
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityRabbit.bz, 0);
    }

    @Override
    public void mobTick() {
        if (this.bE > 0) {
            --this.bE;
        }

        if (this.bF > 0) {
            this.bF -= this.random.nextInt(3);
            if (this.bF < 0) {
                this.bF = 0;
            }
        }

        if (this.onGround) {
            if (!this.bD) {
                this.setJumping(false);
                this.ef();
            }

            if (this.getRabbitType() == 99 && this.bE == 0) {
                EntityLiving entityliving = this.getGoalTarget();

                if (entityliving != null && this.h((Entity) entityliving) < 16.0D) {
                    this.b(entityliving.locX, entityliving.locZ);
                    this.moveController.a(entityliving.locX, entityliving.locY, entityliving.locZ, this.moveController.c());
                    this.dV();
                    this.bD = true;
                }
            }

            EntityRabbit.ControllerJumpRabbit entityrabbit_controllerjumprabbit = (EntityRabbit.ControllerJumpRabbit) this.bt;

            if (!entityrabbit_controllerjumprabbit.c()) {
                if (this.moveController.b() && this.bE == 0) {
                    PathEntity pathentity = this.navigation.l();
                    Vec3D vec3d = new Vec3D(this.moveController.d(), this.moveController.e(), this.moveController.f());

                    if (pathentity != null && pathentity.f() < pathentity.e()) {
                        vec3d = pathentity.a((Entity) this);
                    }

                    this.b(vec3d.x, vec3d.z);
                    this.dV();
                }
            } else if (!entityrabbit_controllerjumprabbit.d()) {
                this.dY();
            }
        }

        this.bD = this.onGround;
    }

    @Override
    public void aA() {}

    private void b(double d0, double d1) {
        this.yaw = (float) (MathHelper.d(d1 - this.locZ, d0 - this.locX) * 57.2957763671875D) - 90.0F;
    }

    private void dY() {
        ((EntityRabbit.ControllerJumpRabbit) this.bt).a(true);
    }

    private void dZ() {
        ((EntityRabbit.ControllerJumpRabbit) this.bt).a(false);
    }

    private void ee() {
        if (this.moveController.c() < 2.2D) {
            this.bE = 10;
        } else {
            this.bE = 1;
        }

    }

    private void ef() {
        this.ee();
        this.dZ();
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (this.bB != this.bC) {
            ++this.bB;
        } else if (this.bC != 0) {
            this.bB = 0;
            this.bC = 0;
            this.setJumping(false);
        }

    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(3.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30000001192092896D);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("RabbitType", this.getRabbitType());
        nbttagcompound.setInt("MoreCarrotTicks", this.bF);
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setRabbitType(nbttagcompound.getInt("RabbitType"));
        this.bF = nbttagcompound.getInt("MoreCarrotTicks");
    }

    protected SoundEffect getSoundJump() {
        return SoundEffects.ENTITY_RABBIT_JUMP;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_RABBIT_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_RABBIT_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_RABBIT_DEATH;
    }

    @Override
    public boolean C(Entity entity) {
        if (this.getRabbitType() == 99) {
            this.a(SoundEffects.ENTITY_RABBIT_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            return entity.damageEntity(DamageSource.mobAttack(this), 8.0F);
        } else {
            return entity.damageEntity(DamageSource.mobAttack(this), 3.0F);
        }
    }

    @Override
    public SoundCategory getSoundCategory() {
        return this.getRabbitType() == 99 ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return this.isInvulnerable(damagesource) ? false : super.damageEntity(damagesource, f);
    }

    private boolean b(Item item) {
        return item == Items.CARROT || item == Items.GOLDEN_CARROT || item == Blocks.DANDELION.getItem();
    }

    @Override
    public EntityRabbit createChild(EntityAgeable entityageable) {
        EntityRabbit entityrabbit = (EntityRabbit) EntityTypes.RABBIT.a(this.world);
        int i = this.a((GeneratorAccess) this.world);

        if (this.random.nextInt(20) != 0) {
            if (entityageable instanceof EntityRabbit && this.random.nextBoolean()) {
                i = ((EntityRabbit) entityageable).getRabbitType();
            } else {
                i = this.getRabbitType();
            }
        }

        entityrabbit.setRabbitType(i);
        return entityrabbit;
    }

    @Override
    public boolean i(ItemStack itemstack) {
        return this.b(itemstack.getItem());
    }

    public int getRabbitType() {
        return (Integer) this.datawatcher.get(EntityRabbit.bz);
    }

    public void setRabbitType(int i) {
        if (i == 99) {
            this.getAttributeInstance(GenericAttributes.ARMOR).setValue(8.0D);
            this.goalSelector.a(4, new EntityRabbit.PathfinderGoalKillerRabbitMeleeAttack(this));
            this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).a(new Class[0])); // CraftBukkit - decompile error
            this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
            this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityWolf.class, true));
            if (!this.hasCustomName()) {
                this.setCustomName(new ChatMessage(SystemUtils.a("entity", EntityRabbit.bA), new Object[0]));
            }
        }

        this.datawatcher.set(EntityRabbit.bz, i);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        Object object = super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
        int i = this.a(generatoraccess);
        boolean flag = false;

        if (object instanceof EntityRabbit.GroupDataRabbit) {
            i = ((EntityRabbit.GroupDataRabbit) object).a;
            flag = true;
        } else {
            object = new EntityRabbit.GroupDataRabbit(i);
        }

        this.setRabbitType(i);
        if (flag) {
            this.setAgeRaw(-24000);
        }

        return (GroupDataEntity) object;
    }

    private int a(GeneratorAccess generatoraccess) {
        BiomeBase biomebase = generatoraccess.getBiome(new BlockPosition(this));
        int i = this.random.nextInt(100);

        return biomebase.b() == BiomeBase.Precipitation.SNOW ? (i < 80 ? 1 : 3) : (biomebase.o() == BiomeBase.Geography.DESERT ? 4 : (i < 50 ? 0 : (i < 90 ? 5 : 2)));
    }

    public static boolean c(EntityTypes<EntityRabbit> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        Block block = generatoraccess.getType(blockposition.down()).getBlock();

        return (block == Blocks.GRASS_BLOCK || block == Blocks.SNOW || block == Blocks.SAND) && generatoraccess.getLightLevel(blockposition, 0) > 8;
    }

    private boolean eg() {
        return this.bF == 0;
    }

    static class PathfinderGoalKillerRabbitMeleeAttack extends PathfinderGoalMeleeAttack {

        public PathfinderGoalKillerRabbitMeleeAttack(EntityRabbit entityrabbit) {
            super(entityrabbit, 1.4D, true);
        }

        @Override
        protected double a(EntityLiving entityliving) {
            return (double) (4.0F + entityliving.getWidth());
        }
    }

    static class PathfinderGoalRabbitPanic extends PathfinderGoalPanic {

        private final EntityRabbit f;

        public PathfinderGoalRabbitPanic(EntityRabbit entityrabbit, double d0) {
            super(entityrabbit, d0);
            this.f = entityrabbit;
        }

        @Override
        public void e() {
            super.e();
            this.f.d(this.b);
        }
    }

    static class PathfinderGoalEatCarrots extends PathfinderGoalGotoTarget {

        private final EntityRabbit entity;
        private boolean h;
        private boolean i;

        public PathfinderGoalEatCarrots(EntityRabbit entityrabbit) {
            super(entityrabbit, 0.699999988079071D, 16);
            this.entity = entityrabbit;
        }

        @Override
        public boolean a() {
            if (this.c <= 0) {
                if (!this.entity.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                    return false;
                }

                this.i = false;
                this.h = this.entity.eg();
                this.h = true;
            }

            return super.a();
        }

        @Override
        public boolean b() {
            return this.i && super.b();
        }

        @Override
        public void e() {
            super.e();
            this.entity.getControllerLook().a((double) this.e.getX() + 0.5D, (double) (this.e.getY() + 1), (double) this.e.getZ() + 0.5D, 10.0F, (float) this.entity.M());
            if (this.k()) {
                World world = this.entity.world;
                BlockPosition blockposition = this.e.up();
                IBlockData iblockdata = world.getType(blockposition);
                Block block = iblockdata.getBlock();

                if (this.i && block instanceof BlockCarrots) {
                    Integer integer = (Integer) iblockdata.get(BlockCarrots.AGE);

                    if (integer == 0) {
                        // CraftBukkit start
                        if (org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(this.entity, blockposition, Blocks.AIR.getBlockData()).isCancelled()) {
                            return;
                        }
                        // CraftBukkit end
                        world.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 2);
                        world.b(blockposition, true);
                    } else {
                        // CraftBukkit start
                        if (org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(
                                this.entity,
                                blockposition,
                                iblockdata.set(BlockCarrots.AGE, integer - 1)
                        ).isCancelled()) {
                            return;
                        }
                        // CraftBukkit end
                        world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockCarrots.AGE, integer - 1), 2);
                        world.triggerEffect(2001, blockposition, Block.getCombinedId(iblockdata));
                    }

                    this.entity.bF = 40;
                }

                this.i = false;
                this.c = 10;
            }

        }

        @Override
        protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
            Block block = iworldreader.getType(blockposition).getBlock();

            if (block == Blocks.FARMLAND && this.h && !this.i) {
                blockposition = blockposition.up();
                IBlockData iblockdata = iworldreader.getType(blockposition);

                block = iblockdata.getBlock();
                if (block instanceof BlockCarrots && ((BlockCarrots) block).isRipe(iblockdata)) {
                    this.i = true;
                    return true;
                }
            }

            return false;
        }
    }

    static class PathfinderGoalRabbitAvoidTarget<T extends EntityLiving> extends PathfinderGoalAvoidTarget<T> {

        private final EntityRabbit i;

        public PathfinderGoalRabbitAvoidTarget(EntityRabbit entityrabbit, Class<T> oclass, float f, double d0, double d1) {
            super(entityrabbit, oclass, f, d0, d1);
            this.i = entityrabbit;
        }

        @Override
        public boolean a() {
            return this.i.getRabbitType() != 99 && super.a();
        }
    }

    static class ControllerMoveRabbit extends ControllerMove {

        private final EntityRabbit i;
        private double j;

        public ControllerMoveRabbit(EntityRabbit entityrabbit) {
            super(entityrabbit);
            this.i = entityrabbit;
        }

        @Override
        public void a() {
            if (this.i.onGround && !this.i.jumping && !((EntityRabbit.ControllerJumpRabbit) this.i.bt).c()) {
                this.i.d(0.0D);
            } else if (this.b()) {
                this.i.d(this.j);
            }

            super.a();
        }

        @Override
        public void a(double d0, double d1, double d2, double d3) {
            if (this.i.isInWater()) {
                d3 = 1.5D;
            }

            super.a(d0, d1, d2, d3);
            if (d3 > 0.0D) {
                this.j = d3;
            }

        }
    }

    public class ControllerJumpRabbit extends ControllerJump {

        private final EntityRabbit c;
        private boolean d;

        public ControllerJumpRabbit(EntityRabbit entityrabbit) {
            super(entityrabbit);
            this.c = entityrabbit;
        }

        public boolean c() {
            return this.a;
        }

        public boolean d() {
            return this.d;
        }

        public void a(boolean flag) {
            this.d = flag;
        }

        @Override
        public void b() {
            if (this.a) {
                this.c.dV();
                this.a = false;
            }

        }
    }

    public static class GroupDataRabbit implements GroupDataEntity {

        public final int a;

        public GroupDataRabbit(int i) {
            this.a = i;
        }
    }
}
