package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Map;

import org.bukkit.event.entity.SheepDyeWoolEvent; // CraftBukkit

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
                // CraftBukkit start
                byte bColor = (byte) this.b.getColorIndex();
                SheepDyeWoolEvent event = new SheepDyeWoolEvent((org.bukkit.entity.Sheep) entitysheep.getBukkitEntity(), org.bukkit.DyeColor.getByWoolData(bColor));
                entitysheep.world.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return false;
                }

                entitysheep.setColor(EnumColor.fromColorIndex((byte) event.getColor().getWoolData()));
                // CraftBukkit end
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
