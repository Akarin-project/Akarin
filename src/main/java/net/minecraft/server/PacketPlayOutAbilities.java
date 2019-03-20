package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutAbilities implements Packet<PacketListenerPlayOut> {

    private boolean a;
    private boolean b;
    private boolean c;
    private boolean d;
    private float e;
    private float f;

    public PacketPlayOutAbilities() {}

    public PacketPlayOutAbilities(PlayerAbilities playerabilities) {
        this.a(playerabilities.isInvulnerable);
        this.b(playerabilities.isFlying);
        this.c(playerabilities.canFly);
        this.d(playerabilities.canInstantlyBuild);
        this.a(playerabilities.a());
        this.b(playerabilities.b());
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        byte b0 = packetdataserializer.readByte();

        this.a((b0 & 1) > 0);
        this.b((b0 & 2) > 0);
        this.c((b0 & 4) > 0);
        this.d((b0 & 8) > 0);
        this.a(packetdataserializer.readFloat());
        this.b(packetdataserializer.readFloat());
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        byte b0 = 0;

        if (this.b()) {
            b0 = (byte) (b0 | 1);
        }

        if (this.c()) {
            b0 = (byte) (b0 | 2);
        }

        if (this.d()) {
            b0 = (byte) (b0 | 4);
        }

        if (this.e()) {
            b0 = (byte) (b0 | 8);
        }

        packetdataserializer.writeByte(b0);
        packetdataserializer.writeFloat(this.e);
        packetdataserializer.writeFloat(this.f);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public boolean b() {
        return this.a;
    }

    public void a(boolean flag) {
        this.a = flag;
    }

    public boolean c() {
        return this.b;
    }

    public void b(boolean flag) {
        this.b = flag;
    }

    public boolean d() {
        return this.c;
    }

    public void c(boolean flag) {
        this.c = flag;
    }

    public boolean e() {
        return this.d;
    }

    public void d(boolean flag) {
        this.d = flag;
    }

    public void a(float f) {
        this.e = f;
    }

    public void b(float f) {
        this.f = f;
    }
}
