package net.minecraft.server;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryMaterials<T> extends IRegistryWritable<T> {

    protected static final Logger LOGGER = LogManager.getLogger();
    protected final RegistryID<T> b = new RegistryID<>(2048); // Paper - use bigger expected size to reduce collisions
    protected final BiMap<MinecraftKey, T> c = HashBiMap.create(2048); // Paper - use bigger expected size to reduce collisions
    protected T[] d; // Paper - Decompile fix
    private int R;

    public RegistryMaterials() {}

    @Override
    public <V extends T> V a(int i, MinecraftKey minecraftkey, V v0) {
        this.b.a(v0, i);
        Validate.notNull(minecraftkey);
        Validate.notNull(v0);
        this.d = null;
        if (this.c.containsKey(minecraftkey)) {
            RegistryMaterials.LOGGER.debug("Adding duplicate key '{}' to registry", minecraftkey);
        }

        this.c.put(minecraftkey, v0);
        if (this.R <= i) {
            this.R = i + 1;
        }

        return v0;
    }

    @Override
    public <V extends T> V a(MinecraftKey minecraftkey, V v0) {
        return this.a(this.R, minecraftkey, v0);
    }

    @Nullable
    @Override
    public MinecraftKey getKey(T t0) {
        return (MinecraftKey) this.c.inverse().get(t0);
    }

    @Override
    public int a(@Nullable T t0) {
        return this.b.getId(t0);
    }

    @Nullable
    @Override
    public T fromId(int i) {
        return this.b.fromId(i);
    }

    public Iterator<T> iterator() {
        return this.b.iterator();
    }

    @Nullable
    @Override
    public T get(@Nullable MinecraftKey minecraftkey) {
        return this.c.get(minecraftkey);
    }

    @Override
    public Optional<T> getOptional(@Nullable MinecraftKey minecraftkey) {
        return Optional.ofNullable(this.c.get(minecraftkey));
    }

    @Override
    public Set<MinecraftKey> keySet() {
        return Collections.unmodifiableSet(this.c.keySet());
    }

    @Override
    public boolean c() {
        return this.c.isEmpty();
    }

    @Nullable
    @Override
    public T a(Random random) {
        if (this.d == null) {
            Collection<?> collection = this.c.values();

            if (collection.isEmpty()) {
                return null;
            }

            this.d = (T[]) collection.toArray(new Object[collection.size()]); // Paper - Decompile fix
        }

        return this.d[random.nextInt(this.d.length)];
    }
}
