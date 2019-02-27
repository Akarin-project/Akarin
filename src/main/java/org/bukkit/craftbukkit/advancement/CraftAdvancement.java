package org.bukkit.craftbukkit.advancement;

import java.util.Collection;
import java.util.Collections;
import net.minecraft.server.Advancement;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

public class CraftAdvancement implements org.bukkit.advancement.Advancement {

    private final Advancement handle;

    public CraftAdvancement(Advancement handle) {
        this.handle = handle;
    }

    public Advancement getHandle() {
        return handle;
    }

    @Override
    public NamespacedKey getKey() {
        return CraftNamespacedKey.fromMinecraft(handle.getName());
    }

    @Override
    public Collection<String> getCriteria() {
        return Collections.unmodifiableCollection(handle.getCriteria().keySet());
    }
}
