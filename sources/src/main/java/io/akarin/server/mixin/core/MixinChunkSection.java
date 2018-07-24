package io.akarin.server.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.akarin.server.core.AkarinGlobalConfig;
import net.minecraft.server.ChunkSection;

@Mixin(value = ChunkSection.class, remap = false)
public abstract class MixinChunkSection {
    @Shadow private int nonEmptyBlockCount;
    
    @Overwrite // OBFHELPER: isEmpty
    public boolean a() {
        return AkarinGlobalConfig.sendLightOnlyChunkSection ? false : nonEmptyBlockCount == 0;
    }
}
