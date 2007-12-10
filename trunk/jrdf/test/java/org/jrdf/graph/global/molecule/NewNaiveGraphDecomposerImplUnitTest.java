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
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.*;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.*;
import org.jrdf.graph.local.iterator.ClosableIterator;
import org.jrdf.set.MemSortedSetFactory;
import static org.jrdf.util.test.SetUtil.asSet;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

public class NewNaiveGraphDecomposerImplUnitTest extends TestCase {
    private final TripleComparator comparator = new GroundedTripleComparatorFactoryImpl().newComparator();
    private final NewMoleculeComparator moleculeComparator = new NewMoleculeComparatorImpl(comparator);
    private final MemSortedSetFactory setFactory = new MemSortedSetFactory();
    private final NewMoleculeFactory moleculeFactory = new NewMoleculeFactoryImpl(comparator, moleculeComparator);
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
        NewMolecule m1 = moleculeFactory.createMolecule(r1r1r1);
        NewMolecule m2 = moleculeFactory.createMolecule(r2r1r1);
        NewMolecule m3 = moleculeFactory.createMolecule(r2r1r2);
        checkMolecules(actualMolecules, m1, m2, m3);
    }

    // TODO: incorrect decomposition of graph!
//    public void testNestedBlankNodeDecompose() throws GraphException, GraphElementFactoryException {
//        GraphElementFactory fac = GRAPH.getElementFactory();
//        TripleFactory tFac = GRAPH.getTripleFactory();
//        URIReference ref1 = fac.createURIReference(URI.create("urn:ref1"));
//        URIReference ref2 = fac.createURIReference(URI.create("urn:ref2"));
//
//        BlankNode bn1 = fac.createBlankNode();
//        BlankNode bn2 = fac.createBlankNode();
//        BlankNode bn3 = fac.createBlankNode();
//
//        Triple t00 = tFac.createTriple(bn1, ref2, bn2);
//        Triple t01 = tFac.createTriple(bn1, ref2, bn3);
//        Triple t1 = tFac.createTriple(bn2, ref1, ref1);
//        Triple t2 = tFac.createTriple(bn3, ref1, ref2);
//
//        GRAPH.add(t00, t01, t1, t2);
//        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
//        NewMolecule m1 = createMultiLevelMolecule(asSet(t00), asSet(t1), Collections.<Triple>emptySet());
//        NewMolecule m2 = createMultiLevelMolecule(asSet(t01), asSet(t2), Collections.<Triple>emptySet());
//        checkMolecules(actualMolecules, m1, m2);
//    }
    
    public void testSimpleLeanification() throws Exception {
        GRAPH.add(B1R1R1, B2R1R1, B3R1R1);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = moleculeFactory.createMolecule(b1r1r1);
        checkMolecules(actualMolecules, m1);
    }

    public void testSingleNestingSubjects() throws Exception {
        GRAPH.add(B1R1R1, B1R2R2, B2R2R1, B2R2R2);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = createMolecule(b1r1r1, b1r2r2);
        NewMolecule m2 = createMolecule(b2r2r1, b2r2r2);
        checkMolecules(actualMolecules, m1, m2);
    }

    public void testSingleNestingSubjectsExtraTriples() throws Exception {
        GRAPH.add(B1R1R1, B1R2R2, B2R2R1, B2R2R2, R1R1R1, R2R1R1, R2R1R2);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = createMolecule(b1r1r1, b1r2r2);
        NewMolecule m2 = createMolecule(b2r2r1, b2r2r2);
        NewMolecule m3 = moleculeFactory.createMolecule(R1R1R1);
        NewMolecule m4 = moleculeFactory.createMolecule(R2R1R1);
        NewMolecule m5 = moleculeFactory.createMolecule(R2R1R2);
        checkMolecules(actualMolecules, m1, m2, m3, m4, m5);
    }

    public void testSingleNestingObjects() throws Exception {
        GRAPH.add(R1R1B1, R2R1B1, R1R2B2, R2R2B2);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = createMolecule(r1r1b1, r2r1b1);
        NewMolecule m2 = createMolecule(r1r2b2, r2r2b2);
        checkMolecules(actualMolecules, m1, m2);
    }

    public void testSingleNestingObjectsExtraTriples() throws Exception {
        GRAPH.add(R1R1B1, R2R1B1, R1R2B2, R2R2B2, R1R1R1, R2R1R1, R2R1R2);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = createMolecule(r1r1b1, r2r1b1);
        NewMolecule m2 = createMolecule(r1r2b2, r2r2b2);
        NewMolecule m3 = createMolecule(r1r1r1);
        NewMolecule m4 = createMolecule(r2r1r1);
        NewMolecule m5 = createMolecule(r2r1r2);
        checkMolecules(actualMolecules, m1, m2, m3, m4, m5);
    }

    public void testSingleNestingBothSides() throws Exception {
        GRAPH.add(R1R1B1, R1R2B2, R2R1B1, R2R2B2, B1R1R1, B1R2R2, B2R2R1);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = createMolecule(r1r1b1, r2r1b1, b1r1r1, b1r2r2);
        NewMolecule m2 = createMolecule(r1r2b2, r2r2b2, b2r2r1);
        checkMolecules(actualMolecules, m1, m2);
    }

    public void testLinkTwoGroups() throws Exception {
        GRAPH.add(R1R1B1, R1R2B2, R2R1B1, R2R2B2, B1R1R1, B1R2R2, B2R2R1, B1R1B2);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = createMultiLevelMolecule(asSet(r1r1b1, r2r1b1, b1r1r1, b1r2r2, b1r1b2),
            asSet(r1r2b2, r2r2b2, b2r2r1), Collections.<Triple>emptySet());
        checkMolecules(actualMolecules, m1);
    }

    public void testLinkThreeGroups() throws Exception {
        GRAPH.add(R1R1B1, R1R2B2, R2R1B1, R2R2B2, B1R1R1, B1R2R2, B2R2R1, B1R1B2, B2R2B3, B3R2R3);
        Set<NewMolecule> actualMolecules = decomposer.decompose(GRAPH);
        NewMolecule m1 = createMultiLevelMolecule(asSet(r1r1b1, r2r1b1, b1r1r1, b1r2r2, b1r1b2),
            asSet(r1r2b2, r2r2b2, b2r2r1, b2r2b3), asSet(b3r2r3));
        checkMolecules(actualMolecules, m1);
    }

    private void checkMolecules(Set<NewMolecule> actualMolecules, NewMolecule... expectedMolecules) {
        assertEquals("Unexpected size of molecules", expectedMolecules.length, actualMolecules.size());
        assertEquals(asSet(moleculeComparator, expectedMolecules), actualMolecules);
    }
}
