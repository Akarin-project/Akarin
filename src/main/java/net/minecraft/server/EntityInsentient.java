package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.EntityUnleashEvent.UnleashReason;
// CraftBukkit end

public abstract class EntityInsentient extends EntityLiving {

    private static final DataWatcherObject<Byte> b = DataWatcher.a(EntityInsentient.class, DataWatcherRegistry.a);
    public int e;
    protected int f;
    protected ControllerLook lookController;
    protected ControllerMove moveController;
    protected ControllerJump bt;
    private final EntityAIBodyControl c;
    protected NavigationAbstract navigation;
    public PathfinderGoalSelector goalSelector;
    @Nullable public PathfinderGoalFloat goalFloat; // Paper
    public PathfinderGoalSelector targetSelector;
    private EntityLiving goalTarget;
    private final EntitySenses bz;
    private final NonNullList<ItemStack> bA;
    public final float[] dropChanceHand;
    private final NonNullList<ItemStack> bB;
    public final float[] dropChanceArmor;
    // private boolean canPickUpLoot; // CraftBukkit - moved up to EntityLiving
    public boolean persistent;
    private final Map<PathType, Float> bE;
    public MinecraftKey lootTableKey;
    public long lootTableSeed;
    @Nullable
    private Entity leashHolder;
    private int bI;
    @Nullable
    private NBTTagCompound bJ;
    private BlockPosition bK;
    private float bL;

    protected EntityInsentient(EntityTypes<? extends EntityInsentient> entitytypes, World world) {
        super(entitytypes, world);
        this.bA = NonNullList.a(2, ItemStack.a);
        this.dropChanceHand = new float[2];
        this.bB = NonNullList.a(4, ItemStack.a);
        this.dropChanceArmor = new float[4];
        this.bE = Maps.newEnumMap(PathType.class);
        this.bK = BlockPosition.ZERO;
        this.bL = -1.0F;
        this.goalSelector = new PathfinderGoalSelector(world != null && world.getMethodProfiler() != null ? world.getMethodProfiler() : null);
        this.targetSelector = new PathfinderGoalSelector(world != null && world.getMethodProfiler() != null ? world.getMethodProfiler() : null);
        this.lookController = new ControllerLook(this);
        this.moveController = new ControllerMove(this);
        this.bt = new ControllerJump(this);
        this.c = this.o();
        this.navigation = this.b(world);
        this.bz = new EntitySenses(this);
        Arrays.fill(this.dropChanceArmor, 0.085F);
        Arrays.fill(this.dropChanceHand, 0.085F);
        if (world != null && !world.isClientSide) {
            this.initPathfinder();
        }

        // CraftBukkit start - default persistance to type's persistance value
        this.persistent = !isTypeNotPersistent(0);
        // CraftBukkit end
    }

    protected void initPathfinder() {}

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeMap().b(GenericAttributes.FOLLOW_RANGE).setValue(16.0D);
        this.getAttributeMap().b(GenericAttributes.ATTACK_KNOCKBACK);
    }

    protected NavigationAbstract b(World world) {
        return new Navigation(this, world);
    }

    public float a(PathType pathtype) {
        Float ofloat = (Float) this.bE.get(pathtype);

        return ofloat == null ? pathtype.a() : ofloat;
    }

    public void a(PathType pathtype, float f) {
        this.bE.put(pathtype, f);
    }

    protected EntityAIBodyControl o() {
        return new EntityAIBodyControl(this);
    }

    public ControllerLook getControllerLook() {
        return this.lookController;
    }

    public ControllerMove getControllerMove() {
        if (this.isPassenger() && this.getVehicle() instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient) this.getVehicle();

            return entityinsentient.getControllerMove();
        } else {
            return this.moveController;
        }
    }

    public ControllerJump getControllerJump() {
        return this.bt;
    }

    public NavigationAbstract getNavigation() {
        if (this.isPassenger() && this.getVehicle() instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient) this.getVehicle();

            return entityinsentient.getNavigation();
        } else {
            return this.navigation;
        }
    }

    public EntitySenses getEntitySenses() {
        return this.bz;
    }

    @Nullable
    public EntityLiving getGoalTarget() {
        return this.goalTarget;
    }

    public org.bukkit.craftbukkit.entity.CraftMob getBukkitMob() { return (org.bukkit.craftbukkit.entity.CraftMob) super.getBukkitEntity(); } // Paper
    public void setGoalTarget(@Nullable EntityLiving entityliving) {
        // CraftBukkit start - fire event
        setGoalTarget(entityliving, EntityTargetEvent.TargetReason.UNKNOWN, true);
    }

    public boolean setGoalTarget(EntityLiving entityliving, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        if (getGoalTarget() == entityliving) return false;
        if (fireEvent) {
            if (reason == EntityTargetEvent.TargetReason.UNKNOWN && getGoalTarget() != null && entityliving == null) {
                reason = getGoalTarget().isAlive() ? EntityTargetEvent.TargetReason.FORGOT_TARGET : EntityTargetEvent.TargetReason.TARGET_DIED;
            }
            if (reason == EntityTargetEvent.TargetReason.UNKNOWN) {
                world.getServer().getLogger().log(java.util.logging.Level.WARNING, "Unknown target reason, please report on the issue tracker", new Exception());
            }
            CraftLivingEntity ctarget = null;
            if (entityliving != null) {
                ctarget = (CraftLivingEntity) entityliving.getBukkitEntity();
            }
            EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(this.getBukkitEntity(), ctarget, reason);
            world.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }

            if (event.getTarget() != null) {
                entityliving = ((CraftLivingEntity) event.getTarget()).getHandle();
            } else {
                entityliving = null;
            }
        }
        this.goalTarget = entityliving;
        return true;
        // CraftBukkit end
    }

    @Override
    public boolean a(EntityTypes<?> entitytypes) {
        return entitytypes != EntityTypes.GHAST;
    }

    public void blockEaten() {}

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityInsentient.b, (byte) 0);
    }

    public int A() {
        return 80;
    }

    public void B() {
        SoundEffect soundeffect = this.getSoundAmbient();

        if (soundeffect != null) {
            this.a(soundeffect, this.getSoundVolume(), this.cV());
        }

    }

    @Override
    public void entityBaseTick() {
        super.entityBaseTick();
        this.world.getMethodProfiler().enter("mobBaseTick");
        if (this.isAlive() && this.random.nextInt(1000) < this.e++) {
            this.l();
            this.B();
        }

        this.world.getMethodProfiler().exit();
    }

    @Override
    protected void c(DamageSource damagesource) {
        this.l();
        super.c(damagesource);
    }

    private void l() {
        this.e = -this.A();
    }

    @Override
    protected int getExpValue(EntityHuman entityhuman) {
        if (this.f > 0) {
            int i = this.f;

            int j;

            for (j = 0; j < this.bB.size(); ++j) {
                if (!((ItemStack) this.bB.get(j)).isEmpty() && this.dropChanceArmor[j] <= 1.0F) {
                    i += 1 + this.random.nextInt(3);
                }
            }

            for (j = 0; j < this.bA.size(); ++j) {
                if (!((ItemStack) this.bA.get(j)).isEmpty() && this.dropChanceHand[j] <= 1.0F) {
                    i += 1 + this.random.nextInt(3);
                }
            }

            return i;
        } else {
            return this.f;
        }
    }

    public void doSpawnEffect() {
        if (this.world.isClientSide) {
            for (int i = 0; i < 20; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                double d3 = 10.0D;

                this.world.addParticle(Particles.POOF, this.locX + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth() - d0 * 10.0D, this.locY + (double) (this.random.nextFloat() * this.getHeight()) - d1 * 10.0D, this.locZ + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth() - d2 * 10.0D, d0, d1, d2);
            }
        } else {
            this.world.broadcastEntityEffect(this, (byte) 20);
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClientSide) {
            this.dM();
            if (this.ticksLived % 5 == 0) {
                this.F();
            }
        }

    }

    protected void F() {
        boolean flag = !(this.getRidingPassenger() instanceof EntityInsentient);
        boolean flag1 = !(this.getVehicle() instanceof EntityBoat);

        this.goalSelector.a(PathfinderGoal.Type.MOVE, flag);
        this.goalSelector.a(PathfinderGoal.Type.JUMP, flag && flag1);
        this.goalSelector.a(PathfinderGoal.Type.LOOK, flag);
    }

    @Override
    protected float e(float f, float f1) {
        this.c.a();
        return f1;
    }

    @Nullable
    protected SoundEffect getSoundAmbient() {
        return null;
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("CanPickUpLoot", this.canPickupLoot());
        nbttagcompound.setBoolean("PersistenceRequired", this.persistent);
        NBTTagList nbttaglist = new NBTTagList();

        NBTTagCompound nbttagcompound1;

        for (Iterator iterator = this.bB.iterator(); iterator.hasNext(); nbttaglist.add(nbttagcompound1)) {
            ItemStack itemstack = (ItemStack) iterator.next();

            nbttagcompound1 = new NBTTagCompound();
            if (!itemstack.isEmpty()) {
                itemstack.save(nbttagcompound1);
            }
        }

        nbttagcompound.set("ArmorItems", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();

        NBTTagCompound nbttagcompound2;

        for (Iterator iterator1 = this.bA.iterator(); iterator1.hasNext(); nbttaglist1.add(nbttagcompound2)) {
            ItemStack itemstack1 = (ItemStack) iterator1.next();

            nbttagcompound2 = new NBTTagCompound();
            if (!itemstack1.isEmpty()) {
                itemstack1.save(nbttagcompound2);
            }
        }

        nbttagcompound.set("HandItems", nbttaglist1);
        NBTTagList nbttaglist2 = new NBTTagList();
        float[] afloat = this.dropChanceArmor;
        int i = afloat.length;

        int j;

        for (j = 0; j < i; ++j) {
            float f = afloat[j];

            nbttaglist2.add(new NBTTagFloat(f));
        }

        nbttagcompound.set("ArmorDropChances", nbttaglist2);
        NBTTagList nbttaglist3 = new NBTTagList();
        float[] afloat1 = this.dropChanceHand;

        j = afloat1.length;

        for (int k = 0; k < j; ++k) {
            float f1 = afloat1[k];

            nbttaglist3.add(new NBTTagFloat(f1));
        }

        nbttagcompound.set("HandDropChances", nbttaglist3);
        if (this.leashHolder != null) {
            nbttagcompound2 = new NBTTagCompound();
            if (this.leashHolder instanceof EntityLiving) {
                UUID uuid = this.leashHolder.getUniqueID();

                nbttagcompound2.a("UUID", uuid);
            } else if (this.leashHolder instanceof EntityHanging) {
                BlockPosition blockposition = ((EntityHanging) this.leashHolder).getBlockPosition();

                nbttagcompound2.setInt("X", blockposition.getX());
                nbttagcompound2.setInt("Y", blockposition.getY());
                nbttagcompound2.setInt("Z", blockposition.getZ());
            }

            nbttagcompound.set("Leash", nbttagcompound2);
        }

        nbttagcompound.setBoolean("LeftHanded", this.isLeftHanded());
        if (this.lootTableKey != null) {
            nbttagcompound.setString("DeathLootTable", this.lootTableKey.toString());
            if (this.lootTableSeed != 0L) {
                nbttagcompound.setLong("DeathLootTableSeed", this.lootTableSeed);
            }
        }

        if (this.isNoAI()) {
            nbttagcompound.setBoolean("NoAI", this.isNoAI());
        }

    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);

        // CraftBukkit start - If looting or persistence is false only use it if it was set after we started using it
        if (nbttagcompound.hasKeyOfType("CanPickUpLoot", 1)) {
            boolean data = nbttagcompound.getBoolean("CanPickUpLoot");
            if (isLevelAtLeast(nbttagcompound, 1) || data) {
                this.setCanPickupLoot(data);
            }
        }

        boolean data = nbttagcompound.getBoolean("PersistenceRequired");
        if (isLevelAtLeast(nbttagcompound, 1) || data) {
            this.persistent = data;
        }
        // CraftBukkit end
        NBTTagList nbttaglist;
        int i;

        if (nbttagcompound.hasKeyOfType("ArmorItems", 9)) {
            nbttaglist = nbttagcompound.getList("ArmorItems", 10);

            for (i = 0; i < this.bB.size(); ++i) {
                this.bB.set(i, ItemStack.a(nbttaglist.getCompound(i)));
            }
        }

        if (nbttagcompound.hasKeyOfType("HandItems", 9)) {
            nbttaglist = nbttagcompound.getList("HandItems", 10);

            for (i = 0; i < this.bA.size(); ++i) {
                this.bA.set(i, ItemStack.a(nbttaglist.getCompound(i)));
            }
        }

        if (nbttagcompound.hasKeyOfType("ArmorDropChances", 9)) {
            nbttaglist = nbttagcompound.getList("ArmorDropChances", 5);

            for (i = 0; i < nbttaglist.size(); ++i) {
                this.dropChanceArmor[i] = nbttaglist.i(i);
            }
        }

        if (nbttagcompound.hasKeyOfType("HandDropChances", 9)) {
            nbttaglist = nbttagcompound.getList("HandDropChances", 5);

            for (i = 0; i < nbttaglist.size(); ++i) {
                this.dropChanceHand[i] = nbttaglist.i(i);
            }
        }

        if (nbttagcompound.hasKeyOfType("Leash", 10)) {
            this.bJ = nbttagcompound.getCompound("Leash");
        }

        this.p(nbttagcompound.getBoolean("LeftHanded"));
        if (nbttagcompound.hasKeyOfType("DeathLootTable", 8)) {
            this.lootTableKey = new MinecraftKey(nbttagcompound.getString("DeathLootTable"));
            this.lootTableSeed = nbttagcompound.getLong("DeathLootTableSeed");
        }

        this.setNoAI(nbttagcompound.getBoolean("NoAI"));
    }

    @Override
    protected void a(DamageSource damagesource, boolean flag) {
        super.a(damagesource, flag);
        this.lootTableKey = null;
    }
    // CraftBukkit - start
    public MinecraftKey getLootTable() {
        return getDefaultLootTable();
    }
    // CraftBukkit - end

    @Override
    protected LootTableInfo.Builder a(boolean flag, DamageSource damagesource) {
        return super.a(flag, damagesource).a(this.lootTableSeed, this.random);
    }

    @Override
    public final MinecraftKey cG() {
        return this.lootTableKey == null ? this.getDefaultLootTable() : this.lootTableKey;
    }

    protected MinecraftKey getDefaultLootTable() {
        return super.cG();
    }

    public void r(float f) {
        this.bd = f;
    }

    public void s(float f) {
        this.bc = f;
    }

    public void t(float f) {
        this.bb = f;
    }

    @Override
    public void o(float f) {
        super.o(f);
        this.r(f);
    }

    @Override
    public void movementTick() {
        super.movementTick();
        this.world.getMethodProfiler().enter("looting");
        if (!this.world.isClientSide && this.canPickupLoot() && this.isAlive() && !this.killed && this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
            List<EntityItem> list = this.world.a(EntityItem.class, this.getBoundingBox().grow(1.0D, 0.0D, 1.0D));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityItem entityitem = (EntityItem) iterator.next();

                if (!entityitem.dead && !entityitem.getItemStack().isEmpty() && !entityitem.q()) {
                    // Paper Start
                    if (!entityitem.canMobPickup) {
                        continue;
                    }
                    // Paper End
                    this.a(entityitem);
                }
            }
        }

        this.world.getMethodProfiler().exit();
    }

    protected void a(EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItemStack();
        EnumItemSlot enumitemslot = h(itemstack);
        ItemStack itemstack1 = this.getEquipment(enumitemslot);
        boolean flag = this.a(itemstack, itemstack1, enumitemslot);

        // CraftBukkit start
        boolean canPickup = flag && this.g(itemstack);
        canPickup = !org.bukkit.craftbukkit.event.CraftEventFactory.callEntityPickupItemEvent(this, entityitem, 0, !canPickup).isCancelled();
        if (canPickup) {
            // CraftBukkit end
            double d0 = (double) this.d(enumitemslot);

            if (!itemstack1.isEmpty() && (double) (this.random.nextFloat() - 0.1F) < d0) {
                this.forceDrops = true; // CraftBukkit
                this.a(itemstack1);
                this.forceDrops = false; // CraftBukkit
            }

            this.setSlot(enumitemslot, itemstack);
            switch (enumitemslot.a()) {
                case HAND:
                    this.dropChanceHand[enumitemslot.b()] = 2.0F;
                    break;
                case ARMOR:
                    this.dropChanceArmor[enumitemslot.b()] = 2.0F;
            }

            this.persistent = true;
            this.receive(entityitem, itemstack.getCount());
            entityitem.die();
        }

    }

    protected boolean a(ItemStack itemstack, ItemStack itemstack1, EnumItemSlot enumitemslot) {
        boolean flag = true;

        if (!itemstack1.isEmpty()) {
            if (enumitemslot.a() == EnumItemSlot.Function.HAND) {
                if (itemstack.getItem() instanceof ItemSword && !(itemstack1.getItem() instanceof ItemSword)) {
                    flag = true;
                } else if (itemstack.getItem() instanceof ItemSword && itemstack1.getItem() instanceof ItemSword) {
                    ItemSword itemsword = (ItemSword) itemstack.getItem();
                    ItemSword itemsword1 = (ItemSword) itemstack1.getItem();

                    if (itemsword.d() == itemsword1.d()) {
                        flag = itemstack.getDamage() < itemstack1.getDamage() || itemstack.hasTag() && !itemstack1.hasTag();
                    } else {
                        flag = itemsword.d() > itemsword1.d();
                    }
                } else if (itemstack.getItem() instanceof ItemBow && itemstack1.getItem() instanceof ItemBow) {
                    flag = itemstack.hasTag() && !itemstack1.hasTag();
                } else {
                    flag = false;
                }
            } else if (itemstack.getItem() instanceof ItemArmor && !(itemstack1.getItem() instanceof ItemArmor)) {
                flag = true;
            } else if (itemstack.getItem() instanceof ItemArmor && itemstack1.getItem() instanceof ItemArmor && !EnchantmentManager.d(itemstack1)) {
                ItemArmor itemarmor = (ItemArmor) itemstack.getItem();
                ItemArmor itemarmor1 = (ItemArmor) itemstack1.getItem();

                if (itemarmor.e() == itemarmor1.e()) {
                    flag = itemstack.getDamage() < itemstack1.getDamage() || itemstack.hasTag() && !itemstack1.hasTag();
                } else {
                    flag = itemarmor.e() > itemarmor1.e();
                }
            } else {
                flag = false;
            }
        }

        return flag;
    }

    protected boolean g(ItemStack itemstack) {
        return true;
    }

    public boolean isTypeNotPersistent(double d0) {
        return true;
    }

    public boolean I() {
        return false;
    }

    protected void checkDespawn() {
        if (!this.isPersistent() && !this.I()) {
            EntityHuman entityhuman = this.world.findNearbyPlayer(this, -1.0D);

            if (entityhuman != null && entityhuman.affectsSpawning) { // Paper - Affects Spawning API
                double d0 = entityhuman.h(this);

                if (d0 > world.paperConfig.hardDespawnDistance) { // CraftBukkit - remove isTypeNotPersistent() check // Paper - custom despawn distances
                    this.die();
                }

                if (this.ticksFarFromPlayer > 600 && this.random.nextInt(800) == 0 && d0 > world.paperConfig.softDespawnDistance) { // CraftBukkit - remove isTypeNotPersistent() check // Paper - custom despawn distances
                    this.die();
                } else if (d0 < 1024.0D) {
                    this.ticksFarFromPlayer = 0;
                }
            }

        } else {
            this.ticksFarFromPlayer = 0;
        }
    }

    @Override
    protected final void doTick() {
        ++this.ticksFarFromPlayer;
        this.world.getMethodProfiler().enter("checkDespawn");
        this.checkDespawn();
        this.world.getMethodProfiler().exit();
        // Spigot Start
        if ( this.fromMobSpawner )
        {
            // Paper start - Allow nerfed mobs to jump and float
            if (goalFloat != null) {
                if (goalFloat.validConditions()) goalFloat.update();
                this.getControllerJump().jumpIfSet();
            }
            // Paper end
            return;
        }
        // Spigot End
        this.world.getMethodProfiler().enter("sensing");
        this.bz.a();
        this.world.getMethodProfiler().exit();
        this.world.getMethodProfiler().enter("targetSelector");
        this.targetSelector.doTick();
        this.world.getMethodProfiler().exit();
        this.world.getMethodProfiler().enter("goalSelector");
        this.goalSelector.doTick();
        this.world.getMethodProfiler().exit();
        this.world.getMethodProfiler().enter("navigation");
        this.navigation.c();
        this.world.getMethodProfiler().exit();
        this.world.getMethodProfiler().enter("mob tick");
        this.mobTick();
        this.world.getMethodProfiler().exit();
        this.world.getMethodProfiler().enter("controls");
        this.world.getMethodProfiler().enter("move");
        this.moveController.a();
        this.world.getMethodProfiler().exitEnter("look");
        this.lookController.a();
        this.world.getMethodProfiler().exitEnter("jump");
        this.bt.b();
        this.world.getMethodProfiler().exit();
        this.world.getMethodProfiler().exit();
        this.K();
    }

    protected void K() {
        PacketDebug.a(this.world, this, this.goalSelector);
    }

    protected void mobTick() {}

    public int M() {
        return 40;
    }

    public int dA() {
        return 75;
    }

    public int dB() {
        return 10;
    }

    public void a(Entity entity, float f, float f1) {
        double d0 = entity.locX - this.locX;
        double d1 = entity.locZ - this.locZ;
        double d2;

        if (entity instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) entity;

            d2 = entityliving.locY + (double) entityliving.getHeadHeight() - (this.locY + (double) this.getHeadHeight());
        } else {
            d2 = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0D - (this.locY + (double) this.getHeadHeight());
        }

        double d3 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1);
        float f2 = (float) (MathHelper.d(d1, d0) * 57.2957763671875D) - 90.0F;
        float f3 = (float) (-(MathHelper.d(d2, d3) * 57.2957763671875D));

        this.pitch = this.a(this.pitch, f3, f1);
        this.yaw = this.a(this.yaw, f2, f);
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

    public static boolean a(EntityTypes<? extends EntityInsentient> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        BlockPosition blockposition1 = blockposition.down();

        return enummobspawn == EnumMobSpawn.SPAWNER || generatoraccess.getType(blockposition1).a((IBlockAccess) generatoraccess, blockposition1, entitytypes);
    }

    public boolean a(GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn) {
        return true;
    }

    public boolean a(IWorldReader iworldreader) {
        return !iworldreader.containsLiquid(this.getBoundingBox()) && iworldreader.i(this);
    }

    public int dC() {
        return 4;
    }

    public boolean c(int i) {
        return false;
    }

    @Override
    public int bv() {
        if (this.getGoalTarget() == null) {
            return 3;
        } else {
            int i = (int) (this.getHealth() - this.getMaxHealth() * 0.33F);

            i -= (3 - this.world.getDifficulty().a()) * 4;
            if (i < 0) {
                i = 0;
            }

            return i + 3;
        }
    }

    @Override
    public Iterable<ItemStack> aZ() {
        return this.bA;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.bB;
    }

    @Override
    public ItemStack getEquipment(EnumItemSlot enumitemslot) {
        switch (enumitemslot.a()) {
            case HAND:
                return (ItemStack) this.bA.get(enumitemslot.b());
            case ARMOR:
                return (ItemStack) this.bB.get(enumitemslot.b());
            default:
                return ItemStack.a;
        }
    }

    @Override
    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        switch (enumitemslot.a()) {
            case HAND:
                this.bA.set(enumitemslot.b(), itemstack);
                break;
            case ARMOR:
                this.bB.set(enumitemslot.b(), itemstack);
        }

    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropDeathLoot(damagesource, i, flag);
        EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
        int j = aenumitemslot.length;

        for (int k = 0; k < j; ++k) {
            EnumItemSlot enumitemslot = aenumitemslot[k];
            ItemStack itemstack = this.getEquipment(enumitemslot);
            float f = this.d(enumitemslot);
            boolean flag1 = f > 1.0F;

            if (!itemstack.isEmpty() && !EnchantmentManager.shouldNotDrop(itemstack) && (flag || flag1) && this.random.nextFloat() - (float) i * 0.01F < f) {
                if (!flag1 && itemstack.e()) {
                    itemstack.setDamage(itemstack.h() - this.random.nextInt(1 + this.random.nextInt(Math.max(itemstack.h() - 3, 1))));
                }

                this.a(itemstack);
            }
        }

    }

    protected float d(EnumItemSlot enumitemslot) {
        float f;

        switch (enumitemslot.a()) {
            case HAND:
                f = this.dropChanceHand[enumitemslot.b()];
                break;
            case ARMOR:
                f = this.dropChanceArmor[enumitemslot.b()];
                break;
            default:
                f = 0.0F;
        }

        return f;
    }

    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        if (this.random.nextFloat() < 0.15F * difficultydamagescaler.d()) {
            int i = this.random.nextInt(2);
            float f = this.world.getDifficulty() == EnumDifficulty.HARD ? 0.1F : 0.25F;

            if (this.random.nextFloat() < 0.095F) {
                ++i;
            }

            if (this.random.nextFloat() < 0.095F) {
                ++i;
            }

            if (this.random.nextFloat() < 0.095F) {
                ++i;
            }

            boolean flag = true;
            EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
            int j = aenumitemslot.length;

            for (int k = 0; k < j; ++k) {
                EnumItemSlot enumitemslot = aenumitemslot[k];

                if (enumitemslot.a() == EnumItemSlot.Function.ARMOR) {
                    ItemStack itemstack = this.getEquipment(enumitemslot);

                    if (!flag && this.random.nextFloat() < f) {
                        break;
                    }

                    flag = false;
                    if (itemstack.isEmpty()) {
                        Item item = a(enumitemslot, i);

                        if (item != null) {
                            this.setSlot(enumitemslot, new ItemStack(item));
                        }
                    }
                }
            }
        }

    }

    public static EnumItemSlot h(ItemStack itemstack) {
        Item item = itemstack.getItem();

        return item != Blocks.CARVED_PUMPKIN.getItem() && (!(item instanceof ItemBlock) || !(((ItemBlock) item).getBlock() instanceof BlockSkullAbstract)) ? (item instanceof ItemArmor ? ((ItemArmor) item).b() : (item == Items.ELYTRA ? EnumItemSlot.CHEST : (item == Items.SHIELD ? EnumItemSlot.OFFHAND : EnumItemSlot.MAINHAND))) : EnumItemSlot.HEAD;
    }

    @Nullable
    public static Item a(EnumItemSlot enumitemslot, int i) {
        switch (enumitemslot) {
            case HEAD:
                if (i == 0) {
                    return Items.LEATHER_HELMET;
                } else if (i == 1) {
                    return Items.GOLDEN_HELMET;
                } else if (i == 2) {
                    return Items.CHAINMAIL_HELMET;
                } else if (i == 3) {
                    return Items.IRON_HELMET;
                } else if (i == 4) {
                    return Items.DIAMOND_HELMET;
                }
            case CHEST:
                if (i == 0) {
                    return Items.LEATHER_CHESTPLATE;
                } else if (i == 1) {
                    return Items.GOLDEN_CHESTPLATE;
                } else if (i == 2) {
                    return Items.CHAINMAIL_CHESTPLATE;
                } else if (i == 3) {
                    return Items.IRON_CHESTPLATE;
                } else if (i == 4) {
                    return Items.DIAMOND_CHESTPLATE;
                }
            case LEGS:
                if (i == 0) {
                    return Items.LEATHER_LEGGINGS;
                } else if (i == 1) {
                    return Items.GOLDEN_LEGGINGS;
                } else if (i == 2) {
                    return Items.CHAINMAIL_LEGGINGS;
                } else if (i == 3) {
                    return Items.IRON_LEGGINGS;
                } else if (i == 4) {
                    return Items.DIAMOND_LEGGINGS;
                }
            case FEET:
                if (i == 0) {
                    return Items.LEATHER_BOOTS;
                } else if (i == 1) {
                    return Items.GOLDEN_BOOTS;
                } else if (i == 2) {
                    return Items.CHAINMAIL_BOOTS;
                } else if (i == 3) {
                    return Items.IRON_BOOTS;
                } else if (i == 4) {
                    return Items.DIAMOND_BOOTS;
                }
            default:
                return null;
        }
    }

    protected void b(DifficultyDamageScaler difficultydamagescaler) {
        float f = difficultydamagescaler.d();

        if (!this.getItemInMainHand().isEmpty() && this.random.nextFloat() < 0.25F * f) {
            this.setSlot(EnumItemSlot.MAINHAND, EnchantmentManager.a(this.random, this.getItemInMainHand(), (int) (5.0F + f * (float) this.random.nextInt(18)), false));
        }

        EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
        int i = aenumitemslot.length;

        for (int j = 0; j < i; ++j) {
            EnumItemSlot enumitemslot = aenumitemslot[j];

            if (enumitemslot.a() == EnumItemSlot.Function.ARMOR) {
                ItemStack itemstack = this.getEquipment(enumitemslot);

                if (!itemstack.isEmpty() && this.random.nextFloat() < 0.5F * f) {
                    this.setSlot(enumitemslot, EnchantmentManager.a(this.random, itemstack, (int) (5.0F + f * (float) this.random.nextInt(18)), false));
                }
            }
        }

    }

    @Nullable
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).addModifier(new AttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05D, AttributeModifier.Operation.MULTIPLY_BASE));
        if (this.random.nextFloat() < 0.05F) {
            this.p(true);
        } else {
            this.p(false);
        }

        return groupdataentity;
    }

    public boolean dD() {
        return false;
    }

    public void setPersistent() {
        this.persistent = true;
    }

    public void a(EnumItemSlot enumitemslot, float f) {
        switch (enumitemslot.a()) {
            case HAND:
                this.dropChanceHand[enumitemslot.b()] = f;
                break;
            case ARMOR:
                this.dropChanceArmor[enumitemslot.b()] = f;
        }

    }

    public boolean canPickupLoot() {
        return this.canPickUpLoot;
    }

    public void setCanPickupLoot(boolean flag) {
        this.canPickUpLoot = flag;
    }

    @Override
    public boolean e(ItemStack itemstack) {
        EnumItemSlot enumitemslot = h(itemstack);

        return this.getEquipment(enumitemslot).isEmpty() && this.canPickupLoot();
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    @Override
    public final boolean b(EntityHuman entityhuman, EnumHand enumhand) {
        if (!this.isAlive()) {
            return false;
        } else if (this.getLeashHolder() == entityhuman) {
            // CraftBukkit start - fire PlayerUnleashEntityEvent
            if (CraftEventFactory.callPlayerUnleashEntityEvent(this, entityhuman).isCancelled()) {
                ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutAttachEntity(this, this.getLeashHolder()));
                return false;
            }
            // CraftBukkit end
            this.unleash(true, !entityhuman.abilities.canInstantlyBuild);
            return true;
        } else {
            ItemStack itemstack = entityhuman.b(enumhand);

            if (itemstack.getItem() == Items.LEAD && this.a(entityhuman)) {
                // CraftBukkit start - fire PlayerLeashEntityEvent
                if (CraftEventFactory.callPlayerLeashEntityEvent(this, entityhuman, entityhuman).isCancelled()) {
                    ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutAttachEntity(this, this.getLeashHolder()));
                    return false;
                }
                // CraftBukkit end
                this.setLeashHolder(entityhuman, true);
                itemstack.subtract(1);
                return true;
            } else {
                return this.a(entityhuman, enumhand) ? true : super.b(entityhuman, enumhand);
            }
        }
    }

    protected boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        return false;
    }

    public boolean dH() {
        return this.a(new BlockPosition(this));
    }

    public boolean a(BlockPosition blockposition) {
        return this.bL == -1.0F ? true : this.bK.m(blockposition) < (double) (this.bL * this.bL);
    }

    public void a(BlockPosition blockposition, int i) {
        this.bK = blockposition;
        this.bL = (float) i;
    }

    public BlockPosition dI() {
        return this.bK;
    }

    public float dJ() {
        return this.bL;
    }

    public boolean dL() {
        return this.bL != -1.0F;
    }

    protected void dM() {
        if (this.bJ != null) {
            this.dT();
        }

        if (this.leashHolder != null) {
            if (!this.isAlive() || !this.leashHolder.isAlive()) {
                this.world.getServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), (!this.isAlive()) ? UnleashReason.PLAYER_UNLEASH : UnleashReason.HOLDER_GONE)); // CraftBukkit
                this.unleash(true, true);
            }

        }
    }

    public void unleash(boolean flag, boolean flag1) {
        if (this.leashHolder != null) {
            this.attachedToPlayer = false;
            if (!(this.leashHolder instanceof EntityHuman)) {
                this.leashHolder.attachedToPlayer = false;
            }

            this.leashHolder = null;
            if (!this.world.isClientSide && flag1) {
                this.forceDrops = true; // CraftBukkit
                this.a((IMaterial) Items.LEAD);
                this.forceDrops = false; // CraftBukkit
            }

            if (!this.world.isClientSide && flag && this.world instanceof WorldServer) {
                ((WorldServer) this.world).getChunkProvider().broadcast(this, new PacketPlayOutAttachEntity(this, (Entity) null));
            }
        }

    }

    public boolean a(EntityHuman entityhuman) {
        return !this.isLeashed() && !(this instanceof IMonster);
    }

    public boolean isLeashed() {
        return this.leashHolder != null;
    }

    @Nullable
    public Entity getLeashHolder() {
        if (this.leashHolder == null && this.bI != 0 && this.world.isClientSide) {
            this.leashHolder = this.world.getEntity(this.bI);
        }

        return this.leashHolder;
    }

    public void setLeashHolder(Entity entity, boolean flag) {
        this.leashHolder = entity;
        this.attachedToPlayer = true;
        if (!(this.leashHolder instanceof EntityHuman)) {
            this.leashHolder.attachedToPlayer = true;
        }

        if (!this.world.isClientSide && flag && this.world instanceof WorldServer) {
            ((WorldServer) this.world).getChunkProvider().broadcast(this, new PacketPlayOutAttachEntity(this, this.leashHolder));
        }

        if (this.isPassenger()) {
            this.stopRiding();
        }

    }

    @Override
    public boolean a(Entity entity, boolean flag) {
        boolean flag1 = super.a(entity, flag);

        if (flag1 && this.isLeashed()) {
            this.unleash(true, true);
        }

        return flag1;
    }

    private void dT() {
        if (this.bJ != null && this.world instanceof WorldServer) {
            if (this.bJ.b("UUID")) {
                UUID uuid = this.bJ.a("UUID");
                Entity entity = ((WorldServer) this.world).getEntity(uuid);

                if (entity != null) {
                    this.setLeashHolder(entity, true);
                }
            } else if (this.bJ.hasKeyOfType("X", 99) && this.bJ.hasKeyOfType("Y", 99) && this.bJ.hasKeyOfType("Z", 99)) {
                BlockPosition blockposition = new BlockPosition(this.bJ.getInt("X"), this.bJ.getInt("Y"), this.bJ.getInt("Z"));

                this.setLeashHolder(EntityLeash.a(this.world, blockposition), true);
            } else {
                this.world.getServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), UnleashReason.UNKNOWN)); // CraftBukkit
                this.unleash(false, true);
            }

            this.bJ = null;
        }

    }

    @Override
    public boolean a_(int i, ItemStack itemstack) {
        EnumItemSlot enumitemslot;

        if (i == 98) {
            enumitemslot = EnumItemSlot.MAINHAND;
        } else if (i == 99) {
            enumitemslot = EnumItemSlot.OFFHAND;
        } else if (i == 100 + EnumItemSlot.HEAD.b()) {
            enumitemslot = EnumItemSlot.HEAD;
        } else if (i == 100 + EnumItemSlot.CHEST.b()) {
            enumitemslot = EnumItemSlot.CHEST;
        } else if (i == 100 + EnumItemSlot.LEGS.b()) {
            enumitemslot = EnumItemSlot.LEGS;
        } else {
            if (i != 100 + EnumItemSlot.FEET.b()) {
                return false;
            }

            enumitemslot = EnumItemSlot.FEET;
        }

        if (!itemstack.isEmpty() && !b(enumitemslot, itemstack) && enumitemslot != EnumItemSlot.HEAD) {
            return false;
        } else {
            this.setSlot(enumitemslot, itemstack);
            return true;
        }
    }

    @Override
    public boolean ca() {
        return this.dD() && super.ca();
    }

    public static boolean b(EnumItemSlot enumitemslot, ItemStack itemstack) {
        EnumItemSlot enumitemslot1 = h(itemstack);

        return enumitemslot1 == enumitemslot || enumitemslot1 == EnumItemSlot.MAINHAND && enumitemslot == EnumItemSlot.OFFHAND || enumitemslot1 == EnumItemSlot.OFFHAND && enumitemslot == EnumItemSlot.MAINHAND;
    }

    @Override
    public boolean df() {
        return super.df() && !this.isNoAI();
    }

    public void setNoAI(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityInsentient.b);

        this.datawatcher.set(EntityInsentient.b, flag ? (byte) (b0 | 1) : (byte) (b0 & -2));
    }

    public void p(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityInsentient.b);

        this.datawatcher.set(EntityInsentient.b, flag ? (byte) (b0 | 2) : (byte) (b0 & -3));
    }

    public boolean isArmsRaisedZombie() { return (this.datawatcher.get(EntityInsentient.b) & 4) != 0; } // Paper - OBFHELPER
    public void setArmsRaisedZombie(boolean flag) { this.q(flag); } // Paper - OBFHELPER
    public void q(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityInsentient.b);

        this.datawatcher.set(EntityInsentient.b, flag ? (byte) (b0 | 4) : (byte) (b0 & -5));
    }

    public boolean isNoAI() {
        return ((Byte) this.datawatcher.get(EntityInsentient.b) & 1) != 0;
    }

    public boolean isLeftHanded() {
        return ((Byte) this.datawatcher.get(EntityInsentient.b) & 2) != 0;
    }

    public boolean dR() {
        return ((Byte) this.datawatcher.get(EntityInsentient.b) & 4) != 0;
    }

    @Override
    public EnumMainHand getMainHand() {
        return this.isLeftHanded() ? EnumMainHand.LEFT : EnumMainHand.RIGHT;
    }

    @Override
    public boolean c(EntityLiving entityliving) {
        return entityliving.getEntityType() == EntityTypes.PLAYER && ((EntityHuman) entityliving).abilities.isInvulnerable ? false : super.c(entityliving);
    }

    @Override
    public boolean C(Entity entity) {
        float f = (float) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
        float f1 = (float) this.getAttributeInstance(GenericAttributes.ATTACK_KNOCKBACK).getValue();

        if (entity instanceof EntityLiving) {
            f += EnchantmentManager.a(this.getItemInMainHand(), ((EntityLiving) entity).getMonsterType());
            f1 += (float) EnchantmentManager.b((EntityLiving) this);
        }

        int i = EnchantmentManager.getFireAspectEnchantmentLevel(this);

        if (i > 0) {
            // CraftBukkit start - Call a combust event when somebody hits with a fire enchanted item
            EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), i * 4);
            org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);

            if (!combustEvent.isCancelled()) {
                entity.setOnFire(combustEvent.getDuration(), false);
            }
            // CraftBukkit end
        }

        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), f);

        if (flag) {
            if (f1 > 0.0F && entity instanceof EntityLiving) {
                ((EntityLiving) entity).a(this, f1 * 0.5F, (double) MathHelper.sin(this.yaw * 0.017453292F), (double) (-MathHelper.cos(this.yaw * 0.017453292F)));
                this.setMot(this.getMot().d(0.6D, 1.0D, 0.6D));
            }

            if (entity instanceof EntityHuman) {
                EntityHuman entityhuman = (EntityHuman) entity;
                ItemStack itemstack = this.getItemInMainHand();
                ItemStack itemstack1 = entityhuman.isHandRaised() ? entityhuman.dl() : ItemStack.a;

                if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.getItem() instanceof ItemAxe && itemstack1.getItem() == Items.SHIELD) {
                    float f2 = 0.25F + (float) EnchantmentManager.getDigSpeedEnchantmentLevel(this) * 0.05F;

                    if (this.random.nextFloat() < f2) {
                        entityhuman.getCooldownTracker().setCooldown(Items.SHIELD, 100);
                        this.world.broadcastEntityEffect(entityhuman, (byte) 30);
                    }
                }
            }

            this.a((EntityLiving) this, entity);
        }

        return flag;
    }

    public boolean isInDaylight() { return this.dS(); } // Paper - OBFHELPER
    protected boolean dS() {
        if (this.world.J() && !this.world.isClientSide) {
            float f = this.aF();
            BlockPosition blockposition = this.getVehicle() instanceof EntityBoat ? (new BlockPosition(this.locX, (double) Math.round(this.locY), this.locZ)).up() : new BlockPosition(this.locX, (double) Math.round(this.locY), this.locZ);

            if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.f(blockposition)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void c(Tag<FluidType> tag) {
        if (this.getNavigation().r()) {
            super.c(tag);
        } else {
            this.setMot(this.getMot().add(0.0D, 0.3D, 0.0D));
        }

    }

    public boolean a(Item item) {
        return this.getItemInMainHand().getItem() == item || this.getItemInOffHand().getItem() == item;
    }
}
