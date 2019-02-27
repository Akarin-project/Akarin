package net.minecraft.server;

import java.util.Collection;

public class RecipeTippedArrow extends IRecipeComplex {

    public RecipeTippedArrow(MinecraftKey minecraftkey) {
        super(minecraftkey);
    }

    public boolean a(IInventory iinventory, World world) {
        if (iinventory.U_() == 3 && iinventory.n() == 3) {
            for (int i = 0; i < iinventory.U_(); ++i) {
                for (int j = 0; j < iinventory.n(); ++j) {
                    ItemStack itemstack = iinventory.getItem(i + j * iinventory.U_());

                    if (itemstack.isEmpty()) {
                        return false;
                    }

                    Item item = itemstack.getItem();

                    if (i == 1 && j == 1) {
                        if (item != Items.LINGERING_POTION) {
                            return false;
                        }
                    } else if (item != Items.ARROW) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public ItemStack craftItem(IInventory iinventory) {
        ItemStack itemstack = iinventory.getItem(1 + iinventory.U_());

        if (itemstack.getItem() != Items.LINGERING_POTION) {
            return ItemStack.a;
        } else {
            ItemStack itemstack1 = new ItemStack(Items.TIPPED_ARROW, 8);

            PotionUtil.a(itemstack1, PotionUtil.d(itemstack));
            PotionUtil.a(itemstack1, (Collection) PotionUtil.b(itemstack));
            return itemstack1;
        }
    }

    public RecipeSerializer<?> a() {
        return RecipeSerializers.k;
    }
}
