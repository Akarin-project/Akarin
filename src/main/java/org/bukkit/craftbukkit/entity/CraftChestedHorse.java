package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityHorseChestedAbstract;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.ChestedHorse;

public abstract class CraftChestedHorse extends CraftAbstractHorse implements ChestedHorse {

    public CraftChestedHorse(CraftServer server, EntityHorseChestedAbstract entity) {
        super(server, entity);
    }

    @Override
    public EntityHorseChestedAbstract getHandle() {
        return (EntityHorseChestedAbstract) super.getHandle();
    }

    @Override
    public boolean isCarryingChest() {
        return getHandle().isCarryingChest();
    }

    @Override
    public void setCarryingChest(boolean chest) {
        if (chest == isCarryingChest()) return;
        getHandle().setCarryingChest(chest);
        getHandle().loadChest();
    }
}
