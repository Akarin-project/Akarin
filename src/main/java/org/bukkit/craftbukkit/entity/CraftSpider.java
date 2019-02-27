package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntitySpider;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Spider;

public class CraftSpider extends CraftMonster implements Spider {

    public CraftSpider(CraftServer server, EntitySpider entity) {
        super(server, entity);
    }

    @Override
    public EntitySpider getHandle() {
        return (EntitySpider) entity;
    }

    @Override
    public String toString() {
        return "CraftSpider";
    }

    public EntityType getType() {
        return EntityType.SPIDER;
    }
}
