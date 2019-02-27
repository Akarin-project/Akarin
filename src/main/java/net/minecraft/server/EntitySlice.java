package net.minecraft.server;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntitySlice<T> extends AbstractSet<T> {

    private static final Set<Class<?>> a = Sets.newHashSet();
    private final Map<Class<?>, List<T>> b = Maps.newHashMap();
    private final Set<Class<?>> c = Sets.newIdentityHashSet();
    private final Class<T> d;
    private final List<T> e = Lists.newArrayList();

    public EntitySlice(Class<T> oclass) {
        this.d = oclass;
        this.c.add(oclass);
        this.b.put(oclass, this.e);
        Iterator iterator = Lists.newArrayList(EntitySlice.a).iterator();

        while (iterator.hasNext()) {
            Class<?> oclass1 = (Class) iterator.next();

            this.a(oclass1);
        }

    }

    protected void a(Class<?> oclass) {
        EntitySlice.a.add(oclass);
        Iterator iterator = this.e.iterator();

        while (iterator.hasNext()) {
            T t0 = iterator.next();

            if (oclass.isAssignableFrom(t0.getClass())) {
                this.a(t0, oclass);
            }
        }

        this.c.add(oclass);
    }

    protected Class<?> b(Class<?> oclass) {
        if (this.d.isAssignableFrom(oclass)) {
            if (!this.c.contains(oclass)) {
                this.a(oclass);
            }

            return oclass;
        } else {
            throw new IllegalArgumentException("Don't know how to search for " + oclass);
        }
    }

    public boolean add(T t0) {
        Iterator iterator = this.c.iterator();

        while (iterator.hasNext()) {
            Class<?> oclass = (Class) iterator.next();

            if (oclass.isAssignableFrom(t0.getClass())) {
                this.a(t0, oclass);
            }
        }

        return true;
    }

    private void a(T t0, Class<?> oclass) {
        List<T> list = (List) this.b.get(oclass);

        if (list == null) {
            this.b.put(oclass, Lists.newArrayList(new Object[] { t0}));
        } else {
            list.add(t0);
        }

    }

    public boolean remove(Object object) {
        T t0 = object;
        boolean flag = false;
        Iterator iterator = this.c.iterator();

        while (iterator.hasNext()) {
            Class<?> oclass = (Class) iterator.next();

            if (oclass.isAssignableFrom(t0.getClass())) {
                List<T> list = (List) this.b.get(oclass);

                if (list != null && list.remove(t0)) {
                    flag = true;
                }
            }
        }

        return flag;
    }

    public boolean contains(Object object) {
        return Iterators.contains(this.c(object.getClass()).iterator(), object);
    }

    public <S> Iterable<S> c(Class<S> oclass) {
        return () -> {
            List<T> list = (List) this.b.get(this.b(oclass));

            if (list == null) {
                return Collections.emptyIterator();
            } else {
                Iterator<T> iterator = list.iterator();

                return Iterators.filter(iterator, oclass);
            }
        };
    }

    public Iterator<T> iterator() {
        return (Iterator) (this.e.isEmpty() ? Collections.emptyIterator() : Iterators.unmodifiableIterator(this.e.iterator()));
    }

    public int size() {
        return this.e.size();
    }
}
