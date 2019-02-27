package net.minecraft.server;

import java.util.Iterator;
import javax.annotation.Nullable;

public class BlockWitherSkull extends BlockSkull {

    private static ShapeDetector c;
    private static ShapeDetector o;

    protected BlockWitherSkull(Block.Info block_info) {
        super(BlockSkull.Type.WITHER_SKELETON, block_info);
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {
        super.postPlace(world, blockposition, iblockdata, entityliving, itemstack);
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntitySkull) {
            a(world, blockposition, (TileEntitySkull) tileentity);
        }

    }

    public static void a(World world, BlockPosition blockposition, TileEntitySkull tileentityskull) {
        Block block = tileentityskull.getBlock().getBlock();
        boolean flag = block == Blocks.WITHER_SKELETON_SKULL || block == Blocks.WITHER_SKELETON_WALL_SKULL;

        if (flag && blockposition.getY() >= 2 && world.getDifficulty() != EnumDifficulty.PEACEFUL && !world.isClientSide) {
            ShapeDetector shapedetector = d();
            ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = shapedetector.a(world, blockposition);

            if (shapedetector_shapedetectorcollection != null) {
                int i;

                for (i = 0; i < 3; ++i) {
                    TileEntitySkull.a(world, shapedetector_shapedetectorcollection.a(i, 0, 0).getPosition());
                }

                for (i = 0; i < shapedetector.c(); ++i) {
                    for (int j = 0; j < shapedetector.b(); ++j) {
                        world.setTypeAndData(shapedetector_shapedetectorcollection.a(i, j, 0).getPosition(), Blocks.AIR.getBlockData(), 2);
                    }
                }

                BlockPosition blockposition1 = shapedetector_shapedetectorcollection.a(1, 0, 0).getPosition();
                EntityWither entitywither = new EntityWither(world);
                BlockPosition blockposition2 = shapedetector_shapedetectorcollection.a(1, 2, 0).getPosition();

                entitywither.setPositionRotation((double) blockposition2.getX() + 0.5D, (double) blockposition2.getY() + 0.55D, (double) blockposition2.getZ() + 0.5D, shapedetector_shapedetectorcollection.getFacing().k() == EnumDirection.EnumAxis.X ? 0.0F : 90.0F, 0.0F);
                entitywither.aQ = shapedetector_shapedetectorcollection.getFacing().k() == EnumDirection.EnumAxis.X ? 0.0F : 90.0F;
                entitywither.l();
                Iterator iterator = world.a(EntityPlayer.class, entitywither.getBoundingBox().g(50.0D)).iterator();

                while (iterator.hasNext()) {
                    EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                    CriterionTriggers.n.a(entityplayer, (Entity) entitywither);
                }

                world.addEntity(entitywither);

                int k;

                for (k = 0; k < 120; ++k) {
                    world.addParticle(Particles.E, (double) blockposition1.getX() + world.random.nextDouble(), (double) (blockposition1.getY() - 2) + world.random.nextDouble() * 3.9D, (double) blockposition1.getZ() + world.random.nextDouble(), 0.0D, 0.0D, 0.0D);
                }

                for (k = 0; k < shapedetector.c(); ++k) {
                    for (int l = 0; l < shapedetector.b(); ++l) {
                        world.update(shapedetector_shapedetectorcollection.a(k, l, 0).getPosition(), Blocks.AIR);
                    }
                }

            }
        }
    }

    public static boolean b(World world, BlockPosition blockposition, ItemStack itemstack) {
        return itemstack.getItem() == Items.WITHER_SKELETON_SKULL && blockposition.getY() >= 2 && world.getDifficulty() != EnumDifficulty.PEACEFUL && !world.isClientSide ? e().a(world, blockposition) != null : false;
    }

    protected static ShapeDetector d() {
        if (BlockWitherSkull.c == null) {
            BlockWitherSkull.c = ShapeDetectorBuilder.a().a("^^^", "###", "~#~").a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SOUL_SAND))).a('^', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.WITHER_SKELETON_SKULL).or(BlockStatePredicate.a(Blocks.WITHER_SKELETON_WALL_SKULL)))).a('~', ShapeDetectorBlock.a(MaterialPredicate.a(Material.AIR))).b();
        }

        return BlockWitherSkull.c;
    }

    protected static ShapeDetector e() {
        if (BlockWitherSkull.o == null) {
            BlockWitherSkull.o = ShapeDetectorBuilder.a().a("   ", "###", "~#~").a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SOUL_SAND))).a('~', ShapeDetectorBlock.a(MaterialPredicate.a(Material.AIR))).b();
        }

        return BlockWitherSkull.o;
    }
}
