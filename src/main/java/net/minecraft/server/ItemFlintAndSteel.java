package net.minecraft.server;

import java.util.Iterator;

public class ItemFlintAndSteel extends Item {

    public ItemFlintAndSteel(Item.Info item_info) {
        super(item_info);
    }

    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        EntityHuman entityhuman = itemactioncontext.getEntity();
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition().shift(itemactioncontext.getClickedFace());

        if (a((GeneratorAccess) world, blockposition)) {
            world.a(entityhuman, blockposition, SoundEffects.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, ItemFlintAndSteel.i.nextFloat() * 0.4F + 0.8F);
            IBlockData iblockdata = ((BlockFire) Blocks.FIRE).a((IBlockAccess) world, blockposition);

            world.setTypeAndData(blockposition, iblockdata, 11);
            ItemStack itemstack = itemactioncontext.getItemStack();

            if (entityhuman instanceof EntityPlayer) {
                CriterionTriggers.y.a((EntityPlayer) entityhuman, blockposition, itemstack);
            }

            if (entityhuman != null) {
                itemstack.damage(1, entityhuman);
            }

            return EnumInteractionResult.SUCCESS;
        } else {
            return EnumInteractionResult.FAIL;
        }
    }

    public static boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        IBlockData iblockdata = ((BlockFire) Blocks.FIRE).a((IBlockAccess) generatoraccess, blockposition);
        boolean flag = false;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();

            if (generatoraccess.getType(blockposition.shift(enumdirection)).getBlock() == Blocks.OBSIDIAN && ((BlockPortal) Blocks.NETHER_PORTAL).b(generatoraccess, blockposition) != null) {
                flag = true;
            }
        }

        return generatoraccess.isEmpty(blockposition) && (iblockdata.canPlace(generatoraccess, blockposition) || flag);
    }
}
