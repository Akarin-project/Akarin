package net.minecraft.server;

import java.util.Iterator;
import javax.annotation.Nullable;

public class InventoryCraftResult implements IInventory, RecipeHolder {

    private final NonNullList<ItemStack> items;
    private IRecipe b;

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

    public void a(@Nullable IRecipe irecipe) {
        this.b = irecipe;
    }

    @Nullable
    public IRecipe i() {
        return this.b;
    }
}
