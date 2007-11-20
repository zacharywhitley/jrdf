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

package org.jrdf;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphFactory;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.graph.local.index.longindex.db4o.LongIndexDb4o;
import org.jrdf.graph.local.index.nodepool.NodePoolFactory;
import org.jrdf.graph.local.index.nodepool.db4o.Db4oNodePoolFactory;
import org.jrdf.graph.local.mem.OrderedGraphFactoryImpl;
import org.jrdf.map.Db4oMapFactory;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.map.MapFactory;
import org.jrdf.util.TempDirectoryHandler;
import org.jrdf.query.QueryFactory;
import org.jrdf.query.QueryFactoryImpl;
import org.jrdf.query.execute.QueryEngine;
import org.jrdf.sparql.SparqlConnection;
import org.jrdf.sparql.SparqlConnectionImpl;
import org.jrdf.sparql.builder.QueryBuilder;

import java.util.Set;
import java.util.HashSet;

/**
 * Uses default in memory constructors to create JRDF entry points.  Returns sorted results.
 *
 * @author Andrew Newman
 * @version $Id: TestJRDFFactory.java 533 2006-06-04 17:50:31 +1000 (Sun, 04 Jun 2006) newmana $
 */
public final class SortedDb4oJRDFFactory implements JRDFFactory {
    private static final QueryFactory QUERY_FACTORY = new QueryFactoryImpl();
    private static final QueryEngine QUERY_ENGINE = QUERY_FACTORY.createQueryEngine();
    private static final QueryBuilder BUILDER = QUERY_FACTORY.createQueryBuilder();
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler();
    private static long graphNumber;
    private GraphFactory orderedGraphFactory;
    private Set<MapFactory> openMaps = new HashSet<MapFactory>();
    private Set<NodePoolFactory> openFactories = new HashSet<NodePoolFactory>();

    private SortedDb4oJRDFFactory() {
    }

    public static JRDFFactory getFactory() {
        return new SortedDb4oJRDFFactory();
    }

    public void refresh() {
    }

    public Graph getNewGraph() {
        graphNumber++;
        MapFactory factory1 = new Db4oMapFactory(HANDLER, "spo" + graphNumber);
        MapFactory factory2 = new Db4oMapFactory(HANDLER, "pos" + graphNumber);
        MapFactory factory3 = new Db4oMapFactory(HANDLER, "osp" + graphNumber);
        LongIndex[] indexes = new LongIndex[]{new LongIndexDb4o(factory1), new LongIndexDb4o(factory2),
            new LongIndexDb4o(factory3)};
        NodePoolFactory nodePoolFactory = new Db4oNodePoolFactory(HANDLER, graphNumber);
        openMaps.add(factory1);
        openMaps.add(factory2);
        openMaps.add(factory3);
        openFactories.add(nodePoolFactory);
        orderedGraphFactory = new OrderedGraphFactoryImpl(indexes, nodePoolFactory);
        return orderedGraphFactory.getGraph();
    }

    public SparqlConnection getNewSparqlConnection() {
        return new SparqlConnectionImpl(BUILDER, QUERY_ENGINE);
    }

    public void close() {
        for (MapFactory openMap : openMaps) {
            openMap.close();
        }
        for (NodePoolFactory openFactory : openFactories) {
            openFactory.close();
        }
        openMaps.clear();
        openFactories.clear();
    }
}
