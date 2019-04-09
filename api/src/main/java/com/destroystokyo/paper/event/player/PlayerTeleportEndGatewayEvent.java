package com.destroystokyo.paper.event.player;

import org.bukkit.Location;
import org.bukkit.block.EndGateway;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a teleport is triggered for an End Gateway
 */
public class PlayerTeleportEndGatewayEvent extends PlayerTeleportEvent {
    @NotNull private final EndGateway gateway;

    public PlayerTeleportEndGatewayEvent(@NotNull Player player, @NotNull Location from, @NotNull Location to, @NotNull EndGateway gateway) {
        super(player, from, to, PlayerTeleportEvent.TeleportCause.END_GATEWAY);
        this.gateway = gateway;
    }

    /**
     * The gateway triggering the teleport
     *
     * @return EndGateway used
     */
    @NotNull
    public EndGateway getGateway() {
        return gateway;
    }
}
