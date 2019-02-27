package net.minecraft.server;

import java.util.List;
import java.util.Random;

public class BlockGrass extends BlockDirtSnowSpreadable implements IBlockFragilePlantElement {

    public BlockGrass(Block.Info block_info) {
        super(block_info);
    }

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return iblockaccess.getType(blockposition.up()).isAir();
    }

    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        BlockPosition blockposition1 = blockposition.up();
        IBlockData iblockdata1 = Blocks.GRASS.getBlockData();
        int i = 0;

        while (i < 128) {
            BlockPosition blockposition2 = blockposition1;
            int j = 0;

            while (true) {
                if (j < i / 16) {
                    blockposition2 = blockposition2.a(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                    if (world.getType(blockposition2.down()).getBlock() == this && !world.getType(blockposition2).k()) {
                        ++j;
                        continue;
                    }
                } else {
                    IBlockData iblockdata2 = world.getType(blockposition2);

                    if (iblockdata2.getBlock() == iblockdata1.getBlock() && random.nextInt(10) == 0) {
                        ((IBlockFragilePlantElement) iblockdata1.getBlock()).b(world, random, blockposition2, iblockdata2);
                    }

                    if (iblockdata2.isAir()) {
                        label38:
                        {
                            IBlockData iblockdata3;

                            if (random.nextInt(8) == 0) {
                                List<WorldGenFeatureCompositeFlower<?>> list = world.getBiome(blockposition2).f();

                                if (list.isEmpty()) {
                                    break label38;
                                }

                                iblockdata3 = ((WorldGenFeatureCompositeFlower) list.get(0)).a(random, blockposition2);
                            } else {
                                iblockdata3 = iblockdata1;
                            }

                            if (iblockdata3.canPlace(world, blockposition2)) {
                                org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockGrowEvent(world, blockposition2, iblockdata3, 3); // CraftBukkit
                            }
                        }
                    }
                }

                ++i;
                break;
            }
        }

    }

    public boolean f(IBlockData iblockdata) {
        return true;
    }

    public TextureType c() {
        return TextureType.CUTOUT_MIPPED;
    }
}
