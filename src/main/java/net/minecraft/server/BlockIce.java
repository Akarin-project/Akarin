package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockIce extends BlockHalfTransparent {

    public BlockIce(Block.Info block_info) {
        super(block_info);
    }

    @Override
    public TextureType c() {
        return TextureType.TRANSLUCENT;
    }

    @Override
    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        super.a(world, entityhuman, blockposition, iblockdata, tileentity, itemstack);
        if (EnchantmentManager.getEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) == 0) {
            if (world.worldProvider.isNether()) {
                world.a(blockposition, false);
                return;
            }

            Material material = world.getType(blockposition.down()).getMaterial();

            if (material.isSolid() || material.isLiquid()) {
                world.setTypeUpdate(blockposition, Blocks.WATER.getBlockData());
            }
        }

    }

    @Override
    public void tick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (world.getBrightness(EnumSkyBlock.BLOCK, blockposition) > 11 - iblockdata.b((IBlockAccess) world, blockposition)) {
            this.melt(iblockdata, world, blockposition);
        }

    }

    protected void melt(IBlockData iblockdata, World world, BlockPosition blockposition) {
        // CraftBukkit start
        if (org.bukkit.craftbukkit.event.CraftEventFactory.callBlockFadeEvent(world, blockposition, world.worldProvider.isNether() ? Blocks.AIR.getBlockData() : Blocks.WATER.getBlockData()).isCancelled()) {
            return;
        }
        // CraftBukkit end
        if (world.worldProvider.isNether()) {
            world.a(blockposition, false);
        } else {
            world.setTypeUpdate(blockposition, Blocks.WATER.getBlockData());
            world.a(blockposition, Blocks.WATER, blockposition);
        }
    }

    @Override
    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.NORMAL;
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EntityTypes<?> entitytypes) {
        return entitytypes == EntityTypes.POLAR_BEAR;
    }
}
