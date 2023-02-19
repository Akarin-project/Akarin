package io.akarin.server.mixin.optimization;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.Container;
import net.minecraft.server.ContainerHorse;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHorseAbstract;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;

@Mixin(value = ContainerHorse.class, remap = false)
public abstract class MixinContainerHorse extends Container {
	@Shadow private IInventory a;
	@Shadow private EntityHorseAbstract f;
	
	@Overwrite
    public boolean canUse(EntityHuman entityhuman) {
		return this.a.a(entityhuman) && this.f.isAlive() && this.f.valid && this.f.g((Entity) entityhuman) < 8.0F; // NeonPaper! - Fix MC-161754
    }
}
