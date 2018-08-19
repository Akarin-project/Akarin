package io.akarin.server.mixin.core;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.akarin.api.internal.mixin.IMixinWorldServer;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.WorldManager;
import net.minecraft.server.WorldServer;

@Mixin(value = WorldManager.class, remap = false)
public abstract class MixinWorldManager {
    @Shadow @Final private WorldServer world;
    
    @Overwrite
    public void a(Entity entity) {
        synchronized (((IMixinWorldServer) this.world).trackLock()) { // Akarin
            this.world.getTracker().track(entity);
        }
        
        if (entity instanceof EntityPlayer) {
            this.world.worldProvider.a((EntityPlayer) entity);
        }
    }
}