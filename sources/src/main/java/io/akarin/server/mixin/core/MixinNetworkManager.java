package io.akarin.server.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.server.NetworkManager;

@Mixin(value = NetworkManager.class, remap = false)
public class MixinNetworkManager {
    
}
