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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.akarin.api.internal.mixin.IMixinRealTimeTicking;
import net.minecraft.server.EntityZombieVillager;

@Mixin(value = EntityZombieVillager.class, remap = false)
public abstract class MixinEntityZombieVillager {
    private static final String ENTITY_ZOMBIE_GET_CONVERSION_BOOST_METHOD = "Lnet/minecraft/entity/monster/EntityZombieVillager;du()I"; // INVOKE: getConversionProgress
    
    @Shadow(aliases = "du") protected abstract int getConversionProgress();
    
    // OBFHELPER: onUpdate
    @Redirect(method = "B_()V", at = @At(value = "INVOKE", target = ENTITY_ZOMBIE_GET_CONVERSION_BOOST_METHOD, ordinal = 0))
    public int fixupConversionTimeBoost(EntityZombieVillager self) {
        int ticks = (int) ((IMixinRealTimeTicking) self.getWorld()).getRealTimeTicks();
        return this.getConversionProgress() * ticks;
    }
}
