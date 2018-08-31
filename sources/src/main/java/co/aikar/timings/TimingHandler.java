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
import io.akarin.api.internal.Akari;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import org.bukkit.Bukkit;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Akarin Changes Note
 * 1) Thread safe timing (safety)
 */
class TimingHandler implements Timing {

    private static AtomicInteger idPool = new AtomicInteger(1);
    final int id = idPool.getAndIncrement();

    final String name;
    private final boolean verbose;

    private final Int2ObjectOpenHashMap<TimingData> children = new LoadingIntMap<>(TimingData::new);

    final TimingData record;
    private final TimingHandler groupHandler;

    private AtomicLong start = new AtomicLong(); // Akarin
    private AtomicInteger timingDepth = new AtomicInteger(); // Akarin
    private volatile boolean added; // Akarin
    private boolean timed;
    private boolean enabled;
    private volatile TimingHandler parent; // Akarin

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
        if (timingDepth.get() != 0 || record.getCurTickCount() == 0) {
            timingDepth.set(0);
            start.set(0);
            return;
        }

        record.processTick(violated);
        Akari.timingsLock.lock(); // Akarin
        for (TimingData handler : children.values()) {
            handler.processTick(violated);
        }
        Akari.timingsLock.unlock(); // Akarin
    }

    @Override
    public Timing startTimingIfSync() {
        startTiming();
        return this;
    }

    @Override
    public void stopTimingIfSync() {
        stopTiming();
    }

    public Timing startTiming() {
        if (enabled && Bukkit.isPrimaryThread() && timingDepth.incrementAndGet() == 1) {
            start.getAndSet(System.nanoTime());
            parent = TimingsManager.CURRENT;
            TimingsManager.CURRENT = this;
        }
        return this;
    }

    public void stopTiming() {
        if (enabled && start.get() != 0 && Bukkit.isPrimaryThread() && timingDepth.decrementAndGet() == 0) { // Akarin - change order
            long prev = start.getAndSet(0); // Akarin
            addDiff(System.nanoTime() - prev); // Akarin
        }
    }

    @Override
    public void abort() {
        if (enabled && timingDepth.get() > 0) {
            start.getAndSet(0);
        }
    }

    void addDiff(long diff) {
        if (TimingsManager.CURRENT == this) {
            TimingsManager.CURRENT = parent;
            if (parent != null) {
                Akari.timingsLock.lock(); // Akarin
                parent.children.get(id).add(diff);
                Akari.timingsLock.unlock(); // Akarin
            }
        }
        record.add(diff);
        if (!added) {
            added = true;
            timed = true;
            Akari.timingsLock.lock(); // Akarin
            TimingsManager.HANDLERS.add(this);
            Akari.timingsLock.unlock(); // Akarin
        }
        if (groupHandler != null) {
            groupHandler.addDiff(diff);
            Akari.timingsLock.lock(); // Akarin
            groupHandler.children.get(id).add(diff);
            Akari.timingsLock.unlock(); // Akarin
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
        start.set(0);
        timingDepth.set(0);
        added = false;
        Akari.timingsLock.lock(); // Akarin
        children.clear();
        Akari.timingsLock.unlock(); // Akarin
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
        int i = 0;
        Akari.timingsLock.lock(); // Akarin
        final TimingData[] clonedChildren = new TimingData[children.size()];
        for (TimingData child : children.values()) {
            clonedChildren[i++] = child.clone();
        }
        Akari.timingsLock.unlock(); // Akarin
        return clonedChildren;
    }
}
