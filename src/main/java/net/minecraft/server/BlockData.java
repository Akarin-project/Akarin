package net.minecraft.server;

import com.google.common.collect.ImmutableMap;

public class BlockData extends BlockDataAbstract<Block, IBlockData> implements IBlockData {

    public BlockData(Block block, ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap) {
        super(block, immutablemap);
    }

    public Block getBlock() {
        return (Block) this.e_;
    }
}
