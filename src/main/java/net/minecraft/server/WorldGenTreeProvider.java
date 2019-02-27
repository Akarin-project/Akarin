package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public abstract class WorldGenTreeProvider {

    public WorldGenTreeProvider() {}

    @Nullable
    protected abstract WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> b(Random random);

    public boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> worldgentreeabstract = this.b(random);

        if (worldgentreeabstract == null) {
            return false;
        } else {
            generatoraccess.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 4);
            if (worldgentreeabstract.generate(generatoraccess, generatoraccess.getChunkProvider().getChunkGenerator(), random, blockposition, WorldGenFeatureConfiguration.e)) {
                return true;
            } else {
                generatoraccess.setTypeAndData(blockposition, iblockdata, 4);
                return false;
            }
        }
    }
}
