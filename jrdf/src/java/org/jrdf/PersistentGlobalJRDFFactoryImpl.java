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

package org.jrdf;

import org.jrdf.collection.CollectionFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.graph.global.MoleculeGraphImpl;
import org.jrdf.graph.global.MoleculeLocalizer;
import org.jrdf.graph.global.MoleculeLocalizerImpl;
import org.jrdf.graph.global.index.ReadableIndex;
import org.jrdf.graph.global.index.ReadableIndexImpl;
import org.jrdf.graph.global.index.WritableIndex;
import org.jrdf.graph.global.index.WritableIndexImpl;
import org.jrdf.graph.global.index.adapter.LongIndexAdapter;
import org.jrdf.graph.global.index.longindex.MoleculeStructureIndex;
import org.jrdf.graph.global.index.longindex.sesame.MoleculeStructureIndexSesameSync;
import org.jrdf.graph.local.OrderedGraphFactoryImpl;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.graph.local.index.nodepool.Localizer;
import org.jrdf.graph.local.index.nodepool.LocalizerImpl;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.graph.local.index.nodepool.StringNodeMapper;
import org.jrdf.graph.local.index.nodepool.StringNodeMapperFactoryImpl;
import org.jrdf.urql.UrqlConnection;
import org.jrdf.util.DirectoryHandler;
import org.jrdf.util.bdb.BdbEnvironmentHandlerImpl;
import org.jrdf.util.btree.BTreeFactory;
import org.jrdf.util.btree.BTreeFactoryImpl;

import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.Set;

/**
 * Uses default in memory constructors to create JRDF entry points.  Returns sorted results.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public final class PersistentGlobalJRDFFactoryImpl implements PersistentGlobalJRDFFactory {
    private static final StringNodeMapper STRING_MAPPER = new StringNodeMapperFactoryImpl().createMapper();
    private final Set<MoleculeStructureIndex<Long>> openStructureIndexes = new HashSet<MoleculeStructureIndex<Long>>();
    private final DirectoryHandler handler;
    private BTreeFactory btreeFactory = new BTreeFactoryImpl();
    private BasePersistentJRDFFactory base;

    private PersistentGlobalJRDFFactoryImpl(DirectoryHandler newHandler) {
        this.handler = newHandler;
        this.base = new BasePersistentJRDFFactoryImpl(newHandler, new BdbEnvironmentHandlerImpl(handler));
        refresh();
    }

    public static PersistentGlobalJRDFFactory getFactory(DirectoryHandler handler) {
        return new PersistentGlobalJRDFFactoryImpl(handler);
    }

    public UrqlConnection getNewUrqlConnection() {
        return base.createUrqlConnection();
    }

    public boolean hasGraph(String name) {
        return base.hasGraph(name);
    }

    public MoleculeGraph getGraph() {
        return getGraph("default");
    }

    public MoleculeGraph getGraph(String name) {
        if (base.hasGraph(name)) {
            return getExistingGraph(name);
        } else {
            return getNewGraph(name);
        }
    }

    public MoleculeGraph getNewGraph(String name) {
        long graphNumber = base.addNewGraph(name);
        return getGraph(graphNumber);
    }

    public MoleculeGraph getExistingGraph(String name) throws IllegalArgumentException {
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
        for (MoleculeStructureIndex<Long> index : openStructureIndexes) {
            index.close();
        }
        openStructureIndexes.clear();
    }

    private MoleculeGraph getGraph(long graphNumber) {
        final NodePool nodePool = base.createNodePool(graphNumber);
        MoleculeStructureIndex<Long>[] structureIndexes = createMoleculeStructureIndexes(graphNumber);
        ReadableIndex<Long> readIndex = new ReadableIndexImpl(structureIndexes);
        WritableIndex<Long> writeIndex = new WritableIndexImpl(structureIndexes);
        LongIndex[] longIndexes = new LongIndex[]{new LongIndexAdapter(structureIndexes[0]),
            new LongIndexAdapter(structureIndexes[1]), new LongIndexAdapter(structureIndexes[2])};
        CollectionFactory collectionFactory = base.createCollectionFactory(graphNumber);
        Graph graph = new OrderedGraphFactoryImpl(longIndexes, nodePool, collectionFactory).getGraph();
        final long curMaxMoleculeId = readIndex.getMaxMoleculeId();
        Localizer localizer = new LocalizerImpl(nodePool, STRING_MAPPER);
        MoleculeLocalizer moleculeLocalizer = new MoleculeLocalizerImpl(localizer, curMaxMoleculeId);
        return new MoleculeGraphImpl(writeIndex, readIndex, moleculeLocalizer, graph, nodePool);
    }

    private MoleculeStructureIndex<Long>[] createMoleculeStructureIndexes(long graphNumber) {
        MoleculeStructureIndex<Long>[] indexes = new MoleculeStructureIndexSesameSync[] {
            new MoleculeStructureIndexSesameSync(btreeFactory.createQuinBTree(handler, "spomd" + graphNumber)),
            new MoleculeStructureIndexSesameSync(btreeFactory.createQuinBTree(handler, "posmd" + graphNumber)),
            new MoleculeStructureIndexSesameSync(btreeFactory.createQuinBTree(handler, "ospmd" + graphNumber)),
            new MoleculeStructureIndexSesameSync(btreeFactory.createQuinBTree(handler, "dmspo" + graphNumber)),
        };
        openStructureIndexes.addAll(asList(indexes));
        return indexes;
    }
}