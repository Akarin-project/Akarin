package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutRespawn implements Packet<PacketListenerPlayOut> {

    private DimensionManager a;
    private EnumDifficulty b;
    private EnumGamemode c;
    private WorldType d;

    public PacketPlayOutRespawn() {}

    public PacketPlayOutRespawn(DimensionManager dimensionmanager, EnumDifficulty enumdifficulty, WorldType worldtype, EnumGamemode enumgamemode) {
        this.a = dimensionmanager;
        this.b = enumdifficulty;
        this.c = enumgamemode;
        this.d = worldtype;
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = DimensionManager.a(packetdataserializer.readInt());
        this.b = EnumDifficulty.getById(packetdataserializer.readUnsignedByte());
        this.c = EnumGamemode.getById(packetdataserializer.readUnsignedByte());
        this.d = WorldType.getType(packetdataserializer.e(16));
        if (this.d == null) {
            this.d = WorldType.NORMAL;
        }

    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeInt(this.a.getDimensionID());
        packetdataserializer.writeByte(this.b.a());
        packetdataserializer.writeByte(this.c.getId());
        packetdataserializer.a(this.d.name());
    }
}
