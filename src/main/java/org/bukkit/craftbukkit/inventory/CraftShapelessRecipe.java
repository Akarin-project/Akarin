package org.bukkit.craftbukkit.inventory;

import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NonNullList;
import net.minecraft.server.RecipeItemStack;
import net.minecraft.server.ShapelessRecipes;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

public class CraftShapelessRecipe extends ShapelessRecipe implements CraftRecipe {
    // TODO: Could eventually use this to add a matches() method or some such
    private ShapelessRecipes recipe;

    public CraftShapelessRecipe(NamespacedKey key, ItemStack result) {
        super(key, result);
    }

    public CraftShapelessRecipe(ItemStack result, ShapelessRecipes recipe) {
        this(CraftNamespacedKey.fromMinecraft(recipe.getKey()), result);
        this.recipe = recipe;
    }

    public static CraftShapelessRecipe fromBukkitRecipe(ShapelessRecipe recipe) {
        if (recipe instanceof CraftShapelessRecipe) {
            return (CraftShapelessRecipe) recipe;
        }
        CraftShapelessRecipe ret = new CraftShapelessRecipe(recipe.getKey(), recipe.getResult());
        ret.setGroup(recipe.getGroup());
        for (RecipeChoice ingred : recipe.getChoiceList()) {
            ret.addIngredient(ingred);
        }
        return ret;
    }

    public void addToCraftingManager() {
        List<org.bukkit.inventory.RecipeChoice> ingred = this.getChoiceList();
        NonNullList<RecipeItemStack> data = NonNullList.a(ingred.size(), RecipeItemStack.a);
        for (int i = 0; i < ingred.size(); i++) {
            data.set(i, toNMS(ingred.get(i), true));
        }

        MinecraftServer.getServer().getCraftingManager().a(new ShapelessRecipes(CraftNamespacedKey.toMinecraft(this.getKey()), this.getGroup(), CraftItemStack.asNMSCopy(this.getResult()), data));
    }
}
