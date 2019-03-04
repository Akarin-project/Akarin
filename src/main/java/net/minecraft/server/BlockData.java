package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

public class BlockData extends BlockDataAbstract<Block, IBlockData> implements IBlockData {

    public BlockData(Block block, ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap) {
        super(block, immutablemap);
    }

    public Block getBlock() {
        return (Block) this.e_;
    }

    // Paper start - impl cached craft block data, lazy load to fix issue with loading at the wrong time
    private CraftBlockData cachedCraftBlockData;

    @Override
    public CraftBlockData createCraftBlockData() {
        if(cachedCraftBlockData == null) cachedCraftBlockData = CraftBlockData.createData(this);
        return (CraftBlockData) cachedCraftBlockData.clone();
    }
    // Paper end
}
