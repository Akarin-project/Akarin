package net.minecraft.server;

import org.bukkit.craftbukkit.inventory.CraftMerchantRecipe; // CraftBukkit

public class MerchantRecipe {

    public ItemStack buyingItem1;
    public ItemStack buyingItem2;
    public ItemStack sellingItem;
    public int uses;
    public int maxUses;
    public boolean rewardExp;
    // CraftBukkit start
    private CraftMerchantRecipe bukkitHandle;

    public CraftMerchantRecipe asBukkit() {
        return (bukkitHandle == null) ? bukkitHandle = new CraftMerchantRecipe(this) : bukkitHandle;
    }

    public MerchantRecipe(ItemStack itemstack, ItemStack itemstack1, ItemStack itemstack2, int i, int j, CraftMerchantRecipe bukkit) {
        this(itemstack, itemstack1, itemstack2, i, j);
        this.bukkitHandle = bukkit;
    }
    // CraftBukkit end

    public MerchantRecipe(NBTTagCompound nbttagcompound) {
        this.buyingItem1 = ItemStack.a;
        this.buyingItem2 = ItemStack.a;
        this.sellingItem = ItemStack.a;
        this.a(nbttagcompound);
    }

    public MerchantRecipe(ItemStack itemstack, ItemStack itemstack1, ItemStack itemstack2) {
        this(itemstack, itemstack1, itemstack2, 0, 7);
    }

    public MerchantRecipe(ItemStack itemstack, ItemStack itemstack1, ItemStack itemstack2, int i, int j) {
        this.buyingItem1 = ItemStack.a;
        this.buyingItem2 = ItemStack.a;
        this.sellingItem = ItemStack.a;
        this.buyingItem1 = itemstack;
        this.buyingItem2 = itemstack1;
        this.sellingItem = itemstack2;
        this.uses = i;
        this.maxUses = j;
        this.rewardExp = true;
    }

    public MerchantRecipe(ItemStack itemstack, ItemStack itemstack1) {
        this(itemstack, ItemStack.a, itemstack1);
    }

    public MerchantRecipe(ItemStack itemstack, Item item) {
        this(itemstack, new ItemStack(item));
    }

    public ItemStack getBuyItem1() {
        return this.buyingItem1;
    }

    public ItemStack getBuyItem2() {
        return this.buyingItem2;
    }

    public boolean hasSecondItem() {
        return !this.buyingItem2.isEmpty();
    }

    public ItemStack getBuyItem3() {
        return this.sellingItem;
    }

    public int e() {
        return this.uses;
    }

    public int f() {
        return this.maxUses;
    }

    public void increaseUses() {
        ++this.uses;
    }

    public void a(int i) {
        this.maxUses += i;
    }

    public boolean h() {
        return this.uses >= this.maxUses;
    }

    public boolean j() {
        return this.rewardExp;
    }

    public void a(NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("buy");

        this.buyingItem1 = ItemStack.a(nbttagcompound1);
        NBTTagCompound nbttagcompound2 = nbttagcompound.getCompound("sell");

        this.sellingItem = ItemStack.a(nbttagcompound2);
        if (nbttagcompound.hasKeyOfType("buyB", 10)) {
            this.buyingItem2 = ItemStack.a(nbttagcompound.getCompound("buyB"));
        }

        if (nbttagcompound.hasKeyOfType("uses", 99)) {
            this.uses = nbttagcompound.getInt("uses");
        }

        if (nbttagcompound.hasKeyOfType("maxUses", 99)) {
            this.maxUses = nbttagcompound.getInt("maxUses");
        } else {
            this.maxUses = 7;
        }

        if (nbttagcompound.hasKeyOfType("rewardExp", 1)) {
            this.rewardExp = nbttagcompound.getBoolean("rewardExp");
        } else {
            this.rewardExp = true;
        }

    }

    public NBTTagCompound k() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.set("buy", this.buyingItem1.save(new NBTTagCompound()));
        nbttagcompound.set("sell", this.sellingItem.save(new NBTTagCompound()));
        if (!this.buyingItem2.isEmpty()) {
            nbttagcompound.set("buyB", this.buyingItem2.save(new NBTTagCompound()));
        }

        nbttagcompound.setInt("uses", this.uses);
        nbttagcompound.setInt("maxUses", this.maxUses);
        nbttagcompound.setBoolean("rewardExp", this.rewardExp);
        return nbttagcompound;
    }
}
