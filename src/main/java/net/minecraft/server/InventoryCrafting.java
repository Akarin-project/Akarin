package net.minecraft.server;

public class InventoryCrafting implements IInventory {

    private ItemStack[] items;
    private int b;
    private Container c;

    // CraftBukkit start
    public ItemStack[] getContents() {
        return this.items;
    }
    // CraftBukkit end

    public InventoryCrafting(Container container, int i, int j) {
        int k = i * j;

        this.items = new ItemStack[k];
        this.c = container;
        this.b = i;
    }

    public int getSize() {
        return this.items.length;
    }

    public ItemStack getItem(int i) {
        return i >= this.getSize() ? null : this.items[i];
    }

    public ItemStack b(int i, int j) {
        if (i >= 0 && i < this.b) {
            int k = i + j * this.b;

            return this.getItem(k);
        } else {
            return null;
        }
    }

    public String getName() {
        return "Crafting";
    }

    public ItemStack splitStack(int i, int j) {
        if (this.items[i] != null) {
            ItemStack itemstack;

            if (this.items[i].count <= j) {
                itemstack = this.items[i];
                this.items[i] = null;
                this.c.a((IInventory) this);
                return itemstack;
            } else {
                itemstack = this.items[i].a(j);
                if (this.items[i].count == 0) {
                    this.items[i] = null;
                }

                this.c.a((IInventory) this);
                return itemstack;
            }
        } else {
            return null;
        }
    }

    public void setItem(int i, ItemStack itemstack) {
        this.items[i] = itemstack;
        this.c.a((IInventory) this);
    }

    public int getMaxStackSize() {
        return 64;
    }

    public void update() {}

    public boolean a_(EntityHuman entityhuman) {
        return true;
    }
}
