package net.minecraft.server;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public abstract class BlockDataAbstract<O, S> implements IBlockDataHolder<S> {

    public static final Function<Entry<IBlockState<?>, Comparable<?>>, String> STATE_TO_VALUE = new Function<Entry<IBlockState<?>, Comparable<?>>, String>() {
        public String apply(@Nullable Entry<IBlockState<?>, Comparable<?>> entry) {
            if (entry == null) {
                return "<NULL>";
            } else {
                IBlockState<?> iblockstate = (IBlockState) entry.getKey();

                return iblockstate.a() + "=" + this.a(iblockstate, (Comparable) entry.getValue());
            }
        }

        private <T extends Comparable<T>> String a(IBlockState<T> iblockstate, Comparable<?> comparable) {
            return iblockstate.a((T) comparable); // Paper - decompiler fix
        }
    };
    protected final O a;
    private final ImmutableMap<IBlockState<?>, Comparable<?>> d;
    private final int e;
    private Table<IBlockState<?>, Comparable<?>, S> f;

    protected BlockDataAbstract(O o0, ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap) {
        this.a = o0;
        this.d = immutablemap;
        this.e = immutablemap.hashCode();
    }

    public <T extends Comparable<T>> S a(IBlockState<T> iblockstate) {
        return this.set(iblockstate, a(iblockstate.getValues(), this.get(iblockstate))); // Paper - decompile fix
    }

    protected static <T> T a(Collection<T> collection, T t0) {
        Iterator<T> iterator = collection.iterator(); // Paper - decompiler fix

        do {
            if (!iterator.hasNext()) {
                return iterator.next();
            }
        } while (!iterator.next().equals(t0));

        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return collection.iterator().next();
        }
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(this.a);
        if (!this.getStateMap().isEmpty()) {
            stringbuilder.append('[');
            stringbuilder.append((String) this.getStateMap().entrySet().stream().map(BlockDataAbstract.STATE_TO_VALUE).collect(Collectors.joining(",")));
            stringbuilder.append(']');
        }

        return stringbuilder.toString();
    }

    public Collection<IBlockState<?>> a() {
        return Collections.unmodifiableCollection(this.d.keySet());
    }

    public final <T extends Comparable<T>> boolean hasProperty(IBlockState<T> iblockstate) { return this.b(iblockstate); } // Paper - OBFHELPER
    public <T extends Comparable<T>> boolean b(IBlockState<T> iblockstate) {
        return this.d.containsKey(iblockstate);
    }

    @Override
    public <T extends Comparable<T>> T get(IBlockState<T> iblockstate) {
        Comparable<?> comparable = (Comparable) this.d.get(iblockstate);

        if (comparable == null) {
            throw new IllegalArgumentException("Cannot get property " + iblockstate + " as it does not exist in " + this.a);
        } else {
            return iblockstate.b().cast(comparable); // Paper - decompiler fix
        }
    }

    @Override
    public <T extends Comparable<T>, V extends T> S set(IBlockState<T> iblockstate, V v0) {
        Comparable<?> comparable = (Comparable) this.d.get(iblockstate);

        if (comparable == null) {
            throw new IllegalArgumentException("Cannot set property " + iblockstate + " as it does not exist in " + this.a);
        } else if (comparable == v0) {
            return (S) this; // Paper - decompiler fix
        } else {
            S s0 = this.f.get(iblockstate, v0);

            if (s0 == null) {
                throw new IllegalArgumentException("Cannot set property " + iblockstate + " to " + v0 + " on " + this.a + ", it is not an allowed value");
            } else {
                return s0;
            }
        }
    }

    public void a(Map<Map<IBlockState<?>, Comparable<?>>, S> map) {
        if (this.f != null) {
            throw new IllegalStateException();
        } else {
            Table<IBlockState<?>, Comparable<?>, S> table = HashBasedTable.create();
            UnmodifiableIterator unmodifiableiterator = this.d.entrySet().iterator();

            while (unmodifiableiterator.hasNext()) {
                Entry<IBlockState<?>, Comparable<?>> entry = (Entry) unmodifiableiterator.next();
                IBlockState<?> iblockstate = (IBlockState) entry.getKey();
                Iterator iterator = iblockstate.getValues().iterator();

                while (iterator.hasNext()) {
                    Comparable<?> comparable = (Comparable) iterator.next();

                    if (comparable != entry.getValue()) {
                        table.put(iblockstate, comparable, map.get(this.c(iblockstate, comparable)));
                    }
                }
            }

            this.f = (Table) (table.isEmpty() ? table : ArrayTable.create(table));
        }
    }

    private Map<IBlockState<?>, Comparable<?>> c(IBlockState<?> iblockstate, Comparable<?> comparable) {
        Map<IBlockState<?>, Comparable<?>> map = Maps.newHashMap(this.d);

        map.put(iblockstate, comparable);
        return map;
    }

    @Override
    public ImmutableMap<IBlockState<?>, Comparable<?>> getStateMap() {
        return this.d;
    }

    public boolean equals(Object object) {
        return this == object;
    }

    public int hashCode() {
        return this.e;
    }
}
