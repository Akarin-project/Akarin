/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.block.impl;

public final class CraftTurtleEgg extends org.bukkit.craftbukkit.block.data.CraftBlockData implements org.bukkit.block.data.type.TurtleEgg {

    public CraftTurtleEgg() {
        super();
    }

    public CraftTurtleEgg(net.minecraft.server.IBlockData state) {
        super(state);
    }

    // org.bukkit.craftbukkit.block.data.type.CraftTurtleEgg

    private static final net.minecraft.server.BlockStateInteger EGGS = getInteger(net.minecraft.server.BlockTurtleEgg.class, "eggs");
    private static final net.minecraft.server.BlockStateInteger HATCH = getInteger(net.minecraft.server.BlockTurtleEgg.class, "hatch");

    @Override
    public int getEggs() {
        return get(EGGS);
    }

    @Override
    public void setEggs(int eggs) {
        set(EGGS, eggs);
    }

    @Override
    public int getMinimumEggs() {
        return getMin(EGGS);
    }

    @Override
    public int getMaximumEggs() {
        return getMax(EGGS);
    }

    @Override
    public int getHatch() {
        return get(HATCH);
    }

    @Override
    public void setHatch(int hatch) {
        set(HATCH, hatch);
    }

    @Override
    public int getMaximumHatch() {
        return getMax(HATCH);
    }
}
