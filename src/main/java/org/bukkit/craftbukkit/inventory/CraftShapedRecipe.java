package org.bukkit.craftbukkit.inventory;

import java.util.Map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NonNullList;
import net.minecraft.server.RecipeItemStack;
import net.minecraft.server.ShapedRecipes;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

public class CraftShapedRecipe extends ShapedRecipe implements CraftRecipe {
    // TODO: Could eventually use this to add a matches() method or some such
    private ShapedRecipes recipe;

    public CraftShapedRecipe(NamespacedKey key, ItemStack result) {
        super(key, result);
    }

    public CraftShapedRecipe(ItemStack result, ShapedRecipes recipe) {
        this(CraftNamespacedKey.fromMinecraft(recipe.getKey()), result);
        this.recipe = recipe;
    }

    public static CraftShapedRecipe fromBukkitRecipe(ShapedRecipe recipe) {
        if (recipe instanceof CraftShapedRecipe) {
            return (CraftShapedRecipe) recipe;
        }
        CraftShapedRecipe ret = new CraftShapedRecipe(recipe.getKey(), recipe.getResult());
        ret.setGroup(recipe.getGroup());
        String[] shape = recipe.getShape();
        ret.shape(shape);
        Map<Character, RecipeChoice> ingredientMap = recipe.getChoiceMap();
        for (char c : ingredientMap.keySet()) {
            RecipeChoice stack = ingredientMap.get(c);
            if (stack != null) {
                ret.setIngredient(c, stack);
            }
        }
        return ret;
    }

    public void addToCraftingManager() {
        String[] shape = this.getShape();
        Map<Character, org.bukkit.inventory.RecipeChoice> ingred = this.getChoiceMap();
        int width = shape[0].length();
        NonNullList<RecipeItemStack> data = NonNullList.a(shape.length * width, RecipeItemStack.a);

        for (int i = 0; i < shape.length; i++) {
            String row = shape[i];
            for (int j = 0; j < row.length(); j++) {
                data.set(i * width + j, toNMS(ingred.get(row.charAt(j))));
            }
        }

        MinecraftServer.getServer().getCraftingManager().a(new ShapedRecipes(CraftNamespacedKey.toMinecraft(this.getKey()), this.getGroup(), width, shape.length, data, CraftItemStack.asNMSCopy(this.getResult())));
    }
}
