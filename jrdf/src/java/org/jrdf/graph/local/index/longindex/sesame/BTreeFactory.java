package org.jrdf.graph.local.index.longindex.sesame;

import org.jrdf.map.DirectoryHandler;

public interface BTreeFactory {
    BTree createBTree(DirectoryHandler handler, String fileName);
}
