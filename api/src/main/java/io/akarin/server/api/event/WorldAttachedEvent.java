package io.akarin.server.api.event;

import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Represents event with a world attached
 */
public interface WorldAttachedEvent {
    public World getWorld();
}
