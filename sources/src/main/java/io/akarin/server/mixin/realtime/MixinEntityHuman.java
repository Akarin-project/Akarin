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
import net.minecraft.server.EntityHuman;

@Mixin(value = EntityHuman.class, remap = false)
public abstract class MixinEntityHuman {
    private static final String ENTITY_PLAYER_XP_COOLDOWN_FIELD = "Lnet/minecraft/entity/player/EntityHuman;bD:I"; // PUTFIELD: xpCooldown
    private static final String ENTITY_PLAYER_SLEEP_TIMER_FIELD = "Lnet/minecraft/entity/player/EntityHuman;sleepTicks:I";
    @Shadow public int bD;
    @Shadow private int sleepTicks;
    
    // OBFHELPER: onUpdate
    @Redirect(method = "B_()V", at = @At(value = "FIELD", target = ENTITY_PLAYER_XP_COOLDOWN_FIELD, opcode = Opcodes.PUTFIELD, ordinal = 0))
    public void fixupXpCooldown(EntityHuman self, int modifier) {
        int ticks = (int) ((IMixinRealTimeTicking) self.getWorld()).getRealTimeTicks();
        this.bD = Math.max(0, this.bD - ticks); // OBFHELPER: xpCooldown
    }

    @Redirect(method = "B_()V", at = @At(value = "FIELD", target = ENTITY_PLAYER_SLEEP_TIMER_FIELD, opcode = Opcodes.PUTFIELD, ordinal = 0))
    public void fixupSleepTimer(EntityHuman self, int modifier) {
        int ticks = (int) ((IMixinRealTimeTicking) self.getWorld()).getRealTimeTicks();
        this.sleepTicks += ticks;
    }

    @Redirect(method = "B_()V", at = @At(value = "FIELD", target = ENTITY_PLAYER_SLEEP_TIMER_FIELD, opcode = Opcodes.PUTFIELD, ordinal = 2))
    public void fixupWakeTimer(EntityHuman self, int modifier) {
        int ticks = (int) ((IMixinRealTimeTicking) self.getWorld()).getRealTimeTicks();
        this.sleepTicks += ticks;
    }
}
