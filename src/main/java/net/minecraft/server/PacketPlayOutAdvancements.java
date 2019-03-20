package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class PacketPlayOutAdvancements implements Packet<PacketListenerPlayOut> {

    private boolean a;
    private Map<MinecraftKey, Advancement.SerializedAdvancement> b;
    private Set<MinecraftKey> c;
    private Map<MinecraftKey, AdvancementProgress> d;

    public PacketPlayOutAdvancements() {}

    public PacketPlayOutAdvancements(boolean flag, Collection<Advancement> collection, Set<MinecraftKey> set, Map<MinecraftKey, AdvancementProgress> map) {
        this.a = flag;
        this.b = Maps.newHashMap();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Advancement advancement = (Advancement) iterator.next();

            this.b.put(advancement.getName(), advancement.a());
        }

        this.c = set;
        this.d = Maps.newHashMap(map);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readBoolean();
        this.b = Maps.newHashMap();
        this.c = Sets.newLinkedHashSet();
        this.d = Maps.newHashMap();
        int i = packetdataserializer.g();

        MinecraftKey minecraftkey;
        int j;

        for (j = 0; j < i; ++j) {
            minecraftkey = packetdataserializer.l();
            Advancement.SerializedAdvancement advancement_serializedadvancement = Advancement.SerializedAdvancement.b(packetdataserializer);

            this.b.put(minecraftkey, advancement_serializedadvancement);
        }

        i = packetdataserializer.g();

        for (j = 0; j < i; ++j) {
            minecraftkey = packetdataserializer.l();
            this.c.add(minecraftkey);
        }

        i = packetdataserializer.g();

        for (j = 0; j < i; ++j) {
            minecraftkey = packetdataserializer.l();
            this.d.put(minecraftkey, AdvancementProgress.b(packetdataserializer));
        }

    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeBoolean(this.a);
        packetdataserializer.d(this.b.size());
        Iterator iterator = this.b.entrySet().iterator();

        Entry entry;

        while (iterator.hasNext()) {
            entry = (Entry) iterator.next();
            MinecraftKey minecraftkey = (MinecraftKey) entry.getKey();
            Advancement.SerializedAdvancement advancement_serializedadvancement = (Advancement.SerializedAdvancement) entry.getValue();

            packetdataserializer.a(minecraftkey);
            advancement_serializedadvancement.a(packetdataserializer);
        }

        packetdataserializer.d(this.c.size());
        iterator = this.c.iterator();

        while (iterator.hasNext()) {
            MinecraftKey minecraftkey1 = (MinecraftKey) iterator.next();

            packetdataserializer.a(minecraftkey1);
        }

        packetdataserializer.d(this.d.size());
        iterator = this.d.entrySet().iterator();

        while (iterator.hasNext()) {
            entry = (Entry) iterator.next();
            packetdataserializer.a((MinecraftKey) entry.getKey());
            ((AdvancementProgress) entry.getValue()).a(packetdataserializer);
        }

    }
}
