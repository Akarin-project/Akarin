package net.minecraft.server;

public class ContainerDispenser extends Container {

    public final IInventory items;

    public ContainerDispenser(IInventory iinventory, IInventory iinventory1) {
        this.items = iinventory1;

        int i;
        int j;

        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 3; ++j) {
                this.a(new Slot(iinventory1, j + i * 3, 62 + j * 18, 17 + i * 18));
            }
        }

        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.a(new Slot(iinventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.a(new Slot(iinventory, i, 8 + i * 18, 142));
        }

    }

    public boolean canUse(EntityHuman entityhuman) {
        return this.items.a(entityhuman);
    }

    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.a;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i < 9) {
                if (!this.a(itemstack1, 9, 45, true)) {
                    return ItemStack.a;
                }
            } else if (!this.a(itemstack1, 0, 9, false)) {
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
}
