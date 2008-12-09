package org.jrdf;

import org.jrdf.collection.CollectionFactory;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.urql.UrqlConnection;

public interface BasePersistentJRDFFactory {
    UrqlConnection createUrqlConnection();

    NodePool createNodePool(long graphNumber);

    CollectionFactory createCollectionFactory(long graphNumber);

    boolean hasGraph(String name);

    long addNewGraph(String name);

    long getGraphId(String name);

    void refresh();

    void close();
}
