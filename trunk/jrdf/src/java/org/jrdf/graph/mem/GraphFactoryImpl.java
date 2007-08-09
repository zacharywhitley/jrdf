/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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
package org.jrdf.graph.mem;

import org.jrdf.graph.Graph;
import org.jrdf.graph.index.graphhandler.GraphHandler;
import org.jrdf.graph.index.graphhandler.mem.GraphHandler012;
import org.jrdf.graph.index.graphhandler.mem.GraphHandler120;
import org.jrdf.graph.index.graphhandler.mem.GraphHandler201;
import org.jrdf.graph.index.longindex.LongIndex;
import org.jrdf.graph.index.nodepool.NodePool;
import org.jrdf.graph.index.nodepool.NodePoolFactory;
import org.jrdf.graph.mem.iterator.IteratorFactory;
import org.jrdf.graph.mem.iterator.IteratorFactoryImpl;

/**
 * Creates a new Graph implementation based on required types.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public final class GraphFactoryImpl implements GraphFactory {
    private LongIndex[] longIndexes;
    private GraphHandler012 handler012;
    private GraphHandler120 handler120;
    private GraphHandler201 handler201;
    private IteratorFactory iteratorFactory;
    private NodePool nodePool;
    private MutableGraph mutableGraph;
    private ImmutableGraph immutableGraph;

    public GraphFactoryImpl(LongIndex[] newLongIndexes, NodePoolFactory newNodePoolFactory) {
        this.longIndexes = newLongIndexes;
        this.nodePool = newNodePoolFactory.createNodePool();
        this.nodePool.clear();
        this.handler012 = new GraphHandler012(longIndexes, nodePool);
        this.handler120 = new GraphHandler120(longIndexes, nodePool);
        this.handler201 = new GraphHandler201(longIndexes, nodePool);
        GraphHandler[] handlers = new GraphHandler[]{handler012, handler120, handler201};
        this.iteratorFactory = new IteratorFactoryImpl(longIndexes, handlers);
        this.immutableGraph = new ImmutableGraphImpl(longIndexes[0], longIndexes[1], longIndexes[2], nodePool,
                iteratorFactory);
        this.mutableGraph = new MutableGraphImpl(longIndexes[0], longIndexes[1], longIndexes[2], nodePool);
    }

    public Graph getGraph() {
        return new GraphImpl(longIndexes, nodePool, handler012, handler201, iteratorFactory,
                mutableGraph, immutableGraph);
    }

    public IteratorFactory getIteratorFactory() {
        return iteratorFactory;
    }
}
