package net.minecraft.server;

public class BlockDropper extends BlockDispenser {

    private static final IDispenseBehavior c = new DispenseBehaviorItem();

    public BlockDropper(Block.Info block_info) {
        super(block_info);
    }

    protected IDispenseBehavior a(ItemStack itemstack) {
        return BlockDropper.c;
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntityDropper();
    }

    public void dispense(World world, BlockPosition blockposition) {
        SourceBlock sourceblock = new SourceBlock(world, blockposition);
        TileEntityDispenser tileentitydispenser = (TileEntityDispenser) sourceblock.getTileEntity();
        int i = tileentitydispenser.p();

        if (i < 0) {
            world.triggerEffect(1001, blockposition, 0);
        } else {
            ItemStack itemstack = tileentitydispenser.getItem(i);

            if (!itemstack.isEmpty()) {
                EnumDirection enumdirection = (EnumDirection) world.getType(blockposition).get(BlockDropper.FACING);
                IInventory iinventory = TileEntityHopper.a(world, blockposition.shift(enumdirection));
                ItemStack itemstack1;

                if (iinventory == null) {
                    itemstack1 = BlockDropper.c.dispense(sourceblock, itemstack);
                } else {
                    itemstack1 = TileEntityHopper.addItem(tileentitydispenser, iinventory, itemstack.cloneItemStack().cloneAndSubtract(1), enumdirection.opposite());
                    if (itemstack1.isEmpty()) {
                        itemstack1 = itemstack.cloneItemStack();
                        itemstack1.subtract(1);
                    } else {
                        itemstack1 = itemstack.cloneItemStack();
                    }
                }

                tileentitydispenser.setItem(i, itemstack1);
            }
        }
    }
}
