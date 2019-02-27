package net.minecraft.server;

public class BlockBeacon extends BlockTileEntity {

    public BlockBeacon(Block.Info block_info) {
        super(block_info);
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntityBeacon();
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (world.isClientSide) {
            return true;
        } else {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityBeacon) {
                entityhuman.openContainer((TileEntityBeacon) tileentity);
                entityhuman.a(StatisticList.INTERACT_WITH_BEACON);
            }

            return true;
        }
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityBeacon) {
                ((TileEntityBeacon) tileentity).setCustomName(itemstack.getName());
            }
        }

    }

    public TextureType c() {
        return TextureType.CUTOUT;
    }

    public static void a(World world, BlockPosition blockposition) {
        HttpUtilities.a.submit(() -> {
            Chunk chunk = world.getChunkAtWorldCoords(blockposition);

            for (int i = blockposition.getY() - 1; i >= 0; --i) {
                BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), i, blockposition.getZ());

                if (!chunk.c(blockposition1)) {
                    break;
                }

                IBlockData iblockdata = world.getType(blockposition1);

                if (iblockdata.getBlock() == Blocks.BEACON) {
                    ((WorldServer) world).postToMainThread(() -> {
                        TileEntity tileentity = world.getTileEntity(blockposition1);

                        if (tileentity instanceof TileEntityBeacon) {
                            ((TileEntityBeacon) tileentity).p();
                            world.playBlockAction(blockposition1, Blocks.BEACON, 1, 0);
                        }

                    });
                }
            }

        });
    }
}
