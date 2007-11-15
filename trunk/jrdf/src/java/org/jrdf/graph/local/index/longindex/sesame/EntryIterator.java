package org.jrdf.graph.local.index.longindex.sesame;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.io.IOException;

/**
 * Class description goes here.
*/
class EntryIterator implements Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> {
    private static final int TRIPLES = 3;
    private ByteHandler handler = new ByteHandler();
    private BTreeIterator iterator;
    private byte[] currentValues;

    public EntryIterator(BTreeIterator newIterator) {
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

    public Entry next() {
        try {
            Long key = handler.fromBytes(currentValues, TRIPLES)[0];
            Long currentKey = new Long(key.longValue());
            Map<Long, Set<Long>> resultMap = new HashMap<Long, Set<Long>>();
            while (currentValues != null && currentKey.equals(key)) {
                Long[] longs = handler.fromBytes(currentValues, TRIPLES);
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
                    currentKey = handler.fromBytes(currentValues, TRIPLES)[0];
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
}
