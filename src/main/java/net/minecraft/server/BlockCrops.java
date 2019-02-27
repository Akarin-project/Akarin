package net.minecraft.server;

import java.util.Random;

public class BlockCrops extends BlockPlant implements IBlockFragilePlantElement {

    public static final BlockStateInteger AGE = BlockProperties.W;
    private static final VoxelShape[] a = new VoxelShape[] { Block.a(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

    protected BlockCrops(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(this.d(), 0));
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockCrops.a[(Integer) iblockdata.get(this.d())];
    }

    protected boolean b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.getBlock() == Blocks.FARMLAND;
    }

    public BlockStateInteger d() {
        return BlockCrops.AGE;
    }

    public int e() {
        return 7;
    }

    protected int k(IBlockData iblockdata) {
        return (Integer) iblockdata.get(this.d());
    }

    public IBlockData setAge(int i) {
        return (IBlockData) this.getBlockData().set(this.d(), i);
    }

    public boolean w(IBlockData iblockdata) {
        return (Integer) iblockdata.get(this.d()) >= this.e();
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        super.a(iblockdata, world, blockposition, random);
        if (world.getLightLevel(blockposition.up(), 0) >= 9) {
            int i = this.k(iblockdata);

            if (i < this.e()) {
                float f = a((Block) this, (IBlockAccess) world, blockposition);

                if (random.nextInt((int) (25.0F / f) + 1) == 0) {
                    world.setTypeAndData(blockposition, this.setAge(i + 1), 2);
                }
            }
        }

    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.k(iblockdata) + this.a(world);
        int j = this.e();

        if (i > j) {
            i = j;
        }

        world.setTypeAndData(blockposition, this.setAge(i), 2);
    }

    protected int a(World world) {
        return MathHelper.nextInt(world.random, 2, 5);
    }

    protected static float a(Block block, IBlockAccess iblockaccess, BlockPosition blockposition) {
        float f = 1.0F;
        BlockPosition blockposition1 = blockposition.down();

        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                float f1 = 0.0F;
                IBlockData iblockdata = iblockaccess.getType(blockposition1.a(i, 0, j));

                if (iblockdata.getBlock() == Blocks.FARMLAND) {
                    f1 = 1.0F;
                    if ((Integer) iblockdata.get(BlockSoil.MOISTURE) > 0) {
                        f1 = 3.0F;
                    }
                }

                if (i != 0 || j != 0) {
                    f1 /= 4.0F;
                }

                f += f1;
            }
        }

        BlockPosition blockposition2 = blockposition.north();
        BlockPosition blockposition3 = blockposition.south();
        BlockPosition blockposition4 = blockposition.west();
        BlockPosition blockposition5 = blockposition.east();
        boolean flag = block == iblockaccess.getType(blockposition4).getBlock() || block == iblockaccess.getType(blockposition5).getBlock();
        boolean flag1 = block == iblockaccess.getType(blockposition2).getBlock() || block == iblockaccess.getType(blockposition3).getBlock();

        if (flag && flag1) {
            f /= 2.0F;
        } else {
            boolean flag2 = block == iblockaccess.getType(blockposition4.north()).getBlock() || block == iblockaccess.getType(blockposition5.north()).getBlock() || block == iblockaccess.getType(blockposition5.south()).getBlock() || block == iblockaccess.getType(blockposition4.south()).getBlock();

            if (flag2) {
                f /= 2.0F;
            }
        }

        return f;
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return (iworldreader.getLightLevel(blockposition, 0) >= 8 || iworldreader.e(blockposition)) && super.canPlace(iblockdata, iworldreader, blockposition);
    }

    protected IMaterial f() {
        return Items.WHEAT_SEEDS;
    }

    protected IMaterial g() {
        return Items.WHEAT;
    }

    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, float f, int i) {
        super.dropNaturally(iblockdata, world, blockposition, f, 0);
        if (!world.isClientSide) {
            int j = this.k(iblockdata);

            if (j >= this.e()) {
                int k = 3 + i;

                for (int l = 0; l < k; ++l) {
                    if (world.random.nextInt(2 * this.e()) <= j) {
                        a(world, blockposition, new ItemStack(this.f()));
                    }
                }
            }

        }
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return this.w(iblockdata) ? this.g() : this.f();
    }

    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(this.f());
    }

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return !this.w(iblockdata);
    }

    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        this.a(world, blockposition, iblockdata);
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockCrops.AGE);
    }
}
