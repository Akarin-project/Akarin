package net.minecraft.server;

import java.util.List;
import java.util.Random;

public class ContainerEnchantTable extends Container {

    public IInventory enchantSlots = new InventorySubcontainer(new ChatComponentText("Enchant"), 2) {
        public int getMaxStackSize() {
            return 64;
        }

        public void update() {
            super.update();
            ContainerEnchantTable.this.a((IInventory) this);
        }
    };
    public World world;
    private final BlockPosition position;
    private final Random l = new Random();
    public int f;
    public int[] costs = new int[3];
    public int[] h = new int[] { -1, -1, -1};
    public int[] i = new int[] { -1, -1, -1};

    public ContainerEnchantTable(PlayerInventory playerinventory, World world, BlockPosition blockposition) {
        this.world = world;
        this.position = blockposition;
        this.f = playerinventory.player.du();
        this.a(new Slot(this.enchantSlots, 0, 15, 47) {
            public boolean isAllowed(ItemStack itemstack) {
                return true;
            }

            public int getMaxStackSize() {
                return 1;
            }
        });
        this.a(new Slot(this.enchantSlots, 1, 35, 47) {
            public boolean isAllowed(ItemStack itemstack) {
                return itemstack.getItem() == Items.LAPIS_LAZULI;
            }
        });

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

    protected void c(ICrafting icrafting) {
        icrafting.setContainerData(this, 0, this.costs[0]);
        icrafting.setContainerData(this, 1, this.costs[1]);
        icrafting.setContainerData(this, 2, this.costs[2]);
        icrafting.setContainerData(this, 3, this.f & -16);
        icrafting.setContainerData(this, 4, this.h[0]);
        icrafting.setContainerData(this, 5, this.h[1]);
        icrafting.setContainerData(this, 6, this.h[2]);
        icrafting.setContainerData(this, 7, this.i[0]);
        icrafting.setContainerData(this, 8, this.i[1]);
        icrafting.setContainerData(this, 9, this.i[2]);
    }

    public void addSlotListener(ICrafting icrafting) {
        super.addSlotListener(icrafting);
        this.c(icrafting);
    }

    public void b() {
        super.b();

        for (int i = 0; i < this.listeners.size(); ++i) {
            ICrafting icrafting = (ICrafting) this.listeners.get(i);

            this.c(icrafting);
        }

    }

    public void a(IInventory iinventory) {
        if (iinventory == this.enchantSlots) {
            ItemStack itemstack = iinventory.getItem(0);
            int i;

            if (!itemstack.isEmpty() && itemstack.canEnchant()) {
                if (!this.world.isClientSide) {
                    i = 0;

                    int j;

                    for (j = -1; j <= 1; ++j) {
                        for (int k = -1; k <= 1; ++k) {
                            if ((j != 0 || k != 0) && this.world.isEmpty(this.position.a(k, 0, j)) && this.world.isEmpty(this.position.a(k, 1, j))) {
                                if (this.world.getType(this.position.a(k * 2, 0, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                                    ++i;
                                }

                                if (this.world.getType(this.position.a(k * 2, 1, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                                    ++i;
                                }

                                if (k != 0 && j != 0) {
                                    if (this.world.getType(this.position.a(k * 2, 0, j)).getBlock() == Blocks.BOOKSHELF) {
                                        ++i;
                                    }

                                    if (this.world.getType(this.position.a(k * 2, 1, j)).getBlock() == Blocks.BOOKSHELF) {
                                        ++i;
                                    }

                                    if (this.world.getType(this.position.a(k, 0, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                                        ++i;
                                    }

                                    if (this.world.getType(this.position.a(k, 1, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                                        ++i;
                                    }
                                }
                            }
                        }
                    }

                    this.l.setSeed((long) this.f);

                    for (j = 0; j < 3; ++j) {
                        this.costs[j] = EnchantmentManager.a(this.l, j, i, itemstack);
                        this.h[j] = -1;
                        this.i[j] = -1;
                        if (this.costs[j] < j + 1) {
                            this.costs[j] = 0;
                        }
                    }

                    for (j = 0; j < 3; ++j) {
                        if (this.costs[j] > 0) {
                            List<WeightedRandomEnchant> list = this.a(itemstack, j, this.costs[j]);

                            if (list != null && !list.isEmpty()) {
                                WeightedRandomEnchant weightedrandomenchant = (WeightedRandomEnchant) list.get(this.l.nextInt(list.size()));

                                this.h[j] = IRegistry.ENCHANTMENT.a((Object) weightedrandomenchant.enchantment);
                                this.i[j] = weightedrandomenchant.level;
                            }
                        }
                    }

                    this.b();
                }
            } else {
                for (i = 0; i < 3; ++i) {
                    this.costs[i] = 0;
                    this.h[i] = -1;
                    this.i[i] = -1;
                }
            }
        }

    }

    public boolean a(EntityHuman entityhuman, int i) {
        ItemStack itemstack = this.enchantSlots.getItem(0);
        ItemStack itemstack1 = this.enchantSlots.getItem(1);
        int j = i + 1;

        if ((itemstack1.isEmpty() || itemstack1.getCount() < j) && !entityhuman.abilities.canInstantlyBuild) {
            return false;
        } else if (this.costs[i] > 0 && !itemstack.isEmpty() && (entityhuman.expLevel >= j && entityhuman.expLevel >= this.costs[i] || entityhuman.abilities.canInstantlyBuild)) {
            if (!this.world.isClientSide) {
                List<WeightedRandomEnchant> list = this.a(itemstack, i, this.costs[i]);

                if (!list.isEmpty()) {
                    entityhuman.enchantDone(itemstack, j);
                    boolean flag = itemstack.getItem() == Items.BOOK;

                    if (flag) {
                        itemstack = new ItemStack(Items.ENCHANTED_BOOK);
                        this.enchantSlots.setItem(0, itemstack);
                    }

                    for (int k = 0; k < list.size(); ++k) {
                        WeightedRandomEnchant weightedrandomenchant = (WeightedRandomEnchant) list.get(k);

                        if (flag) {
                            ItemEnchantedBook.a(itemstack, weightedrandomenchant);
                        } else {
                            itemstack.addEnchantment(weightedrandomenchant.enchantment, weightedrandomenchant.level);
                        }
                    }

                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack1.subtract(j);
                        if (itemstack1.isEmpty()) {
                            this.enchantSlots.setItem(1, ItemStack.a);
                        }
                    }

                    entityhuman.a(StatisticList.ENCHANT_ITEM);
                    if (entityhuman instanceof EntityPlayer) {
                        CriterionTriggers.i.a((EntityPlayer) entityhuman, itemstack, j);
                    }

                    this.enchantSlots.update();
                    this.f = entityhuman.du();
                    this.a(this.enchantSlots);
                    this.world.a((EntityHuman) null, this.position, SoundEffects.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private List<WeightedRandomEnchant> a(ItemStack itemstack, int i, int j) {
        this.l.setSeed((long) (this.f + i));
        List<WeightedRandomEnchant> list = EnchantmentManager.b(this.l, itemstack, j, false);

        if (itemstack.getItem() == Items.BOOK && list.size() > 1) {
            list.remove(this.l.nextInt(list.size()));
        }

        return list;
    }

    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        if (!this.world.isClientSide) {
            this.a(entityhuman, entityhuman.world, this.enchantSlots);
        }
    }

    public boolean canUse(EntityHuman entityhuman) {
        return this.world.getType(this.position).getBlock() != Blocks.ENCHANTING_TABLE ? false : entityhuman.d((double) this.position.getX() + 0.5D, (double) this.position.getY() + 0.5D, (double) this.position.getZ() + 0.5D) <= 64.0D;
    }

    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        ItemStack itemstack = ItemStack.a;
        Slot slot = (Slot) this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i == 0) {
                if (!this.a(itemstack1, 2, 38, true)) {
                    return ItemStack.a;
                }
            } else if (i == 1) {
                if (!this.a(itemstack1, 2, 38, true)) {
                    return ItemStack.a;
                }
            } else if (itemstack1.getItem() == Items.LAPIS_LAZULI) {
                if (!this.a(itemstack1, 1, 2, true)) {
                    return ItemStack.a;
                }
            } else {
                if (((Slot) this.slots.get(0)).hasItem() || !((Slot) this.slots.get(0)).isAllowed(itemstack1)) {
                    return ItemStack.a;
                }

                if (itemstack1.hasTag() && itemstack1.getCount() == 1) {
                    ((Slot) this.slots.get(0)).set(itemstack1.cloneItemStack());
                    itemstack1.setCount(0);
                } else if (!itemstack1.isEmpty()) {
                    ((Slot) this.slots.get(0)).set(new ItemStack(itemstack1.getItem()));
                    itemstack1.subtract(1);
                }
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
