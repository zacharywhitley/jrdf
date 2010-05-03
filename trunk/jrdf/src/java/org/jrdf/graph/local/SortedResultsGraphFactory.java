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
package org.jrdf.graph.local;

import org.jrdf.collection.IteratorTrackingCollectionFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphValueFactory;
import org.jrdf.graph.TripleFactory;
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
import org.jrdf.graph.local.iterator.SortedIteratorFactoryImpl;
import org.jrdf.graph.local.iterator.ResourceIteratorFactory;
import org.jrdf.graph.local.iterator.ResourceIteratorFactoryImpl;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Creates a new Graph implementation based on required types.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public class SortedResultsGraphFactory implements ReadWriteGraphFactory {
    private LongIndex[] longIndexes;
    private List<GraphHandler> graphHandlers;
    private IteratorFactory iteratorFactory;
    private NodePool nodePool;
    private IteratorTrackingCollectionFactory collectionFactory;
    private ReadWriteGraph readWriteGraph;
    private Localizer localizer;
    private GraphElementFactory elementFactory;
    private TripleFactory tripleFactory;
    private ResourceIteratorFactory resourceIteratorFactory;
    private NodePoolFactory nodePoolFactory;

    public SortedResultsGraphFactory(LongIndex[] newLongIndexes, NodePool newNodePool,
        IteratorTrackingCollectionFactory newCollectionFactory) {
        this.longIndexes = newLongIndexes;
        this.nodePool = newNodePool;
        this.collectionFactory = newCollectionFactory;
        init();
    }

    public SortedResultsGraphFactory(LongIndex[] newLongIndexes, NodePoolFactory newNodePoolFactory,
        IteratorTrackingCollectionFactory newCollectionFactory) {
        this.longIndexes = newLongIndexes;
        this.nodePoolFactory = newNodePoolFactory;
        this.nodePool = nodePoolFactory.createNewNodePool();
        this.collectionFactory = newCollectionFactory;
        init();
    }

    public Graph getGraph() {
        return new GraphImpl(nodePool, readWriteGraph, elementFactory, tripleFactory, resourceIteratorFactory);
    }

    public ReadWriteGraph getReadWriteGraph() {
        return readWriteGraph;
    }

    private void init() {
        final GraphHandler graphHandler012 = new GraphHandler012(longIndexes, nodePool);
        final GraphHandler graphHandler120 = new GraphHandler120(longIndexes, nodePool);
        final GraphHandler graphHandler201 = new GraphHandler201(longIndexes, nodePool);
        this.graphHandlers = asList(graphHandler012, graphHandler120, graphHandler201);
        this.localizer = new LocalizerImpl(nodePool, new StringNodeMapperFactoryImpl().createMapper());
        this.iteratorFactory = new SortedIteratorFactoryImpl(graphHandlers, localizer, collectionFactory);
        this.readWriteGraph = new ReadWriteGraphImpl(longIndexes, nodePool, iteratorFactory);
        GraphValueFactory valueFactory = new GraphValueFactoryImpl(nodePool, localizer);
        ResourceFactory resourceFactory = new ResourceFactoryImpl(readWriteGraph, valueFactory);
        this.elementFactory = new GraphElementFactoryImpl(resourceFactory, localizer, valueFactory);
        this.tripleFactory = new TripleFactoryImpl(readWriteGraph, elementFactory);
        this.resourceIteratorFactory = new ResourceIteratorFactoryImpl(longIndexes, resourceFactory, nodePool);
    }

//    public void close() {
//        try {
//            for (LongIndex index : longIndexes) {
//                index.close();
//            }
//        } finally {
//            try {
//                nodePoolFactory.close();
//            } finally {
//                collectionFactory.close();
//            }
//        }
//    }

}
