package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftInventoryStonecutter;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.Player;
// CraftBukkit end

public class ContainerStonecutter extends Container {

    static final ImmutableList<Item> c = ImmutableList.of(Items.b, Items.aq, Items.ge, Items.eq, Items.m, Items.cX, Items.bL, Items.ds, Items.gm, Items.bU, Items.fX, Items.fY, new Item[]{Items.fZ, Items.g, Items.h, Items.c, Items.d, Items.e, Items.f, Items.cY, Items.bO, Items.bJ, Items.bI, Items.bH, Items.dx, Items.dy, Items.bK, Items.as, Items.gg});
    private final ContainerAccess containerAccess;
    private final ContainerProperty containerProperty;
    private final World world;
    private List<RecipeStonecutting> j;
    private ItemStack k;
    private long l;
    final Slot d;
    final Slot e;
    private Runnable m;
    public final IInventory inventory;
    private final InventoryCraftResult resultInventory;
    // CraftBukkit start
    private CraftInventoryView bukkitEntity = null;
    private Player player;

    @Override
    public CraftInventoryView getBukkitView() {
        if (bukkitEntity != null) {
            return bukkitEntity;
        }

        CraftInventoryStonecutter inventory = new CraftInventoryStonecutter(this.inventory, this.resultInventory);
        bukkitEntity = new CraftInventoryView(this.player, inventory, this);
        return bukkitEntity;
    }
    // CraftBukkit end

    public ContainerStonecutter(int i, PlayerInventory playerinventory) {
        this(i, playerinventory, ContainerAccess.a);
    }

    public ContainerStonecutter(int i, PlayerInventory playerinventory, final ContainerAccess containeraccess) {
        super(Containers.STONECUTTER, i);
        this.containerProperty = ContainerProperty.a();
        this.j = Lists.newArrayList();
        this.k = ItemStack.a;
        this.m = () -> {
        };
        this.inventory = new InventorySubcontainer(1) {
            @Override
            public void update() {
                super.update();
                ContainerStonecutter.this.a((IInventory) this);
                ContainerStonecutter.this.m.run();
            }
        };
        this.resultInventory = new InventoryCraftResult();
        this.containerAccess = containeraccess;
        this.world = playerinventory.player.world;
        this.d = this.a(new Slot(this.inventory, 0, 20, 33));
        this.e = this.a(new Slot(this.resultInventory, 1, 143, 33) {
            @Override
            public boolean isAllowed(ItemStack itemstack) {
                return false;
            }

            @Override
            public ItemStack a(EntityHuman entityhuman, ItemStack itemstack) {
                ItemStack itemstack1 = ContainerStonecutter.this.d.a(1);

                if (!itemstack1.isEmpty()) {
                    ContainerStonecutter.this.i();
                }

                itemstack.getItem().b(itemstack, entityhuman.world, entityhuman);
                containeraccess.a((world, blockposition) -> {
                    long j = world.getTime();

                    if (ContainerStonecutter.this.l != j) {
                        world.playSound((EntityHuman) null, blockposition, SoundEffects.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        ContainerStonecutter.this.l = j;
                    }

                });
                return super.a(entityhuman, itemstack);
            }
        });

        int j;

        for (j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.a(new Slot(playerinventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.a(new Slot(playerinventory, j, 8 + j * 18, 142));
        }

        this.a(this.containerProperty);
        player = (Player) playerinventory.player.getBukkitEntity(); // CraftBukkit
    }

    @Override
    public boolean canUse(EntityHuman entityhuman) {
        if (!this.checkReachable) return true; // CraftBukkit
        return a(this.containerAccess, entityhuman, Blocks.STONECUTTER);
    }

    @Override
    public boolean a(EntityHuman entityhuman, int i) {
        if (i >= 0 && i < this.j.size()) {
            this.containerProperty.set(i);
            this.i();
        }

        return true;
    }

    @Override
    public void a(IInventory iinventory) {
        ItemStack itemstack = this.d.getItem();

        if (itemstack.getItem() != this.k.getItem()) {
            this.k = itemstack.cloneItemStack();
            this.a(iinventory, itemstack);
        }

    }

    private void a(IInventory iinventory, ItemStack itemstack) {
        this.j.clear();
        this.containerProperty.set(-1);
        this.e.set(ItemStack.a);
        if (!itemstack.isEmpty()) {
            this.j = this.world.getCraftingManager().b(Recipes.STONECUTTING, iinventory, this.world);
        }

    }

    private void i() {
        if (!this.j.isEmpty()) {
            RecipeStonecutting recipestonecutting = (RecipeStonecutting) this.j.get(this.containerProperty.get());

            this.e.set(recipestonecutting.a(this.inventory));
        } else {
            this.e.set(ItemStack.a);
        }

        this.c();
    }

    @Override
    public Containers<?> getType() {
        return Containers.STONECUTTER;
    }

    @Override
    public boolean a(ItemStack itemstack, Slot slot) {
        return false;
    }

    @Override
    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.a;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            Item item = itemstack1.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i == 1) {
                item.b(itemstack1, entityhuman.world, entityhuman);
                if (!this.a(itemstack1, 2, 38, true)) {
                    return ItemStack.a;
                }

                slot.a(itemstack1, itemstack);
            } else if (i == 0) {
                if (!this.a(itemstack1, 2, 38, false)) {
                    return ItemStack.a;
                }
            } else if (ContainerStonecutter.c.contains(item)) {
                if (!this.a(itemstack1, 0, 1, false)) {
                    return ItemStack.a;
                }
            } else if (i >= 2 && i < 29) {
                if (!this.a(itemstack1, 29, 38, false)) {
                    return ItemStack.a;
                }
            } else if (i >= 29 && i < 38 && !this.a(itemstack1, 2, 29, false)) {
                return ItemStack.a;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.a);
            }

            slot.d();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.a;
            }

            slot.a(entityhuman, itemstack1);
            this.c();
        }

        return itemstack;
    }

    @Override
    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        this.resultInventory.splitWithoutUpdate(1);
        this.containerAccess.a((world, blockposition) -> {
            this.a(entityhuman, entityhuman.world, this.inventory);
        });
    }
}
