package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityBlaze;
import net.minecraft.server.EntityMonster;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Blaze;

public class CraftBlaze extends CraftMonster implements Blaze {
    public CraftBlaze(CraftServer server, EntityBlaze entity) {
        super(server, entity);
    }

    @Override
    public EntityBlaze getHandle() {
        return (EntityBlaze) entity;
    }

    @Override
    public String toString() {
        return "CraftBlaze";
    }
}
