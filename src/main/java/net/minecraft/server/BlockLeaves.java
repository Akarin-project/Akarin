package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

import org.bukkit.event.block.LeavesDecayEvent; // CraftBukkit

public class BlockLeaves extends Block {

    public static final BlockStateInteger DISTANCE = BlockProperties.ab;
    public static final BlockStateBoolean PERSISTENT = BlockProperties.s;
    protected static boolean c;

    public BlockLeaves(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockLeaves.DISTANCE, 7)).set(BlockLeaves.PERSISTENT, false));
    }

    public boolean isTicking(IBlockData iblockdata) {
        return (Integer) iblockdata.get(BlockLeaves.DISTANCE) == 7 && !(Boolean) iblockdata.get(BlockLeaves.PERSISTENT);
    }

    public void b(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (!(Boolean) iblockdata.get(BlockLeaves.PERSISTENT) && (Integer) iblockdata.get(BlockLeaves.DISTANCE) == 7) {
            // CraftBukkit start
            LeavesDecayEvent event = new LeavesDecayEvent(world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()));
            world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled() || world.getType(blockposition).getBlock() != this) {
                return;
            }
            // CraftBukkit end
            iblockdata.a(world, blockposition, 0);
            world.setAir(blockposition);
        }

    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        world.setTypeAndData(blockposition, a(iblockdata, (GeneratorAccess) world, blockposition), 3);
    }

    public int j(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return 1;
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        int i = w(iblockdata1) + 1;

        if (i != 1 || (Integer) iblockdata.get(BlockLeaves.DISTANCE) != i) {
            generatoraccess.getBlockTickList().a(blockposition, this, 1);
        }

        return iblockdata;
    }

    private static IBlockData a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        int i = 7;
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
        Throwable throwable = null;

        try {
            EnumDirection[] aenumdirection = EnumDirection.values();
            int j = aenumdirection.length;

            for (int k = 0; k < j; ++k) {
                EnumDirection enumdirection = aenumdirection[k];

                blockposition_pooledblockposition.g(blockposition).c(enumdirection);
                i = Math.min(i, w(generatoraccess.getType(blockposition_pooledblockposition)) + 1);
                if (i == 1) {
                    break;
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

        return (IBlockData) iblockdata.set(BlockLeaves.DISTANCE, i);
    }

    private static int w(IBlockData iblockdata) {
        return TagsBlock.LOGS.isTagged(iblockdata.getBlock()) ? 0 : (iblockdata.getBlock() instanceof BlockLeaves ? (Integer) iblockdata.get(BlockLeaves.DISTANCE) : 7);
    }

    public int a(IBlockData iblockdata, Random random) {
        return random.nextInt(20) == 0 ? 1 : 0;
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        Block block = iblockdata.getBlock();

        return block == Blocks.OAK_LEAVES ? Blocks.OAK_SAPLING : (block == Blocks.SPRUCE_LEAVES ? Blocks.SPRUCE_SAPLING : (block == Blocks.BIRCH_LEAVES ? Blocks.BIRCH_SAPLING : (block == Blocks.JUNGLE_LEAVES ? Blocks.JUNGLE_SAPLING : (block == Blocks.ACACIA_LEAVES ? Blocks.ACACIA_SAPLING : (block == Blocks.DARK_OAK_LEAVES ? Blocks.DARK_OAK_SAPLING : Blocks.OAK_SAPLING)))));
    }

    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, float f, int i) {
        if (!world.isClientSide) {
            int j = this.k(iblockdata);

            if (i > 0) {
                j -= 2 << i;
                if (j < 10) {
                    j = 10;
                }
            }

            if (world.random.nextInt(j) == 0) {
                a(world, blockposition, new ItemStack(this.getDropType(iblockdata, world, blockposition, i)));
            }

            j = 200;
            if (i > 0) {
                j -= 10 << i;
                if (j < 40) {
                    j = 40;
                }
            }

            this.a(world, blockposition, iblockdata, j);
        }

    }

    protected void a(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {
        if ((iblockdata.getBlock() == Blocks.OAK_LEAVES || iblockdata.getBlock() == Blocks.DARK_OAK_LEAVES) && world.random.nextInt(i) == 0) {
            a(world, blockposition, new ItemStack(Items.APPLE));
        }

    }

    protected int k(IBlockData iblockdata) {
        return iblockdata.getBlock() == Blocks.JUNGLE_LEAVES ? 40 : 20;
    }

    public TextureType c() {
        return BlockLeaves.c ? TextureType.CUTOUT_MIPPED : TextureType.SOLID;
    }

    public boolean q(IBlockData iblockdata) {
        return false;
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        if (!world.isClientSide && itemstack.getItem() == Items.SHEARS) {
            entityhuman.b(StatisticList.BLOCK_MINED.b(this));
            entityhuman.applyExhaustion(0.005F);
            a(world, blockposition, new ItemStack(this));
        } else {
            super.a(world, entityhuman, blockposition, iblockdata, tileentity, itemstack);
        }
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockLeaves.DISTANCE, BlockLeaves.PERSISTENT);
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return a((IBlockData) this.getBlockData().set(BlockLeaves.PERSISTENT, true), (GeneratorAccess) blockactioncontext.getWorld(), blockactioncontext.getClickPosition());
    }
}
