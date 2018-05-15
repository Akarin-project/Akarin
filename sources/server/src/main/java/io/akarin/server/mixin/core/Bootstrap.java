package io.akarin.server.mixin.core;

import org.bukkit.craftbukkit.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Main.class, remap = false)
public class Bootstrap {
    @Inject(method = "main([Ljava/lang/String;)V", at = @At("HEAD"))
    private static void configureMixin(CallbackInfo info) {
        ;
    }
}
