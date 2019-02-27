package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockRedstoneLamp extends Block {

    public static final BlockStateBoolean a = BlockRedstoneTorch.LIT;

    public BlockRedstoneLamp(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) this.getBlockData().set(BlockRedstoneLamp.a, false));
    }

    public int m(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockRedstoneLamp.a) ? super.m(iblockdata) : 0;
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        super.onPlace(iblockdata, world, blockposition, iblockdata1);
    }

    @Nullable
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockRedstoneLamp.a, blockactioncontext.getWorld().isBlockIndirectlyPowered(blockactioncontext.getClickPosition()));
    }

    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.isClientSide) {
            boolean flag = (Boolean) iblockdata.get(BlockRedstoneLamp.a);

            if (flag != world.isBlockIndirectlyPowered(blockposition)) {
                if (flag) {
                    world.getBlockTickList().a(blockposition, this, 4);
                } else {
                    world.setTypeAndData(blockposition, (IBlockData) iblockdata.a((IBlockState) BlockRedstoneLamp.a), 2);
                }
            }

        }
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (!world.isClientSide) {
            if ((Boolean) iblockdata.get(BlockRedstoneLamp.a) && !world.isBlockIndirectlyPowered(blockposition)) {
                world.setTypeAndData(blockposition, (IBlockData) iblockdata.a((IBlockState) BlockRedstoneLamp.a), 2);
            }

        }
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockRedstoneLamp.a);
    }
}
