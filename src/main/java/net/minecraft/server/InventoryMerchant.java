package net.minecraft.server;

import java.util.Iterator;
import javax.annotation.Nullable;

public class InventoryMerchant implements IInventory {

    private final IMerchant merchant;
    private final NonNullList<ItemStack> itemsInSlots;
    private final EntityHuman player;
    private MerchantRecipe recipe;
    public int selectedIndex;

    public InventoryMerchant(EntityHuman entityhuman, IMerchant imerchant) {
        this.itemsInSlots = NonNullList.a(3, ItemStack.a);
        this.player = entityhuman;
        this.merchant = imerchant;
    }

    public int getSize() {
        return this.itemsInSlots.size();
    }

    public boolean P_() {
        Iterator iterator = this.itemsInSlots.iterator();

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
        return (ItemStack) this.itemsInSlots.get(i);
    }

    public ItemStack splitStack(int i, int j) {
        ItemStack itemstack = (ItemStack) this.itemsInSlots.get(i);

        if (i == 2 && !itemstack.isEmpty()) {
            return ContainerUtil.a(this.itemsInSlots, i, itemstack.getCount());
        } else {
            ItemStack itemstack1 = ContainerUtil.a(this.itemsInSlots, i, j);

            if (!itemstack1.isEmpty() && this.e(i)) {
                this.i();
            }

            return itemstack1;
        }
    }

    private boolean e(int i) {
        return i == 0 || i == 1;
    }

    public ItemStack splitWithoutUpdate(int i) {
        return ContainerUtil.a(this.itemsInSlots, i);
    }

    public void setItem(int i, ItemStack itemstack) {
        this.itemsInSlots.set(i, itemstack);
        if (!itemstack.isEmpty() && itemstack.getCount() > this.getMaxStackSize()) {
            itemstack.setCount(this.getMaxStackSize());
        }

        if (this.e(i)) {
            this.i();
        }

    }

    public IChatBaseComponent getDisplayName() {
        return new ChatMessage("mob.villager", new Object[0]);
    }

    public boolean hasCustomName() {
        return false;
    }

    @Nullable
    public IChatBaseComponent getCustomName() {
        return null;
    }

    public int getMaxStackSize() {
        return 64;
    }

    public boolean a(EntityHuman entityhuman) {
        return this.merchant.getTrader() == entityhuman;
    }

    public void startOpen(EntityHuman entityhuman) {}

    public void closeContainer(EntityHuman entityhuman) {}

    public boolean b(int i, ItemStack itemstack) {
        return true;
    }

    public void update() {
        this.i();
    }

    public void i() {
        this.recipe = null;
        ItemStack itemstack = (ItemStack) this.itemsInSlots.get(0);
        ItemStack itemstack1 = (ItemStack) this.itemsInSlots.get(1);

        if (itemstack.isEmpty()) {
            itemstack = itemstack1;
            itemstack1 = ItemStack.a;
        }

        if (itemstack.isEmpty()) {
            this.setItem(2, ItemStack.a);
        } else {
            MerchantRecipeList merchantrecipelist = this.merchant.getOffers(this.player);

            if (merchantrecipelist != null) {
                MerchantRecipe merchantrecipe = merchantrecipelist.a(itemstack, itemstack1, this.selectedIndex);

                if (merchantrecipe != null && !merchantrecipe.h()) {
                    this.recipe = merchantrecipe;
                    this.setItem(2, merchantrecipe.getBuyItem3().cloneItemStack());
                } else if (!itemstack1.isEmpty()) {
                    merchantrecipe = merchantrecipelist.a(itemstack1, itemstack, this.selectedIndex);
                    if (merchantrecipe != null && !merchantrecipe.h()) {
                        this.recipe = merchantrecipe;
                        this.setItem(2, merchantrecipe.getBuyItem3().cloneItemStack());
                    } else {
                        this.setItem(2, ItemStack.a);
                    }
                } else {
                    this.setItem(2, ItemStack.a);
                }
            }

            this.merchant.a(this.getItem(2));
        }

    }

    public MerchantRecipe getRecipe() {
        return this.recipe;
    }

    public void d(int i) {
        this.selectedIndex = i;
        this.i();
    }

    public int getProperty(int i) {
        return 0;
    }

    public void setProperty(int i, int j) {}

    public int h() {
        return 0;
    }

    public void clear() {
        this.itemsInSlots.clear();
    }
}
