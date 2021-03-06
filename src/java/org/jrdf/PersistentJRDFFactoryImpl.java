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

import org.jrdf.collection.IteratorTrackingCollectionFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphFactory;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.graph.local.index.longindex.sesame.LongIndexSesameSync;
import org.jrdf.graph.local.index.nodepool.NodePoolFactory;
import org.jrdf.sparql.SparqlConnection;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.bdb.BdbEnvironmentHandlerImpl;
import org.jrdf.util.btree.BTree;
import org.jrdf.util.btree.BTreeFactory;
import org.jrdf.util.btree.BTreeFactoryImpl;

/**
 * Uses BDB JE and a BTree to store graphs, that can be accessed by name (using hasGraph and getGraph). Returns
 * sorted results.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public final class PersistentJRDFFactoryImpl implements PersistentJRDFFactory {
    private final DirectoryHandler dirHandler;
    private BTreeFactory btreeFactory = new BTreeFactoryImpl();
    private BasePersistentJRDFFactory base;

    private PersistentJRDFFactoryImpl(DirectoryHandler newHandler) {
        this.dirHandler = newHandler;
        this.base = new BasePersistentJRDFFactoryImpl(newHandler, new BdbEnvironmentHandlerImpl(dirHandler));
        refresh();
    }

    public static PersistentJRDFFactory getFactory(DirectoryHandler handler) {
        return new PersistentJRDFFactoryImpl(handler);
    }

    public SparqlConnection getNewSparqlConnection() {
        return base.createSparqlConnection();
    }

    public boolean hasGraph(String name) {
        return base.hasGraph(name);
    }

    public Graph getGraph() {
        Graph graph = getGraph("default");
        graph.clear();
        return graph;
    }

    public Graph getGraph(String name) {
        if (base.hasGraph(name)) {
            return getExistingGraph(name);
        } else {
            return getNewGraph(name);
        }
    }

    public Graph getNewGraph(String name) {
        if (base.hasGraph(name)) {
            throw new IllegalArgumentException("Graph already exists: " + name);
        }
        long graphNumber = base.addNewGraph(name);
        return getGraph(graphNumber);
    }

    public Graph getExistingGraph(String name) throws IllegalArgumentException {
        if (!base.hasGraph(name)) {
            throw new IllegalArgumentException("Cannot get graph named: " + name);
        } else {
            return getGraph(base.getGraphId(name));
        }
    }

    public void refresh() {
        base.refresh();
    }

    public void close() {
        base.close();
    }

    private Graph getGraph(long graphNumber) {
        LongIndex[] indexes = createIndexes(graphNumber);
        final NodePoolFactory nodePool = base.createNodePoolFactory(graphNumber);
        IteratorTrackingCollectionFactory collectionFactory = base.createCollectionFactory(graphNumber);
        GraphFactory graphFactory = base.createGraphFactory(indexes, nodePool, collectionFactory);
        return graphFactory.getGraph();
    }

    private LongIndex[] createIndexes(long graphNumber) {
        BTree[] bTrees = createBTrees(graphNumber);
        return new LongIndex[]{newIndex(bTrees[0]), newIndex(bTrees[1]), newIndex(bTrees[2])};
    }

    private BTree[] createBTrees(long graphNumber) {
        return new BTree[]{newBtree("spo" + graphNumber), newBtree("pos" + graphNumber), newBtree("osp" + graphNumber)};
    }

    private BTree newBtree(String btreeName) {
        return btreeFactory.createBTree(dirHandler, btreeName);
    }

    private LongIndexSesameSync newIndex(BTree bTree) {
        return new LongIndexSesameSync(bTree);
    }
}
