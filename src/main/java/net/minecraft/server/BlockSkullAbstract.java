package net.minecraft.server;

public abstract class BlockSkullAbstract extends BlockTileEntity {

    private final BlockSkull.a a;

    public BlockSkullAbstract(BlockSkull.a blockskull_a, Block.Info block_info) {
        super(block_info);
        this.a = blockskull_a;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntitySkull();
    }

    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, float f, int i) {}

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.isClientSide && entityhuman.abilities.canInstantlyBuild) {
            TileEntitySkull.a(world, blockposition);
        }

        super.a(world, blockposition, iblockdata, entityhuman);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata.getBlock() != iblockdata1.getBlock() && !world.isClientSide) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntitySkull) {
                TileEntitySkull tileentityskull = (TileEntitySkull) tileentity;

                if (tileentityskull.shouldDrop()) {
                    ItemStack itemstack = this.a((IBlockAccess) world, blockposition, iblockdata);
                    Block block = tileentityskull.getBlock().getBlock();

                    if ((block == Blocks.PLAYER_HEAD || block == Blocks.PLAYER_WALL_HEAD) && tileentityskull.getGameProfile() != null) {
                        NBTTagCompound nbttagcompound = new NBTTagCompound();

                        GameProfileSerializer.serialize(nbttagcompound, tileentityskull.getGameProfile());
                        itemstack.getOrCreateTag().set("SkullOwner", nbttagcompound);
                    }

                    a(world, blockposition, itemstack);
                }
            }

            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }
}
