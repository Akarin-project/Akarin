package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityEndermite;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.EntityType;

public class CraftEndermite extends CraftMonster implements Endermite {

    public CraftEndermite(CraftServer server, EntityEndermite entity) {
        super(server, entity);
    }

    @Override
    public EntityEndermite getHandle() {
        return (EntityEndermite) super.getHandle();
    }

    @Override
    public String toString() {
        return "CraftEndermite";
    }

    @Override
    public EntityType getType() {
        return EntityType.ENDERMITE;
    }

    @Override
    public boolean isPlayerSpawned() {
        return getHandle().l(); // PAIL
    }

    @Override
    public void setPlayerSpawned(boolean playerSpawned) {
        getHandle().a(playerSpawned); // PAIL
    }
}
