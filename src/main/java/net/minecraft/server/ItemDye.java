package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Map;

public class ItemDye extends Item {

    private static final Map<EnumColor, ItemDye> a = Maps.newEnumMap(EnumColor.class);
    private final EnumColor b;

    public ItemDye(EnumColor enumcolor, Item.Info item_info) {
        super(item_info);
        this.b = enumcolor;
        ItemDye.a.put(enumcolor, this);
    }

    public boolean a(ItemStack itemstack, EntityHuman entityhuman, EntityLiving entityliving, EnumHand enumhand) {
        if (entityliving instanceof EntitySheep) {
            EntitySheep entitysheep = (EntitySheep) entityliving;

            if (!entitysheep.isSheared() && entitysheep.getColor() != this.b) {
                entitysheep.setColor(this.b);
                itemstack.subtract(1);
            }

            return true;
        } else {
            return false;
        }
    }

    public EnumColor d() {
        return this.b;
    }

    public static ItemDye a(EnumColor enumcolor) {
        return (ItemDye) ItemDye.a.get(enumcolor);
    }
}
