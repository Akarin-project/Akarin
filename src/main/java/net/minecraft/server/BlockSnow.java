package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockSnow extends Block {

    public static final BlockStateInteger LAYERS = BlockProperties.ak;
    protected static final VoxelShape[] b = new VoxelShape[]{VoxelShapes.a(), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

    protected BlockSnow(Block.Info block_info) {
        super(block_info);
        this.o((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockSnow.LAYERS, 1));
    }

    @Override
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

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockSnow.b[(Integer) iblockdata.get(BlockSnow.LAYERS)];
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockSnow.b[(Integer) iblockdata.get(BlockSnow.LAYERS) - 1];
    }

    @Override
    public boolean n(IBlockData iblockdata) {
        return true;
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        IBlockData iblockdata1 = iworldreader.getType(blockposition.down());
        Block block = iblockdata1.getBlock();

        return block != Blocks.ICE && block != Blocks.PACKED_ICE && block != Blocks.BARRIER ? Block.a(iblockdata1.getCollisionShape(iworldreader, blockposition.down()), EnumDirection.UP) || block == this && (Integer) iblockdata1.get(BlockSnow.LAYERS) == 8 : false;
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return !iblockdata.canPlace(generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public void tick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (world.getBrightness(EnumSkyBlock.BLOCK, blockposition) > 11) {
            // CraftBukkit start
            if (org.bukkit.craftbukkit.event.CraftEventFactory.callBlockFadeEvent(world, blockposition, Blocks.AIR.getBlockData()).isCancelled()) {
                return;
            }
            // CraftBukkit end
            c(iblockdata, world, blockposition);
            world.a(blockposition, false);
        }

    }

    @Override
    public boolean a(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        int i = (Integer) iblockdata.get(BlockSnow.LAYERS);

        return blockactioncontext.getItemStack().getItem() == this.getItem() && i < 8 ? (blockactioncontext.c() ? blockactioncontext.getClickedFace() == EnumDirection.UP : true) : i == 1;
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition());

        if (iblockdata.getBlock() == this) {
            int i = (Integer) iblockdata.get(BlockSnow.LAYERS);

            return (IBlockData) iblockdata.set(BlockSnow.LAYERS, Math.min(8, i + 1));
        } else {
            return super.getPlacedState(blockactioncontext);
        }
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockSnow.LAYERS);
    }
}
