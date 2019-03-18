/*
 * This file is licensed under the MIT License (MIT).
 *
 * Copyright (c) 2014 Daniel Ennis <http://aikar.co>
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
package co.aikar.timings;

import co.aikar.util.LoadingIntMap;
import io.akarin.server.core.AkarinGlobalConfig;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.bukkit.Bukkit;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

class TimingHandler implements Timing {

    private static AtomicInteger idPool = new AtomicInteger(1);
    final int id = idPool.getAndIncrement();

    final String name;
    private final boolean verbose;

    private final Int2ObjectOpenHashMap<TimingData> children = new LoadingIntMap<>(TimingData::new);

    final TimingData record;
    private final TimingHandler groupHandler;

    private long start = 0;
    private int timingDepth = 0;
    private boolean added;
    private boolean timed;
    private boolean enabled;
    private TimingHandler parent;

    TimingHandler(TimingIdentifier id) {
        if (id.name.startsWith("##")) {
            verbose = true;
            this.name = id.name.substring(3);
        } else {
            this.name = id.name;
            verbose = false;
        }

        this.record = new TimingData(this.id);
        this.groupHandler = id.groupHandler;

        TimingIdentifier.getGroup(id.group).handlers.add(this);
        checkEnabled();
    }

    final void checkEnabled() {
        enabled = Timings.timingsEnabled && (!verbose || Timings.verboseEnabled);
    }

    void processTick(boolean violated) {
        if (timingDepth != 0 || record.getCurTickCount() == 0) {
            timingDepth = 0;
            start = 0;
            return;
        }

        record.processTick(violated);
        for (TimingData handler : children.values()) {
            handler.processTick(violated);
        }
    }

    @Override
    // Akarin start
    public Timing startTimingIfSync() {
        return startTiming(false);
    }
    @Override
    // Akarin end
    public Timing startTimingIfSync(boolean assertThread) {
        startTiming(assertThread);
        return this;
    }

    @Override
    public void stopTimingIfSync() {
        stopTiming();
    }
    
    // Akarin start
    @Override
    public Timing startTiming() {
        return startTiming(false);
    }

    @Override
    public Timing startTiming(boolean assertThread) {
        if (enabled && (ThreadAssertion.isMainThread() || Bukkit.isPrimaryThread()) /*&& ++timingDepth == 1*/) {
            if (AkarinGlobalConfig.lazyThreadAssertion && assertThread) ThreadAssertion.setMainThread(true);
            if (++timingDepth != 1) return this;
            // Akarin end
            start = System.nanoTime();
            parent = TimingsManager.CURRENT;
            TimingsManager.CURRENT = this;
        }
        return this;
    }

    @Override
    public void stopTiming() {
        // Akarin start
        if (enabled && timingDepth > 0 && (ThreadAssertion.isMainThread() || Bukkit.isPrimaryThread()) /*&& --timingDepth == 0 && start != 0*/) {
            if (AkarinGlobalConfig.lazyThreadAssertion) ThreadAssertion.setMainThread(false);
            if (--timingDepth != 0 || start == 0) return;
            // Akarin end
            addDiff(System.nanoTime() - start);
            start = 0;
        }
    }

    @Override
    public void abort() {
        if (enabled && timingDepth > 0) {
            start = 0;
        }
    }

    void addDiff(long diff) {
        if (TimingsManager.CURRENT == this) {
            TimingsManager.CURRENT = parent;
            if (parent != null) {
                parent.children.get(id).add(diff);
            }
        }
        record.add(diff);
        if (!added) {
            added = true;
            timed = true;
            TimingsManager.HANDLERS.add(this);
        }
        if (groupHandler != null) {
            groupHandler.addDiff(diff);
            groupHandler.children.get(id).add(diff);
        }
    }

    /**
     * Reset this timer, setting all values to zero.
     *
     * @param full
     */
    void reset(boolean full) {
        record.reset();
        if (full) {
            timed = false;
        }
        start = 0;
        timingDepth = 0;
        added = false;
        children.clear();
        checkEnabled();
    }

    @Override
    public TimingHandler getTimingHandler() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return (this == o);
    }

    @Override
    public int hashCode() {
        return id;
    }

    /**
     * This is simply for the Closeable interface so it can be used with
     * try-with-resources ()
     */
    @Override
    public void close() {
        stopTimingIfSync();
    }

    public boolean isSpecial() {
        return this == TimingsManager.FULL_SERVER_TICK || this == TimingsManager.TIMINGS_TICK;
    }

    boolean isTimed() {
        return timed;
    }

    public boolean isEnabled() {
        return enabled;
    }

    TimingData[] cloneChildren() {
        final TimingData[] clonedChildren = new TimingData[children.size()];
        int i = 0;
        for (TimingData child : children.values()) {
            clonedChildren[i++] = child.clone();
        }
        return clonedChildren;
    }
}
