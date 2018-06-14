package io.akarin.server.mixin.bootstrap;

import java.io.File;
import java.io.PrintStream;

import org.bukkit.craftbukkit.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.akarin.api.internal.Akari;
import io.akarin.server.core.AkarinGlobalConfig;

@Mixin(value = Main.class, remap = false)
public abstract class Bootstrap {
    @Inject(method = "main([Ljava/lang/String;)V", at = @At("HEAD"))
    private static void premain(CallbackInfo info) {
        AkarinGlobalConfig.init(new File("akarin.yml"));
    }
    
    /*
     * Redirect notify message
     */
    @Redirect(method = "main", at = @At(
            value = "INVOKE_STRING",
            target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V",
            args = "ldc=*** Warning, you've not updated in a while! ***"
    ))
    private static void notifyUpdate(PrintStream stream, String text) {
        Akari.logger.warn("You've not updated in a while, the current version may outdated");
    }
    
    @Redirect(method = "main", at = @At(
            value = "INVOKE_STRING",
            target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V",
            args = "ldc=*** Please download a new build as per instructions from https://paperci.emc.gs/ ***"
    ))
    private static void notifyWebsite(PrintStream stream, String text) {
        Akari.logger.warn("Visit our website for latest information https://akarin.io/");
    }
    
    @Redirect(method = "main", at = @At(
            value = "INVOKE_STRING",
            target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V",
            args = "ldc=Loading libraries, please wait..."
    ))
    private static void notifyLoading(PrintStream stream, String text) {
        Akari.logger.info("Loading libraries as parallel capable..");
    }
}
