package io.akarin.server.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import io.akarin.api.internal.mixin.IMixinLockProvider;
import net.minecraft.server.WorldServer;

@Mixin(value = WorldServer.class, remap = false)
public abstract class MixinWorldServer implements IMixinLockProvider {
    private final Object tickLock = new Object();

    @Override
    public Object lock() {
        return tickLock;
    }
}
