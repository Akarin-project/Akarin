/*
 * This file is part of Mixin, licensed under the MIT License (MIT).
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
package org.spongepowered.asm.mixin.transformer;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;

/**
 * Passthrough coprocessor which simply keeps track of classes which are
 * loadable.
 */
class MixinCoprocessorPassthrough extends MixinCoprocessor {
    
    /**
     * Loadable classes within mixin packages
     */
    private final Set<String> loadable = new HashSet<String>();

    MixinCoprocessorPassthrough() {
    }
    
    @Override
    String getName() {
        return "passthrough";
    }

    @Override
    public void onPrepare(MixinInfo mixin) {
        if (mixin.isLoadable()) {
            this.registerLoadable(mixin.getClassName());
        }
    }

    void registerLoadable(String className) {
        this.loadable.add(className);
    }
    
    @Override
    ProcessResult process(String className, ClassNode classNode) {
        return this.loadable.contains(className) ? ProcessResult.PASSTHROUGH_NONE : ProcessResult.NONE;
    }

}
