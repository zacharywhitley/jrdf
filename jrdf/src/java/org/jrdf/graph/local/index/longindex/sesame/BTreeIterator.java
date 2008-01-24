/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2007.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.jrdf.graph.local.index.longindex.sesame;

import java.io.IOException;

/**
 * An iterator that iterates over the values in a BTree.
 *
 * @see BTree
 * @author Arjohn Kampman
 */
public interface BTreeIterator {

    /**
     * Returns the next value in the BTree.
     *
     * @return A value that is stored in the BTree, or <tt>null</tt> if all
     *         values have been returned.
     * @exception IOException
     *            In case an I/O error occurred.
     */
    byte[] next() throws IOException;

    /**
     * Replaces the last value returned by {@link #next} with the specified
     * value.
     *
     * @exception IOException
     *            In case an I/O error occurred.
     */
    void set(byte[] value) throws IOException;

    /**
     * Closes the iterator, freeing any resources that it uses. Once closed, the
     * iterator will not return any more values.
     *
     * @exception IOException
     *            In case an I/O error occurred.
     */
    void close() throws IOException;
}
