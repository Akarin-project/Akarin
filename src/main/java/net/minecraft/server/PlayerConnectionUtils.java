package net.minecraft.server;

public class PlayerConnectionUtils {

    public static <T extends PacketListener> void ensureMainThread(Packet<T> packet, T t0, IAsyncTaskHandler iasynctaskhandler) throws CancelledPacketHandleException {
        if (!iasynctaskhandler.isMainThread()) {
            iasynctaskhandler.postToMainThread(() -> {
                if (t0 instanceof PlayerConnection && ((PlayerConnection) t0).processedDisconnect) return; // CraftBukkit
                packet.a(t0);
            });
            throw CancelledPacketHandleException.INSTANCE;
        }
    }
}
