package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class PacketPlayOutTabComplete implements Packet<PacketListenerPlayOut> {

    private int a;
    private Suggestions b;

    public PacketPlayOutTabComplete() {}

    public PacketPlayOutTabComplete(int i, Suggestions suggestions) {
        this.a = i;
        this.b = suggestions;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.g();
        int i = packetdataserializer.g();
        int j = packetdataserializer.g();
        StringRange stringrange = StringRange.between(i, i + j);
        int k = packetdataserializer.g();
        List<Suggestion> list = Lists.newArrayListWithCapacity(k);

        for (int l = 0; l < k; ++l) {
            String s = packetdataserializer.e(32767);
            IChatBaseComponent ichatbasecomponent = packetdataserializer.readBoolean() ? packetdataserializer.f() : null;

            list.add(new Suggestion(stringrange, s, ichatbasecomponent));
        }

        this.b = new Suggestions(stringrange, list);
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a);
        packetdataserializer.d(this.b.getRange().getStart());
        packetdataserializer.d(this.b.getRange().getLength());
        packetdataserializer.d(this.b.getList().size());
        Iterator iterator = this.b.getList().iterator();

        while (iterator.hasNext()) {
            Suggestion suggestion = (Suggestion) iterator.next();

            packetdataserializer.a(suggestion.getText());
            packetdataserializer.writeBoolean(suggestion.getTooltip() != null);
            if (suggestion.getTooltip() != null) {
                packetdataserializer.a(ChatComponentUtils.a(suggestion.getTooltip()));
            }
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
