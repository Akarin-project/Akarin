package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.loot.LootTable;

public abstract class CraftMob extends CraftLivingEntity implements Mob {
    public CraftMob(CraftServer server, EntityInsentient entity) {
        super(server, entity);
    }

    @Override
    public void setTarget(LivingEntity target) {
        EntityInsentient entity = getHandle();
        if (target == null) {
            entity.setGoalTarget(null, null, false);
        } else if (target instanceof CraftLivingEntity) {
            entity.setGoalTarget(((CraftLivingEntity) target).getHandle(), null, false);
        }
    }

    @Override
    public CraftLivingEntity getTarget() {
        if (getHandle().getGoalTarget() == null) return null;

        return (CraftLivingEntity) getHandle().getGoalTarget().getBukkitEntity();
    }

    @Override
    public EntityInsentient getHandle() {
        return (EntityInsentient) entity;
    }

    @Override
    public String toString() {
        return "CraftMob";
    }

    @Override
    public void setLootTable(LootTable table) {
        getHandle().lootTableKey = (table == null) ? null : CraftNamespacedKey.toMinecraft(table.getKey());
    }

    @Override
    public LootTable getLootTable() {
        if (getHandle().lootTableKey == null) {
            getHandle().lootTableKey = getHandle().getLootTable();
        }

        NamespacedKey key = CraftNamespacedKey.fromMinecraft(getHandle().lootTableKey);
        return Bukkit.getLootTable(key);
    }

    @Override
    public void setSeed(long seed) {
        getHandle().lootTableSeed = seed;
    }

    @Override
    public long getSeed() {
        return getHandle().lootTableSeed;
    }
}
