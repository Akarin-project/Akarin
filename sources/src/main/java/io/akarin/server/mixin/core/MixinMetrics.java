package io.akarin.server.mixin.core;

import java.util.List;

import org.bukkit.Bukkit;
import io.akarin.api.LogWrapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import com.destroystokyo.paper.Metrics;
import com.destroystokyo.paper.Metrics.CustomChart;

@Mixin(value = Metrics.class, remap = false)
public class MixinMetrics {
    // The url to which the data is sent - bukkit/Torch (keep our old name)
    @Shadow @Mutable @Final public static String URL = "https://bStats.org/submitData/bukkit";
    
    // The name of the server software
    @Shadow @Final private String name;
    
    // A list with all custom charts
    @Shadow @Final private List<CustomChart> charts;
    
    /**
     * Injects the plugin specific data - insert version
     *
     * @return The plugin specific data.
     */
    @Overwrite
    private JSONObject getPluginData() {
        JSONObject data = new JSONObject();

        data.put("pluginName", name); // Append the name of the server software
        data.put("pluginVersion", Metrics.class.getPackage().getImplementationVersion() != null ? Metrics.class.getPackage().getImplementationVersion() : "unknown"); // Akarin
        JSONArray customCharts = new JSONArray();
        data.put("customCharts", customCharts);

        return data;
    }
    
    // The uuid of the server
    @Shadow @Final private String serverUUID;
    
    /**
     * Gets the server specific data - insert minecraft data
     *
     * @return The server specific data.
     */
    @Overwrite
    private JSONObject getServerData() {
        // Minecraft specific data
        int playerAmount = Bukkit.getOnlinePlayers().size();
        int onlineMode = Bukkit.getOnlineMode() ? 1 : 0;
        String bukkitVersion = org.bukkit.Bukkit.getVersion();
        bukkitVersion = bukkitVersion.substring(bukkitVersion.indexOf("MC: ") + 4, bukkitVersion.length() - 1);
        LogWrapper.logger.warn("Akarin debug: " + URL);
        
        JSONObject data = new JSONObject();
        data.put("playerAmount", playerAmount);
        data.put("onlineMode", onlineMode);
        data.put("bukkitVersion", bukkitVersion);
        
        // OS specific data
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String osVersion = System.getProperty("os.version");
        int coreCount = Runtime.getRuntime().availableProcessors();

        data.put("serverUUID", serverUUID);

        data.put("osName", osName);
        data.put("osArch", osArch);
        data.put("osVersion", osVersion);
        data.put("coreCount", coreCount);

        return data;
    }
}
