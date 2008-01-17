/*
 * $Header$
 * $Revision$
 * $Date$
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
package org.jrdf.graph.local.disk;

import org.jrdf.graph.Graph;
import org.jrdf.graph.local.disk.iterator.DiskIteratorFactory;
import org.jrdf.graph.local.index.graphhandler.GraphHandler;
import org.jrdf.graph.local.index.graphhandler.GraphHandler012;
import org.jrdf.graph.local.index.graphhandler.GraphHandler120;
import org.jrdf.graph.local.index.graphhandler.GraphHandler201;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.graph.local.index.longindex.sesame.TripleBTree;
import org.jrdf.graph.local.index.nodepool.Localizer;
import org.jrdf.graph.local.index.nodepool.LocalizerImpl;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.graph.local.index.nodepool.NodePoolFactory;
import org.jrdf.graph.local.index.nodepool.StringNodeMapper;
import org.jrdf.graph.local.index.nodepool.StringNodeMapperImpl;
import org.jrdf.graph.local.iterator.IteratorFactory;
import org.jrdf.graph.local.iterator.OrderedIteratorFactoryImpl;
import org.jrdf.graph.local.mem.GraphImpl;
import org.jrdf.graph.local.mem.ReadWriteGraph;
import org.jrdf.graph.local.mem.ReadWriteGraphFactory;
import org.jrdf.graph.local.mem.ReadWriteGraphImpl;
import org.jrdf.graph.local.mem.ResourceFactory;
import org.jrdf.graph.local.mem.ResourceFactoryImpl;
import org.jrdf.set.BdbSortedSetFactory;
import org.jrdf.util.TempDirectoryHandler;
import org.jrdf.util.boundary.RegexMatcherFactory;
import org.jrdf.util.boundary.RegexMatcherFactoryImpl;
import org.jrdf.util.bdb.BdbEnvironmentHandlerImpl;
import org.jrdf.parser.ntriples.parser.LiteralMatcher;
import org.jrdf.parser.ntriples.parser.RegexLiteralMatcher;
import org.jrdf.parser.ntriples.parser.NTripleUtilImpl;

/**
 * Creates a new Graph implementation based on required types.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public class OrderedGraphFactoryImpl implements ReadWriteGraphFactory {
    private LongIndex[] longIndexes;
    private GraphHandler[] graphHandlers;
    private IteratorFactory iteratorFactory;
    private NodePool nodePool;
    private ReadWriteGraph readWriteGraph;
    private ResourceFactory resourceFactory;
    private Localizer localizer;

    public OrderedGraphFactoryImpl(LongIndex[] newLongIndexes, NodePoolFactory newNodePoolFactory, TripleBTree[] trees,
            long graphNumber) {
        this.longIndexes = newLongIndexes;
        nodePool = newNodePoolFactory.createNodePool();
        this.nodePool.clear();
        RegexMatcherFactory regexFactory = new RegexMatcherFactoryImpl();
        LiteralMatcher matcher = new RegexLiteralMatcher(regexFactory, new NTripleUtilImpl(regexFactory));
        StringNodeMapper mapper = new StringNodeMapperImpl(matcher);
        this.localizer = new LocalizerImpl(nodePool, mapper);
        this.graphHandlers = new GraphHandler[]{new GraphHandler012(newLongIndexes, nodePool),
            new GraphHandler120(newLongIndexes, nodePool), new GraphHandler201(newLongIndexes, nodePool)};
        IteratorFactory tmpIteratorFactory = new DiskIteratorFactory(newLongIndexes, graphHandlers, nodePool, localizer,
            trees);
        this.iteratorFactory = new OrderedIteratorFactoryImpl(tmpIteratorFactory, localizer, newLongIndexes[0],
            graphHandlers[0], new BdbSortedSetFactory(new BdbEnvironmentHandlerImpl(new TempDirectoryHandler()),
                "tmpResults" + graphNumber));
        this.readWriteGraph = new ReadWriteGraphImpl(longIndexes, nodePool, iteratorFactory);
        this.resourceFactory = new ResourceFactoryImpl(localizer, readWriteGraph);
    }

    public Graph getGraph() {
        return new GraphImpl(longIndexes, nodePool, iteratorFactory, readWriteGraph, resourceFactory);
    }

    public ReadWriteGraph getReadWriteGraph() {
        return readWriteGraph;
    }

    public IteratorFactory getIteratorFactory() {
        return iteratorFactory;
    }
}