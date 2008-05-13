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

package org.jrdf.graph.global.index;

import junit.framework.TestCase;
import org.jrdf.graph.Graph;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.Resource;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.GroundedTripleComparatorImpl;
import org.jrdf.graph.global.molecule.mem.NewMolecule;
import org.jrdf.graph.global.molecule.mem.NewMoleculeComparator;
import org.jrdf.graph.global.molecule.mem.NewMoleculeFactory;
import org.jrdf.graph.global.molecule.mem.NewMoleculeFactoryImpl;
import org.jrdf.graph.global.molecule.mem.NewMoleculeHeadTripleComparatorImpl;
import org.jrdf.graph.local.BlankNodeComparator;
import org.jrdf.graph.local.GraphImpl;
import org.jrdf.graph.local.LocalizedBlankNodeComparatorImpl;
import org.jrdf.graph.local.LocalizedNodeComparator;
import org.jrdf.graph.local.LocalizedNodeComparatorImpl;
import org.jrdf.graph.local.NodeComparatorImpl;
import org.jrdf.graph.local.ReadWriteGraphImpl;
import org.jrdf.graph.local.TripleComparatorImpl;
import org.jrdf.graph.local.index.graphhandler.GraphHandler;
import org.jrdf.graph.local.index.graphhandler.GraphHandler012;
import org.jrdf.graph.local.index.graphhandler.GraphHandler120;
import org.jrdf.graph.local.index.graphhandler.GraphHandler201;
import org.jrdf.graph.local.index.longindex.LongIndex;
import org.jrdf.graph.local.index.longindex.mem.LongIndexMem;
import org.jrdf.graph.local.index.nodepool.Localizer;
import org.jrdf.graph.local.index.nodepool.LocalizerImpl;
import org.jrdf.graph.local.index.nodepool.NodePool;
import org.jrdf.graph.local.index.nodepool.NodePoolFactory;
import org.jrdf.graph.local.index.nodepool.StringNodeMapper;
import org.jrdf.graph.local.index.nodepool.StringNodeMapperFactoryImpl;
import org.jrdf.graph.local.index.nodepool.bdb.BdbNodePoolFactory;
import org.jrdf.graph.local.iterator.LocalIteratorFactory;
import org.jrdf.util.NodeTypeComparator;
import org.jrdf.util.NodeTypeComparatorImpl;
import org.jrdf.util.TempDirectoryHandler;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.bdb.BdbEnvironmentHandler;
import org.jrdf.util.bdb.BdbEnvironmentHandlerImpl;

import static java.net.URI.create;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MoleculeGraphImplIntegrationTest extends TestCase {
    private final NodeTypeComparator typeComparator = new NodeTypeComparatorImpl();
    private final LocalizedNodeComparator localNodeComparator = new LocalizedNodeComparatorImpl();
    private final BlankNodeComparator blankNodeComparator = new LocalizedBlankNodeComparatorImpl(localNodeComparator);
    private final NodeComparator nodeComparator = new NodeComparatorImpl(typeComparator, blankNodeComparator);
    private final TripleComparator tripleComparator = new TripleComparatorImpl(nodeComparator);
    private final TripleComparator comparator = new GroundedTripleComparatorImpl(tripleComparator);
    private final NewMoleculeComparator moleculeComparator = new NewMoleculeHeadTripleComparatorImpl(comparator);
    private final NewMoleculeFactory moleculeFactory = new NewMoleculeFactoryImpl(moleculeComparator);
    private final TempDirectoryHandler HANDLER = new TempDirectoryHandler();
    private final BdbEnvironmentHandler BDB_HANDLER = new BdbEnvironmentHandlerImpl(HANDLER);
    private final NodePoolFactory nodePoolFactory = new BdbNodePoolFactory(BDB_HANDLER, 0);

    public void testSimpleAddRemove() throws Exception {
        NewMoleculeIndex<Long> spom = new NewMoleculeIndexImpl(new HashMap<Long, Map<Long, Map<Long, Set<Long>>>>());
        NewMoleculeIndex<Long> posm = new NewMoleculeIndexImpl(new HashMap<Long, Map<Long, Map<Long, Set<Long>>>>());
        NewMoleculeIndex<Long> ospm = new NewMoleculeIndexImpl(new HashMap<Long, Map<Long, Map<Long, Set<Long>>>>());
        NewMoleculeIndex<Long>[] indexes = new NewMoleculeIndex[]{spom, posm, ospm};
        NewMoleculeStructureIndex<Long> structureIndex = new NewMoleculeStructureIndexImpl(
            new HashMap<Long, Map<Long, Map<Long, Map<Long, Set<Long>>>>>());
        WritableIndex<Long> writeIndex = new WritableIndexImpl(indexes, structureIndex);
        ReadableIndex<Long> readIndex = new ReadableIndexImpl(indexes, structureIndex);
        NodePool nodePool = nodePoolFactory.createNodePool();
        Localizer localizer = new LocalizerImpl(nodePool, stringMapper());
        MoleculeLocalizer moleculeLocalizer = new MoleculeLocalizerImpl(localizer);
        MoleculeGraph moleculeGraph = new MoleculeGraphImpl(writeIndex, readIndex, moleculeLocalizer);
        LongIndex[] longIndexes = new LongIndex[]{new LongIndexMem(), new LongIndexMem(), new LongIndexMem()};
        GraphHandler[] graphHandlers = new GraphHandler[]{new GraphHandler012(longIndexes, nodePool),
            new GraphHandler120(longIndexes, nodePool), new GraphHandler201(longIndexes, nodePool)};
        Graph graph = new GraphImpl(longIndexes, nodePool, new ReadWriteGraphImpl(longIndexes, nodePool,
            new LocalIteratorFactory(longIndexes, graphHandlers, nodePool)));

        Resource b1 = graph.getElementFactory().createResource();
        Resource r1 = graph.getElementFactory().createResource(create("urn:foo"));
        NewMolecule molecule = moleculeFactory.createMolecule(b1.asTriple(r1, b1));
        moleculeGraph.add(molecule);
        assertEquals(1, spom.getSize());
//        System.err.println("Yo: " + spom.getSize());
        ClosableIterator<Map.Entry<Long,Map<Long,Map<Long,Set<Long>>>>> iterator = spom.iterator();
        Map.Entry<Long, Map<Long, Map<Long, Set<Long>>>> mapEntry = iterator.next();
//        System.err.println("Entry: " + mapEntry);
        ClosableIterator<Map.Entry<Long, Map<Long, Map<Long, Map<Long, Set<Long>>>>>> iterator1 =
            structureIndex.iterator();
//        System.err.println("Got: " + iterator1.next());
        moleculeGraph.delete(molecule);
//        System.err.println("Yo: " + spom.getSize());
        iterator = spom.iterator();
        assertFalse(iterator.hasNext());
    }

    private StringNodeMapper stringMapper() {
        return new StringNodeMapperFactoryImpl().createMapper();
    }
}
