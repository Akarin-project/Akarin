package net.minecraft.server;

public class RecipiesShield extends IRecipeComplex {

    public RecipiesShield(MinecraftKey minecraftkey) {
        super(minecraftkey);
    }

    public boolean a(IInventory iinventory, World world) {
        if (!(iinventory instanceof InventoryCrafting)) {
            return false;
        } else {
            ItemStack itemstack = ItemStack.a;
            ItemStack itemstack1 = ItemStack.a;

            for (int i = 0; i < iinventory.getSize(); ++i) {
                ItemStack itemstack2 = iinventory.getItem(i);

                if (!itemstack2.isEmpty()) {
                    if (itemstack2.getItem() instanceof ItemBanner) {
                        if (!itemstack1.isEmpty()) {
                            return false;
                        }

                        itemstack1 = itemstack2;
                    } else {
                        if (itemstack2.getItem() != Items.SHIELD) {
                            return false;
                        }

                        if (!itemstack.isEmpty()) {
                            return false;
                        }

                        if (itemstack2.b("BlockEntityTag") != null) {
                            return false;
                        }

                        itemstack = itemstack2;
                    }
                }
            }

            if (!itemstack.isEmpty() && !itemstack1.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public ItemStack craftItem(IInventory iinventory) {
        ItemStack itemstack = ItemStack.a;
        ItemStack itemstack1 = ItemStack.a;

        for (int i = 0; i < iinventory.getSize(); ++i) {
            ItemStack itemstack2 = iinventory.getItem(i);

            if (!itemstack2.isEmpty()) {
                if (itemstack2.getItem() instanceof ItemBanner) {
                    itemstack = itemstack2;
                } else if (itemstack2.getItem() == Items.SHIELD) {
                    itemstack1 = itemstack2.cloneItemStack();
                }
            }
        }

        if (itemstack1.isEmpty()) {
            return itemstack1;
        } else {
            NBTTagCompound nbttagcompound = itemstack.b("BlockEntityTag");
            NBTTagCompound nbttagcompound1 = nbttagcompound == null ? new NBTTagCompound() : nbttagcompound.clone();

            nbttagcompound1.setInt("Base", ((ItemBanner) itemstack.getItem()).b().getColorIndex());
            itemstack1.a("BlockEntityTag", (NBTBase) nbttagcompound1);
            return itemstack1;
        }
    }

    public RecipeSerializer<?> a() {
        return RecipeSerializers.n;
    }
}
