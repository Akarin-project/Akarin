/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.block.impl;

public final class CraftSapling extends org.bukkit.craftbukkit.block.data.CraftBlockData implements org.bukkit.block.data.type.Sapling {

    public CraftSapling() {
        super();
    }

    public CraftSapling(net.minecraft.server.IBlockData state) {
        super(state);
    }

    // org.bukkit.craftbukkit.block.data.type.CraftSapling

    private static final net.minecraft.server.BlockStateInteger STAGE = getInteger(net.minecraft.server.BlockSapling.class, "stage");

    @Override
    public int getStage() {
        return get(STAGE);
    }

    @Override
    public void setStage(int stage) {
        set(STAGE, stage);
    }

    @Override
    public int getMaximumStage() {
        return getMax(STAGE);
    }
}
