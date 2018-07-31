package io.akarin.server.mixin.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VersionCommand;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.google.common.base.Charsets;

import io.akarin.api.internal.Akari;
import io.akarin.server.core.AkarinGlobalConfig;
import net.minecraft.server.MCUtil;

@Mixin(value = VersionCommand.class, remap = false)
public abstract class MixinVersionCommand {
    @Overwrite
    private static int getFromRepo(String repo, String hash) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://api.github.com/repos/" + repo + "/compare/ver/1.12.2..." + hash).openConnection();
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) return -2; // Unknown commit
            try (
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8))
            ) {
                JSONObject obj = (JSONObject) new JSONParser().parse(reader);
                String status = (String) obj.get("status");
                switch (status) {
                    case "identical":
                        return 0;
                    case "behind":
                        return ((Number) obj.get("behind_by")).intValue();
                    default:
                        return -1;
                }
            } catch (ParseException | NumberFormatException e) {
                e.printStackTrace();
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Match current version with repository and calculate the distance
     * @param repo
     * @param verInfo
     * @return Version distance from lastest
     */
    @Overwrite
    private static int getDistance(String repo, String verInfo) {
        verInfo = verInfo.replace("\"", "");
        return getFromRepo("Akarin-project/Akarin", verInfo);
    }
    
    /**
     * A workaround for unexpected calling
     * @param currentVer
     * @return Version distance from lastest
     */
    @Overwrite
    private static int getFromJenkins(int currentVer) {
        String[] parts = Bukkit.getVersion().substring("git-Akarin-".length()).split("[-\\s]");
        return getFromRepo("Akarin-project/Akarin", parts[0]);
    }
    
    @Shadow private boolean hasVersion;
    @Shadow private String versionMessage;
    @Shadow private @Final Set<CommandSender> versionWaiters;
    
    private volatile boolean versionObtaining;
    private long lastCheckMillis;
    
    private CommandSender currentSender;
    private boolean customVersion;
    
    // The name can lead to misunderstand,
    // this method doesn't send the whole version message (e.g. 'This server is running {} version' or 'Previous version'),
    // it is only responsible for checking the version distance!
    @Overwrite
    private void sendVersion(CommandSender sender) {
        // Skipping if already detected a custom version identifier (e.g. 'git-Akarin-')
        // This should be lying in 'obtainVersion' method, but bump for faster returning
        if (customVersion) return;
        
        synchronized (versionWaiters) {
            versionWaiters.add(sender);
        }
        if (versionObtaining) return;
        // The volatile guarantees the safety between different threads.
        // Remembers that we are still on main thread now,
        // it's not guarantee that what time the last check ends,
        // but it's guarantee that only one request will be accepted at the same time.
        // After this, de-sync operations are safety - without lock -
        // (this is really a special case that we cancel new tasks instead let them wait).
        versionObtaining = true;
        
        if (hasVersion) {
            long current = System.currentTimeMillis();
            if (current - lastCheckMillis > AkarinGlobalConfig.versionUpdateInterval) {
                lastCheckMillis = current;
                hasVersion = false;
            } else {
                sender.sendMessage(versionMessage);
                return;
            }
        }
        if (!hasVersion) {
            obtainVersion(sender);
            if (AkarinGlobalConfig.legacyVersioningCompat) currentSender = sender;
        }
    }
    
    @Overwrite
    private void obtainVersion() {
        if (AkarinGlobalConfig.legacyVersioningCompat) {
            obtainVersion(currentSender);
            currentSender = null; // try release
        } else {
            Akari.logger.warn("A legacy version lookup was caught, legacy-versioning-compat enabled forcely!");
            AkarinGlobalConfig.legacyVersioningCompat = true;
            AkarinGlobalConfig.set("bonus.legacy-versioning-compat", true);
        }
    }
    
    private void obtainVersion(CommandSender sender) {
        // We post all things because a custom version is rare (expiring is not rare),
        // and we'd better post this task as early as we can, since it's a will (horrible destiny).
        MCUtil.scheduleAsyncTask(() -> {
            sender.sendMessage("Checking version, please wait...");
            
            String version = Akari.getServerVersion();
            if (version == null) {
                version = "Unique"; // Custom - > Unique
                customVersion = true;
                return;
            }
            
            if (version.startsWith("git-Akarin-")) {
                String[] parts = version.substring("git-Akarin-".length()).split("[-\\s]");
                int distance = getDistance(null, parts[0]);
                switch (distance) {
                    case -1:
                        setVersionMessage("Error obtaining version information");
                        break;
                    case 0:
                        setVersionMessage("You are running the latest version");
                        break;
                    case -2:
                        setVersionMessage("Unknown version");
                        customVersion = true;
                        break;
                    default:
                        setVersionMessage("You are " + distance + " version(s) behind");
                }
            } else {
                customVersion = true;
            }
            versionObtaining = false;
        });
    }
    
    @Overwrite
    private void setVersionMessage(String message) {
        versionMessage = message;
        hasVersion = true;
        
        synchronized (versionWaiters) {
            for (CommandSender sender : versionWaiters) {
                sender.sendMessage(versionMessage);
            }
            versionWaiters.clear();
        }
    }
}
