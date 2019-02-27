package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class BlockChest extends BlockTileEntity implements IFluidSource, IFluidContainer {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateEnum<BlockPropertyChestType> b = BlockProperties.ap;
    public static final BlockStateBoolean c = BlockProperties.y;
    protected static final VoxelShape o = Block.a(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D);
    protected static final VoxelShape p = Block.a(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D);
    protected static final VoxelShape q = Block.a(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    protected static final VoxelShape r = Block.a(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D);
    protected static final VoxelShape s = Block.a(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

    protected BlockChest(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockChest.FACING, EnumDirection.NORTH)).set(BlockChest.b, BlockPropertyChestType.SINGLE)).set(BlockChest.c, false));
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.ENTITYBLOCK_ANIMATED;
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(BlockChest.c)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        if (iblockdata1.getBlock() == this && enumdirection.k().c()) {
            BlockPropertyChestType blockpropertychesttype = (BlockPropertyChestType) iblockdata1.get(BlockChest.b);

            if (iblockdata.get(BlockChest.b) == BlockPropertyChestType.SINGLE && blockpropertychesttype != BlockPropertyChestType.SINGLE && iblockdata.get(BlockChest.FACING) == iblockdata1.get(BlockChest.FACING) && k(iblockdata1) == enumdirection.opposite()) {
                return (IBlockData) iblockdata.set(BlockChest.b, blockpropertychesttype.a());
            }
        } else if (k(iblockdata) == enumdirection) {
            return (IBlockData) iblockdata.set(BlockChest.b, BlockPropertyChestType.SINGLE);
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        if (iblockdata.get(BlockChest.b) == BlockPropertyChestType.SINGLE) {
            return BlockChest.s;
        } else {
            switch (k(iblockdata)) {
            case NORTH:
            default:
                return BlockChest.o;
            case SOUTH:
                return BlockChest.p;
            case WEST:
                return BlockChest.q;
            case EAST:
                return BlockChest.r;
            }
        }
    }

    public static EnumDirection k(IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockChest.FACING);

        return iblockdata.get(BlockChest.b) == BlockPropertyChestType.LEFT ? enumdirection.e() : enumdirection.f();
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        BlockPropertyChestType blockpropertychesttype = BlockPropertyChestType.SINGLE;
        EnumDirection enumdirection = blockactioncontext.f().opposite();
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());
        boolean flag = blockactioncontext.isSneaking();
        EnumDirection enumdirection1 = blockactioncontext.getClickedFace();

        if (enumdirection1.k().c() && flag) {
            EnumDirection enumdirection2 = this.a(blockactioncontext, enumdirection1.opposite());

            if (enumdirection2 != null && enumdirection2.k() != enumdirection1.k()) {
                enumdirection = enumdirection2;
                blockpropertychesttype = enumdirection2.f() == enumdirection1.opposite() ? BlockPropertyChestType.RIGHT : BlockPropertyChestType.LEFT;
            }
        }

        if (blockpropertychesttype == BlockPropertyChestType.SINGLE && !flag) {
            if (enumdirection == this.a(blockactioncontext, enumdirection.e())) {
                blockpropertychesttype = BlockPropertyChestType.LEFT;
            } else if (enumdirection == this.a(blockactioncontext, enumdirection.f())) {
                blockpropertychesttype = BlockPropertyChestType.RIGHT;
            }
        }

        return (IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockChest.FACING, enumdirection)).set(BlockChest.b, blockpropertychesttype)).set(BlockChest.c, fluid.c() == FluidTypes.WATER);
    }

    public FluidType removeFluid(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if ((Boolean) iblockdata.get(BlockChest.c)) {
            generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockChest.c, false), 3);
            return FluidTypes.WATER;
        } else {
            return FluidTypes.EMPTY;
        }
    }

    public Fluid h(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockChest.c) ? FluidTypes.WATER.a(false) : super.h(iblockdata);
    }

    public boolean canPlace(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        return !(Boolean) iblockdata.get(BlockChest.c) && fluidtype == FluidTypes.WATER;
    }

    public boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        if (!(Boolean) iblockdata.get(BlockChest.c) && fluid.c() == FluidTypes.WATER) {
            if (!generatoraccess.e()) {
                generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockChest.c, true), 3);
                generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
            }

            return true;
        } else {
            return false;
        }
    }

    @Nullable
    private EnumDirection a(BlockActionContext blockactioncontext, EnumDirection enumdirection) {
        IBlockData iblockdata = blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition().shift(enumdirection));

        return iblockdata.getBlock() == this && iblockdata.get(BlockChest.b) == BlockPropertyChestType.SINGLE ? (EnumDirection) iblockdata.get(BlockChest.FACING) : null;
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityChest) {
                ((TileEntityChest) tileentity).setCustomName(itemstack.getName());
            }
        }

    }

    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata.getBlock() != iblockdata1.getBlock()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof IInventory) {
                InventoryUtils.dropInventory(world, blockposition, (IInventory) tileentity);
                world.updateAdjacentComparators(blockposition, this);
            }

            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (world.isClientSide) {
            return true;
        } else {
            ITileInventory itileinventory = this.getInventory(iblockdata, world, blockposition, false);

            if (itileinventory != null) {
                entityhuman.openContainer(itileinventory);
                entityhuman.b(this.d());
            }

            return true;
        }
    }

    protected Statistic<MinecraftKey> d() {
        return StatisticList.CUSTOM.b(StatisticList.OPEN_CHEST);
    }

    @Nullable
    public ITileInventory getInventory(IBlockData iblockdata, World world, BlockPosition blockposition, boolean flag) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (!(tileentity instanceof TileEntityChest)) {
            return null;
        } else if (!flag && this.a(world, blockposition)) {
            return null;
        } else {
            Object object = (TileEntityChest) tileentity;
            BlockPropertyChestType blockpropertychesttype = (BlockPropertyChestType) iblockdata.get(BlockChest.b);

            if (blockpropertychesttype == BlockPropertyChestType.SINGLE) {
                return (ITileInventory) object;
            } else {
                BlockPosition blockposition1 = blockposition.shift(k(iblockdata));
                IBlockData iblockdata1 = world.getType(blockposition1);

                if (iblockdata1.getBlock() == this) {
                    BlockPropertyChestType blockpropertychesttype1 = (BlockPropertyChestType) iblockdata1.get(BlockChest.b);

                    if (blockpropertychesttype1 != BlockPropertyChestType.SINGLE && blockpropertychesttype != blockpropertychesttype1 && iblockdata1.get(BlockChest.FACING) == iblockdata.get(BlockChest.FACING)) {
                        if (!flag && this.a(world, blockposition1)) {
                            return null;
                        }

                        TileEntity tileentity1 = world.getTileEntity(blockposition1);

                        if (tileentity1 instanceof TileEntityChest) {
                            Object object1 = blockpropertychesttype == BlockPropertyChestType.RIGHT ? object : (ITileInventory) tileentity1;
                            Object object2 = blockpropertychesttype == BlockPropertyChestType.RIGHT ? (ITileInventory) tileentity1 : object;

                            object = new InventoryLargeChest(new ChatMessage("container.chestDouble", new Object[0]), (ITileInventory) object1, (ITileInventory) object2);
                        }
                    }
                }

                return (ITileInventory) object;
            }
        }
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntityChest();
    }

    private boolean a(World world, BlockPosition blockposition) {
        return this.a((IBlockAccess) world, blockposition) || this.b(world, blockposition);
    }

    private boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockaccess.getType(blockposition.up()).isOccluding();
    }

    private boolean b(World world, BlockPosition blockposition) {
        List<EntityOcelot> list = world.a(EntityOcelot.class, new AxisAlignedBB((double) blockposition.getX(), (double) (blockposition.getY() + 1), (double) blockposition.getZ(), (double) (blockposition.getX() + 1), (double) (blockposition.getY() + 2), (double) (blockposition.getZ() + 1)));

        if (!list.isEmpty()) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityOcelot entityocelot = (EntityOcelot) iterator.next();

                if (entityocelot.isSitting()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return Container.b((IInventory) this.getInventory(iblockdata, world, blockposition, false));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockChest.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockChest.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockChest.FACING)));
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockChest.FACING, BlockChest.b, BlockChest.c);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
