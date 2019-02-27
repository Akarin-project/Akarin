package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockStem extends BlockPlant implements IBlockFragilePlantElement {

    public static final BlockStateInteger AGE = BlockProperties.W;
    protected static final VoxelShape[] b = new VoxelShape[] { Block.a(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 4.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 6.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 8.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 12.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D)};
    private final BlockStemmed blockFruit;

    protected BlockStem(BlockStemmed blockstemmed, Block.Info block_info) {
        super(block_info);
        this.blockFruit = blockstemmed;
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockStem.AGE, 0));
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockStem.b[(Integer) iblockdata.get(BlockStem.AGE)];
    }

    protected boolean b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.getBlock() == Blocks.FARMLAND;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        super.a(iblockdata, world, blockposition, random);
        if (world.getLightLevel(blockposition.up(), 0) >= 9) {
            float f = BlockCrops.a((Block) this, (IBlockAccess) world, blockposition);

            if (random.nextInt((int) (25.0F / f) + 1) == 0) {
                int i = (Integer) iblockdata.get(BlockStem.AGE);

                if (i < 7) {
                    iblockdata = (IBlockData) iblockdata.set(BlockStem.AGE, i + 1);
                    world.setTypeAndData(blockposition, iblockdata, 2);
                } else {
                    EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);
                    BlockPosition blockposition1 = blockposition.shift(enumdirection);
                    Block block = world.getType(blockposition1.down()).getBlock();

                    if (world.getType(blockposition1).isAir() && (block == Blocks.FARMLAND || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.GRASS_BLOCK)) {
                        world.setTypeUpdate(blockposition1, this.blockFruit.getBlockData());
                        world.setTypeUpdate(blockposition, (IBlockData) this.blockFruit.e().getBlockData().set(BlockFacingHorizontal.FACING, enumdirection));
                    }
                }
            }

        }
    }

    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, float f, int i) {
        super.dropNaturally(iblockdata, world, blockposition, f, i);
        if (!world.isClientSide) {
            Item item = this.d();

            if (item != null) {
                int j = (Integer) iblockdata.get(BlockStem.AGE);

                for (int k = 0; k < 3; ++k) {
                    if (world.random.nextInt(15) <= j) {
                        a(world, blockposition, new ItemStack(item));
                    }
                }

            }
        }
    }

    @Nullable
    protected Item d() {
        return this.blockFruit == Blocks.PUMPKIN ? Items.PUMPKIN_SEEDS : (this.blockFruit == Blocks.MELON ? Items.MELON_SEEDS : null);
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.AIR;
    }

    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        Item item = this.d();

        return item == null ? ItemStack.a : new ItemStack(item);
    }

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return (Integer) iblockdata.get(BlockStem.AGE) != 7;
    }

    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        int i = Math.min(7, (Integer) iblockdata.get(BlockStem.AGE) + MathHelper.nextInt(world.random, 2, 5));
        IBlockData iblockdata1 = (IBlockData) iblockdata.set(BlockStem.AGE, i);

        world.setTypeAndData(blockposition, iblockdata1, 2);
        if (i == 7) {
            iblockdata1.a(world, blockposition, world.random);
        }

    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockStem.AGE);
    }

    public BlockStemmed e() {
        return this.blockFruit;
    }
}
