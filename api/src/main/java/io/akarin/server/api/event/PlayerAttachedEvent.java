package io.akarin.server.api.event;

import org.bukkit.entity.Player;

/**
 * Represents event with a player attached
 */
public interface PlayerAttachedEvent {
    public Player getPlayer();
}
