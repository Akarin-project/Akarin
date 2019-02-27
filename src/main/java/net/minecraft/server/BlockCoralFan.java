package net.minecraft.server;

import java.util.Random;

public class BlockCoralFan extends BlockCoralFanAbstract {

    private final Block a;

    protected BlockCoralFan(Block block, Block.Info block_info) {
        super(block_info);
        this.a = block;
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        this.a(iblockdata, (GeneratorAccess) world, blockposition);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (!b_(iblockdata, world, blockposition)) {
            world.setTypeAndData(blockposition, (IBlockData) this.a.getBlockData().set(BlockCoralFan.b, false), 2);
        }

    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (enumdirection == EnumDirection.DOWN && !iblockdata.canPlace(generatoraccess, blockposition)) {
            return Blocks.AIR.getBlockData();
        } else {
            this.a(iblockdata, generatoraccess, blockposition);
            if ((Boolean) iblockdata.get(BlockCoralFan.b)) {
                generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
            }

            return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        }
    }
}
