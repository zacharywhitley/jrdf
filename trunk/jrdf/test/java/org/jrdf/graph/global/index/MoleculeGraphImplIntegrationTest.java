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
import org.jrdf.MoleculeJRDFFactory;
import org.jrdf.SortedDiskGlobalJRDFFactory;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Resource;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeComparator;
import org.jrdf.graph.global.molecule.MoleculeFactory;
import org.jrdf.graph.global.molecule.mem.MoleculeFactoryImpl;
import org.jrdf.graph.global.molecule.mem.MoleculeHeadTripleComparatorImpl;
import org.jrdf.graph.local.TripleComparatorFactoryImpl;
import org.jrdf.util.ClosableIterator;

import java.net.URI;
import static java.net.URI.create;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// TODO Write a test to check that writing triples and getting molecules synchronise.  Especially with creating
// new URIs across data structures.  e.g. create a triple with a new molecule and then do a find on it.
public class MoleculeGraphImplIntegrationTest extends TestCase {
    private static final MoleculeJRDFFactory FACTORY = SortedDiskGlobalJRDFFactory.getFactory();
    private static final TripleComparator COMPARATOR = new TripleComparatorFactoryImpl().newComparator();

    private URIReference REF1;
    private URIReference REF2;
    private URIReference REF3;
    private BlankNode BNODE1;
    private BlankNode BNODE2;
    private BlankNode BNODE3;

    private final MoleculeComparator moleculeComparator = new MoleculeHeadTripleComparatorImpl(COMPARATOR);
    private final MoleculeFactory moleculeFactory = new MoleculeFactoryImpl(moleculeComparator);
    private MoleculeGraph moleculeGraph;
    private TripleFactory tripleFactory;
    private GraphElementFactory element_factory;
    private Triple b3R2R2;
    private Triple b3R2R3;
    private Triple b2R2B3;
    private Triple b2R2R1;
    private Triple r1R2B2;
    private Triple b1R1B2;
    private Triple b1R2R2;
    private Triple b1R1R1;

    public void setUp() throws Exception {
        super.setUp();
        moleculeGraph = FACTORY.getNewGraph();
        element_factory = moleculeGraph.getElementFactory();
        tripleFactory = moleculeGraph.getTripleFactory();
        REF1 = element_factory.createURIReference(URI.create("urn:ref1"));
        REF2 = element_factory.createURIReference(URI.create("urn:ref2"));
        REF3 = element_factory.createURIReference(URI.create("urn:ref3"));
        BNODE1 = element_factory.createBlankNode();
        BNODE2 = element_factory.createBlankNode();
        BNODE3 = element_factory.createBlankNode();

        b1R1R1 = tripleFactory.createTriple(BNODE1, REF1, REF1);
        b1R2R2 = tripleFactory.createTriple(BNODE1, REF2, REF2);
        b1R1B2 = tripleFactory.createTriple(BNODE1, REF1, BNODE2);
        r1R2B2 = tripleFactory.createTriple(REF1, REF2, BNODE2);
        b2R2R1 = tripleFactory.createTriple(BNODE2, REF2, REF1);
        b2R2B3 = tripleFactory.createTriple(BNODE2, REF2, BNODE3);
        b3R2R3 = tripleFactory.createTriple(BNODE3, REF2, REF3);
        b3R2R2 = tripleFactory.createTriple(BNODE3, REF2, REF2);
    }

    @Override
    public void tearDown() {
        moleculeGraph.clear();
        moleculeGraph.close();
    }

    public void testSimpleAddRemove() throws Exception {
        Resource b1 = moleculeGraph.getElementFactory().createResource();
        Resource r1 = moleculeGraph.getElementFactory().createResource(create("urn:foo"));
        Molecule molecule = moleculeFactory.createMolecule(b1.asTriple(r1, b1));
        moleculeGraph.add(molecule);
        assertEquals(1, moleculeGraph.getNumberOfTriples());
        Resource b2 = moleculeGraph.getElementFactory().createResource();
        Molecule molecule2 = moleculeFactory.createMolecule(b2.asTriple(r1, b2));
        moleculeGraph.add(molecule2);
        assertEquals(2, moleculeGraph.getNumberOfTriples());
        ClosableIterator<Triple> iterator = moleculeGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        assertTrue(iterator.hasNext());
        moleculeGraph.delete(molecule);
        assertEquals(1, moleculeGraph.getNumberOfTriples());
        moleculeGraph.delete(molecule2);
        assertEquals(0, moleculeGraph.getNumberOfTriples());
        iterator = moleculeGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        assertFalse(iterator.hasNext());
    }

    public void testMoleculeBdbIndex() throws GraphException {
        Resource b1 = moleculeGraph.getElementFactory().createResource();
        Resource r1 = moleculeGraph.getElementFactory().createResource(create("urn:foo"));
        Molecule molecule = moleculeFactory.createMolecule(b1.asTriple(r1, b1));
        moleculeGraph.add(molecule);
        assertEquals(1, moleculeGraph.getNumberOfTriples());
        Resource b2 = moleculeGraph.getElementFactory().createResource();
        Molecule molecule2 = moleculeFactory.createMolecule(b2.asTriple(r1, b2));
        moleculeGraph.add(molecule2);
        assertEquals(2, moleculeGraph.getNumberOfTriples());
        ClosableIterator<Triple> iterator = moleculeGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        assertTrue(iterator.hasNext());
        moleculeGraph.delete(molecule);
        assertEquals(1, moleculeGraph.getNumberOfTriples());
        moleculeGraph.delete(molecule2);
        assertEquals(0, moleculeGraph.getNumberOfTriples());
        iterator = moleculeGraph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        assertFalse(iterator.hasNext());
    }

    public void testMoleuleIndexToMolecule() throws GraphException {
        final GraphElementFactory graphElementFactory = moleculeGraph.getElementFactory();
        Resource r1 = graphElementFactory.createResource(create("urn:foo"));
        Resource b0 = graphElementFactory.createResource();
        Resource b1 = graphElementFactory.createResource();
        final Triple triple = b0.asTriple(r1, b1);
        Molecule molecule = moleculeFactory.createMolecule(triple);
        Resource b2 = graphElementFactory.createResource();
        Resource b3 = graphElementFactory.createResource();
        final Triple triple1 = b1.asTriple(r1, b2);
        Molecule m1 = moleculeFactory.createMolecule(triple1);
        molecule.add(triple, m1);
        final Triple triple2 = b2.asTriple(r1, b3);
        Molecule m2 = moleculeFactory.createMolecule(triple2);
        m1.add(triple1, m2);
        moleculeGraph.add(molecule);
        Molecule actualMolecule = moleculeGraph.findTopLevelMolecule(triple2);
        assertEquals("Equal molecules", molecule, actualMolecule);
    }

    public void testMoleculeIndexComplex() throws GraphException, InterruptedException {
        Triple[] triples = new Triple[] {b1R1R1, b1R2R2, b1R1B2, r1R2B2,
                b2R2R1, b2R2B3, b3R2R3, b3R2R2};

        Molecule molecule = moleculeFactory.createMolecule(b1R1R1, b1R2R2, b1R1B2);
        Molecule sm1 = moleculeFactory.createMolecule(r1R2B2, b2R2R1, b2R2B3);
        Molecule sm2 = moleculeFactory.createMolecule(b3R2R3, b3R2R2);
        molecule.add(b1R1B2, sm1);
        sm1.add(b2R2B3, sm2);
        moleculeGraph.add(molecule);

        assertEquals("# triples", triples.length, moleculeGraph.getNumberOfTriples());
        for (Triple triple : triples) {
            Molecule actualMolecule = moleculeGraph.findTopLevelMolecule(triple);
            assertEquals("Equal molecules", molecule, actualMolecule);
        }
        assertEquals(8, moleculeGraph.getNumberOfTriples());
        Molecule mol = moleculeGraph.findEnclosingMolecule(b2R2B3);
        assertEquals("Same molecule", sm1, mol);
        Triple tempTriple = tripleFactory.createTriple(REF1, REF1, REF1);
        mol.add(tempTriple);
        moleculeGraph.delete(molecule);
        assertEquals(0, moleculeGraph.getNumberOfTriples());
        assertEquals("# of submolecules", 1, molecule.getSubMolecules(b1R1B2).size());
        assertTrue(molecule.removeMolecule(b1R1B2, sm1));
        assertEquals("# of submolecules", 0, molecule.getSubMolecules(b1R1B2).size());
        molecule.add(b1R1B2, mol);
        assertEquals("# of triples", triples.length + 1, molecule.size());
    }

    public void testEmptyMoleculeIterator() throws GraphException {
        Molecule molecule = moleculeFactory.createMolecule();
        moleculeGraph.add(molecule);
        ClosableIterator<Molecule> iterator = moleculeGraph.iterator();
        assertFalse("Empty iterator", iterator.hasNext());
    }

    public void testSimpleMoleculeIterator() throws GraphException {
        Molecule molecule = moleculeFactory.createMolecule(r1R2B2, b2R2R1, b2R2B3);
        moleculeGraph.add(molecule);
        ClosableIterator<Molecule> iterator = moleculeGraph.iterator();
        assertTrue("Got item", iterator.hasNext());
        Molecule mol1 = iterator.next();
        assertEquals("Same molecule", 0, moleculeComparator.compare(molecule, mol1));
        assertFalse("Empty iterator", iterator.hasNext());
    }

    public void testMultiMoleculeIterator() throws GraphException {
        Triple[] triples = new Triple[] {b1R1R1, b1R2R2, b1R1B2, r1R2B2,
                b2R2R1, b2R2B3, b3R2R3, b3R2R2};

        Molecule molecule = moleculeFactory.createMolecule(b1R1R1, b1R2R2, b1R1B2);
        Molecule sm1 = moleculeFactory.createMolecule(r1R2B2, b2R2R1, b2R2B3);
        Molecule sm2 = moleculeFactory.createMolecule(b3R2R3, b3R2R2);
        moleculeGraph.add(molecule);
        moleculeGraph.add(sm1);
        moleculeGraph.add(sm2);
        ClosableIterator<Molecule> iterator = moleculeGraph.iterator();
        int size = 0;
        Set<Triple> set = new HashSet<Triple>();
        while (iterator.hasNext()) {
            size++;
            Molecule mol = iterator.next();
            final Iterator<Triple> tripleI = mol.iterator();
            while (tripleI.hasNext()) {
                set.add(tripleI.next());
            }
        }
        assertEquals("# molecules", 3, size);
        assertEquals("# triples", triples.length, set.size());
        for (Triple triple : triples) {
            assertTrue(set.contains(triple));
        }
    }

    public void testMultiLevelMoleculeIterator() throws GraphException {
        Triple[] triples = new Triple[] {b1R1R1, b1R2R2, b1R1B2, r1R2B2,
                b2R2R1, b2R2B3, b3R2R3, b3R2R2};

        Molecule molecule = moleculeFactory.createMolecule(b1R1R1, b1R2R2, b1R1B2);
        Molecule sm1 = moleculeFactory.createMolecule(r1R2B2, b2R2R1, b2R2B3);
        Molecule sm2 = moleculeFactory.createMolecule(b3R2R3, b3R2R2);
        sm2.add(b2R2B3, sm2);
        molecule.add(b1R1B2, sm1);
        moleculeGraph.add(molecule);
        ClosableIterator<Molecule> iterator = moleculeGraph.iterator();
        assertTrue("Got item", iterator.hasNext());
        Molecule mol1 = iterator.next();
        assertEquals("Same molecule", 0, moleculeComparator.compare(molecule, mol1));
        assertFalse("Empty iterator", iterator.hasNext());
    }
}
