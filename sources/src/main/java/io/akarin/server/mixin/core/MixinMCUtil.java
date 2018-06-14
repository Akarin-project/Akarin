package io.akarin.server.mixin.core;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.server.MCUtil;

@Mixin(value = MCUtil.class, remap = false)
public abstract class MixinMCUtil {
    @Overwrite
    public static <T> T ensureMain(String reason, Supplier<T> run) {
        return run.get();
    }
}
