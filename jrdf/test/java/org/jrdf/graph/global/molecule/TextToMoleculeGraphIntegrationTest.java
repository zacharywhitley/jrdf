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

import org.jrdf.collection.MemMapFactory;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.parser.ntriples.parser.NodeMaps;
import org.jrdf.parser.ntriples.parser.NodeMapsImpl;
import org.jrdf.parser.ntriples.parser.NodeParsersFactory;
import org.jrdf.parser.ntriples.parser.NodeParsersFactoryImpl;
import org.jrdf.parser.ntriples.parser.RegexTripleParser;
import org.jrdf.parser.ntriples.parser.RegexTripleParserImpl;
import org.jrdf.parser.ntriples.parser.TripleParser;
import org.jrdf.parser.ntriples.parser.TripleParserImpl;
import org.jrdf.util.boundary.RegexMatcherFactoryImpl;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

public class TextToMoleculeGraphIntegrationTest extends AbstractMoleculeGraphIntegrationTest {
    private MoleculeGraph destGraph;
    private TextToMolecule textToMolecule;
    private TripleParserImpl tripleParser;
    private TextToMoleculeGraph graphBuilder;

    public void setUp() throws Exception {
        super.setUp();
        destGraph = factory.getNewGraph();
        final RegexMatcherFactoryImpl matcherFactory = new RegexMatcherFactoryImpl();
        final NodeParsersFactory parsersFactory = new NodeParsersFactoryImpl(destGraph, new MemMapFactory());
        final NodeMaps nodeMaps = new NodeMapsImpl(parsersFactory.getUriReferenceParser(),
            parsersFactory.getBlankNodeParser(), parsersFactory.getLiteralParser());
        final RegexTripleParser parser = new RegexTripleParserImpl(matcherFactory, destGraph.getTripleFactory(),
            nodeMaps);
        final TripleParser tripleParser = new TripleParserImpl(matcherFactory, parsersFactory.getBlankNodeParser(),
            parser);
        textToMolecule = new TextToMolecule(matcherFactory, tripleParser, moleculeFactory);
        graphBuilder = new TextToMoleculeGraph(textToMolecule);
    }

    public void tearDown() {
        destGraph.clear();
        destGraph.close();
        super.tearDown();
    }

    public void testOneMoleculeGraph() throws Exception {
        Molecule m1 = moleculeFactory.createMolecule(b1r1b2);
        graph.add(m1);
        graphBuilder.parse(new StringReader(graph.toString()));
        assertTrue(graphBuilder.hasNext());
        Molecule mol = graphBuilder.next();
        assertEquals("Same molecule", 0, globalMoleculeComparator.compare(m1, mol));
        assertFalse(graphBuilder.hasNext());
    }

    public void testTwoLevelMolecule() throws Exception {
        Molecule m1 = moleculeFactory.createMolecule(b1r1b2);
        Molecule m2 = moleculeFactory.createMolecule(b2r2b3);
        m1.add(b1r1b2, m2);
        Set<Molecule> mols = new HashSet<Molecule>();
        mols.add(m1);
        graph.add(m1);
        graphBuilder.parse(new StringReader(graph.toString()));
        assertTrue(graphBuilder.hasNext());
        Molecule mol = graphBuilder.next();
        assertEquals("Same molecule", 0, globalMoleculeComparator.compare(m1, mol));
        assertFalse(graphBuilder.hasNext());
    }

    public void testTwoMolecules() throws Exception {
        Molecule m1 = moleculeFactory.createMolecule(b1r1b2);
        Molecule m2 = moleculeFactory.createMolecule(b1r2r2);
        graph.add(m1);
        graph.add(m2);
        Set<Molecule> mols = new HashSet<Molecule>();
        mols.add(m1);
        mols.add(m2);
        graphBuilder.parse(new StringReader(graph.toString()));
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
            if (globalMoleculeComparator.compare(mol, molecule) == 0) {
                return true;
            }
        }
        return false;
    }
}
