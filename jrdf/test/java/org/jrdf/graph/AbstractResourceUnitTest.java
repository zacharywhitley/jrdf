/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

package org.jrdf.graph;

import junit.framework.TestCase;
import org.jrdf.graph.index.graphhandler.GraphHandler;
import org.jrdf.graph.index.graphhandler.mem.GraphHandler012;
import org.jrdf.graph.index.graphhandler.mem.GraphHandler120;
import org.jrdf.graph.index.graphhandler.mem.GraphHandler201;
import org.jrdf.graph.index.longindex.LongIndex;
import org.jrdf.graph.index.longindex.mem.LongIndexMem;
import org.jrdf.graph.index.nodepool.NodePool;
import org.jrdf.graph.index.nodepool.map.MemNodePoolFactory;
import org.jrdf.graph.mem.AbstractResource;
import org.jrdf.graph.mem.ImmutableGraph;
import org.jrdf.graph.mem.ImmutableGraphImpl;
import org.jrdf.graph.mem.MutableGraph;
import org.jrdf.graph.mem.MutableGraphImpl;
import org.jrdf.graph.mem.URIReferenceResourceImpl;
import org.jrdf.graph.mem.GraphImpl;
import org.jrdf.graph.mem.iterator.IteratorFactory;
import org.jrdf.graph.mem.iterator.IteratorFactoryImpl;
import org.jrdf.util.test.MockFactory;

import java.net.URI;

public class AbstractResourceUnitTest extends TestCase {
    private AbstractResource uriReference;
    private IteratorFactory iteratorFactory;
    private MutableGraph mutableGraph;
    private ImmutableGraph immutableGraph;
    private GraphElementFactory elementFactory;
    private MockFactory mockFactory = new MockFactory();
    private NodePool nodePool;

    public void setUp() throws Exception {

        LongIndex[] longIndexes = new LongIndex[]{new LongIndexMem(), new LongIndexMem(), new LongIndexMem()};
        this.nodePool = new MemNodePoolFactory().createNodePool();
        GraphHandler[] graphHandler = getLongIndexes(longIndexes);
        this.iteratorFactory = new IteratorFactoryImpl(longIndexes, graphHandler);
        this.mutableGraph = new MutableGraphImpl(nodePool, longIndexes[0], longIndexes[1], longIndexes[2]);
        this.immutableGraph = new ImmutableGraphImpl(nodePool, longIndexes[0], longIndexes[1], longIndexes[2],
            iteratorFactory);
        Graph graph = new GraphImpl(longIndexes, nodePool, mockFactory.createMock(GraphElementFactory.class),
            (GraphHandler012) graphHandler[0], iteratorFactory, mutableGraph, immutableGraph);
        this.elementFactory = graph.getElementFactory();
    }

    private GraphHandler[] getLongIndexes(LongIndex[] longIndexes) {
        GraphHandler012 graphHandler012 = new GraphHandler012(longIndexes, nodePool);
        GraphHandler201 graphHandler201 = new GraphHandler201(longIndexes, nodePool);
        GraphHandler120 graphHandler120 = new GraphHandler120(longIndexes, nodePool);
        return new GraphHandler[]{graphHandler012, graphHandler120, graphHandler201};
    }

    public void testCreateResourceAsURIReference() throws Exception {
        URI uri = new URI("http://namespace#somevalue");
        this.uriReference = new URIReferenceResourceImpl(elementFactory.createURIReference(uri), iteratorFactory, mutableGraph,
            immutableGraph);
        assertEquals(uriReference, null);
    }
}
