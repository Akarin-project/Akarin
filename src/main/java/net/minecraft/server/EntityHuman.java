package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.util.CraftVector;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;
// CraftBukkit end

public abstract class EntityHuman extends EntityLiving {

    public static final EntitySize bs = EntitySize.b(0.6F, 1.8F);
    // CraftBukkit - decompile error
    private static final Map<EntityPose, EntitySize> b = ImmutableMap.<EntityPose, EntitySize>builder().put(EntityPose.STANDING, EntityHuman.bs).put(EntityPose.SLEEPING, EntityHuman.as).put(EntityPose.FALL_FLYING, EntitySize.b(0.6F, 0.6F)).put(EntityPose.SWIMMING, EntitySize.b(0.6F, 0.6F)).put(EntityPose.SPIN_ATTACK, EntitySize.b(0.6F, 0.6F)).put(EntityPose.SNEAKING, EntitySize.b(0.6F, 1.5F)).put(EntityPose.DYING, EntitySize.c(0.2F, 0.2F)).build();
    private static final DataWatcherObject<Float> c = DataWatcher.a(EntityHuman.class, DataWatcherRegistry.c);
    private static final DataWatcherObject<Integer> d = DataWatcher.a(EntityHuman.class, DataWatcherRegistry.b);
    protected static final DataWatcherObject<Byte> bt = DataWatcher.a(EntityHuman.class, DataWatcherRegistry.a);
    protected static final DataWatcherObject<Byte> bu = DataWatcher.a(EntityHuman.class, DataWatcherRegistry.a);
    protected static final DataWatcherObject<NBTTagCompound> bv = DataWatcher.a(EntityHuman.class, DataWatcherRegistry.p);
    protected static final DataWatcherObject<NBTTagCompound> bw = DataWatcher.a(EntityHuman.class, DataWatcherRegistry.p);
    private long e;
    public final PlayerInventory inventory = new PlayerInventory(this);
    protected InventoryEnderChest enderChest = new InventoryEnderChest(this); // CraftBukkit - add "this" to constructor
    public final ContainerPlayer defaultContainer;
    public Container activeContainer;
    protected FoodMetaData foodData = new FoodMetaData(this); // CraftBukkit - add "this" to constructor
    protected int bC;
    public float bD;
    public float bE;
    public int bF;
    public double bG;
    public double bH;
    public double bI;
    public double bJ;
    public double bK;
    public double bL;
    public int sleepTicks;
    protected boolean bM;
    private BlockPosition g;
    private boolean bU;
    public final PlayerAbilities abilities = new PlayerAbilities();
    public int expLevel;
    public int expTotal;
    public float exp;
    protected int bR;
    protected final float bS = 0.02F;
    private int bV;
    private GameProfile bW; public final void setProfile(final GameProfile profile) { this.bW = profile; } // Paper - OBFHELPER
    private ItemStack bY;
    private final ItemCooldown bZ;
    @Nullable
    public EntityFishingHook hookedFish;
    // Paper start
    public boolean affectsSpawning = true;
    // Paper end

    // CraftBukkit start
    public boolean fauxSleeping;
    public String spawnWorld = "";
    public int oldLevel = -1;

    @Override
    public CraftHumanEntity getBukkitEntity() {
        return (CraftHumanEntity) super.getBukkitEntity();
    }
    // CraftBukkit end

    public EntityHuman(World world, GameProfile gameprofile) {
        super(EntityTypes.PLAYER, world);
        this.bY = ItemStack.a;
        this.bZ = this.g();
        this.a(a(gameprofile));
        this.bW = gameprofile;
        this.defaultContainer = new ContainerPlayer(this.inventory, !world.isClientSide, this);
        this.activeContainer = this.defaultContainer;
        BlockPosition blockposition = world.getSpawn();

        this.setPositionRotation((double) blockposition.getX() + 0.5D, (double) (blockposition.getY() + 1), (double) blockposition.getZ() + 0.5D, 0.0F, 0.0F);
        this.aX = 180.0F;
    }

    public boolean a(World world, BlockPosition blockposition, EnumGamemode enumgamemode) {
        if (!enumgamemode.d()) {
            return false;
        } else if (enumgamemode == EnumGamemode.SPECTATOR) {
            return true;
        } else if (this.dQ()) {
            return false;
        } else {
            ItemStack itemstack = this.getItemInMainHand();

            return itemstack.isEmpty() || !itemstack.a(world.t(), new ShapeDetectorBlock(world, blockposition, false));
        }
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE).setValue(1.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.10000000149011612D);
        this.getAttributeMap().b(GenericAttributes.ATTACK_SPEED);
        this.getAttributeMap().b(GenericAttributes.LUCK);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityHuman.c, 0.0F);
        this.datawatcher.register(EntityHuman.d, 0);
        this.datawatcher.register(EntityHuman.bt, (byte) 0);
        this.datawatcher.register(EntityHuman.bu, (byte) 1);
        this.datawatcher.register(EntityHuman.bv, new NBTTagCompound());
        this.datawatcher.register(EntityHuman.bw, new NBTTagCompound());
    }

    @Override
    public void tick() {
        this.noclip = this.isSpectator();
        if (this.isSpectator()) {
            this.onGround = false;
        }

        if (this.bF > 0) {
            --this.bF;
        }

        if (this.isSleeping()) {
            ++this.sleepTicks;
            if (this.sleepTicks > 100) {
                this.sleepTicks = 100;
            }

            if (!this.world.isClientSide && this.world.J()) {
                this.wakeup(false, true, true);
            }
        } else if (this.sleepTicks > 0) {
            ++this.sleepTicks;
            if (this.sleepTicks >= 110) {
                this.sleepTicks = 0;
            }
        }

        this.dA();
        super.tick();
        if (!this.world.isClientSide && this.activeContainer != null && !this.activeContainer.canUse(this)) {
            this.closeInventory(org.bukkit.event.inventory.InventoryCloseEvent.Reason.CANT_USE); // Paper
            this.activeContainer = this.defaultContainer;
        }

        if (this.isBurning() && this.abilities.isInvulnerable) {
            this.extinguish();
        }

        this.n();
        if (!this.world.isClientSide) {
            this.foodData.a(this);
            this.a(StatisticList.PLAY_ONE_MINUTE);
            if (this.isAlive()) {
                this.a(StatisticList.TIME_SINCE_DEATH);
            }

            if (this.isSneaking()) {
                this.a(StatisticList.SNEAK_TIME);
            }

            if (!this.isSleeping()) {
                this.a(StatisticList.TIME_SINCE_REST);
            }
        }

        int i = 29999999;
        double d0 = MathHelper.a(this.locX, -2.9999999E7D, 2.9999999E7D);
        double d1 = MathHelper.a(this.locZ, -2.9999999E7D, 2.9999999E7D);

        if (d0 != this.locX || d1 != this.locZ) {
            this.setPosition(d0, this.locY, d1);
        }

        ++this.aD;
        ItemStack itemstack = this.getItemInMainHand();

        if (!ItemStack.matches(this.bY, itemstack)) {
            if (!ItemStack.d(this.bY, itemstack)) {
                this.dZ();
            }

            this.bY = itemstack.isEmpty() ? ItemStack.a : itemstack.cloneItemStack();
        }

        this.l();
        this.bZ.a();
        this.dB();
    }

    protected boolean dA() {
        this.bM = this.a(TagsFluid.WATER, true);
        return this.bM;
    }

    private void l() {
        ItemStack itemstack = this.getEquipment(EnumItemSlot.HEAD);

        if (itemstack.getItem() == Items.TURTLE_HELMET && !this.a(TagsFluid.WATER)) {
            this.addEffect(new MobEffect(MobEffects.WATER_BREATHING, 200, 0, false, false, true), org.bukkit.event.entity.EntityPotionEffectEvent.Cause.TURTLE_HELMET); // CraftBukkit
        }

    }

    protected ItemCooldown g() {
        return new ItemCooldown();
    }

    private void n() {
        this.bG = this.bJ;
        this.bH = this.bK;
        this.bI = this.bL;
        double d0 = this.locX - this.bJ;
        double d1 = this.locY - this.bK;
        double d2 = this.locZ - this.bL;
        double d3 = 10.0D;

        if (d0 > 10.0D) {
            this.bJ = this.locX;
            this.bG = this.bJ;
        }

        if (d2 > 10.0D) {
            this.bL = this.locZ;
            this.bI = this.bL;
        }

        if (d1 > 10.0D) {
            this.bK = this.locY;
            this.bH = this.bK;
        }

        if (d0 < -10.0D) {
            this.bJ = this.locX;
            this.bG = this.bJ;
        }

        if (d2 < -10.0D) {
            this.bL = this.locZ;
            this.bI = this.bL;
        }

        if (d1 < -10.0D) {
            this.bK = this.locY;
            this.bH = this.bK;
        }

        this.bJ += d0 * 0.25D;
        this.bL += d2 * 0.25D;
        this.bK += d1 * 0.25D;
    }

    protected void dB() {
        if (this.c(EntityPose.SWIMMING)) {
            EntityPose entitypose;

            if (this.isGliding()) {
                entitypose = EntityPose.FALL_FLYING;
            } else if (this.isSleeping()) {
                entitypose = EntityPose.SLEEPING;
            } else if (this.isSwimming()) {
                entitypose = EntityPose.SWIMMING;
            } else if (this.isRiptiding()) {
                entitypose = EntityPose.SPIN_ATTACK;
            } else if (this.isSneaking() && !this.abilities.isFlying) {
                entitypose = EntityPose.SNEAKING;
            } else {
                entitypose = EntityPose.STANDING;
            }

            EntityPose entitypose1;

            if (!this.isSpectator() && !this.isPassenger() && !this.c(entitypose)) {
                if (this.c(EntityPose.SNEAKING)) {
                    entitypose1 = EntityPose.SNEAKING;
                } else {
                    entitypose1 = EntityPose.SWIMMING;
                }
            } else {
                entitypose1 = entitypose;
            }

            this.setPose(entitypose1);
        }
    }

    @Override
    public int ab() {
        return this.abilities.isInvulnerable ? 1 : 80;
    }

    @Override
    protected SoundEffect getSoundSwim() {
        return SoundEffects.ENTITY_PLAYER_SWIM;
    }

    @Override
    protected SoundEffect getSoundSplash() {
        return SoundEffects.ENTITY_PLAYER_SPLASH;
    }

    @Override
    protected SoundEffect getSoundSplashHighSpeed() {
        return SoundEffects.ENTITY_PLAYER_SPLASH_HIGH_SPEED;
    }

    @Override
    public int aX() {
        return 10;
    }

    @Override
    public void a(SoundEffect soundeffect, float f, float f1) {
        this.world.playSound(this, this.locX, this.locY, this.locZ, soundeffect, this.getSoundCategory(), f, f1);
    }

    public void a(SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1) {}

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.PLAYERS;
    }

    @Override
    public int getMaxFireTicks() {
        return 20;
    }

    // Paper start - unused code, but to keep signatures aligned
    public void closeInventory(org.bukkit.event.inventory.InventoryCloseEvent.Reason reason) {
        closeInventory();
        this.activeContainer = this.defaultContainer;
    }
    // Paper end

    public void closeInventory() {
        this.activeContainer = this.defaultContainer;
    }

    @Override
    public void passengerTick() {
        if (!this.world.isClientSide && this.isSneaking() && this.isPassenger()) {
            this.stopRiding();
            this.setSneaking(false);
        } else {
            double d0 = this.locX;
            double d1 = this.locY;
            double d2 = this.locZ;
            float f = this.yaw;
            float f1 = this.pitch;

            super.passengerTick();
            this.bD = this.bE;
            this.bE = 0.0F;
            this.m(this.locX - d0, this.locY - d1, this.locZ - d2);
            if (this.getVehicle() instanceof EntityPig) {
                this.pitch = f1;
                this.yaw = f;
                this.aK = ((EntityPig) this.getVehicle()).aK;
            }

        }
    }

    @Override
    protected void doTick() {
        super.doTick();
        this.cO();
        this.aM = this.yaw;
    }

    @Override
    public void movementTick() {
        if (this.bC > 0) {
            --this.bC;
        }

        if (this.world.getDifficulty() == EnumDifficulty.PEACEFUL && this.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
            if (this.getHealth() < this.getMaxHealth() && this.ticksLived % 20 == 0) {
                // CraftBukkit - added regain reason of "REGEN" for filtering purposes.
                this.heal(1.0F, org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.REGEN);
            }

            if (this.foodData.c() && this.ticksLived % 10 == 0) {
                this.foodData.a(this.foodData.getFoodLevel() + 1);
            }
        }

        this.inventory.j();
        this.bD = this.bE;
        super.movementTick();
        AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

        if (!this.world.isClientSide) {
            attributeinstance.setValue((double) this.abilities.b());
        }

        this.aO = 0.02F;
        if (this.isSprinting()) {
            this.aO = (float) ((double) this.aO + 0.005999999865889549D);
        }

        this.o((float) attributeinstance.getValue());
        float f;

        if (this.onGround && this.getHealth() > 0.0F && !this.isSwimming()) {
            f = Math.min(0.1F, MathHelper.sqrt(b(this.getMot())));
        } else {
            f = 0.0F;
        }

        this.bE += (f - this.bE) * 0.4F;
        if (this.getHealth() > 0.0F && !this.isSpectator()) {
            AxisAlignedBB axisalignedbb;

            if (this.isPassenger() && !this.getVehicle().dead) {
                axisalignedbb = this.getBoundingBox().b(this.getVehicle().getBoundingBox()).grow(1.0D, 0.0D, 1.0D);
            } else {
                axisalignedbb = this.getBoundingBox().grow(1.0D, 0.5D, 1.0D);
            }

            List<Entity> list = this.world.getEntities(this, axisalignedbb);

            for (int i = 0; i < list.size(); ++i) {
                Entity entity = (Entity) list.get(i);

                if (!entity.dead) {
                    this.c(entity);
                }
            }
        }

        this.j(this.getShoulderEntityLeft());
        this.j(this.getShoulderEntityRight());
        if (!this.world.isClientSide && (this.fallDistance > 0.5F || this.isInWater() || this.isPassenger()) || this.abilities.isFlying || this.isSleeping()) {
            if (!this.world.paperConfig.parrotsHangOnBetter) this.releaseShoulderEntities(); // Paper - Hang on!
        }

    }

    private void j(@Nullable NBTTagCompound nbttagcompound) {
        if (nbttagcompound != null && !nbttagcompound.hasKey("Silent") || !nbttagcompound.getBoolean("Silent")) {
            String s = nbttagcompound.getString("id");

            EntityTypes.a(s).filter((entitytypes) -> {
                return entitytypes == EntityTypes.PARROT;
            }).ifPresent((entitytypes) -> {
                EntityParrot.a(this.world, (Entity) this);
            });
        }

    }

    private void c(Entity entity) {
        entity.pickup(this);
    }

    public int getScore() {
        return (Integer) this.datawatcher.get(EntityHuman.d);
    }

    public void setScore(int i) {
        this.datawatcher.set(EntityHuman.d, i);
    }

    public void addScore(int i) {
        int j = this.getScore();

        this.datawatcher.set(EntityHuman.d, j + i);
    }

    @Override
    public void die(DamageSource damagesource) {
        super.die(damagesource);
        this.setPosition(this.locX, this.locY, this.locZ);
        if (!this.isSpectator()) {
            this.d(damagesource);
        }

        if (damagesource != null) {
            this.setMot((double) (-MathHelper.cos((this.az + this.yaw) * 0.017453292F) * 0.1F), 0.10000000149011612D, (double) (-MathHelper.sin((this.az + this.yaw) * 0.017453292F) * 0.1F));
        } else {
            this.setMot(0.0D, 0.1D, 0.0D);
        }

        this.a(StatisticList.DEATHS);
        this.a(StatisticList.CUSTOM.b(StatisticList.TIME_SINCE_DEATH));
        this.a(StatisticList.CUSTOM.b(StatisticList.TIME_SINCE_REST));
        this.extinguish();
        this.setFlag(0, false);
    }

    @Override
    protected void cF() {
        super.cF();
        if (!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            this.removeCursedItems();
            this.inventory.dropContents();
        }

    }

    protected void removeCursedItems() {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);

            if (!itemstack.isEmpty() && EnchantmentManager.shouldNotDrop(itemstack)) {
                this.inventory.splitWithoutUpdate(i);
            }
        }

    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return damagesource == DamageSource.BURN ? SoundEffects.ENTITY_PLAYER_HURT_ON_FIRE : (damagesource == DamageSource.DROWN ? SoundEffects.ENTITY_PLAYER_HURT_DROWN : (damagesource == DamageSource.SWEET_BERRY_BUSH ? SoundEffects.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH : SoundEffects.ENTITY_PLAYER_HURT));
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_PLAYER_DEATH;
    }

    @Nullable
    public EntityItem n(boolean flag) {
        // Called only when dropped by Q or CTRL-Q
        return this.a(this.inventory.splitStack(this.inventory.itemInHandIndex, flag && !this.inventory.getItemInHand().isEmpty() ? this.inventory.getItemInHand().getCount() : 1), false, true);
    }

    @Nullable
    public EntityItem drop(ItemStack itemstack, boolean flag) {
        return this.a(itemstack, false, flag);
    }

    @Nullable
    public EntityItem a(ItemStack itemstack, boolean flag, boolean flag1) {
        if (itemstack.isEmpty()) {
            return null;
        } else {
            double d0 = this.locY - 0.30000001192092896D + (double) this.getHeadHeight();
            EntityItem entityitem = new EntityItem(this.world, this.locX, d0, this.locZ, itemstack);

            entityitem.setPickupDelay(40);
            if (flag1) {
                entityitem.setThrower(this.getUniqueID());
            }

            float f;
            float f1;

            if (flag) {
                f = this.random.nextFloat() * 0.5F;
                f1 = this.random.nextFloat() * 6.2831855F;
                this.setMot((double) (-MathHelper.sin(f1) * f), 0.20000000298023224D, (double) (MathHelper.cos(f1) * f));
            } else {
                f = 0.3F;
                f1 = MathHelper.sin(this.pitch * 0.017453292F);
                float f2 = MathHelper.cos(this.pitch * 0.017453292F);
                float f3 = MathHelper.sin(this.yaw * 0.017453292F);
                float f4 = MathHelper.cos(this.yaw * 0.017453292F);
                float f5 = this.random.nextFloat() * 6.2831855F;
                float f6 = 0.02F * this.random.nextFloat();

                entityitem.setMot((double) (-f3 * f2 * 0.3F) + Math.cos((double) f5) * (double) f6, (double) (-f1 * 0.3F + 0.1F + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double) (f4 * f2 * 0.3F) + Math.sin((double) f5) * (double) f6);
            }

            // CraftBukkit start - fire PlayerDropItemEvent
            Player player = (Player) this.getBukkitEntity();
            Item drop = (Item) entityitem.getBukkitEntity();

            PlayerDropItemEvent event = new PlayerDropItemEvent(player, drop);
            this.world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                org.bukkit.inventory.ItemStack cur = player.getInventory().getItemInHand();
                if (flag1 && (cur == null || cur.getAmount() == 0)) {
                    // The complete stack was dropped
                    player.getInventory().setItemInHand(drop.getItemStack());
                } else if (flag1 && cur.isSimilar(drop.getItemStack()) && cur.getAmount() < cur.getMaxStackSize() && drop.getItemStack().getAmount() == 1) {
                    // Only one item is dropped
                    cur.setAmount(cur.getAmount() + 1);
                    player.getInventory().setItemInHand(cur);
                } else {
                    // Fallback
                    player.getInventory().addItem(drop.getItemStack());
                }
                return null;
            }
            // CraftBukkit end
            // Paper start - remove player from map on drop
            if (itemstack.getItem() == Items.FILLED_MAP) {
                WorldMap worldmap = ItemWorldMap.getSavedMap(itemstack, this.world);
                worldmap.updateSeenPlayers(this, itemstack);
            }
            // Paper end

            return entityitem;
        }
    }

    public float b(IBlockData iblockdata) {
        float f = this.inventory.a(iblockdata);

        if (f > 1.0F) {
            int i = EnchantmentManager.getDigSpeedEnchantmentLevel(this);
            ItemStack itemstack = this.getItemInMainHand();

            if (i > 0 && !itemstack.isEmpty()) {
                f += (float) (i * i + 1);
            }
        }

        if (MobEffectUtil.a(this)) {
            f *= 1.0F + (float) (MobEffectUtil.b(this) + 1) * 0.2F;
        }

        if (this.hasEffect(MobEffects.SLOWER_DIG)) {
            float f1;

            switch (this.getEffect(MobEffects.SLOWER_DIG).getAmplifier()) {
                case 0:
                    f1 = 0.3F;
                    break;
                case 1:
                    f1 = 0.09F;
                    break;
                case 2:
                    f1 = 0.0027F;
                    break;
                case 3:
                default:
                    f1 = 8.1E-4F;
            }

            f *= f1;
        }

        if (this.a(TagsFluid.WATER) && !EnchantmentManager.h((EntityLiving) this)) {
            f /= 5.0F;
        }

        if (!this.onGround) {
            f /= 5.0F;
        }

        return f;
    }

    public boolean hasBlock(IBlockData iblockdata) {
        return iblockdata.getMaterial().isAlwaysDestroyable() || this.inventory.b(iblockdata);
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.a(a(this.bW));
        NBTTagList nbttaglist = nbttagcompound.getList("Inventory", 10);

        this.inventory.b(nbttaglist);
        this.inventory.itemInHandIndex = nbttagcompound.getInt("SelectedItemSlot");
        this.sleepTicks = nbttagcompound.getShort("SleepTimer");
        this.exp = nbttagcompound.getFloat("XpP");
        this.expLevel = nbttagcompound.getInt("XpLevel");
        this.expTotal = nbttagcompound.getInt("XpTotal");
        this.bR = nbttagcompound.getInt("XpSeed");
        if (this.bR == 0) {
            this.bR = this.random.nextInt();
        }

        this.setScore(nbttagcompound.getInt("Score"));

        // CraftBukkit start
        this.spawnWorld = nbttagcompound.getString("SpawnWorld");
        if ("".equals(spawnWorld)) {
            this.spawnWorld = this.world.getServer().getWorlds().get(0).getName();
        }
        // CraftBukkit end

        if (nbttagcompound.hasKeyOfType("SpawnX", 99) && nbttagcompound.hasKeyOfType("SpawnY", 99) && nbttagcompound.hasKeyOfType("SpawnZ", 99)) {
            this.g = new BlockPosition(nbttagcompound.getInt("SpawnX"), nbttagcompound.getInt("SpawnY"), nbttagcompound.getInt("SpawnZ"));
            this.bU = nbttagcompound.getBoolean("SpawnForced");
        }

        this.foodData.a(nbttagcompound);
        this.abilities.b(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("EnderItems", 9)) {
            this.enderChest.a(nbttagcompound.getList("EnderItems", 10));
        }

        if (nbttagcompound.hasKeyOfType("ShoulderEntityLeft", 10)) {
            this.setShoulderEntityLeft(nbttagcompound.getCompound("ShoulderEntityLeft"));
        }

        if (nbttagcompound.hasKeyOfType("ShoulderEntityRight", 10)) {
            this.setShoulderEntityRight(nbttagcompound.getCompound("ShoulderEntityRight"));
        }

    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("DataVersion", SharedConstants.a().getWorldVersion());
        nbttagcompound.set("Inventory", this.inventory.a(new NBTTagList()));
        nbttagcompound.setInt("SelectedItemSlot", this.inventory.itemInHandIndex);
        nbttagcompound.setShort("SleepTimer", (short) this.sleepTicks);
        nbttagcompound.setFloat("XpP", this.exp);
        nbttagcompound.setInt("XpLevel", this.expLevel);
        nbttagcompound.setInt("XpTotal", this.expTotal);
        nbttagcompound.setInt("XpSeed", this.bR);
        nbttagcompound.setInt("Score", this.getScore());
        if (this.g != null) {
            nbttagcompound.setInt("SpawnX", this.g.getX());
            nbttagcompound.setInt("SpawnY", this.g.getY());
            nbttagcompound.setInt("SpawnZ", this.g.getZ());
            nbttagcompound.setBoolean("SpawnForced", this.bU);
        }

        this.foodData.b(nbttagcompound);
        this.abilities.a(nbttagcompound);
        nbttagcompound.set("EnderItems", this.enderChest.f());
        if (!this.getShoulderEntityLeft().isEmpty()) {
            nbttagcompound.set("ShoulderEntityLeft", this.getShoulderEntityLeft());
        }

        if (!this.getShoulderEntityRight().isEmpty()) {
            nbttagcompound.set("ShoulderEntityRight", this.getShoulderEntityRight());
        }
        nbttagcompound.setString("SpawnWorld", spawnWorld); // CraftBukkit - fixes bed spawns for multiworld worlds

    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (this.abilities.isInvulnerable && !damagesource.ignoresInvulnerability()) {
            this.forceExplosionKnockback = true; // SPIGOT-5258 - Make invulnerable players get knockback from explosions
            return false;
        } else {
            this.ticksFarFromPlayer = 0;
            if (this.getHealth() <= 0.0F) {
                return false;
            } else {
                // this.releaseShoulderEntities(); // CraftBukkit - moved down
                if (damagesource.s()) {
                    if (this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
                        return false; // CraftBukkit - f = 0.0f -> return false
                    }

                    if (this.world.getDifficulty() == EnumDifficulty.EASY) {
                        f = Math.min(f / 2.0F + 1.0F, f);
                    }

                    if (this.world.getDifficulty() == EnumDifficulty.HARD) {
                        f = f * 3.0F / 2.0F;
                    }
                }

                // CraftBukkit start - Don't filter out 0 damage
                boolean damaged = super.damageEntity(damagesource, f);
                if (damaged) {
                    this.releaseShoulderEntities();
                }
                return damaged;
                // CraftBukkit end
            }
        }
    }

    @Override
    protected void shieldBlock(EntityLiving entityliving) {
        super.shieldBlock(entityliving);
        if (entityliving.getItemInMainHand().getItem() instanceof ItemAxe) {
            this.o(true);
        }

    }

    public boolean a(EntityHuman entityhuman) {
        // CraftBukkit start - Change to check OTHER player's scoreboard team according to API
        // To summarize this method's logic, it's "Can parameter hurt this"
        org.bukkit.scoreboard.Team team;
        if (entityhuman instanceof EntityPlayer) {
            EntityPlayer thatPlayer = (EntityPlayer) entityhuman;
            team = thatPlayer.getBukkitEntity().getScoreboard().getPlayerTeam(thatPlayer.getBukkitEntity());
            if (team == null || team.allowFriendlyFire()) {
                return true;
            }
        } else {
            // This should never be called, but is implemented anyway
            org.bukkit.OfflinePlayer thisPlayer = entityhuman.world.getServer().getOfflinePlayer(entityhuman.getName());
            team = entityhuman.world.getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(thisPlayer);
            if (team == null || team.allowFriendlyFire()) {
                return true;
            }
        }

        if (this instanceof EntityPlayer) {
            return !team.hasPlayer(((EntityPlayer) this).getBukkitEntity());
        }
        return !team.hasPlayer(this.world.getServer().getOfflinePlayer(this.getName()));
        // CraftBukkit end
    }

    @Override
    protected void damageArmor(float f) {
        this.inventory.a(f);
    }

    @Override
    protected void damageShield(float f) {
        if (f >= 3.0F && this.activeItem.getItem() == Items.SHIELD) {
            int i = 1 + MathHelper.d(f);
            EnumHand enumhand = this.getRaisedHand();

            this.activeItem.damage(i, this, (entityhuman) -> {
                entityhuman.d(enumhand);
            });
            if (this.activeItem.isEmpty()) {
                if (enumhand == EnumHand.MAIN_HAND) {
                    this.setSlot(EnumItemSlot.MAINHAND, ItemStack.a);
                } else {
                    this.setSlot(EnumItemSlot.OFFHAND, ItemStack.a);
                }

                this.activeItem = ItemStack.a;
                this.a(SoundEffects.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);
            }
        }

    }

    // CraftBukkit start
    @Override
    protected boolean damageEntity0(DamageSource damagesource, float f) { // void -> boolean
        if (true) {
            return super.damageEntity0(damagesource, f);
        }
        // CraftBukkit end
        if (!this.isInvulnerable(damagesource)) {
            f = this.applyArmorModifier(damagesource, f);
            f = this.applyMagicModifier(damagesource, f);
            float f1 = f;

            f = Math.max(f - this.getAbsorptionHearts(), 0.0F);
            this.setAbsorptionHearts(this.getAbsorptionHearts() - (f1 - f));
            float f2 = f1 - f;

            if (f2 > 0.0F && f2 < 3.4028235E37F) {
                this.a(StatisticList.DAMAGE_ABSORBED, Math.round(f2 * 10.0F));
            }

            if (f != 0.0F) {
                this.applyExhaustion(damagesource.getExhaustionCost());
                float f3 = this.getHealth();

                this.setHealth(this.getHealth() - f);
                this.getCombatTracker().trackDamage(damagesource, f3, f);
                if (f < 3.4028235E37F) {
                    this.a(StatisticList.DAMAGE_TAKEN, Math.round(f * 10.0F));
                }

            }
        }
        return false; // CraftBukkit
    }

    public void openSign(TileEntitySign tileentitysign) {}

    public void a(CommandBlockListenerAbstract commandblocklistenerabstract) {}

    public void a(TileEntityCommand tileentitycommand) {}

    public void a(TileEntityStructure tileentitystructure) {}

    public void a(TileEntityJigsaw tileentityjigsaw) {}

    public void openHorseInventory(EntityHorseAbstract entityhorseabstract, IInventory iinventory) {}

    public OptionalInt openContainer(@Nullable ITileInventory itileinventory) {
        return OptionalInt.empty();
    }

    public void openTrade(int i, MerchantRecipeList merchantrecipelist, int j, int k, boolean flag, boolean flag1) {}

    public void openBook(ItemStack itemstack, EnumHand enumhand) {}

    public EnumInteractionResult a(Entity entity, EnumHand enumhand) {
        if (this.isSpectator()) {
            if (entity instanceof ITileInventory) {
                this.openContainer((ITileInventory) entity);
            }

            return EnumInteractionResult.PASS;
        } else {
            ItemStack itemstack = this.b(enumhand);
            ItemStack itemstack1 = itemstack.isEmpty() ? ItemStack.a : itemstack.cloneItemStack();

            if (entity.b(this, enumhand)) {
                if (this.abilities.canInstantlyBuild && itemstack == this.b(enumhand) && itemstack.getCount() < itemstack1.getCount()) {
                    itemstack.setCount(itemstack1.getCount());
                }

                return EnumInteractionResult.SUCCESS;
            } else {
                if (!itemstack.isEmpty() && entity instanceof EntityLiving) {
                    if (this.abilities.canInstantlyBuild) {
                        itemstack = itemstack1;
                    }

                    if (itemstack.a(this, (EntityLiving) entity, enumhand)) {
                        if (itemstack.isEmpty() && !this.abilities.canInstantlyBuild) {
                            this.a(enumhand, ItemStack.a);
                        }

                        return EnumInteractionResult.SUCCESS;
                    }
                }

                return EnumInteractionResult.PASS;
            }
        }
    }

    @Override
    public double aO() {
        return -0.35D;
    }

    // Paper start
    @Override public void stopRiding() { stopRiding(false); }
    @Override public void stopRiding(boolean suppressCancellation) {
        // Paper end
        super.stopRiding(suppressCancellation); // Paper - suppress
        this.j = 0;
    }

    @Override
    protected boolean isFrozen() {
        return super.isFrozen() || this.isSleeping();
    }

    // Paper start - send SoundEffect to everyone who can see fromEntity
    private static void sendSoundEffect(EntityHuman fromEntity, double x, double y, double z, SoundEffect soundEffect, SoundCategory soundCategory, float volume, float pitch) {
        fromEntity.world.sendSoundEffect(fromEntity, x, y, z, soundEffect, soundCategory, volume, pitch); // This will not send the effect to the entity himself
        if (fromEntity instanceof EntityPlayer) {
            ((EntityPlayer) fromEntity).playerConnection.sendPacket(new PacketPlayOutNamedSoundEffect(soundEffect, soundCategory, x, y, z, volume, pitch));
        }
    }
    // Paper end

    public void attack(Entity entity) {
        if (entity.bs()) {
            if (!entity.t(this)) {
                float f = (float) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
                float f1;

                if (entity instanceof EntityLiving) {
                    f1 = EnchantmentManager.a(this.getItemInMainHand(), ((EntityLiving) entity).getMonsterType());
                } else {
                    f1 = EnchantmentManager.a(this.getItemInMainHand(), EnumMonsterType.UNDEFINED);
                }

                float f2 = this.s(0.5F);

                f *= 0.2F + f2 * f2 * 0.8F;
                f1 *= f2;
                this.dZ();
                if (f > 0.0F || f1 > 0.0F) {
                    boolean flag = f2 > 0.9F;
                    boolean flag1 = false;
                    byte b0 = 0;
                    int i = b0 + EnchantmentManager.b((EntityLiving) this);

                    if (this.isSprinting() && flag) {
                        sendSoundEffect(this, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_PLAYER_ATTACK_KNOCKBACK, this.getSoundCategory(), 1.0F, 1.0F);  // Paper - send while respecting visibility
                        ++i;
                        flag1 = true;
                    }

                    boolean flag2 = flag && this.fallDistance > 0.0F && !this.onGround && !this.isClimbing() && !this.isInWater() && !this.hasEffect(MobEffects.BLINDNESS) && !this.isPassenger() && entity instanceof EntityLiving;

                    flag2 = flag2 && !world.paperConfig.disablePlayerCrits; // Paper
                    flag2 = flag2 && !this.isSprinting();
                    if (flag2) {
                        f *= 1.5F;
                    }

                    f += f1;
                    boolean flag3 = false;
                    double d0 = (double) (this.E - this.D);

                    if (flag && !flag2 && !flag1 && this.onGround && d0 < (double) this.db()) {
                        ItemStack itemstack = this.b(EnumHand.MAIN_HAND);

                        if (itemstack.getItem() instanceof ItemSword) {
                            flag3 = true;
                        }
                    }

                    float f3 = 0.0F;
                    boolean flag4 = false;
                    int j = EnchantmentManager.getFireAspectEnchantmentLevel(this);

                    if (entity instanceof EntityLiving) {
                        f3 = ((EntityLiving) entity).getHealth();
                        if (j > 0 && !entity.isBurning()) {
                            // CraftBukkit start - Call a combust event when somebody hits with a fire enchanted item
                            EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), 1);
                            org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);

                            if (!combustEvent.isCancelled()) {
                                flag4 = true;
                                entity.setOnFire(combustEvent.getDuration(), false);
                            }
                            // CraftBukkit end
                        }
                    }

                    Vec3D vec3d = entity.getMot();
                    boolean flag5 = entity.damageEntity(DamageSource.playerAttack(this), f);

                    if (flag5) {
                        if (i > 0) {
                            if (entity instanceof EntityLiving) {
                                ((EntityLiving) entity).a(this, (float) i * 0.5F, (double) MathHelper.sin(this.yaw * 0.017453292F), (double) (-MathHelper.cos(this.yaw * 0.017453292F)));
                            } else {
                                entity.f((double) (-MathHelper.sin(this.yaw * 0.017453292F) * (float) i * 0.5F), 0.1D, (double) (MathHelper.cos(this.yaw * 0.017453292F) * (float) i * 0.5F));
                            }

                            this.setMot(this.getMot().d(0.6D, 1.0D, 0.6D));
                            // Paper start - Configuration option to disable automatic sprint interruption
                            if (!world.paperConfig.disableSprintInterruptionOnAttack) {
                                this.setSprinting(false);
                            }
                            // Paper end
                        }

                        if (flag3) {
                            float f4 = 1.0F + EnchantmentManager.a((EntityLiving) this) * f;
                            List<EntityLiving> list = this.world.a(EntityLiving.class, entity.getBoundingBox().grow(1.0D, 0.25D, 1.0D));
                            Iterator iterator = list.iterator();

                            while (iterator.hasNext()) {
                                EntityLiving entityliving = (EntityLiving) iterator.next();

                                if (entityliving != this && entityliving != entity && !this.r(entityliving) && (!(entityliving instanceof EntityArmorStand) || !((EntityArmorStand) entityliving).isMarker()) && this.h((Entity) entityliving) < 9.0D) {
                                    // CraftBukkit start - Only apply knockback if the damage hits
                                    if (entityliving.damageEntity(DamageSource.playerAttack(this).sweep(), f4)) {
                                    entityliving.a(this, 0.4F, (double) MathHelper.sin(this.yaw * 0.017453292F), (double) (-MathHelper.cos(this.yaw * 0.017453292F)));
                                    }
                                    // CraftBukkit end
                                }
                            }

                            sendSoundEffect(this, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_PLAYER_ATTACK_SWEEP, this.getSoundCategory(), 1.0F, 1.0F);  // Paper - send while respecting visibility
                            this.dE();
                        }

                        if (entity instanceof EntityPlayer && entity.velocityChanged) {
                            // CraftBukkit start - Add Velocity Event
                            boolean cancelled = false;
                            Player player = (Player) entity.getBukkitEntity();
                            org.bukkit.util.Vector velocity = CraftVector.toBukkit(vec3d);

                            PlayerVelocityEvent event = new PlayerVelocityEvent(player, velocity.clone());
                            world.getServer().getPluginManager().callEvent(event);

                            if (event.isCancelled()) {
                                cancelled = true;
                            } else if (!velocity.equals(event.getVelocity())) {
                                player.setVelocity(event.getVelocity());
                            }

                            if (!cancelled) {
                            ((EntityPlayer) entity).playerConnection.sendPacket(new PacketPlayOutEntityVelocity(entity));
                            entity.velocityChanged = false;
                            entity.setMot(vec3d);
                            }
                            // CraftBukkit end
                        }

                        if (flag2) {
                            sendSoundEffect(this, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_PLAYER_ATTACK_CRIT, this.getSoundCategory(), 1.0F, 1.0F);  // Paper - send while respecting visibility
                            this.a(entity);
                        }

                        if (!flag2 && !flag3) {
                            if (flag) {
                                sendSoundEffect(this, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0F, 1.0F);  // Paper - send while respecting visibility
                            } else {
                                sendSoundEffect(this, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_PLAYER_ATTACK_WEAK, this.getSoundCategory(), 1.0F, 1.0F);  // Paper - send while respecting visibility
                            }
                        }

                        if (f1 > 0.0F) {
                            this.b(entity);
                        }

                        this.z(entity);
                        if (entity instanceof EntityLiving) {
                            EnchantmentManager.a((EntityLiving) entity, (Entity) this);
                        }

                        EnchantmentManager.b((EntityLiving) this, entity);
                        ItemStack itemstack1 = this.getItemInMainHand();
                        Object object = entity;

                        if (entity instanceof EntityComplexPart) {
                            object = ((EntityComplexPart) entity).owner;
                        }

                        if (!this.world.isClientSide && !itemstack1.isEmpty() && object instanceof EntityLiving) {
                            itemstack1.a((EntityLiving) object, this);
                            if (itemstack1.isEmpty()) {
                                this.a(EnumHand.MAIN_HAND, ItemStack.a);
                            }
                        }

                        if (entity instanceof EntityLiving) {
                            float f5 = f3 - ((EntityLiving) entity).getHealth();

                            this.a(StatisticList.DAMAGE_DEALT, Math.round(f5 * 10.0F));
                            if (j > 0) {
                                // CraftBukkit start - Call a combust event when somebody hits with a fire enchanted item
                                EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), j * 4);
                                org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);

                                if (!combustEvent.isCancelled()) {
                                    entity.setOnFire(combustEvent.getDuration(), false);
                                }
                                // CraftBukkit end
                            }

                            if (this.world instanceof WorldServer && f5 > 2.0F) {
                                int k = (int) ((double) f5 * 0.5D);

                                ((WorldServer) this.world).a(Particles.DAMAGE_INDICATOR, entity.locX, entity.locY + (double) (entity.getHeight() * 0.5F), entity.locZ, k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }

                        this.applyExhaustion(world.spigotConfig.combatExhaustion); // Spigot - Change to use configurable value
                    } else {
                        sendSoundEffect(this, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_PLAYER_ATTACK_NODAMAGE, this.getSoundCategory(), 1.0F, 1.0F); // Paper - send while respecting visibility
                        if (flag4) {
                            entity.extinguish();
                        }
                        // CraftBukkit start - resync on cancelled event
                        if (this instanceof EntityPlayer) {
                            ((EntityPlayer) this).getBukkitEntity().updateInventory();
                        }
                        // CraftBukkit end
                    }
                }

            }
        }
    }

    @Override
    protected void f(EntityLiving entityliving) {
        this.attack(entityliving);
    }

    public void o(boolean flag) {
        float f = 0.25F + (float) EnchantmentManager.getDigSpeedEnchantmentLevel(this) * 0.05F;

        if (flag) {
            f += 0.75F;
        }

        if (this.random.nextFloat() < f) {
            this.getCooldownTracker().setCooldown(Items.SHIELD, 100);
            this.dp();
            this.world.broadcastEntityEffect(this, (byte) 30);
        }

    }

    public void a(Entity entity) {}

    public void b(Entity entity) {}

    public void dE() {
        double d0 = (double) (-MathHelper.sin(this.yaw * 0.017453292F));
        double d1 = (double) MathHelper.cos(this.yaw * 0.017453292F);

        if (this.world instanceof WorldServer) {
            ((WorldServer) this.world).a(Particles.SWEEP_ATTACK, this.locX + d0, this.locY + (double) this.getHeight() * 0.5D, this.locZ + d1, 0, d0, 0.0D, d1, 0.0D);
        }

    }

    @Override
    public void die() {
        super.die();
        this.defaultContainer.b(this);
        if (this.activeContainer != null) {
            this.activeContainer.b(this);
        }

    }

    public boolean dG() {
        return false;
    }

    public GameProfile getProfile() {
        return this.bW;
    }

    // CraftBukkit start - moved bed result checks from below into separate method
    private Either<EntityHuman.EnumBedResult, Unit> getBedResult(BlockPosition blockposition, EnumDirection enumdirection) {
        if (!this.world.isClientSide) {
            if (this.isSleeping() || !this.isAlive()) {
                return Either.left(EntityHuman.EnumBedResult.OTHER_PROBLEM);
            }

            // CraftBukkit - moved world and biome check from BlockBed interact handling here
            if (!world.worldProvider.canRespawn() || world.getBiome(blockposition) == Biomes.NETHER || !this.world.worldProvider.isOverworld()) {
                return Either.left(EntityHuman.EnumBedResult.NOT_POSSIBLE_HERE);
            }

            if (this.world.J()) {
                return Either.left(EntityHuman.EnumBedResult.NOT_POSSIBLE_NOW);
            }

            if (!this.a(blockposition, enumdirection)) {
                return Either.left(EntityHuman.EnumBedResult.TOO_FAR_AWAY);
            }

            if (this.b(blockposition, enumdirection)) {
                return Either.left(EntityHuman.EnumBedResult.OBSTRUCTED);
            }

            if (!this.isCreative()) {
                double d0 = 8.0D;
                double d1 = 5.0D;
                List<EntityMonster> list = this.world.a(EntityMonster.class, new AxisAlignedBB((double) blockposition.getX() - 8.0D, (double) blockposition.getY() - 5.0D, (double) blockposition.getZ() - 8.0D, (double) blockposition.getX() + 8.0D, (double) blockposition.getY() + 5.0D, (double) blockposition.getZ() + 8.0D), (entitymonster) -> {
                    return entitymonster.e(this);
                });

                if (!list.isEmpty()) {
                    return Either.left(EntityHuman.EnumBedResult.NOT_SAFE);
                }
            }
        }
        return Either.right(Unit.INSTANCE);
    }

    public Either<EntityHuman.EnumBedResult, Unit> sleep(BlockPosition blockposition) {
        // CraftBukkit start - moved checks into separate method above, add force
        return this.sleep(blockposition, false);
    }

    public Either<EntityHuman.EnumBedResult, Unit> sleep(BlockPosition blockposition, boolean force) {
        EnumDirection enumdirection = (EnumDirection) this.world.getType(blockposition).get(BlockFacingHorizontal.FACING);
        Either<EntityHuman.EnumBedResult, Unit> bedResult = this.getBedResult(blockposition, enumdirection);

        if (bedResult.left().orElse(null) == EntityHuman.EnumBedResult.OTHER_PROBLEM) {
            return bedResult; // return immediately if the result is not bypassable by plugins
        }

        if (force) {
            bedResult = Either.right(Unit.INSTANCE);
        }

        if (this.getBukkitEntity() instanceof Player) {
            bedResult = org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerBedEnterEvent(this, blockposition, bedResult);

            if (bedResult.left().isPresent()) {
                return bedResult;
            }
        }
        // CraftBukkit end

        this.e(blockposition);
        this.sleepTicks = 0;
        if (this.world instanceof WorldServer) {
            ((WorldServer) this.world).everyoneSleeping();
        }

        return Either.right(Unit.INSTANCE);
    }

    @Override
    public void e(BlockPosition blockposition) {
        this.a(StatisticList.CUSTOM.b(StatisticList.TIME_SINCE_REST));
        super.e(blockposition);
    }

    private boolean a(BlockPosition blockposition, EnumDirection enumdirection) {
        if (Math.abs(this.locX - (double) blockposition.getX()) <= 3.0D && Math.abs(this.locY - (double) blockposition.getY()) <= 2.0D && Math.abs(this.locZ - (double) blockposition.getZ()) <= 3.0D) {
            return true;
        } else {
            BlockPosition blockposition1 = blockposition.shift(enumdirection.opposite());

            return Math.abs(this.locX - (double) blockposition1.getX()) <= 3.0D && Math.abs(this.locY - (double) blockposition1.getY()) <= 2.0D && Math.abs(this.locZ - (double) blockposition1.getZ()) <= 3.0D;
        }
    }

    private boolean b(BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition blockposition1 = blockposition.up();

        return !this.f(blockposition1) || !this.f(blockposition1.shift(enumdirection.opposite()));
    }

    public void wakeup(boolean flag, boolean flag1, boolean flag2) {
        Optional<BlockPosition> optional = this.getBedPosition();

        super.dy();
        if (this.world instanceof WorldServer && flag1) {
            ((WorldServer) this.world).everyoneSleeping();
        }

        // CraftBukkit start - fire PlayerBedLeaveEvent
        if (this.getBukkitEntity() instanceof Player) {
            Player player = (Player) this.getBukkitEntity();

            org.bukkit.block.Block bed;
            BlockPosition blockposition = optional.orElse(null);
            if (blockposition != null) {
                bed = this.world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
            } else {
                bed = this.world.getWorld().getBlockAt(player.getLocation());
            }

            PlayerBedLeaveEvent event = new PlayerBedLeaveEvent(player, bed, flag2);
            this.world.getServer().getPluginManager().callEvent(event);
            flag2 = event.shouldSetSpawnLocation();
        }
        // CraftBukkit end

        this.sleepTicks = flag ? 0 : 100;
        if (flag2) {
            optional.ifPresent((blockposition) -> {
                this.setRespawnPosition(blockposition, false);
            });
        }

    }

    @Override
    public void dy() {
        this.wakeup(true, true, false);
    }

    public static Optional<Vec3D> getBed(IWorldReader iworldreader, BlockPosition blockposition, boolean flag) {
        Block block = iworldreader.getType(blockposition).getBlock();

        if (!(block instanceof BlockBed)) {
            if (!flag) {
                return Optional.empty();
            } else {
                boolean flag1 = block.S_();
                boolean flag2 = iworldreader.getType(blockposition.up()).getBlock().S_();

                return flag1 && flag2 ? Optional.of(new Vec3D((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.1D, (double) blockposition.getZ() + 0.5D)) : Optional.empty();
            }
        } else {
            return BlockBed.a(EntityTypes.PLAYER, iworldreader, blockposition, 0);
        }
    }

    public boolean isDeeplySleeping() {
        return this.isSleeping() && this.sleepTicks >= 100;
    }

    public int dJ() {
        return this.sleepTicks;
    }

    public void a(IChatBaseComponent ichatbasecomponent, boolean flag) {}

    public BlockPosition getBed() {
        return this.g;
    }

    public boolean isRespawnForced() {
        return this.bU;
    }

    public void setRespawnPosition(BlockPosition blockposition, boolean flag) {
        if (blockposition != null) {
            this.g = blockposition;
            this.bU = flag;
            this.spawnWorld = this.world.worldData.getName(); // CraftBukkit
        } else {
            this.g = null;
            this.bU = false;
            this.spawnWorld = ""; // CraftBukkit
        }

    }

    public void a(MinecraftKey minecraftkey) {
        this.b(StatisticList.CUSTOM.b(minecraftkey));
    }

    public void a(MinecraftKey minecraftkey, int i) {
        this.a(StatisticList.CUSTOM.b(minecraftkey), i);
    }

    public void b(Statistic<?> statistic) {
        this.a(statistic, 1);
    }

    public void a(Statistic<?> statistic, int i) {}

    public void a(Statistic<?> statistic) {}

    public int discoverRecipes(Collection<IRecipe<?>> collection) {
        return 0;
    }

    public void a(MinecraftKey[] aminecraftkey) {}

    public int undiscoverRecipes(Collection<IRecipe<?>> collection) {
        return 0;
    }

    @Override
    public void jump() {
        super.jump();
        this.a(StatisticList.JUMP);
        if (this.isSprinting()) {
            this.applyExhaustion(world.spigotConfig.jumpSprintExhaustion); // Spigot - Change to use configurable value
        } else {
            this.applyExhaustion(world.spigotConfig.jumpWalkExhaustion); // Spigot - Change to use configurable value
        }

    }

    @Override
    public void e(Vec3D vec3d) {
        double d0 = this.locX;
        double d1 = this.locY;
        double d2 = this.locZ;
        double d3;

        if (this.isSwimming() && !this.isPassenger()) {
            d3 = this.getLookDirection().y;
            double d4 = d3 < -0.2D ? 0.085D : 0.06D;

            if (d3 <= 0.0D || this.jumping || !this.world.getType(new BlockPosition(this.locX, this.locY + 1.0D - 0.1D, this.locZ)).p().isEmpty()) {
                Vec3D vec3d1 = this.getMot();

                this.setMot(vec3d1.add(0.0D, (d3 - vec3d1.y) * d4, 0.0D));
            }
        }

        if (this.abilities.isFlying && !this.isPassenger()) {
            d3 = this.getMot().y;
            float f = this.aO;

            this.aO = this.abilities.a() * (float) (this.isSprinting() ? 2 : 1);
            super.e(vec3d);
            Vec3D vec3d2 = this.getMot();

            this.setMot(vec3d2.x, d3 * 0.6D, vec3d2.z);
            this.aO = f;
            this.fallDistance = 0.0F;
            // CraftBukkit start
            if (getFlag(7) && !org.bukkit.craftbukkit.event.CraftEventFactory.callToggleGlideEvent(this, false).isCancelled()) {
                this.setFlag(7, false);
            }
            // CraftBukkit end
        } else {
            super.e(vec3d);
        }

        this.checkMovement(this.locX - d0, this.locY - d1, this.locZ - d2);
    }

    @Override
    public void ax() {
        if (this.abilities.isFlying) {
            this.setSwimming(false);
        } else {
            super.ax();
        }

    }

    protected boolean f(BlockPosition blockposition) {
        return !this.world.getType(blockposition).m(this.world, blockposition);
    }

    @Override
    public float db() {
        return (float) this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();
    }

    public void checkMovement(double d0, double d1, double d2) {
        if (!this.isPassenger()) {
            int i;

            if (this.isSwimming()) {
                i = Math.round(MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.a(StatisticList.SWIM_ONE_CM, i);
                    this.applyExhaustion(0.01F * (float) i * 0.01F);
                }
            } else if (this.a(TagsFluid.WATER, true)) {
                i = Math.round(MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.a(StatisticList.WALK_UNDER_WATER_ONE_CM, i);
                    this.applyExhaustion(world.spigotConfig.swimMultiplier * (float) i * 0.01F); // Spigot
                }
            } else if (this.isInWater()) {
                i = Math.round(MathHelper.sqrt(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.a(StatisticList.WALK_ON_WATER_ONE_CM, i);
                    this.applyExhaustion(world.spigotConfig.swimMultiplier * (float) i * 0.01F); // Spigot
                }
            } else if (this.isClimbing()) {
                if (d1 > 0.0D) {
                    this.a(StatisticList.CLIMB_ONE_CM, (int) Math.round(d1 * 100.0D));
                }
            } else if (this.onGround) {
                i = Math.round(MathHelper.sqrt(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 0) {
                    if (this.isSprinting()) {
                        this.a(StatisticList.SPRINT_ONE_CM, i);
                        this.applyExhaustion(world.spigotConfig.sprintMultiplier * (float) i * 0.01F); // Spigot
                    } else if (this.isSneaking()) {
                        this.a(StatisticList.CROUCH_ONE_CM, i);
                        this.applyExhaustion(world.spigotConfig.otherMultiplier * (float) i * 0.01F); // Spigot
                    } else {
                        this.a(StatisticList.WALK_ONE_CM, i);
                        this.applyExhaustion(world.spigotConfig.otherMultiplier * (float) i * 0.01F); // Spigot
                    }
                }
            } else if (this.isGliding()) {
                i = Math.round(MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);
                this.a(StatisticList.AVIATE_ONE_CM, i);
            } else {
                i = Math.round(MathHelper.sqrt(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 25) {
                    this.a(StatisticList.FLY_ONE_CM, i);
                }
            }

        }
    }

    private void m(double d0, double d1, double d2) {
        if (this.isPassenger()) {
            int i = Math.round(MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);

            if (i > 0) {
                if (this.getVehicle() instanceof EntityMinecartAbstract) {
                    this.a(StatisticList.MINECART_ONE_CM, i);
                } else if (this.getVehicle() instanceof EntityBoat) {
                    this.a(StatisticList.BOAT_ONE_CM, i);
                } else if (this.getVehicle() instanceof EntityPig) {
                    this.a(StatisticList.PIG_ONE_CM, i);
                } else if (this.getVehicle() instanceof EntityHorseAbstract) {
                    this.a(StatisticList.HORSE_ONE_CM, i);
                }
            }
        }

    }

    @Override
    public void b(float f, float f1) {
        if (!this.abilities.canFly) {
            if (f >= 2.0F) {
                this.a(StatisticList.FALL_ONE_CM, (int) Math.round((double) f * 100.0D));
            }

            super.b(f, f1);
        }
    }

    @Override
    protected void az() {
        if (!this.isSpectator()) {
            super.az();
        }

    }

    @Override
    protected SoundEffect getSoundFall(int i) {
        return i > 4 ? SoundEffects.ENTITY_PLAYER_BIG_FALL : SoundEffects.ENTITY_PLAYER_SMALL_FALL;
    }

    @Override
    public void b(EntityLiving entityliving) {
        this.b(StatisticList.ENTITY_KILLED.b(entityliving.getEntityType()));
    }

    @Override
    public void a(IBlockData iblockdata, Vec3D vec3d) {
        if (!this.abilities.isFlying) {
            super.a(iblockdata, vec3d);
        }

    }

    public void giveExp(int i) {
        this.addScore(i);
        this.exp += (float) i / (float) this.getExpToLevel();
        this.expTotal = MathHelper.clamp(this.expTotal + i, 0, Integer.MAX_VALUE);

        while (this.exp < 0.0F) {
            float f = this.exp * (float) this.getExpToLevel();

            if (this.expLevel > 0) {
                this.levelDown(-1);
                this.exp = 1.0F + f / (float) this.getExpToLevel();
            } else {
                this.levelDown(-1);
                this.exp = 0.0F;
            }
        }

        while (this.exp >= 1.0F) {
            this.exp = (this.exp - 1.0F) * (float) this.getExpToLevel();
            this.levelDown(1);
            this.exp /= (float) this.getExpToLevel();
        }

    }

    public int dM() {
        return this.bR;
    }

    public void enchantDone(ItemStack itemstack, int i) {
        this.expLevel -= i;
        if (this.expLevel < 0) {
            this.expLevel = 0;
            this.exp = 0.0F;
            this.expTotal = 0;
        }

        this.bR = this.random.nextInt();
    }

    public void levelDown(int i) {
        this.expLevel += i;
        if (this.expLevel < 0) {
            this.expLevel = 0;
            this.exp = 0.0F;
            this.expTotal = 0;
        }

        if (i > 0 && this.expLevel % 5 == 0 && (float) this.bV < (float) this.ticksLived - 100.0F) {
            float f = this.expLevel > 30 ? 1.0F : (float) this.expLevel / 30.0F;

            this.world.playSound((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_PLAYER_LEVELUP, this.getSoundCategory(), f * 0.75F, 1.0F);
            this.bV = this.ticksLived;
        }

    }

    public int getExpToLevel() {
        return this.expLevel >= 30 ? 112 + (this.expLevel - 30) * 9 : (this.expLevel >= 15 ? 37 + (this.expLevel - 15) * 5 : 7 + this.expLevel * 2);
    }

    public void applyExhaustion(float f) {
        if (!this.abilities.isInvulnerable) {
            if (!this.world.isClientSide) {
                this.foodData.a(f);
            }

        }
    }

    public FoodMetaData getFoodData() {
        return this.foodData;
    }

    public boolean p(boolean flag) {
        return !this.abilities.isInvulnerable && (flag || this.foodData.c());
    }

    public boolean dP() {
        return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
    }

    public boolean dQ() {
        return this.abilities.mayBuild;
    }

    public boolean a(BlockPosition blockposition, EnumDirection enumdirection, ItemStack itemstack) {
        if (this.abilities.mayBuild) {
            return true;
        } else {
            BlockPosition blockposition1 = blockposition.shift(enumdirection.opposite());
            ShapeDetectorBlock shapedetectorblock = new ShapeDetectorBlock(this.world, blockposition1, false);

            return itemstack.b(this.world.t(), shapedetectorblock);
        }
    }

    @Override
    protected int getExpValue(EntityHuman entityhuman) {
        if (!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !this.isSpectator()) {
            int i = this.expLevel * 7;

            return i > 100 ? 100 : i;
        } else {
            return 0;
        }
    }

    @Override
    protected boolean alwaysGivesExp() {
        return true;
    }

    @Override
    protected boolean playStepSound() {
        return !this.abilities.isFlying;
    }

    public void updateAbilities() {}

    public void a(EnumGamemode enumgamemode) {}

    @Override
    public IChatBaseComponent getDisplayName() {
        return new ChatComponentText(this.bW.getName());
    }

    public InventoryEnderChest getEnderChest() {
        return this.enderChest;
    }

    @Override
    public ItemStack getEquipment(EnumItemSlot enumitemslot) {
        return enumitemslot == EnumItemSlot.MAINHAND ? this.inventory.getItemInHand() : (enumitemslot == EnumItemSlot.OFFHAND ? (ItemStack) this.inventory.extraSlots.get(0) : (enumitemslot.a() == EnumItemSlot.Function.ARMOR ? (ItemStack) this.inventory.armor.get(enumitemslot.b()) : ItemStack.a));
    }

    @Override
    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) {
        if (enumitemslot == EnumItemSlot.MAINHAND) {
            this.b(itemstack);
            this.inventory.items.set(this.inventory.itemInHandIndex, itemstack);
        } else if (enumitemslot == EnumItemSlot.OFFHAND) {
            this.b(itemstack);
            this.inventory.extraSlots.set(0, itemstack);
        } else if (enumitemslot.a() == EnumItemSlot.Function.ARMOR) {
            this.b(itemstack);
            this.inventory.armor.set(enumitemslot.b(), itemstack);
        }

    }

    public boolean g(ItemStack itemstack) {
        this.b(itemstack);
        return this.inventory.pickup(itemstack);
    }

    @Override
    public Iterable<ItemStack> aZ() {
        return Lists.newArrayList(new ItemStack[]{this.getItemInMainHand(), this.getItemInOffHand()});
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.inventory.armor;
    }

    public boolean g(NBTTagCompound nbttagcompound) {
        if (!this.isPassenger() && this.onGround && !this.isInWater()) {
            if (this.getShoulderEntityLeft().isEmpty()) {
                this.setShoulderEntityLeft(nbttagcompound);
                this.e = this.world.getTime();
                return true;
            } else if (this.getShoulderEntityRight().isEmpty()) {
                this.setShoulderEntityRight(nbttagcompound);
                this.e = this.world.getTime();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    protected void releaseShoulderEntities() {
        if (this.e + 20L < this.world.getTime()) {
            // CraftBukkit start
            if (this.spawnEntityFromShoulder(this.getShoulderEntityLeft())) {
                this.setShoulderEntityLeft(new NBTTagCompound());
            }
            if (this.spawnEntityFromShoulder(this.getShoulderEntityRight())) {
                this.setShoulderEntityRight(new NBTTagCompound());
            }
            // CraftBukkit end
        }

    }

    // Paper start
    public Entity releaseLeftShoulderEntity() {
        Entity entity = this.spawnEntityFromShoulder0(this.getShoulderEntityLeft());
        if (entity != null) {
            this.setShoulderEntityLeft(new NBTTagCompound());
        }
        return entity;
    }

    public Entity releaseRightShoulderEntity() {
        Entity entity = this.spawnEntityFromShoulder0(this.getShoulderEntityRight());
        if (entity != null) {
            this.setShoulderEntityRight(new NBTTagCompound());
        }
        return entity;
    }
    // Paper - maintain old signature
    private boolean spawnEntityFromShoulder(NBTTagCompound nbttagcompound) { // CraftBukkit void->boolean
        return spawnEntityFromShoulder0(nbttagcompound) != null;
    }

    // Paper - return entity
    private Entity spawnEntityFromShoulder0(@Nullable NBTTagCompound nbttagcompound) {
        if (!this.world.isClientSide && nbttagcompound != null && !nbttagcompound.isEmpty()) {
            return EntityTypes.a(nbttagcompound, this.world).map((entity) -> { // CraftBukkit
                if (entity instanceof EntityTameableAnimal) {
                    ((EntityTameableAnimal) entity).setOwnerUUID(this.uniqueID);
                }

                entity.setPosition(this.locX, this.locY + 0.699999988079071D, this.locZ);
                boolean addedToWorld = ((WorldServer) this.world).addEntitySerialized(entity, CreatureSpawnEvent.SpawnReason.SHOULDER_ENTITY); // CraftBukkit
                return addedToWorld ? entity : null;
            }).orElse(null); // CraftBukkit // Paper - false -> null
        }

        return null; // Paper - return null
    }
    // Paper end

    @Override
    public abstract boolean isSpectator();

    @Override
    public boolean isSwimming() {
        return !this.abilities.isFlying && !this.isSpectator() && super.isSwimming();
    }

    public abstract boolean isCreative();

    @Override
    public boolean bE() {
        return !this.abilities.isFlying;
    }

    public Scoreboard getScoreboard() {
        return this.world.getScoreboard();
    }

    @Override
    public IChatBaseComponent getScoreboardDisplayName() {
        IChatBaseComponent ichatbasecomponent = ScoreboardTeam.a(this.getScoreboardTeam(), this.getDisplayName());

        return this.c(ichatbasecomponent);
    }

    public IChatBaseComponent dU() {
        return (new ChatComponentText("")).addSibling(this.getDisplayName()).a(" (").a(this.bW.getId().toString()).a(")");
    }

    private IChatBaseComponent c(IChatBaseComponent ichatbasecomponent) {
        String s = this.getProfile().getName();

        return ichatbasecomponent.a((chatmodifier) -> {
            chatmodifier.setChatClickable(new ChatClickable(ChatClickable.EnumClickAction.SUGGEST_COMMAND, "/tell " + s + " ")).setChatHoverable(this.bK()).setInsertion(s);
        });
    }

    @Override
    public String getName() {
        return this.getProfile().getName();
    }

    @Override
    public float b(EntityPose entitypose, EntitySize entitysize) {
        switch (entitypose) {
            case SWIMMING:
            case FALL_FLYING:
            case SPIN_ATTACK:
                return 0.4F;
            case SNEAKING:
                return 1.27F;
            default:
                return 1.62F;
        }
    }

    @Override
    public void setAbsorptionHearts(float f) {
        if (f < 0.0F) {
            f = 0.0F;
        }

        this.getDataWatcher().set(EntityHuman.c, f);
    }

    @Override
    public float getAbsorptionHearts() {
        return (Float) this.getDataWatcher().get(EntityHuman.c);
    }

    public static UUID a(GameProfile gameprofile) {
        UUID uuid = gameprofile.getId();

        if (uuid == null) {
            uuid = getOfflineUUID(gameprofile.getName());
        }

        return uuid;
    }

    public static UUID getOfflineUUID(String s) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + s).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean a_(int i, ItemStack itemstack) {
        if (i >= 0 && i < this.inventory.items.size()) {
            this.inventory.setItem(i, itemstack);
            return true;
        } else {
            EnumItemSlot enumitemslot;

            if (i == 100 + EnumItemSlot.HEAD.b()) {
                enumitemslot = EnumItemSlot.HEAD;
            } else if (i == 100 + EnumItemSlot.CHEST.b()) {
                enumitemslot = EnumItemSlot.CHEST;
            } else if (i == 100 + EnumItemSlot.LEGS.b()) {
                enumitemslot = EnumItemSlot.LEGS;
            } else if (i == 100 + EnumItemSlot.FEET.b()) {
                enumitemslot = EnumItemSlot.FEET;
            } else {
                enumitemslot = null;
            }

            if (i == 98) {
                this.setSlot(EnumItemSlot.MAINHAND, itemstack);
                return true;
            } else if (i == 99) {
                this.setSlot(EnumItemSlot.OFFHAND, itemstack);
                return true;
            } else if (enumitemslot == null) {
                int j = i - 200;

                if (j >= 0 && j < this.enderChest.getSize()) {
                    this.enderChest.setItem(j, itemstack);
                    return true;
                } else {
                    return false;
                }
            } else {
                if (!itemstack.isEmpty()) {
                    if (!(itemstack.getItem() instanceof ItemArmor) && !(itemstack.getItem() instanceof ItemElytra)) {
                        if (enumitemslot != EnumItemSlot.HEAD) {
                            return false;
                        }
                    } else if (EntityInsentient.h(itemstack) != enumitemslot) {
                        return false;
                    }
                }

                this.inventory.setItem(enumitemslot.b() + this.inventory.items.size(), itemstack);
                return true;
            }
        }
    }

    @Override
    public EnumMainHand getMainHand() {
        return (Byte) this.datawatcher.get(EntityHuman.bu) == 0 ? EnumMainHand.LEFT : EnumMainHand.RIGHT;
    }

    public void a(EnumMainHand enummainhand) {
        this.datawatcher.set(EntityHuman.bu, (byte) (enummainhand == EnumMainHand.LEFT ? 0 : 1));
    }

    public NBTTagCompound getShoulderEntityLeft() {
        return (NBTTagCompound) this.datawatcher.get(EntityHuman.bv);
    }

    public void setShoulderEntityLeft(NBTTagCompound nbttagcompound) {
        this.datawatcher.set(EntityHuman.bv, nbttagcompound);
    }

    public NBTTagCompound getShoulderEntityRight() {
        return (NBTTagCompound) this.datawatcher.get(EntityHuman.bw);
    }

    public void setShoulderEntityRight(NBTTagCompound nbttagcompound) {
        this.datawatcher.set(EntityHuman.bw, nbttagcompound);
    }

    public float getCooldownPeriod() { return this.dY(); } // Paper - OBFHELPER
    public float dY() {
        return (float) (1.0D / this.getAttributeInstance(GenericAttributes.ATTACK_SPEED).getValue() * 20.0D);
    }

    public float getCooledAttackStrength(float adjustTicks) { return s(adjustTicks); } // Paper - OBFHELPER
    public float s(float f) {
        return MathHelper.a(((float) this.aD + f) / this.dY(), 0.0F, 1.0F);
    }

    public void resetCooldown() { this.dZ(); } // Paper - OBFHELPER
    public void dZ() {
        this.aD = 0;
    }

    public ItemCooldown getCooldownTracker() {
        return this.bZ;
    }

    public float eb() {
        return (float) this.getAttributeInstance(GenericAttributes.LUCK).getValue();
    }

    public boolean isCreativeAndOp() {
        return this.abilities.canInstantlyBuild && this.y() >= 2;
    }

    @Override
    public boolean e(ItemStack itemstack) {
        EnumItemSlot enumitemslot = EntityInsentient.h(itemstack);

        return this.getEquipment(enumitemslot).isEmpty();
    }

    @Override
    public EntitySize a(EntityPose entitypose) {
        return (EntitySize) EntityHuman.b.getOrDefault(entitypose, EntityHuman.bs);
    }

    // Paper start
    protected boolean tryReadyArrow(ItemStack bow, ItemStack itemstack) {
        return !(this instanceof EntityPlayer) ||
                new com.destroystokyo.paper.event.player.PlayerReadyArrowEvent(
                    ((EntityPlayer) this).getBukkitEntity(),
                    org.bukkit.craftbukkit.inventory.CraftItemStack.asCraftMirror(bow),
                    org.bukkit.craftbukkit.inventory.CraftItemStack.asCraftMirror(itemstack)
                ).callEvent();
        // Paper end
    }

    @Override
    public ItemStack f(ItemStack itemstack) {
        if (!(itemstack.getItem() instanceof ItemProjectileWeapon)) {
            return ItemStack.a;
        } else {
            Predicate<ItemStack> predicate = ((ItemProjectileWeapon) itemstack.getItem()).d();
            ItemStack itemstack1 = ItemProjectileWeapon.a((EntityLiving) this, predicate);

            if (!itemstack1.isEmpty()) {
                return itemstack1;
            } else {
                predicate = ((ItemProjectileWeapon) itemstack.getItem()).b();

                for (int i = 0; i < this.inventory.getSize(); ++i) {
                    ItemStack itemstack2 = this.inventory.getItem(i);

                    if (predicate.test(itemstack2) && tryReadyArrow(itemstack, itemstack2)) { // Paper
                        return itemstack2;
                    }
                }

                return this.abilities.canInstantlyBuild ? new ItemStack(Items.ARROW) : ItemStack.a;
            }
        }
    }

    @Override
    public ItemStack a(World world, ItemStack itemstack) {
        this.getFoodData().a(itemstack.getItem(), itemstack);
        this.b(StatisticList.ITEM_USED.b(itemstack.getItem()));
        world.playSound((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        if (this instanceof EntityPlayer) {
            CriterionTriggers.z.a((EntityPlayer) this, itemstack);
        }

        return super.a(world, itemstack);
    }

    public static enum EnumBedResult {

        NOT_POSSIBLE_HERE, NOT_POSSIBLE_NOW(new ChatMessage("block.minecraft.bed.no_sleep", new Object[0])), TOO_FAR_AWAY(new ChatMessage("block.minecraft.bed.too_far_away", new Object[0])), OBSTRUCTED(new ChatMessage("block.minecraft.bed.obstructed", new Object[0])), OTHER_PROBLEM, NOT_SAFE(new ChatMessage("block.minecraft.bed.not_safe", new Object[0]));

        @Nullable
        private final IChatBaseComponent g;

        private EnumBedResult() {
            this.g = null;
        }

        private EnumBedResult(IChatBaseComponent ichatbasecomponent) {
            this.g = ichatbasecomponent;
        }

        @Nullable
        public IChatBaseComponent a() {
            return this.g;
        }
    }
}
