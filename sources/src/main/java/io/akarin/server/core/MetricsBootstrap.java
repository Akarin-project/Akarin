package io.akarin.server.core;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import com.destroystokyo.paper.Metrics;

import net.minecraft.server.MinecraftServer;

public abstract class MetricsBootstrap {
    // Dragged from 'PaperMetrics' cause it's a private subclass, this will be called from mixined 'PaperConfig' class
    public static void startMetrics() {
        // Get the config file
        File configFile = new File(new File((File) MinecraftServer.getServer().options.valueOf("plugins"), "bStats"), "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        // Check if the config file exists
        if (!config.isSet("serverUuid")) {

            // Add default values
            config.addDefault("enabled", true);
            // Every server gets it's unique random id.
            config.addDefault("serverUuid", UUID.randomUUID().toString());
            // Should failed request be logged?
            config.addDefault("logFailedRequests", false);

            // Inform the server owners about bStats
            config.options().header(
                    "bStats collects some data for plugin authors like how many servers are using their plugins.\n" +
                            "To honor their work, you should not disable it.\n" +
                            "This has nearly no effect on the server performance!\n" +
                            "Check out https://bStats.org/ to learn more :)"
            ).copyDefaults(true);
            try {
                config.save(configFile);
            } catch (IOException ignored) {
                ;
            }
        }
        // Load the data
        String serverUUID = config.getString("serverUuid");
        boolean logFailedRequests = config.getBoolean("logFailedRequests", false);
        // Only start Metrics, if it's enabled in the config
        if (config.getBoolean("enabled", true)) {
            new Metrics("Torch", serverUUID, logFailedRequests, Bukkit.getLogger()); // Paper -> Torch
        }
    }
}
