package com.destroystokyo.paper.event.entity;

import org.bukkit.Location;
import org.bukkit.block.EndGateway;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Fired any time an entity attempts to teleport in an end gateway
 */
public class EntityTeleportEndGatewayEvent extends EntityTeleportEvent {

    @NotNull private final EndGateway gateway;

    public EntityTeleportEndGatewayEvent(@NotNull Entity what, @NotNull Location from, @NotNull Location to, @NotNull EndGateway gateway) {
        super(what, from, to);
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
