package com.destroystokyo.paper.event.block;

import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Fired anytime the server intends to 'destroy' a block through some triggering reason.
 * This does not fire anytime a block is set to air, but only with more direct triggers such
 * as physics updates, pistons, Entities changing blocks, commands set to "Destroy".
 *
 * This event is associated with the game playing a sound effect at the block in question, when
 * something can be described as "intend to destroy what is there",
 *
 * Events such as leaves decaying, pistons retracting (where the block is moving), does NOT fire this event.
 *
 */
public class BlockDestroyEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @NotNull private final BlockData newState;
    private final boolean willDrop;
    private boolean playEffect;

    private boolean cancelled = false;

    public BlockDestroyEvent(@NotNull Block block, @NotNull BlockData newState, boolean willDrop) {
        super(block);
        this.newState = newState;
        this.willDrop = willDrop;
    }

    /**
     * @return The new state of this block (Air, or a Fluid type)
     */
    @NotNull
    public BlockData getNewState() {
        return newState;
    }

    /**
     * @return If the server is going to drop the block in question with this destroy event
     */
    public boolean willDrop() {
        return this.willDrop;
    }

    /**
     * @return If the server is going to play the sound effect for this destruction
     */
    public boolean playEffect() {
        return this.playEffect;
    }

    /**
     * @param playEffect If the server should play the sound effect for this destruction
     */
    public void setPlayEffect(boolean playEffect) {
        this.playEffect = playEffect;
    }

    /**
     * @return If the event is cancelled, meaning the block will not be destroyed
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * If the event is cancelled, the block will remain in its previous state.
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
