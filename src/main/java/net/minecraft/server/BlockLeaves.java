package net.minecraft.server;

import java.util.Random;

import org.bukkit.event.block.LeavesDecayEvent; // CraftBukkit

public class BlockLeaves extends Block {

    public static final BlockStateInteger DISTANCE = BlockProperties.ah;
    public static final BlockStateBoolean PERSISTENT = BlockProperties.v;
    protected static boolean c;

    public BlockLeaves(Block.Info block_info) {
        super(block_info);
        this.o((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockLeaves.DISTANCE, 7)).set(BlockLeaves.PERSISTENT, false));
    }

    @Override
    public boolean isTicking(IBlockData iblockdata) {
        return (Integer) iblockdata.get(BlockLeaves.DISTANCE) == 7 && !(Boolean) iblockdata.get(BlockLeaves.PERSISTENT);
    }

    @Override
    public void c(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (!(Boolean) iblockdata.get(BlockLeaves.PERSISTENT) && (Integer) iblockdata.get(BlockLeaves.DISTANCE) == 7) {
            // CraftBukkit start
            LeavesDecayEvent event = new LeavesDecayEvent(world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()));
            world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled() || world.getType(blockposition).getBlock() != this) {
                return;
            }
            // CraftBukkit end
            c(iblockdata, world, blockposition);
            world.a(blockposition, false);
        }

    }

    @Override
    public void tick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        world.setTypeAndData(blockposition, a(iblockdata, (GeneratorAccess) world, blockposition), 3);
    }

    @Override
    public int k(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return 1;
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        int i = j(iblockdata1) + 1;

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
                i = Math.min(i, j(generatoraccess.getType(blockposition_pooledblockposition)) + 1);
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

    private static int j(IBlockData iblockdata) {
        return TagsBlock.LOGS.isTagged(iblockdata.getBlock()) ? 0 : (iblockdata.getBlock() instanceof BlockLeaves ? (Integer) iblockdata.get(BlockLeaves.DISTANCE) : 7);
    }

    @Override
    public boolean f(IBlockData iblockdata) {
        return false;
    }

    @Override
    public TextureType c() {
        return BlockLeaves.c ? TextureType.CUTOUT_MIPPED : TextureType.SOLID;
    }

    @Override
    public boolean c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return false;
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EntityTypes<?> entitytypes) {
        return entitytypes == EntityTypes.OCELOT || entitytypes == EntityTypes.PARROT;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockLeaves.DISTANCE, BlockLeaves.PERSISTENT);
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return a((IBlockData) this.getBlockData().set(BlockLeaves.PERSISTENT, true), (GeneratorAccess) blockactioncontext.getWorld(), blockactioncontext.getClickPosition());
    }
}
