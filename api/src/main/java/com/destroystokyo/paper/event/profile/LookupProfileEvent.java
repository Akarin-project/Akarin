package com.destroystokyo.paper.event.profile;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.jetbrains.annotations.NotNull;

/**
 * Allows a plugin to be notified anytime AFTER a Profile has been looked up from the Mojang API
 * This is an opportunity to view the response and potentially cache things.
 *
 * No guarantees are made about thread execution context for this event. If you need to know, check
 * event.isAsync()
 */
public class LookupProfileEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @NotNull private final PlayerProfile profile;

    public LookupProfileEvent(@NotNull PlayerProfile profile) {
        super(!Bukkit.isPrimaryThread());
        this.profile = profile;
    }

    /**
     * @return The profile that was recently looked up. This profile can be mutated
     */
    @NotNull
    public PlayerProfile getPlayerProfile() {
        return profile;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
