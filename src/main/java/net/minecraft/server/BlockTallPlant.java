package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockTallPlant extends BlockPlant {

    public static final BlockStateEnum<BlockPropertyDoubleBlockHalf> HALF = BlockProperties.P;

    public BlockTallPlant(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockTallPlant.HALF, BlockPropertyDoubleBlockHalf.LOWER));
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        BlockPropertyDoubleBlockHalf blockpropertydoubleblockhalf = (BlockPropertyDoubleBlockHalf) iblockdata.get(BlockTallPlant.HALF);

        return enumdirection.k() == EnumDirection.EnumAxis.Y && blockpropertydoubleblockhalf == BlockPropertyDoubleBlockHalf.LOWER == (enumdirection == EnumDirection.UP) && (iblockdata1.getBlock() != this || iblockdata1.get(BlockTallPlant.HALF) == blockpropertydoubleblockhalf) ? Blocks.AIR.getBlockData() : (blockpropertydoubleblockhalf == BlockPropertyDoubleBlockHalf.LOWER && enumdirection == EnumDirection.DOWN && !iblockdata.canPlace(generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1));
    }

    @Nullable
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        BlockPosition blockposition = blockactioncontext.getClickPosition();

        return blockposition.getY() < 255 && blockactioncontext.getWorld().getType(blockposition.up()).a(blockactioncontext) ? super.getPlacedState(blockactioncontext) : null;
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        world.setTypeAndData(blockposition.up(), (IBlockData) this.getBlockData().set(BlockTallPlant.HALF, BlockPropertyDoubleBlockHalf.UPPER), 3);
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        if (iblockdata.get(BlockTallPlant.HALF) != BlockPropertyDoubleBlockHalf.UPPER) {
            return super.canPlace(iblockdata, iworldreader, blockposition);
        } else {
            IBlockData iblockdata1 = iworldreader.getType(blockposition.down());

            return iblockdata1.getBlock() == this && iblockdata1.get(BlockTallPlant.HALF) == BlockPropertyDoubleBlockHalf.LOWER;
        }
    }

    public void a(GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        generatoraccess.setTypeAndData(blockposition, (IBlockData) this.getBlockData().set(BlockTallPlant.HALF, BlockPropertyDoubleBlockHalf.LOWER), i);
        generatoraccess.setTypeAndData(blockposition.up(), (IBlockData) this.getBlockData().set(BlockTallPlant.HALF, BlockPropertyDoubleBlockHalf.UPPER), i);
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        super.a(world, entityhuman, blockposition, Blocks.AIR.getBlockData(), tileentity, itemstack);
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        BlockPropertyDoubleBlockHalf blockpropertydoubleblockhalf = (BlockPropertyDoubleBlockHalf) iblockdata.get(BlockTallPlant.HALF);
        boolean flag = blockpropertydoubleblockhalf == BlockPropertyDoubleBlockHalf.LOWER;
        BlockPosition blockposition1 = flag ? blockposition.up() : blockposition.down();
        IBlockData iblockdata1 = world.getType(blockposition1);

        if (iblockdata1.getBlock() == this && iblockdata1.get(BlockTallPlant.HALF) != blockpropertydoubleblockhalf) {
            world.setTypeAndData(blockposition1, Blocks.AIR.getBlockData(), 35);
            world.a(entityhuman, 2001, blockposition1, Block.getCombinedId(iblockdata1));
            if (!world.isClientSide && !entityhuman.u()) {
                if (flag) {
                    this.a(iblockdata, world, blockposition, entityhuman.getItemInMainHand());
                } else {
                    this.a(iblockdata1, world, blockposition1, entityhuman.getItemInMainHand());
                }
            }
        }

        super.a(world, blockposition, iblockdata, entityhuman);
    }

    protected void a(IBlockData iblockdata, World world, BlockPosition blockposition, ItemStack itemstack) {
        iblockdata.a(world, blockposition, 0);
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return (IMaterial) (iblockdata.get(BlockTallPlant.HALF) == BlockPropertyDoubleBlockHalf.LOWER ? super.getDropType(iblockdata, world, blockposition, i) : Items.AIR);
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockTallPlant.HALF);
    }

    public Block.EnumRandomOffset q() {
        return Block.EnumRandomOffset.XZ;
    }
}
