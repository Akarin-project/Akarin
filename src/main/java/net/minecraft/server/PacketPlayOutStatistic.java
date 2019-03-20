package net.minecraft.server;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.IOException;

public class PacketPlayOutStatistic implements Packet<PacketListenerPlayOut> {

    private Object2IntMap<Statistic<?>> a;

    public PacketPlayOutStatistic() {}

    public PacketPlayOutStatistic(Object2IntMap<Statistic<?>> object2intmap) {
        this.a = object2intmap;
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        int i = packetdataserializer.g();

        this.a = new Object2IntOpenHashMap(i);

        for (int j = 0; j < i; ++j) {
            this.a((StatisticWrapper) IRegistry.STATS.fromId(packetdataserializer.g()), packetdataserializer);
        }

    }

    private <T> void a(StatisticWrapper<T> statisticwrapper, PacketDataSerializer packetdataserializer) {
        int i = packetdataserializer.g();
        int j = packetdataserializer.g();

        this.a.put(statisticwrapper.b(statisticwrapper.a().fromId(i)), j);
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a.size());
        ObjectIterator objectiterator = this.a.object2IntEntrySet().iterator();

        while (objectiterator.hasNext()) {
            Entry<Statistic<?>> entry = (Entry) objectiterator.next();
            Statistic<?> statistic = (Statistic) entry.getKey();

            packetdataserializer.d(IRegistry.STATS.a(statistic.a())); // Akarin - fixes decompile error
            packetdataserializer.d(this.a(statistic));
            packetdataserializer.d(entry.getIntValue());
        }

    }

    private <T> int a(Statistic<T> statistic) {
        return statistic.a().a().a(statistic.b());
    }
}
