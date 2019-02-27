package net.minecraft.server;

import java.util.Random;

public class BlockSnowBlock extends Block {

    protected BlockSnowBlock(Block.Info block_info) {
        super(block_info);
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.SNOWBALL;
    }

    public int a(IBlockData iblockdata, Random random) {
        return 4;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (world.getBrightness(EnumSkyBlock.BLOCK, blockposition) > 11) {
            iblockdata.a(world, blockposition, 0);
            world.setAir(blockposition);
        }

    }
}
