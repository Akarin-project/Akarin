package net.minecraft.server;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;

public abstract class EntitySkeletonAbstract extends EntityMonster implements IRangedEntity {

    private final PathfinderGoalBowShoot<EntitySkeletonAbstract> b = new PathfinderGoalBowShoot<>(this, 1.0D, 20, 15.0F);
    private final PathfinderGoalMeleeAttack c = new PathfinderGoalMeleeAttack(this, 1.2D, false) {
        @Override
        public void d() {
            super.d();
            EntitySkeletonAbstract.this.q(false);
        }

        @Override
        public void c() {
            super.c();
            EntitySkeletonAbstract.this.q(true);
        }
    };

    protected EntitySkeletonAbstract(EntityTypes<? extends EntitySkeletonAbstract> entitytypes, World world) {
        super(entitytypes, world);
        this.dV();
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(2, new PathfinderGoalRestrictSun(this));
        this.goalSelector.a(3, new PathfinderGoalFleeSun(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalAvoidTarget<>(this, EntityWolf.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityTurtle.class, 10, true, false, EntityTurtle.bz));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(this.l(), 0.15F, 1.0F);
    }

    abstract SoundEffect l();

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    @Override
    public void movementTick() {
        boolean flag = this.dS();

        if (flag) {
            ItemStack itemstack = this.getEquipment(EnumItemSlot.HEAD);

            if (!itemstack.isEmpty()) {
                if (itemstack.e()) {
                    itemstack.setDamage(itemstack.getDamage() + this.random.nextInt(2));
                    if (itemstack.getDamage() >= itemstack.h()) {
                        this.c(EnumItemSlot.HEAD);
                        this.setSlot(EnumItemSlot.HEAD, ItemStack.a);
                    }
                }

                flag = false;
            }

            if (flag) {
                this.setOnFire(8);
            }
        }

        super.movementTick();
    }

    @Override
    public void passengerTick() {
        super.passengerTick();
        if (this.getVehicle() instanceof EntityCreature) {
            EntityCreature entitycreature = (EntityCreature) this.getVehicle();

            this.aK = entitycreature.aK;
        }

    }

    @Override
    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        super.a(difficultydamagescaler);
        this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        groupdataentity = super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
        this.a(difficultydamagescaler);
        this.b(difficultydamagescaler);
        this.dV();
        this.setCanPickupLoot(this.random.nextFloat() < 0.55F * difficultydamagescaler.d());
        if (this.getEquipment(EnumItemSlot.HEAD).isEmpty()) {
            LocalDate localdate = LocalDate.now();
            int i = localdate.get(ChronoField.DAY_OF_MONTH);
            int j = localdate.get(ChronoField.MONTH_OF_YEAR);

            if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
                this.setSlot(EnumItemSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.dropChanceArmor[EnumItemSlot.HEAD.b()] = 0.0F;
            }
        }

        return groupdataentity;
    }

    public void dV() {
        if (this.world != null && !this.world.isClientSide) {
            this.goalSelector.a((PathfinderGoal) this.c);
            this.goalSelector.a((PathfinderGoal) this.b);
            ItemStack itemstack = this.b(ProjectileHelper.a(this, Items.BOW));

            if (itemstack.getItem() == Items.BOW) {
                byte b0 = 20;

                if (this.world.getDifficulty() != EnumDifficulty.HARD) {
                    b0 = 40;
                }

                this.b.a(b0);
                this.goalSelector.a(4, this.b);
            } else {
                this.goalSelector.a(4, this.c);
            }

        }
    }

    @Override
    public void a(EntityLiving entityliving, float f) {
        ItemStack itemstack = this.f(this.b(ProjectileHelper.a(this, Items.BOW)));
        EntityArrow entityarrow = this.b(itemstack, f);
        double d0 = entityliving.locX - this.locX;
        double d1 = entityliving.getBoundingBox().minY + (double) (entityliving.getHeight() / 3.0F) - entityarrow.locY;
        double d2 = entityliving.locZ - this.locZ;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);

        entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float) (14 - this.world.getDifficulty().a() * 4));
        // CraftBukkit start
        org.bukkit.event.entity.EntityShootBowEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callEntityShootBowEvent(this, this.getItemInMainHand(), this.getItemInOffHand(), entityarrow, 0.8F); // Paper
        if (event.isCancelled()) {
            event.getProjectile().remove();
            return;
        }

        if (event.getProjectile() == entityarrow.getBukkitEntity()) {
            world.addEntity(entityarrow);
        }
        // CraftBukkit end
        this.a(SoundEffects.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        // this.world.addEntity(entityarrow); // CraftBukkit - moved up
    }

    protected EntityArrow b(ItemStack itemstack, float f) {
        return ProjectileHelper.a(this, itemstack, f);
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.dV();
    }

    @Override
    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        super.setSlot(enumitemslot, itemstack);
        if (!this.world.isClientSide) {
            this.dV();
        }

    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 1.74F;
    }

    @Override
    public double aO() {
        return -0.6D;
    }
}
