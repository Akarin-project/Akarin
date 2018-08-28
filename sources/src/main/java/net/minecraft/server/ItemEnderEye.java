package net.minecraft.server;

import io.akarin.server.core.AkarinGlobalConfig;

/**
 * Akarin Changes Note
 * 1) Add end portal disable feature (feature)
 */
public class ItemEnderEye extends Item {

    public ItemEnderEye(Item.Info item_info) {
        super(item_info);
    }

    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        IBlockData iblockdata = world.getType(blockposition);

        if (iblockdata.getBlock() == Blocks.END_PORTAL_FRAME && !((Boolean) iblockdata.get(BlockEnderPortalFrame.EYE)).booleanValue()) {
            if (world.isClientSide) {
                return EnumInteractionResult.SUCCESS;
            } else {
                IBlockData iblockdata1 = (IBlockData) iblockdata.set(BlockEnderPortalFrame.EYE, Boolean.valueOf(true));

                Block.a(iblockdata, iblockdata1, world, blockposition);
                world.setTypeAndData(blockposition, iblockdata1, 2);
                world.updateAdjacentComparators(blockposition, Blocks.END_PORTAL_FRAME);
                itemactioncontext.getItemStack().subtract(1);

                for (int i = 0; i < 16; ++i) {
                    double d0 = (double) ((float) blockposition.getX() + (5.0F + ItemEnderEye.i.nextFloat() * 6.0F) / 16.0F);
                    double d1 = (double) ((float) blockposition.getY() + 0.8125F);
                    double d2 = (double) ((float) blockposition.getZ() + (5.0F + ItemEnderEye.i.nextFloat() * 6.0F) / 16.0F);
                    double d3 = 0.0D;
                    double d4 = 0.0D;
                    double d5 = 0.0D;

                    world.addParticle(Particles.M, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }

                world.a((EntityHuman) null, blockposition, SoundEffects.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (AkarinGlobalConfig.disableEndPortalCreate) return EnumInteractionResult.SUCCESS; // Akarin
                ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = BlockEnderPortalFrame.d().a(world, blockposition);

                if (shapedetector_shapedetectorcollection != null) {
                    BlockPosition blockposition1 = shapedetector_shapedetectorcollection.a().a(-3, 0, -3);

                    for (int j = 0; j < 3; ++j) {
                        for (int k = 0; k < 3; ++k) {
                            world.setTypeAndData(blockposition1.a(j, 0, k), Blocks.END_PORTAL.getBlockData(), 2);
                        }
                    }

                    world.a(1038, blockposition1.a(1, 0, 1), 0);
                }

                return EnumInteractionResult.SUCCESS;
            }
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        MovingObjectPosition movingobjectposition = this.a(world, entityhuman, false);

        if (movingobjectposition != null && movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.BLOCK && world.getType(movingobjectposition.a()).getBlock() == Blocks.END_PORTAL_FRAME) {
            return new InteractionResultWrapper(EnumInteractionResult.PASS, itemstack);
        } else {
            entityhuman.c(enumhand);
            if (!world.isClientSide) {
                BlockPosition blockposition = ((WorldServer) world).getChunkProviderServer().a(world, "Stronghold", new BlockPosition(entityhuman), 100, false);

                if (blockposition != null) {
                    EntityEnderSignal entityendersignal = new EntityEnderSignal(world, entityhuman.locX, entityhuman.locY + (double) (entityhuman.length / 2.0F), entityhuman.locZ);

                    entityendersignal.a(blockposition);
                    world.addEntity(entityendersignal);
                    if (entityhuman instanceof EntityPlayer) {
                        CriterionTriggers.m.a((EntityPlayer) entityhuman, blockposition);
                    }

                    world.a((EntityHuman) null, entityhuman.locX, entityhuman.locY, entityhuman.locZ, SoundEffects.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 0.5F, 0.4F / (ItemEnderEye.i.nextFloat() * 0.4F + 0.8F));
                    world.a((EntityHuman) null, 1003, new BlockPosition(entityhuman), 0);
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }

                    entityhuman.b(StatisticList.ITEM_USED.b(this));
                    return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, itemstack);
                }
            }

            return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, itemstack);
        }
    }
}
