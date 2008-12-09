package org.jrdf;

import org.jrdf.collection.CollectionFactory;
import org.jrdf.collection.BdbCollectionFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.graph.local.index.nodepool.NodePoolFactory;
import org.jrdf.graph.local.index.nodepool.bdb.BdbNodePoolFactory;
import org.jrdf.query.QueryFactory;
import org.jrdf.query.QueryFactoryImpl;
import org.jrdf.query.execute.QueryEngine;
import org.jrdf.urql.UrqlConnection;
import org.jrdf.urql.UrqlConnectionImpl;
import org.jrdf.urql.builder.QueryBuilder;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.Models;
import org.jrdf.util.ModelsImpl;
import org.jrdf.util.bdb.BdbEnvironmentHandler;
import static org.jrdf.writer.Writer.writeNTriples;
import org.jrdf.parser.RdfReader;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class BasePersistentJRDFFactoryImpl implements BasePersistentJRDFFactory {
    private static final QueryFactory QUERY_FACTORY = new QueryFactoryImpl();
    private static final QueryEngine QUERY_ENGINE = QUERY_FACTORY.createQueryEngine();
    private static final QueryBuilder BUILDER = QUERY_FACTORY.createQueryBuilder();
    private final Set<NodePoolFactory> openNodePoolFactories = new HashSet<NodePoolFactory>();
    private final BdbEnvironmentHandler bdbHandler;
    private CollectionFactory collectionFactory;
    private Models models;
    private File file;
    private Graph modelsGraph;

    public BasePersistentJRDFFactoryImpl(DirectoryHandler newHandler, BdbEnvironmentHandler newBdbHandler) {
        this.bdbHandler = newBdbHandler;
        newHandler.makeDir();
        file = new File(newHandler.getDir(), "graphs.nt");
    }

    public UrqlConnection createUrqlConnection() {
        return new UrqlConnectionImpl(BUILDER, QUERY_ENGINE);
    }

    public NodePool createNodePool(long graphNumber) {
        NodePoolFactory nodePoolFactory = new BdbNodePoolFactory(bdbHandler, graphNumber);
        final NodePool nodePool = nodePoolFactory.openExistingNodePool();
        openNodePoolFactories.add(nodePoolFactory);
        return nodePool;
    }

    public CollectionFactory createCollectionFactory(long graphNumber) {
        collectionFactory = new BdbCollectionFactory(bdbHandler, "collection" + graphNumber);
        return collectionFactory;
    }

    public boolean hasGraph(String name) {
        return models.hasGraph(name);
    }

    public long addNewGraph(String name) {
        if (models.getId(name) != 0) {
            throw new IllegalArgumentException("Graph " + name + " already exists");
        }
        long graphNumber = models.addGraph(name);
        writeNTriples(file, modelsGraph);
        return graphNumber;
    }

    public long getGraphId(String name) {
        return models.getId(name);
    }

    public void refresh() {
        modelsGraph = new RdfReader().parseNTriples(file);
        models = new ModelsImpl(modelsGraph);
    }

    public void close() {
        for (NodePoolFactory openFactory : openNodePoolFactories) {
            openFactory.close();
        }
        if (collectionFactory != null) {
            collectionFactory.close();
        }
        openNodePoolFactories.clear();
    }
}
