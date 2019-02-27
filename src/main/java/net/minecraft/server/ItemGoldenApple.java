package net.minecraft.server;

public class ItemGoldenApple extends ItemFood {

    public ItemGoldenApple(int i, float f, boolean flag, Item.Info item_info) {
        super(i, f, flag, item_info);
    }

    protected void a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        if (!world.isClientSide) {
            entityhuman.addEffect(new MobEffect(MobEffects.REGENERATION, 100, 1));
            entityhuman.addEffect(new MobEffect(MobEffects.ABSORBTION, 2400, 0));
        }

    }
}
