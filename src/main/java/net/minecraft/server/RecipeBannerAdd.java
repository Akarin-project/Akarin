package net.minecraft.server;

import javax.annotation.Nullable;

public class RecipeBannerAdd extends ShapelessRecipes implements IRecipe { // CraftBukkit - added extends

    // CraftBukkit start - Delegate to new parent class with bogus info
    public RecipeBannerAdd(MinecraftKey minecraftkey) {
        super(minecraftkey, "", new ItemStack(Items.WHITE_BANNER, 0), NonNullList.a(RecipeItemStack.a, RecipeItemStack.a(Items.WHITE_BANNER)));
    }
    // CraftBukkit end

    public boolean a(IInventory iinventory, World world) {
        if (!(iinventory instanceof InventoryCrafting)) {
            return false;
        } else {
            boolean flag = false;

            for (int i = 0; i < iinventory.getSize(); ++i) {
                ItemStack itemstack = iinventory.getItem(i);

                if (itemstack.getItem() instanceof ItemBanner) {
                    if (flag) {
                        return false;
                    }

                    if (TileEntityBanner.a(itemstack) >= 6) {
                        return false;
                    }

                    flag = true;
                }
            }

            return flag && this.c(iinventory) != null;
        }
    }

    public ItemStack craftItem(IInventory iinventory) {
        ItemStack itemstack = ItemStack.a;

        for (int i = 0; i < iinventory.getSize(); ++i) {
            ItemStack itemstack1 = iinventory.getItem(i);

            if (!itemstack1.isEmpty() && itemstack1.getItem() instanceof ItemBanner) {
                itemstack = itemstack1.cloneItemStack();
                itemstack.setCount(1);
                break;
            }
        }

        EnumBannerPatternType enumbannerpatterntype = this.c(iinventory);

        if (enumbannerpatterntype != null) {
            EnumColor enumcolor = EnumColor.WHITE;

            for (int j = 0; j < iinventory.getSize(); ++j) {
                Item item = iinventory.getItem(j).getItem();

                if (item instanceof ItemDye) {
                    enumcolor = ((ItemDye) item).d();
                    break;
                }
            }

            NBTTagCompound nbttagcompound = itemstack.a("BlockEntityTag");
            NBTTagList nbttaglist;

            if (nbttagcompound.hasKeyOfType("Patterns", 9)) {
                nbttaglist = nbttagcompound.getList("Patterns", 10);
            } else {
                nbttaglist = new NBTTagList();
                nbttagcompound.set("Patterns", nbttaglist);
            }

            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            nbttagcompound1.setString("Pattern", enumbannerpatterntype.b());
            nbttagcompound1.setInt("Color", enumcolor.getColorIndex());
            nbttaglist.add((NBTBase) nbttagcompound1);
        }

        return itemstack;
    }

    @Nullable
    private EnumBannerPatternType c(IInventory iinventory) {
        EnumBannerPatternType[] aenumbannerpatterntype = EnumBannerPatternType.values();
        int i = aenumbannerpatterntype.length;

        for (int j = 0; j < i; ++j) {
            EnumBannerPatternType enumbannerpatterntype = aenumbannerpatterntype[j];

            if (enumbannerpatterntype.d()) {
                boolean flag = true;
                int k;

                if (enumbannerpatterntype.e()) {
                    boolean flag1 = false;
                    boolean flag2 = false;

                    for (k = 0; k < iinventory.getSize() && flag; ++k) {
                        ItemStack itemstack = iinventory.getItem(k);

                        if (!itemstack.isEmpty() && !(itemstack.getItem() instanceof ItemBanner)) {
                            if (itemstack.getItem() instanceof ItemDye) {
                                if (flag2) {
                                    flag = false;
                                    break;
                                }

                                flag2 = true;
                            } else {
                                if (flag1 || !itemstack.doMaterialsMatch(enumbannerpatterntype.f())) {
                                    flag = false;
                                    break;
                                }

                                flag1 = true;
                            }
                        }
                    }

                    if (!flag1 || !flag2) {
                        flag = false;
                    }
                } else if (iinventory.getSize() != enumbannerpatterntype.c().length * enumbannerpatterntype.c()[0].length()) {
                    flag = false;
                } else {
                    EnumColor enumcolor = null;

                    for (int l = 0; l < iinventory.getSize() && flag; ++l) {
                        k = l / 3;
                        int i1 = l % 3;
                        ItemStack itemstack1 = iinventory.getItem(l);
                        Item item = itemstack1.getItem();

                        if (!itemstack1.isEmpty() && !(item instanceof ItemBanner)) {
                            if (!(item instanceof ItemDye)) {
                                flag = false;
                                break;
                            }

                            EnumColor enumcolor1 = ((ItemDye) item).d();

                            if (enumcolor != null && enumcolor != enumcolor1) {
                                flag = false;
                                break;
                            }

                            if (enumbannerpatterntype.c()[k].charAt(i1) == ' ') {
                                flag = false;
                                break;
                            }

                            enumcolor = enumcolor1;
                        } else if (enumbannerpatterntype.c()[k].charAt(i1) != ' ') {
                            flag = false;
                            break;
                        }
                    }
                }

                if (flag) {
                    return enumbannerpatterntype;
                }
            }
        }

        return null;
    }

    public RecipeSerializer<?> a() {
        return RecipeSerializers.m;
    }
}
