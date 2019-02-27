package net.minecraft.server;

import java.util.Random;

public class BlockCoralFanWall extends BlockCoralFanWallAbstract {

    private final Block c;

    protected BlockCoralFanWall(Block block, Block.Info block_info) {
        super(block_info);
        this.c = block;
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        this.a(iblockdata, (GeneratorAccess) world, blockposition);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (!b_(iblockdata, world, blockposition)) {
            world.setTypeAndData(blockposition, (IBlockData) ((IBlockData) this.c.getBlockData().set(BlockCoralFanWall.b, false)).set(BlockCoralFanWall.a, iblockdata.get(BlockCoralFanWall.a)), 2);
        }

    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (enumdirection.opposite() == iblockdata.get(BlockCoralFanWall.a) && !iblockdata.canPlace(generatoraccess, blockposition)) {
            return Blocks.AIR.getBlockData();
        } else {
            if ((Boolean) iblockdata.get(BlockCoralFanWall.b)) {
                generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
            }

            this.a(iblockdata, generatoraccess, blockposition);
            return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        }
    }
}
