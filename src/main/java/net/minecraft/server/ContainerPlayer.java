package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
// CraftBukkit end

public class ContainerPlayer extends ContainerRecipeBook<InventoryCrafting> {

    private static final String[] d = new String[]{"item/empty_armor_slot_boots", "item/empty_armor_slot_leggings", "item/empty_armor_slot_chestplate", "item/empty_armor_slot_helmet"};
    private static final EnumItemSlot[] e = new EnumItemSlot[]{EnumItemSlot.HEAD, EnumItemSlot.CHEST, EnumItemSlot.LEGS, EnumItemSlot.FEET};
    // CraftBukkit start
    private final InventoryCrafting craftInventory;
    private final InventoryCraftResult resultInventory;
    // CraftBukkit end
    public final boolean c;
    private final EntityHuman owner;
    // CraftBukkit start
    private CraftInventoryView bukkitEntity = null;
    private PlayerInventory player;
    // CraftBukkit end

    public ContainerPlayer(PlayerInventory playerinventory, boolean flag, EntityHuman entityhuman) {
        super((Containers) null, 0);
        this.c = flag;
        this.owner = entityhuman;
        // CraftBukkit start
        this.resultInventory = new InventoryCraftResult(); // CraftBukkit - moved to before InventoryCrafting construction
        this.craftInventory = new InventoryCrafting(this, 2, 2, playerinventory.player); // CraftBukkit - pass player
        this.craftInventory.resultInventory = this.resultInventory; // CraftBukkit - let InventoryCrafting know about its result slot
        this.player = playerinventory; // CraftBukkit - save player
        setTitle(new ChatMessage("container.crafting")); // SPIGOT-4722: Allocate title for player inventory
        // CraftBukkit end
        this.a((Slot) (new SlotResult(playerinventory.player, this.craftInventory, this.resultInventory, 0, 154, 28)));

        int i;
        int j;

        for (i = 0; i < 2; ++i) {
            for (j = 0; j < 2; ++j) {
                this.a(new Slot(this.craftInventory, j + i * 2, 98 + j * 18, 18 + i * 18));
            }
        }

        for (i = 0; i < 4; ++i) {
            final EnumItemSlot enumitemslot = ContainerPlayer.e[i];

            this.a(new Slot(playerinventory, 39 - i, 8, 8 + i * 18) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean isAllowed(ItemStack itemstack) {
                    return enumitemslot == EntityInsentient.h(itemstack);
                }

                @Override
                public boolean isAllowed(EntityHuman entityhuman1) {
                    ItemStack itemstack = this.getItem();

                    return !itemstack.isEmpty() && !entityhuman1.isCreative() && EnchantmentManager.d(itemstack) ? false : super.isAllowed(entityhuman1);
                }
            });
        }

        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.a(new Slot(playerinventory, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.a(new Slot(playerinventory, i, 8 + i * 18, 142));
        }

        this.a(new Slot(playerinventory, 40, 77, 62) {
        });
    }

    @Override
    public void a(AutoRecipeStackManager autorecipestackmanager) {
        this.craftInventory.a(autorecipestackmanager);
    }

    @Override
    public void e() {
        this.resultInventory.clear();
        this.craftInventory.clear();
    }

    @Override
    public boolean a(IRecipe<? super InventoryCrafting> irecipe) {
        return irecipe.a(this.craftInventory, this.owner.world);
    }

    @Override
    public void a(IInventory iinventory) {
        ContainerWorkbench.a(this.windowId, this.owner.world, this.owner, this.craftInventory, this.resultInventory, this); // CraftBukkit
    }

    @Override
    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        this.resultInventory.clear();
        if (!entityhuman.world.isClientSide) {
            this.a(entityhuman, entityhuman.world, (IInventory) this.craftInventory);
        }
    }

    @Override
    public boolean canUse(EntityHuman entityhuman) {
        return true;
    }

    @Override
    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.a;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            EnumItemSlot enumitemslot = EntityInsentient.h(itemstack);

            if (i == 0) {
                if (!this.a(itemstack1, 9, 45, true)) {
                    return ItemStack.a;
                }

                slot.a(itemstack1, itemstack);
            } else if (i >= 1 && i < 5) {
                if (!this.a(itemstack1, 9, 45, false)) {
                    return ItemStack.a;
                }
            } else if (i >= 5 && i < 9) {
                if (!this.a(itemstack1, 9, 45, false)) {
                    return ItemStack.a;
                }
            } else if (enumitemslot.a() == EnumItemSlot.Function.ARMOR && !((Slot) this.slots.get(8 - enumitemslot.b())).hasItem()) {
                int j = 8 - enumitemslot.b();

                if (!this.a(itemstack1, j, j + 1, false)) {
                    return ItemStack.a;
                }
            } else if (enumitemslot == EnumItemSlot.OFFHAND && !((Slot) this.slots.get(45)).hasItem()) {
                if (!this.a(itemstack1, 45, 46, false)) {
                    return ItemStack.a;
                }
            } else if (i >= 9 && i < 36) {
                if (!this.a(itemstack1, 36, 45, false)) {
                    return ItemStack.a;
                }
            } else if (i >= 36 && i < 45) {
                if (!this.a(itemstack1, 9, 36, false)) {
                    return ItemStack.a;
                }
            } else if (!this.a(itemstack1, 9, 45, false)) {
                return ItemStack.a;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.a);
            } else {
                slot.d();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.a;
            }

            ItemStack itemstack2 = slot.a(entityhuman, itemstack1);

            if (i == 0) {
                entityhuman.drop(itemstack2, false);
            }
        }

        return itemstack;
    }

    @Override
    public boolean a(ItemStack itemstack, Slot slot) {
        return slot.inventory != this.resultInventory && super.a(itemstack, slot);
    }

    @Override
    public int f() {
        return 0;
    }

    @Override
    public int g() {
        return this.craftInventory.g();
    }

    @Override
    public int h() {
        return this.craftInventory.f();
    }

    // CraftBukkit start
    @Override
    public CraftInventoryView getBukkitView() {
        if (bukkitEntity != null) {
            return bukkitEntity;
        }

        CraftInventoryCrafting inventory = new CraftInventoryCrafting(this.craftInventory, this.resultInventory);
        bukkitEntity = new CraftInventoryView(this.player.player.getBukkitEntity(), inventory, this);
        return bukkitEntity;
    }
    // CraftBukkit end
}
