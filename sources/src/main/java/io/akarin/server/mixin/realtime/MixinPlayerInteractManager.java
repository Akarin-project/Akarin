/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.akarin.server.mixin.realtime;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.akarin.api.internal.mixin.IMixinRealTimeTicking;
import net.minecraft.server.PlayerInteractManager;
import net.minecraft.server.World;

@Mixin(value = PlayerInteractManager.class, remap = false)
public abstract class MixinPlayerInteractManager {
    private static final String PLAYER_INTERACTION_BLOCK_DAMAGE_FIELD = "Lnet/minecraft/server/management/PlayerInteractManager;currentTick:I";
    @Shadow public World world;
    @Shadow private int currentTick;
    
    // OBFHELPER: updateBlockRemoving
    @Redirect(method = "a()V", at = @At(value = "FIELD", target = PLAYER_INTERACTION_BLOCK_DAMAGE_FIELD, opcode = Opcodes.PUTFIELD, ordinal = 0))
    public void fixupDiggingTime(PlayerInteractManager self, int modifier) {
        int ticks = (int) ((IMixinRealTimeTicking) this.world.getMinecraftServer()).getRealTimeTicks();
        this.currentTick += ticks;
    }
}
