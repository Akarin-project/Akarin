package net.minecraft.server;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryMaterials<V> implements IRegistry<V> {

    protected static final Logger a = LogManager.getLogger();
    protected final RegistryID<V> b = new RegistryID<>(256);
    protected final BiMap<MinecraftKey, V> c = HashBiMap.create();
    protected Object[] d;
    private int x;

    public RegistryMaterials() {}

    public void a(int i, MinecraftKey minecraftkey, V v0) {
        this.b.a(v0, i);
        Validate.notNull(minecraftkey);
        Validate.notNull(v0);
        this.d = null;
        if (this.c.containsKey(minecraftkey)) {
            RegistryMaterials.a.debug("Adding duplicate key '{}' to registry", minecraftkey);
        }

        this.c.put(minecraftkey, v0);
        if (this.x <= i) {
            this.x = i + 1;
        }

    }

    public void a(MinecraftKey minecraftkey, V v0) {
        this.a(this.x, minecraftkey, v0);
    }

    @Nullable
    public MinecraftKey getKey(V v0) {
        return (MinecraftKey) this.c.inverse().get(v0);
    }

    public V getOrDefault(@Nullable MinecraftKey minecraftkey) {
        throw new UnsupportedOperationException("No default value");
    }

    public MinecraftKey b() {
        throw new UnsupportedOperationException("No default key");
    }

    public int a(@Nullable V v0) {
        return this.b.getId(v0);
    }

    @Nullable
    public V fromId(int i) {
        return this.b.fromId(i);
    }

    public Iterator<V> iterator() {
        return this.b.iterator();
    }

    @Nullable
    public V get(@Nullable MinecraftKey minecraftkey) {
        return this.c.get(minecraftkey);
    }

    public Set<MinecraftKey> keySet() {
        return Collections.unmodifiableSet(this.c.keySet());
    }

    public boolean d() {
        return this.c.isEmpty();
    }

    @Nullable
    public V a(Random random) {
        if (this.d == null) {
            Collection<?> collection = this.c.values();

            if (collection.isEmpty()) {
                return null;
            }

            this.d = collection.toArray(new Object[collection.size()]);
        }

        return this.d[random.nextInt(this.d.length)];
    }

    public boolean c(MinecraftKey minecraftkey) {
        return this.c.containsKey(minecraftkey);
    }
}
