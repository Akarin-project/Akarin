package io.akarin.server.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.server.TileEntityEnchantTable;

@Mixin(value = TileEntityEnchantTable.class, remap = false)
public class MixinTileEntityEnchantTable {
    @Overwrite
    public void e() {} // No tickable
}
