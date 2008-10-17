/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.example;

import org.jrdf.JRDFFactory;
import org.jrdf.SortedMemoryJRDFFactory;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Resource;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.answer.Answer;
import org.jrdf.query.answer.xml.AnswerXMLPagenatedStreamWriter;
import org.jrdf.urql.UrqlConnection;
import org.jrdf.util.ClosableIterable;
import org.jrdf.vocabulary.XSD;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import static java.net.URI.create;

/**
 * An example that performs simple operations on a JRDF Graph.
 *
 * @author Robert Turner
 */
public class JrdfExample {

    private static final JRDFFactory JRDF_FACTORY = SortedMemoryJRDFFactory.getFactory();

    //Resources
    private Resource person;
    private Resource address;

    //Properties
    private static final URI HAS_ADDRESS = create("http://example.org/terms#address");
    private static final URI HAS_STREET = create("http://example.org/terms#street");
    private static final URI HAS_CITY = create("http://example.org/terms#city");
    private static final URI HAS_STATE = create("http://example.org/terms#state");
    private static final URI HAS_POSTCODE = create("http://example.org/terms#postalCode");
    private static final URI PERSON = create("http://example.org/staffid#85740");
    private Triple addressStatement;
    private Triple cityStatement;

    /**
     * Default Constructor.
     */
    public JrdfExample() {

    }

    /**
     * Obtains a JRDF Graph and performs example operations on it.
     *
     * @throws Exception
     */
    private void runExample() throws Exception {
        System.out.println("Running example.");
        System.out.println("Creating Graph...");
        Graph graph = JRDF_FACTORY.getGraph();
        System.out.println("Creating Graph Elements...");
        populateGraph(graph);
        searchGraph(graph);
        performReification(graph);
        removeStatement(graph);
        performQuery(graph);
        System.out.println("Example finished.");
    }

    private void populateGraph(Graph graph) throws GraphException {
        GraphElementFactory elementFactory = graph.getElementFactory();
        person = elementFactory.createResource(PERSON);
        address = elementFactory.createResource();
        addressStatement = person.asTriple(HAS_ADDRESS, address);
        graph.add(addressStatement);
        address.addValue(HAS_STREET, "1501 Grant Avenue");
        cityStatement = address.asTriple(HAS_CITY, "Bedford");
        graph.add(cityStatement);
        address.addValue(HAS_STATE, "Massachusetts");
        address.addValue(HAS_POSTCODE, "01730", XSD.INTEGER);
        print("Graph contains: ", graph);
    }

    /**
     * Performs find() operations on the Graph.
     *
     * @param graph Graph
     * @throws Exception
     */
    private void searchGraph(Graph graph) throws Exception {
        System.out.println("Searching Graph...");

        //get the Factory
        TripleFactory tripleFactory = graph.getTripleFactory();
        getAllTriples(tripleFactory, graph);
        doSearch(tripleFactory, graph);
    }

    private void doSearch(TripleFactory tripleFactory, Graph graph) throws GraphException {
        //search for address (as a subject)
        print("Search for address as a subject: ", graph.find(address, ANY_PREDICATE_NODE, ANY_OBJECT_NODE));

        //search for the city: "Bedford"
        ObjectNode city = graph.getElementFactory().createLiteral("Bedford");
        print("Search for city ('Bedford'): ", graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, city));

        //search for any subject that has an address
        PredicateNode hasAddress = graph.getElementFactory().createURIReference(HAS_ADDRESS);
        Triple findAddresses = tripleFactory.createTriple(ANY_SUBJECT_NODE, hasAddress, ANY_OBJECT_NODE);
        print("Search for subjects that have an address: ", graph.find(findAddresses));
    }

    private void getAllTriples(TripleFactory tripleFactory, Graph graph) throws GraphException {
        //get all Triples
        Triple findAll = tripleFactory.createTriple(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        ClosableIterable<Triple> allTriples = graph.find(findAll);
        try {
            print("Search for all triples: ", allTriples);
        } finally {
            allTriples.iterator().close();
        }
    }

    /**
     * Reifies a Statement.
     *
     * @param graph Graph
     * @throws Exception
     */
    private void performReification(Graph graph) throws Exception {
        System.out.println("Reifying a statement...");

        //get the Factories
        GraphElementFactory elementFactory = graph.getElementFactory();
        TripleFactory tripleFactory = graph.getTripleFactory();

        //create a resource to identify the statement
        URIReference statement = elementFactory.createURIReference(new URI("http://example.org/statement#address"));

        //reify the address statement (person, hasAddress, address)
        tripleFactory.reifyTriple(addressStatement, statement);

        //insert a statement about the original statement
        URIReference manager = elementFactory.createURIReference(new URI("http://example.org/managerid#65"));
        URIReference hasConfirmed = elementFactory.createURIReference(new URI("http://example.org/terms#hasConfirmed"));
        Triple confirmationStatement = tripleFactory.createTriple(manager, hasConfirmed, statement);
        graph.add(confirmationStatement);

        //print the contents
        print("Graph contains (after reification): ", graph);
    }

    /**
     * Deletes a statement from the Graph.
     *
     * @param graph Graph
     * @throws Exception
     */
    private void removeStatement(Graph graph) throws Exception {
        System.out.println("Removing a statement...");

        //delete the city (address, hasCity, city)
        graph.remove(cityStatement);

        //print the contents
        print("Graph contains (after remove): ", graph);
    }

    private void performQuery(Graph graph) throws InvalidQuerySyntaxException, GraphException,
        XMLStreamException, IOException {
        UrqlConnection connection = JRDF_FACTORY.getNewUrqlConnection();
        final Answer answer = connection.executeQuery(graph, "SELECT ?s ?p ?o WHERE { ?s ?p ?o }");
        System.out.println("Query Result:\n" + answer);
        StringWriter writer = new StringWriter();
        AnswerXMLPagenatedStreamWriter answerXMLWriter = new AnswerXMLPagenatedStreamWriter(answer, writer);
        answerXMLWriter.write(writer);
        String xmlString = writer.toString();
        System.out.printf("Query result xml:\n" + xmlString);
    }

    /**
     * Prints the entire contents of a Graph to System.out
     *
     * @param message String
     * @param graph   Graph
     * @throws IllegalArgumentException
     * @throws GraphException
     */
    private void print(String message, Graph graph) throws IllegalArgumentException, GraphException {
        //validate
        if (null == graph) {
            throw new IllegalArgumentException("Graph is null.");
        }

        //find all statements
        Triple findAll = graph.getTripleFactory().createTriple(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        ClosableIterable<Triple> allTriples = graph.find(findAll);

        //print them
        print(message, allTriples);

        //release any resources
        allTriples.iterator().close();
    }

    /**
     * Prints the contents of an Iterator to System.out
     *
     * @param message  String
     * @param triples Iterator
     * @throws IllegalArgumentException
     */
    private void print(String message, ClosableIterable<Triple> triples) throws IllegalArgumentException {
        if (null == triples) {
            throw new IllegalArgumentException("Iterator is null.");
        }
        try {
            //print message first
            System.out.println(message);

            //print the contents
            for (Triple triple : triples) {
                System.out.println(String.valueOf(triple));
            }

            //print an empty line as a spacer
            System.out.println("");
        } finally {
            triples.iterator().close();
        }
    }

    /**
     * Instantiates a JRDFExample and runs it.
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        try {
            JrdfExample example = new JrdfExample();
            example.runExample();
        } catch (Exception exception) {
            //print an exception if one occurs
            exception.printStackTrace();
        }
    }

}
