package net.minecraft.server;

import org.bukkit.event.player.PlayerFishEvent; // CraftBukkit

public class ItemFishingRod extends Item {

    public ItemFishingRod(Item.Info item_info) {
        super(item_info);
        // CraftBukkit start - obfuscator went a little crazy
        /*
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
        */
        // CraftBukkit end
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        int i;

        if (entityhuman.hookedFish != null) {
            if (!world.isClientSide) {
                i = entityhuman.hookedFish.b(itemstack);
                itemstack.damage(i, entityhuman, (entityhuman1) -> {
                    entityhuman1.d(enumhand);
                });
            }

            entityhuman.a(enumhand);
            world.playSound((EntityHuman) null, entityhuman.locX, entityhuman.locY, entityhuman.locZ, SoundEffects.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (ItemFishingRod.i.nextFloat() * 0.4F + 0.8F));
        } else {
            // world.playSound((EntityHuman) null, entityhuman.locX, entityhuman.locY, entityhuman.locZ, SoundEffects.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (ItemFishingRod.i.nextFloat() * 0.4F + 0.8F));
            if (!world.isClientSide) {
                i = EnchantmentManager.c(itemstack);
                int j = EnchantmentManager.b(itemstack);

                // CraftBukkit start
                EntityFishingHook entityfishinghook = new EntityFishingHook(entityhuman, world, j, i);
                PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), null, (org.bukkit.entity.FishHook) entityfishinghook.getBukkitEntity(), PlayerFishEvent.State.FISHING);
                world.getServer().getPluginManager().callEvent(playerFishEvent);

                if (playerFishEvent.isCancelled()) {
                    entityhuman.hookedFish = null;
                    return new InteractionResultWrapper(EnumInteractionResult.PASS, itemstack);
                }
                world.playSound((EntityHuman) null, entityhuman.locX, entityhuman.locY, entityhuman.locZ, SoundEffects.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (ItemFishingRod.i.nextFloat() * 0.4F + 0.8F));
                world.addEntity(entityfishinghook);
                // CraftBukkit end
            }

            entityhuman.a(enumhand);
            entityhuman.b(StatisticList.ITEM_USED.b(this));
        }

        return new InteractionResultWrapper<>(EnumInteractionResult.SUCCESS, itemstack);
    }

    @Override
    public int c() {
        return 1;
    }
}
