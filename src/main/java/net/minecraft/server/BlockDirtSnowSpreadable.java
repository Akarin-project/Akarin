package net.minecraft.server;

import java.util.Random;

public abstract class BlockDirtSnowSpreadable extends BlockDirtSnow {

    protected BlockDirtSnowSpreadable(Block.Info block_info) {
        super(block_info);
    }

    private static boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.up();

        return iworldreader.getLightLevel(blockposition1) >= 4 || iworldreader.getType(blockposition1).b(iworldreader, blockposition1) < iworldreader.K();
    }

    private static boolean b(IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.up();

        return iworldreader.getLightLevel(blockposition1) >= 4 && iworldreader.getType(blockposition1).b(iworldreader, blockposition1) < iworldreader.K() && !iworldreader.getFluid(blockposition1).a(TagsFluid.WATER);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (!world.isClientSide) {
            if (!a((IWorldReader) world, blockposition)) {
                world.setTypeUpdate(blockposition, Blocks.DIRT.getBlockData());
            } else {
                if (world.getLightLevel(blockposition.up()) >= 9) {
                    for (int i = 0; i < 4; ++i) {
                        BlockPosition blockposition1 = blockposition.a(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);

                        if (!world.p(blockposition1)) {
                            return;
                        }

                        if (world.getType(blockposition1).getBlock() == Blocks.DIRT && b(world, blockposition1)) {
                            world.setTypeUpdate(blockposition1, this.getBlockData());
                        }
                    }
                }

            }
        }
    }
}
