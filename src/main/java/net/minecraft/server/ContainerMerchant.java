package net.minecraft.server;

import org.bukkit.craftbukkit.inventory.CraftInventoryView; // CraftBukkit

public class ContainerMerchant extends Container {

    private final IMerchant merchant;
    private final InventoryMerchant f;
    private final World g;

    // CraftBukkit start
    private CraftInventoryView bukkitEntity = null;
    private PlayerInventory player;

    @Override
    public CraftInventoryView getBukkitView() {
        if (bukkitEntity == null) {
            bukkitEntity = new CraftInventoryView(this.player.player.getBukkitEntity(), new org.bukkit.craftbukkit.inventory.CraftInventoryMerchant((InventoryMerchant) f), this);
        }
        return bukkitEntity;
    }
    // CraftBukkit end

    public ContainerMerchant(PlayerInventory playerinventory, IMerchant imerchant, World world) {
        this.merchant = imerchant;
        this.g = world;
        this.f = new InventoryMerchant(playerinventory.player, imerchant);
        this.a(new Slot(this.f, 0, 36, 53));
        this.a(new Slot(this.f, 1, 62, 53));
        this.a((Slot) (new SlotMerchantResult(playerinventory.player, imerchant, this.f, 2, 120, 53)));
        this.player = playerinventory; // CraftBukkit - save player

        int i;

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.a(new Slot(playerinventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.a(new Slot(playerinventory, i, 8 + i * 18, 142));
        }

    }

    public InventoryMerchant d() {
        return this.f;
    }

    public void a(IInventory iinventory) {
        this.f.i();
        super.a(iinventory);
    }

    public void d(int i) {
        this.f.d(i);
    }

    public boolean canUse(EntityHuman entityhuman) {
        return this.merchant.getTrader() == entityhuman;
    }

    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.a;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i == 2) {
                if (!this.a(itemstack1, 3, 39, true)) {
                    return ItemStack.a;
                }

                slot.a(itemstack1, itemstack);
            } else if (i != 0 && i != 1) {
                if (i >= 3 && i < 30) {
                    if (!this.a(itemstack1, 30, 39, false)) {
                        return ItemStack.a;
                    }
                } else if (i >= 30 && i < 39 && !this.a(itemstack1, 3, 30, false)) {
                    return ItemStack.a;
                }
            } else if (!this.a(itemstack1, 3, 39, false)) {
                return ItemStack.a;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.a);
            } else {
                slot.f();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.a;
            }

            slot.a(entityhuman, itemstack1);
        }

        return itemstack;
    }

    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        this.merchant.setTradingPlayer((EntityHuman) null);
        super.b(entityhuman);
        if (!this.g.isClientSide) {
            ItemStack itemstack = this.f.splitWithoutUpdate(0);

            if (!itemstack.isEmpty()) {
                entityhuman.drop(itemstack, false);
            }

            itemstack = this.f.splitWithoutUpdate(1);
            if (!itemstack.isEmpty()) {
                entityhuman.drop(itemstack, false);
            }

        }
    }
}
