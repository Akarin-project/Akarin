package io.akarin.server.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.TileEntityLootable;

@Mixin(value = TileEntityLootable.class, remap = false)
public abstract class MixinTileEntityLootable {
	@Inject(at = @At("HEAD"), method = "b(Lnet/minecraft/server/EntityHuman;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V", cancellable = true)
	private void b(EntityHuman entityhuman, CallbackInfo ci) {
		if (entityhuman == null) ci.cancel();
	}
}
