package net.minecraft.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
// CraftBukkit start
import java.util.ArrayList;
import java.util.List;
import org.bukkit.craftbukkit.inventory.CraftFurnaceRecipe;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.Recipe;
// CraftBukkit end

public class FurnaceRecipe implements IRecipe {

    private final MinecraftKey key;
    private final String group;
    private final RecipeItemStack ingredient;
    private final ItemStack result;
    private final float experience;
    private final int cookingTime;

    public FurnaceRecipe(MinecraftKey minecraftkey, String s, RecipeItemStack recipeitemstack, ItemStack itemstack, float f, int i) {
        this.key = minecraftkey;
        this.group = s;
        this.ingredient = recipeitemstack;
        this.result = itemstack;
        this.experience = f;
        this.cookingTime = i;
    }

    public boolean a(IInventory iinventory, World world) {
        return iinventory instanceof TileEntityFurnace && this.ingredient.test(iinventory.getItem(0));
    }

    public ItemStack craftItem(IInventory iinventory) {
        return this.result.cloneItemStack();
    }

    public RecipeSerializer<?> a() {
        return RecipeSerializers.p;
    }

    public NonNullList<RecipeItemStack> e() {
        NonNullList<RecipeItemStack> nonnulllist = NonNullList.a();

        nonnulllist.add(this.ingredient);
        return nonnulllist;
    }

    public float g() {
        return this.experience;
    }

    public ItemStack d() {
        return this.result;
    }

    public int h() {
        return this.cookingTime;
    }

    public MinecraftKey getKey() {
        return this.key;
    }

    @Override
    public Recipe toBukkitRecipe() {
        CraftItemStack result = CraftItemStack.asCraftMirror(this.result);

        CraftFurnaceRecipe recipe = new CraftFurnaceRecipe(CraftNamespacedKey.fromMinecraft(this.key), result, CraftRecipe.toBukkit(this.ingredient), this.experience, this.cookingTime);
        recipe.setGroup(this.group);

        return recipe;
    }

    public static class a implements RecipeSerializer<FurnaceRecipe> {

        public a() {}

        public FurnaceRecipe a(MinecraftKey minecraftkey, JsonObject jsonobject) {
            String s = ChatDeserializer.a(jsonobject, "group", "");
            RecipeItemStack recipeitemstack;

            if (ChatDeserializer.d(jsonobject, "ingredient")) {
                recipeitemstack = RecipeItemStack.a((JsonElement) ChatDeserializer.u(jsonobject, "ingredient"));
            } else {
                recipeitemstack = RecipeItemStack.a((JsonElement) ChatDeserializer.t(jsonobject, "ingredient"));
            }

            String s1 = ChatDeserializer.h(jsonobject, "result");
            Item item = (Item) IRegistry.ITEM.get(new MinecraftKey(s1));

            if (item != null) {
                ItemStack itemstack = new ItemStack(item);
                float f = ChatDeserializer.a(jsonobject, "experience", 0.0F);
                int i = ChatDeserializer.a(jsonobject, "cookingtime", 200);

                return new FurnaceRecipe(minecraftkey, s, recipeitemstack, itemstack, f, i);
            } else {
                throw new IllegalStateException(s1 + " did not exist");
            }
        }

        public FurnaceRecipe a(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
            String s = packetdataserializer.e(32767);
            RecipeItemStack recipeitemstack = RecipeItemStack.b(packetdataserializer);
            ItemStack itemstack = packetdataserializer.k();
            float f = packetdataserializer.readFloat();
            int i = packetdataserializer.g();

            return new FurnaceRecipe(minecraftkey, s, recipeitemstack, itemstack, f, i);
        }

        public void a(PacketDataSerializer packetdataserializer, FurnaceRecipe furnacerecipe) {
            packetdataserializer.a(furnacerecipe.group);
            furnacerecipe.ingredient.a(packetdataserializer);
            packetdataserializer.a(furnacerecipe.result);
            packetdataserializer.writeFloat(furnacerecipe.experience);
            packetdataserializer.d(furnacerecipe.cookingTime);
        }

        public String a() {
            return "smelting";
        }
    }
}
