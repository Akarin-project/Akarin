/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.block.impl;

public final class CraftPoweredRail extends org.bukkit.craftbukkit.block.data.CraftBlockData implements org.bukkit.block.data.type.RedstoneRail, org.bukkit.block.data.Powerable, org.bukkit.block.data.Rail {

    public CraftPoweredRail() {
        super();
    }

    public CraftPoweredRail(net.minecraft.server.IBlockData state) {
        super(state);
    }

    // org.bukkit.craftbukkit.block.data.CraftPowerable

    private static final net.minecraft.server.BlockStateBoolean POWERED = getBoolean(net.minecraft.server.BlockPoweredRail.class, "powered");

    @Override
    public boolean isPowered() {
        return get(POWERED);
    }

    @Override
    public void setPowered(boolean powered) {
        set(POWERED, powered);
    }

    // org.bukkit.craftbukkit.block.data.CraftRail

    private static final net.minecraft.server.BlockStateEnum<?> SHAPE = getEnum(net.minecraft.server.BlockPoweredRail.class, "shape");

    @Override
    public Shape getShape() {
        return get(SHAPE, Shape.class);
    }

    @Override
    public void setShape(Shape shape) {
        set(SHAPE, shape);
    }

    @Override
    public java.util.Set<Shape> getShapes() {
        return getValues(SHAPE, Shape.class);
    }
}
