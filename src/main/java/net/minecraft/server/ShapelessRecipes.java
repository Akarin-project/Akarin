package net.minecraft.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;

public class ShapelessRecipes implements IRecipe {

    private final MinecraftKey key;
    private final String group;
    private final ItemStack result;
    private final NonNullList<RecipeItemStack> ingredients;

    public ShapelessRecipes(MinecraftKey minecraftkey, String s, ItemStack itemstack, NonNullList<RecipeItemStack> nonnulllist) {
        this.key = minecraftkey;
        this.group = s;
        this.result = itemstack;
        this.ingredients = nonnulllist;
    }

    public MinecraftKey getKey() {
        return this.key;
    }

    public RecipeSerializer<?> a() {
        return RecipeSerializers.b;
    }

    public ItemStack d() {
        return this.result;
    }

    public NonNullList<RecipeItemStack> e() {
        return this.ingredients;
    }

    public boolean a(IInventory iinventory, World world) {
        if (!(iinventory instanceof InventoryCrafting)) {
            return false;
        } else {
            AutoRecipeStackManager autorecipestackmanager = new AutoRecipeStackManager();
            int i = 0;

            for (int j = 0; j < iinventory.n(); ++j) {
                for (int k = 0; k < iinventory.U_(); ++k) {
                    ItemStack itemstack = iinventory.getItem(k + j * iinventory.U_());

                    if (!itemstack.isEmpty()) {
                        ++i;
                        autorecipestackmanager.b(new ItemStack(itemstack.getItem()));
                    }
                }
            }

            return i == this.ingredients.size() && autorecipestackmanager.a(this, (IntList) null);
        }
    }

    public ItemStack craftItem(IInventory iinventory) {
        return this.result.cloneItemStack();
    }

    public static class a implements RecipeSerializer<ShapelessRecipes> {

        public a() {}

        public ShapelessRecipes a(MinecraftKey minecraftkey, JsonObject jsonobject) {
            String s = ChatDeserializer.a(jsonobject, "group", "");
            NonNullList<RecipeItemStack> nonnulllist = a(ChatDeserializer.u(jsonobject, "ingredients"));

            if (nonnulllist.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (nonnulllist.size() > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe");
            } else {
                ItemStack itemstack = ShapedRecipes.a(ChatDeserializer.t(jsonobject, "result"));

                return new ShapelessRecipes(minecraftkey, s, itemstack, nonnulllist);
            }
        }

        private static NonNullList<RecipeItemStack> a(JsonArray jsonarray) {
            NonNullList<RecipeItemStack> nonnulllist = NonNullList.a();

            for (int i = 0; i < jsonarray.size(); ++i) {
                RecipeItemStack recipeitemstack = RecipeItemStack.a(jsonarray.get(i));

                if (!recipeitemstack.d()) {
                    nonnulllist.add(recipeitemstack);
                }
            }

            return nonnulllist;
        }

        public String a() {
            return "crafting_shapeless";
        }

        public ShapelessRecipes a(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
            String s = packetdataserializer.e(32767);
            int i = packetdataserializer.g();
            NonNullList<RecipeItemStack> nonnulllist = NonNullList.a(i, RecipeItemStack.a);

            for (int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, RecipeItemStack.b(packetdataserializer));
            }

            ItemStack itemstack = packetdataserializer.k();

            return new ShapelessRecipes(minecraftkey, s, itemstack, nonnulllist);
        }

        public void a(PacketDataSerializer packetdataserializer, ShapelessRecipes shapelessrecipes) {
            packetdataserializer.a(shapelessrecipes.group);
            packetdataserializer.d(shapelessrecipes.ingredients.size());
            Iterator iterator = shapelessrecipes.ingredients.iterator();

            while (iterator.hasNext()) {
                RecipeItemStack recipeitemstack = (RecipeItemStack) iterator.next();

                recipeitemstack.a(packetdataserializer);
            }

            packetdataserializer.a(shapelessrecipes.result);
        }
    }
}
