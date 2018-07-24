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
import net.minecraft.server.MathHelper;
import net.minecraft.server.TileEntity;
import net.minecraft.server.TileEntityFurnace;

@Mixin(value = TileEntityFurnace.class, remap = false)
public abstract class MixinTileEntityFurnace extends TileEntity {
    private static final String FURNACE_BURN_TIME_FIELD = "Lnet/minecraft/tileentity/TileEntityFurnace;burnTime:I";
    private static final String FURNACE_COOK_TIME_FIELD = "Lnet/minecraft/tileentity/TileEntityFurnace;cookTime:I";
    @Shadow private int burnTime;
    @Shadow private int cookTime;
    @Shadow private int cookTimeTotal;
    
    // OBFHELPER: update
    @Redirect(method = "e()V", at = @At(value = "FIELD", target = FURNACE_BURN_TIME_FIELD, opcode = Opcodes.PUTFIELD, ordinal = 0))
    public void fixupBurnTime(TileEntityFurnace self, int modifier) {
        int ticks = (int) ((IMixinRealTimeTicking) this.getWorld()).getRealTimeTicks();
        this.burnTime = Math.max(0, this.burnTime - ticks);
    }
    
    @Redirect(method = "e()V", at = @At(value = "FIELD", target = FURNACE_COOK_TIME_FIELD, opcode = Opcodes.PUTFIELD, ordinal = 0))
    public void fixupCookTime(TileEntityFurnace self, int modifier) {
        int ticks = (int) ((IMixinRealTimeTicking) this.getWorld()).getRealTimeTicks();
        this.cookTime = Math.min(this.cookTimeTotal, this.cookTime + ticks);
    }
    
    @Redirect(method = "e()V", at = @At(value = "FIELD", target = FURNACE_COOK_TIME_FIELD, opcode = Opcodes.PUTFIELD, ordinal = 3))
    public void fixupCookTimeCooldown(TileEntityFurnace self, int modifier) {
        int ticks = (int) ((IMixinRealTimeTicking) this.getWorld()).getRealTimeTicks();
        this.cookTime = MathHelper.clamp(this.cookTime - (2 * ticks), 0, this.cookTimeTotal);
    }
}
