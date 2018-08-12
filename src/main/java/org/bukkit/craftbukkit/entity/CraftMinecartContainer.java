package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityMinecartAbstract;
import net.minecraft.server.EntityMinecartContainer;
import net.minecraft.server.MinecraftKey;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

public abstract class CraftMinecartContainer extends CraftMinecart implements Lootable {

    public CraftMinecartContainer(CraftServer server, EntityMinecartAbstract entity) {
        super(server, entity);
    }

    @Override
    public EntityMinecartContainer getHandle() {
        return (EntityMinecartContainer) entity;
    }

    @Override
    public void setLootTable(LootTable table) {
        setLootTable(table, getSeed());
    }

    @Override
    public LootTable getLootTable() {
        MinecraftKey nmsTable = getHandle().Q_(); // PAIL getLootTable
        if (nmsTable == null) {
            return null; // return empty loot table?
        }

        NamespacedKey key = CraftNamespacedKey.fromMinecraft(nmsTable);
        return Bukkit.getLootTable(key);
    }

    @Override
    public void setSeed(long seed) {
        setLootTable(getLootTable(), seed);
    }

    @Override
    public long getSeed() {
        return getHandle().d; // PAIL rename lootTableSeed
    }

    private void setLootTable(LootTable table, long seed) {
        MinecraftKey newKey = (table == null) ? null : CraftNamespacedKey.toMinecraft(table.getKey());
        getHandle().a(newKey, seed);
    }
}
