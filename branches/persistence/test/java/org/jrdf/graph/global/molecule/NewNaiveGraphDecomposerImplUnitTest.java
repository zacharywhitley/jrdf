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

package org.jrdf.graph.global.molecule;

import junit.framework.TestCase;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.GroundedTripleComparatorImpl;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.createMolecule;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.createMultiLevelMolecule;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.*;
import org.jrdf.util.ClosableIterator;
import org.jrdf.graph.local.mem.BlankNodeComparator;
import org.jrdf.graph.local.mem.LocalizedBlankNodeComparatorImpl;
import org.jrdf.graph.local.mem.LocalizedNodeComparator;
import org.jrdf.graph.local.mem.LocalizedNodeComparatorImpl;
import org.jrdf.graph.local.mem.NodeComparatorImpl;
import org.jrdf.graph.local.mem.TripleComparatorImpl;
import org.jrdf.set.MemSortedSetFactory;
import org.jrdf.util.NodeTypeComparator;
import org.jrdf.util.NodeTypeComparatorImpl;
import static org.jrdf.util.test.SetUtil.asSet;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

public class NewNaiveGraphDecomposerImplUnitTest extends TestCase {
    private final NodeTypeComparator typeComparator = new NodeTypeComparatorImpl();
    private final LocalizedNodeComparator localNodeComparator = new LocalizedNodeComparatorImpl();
    private final BlankNodeComparator blankNodeComparator = new LocalizedBlankNodeComparatorImpl(localNodeComparator);
    private final NodeComparator nodeComparator = new NodeComparatorImpl(typeComparator, blankNodeComparator);
    private final TripleComparator tripleComparator = new TripleComparatorImpl(nodeComparator);
    private final TripleComparator comparator = new GroundedTripleComparatorImpl(tripleComparator);
    private final NewMoleculeComparator moleculeComparator = new NewMoleculeHeadTripleComparatorImpl(comparator);
    private final MemSortedSetFactory setFactory = new MemSortedSetFactory();
    private final NewMoleculeFactory moleculeFactory = new NewMoleculeFactoryImpl(comparator, moleculeComparator,
        new MoleculeSubsumptionImpl());
    private final NewGraphDecomposer decomposer = new NewNaiveGraphDecomposerImpl(setFactory, moleculeFactory,
        moleculeComparator, comparator);

    public void setUp() throws Exception {
        super.setUp();
        ClosableIterator<Triple> iterator = GRAPH.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        GRAPH.remove(iterator);
    }

    public void testEmptyGraph() throws Exception {
        assertEquals(0, GRAPH.getNumberOfTriples());
        Set<NewMolecule> molecules = decomposer.decompose(GRAPH);
        assertEquals("Unexpected size of molecules", 0, molecules.size());
    }

    public void testGroundedGraph() throws Exception {
        GRAPH.add(R1R1R1, R2R1R1, R2R1R2);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = moleculeFactory.createMolecule(R1R1R1);
        NewMolecule m2 = moleculeFactory.createMolecule(R2R1R1);
        NewMolecule m3 = moleculeFactory.createMolecule(R2R1R2);
        checkMolecules(actualMolecules, m1, m2, m3);
    }

    public void testSimpleDecompose() throws Exception {
        GRAPH.add(R1R1B1, B1R1B2);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = moleculeFactory.createMolecule(R1R1B1, B1R1B2);
        checkMolecules(actualMolecules, m1);
    }

    public void testLinkingTriple() throws GraphException {
        GRAPH.add(B1R1R1, B2R1B1);
        Set<NewMolecule> molecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = createMultiLevelMolecule(asSet(B2R1B1), asSet(B1R1R1), Collections.<Triple>emptySet());
        checkMolecules(molecules, m1);
    }

    public void testOrderingSingleMolecule() throws Exception {
        GRAPH.add(R2R2B2, R2R1B1, B1R1B2);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = createMultiLevelMolecule(asSet(R2R2B2, R2R1B1, B1R1B2), Collections.<Triple>emptySet(),
            Collections.<Triple>emptySet());
        checkMolecules(actualMolecules, m1);
    }

    public void testOrderingMultiMolecule() throws Exception {
        GRAPH.add(R2R2B1, R2R1B2, B1R1B2);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = createMultiLevelMolecule(asSet(R2R2B1, B1R1B2), asSet(R2R1B2), Collections.<Triple>emptySet());
        checkMolecules(actualMolecules, m1);
    }

    public void testNestedBlankNodeDecompose() throws GraphException, GraphElementFactoryException {
        GRAPH.add(B1R1B2, B1R1B3, B2R2R2, B3R2R3);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = createMultiLevelMolecule(asSet(B1R1B2), asSet(B2R2R2), Collections.<Triple>emptySet());
        NewMolecule m2 = createMultiLevelMolecule(asSet(B1R1B3), asSet(B3R2R3), Collections.<Triple>emptySet());
        checkMolecules(actualMolecules, m1, m2);
    }

    public void testCircularBlankNodes() throws GraphElementFactoryException, GraphException {
        GRAPH.add(B1R1B2, B2R2B3, B3R3B1);
        Set<NewMolecule> molecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = createMultiLevelMolecule(asSet(B1R1B2), asSet(B2R2B3), asSet(B3R3B1));
        checkMolecules(molecules, m1);
    }

    public void testNoSimpleLeanification() throws Exception {
        GRAPH.add(B1R1R1, B2R1R1, B3R1R1);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = moleculeFactory.createMolecule(B1R1R1);
        NewMolecule m2 = moleculeFactory.createMolecule(B2R1R1);
        NewMolecule m3 = moleculeFactory.createMolecule(B3R1R1);
        checkMolecules(actualMolecules, m1, m2, m3);
    }

    public void test3LevelMolecule() throws GraphElementFactoryException, GraphException {
        GraphElementFactory fac = GRAPH.getElementFactory();
        BlankNode b1 = fac.createBlankNode();
        BlankNode b2 = fac.createBlankNode();
        BlankNode b3 = fac.createBlankNode();
        BlankNode b4 = fac.createBlankNode();
        PredicateNode p1 = fac.createURIReference(URI.create("urn:p1"));
        PredicateNode p2 = fac.createURIReference(URI.create("urn:p2"));
        PredicateNode p3 = fac.createURIReference(URI.create("urn:p3"));
        ObjectNode o1 = fac.createURIReference(URI.create("urn:o1"));
        ObjectNode o2 = fac.createURIReference(URI.create("urn:o2"));
        GRAPH.add(b1, p1, b2);
        GRAPH.add(b2, p2, b3);
        GRAPH.add(b3, p3, o1);

        GRAPH.add(b1, p1, b2);
        GRAPH.add(b2, p2, b4);
        GRAPH.add(b4, p3, o2);
        Set<NewMolecule> molecules = decomposer.decompose(GRAPH);
    }

    public void testSingleNestingSubjects() throws Exception {
        GRAPH.add(B1R1R1, B1R2R2, B2R2R1, B2R2R2);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = createMolecule(B1R1R1, B1R2R2);
        NewMolecule m2 = createMolecule(B2R2R1, B2R2R2);
        checkMolecules(actualMolecules, m1, m2);
    }

    public void testSingleNestingSubjectsExtraTriples() throws Exception {
        GRAPH.add(B1R1R1, B1R2R2, B2R2R1, B2R2R2, R1R1R1, R2R1R1, R2R1R2);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = createMolecule(B1R1R1, B1R2R2);
        NewMolecule m2 = createMolecule(B2R2R1, B2R2R2);
        NewMolecule m3 = moleculeFactory.createMolecule(R1R1R1);
        NewMolecule m4 = moleculeFactory.createMolecule(R2R1R1);
        NewMolecule m5 = moleculeFactory.createMolecule(R2R1R2);
        checkMolecules(actualMolecules, m1, m2, m3, m4, m5);
    }

    public void testSingleNestingObjects() throws Exception {
        GRAPH.add(R1R1B1, R2R1B1, R1R2B2, R2R2B2);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = createMolecule(R1R1B1, R2R1B1);
        NewMolecule m2 = createMolecule(R1R2B2, R2R2B2);
        checkMolecules(actualMolecules, m1, m2);
    }

    public void testSingleNestingObjectsExtraTriples() throws Exception {
        GRAPH.add(R1R1B1, R2R1B1, R1R2B2, R2R2B2, R1R1R1, R2R1R1, R2R1R2);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = createMolecule(R1R1B1, R2R1B1);
        NewMolecule m2 = createMolecule(R1R2B2, R2R2B2);
        NewMolecule m3 = createMolecule(R1R1R1);
        NewMolecule m4 = createMolecule(R2R1R1);
        NewMolecule m5 = createMolecule(R2R1R2);
        checkMolecules(actualMolecules, m1, m2, m3, m4, m5);
    }

    public void testSingleNestingBothSides() throws Exception {
        GRAPH.add(R1R1B1, R1R2B2, R2R1B1, R2R2B2, B1R1R1, B1R2R2, B2R2R1);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = createMolecule(R1R1B1, R2R1B1, B1R1R1, B1R2R2);
        NewMolecule m2 = createMolecule(R1R2B2, R2R2B2, B2R2R1);
        checkMolecules(actualMolecules, m1, m2);
    }

    public void testLinkTwoGroups() throws Exception {
        GRAPH.add(R1R1B1, R1R2B2, R2R1B1, R2R2B2, B1R1R1, B1R2R2, B2R2R1, B1R1B2);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = createMultiLevelMolecule(asSet(R1R1B1, R2R1B1, B1R1R1, B1R2R2, B1R1B2),
            asSet(R1R2B2, R2R2B2, B2R2R1), Collections.<Triple>emptySet());
        checkMolecules(actualMolecules, m1);
    }

    public void testLinkThreeGroups() throws Exception {
        GRAPH.add(R1R1B1, R1R2B2, R2R1B1, R2R2B2, B1R1R1, B1R2R2, B2R2R1, B1R1B2, B2R2B3, B3R2R3);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = createMultiLevelMolecule(asSet(R1R1B1, R2R1B1, B1R1R1, B1R2R2, B1R1B2),
            asSet(R1R2B2, R2R2B2, B2R2R1, B2R2B3), asSet(B3R2R3));
        checkMolecules(actualMolecules, m1);
    }

    private void checkMolecules(Set<NewMolecule> actualMolecules, NewMolecule... expectedMolecules) {
        assertEquals("Unexpected size of molecules", expectedMolecules.length, actualMolecules.size());
        assertEquals(asSet(expectedMolecules), actualMolecules);
    }
}
