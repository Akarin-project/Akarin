package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutChat implements Packet<PacketListenerPlayOut> {
    private static final int MAX_LENGTH = Short.MAX_VALUE * 8 + 8; // Paper
    private IChatBaseComponent a;
    public net.md_5.bungee.api.chat.BaseComponent[] components; // Spigot
    private ChatMessageType b;

    public PacketPlayOutChat() {}

    public PacketPlayOutChat(IChatBaseComponent ichatbasecomponent) {
        this(ichatbasecomponent, ChatMessageType.SYSTEM);
    }

    public PacketPlayOutChat(IChatBaseComponent ichatbasecomponent, ChatMessageType chatmessagetype) {
        this.a = ichatbasecomponent;
        this.b = chatmessagetype;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.h();
        this.b = ChatMessageType.a(packetdataserializer.readByte());
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        // Spigot start
        if (components != null) {
            //packetdataserializer.a(net.md_5.bungee.chat.ComponentSerializer.toString(components)); // Paper - comment, replaced with below
            // Paper start - don't nest if we don't need to so that we can preserve formatting
            if (this.components.length == 1) {
                packetdataserializer.a(net.md_5.bungee.chat.ComponentSerializer.toString(this.components[0]), MAX_LENGTH); // Paper - use proper max length
            } else {
                packetdataserializer.a(net.md_5.bungee.chat.ComponentSerializer.toString(this.components), MAX_LENGTH); // Paper - use proper max length
            }
            // Paper end
        } else {
            packetdataserializer.a(this.a);
        }
        // Spigot end
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

    @Override
    public boolean a() {
        return true;
    }
}
