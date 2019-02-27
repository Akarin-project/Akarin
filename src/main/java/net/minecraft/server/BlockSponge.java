package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Queue;

public class BlockSponge extends Block {

    protected BlockSponge(Block.Info block_info) {
        super(block_info);
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        if (iblockdata1.getBlock() != iblockdata.getBlock()) {
            this.a(world, blockposition);
        }
    }

    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        this.a(world, blockposition);
        super.doPhysics(iblockdata, world, blockposition, block, blockposition1);
    }

    protected void a(World world, BlockPosition blockposition) {
        if (this.b(world, blockposition)) {
            world.setTypeAndData(blockposition, Blocks.WET_SPONGE.getBlockData(), 2);
            world.triggerEffect(2001, blockposition, Block.getCombinedId(Blocks.WATER.getBlockData()));
        }

    }

    private boolean b(World world, BlockPosition blockposition) {
        Queue<Tuple<BlockPosition, Integer>> queue = Lists.newLinkedList();

        queue.add(new Tuple<>(blockposition, 0));
        int i = 0;

        while (!queue.isEmpty()) {
            Tuple<BlockPosition, Integer> tuple = (Tuple) queue.poll();
            BlockPosition blockposition1 = (BlockPosition) tuple.a();
            int j = (Integer) tuple.b();
            EnumDirection[] aenumdirection = EnumDirection.values();
            int k = aenumdirection.length;

            for (int l = 0; l < k; ++l) {
                EnumDirection enumdirection = aenumdirection[l];
                BlockPosition blockposition2 = blockposition1.shift(enumdirection);
                IBlockData iblockdata = world.getType(blockposition2);
                Fluid fluid = world.getFluid(blockposition2);
                Material material = iblockdata.getMaterial();

                if (fluid.a(TagsFluid.WATER)) {
                    if (iblockdata.getBlock() instanceof IFluidSource && ((IFluidSource) iblockdata.getBlock()).removeFluid(world, blockposition2, iblockdata) != FluidTypes.EMPTY) {
                        ++i;
                        if (j < 6) {
                            queue.add(new Tuple<>(blockposition2, j + 1));
                        }
                    } else if (iblockdata.getBlock() instanceof BlockFluids) {
                        world.setTypeAndData(blockposition2, Blocks.AIR.getBlockData(), 3);
                        ++i;
                        if (j < 6) {
                            queue.add(new Tuple<>(blockposition2, j + 1));
                        }
                    } else if (material == Material.WATER_PLANT || material == Material.REPLACEABLE_WATER_PLANT) {
                        iblockdata.a(world, blockposition2, 0);
                        world.setTypeAndData(blockposition2, Blocks.AIR.getBlockData(), 3);
                        ++i;
                        if (j < 6) {
                            queue.add(new Tuple<>(blockposition2, j + 1));
                        }
                    }
                }
            }

            if (i > 64) {
                break;
            }
        }

        return i > 0;
    }
}
