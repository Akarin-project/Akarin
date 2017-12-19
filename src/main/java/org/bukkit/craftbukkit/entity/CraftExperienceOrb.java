package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityExperienceOrb;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;

public class CraftExperienceOrb extends CraftEntity implements ExperienceOrb {
    public CraftExperienceOrb(CraftServer server, EntityExperienceOrb entity) {
        super(server, entity);
    }

    public int getExperience() {
        return getHandle().value;
    }

    public void setExperience(int value) {
        getHandle().value = value;
    }

    // Paper start
    public java.util.UUID getTriggerEntityId() {
        return getHandle().triggerEntityId;
    }
    public java.util.UUID getSourceEntityId() {
        return getHandle().sourceEntityId;
    }
    public SpawnReason getSpawnReason() {
        return getHandle().spawnReason;
    }
    // Paper end

    @Override
    public EntityExperienceOrb getHandle() {
        return (EntityExperienceOrb) entity;
    }

    @Override
    public String toString() {
        return "CraftExperienceOrb";
    }

    public EntityType getType() {
        return EntityType.EXPERIENCE_ORB;
    }
}
