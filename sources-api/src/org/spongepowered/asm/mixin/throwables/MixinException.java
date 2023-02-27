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
package org.spongepowered.asm.mixin.throwables;

import org.spongepowered.asm.mixin.extensibility.IActivityContext;
import org.spongepowered.asm.mixin.transformer.ActivityStack;

/**
 * Base class for all mixin processor exceptions
 */
public class MixinException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String activityDescriptor;

    public MixinException(String message) {
        super(message);
    }

    public MixinException(String message, IActivityContext context) {
        super(message);
        this.activityDescriptor = context != null ? context.toString() : null;
    }

    public MixinException(Throwable cause) {
        super(cause);
    }

    public MixinException(Throwable cause, IActivityContext context) {
        super(cause);
        this.activityDescriptor = context != null ? context.toString() : null;
    }

    public MixinException(String message, Throwable cause) {
        super(message, cause);
    }

    public MixinException(String message, Throwable cause, IActivityContext context) {
        super(message, cause);
        this.activityDescriptor = context != null ? context.toString() : null;
    }
    
    /**
     * Decorate this exception with additional upstream activity context
     *  
     * @param upstreamContext Upstream activity stack to decorate
     */
    public void prepend(IActivityContext upstreamContext) {
        String strContext = upstreamContext.toString();
        this.activityDescriptor = this.activityDescriptor != null ? strContext + ActivityStack.GLUE_STRING + this.activityDescriptor
                : ActivityStack.GLUE_STRING + strContext;
    }
    
    @Override
    public String getMessage() {
        String message = super.getMessage();
        return this.activityDescriptor != null ? message + " [" +  this.activityDescriptor + "]" : message;
    }
    
}
