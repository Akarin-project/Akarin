package net.minecraft.server;

import java.util.Random;

public class BlockNetherWart extends BlockPlant {

    public static final BlockStateInteger AGE = BlockProperties.U;
    private static final VoxelShape[] b = new VoxelShape[] { Block.a(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 11.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D)};

    protected BlockNetherWart(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockNetherWart.AGE, 0));
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockNetherWart.b[(Integer) iblockdata.get(BlockNetherWart.AGE)];
    }

    protected boolean b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.getBlock() == Blocks.SOUL_SAND;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        int i = (Integer) iblockdata.get(BlockNetherWart.AGE);

        if (i < 3 && random.nextInt(10) == 0) {
            iblockdata = (IBlockData) iblockdata.set(BlockNetherWart.AGE, i + 1);
            world.setTypeAndData(blockposition, iblockdata, 2);
        }

        super.a(iblockdata, world, blockposition, random);
    }

    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, float f, int i) {
        if (!world.isClientSide) {
            int j = 1;

            if ((Integer) iblockdata.get(BlockNetherWart.AGE) >= 3) {
                j = 2 + world.random.nextInt(3);
                if (i > 0) {
                    j += world.random.nextInt(i + 1);
                }
            }

            for (int k = 0; k < j; ++k) {
                a(world, blockposition, new ItemStack(Items.NETHER_WART));
            }

        }
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.AIR;
    }

    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Items.NETHER_WART);
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockNetherWart.AGE);
    }
}
