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
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.global.GroundedTripleComparatorImpl;
import org.jrdf.graph.local.mem.BlankNodeComparator;
import org.jrdf.graph.local.mem.GlobalizedBlankNodeComparatorImpl;
import org.jrdf.graph.local.mem.NodeComparatorImpl;
import org.jrdf.util.NodeTypeComparatorImpl;

import java.net.URI;
import java.util.Set;

public class NaiveGraphDecomposerImplUnitTest extends TestCase {
    private JRDFFactory factory = SortedMemoryJRDFFactoryImpl.getFactory();
    private Graph newGraph = factory.getNewGraph();
    private GraphElementFactory elementFactory = newGraph.getElementFactory();
    private TripleFactory tripleFactory = newGraph.getTripleFactory();
    private BlankNodeComparator blankNodeComparator = new GlobalizedBlankNodeComparatorImpl();
    private NodeComparator nodeComparator = new NodeComparatorImpl(new NodeTypeComparatorImpl(), blankNodeComparator);
    private TripleComparator tripleComparator = new GroundedTripleComparatorImpl(nodeComparator);
    private MoleculeFactory moleculeFactory = new MoleculeFactoryImpl(tripleComparator);
    private URIReference ref1;
    private URIReference ref2;
    private BlankNode blankNode1;
    private BlankNode blankNode2;
    private Triple triple0;
    private Triple triple1;
    private Triple triple2;
    private Triple triple3;
    private Triple triple4;
    private Triple triple5;
    private Triple triple6;
    private Triple triple7;
    private GraphDecomposer decomposer;

    public void setUp() throws Exception {
        super.setUp();
        newGraph.clear();
        ref1 = elementFactory.createURIReference(URI.create("urn:foo"));
        ref2 = elementFactory.createURIReference(URI.create("urn:bar"));
        blankNode1 = elementFactory.createBlankNode();
        blankNode2 = elementFactory.createBlankNode();
        triple0 = tripleFactory.createTriple(ref2, ref1, ref2);
        triple1 = tripleFactory.createTriple(ref1, ref1, blankNode1);
        triple2 = tripleFactory.createTriple(blankNode1, ref1, ref1);
        triple3 = tripleFactory.createTriple(blankNode2, ref1, blankNode1);
        triple4 = tripleFactory.createTriple(blankNode2, ref2, ref1);
        triple5 = tripleFactory.createTriple(ref2, ref1, ref1);
        triple6 = tripleFactory.createTriple(blankNode1, ref1, ref1);
        triple7 = tripleFactory.createTriple(blankNode1, ref1, blankNode2);
        decomposer = new NaiveGraphDecomposerImpl();
    }

    public void testGeneralDecomposeGraph() throws Exception {
        newGraph.add(triple0, triple1, triple2, triple3, triple4, triple5);
        Molecule expectedResults1 = moleculeFactory.createMolecule(triple1, triple2, triple3, triple4);
        Molecule expectedResults2 = moleculeFactory.createMolecule(triple5);
        Molecule expectedResults3 = moleculeFactory.createMolecule(triple0);
        Set<Molecule> molecules = decomposer.decompose(newGraph);
        checkMolecules(molecules, expectedResults1, expectedResults2, expectedResults3);
    }
    
    public void testRedundantTriple() throws Exception {
        newGraph.add(triple1, triple2, triple3, triple4, triple5, triple6, triple7);
        Molecule equivalentResult1a = moleculeFactory.createMolecule(triple1, triple2, triple4, triple7, triple6);
        Molecule equivalentResult1b = moleculeFactory.createMolecule(triple1, triple2, triple3, triple4, triple7);
        Molecule equivalentResult1c = moleculeFactory.createMolecule(triple1, triple2, triple3, triple4, triple7, triple6);
        Molecule expectedResults2 = moleculeFactory.createMolecule(triple5);
        Set<Molecule> molecules = decomposer.decompose(newGraph);
        checkMolecules(molecules, equivalentResult1a, expectedResults2);
        checkMolecules(molecules, equivalentResult1b, expectedResults2);
        checkMolecules(molecules, equivalentResult1c, expectedResults2);
    }

    public void testGroundedDecompose() throws Exception {
        checkForSingleTriple(triple1);
    }

    public void testSingleBlankDecompose() throws Exception {
        checkForSingleTriple(triple2);
    }

    public void testSingleBlankDecompose2() throws Exception {
        checkForSingleTriple(triple6);
    }

    public void testTwoBlankDecompose() throws Exception {
        checkForSingleTriple(triple3);
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
