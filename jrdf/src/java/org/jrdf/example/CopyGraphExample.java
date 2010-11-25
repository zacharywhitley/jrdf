/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 *  ====================================================================
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
 */

package org.jrdf.example;

import org.jrdf.GlobalJRDFFactory;
import org.jrdf.JRDFFactory;
import org.jrdf.SortedMemoryGlobalJRDFFactory;
import org.jrdf.SortedMemoryJRDFFactory;
import org.jrdf.collection.MemCollectionFactory;
import org.jrdf.collection.MemMapFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.global.MoleculeGraph;
import org.jrdf.graph.global.molecule.GraphDecomposer;
import org.jrdf.graph.global.molecule.Molecule;
import org.jrdf.graph.global.molecule.MoleculeComparator;
import org.jrdf.graph.global.molecule.MoleculeFactory;
import org.jrdf.graph.global.molecule.TextToMolecule;
import org.jrdf.graph.global.molecule.TextToMoleculeGraph;
import org.jrdf.graph.global.molecule.mem.MoleculeFactoryImpl;
import org.jrdf.graph.global.molecule.mem.MoleculeHeadTripleComparatorImpl;
import org.jrdf.graph.global.molecule.mem.NaiveGraphDecomposerImpl;
import org.jrdf.graph.local.TripleComparatorFactoryImpl;
import org.jrdf.parser.Parser;
import org.jrdf.parser.ntriples.parser.NodeMaps;
import org.jrdf.parser.ntriples.parser.NodeMapsImpl;
import org.jrdf.parser.ntriples.parser.NodeParsersFactory;
import org.jrdf.parser.ntriples.parser.NodeParsersFactoryImpl;
import org.jrdf.parser.ntriples.parser.RegexTripleParser;
import org.jrdf.parser.ntriples.parser.RegexTripleParserImpl;
import org.jrdf.parser.ntriples.parser.TripleParser;
import org.jrdf.parser.ntriples.parser.TripleParserImpl;
import org.jrdf.parser.rdfxml.GraphRdfXmlParser;
import org.jrdf.util.boundary.RegexMatcherFactoryImpl;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.Set;

import static org.jrdf.example.performance.ReadRdfUrlTestUtil.getDocumentURL;
import static org.jrdf.example.performance.ReadRdfUrlTestUtil.getInputStream;
import static org.jrdf.util.EscapeURL.toEscapedString;

public class CopyGraphExample {
    private static final String DEFAULT_RDF_URL = "http://planetrdf.com/index.rdf";
    private static final JRDFFactory JRDF_FACTORY = SortedMemoryJRDFFactory.getFactory();
    private static final GlobalJRDFFactory GLOBAL_JRDF_FACTORY = SortedMemoryGlobalJRDFFactory.getFactory();
    private static final TripleComparator COMPARATOR = new TripleComparatorFactoryImpl().newComparator();
    private static final MoleculeComparator MOLECULE_COMPARATOR = new MoleculeHeadTripleComparatorImpl(COMPARATOR);
    private static final MoleculeFactory MOLECULE_FACTORY = new MoleculeFactoryImpl(MOLECULE_COMPARATOR);

    public static void main(String[] args) throws Exception {
        URL url = getDocumentURL(args, DEFAULT_RDF_URL);
        InputStream in = getInputStream(url);
        try {
            final Graph srcGraph = JRDF_FACTORY.getGraph();
            final MoleculeGraph destGraph = GLOBAL_JRDF_FACTORY.getGraph();
            Parser parser = new GraphRdfXmlParser(srcGraph, new MemMapFactory());
            parser.parse(in, toEscapedString(url));
            TextToMoleculeGraph graphBuilder = getGraphBuilder(destGraph);
            graphBuilder.parse(new StringReader(graphToMoleculeText(srcGraph)));
            while (graphBuilder.hasNext()) {
                Molecule molecule = graphBuilder.next();
                destGraph.add(molecule);
            }
        } finally {
            in.close();
        }
    }

    private static String graphToMoleculeText(Graph srcGraph) {
        GraphDecomposer graphDecomposer = getDecompose();
        Set<Molecule> molecules = graphDecomposer.decompose(srcGraph);
        StringBuilder builder = new StringBuilder();
        for (Molecule molecule : molecules) {
            builder.append(molecule.toString()).append("\n");
        }
        return builder.toString();
    }

    private static GraphDecomposer getDecompose() {
        MemCollectionFactory setFactory = new MemCollectionFactory();
        return new NaiveGraphDecomposerImpl(setFactory, MOLECULE_FACTORY, MOLECULE_COMPARATOR, COMPARATOR);
    }

    private static TextToMoleculeGraph getGraphBuilder(MoleculeGraph destGraph) {
        final RegexMatcherFactoryImpl matcherFactory = new RegexMatcherFactoryImpl();
        final NodeParsersFactory parsersFactory = new NodeParsersFactoryImpl(destGraph, new MemMapFactory());
        final NodeMaps nodeMaps = new NodeMapsImpl(parsersFactory.getUriReferenceParser(),
            parsersFactory.getBlankNodeParser(), parsersFactory.getLiteralParser());
        final RegexTripleParser parser = new RegexTripleParserImpl(matcherFactory, destGraph.getTripleFactory(),
            nodeMaps);
        final TripleParser tripleParser = new TripleParserImpl(matcherFactory, parsersFactory.getBlankNodeParser(),
            parser);
        final TextToMolecule textToMolecule = new TextToMolecule(matcherFactory, tripleParser, MOLECULE_FACTORY);
        return new TextToMoleculeGraph(textToMolecule);
    }
}
