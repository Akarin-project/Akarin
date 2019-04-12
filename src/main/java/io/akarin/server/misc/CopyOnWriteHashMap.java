package io.akarin.server.misc;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A thread-safe version of {@link Map} in which all operations that change the
 * Map are implemented by making a new copy of the underlying Map.
 * <p/>
 * While the creation of a new Map can be expensive, this class is designed for
 * cases in which the primary function is to read data from the Map, not to
 * modify the Map.  Therefore the operations that do not cause a change to this
 * class happen quickly and concurrently.
 *
 * @author <a href="mailto:Kuzma.Deretuke@gmail.com">Kuzma Deretuke</a>
 */
public class CopyOnWriteHashMap<K, V> implements Map<K, V>, Serializable, Cloneable {
    private static final long serialVersionUID = 5481095911554321115L;
    private AtomicReference<Map<K, V>> internalMap = new AtomicReference<Map<K, V>>();

    /**
     * Creates a new instance of CopyOnWriteHashMap.
     */
    public CopyOnWriteHashMap() {
        internalMap.set(new HashMap<K, V>());
    }

    /**
     * Creates a new instance of CopyOnWriteHashMap with the specified initial size.
     *
     * @param initialCapacity The initial size of the Map.
     */
    public CopyOnWriteHashMap(int initialCapacity) {
        internalMap.set(new HashMap<K, V>(initialCapacity));
    }

    /**
     * Creates a new instance of CopyOnWriteHashMap in which the initial data,
     * being held by this map, is contained in the supplied map.
     *
     * @param data A Map containing the initial contents to be placed into this class.
     */
    public CopyOnWriteHashMap(Map<K, V> data) {
        internalMap.set(new HashMap<K, V>(data));
    }

    @Override
    public V put(K key, V value) {
        Map<K, V> oldMap;
        Map<K, V> newMap;
        V val;
        do {
            oldMap = internalMap.get();
            newMap = new HashMap<K, V>(oldMap);
            val = newMap.put(key, value);
        }
        while (!internalMap.compareAndSet(oldMap, newMap));
        return val;
    }

    @Override
    public V remove(Object key) {
        Map<K, V> oldMap;
        Map<K, V> newMap;
        V val;
        do {
            oldMap = internalMap.get();
            newMap = new HashMap<K, V>(oldMap);
            val = newMap.remove(key);
        }
        while (!internalMap.compareAndSet(oldMap, newMap));
        return val;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> newData) {
        Map<K, V> oldMap;
        Map<K, V> newMap;
        do {
            oldMap = internalMap.get();
            newMap = new HashMap<K, V>(oldMap);
            newMap.putAll(newData);
        }
        while (!internalMap.compareAndSet(oldMap, newMap));
    }

    @Override
    public void clear() {
        internalMap.set(new HashMap<K, V>());
    }

    //
    //  Below are methods that do not modify the internal map
    //          

    @Override
    public int size() {
        return internalMap.get().size();
    }

    @Override
    public boolean isEmpty() {
        return internalMap.get().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return internalMap.get().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return internalMap.get().containsValue(value);
    }

    @Override
    public V get(Object key) {
        return internalMap.get().get(key);
    }

    @Override
    public Set<K> keySet() {
        return internalMap.get().keySet();
    }

    @Override
    public Collection<V> values() {
        return internalMap.get().values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return internalMap.get().entrySet();
    }

    @Override
    public int hashCode() {
        return internalMap.get().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return internalMap.get().equals(o);
    }

    @Override
    public String toString() {
        Map<K, V> map = internalMap.get();
        Iterator<Entry<K, V>> i = map.entrySet().iterator();
        if (!i.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (; ; ) {
            Entry<K, V> e = i.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key == this ? "(this Map)" : (key == map ? "(internal Map)" : key));
            sb.append('=');
            sb.append(value == this ? "(this Map)" : (value == map ? "(internal Map)" : value));
            if (!i.hasNext())
                return sb.append('}').toString();
            sb.append(',').append(' ');
        }
    }

    @Override
    public Object clone() {
        try {
            CopyOnWriteHashMap<K, V> clone = (CopyOnWriteHashMap<K, V>) super.clone();
            clone.internalMap = new AtomicReference<Map<K, V>>(new HashMap<K, V>(internalMap.get()));
            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}