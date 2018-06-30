package io.akarin.server.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import co.aikar.timings.MinecraftTimings;
import co.aikar.timings.Timing;
import io.akarin.api.internal.Akari;
import net.minecraft.server.CancelledPacketHandleException;
import net.minecraft.server.IAsyncTaskHandler;
import net.minecraft.server.Packet;
import net.minecraft.server.PacketListener;
import net.minecraft.server.PlayerConnectionUtils;

@Mixin(value = PlayerConnectionUtils.class, remap = false)
public abstract class MixinPlayerConnectionUtils {
    @Overwrite
    public static <T extends PacketListener> void ensureMainThread(final Packet<T> packet, final T listener, IAsyncTaskHandler iasynctaskhandler) throws CancelledPacketHandleException {
        if (!iasynctaskhandler.isMainThread()) {
            Timing timing = MinecraftTimings.getPacketTiming(packet);
            // MinecraftServer#postToMainThread inlined thread check, no twice
            Akari.callbackQueue.add(() -> {
                try (Timing ignored = timing.startTiming()) {
                    packet.a(listener);
                }
            });
            throw CancelledPacketHandleException.INSTANCE;
        }
    }
}
