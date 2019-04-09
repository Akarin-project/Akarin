package com.destroystokyo.paper.event.server;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when whitelist is toggled
 *
 * @author Mark Vainomaa
 */
public class WhitelistToggleEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private boolean enabled;

    public WhitelistToggleEvent(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets whether whitelist is going to be enabled or not
     *
     * @return Whether whitelist is going to be enabled or not
     */
    public boolean isEnabled() {
        return enabled;
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
