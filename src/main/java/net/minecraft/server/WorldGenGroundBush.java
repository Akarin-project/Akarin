package net.minecraft.server;

import java.util.Random;
import java.util.Set;

public class WorldGenGroundBush extends WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> {

    private final IBlockData a;
    private final IBlockData b;

    public WorldGenGroundBush(IBlockData iblockdata, IBlockData iblockdata1) {
        super(false);
        this.b = iblockdata;
        this.a = iblockdata1;
    }

    public boolean a(Set<BlockPosition> set, GeneratorAccess generatoraccess, Random random, BlockPosition blockposition) {
        for (IBlockData iblockdata = generatoraccess.getType(blockposition); (iblockdata.isAir() || iblockdata.a(TagsBlock.LEAVES)) && blockposition.getY() > 0; iblockdata = generatoraccess.getType(blockposition)) {
            blockposition = blockposition.down();
        }

        Block block = generatoraccess.getType(blockposition).getBlock();

        if (Block.d(block) || block == Blocks.GRASS_BLOCK) {
            blockposition = blockposition.up();
            this.a(set, generatoraccess, blockposition, this.b);

            for (int i = blockposition.getY(); i <= blockposition.getY() + 2; ++i) {
                int j = i - blockposition.getY();
                int k = 2 - j;

                for (int l = blockposition.getX() - k; l <= blockposition.getX() + k; ++l) {
                    int i1 = l - blockposition.getX();

                    for (int j1 = blockposition.getZ() - k; j1 <= blockposition.getZ() + k; ++j1) {
                        int k1 = j1 - blockposition.getZ();

                        if (Math.abs(i1) != k || Math.abs(k1) != k || random.nextInt(2) != 0) {
                            BlockPosition blockposition1 = new BlockPosition(l, i, j1);
                            IBlockData iblockdata1 = generatoraccess.getType(blockposition1);

                            if (iblockdata1.isAir() || iblockdata1.a(TagsBlock.LEAVES)) {
                                this.a(generatoraccess, blockposition1, this.a);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
