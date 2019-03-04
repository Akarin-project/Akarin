package net.minecraft.server;

import java.util.Iterator;
import javax.annotation.Nullable;
// CraftBukkit start
import java.util.List;
import org.bukkit.Location;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
// CraftBukkit end

public class InventoryCrafting implements IInventory, AutoRecipeOutput {

    private final NonNullList<ItemStack> items;
    private final int b;
    private final int c;
    public final Container container;

    // CraftBukkit start - add fields
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    private IRecipe currentRecipe;
    public IInventory resultInventory;
    private EntityHuman owner;
    private int maxStack = MAX_STACK;

    public List<ItemStack> getContents() {
        return this.items;
    }

    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public InventoryType getInvType() {
        return items.size() == 4 ? InventoryType.CRAFTING : InventoryType.WORKBENCH;
    }

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    public org.bukkit.inventory.InventoryHolder getOwner() {
        return (owner == null) ? null : owner.getBukkitEntity();
    }

    public void setMaxStackSize(int size) {
        maxStack = size;
        resultInventory.setMaxStackSize(size);
    }

    @Override
    public Location getLocation() {
        return owner.getBukkitEntity().getLocation();
    }

    @Override
    public IRecipe getCurrentRecipe() {
        return currentRecipe;
    }

    @Override
    public void setCurrentRecipe(IRecipe currentRecipe) {
        this.currentRecipe = currentRecipe;
    }

    public InventoryCrafting(Container container, int i, int j, EntityHuman player) {
        this(container, i, j);
        this.owner = player;
    }
    // CraftBukkit end

    public InventoryCrafting(Container container, int i, int j) {
        this.items = NonNullList.a(i * j, ItemStack.a);
        this.container = container;
        this.b = i;
        this.c = j;
    }

    public int getSize() {
        return this.items.size();
    }

    public boolean P_() {
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
        return i >= this.getSize() ? ItemStack.a : (ItemStack) this.items.get(i);
    }

    public IChatBaseComponent getDisplayName() {
        return new ChatMessage("container.crafting", new Object[0]);
    }

    public boolean hasCustomName() {
        return false;
    }

    @Nullable
    public IChatBaseComponent getCustomName() {
        return null;
    }

    public ItemStack splitWithoutUpdate(int i) {
        return ContainerUtil.a(this.items, i);
    }

    public ItemStack splitStack(int i, int j) {
        ItemStack itemstack = ContainerUtil.a(this.items, i, j);

        if (!itemstack.isEmpty()) {
            this.container.a((IInventory) this);
        }

        return itemstack;
    }

    public void setItem(int i, ItemStack itemstack) {
        this.items.set(i, itemstack);
        this.container.a((IInventory) this);
    }

    public int getMaxStackSize() {
        return 64;
    }

    public void update() {}

    public boolean a(EntityHuman entityhuman) {
        return true;
    }

    public void startOpen(EntityHuman entityhuman) {}

    public void closeContainer(EntityHuman entityhuman) {}

    public boolean b(int i, ItemStack itemstack) {
        return true;
    }

    public int getProperty(int i) {
        return 0;
    }

    public void setProperty(int i, int j) {}

    public int h() {
        return 0;
    }

    public void clear() {
        this.items.clear();
    }

    public int n() {
        return this.c;
    }

    public int U_() {
        return this.b;
    }

    public void a(AutoRecipeStackManager autorecipestackmanager) {
        Iterator iterator = this.items.iterator();

        while (iterator.hasNext()) {
            ItemStack itemstack = (ItemStack) iterator.next();

            autorecipestackmanager.a(itemstack);
        }

    }
}
