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

package org.jrdf.example;

import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.BlankNode;
import org.jrdf.map.MapFactory;
import org.jrdf.util.TempDirectoryHandler;
import org.jrdf.parser.Parser;
import org.jrdf.parser.rdfxml.RdfXmlParser;
import org.jrdf.graph.local.iterator.ClosableIterator;
import org.jrdf.writer.BlankNodeRegistry;
import org.jrdf.writer.RdfWriter;
import org.jrdf.writer.mem.RdfNamespaceMapImpl;
import org.jrdf.writer.rdfxml.RdfXmlWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGraphPerformance {
    private static final int NUMBER_OF_NODES_TO_ADD = 50000;
    private static final int NUMBER_OF_NODES_TO_FIND = 100;
    private static final int NUMBER_OF_PREDICATES = 10;
    private static final int NO_PREDICATES = NUMBER_OF_PREDICATES;
    private static final int NO_MILLISECONDS_IN_A_SECOND = 1000;
    private static final String SUBJECT_PREFIX = "http://foo";
    private static final String PREDICATE_PREFIX = "http://bar";
    private static final String OBJECT_PREFIX = "http://foo";
    private static final String URI_STRING = "http://foo/bar";
    private static final String PATH = "/org/jrdf/example/pizza.rdf";
    private final TempDirectoryHandler dirHandler = new TempDirectoryHandler();
    private List<URI> predicates = new ArrayList<URI>();
    private GraphElementFactory graphElementFactory;
    private int noFinds;

    public AbstractGraphPerformance() {
        for (int i = 0; i < NO_PREDICATES; i++) {
            URI predicateURI = URI.create(PREDICATE_PREFIX + i);
            predicates.add(predicateURI);
        }
    }

    public void testPerformance() throws Exception {
        Graph graph = getGraph();
        graphElementFactory = graph.getElementFactory();
        //parsePerformance();
        addPerformance(NUMBER_OF_NODES_TO_ADD, graph);
        writePerformance(graph);
        findPerformance(NUMBER_OF_NODES_TO_FIND, graph);
    }

    protected abstract Graph getGraph();

    protected abstract MapFactory getMapFactory();

    protected abstract BlankNodeRegistry getBlankNodeRegistry();

    public void parsePerformance() throws Exception {
        URL source = getClass().getResource(PATH);
        InputStream stream = source.openStream();
        final Graph graph = getGraph();
        Parser parser = new RdfXmlParser(graph.getElementFactory(), getMapFactory());
        long startTime = System.currentTimeMillis();
        parser.parse(stream, URI_STRING);
        outputResult(graph, startTime, "Testing Parsing Performance (" + PATH + "): ");
    }

    /**
     * Creates 10 times the given number of nodes for a given graph.
     *
     * @param numberOfNodes the number of nodes to create with 10 objects.
     * @param graph         the graph to add.
     * @throws Exception if there is an exception adding the nodes.
     */
    private void addPerformance(int numberOfNodes, Graph graph) throws Exception {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numberOfNodes; i++) {
            BlankNode subject = graphElementFactory.createBlankNode();
            for (int j = 0; j < NUMBER_OF_PREDICATES; j++) {
                graph.add(subject,
                        graphElementFactory.createURIReference(URI.create(PREDICATE_PREFIX + j)),
                        graphElementFactory.createURIReference(URI.create(OBJECT_PREFIX + j)));
            }
        }
        outputResult(graph, startTime, "Testing Add Performance:");
    }

    private void findPerformance(int nodes, Graph graph) throws Exception {
        long startTime = System.currentTimeMillis();
        for (int index = 0; index < nodes; index++) {
            URI subjectURI = URI.create(SUBJECT_PREFIX + index);
            find1(graph, subjectURI);
        }
        outputResult(graph, startTime, "Testing Find Performance: " + noFinds);
    }

    private void writePerformance(Graph graph) throws Exception {
        long startTime = System.currentTimeMillis();
        Writer out = new FileWriter(new File(dirHandler.getDir(), "foo.rdf"));
        try {
            BlankNodeRegistry nodeRegistry = getBlankNodeRegistry();
            nodeRegistry.clear();
            RdfWriter writer = new RdfXmlWriter(nodeRegistry, new RdfNamespaceMapImpl());
            writer.write(graph, out);
        } finally {
            out.close();
        }
        outputResult(graph, startTime, "Testing RDF/XML Write Performance:");
    }

    private void outputResult(Graph graph, long startTime, String what) throws GraphException {
        long finishTime = System.currentTimeMillis();
        System.out.println("\n" + what);
        System.out.println("Triples: " + graph.getNumberOfTriples() + " Took: " + (finishTime - startTime) +
                " ms = " + ((finishTime - startTime) / NO_MILLISECONDS_IN_A_SECOND) + " s");
    }

    private void find1(Graph graph, URI subjectURI) throws GraphException, GraphElementFactoryException {
        ClosableIterator<Triple> itr = findRandomPredicates(graph,
                graphElementFactory.createURIReference(subjectURI));
        while (itr.hasNext()) {
            Triple triple1 = itr.next();
            ObjectNode object1 = triple1.getObject();
            if (!(object1 instanceof SubjectNode)) {
                continue;
            }
            find2(graph, object1);
        }
    }

    private void find2(Graph graph, ObjectNode object1) throws GraphException, GraphElementFactoryException {
        ClosableIterator<Triple> itr2 = findRandomPredicates(graph, (SubjectNode) object1);
        while (itr2.hasNext()) {
            Triple triple2 = itr2.next();
            ObjectNode object2 = triple2.getObject();
            if (!(object2 instanceof SubjectNode)) {
                continue;
            }
            find3(graph, object2);
        }
    }

    private void find3(Graph graph, ObjectNode object2) throws GraphException, GraphElementFactoryException {
        ClosableIterator<Triple> itr3 = findRandomPredicates(graph, (SubjectNode) object2);
        while (itr3.hasNext()) {
            Triple triple3 = itr3.next();
            ObjectNode object3 = triple3.getObject();
            if (!(object3 instanceof SubjectNode)) {
                continue;
            }
        }
    }

    private ClosableIterator<Triple> findRandomPredicates(Graph graph, SubjectNode subject)
        throws GraphException, GraphElementFactoryException {
        noFinds++;
        return graph.find(subject,
                graphElementFactory.createURIReference(predicates.get((int) Math.random() * NO_PREDICATES)),
                graphElementFactory.createURIReference(URI.create(OBJECT_PREFIX + (int) Math.random() *
                        NUMBER_OF_PREDICATES)));
    }
}
