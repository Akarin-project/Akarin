package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutEntity implements Packet<PacketListenerPlayOut> {

    protected int a;
    protected int b;
    protected int c;
    protected int d;
    protected byte e;
    protected byte f;
    protected boolean g;
    protected boolean h;

    public PacketPlayOutEntity() {}

    public PacketPlayOutEntity(int i) {
        this.a = i;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.g();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public String toString() {
        return "Entity_" + super.toString();
    }

    public static class PacketPlayOutEntityLook extends PacketPlayOutEntity {

        public PacketPlayOutEntityLook() {
            this.h = true;
        }

        public PacketPlayOutEntityLook(int i, byte b0, byte b1, boolean flag) {
            super(i);
            this.e = b0;
            this.f = b1;
            this.h = true;
            this.g = flag;
        }

        public void a(PacketDataSerializer packetdataserializer) throws IOException {
            super.a(packetdataserializer);
            this.e = packetdataserializer.readByte();
            this.f = packetdataserializer.readByte();
            this.g = packetdataserializer.readBoolean();
        }

        public void b(PacketDataSerializer packetdataserializer) throws IOException {
            super.b(packetdataserializer);
            packetdataserializer.writeByte(this.e);
            packetdataserializer.writeByte(this.f);
            packetdataserializer.writeBoolean(this.g);
        }
    }

    public static class PacketPlayOutRelEntityMove extends PacketPlayOutEntity {

        public PacketPlayOutRelEntityMove() {}

        public PacketPlayOutRelEntityMove(int i, long j, long k, long l, boolean flag) {
            super(i);
            this.b = (int) j;
            this.c = (int) k;
            this.d = (int) l;
            this.g = flag;
        }

        public void a(PacketDataSerializer packetdataserializer) throws IOException {
            super.a(packetdataserializer);
            this.b = packetdataserializer.readShort();
            this.c = packetdataserializer.readShort();
            this.d = packetdataserializer.readShort();
            this.g = packetdataserializer.readBoolean();
        }

        public void b(PacketDataSerializer packetdataserializer) throws IOException {
            super.b(packetdataserializer);
            packetdataserializer.writeShort(this.b);
            packetdataserializer.writeShort(this.c);
            packetdataserializer.writeShort(this.d);
            packetdataserializer.writeBoolean(this.g);
        }
    }

    public static class PacketPlayOutRelEntityMoveLook extends PacketPlayOutEntity {

        public PacketPlayOutRelEntityMoveLook() {
            this.h = true;
        }

        public PacketPlayOutRelEntityMoveLook(int i, long j, long k, long l, byte b0, byte b1, boolean flag) {
            super(i);
            this.b = (int) j;
            this.c = (int) k;
            this.d = (int) l;
            this.e = b0;
            this.f = b1;
            this.g = flag;
            this.h = true;
        }

        public void a(PacketDataSerializer packetdataserializer) throws IOException {
            super.a(packetdataserializer);
            this.b = packetdataserializer.readShort();
            this.c = packetdataserializer.readShort();
            this.d = packetdataserializer.readShort();
            this.e = packetdataserializer.readByte();
            this.f = packetdataserializer.readByte();
            this.g = packetdataserializer.readBoolean();
        }

        public void b(PacketDataSerializer packetdataserializer) throws IOException {
            super.b(packetdataserializer);
            packetdataserializer.writeShort(this.b);
            packetdataserializer.writeShort(this.c);
            packetdataserializer.writeShort(this.d);
            packetdataserializer.writeByte(this.e);
            packetdataserializer.writeByte(this.f);
            packetdataserializer.writeBoolean(this.g);
        }
    }
}
