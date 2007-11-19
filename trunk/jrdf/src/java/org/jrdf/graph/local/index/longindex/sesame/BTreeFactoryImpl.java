package org.jrdf.graph.local.index.longindex.sesame;

import org.jrdf.map.DirectoryHandler;

import java.io.File;
import java.io.IOException;

public class BTreeFactoryImpl implements BTreeFactory {
    private static final int BLOCK_SIZE = 4096;
    private static final int VALUE_SIZE = 24;

    public BTree createBTree(DirectoryHandler handler, String fileName) {
        BTreeValueComparator comparator = new DefaultBTreeValueComparator();
        try {
            File parent = handler.getDir();
            parent.mkdirs();
            File file = new File(handler.getDir(), fileName);
            return new BTree(file, BLOCK_SIZE, VALUE_SIZE, comparator);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
