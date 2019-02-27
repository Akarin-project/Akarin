package net.minecraft.server;

public class ItemEgg extends Item {

    public ItemEgg(Item.Info item_info) {
        super(item_info);
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!entityhuman.abilities.canInstantlyBuild) {
            itemstack.subtract(1);
        }

        world.a((EntityHuman) null, entityhuman.locX, entityhuman.locY, entityhuman.locZ, SoundEffects.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (ItemEgg.i.nextFloat() * 0.4F + 0.8F));
        if (!world.isClientSide) {
            EntityEgg entityegg = new EntityEgg(world, entityhuman);

            entityegg.a(entityhuman, entityhuman.pitch, entityhuman.yaw, 0.0F, 1.5F, 1.0F);
            world.addEntity(entityegg);
        }

        entityhuman.b(StatisticList.ITEM_USED.b(this));
        return new InteractionResultWrapper<>(EnumInteractionResult.SUCCESS, itemstack);
    }
}
