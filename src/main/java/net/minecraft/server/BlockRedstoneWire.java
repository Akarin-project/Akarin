package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class BlockRedstoneWire extends Block {

    public static final BlockStateEnum<BlockPropertyRedstoneSide> NORTH = BlockProperties.M;
    public static final BlockStateEnum<BlockPropertyRedstoneSide> EAST = BlockProperties.L;
    public static final BlockStateEnum<BlockPropertyRedstoneSide> SOUTH = BlockProperties.N;
    public static final BlockStateEnum<BlockPropertyRedstoneSide> WEST = BlockProperties.O;
    public static final BlockStateInteger POWER = BlockProperties.al;
    public static final Map<EnumDirection, BlockStateEnum<BlockPropertyRedstoneSide>> q = Maps.newEnumMap(ImmutableMap.of(EnumDirection.NORTH, BlockRedstoneWire.NORTH, EnumDirection.EAST, BlockRedstoneWire.EAST, EnumDirection.SOUTH, BlockRedstoneWire.SOUTH, EnumDirection.WEST, BlockRedstoneWire.WEST));
    protected static final VoxelShape[] r = new VoxelShape[] { Block.a(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.a(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.a(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.a(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.a(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.a(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.a(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.a(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.a(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.a(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.a(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.a(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.a(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D)};
    private boolean s = true;
    private final Set<BlockPosition> t = Sets.newHashSet();

    public BlockRedstoneWire(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockRedstoneWire.NORTH, BlockPropertyRedstoneSide.NONE)).set(BlockRedstoneWire.EAST, BlockPropertyRedstoneSide.NONE)).set(BlockRedstoneWire.SOUTH, BlockPropertyRedstoneSide.NONE)).set(BlockRedstoneWire.WEST, BlockPropertyRedstoneSide.NONE)).set(BlockRedstoneWire.POWER, 0));
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockRedstoneWire.r[w(iblockdata)];
    }

    private static int w(IBlockData iblockdata) {
        int i = 0;
        boolean flag = iblockdata.get(BlockRedstoneWire.NORTH) != BlockPropertyRedstoneSide.NONE;
        boolean flag1 = iblockdata.get(BlockRedstoneWire.EAST) != BlockPropertyRedstoneSide.NONE;
        boolean flag2 = iblockdata.get(BlockRedstoneWire.SOUTH) != BlockPropertyRedstoneSide.NONE;
        boolean flag3 = iblockdata.get(BlockRedstoneWire.WEST) != BlockPropertyRedstoneSide.NONE;

        if (flag || flag2 && !flag && !flag1 && !flag3) {
            i |= 1 << EnumDirection.NORTH.get2DRotationValue();
        }

        if (flag1 || flag3 && !flag && !flag1 && !flag2) {
            i |= 1 << EnumDirection.EAST.get2DRotationValue();
        }

        if (flag2 || flag && !flag1 && !flag2 && !flag3) {
            i |= 1 << EnumDirection.SOUTH.get2DRotationValue();
        }

        if (flag3 || flag1 && !flag && !flag2 && !flag3) {
            i |= 1 << EnumDirection.WEST.get2DRotationValue();
        }

        return i;
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();

        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockRedstoneWire.WEST, this.a((IBlockAccess) world, blockposition, EnumDirection.WEST))).set(BlockRedstoneWire.EAST, this.a((IBlockAccess) world, blockposition, EnumDirection.EAST))).set(BlockRedstoneWire.NORTH, this.a((IBlockAccess) world, blockposition, EnumDirection.NORTH))).set(BlockRedstoneWire.SOUTH, this.a((IBlockAccess) world, blockposition, EnumDirection.SOUTH));
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == EnumDirection.DOWN ? iblockdata : (enumdirection == EnumDirection.UP ? (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockRedstoneWire.WEST, this.a((IBlockAccess) generatoraccess, blockposition, EnumDirection.WEST))).set(BlockRedstoneWire.EAST, this.a((IBlockAccess) generatoraccess, blockposition, EnumDirection.EAST))).set(BlockRedstoneWire.NORTH, this.a((IBlockAccess) generatoraccess, blockposition, EnumDirection.NORTH))).set(BlockRedstoneWire.SOUTH, this.a((IBlockAccess) generatoraccess, blockposition, EnumDirection.SOUTH)) : (IBlockData) iblockdata.set((IBlockState) BlockRedstoneWire.q.get(enumdirection), this.a((IBlockAccess) generatoraccess, blockposition, enumdirection)));
    }

    public void b(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        BlockPosition.b blockposition_b = BlockPosition.b.r();
        Throwable throwable = null;

        try {
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();
                BlockPropertyRedstoneSide blockpropertyredstoneside = (BlockPropertyRedstoneSide) iblockdata.get((IBlockState) BlockRedstoneWire.q.get(enumdirection));

                if (blockpropertyredstoneside != BlockPropertyRedstoneSide.NONE && generatoraccess.getType(blockposition_b.g(blockposition).c(enumdirection)).getBlock() != this) {
                    blockposition_b.c(EnumDirection.DOWN);
                    IBlockData iblockdata1 = generatoraccess.getType(blockposition_b);

                    if (iblockdata1.getBlock() != Blocks.OBSERVER) {
                        BlockPosition blockposition1 = blockposition_b.shift(enumdirection.opposite());
                        IBlockData iblockdata2 = iblockdata1.updateState(enumdirection.opposite(), generatoraccess.getType(blockposition1), generatoraccess, blockposition_b, blockposition1);

                        a(iblockdata1, iblockdata2, generatoraccess, blockposition_b, i);
                    }

                    blockposition_b.g(blockposition).c(enumdirection).c(EnumDirection.UP);
                    IBlockData iblockdata3 = generatoraccess.getType(blockposition_b);

                    if (iblockdata3.getBlock() != Blocks.OBSERVER) {
                        BlockPosition blockposition2 = blockposition_b.shift(enumdirection.opposite());
                        IBlockData iblockdata4 = iblockdata3.updateState(enumdirection.opposite(), generatoraccess.getType(blockposition2), generatoraccess, blockposition_b, blockposition2);

                        a(iblockdata3, iblockdata4, generatoraccess, blockposition_b, i);
                    }
                }
            }
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_b != null) {
                if (throwable != null) {
                    try {
                        blockposition_b.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_b.close();
                }
            }

        }

    }

    private BlockPropertyRedstoneSide a(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        IBlockData iblockdata = iblockaccess.getType(blockposition.shift(enumdirection));
        IBlockData iblockdata1 = iblockaccess.getType(blockposition.up());

        if (!iblockdata1.isOccluding()) {
            boolean flag = iblockaccess.getType(blockposition1).q() || iblockaccess.getType(blockposition1).getBlock() == Blocks.GLOWSTONE;

            if (flag && k(iblockaccess.getType(blockposition1.up()))) {
                if (iblockdata.k()) {
                    return BlockPropertyRedstoneSide.UP;
                }

                return BlockPropertyRedstoneSide.SIDE;
            }
        }

        return !a(iblockaccess.getType(blockposition1), enumdirection) && (iblockdata.isOccluding() || !k(iblockaccess.getType(blockposition1.down()))) ? BlockPropertyRedstoneSide.NONE : BlockPropertyRedstoneSide.SIDE;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        IBlockData iblockdata1 = iworldreader.getType(blockposition.down());

        return iblockdata1.q() || iblockdata1.getBlock() == Blocks.GLOWSTONE;
    }

    private IBlockData a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        iblockdata = this.b(world, blockposition, iblockdata);
        List<BlockPosition> list = Lists.newArrayList(this.t);

        this.t.clear();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();

            world.applyPhysics(blockposition1, this);
        }

        return iblockdata;
    }

    private IBlockData b(World world, BlockPosition blockposition, IBlockData iblockdata) {
        IBlockData iblockdata1 = iblockdata;
        int i = (Integer) iblockdata.get(BlockRedstoneWire.POWER);
        byte b0 = 0;
        int j = this.getPower(b0, iblockdata);

        this.s = false;
        int k = world.u(blockposition);

        this.s = true;
        if (k > 0 && k > j - 1) {
            j = k;
        }

        int l = 0;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.shift(enumdirection);
            boolean flag = blockposition1.getX() != blockposition.getX() || blockposition1.getZ() != blockposition.getZ();
            IBlockData iblockdata2 = world.getType(blockposition1);

            if (flag) {
                l = this.getPower(l, iblockdata2);
            }

            if (iblockdata2.isOccluding() && !world.getType(blockposition.up()).isOccluding()) {
                if (flag && blockposition.getY() >= blockposition.getY()) {
                    l = this.getPower(l, world.getType(blockposition1.up()));
                }
            } else if (!iblockdata2.isOccluding() && flag && blockposition.getY() <= blockposition.getY()) {
                l = this.getPower(l, world.getType(blockposition1.down()));
            }
        }

        if (l > j) {
            j = l - 1;
        } else if (j > 0) {
            --j;
        } else {
            j = 0;
        }

        if (k > j - 1) {
            j = k;
        }

        if (i != j) {
            iblockdata = (IBlockData) iblockdata.set(BlockRedstoneWire.POWER, j);
            if (world.getType(blockposition) == iblockdata1) {
                world.setTypeAndData(blockposition, iblockdata, 2);
            }

            this.t.add(blockposition);
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i1 = aenumdirection.length;

            for (int j1 = 0; j1 < i1; ++j1) {
                EnumDirection enumdirection1 = aenumdirection[j1];

                this.t.add(blockposition.shift(enumdirection1));
            }
        }

        return iblockdata;
    }

    private void a(World world, BlockPosition blockposition) {
        if (world.getType(blockposition).getBlock() == this) {
            world.applyPhysics(blockposition, this);
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                world.applyPhysics(blockposition.shift(enumdirection), this);
            }

        }
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        if (iblockdata1.getBlock() != iblockdata.getBlock() && !world.isClientSide) {
            this.a(world, blockposition, iblockdata);
            Iterator iterator = EnumDirection.EnumDirectionLimit.VERTICAL.iterator();

            EnumDirection enumdirection;

            while (iterator.hasNext()) {
                enumdirection = (EnumDirection) iterator.next();
                world.applyPhysics(blockposition.shift(enumdirection), this);
            }

            iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                enumdirection = (EnumDirection) iterator.next();
                this.a(world, blockposition.shift(enumdirection));
            }

            iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                enumdirection = (EnumDirection) iterator.next();
                BlockPosition blockposition1 = blockposition.shift(enumdirection);

                if (world.getType(blockposition1).isOccluding()) {
                    this.a(world, blockposition1.up());
                } else {
                    this.a(world, blockposition1.down());
                }
            }

        }
    }

    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag && iblockdata.getBlock() != iblockdata1.getBlock()) {
            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
            if (!world.isClientSide) {
                EnumDirection[] aenumdirection = EnumDirection.values();
                int i = aenumdirection.length;

                for (int j = 0; j < i; ++j) {
                    EnumDirection enumdirection = aenumdirection[j];

                    world.applyPhysics(blockposition.shift(enumdirection), this);
                }

                this.a(world, blockposition, iblockdata);
                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                EnumDirection enumdirection1;

                while (iterator.hasNext()) {
                    enumdirection1 = (EnumDirection) iterator.next();
                    this.a(world, blockposition.shift(enumdirection1));
                }

                iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                while (iterator.hasNext()) {
                    enumdirection1 = (EnumDirection) iterator.next();
                    BlockPosition blockposition1 = blockposition.shift(enumdirection1);

                    if (world.getType(blockposition1).isOccluding()) {
                        this.a(world, blockposition1.up());
                    } else {
                        this.a(world, blockposition1.down());
                    }
                }

            }
        }
    }

    public int getPower(int i, IBlockData iblockdata) {
        if (iblockdata.getBlock() != this) {
            return i;
        } else {
            int j = (Integer) iblockdata.get(BlockRedstoneWire.POWER);

            return j > i ? j : i;
        }
    }

    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.isClientSide) {
            if (iblockdata.canPlace(world, blockposition)) {
                this.a(world, blockposition, iblockdata);
            } else {
                iblockdata.a(world, blockposition, 0);
                world.setAir(blockposition);
            }

        }
    }

    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return !this.s ? 0 : iblockdata.a(iblockaccess, blockposition, enumdirection);
    }

    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        if (!this.s) {
            return 0;
        } else {
            int i = (Integer) iblockdata.get(BlockRedstoneWire.POWER);

            if (i == 0) {
                return 0;
            } else if (enumdirection == EnumDirection.UP) {
                return i;
            } else {
                EnumSet<EnumDirection> enumset = EnumSet.noneOf(EnumDirection.class);
                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                while (iterator.hasNext()) {
                    EnumDirection enumdirection1 = (EnumDirection) iterator.next();

                    if (this.b(iblockaccess, blockposition, enumdirection1)) {
                        enumset.add(enumdirection1);
                    }
                }

                if (enumdirection.k().c() && enumset.isEmpty()) {
                    return i;
                } else if (enumset.contains(enumdirection) && !enumset.contains(enumdirection.f()) && !enumset.contains(enumdirection.e())) {
                    return i;
                } else {
                    return 0;
                }
            }
        }
    }

    private boolean b(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        IBlockData iblockdata = iblockaccess.getType(blockposition1);
        boolean flag = iblockdata.isOccluding();
        boolean flag1 = iblockaccess.getType(blockposition.up()).isOccluding();

        return !flag1 && flag && a(iblockaccess, blockposition1.up()) ? true : (a(iblockdata, enumdirection) ? true : (iblockdata.getBlock() == Blocks.REPEATER && (Boolean) iblockdata.get(BlockDiodeAbstract.c) && iblockdata.get(BlockDiodeAbstract.FACING) == enumdirection ? true : !flag && a(iblockaccess, blockposition1.down())));
    }

    protected static boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return k(iblockaccess.getType(blockposition));
    }

    protected static boolean k(IBlockData iblockdata) {
        return a(iblockdata, (EnumDirection) null);
    }

    protected static boolean a(IBlockData iblockdata, @Nullable EnumDirection enumdirection) {
        Block block = iblockdata.getBlock();

        if (block == Blocks.REDSTONE_WIRE) {
            return true;
        } else if (iblockdata.getBlock() == Blocks.REPEATER) {
            EnumDirection enumdirection1 = (EnumDirection) iblockdata.get(BlockRepeater.FACING);

            return enumdirection1 == enumdirection || enumdirection1.opposite() == enumdirection;
        } else {
            return Blocks.OBSERVER == iblockdata.getBlock() ? enumdirection == iblockdata.get(BlockObserver.FACING) : iblockdata.isPowerSource() && enumdirection != null;
        }
    }

    public boolean isPowerSource(IBlockData iblockdata) {
        return this.s;
    }

    public TextureType c() {
        return TextureType.CUTOUT;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
        case CLOCKWISE_180:
            return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockRedstoneWire.NORTH, iblockdata.get(BlockRedstoneWire.SOUTH))).set(BlockRedstoneWire.EAST, iblockdata.get(BlockRedstoneWire.WEST))).set(BlockRedstoneWire.SOUTH, iblockdata.get(BlockRedstoneWire.NORTH))).set(BlockRedstoneWire.WEST, iblockdata.get(BlockRedstoneWire.EAST));
        case COUNTERCLOCKWISE_90:
            return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockRedstoneWire.NORTH, iblockdata.get(BlockRedstoneWire.EAST))).set(BlockRedstoneWire.EAST, iblockdata.get(BlockRedstoneWire.SOUTH))).set(BlockRedstoneWire.SOUTH, iblockdata.get(BlockRedstoneWire.WEST))).set(BlockRedstoneWire.WEST, iblockdata.get(BlockRedstoneWire.NORTH));
        case CLOCKWISE_90:
            return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockRedstoneWire.NORTH, iblockdata.get(BlockRedstoneWire.WEST))).set(BlockRedstoneWire.EAST, iblockdata.get(BlockRedstoneWire.NORTH))).set(BlockRedstoneWire.SOUTH, iblockdata.get(BlockRedstoneWire.EAST))).set(BlockRedstoneWire.WEST, iblockdata.get(BlockRedstoneWire.SOUTH));
        default:
            return iblockdata;
        }
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        switch (enumblockmirror) {
        case LEFT_RIGHT:
            return (IBlockData) ((IBlockData) iblockdata.set(BlockRedstoneWire.NORTH, iblockdata.get(BlockRedstoneWire.SOUTH))).set(BlockRedstoneWire.SOUTH, iblockdata.get(BlockRedstoneWire.NORTH));
        case FRONT_BACK:
            return (IBlockData) ((IBlockData) iblockdata.set(BlockRedstoneWire.EAST, iblockdata.get(BlockRedstoneWire.WEST))).set(BlockRedstoneWire.WEST, iblockdata.get(BlockRedstoneWire.EAST));
        default:
            return super.a(iblockdata, enumblockmirror);
        }
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockRedstoneWire.NORTH, BlockRedstoneWire.EAST, BlockRedstoneWire.SOUTH, BlockRedstoneWire.WEST, BlockRedstoneWire.POWER);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
