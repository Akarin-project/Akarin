package net.minecraft.server;

public class PacketStatusListener implements PacketStatusInListener {

    private static final IChatBaseComponent a = new ChatMessage("multiplayer.status.request_handled", new Object[0]);
    private final MinecraftServer minecraftServer;
    private final NetworkManager networkManager;
    private boolean d;

    public PacketStatusListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
        this.minecraftServer = minecraftserver;
        this.networkManager = networkmanager;
    }

    public void a(IChatBaseComponent ichatbasecomponent) {}

    public void a(PacketStatusInStart packetstatusinstart) {
        if (this.d) {
            this.networkManager.close(PacketStatusListener.a);
        } else {
            this.d = true;
            this.networkManager.sendPacket(new PacketStatusOutServerInfo(this.minecraftServer.getServerPing()));
        }
    }

    public void a(PacketStatusInPing packetstatusinping) {
        this.networkManager.sendPacket(new PacketStatusOutPong(packetstatusinping.b()));
        this.networkManager.close(PacketStatusListener.a);
    }
}
