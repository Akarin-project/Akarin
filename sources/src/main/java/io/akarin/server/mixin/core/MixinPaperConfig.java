package io.akarin.server.mixin.core;

import java.util.Map;

import org.bukkit.command.Command;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.destroystokyo.paper.PaperConfig;

import io.akarin.server.core.MetricsBootstrap;
import net.minecraft.server.MinecraftServer;

@Mixin(value = PaperConfig.class, remap = false)
public class MixinPaperConfig {
    @Shadow static Map<String, Command> commands;
    @Shadow private static boolean metricsStarted;
    
    @Overwrite
    public static void registerCommands() {
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "Paper", entry.getValue());
        }

        if (!metricsStarted) {
            MetricsBootstrap.startMetrics();
            metricsStarted = true;
        }
    }
}
