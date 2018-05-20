package io.akarin.server.mixin.core.network;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.server.ServerConnection;

@Mixin(value = ServerConnection.class, remap = false)
public class NonblockingServerConnection {
    
}
