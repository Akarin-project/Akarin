package org.bukkit.craftbukkit.inventory;

import java.util.Arrays;
import java.util.List;

import net.minecraft.server.IRecipe;
import net.minecraft.server.IInventory;
import net.minecraft.server.InventoryCrafting;

import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class CraftInventoryCrafting extends CraftInventory implements CraftingInventory {
    private final IInventory resultInventory;

    public CraftInventoryCrafting(InventoryCrafting inventory, IInventory resultInventory) {
        super(inventory);
        this.resultInventory = resultInventory;
    }

    public IInventory getResultInventory() {
        return resultInventory;
    }

    public IInventory getMatrixInventory() {
        return inventory;
    }

    @Override
    public int getSize() {
        return getResultInventory().getSize() + getMatrixInventory().getSize();
    }

    @Override
    public void setContents(ItemStack[] items) {
        if (getSize() > items.length) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + getSize() + " or less");
        }
        setContents(items[0], Arrays.copyOfRange(items, 1, items.length));
    }

    @Override
    public ItemStack[] getContents() {
        ItemStack[] items = new ItemStack[getSize()];
        List<net.minecraft.server.ItemStack> mcResultItems = getResultInventory().getContents();

        int i = 0;
        for (i = 0; i < mcResultItems.size(); i++ ) {
            items[i] = CraftItemStack.asCraftMirror(mcResultItems.get(i));
        }

        List<net.minecraft.server.ItemStack> mcItems = getMatrixInventory().getContents();

        for (int j = 0; j < mcItems.size(); j++) {
            items[i + j] = CraftItemStack.asCraftMirror(mcItems.get(j));
        }

        return items;
    }

    public void setContents(ItemStack result, ItemStack[] contents) {
        setResult(result);
        setMatrix(contents);
    }

    @Override
    public CraftItemStack getItem(int index) {
        if (index < getResultInventory().getSize()) {
            net.minecraft.server.ItemStack item = getResultInventory().getItem(index);
            return item.isEmpty() ? null : CraftItemStack.asCraftMirror(item);
        } else {
            net.minecraft.server.ItemStack item = getMatrixInventory().getItem(index - getResultInventory().getSize());
            return item.isEmpty() ? null : CraftItemStack.asCraftMirror(item);
        }
    }

    @Override
    public void setItem(int index, ItemStack item) {
        if (index < getResultInventory().getSize()) {
            getResultInventory().setItem(index, CraftItemStack.asNMSCopy(item));
        } else {
            getMatrixInventory().setItem((index - getResultInventory().getSize()), CraftItemStack.asNMSCopy(item));
        }
    }

    public ItemStack[] getMatrix() {
        List<net.minecraft.server.ItemStack> matrix = getMatrixInventory().getContents();

        return asCraftMirror(matrix);
    }

    public ItemStack getResult() {
        net.minecraft.server.ItemStack item = getResultInventory().getItem(0);
        if (!item.isEmpty()) return CraftItemStack.asCraftMirror(item);
        return null;
    }

    public void setMatrix(ItemStack[] contents) {
        if (getMatrixInventory().getSize() > contents.length) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + getMatrixInventory().getSize() + " or less");
        }

        for (int i = 0; i < getMatrixInventory().getSize(); i++) {
            if (i < contents.length) {
                getMatrixInventory().setItem(i, CraftItemStack.asNMSCopy(contents[i]));
            } else {
                getMatrixInventory().setItem(i, net.minecraft.server.ItemStack.a);
            }
        }
    }

    public void setResult(ItemStack item) {
        List<net.minecraft.server.ItemStack> contents = getResultInventory().getContents();
        contents.set(0, CraftItemStack.asNMSCopy(item));
    }

    public Recipe getRecipe() {
        IRecipe recipe = ((InventoryCrafting)getInventory()).currentRecipe;
        return recipe == null ? null : recipe.toBukkitRecipe();
    }
}
