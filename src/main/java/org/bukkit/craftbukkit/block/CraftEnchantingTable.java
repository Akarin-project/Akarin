package org.bukkit.craftbukkit.block;

import net.minecraft.server.TileEntityEnchantTable;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.EnchantingTable;

public class CraftEnchantingTable extends CraftBlockEntityState<TileEntityEnchantTable> implements EnchantingTable {

    public CraftEnchantingTable(final Block block) {
        super(block, TileEntityEnchantTable.class);
    }

    public CraftEnchantingTable(final Material material, final TileEntityEnchantTable te) {
        super(material, te);
    }

    @Override
    public String getCustomName() {
        TileEntityEnchantTable enchant = this.getSnapshot();
        return enchant.hasCustomName() ? enchant.getName() : null;
    }

    @Override
    public void setCustomName(String name) {
        this.getSnapshot().setCustomName(name);
    }
}
