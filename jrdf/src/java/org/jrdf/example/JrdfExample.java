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
import org.jrdf.graph.BlankNode;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Triple;
import org.jrdf.graph.TripleFactory;
import org.jrdf.graph.TripleFactoryException;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.local.iterator.ClosableIterator;

import java.net.URI;
import java.util.Iterator;

/**
 * An example that performs simple operations on a JRDF Graph.
 *
 * @author Robert Turner
 */
public class JrdfExample {

    private static final JRDFFactory JRDF_FACTORY = SortedMemoryJRDFFactory.getFactory();

    //Resources
    private URIReference person;

    private BlankNode address;
    //Properties
    private URIReference hasAddress;
    private URIReference hasStreet;
    private URIReference hasCity;
    private URIReference hasState;

    private URIReference hasPostCode;
    //Values
    private Literal street;
    private Literal city;
    private Literal state;

    private Literal postCode;
    //Statements
    private Triple addressStatement;
    private Triple streetStatement;
    private Triple cityStatement;
    private Triple stateStatement;
    private Triple postCodeStatement;

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

        //get a JRDF Graph
        System.out.println("Creating Graph...");
        Graph graph = JRDF_FACTORY.getNewGraph();

        //create example data
        initializeData(graph);

        //insert example statements
        populateGraph(graph);

        //perform find() operations
        searchGraph(graph);

        //reify a Statement
        performReification(graph);

        //remove a statement
        removeStatement(graph);

        System.out.println("Example finished.");
    }

    /**
     * Creates Nodes and Triples used by the Example.
     *
     * @param graph Graph
     * @throws Exception
     */
    private void initializeData(Graph graph) throws Exception {

        //initialize Nodes and Triples
        System.out.println("Creating Graph Elements...");

        //get the Factory
        GraphElementFactory elementFactory = graph.getElementFactory();
        TripleFactory tripleFactory = graph.getTripleFactory();

        assignVlues(elementFactory);

        //create statements
        createStatments(tripleFactory);
    }

    private void assignVlues(GraphElementFactory elementFactory) throws Exception {
        //create resources
        person = elementFactory.createURIReference(new URI("http://example.org/staffid#85740"));
        address = elementFactory.createBlankNode();

        //create properties
        hasAddress = elementFactory.createURIReference(new URI("http://example.org/terms#address"));
        hasStreet = elementFactory.createURIReference(new URI("http://example.org/terms#street"));
        hasCity = elementFactory.createURIReference(new URI("http://example.org/terms#city"));
        hasState = elementFactory.createURIReference(new URI("http://example.org/terms#state"));
        hasPostCode = elementFactory.createURIReference(new URI("http://example.org/terms#postalCode"));

        //create values
        street = elementFactory.createLiteral("1501 Grant Avenue");
        city = elementFactory.createLiteral("Bedford");
        state = elementFactory.createLiteral("Massachusetts");
        postCode = elementFactory.createLiteral("01730");
    }

    private void createStatments(TripleFactory tripleFactory) {
        addressStatement = tripleFactory.createTriple(person, hasAddress, address);
        streetStatement = tripleFactory.createTriple(address, hasStreet, street);
        cityStatement = tripleFactory.createTriple(address, hasCity, city);
        stateStatement = tripleFactory.createTriple(address, hasState, state);
        postCodeStatement = tripleFactory.createTriple(address, hasPostCode, postCode);
    }

    /**
     * Inserts example statements into the Graph.
     *
     * @param graph Graph
     * @throws Exception
     */
    private void populateGraph(Graph graph) throws Exception {

        System.out.println("Populating Graph...");

        //insert the statements
        graph.add(addressStatement);
        graph.add(streetStatement);
        graph.add(cityStatement);
        graph.add(stateStatement);
        graph.add(postCodeStatement);

        //print contents
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
        Triple findAddress = tripleFactory.createTriple(address, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        ClosableIterator addressSubject = graph.find(findAddress);
        print("Search for address as a subject: ", addressSubject);
        addressSubject.close();

        //search for the city: "Bedford"
        Triple findCity = tripleFactory.createTriple(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, city);
        ClosableIterator bedfordCity = graph.find(findCity);
        print("Search for city ('Bedford'): ", bedfordCity);
        bedfordCity.close();

        //search for any subject that has an address
        Triple findAddresses = tripleFactory.createTriple(ANY_SUBJECT_NODE, hasAddress, ANY_OBJECT_NODE);
        ClosableIterator addresses = graph.find(findAddresses);
        print("Search for subjects that have an address: ", addresses);
        addresses.close();
    }

    private void getAllTriples(TripleFactory tripleFactory, Graph graph) throws GraphException {
        //get all Triples
        Triple findAll = tripleFactory.createTriple(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        ClosableIterator allTriples = graph.find(findAll);
        print("Search for all triples: ", allTriples);
        allTriples.close();
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

    /**
     * Prints the entire contents of a Graph to System.out
     *
     * @param message String
     * @param graph   Graph
     * @throws IllegalArgumentException
     * @throws GraphException
     */
    private void print(String message, Graph graph) throws IllegalArgumentException, GraphException,
        TripleFactoryException {

        //validate
        if (null == graph) {

            throw new IllegalArgumentException("Graph is null.");
        }

        //find all statements
        Triple findAll = graph.getTripleFactory().createTriple(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        ClosableIterator allTriples = graph.find(findAll);

        //print them
        print(message, allTriples);

        //release any resources
        allTriples.close();
    }

    /**
     * Prints the contents of an Iterator to System.out
     *
     * @param message  String
     * @param iterator Iterator
     * @throws IllegalArgumentException
     */
    private void print(String message, Iterator iterator) throws
        IllegalArgumentException {

        //validate
        if (null == iterator) {

            throw new IllegalArgumentException("Iterator is null.");
        }

        //print message first
        System.out.println(message);

        //print the contents
        while (iterator.hasNext()) {
            System.out.println(String.valueOf(iterator.next()));
        }

        //print an empty line as a spacer
        System.out.println("");
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
