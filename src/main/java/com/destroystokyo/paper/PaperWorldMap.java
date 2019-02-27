package com.destroystokyo.paper;

import net.minecraft.server.DimensionManager;
import net.minecraft.server.WorldServer;

import javax.annotation.Nonnull;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PaperWorldMap extends HashMap<DimensionManager, WorldServer> {
    private final List<WorldServer> worlds = new ArrayList<>();
    private final List<WorldServer> worldsIterable = new ArrayList<WorldServer>() {
        @Override
        public Iterator<WorldServer> iterator() {
            Iterator<WorldServer> iterator = super.iterator();
            return new Iterator<WorldServer>() {
                private WorldServer last;

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public WorldServer next() {
                    this.last = iterator.next();
                    return last;
                }

                @Override
                public void remove() {
                    worlds.set(last.dimension.getDimensionID()+1, null);
                }
            };
        }
    };
    @Override
    public int size() {
        return worldsIterable.size();
    }

    @Override
    public boolean isEmpty() {
        return worldsIterable.isEmpty();
    }

    @Override
    public WorldServer get(Object key) {
        // Will hit the below method
        return key instanceof DimensionManager ? get((DimensionManager) key) : null;
    }

    public WorldServer get(DimensionManager key) {
        int id = key.getDimensionID()+1;
        return worlds.size() > id ? worlds.get(id) : null;
    }

    @Override
    public boolean containsKey(Object key) {
        // will hit below method
        return key instanceof DimensionManager && containsKey((DimensionManager) key);
    }
    public boolean containsKey(DimensionManager key) {
        return get(key) != null;
    }

    @Override
    public WorldServer put(DimensionManager key, WorldServer value) {
        while (worlds.size() <= key.getDimensionID()+1) {
            worlds.add(null);
        }
        WorldServer old = worlds.set(key.getDimensionID()+1, value);
        if (old != null) {
            worldsIterable.remove(old);
        }
        worldsIterable.add(value);
        return old;
    }

    @Override
    public void putAll(Map<? extends DimensionManager, ? extends WorldServer> m) {
        for (Entry<? extends DimensionManager, ? extends WorldServer> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public WorldServer remove(Object key) {
        return key instanceof DimensionManager ? remove((DimensionManager) key) : null;
    }

    public WorldServer remove(DimensionManager key) {
        WorldServer old;
        if (key.getDimensionID()+1 == worlds.size() - 1) {
            old = worlds.remove(key.getDimensionID()+1);
        } else {
            old = worlds.set(key.getDimensionID() + 1, null);
        }
        if (old != null) {
            worldsIterable.remove(old);
        }
        return old;
    }

    @Override
    public void clear() {
        throw new RuntimeException("What the hell are you doing?");
    }

    @Override
    public boolean containsValue(Object value) {
        return value instanceof WorldServer && get(((WorldServer) value).dimension) != null;
    }

    @Nonnull
    @Override
    public Set<DimensionManager> keySet() {
        return new AbstractSet<DimensionManager>() {
            @Override
            public Iterator<DimensionManager> iterator() {
                Iterator<WorldServer> iterator = worldsIterable.iterator();
                return new Iterator<DimensionManager>() {

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public DimensionManager next() {
                        return iterator.next().dimension;
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }

            @Override
            public int size() {
                return worlds.size();
            }
        };
    }

    @Override
    public Collection<WorldServer> values() {
        return worldsIterable;
    }

    @Override
    public Set<Entry<DimensionManager, WorldServer>> entrySet() {
        return new AbstractSet<Entry<DimensionManager, WorldServer>>() {
            @Override
            public Iterator<Entry<DimensionManager, WorldServer>> iterator() {
                Iterator<WorldServer> iterator = worldsIterable.iterator();
                return new Iterator<Entry<DimensionManager, WorldServer>>() {

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Entry<DimensionManager, WorldServer> next() {
                        WorldServer entry = iterator.next();
                        return new SimpleEntry<>(entry.dimension, entry);
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }

            @Override
            public int size() {
                return worldsIterable.size();
            }
        };
    }
}
