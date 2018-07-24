package io.akarin.server.mixin.realtime;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.akarin.api.internal.mixin.IMixinRealTimeTicking;
import net.minecraft.server.MinecraftServer;

@Mixin(value = MinecraftServer.class, remap = false, priority = 1001)
public abstract class MixinMinecraftServer implements IMixinRealTimeTicking {
    private static long lastTickNanos = System.nanoTime();
    private static long realTimeTicks = 1;
    
    @Inject(method = "C()V", at = @At("HEAD")) // OBFHELPER: fullTick
    public void onTickUpdateRealTimeTicks(CallbackInfo ci) {
        long currentNanos = System.nanoTime();
        realTimeTicks = (currentNanos - lastTickNanos) / 50000000;
        if (realTimeTicks < 1) {
            realTimeTicks = 1;
        }
        lastTickNanos = currentNanos;
    }
    
    @Override
    public long getRealTimeTicks() {
        return realTimeTicks;
    }
}
