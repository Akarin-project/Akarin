package org.bukkit.event.vehicle;

import org.bukkit.World;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import io.akarin.server.api.event.WorldAttachedEvent;

/**
 * Represents a vehicle-related event.
 */
public abstract class VehicleEvent extends Event implements WorldAttachedEvent { // Akarin
    protected Vehicle vehicle;
    @Override @NotNull public World getWorld() { return vehicle.getWorld(); } // Akarin

    public VehicleEvent(@NotNull final Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    /**
     * Get the vehicle.
     *
     * @return the vehicle
     */
    @NotNull
    public final Vehicle getVehicle() {
        return vehicle;
    }
}
