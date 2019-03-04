package net.minecraft.server;

import java.util.Iterator;
import javax.annotation.Nullable;
// CraftBukkit start
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
// CraftBukkit end

public class InventoryCraftResult implements IInventory, RecipeHolder {

    private final NonNullList<ItemStack> items;
    private IRecipe b;

    // CraftBukkit start
    private int maxStack = MAX_STACK;

    public java.util.List<ItemStack> getContents() {
        return this.items;
    }

    public org.bukkit.inventory.InventoryHolder getOwner() {
        return null; // Result slots don't get an owner
    }

    // Don't need a transaction; the InventoryCrafting keeps track of it for us
    public void onOpen(CraftHumanEntity who) {}
    public void onClose(CraftHumanEntity who) {}
    public java.util.List<HumanEntity> getViewers() {
        return new java.util.ArrayList<HumanEntity>();
    }

    public void setMaxStackSize(int size) {
        maxStack = size;
    }

    @Override
    public Location getLocation() {
        return null;
    }
    // CraftBukkit end

    public InventoryCraftResult() {
        this.items = NonNullList.a(1, ItemStack.a);
    }

    public int getSize() {
        return 1;
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
        return (ItemStack) this.items.get(0);
    }

    public IChatBaseComponent getDisplayName() {
        return new ChatComponentText("Result");
    }

    public boolean hasCustomName() {
        return false;
    }

    @Nullable
    public IChatBaseComponent getCustomName() {
        return null;
    }

    public ItemStack splitStack(int i, int j) {
        return ContainerUtil.a(this.items, 0);
    }

    public ItemStack splitWithoutUpdate(int i) {
        return ContainerUtil.a(this.items, 0);
    }

    public void setItem(int i, ItemStack itemstack) {
        this.items.set(0, itemstack);
    }

    public int getMaxStackSize() {
        return maxStack; // CraftBukkit
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

    public void a(@Nullable IRecipe irecipe) {
        this.b = irecipe;
    }

    @Nullable
    public IRecipe i() {
        return this.b;
    }
}
