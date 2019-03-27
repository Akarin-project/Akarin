package co.aikar.timings;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.collect.MapMaker;
import net.minecraft.server.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.bukkit.craftbukkit.scheduler.CraftTask;

import java.util.Map;

public final class MinecraftTimings {

    public static final Timing playerListTimer = Timings.ofSafe("Player List");
    public static final Timing commandFunctionsTimer = Timings.ofSafe("Command Functions");
    public static final Timing connectionTimer = Timings.ofSafe("Connection Handler");
    public static final Timing tickablesTimer = Timings.ofSafe("Tickables");
    public static final Timing minecraftSchedulerTimer = Timings.ofSafe("Minecraft Scheduler");
    public static final Timing bukkitSchedulerTimer = Timings.ofSafe("Bukkit Scheduler");
    public static final Timing bukkitSchedulerPendingTimer = Timings.ofSafe("Bukkit Scheduler - Pending");
    public static final Timing bukkitSchedulerFinishTimer = Timings.ofSafe("Bukkit Scheduler - Finishing");
    public static final Timing chunkIOTickTimer = Timings.ofSafe("ChunkIOTick");
    public static final Timing timeUpdateTimer = Timings.ofSafe("Time Update");
    public static final Timing serverCommandTimer = Timings.ofSafe("Server Command");
    public static final Timing savePlayers = Timings.ofSafe("Save Players");

    public static final Timing tickEntityTimer = Timings.ofSafe("## tickEntity");
    public static final Timing tickTileEntityTimer = Timings.ofSafe("## tickTileEntity");
    public static final Timing packetProcessTimer = Timings.ofSafe("## Packet Processing");
    public static final Timing scheduledBlocksTimer = Timings.ofSafe("## Scheduled Blocks");
    public static final Timing structureGenerationTimer = Timings.ofSafe("Structure Generation");

    public static final Timing processQueueTimer = Timings.ofSafe("processQueue");

    public static final Timing playerCommandTimer = Timings.ofSafe("playerCommand");

    public static final Timing entityActivationCheckTimer = Timings.ofSafe("entityActivationCheck");

    public static final Timing antiXrayUpdateTimer = Timings.ofSafe("anti-xray - update");
    public static final Timing antiXrayObfuscateTimer = Timings.ofSafe("anti-xray - obfuscate");

    private static final Cache<Class<?>, String> taskNameCache = com.github.benmanes.caffeine.cache.Caffeine.newBuilder().weakKeys().build(); // Akarin - caffeine

    private MinecraftTimings() {}

    /**
     * Gets a timer associated with a plugins tasks.
     * @param bukkitTask
     * @param period
     * @return
     */
    public static Timing getPluginTaskTimings(BukkitTask bukkitTask, long period) {
        if (!bukkitTask.isSync()) {
            return NullTimingHandler.NULL;
        }
        Plugin plugin;

        CraftTask craftTask = (CraftTask) bukkitTask;

        final Class<?> taskClass = craftTask.getTaskClass();
        if (bukkitTask.getOwner() != null) {
            plugin = bukkitTask.getOwner();
        } else {
            plugin = TimingsManager.getPluginByClassloader(taskClass);
        }

        final String taskname = taskNameCache.get(taskClass, clazz -> // Akarin - caffeine
                clazz.isAnonymousClass() || clazz.isLocalClass()
                        ? clazz.getName()
                        : clazz.getCanonicalName());

        StringBuilder name = new StringBuilder(64);
        name.append("Task: ").append(taskname);
        if (period > 0) {
            name.append(" (interval:").append(period).append(")");
        } else {
            name.append(" (Single)");
        }

        if (plugin == null) {
            return Timings.ofSafe(null, name.toString());
        }

        return Timings.ofSafe(plugin, name.toString());
    }

    /**
     * Get a named timer for the specified entity type to track type specific timings.
     * @param entity
     * @return
     */
    public static Timing getEntityTimings(Entity entity) {
        String entityType = entity.getClass().getName();
        return Timings.ofSafe("Minecraft", "## tickEntity - " + entityType, tickEntityTimer);
    }

    /**
     * Get a named timer for the specified tile entity type to track type specific timings.
     * @param entity
     * @return
     */
    public static Timing getTileEntityTimings(TileEntity entity) {
        String entityType = entity.getClass().getName();
        return Timings.ofSafe("Minecraft", "## tickTileEntity - " + entityType, tickTileEntityTimer);
    }
    public static Timing getCancelTasksTimer() {
        return Timings.ofSafe("Cancel Tasks");
    }
    public static Timing getCancelTasksTimer(Plugin plugin) {
        return Timings.ofSafe(plugin, "Cancel Tasks");
    }

    public static void stopServer() {
        TimingsManager.stopServer();
    }

    public static Timing getBlockTiming(Block block) {
        return Timings.ofSafe("## Scheduled Block: " + block.toString(), scheduledBlocksTimer);
    }
/*
    public static Timing getStructureTiming(StructureGenerator structureGenerator) {
        return Timings.ofSafe("Structure Generator - " + structureGenerator.getName(), structureGenerationTimer);
    }*/

    public static Timing getPacketTiming(Packet packet) {
        return Timings.ofSafe("## Packet - " + packet.getClass().getSimpleName(), packetProcessTimer);
    }

    public static Timing getCommandFunctionTiming(CustomFunction function) {
        return Timings.ofSafe("Command Function - " + function.getMinecraftKey().toString());
    }
}
