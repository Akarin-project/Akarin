package net.minecraft.server;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

public class BlockLever extends BlockAttachable {

    public static final BlockStateBoolean POWERED = BlockProperties.t;
    protected static final VoxelShape b = Block.a(5.0D, 4.0D, 10.0D, 11.0D, 12.0D, 16.0D);
    protected static final VoxelShape c = Block.a(5.0D, 4.0D, 0.0D, 11.0D, 12.0D, 6.0D);
    protected static final VoxelShape o = Block.a(10.0D, 4.0D, 5.0D, 16.0D, 12.0D, 11.0D);
    protected static final VoxelShape p = Block.a(0.0D, 4.0D, 5.0D, 6.0D, 12.0D, 11.0D);
    protected static final VoxelShape q = Block.a(5.0D, 0.0D, 4.0D, 11.0D, 6.0D, 12.0D);
    protected static final VoxelShape r = Block.a(4.0D, 0.0D, 5.0D, 12.0D, 6.0D, 11.0D);
    protected static final VoxelShape s = Block.a(5.0D, 10.0D, 4.0D, 11.0D, 16.0D, 12.0D);
    protected static final VoxelShape t = Block.a(4.0D, 10.0D, 5.0D, 12.0D, 16.0D, 11.0D);

    protected BlockLever(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockLever.FACING, EnumDirection.NORTH)).set(BlockLever.POWERED, false)).set(BlockLever.FACE, BlockPropertyAttachPosition.WALL));
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        switch ((BlockPropertyAttachPosition) iblockdata.get(BlockLever.FACE)) {
        case FLOOR:
            switch (((EnumDirection) iblockdata.get(BlockLever.FACING)).k()) {
            case X:
                return BlockLever.r;
            case Z:
            default:
                return BlockLever.q;
            }
        case WALL:
            switch ((EnumDirection) iblockdata.get(BlockLever.FACING)) {
            case EAST:
                return BlockLever.p;
            case WEST:
                return BlockLever.o;
            case SOUTH:
                return BlockLever.c;
            case NORTH:
            default:
                return BlockLever.b;
            }
        case CEILING:
        default:
            switch (((EnumDirection) iblockdata.get(BlockLever.FACING)).k()) {
            case X:
                return BlockLever.t;
            case Z:
            default:
                return BlockLever.s;
            }
        }
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        iblockdata = (IBlockData) iblockdata.a((IBlockState) BlockLever.POWERED);
        boolean flag = (Boolean) iblockdata.get(BlockLever.POWERED);

        if (world.isClientSide) {
            if (flag) {
                a(iblockdata, world, blockposition, 1.0F);
            }

            return true;
        } else {
            // CraftBukkit start - Interact Lever
            boolean powered = !flag; // Old powered state
            org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
            int old = (powered) ? 15 : 0;
            int current = (!powered) ? 15 : 0;

            BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, old, current);
            world.getServer().getPluginManager().callEvent(eventRedstone);

            if ((eventRedstone.getNewCurrent() > 0) != (!powered)) {
                return true;
            }
            // CraftBukkit end

            world.setTypeAndData(blockposition, iblockdata, 3);
            float f3 = flag ? 0.6F : 0.5F;

            world.a((EntityHuman) null, blockposition, SoundEffects.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, f3);
            this.b(iblockdata, world, blockposition);
            return true;
        }
    }

    private static void a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, float f) {
        EnumDirection enumdirection = ((EnumDirection) iblockdata.get(BlockLever.FACING)).opposite();
        EnumDirection enumdirection1 = k(iblockdata).opposite();
        double d0 = (double) blockposition.getX() + 0.5D + 0.1D * (double) enumdirection.getAdjacentX() + 0.2D * (double) enumdirection1.getAdjacentX();
        double d1 = (double) blockposition.getY() + 0.5D + 0.1D * (double) enumdirection.getAdjacentY() + 0.2D * (double) enumdirection1.getAdjacentY();
        double d2 = (double) blockposition.getZ() + 0.5D + 0.1D * (double) enumdirection.getAdjacentZ() + 0.2D * (double) enumdirection1.getAdjacentZ();

        generatoraccess.addParticle(new ParticleParamRedstone(1.0F, 0.0F, 0.0F, f), d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }

    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag && iblockdata.getBlock() != iblockdata1.getBlock()) {
            if ((Boolean) iblockdata.get(BlockLever.POWERED)) {
                this.b(iblockdata, world, blockposition);
            }

            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.get(BlockLever.POWERED) ? 15 : 0;
    }

    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.get(BlockLever.POWERED) && k(iblockdata) == enumdirection ? 15 : 0;
    }

    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    private void b(IBlockData iblockdata, World world, BlockPosition blockposition) {
        world.applyPhysics(blockposition, this);
        world.applyPhysics(blockposition.shift(k(iblockdata).opposite()), this);
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockLever.FACE, BlockLever.FACING, BlockLever.POWERED);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
