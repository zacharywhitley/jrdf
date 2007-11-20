package org.jrdf.graph.local.index.longindex.sesame;

import org.jrdf.util.DirectoryHandler;

public interface BTreeFactory {
    BTree createBTree(DirectoryHandler handler, String fileName);
}
