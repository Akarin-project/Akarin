package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityFish;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Fish;

public class CraftFish extends CraftCreature implements Fish {

    public CraftFish(CraftServer server, EntityFish entity) {
        super(server, entity);
    }

    @Override
    public EntityFish getHandle() {
        return (EntityFish) entity;
    }

    @Override
    public String toString() {
        return "CraftFish";
    }
}
