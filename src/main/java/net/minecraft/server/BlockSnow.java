package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockSnow extends Block {

    public static final BlockStateInteger LAYERS = BlockProperties.ae;
    protected static final VoxelShape[] b = new VoxelShape[] { VoxelShapes.a(), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

    protected BlockSnow(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockSnow.LAYERS, 1));
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        switch (pathmode) {
        case LAND:
            return (Integer) iblockdata.get(BlockSnow.LAYERS) < 5;
        case WATER:
            return false;
        case AIR:
            return false;
        default:
            return false;
        }
    }

    public boolean a(IBlockData iblockdata) {
        return (Integer) iblockdata.get(BlockSnow.LAYERS) == 8;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.DOWN ? EnumBlockFaceShape.SOLID : EnumBlockFaceShape.UNDEFINED;
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockSnow.b[(Integer) iblockdata.get(BlockSnow.LAYERS)];
    }

    public VoxelShape f(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockSnow.b[(Integer) iblockdata.get(BlockSnow.LAYERS) - 1];
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        IBlockData iblockdata1 = iworldreader.getType(blockposition.down());
        Block block = iblockdata1.getBlock();

        if (block != Blocks.ICE && block != Blocks.PACKED_ICE && block != Blocks.BARRIER) {
            EnumBlockFaceShape enumblockfaceshape = iblockdata1.c(iworldreader, blockposition.down(), EnumDirection.UP);

            return enumblockfaceshape == EnumBlockFaceShape.SOLID || iblockdata1.a(TagsBlock.LEAVES) || block == this && (Integer) iblockdata1.get(BlockSnow.LAYERS) == 8;
        } else {
            return false;
        }
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return !iblockdata.canPlace(generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        Integer integer = (Integer) iblockdata.get(BlockSnow.LAYERS);

        if (this.X_() && EnchantmentManager.getEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) > 0) {
            if (integer == 8) {
                a(world, blockposition, new ItemStack(Blocks.SNOW_BLOCK));
            } else {
                for (int i = 0; i < integer; ++i) {
                    a(world, blockposition, this.t(iblockdata));
                }
            }
        } else {
            a(world, blockposition, new ItemStack(Items.SNOWBALL, integer));
        }

        world.setAir(blockposition);
        entityhuman.b(StatisticList.BLOCK_MINED.b(this));
        entityhuman.applyExhaustion(0.005F);
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.AIR;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (world.getBrightness(EnumSkyBlock.BLOCK, blockposition) > 11) {
            iblockdata.a(world, blockposition, 0);
            world.setAir(blockposition);
        }

    }

    public boolean a(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        int i = (Integer) iblockdata.get(BlockSnow.LAYERS);

        return blockactioncontext.getItemStack().getItem() == this.getItem() && i < 8 ? (blockactioncontext.c() ? blockactioncontext.getClickedFace() == EnumDirection.UP : true) : i == 1;
    }

    @Nullable
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition());

        if (iblockdata.getBlock() == this) {
            int i = (Integer) iblockdata.get(BlockSnow.LAYERS);

            return (IBlockData) iblockdata.set(BlockSnow.LAYERS, Math.min(8, i + 1));
        } else {
            return super.getPlacedState(blockactioncontext);
        }
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockSnow.LAYERS);
    }

    protected boolean X_() {
        return true;
    }
}
