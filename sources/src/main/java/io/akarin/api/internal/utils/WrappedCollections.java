package io.akarin.api.internal.utils;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class WrappedCollections {
    // Wrappers
    public static <T> Collection<T> wrappedCollection(Collection<T> c) {
        return new WrappedCollection<>(c);
    }

    /**
     * @serial include
     */
    static class WrappedCollection<E> implements Collection<E>, Serializable {
        private static final long serialVersionUID = 3053995032091335093L;

        final Collection<E> list;  // Backing Collection
        final Object mutex;     // Object on which to synchronize

        WrappedCollection(Collection<E> c) {
            this.list = Objects.requireNonNull(c);
            mutex = this;
        }

        @Override
        public int size() {
            return list.size();
        }
        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }
        @Override
        public boolean contains(Object o) {
            return list.contains(o);
        }
        @Override
        public Object[] toArray() {
            return list.toArray();
        }
        @Override
        public <T> T[] toArray(T[] a) {
            return list.toArray(a);
        }

        @Override
        public Iterator<E> iterator() {
            return list.iterator();
        }

        @Override
        public boolean add(E e) {
            return list.add(e);
        }
        @Override
        public boolean remove(Object o) {
            return list.remove(o);
        }

        @Override
        public boolean containsAll(Collection<?> coll) {
            return list.containsAll(coll);
        }
        @Override
        public boolean addAll(Collection<? extends E> coll) {
            return list.addAll(coll);
        }
        @Override
        public boolean removeAll(Collection<?> coll) {
            return list.removeAll(coll);
        }
        @Override
        public boolean retainAll(Collection<?> coll) {
            return list.retainAll(coll);
        }
        @Override
        public void clear() {
            list.clear();
        }
        @Override
        public String toString() {
            return list.toString();
        }
        // Override default methods in Collection
        @Override
        public void forEach(Consumer<? super E> consumer) {
            list.forEach(consumer);
        }
        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            return list.removeIf(filter);
        }
        @Override
        public Spliterator<E> spliterator() {
            return list.spliterator();
        }
        @Override
        public Stream<E> stream() {
            return list.stream();
        }
        @Override
        public Stream<E> parallelStream() {
            return list.parallelStream();
        }
        private void writeObject(ObjectOutputStream s) throws IOException {
            s.defaultWriteObject();
        }
    }

    public static <T> List<T> wrappedList(List<T> list) {
        return new WrappedList<>(list);
    }

    /**
     * @serial include
     */
    static class WrappedList<E> extends WrappedCollection<E> implements List<E> {
        private static final long serialVersionUID = -7754090372962971524L;

        final List<E> list;

        WrappedList(List<E> list) {
            super(list);
            this.list = list;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            return list.equals(o);
        }
        @Override
        public int hashCode() {
            return list.hashCode();
        }

        @Override
        public E get(int index) {
            return list.get(index);
        }
        @Override
        public E set(int index, E element) {
            return list.set(index, element);
        }
        @Override
        public void add(int index, E element) {
            list.add(index, element);
        }
        @Override
        public E remove(int index) {
            return list.remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return list.indexOf(o);
        }
        @Override
        public int lastIndexOf(Object o) {
            return list.lastIndexOf(o);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            return list.addAll(index, c);
        }

        @Override
        public ListIterator<E> listIterator() {
            return list.listIterator();
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            return list.listIterator(index);
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return new WrappedList<>(list.subList(fromIndex, toIndex));
        }

        @Override
        public void replaceAll(UnaryOperator<E> operator) {
            list.replaceAll(operator);
        }
        @Override
        public void sort(Comparator<? super E> c) {
            list.sort(c);
        }

        private Object readResolve() {
            return this;
        }
    }
}