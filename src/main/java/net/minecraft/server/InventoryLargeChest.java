package net.minecraft.server;

import javax.annotation.Nullable;

public class InventoryLargeChest implements ITileInventory {

    private final IChatBaseComponent a;
    public final ITileInventory left;
    public final ITileInventory right;

    public InventoryLargeChest(IChatBaseComponent ichatbasecomponent, ITileInventory itileinventory, ITileInventory itileinventory1) {
        this.a = ichatbasecomponent;
        if (itileinventory == null) {
            itileinventory = itileinventory1;
        }

        if (itileinventory1 == null) {
            itileinventory1 = itileinventory;
        }

        this.left = itileinventory;
        this.right = itileinventory1;
        if (itileinventory.isLocked()) {
            itileinventory1.setLock(itileinventory.getLock());
        } else if (itileinventory1.isLocked()) {
            itileinventory.setLock(itileinventory1.getLock());
        }

    }

    public int getSize() {
        return this.left.getSize() + this.right.getSize();
    }

    public boolean P_() {
        return this.left.P_() && this.right.P_();
    }

    public boolean a(IInventory iinventory) {
        return this.left == iinventory || this.right == iinventory;
    }

    public IChatBaseComponent getDisplayName() {
        return this.left.hasCustomName() ? this.left.getDisplayName() : (this.right.hasCustomName() ? this.right.getDisplayName() : this.a);
    }

    public boolean hasCustomName() {
        return this.left.hasCustomName() || this.right.hasCustomName();
    }

    @Nullable
    public IChatBaseComponent getCustomName() {
        return this.left.hasCustomName() ? this.left.getCustomName() : this.right.getCustomName();
    }

    public ItemStack getItem(int i) {
        return i >= this.left.getSize() ? this.right.getItem(i - this.left.getSize()) : this.left.getItem(i);
    }

    public ItemStack splitStack(int i, int j) {
        return i >= this.left.getSize() ? this.right.splitStack(i - this.left.getSize(), j) : this.left.splitStack(i, j);
    }

    public ItemStack splitWithoutUpdate(int i) {
        return i >= this.left.getSize() ? this.right.splitWithoutUpdate(i - this.left.getSize()) : this.left.splitWithoutUpdate(i);
    }

    public void setItem(int i, ItemStack itemstack) {
        if (i >= this.left.getSize()) {
            this.right.setItem(i - this.left.getSize(), itemstack);
        } else {
            this.left.setItem(i, itemstack);
        }

    }

    public int getMaxStackSize() {
        return this.left.getMaxStackSize();
    }

    public void update() {
        this.left.update();
        this.right.update();
    }

    public boolean a(EntityHuman entityhuman) {
        return this.left.a(entityhuman) && this.right.a(entityhuman);
    }

    public void startOpen(EntityHuman entityhuman) {
        this.left.startOpen(entityhuman);
        this.right.startOpen(entityhuman);
    }

    public void closeContainer(EntityHuman entityhuman) {
        this.left.closeContainer(entityhuman);
        this.right.closeContainer(entityhuman);
    }

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

    public boolean isLocked() {
        return this.left.isLocked() || this.right.isLocked();
    }

    public void setLock(ChestLock chestlock) {
        this.left.setLock(chestlock);
        this.right.setLock(chestlock);
    }

    public ChestLock getLock() {
        return this.left.getLock();
    }

    public String getContainerName() {
        return this.left.getContainerName();
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        return new ContainerChest(playerinventory, this, entityhuman);
    }

    public void clear() {
        this.left.clear();
        this.right.clear();
    }
}
