package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityDolphin;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.EntityType;

public class CraftDolphin extends CraftCreature implements Dolphin {

    public CraftDolphin(CraftServer server, EntityDolphin entity) {
        super(server, entity);
    }

    @Override
    public EntityDolphin getHandle() {
        return (EntityDolphin) super.getHandle();
    }

    @Override
    public String toString() {
        return "CraftDolphin";
    }

    @Override
    public EntityType getType() {
        return EntityType.DOLPHIN;
    }
}
