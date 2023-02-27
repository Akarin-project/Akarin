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
package org.spongepowered.tools.obfuscation.interfaces;

import java.util.Collection;

import javax.lang.model.element.TypeElement;

import org.spongepowered.asm.util.asm.IAnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;

/**
 * A mixin validator module, basically just a way of making the various sanity
 * checks modular
 */
public interface IMixinValidator {
    
    /**
     * Validation pass
     */
    public enum ValidationPass {
        EARLY,
        LATE,
        FINAL
    }
    
    /**
     * Validate all the things, return false to halt processing of further
     * validators. Raise compiler errors/warnings directly.
     * @param pass current validation pass
     * @param mixin Mixin being validated
     * @param annotation Mixin annotation
     * @param targets Mixin targets
     * 
     * @return False to halt processing of further validators
     */
    public abstract boolean validate(ValidationPass pass, TypeElement mixin, IAnnotationHandle annotation, Collection<TypeHandle> targets);

}
