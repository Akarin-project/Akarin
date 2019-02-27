package net.minecraft.server;

import java.util.Random;

public class BlockCoralPlant extends BlockCoralBase {

    private final Block c;
    protected static final VoxelShape a = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

    protected BlockCoralPlant(Block block, Block.Info block_info) {
        super(block_info);
        this.c = block;
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        this.a(iblockdata, (GeneratorAccess) world, blockposition);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (!b_(iblockdata, world, blockposition)) {
            // CraftBukkit start
            if (org.bukkit.craftbukkit.event.CraftEventFactory.callBlockFadeEvent(world, blockposition, this.c.getBlockData().set(BlockCoralPlant.b, false)).isCancelled()) {
                return;
            }
            // CraftBukkit end
            world.setTypeAndData(blockposition, (IBlockData) this.c.getBlockData().set(BlockCoralPlant.b, false), 2);
        }

    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (enumdirection == EnumDirection.DOWN && !iblockdata.canPlace(generatoraccess, blockposition)) {
            return Blocks.AIR.getBlockData();
        } else {
            this.a(iblockdata, generatoraccess, blockposition);
            if ((Boolean) iblockdata.get(BlockCoralPlant.b)) {
                generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
            }

            return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        }
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockCoralPlant.a;
    }
}
