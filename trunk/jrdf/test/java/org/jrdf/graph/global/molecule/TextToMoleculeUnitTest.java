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
import org.jrdf.MemoryJRDFFactory;
import org.jrdf.collection.MemMapFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import org.jrdf.graph.global.GroundedTripleComparatorImpl;
import static org.jrdf.graph.global.molecule.GlobalGraphTestUtil.createMultiLevelMolecule;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R1B2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R1R1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B1R2R2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R2B3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B2R2R1;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B3R2R2;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.B3R2R3;
import static org.jrdf.graph.global.molecule.LocalGraphTestUtil.R1R2B2;
import org.jrdf.graph.global.molecule.mem.MoleculeFactoryImpl;
import org.jrdf.graph.global.molecule.mem.MoleculeHeadTripleComparatorImpl;
import org.jrdf.graph.global.molecule.mem.MoleculeTraverserImpl;
import org.jrdf.graph.local.TripleComparatorFactoryImpl;
import org.jrdf.parser.ntriples.parser.NodeParsersFactory;
import org.jrdf.parser.ntriples.parser.NodeParsersFactoryImpl;
import org.jrdf.parser.ntriples.parser.TripleParserImpl;
import org.jrdf.parser.ntriples.parser.NodeMaps;
import org.jrdf.parser.ntriples.parser.NodeMapsImpl;
import org.jrdf.parser.ntriples.parser.RegexTripleParser;
import org.jrdf.parser.ntriples.parser.RegexTripleParserImpl;
import org.jrdf.util.boundary.RegexMatcherFactoryImpl;
import static org.jrdf.util.test.SetUtil.asSet;

import java.io.StringReader;

public class TextToMoleculeUnitTest extends TestCase {
    private Graph graph = MemoryJRDFFactory.getFactory().getNewGraph();
    private TextToMolecule textToMolecule;
    private TripleParserImpl tripleParser;
    private MoleculeTraverser traverser;
    private static final TripleComparator TRIPLE_COMPARATOR = new TripleComparatorFactoryImpl().newComparator();
    private static final TripleComparator COMPARATOR = new GroundedTripleComparatorImpl(TRIPLE_COMPARATOR);
    private static final MoleculeComparator MOLECULE_COMPARATOR = new MoleculeHeadTripleComparatorImpl(COMPARATOR);
    private static final TripleComparator GLOBAL_TRIPLE_COMPARATOR = new GroundedTripleComparatorFactoryImpl().
        newComparator();
    private static final MoleculeComparator GLOBAL_MOLECULE_COMPARATOR = new MoleculeHeadTripleComparatorImpl(
        GLOBAL_TRIPLE_COMPARATOR);
    private static final MoleculeFactoryImpl FACTORY = new MoleculeFactoryImpl(MOLECULE_COMPARATOR);

    public void setUp() throws Exception {
        super.setUp();
        final NodeParsersFactory parsersFactory = new NodeParsersFactoryImpl(graph, new MemMapFactory());
        final RegexMatcherFactoryImpl matcherFactory = new RegexMatcherFactoryImpl();
        final NodeMaps nodeMaps = new NodeMapsImpl(parsersFactory.getUriReferenceParser(),
            parsersFactory.getBlankNodeParser(), parsersFactory.getLiteralParser());
        final RegexTripleParser parser = new RegexTripleParserImpl(matcherFactory, graph.getTripleFactory(),
            nodeMaps);
        tripleParser = new TripleParserImpl(matcherFactory, parsersFactory.getBlankNodeParser(),
            parser);
        traverser = new MoleculeTraverserImpl();
        textToMolecule = new TextToMolecule(matcherFactory, tripleParser, FACTORY);
    }

    public void testOneLevelMolecule() {
        Molecule molecule = createMultiLevelMolecule(asSet(B1R1R1, B1R2R2, B1R1B2),
                asSet(R1R2B2, B2R2R1, B2R2B3), asSet(B3R2R3, B3R2R2));
        final StringBuilder result = new StringBuilder();
        final MoleculeHandler moleculeToText = new MoleculeToText(result);
        traverser.traverse(molecule, moleculeToText);
        String toParse = result.toString();
        final Molecule molecule1 = textToMolecule.parse(new StringReader(toParse));
        assertEquals("Same molecule", 0, GLOBAL_MOLECULE_COMPARATOR.compare(molecule, molecule1));
    }

    public void testSimpleMolecule() {
        Molecule molecule = createMultiLevelMolecule(asSet(B1R1R1), asSet(B1R1B2), asSet(B2R2R1));
        final StringBuilder result = new StringBuilder();
        final MoleculeHandler moleculeToText = new MoleculeToText(result);
        traverser.traverse(molecule, moleculeToText);
        final String text = result.toString();
        Molecule molecule1 = textToMolecule.parse(new StringReader(text));
        assertEquals("Same molecule", 0, GLOBAL_MOLECULE_COMPARATOR.compare(molecule, molecule1));
    }
}
