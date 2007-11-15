package org.jrdf.graph.local.index.longindex.sesame;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.local.index.longindex.LongIndex;
import static org.jrdf.graph.local.index.longindex.sesame.ByteArrayUtil.putLong;
import org.jrdf.map.DirectoryHandler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class LongIndexSesame implements LongIndex {
    private static final int BLOCK_SIZE = 4096;
    private static final int VALUE_SIZE = 24;
    private BTree btree;
    private ByteHandler handler = new ByteHandler();
    private static final int TRIPLES = 3;

    public LongIndexSesame(DirectoryHandler handler, String fileName) {
        BTreeValueComparator comparator = new DefaultBTreeValueComparator();
        try {
            File file = new File(handler.getDir(), fileName);
            btree = new BTree(file, BLOCK_SIZE, VALUE_SIZE, comparator);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void add(Long... node) throws GraphException {
        try {
            btree.insert(handler.toBytes(node));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(Long... node) throws GraphException {
        try {
            btree.remove(handler.toBytes(node));
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            byte[] key = handler.toBytes(first, 0L, 0L);
            byte[] filter = new byte[VALUE_SIZE];
            putLong(0xffffffffffffffffL, filter, 0);
            BTreeIterator bTreeIterator = btree.iterateValues(key, filter);
            byte[] bytes = bTreeIterator.next();
            Map<Long, Set<Long>> resultMap = new HashMap<Long, Set<Long>>();
            while (bytes != null) {
                Long[] longs = handler.fromBytes(bytes, TRIPLES);
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
            return resultMap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean contains(Long first) {
        try {
            byte[] key = handler.toBytes(first, 0L, 0L);
            byte[] filter = new byte[VALUE_SIZE];
            putLong(0xffffffffffffffffL, filter, 0);
            byte[] bytes = btree.iterateValues(key, filter).next();
            return bytes != null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean removeSubIndex(Long first) {
        try {
            byte[] key = handler.toBytes(first, 0L, 0L);
            byte[] filter = new byte[VALUE_SIZE];
            putLong(0xffffffffffffffffL, filter, 0);
            BTreeIterator bTreeIterator = btree.iterateValues(key, filter);
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

    private static class EntryIterator implements Iterator<Map.Entry<Long, Map<Long, Set<Long>>>> {
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
                while (currentValues != null || currentKey.equals(key)) {
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
                Entry entry = new Entry(key, resultMap);
                return entry;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void remove() {
            throw new UnsupportedOperationException("Cannot set values - read only");
        }
    }

    private static class Entry implements Map.Entry<Long, Map<Long, Set<Long>>> {
        private final Long key;
        private final Map<Long, Set<Long>> values;

        public Entry(Long key, Map<Long, Set<Long>> values) {
            this.key = key;
            this.values = values;
        }

        public Long getKey() {
            return key;
        }

        public Map<Long, Set<Long>> getValue() {
            return values;
        }

        public Map<Long, Set<Long>> setValue(Map<Long, Set<Long>> value) {
            throw new UnsupportedOperationException("Cannot set values - read only");
        }

        public String toString() {
            return "Key: " + key + " Entries: " + values;
        }
    }
}
