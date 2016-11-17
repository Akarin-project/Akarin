package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityLlamaSpit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.projectiles.ProjectileSource;

public class CraftLlamaSpit extends AbstractProjectile implements LlamaSpit {

    public CraftLlamaSpit(CraftServer server, EntityLlamaSpit entity) {
        super(server, entity);
    }

    @Override
    public EntityLlamaSpit getHandle() {
        return (EntityLlamaSpit) super.getHandle();
    }

    @Override
    public String toString() {
        return "CraftLlamaSpit";
    }

    @Override
    public EntityType getType() {
        return EntityType.LLAMA_SPIT;
    }

    @Override
    public LivingEntity _INVALID_getShooter() {
        return (getHandle().shooter != null) ? (LivingEntity) getHandle().shooter.getBukkitEntity() : null;
    }

    @Override
    public ProjectileSource getShooter() {
        return (getHandle().shooter != null) ? (ProjectileSource) getHandle().shooter.getBukkitEntity() : null;
    }

    @Override
    public void _INVALID_setShooter(LivingEntity shooter) {
        getHandle().shooter = (shooter != null) ? ((CraftLivingEntity) shooter).getHandle() : null;
    }

    @Override
    public void setShooter(ProjectileSource source) {
        getHandle().shooter = (source != null) ? ((CraftLivingEntity) source).getHandle() : null;
    }
}
