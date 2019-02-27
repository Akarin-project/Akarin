package net.minecraft.server;

import java.util.Random;

import org.bukkit.event.entity.EntityPortalEnterEvent; // CraftBukkit

public class BlockEnderPortal extends BlockTileEntity {

    protected static final VoxelShape a = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    protected BlockEnderPortal(Block.Info block_info) {
        super(block_info);
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntityEnderPortal();
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockEnderPortal.a;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public int a(IBlockData iblockdata, Random random) {
        return 0;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!world.isClientSide && !entity.isPassenger() && !entity.isVehicle() && entity.bm() && VoxelShapes.c(VoxelShapes.a(entity.getBoundingBox().d((double) (-blockposition.getX()), (double) (-blockposition.getY()), (double) (-blockposition.getZ()))), iblockdata.getShape(world, blockposition), OperatorBoolean.AND)) {
            // CraftBukkit start - Entity in portal
            EntityPortalEnterEvent event = new EntityPortalEnterEvent(entity.getBukkitEntity(), new org.bukkit.Location(world.getWorld(), blockposition.getX(), blockposition.getY(), blockposition.getZ()));
            world.getServer().getPluginManager().callEvent(event);
            // CraftBukkit end
            entity.a(DimensionManager.THE_END);
        }

    }

    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return ItemStack.a;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
