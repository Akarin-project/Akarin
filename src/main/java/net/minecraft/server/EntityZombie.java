package net.minecraft.server;

import com.mojang.datafixers.types.DynamicOps;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTransformEvent;
// CraftBukkit end

public class EntityZombie extends EntityMonster {

    protected static final IAttribute d = (new AttributeRanged((IAttribute) null, "zombie.spawnReinforcements", 0.0D, 0.0D, 1.0D)).a("Spawn Reinforcements Chance");
    private static final UUID b = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    private final AttributeModifier c = new AttributeModifier(EntityZombie.b, "Baby speed boost", world.paperConfig.babyZombieMovementModifier, AttributeModifier.Operation.MULTIPLY_BASE); private final AttributeModifier babyModifier = this.c; // Paper - remove static - Make baby speed configurable
    private static final DataWatcherObject<Boolean> bz = DataWatcher.a(EntityZombie.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> bA = DataWatcher.a(EntityZombie.class, DataWatcherRegistry.b);
    public static final DataWatcherObject<Boolean> DROWN_CONVERTING = DataWatcher.a(EntityZombie.class, DataWatcherRegistry.i);
    private static final Predicate<EnumDifficulty> bC = (enumdifficulty) -> {
        return enumdifficulty == EnumDifficulty.HARD;
    };
    private final PathfinderGoalBreakDoor bD;
    private boolean bE;
    private int bF;
    public int drownedConversionTime;
    private int lastTick = MinecraftServer.currentTick; // CraftBukkit - add field
    private boolean shouldBurnInDay = true; // Paper

    public EntityZombie(EntityTypes<? extends EntityZombie> entitytypes, World world) {
        super(entitytypes, world);
        this.bD = new PathfinderGoalBreakDoor(this, EntityZombie.bC);
    }

    public EntityZombie(World world) {
        this(EntityTypes.ZOMBIE, world);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(4, new EntityZombie.a(this, 1.0D, 3));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.l();
    }

    protected void l() {
        this.goalSelector.a(2, new PathfinderGoalZombieAttack(this, 1.0D, false));
        this.goalSelector.a(6, new PathfinderGoalMoveThroughVillage(this, 1.0D, true, 4, this::ed));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).a(EntityPigZombie.class));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
        if ( world.spigotConfig.zombieAggressiveTowardsVillager ) this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, false)); // Spigot
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
        this.targetSelector.a(5, new PathfinderGoalNearestAttackableTarget<>(this, EntityTurtle.class, 10, true, false, EntityTurtle.bz));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(35.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.23000000417232513D);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(3.0D);
        this.getAttributeInstance(GenericAttributes.ARMOR).setValue(2.0D);
        this.getAttributeMap().b(EntityZombie.d).setValue(this.random.nextDouble() * 0.10000000149011612D);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.getDataWatcher().register(EntityZombie.bz, false);
        this.getDataWatcher().register(EntityZombie.bA, 0);
        this.getDataWatcher().register(EntityZombie.DROWN_CONVERTING, false);
    }

    public boolean isDrowning() { return isDrownConverting(); } // Paper - OBFHELPER
    public boolean isDrownConverting() {
        return (Boolean) this.getDataWatcher().get(EntityZombie.DROWN_CONVERTING);
    }

    public boolean ed() {
        return this.bE;
    }

    public void s(boolean flag) {
        if (this.dV()) {
            if (this.bE != flag) {
                this.bE = flag;
                ((Navigation) this.getNavigation()).a(flag);
                if (flag) {
                    this.goalSelector.a(1, this.bD);
                } else {
                    this.goalSelector.a((PathfinderGoal) this.bD);
                }
            }
        } else if (this.bE) {
            this.goalSelector.a((PathfinderGoal) this.bD);
            this.bE = false;
        }

    }

    protected boolean dV() {
        return true;
    }

    @Override
    public boolean isBaby() {
        return (Boolean) this.getDataWatcher().get(EntityZombie.bz);
    }

    @Override
    protected int getExpValue(EntityHuman entityhuman) {
        if (this.isBaby()) {
            this.f = (int) ((float) this.f * 2.5F);
        }

        return super.getExpValue(entityhuman);
    }

    public void setBaby(boolean flag) {
        this.getDataWatcher().set(EntityZombie.bz, flag);
        if (this.world != null && !this.world.isClientSide) {
            AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

            attributeinstance.removeModifier(this.babyModifier); // Paper
            if (flag) {
                attributeinstance.addModifier(this.babyModifier); // Paper
            }
        }

    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityZombie.bz.equals(datawatcherobject)) {
            this.updateSize();
        }

        super.a(datawatcherobject);
    }

    protected boolean dY() {
        return true;
    }

    @Override
    public void tick() {
        if (!this.world.isClientSide && this.isAlive()) {
            if (this.isDrownConverting()) {
                // CraftBukkit start - Use wall time instead of ticks for conversion
                int elapsedTicks = MinecraftServer.currentTick - this.lastTick;
                this.lastTick = MinecraftServer.currentTick;
                this.drownedConversionTime -= elapsedTicks;
                // CraftBukkit end
                if (this.drownedConversionTime < 0) {
                    this.ea();
                }
            } else if (this.dY()) {
                if (this.a(TagsFluid.WATER)) {
                    ++this.bF;
                    if (this.bF >= 600) {
                        this.startDrownedConversion(300);
                        this.lastTick = MinecraftServer.currentTick; // Paper - Make sure this is set at start of process - GH-1887
                    }
                } else {
                    this.bF = -1;
                }
            }
        }

        super.tick();
    }

    @Override
    public void movementTick() {
        if (this.isAlive()) {
            boolean flag = this.I_() && this.dS();

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
        }

        super.movementTick();
    }

    public void startDrownedConversion(int i) {
        this.lastTick = MinecraftServer.currentTick; // CraftBukkit
        this.drownedConversionTime = i;
        this.getDataWatcher().set(EntityZombie.DROWN_CONVERTING, true);
    }

    // Paper start
    public void stopDrowning() {
        this.drownedConversionTime = -1;
        this.getDataWatcher().set(EntityZombie.DROWN_CONVERTING, false);
    }
    // Paper end

    protected void ea() {
        this.b(EntityTypes.DROWNED);
        this.world.a((EntityHuman) null, 1040, new BlockPosition(this), 0);
    }

    protected void b(EntityTypes<? extends EntityZombie> entitytypes) {
        if (!this.dead) {
            EntityZombie entityzombie = (EntityZombie) entitytypes.a(this.world);

            entityzombie.u(this);
            entityzombie.setCanPickupLoot(this.canPickupLoot());
            entityzombie.s(entityzombie.dV() && this.ed());
            entityzombie.v(entityzombie.world.getDamageScaler(new BlockPosition(entityzombie)).d());
            entityzombie.setBaby(this.isBaby());
            entityzombie.setNoAI(this.isNoAI());
            EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
            int i = aenumitemslot.length;

            for (int j = 0; j < i; ++j) {
                EnumItemSlot enumitemslot = aenumitemslot[j];
                ItemStack itemstack = this.getEquipment(enumitemslot);

                if (!itemstack.isEmpty()) {
                    entityzombie.setSlot(enumitemslot, itemstack.cloneItemStack());
                    entityzombie.a(enumitemslot, this.d(enumitemslot));
                    itemstack.setCount(0);
                }
            }

            if (this.hasCustomName()) {
                entityzombie.setCustomName(this.getCustomName());
                entityzombie.setCustomNameVisible(this.getCustomNameVisible());
            }

            // CraftBukkit start
            if (CraftEventFactory.callEntityTransformEvent(this, entityzombie, EntityTransformEvent.TransformReason.DROWNED).isCancelled()) {
                ((Zombie) getBukkitEntity()).setConversionTime(-1); // SPIGOT-5208: End conversion to stop event spam
                return;
            }
            // CraftBukkit end
            if (!new com.destroystokyo.paper.event.entity.EntityTransformedEvent(this.getBukkitEntity(), entityzombie.getBukkitEntity(), com.destroystokyo.paper.event.entity.EntityTransformedEvent.TransformedReason.DROWNED).callEvent()) return; // Paper
            this.world.addEntity(entityzombie, CreatureSpawnEvent.SpawnReason.DROWNED); // CraftBukkit - added spawn reason
            this.die();
        }
    }

    public boolean shouldBurnInDay() { return I_(); } // Paper - OBFHELPER
    protected boolean I_() {
        return shouldBurnInDay;
    }

    // Paper start
    public void setShouldBurnInDay(boolean shouldBurnInDay) {
        this.shouldBurnInDay = shouldBurnInDay;
    }
    // Paper end

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (super.damageEntity(damagesource, f)) {
            EntityLiving entityliving = this.getGoalTarget();

            if (entityliving == null && damagesource.getEntity() instanceof EntityLiving) {
                entityliving = (EntityLiving) damagesource.getEntity();
            }

            if (entityliving != null && this.world.getDifficulty() == EnumDifficulty.HARD && (double) this.random.nextFloat() < this.getAttributeInstance(EntityZombie.d).getValue() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                int i = MathHelper.floor(this.locX);
                int j = MathHelper.floor(this.locY);
                int k = MathHelper.floor(this.locZ);
                EntityZombie entityzombie = new EntityZombie(this.world);

                for (int l = 0; l < 50; ++l) {
                    int i1 = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    int j1 = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    int k1 = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    BlockPosition blockposition = new BlockPosition(i1, j1 - 1, k1);

                    if (this.world.getType(blockposition).a((IBlockAccess) this.world, blockposition, (Entity) entityzombie) && this.world.getLightLevel(new BlockPosition(i1, j1, k1)) < 10) {
                        entityzombie.setPosition((double) i1, (double) j1, (double) k1);
                        if (!this.world.isPlayerNearby((double) i1, (double) j1, (double) k1, 7.0D) && this.world.i((Entity) entityzombie) && this.world.getCubes(entityzombie) && !this.world.containsLiquid(entityzombie.getBoundingBox())) {
                            this.world.addEntity(entityzombie, CreatureSpawnEvent.SpawnReason.REINFORCEMENTS); // CraftBukkit
                            entityzombie.setGoalTarget(entityliving, EntityTargetEvent.TargetReason.REINFORCEMENT_TARGET, true); // CraftBukkit
                            entityzombie.prepare(this.world, this.world.getDamageScaler(new BlockPosition(entityzombie)), EnumMobSpawn.REINFORCEMENT, (GroupDataEntity) null, (NBTTagCompound) null);
                            this.getAttributeInstance(EntityZombie.d).addModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806D, AttributeModifier.Operation.ADDITION));
                            entityzombie.getAttributeInstance(EntityZombie.d).addModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05000000074505806D, AttributeModifier.Operation.ADDITION));
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

    @Override
    public boolean C(Entity entity) {
        boolean flag = super.C(entity);

        if (flag) {
            float f = this.world.getDamageScaler(new BlockPosition(this)).b();

            if (this.getItemInMainHand().isEmpty() && this.isBurning() && this.random.nextFloat() < f * 0.3F) {
                // CraftBukkit start
                EntityCombustByEntityEvent event = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), 2 * (int) f); // PAIL: fixme
                this.world.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    entity.setOnFire(event.getDuration(), false);
                }
                // CraftBukkit end
            }
        }

        return flag;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_ZOMBIE_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_ZOMBIE_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_ZOMBIE_DEATH;
    }

    protected SoundEffect getSoundStep() {
        return SoundEffects.ENTITY_ZOMBIE_STEP;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(this.getSoundStep(), 0.15F, 1.0F);
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    @Override
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

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        if (this.isBaby()) {
            nbttagcompound.setBoolean("IsBaby", true);
        }

        nbttagcompound.setBoolean("CanBreakDoors", this.ed());
        nbttagcompound.setInt("InWaterTime", this.isInWater() ? this.bF : -1);
        nbttagcompound.setInt("DrownedConversionTime", this.isDrownConverting() ? this.drownedConversionTime : -1);
        nbttagcompound.setBoolean("Paper.ShouldBurnInDay", shouldBurnInDay); // Paper
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.getBoolean("IsBaby")) {
            this.setBaby(true);
        }

        this.s(nbttagcompound.getBoolean("CanBreakDoors"));
        this.bF = nbttagcompound.getInt("InWaterTime");
        if (nbttagcompound.hasKeyOfType("DrownedConversionTime", 99) && nbttagcompound.getInt("DrownedConversionTime") > -1) {
            this.startDrownedConversion(nbttagcompound.getInt("DrownedConversionTime"));
        }
        // Paper start
        if (nbttagcompound.hasKey("Paper.ShouldBurnInDay")) {
            shouldBurnInDay = nbttagcompound.getBoolean("Paper.ShouldBurnInDay");
        }
        // Paper end
    }

    @Override
    public void b(EntityLiving entityliving) {
        super.b(entityliving);
        if ((this.world.getDifficulty() == EnumDifficulty.NORMAL || this.world.getDifficulty() == EnumDifficulty.HARD) && entityliving instanceof EntityVillager) {
            if (this.world.getDifficulty() != EnumDifficulty.HARD && this.random.nextBoolean()) {
                return;
            }

            EntityVillager entityvillager = (EntityVillager) entityliving;
            EntityZombieVillager entityzombievillager = (EntityZombieVillager) EntityTypes.ZOMBIE_VILLAGER.a(this.world);

            entityzombievillager.u(entityvillager);
            // entityvillager.die(); // CraftBukkit - moved down
            entityzombievillager.prepare(this.world, this.world.getDamageScaler(new BlockPosition(entityzombievillager)), EnumMobSpawn.CONVERSION, new EntityZombie.GroupDataZombie(false), (NBTTagCompound) null);
            entityzombievillager.setVillagerData(entityvillager.getVillagerData());
            entityzombievillager.a((NBTBase) entityvillager.es().a((DynamicOps) DynamicOpsNBT.a).getValue());
            entityzombievillager.setOffers(entityvillager.getOffers().a());
            entityzombievillager.a(entityvillager.getExperience());
            entityzombievillager.setBaby(entityvillager.isBaby());
            entityzombievillager.setNoAI(entityvillager.isNoAI());
            if (entityvillager.hasCustomName()) {
                entityzombievillager.setCustomName(entityvillager.getCustomName());
                entityzombievillager.setCustomNameVisible(entityvillager.getCustomNameVisible());
            }

            // CraftBukkit start
            if (CraftEventFactory.callEntityTransformEvent(this, entityzombievillager, EntityTransformEvent.TransformReason.INFECTION).isCancelled()) {
                return;
            }
            if (!new com.destroystokyo.paper.event.entity.EntityTransformedEvent(this.getBukkitEntity(), entityvillager.getBukkitEntity(), com.destroystokyo.paper.event.entity.EntityTransformedEvent.TransformedReason.INFECTED).callEvent()) return; // Paper
            entityvillager.die(); // CraftBukkit - from above
            this.world.addEntity(entityzombievillager, CreatureSpawnEvent.SpawnReason.INFECTION); // CraftBukkit - add SpawnReason
            // CraftBukkit end
            this.world.a((EntityHuman) null, 1026, new BlockPosition(this), 0);
        }

    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return this.isBaby() ? 0.93F : 1.74F;
    }

    @Override
    protected boolean g(ItemStack itemstack) {
        return itemstack.getItem() == Items.EGG && this.isBaby() && this.isPassenger() ? false : super.g(itemstack);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        Object object = super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
        float f = difficultydamagescaler.d();

        this.setCanPickupLoot(this.random.nextFloat() < 0.55F * f);
        if (object == null) {
            object = new EntityZombie.GroupDataZombie(generatoraccess.getRandom().nextFloat() < 0.05F);
        }

        if (object instanceof EntityZombie.GroupDataZombie) {
            EntityZombie.GroupDataZombie entityzombie_groupdatazombie = (EntityZombie.GroupDataZombie) object;

            if (entityzombie_groupdatazombie.a) {
                this.setBaby(true);
                if ((double) generatoraccess.getRandom().nextFloat() < 0.05D) {
                    List<EntityChicken> list = generatoraccess.a(EntityChicken.class, this.getBoundingBox().grow(5.0D, 3.0D, 5.0D), IEntitySelector.c);

                    if (!list.isEmpty()) {
                        EntityChicken entitychicken = (EntityChicken) list.get(0);

                        entitychicken.r(true);
                        this.startRiding(entitychicken);
                    }
                } else if ((double) generatoraccess.getRandom().nextFloat() < 0.05D) {
                    EntityChicken entitychicken1 = (EntityChicken) EntityTypes.CHICKEN.a(this.world);

                    entitychicken1.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, 0.0F);
                    entitychicken1.prepare(generatoraccess, difficultydamagescaler, EnumMobSpawn.JOCKEY, (GroupDataEntity) null, (NBTTagCompound) null);
                    entitychicken1.r(true);
                    generatoraccess.addEntity(entitychicken1, CreatureSpawnEvent.SpawnReason.MOUNT); // CraftBukkit
                    this.startRiding(entitychicken1);
                }
            }

            this.s(this.dV() && this.random.nextFloat() < f * 0.1F);
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

        this.v(f);
        return (GroupDataEntity) object;
    }

    protected void v(float f) {
        this.getAttributeInstance(GenericAttributes.KNOCKBACK_RESISTANCE).addModifier(new AttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05000000074505806D, AttributeModifier.Operation.ADDITION));
        double d0 = this.random.nextDouble() * 1.5D * (double) f;

        if (d0 > 1.0D) {
            this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).addModifier(new AttributeModifier("Random zombie-spawn bonus", d0, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }

        if (this.random.nextFloat() < f * 0.05F) {
            this.getAttributeInstance(EntityZombie.d).addModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25D + 0.5D, AttributeModifier.Operation.ADDITION));
            this.getAttributeInstance(GenericAttributes.MAX_HEALTH).addModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0D + 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
            this.s(this.dV());
        }

    }

    @Override
    public double aO() {
        return this.isBaby() ? 0.0D : -0.45D;
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropDeathLoot(damagesource, i, flag);
        Entity entity = damagesource.getEntity();

        if (entity instanceof EntityCreeper) {
            EntityCreeper entitycreeper = (EntityCreeper) entity;

            if (entitycreeper.canCauseHeadDrop()) {
                entitycreeper.setCausedHeadDrop();
                ItemStack itemstack = this.dX();

                if (!itemstack.isEmpty()) {
                    this.a(itemstack);
                }
            }
        }

    }

    protected ItemStack dX() {
        return new ItemStack(Items.ZOMBIE_HEAD);
    }

    class a extends PathfinderGoalRemoveBlock {

        a(EntityCreature entitycreature, double d0, int i) {
            super(Blocks.TURTLE_EGG, entitycreature, d0, i);
        }

        @Override
        public void a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
            generatoraccess.playSound((EntityHuman) null, blockposition, SoundEffects.ENTITY_ZOMBIE_DESTROY_EGG, SoundCategory.HOSTILE, 0.5F, 0.9F + EntityZombie.this.random.nextFloat() * 0.2F);
        }

        @Override
        public void a(World world, BlockPosition blockposition) {
            world.playSound((EntityHuman) null, blockposition, SoundEffects.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
        }

        @Override
        public double h() {
            return 1.14D;
        }
    }

    public class GroupDataZombie implements GroupDataEntity {

        public final boolean a;

        private GroupDataZombie(boolean flag) {
            this.a = flag;
        }
    }
}
