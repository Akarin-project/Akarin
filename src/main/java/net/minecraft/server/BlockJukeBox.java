package net.minecraft.server;

public class BlockJukeBox extends BlockTileEntity {

    public static final BlockStateBoolean HAS_RECORD = BlockProperties.n;

    protected BlockJukeBox(Block.Info block_info) {
        super(block_info);
        this.o((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockJukeBox.HAS_RECORD, false));
    }

    @Override
    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if ((Boolean) iblockdata.get(BlockJukeBox.HAS_RECORD)) {
            this.dropRecord(world, blockposition);
            iblockdata = (IBlockData) iblockdata.set(BlockJukeBox.HAS_RECORD, false);
            world.setTypeAndData(blockposition, iblockdata, 2);
            return true;
        } else {
            return false;
        }
    }

    public void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, ItemStack itemstack) {
        TileEntity tileentity = generatoraccess.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityJukeBox) {
            // CraftBukkit start - There can only be one
            itemstack = itemstack.cloneItemStack();
            if (!itemstack.isEmpty()) {
                itemstack.setCount(1);
            }
            ((TileEntityJukeBox) tileentity).setRecord(itemstack);
            // CraftBukkit end
            generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockJukeBox.HAS_RECORD, true), 2);
        }
    }

    public void dropRecord(World world, BlockPosition blockposition) {
        if (!world.isClientSide) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityJukeBox) {
                TileEntityJukeBox tileentityjukebox = (TileEntityJukeBox) tileentity;
                ItemStack itemstack = tileentityjukebox.getRecord();

                if (!itemstack.isEmpty()) {
                    world.triggerEffect(1010, blockposition, 0);
                    tileentityjukebox.clear();
                    float f = 0.7F;
                    double d0 = (double) (world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                    double d1 = (double) (world.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
                    double d2 = (double) (world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                    ItemStack itemstack1 = itemstack.cloneItemStack();
                    EntityItem entityitem = new EntityItem(world, (double) blockposition.getX() + d0, (double) blockposition.getY() + d1, (double) blockposition.getZ() + d2, itemstack1);

                    entityitem.defaultPickupDelay();
                    world.addEntity(entityitem);
                }
            }
        }
    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata.getBlock() != iblockdata1.getBlock()) {
            this.dropRecord(world, blockposition);
            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityJukeBox();
    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityJukeBox) {
            Item item = ((TileEntityJukeBox) tileentity).getRecord().getItem();

            if (item instanceof ItemRecord) {
                return ((ItemRecord) item).d();
            }
        }

        return 0;
    }

    @Override
    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockJukeBox.HAS_RECORD);
    }
}
