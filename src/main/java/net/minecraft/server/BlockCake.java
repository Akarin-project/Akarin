package net.minecraft.server;

public class BlockCake extends Block {

    public static final BlockStateInteger BITES = BlockProperties.Z;
    protected static final VoxelShape[] b = new VoxelShape[] { Block.a(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.a(3.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.a(5.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.a(7.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.a(9.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.a(11.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.a(13.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D)};

    protected BlockCake(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockCake.BITES, 0));
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockCake.b[(Integer) iblockdata.get(BlockCake.BITES)];
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (!world.isClientSide) {
            return this.a((GeneratorAccess) world, blockposition, iblockdata, entityhuman);
        } else {
            ItemStack itemstack = entityhuman.b(enumhand);

            return this.a((GeneratorAccess) world, blockposition, iblockdata, entityhuman) || itemstack.isEmpty();
        }
    }

    private boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!entityhuman.q(false)) {
            return false;
        } else {
            entityhuman.a(StatisticList.EAT_CAKE_SLICE);
            entityhuman.getFoodData().eat(2, 0.1F);
            int i = (Integer) iblockdata.get(BlockCake.BITES);

            if (i < 6) {
                generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockCake.BITES, i + 1), 3);
            } else {
                generatoraccess.setAir(blockposition);
            }

            return true;
        }
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == EnumDirection.DOWN && !iblockdata.canPlace(generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return iworldreader.getType(blockposition.down()).getMaterial().isBuildable();
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.AIR;
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockCake.BITES);
    }

    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return (7 - (Integer) iblockdata.get(BlockCake.BITES)) * 2;
    }

    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
