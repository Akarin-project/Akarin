package io.akarin.api.internal.mixin;

import net.minecraft.server.EnumDifficulty;

public interface IMixinWorldData {
    public void setDifficultyAsync(EnumDifficulty diff);
}