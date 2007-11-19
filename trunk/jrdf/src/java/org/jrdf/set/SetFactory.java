package org.jrdf.set;

import java.util.SortedSet;

public interface SetFactory {
    <T> SortedSet<T> createSet(Class<T> clazz);

    void close();
}
