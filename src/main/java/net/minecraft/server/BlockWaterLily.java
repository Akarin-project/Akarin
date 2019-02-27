package net.minecraft.server;

public class BlockWaterLily extends BlockPlant {

    protected static final VoxelShape a = Block.a(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

    protected BlockWaterLily(Block.Info block_info) {
        super(block_info);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        super.a(iblockdata, world, blockposition, entity);
        if (entity instanceof EntityBoat && !org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(entity, blockposition, Blocks.AIR.getBlockData()).isCancelled()) { // CraftBukkit
            world.setAir(new BlockPosition(blockposition), true);
        }

    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockWaterLily.a;
    }

    protected boolean b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        Fluid fluid = iblockaccess.getFluid(blockposition);

        return fluid.c() == FluidTypes.WATER || iblockdata.getMaterial() == Material.ICE;
    }
}
