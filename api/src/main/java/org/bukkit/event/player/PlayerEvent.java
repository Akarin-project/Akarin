package org.bukkit.event.player;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import io.akarin.server.api.event.WorldAttachedEvent;

/**
 * Represents a player related event
 */
public abstract class PlayerEvent extends Event implements WorldAttachedEvent { // Akarin
    protected Player player;
    @Override @NotNull public World getWorld() { return player.getWorld(); } // Akarin

    public PlayerEvent(@NotNull final Player who) {
        player = who;
    }

    PlayerEvent(@NotNull final Player who, boolean async) {
        super(async);
        player = who;

    }

    /**
     * Returns the player involved in this event
     *
     * @return Player who is involved in this event
     */
    @NotNull
    public final Player getPlayer() {
        return player;
    }
}
