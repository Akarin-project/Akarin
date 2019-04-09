package com.destroystokyo.paper.event.executor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.destroystokyo.paper.util.SneakyThrow;
import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

public class StaticMethodHandleEventExecutor implements EventExecutor {
    private final Class<? extends Event> eventClass;
    private final MethodHandle handle;

    public StaticMethodHandleEventExecutor(@NotNull Class<? extends Event> eventClass, @NotNull Method m) {
        Preconditions.checkArgument(Modifier.isStatic(m.getModifiers()), "Not a static method: %s", m);
        Preconditions.checkArgument(eventClass != null, "eventClass is null");
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
            handle.invoke(event);
        } catch (Throwable throwable) {
            SneakyThrow.sneaky(throwable);
        }
    }
}
