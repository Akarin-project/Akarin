package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityGuardian;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;

public class CraftGuardian extends CraftMonster implements Guardian {

    public CraftGuardian(CraftServer server, EntityGuardian entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "CraftGuardian";
    }

    @Override
    public EntityType getType() {
        return EntityType.GUARDIAN;
    }
}
