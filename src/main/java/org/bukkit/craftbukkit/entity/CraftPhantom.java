package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityPhantom;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;

public class CraftPhantom extends CraftFlying implements Phantom {

    public CraftPhantom(CraftServer server, EntityPhantom entity) {
        super(server, entity);
    }

    @Override
    public EntityPhantom getHandle() {
        return (EntityPhantom) super.getHandle();
    }

    @Override
    public int getSize() {
        return getHandle().getSize();
    }

    @Override
    public void setSize(int sz) {
        getHandle().setSize(sz);
    }

    @Override
    public String toString() {
        return "CraftPhantom";
    }

    @Override
    public EntityType getType() {
        return EntityType.PHANTOM;
    }
}
