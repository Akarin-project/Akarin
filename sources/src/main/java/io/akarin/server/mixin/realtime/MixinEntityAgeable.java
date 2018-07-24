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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import io.akarin.api.internal.mixin.IMixinRealTimeTicking;
import net.minecraft.server.EntityAgeable;

@Mixin(value = EntityAgeable.class, remap = false)
public abstract class MixinEntityAgeable {
    private static final String ENTITY_AGEABLE_SET_GROWING_AGE_METHOD = "Lnet/minecraft/entity/EntityAgeable;setAgeRaw(I)V";
    
    // OBFHELPER: onLivingUpdate
    @Redirect(method = "n()V", at = @At(value = "INVOKE", target = ENTITY_AGEABLE_SET_GROWING_AGE_METHOD, ordinal = 0))
    public void fixupGrowingUp(EntityAgeable self, int age) {
        // Subtract the one the original update method added
        int diff = (int) ((IMixinRealTimeTicking) self.getWorld()).getRealTimeTicks() - 1;
        self.setAgeRaw(Math.min(0, age + diff));
    }
    
    @Redirect(method = "n()V", at = @At(value = "INVOKE", target = ENTITY_AGEABLE_SET_GROWING_AGE_METHOD, ordinal = 1))
    public void fixupBreedingCooldown(EntityAgeable self, int age) {
        // Subtract the one the original update method added
        int diff = (int) ((IMixinRealTimeTicking) self.getWorld()).getRealTimeTicks() - 1;
        self.setAgeRaw(Math.max(0, age - diff));
    }
}
