package net.minecraft.server;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class StatisticManager {

    protected final Object2IntMap<Statistic<?>> a = Object2IntMaps.synchronize(new Object2IntOpenHashMap());

    public StatisticManager() {
        this.a.defaultReturnValue(0);
    }

    public void b(EntityHuman entityhuman, Statistic<?> statistic, int i) {
        // CraftBukkit start - fire Statistic events
        org.bukkit.event.Cancellable cancellable = org.bukkit.craftbukkit.event.CraftEventFactory.handleStatisticsIncrease(entityhuman, statistic, this.getStatisticValue(statistic), i);
        if (cancellable != null && cancellable.isCancelled()) {
            return;
        }
        // CraftBukkit end
        this.setStatistic(entityhuman, statistic, this.getStatisticValue(statistic) + i);
    }

    public void setStatistic(EntityHuman entityhuman, Statistic<?> statistic, int i) {
        this.a.put(statistic, i);
    }

    public int getStatisticValue(Statistic<?> statistic) {
        return this.a.getInt(statistic);
    }
}
