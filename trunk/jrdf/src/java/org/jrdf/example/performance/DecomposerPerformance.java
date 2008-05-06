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

package org.jrdf.example.performance;

import org.jrdf.JRDFFactory;
import org.jrdf.SortedMemoryJRDFFactory;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.NodeComparator;
import org.jrdf.graph.TripleComparator;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.global.GroundedTripleComparatorFactoryImpl;
import org.jrdf.graph.global.GroundedTripleComparatorImpl;
import org.jrdf.graph.global.molecule.BlankNodeMapper;
import org.jrdf.graph.global.molecule.BlankNodeMapperImpl;
import org.jrdf.graph.global.molecule.LocalMergeSubmolecules;
import org.jrdf.graph.global.molecule.MergeLocalSubmoleculesImpl;
import org.jrdf.graph.global.molecule.MergeSubmolecules;
import org.jrdf.graph.global.molecule.MergeSubmoleculesImpl;
import org.jrdf.graph.global.molecule.MoleculeSubsumptionImpl;
import org.jrdf.graph.global.molecule.mem.NewGraphDecomposer;
import org.jrdf.graph.global.molecule.mem.NewMolecule;
import org.jrdf.graph.global.molecule.mem.NewMoleculeComparator;
import org.jrdf.graph.global.molecule.mem.NewMoleculeFactory;
import org.jrdf.graph.global.molecule.mem.NewMoleculeFactoryImpl;
import org.jrdf.graph.global.molecule.mem.NewMoleculeHeadTripleComparatorImpl;
import org.jrdf.graph.global.molecule.mem.NewNaiveGraphDecomposerImpl;
import org.jrdf.graph.local.BlankNodeComparator;
import org.jrdf.graph.local.LocalizedBlankNodeComparatorImpl;
import org.jrdf.graph.local.LocalizedNodeComparator;
import org.jrdf.graph.local.LocalizedNodeComparatorImpl;
import org.jrdf.graph.local.NodeComparatorImpl;
import org.jrdf.graph.local.TripleComparatorImpl;
import org.jrdf.parser.ParseException;
import org.jrdf.parser.Parser;
import org.jrdf.parser.StatementHandlerException;
import org.jrdf.parser.rdfxml.GraphRdfXmlParser;
import org.jrdf.set.MemSortedSetFactory;
import org.jrdf.util.EscapeURL;
import org.jrdf.util.NodeTypeComparator;
import org.jrdf.util.NodeTypeComparatorImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class DecomposerPerformance {
    private static final int CHAIN_SIZE = 3;
    private static final int LOOP_SIZE = 9;
    private static final int NUMBER_OF_MOLECULES = 100;
    private final JRDFFactory factory = SortedMemoryJRDFFactory.getFactory();
    private final Graph graph = factory.getNewGraph();
    private final GraphElementFactory elementFactory = graph.getElementFactory();
    private final NodeTypeComparator typeComparator = new NodeTypeComparatorImpl();
    private final LocalizedNodeComparator localNodeComparator = new LocalizedNodeComparatorImpl();
    private final BlankNodeComparator blankNodeComparator = new LocalizedBlankNodeComparatorImpl(localNodeComparator);
    private final NodeComparator nodeComparator = new NodeComparatorImpl(typeComparator, blankNodeComparator);
    private final TripleComparator tripleComparator = new TripleComparatorImpl(nodeComparator);
    private final TripleComparator comparator = new GroundedTripleComparatorImpl(tripleComparator);
    private final NewMoleculeComparator moleculeComparator = new NewMoleculeHeadTripleComparatorImpl(comparator);
    private final MemSortedSetFactory setFactory = new MemSortedSetFactory();
    private final NewMoleculeFactory moleculeFactory = new NewMoleculeFactoryImpl(moleculeComparator);
    private final NewGraphDecomposer decomposer = new NewNaiveGraphDecomposerImpl(setFactory, moleculeFactory,
        moleculeComparator, comparator);
    private final BlankNodeMapper mapper = new BlankNodeMapperImpl();
    private final MergeSubmolecules globalMerger = new MergeSubmoleculesImpl(comparator, moleculeComparator,
        moleculeFactory, new MoleculeSubsumptionImpl());
    private final LocalMergeSubmolecules localMerger = new MergeLocalSubmoleculesImpl(globalMerger, moleculeFactory);

    private void testPerformance() throws Exception {
        for (int i = 0; i < NUMBER_OF_MOLECULES; i++) {
            addChain("urn:foo");
        }
        System.err.println("Graph size = " + graph.getNumberOfTriples());
        long startTime = System.currentTimeMillis();
        Set<NewMolecule> moleculeSet = decomposer.decompose(graph);
        NewMoleculeHeadTripleComparatorImpl tripleComparator = new NewMoleculeHeadTripleComparatorImpl(
            new GroundedTripleComparatorFactoryImpl().newComparator());
        Set<NewMolecule> results = new TreeSet<NewMolecule>(tripleComparator);
        NewMolecule[] molecules = moleculeSet.toArray(new NewMolecule[]{});
        results.add(molecules[0]);
        int count = 0;
        for (int i = 0; i < molecules.length; i++) {
            for (int j = i + 1; j < molecules.length; j++) {
                Map<BlankNode, BlankNode> map = mapper.createMap(molecules[i], molecules[j]);
                NewMolecule molecule = localMerger.merge(molecules[i], molecules[j], map);
                //System.err.println("merging i = " + i + ", j = " + j);
                /*if (molecule != null) {
                    System.err.println("merged = " + molecule.toString());
                }*/
                addResult(results, molecules, i, molecule);
                count++;
            }
        }
        System.err.println("Time taken " + (System.currentTimeMillis() - startTime) + " comparisons: " + count);
    }

    private void addResult(Set<NewMolecule> results, NewMolecule[] molecules, int i, NewMolecule molecule) {
        if (molecule != null) {
            if (!results.contains(molecule)) {
                results.add(molecule);
            }
        } else {
            if (!results.contains(molecules[i])) {
                results.add(molecules[i]);
            }
        }
    }

    private void addGrounded(String predicate) throws Exception {
        URIReference p = elementFactory.createURIReference(URI.create(predicate));
        graph.add(p, p, p);
    }

    private void addTriple(String predicate) throws Exception {
        BlankNode s = elementFactory.createBlankNode();
        URIReference p = elementFactory.createURIReference(URI.create(predicate));
        BlankNode o = elementFactory.createBlankNode();
        graph.add(s, p, o);
    }

    private void addChain(String predicate) throws Exception {
        URIReference p1 = elementFactory.createURIReference(URI.create(predicate));
        BlankNode thisNode = elementFactory.createBlankNode();
        for (int i = 0; i < CHAIN_SIZE; i++) {
            BlankNode nextNode = elementFactory.createBlankNode();
            graph.add(thisNode, p1, nextNode);
            thisNode = nextNode;
        }
    }

    private void addLoop(String predicate) throws Exception {
        URIReference p1 = elementFactory.createURIReference(URI.create(predicate));
        BlankNode firstNode = elementFactory.createBlankNode();
        BlankNode thisNode = firstNode;
        for (int i = 0; i < LOOP_SIZE; i++) {
            BlankNode nextNode = elementFactory.createBlankNode();
            graph.add(thisNode, p1, nextNode);
            thisNode = nextNode;
        }
        graph.add(thisNode, p1, firstNode);
    }

    public static void main(String[] args) throws Exception {
        DecomposerPerformance performance = new DecomposerPerformance();
        performance.testPerformance();
    }

    private static void readFromInputStream(Graph graph, URL url)
        throws IOException, GraphException, ParseException, StatementHandlerException {
        System.out.println("Reading RDF/XML from: " + url.getPath());
        InputStream in = getInputStream(url);
        Parser parser = new GraphRdfXmlParser(graph);
        parser.parse(in, EscapeURL.toEscapedString(url));
        in.close();
    }

    private static InputStream getInputStream(URL url) throws IOException {
        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        urlConnection.connect();
        String encoding = urlConnection.getContentEncoding();
        InputStream in;
        if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
            in = new GZIPInputStream(urlConnection.getInputStream());
        } else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
            in = new InflaterInputStream(urlConnection.getInputStream(), new Inflater(true));
        } else {
            in = urlConnection.getInputStream();
        }
        return in;
    }
}
