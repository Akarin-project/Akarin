package io.akarin.server.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.BlockPosition;
import net.minecraft.server.WorldBorder;

@Mixin(value = WorldBorder.class, remap = false)
public abstract class MixinWorldBorder {
	@Shadow
	public abstract boolean isInBounds(BlockPosition blockposition);

	@Overwrite
	public boolean isBlockInBounds(int chunkX, int chunkZ) {
		BlockPosition.MutableBlockPosition mutPos = new BlockPosition.MutableBlockPosition(); // Dionysus - avoid collisions with other threads
		mutPos.setValues(chunkX, 64, chunkZ);
		return isInBounds(mutPos);
	}

	@Overwrite
	public boolean isChunkInBounds(int chunkX, int chunkZ) {
		BlockPosition.MutableBlockPosition mutPos = new BlockPosition.MutableBlockPosition(); // Dionysus - avoid collisions with other threads																			// threads
		mutPos.setValues(((chunkX << 4) + 15), 64, (chunkZ << 4) + 15);
		return isInBounds(mutPos);
	}

}
