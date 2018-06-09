package io.akarin.server.mixin.core;

import java.util.UUID;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.google.common.base.Optional;

import net.minecraft.server.DataWatcherObject;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHorseAbstract;
import net.minecraft.server.World;

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
@Mixin(value = EntityHorseAbstract.class, remap = false)
public abstract class MixinEntityHorseAbstract extends Entity {
    @Shadow @Final private static DataWatcherObject<Optional<UUID>> bJ;
    
    @Nullable private Optional<UUID> cachedOwnerId;
    
    @Overwrite
    @Nullable public UUID getOwnerUUID() {
        if (cachedOwnerId == null) cachedOwnerId = datawatcher.get(bJ);
        return cachedOwnerId.orNull();
    }
    
    @Overwrite
    public void setOwnerUUID(@Nullable UUID uuid) {
        cachedOwnerId = Optional.fromNullable(uuid);
        datawatcher.set(bJ, cachedOwnerId);
    }
    
    /**
     * Extends from superclass
     * @param world
     */
    public MixinEntityHorseAbstract(World world) {
        super(world);
    }
}
