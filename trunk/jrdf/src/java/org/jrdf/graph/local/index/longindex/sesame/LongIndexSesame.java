package org.jrdf.graph.local.index.longindex.sesame;

import org.jrdf.graph.GraphException;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.map.DirectoryHandler;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class LongIndexSesame implements LongIndex {
    private static final int BLOCK_SIZE = 4096;
    private static final int VALUE_SIZE = 24;
    private BTree btree;
    private ByteHandler handler = new ByteHandler();

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
        return null;
    }

    public Map<Long, Set<Long>> getSubIndex(Long first) {
        return null;
    }

    public boolean contains(Long first) {
        byte[] array = new byte[VALUE_SIZE];
        ByteArrayUtil.putLong(0xffffffffffffffffL, array, 0);
        return false;
    }

    public boolean removeSubIndex(Long first) {
        return false;
    }

    public long getSize() {
        return 0;
    }

    public void close() {
    }
}
