package net.minecraft.server;

public class ContainerChest extends Container {

    private final IInventory container;
    private final int f;

    public ContainerChest(IInventory iinventory, IInventory iinventory1, EntityHuman entityhuman) {
        this.container = iinventory1;
        this.f = iinventory1.getSize() / 9;
        iinventory1.startOpen(entityhuman);
        int i = (this.f - 4) * 18;

        int j;
        int k;

        for (j = 0; j < this.f; ++j) {
            for (k = 0; k < 9; ++k) {
                this.a(new Slot(iinventory1, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.a(new Slot(iinventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.a(new Slot(iinventory, j, 8 + j * 18, 161 + i));
        }

    }

    public boolean canUse(EntityHuman entityhuman) {
        return this.container.a(entityhuman);
    }

    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.a;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i < this.f * 9) {
                if (!this.a(itemstack1, this.f * 9, this.slots.size(), true)) {
                    return ItemStack.a;
                }
            } else if (!this.a(itemstack1, 0, this.f * 9, false)) {
                return ItemStack.a;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.a);
            } else {
                slot.f();
            }
        }

        return itemstack;
    }

    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        this.container.closeContainer(entityhuman);
    }

    public IInventory d() {
        return this.container;
    }
}
