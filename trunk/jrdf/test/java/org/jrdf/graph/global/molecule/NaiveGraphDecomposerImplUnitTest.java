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
import org.jrdf.graph.AnyObjectNode;
import org.jrdf.graph.AnyPredicateNode;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Triple;
import org.jrdf.graph.local.iterator.ClosableIterator;
import static org.jrdf.graph.AnyObjectNode.*;
import static org.jrdf.graph.AnyPredicateNode.*;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.GRAPH;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.MOLECULE_FACTORY;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.b1r1b2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.b1r1r1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.b1r1r1_2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.b1r2r2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.b2r1b1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.b2r1r1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.b2r2r1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.b2r2r2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.r1r1b1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.r2r1r1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.r2r1r2;

import java.util.Set;

public class NaiveGraphDecomposerImplUnitTest extends TestCase {
    private GraphDecomposer decomposer = new NaiveGraphDecomposerImpl();

    public void setUp() throws Exception {
        super.setUp();
        ClosableIterator<Triple> iterator = GRAPH.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        GRAPH.remove(iterator);
    }

    public void testGeneralDecomposeGraph() throws Exception {
        GRAPH.add(r2r1r2, r1r1b1, b1r1r1, b2r1b1, b2r2r1, r2r1r1);
        Molecule expectedResults1 = MOLECULE_FACTORY.createMolecule(r1r1b1, b1r1r1, b2r1b1, b2r2r1);
        Molecule expectedResults2 = MOLECULE_FACTORY.createMolecule(r2r1r1);
        Molecule expectedResults3 = MOLECULE_FACTORY.createMolecule(r2r1r2);
        Set<Molecule> molecules = decomposer.decompose(GRAPH);
        checkMolecules(molecules, expectedResults1, expectedResults2, expectedResults3);
    }

    // TODO: Write test here for GlobalGraph - should get back only one result!!!
    public void testNoLeanificationOnLocalGraph() throws Exception {
        GRAPH.add(b1r1r1, b2r1r1, b1r2r2, b2r2r2);
        Molecule expectedResult1 = MOLECULE_FACTORY.createMolecule(b1r1r1, b1r2r2);
        Molecule expectedResult2 = MOLECULE_FACTORY.createMolecule(b2r1r1, b2r2r2);
        Set<Molecule> molecules = decomposer.decompose(GRAPH);
        checkMolecules(molecules, expectedResult1, expectedResult2);
    }

    public void testBlankNodesWithSameReferenceTest() throws Exception {
        GRAPH.add(r1r1b1, b1r1r1, b2r1b1, b2r2r1, r2r1r1, b1r1r1_2, b1r1b2);
        Molecule equivalentResult1a = MOLECULE_FACTORY.createMolecule(r1r1b1, b1r1r1, b2r2r1, b1r1b2, b1r1r1_2);
        Molecule equivalentResult1b = MOLECULE_FACTORY.createMolecule(r1r1b1, b1r1r1, b2r1b1, b2r2r1, b1r1b2);
        Molecule equivalentResult1c = MOLECULE_FACTORY.createMolecule(r1r1b1, b1r1r1, b2r1b1, b2r2r1, b1r1b2, b1r1r1_2);
        Molecule expectedResults2 = MOLECULE_FACTORY.createMolecule(r2r1r1);
        Set<Molecule> molecules = decomposer.decompose(GRAPH);
        checkMolecules(molecules, equivalentResult1a, expectedResults2);
        checkMolecules(molecules, equivalentResult1b, expectedResults2);
        checkMolecules(molecules, equivalentResult1c, expectedResults2);
    }

    public void testGroundedDecompose() throws Exception {
        checkForSingleTriple(r1r1b1);
    }

    public void testSingleBlankDecompose() throws Exception {
        checkForSingleTriple(b1r1r1);
    }

    public void testSingleBlankDecompose2() throws Exception {
        checkForSingleTriple(b1r1r1_2);
    }

    public void testTwoBlankDecompose() throws Exception {
        checkForSingleTriple(b2r1b1);
    }

    private void checkForSingleTriple(Triple triple) throws GraphException {
        GRAPH.add(triple);
        Set<Molecule> molecules = decomposer.decompose(GRAPH);
        Molecule expectedResults1 = MOLECULE_FACTORY.createMolecule(triple);
        checkMolecules(molecules, expectedResults1);
    }

    private void checkMolecules(Set<Molecule> actualMolecules, Molecule... expectedResults) {
        assertEquals("Wrong number of molecules decomposed", expectedResults.length, actualMolecules.size());
        for (Molecule result : expectedResults) {
            assertTrue("Expected to find result: " + result + " in " + actualMolecules,
                actualMolecules.contains(result));
        }
    }
}
