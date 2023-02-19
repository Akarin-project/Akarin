package io.akarin.server.mixin.optimization;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.server.BlockFluids;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.BlockStationary;
import net.minecraft.server.IBlockData;
import net.minecraft.server.Material;
import net.minecraft.server.World;

@Mixin(value = BlockStationary.class, remap = false)
public abstract class MixinBlockStationary extends BlockFluids {
    protected MixinBlockStationary(Material material) {
		super(material);
	}
    @Overwrite
	private boolean d(World world, BlockPosition blockposition) {
        if (blockposition.getY() >= 0 && blockposition.getY() < 256) {
            IBlockData blockData = world.getTypeIfLoaded(blockposition);

            if (blockData != null) {
                return blockData.getMaterial().isBurnable();
            }
        }

        return false;
    }
}
