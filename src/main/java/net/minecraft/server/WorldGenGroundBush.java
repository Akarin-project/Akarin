package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class WorldGenGroundBush extends WorldGenTreeAbstract<WorldGenFeatureEmptyConfiguration> {

    private final IBlockData a;
    private final IBlockData aS;

    public WorldGenGroundBush(Function<Dynamic<?>, ? extends WorldGenFeatureEmptyConfiguration> function, IBlockData iblockdata, IBlockData iblockdata1) {
        super(function, false);
        this.aS = iblockdata;
        this.a = iblockdata1;
    }

    @Override
    public boolean a(Set<BlockPosition> set, VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, StructureBoundingBox structureboundingbox) {
        blockposition = virtuallevelwritable.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, blockposition).down();
        if (h(virtuallevelwritable, blockposition)) {
            blockposition = blockposition.up();
            this.a(set, (IWorldWriter) virtuallevelwritable, blockposition, this.aS, structureboundingbox);

            for (int i = blockposition.getY(); i <= blockposition.getY() + 2; ++i) {
                int j = i - blockposition.getY();
                int k = 2 - j;

                for (int l = blockposition.getX() - k; l <= blockposition.getX() + k; ++l) {
                    int i1 = l - blockposition.getX();

                    for (int j1 = blockposition.getZ() - k; j1 <= blockposition.getZ() + k; ++j1) {
                        int k1 = j1 - blockposition.getZ();

                        if (Math.abs(i1) != k || Math.abs(k1) != k || random.nextInt(2) != 0) {
                            BlockPosition blockposition1 = new BlockPosition(l, i, j1);

                            if (g(virtuallevelwritable, blockposition1)) {
                                this.a(set, (IWorldWriter) virtuallevelwritable, blockposition1, this.a, structureboundingbox);
                            }
                        }
                    }
                }
            }
        // CraftBukkit start - Return false if gen was unsuccessful
        } else {
            return false;
        }
        // CraftBukkit end


        return true;
    }
}
