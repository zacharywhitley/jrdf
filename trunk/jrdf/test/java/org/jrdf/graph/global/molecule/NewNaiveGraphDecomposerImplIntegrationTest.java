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

package org.jrdf.graph.global.molecule;

import junit.framework.TestCase;
import org.jrdf.JRDFFactory;
import org.jrdf.SortedMemoryJRDFFactory;
import org.jrdf.set.MemSortedSetFactory;
import org.jrdf.util.NodeTypeComparator;
import org.jrdf.util.NodeTypeComparatorImpl;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.Resource;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.GroundedTripleComparatorImpl;
import org.jrdf.graph.global.molecule.mem.NewMoleculeComparator;
import org.jrdf.graph.global.molecule.mem.NewMoleculeHeadTripleComparatorImpl;
import org.jrdf.graph.global.molecule.mem.NewMoleculeFactory;
import org.jrdf.graph.global.molecule.mem.NewMoleculeFactoryImpl;
import org.jrdf.graph.global.molecule.mem.NewGraphDecomposer;
import org.jrdf.graph.global.molecule.mem.NewNaiveGraphDecomposerImpl;
import org.jrdf.graph.global.molecule.mem.NewMolecule;
import org.jrdf.graph.local.LocalizedNodeComparator;
import org.jrdf.graph.local.LocalizedNodeComparatorImpl;
import org.jrdf.graph.local.BlankNodeComparator;
import org.jrdf.graph.local.LocalizedBlankNodeComparatorImpl;
import org.jrdf.graph.local.NodeComparatorImpl;
import org.jrdf.graph.local.TripleComparatorImpl;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.createMolecule;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.GRAPH;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.mergeMolecules;

import static java.net.URI.create;
import java.util.Set;

public class NewNaiveGraphDecomposerImplIntegrationTest extends TestCase {
    private final JRDFFactory factory = SortedMemoryJRDFFactory.getFactory();
    private final Graph graph = factory.getNewGraph();
    private final GraphElementFactory elementFactory = graph.getElementFactory();
    private final NodeTypeComparator typeComparator = new NodeTypeComparatorImpl();
    private final LocalizedNodeComparator localNodeComparator = new LocalizedNodeComparatorImpl();
    private final BlankNodeComparator blankNodeComparator = new LocalizedBlankNodeComparatorImpl(localNodeComparator);
    private final NodeComparator nodeComparator = new NodeComparatorImpl(typeComparator, blankNodeComparator);
    private final TripleComparator tripleComparator = new TripleComparatorImpl(nodeComparator);
    private final TripleComparator comparator = new GroundedTripleComparatorImpl(tripleComparator);
    private final NewMoleculeComparator moleculeComparator = new NewMoleculeHeadTripleComparatorImpl(comparator);
    private final MemSortedSetFactory setFactory = new MemSortedSetFactory();
    private final NewMoleculeFactory moleculeFactory = new NewMoleculeFactoryImpl(moleculeComparator);
    private final NewGraphDecomposer decomposer = new NewNaiveGraphDecomposerImpl(setFactory, moleculeFactory,
        moleculeComparator, comparator);
    private final BlankNodeMapper mapper = new BlankNodeMapperImpl();
    private final MergeSubmolecules globalMerger = new MergeSubmoleculesImpl(comparator, moleculeComparator,
        moleculeFactory, new MoleculeSubsumptionImpl());
    private final LocalMergeSubmolecules localMerger = new MergeLocalSubmoleculesImpl(globalMerger, moleculeFactory);

    public void test3LevelMolecule() throws GraphException {
        GraphElementFactory fac = GRAPH.getElementFactory();
        TripleFactory tfac = GRAPH.getTripleFactory();
        Resource b1 = fac.createResource();
        Resource b2 = fac.createResource();
        Resource b3 = fac.createResource();
        Resource b4 = fac.createResource();
        URIReference u1 = fac.createURIReference(create("urn:p1"));
        URIReference u2 = fac.createURIReference(create("urn:p2"));
        URIReference u3 = fac.createURIReference(create("urn:p3"));
        URIReference u4 = fac.createURIReference(create("urn:o1"));
        URIReference u5 = fac.createURIReference(create("urn:o2"));
        b1.addValue(u1, b2);
        b2.addValue(u2, b3);
        b3.addValue(u3, u4);
        b2.addValue(u2, b4);
        b4.addValue(u3, u5);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule branch1 = createMolecule(tfac.createTriple(b2, u2, b3));
        NewMolecule leaf1 = createMolecule(tfac.createTriple(b3, u3, u4));
        NewMolecule branch2 = createMolecule(tfac.createTriple(b2, u2, b4));
        NewMolecule leaf2 = createMolecule(tfac.createTriple(b4, u3, u5));
        NewMolecule m1 = mergeMolecules(branch1, leaf1);
        NewMolecule m2 = mergeMolecules(branch2, leaf2);
        NewMolecule m = moleculeFactory.createMolecule(tfac.createTriple(b1, u1, b2));
        m.add(tfac.createTriple(b1, u1, b2), m1);
        m.add(tfac.createTriple(b1, u1, b2), m2);
        // merge and test for values.
    }
}
