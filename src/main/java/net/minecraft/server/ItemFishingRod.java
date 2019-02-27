package net.minecraft.server;

public class ItemFishingRod extends Item {

    public ItemFishingRod(Item.Info item_info) {
        super(item_info);
        this.a(new MinecraftKey("cast"), (itemstack, world, entityliving) -> {
            if (entityliving == null) {
                return 0.0F;
            } else {
                boolean flag = entityliving.getItemInMainHand() == itemstack;
                boolean flag1 = entityliving.getItemInOffHand() == itemstack;

                if (entityliving.getItemInMainHand().getItem() instanceof ItemFishingRod) {
                    flag1 = false;
                }

                return (flag || flag1) && entityliving instanceof EntityHuman && ((EntityHuman) entityliving).hookedFish != null ? 1.0F : 0.0F;
            }
        });
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (entityhuman.hookedFish != null) {
            int i = entityhuman.hookedFish.b(itemstack);

            itemstack.damage(i, entityhuman);
            entityhuman.a(enumhand);
            world.a((EntityHuman) null, entityhuman.locX, entityhuman.locY, entityhuman.locZ, SoundEffects.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (ItemFishingRod.i.nextFloat() * 0.4F + 0.8F));
        } else {
            world.a((EntityHuman) null, entityhuman.locX, entityhuman.locY, entityhuman.locZ, SoundEffects.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (ItemFishingRod.i.nextFloat() * 0.4F + 0.8F));
            if (!world.isClientSide) {
                EntityFishingHook entityfishinghook = new EntityFishingHook(world, entityhuman);
                int j = EnchantmentManager.c(itemstack);

                if (j > 0) {
                    entityfishinghook.a(j);
                }

                int k = EnchantmentManager.b(itemstack);

                if (k > 0) {
                    entityfishinghook.b(k);
                }

                world.addEntity(entityfishinghook);
            }

            entityhuman.a(enumhand);
            entityhuman.b(StatisticList.ITEM_USED.b(this));
        }

        return new InteractionResultWrapper<>(EnumInteractionResult.SUCCESS, itemstack);
    }

    public int c() {
        return 1;
    }
}
