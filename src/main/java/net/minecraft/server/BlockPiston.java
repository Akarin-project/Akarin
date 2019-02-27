package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BlockPiston extends BlockDirectional {

    public static final BlockStateBoolean EXTENDED = BlockProperties.f;
    protected static final VoxelShape c = Block.a(0.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
    protected static final VoxelShape o = Block.a(4.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape p = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 12.0D);
    protected static final VoxelShape q = Block.a(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape r = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    protected static final VoxelShape s = Block.a(0.0D, 4.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private final boolean sticky;

    public BlockPiston(boolean flag, Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockPiston.FACING, EnumDirection.NORTH)).set(BlockPiston.EXTENDED, false));
        this.sticky = flag;
    }

    public boolean q(IBlockData iblockdata) {
        return !(Boolean) iblockdata.get(BlockPiston.EXTENDED);
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        if ((Boolean) iblockdata.get(BlockPiston.EXTENDED)) {
            switch ((EnumDirection) iblockdata.get(BlockPiston.FACING)) {
            case DOWN:
                return BlockPiston.s;
            case UP:
            default:
                return BlockPiston.r;
            case NORTH:
                return BlockPiston.q;
            case SOUTH:
                return BlockPiston.p;
            case WEST:
                return BlockPiston.o;
            case EAST:
                return BlockPiston.c;
            }
        } else {
            return VoxelShapes.b();
        }
    }

    public boolean r(IBlockData iblockdata) {
        return !(Boolean) iblockdata.get(BlockPiston.EXTENDED) || iblockdata.get(BlockPiston.FACING) == EnumDirection.DOWN;
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (!world.isClientSide) {
            this.a(world, blockposition, iblockdata);
        }

    }

    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.isClientSide) {
            this.a(world, blockposition, iblockdata);
        }

    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        if (iblockdata1.getBlock() != iblockdata.getBlock()) {
            if (!world.isClientSide && world.getTileEntity(blockposition) == null) {
                this.a(world, blockposition, iblockdata);
            }

        }
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) ((IBlockData) this.getBlockData().set(BlockPiston.FACING, blockactioncontext.d().opposite())).set(BlockPiston.EXTENDED, false);
    }

    private void a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockPiston.FACING);
        boolean flag = this.a(world, blockposition, enumdirection);

        if (flag && !(Boolean) iblockdata.get(BlockPiston.EXTENDED)) {
            if ((new PistonExtendsChecker(world, blockposition, enumdirection, true)).a()) {
                world.playBlockAction(blockposition, this, 0, enumdirection.a());
            }
        } else if (!flag && (Boolean) iblockdata.get(BlockPiston.EXTENDED)) {
            BlockPosition blockposition1 = blockposition.shift(enumdirection, 2);
            IBlockData iblockdata1 = world.getType(blockposition1);
            byte b0 = 1;

            if (iblockdata1.getBlock() == Blocks.MOVING_PISTON && iblockdata1.get(BlockPiston.FACING) == enumdirection) {
                TileEntity tileentity = world.getTileEntity(blockposition1);

                if (tileentity instanceof TileEntityPiston) {
                    TileEntityPiston tileentitypiston = (TileEntityPiston) tileentity;

                    if (tileentitypiston.c() && (tileentitypiston.a(0.0F) < 0.5F || world.getTime() == tileentitypiston.k() || ((WorldServer) world).j_())) {
                        b0 = 2;
                    }
                }
            }

            world.playBlockAction(blockposition, this, b0, enumdirection.a());
        }

    }

    private boolean a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        int j;

        for (j = 0; j < i; ++j) {
            EnumDirection enumdirection1 = aenumdirection[j];

            if (enumdirection1 != enumdirection && world.isBlockFacePowered(blockposition.shift(enumdirection1), enumdirection1)) {
                return true;
            }
        }

        if (world.isBlockFacePowered(blockposition, EnumDirection.DOWN)) {
            return true;
        } else {
            BlockPosition blockposition1 = blockposition.up();
            EnumDirection[] aenumdirection1 = EnumDirection.values();

            j = aenumdirection1.length;

            for (int k = 0; k < j; ++k) {
                EnumDirection enumdirection2 = aenumdirection1[k];

                if (enumdirection2 != EnumDirection.DOWN && world.isBlockFacePowered(blockposition1.shift(enumdirection2), enumdirection2)) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean a(IBlockData iblockdata, World world, BlockPosition blockposition, int i, int j) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockPiston.FACING);

        if (!world.isClientSide) {
            boolean flag = this.a(world, blockposition, enumdirection);

            if (flag && (i == 1 || i == 2)) {
                world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockPiston.EXTENDED, true), 2);
                return false;
            }

            if (!flag && i == 0) {
                return false;
            }
        }

        if (i == 0) {
            if (!this.a(world, blockposition, enumdirection, true)) {
                return false;
            }

            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockPiston.EXTENDED, true), 67);
            world.a((EntityHuman) null, blockposition, SoundEffects.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.25F + 0.6F);
        } else if (i == 1 || i == 2) {
            TileEntity tileentity = world.getTileEntity(blockposition.shift(enumdirection));

            if (tileentity instanceof TileEntityPiston) {
                ((TileEntityPiston) tileentity).j();
            }

            world.setTypeAndData(blockposition, (IBlockData) ((IBlockData) Blocks.MOVING_PISTON.getBlockData().set(BlockPistonMoving.a, enumdirection)).set(BlockPistonMoving.b, this.sticky ? BlockPropertyPistonType.STICKY : BlockPropertyPistonType.DEFAULT), 3);
            world.setTileEntity(blockposition, BlockPistonMoving.a((IBlockData) this.getBlockData().set(BlockPiston.FACING, EnumDirection.fromType1(j & 7)), enumdirection, false, true));
            if (this.sticky) {
                BlockPosition blockposition1 = blockposition.a(enumdirection.getAdjacentX() * 2, enumdirection.getAdjacentY() * 2, enumdirection.getAdjacentZ() * 2);
                IBlockData iblockdata1 = world.getType(blockposition1);
                Block block = iblockdata1.getBlock();
                boolean flag1 = false;

                if (block == Blocks.MOVING_PISTON) {
                    TileEntity tileentity1 = world.getTileEntity(blockposition1);

                    if (tileentity1 instanceof TileEntityPiston) {
                        TileEntityPiston tileentitypiston = (TileEntityPiston) tileentity1;

                        if (tileentitypiston.d() == enumdirection && tileentitypiston.c()) {
                            tileentitypiston.j();
                            flag1 = true;
                        }
                    }
                }

                if (!flag1) {
                    if (i == 1 && !iblockdata1.isAir() && a(iblockdata1, world, blockposition1, enumdirection.opposite(), false, enumdirection) && (iblockdata1.getPushReaction() == EnumPistonReaction.NORMAL || block == Blocks.PISTON || block == Blocks.STICKY_PISTON)) {
                        this.a(world, blockposition, enumdirection, false);
                    } else {
                        world.setAir(blockposition.shift(enumdirection));
                    }
                }
            } else {
                world.setAir(blockposition.shift(enumdirection));
            }

            world.a((EntityHuman) null, blockposition, SoundEffects.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.15F + 0.6F);
        }

        return true;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public static boolean a(IBlockData iblockdata, World world, BlockPosition blockposition, EnumDirection enumdirection, boolean flag, EnumDirection enumdirection1) {
        Block block = iblockdata.getBlock();

        if (block == Blocks.OBSIDIAN) {
            return false;
        } else if (!world.getWorldBorder().a(blockposition)) {
            return false;
        } else if (blockposition.getY() >= 0 && (enumdirection != EnumDirection.DOWN || blockposition.getY() != 0)) {
            if (blockposition.getY() <= world.getHeight() - 1 && (enumdirection != EnumDirection.UP || blockposition.getY() != world.getHeight() - 1)) {
                if (block != Blocks.PISTON && block != Blocks.STICKY_PISTON) {
                    if (iblockdata.e(world, blockposition) == -1.0F) {
                        return false;
                    }

                    switch (iblockdata.getPushReaction()) {
                    case BLOCK:
                        return false;
                    case DESTROY:
                        return flag;
                    case PUSH_ONLY:
                        return enumdirection == enumdirection1;
                    }
                } else if ((Boolean) iblockdata.get(BlockPiston.EXTENDED)) {
                    return false;
                }

                return !block.isTileEntity();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean a(World world, BlockPosition blockposition, EnumDirection enumdirection, boolean flag) {
        BlockPosition blockposition1 = blockposition.shift(enumdirection);

        if (!flag && world.getType(blockposition1).getBlock() == Blocks.PISTON_HEAD) {
            world.setTypeAndData(blockposition1, Blocks.AIR.getBlockData(), 20);
        }

        PistonExtendsChecker pistonextendschecker = new PistonExtendsChecker(world, blockposition, enumdirection, flag);

        if (!pistonextendschecker.a()) {
            return false;
        } else {
            List<BlockPosition> list = pistonextendschecker.getMovedBlocks();
            List<IBlockData> list1 = Lists.newArrayList();

            for (int i = 0; i < list.size(); ++i) {
                BlockPosition blockposition2 = (BlockPosition) list.get(i);

                list1.add(world.getType(blockposition2));
            }

            List<BlockPosition> list2 = pistonextendschecker.getBrokenBlocks();
            int j = list.size() + list2.size();
            IBlockData[] aiblockdata = new IBlockData[j];
            EnumDirection enumdirection1 = flag ? enumdirection : enumdirection.opposite();
            Set<BlockPosition> set = Sets.newHashSet(list);

            BlockPosition blockposition3;
            int k;
            IBlockData iblockdata;

            for (k = list2.size() - 1; k >= 0; --k) {
                blockposition3 = (BlockPosition) list2.get(k);
                iblockdata = world.getType(blockposition3);
                iblockdata.a(world, blockposition3, 0);
                world.setTypeAndData(blockposition3, Blocks.AIR.getBlockData(), 18);
                --j;
                aiblockdata[j] = iblockdata;
            }

            for (k = list.size() - 1; k >= 0; --k) {
                blockposition3 = (BlockPosition) list.get(k);
                iblockdata = world.getType(blockposition3);
                blockposition3 = blockposition3.shift(enumdirection1);
                set.remove(blockposition3);
                world.setTypeAndData(blockposition3, (IBlockData) Blocks.MOVING_PISTON.getBlockData().set(BlockPiston.FACING, enumdirection), 68);
                world.setTileEntity(blockposition3, BlockPistonMoving.a((IBlockData) list1.get(k), enumdirection, flag, false));
                --j;
                aiblockdata[j] = iblockdata;
            }

            IBlockData iblockdata1;

            if (flag) {
                BlockPropertyPistonType blockpropertypistontype = this.sticky ? BlockPropertyPistonType.STICKY : BlockPropertyPistonType.DEFAULT;

                iblockdata1 = (IBlockData) ((IBlockData) Blocks.PISTON_HEAD.getBlockData().set(BlockPistonExtension.FACING, enumdirection)).set(BlockPistonExtension.TYPE, blockpropertypistontype);
                iblockdata = (IBlockData) ((IBlockData) Blocks.MOVING_PISTON.getBlockData().set(BlockPistonMoving.a, enumdirection)).set(BlockPistonMoving.b, this.sticky ? BlockPropertyPistonType.STICKY : BlockPropertyPistonType.DEFAULT);
                set.remove(blockposition1);
                world.setTypeAndData(blockposition1, iblockdata, 68);
                world.setTileEntity(blockposition1, BlockPistonMoving.a(iblockdata1, enumdirection, true, true));
            }

            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                blockposition3 = (BlockPosition) iterator.next();
                world.setTypeAndData(blockposition3, Blocks.AIR.getBlockData(), 66);
            }

            for (k = list2.size() - 1; k >= 0; --k) {
                iblockdata1 = aiblockdata[j++];
                BlockPosition blockposition4 = (BlockPosition) list2.get(k);

                iblockdata1.b(world, blockposition4, 2);
                world.applyPhysics(blockposition4, iblockdata1.getBlock());
            }

            for (k = list.size() - 1; k >= 0; --k) {
                world.applyPhysics((BlockPosition) list.get(k), aiblockdata[j++].getBlock());
            }

            if (flag) {
                world.applyPhysics(blockposition1, Blocks.PISTON_HEAD);
            }

            return true;
        }
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockPiston.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockPiston.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockPiston.FACING)));
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockPiston.FACING, BlockPiston.EXTENDED);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return iblockdata.get(BlockPiston.FACING) != enumdirection.opposite() && (Boolean) iblockdata.get(BlockPiston.EXTENDED) ? EnumBlockFaceShape.UNDEFINED : EnumBlockFaceShape.SOLID;
    }

    public int j(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return 0;
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
