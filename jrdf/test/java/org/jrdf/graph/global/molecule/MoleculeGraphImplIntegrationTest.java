/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2009 The JRDF Project.  All rights reserved.
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

import org.jrdf.collection.MemMapFactory;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Resource;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleImpl;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.parser.ntriples.parser.NodeMaps;
import org.jrdf.parser.ntriples.parser.NodeMapsImpl;
import org.jrdf.parser.ntriples.parser.NodeParsersFactory;
import org.jrdf.parser.ntriples.parser.NodeParsersFactoryImpl;
import org.jrdf.parser.ntriples.parser.RegexTripleParser;
import org.jrdf.parser.ntriples.parser.RegexTripleParserImpl;
import org.jrdf.parser.ntriples.parser.TripleParser;
import org.jrdf.parser.ntriples.parser.TripleParserImpl;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.boundary.RegexMatcherFactory;
import org.jrdf.util.boundary.RegexMatcherFactoryImpl;
import org.jrdf.vocabulary.RDF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import static java.net.URI.create;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// TODO Write a test to check that writing triples and getting molecules synchronise.  Especially with creating
// new URIs across data structures.  e.g. create a triple with a new molecule and then do a find on it.
public class MoleculeGraphImplIntegrationTest extends AbstractMoleculeGraphIntegrationTest {
    private MoleculeGraph destGraph;
    private static final int NUMBER_OF_MOLECULES_TO_ADD = 10;

    public void testSimpleAdds() throws Exception {
        for (int i = 0; i < NUMBER_OF_MOLECULES_TO_ADD; i++) {
            Molecule molecule = moleculeFactory.createMolecule(b1r1r1, b1r2r2, b1r1b2);
            Molecule sm1 = moleculeFactory.createMolecule(r1r2b2, b2r2r1, b2r2b3);
            Molecule sm2 = moleculeFactory.createMolecule(b3r2r3, b3r2r2);
            molecule.add(b1r1b2, sm1);
            sm1.add(b2r2b3, sm2);
            graph.add(molecule);
        }
        assertEquals(NUMBER_OF_MOLECULES_TO_ADD, graph.getNumberOfMolecules());
        assertEquals(NUMBER_OF_MOLECULES_TO_ADD * 8, graph.getNumberOfTriples());
    }

    public void testSimpleAddRemove() throws Exception {
        Resource b1 = graph.getElementFactory().createResource();
        Resource r1 = graph.getElementFactory().createResource(create("urn:foo"));
        Molecule molecule = moleculeFactory.createMolecule(b1.asTriple(r1, b1));
        graph.add(molecule);
        assertEquals(1, graph.getNumberOfTriples());
        Resource b2 = graph.getElementFactory().createResource();
        Molecule molecule2 = moleculeFactory.createMolecule(b2.asTriple(r1, b2));
        graph.add(molecule2);
        assertEquals(2, graph.getNumberOfTriples());
        ClosableIterator<Triple> iterator = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).
            iterator();
        assertTrue(iterator.hasNext());
        graph.delete(molecule);
        assertEquals(1, graph.getNumberOfTriples());
        graph.delete(molecule2);
        assertEquals(0, graph.getNumberOfTriples());
        iterator = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).iterator();
        assertFalse(iterator.hasNext());
    }

    public void testMoleculeBdbIndex() throws GraphException {
        final GraphElementFactory elementFactory = graph.getElementFactory();
        Resource b1 = elementFactory.createResource();
        Resource r1 = elementFactory.createResource(create("urn:foo"));
        Molecule molecule = moleculeFactory.createMolecule(b1.asTriple(r1, b1));
        graph.add(molecule);
        assertEquals(1, graph.getNumberOfTriples());
        Resource b2 = elementFactory.createResource();
        Molecule molecule2 = moleculeFactory.createMolecule(b2.asTriple(r1, b2));
        graph.add(molecule2);
        assertEquals(2, graph.getNumberOfTriples());
        ClosableIterator<Triple> iterator = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).
            iterator();
        assertTrue(iterator.hasNext());
        graph.delete(molecule);
        assertEquals(1, graph.getNumberOfTriples());
        graph.delete(molecule2);
        assertEquals(0, graph.getNumberOfTriples());
        iterator = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).iterator();
        assertFalse(iterator.hasNext());
    }

    public void testMoleuleIndexToMolecule() throws GraphException {
        final GraphElementFactory graphElementFactory = graph.getElementFactory();
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
        graph.add(molecule);
        Molecule actualMolecule = graph.findTopLevelMolecule(triple2);
        assertEquals("Equal molecules", molecule, actualMolecule);
    }

    /*//TODO fix me!
    public void testMoleculeIndexComplex() throws GraphException, InterruptedException {
        Triple[] triples = new Triple[]{B1R1R1, B1R2R2, B1R1B2, R1R2B2, B2R2R1, B2R2B3, B3R2R3, B3R2R2};
        Molecule molecule = MOLECULE_FACTORY.createMolecule(B1R1R1, B1R2R2, B1R1B2);
        Molecule sm1 = MOLECULE_FACTORY.createMolecule(R1R2B2, B2R2R1, B2R2B3);
        Molecule sm2 = MOLECULE_FACTORY.createMolecule(B3R2R3, B3R2R2);
        molecule.add(B1R1B2, sm1);
        sm1.add(B2R2B3, sm2);
        GRAPH.add(molecule);

        assertEquals("# triples", triples.length, GRAPH.getNumberOfTriples());
        for (Triple triple : triples) {
            Molecule actualMolecule = GRAPH.findTopLevelMolecule(triple);
            assertEquals("Equal molecules", molecule, actualMolecule);
        }
        assertEquals(8, GRAPH.getNumberOfTriples());
        Molecule mol = GRAPH.findEnclosingMolecule(B2R2B3);
        assertEquals("Same molecule", sm1, mol);
        Triple tempTriple = TRIPLE_FACTORY.createTriple(REF1, REF1,
                REF1);
        mol.add(tempTriple);
        GRAPH.delete(molecule);
        assertEquals(0, GRAPH.getNumberOfTriples());
        assertEquals("# of submolecules", 1, molecule.getSubMolecules(B1R1B2).size());
        assertTrue(molecule.removeMolecule(B1R1B2, sm1));
        assertEquals("# of submolecules", 0, molecule.getSubMolecules(B1R1B2).size());
        molecule.add(B1R1B2, mol);
        assertEquals("# of triples", triples.length + 1, molecule.size());
    }*/

    public void testEmptyMoleculeIterator() throws GraphException {
        Molecule molecule = moleculeFactory.createMolecule();
        graph.add(molecule);
        ClosableIterator<Molecule> iterator = graph.iterator();
        try {
            assertFalse("Empty iterator", iterator.hasNext());
        } finally {
            iterator.close();
        }
    }

    public void testSimpleMoleculeIterator() throws GraphException {
        Molecule molecule = moleculeFactory.createMolecule(r1r2b2, b2r2r1, b2r2b3);
        graph.add(molecule);
        ClosableIterator<Molecule> iterator = graph.iterator();
        try {
            assertTrue("Got item", iterator.hasNext());
            Molecule mol1 = iterator.next();
            assertEquals("Same molecule", 0, moleculeComparator.compare(molecule, mol1));
            assertFalse("Empty iterator", iterator.hasNext());
        } finally {
            iterator.close();
        }
    }

    public void testMultiMoleculeIterator() throws GraphException {
        Triple[] triples = new Triple[]{b1r1r1, b1r2r2, b1r1b2, r1r2b2, b2r2r1, b2r2b3, b3r2r3, b3r2r2};
        Molecule molecule = moleculeFactory.createMolecule(b1r1r1, b1r2r2, b1r1b2);
        Molecule sm1 = moleculeFactory.createMolecule(r1r2b2, b2r2r1, b2r2b3);
        Molecule sm2 = moleculeFactory.createMolecule(b3r2r3, b3r2r2);
        graph.add(molecule);
        graph.add(sm1);
        graph.add(sm2);
        ClosableIterator<Molecule> iterator = graph.iterator();
        try {
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
        } finally {
            iterator.close();
        }
    }

    public void testMultiLevelMoleculeIterator() throws GraphException {
        Molecule molecule = moleculeFactory.createMolecule(b1r1r1, b1r2r2, b1r1b2);
        Molecule sm1 = moleculeFactory.createMolecule(r1r2b2, b2r2r1, b2r2b3);
        Molecule sm2 = moleculeFactory.createMolecule(b3r2r3, b3r2r2);
        sm2.add(b2r2b3, sm2);
        molecule.add(b1r1b2, sm1);
        graph.add(molecule);
        ClosableIterator<Molecule> iterator = graph.iterator();
        try {
            assertTrue("Got item", iterator.hasNext());
            Molecule mol1 = iterator.next();
            assertEquals("Same molecule", 0, moleculeComparator.compare(molecule, mol1));
            assertFalse("Empty iterator", iterator.hasNext());
        } finally {
            iterator.close();
        }
    }

    public void testMultiLevelMoleculeFind() throws GraphException {
        Molecule molecule = moleculeFactory.createMolecule(b1r1r1, b1r2r2, b1r1b2);
        Molecule sm1 = moleculeFactory.createMolecule(r1r2b2, b2r2r1, b2r2b3);
        Molecule sm2 = moleculeFactory.createMolecule(b3r2r3, b3r2r2);
        sm2.add(b2r2b3, sm2);
        molecule.add(b1r1b2, sm1);
        graph.add(molecule);
        Molecule mol1 = graph.findTopLevelMolecule(b2r2r1);
        assertEquals("Same molecule", 0, moleculeComparator.compare(molecule, mol1));
    }

    public void testProteinFindTriple() throws IOException, GraphException {
        readTextToGraph();
        final long triples = destGraph.getNumberOfTriples();
        final GraphElementFactory destElementFactory = destGraph.getElementFactory();
        URIReference typePredicate = destElementFactory.createURIReference(RDF.TYPE);
        URIReference object = destElementFactory.createURIReference(
            create("http://www.biopax.org/release/biopax-level2.owl#physicalInteraction"));
        ClosableIterator<Triple> interactions = destGraph.find(ANY_SUBJECT_NODE, typePredicate, object).iterator();
        try {
            assertTrue(interactions.hasNext());
            Triple interaction = interactions.next();
            Molecule interactionMolecule = destGraph.findTopLevelMolecule(interaction);
            assertEquals(triples, interactionMolecule.size());
        } finally {
            interactions.close();
        }
    }

    private void readTextToGraph() throws IOException {
        final URL resource = getClass().getClassLoader().getResource("org/jrdf/graph/global/molecule/mem/result.txt");
        String text = getAsString(resource);
        destGraph = factory.getGraph();
        TextToMolecule textToMolecule = new TextToMolecule(new RegexMatcherFactoryImpl(), getTripleParser(destGraph),
            moleculeFactory);
        TextToMoleculeGraph graphBuilder = new TextToMoleculeGraph(textToMolecule);
        graphBuilder.parse(new StringReader(text));
        while (graphBuilder.hasNext()) {
            destGraph.add(graphBuilder.next());
        }
    }

    public void testMoleculeGraphToTextToMolecule() {
        Molecule molecule = moleculeFactory.createMolecule();
        Molecule sm1 = moleculeFactory.createMolecule(b2r2b3);
        sm1.add(b1r1b2);
        molecule.add(r1r2b2, sm1);
        graph.add(molecule);

        TextToMolecule textToMolecule = new TextToMolecule(new RegexMatcherFactoryImpl(), getTripleParser(graph),
            moleculeFactory);
        textToMolecule.parse(new StringReader(graph.toString()));
    }

    public void testAddNewRootTripleToMolecule() throws GraphException {
        Molecule molecule = moleculeFactory.createMolecule(b2r2b3);
        Molecule molecule1 = moleculeFactory.createMolecule(b2r2r1);
        molecule.add(b1r1b2, molecule1);
        graph.add(molecule);
        //GRAPH.add(molecule1);
        Molecule newMolecule = graph.addRootTriple(molecule, r1r2b2);
        assertEquals("Same molecule", 0, globalMoleculeComparator.compare(molecule.add(r1r2b2), newMolecule));
    }

    public void testRemoveRootTripleFromMolecule() throws GraphException {
        Molecule molecule = moleculeFactory.createMolecule(b2r2b3);
        Molecule molecule1 = moleculeFactory.createMolecule(b2r2r1);
        molecule.add(b1r1b2, molecule1);
        graph.add(molecule);
        graph.add(molecule1);
        Molecule newMolecule = graph.removeRootTriple(molecule, b1r1b2);
        molecule.remove(b1r1b2);
        assertEquals("Same molecule", 0, globalMoleculeComparator.compare(molecule, newMolecule));
        assertEquals("Same molecule", 1, newMolecule.size());
    }

    public void testFindAnyNode() throws GraphException {
        Triple triple = new TripleImpl(ANY_SUBJECT_NODE, ref1, bnode2);
        Molecule molecule = moleculeFactory.createMolecule(b2r2b3);
        Molecule molecule1 = moleculeFactory.createMolecule(b2r2r1);
        molecule.add(b1r1b2, molecule1);
        graph.add(molecule);
        graph.add(molecule1);
        ClosableIterator<Molecule> iterator = graph.findMolecules(triple);
        assertTrue(iterator.hasNext());
        Molecule mol1 = iterator.next();
        assertEquals("Same molecule", 0, moleculeComparator.compare(molecule, mol1));
        assertFalse(iterator.hasNext());
        iterator.close();
        triple = new TripleImpl(bnode2, ref1, ANY_OBJECT_NODE);
        iterator = graph.findMolecules(triple);
        assertFalse(iterator.hasNext());
        iterator.close();
    }

    private TripleParser getTripleParser(Graph newGraph) {
        final NodeParsersFactory parsersFactory = new NodeParsersFactoryImpl(newGraph, new MemMapFactory());
        final RegexMatcherFactory matcherFactory = new RegexMatcherFactoryImpl();
        final NodeMaps nodeMaps = new NodeMapsImpl(parsersFactory.getUriReferenceParser(),
            parsersFactory.getBlankNodeParser(), parsersFactory.getLiteralParser());
        final RegexTripleParser parser = new RegexTripleParserImpl(matcherFactory, newGraph.getTripleFactory(),
            nodeMaps);
        return new TripleParserImpl(matcherFactory, parsersFactory.getBlankNodeParser(), parser);
    }

    private String getAsString(URL resource) throws IOException {
        final InputStream inputStream = resource.openStream();
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        char[] buf = new char[1024];
        int numRead;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }
}
