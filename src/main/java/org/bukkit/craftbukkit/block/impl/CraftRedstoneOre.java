/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.block.impl;

public final class CraftRedstoneOre extends org.bukkit.craftbukkit.block.data.CraftBlockData implements org.bukkit.block.data.Lightable {

    public CraftRedstoneOre() {
        super();
    }

    public CraftRedstoneOre(net.minecraft.server.IBlockData state) {
        super(state);
    }

    // org.bukkit.craftbukkit.block.data.CraftLightable

    private static final net.minecraft.server.BlockStateBoolean LIT = getBoolean(net.minecraft.server.BlockRedstoneOre.class, "lit");

    @Override
    public boolean isLit() {
        return get(LIT);
    }

    @Override
    public void setLit(boolean lit) {
        set(LIT, lit);
    }
}
