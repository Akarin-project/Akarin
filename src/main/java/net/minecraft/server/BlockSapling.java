package net.minecraft.server;

import java.util.Random;

public class BlockSapling extends BlockPlant implements IBlockFragilePlantElement {

    public static final BlockStateInteger STAGE = BlockProperties.am;
    protected static final VoxelShape b = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
    private final WorldGenTreeProvider c;

    protected BlockSapling(WorldGenTreeProvider worldgentreeprovider, Block.Info block_info) {
        super(block_info);
        this.c = worldgentreeprovider;
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockSapling.STAGE, 0));
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockSapling.b;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        super.a(iblockdata, world, blockposition, random);
        if (world.getLightLevel(blockposition.up()) >= 9 && random.nextInt(7) == 0) {
            this.grow(world, blockposition, iblockdata, random);
        }

    }

    public void grow(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if ((Integer) iblockdata.get(BlockSapling.STAGE) == 0) {
            generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.a((IBlockState) BlockSapling.STAGE), 4);
        } else {
            this.c.a(generatoraccess, blockposition, iblockdata, random);
        }

    }

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return true;
    }

    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return (double) world.random.nextFloat() < 0.45D;
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        this.grow(world, blockposition, iblockdata, random);
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockSapling.STAGE);
    }
}
