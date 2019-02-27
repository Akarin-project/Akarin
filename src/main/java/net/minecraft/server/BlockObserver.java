package net.minecraft.server;

import java.util.Random;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class BlockObserver extends BlockDirectional {

    public static final BlockStateBoolean b = BlockProperties.t;

    public BlockObserver(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockObserver.FACING, EnumDirection.SOUTH)).set(BlockObserver.b, false));
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockObserver.FACING, BlockObserver.b);
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockObserver.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockObserver.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockObserver.FACING)));
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if ((Boolean) iblockdata.get(BlockObserver.b)) {
            // CraftBukkit start
            if (CraftEventFactory.callRedstoneChange(world, blockposition, 15, 0).getNewCurrent() != 0) {
                return;
            }
            // CraftBukkit end
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockObserver.b, false), 2);
        } else {
            // CraftBukkit start
            if (CraftEventFactory.callRedstoneChange(world, blockposition, 0, 15).getNewCurrent() != 15) {
                return;
            }
            // CraftBukkit end
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockObserver.b, true), 2);
            world.getBlockTickList().a(blockposition, this, 2);
        }

        this.a(world, blockposition, iblockdata);
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (iblockdata.get(BlockObserver.FACING) == enumdirection && !(Boolean) iblockdata.get(BlockObserver.b)) {
            this.a(generatoraccess, blockposition);
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    private void a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        if (!generatoraccess.e() && !generatoraccess.getBlockTickList().a(blockposition, this)) {
            generatoraccess.getBlockTickList().a(blockposition, this, 2);
        }

    }

    protected void a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockObserver.FACING);
        BlockPosition blockposition1 = blockposition.shift(enumdirection.opposite());

        world.a(blockposition1, (Block) this, blockposition);
        world.a(blockposition1, (Block) this, enumdirection);
    }

    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return iblockdata.a(iblockaccess, blockposition, enumdirection);
    }

    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.get(BlockObserver.b) && iblockdata.get(BlockObserver.FACING) == enumdirection ? 15 : 0;
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        if (iblockdata.getBlock() != iblockdata1.getBlock()) {
            if (!world.e() && (Boolean) iblockdata.get(BlockObserver.b) && !world.getBlockTickList().a(blockposition, this)) {
                IBlockData iblockdata2 = (IBlockData) iblockdata.set(BlockObserver.b, false);

                world.setTypeAndData(blockposition, iblockdata2, 18);
                this.a(world, blockposition, iblockdata2);
            }

        }
    }

    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata.getBlock() != iblockdata1.getBlock()) {
            if (!world.isClientSide && (Boolean) iblockdata.get(BlockObserver.b) && world.getBlockTickList().a(blockposition, this)) {
                this.a(world, blockposition, (IBlockData) iblockdata.set(BlockObserver.b, false));
            }

        }
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockObserver.FACING, blockactioncontext.d().opposite().opposite());
    }
}
