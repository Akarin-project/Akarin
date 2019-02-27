package net.minecraft.server;

public class ItemLingeringPotion extends ItemPotion {

    public ItemLingeringPotion(Item.Info item_info) {
        super(item_info);
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        ItemStack itemstack1 = entityhuman.abilities.canInstantlyBuild ? itemstack.cloneItemStack() : itemstack.cloneAndSubtract(1);

        world.a((EntityHuman) null, entityhuman.locX, entityhuman.locY, entityhuman.locZ, SoundEffects.ENTITY_LINGERING_POTION_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (ItemLingeringPotion.i.nextFloat() * 0.4F + 0.8F));
        if (!world.isClientSide) {
            EntityPotion entitypotion = new EntityPotion(world, entityhuman, itemstack1);

            entitypotion.a(entityhuman, entityhuman.pitch, entityhuman.yaw, -20.0F, 0.5F, 1.0F);
            world.addEntity(entitypotion);
        }

        entityhuman.b(StatisticList.ITEM_USED.b(this));
        return new InteractionResultWrapper<>(EnumInteractionResult.SUCCESS, itemstack);
    }
}
