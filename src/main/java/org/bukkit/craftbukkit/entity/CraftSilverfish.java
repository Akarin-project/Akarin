package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntitySilverfish;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Silverfish;

public class CraftSilverfish extends CraftMonster implements Silverfish {
    public CraftSilverfish(CraftServer server, EntitySilverfish entity) {
        super(server, entity);
    }

    @Override
    public EntitySilverfish getHandle() {
        return (EntitySilverfish) entity;
    }

    @Override
    public String toString() {
        return "CraftSilverfish";
    }

    public EntityType getType() {
        return EntityType.SILVERFISH;
    }
}
