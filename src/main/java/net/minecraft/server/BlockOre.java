package net.minecraft.server;

import java.util.Random;

public class BlockOre extends Block {

    public BlockOre(Block.Info block_info) {
        super(block_info);
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return (IMaterial) (this == Blocks.COAL_ORE ? Items.COAL : (this == Blocks.DIAMOND_ORE ? Items.DIAMOND : (this == Blocks.LAPIS_ORE ? Items.LAPIS_LAZULI : (this == Blocks.EMERALD_ORE ? Items.EMERALD : (this == Blocks.NETHER_QUARTZ_ORE ? Items.QUARTZ : this)))));
    }

    public int a(IBlockData iblockdata, Random random) {
        return this == Blocks.LAPIS_ORE ? 4 + random.nextInt(5) : 1;
    }

    public int getDropCount(IBlockData iblockdata, int i, World world, BlockPosition blockposition, Random random) {
        if (i > 0 && this != this.getDropType((IBlockData) this.getStates().a().iterator().next(), world, blockposition, i)) {
            int j = random.nextInt(i + 2) - 1;

            if (j < 0) {
                j = 0;
            }

            return this.a(iblockdata, random) * (j + 1);
        } else {
            return this.a(iblockdata, random);
        }
    }

    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, float f, int i) {
        super.dropNaturally(iblockdata, world, blockposition, f, i);
        if (this.getDropType(iblockdata, world, blockposition, i) != this) {
            int j = 0;

            if (this == Blocks.COAL_ORE) {
                j = MathHelper.nextInt(world.random, 0, 2);
            } else if (this == Blocks.DIAMOND_ORE) {
                j = MathHelper.nextInt(world.random, 3, 7);
            } else if (this == Blocks.EMERALD_ORE) {
                j = MathHelper.nextInt(world.random, 3, 7);
            } else if (this == Blocks.LAPIS_ORE) {
                j = MathHelper.nextInt(world.random, 2, 5);
            } else if (this == Blocks.NETHER_QUARTZ_ORE) {
                j = MathHelper.nextInt(world.random, 2, 5);
            }

            this.dropExperience(world, blockposition, j);
        }

    }

    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(this);
    }
}
