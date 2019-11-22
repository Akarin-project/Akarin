package net.minecraft.server;

import java.util.Random;

public abstract class BlockDirtSnowSpreadable extends BlockDirtSnow {

    protected BlockDirtSnowSpreadable(Block.Info block_info) {
        super(block_info);
    }

    private static boolean b(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.up();
        IBlockData iblockdata1 = iworldreader.getType(blockposition1);

        if (iblockdata1.getBlock() == Blocks.SNOW && (Integer) iblockdata1.get(BlockSnow.LAYERS) == 1) {
            return true;
        } else {
            int i = LightEngineLayer.a(iworldreader, iblockdata, blockposition, iblockdata1, blockposition1, EnumDirection.UP, iblockdata1.b((IBlockAccess) iworldreader, blockposition1));

            return i < iworldreader.H();
        }
    }

    private static boolean c(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.up();

        return b(iblockdata, iworldreader, blockposition) && !iworldreader.getFluid(blockposition1).a(TagsFluid.WATER);
    }

    @Override
    public void tick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (this instanceof BlockGrass && world.paperConfig.grassUpdateRate != 1 && (world.paperConfig.grassUpdateRate < 1 || (MinecraftServer.currentTick + blockposition.hashCode()) % world.paperConfig.grassUpdateRate != 0)) { return; } // Paper
        if (!world.isClientSide) {
            if (!b(iblockdata, (IWorldReader) world, blockposition)) {
                // CraftBukkit start
                if (org.bukkit.craftbukkit.event.CraftEventFactory.callBlockFadeEvent(world, blockposition, Blocks.DIRT.getBlockData()).isCancelled()) {
                    return;
                }
                // CraftBukkit end
                world.setTypeUpdate(blockposition, Blocks.DIRT.getBlockData());
            } else {
                if (world.getLightLevel(blockposition.up()) >= 9) {
                    IBlockData iblockdata1 = this.getBlockData();

                    for (int i = 0; i < 4; ++i) {
                        BlockPosition blockposition1 = blockposition.b(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);

                        if (world.getType(blockposition1).getBlock() == Blocks.DIRT && c(iblockdata1, (IWorldReader) world, blockposition1)) {
                            org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockSpreadEvent(world, blockposition, blockposition1, (IBlockData) iblockdata1.set(BlockDirtSnowSpreadable.a, world.getType(blockposition1.up()).getBlock() == Blocks.SNOW)); // CraftBukkit
                        }
                    }
                }

            }
        }
    }
}
