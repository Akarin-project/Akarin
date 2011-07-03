package net.minecraft.server;

import org.bukkit.event.player.PlayerFishEvent; // CraftBukkit

public class ItemFishingRod extends Item {

    public ItemFishingRod(int i) {
        super(i);
        this.d(64);
        this.c(1);
    }

    public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        if (entityhuman.hookedFish != null) {
            int i = entityhuman.hookedFish.h();

            itemstack.damage(i, entityhuman);
            entityhuman.w();
        } else {
            // CraftBukkit start
            PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), null,PlayerFishEvent.State.FISHING);
            world.getServer().getPluginManager().callEvent(playerFishEvent);

            if (playerFishEvent.isCancelled()) {
                return itemstack;
            }
            // CraftBukkit end
            world.makeSound(entityhuman, "random.bow", 0.5F, 0.4F / (b.nextFloat() * 0.4F + 0.8F));
            if (!world.isStatic) {
                world.addEntity(new EntityFish(world, entityhuman));
            }

            entityhuman.w();
        }

        return itemstack;
    }
}
