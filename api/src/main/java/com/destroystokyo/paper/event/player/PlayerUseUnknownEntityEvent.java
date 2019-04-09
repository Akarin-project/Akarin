package com.destroystokyo.paper.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class PlayerUseUnknownEntityEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final int entityId;
    private final boolean attack;
    @NotNull private final EquipmentSlot hand;

    public PlayerUseUnknownEntityEvent(@NotNull Player who, int entityId, boolean attack, @NotNull EquipmentSlot hand) {
        super(who);
        this.entityId = entityId;
        this.attack = attack;
        this.hand = hand;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public boolean isAttack() {
        return this.attack;
    }

    @NotNull
    public EquipmentSlot getHand() {
        return this.hand;
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
