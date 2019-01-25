package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.RecipeItemStack;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;

public interface CraftRecipe extends Recipe {

    void addToCraftingManager();

    default RecipeItemStack toNMS(RecipeChoice bukkit, boolean requireNotEmpty) {
        RecipeItemStack stack;

        if (bukkit == null) {
            stack = RecipeItemStack.a;
        } else if (bukkit instanceof RecipeChoice.MaterialChoice) {
            stack = new RecipeItemStack(((RecipeChoice.MaterialChoice) bukkit).getChoices().stream().map((mat) -> new net.minecraft.server.RecipeItemStack.StackProvider(CraftItemStack.asNMSCopy(new ItemStack(mat)))));
        } else if (bukkit instanceof RecipeChoice.ExactChoice) {
            stack = new RecipeItemStack(((RecipeChoice.ExactChoice) bukkit).getChoices().stream().map((mat) -> new net.minecraft.server.RecipeItemStack.StackProvider(CraftItemStack.asNMSCopy(mat))));
            stack.exact = true;
        } else {
            throw new IllegalArgumentException("Unknown recipe stack instance " + bukkit);
        }

        stack.buildChoices();
        if (requireNotEmpty && stack.choices.length == 0) {
            throw new IllegalArgumentException("Recipe requires at least one non-air choice!");
        }

        return stack;
    }

    public static RecipeChoice toBukkit(RecipeItemStack list) {
        list.buildChoices();

        if (list.choices.length == 0) {
            return null;
        }

        if (list.exact) {
            List<org.bukkit.inventory.ItemStack> choices = new ArrayList<>(list.choices.length);
            for (net.minecraft.server.ItemStack i : list.choices) {
                choices.add(CraftItemStack.asBukkitCopy(i));
            }

            return new RecipeChoice.ExactChoice(choices);
        } else {

            List<org.bukkit.Material> choices = new ArrayList<>(list.choices.length);
            for (net.minecraft.server.ItemStack i : list.choices) {
                choices.add(CraftMagicNumbers.getMaterial(i.getItem()));
            }

            return new RecipeChoice.MaterialChoice(choices);
        }
    }
}
