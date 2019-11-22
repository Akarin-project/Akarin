package net.minecraft.server;

import java.util.Iterator;

public class ItemFlintAndSteel extends Item {

    public ItemFlintAndSteel(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        EntityHuman entityhuman = itemactioncontext.getEntity();
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        BlockPosition blockposition1 = blockposition.shift(itemactioncontext.getClickedFace());
        IBlockData iblockdata;

        if (a(world.getType(blockposition1), (GeneratorAccess) world, blockposition1)) {
            // CraftBukkit start - Store the clicked block
            if (org.bukkit.craftbukkit.event.CraftEventFactory.callBlockIgniteEvent(world, blockposition1, org.bukkit.event.block.BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL, entityhuman).isCancelled()) {
                itemactioncontext.getItemStack().damage(1, entityhuman, (entityhuman1) -> {
                    entityhuman1.d(itemactioncontext.n());
                });
                return EnumInteractionResult.PASS;
            }
            // CraftBukkit end
            world.playSound(entityhuman, blockposition1, SoundEffects.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, ItemFlintAndSteel.i.nextFloat() * 0.4F + 0.8F);
            iblockdata = ((BlockFire) Blocks.FIRE).a((IBlockAccess) world, blockposition1);
            world.setTypeAndData(blockposition1, iblockdata, 11);
            ItemStack itemstack = itemactioncontext.getItemStack();

            if (entityhuman instanceof EntityPlayer) {
                CriterionTriggers.y.a((EntityPlayer) entityhuman, blockposition1, itemstack);
                itemstack.damage(1, entityhuman, (entityhuman1) -> {
                    entityhuman1.d(itemactioncontext.n());
                });
            }

            return EnumInteractionResult.SUCCESS;
        } else {
            iblockdata = world.getType(blockposition);
            if (a(iblockdata)) {
                world.playSound(entityhuman, blockposition, SoundEffects.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, ItemFlintAndSteel.i.nextFloat() * 0.4F + 0.8F);
                world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockProperties.r, true), 11);
                if (entityhuman != null) {
                    itemactioncontext.getItemStack().damage(1, entityhuman, (entityhuman1) -> {
                        entityhuman1.d(itemactioncontext.n());
                    });
                }

                return EnumInteractionResult.SUCCESS;
            } else {
                return EnumInteractionResult.FAIL;
            }
        }
    }

    public static boolean a(IBlockData iblockdata) {
        return iblockdata.getBlock() == Blocks.CAMPFIRE && !(Boolean) iblockdata.get(BlockProperties.C) && !(Boolean) iblockdata.get(BlockProperties.r);
    }

    public static boolean a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        IBlockData iblockdata1 = ((BlockFire) Blocks.FIRE).a((IBlockAccess) generatoraccess, blockposition);
        boolean flag = false;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();

            if (generatoraccess.getType(blockposition.shift(enumdirection)).getBlock() == Blocks.OBSIDIAN && ((BlockPortal) Blocks.NETHER_PORTAL).b(generatoraccess, blockposition) != null) {
                flag = true;
            }
        }

        return iblockdata.isAir() && (iblockdata1.canPlace(generatoraccess, blockposition) || flag);
    }
}
