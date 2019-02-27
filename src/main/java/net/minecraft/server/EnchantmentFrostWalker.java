package net.minecraft.server;

import java.util.Iterator;
// CraftBukkit start
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.event.block.EntityBlockFormEvent;
// CraftBukkit end

public class EnchantmentFrostWalker extends Enchantment {

    public EnchantmentFrostWalker(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.ARMOR_FEET, aenumitemslot);
    }

    public int a(int i) {
        return i * 10;
    }

    public int b(int i) {
        return this.a(i) + 15;
    }

    public boolean isTreasure() {
        return true;
    }

    public int getMaxLevel() {
        return 2;
    }

    public static void a(EntityLiving entityliving, World world, BlockPosition blockposition, int i) {
        if (entityliving.onGround) {
            IBlockData iblockdata = Blocks.FROSTED_ICE.getBlockData();
            float f = (float) Math.min(16, 2 + i);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(0, 0, 0);
            Iterator iterator = BlockPosition.b(blockposition.a((double) (-f), -1.0D, (double) (-f)), blockposition.a((double) f, -1.0D, (double) f)).iterator();

            while (iterator.hasNext()) {
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = (BlockPosition.MutableBlockPosition) iterator.next();

                if (blockposition_mutableblockposition1.g(entityliving.locX, entityliving.locY, entityliving.locZ) <= (double) (f * f)) {
                    blockposition_mutableblockposition.c(blockposition_mutableblockposition1.getX(), blockposition_mutableblockposition1.getY() + 1, blockposition_mutableblockposition1.getZ());
                    IBlockData iblockdata1 = world.getType(blockposition_mutableblockposition);

                    if (iblockdata1.isAir()) {
                        IBlockData iblockdata2 = world.getType(blockposition_mutableblockposition1);

                        if (iblockdata2.getMaterial() == Material.WATER && (Integer) iblockdata2.get(BlockFluids.LEVEL) == 0 && iblockdata.canPlace(world, blockposition_mutableblockposition1) && world.a(iblockdata, (BlockPosition) blockposition_mutableblockposition1)) {
                            // CraftBukkit Start - Call EntityBlockFormEvent for Frost Walker
                            if (org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockFormEvent(world, blockposition_mutableblockposition1, iblockdata, entityliving)) {
                                world.getBlockTickList().a(blockposition_mutableblockposition1.h(), Blocks.FROSTED_ICE, MathHelper.nextInt(entityliving.getRandom(), 60, 120));
                            }
                            // CraftBukkit End
                        }
                    }
                }
            }

        }
    }

    public boolean a(Enchantment enchantment) {
        return super.a(enchantment) && enchantment != Enchantments.DEPTH_STRIDER;
    }
}
