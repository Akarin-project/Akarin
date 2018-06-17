package io.akarin.server.mixin.bootstrap;

import org.spigotmc.RestartCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.akarin.api.internal.Akari;
import io.akarin.server.core.AkarinGlobalConfig;

@Mixin(value = RestartCommand.class, remap = false)
public abstract class MixinRestartCommand {
    @Inject(method = "restart()V", at = @At("HEAD"))
    private static void beforeRestart(CallbackInfo ci) {
        if (AkarinGlobalConfig.noResponseDoGC) {
            Akari.logger.warn("Attempting to garbage collect, may takes a few seconds");
            System.runFinalization();
            System.gc();
        }
    }
}
