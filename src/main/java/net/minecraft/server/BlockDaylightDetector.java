package net.minecraft.server;

public class BlockDaylightDetector extends BlockTileEntity {

    public static final BlockStateInteger POWER = BlockProperties.al;
    public static final BlockStateBoolean b = BlockProperties.m;
    protected static final VoxelShape c = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);

    public BlockDaylightDetector(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockDaylightDetector.POWER, 0)).set(BlockDaylightDetector.b, false));
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockDaylightDetector.c;
    }

    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Integer) iblockdata.get(BlockDaylightDetector.POWER);
    }

    public static void b(IBlockData iblockdata, World world, BlockPosition blockposition) {
        if (world.worldProvider.g()) {
            int i = world.getBrightness(EnumSkyBlock.SKY, blockposition) - world.c();
            float f = world.c(1.0F);
            boolean flag = (Boolean) iblockdata.get(BlockDaylightDetector.b);

            if (flag) {
                i = 15 - i;
            } else if (i > 0) {
                float f1 = f < 3.1415927F ? 0.0F : 6.2831855F;

                f += (f1 - f) * 0.2F;
                i = Math.round((float) i * MathHelper.cos(f));
            }

            i = MathHelper.clamp(i, 0, 15);
            if ((Integer) iblockdata.get(BlockDaylightDetector.POWER) != i) {
                world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockDaylightDetector.POWER, i), 3);
            }

        }
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (entityhuman.dy()) {
            if (world.isClientSide) {
                return true;
            } else {
                IBlockData iblockdata1 = (IBlockData) iblockdata.a((IBlockState) BlockDaylightDetector.b);

                world.setTypeAndData(blockposition, iblockdata1, 4);
                b(iblockdata1, world, blockposition);
                return true;
            }
        } else {
            return super.interact(iblockdata, world, blockposition, entityhuman, enumhand, enumdirection, f, f1, f2);
        }
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntityLightDetector();
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockDaylightDetector.POWER, BlockDaylightDetector.b);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.DOWN ? EnumBlockFaceShape.SOLID : EnumBlockFaceShape.UNDEFINED;
    }
}
