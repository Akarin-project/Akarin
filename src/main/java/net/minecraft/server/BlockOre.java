package net.minecraft.server;

import java.util.Random;

public class BlockOre extends Block {

    public BlockOre(Block.Info block_info) {
        super(block_info);
    }

    protected int a(Random random) {
        return this == Blocks.COAL_ORE ? MathHelper.nextInt(random, 0, 2) : (this == Blocks.DIAMOND_ORE ? MathHelper.nextInt(random, 3, 7) : (this == Blocks.EMERALD_ORE ? MathHelper.nextInt(random, 3, 7) : (this == Blocks.LAPIS_ORE ? MathHelper.nextInt(random, 2, 5) : (this == Blocks.NETHER_QUARTZ_ORE ? MathHelper.nextInt(random, 2, 5) : 0))));
    }

    @Override
    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, ItemStack itemstack) {
        super.dropNaturally(iblockdata, world, blockposition, itemstack);
        /* CraftBukkit start - Delegated to getExpDrop
        if (EnchantmentManager.getEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) == 0) {
            int i = this.a(world.random);

            if (i > 0) {
                this.dropExperience(world, blockposition, i);
            }
        }
        // */

    }

    @Override
    public int getExpDrop(IBlockData iblockdata, World world, BlockPosition blockposition, ItemStack itemstack) {
        if (EnchantmentManager.getEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) == 0) {
            int i = this.a(world.random);

            if (i > 0) {
                return i;
            }
        }

        return 0;
        // CraftBukkit end
    }
}
