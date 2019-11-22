package net.minecraft.server;

public class BlockDaylightDetector extends BlockTileEntity {

    public static final BlockStateInteger POWER = BlockProperties.as;
    public static final BlockStateBoolean b = BlockProperties.p;
    protected static final VoxelShape c = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);

    public BlockDaylightDetector(Block.Info block_info) {
        super(block_info);
        this.o((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockDaylightDetector.POWER, 0)).set(BlockDaylightDetector.b, false));
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockDaylightDetector.c;
    }

    @Override
    public boolean n(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Integer) iblockdata.get(BlockDaylightDetector.POWER);
    }

    public static void d(IBlockData iblockdata, World world, BlockPosition blockposition) {
        if (world.worldProvider.g()) {
            int i = world.getBrightness(EnumSkyBlock.SKY, blockposition) - world.c();
            float f = world.b(1.0F);
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
                i = org.bukkit.craftbukkit.event.CraftEventFactory.callRedstoneChange(world, blockposition, ((Integer) iblockdata.get(POWER)), i).getNewCurrent(); // CraftBukkit - Call BlockRedstoneEvent
                world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockDaylightDetector.POWER, i), 3);
            }

        }
    }

    @Override
    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (entityhuman.dQ()) {
            if (world.isClientSide) {
                return true;
            } else {
                IBlockData iblockdata1 = (IBlockData) iblockdata.a((IBlockState) BlockDaylightDetector.b);

                world.setTypeAndData(blockposition, iblockdata1, 4);
                d(iblockdata1, world, blockposition);
                return true;
            }
        } else {
            return super.interact(iblockdata, world, blockposition, entityhuman, enumhand, movingobjectpositionblock);
        }
    }

    @Override
    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityLightDetector();
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockDaylightDetector.POWER, BlockDaylightDetector.b);
    }
}
