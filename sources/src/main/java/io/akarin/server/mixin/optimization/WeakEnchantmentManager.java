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
package io.akarin.server.mixin.optimization;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.DamageSource;
import net.minecraft.server.EnchantmentManager;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.ItemStack;

/**
 * Fixes MC-128547(https://bugs.mojang.com/browse/MC-128547)
 */
@Mixin(value = EnchantmentManager.class, remap = false)
public class WeakEnchantmentManager {
    @Shadow(aliases = "a") private static void applyEnchantmentModifierArray(EnchantmentManager.EnchantmentModifier modifier, Iterable<ItemStack> iterable) {}
    @Shadow(aliases = "a") private static void applyEnchantmentModifier(EnchantmentManager.EnchantmentModifier modifier, ItemStack itemstack) {}
    
    @Overwrite
    public static int a(Iterable<ItemStack> iterable, DamageSource damageSource) {
        EnchantmentManager.a.a = 0; // PAIL: damageModifier
        EnchantmentManager.a.b = damageSource;
        applyEnchantmentModifierArray(EnchantmentManager.a, iterable);
        EnchantmentManager.a.b = null; // Akarin - Remove reference to Damagesource
        return EnchantmentManager.a.a;
    }
    
    @Overwrite
    public static void a(EntityLiving user, Entity attacker) { // PAIL: applyThornEnchantments
        EnchantmentManager.c.b = attacker;
        EnchantmentManager.c.a = user;
        if (user != null) {
            applyEnchantmentModifierArray(EnchantmentManager.c, user.aQ()); // PAIL: getEquipmentAndArmor
        }
        
        if (attacker instanceof EntityHuman) {
            applyEnchantmentModifier(EnchantmentManager.c, user.getItemInMainHand());
        }
        
        // Akarin Start - remove references to entity objects to avoid memory leaks
        EnchantmentManager.c.b = null;
        EnchantmentManager.c.a = null;
        // Akarin end
    }

    @Overwrite
    public static void b(EntityLiving user, Entity target) { // PAIL: applyArthropodEnchantments
        EnchantmentManager.d.a = user;
        EnchantmentManager.d.b = target;
        if (user != null) {
            applyEnchantmentModifierArray(EnchantmentManager.d, user.aQ()); // PAIL: getEquipmentAndArmor
        }
        
        if (user instanceof EntityHuman) {
            applyEnchantmentModifier(EnchantmentManager.d, user.getItemInMainHand());
        }
        
        // Akarin Start - remove references to entity objects to avoid memory leaks
        EnchantmentManager.d.a = null;
        EnchantmentManager.d.b = null;
        // Akarin end
    }
}
