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

package org.jrdf.example;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.index.longindex.LongIndex;
import org.jrdf.graph.index.longindex.bdb.LongIndexBdb;
import org.jrdf.graph.index.nodepool.NodePoolFactory;
import org.jrdf.graph.index.nodepool.map.BdbNodePoolFactory;
import org.jrdf.graph.mem.GraphFactory;
import org.jrdf.graph.mem.NodeComparatorImpl;
import org.jrdf.graph.mem.OrderedGraphFactoryImpl;
import org.jrdf.map.BdbMapFactory;
import org.jrdf.map.StoredMapHandler;
import org.jrdf.map.StoredMapHandlerImpl;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.NodeTypeComparatorImpl;

import java.net.URI;

public class BdbPerformance {
    private static final String URI_STRING = "http://foo/bar";
    private static final URI URI_1 = URI.create(URI_STRING);
    private LongIndex[] indexes;
    private StoredMapHandler handler;
    private GraphElementFactory graphElementFactory;

    public BdbPerformance() throws Exception {
        handler = new StoredMapHandlerImpl();
        indexes = new LongIndex[]{new LongIndexBdb(new BdbMapFactory(handler, "catalog", "database")),
                new LongIndexBdb(new BdbMapFactory(handler, "catalog", "database")),
                new LongIndexBdb(new BdbMapFactory(handler, "catalog", "database"))};
    }

    public void testPerformance() throws Exception {
        int numberOfNodes = 100;
        Graph graph = getOnDiskGraph();
        graphElementFactory = graph.getElementFactory();
        addPerformance(numberOfNodes, graph);
        //findPerformance(numberOfNodes, graph);
    }

    private void addPerformance(int numberOfNodes, Graph graph) throws Exception {
        //Test
        int rnd = (int) (Math.random() * numberOfNodes);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numberOfNodes; i++) {
            if (i == rnd) {
                graph.add(graphElementFactory.createResource(URI_1),
                        graphElementFactory.createResource(URI_1),
                        graphElementFactory.createLiteral("Abdul"));
            } else {
                graph.add(graphElementFactory.createResource(),
                        graphElementFactory.createResource(URI_1),
                        graphElementFactory.createResource());
            }
        }
        long finishTime = System.currentTimeMillis();
        System.out.println("Testing Add Performance:");
        System.out.println("Adding " + numberOfNodes + " Triples took: " + (finishTime - startTime) + " ms = " +
                ((finishTime - startTime) / 1000) + " s");
        findPerformance(numberOfNodes, graph);
    }

    private void findPerformance(int nodes, Graph graph) throws Exception {
        long startTime = System.currentTimeMillis();
        ClosableIterator itr = graph.find(graphElementFactory.createResource(URI_1),
                graphElementFactory.createResource(URI_1),
                graphElementFactory.createLiteral("Abdul"));
        long finishTime = System.currentTimeMillis();
        System.out.println("\nTesting Find BDB Performance:");
        System.out.println("To find " + itr.next().toString() + " from " + nodes + " Triples took: " +
                (finishTime - startTime) + " ms = " + ((finishTime - startTime) / 1000) + " s");
    }

    private Graph getOnDiskGraph() {
        NodePoolFactory nodePoolFactory = new BdbNodePoolFactory(handler);
        NodeComparator comparator = new NodeComparatorImpl(new NodeTypeComparatorImpl());
        GraphFactory factory = new OrderedGraphFactoryImpl(indexes, nodePoolFactory, comparator);
        return factory.getGraph();
    }

    public static void main(String[] args) throws Exception {
        BdbPerformance bdbPerformance = new BdbPerformance();
        bdbPerformance.testPerformance();
    }
}
