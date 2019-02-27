package net.minecraft.server;

import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// CraftBukkit start
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftMerchantRecipe;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.entity.VillagerReplenishTradeEvent;
// CraftBukkit end

public class EntityVillager extends EntityAgeable implements NPC, IMerchant {

    private static final Logger bC = LogManager.getLogger();
    private static final DataWatcherObject<Integer> bD = DataWatcher.a(EntityVillager.class, DataWatcherRegistry.b);
    private int profession;
    private boolean bF;
    private boolean bG;
    private Village village;
    @Nullable
    private EntityHuman tradingPlayer;
    @Nullable
    public MerchantRecipeList trades;
    private int bK;
    private boolean bL;
    private boolean bM;
    public int riches;
    private String bO;
    public int careerId;
    public int careerLevel;
    private boolean bR;
    private boolean bS;
    public final InventorySubcontainer inventory;
    private static final EntityVillager.IMerchantRecipeOption[][][][] bU = new EntityVillager.IMerchantRecipeOption[][][][] { { { { new EntityVillager.MerchantRecipeOptionBuy(Items.WHEAT, new EntityVillager.MerchantOptionRandomRange(18, 22)), new EntityVillager.MerchantRecipeOptionBuy(Items.POTATO, new EntityVillager.MerchantOptionRandomRange(15, 19)), new EntityVillager.MerchantRecipeOptionBuy(Items.CARROT, new EntityVillager.MerchantOptionRandomRange(15, 19)), new EntityVillager.MerchantRecipeOptionSell(Items.BREAD, new EntityVillager.MerchantOptionRandomRange(-4, -2))}, { new EntityVillager.MerchantRecipeOptionBuy(Blocks.PUMPKIN, new EntityVillager.MerchantOptionRandomRange(8, 13)), new EntityVillager.MerchantRecipeOptionSell(Items.PUMPKIN_PIE, new EntityVillager.MerchantOptionRandomRange(-3, -2))}, { new EntityVillager.MerchantRecipeOptionBuy(Blocks.MELON, new EntityVillager.MerchantOptionRandomRange(7, 12)), new EntityVillager.MerchantRecipeOptionSell(Items.APPLE, new EntityVillager.MerchantOptionRandomRange(-7, -5))}, { new EntityVillager.MerchantRecipeOptionSell(Items.COOKIE, new EntityVillager.MerchantOptionRandomRange(-10, -6)), new EntityVillager.MerchantRecipeOptionSell(Blocks.CAKE, new EntityVillager.MerchantOptionRandomRange(1, 1))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Items.STRING, new EntityVillager.MerchantOptionRandomRange(15, 20)), new EntityVillager.MerchantRecipeOptionBuy(Items.COAL, new EntityVillager.MerchantOptionRandomRange(16, 24)), new EntityVillager.MerchantRecipeOptionProcess(Items.COD, new EntityVillager.MerchantOptionRandomRange(6, 6), Items.COOKED_COD, new EntityVillager.MerchantOptionRandomRange(6, 6)), new EntityVillager.MerchantRecipeOptionProcess(Items.SALMON, new EntityVillager.MerchantOptionRandomRange(6, 6), Items.COOKED_SALMON, new EntityVillager.MerchantOptionRandomRange(6, 6))}, { new EntityVillager.MerchantRecipeOptionEnchant(Items.FISHING_ROD, new EntityVillager.MerchantOptionRandomRange(7, 8))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Blocks.WHITE_WOOL, new EntityVillager.MerchantOptionRandomRange(16, 22)), new EntityVillager.MerchantRecipeOptionSell(Items.SHEARS, new EntityVillager.MerchantOptionRandomRange(3, 4))}, { new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Blocks.WHITE_WOOL), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Blocks.ORANGE_WOOL), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Blocks.MAGENTA_WOOL), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Blocks.LIGHT_BLUE_WOOL), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Blocks.YELLOW_WOOL), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Blocks.LIME_WOOL), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Blocks.PINK_WOOL), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Blocks.GRAY_WOOL), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Blocks.LIGHT_GRAY_WOOL), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Blocks.CYAN_WOOL), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Blocks.PURPLE_WOOL), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Blocks.BLUE_WOOL), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Blocks.BROWN_WOOL), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Blocks.GREEN_WOOL), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Blocks.RED_WOOL), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Blocks.BLACK_WOOL), new EntityVillager.MerchantOptionRandomRange(1, 2))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Items.STRING, new EntityVillager.MerchantOptionRandomRange(15, 20)), new EntityVillager.MerchantRecipeOptionSell(Items.ARROW, new EntityVillager.MerchantOptionRandomRange(-12, -8))}, { new EntityVillager.MerchantRecipeOptionSell(Items.BOW, new EntityVillager.MerchantOptionRandomRange(2, 3)), new EntityVillager.MerchantRecipeOptionProcess(Blocks.GRAVEL, new EntityVillager.MerchantOptionRandomRange(10, 10), Items.FLINT, new EntityVillager.MerchantOptionRandomRange(6, 10))}}}, { { { new EntityVillager.MerchantRecipeOptionBuy(Items.PAPER, new EntityVillager.MerchantOptionRandomRange(24, 36)), new EntityVillager.MerchantRecipeOptionBook()}, { new EntityVillager.MerchantRecipeOptionBuy(Items.BOOK, new EntityVillager.MerchantOptionRandomRange(8, 10)), new EntityVillager.MerchantRecipeOptionSell(Items.COMPASS, new EntityVillager.MerchantOptionRandomRange(10, 12)), new EntityVillager.MerchantRecipeOptionSell(Blocks.BOOKSHELF, new EntityVillager.MerchantOptionRandomRange(3, 4))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.WRITTEN_BOOK, new EntityVillager.MerchantOptionRandomRange(2, 2)), new EntityVillager.MerchantRecipeOptionSell(Items.CLOCK, new EntityVillager.MerchantOptionRandomRange(10, 12)), new EntityVillager.MerchantRecipeOptionSell(Blocks.GLASS, new EntityVillager.MerchantOptionRandomRange(-5, -3))}, { new EntityVillager.MerchantRecipeOptionBook()}, { new EntityVillager.MerchantRecipeOptionBook()}, { new EntityVillager.MerchantRecipeOptionSell(Items.NAME_TAG, new EntityVillager.MerchantOptionRandomRange(20, 22))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Items.PAPER, new EntityVillager.MerchantOptionRandomRange(24, 36))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.COMPASS, new EntityVillager.MerchantOptionRandomRange(1, 1))}, { new EntityVillager.MerchantRecipeOptionSell(Items.MAP, new EntityVillager.MerchantOptionRandomRange(7, 11))}, { new EntityVillager.h(new EntityVillager.MerchantOptionRandomRange(12, 20), "Monument", MapIcon.Type.MONUMENT), new EntityVillager.h(new EntityVillager.MerchantOptionRandomRange(16, 28), "Mansion", MapIcon.Type.MANSION)}}}, { { { new EntityVillager.MerchantRecipeOptionBuy(Items.ROTTEN_FLESH, new EntityVillager.MerchantOptionRandomRange(36, 40)), new EntityVillager.MerchantRecipeOptionBuy(Items.GOLD_INGOT, new EntityVillager.MerchantOptionRandomRange(8, 10))}, { new EntityVillager.MerchantRecipeOptionSell(Items.REDSTONE, new EntityVillager.MerchantOptionRandomRange(-4, -1)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Items.LAPIS_LAZULI), new EntityVillager.MerchantOptionRandomRange(-2, -1))}, { new EntityVillager.MerchantRecipeOptionSell(Items.ENDER_PEARL, new EntityVillager.MerchantOptionRandomRange(4, 7)), new EntityVillager.MerchantRecipeOptionSell(Blocks.GLOWSTONE, new EntityVillager.MerchantOptionRandomRange(-3, -1))}, { new EntityVillager.MerchantRecipeOptionSell(Items.EXPERIENCE_BOTTLE, new EntityVillager.MerchantOptionRandomRange(3, 11))}}}, { { { new EntityVillager.MerchantRecipeOptionBuy(Items.COAL, new EntityVillager.MerchantOptionRandomRange(16, 24)), new EntityVillager.MerchantRecipeOptionSell(Items.IRON_HELMET, new EntityVillager.MerchantOptionRandomRange(4, 6))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.IRON_INGOT, new EntityVillager.MerchantOptionRandomRange(7, 9)), new EntityVillager.MerchantRecipeOptionSell(Items.IRON_CHESTPLATE, new EntityVillager.MerchantOptionRandomRange(10, 14))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.DIAMOND, new EntityVillager.MerchantOptionRandomRange(3, 4)), new EntityVillager.MerchantRecipeOptionEnchant(Items.DIAMOND_CHESTPLATE, new EntityVillager.MerchantOptionRandomRange(16, 19))}, { new EntityVillager.MerchantRecipeOptionSell(Items.CHAINMAIL_BOOTS, new EntityVillager.MerchantOptionRandomRange(5, 7)), new EntityVillager.MerchantRecipeOptionSell(Items.CHAINMAIL_LEGGINGS, new EntityVillager.MerchantOptionRandomRange(9, 11)), new EntityVillager.MerchantRecipeOptionSell(Items.CHAINMAIL_HELMET, new EntityVillager.MerchantOptionRandomRange(5, 7)), new EntityVillager.MerchantRecipeOptionSell(Items.CHAINMAIL_CHESTPLATE, new EntityVillager.MerchantOptionRandomRange(11, 15))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Items.COAL, new EntityVillager.MerchantOptionRandomRange(16, 24)), new EntityVillager.MerchantRecipeOptionSell(Items.IRON_AXE, new EntityVillager.MerchantOptionRandomRange(6, 8))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.IRON_INGOT, new EntityVillager.MerchantOptionRandomRange(7, 9)), new EntityVillager.MerchantRecipeOptionEnchant(Items.IRON_SWORD, new EntityVillager.MerchantOptionRandomRange(9, 10))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.DIAMOND, new EntityVillager.MerchantOptionRandomRange(3, 4)), new EntityVillager.MerchantRecipeOptionEnchant(Items.DIAMOND_SWORD, new EntityVillager.MerchantOptionRandomRange(12, 15)), new EntityVillager.MerchantRecipeOptionEnchant(Items.DIAMOND_AXE, new EntityVillager.MerchantOptionRandomRange(9, 12))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Items.COAL, new EntityVillager.MerchantOptionRandomRange(16, 24)), new EntityVillager.MerchantRecipeOptionEnchant(Items.IRON_SHOVEL, new EntityVillager.MerchantOptionRandomRange(5, 7))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.IRON_INGOT, new EntityVillager.MerchantOptionRandomRange(7, 9)), new EntityVillager.MerchantRecipeOptionEnchant(Items.IRON_PICKAXE, new EntityVillager.MerchantOptionRandomRange(9, 11))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.DIAMOND, new EntityVillager.MerchantOptionRandomRange(3, 4)), new EntityVillager.MerchantRecipeOptionEnchant(Items.DIAMOND_PICKAXE, new EntityVillager.MerchantOptionRandomRange(12, 15))}}}, { { { new EntityVillager.MerchantRecipeOptionBuy(Items.PORKCHOP, new EntityVillager.MerchantOptionRandomRange(14, 18)), new EntityVillager.MerchantRecipeOptionBuy(Items.CHICKEN, new EntityVillager.MerchantOptionRandomRange(14, 18))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.COAL, new EntityVillager.MerchantOptionRandomRange(16, 24)), new EntityVillager.MerchantRecipeOptionSell(Items.COOKED_PORKCHOP, new EntityVillager.MerchantOptionRandomRange(-7, -5)), new EntityVillager.MerchantRecipeOptionSell(Items.COOKED_CHICKEN, new EntityVillager.MerchantOptionRandomRange(-8, -6))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Items.LEATHER, new EntityVillager.MerchantOptionRandomRange(9, 12)), new EntityVillager.MerchantRecipeOptionSell(Items.LEATHER_LEGGINGS, new EntityVillager.MerchantOptionRandomRange(2, 4))}, { new EntityVillager.MerchantRecipeOptionEnchant(Items.LEATHER_CHESTPLATE, new EntityVillager.MerchantOptionRandomRange(7, 12))}, { new EntityVillager.MerchantRecipeOptionSell(Items.SADDLE, new EntityVillager.MerchantOptionRandomRange(8, 10))}}}, { new EntityVillager.IMerchantRecipeOption[0][]}};

    public EntityVillager(World world) {
        this(world, 0);
    }

    public EntityVillager(World world, int i) {
        super(EntityTypes.VILLAGER, world);
        this.inventory = new InventorySubcontainer(new ChatComponentText("Items"), 8, (CraftVillager) this.getBukkitEntity()); // CraftBukkit add argument
        this.setProfession(i);
        this.setSize(0.6F, 1.95F);
        ((Navigation) this.getNavigation()).a(true);
        this.p(true);
    }

    protected void n() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityEvoker.class, 12.0F, 0.8D, 0.8D));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityVindicator.class, 8.0F, 0.8D, 0.8D));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityVex.class, 8.0F, 0.6D, 0.6D));
        this.goalSelector.a(1, new PathfinderGoalTradeWithPlayer(this));
        this.goalSelector.a(1, new PathfinderGoalLookAtTradingPlayer(this));
        this.goalSelector.a(2, new PathfinderGoalMoveIndoors(this));
        this.goalSelector.a(3, new PathfinderGoalRestrictOpenDoor(this));
        this.goalSelector.a(4, new PathfinderGoalOpenDoor(this, true));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 0.6D));
        this.goalSelector.a(6, new PathfinderGoalMakeLove(this));
        this.goalSelector.a(7, new PathfinderGoalTakeFlower(this));
        this.goalSelector.a(9, new PathfinderGoalInteract(this, EntityHuman.class, 3.0F, 1.0F));
        this.goalSelector.a(9, new PathfinderGoalInteractVillagers(this));
        this.goalSelector.a(9, new PathfinderGoalRandomStrollLand(this, 0.6D));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
    }

    private void dJ() {
        if (!this.bS) {
            this.bS = true;
            if (this.isBaby()) {
                this.goalSelector.a(8, new PathfinderGoalPlay(this, 0.32D));
            } else if (this.getProfession() == 0) {
                this.goalSelector.a(6, new PathfinderGoalVillagerFarm(this, 0.6D));
            }

        }
    }

    protected void l() {
        if (this.getProfession() == 0) {
            this.goalSelector.a(8, new PathfinderGoalVillagerFarm(this, 0.6D));
        }

        super.l();
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.5D);
    }

    protected void mobTick() {
        if (--this.profession <= 0) {
            BlockPosition blockposition = new BlockPosition(this);

            this.world.af().a(blockposition);
            this.profession = 70 + this.random.nextInt(50);
            this.village = this.world.af().getClosestVillage(blockposition, 32);
            if (this.village == null) {
                this.dv();
            } else {
                BlockPosition blockposition1 = this.village.a();

                this.a(blockposition1, this.village.b());
                if (this.bR) {
                    this.bR = false;
                    this.village.b(5);
                }
            }
        }

        if (!this.dB() && this.bK > 0) {
            --this.bK;
            if (this.bK <= 0) {
                if (this.bL) {
                    Iterator iterator = this.trades.iterator();

                    while (iterator.hasNext()) {
                        MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

                        if (merchantrecipe.h()) {
                            // CraftBukkit start
                            int bonus = this.random.nextInt(6) + this.random.nextInt(6) + 2;
                            VillagerReplenishTradeEvent event = new VillagerReplenishTradeEvent((Villager) this.getBukkitEntity(), merchantrecipe.asBukkit(), bonus);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                merchantrecipe.a(event.getBonus());
                            }
                            // CraftBukkit end
                        }
                    }

                    this.populateTrades();
                    this.bL = false;
                    if (this.village != null && this.bO != null) {
                        this.world.broadcastEntityEffect(this, (byte) 14);
                        this.village.a(this.bO, 1);
                    }
                }

                this.addEffect(new MobEffect(MobEffects.REGENERATION, 200, 0), org.bukkit.event.entity.EntityPotionEffectEvent.Cause.VILLAGER_TRADE); // CraftBukkit
            }
        }

        super.mobTick();
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        boolean flag = itemstack.getItem() == Items.NAME_TAG;

        if (flag) {
            itemstack.a(entityhuman, (EntityLiving) this, enumhand);
            return true;
        } else if (itemstack.getItem() != Items.VILLAGER_SPAWN_EGG && this.isAlive() && !this.dB() && !this.isBaby()) {
            if (this.trades == null) {
                this.populateTrades();
            }

            if (enumhand == EnumHand.MAIN_HAND) {
                entityhuman.a(StatisticList.TALKED_TO_VILLAGER);
            }

            if (!this.world.isClientSide && !this.trades.isEmpty()) {
                this.setTradingPlayer(entityhuman);
                entityhuman.openTrade(this);
            } else if (this.trades.isEmpty()) {
                return super.a(entityhuman, enumhand);
            }

            return true;
        } else {
            return super.a(entityhuman, enumhand);
        }
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityVillager.bD, 0);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Profession", this.getProfession());
        nbttagcompound.setInt("Riches", this.riches);
        nbttagcompound.setInt("Career", this.careerId);
        nbttagcompound.setInt("CareerLevel", this.careerLevel);
        nbttagcompound.setBoolean("Willing", this.bM);
        if (this.trades != null) {
            nbttagcompound.set("Offers", this.trades.a());
        }

        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.inventory.getSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);

            if (!itemstack.isEmpty()) {
                nbttaglist.add((NBTBase) itemstack.save(new NBTTagCompound()));
            }
        }

        nbttagcompound.set("Inventory", nbttaglist);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setProfession(nbttagcompound.getInt("Profession"));
        this.riches = nbttagcompound.getInt("Riches");
        this.careerId = nbttagcompound.getInt("Career");
        this.careerLevel = nbttagcompound.getInt("CareerLevel");
        this.bM = nbttagcompound.getBoolean("Willing");
        if (nbttagcompound.hasKeyOfType("Offers", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Offers");

            this.trades = new MerchantRecipeList(nbttagcompound1);
        }

        NBTTagList nbttaglist = nbttagcompound.getList("Inventory", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            ItemStack itemstack = ItemStack.a(nbttaglist.getCompound(i));

            if (!itemstack.isEmpty()) {
                this.inventory.a(itemstack);
            }
        }

        this.p(true);
        this.dJ();
    }

    public boolean isTypeNotPersistent() {
        return false;
    }

    protected SoundEffect D() {
        return this.dB() ? SoundEffects.ENTITY_VILLAGER_TRADE : SoundEffects.ENTITY_VILLAGER_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_VILLAGER_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_VILLAGER_DEATH;
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.aA;
    }

    public void setProfession(int i) {
        this.datawatcher.set(EntityVillager.bD, i);
    }

    public int getProfession() {
        return Math.max((Integer) this.datawatcher.get(EntityVillager.bD) % 6, 0);
    }

    public boolean isInLove() {
        return this.bF;
    }

    public void s(boolean flag) {
        this.bF = flag;
    }

    public void t(boolean flag) {
        this.bG = flag;
    }

    public boolean dA() {
        return this.bG;
    }

    public void setLastDamager(@Nullable EntityLiving entityliving) {
        super.setLastDamager(entityliving);
        if (this.village != null && entityliving != null) {
            this.village.a(entityliving);
            if (entityliving instanceof EntityHuman) {
                byte b0 = -1;

                if (this.isBaby()) {
                    b0 = -3;
                }

                this.village.a(((EntityHuman) entityliving).getProfile().getName(), b0);
                if (this.isAlive()) {
                    this.world.broadcastEntityEffect(this, (byte) 13);
                }
            }
        }

    }

    public void die(DamageSource damagesource) {
        if (this.village != null) {
            Entity entity = damagesource.getEntity();

            if (entity != null) {
                if (entity instanceof EntityHuman) {
                    this.village.a(((EntityHuman) entity).getProfile().getName(), -2);
                } else if (entity instanceof IMonster) {
                    this.village.h();
                }
            } else {
                EntityHuman entityhuman = this.world.findNearbyPlayer(this, 16.0D);

                if (entityhuman != null) {
                    this.village.h();
                }
            }
        }

        super.die(damagesource);
    }

    public void setTradingPlayer(@Nullable EntityHuman entityhuman) {
        this.tradingPlayer = entityhuman;
    }

    @Nullable
    public EntityHuman getTrader() {
        return this.tradingPlayer;
    }

    public boolean dB() {
        return this.tradingPlayer != null;
    }

    public boolean u(boolean flag) {
        if (!this.bM && flag && this.dE()) {
            boolean flag1 = false;

            for (int i = 0; i < this.inventory.getSize(); ++i) {
                ItemStack itemstack = this.inventory.getItem(i);

                if (!itemstack.isEmpty()) {
                    if (itemstack.getItem() == Items.BREAD && itemstack.getCount() >= 3) {
                        flag1 = true;
                        this.inventory.splitStack(i, 3);
                    } else if ((itemstack.getItem() == Items.POTATO || itemstack.getItem() == Items.CARROT) && itemstack.getCount() >= 12) {
                        flag1 = true;
                        this.inventory.splitStack(i, 12);
                    }
                }

                if (flag1) {
                    this.world.broadcastEntityEffect(this, (byte) 18);
                    this.bM = true;
                    break;
                }
            }
        }

        return this.bM;
    }

    public void v(boolean flag) {
        this.bM = flag;
    }

    public void a(MerchantRecipe merchantrecipe) {
        merchantrecipe.increaseUses();
        this.a_ = -this.z();
        this.a(SoundEffects.ENTITY_VILLAGER_YES, this.cD(), this.cE());
        int i = 3 + this.random.nextInt(4);

        if (merchantrecipe.e() == 1 || this.random.nextInt(5) == 0) {
            this.bK = 40;
            this.bL = true;
            this.bM = true;
            if (this.tradingPlayer != null) {
                this.bO = this.tradingPlayer.getProfile().getName();
            } else {
                this.bO = null;
            }

            i += 5;
        }

        if (merchantrecipe.getBuyItem1().getItem() == Items.EMERALD) {
            this.riches += merchantrecipe.getBuyItem1().getCount();
        }

        if (merchantrecipe.j()) {
            this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY + 0.5D, this.locZ, i));
        }

        if (this.tradingPlayer instanceof EntityPlayer) {
            CriterionTriggers.s.a((EntityPlayer) this.tradingPlayer, this, merchantrecipe.getBuyItem3());
        }

    }

    public void a(ItemStack itemstack) {
        if (!this.world.isClientSide && this.a_ > -this.z() + 20) {
            this.a_ = -this.z();
            this.a(itemstack.isEmpty() ? SoundEffects.ENTITY_VILLAGER_NO : SoundEffects.ENTITY_VILLAGER_YES, this.cD(), this.cE());
        }

    }

    @Nullable
    public MerchantRecipeList getOffers(EntityHuman entityhuman) {
        if (this.trades == null) {
            this.populateTrades();
        }

        return this.trades;
    }

    public void populateTrades() {
        EntityVillager.IMerchantRecipeOption[][][] aentityvillager_imerchantrecipeoption = EntityVillager.bU[this.getProfession()];

        if (this.careerId != 0 && this.careerLevel != 0) {
            ++this.careerLevel;
        } else {
            this.careerId = this.random.nextInt(aentityvillager_imerchantrecipeoption.length) + 1;
            this.careerLevel = 1;
        }

        if (this.trades == null) {
            this.trades = new MerchantRecipeList();
        }

        int i = this.careerId - 1;
        int j = this.careerLevel - 1;

        if (i >= 0 && i < aentityvillager_imerchantrecipeoption.length) {
            EntityVillager.IMerchantRecipeOption[][] aentityvillager_imerchantrecipeoption1 = aentityvillager_imerchantrecipeoption[i];

            if (j >= 0 && j < aentityvillager_imerchantrecipeoption1.length) {
                EntityVillager.IMerchantRecipeOption[] aentityvillager_imerchantrecipeoption2 = aentityvillager_imerchantrecipeoption1[j];
                EntityVillager.IMerchantRecipeOption[] aentityvillager_imerchantrecipeoption3 = aentityvillager_imerchantrecipeoption2;
                int k = aentityvillager_imerchantrecipeoption2.length;

                for (int l = 0; l < k; ++l) {
                    EntityVillager.IMerchantRecipeOption entityvillager_imerchantrecipeoption = aentityvillager_imerchantrecipeoption3[l];

                    // CraftBukkit start
                    // this is a hack. this must be done because otherwise, if
                    // mojang adds a new type of villager merchant option, it will need to
                    // have event handling added manually. this is better than having to do that.
                    MerchantRecipeList list = new MerchantRecipeList();
                    entityvillager_imerchantrecipeoption.a(this, list, this.random);
                    for (MerchantRecipe recipe : list) {
                        VillagerAcquireTradeEvent event = new VillagerAcquireTradeEvent((Villager) getBukkitEntity(), recipe.asBukkit());
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            this.trades.add(CraftMerchantRecipe.fromBukkit(event.getRecipe()).toMinecraft());
                        }
                    }
                    // CraftBukkit end
                }
            }

        }
    }

    public World getWorld() {
        return this.world;
    }

    public BlockPosition getPosition() {
        return new BlockPosition(this);
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        ScoreboardTeamBase scoreboardteambase = this.getScoreboardTeam();
        IChatBaseComponent ichatbasecomponent = this.getCustomName();

        if (ichatbasecomponent != null) {
            return ScoreboardTeam.a(scoreboardteambase, ichatbasecomponent).a((chatmodifier) -> {
                chatmodifier.setChatHoverable(this.bC()).setInsertion(this.bu());
            });
        } else {
            if (this.trades == null) {
                this.populateTrades();
            }

            String s = null;

            switch (this.getProfession()) {
            case 0:
                if (this.careerId == 1) {
                    s = "farmer";
                } else if (this.careerId == 2) {
                    s = "fisherman";
                } else if (this.careerId == 3) {
                    s = "shepherd";
                } else if (this.careerId == 4) {
                    s = "fletcher";
                }
                break;
            case 1:
                if (this.careerId == 1) {
                    s = "librarian";
                } else if (this.careerId == 2) {
                    s = "cartographer";
                }
                break;
            case 2:
                s = "cleric";
                break;
            case 3:
                if (this.careerId == 1) {
                    s = "armorer";
                } else if (this.careerId == 2) {
                    s = "weapon_smith";
                } else if (this.careerId == 3) {
                    s = "tool_smith";
                }
                break;
            case 4:
                if (this.careerId == 1) {
                    s = "butcher";
                } else if (this.careerId == 2) {
                    s = "leatherworker";
                }
                break;
            case 5:
                s = "nitwit";
            }

            if (s != null) {
                IChatBaseComponent ichatbasecomponent1 = (new ChatMessage(this.P().d() + '.' + s, new Object[0])).a((chatmodifier) -> {
                    chatmodifier.setChatHoverable(this.bC()).setInsertion(this.bu());
                });

                if (scoreboardteambase != null) {
                    ichatbasecomponent1.a(scoreboardteambase.getColor());
                }

                return ichatbasecomponent1;
            } else {
                return super.getScoreboardDisplayName();
            }
        }
    }

    public float getHeadHeight() {
        return this.isBaby() ? 0.81F : 1.62F;
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        return this.a(difficultydamagescaler, groupdataentity, nbttagcompound, true);
    }

    public GroupDataEntity a(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound, boolean flag) {
        groupdataentity = super.prepare(difficultydamagescaler, groupdataentity, nbttagcompound);
        if (flag) {
            this.setProfession(this.world.random.nextInt(6));
        }

        this.dJ();
        this.populateTrades();
        return groupdataentity;
    }

    public void dC() {
        this.bR = true;
    }

    public EntityVillager createChild(EntityAgeable entityageable) {
        EntityVillager entityvillager = new EntityVillager(this.world);

        entityvillager.prepare(this.world.getDamageScaler(new BlockPosition(entityvillager)), (GroupDataEntity) null, (NBTTagCompound) null);
        return entityvillager;
    }

    public boolean a(EntityHuman entityhuman) {
        return false;
    }

    public void onLightningStrike(EntityLightning entitylightning) {
        if (!this.world.isClientSide && !this.dead) {
            EntityWitch entitywitch = new EntityWitch(this.world);

            entitywitch.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            entitywitch.prepare(this.world.getDamageScaler(new BlockPosition(entitywitch)), (GroupDataEntity) null, (NBTTagCompound) null);
            entitywitch.setNoAI(this.isNoAI());
            if (this.hasCustomName()) {
                entitywitch.setCustomName(this.getCustomName());
                entitywitch.setCustomNameVisible(this.getCustomNameVisible());
            }

            // CraftBukkit start
            if (CraftEventFactory.callEntityTransformEvent(this, entitywitch, EntityTransformEvent.TransformReason.LIGHTNING).isCancelled()) {
                return;
            }
            this.world.addEntity(entitywitch, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.LIGHTNING);
            // CraftBukkit end
            this.die();
        }
    }

    public InventorySubcontainer dD() {
        return this.inventory;
    }

    protected void a(EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItemStack();
        Item item = itemstack.getItem();

        if (this.a(item)) {
            ItemStack itemstack1 = this.inventory.a(itemstack);

            if (itemstack1.isEmpty()) {
                entityitem.die();
            } else {
                itemstack.setCount(itemstack1.getCount());
            }
        }

    }

    private boolean a(Item item) {
        return item == Items.BREAD || item == Items.POTATO || item == Items.CARROT || item == Items.WHEAT || item == Items.WHEAT_SEEDS || item == Items.BEETROOT || item == Items.BEETROOT_SEEDS;
    }

    public boolean dE() {
        return this.p(1);
    }

    public boolean dF() {
        return this.p(2);
    }

    public boolean dG() {
        boolean flag = this.getProfession() == 0;

        return flag ? !this.p(5) : !this.p(1);
    }

    private boolean p(int i) {
        boolean flag = this.getProfession() == 0;

        for (int j = 0; j < this.inventory.getSize(); ++j) {
            ItemStack itemstack = this.inventory.getItem(j);
            Item item = itemstack.getItem();
            int k = itemstack.getCount();

            if (item == Items.BREAD && k >= 3 * i || item == Items.POTATO && k >= 12 * i || item == Items.CARROT && k >= 12 * i || item == Items.BEETROOT && k >= 12 * i) {
                return true;
            }

            if (flag && item == Items.WHEAT && k >= 9 * i) {
                return true;
            }
        }

        return false;
    }

    public boolean dH() {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            Item item = this.inventory.getItem(i).getItem();

            if (item == Items.WHEAT_SEEDS || item == Items.POTATO || item == Items.CARROT || item == Items.BEETROOT_SEEDS) {
                return true;
            }
        }

        return false;
    }

    public boolean c(int i, ItemStack itemstack) {
        if (super.c(i, itemstack)) {
            return true;
        } else {
            int j = i - 300;

            if (j >= 0 && j < this.inventory.getSize()) {
                this.inventory.setItem(j, itemstack);
                return true;
            } else {
                return false;
            }
        }
    }

    static class MerchantRecipeOptionProcess implements EntityVillager.IMerchantRecipeOption {

        public ItemStack a;
        public EntityVillager.MerchantOptionRandomRange b;
        public ItemStack c;
        public EntityVillager.MerchantOptionRandomRange d;

        public MerchantRecipeOptionProcess(IMaterial imaterial, EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange, Item item, EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange1) {
            this.a = new ItemStack(imaterial);
            this.b = entityvillager_merchantoptionrandomrange;
            this.c = new ItemStack(item);
            this.d = entityvillager_merchantoptionrandomrange1;
        }

        public void a(IMerchant imerchant, MerchantRecipeList merchantrecipelist, Random random) {
            int i = this.b.a(random);
            int j = this.d.a(random);

            merchantrecipelist.add(new MerchantRecipe(new ItemStack(this.a.getItem(), i), new ItemStack(Items.EMERALD), new ItemStack(this.c.getItem(), j)));
        }
    }

    static class h implements EntityVillager.IMerchantRecipeOption {

        public EntityVillager.MerchantOptionRandomRange a;
        public String b;
        public MapIcon.Type c;

        public h(EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange, String s, MapIcon.Type mapicon_type) {
            this.a = entityvillager_merchantoptionrandomrange;
            this.b = s;
            this.c = mapicon_type;
        }

        public void a(IMerchant imerchant, MerchantRecipeList merchantrecipelist, Random random) {
            int i = this.a.a(random);
            World world = imerchant.getWorld();
            BlockPosition blockposition = world.a(this.b, imerchant.getPosition(), 100, true);

            if (blockposition != null) {
                ItemStack itemstack = ItemWorldMap.createFilledMapView(world, blockposition.getX(), blockposition.getZ(), (byte) 2, true, true);

                ItemWorldMap.applySepiaFilter(world, itemstack);
                WorldMap.decorateMap(itemstack, blockposition, "+", this.c);
                itemstack.a((IChatBaseComponent) (new ChatMessage("filled_map." + this.b.toLowerCase(Locale.ROOT), new Object[0])));
                merchantrecipelist.add(new MerchantRecipe(new ItemStack(Items.EMERALD, i), new ItemStack(Items.COMPASS), itemstack));
            }

        }
    }

    static class MerchantRecipeOptionBook implements EntityVillager.IMerchantRecipeOption {

        public MerchantRecipeOptionBook() {}

        public void a(IMerchant imerchant, MerchantRecipeList merchantrecipelist, Random random) {
            Enchantment enchantment = (Enchantment) IRegistry.ENCHANTMENT.a(random);
            int i = MathHelper.nextInt(random, enchantment.getStartLevel(), enchantment.getMaxLevel());
            ItemStack itemstack = ItemEnchantedBook.a(new WeightedRandomEnchant(enchantment, i));
            int j = 2 + random.nextInt(5 + i * 10) + 3 * i;

            if (enchantment.isTreasure()) {
                j *= 2;
            }

            if (j > 64) {
                j = 64;
            }

            merchantrecipelist.add(new MerchantRecipe(new ItemStack(Items.BOOK), new ItemStack(Items.EMERALD, j), itemstack));
        }
    }

    static class MerchantRecipeOptionEnchant implements EntityVillager.IMerchantRecipeOption {

        public ItemStack a;
        public EntityVillager.MerchantOptionRandomRange b;

        public MerchantRecipeOptionEnchant(Item item, EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange) {
            this.a = new ItemStack(item);
            this.b = entityvillager_merchantoptionrandomrange;
        }

        public void a(IMerchant imerchant, MerchantRecipeList merchantrecipelist, Random random) {
            int i = 1;

            if (this.b != null) {
                i = this.b.a(random);
            }

            ItemStack itemstack = new ItemStack(Items.EMERALD, i);
            ItemStack itemstack1 = EnchantmentManager.a(random, new ItemStack(this.a.getItem()), 5 + random.nextInt(15), false);

            merchantrecipelist.add(new MerchantRecipe(itemstack, itemstack1));
        }
    }

    static class MerchantRecipeOptionSell implements EntityVillager.IMerchantRecipeOption {

        public ItemStack a;
        public EntityVillager.MerchantOptionRandomRange b;

        public MerchantRecipeOptionSell(Block block, EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange) {
            this(new ItemStack(block), entityvillager_merchantoptionrandomrange);
        }

        public MerchantRecipeOptionSell(Item item, EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange) {
            this(new ItemStack(item), entityvillager_merchantoptionrandomrange);
        }

        public MerchantRecipeOptionSell(ItemStack itemstack, EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange) {
            this.a = itemstack;
            this.b = entityvillager_merchantoptionrandomrange;
        }

        public void a(IMerchant imerchant, MerchantRecipeList merchantrecipelist, Random random) {
            int i = 1;

            if (this.b != null) {
                i = this.b.a(random);
            }

            ItemStack itemstack;
            ItemStack itemstack1;

            if (i < 0) {
                itemstack = new ItemStack(Items.EMERALD);
                itemstack1 = new ItemStack(this.a.getItem(), -i);
            } else {
                itemstack = new ItemStack(Items.EMERALD, i);
                itemstack1 = new ItemStack(this.a.getItem());
            }

            merchantrecipelist.add(new MerchantRecipe(itemstack, itemstack1));
        }
    }

    static class MerchantRecipeOptionBuy implements EntityVillager.IMerchantRecipeOption {

        public Item a;
        public EntityVillager.MerchantOptionRandomRange b;

        public MerchantRecipeOptionBuy(IMaterial imaterial, EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange) {
            this.a = imaterial.getItem();
            this.b = entityvillager_merchantoptionrandomrange;
        }

        public void a(IMerchant imerchant, MerchantRecipeList merchantrecipelist, Random random) {
            ItemStack itemstack = new ItemStack(this.a, this.b == null ? 1 : this.b.a(random));

            merchantrecipelist.add(new MerchantRecipe(itemstack, Items.EMERALD));
        }
    }

    interface IMerchantRecipeOption {

        void a(IMerchant imerchant, MerchantRecipeList merchantrecipelist, Random random);
    }

    static class MerchantOptionRandomRange extends Tuple<Integer, Integer> {

        public MerchantOptionRandomRange(int i, int j) {
            super(i, j);
            if (j < i) {
                EntityVillager.bC.warn("PriceRange({}, {}) invalid, {} smaller than {}", i, j, j, i);
            }

        }

        public int a(Random random) {
            return (Integer) this.a() >= (Integer) this.b() ? (Integer) this.a() : (Integer) this.a() + random.nextInt((Integer) this.b() - (Integer) this.a() + 1);
        }
    }
}
