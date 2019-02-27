package net.minecraft.server;

public class BlockJukeBox extends BlockTileEntity {

    public static final BlockStateBoolean HAS_RECORD = BlockProperties.l;

    protected BlockJukeBox(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockJukeBox.HAS_RECORD, false));
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
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
            ((TileEntityJukeBox) tileentity).setRecord(itemstack.cloneItemStack());
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
                    world.a(blockposition, (SoundEffect) null);
                    tileentityjukebox.setRecord(ItemStack.a);
                    float f = 0.7F;
                    double d0 = (double) (world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                    double d1 = (double) (world.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
                    double d2 = (double) (world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                    ItemStack itemstack1 = itemstack.cloneItemStack();
                    EntityItem entityitem = new EntityItem(world, (double) blockposition.getX() + d0, (double) blockposition.getY() + d1, (double) blockposition.getZ() + d2, itemstack1);

                    entityitem.n();
                    world.addEntity(entityitem);
                }
            }
        }
    }

    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata.getBlock() != iblockdata1.getBlock()) {
            this.dropRecord(world, blockposition);
            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, float f, int i) {
        if (!world.isClientSide) {
            super.dropNaturally(iblockdata, world, blockposition, f, 0);
        }
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntityJukeBox();
    }

    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

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

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockJukeBox.HAS_RECORD);
    }
}
