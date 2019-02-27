package net.minecraft.server;

public class RecipeShulkerBox extends IRecipeComplex {

    public RecipeShulkerBox(MinecraftKey minecraftkey) {
        super(minecraftkey);
    }

    public boolean a(IInventory iinventory, World world) {
        if (!(iinventory instanceof InventoryCrafting)) {
            return false;
        } else {
            int i = 0;
            int j = 0;

            for (int k = 0; k < iinventory.getSize(); ++k) {
                ItemStack itemstack = iinventory.getItem(k);

                if (!itemstack.isEmpty()) {
                    if (Block.asBlock(itemstack.getItem()) instanceof BlockShulkerBox) {
                        ++i;
                    } else {
                        if (!(itemstack.getItem() instanceof ItemDye)) {
                            return false;
                        }

                        ++j;
                    }

                    if (j > 1 || i > 1) {
                        return false;
                    }
                }
            }

            return i == 1 && j == 1;
        }
    }

    public ItemStack craftItem(IInventory iinventory) {
        ItemStack itemstack = ItemStack.a;
        ItemDye itemdye = (ItemDye) Items.BONE_MEAL;

        for (int i = 0; i < iinventory.getSize(); ++i) {
            ItemStack itemstack1 = iinventory.getItem(i);

            if (!itemstack1.isEmpty()) {
                Item item = itemstack1.getItem();

                if (Block.asBlock(item) instanceof BlockShulkerBox) {
                    itemstack = itemstack1;
                } else if (item instanceof ItemDye) {
                    itemdye = (ItemDye) item;
                }
            }
        }

        ItemStack itemstack2 = BlockShulkerBox.b(itemdye.d());

        if (itemstack.hasTag()) {
            itemstack2.setTag(itemstack.getTag().clone());
        }

        return itemstack2;
    }

    public RecipeSerializer<?> a() {
        return RecipeSerializers.o;
    }
}
