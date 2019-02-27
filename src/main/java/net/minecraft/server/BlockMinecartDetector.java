package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class BlockMinecartDetector extends BlockMinecartTrackAbstract {

    public static final BlockStateEnum<BlockPropertyTrackPosition> SHAPE = BlockProperties.S;
    public static final BlockStateBoolean POWERED = BlockProperties.t;

    public BlockMinecartDetector(Block.Info block_info) {
        super(true, block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockMinecartDetector.POWERED, false)).set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_SOUTH));
    }

    public int a(IWorldReader iworldreader) {
        return 20;
    }

    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!world.isClientSide) {
            if (!(Boolean) iblockdata.get(BlockMinecartDetector.POWERED)) {
                this.a(world, blockposition, iblockdata);
            }
        }
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (!world.isClientSide && (Boolean) iblockdata.get(BlockMinecartDetector.POWERED)) {
            this.a(world, blockposition, iblockdata);
        }
    }

    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.get(BlockMinecartDetector.POWERED) ? 15 : 0;
    }

    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return !(Boolean) iblockdata.get(BlockMinecartDetector.POWERED) ? 0 : (enumdirection == EnumDirection.UP ? 15 : 0);
    }

    private void a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        boolean flag = (Boolean) iblockdata.get(BlockMinecartDetector.POWERED);
        boolean flag1 = false;
        List<EntityMinecartAbstract> list = this.a(world, blockposition, EntityMinecartAbstract.class, (Predicate) null);

        if (!list.isEmpty()) {
            flag1 = true;
        }

        if (flag1 && !flag) {
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockMinecartDetector.POWERED, true), 3);
            this.b(world, blockposition, iblockdata, true);
            world.applyPhysics(blockposition, this);
            world.applyPhysics(blockposition.down(), this);
            world.a(blockposition, blockposition);
        }

        if (!flag1 && flag) {
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockMinecartDetector.POWERED, false), 3);
            this.b(world, blockposition, iblockdata, false);
            world.applyPhysics(blockposition, this);
            world.applyPhysics(blockposition.down(), this);
            world.a(blockposition, blockposition);
        }

        if (flag1) {
            world.getBlockTickList().a(blockposition, this, this.a((IWorldReader) world));
        }

        world.updateAdjacentComparators(blockposition, this);
    }

    protected void b(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        MinecartTrackLogic minecarttracklogic = new MinecartTrackLogic(world, blockposition, iblockdata);
        List<BlockPosition> list = minecarttracklogic.a();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();
            IBlockData iblockdata1 = world.getType(blockposition1);

            iblockdata1.doPhysics(world, blockposition1, iblockdata1.getBlock(), blockposition);
        }

    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        if (iblockdata1.getBlock() != iblockdata.getBlock()) {
            super.onPlace(iblockdata, world, blockposition, iblockdata1);
            this.a(world, blockposition, iblockdata);
        }
    }

    public IBlockState<BlockPropertyTrackPosition> e() {
        return BlockMinecartDetector.SHAPE;
    }

    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        if ((Boolean) iblockdata.get(BlockMinecartDetector.POWERED)) {
            List<EntityMinecartCommandBlock> list = this.a(world, blockposition, EntityMinecartCommandBlock.class, (Predicate) null);

            if (!list.isEmpty()) {
                return ((EntityMinecartCommandBlock) list.get(0)).getCommandBlock().i();
            }

            List<EntityMinecartAbstract> list1 = this.a(world, blockposition, EntityMinecartAbstract.class, IEntitySelector.d);

            if (!list1.isEmpty()) {
                return Container.b((IInventory) list1.get(0));
            }
        }

        return 0;
    }

    protected <T extends EntityMinecartAbstract> List<T> a(World world, BlockPosition blockposition, Class<T> oclass, @Nullable Predicate<Entity> predicate) {
        return world.a(oclass, this.a(blockposition), predicate);
    }

    private AxisAlignedBB a(BlockPosition blockposition) {
        float f = 0.2F;

        return new AxisAlignedBB((double) ((float) blockposition.getX() + 0.2F), (double) blockposition.getY(), (double) ((float) blockposition.getZ() + 0.2F), (double) ((float) (blockposition.getX() + 1) - 0.2F), (double) ((float) (blockposition.getY() + 1) - 0.2F), (double) ((float) (blockposition.getZ() + 1) - 0.2F));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
        case CLOCKWISE_180:
            switch ((BlockPropertyTrackPosition) iblockdata.get(BlockMinecartDetector.SHAPE)) {
            case ASCENDING_EAST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_WEST);
            case ASCENDING_WEST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_EAST);
            case ASCENDING_NORTH:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_SOUTH);
            case ASCENDING_SOUTH:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_NORTH);
            case SOUTH_EAST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
            case SOUTH_WEST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
            case NORTH_WEST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
            case NORTH_EAST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
            }
        case COUNTERCLOCKWISE_90:
            switch ((BlockPropertyTrackPosition) iblockdata.get(BlockMinecartDetector.SHAPE)) {
            case ASCENDING_EAST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_NORTH);
            case ASCENDING_WEST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_SOUTH);
            case ASCENDING_NORTH:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_WEST);
            case ASCENDING_SOUTH:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_EAST);
            case SOUTH_EAST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
            case SOUTH_WEST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
            case NORTH_WEST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
            case NORTH_EAST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
            case NORTH_SOUTH:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.EAST_WEST);
            case EAST_WEST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_SOUTH);
            }
        case CLOCKWISE_90:
            switch ((BlockPropertyTrackPosition) iblockdata.get(BlockMinecartDetector.SHAPE)) {
            case ASCENDING_EAST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_SOUTH);
            case ASCENDING_WEST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_NORTH);
            case ASCENDING_NORTH:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_EAST);
            case ASCENDING_SOUTH:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_WEST);
            case SOUTH_EAST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
            case SOUTH_WEST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
            case NORTH_WEST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
            case NORTH_EAST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
            case NORTH_SOUTH:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.EAST_WEST);
            case EAST_WEST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_SOUTH);
            }
        default:
            return iblockdata;
        }
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        BlockPropertyTrackPosition blockpropertytrackposition = (BlockPropertyTrackPosition) iblockdata.get(BlockMinecartDetector.SHAPE);

        switch (enumblockmirror) {
        case LEFT_RIGHT:
            switch (blockpropertytrackposition) {
            case ASCENDING_NORTH:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_SOUTH);
            case ASCENDING_SOUTH:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_NORTH);
            case SOUTH_EAST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
            case SOUTH_WEST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
            case NORTH_WEST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
            case NORTH_EAST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
            default:
                return super.a(iblockdata, enumblockmirror);
            }
        case FRONT_BACK:
            switch (blockpropertytrackposition) {
            case ASCENDING_EAST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_WEST);
            case ASCENDING_WEST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.ASCENDING_EAST);
            case ASCENDING_NORTH:
            case ASCENDING_SOUTH:
            default:
                break;
            case SOUTH_EAST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_WEST);
            case SOUTH_WEST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.SOUTH_EAST);
            case NORTH_WEST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_EAST);
            case NORTH_EAST:
                return (IBlockData) iblockdata.set(BlockMinecartDetector.SHAPE, BlockPropertyTrackPosition.NORTH_WEST);
            }
        }

        return super.a(iblockdata, enumblockmirror);
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockMinecartDetector.SHAPE, BlockMinecartDetector.POWERED);
    }
}
