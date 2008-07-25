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
import org.jrdf.graph.global.index.longindex.sesame.MoleculeIndexSesame;
import org.jrdf.graph.global.index.longindex.sesame.MoleculeStructureIndexSesame;
import org.jrdf.graph.local.OrderedGraphFactoryImpl;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.graph.local.index.nodepool.Localizer;
import org.jrdf.graph.local.index.nodepool.LocalizerImpl;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.graph.local.index.nodepool.NodePoolFactory;
import org.jrdf.graph.local.index.nodepool.StringNodeMapper;
import org.jrdf.graph.local.index.nodepool.StringNodeMapperFactoryImpl;
import org.jrdf.graph.local.index.nodepool.bdb.BdbNodePoolFactory;
import org.jrdf.map.BdbMapFactory;
import org.jrdf.map.MapFactory;
import org.jrdf.query.QueryFactory;
import org.jrdf.query.QueryFactoryImpl;
import org.jrdf.query.execute.QueryEngine;
import org.jrdf.urql.UrqlConnection;
import org.jrdf.urql.UrqlConnectionImpl;
import org.jrdf.urql.builder.QueryBuilder;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.TempDirectoryHandler;
import org.jrdf.util.bdb.BdbEnvironmentHandler;
import org.jrdf.util.bdb.BdbEnvironmentHandlerImpl;
import org.jrdf.util.btree.BTree;
import org.jrdf.util.btree.BTreeFactory;
import org.jrdf.util.btree.BTreeFactoryImpl;

import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.Set;

/**
 * Uses default in memory constructors to create JRDF entry points.  Returns sorted results.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public final class SortedDiskGlobalJRDFFactory implements MoleculeJRDFFactory {
    private static final QueryFactory QUERY_FACTORY = new QueryFactoryImpl();
    private static final QueryBuilder BUILDER = QUERY_FACTORY.createQueryBuilder();
    private static final QueryEngine QUERY_ENGINE = QUERY_FACTORY.createQueryEngine();
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler();
    private static final BdbEnvironmentHandler BDB_HANDLER = new BdbEnvironmentHandlerImpl(HANDLER);
    private static final StringNodeMapper STRING_MAPPER = new StringNodeMapperFactoryImpl().createMapper();
    private static long graphNumber;
    private Set<MoleculeIndex<Long>> openIndexes = new HashSet<MoleculeIndex<Long>>();
    private Set<MoleculeStructureIndex<Long>> openStructureIndexes = new HashSet<MoleculeStructureIndex<Long>>();
    private Set<NodePoolFactory> openFactories = new HashSet<NodePoolFactory>();
    private Set<MapFactory> openMapFactories = new HashSet<MapFactory>();
    private BTreeFactory btreeFactory = new BTreeFactoryImpl();
    private CollectionFactory collectionFactory;

    private SortedDiskGlobalJRDFFactory() {
    }

    public static MoleculeJRDFFactory getFactory() {
        return new SortedDiskGlobalJRDFFactory();
    }

    public void refresh() {
    }

    public MoleculeGraph getNewGraph() {
        graphNumber++;
        MapFactory factory = new BdbMapFactory(BDB_HANDLER, "database" + graphNumber);
        MoleculeIndex<Long>[] indexes = createIndexes();
        NodePoolFactory nodePoolFactory = new BdbNodePoolFactory(BDB_HANDLER, graphNumber);
        MoleculeStructureIndex<Long>[] structureIndex = createMoleculeStructureIndexes(graphNumber);
        ReadableIndex<Long> readIndex = new ReadableIndexImpl(indexes, structureIndex);
        WritableIndex<Long> writeIndex = new WritableIndexImpl(indexes, structureIndex);
        NodePool nodePool = nodePoolFactory.createNewNodePool();
        Localizer localizer = new LocalizerImpl(nodePool, STRING_MAPPER);
        MoleculeLocalizer moleculeLocalizer = new MoleculeLocalizerImpl(localizer);
        LongIndex[] longIndexes = new LongIndex[]{new LongIndexAdapter(indexes[0]),
            new LongIndexAdapter(indexes[1]), new LongIndexAdapter(indexes[2])};
        collectionFactory = new BdbCollectionFactory(BDB_HANDLER, "collection" + graphNumber);
        Graph graph = new OrderedGraphFactoryImpl(longIndexes, nodePool, collectionFactory).getGraph();
        openIndexes.addAll(asList(indexes));
        openMapFactories.add(factory);
        openFactories.add(nodePoolFactory);
        return new MoleculeGraphImpl(writeIndex, readIndex, moleculeLocalizer, graph);
    }

    public UrqlConnection getNewUrqlConnection() {
        return new UrqlConnectionImpl(BUILDER, QUERY_ENGINE);
    }

    public void close() {
        collectionFactory.close();
        for (MoleculeIndex<Long> index : openIndexes) {
            index.close();
        }
        for (MapFactory factory : openMapFactories) {
            factory.close();
        }
        for (NodePoolFactory openFactory : openFactories) {
            openFactory.close();
        }
        for (MoleculeStructureIndex<Long> index : openStructureIndexes) {
            index.close();
        }
        openIndexes.clear();
        openStructureIndexes.clear();
        openFactories.clear();
        openMapFactories.clear();
    }

    private MoleculeIndex<Long>[] createIndexes() {
        BTree[] bTrees = createBTrees();
        return new MoleculeIndexSesame[]{new MoleculeIndexSesame(bTrees[0]), new MoleculeIndexSesame(bTrees[1]),
            new MoleculeIndexSesame(bTrees[2])};
    }

    private MoleculeStructureIndex<Long>[] createMoleculeStructureIndexes(long graphNumber) {
        MoleculeStructureIndex<Long>[] indexes = new MoleculeStructureIndexSesame[] {
            new MoleculeStructureIndexSesame(btreeFactory.createQuinBTree(HANDLER, "mmspo" + graphNumber)),
            new MoleculeStructureIndexSesame(btreeFactory.createQuinBTree(HANDLER, "spomm" + graphNumber))
        };
        openStructureIndexes.addAll(asList(indexes));
        return indexes;
    }


    private BTree[] createBTrees() {
        return new BTree[]{btreeFactory.createQuadBTree(HANDLER, "spom" + graphNumber),
                btreeFactory.createQuadBTree(HANDLER, "posm" + graphNumber),
                btreeFactory.createQuadBTree(HANDLER, "ospm" + graphNumber)};
    }
}