package io.akarin.server.mixin.core;

import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import org.bukkit.craftbukkit.util.Waitable;
import org.spigotmc.AsyncCatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import io.akarin.api.internal.Akari;
import net.minecraft.server.MCUtil;
import net.minecraft.server.MinecraftServer;

@Mixin(value = MCUtil.class, remap = false)
public abstract class MixinMCUtil {
    @Overwrite
    public static <T> T ensureMain(String reason, Supplier<T> run) {
        if (AsyncCatcher.enabled && !Akari.isPrimaryThread()) {
            new IllegalStateException("Asynchronous " + reason + "! Blocking thread until it returns ").printStackTrace();
            Waitable<T> wait = new Waitable<T>() {
                @Override
                protected T evaluate() {
                    return run.get();
                }
            };
            MinecraftServer.getServer().processQueue.add(wait);
            try {
                return wait.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }
        
        return run.get();
    }
}
