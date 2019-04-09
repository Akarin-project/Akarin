package com.destroystokyo.paper.event.executor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import com.destroystokyo.paper.util.SneakyThrow;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

public class MethodHandleEventExecutor implements EventExecutor {
    private final Class<? extends Event> eventClass;
    private final MethodHandle handle;

    public MethodHandleEventExecutor(@NotNull Class<? extends Event> eventClass, @NotNull MethodHandle handle) {
        this.eventClass = eventClass;
        this.handle = handle;
    }

    public MethodHandleEventExecutor(@NotNull Class<? extends Event> eventClass, @NotNull Method m) {
        this.eventClass = eventClass;
        try {
            m.setAccessible(true);
            this.handle = MethodHandles.lookup().unreflect(m);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Unable to set accessible", e);
        }
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {
        if (!eventClass.isInstance(event)) return;
        try {
            handle.invoke(listener, event);
        } catch (Throwable t) {
            SneakyThrow.sneaky(t);
        }
    }
}
