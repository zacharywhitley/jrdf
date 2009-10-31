/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2009 The JRDF Project.  All rights reserved.
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
package org.jrdf.graph.local.disk;

import org.jrdf.collection.BdbCollectionFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphValueFactory;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.local.GraphElementFactoryImpl;
import org.jrdf.graph.local.GraphImpl;
import org.jrdf.graph.local.GraphValueFactoryImpl;
import org.jrdf.graph.local.ReadWriteGraph;
import org.jrdf.graph.local.ReadWriteGraphFactory;
import org.jrdf.graph.local.ReadWriteGraphImpl;
import org.jrdf.graph.local.ResourceFactory;
import org.jrdf.graph.local.ResourceFactoryImpl;
import org.jrdf.graph.local.TripleFactoryImpl;
import org.jrdf.graph.local.index.graphhandler.GraphHandler;
import org.jrdf.graph.local.index.graphhandler.GraphHandler012;
import org.jrdf.graph.local.index.graphhandler.GraphHandler120;
import org.jrdf.graph.local.index.graphhandler.GraphHandler201;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.graph.local.index.nodepool.Localizer;
import org.jrdf.graph.local.index.nodepool.LocalizerImpl;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.graph.local.index.nodepool.NodePoolFactory;
import org.jrdf.graph.local.index.nodepool.StringNodeMapperFactoryImpl;
import org.jrdf.graph.local.iterator.IteratorFactory;
import org.jrdf.graph.local.iterator.OrderedIteratorFactoryImpl;
import org.jrdf.graph.local.iterator.ResourceIteratorFactory;
import org.jrdf.graph.local.iterator.ResourceIteratorFactoryImpl;
import org.jrdf.util.TempDirectoryHandler;
import org.jrdf.util.bdb.BdbEnvironmentHandlerImpl;
import org.jrdf.util.bdb.BdbEnvironmentHandler;
import org.jrdf.util.btree.BTree;

import static java.util.Arrays.asList;
import java.util.List;

/**
 * Creates a new Graph implementation based on required types.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public class OrderedGraphFactoryImpl implements ReadWriteGraphFactory {
    private LongIndex[] longIndexes;
    private List<GraphHandler> graphHandlers;
    private IteratorFactory iteratorFactory;
    private NodePool nodePool;
    private ReadWriteGraph readWriteGraph;
    private Localizer localizer;
    private GraphElementFactory elementFactory;
    private TripleFactory tripleFactory;
    private ResourceIteratorFactory resourceIteratorFactory;

    public OrderedGraphFactoryImpl(LongIndex[] newLongIndexes, NodePoolFactory newNodePoolFactory, BTree[] trees,
        long graphNumber) {
        this.longIndexes = newLongIndexes;
        nodePool = newNodePoolFactory.createNewNodePool();
        this.nodePool.clear();
        this.localizer = new LocalizerImpl(nodePool, new StringNodeMapperFactoryImpl().createMapper());
        this.graphHandlers = createGraphHandlers(newLongIndexes);
        this.iteratorFactory = createIteratorFactory(graphNumber);
        this.readWriteGraph = new ReadWriteGraphImpl(longIndexes, nodePool, iteratorFactory);
        GraphValueFactory valueFactory = new GraphValueFactoryImpl(nodePool, localizer);
        ResourceFactory resourceFactory = new ResourceFactoryImpl(readWriteGraph, valueFactory);
        this.elementFactory = new GraphElementFactoryImpl(resourceFactory, localizer, valueFactory);
        this.tripleFactory = new TripleFactoryImpl(readWriteGraph, elementFactory);
        this.resourceIteratorFactory = new ResourceIteratorFactoryImpl(longIndexes, resourceFactory, nodePool);
    }

    public Graph getGraph() {
        return new GraphImpl(nodePool, readWriteGraph, elementFactory, tripleFactory, resourceIteratorFactory);
    }

    public ReadWriteGraph getReadWriteGraph() {
        return readWriteGraph;
    }

    public IteratorFactory getIteratorFactory() {
        return iteratorFactory;
    }

    private List<GraphHandler> createGraphHandlers(LongIndex[] newLongIndexes) {
        final GraphHandler graphHandler012 = new GraphHandler012(newLongIndexes, nodePool);
        final GraphHandler graphHandler120 = new GraphHandler120(newLongIndexes, nodePool);
        final GraphHandler graphHandler201 = new GraphHandler201(newLongIndexes, nodePool);
        return asList(graphHandler012, graphHandler120, graphHandler201);
    }

    private IteratorFactory createIteratorFactory(long graphNumber) {
        final BdbEnvironmentHandler environmentHandler = new BdbEnvironmentHandlerImpl(new TempDirectoryHandler());
        final BdbCollectionFactory collectionFactory = new BdbCollectionFactory(environmentHandler, "tmpResults" +
            graphNumber);
        return new OrderedIteratorFactoryImpl(localizer, graphHandlers, collectionFactory);
    }
}