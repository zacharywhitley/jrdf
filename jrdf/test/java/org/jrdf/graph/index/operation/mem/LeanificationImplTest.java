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

package org.jrdf.graph.index.operation.mem;

import junit.framework.TestCase;
import org.jrdf.JRDFFactory;
import org.jrdf.SortedMemoryJRDFFactoryImpl;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.index.operation.Leanification;
import org.jrdf.parser.Parser;
import org.jrdf.parser.rdfxml.GraphRdfXmlParser;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.EscapeURL;

import java.net.URI;
import java.net.URL;

public class LeanificationImplTest extends TestCase {
    private static final JRDFFactory JRDF_FACTORY = SortedMemoryJRDFFactoryImpl.getFactory();
    private static final URI FOAF_KNOWS = URI.create("http://xmlns.com/foaf/0.1/knows");
    private BlankNode blankNodeA;
    private BlankNode blankNodeB;

    private URIReference knows;


    private BlankNode blankNodeC;
    private BlankNode blankNodeD;

    private Graph phatGraph = JRDF_FACTORY.getNewGraph();
    private GraphElementFactory graphElementFactory = phatGraph.getElementFactory();
    private URIReference exp1;
    private URIReference exp2;
    private BlankNode blankNode1;
    private BlankNode blankNode2;
    private BlankNode blankNode3;
    private BlankNode blankNode4;
    private BlankNode blankNode5;
    private BlankNode blankNode6;
    private URIReference predicateHas;
    private URIReference predicateCellType;

    private URIReference predicateType;

    private URIReference uriRefVegeCell;

    public void setUp() throws Exception {
        blankNode1 = graphElementFactory.createBlankNode();
        blankNode2 = graphElementFactory.createBlankNode();
        blankNode3 = graphElementFactory.createBlankNode();
        blankNode4 = graphElementFactory.createBlankNode();
        blankNode5 = graphElementFactory.createBlankNode();
        blankNode6 = graphElementFactory.createBlankNode();

        predicateHas = graphElementFactory.createURIReference(URI.create("urn:has"));
        predicateCellType = graphElementFactory.createURIReference(URI.create("urn:cellType"));
        predicateType = graphElementFactory.createURIReference(URI.create("urn:type"));

        exp1 = graphElementFactory.createURIReference(URI.create("urn:exp1"));
        exp2 = graphElementFactory.createURIReference(URI.create("urn:exp2"));
        uriRefVegeCell = graphElementFactory.createURIReference(URI.create("urn:vege"));
    }

    public void testRemoveRedundantTriples() throws Exception {
        getGraph1();
        getGraph2();
        getGraph3();
    }

    public void testLeanificationWithBiomanta() throws Exception {
        Graph graph = loadGraph("foaf1.owl");
        print("Graph before: ", graph);
        Leanification graphLeanifier = new LeanificationImpl();
        graphLeanifier.leanify(graph);
        print("Graph after: ", graph);
    }
    private void graphLeanificationOutput(Graph graph)
        throws Exception {
        print("Graph before: ", graph);
        Leanification graphLeanifier = new LeanificationImpl();
        graphLeanifier.leanify(graph);
        print("Graph after: ", graph);
    }

    private void getGraph1() throws Exception {
        phatGraph.add(blankNode1, predicateCellType, blankNode2);
        phatGraph.add(blankNode2, predicateCellType, blankNode3);
        phatGraph.add(blankNode3, predicateCellType, blankNode4);
        phatGraph.add(blankNode4, predicateCellType, blankNode5);
        graphLeanificationOutput(phatGraph);
    }
    private void getGraph2() throws Exception {
        phatGraph.add(blankNode1, predicateCellType, blankNode2);
        phatGraph.add(blankNode2, predicateCellType, blankNode3);
        phatGraph.add(blankNode3, predicateCellType, exp1);
        phatGraph.add(blankNode4, predicateCellType, blankNode4);
        graphLeanificationOutput(phatGraph);
    }

    private void getGraph3() throws Exception {
        phatGraph.add(blankNode1, predicateCellType, blankNode2);
        phatGraph.add(blankNode2, predicateCellType, blankNode3);
        phatGraph.add(exp1, predicateCellType, blankNode4);
        phatGraph.add(exp1, predicateCellType, blankNode3);
        phatGraph.add(blankNode3, predicateCellType, blankNode2);
        phatGraph.add(blankNode4, predicateCellType, blankNode3);
        graphLeanificationOutput(phatGraph);
    }

    private Graph loadGraph(String fileName) throws Exception {
        Graph graph = JRDF_FACTORY.getNewGraph();
        URL url = getClass().getResource(fileName);
        Parser parser =  new GraphRdfXmlParser(graph);
        parser.parse(url.openStream(), EscapeURL.toEscapedString(url));
        return graph;
    }

    private void print(String message, Graph graph) throws Exception {
        if (graph.isEmpty()) {
            throw new IllegalArgumentException("Graph is empty.");
        }
        ClosableIterator<Triple> iterator = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        System.out.println(message);
        while (iterator.hasNext()) {
            System.out.println(String.valueOf(iterator.next()));
        }
        System.out.println("");
    }
}
