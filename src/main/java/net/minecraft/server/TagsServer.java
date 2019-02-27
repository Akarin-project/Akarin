package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class TagsServer<T> extends Tags<T> {

    private final IRegistry<T> a;
    public int version; // CraftBukkit

    public TagsServer(IRegistry<T> iregistry, String s, String s1) {
        super(iregistry::c, iregistry::get, s, false, s1);
        this.a = iregistry;
    }

    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.c().size());
        Iterator iterator = this.c().entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<MinecraftKey, Tag<T>> entry = (Entry) iterator.next();

            packetdataserializer.a((MinecraftKey) entry.getKey());
            packetdataserializer.d(((Tag) entry.getValue()).a().size());
            Iterator iterator1 = ((Tag) entry.getValue()).a().iterator();

            while (iterator1.hasNext()) {
                T t0 = (T) iterator1.next(); // CraftBukkit - decompile error

                packetdataserializer.d(this.a.a(t0));
            }
        }

    }

    public void b(PacketDataSerializer packetdataserializer) {
        int i = packetdataserializer.g();

        for (int j = 0; j < i; ++j) {
            MinecraftKey minecraftkey = packetdataserializer.l();
            int k = packetdataserializer.g();
            List<T> list = Lists.newArrayList();

            for (int l = 0; l < k; ++l) {
                list.add(this.a.fromId(packetdataserializer.g()));
            }

            this.c().put(minecraftkey, (Tag<T>) Tag.a.a().a((Collection) list).b(minecraftkey)); // CraftBukkit - decompile error
        }

    }
}
