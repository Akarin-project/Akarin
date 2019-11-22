package net.minecraft.server;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;

public class EntityDrowned extends EntityZombie implements IRangedEntity {

    private boolean bz;
    protected final NavigationGuardian b;
    protected final Navigation c;

    public EntityDrowned(EntityTypes<? extends EntityDrowned> entitytypes, World world) {
        super(entitytypes, world);
        this.K = 1.0F;
        this.moveController = new EntityDrowned.d(this);
        this.a(PathType.WATER, 0.0F);
        this.b = new NavigationGuardian(this, world);
        this.c = new Navigation(this, world);
    }

    @Override
    protected void l() {
        this.goalSelector.a(1, new EntityDrowned.c(this, 1.0D));
        this.goalSelector.a(2, new EntityDrowned.f(this, 1.0D, 40, 10.0F));
        this.goalSelector.a(2, new EntityDrowned.a(this, 1.0D, false));
        this.goalSelector.a(5, new EntityDrowned.b(this, 1.0D));
        this.goalSelector.a(6, new EntityDrowned.e(this, 1.0D, this.world.getSeaLevel()));
        this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityDrowned.class})).a(EntityPigZombie.class));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, this::h));
        if ( world.spigotConfig.zombieAggressiveTowardsVillager ) this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, false)); // Paper
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
        this.targetSelector.a(5, new PathfinderGoalNearestAttackableTarget<>(this, EntityTurtle.class, 10, true, false, EntityTurtle.bz));
    }

    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        groupdataentity = super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
        if (this.getEquipment(EnumItemSlot.OFFHAND).isEmpty() && this.random.nextFloat() < 0.03F) {
            this.setSlot(EnumItemSlot.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
            this.dropChanceHand[EnumItemSlot.OFFHAND.b()] = 2.0F;
        }

        return groupdataentity;
    }

    public static boolean b(EntityTypes<EntityDrowned> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        BiomeBase biomebase = generatoraccess.getBiome(blockposition);
        boolean flag = generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL && a(generatoraccess, blockposition, random) && (enummobspawn == EnumMobSpawn.SPAWNER || generatoraccess.getFluid(blockposition).a(TagsFluid.WATER));

        return biomebase != Biomes.RIVER && biomebase != Biomes.FROZEN_RIVER ? random.nextInt(40) == 0 && a(generatoraccess, blockposition) && flag : random.nextInt(15) == 0 && flag;
    }

    private static boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        return blockposition.getY() < generatoraccess.getSeaLevel() - 5;
    }

    @Override
    protected boolean dV() {
        return false;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.isInWater() ? SoundEffects.ENTITY_DROWNED_AMBIENT_WATER : SoundEffects.ENTITY_DROWNED_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return this.isInWater() ? SoundEffects.ENTITY_DROWNED_HURT_WATER : SoundEffects.ENTITY_DROWNED_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return this.isInWater() ? SoundEffects.ENTITY_DROWNED_DEATH_WATER : SoundEffects.ENTITY_DROWNED_DEATH;
    }

    @Override
    protected SoundEffect getSoundStep() {
        return SoundEffects.ENTITY_DROWNED_STEP;
    }

    @Override
    protected SoundEffect getSoundSwim() {
        return SoundEffects.ENTITY_DROWNED_SWIM;
    }

    @Override
    protected ItemStack dX() {
        return ItemStack.a;
    }

    @Override
    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        if ((double) this.random.nextFloat() > 0.9D) {
            int i = this.random.nextInt(16);

            if (i < 10) {
                this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.TRIDENT));
            } else {
                this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
            }
        }

    }

    @Override
    protected boolean a(ItemStack itemstack, ItemStack itemstack1, EnumItemSlot enumitemslot) {
        return itemstack1.getItem() == Items.NAUTILUS_SHELL ? false : (itemstack1.getItem() == Items.TRIDENT ? (itemstack.getItem() == Items.TRIDENT ? itemstack.getDamage() < itemstack1.getDamage() : false) : (itemstack.getItem() == Items.TRIDENT ? true : super.a(itemstack, itemstack1, enumitemslot)));
    }

    @Override
    protected boolean dY() {
        return false;
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        return iworldreader.i(this);
    }

    public boolean h(@Nullable EntityLiving entityliving) {
        return entityliving != null ? !this.world.J() || entityliving.isInWater() : false;
    }

    @Override
    public boolean bE() {
        return !this.isSwimming();
    }

    private boolean ee() {
        if (this.bz) {
            return true;
        } else {
            EntityLiving entityliving = this.getGoalTarget();

            return entityliving != null && entityliving.isInWater();
        }
    }

    @Override
    public void e(Vec3D vec3d) {
        if (this.df() && this.isInWater() && this.ee()) {
            this.a(0.01F, vec3d);
            this.move(EnumMoveType.SELF, this.getMot());
            this.setMot(this.getMot().a(0.9D));
        } else {
            super.e(vec3d);
        }

    }

    @Override
    public void ax() {
        if (!this.world.isClientSide) {
            if (this.df() && this.isInWater() && this.ee()) {
                this.navigation = this.b;
                this.setSwimming(true);
            } else {
                this.navigation = this.c;
                this.setSwimming(false);
            }
        }

    }

    protected boolean dZ() {
        PathEntity pathentity = this.getNavigation().l();

        if (pathentity != null) {
            BlockPosition blockposition = pathentity.k();

            if (blockposition != null) {
                double d0 = this.e((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());

                if (d0 < 4.0D) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void a(EntityLiving entityliving, float f) {
        EntityThrownTrident entitythrowntrident = new EntityThrownTrident(this.world, this, new ItemStack(Items.TRIDENT));
        double d0 = entityliving.locX - this.locX;
        double d1 = entityliving.getBoundingBox().minY + (double) (entityliving.getHeight() / 3.0F) - entitythrowntrident.locY;
        double d2 = entityliving.locZ - this.locZ;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);

        entitythrowntrident.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float) (14 - this.world.getDifficulty().a() * 4));
        this.a(SoundEffects.ENTITY_DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.world.addEntity(entitythrowntrident);
    }

    public void r(boolean flag) {
        this.bz = flag;
    }

    static class d extends ControllerMove {

        private final EntityDrowned i;

        public d(EntityDrowned entitydrowned) {
            super(entitydrowned);
            this.i = entitydrowned;
        }

        @Override
        public void a() {
            EntityLiving entityliving = this.i.getGoalTarget();

            if (this.i.ee() && this.i.isInWater()) {
                if (entityliving != null && entityliving.locY > this.i.locY || this.i.bz) {
                    this.i.setMot(this.i.getMot().add(0.0D, 0.002D, 0.0D));
                }

                if (this.h != ControllerMove.Operation.MOVE_TO || this.i.getNavigation().n()) {
                    this.i.o(0.0F);
                    return;
                }

                double d0 = this.b - this.i.locX;
                double d1 = this.c - this.i.locY;
                double d2 = this.d - this.i.locZ;
                double d3 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

                d1 /= d3;
                float f = (float) (MathHelper.d(d2, d0) * 57.2957763671875D) - 90.0F;

                this.i.yaw = this.a(this.i.yaw, f, 90.0F);
                this.i.aK = this.i.yaw;
                float f1 = (float) (this.e * this.i.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());
                float f2 = MathHelper.g(0.125F, this.i.db(), f1);

                this.i.o(f2);
                this.i.setMot(this.i.getMot().add((double) f2 * d0 * 0.005D, (double) f2 * d1 * 0.1D, (double) f2 * d2 * 0.005D));
            } else {
                if (!this.i.onGround) {
                    this.i.setMot(this.i.getMot().add(0.0D, -0.008D, 0.0D));
                }

                super.a();
            }

        }
    }

    static class a extends PathfinderGoalZombieAttack {

        private final EntityDrowned d;

        public a(EntityDrowned entitydrowned, double d0, boolean flag) {
            super((EntityZombie) entitydrowned, d0, flag);
            this.d = entitydrowned;
        }

        @Override
        public boolean a() {
            return super.a() && this.d.h(this.d.getGoalTarget());
        }

        @Override
        public boolean b() {
            return super.b() && this.d.h(this.d.getGoalTarget());
        }
    }

    static class c extends PathfinderGoal {

        private final EntityCreature a;
        private double b;
        private double c;
        private double d;
        private final double e;
        private final World f;

        public c(EntityCreature entitycreature, double d0) {
            this.a = entitycreature;
            this.e = d0;
            this.f = entitycreature.world;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            if (!this.f.J()) {
                return false;
            } else if (this.a.isInWater()) {
                return false;
            } else {
                Vec3D vec3d = this.g();

                if (vec3d == null) {
                    return false;
                } else {
                    this.b = vec3d.x;
                    this.c = vec3d.y;
                    this.d = vec3d.z;
                    return true;
                }
            }
        }

        @Override
        public boolean b() {
            return !this.a.getNavigation().n();
        }

        @Override
        public void c() {
            this.a.getNavigation().a(this.b, this.c, this.d, this.e);
        }

        @Nullable
        private Vec3D g() {
            Random random = this.a.getRandom();
            BlockPosition blockposition = new BlockPosition(this.a.locX, this.a.getBoundingBox().minY, this.a.locZ);

            for (int i = 0; i < 10; ++i) {
                BlockPosition blockposition1 = blockposition.b(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);

                if (this.f.getType(blockposition1).getBlock() == Blocks.WATER) {
                    return new Vec3D((double) blockposition1.getX(), (double) blockposition1.getY(), (double) blockposition1.getZ());
                }
            }

            return null;
        }
    }

    static class b extends PathfinderGoalGotoTarget {

        private final EntityDrowned g;

        public b(EntityDrowned entitydrowned, double d0) {
            super(entitydrowned, d0, 8, 2);
            this.g = entitydrowned;
        }

        @Override
        public boolean a() {
            return super.a() && !this.g.world.J() && this.g.isInWater() && this.g.locY >= (double) (this.g.world.getSeaLevel() - 3);
        }

        @Override
        public boolean b() {
            return super.b();
        }

        @Override
        protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
            BlockPosition blockposition1 = blockposition.up();

            return iworldreader.isEmpty(blockposition1) && iworldreader.isEmpty(blockposition1.up()) ? iworldreader.getType(blockposition).a((IBlockAccess) iworldreader, blockposition, (Entity) this.g) : false;
        }

        @Override
        public void c() {
            this.g.r(false);
            this.g.navigation = this.g.c;
            super.c();
        }

        @Override
        public void d() {
            super.d();
        }
    }

    static class e extends PathfinderGoal {

        private final EntityDrowned a;
        private final double b;
        private final int c;
        private boolean d;

        public e(EntityDrowned entitydrowned, double d0, int i) {
            this.a = entitydrowned;
            this.b = d0;
            this.c = i;
        }

        @Override
        public boolean a() {
            return !this.a.world.J() && this.a.isInWater() && this.a.locY < (double) (this.c - 2);
        }

        @Override
        public boolean b() {
            return this.a() && !this.d;
        }

        @Override
        public void e() {
            if (this.a.locY < (double) (this.c - 1) && (this.a.getNavigation().n() || this.a.dZ())) {
                Vec3D vec3d = RandomPositionGenerator.a(this.a, 4, 8, new Vec3D(this.a.locX, (double) (this.c - 1), this.a.locZ));

                if (vec3d == null) {
                    this.d = true;
                    return;
                }

                this.a.getNavigation().a(vec3d.x, vec3d.y, vec3d.z, this.b);
            }

        }

        @Override
        public void c() {
            this.a.r(true);
            this.d = false;
        }

        @Override
        public void d() {
            this.a.r(false);
        }
    }

    static class f extends PathfinderGoalArrowAttack {

        private final EntityDrowned a;

        public f(IRangedEntity irangedentity, double d0, int i, float f) {
            super(irangedentity, d0, i, f);
            this.a = (EntityDrowned) irangedentity;
        }

        @Override
        public boolean a() {
            return super.a() && this.a.getItemInMainHand().getItem() == Items.TRIDENT;
        }

        @Override
        public void c() {
            super.c();
            this.a.q(true);
            this.a.c(EnumHand.MAIN_HAND);
        }

        @Override
        public void d() {
            super.d();
            this.a.dp();
            this.a.q(false);
        }
    }
}
