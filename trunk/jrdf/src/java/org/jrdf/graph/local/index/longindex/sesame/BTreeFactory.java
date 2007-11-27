package org.jrdf.graph.local.index.longindex.sesame;

import org.jrdf.util.DirectoryHandler;

public interface BTreeFactory {
    TripleBTree createBTree(DirectoryHandler handler, String fileName);
}
