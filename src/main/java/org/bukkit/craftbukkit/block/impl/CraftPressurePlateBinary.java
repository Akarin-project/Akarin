/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.block.impl;

public final class CraftPressurePlateBinary extends org.bukkit.craftbukkit.block.data.CraftBlockData implements org.bukkit.block.data.Powerable {

    public CraftPressurePlateBinary() {
        super();
    }

    public CraftPressurePlateBinary(net.minecraft.server.IBlockData state) {
        super(state);
    }

    // org.bukkit.craftbukkit.block.data.CraftPowerable

    private static final net.minecraft.server.BlockStateBoolean POWERED = getBoolean(net.minecraft.server.BlockPressurePlateBinary.class, "powered");

    @Override
    public boolean isPowered() {
        return get(POWERED);
    }

    @Override
    public void setPowered(boolean powered) {
        set(POWERED, powered);
    }
}
