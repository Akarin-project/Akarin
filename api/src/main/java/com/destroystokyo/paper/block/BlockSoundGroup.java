package com.destroystokyo.paper.block;

import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the sounds that a {@link Block} makes in certain situations
 * <p>
 * The sound group includes break, step, place, hit, and fall sounds.
 */
public interface BlockSoundGroup {
    /**
     * Gets the sound that plays when breaking this block
     *
     * @return The break sound
     */
    @NotNull
    Sound getBreakSound();

    /**
     * Gets the sound that plays when stepping on this block
     *
     * @return The step sound
     */
    @NotNull
    Sound getStepSound();

    /**
     * Gets the sound that plays when placing this block
     *
     * @return The place sound
     */
    @NotNull
    Sound getPlaceSound();

    /**
     * Gets the sound that plays when hitting this block
     *
     * @return The hit sound
     */
    @NotNull
    Sound getHitSound();

    /**
     * Gets the sound that plays when this block falls
     *
     * @return The fall sound
     */
    @NotNull
    Sound getFallSound();
}
