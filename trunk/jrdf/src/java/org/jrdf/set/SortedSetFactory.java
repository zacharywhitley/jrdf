package org.jrdf.set;

import java.util.Comparator;
import java.util.SortedSet;

/**
 * An abstract from specifically knowing how to create Sorted Sets.  This allows different implementations to be
 * swapped more easily (for example from memory bound to disk based).
 */
public interface SortedSetFactory {
    /**
     * Creates a sorted set for known type.  The supported types depend on the implementation - current implementations
     * support types such as Triple and PredicateNode.  Otherwise, it will produce a sorted set without a comparator -
     * which will need to be added later.
     *
     * @param clazz The type of set to create.
     * @return A sorted set.
     */
    <T> SortedSet<T> createSet(Class<T> clazz);

    /**
     * Creates a sorted set for a known type with a given comparator.  This allows non-supported types to be added.
     * This is optional and may not be supported by all implementations.
     *
     * @param clazz The type of set to create.
     * @param comparator The comparator to use to determine the sort order.
     * @return A sorted set.
     */
    <T> SortedSet<T> createSet(Class<T> clazz, Comparator<? super T> comparator);

    /**
     * Close any resources used by the factory - possibly database connections, file handles and the like.  It is
     * expected that a factory used that is not close may cause resource leaks.
     */
    void close();
}
