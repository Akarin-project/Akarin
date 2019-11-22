package net.minecraft.server;

public class ItemWaterLily extends ItemBlock {

    public ItemWaterLily(Block block, Item.Info item_info) {
        super(block, item_info);
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        return EnumInteractionResult.PASS;
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        MovingObjectPosition movingobjectposition = a(world, entityhuman, RayTrace.FluidCollisionOption.SOURCE_ONLY);

        if (movingobjectposition.getType() == MovingObjectPosition.EnumMovingObjectType.MISS) {
            return new InteractionResultWrapper<>(EnumInteractionResult.PASS, itemstack);
        } else {
            if (movingobjectposition.getType() == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
                MovingObjectPositionBlock movingobjectpositionblock = (MovingObjectPositionBlock) movingobjectposition;
                BlockPosition blockposition = movingobjectpositionblock.getBlockPosition();
                EnumDirection enumdirection = movingobjectpositionblock.getDirection();

                if (!world.a(entityhuman, blockposition) || !entityhuman.a(blockposition.shift(enumdirection), enumdirection, itemstack)) {
                    return new InteractionResultWrapper<>(EnumInteractionResult.FAIL, itemstack);
                }

                BlockPosition blockposition1 = blockposition.up();
                IBlockData iblockdata = world.getType(blockposition);
                Material material = iblockdata.getMaterial();
                Fluid fluid = world.getFluid(blockposition);

                if ((fluid.getType() == FluidTypes.WATER || material == Material.ICE) && world.isEmpty(blockposition1)) {
                    // CraftBukkit start - special case for handling block placement with water lilies
                    org.bukkit.block.BlockState blockstate = org.bukkit.craftbukkit.block.CraftBlockState.getBlockState(world, blockposition1);
                    world.setTypeAndData(blockposition1, Blocks.LILY_PAD.getBlockData(), 11);
                    org.bukkit.event.block.BlockPlaceEvent placeEvent = org.bukkit.craftbukkit.event.CraftEventFactory.callBlockPlaceEvent(world, entityhuman, enumhand, blockstate, blockposition.getX(), blockposition.getY(), blockposition.getZ());
                    if (placeEvent != null && (placeEvent.isCancelled() || !placeEvent.canBuild())) {
                        blockstate.update(true, false);
                        return new InteractionResultWrapper(EnumInteractionResult.PASS, itemstack);
                    }
                    // CraftBukkit end
                    if (entityhuman instanceof EntityPlayer) {
                        CriterionTriggers.y.a((EntityPlayer) entityhuman, blockposition1, itemstack);
                    }

                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }

                    entityhuman.b(StatisticList.ITEM_USED.b(this));
                    world.playSound(entityhuman, blockposition, SoundEffects.BLOCK_LILY_PAD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    return new InteractionResultWrapper<>(EnumInteractionResult.SUCCESS, itemstack);
                }
            }

            return new InteractionResultWrapper<>(EnumInteractionResult.FAIL, itemstack);
        }
    }
}
