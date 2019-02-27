package net.minecraft.server;

public class ItemEnderPearl extends Item {

    public ItemEnderPearl(Item.Info item_info) {
        super(item_info);
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!entityhuman.abilities.canInstantlyBuild) {
            itemstack.subtract(1);
        }

        world.a((EntityHuman) null, entityhuman.locX, entityhuman.locY, entityhuman.locZ, SoundEffects.ENTITY_ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (ItemEnderPearl.i.nextFloat() * 0.4F + 0.8F));
        entityhuman.getCooldownTracker().a(this, 20);
        if (!world.isClientSide) {
            EntityEnderPearl entityenderpearl = new EntityEnderPearl(world, entityhuman);

            entityenderpearl.a(entityhuman, entityhuman.pitch, entityhuman.yaw, 0.0F, 1.5F, 1.0F);
            world.addEntity(entityenderpearl);
        }

        entityhuman.b(StatisticList.ITEM_USED.b(this));
        return new InteractionResultWrapper<>(EnumInteractionResult.SUCCESS, itemstack);
    }
}
