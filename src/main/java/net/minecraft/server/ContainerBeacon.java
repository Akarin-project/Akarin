package net.minecraft.server;

import org.bukkit.craftbukkit.inventory.CraftInventoryView; // CraftBukkit

public class ContainerBeacon extends Container {

    private final IInventory beacon;
    private final ContainerBeacon.SlotBeacon f;
    // CraftBukkit start
    private CraftInventoryView bukkitEntity = null;
    private PlayerInventory player;
    // CraftBukkit end

    public ContainerBeacon(IInventory iinventory, IInventory iinventory1) {
        player = (PlayerInventory) iinventory; // CraftBukkit - TODO: check this
        this.beacon = iinventory1;
        this.f = new ContainerBeacon.SlotBeacon(iinventory1, 0, 136, 110);
        this.a((Slot) this.f);
        boolean flag = true;
        boolean flag1 = true;

        int i;

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.a(new Slot(iinventory, j + i * 9 + 9, 36 + j * 18, 137 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.a(new Slot(iinventory, i, 36 + i * 18, 195));
        }

    }

    public void addSlotListener(ICrafting icrafting) {
        super.addSlotListener(icrafting);
        icrafting.setContainerData(this, this.beacon);
    }

    public IInventory d() {
        return this.beacon;
    }

    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        if (!entityhuman.world.isClientSide) {
            ItemStack itemstack = this.f.a(this.f.getMaxStackSize());

            if (!itemstack.isEmpty()) {
                entityhuman.drop(itemstack, false);
            }

        }
    }

    public boolean canUse(EntityHuman entityhuman) {
        if (!this.checkReachable) return true; // CraftBukkit
        return this.beacon.a(entityhuman);
    }

    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.a;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i == 0) {
                if (!this.a(itemstack1, 1, 37, true)) {
                    return ItemStack.a;
                }

                slot.a(itemstack1, itemstack);
            } else if (!this.f.hasItem() && this.f.isAllowed(itemstack1) && itemstack1.getCount() == 1) {
                if (!this.a(itemstack1, 0, 1, false)) {
                    return ItemStack.a;
                }
            } else if (i >= 1 && i < 28) {
                if (!this.a(itemstack1, 28, 37, false)) {
                    return ItemStack.a;
                }
            } else if (i >= 28 && i < 37) {
                if (!this.a(itemstack1, 1, 28, false)) {
                    return ItemStack.a;
                }
            } else if (!this.a(itemstack1, 1, 37, false)) {
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

    class SlotBeacon extends Slot {

        public SlotBeacon(IInventory iinventory, int i, int j, int k) {
            super(iinventory, i, j, k);
        }

        public boolean isAllowed(ItemStack itemstack) {
            Item item = itemstack.getItem();

            return item == Items.EMERALD || item == Items.DIAMOND || item == Items.GOLD_INGOT || item == Items.IRON_INGOT;
        }

        public int getMaxStackSize() {
            return 1;
        }
    }

    // CraftBukkit start
    @Override
    public CraftInventoryView getBukkitView() {
        if (bukkitEntity != null) {
            return bukkitEntity;
        }

        org.bukkit.craftbukkit.inventory.CraftInventory inventory = new org.bukkit.craftbukkit.inventory.CraftInventoryBeacon((TileEntityBeacon) this.beacon); // TODO - check this
        bukkitEntity = new CraftInventoryView(this.player.player.getBukkitEntity(), inventory, this);
        return bukkitEntity;
    }
    // CraftBukkit end
}
