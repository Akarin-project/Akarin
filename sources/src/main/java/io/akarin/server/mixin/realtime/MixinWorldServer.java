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

import org.bukkit.World.Environment;
import org.bukkit.generator.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.akarin.api.internal.mixin.IMixinRealTimeTicking;
import net.minecraft.server.IDataManager;
import net.minecraft.server.MethodProfiler;
import net.minecraft.server.World;
import net.minecraft.server.WorldData;
import net.minecraft.server.WorldProvider;
import net.minecraft.server.WorldServer;

@Mixin(value = WorldServer.class, remap = false, priority = 1001)
public abstract class MixinWorldServer extends World implements IMixinRealTimeTicking {

    protected MixinWorldServer(IDataManager idatamanager, WorldData worlddata, WorldProvider worldprovider, MethodProfiler methodprofiler, boolean flag, ChunkGenerator gen, Environment env) {
        super(idatamanager, worlddata, worldprovider, methodprofiler, flag, gen, env);
    }
    
    @Inject(method = "doTick()V", at = @At("HEAD"))
    public void fixTimeOfDay(CallbackInfo ci) {
        if (this.getGameRules().getBoolean("doDaylightCycle")) {
            // Subtract the one the original tick method is going to add
            long diff = this.getRealTimeTicks() - 1;
            // Don't set if we're not changing it as other mods might be listening for changes
            if (diff > 0) {
                this.worldData.setDayTime(this.worldData.getDayTime() + diff);
            }
        }
    }
    
}
