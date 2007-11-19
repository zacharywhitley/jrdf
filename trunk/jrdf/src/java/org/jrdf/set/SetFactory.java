package org.jrdf.set;

import java.util.Comparator;
import java.util.SortedSet;

public interface SetFactory {
    <T> SortedSet<T> createSet(Class<T> clazz);

    <T> SortedSet<T> createSet(Class<T> clazz, Comparator<? super T> comparator);

    void close();
}
