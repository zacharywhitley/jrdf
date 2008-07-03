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

package org.jrdf.graph.global;

import org.jrdf.JRDFFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.global.index.adapter.LongIndexAdapter;
import org.jrdf.graph.global.index.longindex.bdb.MoleculeIndexBdb;
import org.jrdf.graph.local.OrderedGraphFactoryImpl;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.graph.local.index.nodepool.NodePoolFactory;
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

import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.Set;

/**
 * Uses default in memory constructors to create JRDF entry points.  Returns sorted results.
 *
 * @author Andrew Newman
 * @version $Id: TestJRDFFactory.java 533 2006-06-04 17:50:31 +1000 (Sun, 04 Jun 2006) newmana $
 */
public final class SortedDiskGlobalJRDFFactory implements JRDFFactory {
    private static final QueryFactory QUERY_FACTORY = new QueryFactoryImpl();
    private static final QueryBuilder BUILDER = QUERY_FACTORY.createQueryBuilder();
    private static final QueryEngine QUERY_ENGINE = QUERY_FACTORY.createQueryEngine();
    private static final DirectoryHandler HANDLER = new TempDirectoryHandler();
    private static final BdbEnvironmentHandler BDB_HANDLER = new BdbEnvironmentHandlerImpl(HANDLER);
    private static long graphNumber;
    private Set<LongIndex> openIndexes = new HashSet<LongIndex>();
    private Set<NodePoolFactory> openFactories = new HashSet<NodePoolFactory>();
    private Set<MapFactory> openMapFactories = new HashSet<MapFactory>();

    private SortedDiskGlobalJRDFFactory() {
    }

    public static JRDFFactory getFactory() {
        return new SortedDiskGlobalJRDFFactory();
    }

    public void refresh() {
    }

    public Graph getNewGraph() {
        graphNumber++;
        MapFactory factory = new BdbMapFactory(BDB_HANDLER, "database" + graphNumber);
        LongIndex[] indexes = createIndexes(factory);
        NodePoolFactory nodePoolFactory = new BdbNodePoolFactory(
                new BdbEnvironmentHandlerImpl(new TempDirectoryHandler()), graphNumber);
        openIndexes.addAll(asList(indexes));
        openMapFactories.add(factory);
        openFactories.add(nodePoolFactory);
        return new OrderedGraphFactoryImpl(indexes, nodePoolFactory).getGraph();
    }

    public UrqlConnection getNewUrqlConnection() {
        return new UrqlConnectionImpl(BUILDER, QUERY_ENGINE);
    }

    public void close() {
        for (LongIndex index : openIndexes) {
            index.close();
        }
        for (MapFactory factory : openMapFactories) {
            factory.close();
        }
        for (NodePoolFactory openFactory : openFactories) {
            openFactory.close();
        }
        openIndexes.clear();
        openFactories.clear();
        openMapFactories.clear();
    }

    private LongIndex[] createIndexes(MapFactory factory) {
        return new LongIndex[]{new LongIndexAdapter(new MoleculeIndexBdb(factory)),
            new LongIndexAdapter(new MoleculeIndexBdb(factory)),
            new LongIndexAdapter(new MoleculeIndexBdb(factory))};
    }
}