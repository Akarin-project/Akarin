package net.minecraft.server;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BlockStateEnum<T extends Enum<T> & INamable> extends BlockState<T> {

    private final ImmutableSet<T> a;
    private final Map<String, T> b = Maps.newHashMap();

    // Paper start - BlockStateEnum is a singleton, so we can use our own hashCode
    private static AtomicInteger hashId = new AtomicInteger(1);
    private int hashCode;
    // Paper end

    protected BlockStateEnum(String s, Class<T> oclass, Collection<T> collection) {
        super(s, oclass);
        this.a = ImmutableSet.copyOf(collection);
        Iterator<T> iterator = collection.iterator(); // Paper - decompile fix

        while (iterator.hasNext()) {
            T t0 = iterator.next(); // Paper - Decompile fix
            String s1 = ((INamable) t0).getName();

            if (this.b.containsKey(s1)) {
                throw new IllegalArgumentException("Multiple values have the same name '" + s1 + "'");
            }

            this.b.put(s1, t0);
        }

    }

    public Collection<T> d() {
        return this.a;
    }

    public Optional<T> b(String s) {
        return Optional.ofNullable(this.b.get(s));
    }

    public String a(T t0) {
        return ((INamable) t0).getName();
    }

    @Override // Paper start - override equals as BlockStateEnum is a singleton
    public boolean equals(Object object) {
        return this == object;
        // Paper end - override equals as BlockStateEnum is a singleton
    }

    public int c() {
        return hashCode; // Paper - hashCode method is final, but we can do this here
    }

    public static <T extends Enum<T> & INamable> BlockStateEnum<T> of(String s, Class<T> oclass) {
        return a(s, oclass, (Predicate) Predicates.alwaysTrue());
    }

    public static <T extends Enum<T> & INamable> BlockStateEnum<T> a(String s, Class<T> oclass, Predicate<T> predicate) {
        return a(s, oclass, (Collection) Arrays.stream(oclass.getEnumConstants()).filter(predicate).collect(Collectors.toList()));
    }

    public static <T extends Enum<T> & INamable> BlockStateEnum<T> of(String s, Class<T> oclass, T... at) {
        return a(s, oclass, (Collection) Lists.newArrayList(at));
    }

    public static <T extends Enum<T> & INamable> BlockStateEnum<T> a(String s, Class<T> oclass, Collection<T> collection) {
        return new BlockStateEnum<>(s, oclass, collection);
    }
}
