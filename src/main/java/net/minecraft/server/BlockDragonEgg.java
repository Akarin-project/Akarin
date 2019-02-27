package net.minecraft.server;

public class BlockDragonEgg extends BlockFalling {

    protected static final VoxelShape a = Block.a(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public BlockDragonEgg(Block.Info block_info) {
        super(block_info);
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockDragonEgg.a;
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        this.b(iblockdata, world, blockposition);
        return true;
    }

    public void attack(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {
        this.b(iblockdata, world, blockposition);
    }

    private void b(IBlockData iblockdata, World world, BlockPosition blockposition) {
        for (int i = 0; i < 1000; ++i) {
            BlockPosition blockposition1 = blockposition.a(world.random.nextInt(16) - world.random.nextInt(16), world.random.nextInt(8) - world.random.nextInt(8), world.random.nextInt(16) - world.random.nextInt(16));

            if (world.getType(blockposition1).isAir()) {
                if (world.isClientSide) {
                    for (int j = 0; j < 128; ++j) {
                        double d0 = world.random.nextDouble();
                        float f = (world.random.nextFloat() - 0.5F) * 0.2F;
                        float f1 = (world.random.nextFloat() - 0.5F) * 0.2F;
                        float f2 = (world.random.nextFloat() - 0.5F) * 0.2F;
                        double d1 = (double) blockposition1.getX() + (double) (blockposition.getX() - blockposition1.getX()) * d0 + (world.random.nextDouble() - 0.5D) + 0.5D;
                        double d2 = (double) blockposition1.getY() + (double) (blockposition.getY() - blockposition1.getY()) * d0 + world.random.nextDouble() - 0.5D;
                        double d3 = (double) blockposition1.getZ() + (double) (blockposition.getZ() - blockposition1.getZ()) * d0 + (world.random.nextDouble() - 0.5D) + 0.5D;

                        world.addParticle(Particles.K, d1, d2, d3, (double) f, (double) f1, (double) f2);
                    }
                } else {
                    world.setTypeAndData(blockposition1, iblockdata, 2);
                    world.setAir(blockposition);
                }

                return;
            }
        }

    }

    public int a(IWorldReader iworldreader) {
        return 5;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
