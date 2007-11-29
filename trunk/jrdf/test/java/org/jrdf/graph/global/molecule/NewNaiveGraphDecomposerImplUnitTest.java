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
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R1R1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R2R2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R2R1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R2R2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.GRAPH;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.R1R1R1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.R2R1R1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.R2R1R2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.*;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.*;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.r1r1b1;
import org.jrdf.graph.local.iterator.ClosableIterator;
import org.jrdf.set.MemSortedSetFactory;
import static org.jrdf.util.test.SetUtil.*;

import java.util.Set;

public class NewNaiveGraphDecomposerImplUnitTest extends TestCase {
    private final TripleComparator comparator = new GroundedTripleComparatorFactoryImpl().newComparator();
    private final NewMoleculeComparator moleculeComparator = new NewMoleculeComparatorImpl(comparator);
    private final MemSortedSetFactory setFactory = new MemSortedSetFactory();
    private final NewMoleculeFactory moleculeFactory = new NewMoleculeFactoryImpl(comparator, moleculeComparator);
    private final NewGraphDecomposer decomposer = new NewNaiveGraphDecomposerImpl(setFactory, moleculeFactory,
        moleculeComparator);

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

    private void checkMolecules(Set<NewMolecule> actualMolecules, NewMolecule... expectedMolecules) {
        assertEquals("Unexpected size of molecules", expectedMolecules.length, actualMolecules.size());
        assertEquals(asSet(moleculeComparator, expectedMolecules), actualMolecules);
    }
}
