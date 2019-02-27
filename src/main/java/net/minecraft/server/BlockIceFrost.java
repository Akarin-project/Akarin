package net.minecraft.server;

import java.util.Random;

public class BlockIceFrost extends BlockIce {

    public static final BlockStateInteger a = BlockProperties.U;

    public BlockIceFrost(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockIceFrost.a, 0));
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if ((random.nextInt(3) == 0 || this.a(world, blockposition, 4)) && world.getLightLevel(blockposition) > 11 - (Integer) iblockdata.get(BlockIceFrost.a) - iblockdata.b(world, blockposition) && this.c(iblockdata, world, blockposition)) {
            BlockPosition.b blockposition_b = BlockPosition.b.r();
            Throwable throwable = null;

            try {
                EnumDirection[] aenumdirection = EnumDirection.values();
                int i = aenumdirection.length;

                for (int j = 0; j < i; ++j) {
                    EnumDirection enumdirection = aenumdirection[j];

                    blockposition_b.g(blockposition).c(enumdirection);
                    IBlockData iblockdata1 = world.getType(blockposition_b);

                    if (iblockdata1.getBlock() == this && !this.c(iblockdata1, world, blockposition_b)) {
                        world.getBlockTickList().a(blockposition_b, this, MathHelper.nextInt(random, 20, 40));
                    }
                }
            } catch (Throwable throwable1) {
                throwable = throwable1;
                throw throwable1;
            } finally {
                if (blockposition_b != null) {
                    if (throwable != null) {
                        try {
                            blockposition_b.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        blockposition_b.close();
                    }
                }

            }

        } else {
            world.getBlockTickList().a(blockposition, this, MathHelper.nextInt(random, 20, 40));
        }
    }

    private boolean c(IBlockData iblockdata, World world, BlockPosition blockposition) {
        int i = (Integer) iblockdata.get(BlockIceFrost.a);

        if (i < 3) {
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockIceFrost.a, i + 1), 2);
            return false;
        } else {
            this.b(iblockdata, world, blockposition);
            return true;
        }
    }

    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (block == this && this.a(world, blockposition, 2)) {
            this.b(iblockdata, world, blockposition);
        }

        super.doPhysics(iblockdata, world, blockposition, block, blockposition1);
    }

    private boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, int i) {
        int j = 0;
        BlockPosition.b blockposition_b = BlockPosition.b.r();
        Throwable throwable = null;

        try {
            EnumDirection[] aenumdirection = EnumDirection.values();
            int k = aenumdirection.length;

            for (int l = 0; l < k; ++l) {
                EnumDirection enumdirection = aenumdirection[l];

                blockposition_b.g(blockposition).c(enumdirection);
                if (iblockaccess.getType(blockposition_b).getBlock() == this) {
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
            if (blockposition_b != null) {
                if (throwable != null) {
                    try {
                        blockposition_b.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_b.close();
                }
            }

        }
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockIceFrost.a);
    }

    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return ItemStack.a;
    }
}
