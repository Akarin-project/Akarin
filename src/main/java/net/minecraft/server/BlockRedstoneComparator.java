package net.minecraft.server;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class BlockRedstoneComparator extends BlockDiodeAbstract implements ITileEntity {

    public static final BlockStateEnum<BlockPropertyComparatorMode> MODE = BlockProperties.aq;

    public BlockRedstoneComparator(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockRedstoneComparator.FACING, EnumDirection.NORTH)).set(BlockRedstoneComparator.c, false)).set(BlockRedstoneComparator.MODE, BlockPropertyComparatorMode.COMPARE));
    }

    protected int k(IBlockData iblockdata) {
        return 2;
    }

    protected int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        TileEntity tileentity = iblockaccess.getTileEntity(blockposition);

        return tileentity instanceof TileEntityComparator ? ((TileEntityComparator) tileentity).c() : 0;
    }

    private int e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return iblockdata.get(BlockRedstoneComparator.MODE) == BlockPropertyComparatorMode.SUBTRACT ? Math.max(this.b(world, blockposition, iblockdata) - this.b((IWorldReader) world, blockposition, iblockdata), 0) : this.b(world, blockposition, iblockdata);
    }

    protected boolean a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.b(world, blockposition, iblockdata);

        return i >= 15 ? true : (i == 0 ? false : i >= this.b((IWorldReader) world, blockposition, iblockdata));
    }

    protected void a(World world, BlockPosition blockposition) {
        world.n(blockposition);
    }

    protected int b(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = super.b(world, blockposition, iblockdata);
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockRedstoneComparator.FACING);
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        IBlockData iblockdata1 = world.getType(blockposition1);

        if (iblockdata1.isComplexRedstone()) {
            i = iblockdata1.a(world, blockposition1);
        } else if (i < 15 && iblockdata1.isOccluding()) {
            blockposition1 = blockposition1.shift(enumdirection);
            iblockdata1 = world.getType(blockposition1);
            if (iblockdata1.isComplexRedstone()) {
                i = iblockdata1.a(world, blockposition1);
            } else if (iblockdata1.isAir()) {
                EntityItemFrame entityitemframe = this.a(world, enumdirection, blockposition1);

                if (entityitemframe != null) {
                    i = entityitemframe.q();
                }
            }
        }

        return i;
    }

    @Nullable
    private EntityItemFrame a(World world, EnumDirection enumdirection, BlockPosition blockposition) {
        List<EntityItemFrame> list = world.a(EntityItemFrame.class, new AxisAlignedBB((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), (double) (blockposition.getX() + 1), (double) (blockposition.getY() + 1), (double) (blockposition.getZ() + 1)), (entityitemframe) -> {
            return entityitemframe != null && entityitemframe.getDirection() == enumdirection;
        });

        return list.size() == 1 ? (EntityItemFrame) list.get(0) : null;
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (!entityhuman.abilities.mayBuild) {
            return false;
        } else {
            iblockdata = (IBlockData) iblockdata.a((IBlockState) BlockRedstoneComparator.MODE);
            float f3 = iblockdata.get(BlockRedstoneComparator.MODE) == BlockPropertyComparatorMode.SUBTRACT ? 0.55F : 0.5F;

            world.a(entityhuman, blockposition, SoundEffects.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, f3);
            world.setTypeAndData(blockposition, iblockdata, 2);
            this.f(world, blockposition, iblockdata);
            return true;
        }
    }

    protected void c(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!world.getBlockTickList().b(blockposition, this)) {
            int i = this.e(world, blockposition, iblockdata);
            TileEntity tileentity = world.getTileEntity(blockposition);
            int j = tileentity instanceof TileEntityComparator ? ((TileEntityComparator) tileentity).c() : 0;

            if (i != j || (Boolean) iblockdata.get(BlockRedstoneComparator.c) != this.a(world, blockposition, iblockdata)) {
                TickListPriority ticklistpriority = this.c((IBlockAccess) world, blockposition, iblockdata) ? TickListPriority.HIGH : TickListPriority.NORMAL;

                world.getBlockTickList().a(blockposition, this, 2, ticklistpriority);
            }

        }
    }

    private void f(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.e(world, blockposition, iblockdata);
        TileEntity tileentity = world.getTileEntity(blockposition);
        int j = 0;

        if (tileentity instanceof TileEntityComparator) {
            TileEntityComparator tileentitycomparator = (TileEntityComparator) tileentity;

            j = tileentitycomparator.c();
            tileentitycomparator.a(i);
        }

        if (j != i || iblockdata.get(BlockRedstoneComparator.MODE) == BlockPropertyComparatorMode.COMPARE) {
            boolean flag = this.a(world, blockposition, iblockdata);
            boolean flag1 = (Boolean) iblockdata.get(BlockRedstoneComparator.c);

            if (flag1 && !flag) {
                world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockRedstoneComparator.c, false), 2);
            } else if (!flag1 && flag) {
                world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockRedstoneComparator.c, true), 2);
            }

            this.d(world, blockposition, iblockdata);
        }

    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        this.f(world, blockposition, iblockdata);
    }

    public boolean a(IBlockData iblockdata, World world, BlockPosition blockposition, int i, int j) {
        super.a(iblockdata, world, blockposition, i, j);
        TileEntity tileentity = world.getTileEntity(blockposition);

        return tileentity != null && tileentity.c(i, j);
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntityComparator();
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockRedstoneComparator.FACING, BlockRedstoneComparator.MODE, BlockRedstoneComparator.c);
    }
}
