package io.akarin.server.mixin.realtime;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import io.akarin.api.internal.mixin.IMixinRealTimeTicking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.World;

@Mixin(value = World.class, remap = false, priority = 1002)
public abstract class MixinWorld implements IMixinRealTimeTicking {
    @Shadow @Nullable public abstract MinecraftServer getMinecraftServer();

    @Override
    public long getRealTimeTicks() {
        if (this.getMinecraftServer() != null) {
            return ((IMixinRealTimeTicking) this.getMinecraftServer()).getRealTimeTicks();
        }
        return 1;
    }
}
