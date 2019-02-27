package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockKelp extends Block implements IFluidContainer {

    public static final BlockStateInteger a = BlockProperties.Y;
    protected static final VoxelShape b = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);

    protected BlockKelp(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockKelp.a, 0));
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockKelp.b;
    }

    @Nullable
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());

        return fluid.a(TagsFluid.WATER) && fluid.g() == 8 ? this.a((GeneratorAccess) blockactioncontext.getWorld()) : null;
    }

    public IBlockData a(GeneratorAccess generatoraccess) {
        return (IBlockData) this.getBlockData().set(BlockKelp.a, generatoraccess.m().nextInt(25));
    }

    public TextureType c() {
        return TextureType.CUTOUT;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public Fluid h(IBlockData iblockdata) {
        return FluidTypes.WATER.a(false);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (!iblockdata.canPlace(world, blockposition)) {
            world.setAir(blockposition, true);
        } else {
            BlockPosition blockposition1 = blockposition.up();
            IBlockData iblockdata1 = world.getType(blockposition1);

            if (iblockdata1.getBlock() == Blocks.WATER && (Integer) iblockdata.get(BlockKelp.a) < 25 && random.nextDouble() < 0.14D) {
                world.setTypeUpdate(blockposition1, (IBlockData) iblockdata.a((IBlockState) BlockKelp.a));
            }

        }
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();
        IBlockData iblockdata1 = iworldreader.getType(blockposition1);
        Block block = iblockdata1.getBlock();

        return block == Blocks.MAGMA_BLOCK ? false : block == this || block == Blocks.KELP_PLANT || Block.a(iblockdata1.getCollisionShape(iworldreader, blockposition1), EnumDirection.UP);
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (!iblockdata.canPlace(generatoraccess, blockposition)) {
            if (enumdirection == EnumDirection.DOWN) {
                return Blocks.AIR.getBlockData();
            }

            generatoraccess.getBlockTickList().a(blockposition, this, 1);
        }

        if (enumdirection == EnumDirection.UP && iblockdata1.getBlock() == this) {
            return Blocks.KELP_PLANT.getBlockData();
        } else {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
            return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        }
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockKelp.a);
    }

    public boolean canPlace(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        return false;
    }

    public boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        return false;
    }
}
