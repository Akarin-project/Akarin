package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

// CraftBukkit start
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
// CraftBukkit end

public class PlayerInventory implements IInventory {

    public final NonNullList<ItemStack> items;
    public final NonNullList<ItemStack> armor;
    public final NonNullList<ItemStack> extraSlots;
    private final List<NonNullList<ItemStack>> f;
    public int itemInHandIndex;
    public EntityHuman player;
    private ItemStack carried;
    private int h;

    // CraftBukkit start - add fields and methods
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    private int maxStack = MAX_STACK;

    public List<ItemStack> getContents() {
        List<ItemStack> combined = new ArrayList<ItemStack>(items.size() + armor.size() + extraSlots.size());
        for (List<net.minecraft.server.ItemStack> sub : this.f) {
            combined.addAll(sub);
        }

        return combined;
    }

    public List<ItemStack> getArmorContents() {
        return this.armor;
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

    public org.bukkit.inventory.InventoryHolder getOwner() {
        return this.player.getBukkitEntity();
    }

    public void setMaxStackSize(int size) {
        maxStack = size;
    }

    @Override
    public Location getLocation() {
        return player.getBukkitEntity().getLocation();
    }
    // CraftBukkit end

    public PlayerInventory(EntityHuman entityhuman) {
        this.items = NonNullList.a(36, ItemStack.a);
        this.armor = NonNullList.a(4, ItemStack.a);
        this.extraSlots = NonNullList.a(1, ItemStack.a);
        this.f = ImmutableList.of(this.items, this.armor, this.extraSlots);
        this.carried = ItemStack.a;
        this.player = entityhuman;
    }

    public ItemStack getItemInHand() {
        return e(this.itemInHandIndex) ? (ItemStack) this.items.get(this.itemInHandIndex) : ItemStack.a;
    }

    public static int getHotbarSize() {
        return 9;
    }

    private boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return !itemstack.isEmpty() && this.b(itemstack, itemstack1) && itemstack.isStackable() && itemstack.getCount() < itemstack.getMaxStackSize() && itemstack.getCount() < this.getMaxStackSize();
    }

    private boolean b(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack.getItem() == itemstack1.getItem() && ItemStack.equals(itemstack, itemstack1);
    }

    // CraftBukkit start - Watch method above! :D
    public int canHold(ItemStack itemstack) {
        int remains = itemstack.getCount();
        for (int i = 0; i < this.items.size(); ++i) {
            ItemStack itemstack1 = this.getItem(i);
            if (itemstack1.isEmpty()) return itemstack.getCount();

            if (this.a(itemstack1, itemstack)) { // PAIL rename isSimilarAndNotFull
                remains -= (itemstack1.getMaxStackSize() < this.getMaxStackSize() ? itemstack1.getMaxStackSize() : this.getMaxStackSize()) - itemstack1.getCount();
            }
            if (remains <= 0) return itemstack.getCount();
        }
        return itemstack.getCount() - remains;
    }
    // CraftBukkit end

    public int getFirstEmptySlotIndex() {
        for (int i = 0; i < this.items.size(); ++i) {
            if (((ItemStack) this.items.get(i)).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    public void d(int i) {
        this.itemInHandIndex = this.l();
        ItemStack itemstack = (ItemStack) this.items.get(this.itemInHandIndex);

        this.items.set(this.itemInHandIndex, this.items.get(i));
        this.items.set(i, itemstack);
    }

    public static boolean e(int i) {
        return i >= 0 && i < 9;
    }

    public int c(ItemStack itemstack) {
        for (int i = 0; i < this.items.size(); ++i) {
            ItemStack itemstack1 = (ItemStack) this.items.get(i);

            if (!((ItemStack) this.items.get(i)).isEmpty() && this.b(itemstack, (ItemStack) this.items.get(i)) && !((ItemStack) this.items.get(i)).f() && !itemstack1.hasEnchantments() && !itemstack1.hasName()) {
                return i;
            }
        }

        return -1;
    }

    public int l() {
        int i;
        int j;

        for (j = 0; j < 9; ++j) {
            i = (this.itemInHandIndex + j) % 9;
            if (((ItemStack) this.items.get(i)).isEmpty()) {
                return i;
            }
        }

        for (j = 0; j < 9; ++j) {
            i = (this.itemInHandIndex + j) % 9;
            if (!((ItemStack) this.items.get(i)).hasEnchantments()) {
                return i;
            }
        }

        return this.itemInHandIndex;
    }

    public int a(Predicate<ItemStack> predicate, int i) {
        int j = 0;

        int k;

        for (k = 0; k < this.getSize(); ++k) {
            ItemStack itemstack = this.getItem(k);

            if (!itemstack.isEmpty() && predicate.test(itemstack)) {
                int l = i <= 0 ? itemstack.getCount() : Math.min(i - j, itemstack.getCount());

                j += l;
                if (i != 0) {
                    itemstack.subtract(l);
                    if (itemstack.isEmpty()) {
                        this.setItem(k, ItemStack.a);
                    }

                    if (i > 0 && j >= i) {
                        return j;
                    }
                }
            }
        }

        if (!this.carried.isEmpty() && predicate.test(this.carried)) {
            k = i <= 0 ? this.carried.getCount() : Math.min(i - j, this.carried.getCount());
            j += k;
            if (i != 0) {
                this.carried.subtract(k);
                if (this.carried.isEmpty()) {
                    this.carried = ItemStack.a;
                }

                if (i > 0 && j >= i) {
                    return j;
                }
            }
        }

        return j;
    }

    private int i(ItemStack itemstack) {
        int i = this.firstPartial(itemstack);

        if (i == -1) {
            i = this.getFirstEmptySlotIndex();
        }

        return i == -1 ? itemstack.getCount() : this.d(i, itemstack);
    }

    private int d(int i, ItemStack itemstack) {
        Item item = itemstack.getItem();
        int j = itemstack.getCount();
        ItemStack itemstack1 = this.getItem(i);

        if (itemstack1.isEmpty()) {
            itemstack1 = new ItemStack(item, 0);
            if (itemstack.hasTag()) {
                itemstack1.setTag(itemstack.getTag().clone());
            }

            this.setItem(i, itemstack1);
        }

        int k = j;

        if (j > itemstack1.getMaxStackSize() - itemstack1.getCount()) {
            k = itemstack1.getMaxStackSize() - itemstack1.getCount();
        }

        if (k > this.getMaxStackSize() - itemstack1.getCount()) {
            k = this.getMaxStackSize() - itemstack1.getCount();
        }

        if (k == 0) {
            return j;
        } else {
            j -= k;
            itemstack1.add(k);
            itemstack1.d(5);
            return j;
        }
    }

    public int firstPartial(ItemStack itemstack) {
        if (this.a(this.getItem(this.itemInHandIndex), itemstack)) {
            return this.itemInHandIndex;
        } else if (this.a(this.getItem(40), itemstack)) {
            return 40;
        } else {
            for (int i = 0; i < this.items.size(); ++i) {
                if (this.a((ItemStack) this.items.get(i), itemstack)) {
                    return i;
                }
            }

            return -1;
        }
    }

    public void p() {
        Iterator iterator = this.f.iterator();

        while (iterator.hasNext()) {
            NonNullList<ItemStack> nonnulllist = (NonNullList) iterator.next();

            for (int i = 0; i < nonnulllist.size(); ++i) {
                if (!((ItemStack) nonnulllist.get(i)).isEmpty()) {
                    ((ItemStack) nonnulllist.get(i)).a(this.player.world, this.player, i, this.itemInHandIndex == i);
                }
            }
        }

    }

    public boolean pickup(ItemStack itemstack) {
        return this.c(-1, itemstack);
    }

    public boolean c(int i, ItemStack itemstack) {
        if (itemstack.isEmpty()) {
            return false;
        } else {
            try {
                if (itemstack.f()) {
                    if (i == -1) {
                        i = this.getFirstEmptySlotIndex();
                    }

                    if (i >= 0) {
                        this.items.set(i, itemstack.cloneItemStack());
                        ((ItemStack) this.items.get(i)).d(5);
                        itemstack.setCount(0);
                        return true;
                    } else if (this.player.abilities.canInstantlyBuild) {
                        itemstack.setCount(0);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    int j;

                    do {
                        j = itemstack.getCount();
                        if (i == -1) {
                            itemstack.setCount(this.i(itemstack));
                        } else {
                            itemstack.setCount(this.d(i, itemstack));
                        }
                    } while (!itemstack.isEmpty() && itemstack.getCount() < j);

                    if (itemstack.getCount() == j && this.player.abilities.canInstantlyBuild) {
                        itemstack.setCount(0);
                        return true;
                    } else {
                        return itemstack.getCount() < j;
                    }
                }
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Adding item to inventory");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Item being added");

                crashreportsystemdetails.a("Item ID", (Object) Item.getId(itemstack.getItem()));
                crashreportsystemdetails.a("Item data", (Object) itemstack.getDamage());
                crashreportsystemdetails.a("Item name", () -> {
                    return itemstack.getName().getString();
                });
                throw new ReportedException(crashreport);
            }
        }
    }

    public void a(World world, ItemStack itemstack) {
        if (!world.isClientSide) {
            while (!itemstack.isEmpty()) {
                int i = this.firstPartial(itemstack);

                if (i == -1) {
                    i = this.getFirstEmptySlotIndex();
                }

                if (i == -1) {
                    this.player.drop(itemstack, false);
                    break;
                }

                int j = itemstack.getMaxStackSize() - this.getItem(i).getCount();

                if (this.c(i, itemstack.cloneAndSubtract(j))) {
                    ((EntityPlayer) this.player).playerConnection.sendPacket(new PacketPlayOutSetSlot(-2, i, this.getItem(i)));
                }
            }

        }
    }

    public ItemStack splitStack(int i, int j) {
        List<ItemStack> list = null;

        NonNullList nonnulllist;

        for (Iterator iterator = this.f.iterator(); iterator.hasNext(); i -= nonnulllist.size()) {
            nonnulllist = (NonNullList) iterator.next();
            if (i < nonnulllist.size()) {
                list = nonnulllist;
                break;
            }
        }

        return list != null && !((ItemStack) list.get(i)).isEmpty() ? ContainerUtil.a(list, i, j) : ItemStack.a;
    }

    public void f(ItemStack itemstack) {
        Iterator iterator = this.f.iterator();

        while (iterator.hasNext()) {
            NonNullList<ItemStack> nonnulllist = (NonNullList) iterator.next();

            for (int i = 0; i < nonnulllist.size(); ++i) {
                if (nonnulllist.get(i) == itemstack) {
                    nonnulllist.set(i, ItemStack.a);
                    break;
                }
            }
        }

    }

    public ItemStack splitWithoutUpdate(int i) {
        NonNullList<ItemStack> nonnulllist = null;

        NonNullList nonnulllist1;

        for (Iterator iterator = this.f.iterator(); iterator.hasNext(); i -= nonnulllist1.size()) {
            nonnulllist1 = (NonNullList) iterator.next();
            if (i < nonnulllist1.size()) {
                nonnulllist = nonnulllist1;
                break;
            }
        }

        if (nonnulllist != null && !((ItemStack) nonnulllist.get(i)).isEmpty()) {
            ItemStack itemstack = (ItemStack) nonnulllist.get(i);

            nonnulllist.set(i, ItemStack.a);
            return itemstack;
        } else {
            return ItemStack.a;
        }
    }

    public void setItem(int i, ItemStack itemstack) {
        NonNullList<ItemStack> nonnulllist = null;

        NonNullList nonnulllist1;

        for (Iterator iterator = this.f.iterator(); iterator.hasNext(); i -= nonnulllist1.size()) {
            nonnulllist1 = (NonNullList) iterator.next();
            if (i < nonnulllist1.size()) {
                nonnulllist = nonnulllist1;
                break;
            }
        }

        if (nonnulllist != null) {
            nonnulllist.set(i, itemstack);
        }

    }

    public float a(IBlockData iblockdata) {
        return ((ItemStack) this.items.get(this.itemInHandIndex)).a(iblockdata);
    }

    public NBTTagList a(NBTTagList nbttaglist) {
        NBTTagCompound nbttagcompound;
        int i;

        for (i = 0; i < this.items.size(); ++i) {
            if (!((ItemStack) this.items.get(i)).isEmpty()) {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) i);
                ((ItemStack) this.items.get(i)).save(nbttagcompound);
                nbttaglist.add((NBTBase) nbttagcompound);
            }
        }

        for (i = 0; i < this.armor.size(); ++i) {
            if (!((ItemStack) this.armor.get(i)).isEmpty()) {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) (i + 100));
                ((ItemStack) this.armor.get(i)).save(nbttagcompound);
                nbttaglist.add((NBTBase) nbttagcompound);
            }
        }

        for (i = 0; i < this.extraSlots.size(); ++i) {
            if (!((ItemStack) this.extraSlots.get(i)).isEmpty()) {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) (i + 150));
                ((ItemStack) this.extraSlots.get(i)).save(nbttagcompound);
                nbttaglist.add((NBTBase) nbttagcompound);
            }
        }

        return nbttaglist;
    }

    public void b(NBTTagList nbttaglist) {
        this.items.clear();
        this.armor.clear();
        this.extraSlots.clear();

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.a(nbttagcompound);

            if (!itemstack.isEmpty()) {
                if (j >= 0 && j < this.items.size()) {
                    this.items.set(j, itemstack);
                } else if (j >= 100 && j < this.armor.size() + 100) {
                    this.armor.set(j - 100, itemstack);
                } else if (j >= 150 && j < this.extraSlots.size() + 150) {
                    this.extraSlots.set(j - 150, itemstack);
                }
            }
        }

    }

    public int getSize() {
        return this.items.size() + this.armor.size() + this.extraSlots.size();
    }

    public boolean P_() {
        Iterator iterator = this.items.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                iterator = this.armor.iterator();

                do {
                    if (!iterator.hasNext()) {
                        iterator = this.extraSlots.iterator();

                        do {
                            if (!iterator.hasNext()) {
                                return true;
                            }

                            itemstack = (ItemStack) iterator.next();
                        } while (itemstack.isEmpty());

                        return false;
                    }

                    itemstack = (ItemStack) iterator.next();
                } while (itemstack.isEmpty());

                return false;
            }

            itemstack = (ItemStack) iterator.next();
        } while (itemstack.isEmpty());

        return false;
    }

    public ItemStack getItem(int i) {
        List<ItemStack> list = null;

        NonNullList nonnulllist;

        for (Iterator iterator = this.f.iterator(); iterator.hasNext(); i -= nonnulllist.size()) {
            nonnulllist = (NonNullList) iterator.next();
            if (i < nonnulllist.size()) {
                list = nonnulllist;
                break;
            }
        }

        return list == null ? ItemStack.a : (ItemStack) list.get(i);
    }

    public IChatBaseComponent getDisplayName() {
        return new ChatMessage("container.inventory", new Object[0]);
    }

    @Nullable
    public IChatBaseComponent getCustomName() {
        return null;
    }

    public boolean hasCustomName() {
        return false;
    }

    public int getMaxStackSize() {
        return maxStack; // CraftBukkit
    }

    public boolean b(IBlockData iblockdata) {
        return this.getItem(this.itemInHandIndex).b(iblockdata);
    }

    public void a(float f) {
        if (f > 0.0F) {
            f /= 4.0F;
            if (f < 1.0F) {
                f = 1.0F;
            }

            for (int i = 0; i < this.armor.size(); ++i) {
                ItemStack itemstack = (ItemStack) this.armor.get(i);

                if (itemstack.getItem() instanceof ItemArmor) {
                    itemstack.damage((int) f, this.player);
                }
            }

        }
    }

    public void dropContents() {
        Iterator iterator = this.f.iterator();

        while (iterator.hasNext()) {
            List<ItemStack> list = (List) iterator.next();

            for (int i = 0; i < list.size(); ++i) {
                ItemStack itemstack = (ItemStack) list.get(i);

                if (!itemstack.isEmpty()) {
                    this.player.a(itemstack, true, false);
                    list.set(i, ItemStack.a);
                }
            }
        }

    }

    public void update() {
        ++this.h;
    }

    public void setCarried(ItemStack itemstack) {
        this.carried = itemstack;
    }

    public ItemStack getCarried() {
        // CraftBukkit start
        if (this.carried.isEmpty()) {
            this.setCarried(ItemStack.a);
        }
        // CraftBukkit end
        return this.carried;
    }

    public boolean a(EntityHuman entityhuman) {
        return this.player.dead ? false : entityhuman.h(this.player) <= 64.0D;
    }

    public boolean h(ItemStack itemstack) {
        Iterator iterator = this.f.iterator();

        while (iterator.hasNext()) {
            List<ItemStack> list = (List) iterator.next();
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                ItemStack itemstack1 = (ItemStack) iterator1.next();

                if (!itemstack1.isEmpty() && itemstack1.doMaterialsMatch(itemstack)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void startOpen(EntityHuman entityhuman) {}

    public void closeContainer(EntityHuman entityhuman) {}

    public boolean b(int i, ItemStack itemstack) {
        return true;
    }

    public void a(PlayerInventory playerinventory) {
        for (int i = 0; i < this.getSize(); ++i) {
            this.setItem(i, playerinventory.getItem(i));
        }

        this.itemInHandIndex = playerinventory.itemInHandIndex;
    }

    public int getProperty(int i) {
        return 0;
    }

    public void setProperty(int i, int j) {}

    public int h() {
        return 0;
    }

    public void clear() {
        Iterator iterator = this.f.iterator();

        while (iterator.hasNext()) {
            List<ItemStack> list = (List) iterator.next();

            list.clear();
        }

    }

    public void a(AutoRecipeStackManager autorecipestackmanager) {
        Iterator iterator = this.items.iterator();

        while (iterator.hasNext()) {
            ItemStack itemstack = (ItemStack) iterator.next();

            autorecipestackmanager.a(itemstack);
        }

    }
}
