package net.minecraft.server;

public class InventoryHorseChest extends InventorySubcontainer {

    // CraftBukkit start
    public InventoryHorseChest(IChatBaseComponent ichatbasecomponent, int i, EntityHorseAbstract owner) {
        super(ichatbasecomponent, i, (org.bukkit.entity.AbstractHorse) owner.getBukkitEntity());
        // CraftBukkit end
    }
}
