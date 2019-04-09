package com.destroystokyo.paper.inventory;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows crafting Items that require full matching itemstacks to complete the recipe for custom items
 * @deprecated Draft API
 */
@Deprecated
public class ItemStackRecipeChoice implements RecipeChoice {

    protected final List<ItemStack> choices = new ArrayList<>();

    public ItemStackRecipeChoice(ItemStack choices) {
        this.choices.add(choices);
    }

    public ItemStackRecipeChoice(List<ItemStack> choices) {
        this.choices.addAll(choices);
    }

    @Override
    public ItemStack getItemStack() {
        return choices.isEmpty() ? null : choices.get(0);
    }

    @Override
    public RecipeChoice clone() {
        try {
            ItemStackRecipeChoice clone = (ItemStackRecipeChoice) super.clone();
            clone.choices.addAll(this.choices);
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError(ex);
        }
    }

    @Override
    public boolean test(ItemStack itemStack) {
        for (ItemStack stack : choices) {
            if (stack.isSimilar(itemStack)) {
                return true;
            }
        }
        return false;
    }
}
