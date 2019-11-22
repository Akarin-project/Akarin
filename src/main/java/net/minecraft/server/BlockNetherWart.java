package net.minecraft.server;

import java.util.Random;

public class BlockNetherWart extends BlockPlant {

    public static final BlockStateInteger AGE = BlockProperties.aa;
    private static final VoxelShape[] b = new VoxelShape[]{Block.a(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 11.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D)};

    protected BlockNetherWart(Block.Info block_info) {
        super(block_info);
        this.o((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockNetherWart.AGE, 0));
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockNetherWart.b[(Integer) iblockdata.get(BlockNetherWart.AGE)];
    }

    @Override
    protected boolean a_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.getBlock() == Blocks.SOUL_SAND;
    }

    @Override
    public void tick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        int i = (Integer) iblockdata.get(BlockNetherWart.AGE);

        if (i < 3 && random.nextInt(Math.max(1, (int) (100.0F / world.spigotConfig.wartModifier) * 10)) == 0) { // Spigot
            iblockdata = (IBlockData) iblockdata.set(BlockNetherWart.AGE, i + 1);
            org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockGrowEvent(world, blockposition, iblockdata, 2); // CraftBukkit
        }

        super.tick(iblockdata, world, blockposition, random);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockNetherWart.AGE);
    }
}
