package org.jrdf.graph.local.index.longindex.sesame;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.local.index.longindex.LongIndex;
import static org.jrdf.graph.local.index.longindex.sesame.ByteArrayUtil.putLong;
import static org.jrdf.graph.local.index.longindex.sesame.ByteHandler.fromBytes;
import static org.jrdf.graph.local.index.longindex.sesame.ByteHandler.toBytes;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class LongIndexSesame implements LongIndex {
    private static final int OFFSET = 8;
    private static final long MASK = 0xffffffffffffffffL;
    private static final int TRIPLES = 3;
    private static final int VALUE_SIZE = 24;
    private BTree btree;

    public LongIndexSesame(BTree newBtree) {
        this.btree = newBtree;
    }

    public void add(Long... node) throws GraphException {
        try {
            btree.insert(toBytes(node));
        } catch (IOException e) {
            throw new GraphException(e);
        }
    }

    public void remove(Long... node) throws GraphException {
        BTreeIterator bTreeIterator = getIterator(node);
        if (getNextBytes(bTreeIterator) == null) {
            throw new GraphException("Unable to remove nonexistent statement");
        }
        removeBytes(toBytes(node));
    }

    public void clear() {
        try {
            btree.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> iterator() {
        return new EntryIterator(btree.iterateAll());
    }

    public Map<Long, Set<Long>> getSubIndex(Long first) {
        Map<Long, Set<Long>> resultMap = new HashMap<Long, Set<Long>>();
        BTreeIterator bTreeIterator = getIterator(first, 0L, 0L);
        byte[] bytes = getNextBytes(bTreeIterator);
        while (bytes != null) {
            Long[] longs = fromBytes(bytes, TRIPLES);
            Set<Long> longSet = getLongSet(longs, resultMap);
            longSet.add(longs[2]);
            resultMap.put(longs[1], longSet);
            bytes = getNextBytes(bTreeIterator);
        }
        return resultMap.isEmpty() ? null : resultMap;
    }

    public boolean contains(Long first) {
        byte[] bytes = getNextBytes(getIterator(first, 0L, 0L));
        return bytes != null;
    }

    public boolean removeSubIndex(Long first) {
        BTreeIterator bTreeIterator = getIterator(first, 0L, 0L);
        byte[] bytes = getNextBytes(bTreeIterator);
        boolean changed = bytes != null;
        while (bytes != null) {
            removeBytes(bytes);
            bytes = getNextBytes(bTreeIterator);
        }
        return changed;
    }

    public long getSize() {
        long counter = 0;
        BTreeIterator bTreeIterator = btree.iterateAll();
        while (getNextBytes(bTreeIterator) != null) {
            counter++;
        }
        return counter;
    }

    public void close() {
        try {
            btree.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BTreeIterator getIterator(Long... node) {
        byte[] key = toBytes(node[0], node[1], node[2]);
        byte[] filter = new byte[VALUE_SIZE];
        for (int i = 0; i < TRIPLES; i++) {
            addToFilter(filter, i, node);
        }
        return btree.iterateValues(key, filter);
    }

    private void addToFilter(byte[] filter, int index, Long... node) {
        if (node[index] != 0) {
            putLong(MASK, filter, index * OFFSET);
        }
    }

    private Set<Long> getLongSet(Long[] longs, Map<Long, Set<Long>> resultMap) {
        Set<Long> longSet;
        if (resultMap.containsKey(longs[1])) {
            longSet = resultMap.get(longs[1]);
        } else {
            longSet = new HashSet<Long>();
        }
        return longSet;
    }

    private void removeBytes(byte[] bytes) {
        try {
            btree.remove(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getNextBytes(BTreeIterator bTreeIterator) {
        try {
            return bTreeIterator.next();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
