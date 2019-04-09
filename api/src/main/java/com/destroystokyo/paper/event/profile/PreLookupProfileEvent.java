package com.destroystokyo.paper.event.profile;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.collect.ArrayListMultimap;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows a plugin to intercept a Profile Lookup for a Profile by name
 *
 * At the point of event fire, the UUID and properties are unset.
 *
 * If a plugin sets the UUID, and optionally the properties, the API call to look up the profile may be skipped.
 *
 * No guarantees are made about thread execution context for this event. If you need to know, check
 * event.isAsync()
 */
public class PreLookupProfileEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @NotNull private final String name;
    private UUID uuid;
    @NotNull private Set<ProfileProperty> properties = new HashSet<>();

    public PreLookupProfileEvent(@NotNull String name) {
        super(!Bukkit.isPrimaryThread());
        this.name = name;
    }

    /**
     * @return Name of the profile
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * If this value is left null by the completion of the event call, then the server will
     * trigger a call to the Mojang API to look up the UUID (Network Request), and subsequently, fire a
     * {@link LookupProfileEvent}
     *
     * @return The UUID of the profile if it has already been provided by a plugin
     */
    @Nullable
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Sets the UUID for this player name. This will skip the initial API call to find the players UUID.
     *
     * However, if Profile Properties are needed by the server, you must also set them or else an API call might still be made.
     *
     * @param uuid the UUID to set for the profile or null to reset
     */
    public void setUUID(@Nullable UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * @return The currently pending prepopulated properties.
     * Any property in this Set will be automatically prefilled on this Profile
     */
    @NotNull
    public Set<ProfileProperty> getProfileProperties() {
        return this.properties;
    }

    /**
     * Clears any existing prepopulated properties and uses the supplied properties
     * Any property in this Set will be automatically prefilled on this Profile
     * @param properties The properties to add
     */
    public void setProfileProperties(@NotNull Set<ProfileProperty> properties) {
        this.properties = new HashSet<>();
        this.properties.addAll(properties);
    }

    /**
     * Adds any properties currently missing to the prepopulated properties set, replacing any that already were set.
     * Any property in this Set will be automatically prefilled on this Profile
     * @param properties The properties to add
     */
    public void addProfileProperties(@NotNull Set<ProfileProperty> properties) {
        this.properties.addAll(properties);
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
