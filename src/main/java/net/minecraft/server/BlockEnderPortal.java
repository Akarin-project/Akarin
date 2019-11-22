package net.minecraft.server;

// CraftBukkit start
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
// CraftBukkit end

public class BlockEnderPortal extends BlockTileEntity {

    protected static final VoxelShape a = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    protected BlockEnderPortal(Block.Info block_info) {
        super(block_info);
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityEnderPortal();
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockEnderPortal.a;
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!world.isClientSide && !entity.isPassenger() && !entity.isVehicle() && entity.canPortal() && VoxelShapes.c(VoxelShapes.a(entity.getBoundingBox().d((double) (-blockposition.getX()), (double) (-blockposition.getY()), (double) (-blockposition.getZ()))), iblockdata.getShape(world, blockposition), OperatorBoolean.AND)) {
            // CraftBukkit start - Entity in portal
            EntityPortalEnterEvent event = new EntityPortalEnterEvent(entity.getBukkitEntity(), new org.bukkit.Location(world.getWorld(), blockposition.getX(), blockposition.getY(), blockposition.getZ()));
            world.getServer().getPluginManager().callEvent(event);

            if (entity instanceof EntityPlayer) {
                ((EntityPlayer) entity).a(world.worldProvider.getDimensionManager().getType() == DimensionManager.THE_END ? DimensionManager.OVERWORLD : DimensionManager.THE_END, PlayerTeleportEvent.TeleportCause.END_PORTAL);
                return;
            }
            entity.a(world.worldProvider.getDimensionManager().getType() == DimensionManager.THE_END ? DimensionManager.OVERWORLD : DimensionManager.THE_END);
            // CraftBukkit end
        }

    }
}
