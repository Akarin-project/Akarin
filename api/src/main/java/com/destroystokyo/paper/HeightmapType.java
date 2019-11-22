package com.destroystokyo.paper;

import org.bukkit.World;

/**
 * Enumeration of different heightmap types maintained by the server. Generally using these maps is much faster
 * than using an iterative search for a block in a given x, z coordinate.
 */
public enum HeightmapType {

    /**
     * The highest block used for lighting in the world. Also the block returned by {@link World#getHighestBlockYAt(int, int)}}
     */
    LIGHT_BLOCKING,

    /**
     * References the highest block in the world.
     */
    ANY,

    /**
     * References the highest solid block in a world.
     */
    SOLID,

    /**
     * References the highest solid or liquid block in a world.
     */
    SOLID_OR_LIQUID,

    /**
     * References the highest solid or liquid block in a world, excluding leaves.
     */
    SOLID_OR_LIQUID_NO_LEAVES;
}
