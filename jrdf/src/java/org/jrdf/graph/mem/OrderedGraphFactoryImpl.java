/*
 * $Header$
 * $Revision: 439 $
 * $Date: 2006-01-27 06:19:29 +1000 (Fri, 27 Jan 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003, 2004 The JRDF Project.  All rights reserved.
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
 */
package org.jrdf.graph.mem;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.index.graphhandler.GraphHandler;
import org.jrdf.graph.index.graphhandler.mem.GraphHandler012;
import org.jrdf.graph.index.graphhandler.mem.GraphHandler120;
import org.jrdf.graph.index.graphhandler.mem.GraphHandler201;
import org.jrdf.graph.index.longindex.LongIndex;
import org.jrdf.graph.index.nodepool.mem.NodePoolMem;
import org.jrdf.graph.mem.iterator.IteratorFactory;
import org.jrdf.graph.mem.iterator.IteratorFactoryImpl;
import org.jrdf.graph.mem.iterator.OrderedIteratorFactoryImpl;

/**
 * Creates a new Graph implementation based on required types.
 *
 * @author Andrew Newman
 * @version $Id: ClosableIterator.java 436 2005-12-19 13:19:55Z newmana $
 */
public class OrderedGraphFactoryImpl implements GraphFactory {
    private LongIndex[] longIndexes;
    private NodePoolMem nodePool;
    private GraphElementFactory elementFactory;
    private GraphHandler[] graphHandlers;
    private IteratorFactory iteratorFactory;

    public OrderedGraphFactoryImpl(LongIndex[] longIndexes, NodePoolMem nodePool) {
        this.longIndexes = longIndexes;
        this.nodePool = nodePool;
        this.graphHandlers = new GraphHandler[]{new GraphHandler012(longIndexes, nodePool),
            new GraphHandler120(longIndexes, nodePool), new GraphHandler201(longIndexes, nodePool)};
        IteratorFactory tmpIteratorFactory = new IteratorFactoryImpl(longIndexes, graphHandlers);
        this.iteratorFactory = new OrderedIteratorFactoryImpl(tmpIteratorFactory, nodePool, longIndexes[0],
            graphHandlers[0]);
        this.elementFactory = new GraphElementFactoryImpl(nodePool);
    }

    public Graph getGraph() {
        return new GraphImpl(longIndexes, nodePool, elementFactory, (GraphHandler012) graphHandlers[0],
            iteratorFactory);
    }
}
