package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.List;

public class RecipeArmorDye extends ShapelessRecipes implements IRecipe { // CraftBukkit - added extends

    // CraftBukkit start - Delegate to new parent class with bogus info
    public RecipeArmorDye(MinecraftKey minecraftkey) {
        super(minecraftkey, "", new ItemStack(Items.LEATHER_HELMET, 0), NonNullList.a(RecipeItemStack.a, RecipeItemStack.a(Items.BONE_MEAL)));
    }
    // CraftBukkit end

    public boolean a(IInventory iinventory, World world) {
        if (!(iinventory instanceof InventoryCrafting)) {
            return false;
        } else {
            ItemStack itemstack = ItemStack.a;
            List<ItemStack> list = Lists.newArrayList();

            for (int i = 0; i < iinventory.getSize(); ++i) {
                ItemStack itemstack1 = iinventory.getItem(i);

                if (!itemstack1.isEmpty()) {
                    if (itemstack1.getItem() instanceof ItemArmorColorable) {
                        if (!itemstack.isEmpty()) {
                            return false;
                        }

                        itemstack = itemstack1;
                    } else {
                        if (!(itemstack1.getItem() instanceof ItemDye)) {
                            return false;
                        }

                        list.add(itemstack1);
                    }
                }
            }

            return !itemstack.isEmpty() && !list.isEmpty();
        }
    }

    public ItemStack craftItem(IInventory iinventory) {
        ItemStack itemstack = ItemStack.a;
        int[] aint = new int[3];
        int i = 0;
        int j = 0;
        ItemArmorColorable itemarmorcolorable = null;

        int k;
        float f;
        int l;

        for (k = 0; k < iinventory.getSize(); ++k) {
            ItemStack itemstack1 = iinventory.getItem(k);

            if (!itemstack1.isEmpty()) {
                Item item = itemstack1.getItem();

                if (item instanceof ItemArmorColorable) {
                    itemarmorcolorable = (ItemArmorColorable) item;
                    if (!itemstack.isEmpty()) {
                        return ItemStack.a;
                    }

                    itemstack = itemstack1.cloneItemStack();
                    itemstack.setCount(1);
                    if (itemarmorcolorable.e(itemstack1)) {
                        int i1 = itemarmorcolorable.f(itemstack);

                        f = (float) (i1 >> 16 & 255) / 255.0F;
                        float f1 = (float) (i1 >> 8 & 255) / 255.0F;
                        float f2 = (float) (i1 & 255) / 255.0F;

                        i = (int) ((float) i + Math.max(f, Math.max(f1, f2)) * 255.0F);
                        aint[0] = (int) ((float) aint[0] + f * 255.0F);
                        aint[1] = (int) ((float) aint[1] + f1 * 255.0F);
                        aint[2] = (int) ((float) aint[2] + f2 * 255.0F);
                        ++j;
                    }
                } else {
                    if (!(item instanceof ItemDye)) {
                        return ItemStack.a;
                    }

                    float[] afloat = ((ItemDye) item).d().d();
                    int j1 = (int) (afloat[0] * 255.0F);

                    l = (int) (afloat[1] * 255.0F);
                    int k1 = (int) (afloat[2] * 255.0F);

                    i += Math.max(j1, Math.max(l, k1));
                    aint[0] += j1;
                    aint[1] += l;
                    aint[2] += k1;
                    ++j;
                }
            }
        }

        if (itemarmorcolorable == null) {
            return ItemStack.a;
        } else {
            k = aint[0] / j;
            int l1 = aint[1] / j;
            int i2 = aint[2] / j;
            float f3 = (float) i / (float) j;

            f = (float) Math.max(k, Math.max(l1, i2));
            k = (int) ((float) k * f3 / f);
            l1 = (int) ((float) l1 * f3 / f);
            i2 = (int) ((float) i2 * f3 / f);
            l = (k << 8) + l1;
            l = (l << 8) + i2;
            itemarmorcolorable.a(itemstack, l);
            return itemstack;
        }
    }

    public RecipeSerializer<?> a() {
        return RecipeSerializers.c;
    }
}
