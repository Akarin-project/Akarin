package net.minecraft.server;

public class RecipeFireworks extends IRecipeComplex {

    private static final RecipeItemStack a = RecipeItemStack.a(Items.PAPER);
    private static final RecipeItemStack b = RecipeItemStack.a(Items.GUNPOWDER);
    private static final RecipeItemStack c = RecipeItemStack.a(Items.FIREWORK_STAR);

    public RecipeFireworks(MinecraftKey minecraftkey) {
        super(minecraftkey);
    }

    public boolean a(IInventory iinventory, World world) {
        if (!(iinventory instanceof InventoryCrafting)) {
            return false;
        } else {
            boolean flag = false;
            int i = 0;

            for (int j = 0; j < iinventory.getSize(); ++j) {
                ItemStack itemstack = iinventory.getItem(j);

                if (!itemstack.isEmpty()) {
                    if (RecipeFireworks.a.test(itemstack)) {
                        if (flag) {
                            return false;
                        }

                        flag = true;
                    } else if (RecipeFireworks.b.test(itemstack)) {
                        ++i;
                        if (i > 3) {
                            return false;
                        }
                    } else if (!RecipeFireworks.c.test(itemstack)) {
                        return false;
                    }
                }
            }

            return flag && i >= 1;
        }
    }

    public ItemStack craftItem(IInventory iinventory) {
        ItemStack itemstack = new ItemStack(Items.FIREWORK_ROCKET, 3);
        NBTTagCompound nbttagcompound = itemstack.a("Fireworks");
        NBTTagList nbttaglist = new NBTTagList();
        int i = 0;

        for (int j = 0; j < iinventory.getSize(); ++j) {
            ItemStack itemstack1 = iinventory.getItem(j);

            if (!itemstack1.isEmpty()) {
                if (RecipeFireworks.b.test(itemstack1)) {
                    ++i;
                } else if (RecipeFireworks.c.test(itemstack1)) {
                    NBTTagCompound nbttagcompound1 = itemstack1.b("Explosion");

                    if (nbttagcompound1 != null) {
                        nbttaglist.add((NBTBase) nbttagcompound1);
                    }
                }
            }
        }

        nbttagcompound.setByte("Flight", (byte) i);
        if (!nbttaglist.isEmpty()) {
            nbttagcompound.set("Explosions", nbttaglist);
        }

        return itemstack;
    }

    public ItemStack d() {
        return new ItemStack(Items.FIREWORK_ROCKET);
    }

    public RecipeSerializer<?> a() {
        return RecipeSerializers.g;
    }
}
