package net.minecraft.server;

import javax.annotation.Nullable;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

public class BlockDoor extends Block {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateBoolean OPEN = BlockProperties.r;
    public static final BlockStateEnum<BlockPropertyDoorHinge> HINGE = BlockProperties.ar;
    public static final BlockStateBoolean POWERED = BlockProperties.t;
    public static final BlockStateEnum<BlockPropertyDoubleBlockHalf> HALF = BlockProperties.P;
    protected static final VoxelShape q = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
    protected static final VoxelShape r = Block.a(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape s = Block.a(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape t = Block.a(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);

    protected BlockDoor(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockDoor.FACING, EnumDirection.NORTH)).set(BlockDoor.OPEN, false)).set(BlockDoor.HINGE, BlockPropertyDoorHinge.LEFT)).set(BlockDoor.POWERED, false)).set(BlockDoor.HALF, BlockPropertyDoubleBlockHalf.LOWER));
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockDoor.FACING);
        boolean flag = !(Boolean) iblockdata.get(BlockDoor.OPEN);
        boolean flag1 = iblockdata.get(BlockDoor.HINGE) == BlockPropertyDoorHinge.RIGHT;

        switch (enumdirection) {
        case EAST:
        default:
            return flag ? BlockDoor.t : (flag1 ? BlockDoor.r : BlockDoor.q);
        case SOUTH:
            return flag ? BlockDoor.q : (flag1 ? BlockDoor.t : BlockDoor.s);
        case WEST:
            return flag ? BlockDoor.s : (flag1 ? BlockDoor.q : BlockDoor.r);
        case NORTH:
            return flag ? BlockDoor.r : (flag1 ? BlockDoor.s : BlockDoor.t);
        }
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        BlockPropertyDoubleBlockHalf blockpropertydoubleblockhalf = (BlockPropertyDoubleBlockHalf) iblockdata.get(BlockDoor.HALF);

        return enumdirection.k() == EnumDirection.EnumAxis.Y && blockpropertydoubleblockhalf == BlockPropertyDoubleBlockHalf.LOWER == (enumdirection == EnumDirection.UP) ? (iblockdata1.getBlock() == this && iblockdata1.get(BlockDoor.HALF) != blockpropertydoubleblockhalf ? (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockDoor.FACING, iblockdata1.get(BlockDoor.FACING))).set(BlockDoor.OPEN, iblockdata1.get(BlockDoor.OPEN))).set(BlockDoor.HINGE, iblockdata1.get(BlockDoor.HINGE))).set(BlockDoor.POWERED, iblockdata1.get(BlockDoor.POWERED)) : Blocks.AIR.getBlockData()) : (blockpropertydoubleblockhalf == BlockPropertyDoubleBlockHalf.LOWER && enumdirection == EnumDirection.DOWN && !iblockdata.canPlace(generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1));
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        super.a(world, entityhuman, blockposition, Blocks.AIR.getBlockData(), tileentity, itemstack);
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        BlockPropertyDoubleBlockHalf blockpropertydoubleblockhalf = (BlockPropertyDoubleBlockHalf) iblockdata.get(BlockDoor.HALF);
        boolean flag = blockpropertydoubleblockhalf == BlockPropertyDoubleBlockHalf.LOWER;
        BlockPosition blockposition1 = flag ? blockposition.up() : blockposition.down();
        IBlockData iblockdata1 = world.getType(blockposition1);

        if (iblockdata1.getBlock() == this && iblockdata1.get(BlockDoor.HALF) != blockpropertydoubleblockhalf) {
            world.setTypeAndData(blockposition1, Blocks.AIR.getBlockData(), 35);
            world.a(entityhuman, 2001, blockposition1, Block.getCombinedId(iblockdata1));
            if (!world.isClientSide && !entityhuman.u()) {
                if (flag) {
                    iblockdata.a(world, blockposition, 0);
                } else {
                    iblockdata1.a(world, blockposition1, 0);
                }
            }
        }

        super.a(world, blockposition, iblockdata, entityhuman);
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        switch (pathmode) {
        case LAND:
            return (Boolean) iblockdata.get(BlockDoor.OPEN);
        case WATER:
            return false;
        case AIR:
            return (Boolean) iblockdata.get(BlockDoor.OPEN);
        default:
            return false;
        }
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    private int d() {
        return this.material == Material.ORE ? 1011 : 1012;
    }

    private int e() {
        return this.material == Material.ORE ? 1005 : 1006;
    }

    @Nullable
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        BlockPosition blockposition = blockactioncontext.getClickPosition();

        if (blockposition.getY() < 255 && blockactioncontext.getWorld().getType(blockposition.up()).a(blockactioncontext)) {
            World world = blockactioncontext.getWorld();
            boolean flag = world.isBlockIndirectlyPowered(blockposition) || world.isBlockIndirectlyPowered(blockposition.up());

            return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockDoor.FACING, blockactioncontext.f())).set(BlockDoor.HINGE, this.b(blockactioncontext))).set(BlockDoor.POWERED, flag)).set(BlockDoor.OPEN, flag)).set(BlockDoor.HALF, BlockPropertyDoubleBlockHalf.LOWER);
        } else {
            return null;
        }
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        world.setTypeAndData(blockposition.up(), (IBlockData) iblockdata.set(BlockDoor.HALF, BlockPropertyDoubleBlockHalf.UPPER), 3);
    }

    private BlockPropertyDoorHinge b(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        EnumDirection enumdirection = blockactioncontext.f();
        BlockPosition blockposition1 = blockposition.up();
        EnumDirection enumdirection1 = enumdirection.f();
        IBlockData iblockdata = world.getType(blockposition.shift(enumdirection1));
        IBlockData iblockdata1 = world.getType(blockposition1.shift(enumdirection1));
        EnumDirection enumdirection2 = enumdirection.e();
        IBlockData iblockdata2 = world.getType(blockposition.shift(enumdirection2));
        IBlockData iblockdata3 = world.getType(blockposition1.shift(enumdirection2));
        int i = (iblockdata.k() ? -1 : 0) + (iblockdata1.k() ? -1 : 0) + (iblockdata2.k() ? 1 : 0) + (iblockdata3.k() ? 1 : 0);
        boolean flag = iblockdata.getBlock() == this && iblockdata.get(BlockDoor.HALF) == BlockPropertyDoubleBlockHalf.LOWER;
        boolean flag1 = iblockdata2.getBlock() == this && iblockdata2.get(BlockDoor.HALF) == BlockPropertyDoubleBlockHalf.LOWER;

        if ((!flag || flag1) && i <= 0) {
            if ((!flag1 || flag) && i >= 0) {
                int j = enumdirection.getAdjacentX();
                int k = enumdirection.getAdjacentZ();
                float f = blockactioncontext.m();
                float f1 = blockactioncontext.o();

                return (j >= 0 || f1 >= 0.5F) && (j <= 0 || f1 <= 0.5F) && (k >= 0 || f <= 0.5F) && (k <= 0 || f >= 0.5F) ? BlockPropertyDoorHinge.LEFT : BlockPropertyDoorHinge.RIGHT;
            } else {
                return BlockPropertyDoorHinge.LEFT;
            }
        } else {
            return BlockPropertyDoorHinge.RIGHT;
        }
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (this.material == Material.ORE) {
            return false;
        } else {
            iblockdata = (IBlockData) iblockdata.a((IBlockState) BlockDoor.OPEN);
            world.setTypeAndData(blockposition, iblockdata, 10);
            world.a(entityhuman, (Boolean) iblockdata.get(BlockDoor.OPEN) ? this.e() : this.d(), blockposition, 0);
            return true;
        }
    }

    public void setDoor(World world, BlockPosition blockposition, boolean flag) {
        IBlockData iblockdata = world.getType(blockposition);

        if (iblockdata.getBlock() == this && (Boolean) iblockdata.get(BlockDoor.OPEN) != flag) {
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockDoor.OPEN, flag), 10);
            this.b(world, blockposition, flag);
        }
    }

    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        // CraftBukkit start
        BlockPosition otherHalf = blockposition.shift(iblockdata.get(BlockDoor.HALF) == BlockPropertyDoubleBlockHalf.LOWER ? EnumDirection.UP : EnumDirection.DOWN);

        org.bukkit.World bworld = world.getWorld();
        org.bukkit.block.Block bukkitBlock = bworld.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
        org.bukkit.block.Block blockTop = bworld.getBlockAt(otherHalf.getX(), otherHalf.getY(), otherHalf.getZ());

        int power = bukkitBlock.getBlockPower();
        int powerTop = blockTop.getBlockPower();
        if (powerTop > power) power = powerTop;
        int oldPower = (Boolean) iblockdata.get(BlockDoor.POWERED) ? 15 : 0;

        if (oldPower == 0 ^ power == 0) {
            BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bukkitBlock, oldPower, power);
            world.getServer().getPluginManager().callEvent(eventRedstone);

            boolean flag = eventRedstone.getNewCurrent() > 0;
            // CraftBukkit end
            if (flag != (Boolean) iblockdata.get(BlockDoor.OPEN)) {
                this.b(world, blockposition, flag);
            }

            world.setTypeAndData(blockposition, (IBlockData) ((IBlockData) iblockdata.set(BlockDoor.POWERED, flag)).set(BlockDoor.OPEN, flag), 2);
        }

    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        IBlockData iblockdata1 = iworldreader.getType(blockposition.down());

        return iblockdata.get(BlockDoor.HALF) == BlockPropertyDoubleBlockHalf.LOWER ? iblockdata1.q() : iblockdata1.getBlock() == this;
    }

    private void b(World world, BlockPosition blockposition, boolean flag) {
        world.a((EntityHuman) null, flag ? this.e() : this.d(), blockposition, 0);
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return (IMaterial) (iblockdata.get(BlockDoor.HALF) == BlockPropertyDoubleBlockHalf.UPPER ? Items.AIR : super.getDropType(iblockdata, world, blockposition, i));
    }

    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }

    public TextureType c() {
        return TextureType.CUTOUT;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockDoor.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockDoor.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return enumblockmirror == EnumBlockMirror.NONE ? iblockdata : (IBlockData) iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockDoor.FACING))).a((IBlockState) BlockDoor.HINGE);
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockDoor.HALF, BlockDoor.FACING, BlockDoor.OPEN, BlockDoor.HINGE, BlockDoor.POWERED);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
