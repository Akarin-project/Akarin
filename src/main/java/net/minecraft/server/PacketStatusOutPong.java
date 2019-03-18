package net.minecraft.server;

import java.io.IOException;

public class PacketStatusOutPong implements Packet<PacketStatusOutListener> {

    private long a;

    public PacketStatusOutPong() {}

    public PacketStatusOutPong(long i) {
        this.a = i;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readLong();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeLong(this.a);
    }

    public void a(PacketStatusOutListener packetstatusoutlistener) {
        packetstatusoutlistener.a(this);
    }
    // Akarin start
    @Override
    public io.akarin.server.core.PacketType getType() {
        return io.akarin.server.core.PacketType.STATUS_OUT_PONG;
    }

    @Override
    public boolean canDispatchImmediately() {
        return true;
    }
    // Akarin end
}
