package net.minecraft.server;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityDrowned extends EntityZombie implements IRangedEntity {

    private boolean bC;
    protected final NavigationGuardian a;
    protected final Navigation b;

    public EntityDrowned(World world) {
        super(EntityTypes.DROWNED, world);
        this.Q = 1.0F;
        this.moveController = new EntityDrowned.e(this);
        this.a(PathType.WATER, 0.0F);
        this.a = new NavigationGuardian(this, world);
        this.b = new Navigation(this, world);
    }

    protected void l() {
        this.goalSelector.a(1, new EntityDrowned.d(this, 1.0D));
        this.goalSelector.a(2, new EntityDrowned.g(this, 1.0D, 40, 10.0F));
        this.goalSelector.a(2, new EntityDrowned.a(this, 1.0D, false));
        this.goalSelector.a(5, new EntityDrowned.c(this, 1.0D));
        this.goalSelector.a(6, new EntityDrowned.f(this, 1.0D, this.world.getSeaLevel()));
        this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true, new Class[] { EntityDrowned.class}));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, new EntityDrowned.b(this)));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillager.class, false));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
        this.targetSelector.a(5, new PathfinderGoalNearestAttackableTarget<>(this, EntityTurtle.class, 10, true, false, EntityTurtle.bC));
    }

    protected NavigationAbstract b(World world) {
        return super.b(world);
    }

    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        groupdataentity = super.prepare(difficultydamagescaler, groupdataentity, nbttagcompound);
        if (this.getEquipment(EnumItemSlot.OFFHAND).isEmpty() && this.random.nextFloat() < 0.03F) {
            this.setSlot(EnumItemSlot.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
            this.dropChanceHand[EnumItemSlot.OFFHAND.b()] = 2.0F;
        }

        return groupdataentity;
    }

    public boolean a(GeneratorAccess generatoraccess, boolean flag) {
        BiomeBase biomebase = generatoraccess.getBiome(new BlockPosition(this.locX, this.locY, this.locZ));

        return biomebase != Biomes.RIVER && biomebase != Biomes.FROZEN_RIVER ? this.random.nextInt(40) == 0 && this.dF() && super.a(generatoraccess, flag) : this.random.nextInt(15) == 0 && super.a(generatoraccess, flag);
    }

    private boolean dF() {
        return this.getBoundingBox().minY < (double) (this.world.getSeaLevel() - 5);
    }

    protected boolean dz() {
        return false;
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.aM;
    }

    protected SoundEffect D() {
        return this.isInWater() ? SoundEffects.ENTITY_DROWNED_AMBIENT_WATER : SoundEffects.ENTITY_DROWNED_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return this.isInWater() ? SoundEffects.ENTITY_DROWNED_HURT_WATER : SoundEffects.ENTITY_DROWNED_HURT;
    }

    protected SoundEffect cs() {
        return this.isInWater() ? SoundEffects.ENTITY_DROWNED_DEATH_WATER : SoundEffects.ENTITY_DROWNED_DEATH;
    }

    protected SoundEffect dA() {
        return SoundEffects.ENTITY_DROWNED_STEP;
    }

    protected SoundEffect ad() {
        return SoundEffects.ENTITY_DROWNED_SWIM;
    }

    protected ItemStack dB() {
        return ItemStack.a;
    }

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

    protected boolean a(ItemStack itemstack, ItemStack itemstack1, EnumItemSlot enumitemslot) {
        return itemstack1.getItem() == Items.NAUTILUS_SHELL ? false : (itemstack1.getItem() == Items.TRIDENT ? (itemstack.getItem() == Items.TRIDENT ? itemstack.getDamage() < itemstack1.getDamage() : false) : (itemstack.getItem() == Items.TRIDENT ? true : super.a(itemstack, itemstack1, enumitemslot)));
    }

    protected boolean dC() {
        return false;
    }

    public boolean a(IWorldReader iworldreader) {
        return iworldreader.a_(this, this.getBoundingBox()) && iworldreader.getCubes(this, this.getBoundingBox());
    }

    public boolean f(@Nullable EntityLiving entityliving) {
        return entityliving != null ? !this.world.L() || entityliving.isInWater() : false;
    }

    public boolean bw() {
        return !this.isSwimming();
    }

    private boolean dI() {
        if (this.bC) {
            return true;
        } else {
            EntityLiving entityliving = this.getGoalTarget();

            return entityliving != null && entityliving.isInWater();
        }
    }

    public void a(float f, float f1, float f2) {
        if (this.cP() && this.isInWater() && this.dI()) {
            this.a(f, f1, f2, 0.01F);
            this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
            this.motX *= 0.8999999761581421D;
            this.motY *= 0.8999999761581421D;
            this.motZ *= 0.8999999761581421D;
        } else {
            super.a(f, f1, f2);
        }

    }

    public void as() {
        if (!this.world.isClientSide) {
            if (this.cP() && this.isInWater() && this.dI()) {
                this.navigation = this.a;
                this.setSwimming(true);
            } else {
                this.navigation = this.b;
                this.setSwimming(false);
            }
        }

    }

    protected boolean dD() {
        PathEntity pathentity = this.getNavigation().m();

        if (pathentity != null) {
            PathPoint pathpoint = pathentity.i();

            if (pathpoint != null) {
                double d0 = this.d((double) pathpoint.a, (double) pathpoint.b, (double) pathpoint.c);

                if (d0 < 4.0D) {
                    return true;
                }
            }
        }

        return false;
    }

    public void a(EntityLiving entityliving, float f) {
        EntityThrownTrident entitythrowntrident = new EntityThrownTrident(this.world, this, new ItemStack(Items.TRIDENT));
        double d0 = entityliving.locX - this.locX;
        double d1 = entityliving.getBoundingBox().minY + (double) (entityliving.length / 3.0F) - entitythrowntrident.locY;
        double d2 = entityliving.locZ - this.locZ;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);

        entitythrowntrident.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float) (14 - this.world.getDifficulty().a() * 4));
        this.a(SoundEffects.ENTITY_DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.world.addEntity(entitythrowntrident);
    }

    public void a(boolean flag) {
        this.bC = flag;
    }

    static class b implements Predicate<EntityHuman> {

        private final EntityDrowned a;

        public b(EntityDrowned entitydrowned) {
            this.a = entitydrowned;
        }

        public boolean test(@Nullable EntityHuman entityhuman) {
            return this.a.f((EntityLiving) entityhuman);
        }
    }

    static class e extends ControllerMove {

        private final EntityDrowned i;

        public e(EntityDrowned entitydrowned) {
            super(entitydrowned);
            this.i = entitydrowned;
        }

        public void a() {
            EntityLiving entityliving = this.i.getGoalTarget();

            if (this.i.dI() && this.i.isInWater()) {
                if (entityliving != null && entityliving.locY > this.i.locY || this.i.bC) {
                    this.i.motY += 0.002D;
                }

                if (this.h != ControllerMove.Operation.MOVE_TO || this.i.getNavigation().p()) {
                    this.i.o(0.0F);
                    return;
                }

                double d0 = this.b - this.i.locX;
                double d1 = this.c - this.i.locY;
                double d2 = this.d - this.i.locZ;
                double d3 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

                d1 /= d3;
                float f = (float) (MathHelper.c(d2, d0) * 57.2957763671875D) - 90.0F;

                this.i.yaw = this.a(this.i.yaw, f, 90.0F);
                this.i.aQ = this.i.yaw;
                float f1 = (float) (this.e * this.i.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());

                this.i.o(this.i.cK() + (f1 - this.i.cK()) * 0.125F);
                this.i.motY += (double) this.i.cK() * d1 * 0.1D;
                this.i.motX += (double) this.i.cK() * d0 * 0.005D;
                this.i.motZ += (double) this.i.cK() * d2 * 0.005D;
            } else {
                if (!this.i.onGround) {
                    this.i.motY -= 0.008D;
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

        public boolean a() {
            return super.a() && this.d.f(this.d.getGoalTarget());
        }

        public boolean b() {
            return super.b() && this.d.f(this.d.getGoalTarget());
        }
    }

    static class d extends PathfinderGoal {

        private final EntityCreature a;
        private double b;
        private double c;
        private double d;
        private final double e;
        private final World f;

        public d(EntityCreature entitycreature, double d0) {
            this.a = entitycreature;
            this.e = d0;
            this.f = entitycreature.world;
            this.a(1);
        }

        public boolean a() {
            if (!this.f.L()) {
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

        public boolean b() {
            return !this.a.getNavigation().p();
        }

        public void c() {
            this.a.getNavigation().a(this.b, this.c, this.d, this.e);
        }

        @Nullable
        private Vec3D g() {
            Random random = this.a.getRandom();
            BlockPosition blockposition = new BlockPosition(this.a.locX, this.a.getBoundingBox().minY, this.a.locZ);

            for (int i = 0; i < 10; ++i) {
                BlockPosition blockposition1 = blockposition.a(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);

                if (this.f.getType(blockposition1).getBlock() == Blocks.WATER) {
                    return new Vec3D((double) blockposition1.getX(), (double) blockposition1.getY(), (double) blockposition1.getZ());
                }
            }

            return null;
        }
    }

    static class c extends PathfinderGoalGotoTarget {

        private final EntityDrowned f;

        public c(EntityDrowned entitydrowned, double d0) {
            super(entitydrowned, d0, 8, 2);
            this.f = entitydrowned;
        }

        public boolean a() {
            return super.a() && !this.f.world.L() && this.f.isInWater() && this.f.locY >= (double) (this.f.world.getSeaLevel() - 3);
        }

        public boolean b() {
            return super.b();
        }

        protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
            BlockPosition blockposition1 = blockposition.up();

            return iworldreader.isEmpty(blockposition1) && iworldreader.isEmpty(blockposition1.up()) ? iworldreader.getType(blockposition).q() : false;
        }

        public void c() {
            this.f.a(false);
            this.f.navigation = this.f.b;
            super.c();
        }

        public void d() {
            super.d();
        }
    }

    static class f extends PathfinderGoal {

        private final EntityDrowned a;
        private final double b;
        private final int c;
        private boolean d;

        public f(EntityDrowned entitydrowned, double d0, int i) {
            this.a = entitydrowned;
            this.b = d0;
            this.c = i;
        }

        public boolean a() {
            return !this.a.world.L() && this.a.isInWater() && this.a.locY < (double) (this.c - 2);
        }

        public boolean b() {
            return this.a() && !this.d;
        }

        public void e() {
            if (this.a.locY < (double) (this.c - 1) && (this.a.getNavigation().p() || this.a.dD())) {
                Vec3D vec3d = RandomPositionGenerator.a(this.a, 4, 8, new Vec3D(this.a.locX, (double) (this.c - 1), this.a.locZ));

                if (vec3d == null) {
                    this.d = true;
                    return;
                }

                this.a.getNavigation().a(vec3d.x, vec3d.y, vec3d.z, this.b);
            }

        }

        public void c() {
            this.a.a(true);
            this.d = false;
        }

        public void d() {
            this.a.a(false);
        }
    }

    static class g extends PathfinderGoalArrowAttack {

        private final EntityDrowned a;

        public g(IRangedEntity irangedentity, double d0, int i, float f) {
            super(irangedentity, d0, i, f);
            this.a = (EntityDrowned) irangedentity;
        }

        public boolean a() {
            return super.a() && this.a.getItemInMainHand().getItem() == Items.TRIDENT;
        }

        public void c() {
            super.c();
            this.a.s(true);
        }

        public void d() {
            super.d();
            this.a.s(false);
        }
    }
}
