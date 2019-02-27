package net.minecraft.server;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public class EntityZombie extends EntityMonster {

    protected static final IAttribute c = (new AttributeRanged((IAttribute) null, "zombie.spawnReinforcements", 0.0D, 0.0D, 1.0D)).a("Spawn Reinforcements Chance");
    private static final UUID a = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    private static final AttributeModifier b = new AttributeModifier(EntityZombie.a, "Baby speed boost", 0.5D, 1);
    private static final DataWatcherObject<Boolean> bC = DataWatcher.a(EntityZombie.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> bD = DataWatcher.a(EntityZombie.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> bE = DataWatcher.a(EntityZombie.class, DataWatcherRegistry.i);
    public static final DataWatcherObject<Boolean> DROWN_CONVERTING = DataWatcher.a(EntityZombie.class, DataWatcherRegistry.i);
    private final PathfinderGoalBreakDoor bG;
    private boolean bH;
    private int bI;
    public int drownedConversionTime;
    private float bK;
    private float bL;

    public EntityZombie(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.bG = new PathfinderGoalBreakDoor(this);
        this.bK = -1.0F;
        this.setSize(0.6F, 1.95F);
    }

    public EntityZombie(World world) {
        this(EntityTypes.ZOMBIE, world);
    }

    protected void n() {
        this.goalSelector.a(4, new EntityZombie.a(Blocks.TURTLE_EGG, this, 1.0D, 3));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.l();
    }

    protected void l() {
        this.goalSelector.a(2, new PathfinderGoalZombieAttack(this, 1.0D, false));
        this.goalSelector.a(6, new PathfinderGoalMoveThroughVillage(this, 1.0D, false));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true, new Class[] { EntityPigZombie.class}));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillager.class, false));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
        this.targetSelector.a(5, new PathfinderGoalNearestAttackableTarget<>(this, EntityTurtle.class, 10, true, false, EntityTurtle.bC));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(35.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.23000000417232513D);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(3.0D);
        this.getAttributeInstance(GenericAttributes.h).setValue(2.0D);
        this.getAttributeMap().b(EntityZombie.c).setValue(this.random.nextDouble() * 0.10000000149011612D);
    }

    protected void x_() {
        super.x_();
        this.getDataWatcher().register(EntityZombie.bC, false);
        this.getDataWatcher().register(EntityZombie.bD, 0);
        this.getDataWatcher().register(EntityZombie.bE, false);
        this.getDataWatcher().register(EntityZombie.DROWN_CONVERTING, false);
    }

    public boolean isDrownConverting() {
        return (Boolean) this.getDataWatcher().get(EntityZombie.DROWN_CONVERTING);
    }

    public void s(boolean flag) {
        this.getDataWatcher().set(EntityZombie.bE, flag);
    }

    public boolean dH() {
        return this.bH;
    }

    public void t(boolean flag) {
        if (this.dz()) {
            if (this.bH != flag) {
                this.bH = flag;
                ((Navigation) this.getNavigation()).a(flag);
                if (flag) {
                    this.goalSelector.a(1, this.bG);
                } else {
                    this.goalSelector.a((PathfinderGoal) this.bG);
                }
            }
        } else if (this.bH) {
            this.goalSelector.a((PathfinderGoal) this.bG);
            this.bH = false;
        }

    }

    protected boolean dz() {
        return true;
    }

    public boolean isBaby() {
        return (Boolean) this.getDataWatcher().get(EntityZombie.bC);
    }

    protected int getExpValue(EntityHuman entityhuman) {
        if (this.isBaby()) {
            this.b_ = (int) ((float) this.b_ * 2.5F);
        }

        return super.getExpValue(entityhuman);
    }

    public void setBaby(boolean flag) {
        this.getDataWatcher().set(EntityZombie.bC, flag);
        if (this.world != null && !this.world.isClientSide) {
            AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

            attributeinstance.c(EntityZombie.b);
            if (flag) {
                attributeinstance.b(EntityZombie.b);
            }
        }

        this.v(flag);
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityZombie.bC.equals(datawatcherobject)) {
            this.v(this.isBaby());
        }

        super.a(datawatcherobject);
    }

    protected boolean dC() {
        return true;
    }

    public void tick() {
        if (!this.world.isClientSide) {
            if (this.isDrownConverting()) {
                --this.drownedConversionTime;
                if (this.drownedConversionTime < 0) {
                    this.dE();
                }
            } else if (this.dC()) {
                if (this.a(TagsFluid.WATER)) {
                    ++this.bI;
                    if (this.bI >= 600) {
                        this.startDrownedConversion(300);
                    }
                } else {
                    this.bI = -1;
                }
            }
        }

        super.tick();
    }

    public void movementTick() {
        boolean flag = this.L_() && this.dq();

        if (flag) {
            ItemStack itemstack = this.getEquipment(EnumItemSlot.HEAD);

            if (!itemstack.isEmpty()) {
                if (itemstack.e()) {
                    itemstack.setDamage(itemstack.getDamage() + this.random.nextInt(2));
                    if (itemstack.getDamage() >= itemstack.h()) {
                        this.c(itemstack);
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

    public void startDrownedConversion(int i) {
        this.drownedConversionTime = i;
        this.getDataWatcher().set(EntityZombie.DROWN_CONVERTING, true);
    }

    protected void dE() {
        this.a((EntityZombie) (new EntityDrowned(this.world)));
        this.world.a((EntityHuman) null, 1040, new BlockPosition((int) this.locX, (int) this.locY, (int) this.locZ), 0);
    }

    protected void a(EntityZombie entityzombie) {
        if (!this.world.isClientSide && !this.dead) {
            entityzombie.u(this);
            entityzombie.a(this.dj(), this.dH(), this.isBaby(), this.isNoAI());
            EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
            int i = aenumitemslot.length;

            for (int j = 0; j < i; ++j) {
                EnumItemSlot enumitemslot = aenumitemslot[j];
                ItemStack itemstack = this.getEquipment(enumitemslot);

                if (!itemstack.isEmpty()) {
                    entityzombie.setSlot(enumitemslot, itemstack);
                    entityzombie.a(enumitemslot, this.c(enumitemslot));
                }
            }

            if (this.hasCustomName()) {
                entityzombie.setCustomName(this.getCustomName());
                entityzombie.setCustomNameVisible(this.getCustomNameVisible());
            }

            this.world.addEntity(entityzombie);
            this.die();
        }
    }

    protected boolean L_() {
        return true;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (super.damageEntity(damagesource, f)) {
            EntityLiving entityliving = this.getGoalTarget();

            if (entityliving == null && damagesource.getEntity() instanceof EntityLiving) {
                entityliving = (EntityLiving) damagesource.getEntity();
            }

            if (entityliving != null && this.world.getDifficulty() == EnumDifficulty.HARD && (double) this.random.nextFloat() < this.getAttributeInstance(EntityZombie.c).getValue() && this.world.getGameRules().getBoolean("doMobSpawning")) {
                int i = MathHelper.floor(this.locX);
                int j = MathHelper.floor(this.locY);
                int k = MathHelper.floor(this.locZ);
                EntityZombie entityzombie = new EntityZombie(this.world);

                for (int l = 0; l < 50; ++l) {
                    int i1 = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    int j1 = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    int k1 = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);

                    if (this.world.getType(new BlockPosition(i1, j1 - 1, k1)).q() && this.world.getLightLevel(new BlockPosition(i1, j1, k1)) < 10) {
                        entityzombie.setPosition((double) i1, (double) j1, (double) k1);
                        if (!this.world.isPlayerNearby((double) i1, (double) j1, (double) k1, 7.0D) && this.world.a_(entityzombie, entityzombie.getBoundingBox()) && this.world.getCubes(entityzombie, entityzombie.getBoundingBox()) && !this.world.containsLiquid(entityzombie.getBoundingBox())) {
                            this.world.addEntity(entityzombie);
                            entityzombie.setGoalTarget(entityliving);
                            entityzombie.prepare(this.world.getDamageScaler(new BlockPosition(entityzombie)), (GroupDataEntity) null, (NBTTagCompound) null);
                            this.getAttributeInstance(EntityZombie.c).b(new AttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806D, 0));
                            entityzombie.getAttributeInstance(EntityZombie.c).b(new AttributeModifier("Zombie reinforcement callee charge", -0.05000000074505806D, 0));
                            break;
                        }
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean B(Entity entity) {
        boolean flag = super.B(entity);

        if (flag) {
            float f = this.world.getDamageScaler(new BlockPosition(this)).b();

            if (this.getItemInMainHand().isEmpty() && this.isBurning() && this.random.nextFloat() < f * 0.3F) {
                entity.setOnFire(2 * (int) f);
            }
        }

        return flag;
    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_ZOMBIE_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_ZOMBIE_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_ZOMBIE_DEATH;
    }

    protected SoundEffect dA() {
        return SoundEffects.ENTITY_ZOMBIE_STEP;
    }

    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(this.dA(), 0.15F, 1.0F);
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.at;
    }

    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        super.a(difficultydamagescaler);
        if (this.random.nextFloat() < (this.world.getDifficulty() == EnumDifficulty.HARD ? 0.05F : 0.01F)) {
            int i = this.random.nextInt(3);

            if (i == 0) {
                this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            } else {
                this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
            }
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        if (this.isBaby()) {
            nbttagcompound.setBoolean("IsBaby", true);
        }

        nbttagcompound.setBoolean("CanBreakDoors", this.dH());
        nbttagcompound.setInt("InWaterTime", this.isInWater() ? this.bI : -1);
        nbttagcompound.setInt("DrownedConversionTime", this.isDrownConverting() ? this.drownedConversionTime : -1);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.getBoolean("IsBaby")) {
            this.setBaby(true);
        }

        this.t(nbttagcompound.getBoolean("CanBreakDoors"));
        this.bI = nbttagcompound.getInt("InWaterTime");
        if (nbttagcompound.hasKeyOfType("DrownedConversionTime", 99) && nbttagcompound.getInt("DrownedConversionTime") > -1) {
            this.startDrownedConversion(nbttagcompound.getInt("DrownedConversionTime"));
        }

    }

    public void b(EntityLiving entityliving) {
        super.b(entityliving);
        if ((this.world.getDifficulty() == EnumDifficulty.NORMAL || this.world.getDifficulty() == EnumDifficulty.HARD) && entityliving instanceof EntityVillager) {
            if (this.world.getDifficulty() != EnumDifficulty.HARD && this.random.nextBoolean()) {
                return;
            }

            EntityVillager entityvillager = (EntityVillager) entityliving;
            EntityZombieVillager entityzombievillager = new EntityZombieVillager(this.world);

            entityzombievillager.u(entityvillager);
            this.world.kill(entityvillager);
            entityzombievillager.prepare(this.world.getDamageScaler(new BlockPosition(entityzombievillager)), new EntityZombie.GroupDataZombie(false), (NBTTagCompound) null);
            entityzombievillager.setProfession(entityvillager.getProfession());
            entityzombievillager.setBaby(entityvillager.isBaby());
            entityzombievillager.setNoAI(entityvillager.isNoAI());
            if (entityvillager.hasCustomName()) {
                entityzombievillager.setCustomName(entityvillager.getCustomName());
                entityzombievillager.setCustomNameVisible(entityvillager.getCustomNameVisible());
            }

            this.world.addEntity(entityzombievillager);
            this.world.a((EntityHuman) null, 1026, new BlockPosition(this), 0);
        }

    }

    public float getHeadHeight() {
        float f = 1.74F;

        if (this.isBaby()) {
            f = (float) ((double) f - 0.81D);
        }

        return f;
    }

    protected boolean d(ItemStack itemstack) {
        return itemstack.getItem() == Items.EGG && this.isBaby() && this.isPassenger() ? false : super.d(itemstack);
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        Object object = super.prepare(difficultydamagescaler, groupdataentity, nbttagcompound);
        float f = difficultydamagescaler.d();

        this.p(this.random.nextFloat() < 0.55F * f);
        if (object == null) {
            object = new EntityZombie.GroupDataZombie(this.world.random.nextFloat() < 0.05F);
        }

        if (object instanceof EntityZombie.GroupDataZombie) {
            EntityZombie.GroupDataZombie entityzombie_groupdatazombie = (EntityZombie.GroupDataZombie) object;

            if (entityzombie_groupdatazombie.a) {
                this.setBaby(true);
                if ((double) this.world.random.nextFloat() < 0.05D) {
                    List<EntityChicken> list = this.world.a(EntityChicken.class, this.getBoundingBox().grow(5.0D, 3.0D, 5.0D), IEntitySelector.c);

                    if (!list.isEmpty()) {
                        EntityChicken entitychicken = (EntityChicken) list.get(0);

                        entitychicken.s(true);
                        this.startRiding(entitychicken);
                    }
                } else if ((double) this.world.random.nextFloat() < 0.05D) {
                    EntityChicken entitychicken1 = new EntityChicken(this.world);

                    entitychicken1.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, 0.0F);
                    entitychicken1.prepare(difficultydamagescaler, (GroupDataEntity) null, (NBTTagCompound) null);
                    entitychicken1.s(true);
                    this.world.addEntity(entitychicken1);
                    this.startRiding(entitychicken1);
                }
            }

            this.t(this.dz() && this.random.nextFloat() < f * 0.1F);
            this.a(difficultydamagescaler);
            this.b(difficultydamagescaler);
        }

        if (this.getEquipment(EnumItemSlot.HEAD).isEmpty()) {
            LocalDate localdate = LocalDate.now();
            int i = localdate.get(ChronoField.DAY_OF_MONTH);
            int j = localdate.get(ChronoField.MONTH_OF_YEAR);

            if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
                this.setSlot(EnumItemSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.dropChanceArmor[EnumItemSlot.HEAD.b()] = 0.0F;
            }
        }

        this.a(f);
        return (GroupDataEntity) object;
    }

    protected void a(boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        this.p(flag);
        this.t(this.dz() && flag1);
        this.a(this.world.getDamageScaler(new BlockPosition(this)).d());
        this.setBaby(flag2);
        this.setNoAI(flag3);
    }

    protected void a(float f) {
        this.getAttributeInstance(GenericAttributes.c).b(new AttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05000000074505806D, 0));
        double d0 = this.random.nextDouble() * 1.5D * (double) f;

        if (d0 > 1.0D) {
            this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).b(new AttributeModifier("Random zombie-spawn bonus", d0, 2));
        }

        if (this.random.nextFloat() < f * 0.05F) {
            this.getAttributeInstance(EntityZombie.c).b(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25D + 0.5D, 0));
            this.getAttributeInstance(GenericAttributes.maxHealth).b(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0D + 1.0D, 2));
            this.t(this.dz());
        }

    }

    public void v(boolean flag) {
        this.v(flag ? 0.5F : 1.0F);
    }

    public final void setSize(float f, float f1) {
        boolean flag = this.bK > 0.0F && this.bL > 0.0F;

        this.bK = f;
        this.bL = f1;
        if (!flag) {
            this.v(1.0F);
        }

    }

    protected final void v(float f) {
        super.setSize(this.bK * f, this.bL * f);
    }

    public double aI() {
        return this.isBaby() ? 0.0D : -0.45D;
    }

    public void die(DamageSource damagesource) {
        super.die(damagesource);
        if (damagesource.getEntity() instanceof EntityCreeper) {
            EntityCreeper entitycreeper = (EntityCreeper) damagesource.getEntity();

            if (entitycreeper.isPowered() && entitycreeper.canCauseHeadDrop()) {
                entitycreeper.setCausedHeadDrop();
                ItemStack itemstack = this.dB();

                if (!itemstack.isEmpty()) {
                    this.a_(itemstack);
                }
            }
        }

    }

    protected ItemStack dB() {
        return new ItemStack(Items.ZOMBIE_HEAD);
    }

    class a extends PathfinderGoalRemoveBlock {

        a(Block block, EntityCreature entitycreature, double d0, int i) {
            super(block, entitycreature, d0, i);
        }

        public void a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
            generatoraccess.a((EntityHuman) null, blockposition, SoundEffects.ENTITY_ZOMBIE_DESTROY_EGG, SoundCategory.HOSTILE, 0.5F, 0.9F + EntityZombie.this.random.nextFloat() * 0.2F);
        }

        public void a(World world, BlockPosition blockposition) {
            world.a((EntityHuman) null, blockposition, SoundEffects.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
        }

        public double g() {
            return 1.3D;
        }
    }

    public class GroupDataZombie implements GroupDataEntity {

        public boolean a;

        private GroupDataZombie(boolean flag) {
            this.a = flag;
        }
    }
}
