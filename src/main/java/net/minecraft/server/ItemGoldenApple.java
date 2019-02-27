package net.minecraft.server;

public class ItemGoldenApple extends ItemFood {

    public ItemGoldenApple(int i, float f, boolean flag, Item.Info item_info) {
        super(i, f, flag, item_info);
    }

    protected void a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        if (!world.isClientSide) {
            // CraftBukkit start
            entityhuman.addEffect(new MobEffect(MobEffects.REGENERATION, 100, 1), org.bukkit.event.entity.EntityPotionEffectEvent.Cause.FOOD);
            entityhuman.addEffect(new MobEffect(MobEffects.ABSORBTION, 2400, 0), org.bukkit.event.entity.EntityPotionEffectEvent.Cause.FOOD);
            // CraftBukkit end
        }

    }
}
