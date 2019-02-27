package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockTrapdoor extends BlockFacingHorizontal implements IFluidSource, IFluidContainer {

    public static final BlockStateBoolean OPEN = BlockProperties.r;
    public static final BlockStateEnum<BlockPropertyHalf> HALF = BlockProperties.Q;
    public static final BlockStateBoolean c = BlockProperties.t;
    public static final BlockStateBoolean o = BlockProperties.y;
    protected static final VoxelShape p = Block.a(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
    protected static final VoxelShape q = Block.a(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape r = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
    protected static final VoxelShape s = Block.a(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape t = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
    protected static final VoxelShape u = Block.a(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    protected BlockTrapdoor(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockTrapdoor.FACING, EnumDirection.NORTH)).set(BlockTrapdoor.OPEN, false)).set(BlockTrapdoor.HALF, BlockPropertyHalf.BOTTOM)).set(BlockTrapdoor.c, false)).set(BlockTrapdoor.o, false));
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        if (!(Boolean) iblockdata.get(BlockTrapdoor.OPEN)) {
            return iblockdata.get(BlockTrapdoor.HALF) == BlockPropertyHalf.TOP ? BlockTrapdoor.u : BlockTrapdoor.t;
        } else {
            switch ((EnumDirection) iblockdata.get(BlockTrapdoor.FACING)) {
            case NORTH:
            default:
                return BlockTrapdoor.s;
            case SOUTH:
                return BlockTrapdoor.r;
            case WEST:
                return BlockTrapdoor.q;
            case EAST:
                return BlockTrapdoor.p;
            }
        }
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        switch (pathmode) {
        case LAND:
            return (Boolean) iblockdata.get(BlockTrapdoor.OPEN);
        case WATER:
            return (Boolean) iblockdata.get(BlockTrapdoor.o);
        case AIR:
            return (Boolean) iblockdata.get(BlockTrapdoor.OPEN);
        default:
            return false;
        }
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (this.material == Material.ORE) {
            return false;
        } else {
            iblockdata = (IBlockData) iblockdata.a((IBlockState) BlockTrapdoor.OPEN);
            world.setTypeAndData(blockposition, iblockdata, 2);
            if ((Boolean) iblockdata.get(BlockTrapdoor.o)) {
                world.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) world));
            }

            this.a(entityhuman, world, blockposition, (Boolean) iblockdata.get(BlockTrapdoor.OPEN));
            return true;
        }
    }

    protected void a(@Nullable EntityHuman entityhuman, World world, BlockPosition blockposition, boolean flag) {
        int i;

        if (flag) {
            i = this.material == Material.ORE ? 1037 : 1007;
            world.a(entityhuman, i, blockposition, 0);
        } else {
            i = this.material == Material.ORE ? 1036 : 1013;
            world.a(entityhuman, i, blockposition, 0);
        }

    }

    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.isClientSide) {
            boolean flag = world.isBlockIndirectlyPowered(blockposition);

            if (flag != (Boolean) iblockdata.get(BlockTrapdoor.c)) {
                if ((Boolean) iblockdata.get(BlockTrapdoor.OPEN) != flag) {
                    iblockdata = (IBlockData) iblockdata.set(BlockTrapdoor.OPEN, flag);
                    this.a((EntityHuman) null, world, blockposition, flag);
                }

                world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockTrapdoor.c, flag), 2);
                if ((Boolean) iblockdata.get(BlockTrapdoor.o)) {
                    world.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) world));
                }
            }

        }
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = this.getBlockData();
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());
        EnumDirection enumdirection = blockactioncontext.getClickedFace();

        if (!blockactioncontext.c() && enumdirection.k().c()) {
            iblockdata = (IBlockData) ((IBlockData) iblockdata.set(BlockTrapdoor.FACING, enumdirection)).set(BlockTrapdoor.HALF, blockactioncontext.n() > 0.5F ? BlockPropertyHalf.TOP : BlockPropertyHalf.BOTTOM);
        } else {
            iblockdata = (IBlockData) ((IBlockData) iblockdata.set(BlockTrapdoor.FACING, blockactioncontext.f().opposite())).set(BlockTrapdoor.HALF, enumdirection == EnumDirection.UP ? BlockPropertyHalf.BOTTOM : BlockPropertyHalf.TOP);
        }

        if (blockactioncontext.getWorld().isBlockIndirectlyPowered(blockactioncontext.getClickPosition())) {
            iblockdata = (IBlockData) ((IBlockData) iblockdata.set(BlockTrapdoor.OPEN, true)).set(BlockTrapdoor.c, true);
        }

        return (IBlockData) iblockdata.set(BlockTrapdoor.o, fluid.c() == FluidTypes.WATER);
    }

    public TextureType c() {
        return TextureType.CUTOUT;
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockTrapdoor.FACING, BlockTrapdoor.OPEN, BlockTrapdoor.HALF, BlockTrapdoor.c, BlockTrapdoor.o);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return (enumdirection == EnumDirection.UP && iblockdata.get(BlockTrapdoor.HALF) == BlockPropertyHalf.TOP || enumdirection == EnumDirection.DOWN && iblockdata.get(BlockTrapdoor.HALF) == BlockPropertyHalf.BOTTOM) && !(Boolean) iblockdata.get(BlockTrapdoor.OPEN) ? EnumBlockFaceShape.SOLID : EnumBlockFaceShape.UNDEFINED;
    }

    public FluidType removeFluid(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if ((Boolean) iblockdata.get(BlockTrapdoor.o)) {
            generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockTrapdoor.o, false), 3);
            return FluidTypes.WATER;
        } else {
            return FluidTypes.EMPTY;
        }
    }

    public Fluid h(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockTrapdoor.o) ? FluidTypes.WATER.a(false) : super.h(iblockdata);
    }

    public boolean canPlace(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        return !(Boolean) iblockdata.get(BlockTrapdoor.o) && fluidtype == FluidTypes.WATER;
    }

    public boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        if (!(Boolean) iblockdata.get(BlockTrapdoor.o) && fluid.c() == FluidTypes.WATER) {
            if (!generatoraccess.e()) {
                generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockTrapdoor.o, true), 3);
                generatoraccess.getFluidTickList().a(blockposition, fluid.c(), fluid.c().a((IWorldReader) generatoraccess));
            }

            return true;
        } else {
            return false;
        }
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(BlockTrapdoor.o)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }
}
