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
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Triple;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R1B2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R1R1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R1R1_2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R2R2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R1B1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R1R1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R2R1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R2R2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.GRAPH;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.MOLECULE_FACTORY;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.R1R1B1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.R2R1R1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.R2R1R2;
import org.jrdf.util.ClosableIterator;

import java.util.Set;

public class NaiveGraphDecomposerImplUnitTest extends TestCase {
    private GraphDecomposer decomposer = new NaiveGraphDecomposerImpl();

    public void setUp() throws Exception {
        super.setUp();
        ClosableIterator<Triple> iterator = GRAPH.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        GRAPH.remove(iterator);
    }

    public void testGeneralDecomposeGraph() throws Exception {
        GRAPH.add(R2R1R2, R1R1B1, B1R1R1, B2R1B1, B2R2R1, R2R1R1);
        Molecule expectedResults1 = MOLECULE_FACTORY.createMolecule(R1R1B1, B1R1R1, B2R1B1, B2R2R1);
        Molecule expectedResults2 = MOLECULE_FACTORY.createMolecule(R2R1R1);
        Molecule expectedResults3 = MOLECULE_FACTORY.createMolecule(R2R1R2);
        Set<Molecule> molecules = decomposer.decompose(GRAPH);
        checkMolecules(molecules, expectedResults1, expectedResults2, expectedResults3);
    }

    // TODO: Write test here for GlobalGraph - should get back only one result!!!
    public void testNoLeanificationOnLocalGraph() throws Exception {
        GRAPH.add(B1R1R1, B2R1R1, B1R2R2, B2R2R2);
        Molecule expectedResult1 = MOLECULE_FACTORY.createMolecule(B1R1R1, B1R2R2);
        Molecule expectedResult2 = MOLECULE_FACTORY.createMolecule(B2R1R1, B2R2R2);
        Set<Molecule> molecules = decomposer.decompose(GRAPH);
        checkMolecules(molecules, expectedResult1, expectedResult2);
    }

    // TODO: incorrect decomposition of graph!
    /*public void testNestedBlankNodeDecompose() throws GraphException, GraphElementFactoryException {
        GraphElementFactory fac = GRAPH.getElementFactory();
        TripleFactory tFac = GRAPH.getTripleFactory();
        URIReference ref0 = fac.createURIReference(URI.create("urn:subject"));
        URIReference ref1 = fac.createURIReference(URI.create("urn:foo"));
        URIReference ref2 = fac.createURIReference(URI.create("urn:bar"));

        BlankNode bn1 = fac.createBlankNode();
        System.out.println("bn1 = " + bn1.toString());
        BlankNode bn2 = fac.createBlankNode();
        System.out.println("bn2 = " + bn2.toString());
        BlankNode bn3 = fac.createBlankNode();
        System.out.println("bn3 = " + bn3.toString());

        Triple t00 = tFac.createTriple(bn1, ref2, bn2);
        Triple t01 = tFac.createTriple(bn1, ref2, bn3);
        Triple t1 = tFac.createTriple(bn2, ref1, ref1);
        Triple t2 = tFac.createTriple(bn3, ref1, ref2);

        GRAPH.add(t00, t01, t1, t2);
        Set<Molecule> molecules = decomposer.decompose(GRAPH);
        for (Molecule m : molecules) {
            System.out.println(m.toString());
        }
        assertEquals("Only 1 molecule", 1, molecules.size());
    }*/

    public void testBlankNodesWithSameReferenceTest() throws Exception {
        GRAPH.add(R1R1B1, B1R1R1, B2R1B1, B2R2R1, R2R1R1, B1R1R1_2, B1R1B2);
        Molecule equivalentResult1a = MOLECULE_FACTORY.createMolecule(R1R1B1, B1R1R1, B2R2R1, B1R1B2, B1R1R1_2);
        Molecule equivalentResult1b = MOLECULE_FACTORY.createMolecule(R1R1B1, B1R1R1, B2R1B1, B2R2R1, B1R1B2);
        Molecule equivalentResult1c = MOLECULE_FACTORY.createMolecule(R1R1B1, B1R1R1, B2R1B1, B2R2R1, B1R1B2, B1R1R1_2);
        Molecule expectedResults2 = MOLECULE_FACTORY.createMolecule(R2R1R1);
        Set<Molecule> molecules = decomposer.decompose(GRAPH);
        checkMolecules(molecules, equivalentResult1a, expectedResults2);
        checkMolecules(molecules, equivalentResult1b, expectedResults2);
        checkMolecules(molecules, equivalentResult1c, expectedResults2);
    }

    public void testGroundedDecompose() throws Exception {
        checkForSingleTriple(R1R1B1);
    }

    public void testSingleBlankDecompose() throws Exception {
        checkForSingleTriple(B1R1R1);
    }

    public void testSingleBlankDecompose2() throws Exception {
        checkForSingleTriple(B1R1R1_2);
    }

    public void testTwoBlankDecompose() throws Exception {
        checkForSingleTriple(B2R1B1);
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
