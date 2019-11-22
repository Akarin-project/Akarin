package net.minecraft.server;

public class RecipeShulkerBox extends ShapelessRecipes { // CraftBukkit - added extends

    // CraftBukkit start - Delegate to new parent class with bogus info
    public RecipeShulkerBox(MinecraftKey minecraftkey) {
        super(minecraftkey, "", new ItemStack(Blocks.WHITE_SHULKER_BOX), NonNullList.a(RecipeItemStack.a, RecipeItemStack.a(Items.BONE_MEAL)));
    }
    // CraftBukkit end

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        int i = 0;
        int j = 0;

        for (int k = 0; k < inventorycrafting.getSize(); ++k) {
            ItemStack itemstack = inventorycrafting.getItem(k);

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

    public ItemStack a(InventoryCrafting inventorycrafting) {
        ItemStack itemstack = ItemStack.a;
        ItemDye itemdye = (ItemDye) Items.WHITE_DYE;

        for (int i = 0; i < inventorycrafting.getSize(); ++i) {
            ItemStack itemstack1 = inventorycrafting.getItem(i);

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

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.m;
    }
}
