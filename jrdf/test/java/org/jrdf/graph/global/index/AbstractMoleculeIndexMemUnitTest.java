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

package org.jrdf.graph.global.index;

import junit.framework.TestCase;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Node;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.BlankNodeImpl;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import org.jrdf.graph.global.LiteralImpl;
import org.jrdf.graph.global.TripleImpl;
import org.jrdf.graph.global.URIReferenceImpl;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeImpl;
import org.jrdf.graph.local.mem.GlobalizedBlankNodeComparatorImpl;
import org.jrdf.graph.local.mem.NodeComparatorImpl;
import org.jrdf.graph.local.mem.TripleComparatorImpl;
import org.jrdf.util.NodeTypeComparatorImpl;
import org.jrdf.vocabulary.RDF;

import java.net.URI;
import java.util.Iterator;

public abstract class AbstractMoleculeIndexMemUnitTest extends TestCase {
    public static final URI EQUVALENT_CLASS = URI.create("http://www.w3.org/2002/07/owl#equivalentClass");
    public static final URI INTERSECTION_OF = URI.create("http://www.w3.org/2002/07/owl#intersectionOf");
    public static final URI CLASS = URI.create("http://www.w3.org/2002/07/owl#Class");
    public static final URI ONE_OF = URI.create("http://www.w3.org/2002/07/owl#oneOf");
    private static final String BASE_URL = "http://www.co-ode.org/ontologies/pizza/pizza.owl#";
    private static final URI AMERICA_URL = URI.create(BASE_URL + "America");
    private static final URI ITALY_URL = URI.create(BASE_URL + "Italy");
    private static final int NUMBER_OF_MOLECULES = 10;
    private static final URI COUNTRY_URL = URI.create(BASE_URL + "Country");
    private final String LITERAL1 = "xyz";
    private final String LITERAL2 = "abc";
    private final TripleComparator groundedTripleComparator = new GroundedTripleComparatorFactoryImpl().newComparator();
    private NodeComparator nodeComparator = new NodeComparatorImpl(new NodeTypeComparatorImpl(), 
        new GlobalizedBlankNodeComparatorImpl());
    private final TripleComparator tripleComparator = new TripleComparatorImpl(nodeComparator);
    protected MoleculeIndex moleculeIndex;

    protected abstract MoleculeIndex getIndex();

    protected abstract Node[] getNodes(Triple triple);

    public void setUp() throws Exception {
        moleculeIndex = getIndex();
    }

    public void testComparators() {
        Molecule m = new MoleculeImpl(groundedTripleComparator);
        Molecule m2 = new MoleculeImpl(tripleComparator);
        BlankNode blankNode1 = new BlankNodeImpl();
        BlankNode blankNode2 = new BlankNodeImpl();
        Triple triple1 = new TripleImpl(blankNode1, new URIReferenceImpl(URI.create("urn:p1")),
            new URIReferenceImpl(URI.create("urn:o")));
        Triple triple2 = new TripleImpl(blankNode1, new URIReferenceImpl(URI.create("urn:p2")), blankNode2);
        Triple triple3 = new TripleImpl(blankNode1, new URIReferenceImpl(URI.create("urn:p3")),
            new URIReferenceImpl(URI.create("urn:o")));
        m = addTriplesToMolecule(m, triple2, triple1, triple3);
        m2 = addTriplesToMolecule(m2, triple2, triple1, triple3);
        checkExpectedOrder(m, triple2, triple1, triple3);
        checkExpectedOrder(m2, triple1, triple2, triple3);
    }

    public void testAddSameMoleculeTwice() throws Exception {
        for (int i = 0; i < NUMBER_OF_MOLECULES; i++) {
            Molecule molecule = createMultiLevelMolecule();
            Triple headTriple = molecule.getHeadTriple();
            Node[] nodes = getNodes(headTriple);
            moleculeIndex.add(nodes[0], nodes[1], nodes[2], molecule);
            addMolecule();
            addMoleculeWithNoTail();
            assertEquals((i + 1) * 3, moleculeIndex.getNumberOfMolecules());
        }
    }

    public void testGetTripleSize() throws Exception {
        addMolecule();
        long numberOfTriples = moleculeIndex.getNumberOfTriples();
        long numberOfMolecules = moleculeIndex.getNumberOfMolecules();
        assertEquals("Expected number of triples did not match", 2, numberOfTriples);
        assertEquals("Expected number of molecules did not match", 1, numberOfMolecules);
    }

    public void testClean() throws Exception {
        addMolecule();
        long numberOfMolecules = moleculeIndex.getNumberOfMolecules();
        int expected = 1;
        assertEquals(expected, numberOfMolecules);

        moleculeIndex.clear();

        numberOfMolecules = moleculeIndex.getNumberOfMolecules();
        expected = 0;
        assertEquals(expected, numberOfMolecules);
    }

    public void testRemove() throws Exception {
        Molecule m1 = addMolecule();
        addMolecule();

        long numberOfMolecules = moleculeIndex.getNumberOfMolecules();
        int expected = 2;
        assertEquals(expected, numberOfMolecules);

        //remove the molecule
        Triple headTriple = m1.getHeadTriple();
        Node[] nodes = getNodes(headTriple);
        moleculeIndex.remove(nodes[0], nodes[1], nodes[2]);

        //check remaining size
        numberOfMolecules = moleculeIndex.getNumberOfMolecules();
        expected = 1;
        assertEquals(expected, numberOfMolecules);

        long numberOfTriples = moleculeIndex.getNumberOfTriples();
        expected = 2;
        assertEquals(expected, numberOfTriples);

        //TODO check remaing molecule is m2
    }

    /**
     * This tests the removal where one of the molecules
     * has no tail triples.
     *
     * @throws Exception
     */
    public void testRemoveMoleculeWithNoTail() throws Exception {
        Molecule m1 = addMolecule();
        addMoleculeWithNoTail();

        long numberOfMolecules = moleculeIndex.getNumberOfMolecules();
        int expected = 2;
        assertEquals(expected, numberOfMolecules);

        //remove the molecule
        Triple headTriple = m1.getHeadTriple();
        Node[] nodes = getNodes(headTriple);
        moleculeIndex.remove(nodes[0], nodes[1], nodes[2]);

        //check remaining size
        numberOfMolecules = moleculeIndex.getNumberOfMolecules();
        expected = 1;
        assertEquals(expected, numberOfMolecules);

        long numberOfTriples = moleculeIndex.getNumberOfTriples();
        expected = 2;
        assertEquals(expected, numberOfTriples);

        //TODO check remaing molecule is m2
    }

    public void testGetMolecule() throws Exception {
        Molecule m1 = addMolecule();
        addMolecule();

        long numberOfMolecules = moleculeIndex.getNumberOfMolecules();
        int expected = 2;
        assertEquals(expected, numberOfMolecules);

        //remove the molecule
        Triple headTriple = m1.getHeadTriple();
        Molecule molecule = moleculeIndex.getMolecule(headTriple);
        assertNotNull(molecule);
        assertEquals(molecule, m1);

        //check remaining size
        numberOfMolecules = moleculeIndex.getNumberOfMolecules();
        expected = 2;
        assertEquals(expected, numberOfMolecules);

        long numberOfTriples = moleculeIndex.getNumberOfTriples();
        expected = 4;
        assertEquals(expected, numberOfTriples);

        //TODO check remaing molecule is m2
    }

    private Molecule addMolecule() {
        Molecule m = new MoleculeImpl(groundedTripleComparator);
        double random = Math.random();
        BlankNodeImpl blankNode = new BlankNodeImpl();
        Triple triple = new TripleImpl(new URIReferenceImpl(URI.create(BASE_URL + random)), new URIReferenceImpl(URI.create(BASE_URL)), blankNode);
        random = Math.random();
        Triple triple2 = new TripleImpl(blankNode, new URIReferenceImpl(URI.create(BASE_URL)), new LiteralImpl(LITERAL2));
        m = m.add(triple);
        m = m.add(triple2);
        Triple headTriple = m.getHeadTriple();
        Node[] nodes = getNodes(headTriple);
        moleculeIndex.add(nodes[0], nodes[1], nodes[2], m);
        return m;
    }

    private Molecule addMoleculeWithNoTail() {
        Molecule m = new MoleculeImpl(groundedTripleComparator);
        double random = Math.random();
        BlankNodeImpl blankNode = new BlankNodeImpl();
        Triple headTriple = new TripleImpl(new URIReferenceImpl(URI.create(BASE_URL + random)), new URIReferenceImpl(URI.create(BASE_URL)), blankNode);
        TripleImpl tailTriple = new TripleImpl(blankNode, new URIReferenceImpl(URI.create(BASE_URL)), new LiteralImpl(LITERAL1));
        m = m.add(headTriple);
        m = m.add(tailTriple);

        Node[] nodes = getNodes(m.getHeadTriple());
        moleculeIndex.add(nodes[0], nodes[1], nodes[2], m);

        Iterator<Triple> iterator = m.tailTriples();
        while (iterator.hasNext()) {
            nodes = getNodes(iterator.next());
            moleculeIndex.add(nodes[0], nodes[1], nodes[2], m);
        }
        return m;
    }

    private Molecule createMultiLevelMolecule() throws Exception {
        Molecule m = new MoleculeImpl(groundedTripleComparator);
        BlankNode blankNodeB = new BlankNodeImpl();
        BlankNode blankNodeC = new BlankNodeImpl();
        BlankNode blankNodeD = new BlankNodeImpl();
        BlankNode blankNodeE = new BlankNodeImpl();
        BlankNode blankNodeF = new BlankNodeImpl();
        m = addTripleToMolecule(m, new URIReferenceImpl(COUNTRY_URL), new URIReferenceImpl(EQUVALENT_CLASS),
            blankNodeB);
        m = addTripleToMolecule(m, blankNodeB, new URIReferenceImpl(INTERSECTION_OF), blankNodeC);
        m = addTripleToMolecule(m, blankNodeC, new URIReferenceImpl(RDF.FIRST), blankNodeD);
        m = addTripleToMolecule(m, blankNodeD, new URIReferenceImpl(RDF.TYPE), new URIReferenceImpl(CLASS));
        m = addTripleToMolecule(m, blankNodeD, new URIReferenceImpl(ONE_OF), blankNodeE);
        m = addTripleToMolecule(m, blankNodeE, new URIReferenceImpl(RDF.FIRST), new URIReferenceImpl(AMERICA_URL));
        m = addTripleToMolecule(m, blankNodeE, new URIReferenceImpl(RDF.REST), blankNodeF);
        m = addTripleToMolecule(m, blankNodeF, new URIReferenceImpl(RDF.FIRST), new URIReferenceImpl(ITALY_URL));
        return m;
    }

    private Molecule addTripleToMolecule(Molecule molecule, SubjectNode subject, PredicateNode predicate,
        ObjectNode object) {
        Triple triple = new TripleImpl(subject, predicate, object);
        return molecule.add(triple);
    }

    private Molecule addTriplesToMolecule(Molecule m, Triple... triples) {
        for (Triple triple : triples) {
            m = m.add(triple);
        }
        return m;
    }

    private void checkExpectedOrder(Molecule actualMolecule, Triple... expectedTriples) {
        Iterator<Triple> iterator = actualMolecule.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Triple triple = iterator.next();
            assertEquals(triple, expectedTriples[index]);
            index++;
        }
    }
}
