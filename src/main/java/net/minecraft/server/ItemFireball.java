package net.minecraft.server;

public class ItemFireball extends Item {

    public ItemFireball(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();

        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            BlockPosition blockposition = itemactioncontext.getClickPosition();
            IBlockData iblockdata = world.getType(blockposition);

            if (iblockdata.getBlock() == Blocks.CAMPFIRE) {
                if (!(Boolean) iblockdata.get(BlockCampfire.b) && !(Boolean) iblockdata.get(BlockCampfire.d)) {
                    // CraftBukkit start - fire BlockIgniteEvent
                    if (org.bukkit.craftbukkit.event.CraftEventFactory.callBlockIgniteEvent(world, blockposition, org.bukkit.event.block.BlockIgniteEvent.IgniteCause.FIREBALL, itemactioncontext.getEntity()).isCancelled()) {
                        if (!itemactioncontext.getEntity().abilities.canInstantlyBuild) {
                            itemactioncontext.getItemStack().subtract(1);
                        }
                        return EnumInteractionResult.PASS;
                    }
                    // CraftBukkit end
                    this.a(world, blockposition);
                    world.setTypeUpdate(blockposition, (IBlockData) iblockdata.set(BlockCampfire.b, true));
                }
            } else {
                blockposition = blockposition.shift(itemactioncontext.getClickedFace());
                if (world.getType(blockposition).isAir()) {
                    // CraftBukkit start - fire BlockIgniteEvent
                    if (org.bukkit.craftbukkit.event.CraftEventFactory.callBlockIgniteEvent(world, blockposition, org.bukkit.event.block.BlockIgniteEvent.IgniteCause.FIREBALL, itemactioncontext.getEntity()).isCancelled()) {
                        if (!itemactioncontext.getEntity().abilities.canInstantlyBuild) {
                            itemactioncontext.getItemStack().subtract(1);
                        }
                        return EnumInteractionResult.PASS;
                    }
                    // CraftBukkit end
                    this.a(world, blockposition);
                    world.setTypeUpdate(blockposition, ((BlockFire) Blocks.FIRE).a((IBlockAccess) world, blockposition));
                }
            }

            itemactioncontext.getItemStack().subtract(1);
            return EnumInteractionResult.SUCCESS;
        }
    }

    private void a(World world, BlockPosition blockposition) {
        world.playSound((EntityHuman) null, blockposition, SoundEffects.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (ItemFireball.i.nextFloat() - ItemFireball.i.nextFloat()) * 0.2F + 1.0F);
    }
}
