package io.akarin.api.mixin;

import org.bukkit.Bukkit;
import org.bukkit.command.defaults.VersionCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = VersionCommand.class, remap = false)
public class MixinVersionCommand {
    @Shadow private static int getFromRepo(String repo, String hash) { return 0; }
    
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
    
    @Shadow private void setVersionMessage(String msg) {}
    private static boolean customVersion;
    
    @Overwrite
    private void obtainVersion() {
        // Skipping if detected
        if (customVersion) return;
        
        String version = Bukkit.getVersion();
        if (version == null) version = "Unique"; // Custom - > Unique
        
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
                    break;
                default:
                    setVersionMessage("You are " + distance + " version(s) behind");
            }
        } else {
            customVersion = true;
        }
    }
}
