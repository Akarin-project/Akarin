package org.bukkit.craftbukkit.entity;

import com.destroystokyo.paper.entity.CraftRangedEntity;
import net.minecraft.server.EntityWitch;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Witch;
import org.bukkit.entity.EntityType;

// Paper start
import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
// Paper end

public class CraftWitch extends CraftMonster implements Witch, CraftRangedEntity<EntityWitch> { // Paper
    public CraftWitch(CraftServer server, EntityWitch entity) {
        super(server, entity);
    }

    @Override
    public EntityWitch getHandle() {
        return (EntityWitch) entity;
    }

    @Override
    public String toString() {
        return "CraftWitch";
    }

    public EntityType getType() {
        return EntityType.WITCH;
    }

    // Paper start
    public boolean isDrinkingPotion() {
        return getHandle().isDrinkingPotion();
    }

    public int getPotionUseTimeLeft() {
        return getHandle().getPotionUseTimeLeft();
    }

    public ItemStack getDrinkingPotion() {
        return CraftItemStack.asCraftMirror(getHandle().getItemInMainHand());
    }

    public void setDrinkingPotion(ItemStack potion) {
        Preconditions.checkArgument(potion == null || potion.getType().isEmpty() || potion.getType() == Material.POTION, "must be potion, air, or null");
        getHandle().setDrinkingPotion(CraftItemStack.asNMSCopy(potion));
    }
    // Paper end
}
