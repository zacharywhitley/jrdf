/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

package org.jrdf;

import org.jrdf.collection.CollectionFactory;
import org.jrdf.collection.BdbCollectionFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.graph.local.index.nodepool.NodePoolFactory;
import org.jrdf.graph.local.index.nodepool.bdb.BdbNodePoolFactory;
import org.jrdf.query.QueryFactory;
import org.jrdf.query.QueryFactoryImpl;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.execute.QueryEngine;
import org.jrdf.urql.UrqlConnection;
import org.jrdf.urql.UrqlConnectionImpl;
import org.jrdf.urql.builder.QueryBuilder;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.Models;
import org.jrdf.util.ModelsImpl;
import org.jrdf.util.bdb.BdbEnvironmentHandler;
import static org.jrdf.writer.Writer.writeNTriples;
import org.jrdf.parser.RdfReader;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class BasePersistentJRDFFactoryImpl implements BasePersistentJRDFFactory {
    private static final QueryFactory QUERY_FACTORY = new QueryFactoryImpl();
    private static final QueryEngine<Relation> QUERY_ENGINE = QUERY_FACTORY.createQueryEngine();
    private static final QueryBuilder BUILDER = QUERY_FACTORY.createQueryBuilder();
    private final Set<NodePoolFactory> openNodePoolFactories = new HashSet<NodePoolFactory>();
    private final BdbEnvironmentHandler bdbHandler;
    private CollectionFactory collectionFactory;
    private Models models;
    private File file;
    private Graph modelsGraph;

    public BasePersistentJRDFFactoryImpl(DirectoryHandler newHandler, BdbEnvironmentHandler newBdbHandler) {
        this.bdbHandler = newBdbHandler;
        newHandler.makeDir();
        file = new File(newHandler.getDir(), "graphs.nt");
    }

    public UrqlConnection createUrqlConnection() {
        return new UrqlConnectionImpl(BUILDER, QUERY_ENGINE);
    }

    public NodePool createNodePool(long graphNumber) {
        NodePoolFactory nodePoolFactory = new BdbNodePoolFactory(bdbHandler, graphNumber);
        final NodePool nodePool = nodePoolFactory.openExistingNodePool();
        openNodePoolFactories.add(nodePoolFactory);
        return nodePool;
    }

    public CollectionFactory createCollectionFactory(long graphNumber) {
        collectionFactory = new BdbCollectionFactory(bdbHandler, "collection" + graphNumber);
        return collectionFactory;
    }

    public boolean hasGraph(String name) {
        return models.hasGraph(name);
    }

    public long addNewGraph(String name) {
        if (models.getId(name) != 0) {
            throw new IllegalArgumentException("Graph " + name + " already exists");
        }
        long graphNumber = models.addGraph(name);
        writeNTriples(file, modelsGraph);
        return graphNumber;
    }

    public long getGraphId(String name) {
        return models.getId(name);
    }

    public void refresh() {
        modelsGraph = new RdfReader().parseNTriples(file);
        models = new ModelsImpl(modelsGraph);
    }

    public void close() {
        for (NodePoolFactory openFactory : openNodePoolFactories) {
            openFactory.close();
        }
        if (collectionFactory != null) {
            collectionFactory.close();
        }
        openNodePoolFactories.clear();
    }
}
