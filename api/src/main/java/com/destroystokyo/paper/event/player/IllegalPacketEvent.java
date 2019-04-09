package com.destroystokyo.paper.event.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IllegalPacketEvent extends PlayerEvent {
    @Nullable private final String type;
    @Nullable private final String ex;
    @Nullable private String kickMessage;
    private boolean shouldKick = true;

    public IllegalPacketEvent(@NotNull Player player, @Nullable String type, @Nullable String kickMessage, @NotNull Exception e) {
        super(player);
        this.type = type;
        this.kickMessage = kickMessage;
        this.ex = e.getMessage();
    }

    public boolean isShouldKick() {
        return shouldKick;
    }

    public void setShouldKick(boolean shouldKick) {
        this.shouldKick = shouldKick;
    }

    @Nullable
    public String getKickMessage() {
        return kickMessage;
    }

    public void setKickMessage(@Nullable String kickMessage) {
        this.kickMessage = kickMessage;
    }

    @Nullable
    public String getType() {
        return type;
    }

    @Nullable
    public String getExceptionMessage() {
        return ex;
    }

    private static final HandlerList handlers = new HandlerList();

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public static void process(@NotNull Player player, @Nullable String type, @Nullable String kickMessage, @NotNull Exception exception) {
        IllegalPacketEvent event = new IllegalPacketEvent(player, type, kickMessage, exception);
        event.callEvent();
        if (event.shouldKick) {
            player.kickPlayer(kickMessage);
        }
        Bukkit.getLogger().severe(player.getName() + "/" + type + ": " + exception.getMessage());
    }
}
