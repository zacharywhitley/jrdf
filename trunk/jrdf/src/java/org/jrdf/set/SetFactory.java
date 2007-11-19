package org.jrdf.set;

import java.util.Set;

public interface SetFactory {
    <T> Set<T> createSet(Class<T> clazz);

    void close();
}
