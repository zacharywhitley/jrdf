package org.jrdf.graph.local.index.longindex.sesame;

import org.jrdf.util.DirectoryHandler;

import java.io.File;
import java.io.IOException;

public class BTreeFactoryImpl implements BTreeFactory {
    private static final int BLOCK_SIZE = 4096;
    private static final int VALUE_SIZE = 24;

    public TripleBTree createBTree(DirectoryHandler handler, String fileName) {
        BTreeValueComparator comparator = new DefaultBTreeValueComparator();
        try {
            File parent = handler.makeDir();
            File file = new File(parent, fileName);
            return new TripleBTree(file, BLOCK_SIZE, VALUE_SIZE, comparator);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
