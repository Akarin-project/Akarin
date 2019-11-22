package net.minecraft.server;

public class RecipeFireworks extends ShapelessRecipes { // CraftBukkit - added extends

    private static final RecipeItemStack a = RecipeItemStack.a(Items.PAPER);
    private static final RecipeItemStack b = RecipeItemStack.a(Items.GUNPOWDER);
    private static final RecipeItemStack c = RecipeItemStack.a(Items.FIREWORK_STAR);

    // CraftBukkit start - Delegate to new parent class with bogus info
    public RecipeFireworks(MinecraftKey minecraftkey) {
        super(minecraftkey, "", new ItemStack(Items.FIREWORK_ROCKET, 3), NonNullList.a(RecipeItemStack.a, RecipeItemStack.a(Items.GUNPOWDER)));
    }
    // CraftBukkit end

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        boolean flag = false;
        int i = 0;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack = inventorycrafting.getItem(j);

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

    public ItemStack a(InventoryCrafting inventorycrafting) {
        ItemStack itemstack = new ItemStack(Items.FIREWORK_ROCKET, 3);
        NBTTagCompound nbttagcompound = itemstack.a("Fireworks");
        NBTTagList nbttaglist = new NBTTagList();
        int i = 0;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

            if (!itemstack1.isEmpty()) {
                if (RecipeFireworks.b.test(itemstack1)) {
                    ++i;
                } else if (RecipeFireworks.c.test(itemstack1)) {
                    NBTTagCompound nbttagcompound1 = itemstack1.b("Explosion");

                    if (nbttagcompound1 != null) {
                        nbttaglist.add(nbttagcompound1);
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

    @Override
    public ItemStack c() {
        return new ItemStack(Items.FIREWORK_ROCKET);
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.g;
    }
}
