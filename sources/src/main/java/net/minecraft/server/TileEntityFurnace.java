package net.minecraft.server;

import java.util.Iterator;
// CraftBukkit start
import java.util.List;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
// CraftBukkit end

public class TileEntityFurnace extends TileEntityContainer implements ITickable, IWorldInventory {

    private static final int[] a = new int[] { 0};
    private static final int[] f = new int[] { 2, 1};
    private static final int[] g = new int[] { 1};
    private NonNullList<ItemStack> items;
    private int burnTime;
    private int ticksForCurrentFuel;
    private int cookTime;
    private int cookTimeTotal;
    private String m;

    // CraftBukkit start - add fields and methods
    private int lastTick = MinecraftServer.currentTick;
    private int maxStack = MAX_STACK;
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();

    public List<ItemStack> getContents() {
        return this.items;
    }

    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    public void setMaxStackSize(int size) {
        maxStack = size;
    }
    // CraftBukkit end

    public TileEntityFurnace() {
        this.items = NonNullList.a(3, ItemStack.a);
    }

    public int getSize() {
        return this.items.size();
    }

    public boolean x_() {
        Iterator iterator = this.items.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            itemstack = (ItemStack) iterator.next();
        } while (itemstack.isEmpty());

        return false;
    }

    public ItemStack getItem(int i) {
        return (ItemStack) this.items.get(i);
    }

    public ItemStack splitStack(int i, int j) {
        return ContainerUtil.a(this.items, i, j);
    }

    public ItemStack splitWithoutUpdate(int i) {
        return ContainerUtil.a(this.items, i);
    }

    public void setItem(int i, ItemStack itemstack) {
        ItemStack itemstack1 = (ItemStack) this.items.get(i);
        boolean flag = !itemstack.isEmpty() && itemstack.doMaterialsMatch(itemstack1) && ItemStack.equals(itemstack, itemstack1);

        this.items.set(i, itemstack);
        if (itemstack.getCount() > this.getMaxStackSize()) {
            itemstack.setCount(this.getMaxStackSize());
        }

        if (i == 0 && !flag) {
            this.cookTimeTotal = this.a(itemstack);
            this.cookTime = 0;
            this.update();
        }

    }

    public String getName() {
        return this.hasCustomName() ? this.m : "container.furnace";
    }

    public boolean hasCustomName() {
        return this.m != null && !this.m.isEmpty();
    }

    public void setCustomName(String s) {
        this.m = s;
    }

    public static void a(DataConverterManager dataconvertermanager) {
        dataconvertermanager.a(DataConverterTypes.BLOCK_ENTITY, (DataInspector) (new DataInspectorItemList(TileEntityFurnace.class, new String[] { "Items"})));
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.items = NonNullList.a(this.getSize(), ItemStack.a);
        ContainerUtil.b(nbttagcompound, this.items);
        this.burnTime = nbttagcompound.getShort("BurnTime");
        this.cookTime = nbttagcompound.getShort("CookTime");
        this.cookTimeTotal = nbttagcompound.getShort("CookTimeTotal");
        this.ticksForCurrentFuel = fuelTime((ItemStack) this.items.get(1));
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.m = nbttagcompound.getString("CustomName");
        }

    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.setShort("BurnTime", (short) this.burnTime);
        nbttagcompound.setShort("CookTime", (short) this.cookTime);
        nbttagcompound.setShort("CookTimeTotal", (short) this.cookTimeTotal);
        ContainerUtil.a(nbttagcompound, this.items);
        if (this.hasCustomName()) {
            nbttagcompound.setString("CustomName", this.m);
        }

        return nbttagcompound;
    }

    public int getMaxStackSize() {
        return 64;
    }

    public boolean isBurning() {
        return this.burnTime > 0;
    }

    public void e() {
        boolean flag = (this.getBlock() == Blocks.LIT_FURNACE); // CraftBukkit - SPIGOT-844 - Check if furnace block is lit using the block instead of burn time
        boolean flag1 = false;

        // CraftBukkit start - Use wall time instead of ticks for cooking
        int elapsedTicks = MinecraftServer.currentTick - this.lastTick;
        this.lastTick = MinecraftServer.currentTick;

        // CraftBukkit - moved from below - edited for wall time
        if (this.isBurning() && this.canBurn()) {
            this.cookTime += elapsedTicks;
            if (this.cookTime >= this.cookTimeTotal) {
                this.cookTime -= this.cookTimeTotal; // Paper
                this.cookTimeTotal = this.a((ItemStack) this.items.get(0));
                this.burn();
                flag1 = true;
            }
        } else {
            this.cookTime = 0;
        }
        // CraftBukkit end

        if (this.isBurning()) {
            this.burnTime -= elapsedTicks; // CraftBukkit - use elapsedTicks in place of constant
        }

        if (!this.world.isClientSide) {
            ItemStack itemstack = (ItemStack) this.items.get(1);

            if (!this.isBurning() && (itemstack.isEmpty() || ((ItemStack) this.items.get(0)).isEmpty())) {
                if (!this.isBurning() && this.cookTime > 0) {
                    this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
                }
            } else if(this.items.get(1) != null && this.items.get(1).getItem() != Items.BUCKET) {
                // CraftBukkit start - Handle multiple elapsed ticks
                if (this.burnTime <= 0 && this.canBurn()) { // CraftBukkit - == to <=
                    CraftItemStack fuel = CraftItemStack.asCraftMirror(itemstack);

                    FurnaceBurnEvent furnaceBurnEvent = new FurnaceBurnEvent(this.world.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ()), fuel, fuelTime(itemstack));
                    this.world.getServer().getPluginManager().callEvent(furnaceBurnEvent);

                    if (furnaceBurnEvent.isCancelled()) {
                        return;
                    }

                    this.ticksForCurrentFuel = furnaceBurnEvent.getBurnTime();
                    this.burnTime += this.ticksForCurrentFuel;
                    if (this.burnTime > 0 && furnaceBurnEvent.isBurning()) {
                        // CraftBukkit end
                        flag1 = true;
                        if (!itemstack.isEmpty()) {
                            Item item = itemstack.getItem();

                            itemstack.subtract(1);
                            if (itemstack.isEmpty()) {
                                Item item1 = item.q();

                                this.items.set(1, item1 == null ? ItemStack.a : new ItemStack(item1));
                            }
                        }
                    }
                }

                /* CraftBukkit start - Moved up
                if (this.isBurning() && this.canBurn()) {
                    ++this.cookTime;
                    if (this.cookTime == this.cookTimeTotal) {
                        this.cookTime = 0;
                        this.cookTimeTotal = this.a((ItemStack) this.items.get(0));
                        this.burn();
                        flag1 = true;
                    }
                } else {
                    this.cookTime = 0;
                }
                */
            }

            if (flag != this.isBurning()) {
                flag1 = true;
                BlockFurnace.a(this.isBurning(), this.world, this.position);
                this.invalidateBlockCache(); // CraftBukkit - Invalidate tile entity's cached block type 
            }
        }

        if (flag1) {
            this.update();
        }

    }

    public int a(ItemStack itemstack) {
        return 200;
    }

    private boolean canBurn() {
        if (((ItemStack) this.items.get(0)).isEmpty()) {
            return false;
        } else {
            ItemStack itemstack = RecipesFurnace.getInstance().getResult((ItemStack) this.items.get(0));

            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack itemstack1 = (ItemStack) this.items.get(2);

                // CraftBukkit - consider resultant count instead of current count
                return itemstack1.isEmpty() ? true : (!itemstack1.doMaterialsMatch(itemstack) ? false : (itemstack1.getCount() + itemstack.getCount() <= this.getMaxStackSize() && itemstack1.getCount() + itemstack.getCount() < itemstack1.getMaxStackSize() ? true : itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize()));
            }
        }
    }

    public void burn() {
        if (this.canBurn()) {
            ItemStack itemstack = (ItemStack) this.items.get(0);
            ItemStack itemstack1 = RecipesFurnace.getInstance().getResult(itemstack);
            ItemStack itemstack2 = (ItemStack) this.items.get(2);

            // CraftBukkit start - fire FurnaceSmeltEvent
            CraftItemStack source = CraftItemStack.asCraftMirror(itemstack);
            org.bukkit.inventory.ItemStack result = CraftItemStack.asBukkitCopy(itemstack1);

            FurnaceSmeltEvent furnaceSmeltEvent = new FurnaceSmeltEvent(this.world.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ()), source, result);
            this.world.getServer().getPluginManager().callEvent(furnaceSmeltEvent);

            if (furnaceSmeltEvent.isCancelled()) {
                return;
            }

            result = furnaceSmeltEvent.getResult();
            itemstack1 = CraftItemStack.asNMSCopy(result);

            if (!itemstack1.isEmpty()) {
                if (itemstack2.isEmpty()) {
                    this.items.set(2, itemstack1.cloneItemStack());
                } else if (CraftItemStack.asCraftMirror(itemstack2).isSimilar(result)) {
                    itemstack2.add(itemstack1.getCount());
                } else {
                    return;
                }
            }

            /*
            if (itemstack2.isEmpty()) {
                this.items.set(2, itemstack1.cloneItemStack());
            } else if (itemstack2.getItem() == itemstack1.getItem()) {
                itemstack2.add(1);
            }
            */
            // CraftBukkit end

            if (itemstack.getItem() == Item.getItemOf(Blocks.SPONGE) && itemstack.getData() == 1 && !((ItemStack) this.items.get(1)).isEmpty() && ((ItemStack) this.items.get(1)).getItem() == Items.BUCKET) {
                this.items.set(1, new ItemStack(Items.WATER_BUCKET));
            }

            itemstack.subtract(1);
        }
    }

    public static int fuelTime(ItemStack itemstack) {
        if (itemstack.isEmpty()) {
            return 0;
        } else {
            Item item = itemstack.getItem();

            return item == Item.getItemOf(Blocks.WOODEN_SLAB) ? 150 : (item == Item.getItemOf(Blocks.WOOL) ? 100 : (item == Item.getItemOf(Blocks.CARPET) ? 67 : (item == Item.getItemOf(Blocks.LADDER) ? 300 : (item == Item.getItemOf(Blocks.WOODEN_BUTTON) ? 100 : (Block.asBlock(item).getBlockData().getMaterial() == Material.WOOD ? 300 : (item == Item.getItemOf(Blocks.COAL_BLOCK) ? 16000 : (item instanceof ItemTool && "WOOD".equals(((ItemTool) item).h()) ? 200 : (item instanceof ItemSword && "WOOD".equals(((ItemSword) item).h()) ? 200 : (item instanceof ItemHoe && "WOOD".equals(((ItemHoe) item).g()) ? 200 : (item == Items.STICK ? 100 : (item != Items.BOW && item != Items.FISHING_ROD ? (item == Items.SIGN ? 200 : (item == Items.COAL ? 1600 : (item == Items.LAVA_BUCKET ? 20000 : (item != Item.getItemOf(Blocks.SAPLING) && item != Items.BOWL ? (item == Items.BLAZE_ROD ? 2400 : (item instanceof ItemDoor && item != Items.IRON_DOOR ? 200 : (item instanceof ItemBoat ? 400 : 0))) : 100)))) : 300)))))))))));
        }
    }

    public static boolean isFuel(ItemStack itemstack) {
        return fuelTime(itemstack) > 0;
    }

    public boolean a(EntityHuman entityhuman) {
        return this.world.getTileEntity(this.position) != this ? false : entityhuman.d((double) this.position.getX() + 0.5D, (double) this.position.getY() + 0.5D, (double) this.position.getZ() + 0.5D) <= 64.0D;
    }

    public void startOpen(EntityHuman entityhuman) {}

    public void closeContainer(EntityHuman entityhuman) {}

    public boolean b(int i, ItemStack itemstack) {
        if (i == 2) {
            return false;
        } else if (i != 1) {
            return true;
        } else {
            ItemStack itemstack1 = (ItemStack) this.items.get(1);

            return isFuel(itemstack) || SlotFurnaceFuel.d_(itemstack) && itemstack1.getItem() != Items.BUCKET;
        }
    }

    public int[] getSlotsForFace(EnumDirection enumdirection) {
        return enumdirection == EnumDirection.DOWN ? TileEntityFurnace.f : (enumdirection == EnumDirection.UP ? TileEntityFurnace.a : TileEntityFurnace.g);
    }

    public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
        return this.b(i, itemstack);
    }

    public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
        if (enumdirection == EnumDirection.DOWN && i == 1) {
            Item item = itemstack.getItem();

            if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
                return false;
            }
        }

        return true;
    }

    public String getContainerName() {
        return "minecraft:furnace";
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        return new ContainerFurnace(playerinventory, this);
    }

    public int getProperty(int i) {
        switch (i) {
        case 0:
            return this.burnTime;

        case 1:
            return this.ticksForCurrentFuel;

        case 2:
            return this.cookTime;

        case 3:
            return this.cookTimeTotal;

        default:
            return 0;
        }
    }

    public void setProperty(int i, int j) {
        switch (i) {
        case 0:
            this.burnTime = j;
            break;

        case 1:
            this.ticksForCurrentFuel = j;
            break;

        case 2:
            this.cookTime = j;
            break;

        case 3:
            this.cookTimeTotal = j;
        }

    }

    public int h() {
        return 4;
    }

    public void clear() {
        this.items.clear();
    }
}
