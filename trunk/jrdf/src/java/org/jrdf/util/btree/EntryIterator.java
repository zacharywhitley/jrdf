/*
 * Copyright Aduna (http://www.aduna-software.com/) (c) 1997-2007.
 *
 * Licensed under the Aduna BSD-style license.
 */

package org.jrdf.util.btree;

import org.jrdf.util.ClosableIterator;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntryIterator implements ClosableIterator<Map.Entry<Long, Map<Long, Set<Long>>>> {
    private static final int TRIPLES = 3;
    private RecordIterator iterator;
    private byte[] currentValues;

    public EntryIterator(RecordIterator newIterator) {
        try {
            this.iterator = newIterator;
            this.currentValues = newIterator.next();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasNext() {
        return currentValues != null;
    }

    public Map.Entry<Long, Map<Long, Set<Long>>> next() {
        try {
            Long key = ByteHandler.fromBytes(currentValues, TRIPLES)[0];
            Long currentKey = Long.valueOf(key);
            Map<Long, Set<Long>> resultMap = new HashMap<Long, Set<Long>>();
            while (currentValues != null && currentKey.equals(key)) {
                Long[] longs = ByteHandler.fromBytes(currentValues, TRIPLES);
                Set<Long> longSet;
                if (resultMap.containsKey(longs[1])) {
                    longSet = resultMap.get(longs[1]);
                } else {
                    longSet = new HashSet<Long>();
                }
                longSet.add(longs[2]);
                resultMap.put(longs[1], longSet);
                currentValues = iterator.next();
                if (currentValues != null) {
                    currentKey = ByteHandler.fromBytes(currentValues, TRIPLES)[0];
                }
            }
            return new Entry(key, resultMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot set values - read only");
    }

    public boolean close() {
        try {
            iterator.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
