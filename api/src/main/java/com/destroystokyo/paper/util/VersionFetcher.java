package com.destroystokyo.paper.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public interface VersionFetcher {
    /**
     * Amount of time to cache results for in milliseconds
     * <p>
     * Negative values will never cache.
     *
     * @return cache time
     */
    long getCacheTime();

    /**
     * Gets the version message to cache and show to command senders. Multiple messages can be sent using newlines (\n)
     * in the string. The string will be split on these newlines and sent as individual messages.
     * <p>
     * NOTE: This is run in a new thread separate from that of the command processing thread
     *
     * @param serverVersion the current version of the server (will match {@link Bukkit#getVersion()})
     * @return the message to show when requesting a version
     */
    @NotNull
    String getVersionMessage(@NotNull String serverVersion);

    class DummyVersionFetcher implements VersionFetcher {

        @Override
        public long getCacheTime() {
            return -1;
        }

        @NotNull
        @Override
        public String getVersionMessage(@NotNull String serverVersion) {
            Bukkit.getLogger().warning("Version provider has not been set, cannot check for updates!");
            Bukkit.getLogger().info("Override the default implementation of org.bukkit.UnsafeValues#getVersionFetcher()");
            new Throwable().printStackTrace();
            return "Unable to check for updates. No version provider set.";
        }
    }
}
