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
import org.jrdf.JRDFFactory;
import org.jrdf.SortedMemoryJRDFFactoryImpl;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;

import java.net.URI;
import java.util.Set;

public class NaiveGraphDecomposerImplUnitTest extends TestCase {
    private JRDFFactory factory = SortedMemoryJRDFFactoryImpl.getFactory();
    private Graph newGraph = factory.getNewGraph();
    private GraphElementFactory elementFactory = newGraph.getElementFactory();
    private TripleFactory tripleFactory = newGraph.getTripleFactory();
    private TripleComparator tripleComparator = new GroundedTripleComparatorFactoryImpl().newComparator();
    private MoleculeFactory moleculeFactory = new MoleculeFactoryImpl(tripleComparator);
    private Triple r2r1r2;
    private Triple r1r1b1;
    private Triple b1r1r1;
    private Triple b2r1b1;
    private Triple b2r2r1;
    private Triple r2r1r1;
    private Triple b1r1r1_2;
    private Triple b1r1b2;
    private Triple b2r1r1;
    private Triple b1r2r2;
    private Triple b2r2r2;
    private GraphDecomposer decomposer;

    public void setUp() throws Exception {
        super.setUp();
        newGraph.clear();
        URIReference ref1 = elementFactory.createURIReference(URI.create("urn:foo"));
        URIReference ref2 = elementFactory.createURIReference(URI.create("urn:bar"));
        BlankNode blankNode1 = elementFactory.createBlankNode();
        BlankNode blankNode2 = elementFactory.createBlankNode();
        r2r1r2 = tripleFactory.createTriple(ref2, ref1, ref2);
        r2r1r1 = tripleFactory.createTriple(ref2, ref1, ref1);
        r1r1b1 = tripleFactory.createTriple(ref1, ref1, blankNode1);
        b1r1r1 = tripleFactory.createTriple(blankNode1, ref1, ref1);
        b1r2r2 = tripleFactory.createTriple(blankNode1, ref2, ref2);
        b2r1r1 = tripleFactory.createTriple(blankNode2, ref1, ref1);
        b2r2r2 = tripleFactory.createTriple(blankNode2, ref2, ref2);
        b1r1r1_2 = tripleFactory.createTriple(blankNode1, ref1, ref1);
        b2r2r1 = tripleFactory.createTriple(blankNode2, ref2, ref1);
        b2r1b1 = tripleFactory.createTriple(blankNode2, ref1, blankNode1);
        b1r1b2 = tripleFactory.createTriple(blankNode1, ref1, blankNode2);
        decomposer = new NaiveGraphDecomposerImpl();
    }

    public void testGeneralDecomposeGraph() throws Exception {
        newGraph.add(r2r1r2, r1r1b1, b1r1r1, b2r1b1, b2r2r1, r2r1r1);
        Molecule expectedResults1 = moleculeFactory.createMolecule(r1r1b1, b1r1r1, b2r1b1,
            b2r2r1);
        Molecule expectedResults2 = moleculeFactory.createMolecule(r2r1r1);
        Molecule expectedResults3 = moleculeFactory.createMolecule(r2r1r2);
        Set<Molecule> molecules = decomposer.decompose(newGraph);
        checkMolecules(molecules, expectedResults1, expectedResults2, expectedResults3);
    }

    // TODO: Write test here for GlobalGraph - should get back only one result!!!
    public void testNoLeanificationOnLocalGraph() throws Exception {
        newGraph.add(b1r1r1, b2r1r1, b1r2r2, b2r2r2);
        Molecule expectedResult1 = moleculeFactory.createMolecule(b1r1r1, b1r2r2);
        Molecule expectedResult2 = moleculeFactory.createMolecule(b2r1r1, b2r2r2);
        Set<Molecule> molecules = decomposer.decompose(newGraph);
        checkMolecules(molecules, expectedResult1, expectedResult2);
    }
    
    public void testBlankNodesWithSameReferenceTest() throws Exception {
        newGraph.add(r1r1b1, b1r1r1, b2r1b1, b2r2r1, r2r1r1, b1r1r1_2, b1r1b2);
        Molecule equivalentResult1a = moleculeFactory.createMolecule(r1r1b1, b1r1r1, b2r2r1, b1r1b2, b1r1r1_2);
        Molecule equivalentResult1b = moleculeFactory.createMolecule(r1r1b1, b1r1r1, b2r1b1, b2r2r1, b1r1b2);
        Molecule equivalentResult1c = moleculeFactory.createMolecule(r1r1b1, b1r1r1, b2r1b1, b2r2r1, b1r1b2, b1r1r1_2);
        Molecule expectedResults2 = moleculeFactory.createMolecule(r2r1r1);
        Set<Molecule> molecules = decomposer.decompose(newGraph);
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
        newGraph.add(triple);
        Set<Molecule> molecules = decomposer.decompose(newGraph);
        Molecule expectedResults1 = moleculeFactory.createMolecule(triple);
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
