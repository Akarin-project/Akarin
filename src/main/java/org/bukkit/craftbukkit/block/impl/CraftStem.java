/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.block.impl;

public final class CraftStem extends org.bukkit.craftbukkit.block.data.CraftBlockData implements org.bukkit.block.data.Ageable {

    public CraftStem() {
        super();
    }

    public CraftStem(net.minecraft.server.IBlockData state) {
        super(state);
    }

    // org.bukkit.craftbukkit.block.data.CraftAgeable

    private static final net.minecraft.server.BlockStateInteger AGE = getInteger(net.minecraft.server.BlockStem.class, "age");

    @Override
    public int getAge() {
        return get(AGE);
    }

    @Override
    public void setAge(int age) {
        set(AGE, age);
    }

    @Override
    public int getMaximumAge() {
        return getMax(AGE);
    }
}
