/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        the JRDF Project (http://jrdf.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The JRDF Project" and "JRDF" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission. For written permission, please contact
 *    newmana@users.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "JRDF"
 *    nor may "JRDF" appear in their names without prior written
 *    permission of the JRDF Project.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the JRDF Project.  For more
 * information on JRDF, please see <http://jrdf.sourceforge.net/>.
 *
 */

package org.jrdf;

import org.jrdf.collection.BdbCollectionFactory;
import org.jrdf.collection.CollectionFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.graph.global.MoleculeGraphImpl;
import org.jrdf.graph.global.MoleculeLocalizer;
import org.jrdf.graph.global.MoleculeLocalizerImpl;
import org.jrdf.graph.global.index.ReadableIndex;
import org.jrdf.graph.global.index.ReadableIndexImpl;
import org.jrdf.graph.global.index.WritableIndex;
import org.jrdf.graph.global.index.WritableIndexImpl;
import org.jrdf.graph.global.index.adapter.LongIndexAdapter;
import org.jrdf.graph.global.index.longindex.MoleculeIndex;
import org.jrdf.graph.global.index.longindex.MoleculeStructureIndex;
import org.jrdf.graph.global.index.longindex.mem.MoleculeStructureIndexMem;
import org.jrdf.graph.global.index.longindex.sesame.MoleculeIndexSesame;
import org.jrdf.graph.local.OrderedGraphFactoryImpl;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.graph.local.index.nodepool.LocalizerImpl;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.graph.local.index.nodepool.NodePoolFactory;
import org.jrdf.graph.local.index.nodepool.StringNodeMapper;
import org.jrdf.graph.local.index.nodepool.StringNodeMapperFactoryImpl;
import org.jrdf.graph.local.index.nodepool.bdb.BdbNodePoolFactory;
import static org.jrdf.parser.Reader.parseNTriples;
import org.jrdf.query.QueryFactory;
import org.jrdf.query.QueryFactoryImpl;
import org.jrdf.query.execute.QueryEngine;
import org.jrdf.urql.UrqlConnection;
import org.jrdf.urql.UrqlConnectionImpl;
import org.jrdf.urql.builder.QueryBuilder;
import org.jrdf.util.ClosableMap;
import org.jrdf.util.ClosableMapImpl;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.Models;
import org.jrdf.util.ModelsImpl;
import org.jrdf.util.bdb.BdbEnvironmentHandler;
import org.jrdf.util.bdb.BdbEnvironmentHandlerImpl;
import org.jrdf.util.btree.BTree;
import org.jrdf.util.btree.BTreeFactory;
import org.jrdf.util.btree.BTreeFactoryImpl;
import static org.jrdf.writer.Writer.writeNTriples;

import java.io.File;
import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.Set;

/**
 * Uses default in memory constructors to create JRDF entry points.  Returns sorted results.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public final class PersistentGlobalJRDFFactoryImpl implements PersistentGlobalJRDFFactory {
    private static final QueryFactory QUERY_FACTORY = new QueryFactoryImpl();
    private static final QueryEngine QUERY_ENGINE = QUERY_FACTORY.createQueryEngine();
    private static final QueryBuilder BUILDER = QUERY_FACTORY.createQueryBuilder();
    private static final StringNodeMapper STRING_MAPPER = new StringNodeMapperFactoryImpl().createMapper();
    private final DirectoryHandler handler;
    private final BdbEnvironmentHandler bdbHandler;
    private Set<MoleculeIndex<Long>> openIndexes = new HashSet<MoleculeIndex<Long>>();
    private Set<NodePoolFactory> openFactories = new HashSet<NodePoolFactory>();
    private BTreeFactory btreeFactory = new BTreeFactoryImpl();
    private long currentGraphNumber;
    private CollectionFactory collectionFactory;
    private Models models;
    private Graph modelsGraph;
    private File file;

    private PersistentGlobalJRDFFactoryImpl(DirectoryHandler handler) {
        this.handler = handler;
        this.bdbHandler = new BdbEnvironmentHandlerImpl(handler);
        handler.makeDir();
        file = new File(handler.getDir(), "graphs.nt");
        modelsGraph = parseNTriples(file);
        models = new ModelsImpl(modelsGraph);
        currentGraphNumber = models.highestId();
    }

    public static PersistentGlobalJRDFFactory getFactory(DirectoryHandler handler) {
        return new PersistentGlobalJRDFFactoryImpl(handler);
    }

    public void refresh() {
    }

    public boolean hasGraph(String name) {
        return models.hasGraph(name);
    }

    public MoleculeGraph getExistingGraph(String name) throws IllegalArgumentException {
        if (models.getId(name) == 0) {
            throw new IllegalArgumentException("Cannot get graph named: " + name);
        } else {
            return getGraph(models.getId(name));
        }
    }

    public MoleculeGraph getNewGraph(String name) {
        currentGraphNumber++;
        models.addGraph(name, currentGraphNumber);
        writeNTriples(file, modelsGraph);
        return getGraph(currentGraphNumber);
    }

    public UrqlConnection getNewUrqlConnection() {
        return new UrqlConnectionImpl(BUILDER, QUERY_ENGINE);
    }

    public void close() {
        collectionFactory.close();
        for (MoleculeIndex<Long> index : openIndexes) {
            index.close();
        }
        for (NodePoolFactory openFactory : openFactories) {
            openFactory.close();
        }
        openIndexes.clear();
        openFactories.clear();
    }

    private MoleculeGraph getGraph(long graphNumber) {
        collectionFactory = new BdbCollectionFactory(bdbHandler, "collection" + graphNumber);
        final NodePool nodePool = getNodePool(graphNumber);
        MoleculeIndex<Long>[] indexes = createMoleculeIndexes(graphNumber);
        MoleculeStructureIndex<Long> structureIndex = new MoleculeStructureIndexMem(
            new ClosableMapImpl<Long, ClosableMap<Long, ClosableMap<Long, ClosableMap<Long, Set<Long>>>>>());
        ReadableIndex<Long> readIndex = new ReadableIndexImpl(indexes, structureIndex);
        WritableIndex<Long> writeIndex = new WritableIndexImpl(indexes, structureIndex);
        LongIndex[] longIndexes = new LongIndex[]{new LongIndexAdapter(indexes[0]),
            new LongIndexAdapter(indexes[1]), new LongIndexAdapter(indexes[2])};
        Graph graph = new OrderedGraphFactoryImpl(longIndexes, nodePool, collectionFactory).getGraph();
        MoleculeLocalizer moleculeLocalizer = new MoleculeLocalizerImpl(new LocalizerImpl(nodePool, STRING_MAPPER));
        return new MoleculeGraphImpl(writeIndex, readIndex, moleculeLocalizer, graph);
    }

    private NodePool getNodePool(long graphNumber) {
        NodePoolFactory nodePoolFactory = new BdbNodePoolFactory(bdbHandler, graphNumber);
        final NodePool nodePool = nodePoolFactory.openExistingNodePool();
        openFactories.add(nodePoolFactory);
        return nodePool;
    }

    private MoleculeIndex<Long>[] createMoleculeIndexes(long graphNumber) {
        BTree[] bTrees = createBTrees(graphNumber);
        final MoleculeIndex<Long>[] indexes = new MoleculeIndexSesame[] {new MoleculeIndexSesame(bTrees[0]),
            new MoleculeIndexSesame(bTrees[1]), new MoleculeIndexSesame(bTrees[2])};
        openIndexes.addAll(asList(indexes));
        return indexes;
    }

    private BTree[] createBTrees(long graphNumber) {
        return new BTree[]{btreeFactory.createBTree(handler, "spo" + graphNumber),
                btreeFactory.createBTree(handler, "pos" + graphNumber),
                btreeFactory.createBTree(handler, "osp" + graphNumber)};
    }
}