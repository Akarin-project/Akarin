package net.minecraft.server;

import io.akarin.server.core.AkarinGlobalConfig;

/**
 * Akarin Changes Note
 * 1) Add end portal disable feature (feature)
 */
public class ItemEnderEye extends Item {

    public ItemEnderEye() {
        this.b(CreativeModeTab.f);
    }

    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        IBlockData iblockdata = world.getType(blockposition);
        ItemStack itemstack = entityhuman.b(enumhand);

        if (entityhuman.a(blockposition.shift(enumdirection), enumdirection, itemstack) && iblockdata.getBlock() == Blocks.END_PORTAL_FRAME && !iblockdata.get(BlockEnderPortalFrame.EYE).booleanValue()) {
            if (world.isClientSide) {
                return EnumInteractionResult.SUCCESS;
            } else {
                world.setTypeAndData(blockposition, iblockdata.set(BlockEnderPortalFrame.EYE, Boolean.valueOf(true)), 2);
                world.updateAdjacentComparators(blockposition, Blocks.END_PORTAL_FRAME);
                itemstack.subtract(1);

                for (int i = 0; i < 16; ++i) {
                    double d0 = blockposition.getX() + (5.0F + ItemEnderEye.j.nextFloat() * 6.0F) / 16.0F;
                    double d1 = blockposition.getY() + 0.8125F;
                    double d2 = blockposition.getZ() + (5.0F + ItemEnderEye.j.nextFloat() * 6.0F) / 16.0F;
                    double d3 = 0.0D;
                    double d4 = 0.0D;
                    double d5 = 0.0D;

                    world.addParticle(EnumParticle.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
                }

                world.a((EntityHuman) null, blockposition, SoundEffects.bp, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (AkarinGlobalConfig.disableEndPortalCreate) return EnumInteractionResult.SUCCESS; // Akarin
                ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = BlockEnderPortalFrame.e().a(world, blockposition);

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
            return EnumInteractionResult.FAIL;
        }
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        MovingObjectPosition movingobjectposition = this.a(world, entityhuman, false);

        if (movingobjectposition != null && movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.BLOCK && world.getType(movingobjectposition.a()).getBlock() == Blocks.END_PORTAL_FRAME) {
            return new InteractionResultWrapper(EnumInteractionResult.PASS, itemstack);
        } else {
            entityhuman.c(enumhand);
            if (!world.isClientSide) {
                BlockPosition blockposition = ((WorldServer) world).getChunkProviderServer().a(world, "Stronghold", new BlockPosition(entityhuman), false);

                if (blockposition != null) {
                    EntityEnderSignal entityendersignal = new EntityEnderSignal(world, entityhuman.locX, entityhuman.locY + entityhuman.length / 2.0F, entityhuman.locZ);

                    entityendersignal.a(blockposition);
                    world.addEntity(entityendersignal);
                    if (entityhuman instanceof EntityPlayer) {
                        CriterionTriggers.l.a((EntityPlayer) entityhuman, blockposition);
                    }

                    world.a((EntityHuman) null, entityhuman.locX, entityhuman.locY, entityhuman.locZ, SoundEffects.bc, SoundCategory.NEUTRAL, 0.5F, 0.4F / (ItemEnderEye.j.nextFloat() * 0.4F + 0.8F));
                    world.a((EntityHuman) null, 1003, new BlockPosition(entityhuman), 0);
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }

                    entityhuman.b(StatisticList.b(this));
                    return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, itemstack);
                }
            }

            return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, itemstack);
        }
    }
}
