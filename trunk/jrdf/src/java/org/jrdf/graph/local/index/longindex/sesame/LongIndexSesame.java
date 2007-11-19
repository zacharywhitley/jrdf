package org.jrdf.graph.local.index.longindex.sesame;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.local.index.longindex.LongIndex;
import static org.jrdf.graph.local.index.longindex.sesame.ByteArrayUtil.putLong;
import static org.jrdf.graph.local.index.longindex.sesame.ByteHandler.*;

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
        try {
            BTreeIterator bTreeIterator = getIterator(node);
            if (bTreeIterator.next() == null) {
                throw new GraphException("Unable to remove nonexistent statement");
            }
            btree.remove(toBytes(node));
        } catch (IOException e) {
            throw new GraphException(e);
        }
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
        try {
            BTreeIterator bTreeIterator = getIterator(first, 0L, 0L);
            byte[] bytes = bTreeIterator.next();
            Map<Long, Set<Long>> resultMap = new HashMap<Long, Set<Long>>();
            while (bytes != null) {
                Long[] longs = fromBytes(bytes, TRIPLES);
                Set<Long> longSet;
                if (resultMap.containsKey(longs[1])) {
                    longSet = resultMap.get(longs[1]);
                } else {
                    longSet = new HashSet<Long>();
                }
                longSet.add(longs[2]);
                resultMap.put(longs[1], longSet);
                bytes = bTreeIterator.next();
            }
            if (resultMap.isEmpty()) {
                return null;
            } else {
                return resultMap;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean contains(Long first) {
        try {
            byte[] bytes = getIterator(first, 0L, 0L).next();
            return bytes != null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean removeSubIndex(Long first) {
        try {
            BTreeIterator bTreeIterator = getIterator(first, 0L, 0L);
            byte[] bytes = bTreeIterator.next();
            boolean changed = bytes != null;
            while (bytes != null) {
                btree.remove(bytes);
                bytes = bTreeIterator.next();
            }
            return changed;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long getSize() {
        long counter = 0;
        try {
            BTreeIterator bTreeIterator = btree.iterateAll();
            while (bTreeIterator.next() != null) {
                counter++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
}
