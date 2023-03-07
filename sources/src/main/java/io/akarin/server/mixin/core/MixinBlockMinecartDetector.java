package io.akarin.server.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.BlockMinecartDetector;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.IBlockData;
import net.minecraft.server.World;

@Mixin(value = BlockMinecartDetector.class, remap = false)
public abstract class MixinBlockMinecartDetector {
	@Inject(at = @At("HEAD"), method = "e", cancellable = true)
	private void e(World world, BlockPosition blockposition, IBlockData iblockdata, CallbackInfo ci) {
		if (iblockdata.getBlock() != (Object)this) ci.cancel();
	}
}
