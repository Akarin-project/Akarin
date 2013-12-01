package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutChat implements Packet<PacketListenerPlayOut> {

    private IChatBaseComponent a;
    private ChatMessageType b;

    public PacketPlayOutChat() {}

    public PacketPlayOutChat(IChatBaseComponent ichatbasecomponent) {
        this(ichatbasecomponent, ChatMessageType.SYSTEM);
    }

    public PacketPlayOutChat(IChatBaseComponent ichatbasecomponent, ChatMessageType chatmessagetype) {
        this.a = ichatbasecomponent;
        this.b = chatmessagetype;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.f();
        this.b = ChatMessageType.a(packetdataserializer.readByte());
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.writeByte(this.b.a());
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public boolean c() {
        return this.b == ChatMessageType.SYSTEM || this.b == ChatMessageType.GAME_INFO;
    }

    public ChatMessageType d() {
        return this.b;
    }

    public boolean a() {
        return true;
    }
}
