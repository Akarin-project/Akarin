package net.minecraft.server;

import java.util.Random;

public class BlockIceFrost extends BlockIce {

    public static final BlockStateInteger a = BlockProperties.aa;

    public BlockIceFrost(Block.Info block_info) {
        super(block_info);
        this.o((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockIceFrost.a, 0));
    }

    @Override
    public void tick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (!world.paperConfig.frostedIceEnabled) return; // Paper - add ability to disable frosted ice
        if ((random.nextInt(3) == 0 || this.a(world, blockposition, 4)) && world.getLightLevel(blockposition) > 11 - (Integer) iblockdata.get(BlockIceFrost.a) - iblockdata.b((IBlockAccess) world, blockposition) && this.e(iblockdata, world, blockposition)) {
            BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
            Throwable throwable = null;

            try {
                EnumDirection[] aenumdirection = EnumDirection.values();
                int i = aenumdirection.length;

                for (int j = 0; j < i; ++j) {
                    EnumDirection enumdirection = aenumdirection[j];

                    blockposition_pooledblockposition.g(blockposition).c(enumdirection);
                    IBlockData iblockdata1 = world.getTypeIfLoaded(blockposition_pooledblockposition); // Paper - don't load chunks
                    if (iblockdata1 == null) continue; // Paper

                    if (iblockdata1.getBlock() == this && !this.e(iblockdata1, world, blockposition_pooledblockposition)) {
                        world.getBlockTickList().a(blockposition_pooledblockposition, this, MathHelper.nextInt(random, world.paperConfig.frostedIceDelayMin, world.paperConfig.frostedIceDelayMax)); // Paper - use configurable min/max delay
                    }
                }
            } catch (Throwable throwable1) {
                throwable = throwable1;
                throw throwable1;
            } finally {
                if (blockposition_pooledblockposition != null) {
                    if (throwable != null) {
                        try {
                            blockposition_pooledblockposition.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        blockposition_pooledblockposition.close();
                    }
                }

            }

        } else {
            world.getBlockTickList().a(blockposition, this, MathHelper.nextInt(random, world.paperConfig.frostedIceDelayMin, world.paperConfig.frostedIceDelayMax)); // Paper - use configurable min/max delay
        }
    }

    private boolean e(IBlockData iblockdata, World world, BlockPosition blockposition) {
        int i = (Integer) iblockdata.get(BlockIceFrost.a);

        if (i < 3) {
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockIceFrost.a, i + 1), 2);
            return false;
        } else {
            this.melt(iblockdata, world, blockposition);
            return true;
        }
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (block == this && this.a(world, blockposition, 2)) {
            this.melt(iblockdata, world, blockposition);
        }

        super.doPhysics(iblockdata, world, blockposition, block, blockposition1, flag);
    }

    private boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, int i) {
        int j = 0;
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
        Throwable throwable = null;

        try {
            EnumDirection[] aenumdirection = EnumDirection.values();
            int k = aenumdirection.length;

            for (int l = 0; l < k; ++l) {
                EnumDirection enumdirection = aenumdirection[l];

                blockposition_pooledblockposition.g(blockposition).c(enumdirection);
                if (((World) iblockaccess).getBlockIfLoaded(blockposition_pooledblockposition) == this) { // Paper - don't load chunks
                    ++j;
                    if (j >= i) {
                        boolean flag = false;

                        return flag;
                    }
                }
            }

            return true;
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_pooledblockposition != null) {
                if (throwable != null) {
                    try {
                        blockposition_pooledblockposition.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_pooledblockposition.close();
                }
            }

        }
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockIceFrost.a);
    }
}
