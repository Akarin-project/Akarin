package io.akarin.server.mixin.optimization;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.server.TileEntityEnchantTable;

@Mixin(value = TileEntityEnchantTable.class, remap = false)
public abstract class MixinTileEntityEnchantTable {
    @Overwrite
    public void e() {} // No tickable
}
