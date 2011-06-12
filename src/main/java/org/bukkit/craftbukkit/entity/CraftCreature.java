package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityCreature;
import net.minecraft.server.EntityLiving;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;

public class CraftCreature extends CraftLivingEntity implements Creature {
    public CraftCreature(CraftServer server, EntityCreature entity) {
        super(server, entity);
    }

    public void setTarget(LivingEntity target) {
        EntityCreature entity = getHandle();
        if (target == null) {
            entity.target = null;
        } else if (target instanceof CraftLivingEntity) {
            EntityLiving victim = ((CraftLivingEntity) target).getHandle();
            entity.target = victim;
            entity.pathEntity = entity.world.findPath(entity, entity.target, 16.0F);
        }
    }

    public CraftLivingEntity getTarget() {
        if (getHandle().target == null) return null;

        return (CraftLivingEntity) getHandle().target.getBukkitEntity();
    }

    @Override
    public EntityCreature getHandle() {
        return (EntityCreature) entity;
    }

    @Override
    public String toString() {
        return "CraftCreature";
    }
}
