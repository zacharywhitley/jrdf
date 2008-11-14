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
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.global.MoleculeGraph;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.B1R1B2;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.B1R2R2;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.B2R2B3;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.factory;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.GLOBAL_MOLECULE_COMPARATOR;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.GRAPH;
import static org.jrdf.graph.global.molecule.MoleculeGraphTestUtil.MOLECULE_FACTORY;
import org.jrdf.graph.global.molecule.mem.MoleculeTraverserImpl;
import org.jrdf.collection.MemMapFactory;
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
import org.jrdf.parser.ntriples.parser.TripleParserImpl;
import org.jrdf.parser.ntriples.parser.URIReferenceParser;
import org.jrdf.parser.ntriples.parser.URIReferenceParserImpl;
import org.jrdf.util.boundary.RegexMatcherFactory;
import org.jrdf.util.boundary.RegexMatcherFactoryImpl;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class TextToMoleculeGraphUnitTest extends TestCase {
    private MoleculeGraph destGraph;
    private TextToMolecule textToMolecule;
    private TripleParserImpl tripleParser;
    private MoleculeTraverser traverser;
    private TextToMoleculeGraph graphBuilder;
    private GraphElementFactory destElementFactory;

    public void setUp() throws Exception {
        super.setUp();
        MoleculeGraphTestUtil.setUp();
        destGraph = factory.getGraph();
        destElementFactory = destGraph.getElementFactory();
        RegexMatcherFactory matcherFactory = new RegexMatcherFactoryImpl();
        NTripleUtil nTripleUtil = new NTripleUtilImpl(matcherFactory);
        URIReferenceParser referenceParser = new URIReferenceParserImpl(destElementFactory, nTripleUtil);
        ParserBlankNodeFactory blankNodeFactory = new ParserBlankNodeFactoryImpl(new MemMapFactory(), destElementFactory);
        final BlankNodeParser blankNodeParser = new BlankNodeParserImpl(blankNodeFactory);
        final LiteralMatcher literalMatcher = new RegexLiteralMatcher(matcherFactory, nTripleUtil);
        final LiteralParser literalParser = new LiteralParserImpl(destElementFactory, literalMatcher);
        traverser = new MoleculeTraverserImpl();
        tripleParser = new TripleParserImpl(referenceParser, blankNodeParser, literalParser, destGraph.getTripleFactory());
        textToMolecule = new TextToMolecule(new RegexMatcherFactoryImpl(), tripleParser, MOLECULE_FACTORY);
        graphBuilder = new TextToMoleculeGraph(textToMolecule);
    }

    public void tearDown() {
        destGraph.clear();
        destGraph.close();
        MoleculeGraphTestUtil.close();
    }

    public void testOneMoleculeGraph() throws IOException {
        Molecule m1 = MOLECULE_FACTORY.createMolecule(B1R1B2);
        GRAPH.add(m1);
        graphBuilder.parse(new StringReader(GRAPH.toString()));
        assertTrue(graphBuilder.hasNext());
        Molecule mol = graphBuilder.next();
        assertEquals("Same molecule", 0, GLOBAL_MOLECULE_COMPARATOR.compare(m1, mol));
        assertFalse(graphBuilder.hasNext());
    }

    public void testTwoLevelMolecule() throws IOException {
        Molecule m1 = MOLECULE_FACTORY.createMolecule(B1R1B2);
        Molecule m2 = MOLECULE_FACTORY.createMolecule(B2R2B3);
        m1.add(B1R1B2, m2);
        Set<Molecule> mols = new HashSet<Molecule>();
        mols.add(m1);
        GRAPH.add(m1);
        graphBuilder.parse(new StringReader(GRAPH.toString()));
        assertTrue(graphBuilder.hasNext());
        Molecule mol = graphBuilder.next();
        assertEquals("Same molecule", 0, GLOBAL_MOLECULE_COMPARATOR.compare(m1, mol));
        assertFalse(graphBuilder.hasNext());
    }

    public void testTwoMolecules() throws IOException, GraphException {
        Molecule m1 = MOLECULE_FACTORY.createMolecule(B1R1B2);
        Molecule m2 = MOLECULE_FACTORY.createMolecule(B1R2R2);
        GRAPH.add(m1);
        GRAPH.add(m2);
        Set<Molecule> mols = new HashSet<Molecule>();
        mols.add(m1);
        mols.add(m2);
        graphBuilder.parse(new StringReader(GRAPH.toString()));
        int size = 0;
        while (graphBuilder.hasNext()) {
            Molecule mol = graphBuilder.next();
            destGraph.add(mol);
            size++;
            assertTrue(setContainsMolecule(mols, mol));
        }
        assertEquals(mols.size(), size);
    }

    private boolean setContainsMolecule(Set<Molecule> set, Molecule molecule) {
        for (Molecule mol : set) {
            if (GLOBAL_MOLECULE_COMPARATOR.compare(mol, molecule) == 0) {
                return true;
            }
        }
        return false;
    }
}
