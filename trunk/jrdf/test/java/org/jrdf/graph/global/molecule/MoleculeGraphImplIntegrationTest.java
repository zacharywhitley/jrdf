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
import org.jrdf.collection.MemMapFactory;
import org.jrdf.graph.AnyObjectNode;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import org.jrdf.graph.AnySubjectNode;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Resource;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleImpl;
import org.jrdf.graph.global.MoleculeGraph;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.B1R1B2;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.B1R1R1;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.B1R2R2;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.B2R2B3;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.B2R2R1;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.B3R2R2;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.B3R2R3;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.BNODE2;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.ELEMENT_FACTORY;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.FACTORY;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.GLOBAL_MOLECULE_COMPARATOR;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.GRAPH;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.MOLECULE_COMPARATOR;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.MOLECULE_FACTORY;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.R1R2B2;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.REF1;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.TRIPLE_FACTORY;
import org.jrdf.parser.ParserBlankNodeFactory;
import org.jrdf.parser.bnodefactory.ParserBlankNodeFactoryImpl;
import org.jrdf.parser.ntriples.parser.BlankNodeParser;
import org.jrdf.parser.ntriples.parser.BlankNodeParserImpl;
import org.jrdf.parser.ntriples.parser.LiteralMatcher;
import org.jrdf.parser.ntriples.parser.LiteralParser;
import org.jrdf.parser.ntriples.parser.LiteralParserImpl;
import org.jrdf.parser.ntriples.parser.NTripleUtil;
import org.jrdf.parser.ntriples.parser.NTripleUtilImpl;
import org.jrdf.parser.ntriples.parser.RegexLiteralMatcher;
import org.jrdf.parser.ntriples.parser.TripleParser;
import org.jrdf.parser.ntriples.parser.TripleParserImpl;
import org.jrdf.parser.ntriples.parser.URIReferenceParser;
import org.jrdf.parser.ntriples.parser.URIReferenceParserImpl;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.boundary.RegexMatcherFactory;
import org.jrdf.util.boundary.RegexMatcherFactoryImpl;
import org.jrdf.vocabulary.RDF;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import static java.net.URI.create;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// TODO Write a test to check that writing triples and getting molecules synchronise.  Especially with creating
// new URIs across data structures.  e.g. create a triple with a new molecule and then do a find on it.
public class MoleculeGraphImplIntegrationTest extends TestCase {
    private MoleculeGraph destGraph;
    private static final int NUMBER_OF_MOLECULES_TO_ADD = 10;

    public void setUp() throws Exception {
        super.setUp();
        MoleculeGraphTestUtil.setUp();
    }

    @Override
    public void tearDown() {
        MoleculeGraphTestUtil.close();
    }

    public void testSimpleAdds() throws Exception {
        for (int i = 0; i < NUMBER_OF_MOLECULES_TO_ADD; i++) {
            Molecule molecule = MOLECULE_FACTORY.createMolecule(B1R1R1, B1R2R2, B1R1B2);
            Molecule sm1 = MOLECULE_FACTORY.createMolecule(R1R2B2, B2R2R1, B2R2B3);
            Molecule sm2 = MOLECULE_FACTORY.createMolecule(B3R2R3, B3R2R2);
            molecule.add(B1R1B2, sm1);
            sm1.add(B2R2B3, sm2);
            GRAPH.add(molecule);
        }
        assertEquals(NUMBER_OF_MOLECULES_TO_ADD, GRAPH.getNumberOfMolecules());
        assertEquals(NUMBER_OF_MOLECULES_TO_ADD * 8, GRAPH.getNumberOfTriples());
    }

    public void testSimpleAddRemove() throws Exception {
        Resource b1 = GRAPH.getElementFactory().createResource();
        Resource r1 = GRAPH.getElementFactory().createResource(create("urn:foo"));
        Molecule molecule = MOLECULE_FACTORY.createMolecule(b1.asTriple(r1, b1));
        GRAPH.add(molecule);
        assertEquals(1, GRAPH.getNumberOfTriples());
        Resource b2 = GRAPH.getElementFactory().createResource();
        Molecule molecule2 = MOLECULE_FACTORY.createMolecule(b2.asTriple(r1, b2));
        GRAPH.add(molecule2);
        assertEquals(2, GRAPH.getNumberOfTriples());
        ClosableIterator<Triple> iterator = GRAPH.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        assertTrue(iterator.hasNext());
        GRAPH.delete(molecule);
        assertEquals(1, GRAPH.getNumberOfTriples());
        GRAPH.delete(molecule2);
        assertEquals(0, GRAPH.getNumberOfTriples());
        iterator = GRAPH.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        assertFalse(iterator.hasNext());
    }

    public void testMoleculeBdbIndex() throws GraphException {
        Resource b1 = ELEMENT_FACTORY.createResource();
        Resource r1 = ELEMENT_FACTORY.createResource(create("urn:foo"));
        Molecule molecule = MOLECULE_FACTORY.createMolecule(b1.asTriple(r1, b1));
        GRAPH.add(molecule);
        assertEquals(1, GRAPH.getNumberOfTriples());
        Resource b2 = ELEMENT_FACTORY.createResource();
        Molecule molecule2 = MOLECULE_FACTORY.createMolecule(b2.asTriple(r1, b2));
        GRAPH.add(molecule2);
        assertEquals(2, GRAPH.getNumberOfTriples());
        ClosableIterator<Triple> iterator = GRAPH.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        assertTrue(iterator.hasNext());
        GRAPH.delete(molecule);
        assertEquals(1, GRAPH.getNumberOfTriples());
        GRAPH.delete(molecule2);
        assertEquals(0, GRAPH.getNumberOfTriples());
        iterator = GRAPH.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        assertFalse(iterator.hasNext());
    }

    public void testMoleuleIndexToMolecule() throws GraphException {
        final GraphElementFactory graphElementFactory = GRAPH.getElementFactory();
        Resource r1 = graphElementFactory.createResource(create("urn:foo"));
        Resource b0 = graphElementFactory.createResource();
        Resource b1 = graphElementFactory.createResource();
        final Triple triple = b0.asTriple(r1, b1);
        Molecule molecule = MOLECULE_FACTORY.createMolecule(triple);
        Resource b2 = graphElementFactory.createResource();
        Resource b3 = graphElementFactory.createResource();
        final Triple triple1 = b1.asTriple(r1, b2);
        Molecule m1 = MOLECULE_FACTORY.createMolecule(triple1);
        molecule.add(triple, m1);
        final Triple triple2 = b2.asTriple(r1, b3);
        Molecule m2 = MOLECULE_FACTORY.createMolecule(triple2);
        m1.add(triple1, m2);
        GRAPH.add(molecule);
        Molecule actualMolecule = GRAPH.findTopLevelMolecule(triple2);
        assertEquals("Equal molecules", molecule, actualMolecule);
    }

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
    }

    public void testEmptyMoleculeIterator() throws GraphException {
        Molecule molecule = MOLECULE_FACTORY.createMolecule();
        GRAPH.add(molecule);
        ClosableIterator<Molecule> iterator = GRAPH.iterator();
        try {
            assertFalse("Empty iterator", iterator.hasNext());
        } finally {
            iterator.close();
        }
    }

    public void testSimpleMoleculeIterator() throws GraphException {
        Molecule molecule = MOLECULE_FACTORY.createMolecule(R1R2B2, B2R2R1, B2R2B3);
        GRAPH.add(molecule);
        ClosableIterator<Molecule> iterator = GRAPH.iterator();
        try {
            assertTrue("Got item", iterator.hasNext());
            Molecule mol1 = iterator.next();
            assertEquals("Same molecule", 0, MOLECULE_COMPARATOR.compare(molecule, mol1));
            assertFalse("Empty iterator", iterator.hasNext());
        } finally {
            iterator.close();
        }
    }

    public void testMultiMoleculeIterator() throws GraphException {
        Triple[] triples = new Triple[]{B1R1R1, B1R2R2, B1R1B2, R1R2B2, B2R2R1, B2R2B3, B3R2R3, B3R2R2};
        Molecule molecule = MOLECULE_FACTORY.createMolecule(B1R1R1, B1R2R2, B1R1B2);
        Molecule sm1 = MOLECULE_FACTORY.createMolecule(R1R2B2, B2R2R1, B2R2B3);
        Molecule sm2 = MOLECULE_FACTORY.createMolecule(B3R2R3, B3R2R2);
        GRAPH.add(molecule);
        GRAPH.add(sm1);
        GRAPH.add(sm2);
        ClosableIterator<Molecule> iterator = GRAPH.iterator();
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
        Molecule molecule = MOLECULE_FACTORY.createMolecule(B1R1R1, B1R2R2, B1R1B2);
        Molecule sm1 = MOLECULE_FACTORY.createMolecule(R1R2B2, B2R2R1, B2R2B3);
        Molecule sm2 = MOLECULE_FACTORY.createMolecule(B3R2R3, B3R2R2);
        sm2.add(B2R2B3, sm2);
        molecule.add(B1R1B2, sm1);
        GRAPH.add(molecule);
        ClosableIterator<Molecule> iterator = GRAPH.iterator();
        try {
            assertTrue("Got item", iterator.hasNext());
            Molecule mol1 = iterator.next();
            assertEquals("Same molecule", 0, MOLECULE_COMPARATOR.compare(molecule, mol1));
            assertFalse("Empty iterator", iterator.hasNext());
        } finally {
            iterator.close();
        }
    }

    public void testMultiLevelMoleculeFind() throws GraphException {
        Molecule molecule = MOLECULE_FACTORY.createMolecule(B1R1R1, B1R2R2, B1R1B2);
        Molecule sm1 = MOLECULE_FACTORY.createMolecule(R1R2B2, B2R2R1, B2R2B3);
        Molecule sm2 = MOLECULE_FACTORY.createMolecule(B3R2R3, B3R2R2);
        sm2.add(B2R2B3, sm2);
        molecule.add(B1R1B2, sm1);
        GRAPH.add(molecule);
        Molecule mol1 = GRAPH.findTopLevelMolecule(B2R2R1);
        assertEquals("Same molecule", 0, MOLECULE_COMPARATOR.compare(molecule, mol1));
    }

    public void testProteinFindTriple() throws IOException, GraphException {
        readTextToGraph();
        final long triples = destGraph.getNumberOfTriples();
        final GraphElementFactory destElementFactory = destGraph.getElementFactory();
        ClosableIterator<Triple> interactions = destGraph.find(AnySubjectNode.ANY_SUBJECT_NODE,
                destElementFactory.createURIReference(RDF.TYPE),
                destElementFactory.createURIReference(URI.create("http://www.biopax.org/release/biopax-level2.owl#physicalInteraction")));
        try {
            assertTrue(interactions.hasNext());
            Triple interaction = interactions.next();
            Molecule interactionMolecule = destGraph.findTopLevelMolecule(interaction);
            assertEquals(triples, interactionMolecule.size());
        } finally {
            interactions.close();
        }
    }

    private void readTextToGraph() throws IOException, GraphException {
        String text =
                "[\n" +
                        "  _:a45 <http://biomanta.sourceforge.net/2007/07/biomanta_extension_02.owl#experimentalMethod> _:a58 .\n" +
                        "  [\n" +
                        "    _:a58 <http://biomanta.sourceforge.net/2007/07/biomanta_extension_02.owl#hasLocalId> \"18004\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" +
                        "    _:a58 <http://www.biopax.org/release/biopax-level2.owl#DB> \"psi-mi\" .\n" +
                        "    _:a58 <http://www.biopax.org/release/biopax-level2.owl#ID> \"MI:0019\" .\n" +
                        "    _:a58 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://biomanta.sourceforge.net/2007/07/biomanta_extension_02.owl#ExperimentMethod> .\n" +
                        "  ]\n" +
                        "  _:a45 <http://biomanta.sourceforge.net/2007/07/biomanta_extension_02.owl#observedInteraction> _:a48 .\n" +
                        "  [\n" +
                        "    _:a48 <http://www.biopax.org/release/biopax-level2.owl#PARTICIPANT> _:a50 .\n" +
                        "    [\n" +
                        "      _:a50 <http://biomanta.sourceforge.net/2007/07/biomanta_extension_02.owl#hasLocalId> \"18006\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" +
                        "      _:a50 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.biopax.org/release/biopax-level2.owl#physicalEntity> .\n" +
                        "    ]\n" +
                        "    _:a48 <http://www.biopax.org/release/biopax-level2.owl#PARTICIPANT> _:a51 .\n" +
                        "    [\n" +
                        "      _:a51 <http://biomanta.sourceforge.net/2007/07/biomanta_extension_02.owl#hasLocalId> \"18008\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" +
                        "      _:a51 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.biopax.org/release/biopax-level2.owl#physicalEntity> .\n" +
                        "    ]\n" +
                        "    _:a48 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.biopax.org/release/biopax-level2.owl#physicalInteraction> .\n" +
                        "  ]\n" +
                        "  _:a45 <http://biomanta.sourceforge.net/2007/07/biomanta_extension_02.owl#referenceForObservation> _:a47 .\n" +
                        "  [\n" +
                        "    _:a47 <http://biomanta.sourceforge.net/2007/07/biomanta_extension_02.owl#aboutLocalId> \"18004\"^^<http://www.w3.org/2001/XMLSchema#int> .\n" +
                        "    _:a47 <http://biomanta.sourceforge.net/2007/07/biomanta_extension_02.owl#link> \"http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=PUBMED&amp;cmd=Retrieve&amp;list_uids=9412461\" .\n" +
                        "    _:a47 <http://www.biopax.org/release/biopax-level2.owl#DB> \"PUBMED\"^^<http://www.w3.org/2001/XMLSchema#string> .\n" +
                        "    _:a47 <http://www.biopax.org/release/biopax-level2.owl#ID> \"9412461\"^^<http://www.w3.org/2001/XMLSchema#string> .\n" +
                        "    _:a47 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.biopax.org/release/biopax-level2.owl#publicationXref> .\n" +
                        "  ]\n" +
                        "  _:a45 <http://biomanta.sourceforge.net/2007/07/biomanta_extension_02.owl#sourceOfData> _:a53 .\n" +
                        "  [\n" +
                        "    _:a53 <http://www.biopax.org/release/biopax-level2.owl#DB> \"mips\"^^<http://www.w3.org/2001/XMLSchema#string> .\n" +
                        "    _:a53 <http://www.biopax.org/release/biopax-level2.owl#ID> \"41\"^^<http://www.w3.org/2001/XMLSchema#string> .\n" +
                        "    _:a53 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.biopax.org/release/biopax-level2.owl#unificationXref> .\n" +
                        "  ]\n" +
                        "  _:a45 <http://www.biopax.org/release/biopax-level2.owl#DB> \"http://ebi.ac.uk/\"^^<http://www.w3.org/2001/XMLSchema#string> .\n" +
                        "  _:a45 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://biomanta.sourceforge.net/2007/07/biomanta_extension_02.owl#ExperimentalObservation> .\n" +
                        "]";

        destGraph = FACTORY.getNewGraph();
        RegexMatcherFactory matcherFactory = new RegexMatcherFactoryImpl();
        NTripleUtil nTripleUtil = new NTripleUtilImpl(matcherFactory);
        final GraphElementFactory destElementFactory = destGraph.getElementFactory();
        URIReferenceParser referenceParser = new URIReferenceParserImpl(destElementFactory, nTripleUtil);
        ParserBlankNodeFactory blankNodeFactory = new ParserBlankNodeFactoryImpl(new MemMapFactory(), destElementFactory);
        final BlankNodeParser blankNodeParser = new BlankNodeParserImpl(blankNodeFactory);
        final LiteralMatcher literalMatcher = new RegexLiteralMatcher(matcherFactory, nTripleUtil);
        final LiteralParser literalParser = new LiteralParserImpl(destElementFactory, literalMatcher);
        TripleParser tripleParser = new TripleParserImpl(referenceParser, blankNodeParser, literalParser, 
                destGraph.getTripleFactory());
        TextToMolecule textToMolecule = new TextToMolecule(new RegexMatcherFactoryImpl(), tripleParser, MOLECULE_FACTORY);
        TextToMoleculeGraph graphBuilder = new TextToMoleculeGraph(textToMolecule);
        graphBuilder.parse(new StringReader(text));
        while (graphBuilder.hasNext()) {
            destGraph.add(graphBuilder.next());
        }
    }

    public void testMoleculeGraphToTextToMolecule() {
        Molecule molecule = MOLECULE_FACTORY.createMolecule();
        Molecule sm1 = MOLECULE_FACTORY.createMolecule(B2R2B3);
        sm1.add(B1R1B2);
        molecule.add(R1R2B2, sm1);
        GRAPH.add(molecule);

        RegexMatcherFactory matcherFactory = new RegexMatcherFactoryImpl();
        NTripleUtil nTripleUtil = new NTripleUtilImpl(matcherFactory);
        URIReferenceParser referenceParser = new URIReferenceParserImpl(GRAPH.getElementFactory(), nTripleUtil);
        ParserBlankNodeFactory blankNodeFactory = new ParserBlankNodeFactoryImpl(new MemMapFactory(),
                GRAPH.getElementFactory());
        final BlankNodeParser blankNodeParser = new BlankNodeParserImpl(blankNodeFactory);
        final LiteralMatcher literalMatcher = new RegexLiteralMatcher(matcherFactory, nTripleUtil);
        final LiteralParser literalParser = new LiteralParserImpl(GRAPH.getElementFactory(), literalMatcher);
        TripleParser tripleParser = new TripleParserImpl(referenceParser, blankNodeParser, literalParser,
                GRAPH.getTripleFactory());
        TextToMolecule textToMolecule = new TextToMolecule(new RegexMatcherFactoryImpl(), tripleParser, MOLECULE_FACTORY);
        textToMolecule.parse(new StringReader(GRAPH.toString()));
    }

    public void testAddNewRootTripleToMolecule() throws GraphException {
        Molecule molecule = MOLECULE_FACTORY.createMolecule(B2R2B3);
        Molecule molecule1 = MOLECULE_FACTORY.createMolecule(B2R2R1);
        molecule.add(B1R1B2, molecule1);
        GRAPH.add(molecule);
        GRAPH.add(molecule1);
        Molecule newMolecule = GRAPH.addRootTriple(molecule, R1R2B2);
        assertEquals("Same molecule", 0, GLOBAL_MOLECULE_COMPARATOR.compare(molecule.add(R1R2B2), newMolecule));
    }                                        

    public void testRemoveRootTripleFromMolecule() throws GraphException {
        Molecule molecule = MOLECULE_FACTORY.createMolecule(B2R2B3);
        Molecule molecule1 = MOLECULE_FACTORY.createMolecule(B2R2R1);
        molecule.add(B1R1B2, molecule1);
        GRAPH.add(molecule);
        GRAPH.add(molecule1);
        Molecule newMolecule = GRAPH.removeRootTriple(molecule, B1R1B2);
        molecule.remove(B1R1B2);
        assertEquals("Same molecule", 0, GLOBAL_MOLECULE_COMPARATOR.compare(molecule, newMolecule));
        assertEquals("Same molecule", 1, newMolecule.size());
    }

    public void testFindAnyNode() throws GraphException {
        Triple triple = new TripleImpl(ANY_SUBJECT_NODE, REF1, BNODE2);
        Molecule molecule = MOLECULE_FACTORY.createMolecule(B2R2B3);
        Molecule molecule1 = MOLECULE_FACTORY.createMolecule(B2R2R1);
        molecule.add(B1R1B2, molecule1);
        GRAPH.add(molecule);
        GRAPH.add(molecule1);
        ClosableIterator<Molecule> iterator = GRAPH.findMolecules(triple);
        assertTrue(iterator.hasNext());
        Molecule mol1 = iterator.next();
        assertEquals("Same molecule", 0, MOLECULE_COMPARATOR.compare(molecule, mol1));
        assertFalse(iterator.hasNext());
        iterator.close();
        triple = new TripleImpl(BNODE2, REF1, ANY_OBJECT_NODE);
        iterator = GRAPH.findMolecules(triple);
        assertFalse(iterator.hasNext());
        iterator.close();
    }
}
