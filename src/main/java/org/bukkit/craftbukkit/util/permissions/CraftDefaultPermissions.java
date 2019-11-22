package org.bukkit.craftbukkit.util.permissions;

import org.bukkit.permissions.Permission;
import org.bukkit.util.permissions.DefaultPermissions;

public final class CraftDefaultPermissions {
    private static final String ROOT= "minecraft";

    private CraftDefaultPermissions() {}

    public static void registerCorePermissions() {
        Permission parent = DefaultPermissions.registerPermission(ROOT, "Gives the user the ability to use all vanilla utilities and commands");
        CommandPermissions.registerPermissions(parent);
        // Spigot start
        DefaultPermissions.registerPermission(ROOT + ".nbt.place", "Gives the user the ability to place restricted blocks with NBT in creative", org.bukkit.permissions.PermissionDefault.OP, parent);
        DefaultPermissions.registerPermission(ROOT + ".nbt.copy", "Gives the user the ability to copy NBT in creative", org.bukkit.permissions.PermissionDefault.TRUE, parent);
        DefaultPermissions.registerPermission(ROOT + ".debugstick", "Gives the user the ability to use the debug stick creative", org.bukkit.permissions.PermissionDefault.OP, parent);
        // Spigot end
        parent.recalculatePermissibles();
    }
}
