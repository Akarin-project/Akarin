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
import net.minecraft.server.TileEntity;
import net.minecraft.server.TileEntityBrewingStand;

@Mixin(value = TileEntityBrewingStand.class, remap = false)
public abstract class MixinTileEntityBrewingStand extends TileEntity {
    private static final String BREWING_STAND_BREW_TIME_FIELD = "Lnet/minecraft/tileentity/TileEntityBrewingStand;brewTime:I";
    @Shadow private int brewTime;
    
    // OBFHELPER: update
    @Redirect(method = "e()V", at = @At(value = "FIELD", target = BREWING_STAND_BREW_TIME_FIELD, opcode = Opcodes.PUTFIELD, ordinal = 0))
    public void fixupBrewTime(TileEntityBrewingStand self, int modifier) {
        int ticks = (int) ((IMixinRealTimeTicking) this.getWorld()).getRealTimeTicks();
        this.brewTime = Math.max(0, this.brewTime - ticks);
    }
}
