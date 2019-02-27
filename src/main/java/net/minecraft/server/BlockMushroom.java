package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

public class BlockMushroom extends BlockPlant implements IBlockFragilePlantElement {

    protected static final VoxelShape a = Block.a(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

    public BlockMushroom(Block.Info block_info) {
        super(block_info);
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockMushroom.a;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (random.nextInt(25) == 0) {
            int i = 5;
            boolean flag = true;
            Iterator iterator = BlockPosition.b(blockposition.a(-4, -1, -4), blockposition.a(4, 1, 4)).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();

                if (world.getType(blockposition1).getBlock() == this) {
                    --i;
                    if (i <= 0) {
                        return;
                    }
                }
            }

            BlockPosition blockposition2 = blockposition.a(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);

            for (int j = 0; j < 4; ++j) {
                if (world.isEmpty(blockposition2) && iblockdata.canPlace(world, blockposition2)) {
                    blockposition = blockposition2;
                }

                blockposition2 = blockposition.a(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            }

            if (world.isEmpty(blockposition2) && iblockdata.canPlace(world, blockposition2)) {
                world.setTypeAndData(blockposition2, iblockdata, 2);
            }
        }

    }

    protected boolean b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.f(iblockaccess, blockposition);
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();
        IBlockData iblockdata1 = iworldreader.getType(blockposition1);
        Block block = iblockdata1.getBlock();

        return block != Blocks.MYCELIUM && block != Blocks.PODZOL ? iworldreader.getLightLevel(blockposition, 0) < 13 && this.b(iblockdata1, (IBlockAccess) iworldreader, blockposition1) : true;
    }

    public boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        generatoraccess.setAir(blockposition);
        WorldGenerator<WorldGenFeatureEmptyConfiguration> worldgenerator = null;

        if (this == Blocks.BROWN_MUSHROOM) {
            worldgenerator = WorldGenerator.U;
        } else if (this == Blocks.RED_MUSHROOM) {
            worldgenerator = WorldGenerator.T;
        }

        if (worldgenerator != null && worldgenerator.generate(generatoraccess, generatoraccess.getChunkProvider().getChunkGenerator(), random, blockposition, WorldGenFeatureConfiguration.e)) {
            return true;
        } else {
            generatoraccess.setTypeAndData(blockposition, iblockdata, 3);
            return false;
        }
    }

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return true;
    }

    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return (double) random.nextFloat() < 0.4D;
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        this.a((GeneratorAccess) world, blockposition, iblockdata, random);
    }

    public boolean e(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return true;
    }
}
