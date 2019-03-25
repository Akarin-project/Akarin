package org.bukkit.event;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.util.*;
import java.util.Map.Entry;

/**
 * A list of event handlers, stored per-event. Based on lahwran's fevents.
 */
public class HandlerList {

    /**
     * Handler array. This field being an array is the key to this system's
     * speed.
     */
    private RegisteredListener[] handlers = null; // Akarin - remove volatile
    private boolean dirty = true; // Akarin

    /**
     * Dynamic handler lists. These are changed using register() and
     * unregister() and are automatically baked to the handlers array any time
     * they have changed.
     */
    private final EnumMap<EventPriority, ArrayList<RegisteredListener>> handlerslots;

    /**
     * List of all HandlerLists which have been created, for use in bakeAll()
     */
    private static ArrayList<HandlerList> allLists = new ArrayList<HandlerList>();

    /**
     * Bake all handler lists. Best used just after all normal event
     * registration is complete, ie just after all plugins are loaded if
     * you're using fevents in a plugin system.
     */
    public static void bakeAll() {
        synchronized (allLists) {
            for (HandlerList h : allLists) {
                h.bake();
            }
        }
    }

    /**
     * Unregister all listeners from all handler lists.
     */
    public static void unregisterAll() {
        synchronized (allLists) {
            for (HandlerList h : allLists) {
                synchronized (h) {
                    for (List<RegisteredListener> list : h.handlerslots.values()) {
                        list.clear();
                    }
                    h.dirty = true; // Akarin
                }
            }
        }
    }

    /**
     * Unregister a specific plugin's listeners from all handler lists.
     *
     * @param plugin plugin to unregister
     */
    public static void unregisterAll(Plugin plugin) {
        synchronized (allLists) {
            for (HandlerList h : allLists) {
                h.unregister(plugin);
            }
        }
    }

    /**
     * Unregister a specific listener from all handler lists.
     *
     * @param listener listener to unregister
     */
    public static void unregisterAll(Listener listener) {
        synchronized (allLists) {
            for (HandlerList h : allLists) {
                h.unregister(listener);
            }
        }
    }

    /**
     * Create a new handler list and initialize using EventPriority.
     * <p>
     * The HandlerList is then added to meta-list for use in bakeAll()
     */
    public HandlerList() {
        handlerslots = new EnumMap<EventPriority, ArrayList<RegisteredListener>>(EventPriority.class);
        for (EventPriority o : EventPriority.values()) {
            handlerslots.put(o, new ArrayList<RegisteredListener>());
        }
        synchronized (allLists) {
            allLists.add(this);
        }
    }

    /**
     * Register a new listener in this handler list
     *
     * @param listener listener to register
     */
    public synchronized void register(RegisteredListener listener) {
        if (handlerslots.get(listener.getPriority()).contains(listener))
            throw new IllegalStateException("This listener is already registered to priority " + listener.getPriority().toString());
        dirty = true; // Akarin
        handlerslots.get(listener.getPriority()).add(listener);
    }

    /**
     * Register a collection of new listeners in this handler list
     *
     * @param listeners listeners to register
     */
    public void registerAll(Collection<RegisteredListener> listeners) {
        for (RegisteredListener listener : listeners) {
            register(listener);
        }
    }

    /**
     * Remove a listener from a specific order slot
     *
     * @param listener listener to remove
     */
    public synchronized void unregister(RegisteredListener listener) {
        if (handlerslots.get(listener.getPriority()).remove(listener)) {
            dirty = true; // Akarin
        }
    }

    /**
     * Remove a specific plugin's listeners from this handler
     *
     * @param plugin plugin to remove
     */
    public synchronized void unregister(Plugin plugin) {
        boolean changed = false;
        for (List<RegisteredListener> list : handlerslots.values()) {
            for (ListIterator<RegisteredListener> i = list.listIterator(); i.hasNext();) {
                if (i.next().getPlugin().equals(plugin)) {
                    i.remove();
                    changed = true;
                }
            }
        }
        if (changed) dirty = true; // Akarin
    }

    /**
     * Remove a specific listener from this handler
     *
     * @param listener listener to remove
     */
    public synchronized void unregister(Listener listener) {
        boolean changed = false;
        for (List<RegisteredListener> list : handlerslots.values()) {
            for (ListIterator<RegisteredListener> i = list.listIterator(); i.hasNext();) {
                if (i.next().getListener().equals(listener)) {
                    i.remove();
                    changed = true;
                }
            }
        }
        if (changed) dirty = true; // Akarin
    }

    /**
     * Bake HashMap and ArrayLists to 2d array - does nothing if not necessary
     */
    public synchronized RegisteredListener[] bake() { // Akarin - add return value
        if (!dirty) return handlers; // don't re-bake when still valid // Akarin
        dirty = false; // Akarin - mark dirty to prevent any rebaking
        List<RegisteredListener> entries = new ArrayList<RegisteredListener>();
        for (Entry<EventPriority, ArrayList<RegisteredListener>> entry : handlerslots.entrySet()) {
            entries.addAll(entry.getValue());
        }
        return handlers = entries.toArray(new RegisteredListener[entries.size()]);
    }

    /**
     * Get the baked registered listeners associated with this handler list
     *
     * @return the array of registered listeners
     */
    public RegisteredListener[] getRegisteredListeners() {
        //RegisteredListener[] handlers; // Akarin
        //while ((handlers = this.handlers) == null) bake(); // This prevents fringe cases of returning null // Akarin
        return dirty ? bake() : handlers;
    }

    /**
     * Get a specific plugin's registered listeners associated with this
     * handler list
     *
     * @param plugin the plugin to get the listeners of
     * @return the list of registered listeners
     */
    public static ArrayList<RegisteredListener> getRegisteredListeners(Plugin plugin) {
        ArrayList<RegisteredListener> listeners = new ArrayList<RegisteredListener>();
        synchronized (allLists) {
            for (HandlerList h : allLists) {
                synchronized (h) {
                    for (List<RegisteredListener> list : h.handlerslots.values()) {
                        for (RegisteredListener listener : list) {
                            if (listener.getPlugin().equals(plugin)) {
                                listeners.add(listener);
                            }
                        }
                    }
                }
            }
        }
        return listeners;
    }

    /**
     * Get a list of all handler lists for every event type
     *
     * @return the list of all handler lists
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<HandlerList> getHandlerLists() {
        synchronized (allLists) {
            return (ArrayList<HandlerList>) allLists.clone();
        }
    }
}
