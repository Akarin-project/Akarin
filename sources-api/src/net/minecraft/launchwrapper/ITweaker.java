/*
 * This file is part of project Orion, licensed under the MIT License (MIT).
 *
 * Copyright (c) Original contributors ("I don't care" license? See https://github.com/Mojang/LegacyLauncher/issues/1)
 * Copyright (c) 2017-2018 Mark Vainomaa <mikroskeem@mikroskeem.eu>
 * Copyright (c) Contributors
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

package net.minecraft.launchwrapper;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * A Tweaker
 */
public interface ITweaker {
    /**
     * Parses application's parameters (which you'll supply to app on command line)
     *
     * @param args {@link List} of application's parameters
     */
    default void acceptOptions(@NotNull List<String> args) {
        acceptOptions(args, null, null, null);
    }

    /**
     * Old {@code acceptOptions} method retained for compatibility reasons.
     *
     * @param args Application's arguments
     * @param gameDir unknown
     * @param assetsDir unknown
     * @param profile unknown
     * @deprecated This method is not used internally. See {@link ITweaker#acceptOptions(List)}
     */
    @Deprecated
    default void acceptOptions(@NotNull List<String> args, File gameDir, final File assetsDir, String profile) {
        throw new IllegalStateException("Please implement this method.");
    }

    /**
     * Asks tweaker for transformers and other options
     *
     * @param classLoader Current {@link LaunchClassLoader} instance
     */
    void injectIntoClassLoader(@NotNull LaunchClassLoader classLoader);

    /**
     * Gets arguments which will be added to endpoint's {@code main(String[])}
     *
     * @return String array of arguments
     */
    @NotNull
    String[] getLaunchArguments();

    /**
     * Gets main endpoint class to pass arguments to and start the wrapped application. Only used
     * for main tweaker.
     *
     * @return Endpoint class name
     */
    @NotNull
    String getLaunchTarget();
}
