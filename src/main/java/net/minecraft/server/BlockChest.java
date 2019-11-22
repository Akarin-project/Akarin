package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class BlockChest extends BlockTileEntity implements IBlockWaterlogged {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateEnum<BlockPropertyChestType> b = BlockProperties.ax;
    public static final BlockStateBoolean c = BlockProperties.C;
    protected static final VoxelShape d = Block.a(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D);
    protected static final VoxelShape e = Block.a(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D);
    protected static final VoxelShape f = Block.a(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    protected static final VoxelShape g = Block.a(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D);
    protected static final VoxelShape h = Block.a(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    private static final BlockChest.ChestFinder<IInventory> i = new BlockChest.ChestFinder<IInventory>() {
        @Override
        public IInventory b(TileEntityChest tileentitychest, TileEntityChest tileentitychest1) {
            return new InventoryLargeChest(tileentitychest, tileentitychest1);
        }

        @Override
        public IInventory b(TileEntityChest tileentitychest) {
            return tileentitychest;
        }
    };
    private static final BlockChest.ChestFinder<ITileInventory> j = new BlockChest.ChestFinder<ITileInventory>() {
        @Override
        public ITileInventory b(final TileEntityChest tileentitychest, final TileEntityChest tileentitychest1) {
            final InventoryLargeChest inventorylargechest = new InventoryLargeChest(tileentitychest, tileentitychest1);

            return new DoubleInventory(tileentitychest, tileentitychest1, inventorylargechest); // CraftBukkit
        }

        @Override
        public ITileInventory b(TileEntityChest tileentitychest) {
            return tileentitychest;
        }
    };

    // CraftBukkit start
    public static class DoubleInventory implements ITileInventory {

        private final TileEntityChest tileentitychest;
        private final TileEntityChest tileentitychest1;
        public final InventoryLargeChest inventorylargechest;

        public DoubleInventory(TileEntityChest tileentitychest, TileEntityChest tileentitychest1, InventoryLargeChest inventorylargechest) {
            this.tileentitychest = tileentitychest;
            this.tileentitychest1 = tileentitychest1;
            this.inventorylargechest = inventorylargechest;
        }

        @Nullable
        @Override
        public Container createMenu(int i, PlayerInventory playerinventory, EntityHuman entityhuman) {
            if (tileentitychest.e(entityhuman) && tileentitychest1.e(entityhuman)) {
                tileentitychest.d(playerinventory.player);
                tileentitychest1.d(playerinventory.player);
                return ContainerChest.b(i, playerinventory, inventorylargechest);
            } else {
                return null;
            }
        }

        @Override
        public IChatBaseComponent getScoreboardDisplayName() {
            return (IChatBaseComponent) (tileentitychest.hasCustomName() ? tileentitychest.getScoreboardDisplayName() : (tileentitychest1.hasCustomName() ? tileentitychest1.getScoreboardDisplayName() : new ChatMessage("container.chestDouble", new Object[0])));
        }
    };
    // CraftBukkit end

    protected BlockChest(Block.Info block_info) {
        super(block_info);
        this.o((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockChest.FACING, EnumDirection.NORTH)).set(BlockChest.b, BlockPropertyChestType.SINGLE)).set(BlockChest.c, false));
    }

    @Override
    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(BlockChest.c)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        if (iblockdata1.getBlock() == this && enumdirection.k().c()) {
            BlockPropertyChestType blockpropertychesttype = (BlockPropertyChestType) iblockdata1.get(BlockChest.b);

            if (iblockdata.get(BlockChest.b) == BlockPropertyChestType.SINGLE && blockpropertychesttype != BlockPropertyChestType.SINGLE && iblockdata.get(BlockChest.FACING) == iblockdata1.get(BlockChest.FACING) && j(iblockdata1) == enumdirection.opposite()) {
                return (IBlockData) iblockdata.set(BlockChest.b, blockpropertychesttype.a());
            }
        } else if (j(iblockdata) == enumdirection) {
            return (IBlockData) iblockdata.set(BlockChest.b, BlockPropertyChestType.SINGLE);
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        if (iblockdata.get(BlockChest.b) == BlockPropertyChestType.SINGLE) {
            return BlockChest.h;
        } else {
            switch (j(iblockdata)) {
                case NORTH:
                default:
                    return BlockChest.d;
                case SOUTH:
                    return BlockChest.e;
                case WEST:
                    return BlockChest.f;
                case EAST:
                    return BlockChest.g;
            }
        }
    }

    public static EnumDirection j(IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockChest.FACING);

        return iblockdata.get(BlockChest.b) == BlockPropertyChestType.LEFT ? enumdirection.e() : enumdirection.f();
    }

    @Override
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

        return (IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockChest.FACING, enumdirection)).set(BlockChest.b, blockpropertychesttype)).set(BlockChest.c, fluid.getType() == FluidTypes.WATER);
    }

    @Override
    public Fluid g(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockChest.c) ? FluidTypes.WATER.a(false) : super.g(iblockdata);
    }

    @Nullable
    private EnumDirection a(BlockActionContext blockactioncontext, EnumDirection enumdirection) {
        IBlockData iblockdata = blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition().shift(enumdirection));

        return iblockdata.getBlock() == this && iblockdata.get(BlockChest.b) == BlockPropertyChestType.SINGLE ? (EnumDirection) iblockdata.get(BlockChest.FACING) : null;
    }

    @Override
    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityChest) {
                ((TileEntityChest) tileentity).setCustomName(itemstack.getName());
            }
        }

    }

    @Override
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

    @Override
    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return true;
        } else {
            ITileInventory itileinventory = this.getInventory(iblockdata, world, blockposition);

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
    public static <T> T getInventory(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, boolean flag, BlockChest.ChestFinder<T> blockchest_chestfinder) {
        TileEntity tileentity = generatoraccess.getTileEntity(blockposition);

        if (!(tileentity instanceof TileEntityChest)) {
            return null;
        } else if (!flag && a(generatoraccess, blockposition)) {
            return null;
        } else {
            TileEntityChest tileentitychest = (TileEntityChest) tileentity;
            BlockPropertyChestType blockpropertychesttype = (BlockPropertyChestType) iblockdata.get(BlockChest.b);

            if (blockpropertychesttype == BlockPropertyChestType.SINGLE) {
                return blockchest_chestfinder.b(tileentitychest);
            } else {
                BlockPosition blockposition1 = blockposition.shift(j(iblockdata));
                // Paper start - don't load chunks if the other side of the chest is in unloaded chunk
                IBlockData iblockdata1 = generatoraccess.getTypeIfLoaded(blockposition1);
                if (iblockdata1 == null) {
                    return null;
                }
                // Paper end

                if (iblockdata1.getBlock() == iblockdata.getBlock()) {
                    BlockPropertyChestType blockpropertychesttype1 = (BlockPropertyChestType) iblockdata1.get(BlockChest.b);

                    if (blockpropertychesttype1 != BlockPropertyChestType.SINGLE && blockpropertychesttype != blockpropertychesttype1 && iblockdata1.get(BlockChest.FACING) == iblockdata.get(BlockChest.FACING)) {
                        if (!flag && a(generatoraccess, blockposition1)) {
                            return null;
                        }

                        TileEntity tileentity1 = generatoraccess.getTileEntity(blockposition1);

                        if (tileentity1 instanceof TileEntityChest) {
                            TileEntityChest tileentitychest1 = blockpropertychesttype == BlockPropertyChestType.RIGHT ? tileentitychest : (TileEntityChest) tileentity1;
                            TileEntityChest tileentitychest2 = blockpropertychesttype == BlockPropertyChestType.RIGHT ? (TileEntityChest) tileentity1 : tileentitychest;

                            return blockchest_chestfinder.b(tileentitychest1, tileentitychest2);
                        }
                    }
                }

                return blockchest_chestfinder.b(tileentitychest);
            }
        }
    }

    @Nullable
    public static IInventory getInventory(IBlockData iblockdata, World world, BlockPosition blockposition, boolean flag) {
        return (IInventory) getInventory(iblockdata, world, blockposition, flag, BlockChest.i);
    }

    @Nullable
    @Override
    public ITileInventory getInventory(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return (ITileInventory) getInventory(iblockdata, world, blockposition, false, BlockChest.j);
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityChest();
    }

    private static boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        return a((IBlockAccess) generatoraccess, blockposition) || b(generatoraccess, blockposition);
    }

    private static boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.up();

        return iblockaccess.getType(blockposition1).isOccluding(iblockaccess, blockposition1);
    }

    private static boolean b(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        // Paper start - Option to disable chest cat detection
        if (((World) generatoraccess).paperConfig.disableChestCatDetection) {
            return false;
        }
        // Paper end
        List<EntityCat> list = generatoraccess.a(EntityCat.class, new AxisAlignedBB((double) blockposition.getX(), (double) (blockposition.getY() + 1), (double) blockposition.getZ(), (double) (blockposition.getX() + 1), (double) (blockposition.getY() + 2), (double) (blockposition.getZ() + 1)));

        if (!list.isEmpty()) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityCat entitycat = (EntityCat) iterator.next();

                if (entitycat.isSitting()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return Container.b(getInventory(iblockdata, world, blockposition, false));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockChest.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockChest.FACING)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockChest.FACING)));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockChest.FACING, BlockChest.b, BlockChest.c);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    interface ChestFinder<T> {

        T b(TileEntityChest tileentitychest, TileEntityChest tileentitychest1);

        T b(TileEntityChest tileentitychest);
    }
}
