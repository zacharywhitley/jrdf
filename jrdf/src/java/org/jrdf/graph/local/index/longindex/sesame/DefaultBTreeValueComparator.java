/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2007.
 *
 * Licensed under the Aduna BSD-style license.
 */
package org.jrdf.graph.local.index.longindex.sesame;

/**
 * A BTreeValueComparator that compares values with eachother by comparing all
 * of their bytes.
 *
 * @author Arjohn Kampman
 */
public class DefaultBTreeValueComparator implements BTreeValueComparator {

    // implements BTreeValueComparator.compareBTreeValues()
    public int compareBTreeValues(byte[] key, byte[] data, int offset, int length) {
        int result = 0;
        for (int i = 0; result == 0 && i < length; i++) {
            result = (key[i] & 0xff) - (data[offset + i] & 0xff);
        }
        return result;
    }
}
