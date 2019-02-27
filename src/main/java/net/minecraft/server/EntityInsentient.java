package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
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

    private static final DataWatcherObject<Byte> a = DataWatcher.a(EntityInsentient.class, DataWatcherRegistry.a);
    public int a_;
    protected int b_;
    protected ControllerLook lookController;
    protected ControllerMove moveController;
    protected ControllerJump h;
    private final EntityAIBodyControl b;
    protected NavigationAbstract navigation;
    public PathfinderGoalSelector goalSelector;
    public PathfinderGoalSelector targetSelector;
    private EntityLiving goalTarget;
    private final EntitySenses bC;
    private final NonNullList<ItemStack> bD;
    public float[] dropChanceHand;
    private final NonNullList<ItemStack> bE;
    public float[] dropChanceArmor;
    // public boolean canPickUpLoot; // CraftBukkit - moved up to EntityLiving
    public boolean persistent;
    private final Map<PathType, Float> bH;
    public MinecraftKey lootTableKey;
    public long lootTableSeed;
    private boolean bK;
    private Entity leashHolder;
    private NBTTagCompound bM;

    protected EntityInsentient(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.bD = NonNullList.a(2, ItemStack.a);
        this.dropChanceHand = new float[2];
        this.bE = NonNullList.a(4, ItemStack.a);
        this.dropChanceArmor = new float[4];
        this.bH = Maps.newEnumMap(PathType.class);
        this.goalSelector = new PathfinderGoalSelector(world != null && world.methodProfiler != null ? world.methodProfiler : null);
        this.targetSelector = new PathfinderGoalSelector(world != null && world.methodProfiler != null ? world.methodProfiler : null);
        this.lookController = new ControllerLook(this);
        this.moveController = new ControllerMove(this);
        this.h = new ControllerJump(this);
        this.b = this.o();
        this.navigation = this.b(world);
        this.bC = new EntitySenses(this);
        Arrays.fill(this.dropChanceArmor, 0.085F);
        Arrays.fill(this.dropChanceHand, 0.085F);
        if (world != null && !world.isClientSide) {
            this.n();
        }

        // CraftBukkit start - default persistance to type's persistance value
        this.persistent = !isTypeNotPersistent();
        // CraftBukkit end
    }

    protected void n() {}

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeMap().b(GenericAttributes.FOLLOW_RANGE).setValue(16.0D);
    }

    protected NavigationAbstract b(World world) {
        return new Navigation(this, world);
    }

    public float a(PathType pathtype) {
        Float ofloat = (Float) this.bH.get(pathtype);

        return ofloat == null ? pathtype.a() : ofloat;
    }

    public void a(PathType pathtype, float f) {
        this.bH.put(pathtype, f);
    }

    protected EntityAIBodyControl o() {
        return new EntityAIBodyControl(this);
    }

    public ControllerLook getControllerLook() {
        return this.lookController;
    }

    public ControllerMove getControllerMove() {
        return this.moveController;
    }

    public ControllerJump getControllerJump() {
        return this.h;
    }

    public NavigationAbstract getNavigation() {
        return this.navigation;
    }

    public EntitySenses getEntitySenses() {
        return this.bC;
    }

    @Nullable
    public EntityLiving getGoalTarget() {
        return this.goalTarget;
    }

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

    public boolean b(Class<? extends EntityLiving> oclass) {
        return oclass != EntityGhast.class;
    }

    public void x() {}

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityInsentient.a, (byte) 0);
    }

    public int z() {
        return 80;
    }

    public void A() {
        SoundEffect soundeffect = this.D();

        if (soundeffect != null) {
            this.a(soundeffect, this.cD(), this.cE());
        }

    }

    public void W() {
        super.W();
        this.world.methodProfiler.enter("mobBaseTick");
        if (this.isAlive() && this.random.nextInt(1000) < this.a_++) {
            this.l();
            this.A();
        }

        this.world.methodProfiler.exit();
    }

    protected void c(DamageSource damagesource) {
        this.l();
        super.c(damagesource);
    }

    private void l() {
        this.a_ = -this.z();
    }

    protected int getExpValue(EntityHuman entityhuman) {
        if (this.b_ > 0) {
            int i = this.b_;

            int j;

            for (j = 0; j < this.bE.size(); ++j) {
                if (!((ItemStack) this.bE.get(j)).isEmpty() && this.dropChanceArmor[j] <= 1.0F) {
                    i += 1 + this.random.nextInt(3);
                }
            }

            for (j = 0; j < this.bD.size(); ++j) {
                if (!((ItemStack) this.bD.get(j)).isEmpty() && this.dropChanceHand[j] <= 1.0F) {
                    i += 1 + this.random.nextInt(3);
                }
            }

            return i;
        } else {
            return this.b_;
        }
    }

    public void doSpawnEffect() {
        if (this.world.isClientSide) {
            for (int i = 0; i < 20; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                double d3 = 10.0D;

                this.world.addParticle(Particles.J, this.locX + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width - d0 * 10.0D, this.locY + (double) (this.random.nextFloat() * this.length) - d1 * 10.0D, this.locZ + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width - d2 * 10.0D, d0, d1, d2);
            }
        } else {
            this.world.broadcastEntityEffect(this, (byte) 20);
        }

    }

    public void tick() {
        super.tick();
        if (!this.world.isClientSide) {
            this.dl();
            if (this.ticksLived % 5 == 0) {
                boolean flag = !(this.bO() instanceof EntityInsentient);
                boolean flag1 = !(this.getVehicle() instanceof EntityBoat);

                this.goalSelector.a(1, flag);
                this.goalSelector.a(4, flag && flag1);
                this.goalSelector.a(2, flag);
            }
        }

    }

    protected float e(float f, float f1) {
        this.b.a();
        return f1;
    }

    @Nullable
    protected SoundEffect D() {
        return null;
    }

    @Nullable
    protected Item getLoot() {
        return null;
    }

    protected void dropDeathLoot(boolean flag, int i) {
        Item item = this.getLoot();

        if (item != null) {
            int j = this.random.nextInt(3);

            if (i > 0) {
                j += this.random.nextInt(i + 1);
            }

            for (int k = 0; k < j; ++k) {
                this.a((IMaterial) item);
            }
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("CanPickUpLoot", this.dj());
        nbttagcompound.setBoolean("PersistenceRequired", this.persistent);
        NBTTagList nbttaglist = new NBTTagList();

        NBTTagCompound nbttagcompound1;

        for (Iterator iterator = this.bE.iterator(); iterator.hasNext(); nbttaglist.add((NBTBase) nbttagcompound1)) {
            ItemStack itemstack = (ItemStack) iterator.next();

            nbttagcompound1 = new NBTTagCompound();
            if (!itemstack.isEmpty()) {
                itemstack.save(nbttagcompound1);
            }
        }

        nbttagcompound.set("ArmorItems", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();

        NBTTagCompound nbttagcompound2;

        for (Iterator iterator1 = this.bD.iterator(); iterator1.hasNext(); nbttaglist1.add((NBTBase) nbttagcompound2)) {
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

            nbttaglist2.add((NBTBase) (new NBTTagFloat(f)));
        }

        nbttagcompound.set("ArmorDropChances", nbttaglist2);
        NBTTagList nbttaglist3 = new NBTTagList();
        float[] afloat1 = this.dropChanceHand;

        j = afloat1.length;

        for (int k = 0; k < j; ++k) {
            float f1 = afloat1[k];

            nbttaglist3.add((NBTBase) (new NBTTagFloat(f1)));
        }

        nbttagcompound.set("HandDropChances", nbttaglist3);
        nbttagcompound.setBoolean("Leashed", this.bK);
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

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);

        // CraftBukkit start - If looting or persistence is false only use it if it was set after we started using it
        if (nbttagcompound.hasKeyOfType("CanPickUpLoot", 1)) {
            boolean data = nbttagcompound.getBoolean("CanPickUpLoot");
            if (isLevelAtLeast(nbttagcompound, 1) || data) {
                this.p(data);
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

            for (i = 0; i < this.bE.size(); ++i) {
                this.bE.set(i, ItemStack.a(nbttaglist.getCompound(i)));
            }
        }

        if (nbttagcompound.hasKeyOfType("HandItems", 9)) {
            nbttaglist = nbttagcompound.getList("HandItems", 10);

            for (i = 0; i < this.bD.size(); ++i) {
                this.bD.set(i, ItemStack.a(nbttaglist.getCompound(i)));
            }
        }

        if (nbttagcompound.hasKeyOfType("ArmorDropChances", 9)) {
            nbttaglist = nbttagcompound.getList("ArmorDropChances", 5);

            for (i = 0; i < nbttaglist.size(); ++i) {
                this.dropChanceArmor[i] = nbttaglist.l(i);
            }
        }

        if (nbttagcompound.hasKeyOfType("HandDropChances", 9)) {
            nbttaglist = nbttagcompound.getList("HandDropChances", 5);

            for (i = 0; i < nbttaglist.size(); ++i) {
                this.dropChanceHand[i] = nbttaglist.l(i);
            }
        }

        this.bK = nbttagcompound.getBoolean("Leashed");
        if (this.bK && nbttagcompound.hasKeyOfType("Leash", 10)) {
            this.bM = nbttagcompound.getCompound("Leash");
        }

        this.r(nbttagcompound.getBoolean("LeftHanded"));
        if (nbttagcompound.hasKeyOfType("DeathLootTable", 8)) {
            this.lootTableKey = new MinecraftKey(nbttagcompound.getString("DeathLootTable"));
            this.lootTableSeed = nbttagcompound.getLong("DeathLootTableSeed");
        }

        this.setNoAI(nbttagcompound.getBoolean("NoAI"));
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return null;
    }
    // CraftBukkit - start
    public MinecraftKey getLootTable() {
        return getDefaultLootTable();
    }
    // CraftBukkit - end

    protected void a(boolean flag, int i, DamageSource damagesource) {
        MinecraftKey minecraftkey = this.lootTableKey;

        if (minecraftkey == null) {
            minecraftkey = this.getDefaultLootTable();
        }

        if (minecraftkey != null) {
            LootTable loottable = this.world.getMinecraftServer().getLootTableRegistry().getLootTable(minecraftkey);

            this.lootTableKey = null;
            LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder((WorldServer) this.world)).entity(this).damageSource(damagesource).position(new BlockPosition(this));

            if (flag && this.killer != null) {
                loottableinfo_builder = loottableinfo_builder.killer(this.killer).luck(this.killer.dJ());
            }

            Collection<ItemStack> collection = loottable.populateLoot(this.lootTableSeed == 0L ? this.random : new Random(this.lootTableSeed), loottableinfo_builder.build());
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                ItemStack itemstack = (ItemStack) iterator.next();

                this.a_(itemstack);
            }

            this.dropEquipment(flag, i);
        } else {
            super.a(flag, i, damagesource);
        }

    }

    public void r(float f) {
        this.bj = f;
    }

    public void s(float f) {
        this.bi = f;
    }

    public void t(float f) {
        this.bh = f;
    }

    public void o(float f) {
        super.o(f);
        this.r(f);
    }

    public void movementTick() {
        super.movementTick();
        this.world.methodProfiler.enter("looting");
        if (!this.world.isClientSide && this.dj() && !this.killed && this.world.getGameRules().getBoolean("mobGriefing")) {
            List<EntityItem> list = this.world.a(EntityItem.class, this.getBoundingBox().grow(1.0D, 0.0D, 1.0D));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityItem entityitem = (EntityItem) iterator.next();

                if (!entityitem.dead && !entityitem.getItemStack().isEmpty() && !entityitem.q()) {
                    this.a(entityitem);
                }
            }
        }

        this.world.methodProfiler.exit();
    }

    protected void a(EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItemStack();
        EnumItemSlot enumitemslot = e(itemstack);
        ItemStack itemstack1 = this.getEquipment(enumitemslot);
        boolean flag = this.a(itemstack, itemstack1, enumitemslot);

        // CraftBukkit start
        boolean canPickup = flag && this.d(itemstack);

        EntityPickupItemEvent entityEvent = new EntityPickupItemEvent((LivingEntity) getBukkitEntity(), (org.bukkit.entity.Item) entityitem.getBukkitEntity(), 0);
        entityEvent.setCancelled(!canPickup);
        this.world.getServer().getPluginManager().callEvent(entityEvent);
        canPickup = !entityEvent.isCancelled();
        if (canPickup) {
            // CraftBukkit end
            double d0 = (double) this.c(enumitemslot);

            if (!itemstack1.isEmpty() && (double) (this.random.nextFloat() - 0.1F) < d0) {
                this.forceDrops = true; // CraftBukkit
                this.a_(itemstack1);
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

    protected boolean d(ItemStack itemstack) {
        return true;
    }

    public boolean isTypeNotPersistent() {
        return true;
    }

    protected void I() {
        if (this.persistent) {
            this.ticksFarFromPlayer = 0;
        } else {
            EntityHuman entityhuman = this.world.findNearbyPlayer(this, -1.0D);

            if (entityhuman != null) {
                double d0 = entityhuman.locX - this.locX;
                double d1 = entityhuman.locY - this.locY;
                double d2 = entityhuman.locZ - this.locZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (d3 > 16384.0D) { // CraftBukkit - remove isTypeNotPersistent() check
                    this.die();
                }

                if (this.ticksFarFromPlayer > 600 && this.random.nextInt(800) == 0 && d3 > 1024.0D) { // CraftBukkit - remove isTypeNotPersistent() check
                    this.die();
                } else if (d3 < 1024.0D) {
                    this.ticksFarFromPlayer = 0;
                }
            }

        }
    }

    protected final void doTick() {
        ++this.ticksFarFromPlayer;
        this.world.methodProfiler.enter("checkDespawn");
        this.I();
        this.world.methodProfiler.exit();
        this.world.methodProfiler.enter("sensing");
        this.bC.a();
        this.world.methodProfiler.exit();
        this.world.methodProfiler.enter("targetSelector");
        this.targetSelector.doTick();
        this.world.methodProfiler.exit();
        this.world.methodProfiler.enter("goalSelector");
        this.goalSelector.doTick();
        this.world.methodProfiler.exit();
        this.world.methodProfiler.enter("navigation");
        this.navigation.d();
        this.world.methodProfiler.exit();
        this.world.methodProfiler.enter("mob tick");
        this.mobTick();
        this.world.methodProfiler.exit();
        if (this.isPassenger() && this.getVehicle() instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient) this.getVehicle();

            entityinsentient.getNavigation().a(this.getNavigation().m(), 1.5D);
            entityinsentient.getControllerMove().a(this.getControllerMove());
        }

        this.world.methodProfiler.enter("controls");
        this.world.methodProfiler.enter("move");
        this.moveController.a();
        this.world.methodProfiler.exitEnter("look");
        this.lookController.a();
        this.world.methodProfiler.exitEnter("jump");
        this.h.b();
        this.world.methodProfiler.exit();
        this.world.methodProfiler.exit();
    }

    protected void mobTick() {}

    public int K() {
        return 40;
    }

    public int L() {
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
        float f2 = (float) (MathHelper.c(d1, d0) * 57.2957763671875D) - 90.0F;
        float f3 = (float) (-(MathHelper.c(d2, d3) * 57.2957763671875D));

        this.pitch = this.c(this.pitch, f3, f1);
        this.yaw = this.c(this.yaw, f2, f);
    }

    private float c(float f, float f1, float f2) {
        float f3 = MathHelper.g(f1 - f);

        if (f3 > f2) {
            f3 = f2;
        }

        if (f3 < -f2) {
            f3 = -f2;
        }

        return f + f3;
    }

    public boolean a(GeneratorAccess generatoraccess, boolean flag) {
        IBlockData iblockdata = generatoraccess.getType((new BlockPosition(this)).down());

        return iblockdata.a((Entity) this);
    }

    public final boolean canSpawn() {
        return this.a((IWorldReader) this.world);
    }

    public boolean a(IWorldReader iworldreader) {
        return !iworldreader.containsLiquid(this.getBoundingBox()) && iworldreader.getCubes(this, this.getBoundingBox()) && iworldreader.a_(this, this.getBoundingBox());
    }

    public int dg() {
        return 4;
    }

    public boolean c(int i) {
        return false;
    }

    public int bn() {
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

    public Iterable<ItemStack> aS() {
        return this.bD;
    }

    public Iterable<ItemStack> getArmorItems() {
        return this.bE;
    }

    public ItemStack getEquipment(EnumItemSlot enumitemslot) {
        switch (enumitemslot.a()) {
        case HAND:
            return (ItemStack) this.bD.get(enumitemslot.b());
        case ARMOR:
            return (ItemStack) this.bE.get(enumitemslot.b());
        default:
            return ItemStack.a;
        }
    }

    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        switch (enumitemslot.a()) {
        case HAND:
            this.bD.set(enumitemslot.b(), itemstack);
            break;
        case ARMOR:
            this.bE.set(enumitemslot.b(), itemstack);
        }

    }

    protected void dropEquipment(boolean flag, int i) {
        EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
        int j = aenumitemslot.length;

        for (int k = 0; k < j; ++k) {
            EnumItemSlot enumitemslot = aenumitemslot[k];
            ItemStack itemstack = this.getEquipment(enumitemslot);
            float f = this.c(enumitemslot);
            boolean flag1 = f > 1.0F;

            if (!itemstack.isEmpty() && !EnchantmentManager.shouldNotDrop(itemstack) && (flag || flag1) && this.random.nextFloat() - (float) i * 0.01F < f) {
                if (!flag1 && itemstack.e()) {
                    itemstack.setDamage(itemstack.h() - this.random.nextInt(1 + this.random.nextInt(Math.max(itemstack.h() - 3, 1))));
                }

                this.a_(itemstack);
            }
        }

    }

    protected float c(EnumItemSlot enumitemslot) {
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

    public static EnumItemSlot e(ItemStack itemstack) {
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
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).b(new AttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05D, 1));
        if (this.random.nextFloat() < 0.05F) {
            this.r(true);
        } else {
            this.r(false);
        }

        return groupdataentity;
    }

    public boolean dh() {
        return false;
    }

    public void di() {
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

    public boolean dj() {
        return this.canPickUpLoot;
    }

    public void p(boolean flag) {
        this.canPickUpLoot = flag;
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    public final boolean b(EntityHuman entityhuman, EnumHand enumhand) {
        if (this.isLeashed() && this.getLeashHolder() == entityhuman) {
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

    protected void dl() {
        if (this.bM != null) {
            this.dr();
        }

        if (this.bK) {
            if (!this.isAlive()) {
                this.world.getServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), UnleashReason.PLAYER_UNLEASH)); // CraftBukkit
                this.unleash(true, true);
            }

            if (this.leashHolder == null || this.leashHolder.dead) {
                this.world.getServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), UnleashReason.HOLDER_GONE)); // CraftBukkit
                this.unleash(true, true);
            }
        }
    }

    public void unleash(boolean flag, boolean flag1) {
        if (this.bK) {
            this.bK = false;
            this.leashHolder = null;
            if (!this.world.isClientSide && flag1) {
                this.forceDrops = true; // CraftBukkit
                this.a((IMaterial) Items.LEAD);
                this.forceDrops = false; // CraftBukkit
            }

            if (!this.world.isClientSide && flag && this.world instanceof WorldServer) {
                ((WorldServer) this.world).getTracker().a((Entity) this, (Packet) (new PacketPlayOutAttachEntity(this, (Entity) null)));
            }
        }

    }

    public boolean a(EntityHuman entityhuman) {
        return !this.isLeashed() && !(this instanceof IMonster);
    }

    public boolean isLeashed() {
        return this.bK;
    }

    public Entity getLeashHolder() {
        return this.leashHolder;
    }

    public void setLeashHolder(Entity entity, boolean flag) {
        this.bK = true;
        this.leashHolder = entity;
        if (!this.world.isClientSide && flag && this.world instanceof WorldServer) {
            ((WorldServer) this.world).getTracker().a((Entity) this, (Packet) (new PacketPlayOutAttachEntity(this, this.leashHolder)));
        }

        if (this.isPassenger()) {
            this.stopRiding();
        }

    }

    public boolean a(Entity entity, boolean flag) {
        boolean flag1 = super.a(entity, flag);

        if (flag1 && this.isLeashed()) {
            this.unleash(true, true);
        }

        return flag1;
    }

    private void dr() {
        if (this.bK && this.bM != null) {
            if (this.bM.b("UUID")) {
                UUID uuid = this.bM.a("UUID");
                List<EntityLiving> list = this.world.a(EntityLiving.class, this.getBoundingBox().g(10.0D));
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    EntityLiving entityliving = (EntityLiving) iterator.next();

                    if (entityliving.getUniqueID().equals(uuid)) {
                        this.setLeashHolder(entityliving, true);
                        break;
                    }
                }
            } else if (this.bM.hasKeyOfType("X", 99) && this.bM.hasKeyOfType("Y", 99) && this.bM.hasKeyOfType("Z", 99)) {
                BlockPosition blockposition = new BlockPosition(this.bM.getInt("X"), this.bM.getInt("Y"), this.bM.getInt("Z"));
                EntityLeash entityleash = EntityLeash.b(this.world, blockposition);

                if (entityleash == null) {
                    entityleash = EntityLeash.a(this.world, blockposition);
                }

                this.setLeashHolder(entityleash, true);
            } else {
                this.world.getServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), UnleashReason.UNKNOWN)); // CraftBukkit
                this.unleash(false, true);
            }
        }

        this.bM = null;
    }

    public boolean c(int i, ItemStack itemstack) {
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

    public boolean bT() {
        return this.dh() && super.bT();
    }

    public static boolean b(EnumItemSlot enumitemslot, ItemStack itemstack) {
        EnumItemSlot enumitemslot1 = e(itemstack);

        return enumitemslot1 == enumitemslot || enumitemslot1 == EnumItemSlot.MAINHAND && enumitemslot == EnumItemSlot.OFFHAND || enumitemslot1 == EnumItemSlot.OFFHAND && enumitemslot == EnumItemSlot.MAINHAND;
    }

    public boolean cP() {
        return super.cP() && !this.isNoAI();
    }

    public void setNoAI(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityInsentient.a);

        this.datawatcher.set(EntityInsentient.a, flag ? (byte) (b0 | 1) : (byte) (b0 & -2));
    }

    public void r(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityInsentient.a);

        this.datawatcher.set(EntityInsentient.a, flag ? (byte) (b0 | 2) : (byte) (b0 & -3));
    }

    public boolean isNoAI() {
        return ((Byte) this.datawatcher.get(EntityInsentient.a) & 1) != 0;
    }

    public boolean isLeftHanded() {
        return ((Byte) this.datawatcher.get(EntityInsentient.a) & 2) != 0;
    }

    public EnumMainHand getMainHand() {
        return this.isLeftHanded() ? EnumMainHand.LEFT : EnumMainHand.RIGHT;
    }

    public boolean B(Entity entity) {
        float f = (float) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
        int i = 0;

        if (entity instanceof EntityLiving) {
            f += EnchantmentManager.a(this.getItemInMainHand(), ((EntityLiving) entity).getMonsterType());
            i += EnchantmentManager.b((EntityLiving) this);
        }

        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), f);

        if (flag) {
            if (i > 0 && entity instanceof EntityLiving) {
                ((EntityLiving) entity).a(this, (float) i * 0.5F, (double) MathHelper.sin(this.yaw * 0.017453292F), (double) (-MathHelper.cos(this.yaw * 0.017453292F)));
                this.motX *= 0.6D;
                this.motZ *= 0.6D;
            }

            int j = EnchantmentManager.getFireAspectEnchantmentLevel(this);

            if (j > 0) {
                // CraftBukkit start - Call a combust event when somebody hits with a fire enchanted item
                EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), j * 4);
                org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);

                if (!combustEvent.isCancelled()) {
                    entity.setOnFire(combustEvent.getDuration(), false);
                }
                // CraftBukkit end
            }

            if (entity instanceof EntityHuman) {
                EntityHuman entityhuman = (EntityHuman) entity;
                ItemStack itemstack = this.getItemInMainHand();
                ItemStack itemstack1 = entityhuman.isHandRaised() ? entityhuman.cW() : ItemStack.a;

                if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.getItem() instanceof ItemAxe && itemstack1.getItem() == Items.SHIELD) {
                    float f1 = 0.25F + (float) EnchantmentManager.getDigSpeedEnchantmentLevel(this) * 0.05F;

                    if (this.random.nextFloat() < f1) {
                        entityhuman.getCooldownTracker().a(Items.SHIELD, 100);
                        this.world.broadcastEntityEffect(entityhuman, (byte) 30);
                    }
                }
            }

            this.a((EntityLiving) this, entity);
        }

        return flag;
    }

    protected boolean dq() {
        if (this.world.L() && !this.world.isClientSide) {
            float f = this.az();
            BlockPosition blockposition = this.getVehicle() instanceof EntityBoat ? (new BlockPosition(this.locX, (double) Math.round(this.locY), this.locZ)).up() : new BlockPosition(this.locX, (double) Math.round(this.locY), this.locZ);

            if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.e(blockposition)) {
                return true;
            }
        }

        return false;
    }

    protected void c(Tag<FluidType> tag) {
        if (this.getNavigation().t()) {
            super.c(tag);
        } else {
            this.motY += 0.30000001192092896D;
        }

    }
}
