package org.jrdf.util;

/**
 * An iterable that produces a closable iterator.  Closable iterators should be closed after usage - to clean up any
 * resources.
 */
public interface ClosableIterable<T> extends Iterable<T> {
    ClosableIterator<T> iterator();
}
