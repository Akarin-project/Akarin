package net.minecraft.server;

import java.util.List;

public class ItemEndCrystal extends Item {

    public ItemEndCrystal(Item.Info item_info) {
        super(item_info);
    }

    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        IBlockData iblockdata = world.getType(blockposition);

        if (iblockdata.getBlock() != Blocks.OBSIDIAN && iblockdata.getBlock() != Blocks.BEDROCK) {
            return EnumInteractionResult.FAIL;
        } else {
            BlockPosition blockposition1 = blockposition.up();

            if (!world.isEmpty(blockposition1)) {
                return EnumInteractionResult.FAIL;
            } else {
                double d0 = (double) blockposition1.getX();
                double d1 = (double) blockposition1.getY();
                double d2 = (double) blockposition1.getZ();
                List<Entity> list = world.getEntities((Entity) null, new AxisAlignedBB(d0, d1, d2, d0 + 1.0D, d1 + 2.0D, d2 + 1.0D));

                if (!list.isEmpty()) {
                    return EnumInteractionResult.FAIL;
                } else {
                    if (!world.isClientSide) {
                        EntityEnderCrystal entityendercrystal = new EntityEnderCrystal(world, d0 + 0.5D, d1, d2 + 0.5D);

                        entityendercrystal.setShowingBottom(false);
                        // CraftBukkit start
                        if (org.bukkit.craftbukkit.event.CraftEventFactory.callEntityPlaceEvent(itemactioncontext, entityendercrystal).isCancelled()) {
                            return EnumInteractionResult.FAIL;
                        }
                        // CraftBukkit end
                        world.addEntity(entityendercrystal);
                        if (world.worldProvider instanceof WorldProviderTheEnd) {
                            EnderDragonBattle enderdragonbattle = ((WorldProviderTheEnd) world.worldProvider).r();

                            enderdragonbattle.e();
                        }
                    }

                    itemactioncontext.getItemStack().subtract(1);
                    return EnumInteractionResult.SUCCESS;
                }
            }
        }
    }
}
