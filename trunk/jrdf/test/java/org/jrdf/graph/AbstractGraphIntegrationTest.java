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

package org.jrdf.graph;

import junit.framework.TestCase;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.local.index.nodepool.ExternalBlankNodeException;
import org.jrdf.query.relation.type.BlankNodeType;
import static org.jrdf.query.relation.type.BlankNodeType.BNODE_TYPE;
import static org.jrdf.query.relation.type.PredicateNodeType.PREDICATE_TYPE;
import static org.jrdf.query.relation.type.ResourceNodeType.RESOURCE_TYPE;
import static org.jrdf.query.relation.type.URIReferenceNodeType.URI_REFERENCE_TYPE;
import org.jrdf.util.ClosableIterable;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.AssertThrows.Block;
import static org.jrdf.util.test.AssertThrows.assertThrows;

import java.net.URI;
import static java.net.URI.create;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Abstract test case for graph implementations.
 *
 * @author <a href="mailto:pgearon@users.sourceforge.net">Paul Gearon</a>
 * @author Andrew Newman
 * @version $Revision$
 */
public abstract class AbstractGraphIntegrationTest extends TestCase {

    /**
     * Instance of a graph object.
     */
    protected Graph graph;

    /**
     * Instance of the triple factory for the graph.
     */
    private TripleFactory tripleFactory;

    /**
     * Blank node 1.
     */
    protected BlankNode blank1;

    /**
     * Blank node 2.
     */
    protected BlankNode blank2;

    protected URIReference ref1;
    protected URIReference ref2;
    protected URIReference ref3;
    protected URIReference ref4;
    protected URIReference ref5;

    /**
     * Used to create literal.
     */
    private static final String TEST_STR1 = "A test string";

    /**
     * Used to create literal.
     */
    private static final String TEST_STR2 = "Another test string";

    /**
     * Literal 1.
     */
    protected Literal l1;

    /**
     * Literal 2.
     */
    protected Literal l2;

    private Triple t1;
    private Triple t2;
    private Triple t3;

    private static final String CANT_ADD_ANY_NODE_MESSAGE = "Cannot insert any node values into the graph";
    private static final String CANT_ADD_NULL_MESSAGE = "Cannot insert null values into the graph";
    private static final String CANT_REMOVE_ANY_NODE_MESSAGE = "Cannot remove any node values into the graph";
    private static final String CANT_REMOVE_NULL_MESSAGE = "Cannot remove null values into the graph";
    private static final String CONTAIN_CANT_USE_NULLS = "Cannot use null values for contains";
    private static final String FIND_CANT_USE_NULLS = "Cannot use null values for finds";
    private static final String FAILED_TO_ADD_TRIPLE = "Failed to add triple.";
    private static final int NUMBER_OF_TRIPLES_TO_ADD = 10;

    /**
     * Create test instance.
     *
     * @throws Exception A generic exception - this should cause the tests to
     *                   fail.
     */
    public void setUp() throws Exception {
        setGlobalValues();
    }

    private void setGlobalValues() throws Exception {
        graph = newGraph();
        GraphElementFactory elementFactory = graph.getElementFactory();
        tripleFactory = graph.getTripleFactory();

        blank1 = elementFactory.createBlankNode();
        blank2 = elementFactory.createBlankNode();

        URI uri1 = new URI("http://namespace#somevalue");
        URI uri2 = new URI("http://namespace#someothervalue");
        URI uri3 = new URI("http://namespace#yetanothervalue");
        URI uri4 = new URI("http://namespace#yetanotheranothervalue");
        URI uri5 = new URI("http://namespace#visforvalue");
        ref1 = elementFactory.createURIReference(uri1);
        ref2 = elementFactory.createURIReference(uri2);
        ref3 = elementFactory.createURIReference(uri3);
        ref4 = elementFactory.createURIReference(uri4);
        ref5 = elementFactory.createURIReference(uri5);

        l1 = elementFactory.createLiteral(TEST_STR1);
        l2 = elementFactory.createLiteral(TEST_STR2);
    }

    //
    // implementation interfaces
    //

    /**
     * Create a graph implementation.
     *
     * @return A new Graph.
     * @throws Exception A generic exception - this should cause the tests to
     *                   fail.
     */
    protected abstract Graph newGraph() throws Exception;

    //
    // Test cases
    //

    /**
     * Tests that a new graph is empty.
     *
     * @throws Exception if query fails when it should have succeeded
     */
    public void testEmpty() throws Exception {
        assertTrue("Graph is not empty but is: " + graph.getNumberOfTriples(), graph.isEmpty());
        assertEquals(0, graph.getNumberOfTriples());
    }

    /**
     * Tests that it is possible to get a NodeFactory from a graph.
     */
    public void testFactory() {
        GraphElementFactory f = graph.getElementFactory();
        assertTrue(null != f);
    }

    /**
     * Tests addition.
     *
     * @throws Exception A generic exception - this should cause the tests to
     *                   fail.
     */
    public void testAddition() throws Exception {

        // add in a triple by nodes
        graph.add(blank1, ref1, blank2);

        assertFalse(graph.isEmpty());
        assertEquals(1, graph.getNumberOfTriples());

        // add in a whole triple
        Triple triple2 = tripleFactory.createTriple(blank2, ref1, blank2);
        graph.add(triple2);

        assertFalse(graph.isEmpty());
        assertEquals(2, graph.getNumberOfTriples());

        // add in the first triple again
        graph.add(blank1, ref1, blank2);

        assertFalse(graph.isEmpty());
        assertEquals(2, graph.getNumberOfTriples());

        // add in the second whole triple again
        Triple triple2b = tripleFactory.createTriple(blank2, ref1, blank2);
        graph.add(triple2b);
        assertFalse(graph.isEmpty());
        assertEquals(2, graph.getNumberOfTriples());

        // and again
        graph.add(triple2);
        assertFalse(graph.isEmpty());
        assertEquals(2, graph.getNumberOfTriples());

        // Add using iterator
        List<Triple> list = new ArrayList<Triple>();
        list.add(tripleFactory.createTriple(ref1, ref1, ref1));
        list.add(tripleFactory.createTriple(ref2, ref2, ref2));

        graph.add(list.iterator());
        assertFalse(graph.isEmpty());
        assertEquals(4, graph.getNumberOfTriples());

        // Try to add nulls
        assertThrows(IllegalArgumentException.class, CANT_ADD_NULL_MESSAGE, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.add(null, ref1, ref1);
            }
        });
        assertThrows(IllegalArgumentException.class, CANT_ADD_NULL_MESSAGE, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.add(ref1, null, ref1);
            }
        });
        assertThrows(IllegalArgumentException.class, CANT_ADD_NULL_MESSAGE, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.add(ref1, ref1, null);
            }
        });

        // Try to add any nodes
        assertThrows(IllegalArgumentException.class, CANT_ADD_ANY_NODE_MESSAGE, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.add(ANY_SUBJECT_NODE, ref1, ref1);
            }
        });
        assertThrows(IllegalArgumentException.class, CANT_ADD_ANY_NODE_MESSAGE, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.add(ref1, ANY_PREDICATE_NODE, ref1);
            }
        });
        assertThrows(IllegalArgumentException.class, CANT_ADD_ANY_NODE_MESSAGE, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.add(ref1, ref1, ANY_OBJECT_NODE);
            }
        });
    }

    public void testMultipleAddtion() throws Exception {
        for (int i = 0; i < NUMBER_OF_TRIPLES_TO_ADD; i++) {
            tripleFactory.addTriple(create("http://subject/" + i), create("http://predicate/" + i),
                    create("http://object/" + i));
        }
        assertEquals(NUMBER_OF_TRIPLES_TO_ADD, graph.getNumberOfTriples());
    }

    /**
     * Tests removal.
     *
     * @throws Exception A generic exception - this should cause the tests to fail.
     */
    public void testRemoval() throws Exception {
        // add some test data
        addTriplesToGraph();
        addFullTriplesToGraph();

        // check that all is well
        assertFalse(graph.isEmpty());
        assertEquals(6, graph.getNumberOfTriples());

        // delete the first statement
        graph.remove(blank1, ref1, blank2);
        assertEquals(5, graph.getNumberOfTriples());

        // delete the last statement
        graph.remove(t3);
        assertEquals(4, graph.getNumberOfTriples());

        // delete the next last statement with a new "triple object"
        t2 = tripleFactory.createTriple(blank2, ref2, blank1);
        graph.remove(t2);
        assertEquals(3, graph.getNumberOfTriples());

        // delete the next last statement with a triple different to what it was built with
        graph.remove(blank2, ref1, blank1);
        assertEquals(2, graph.getNumberOfTriples());

        // delete the next last statement with a triple different to what it was built with
        graph.remove(ref1, ref2, l2);
        assertEquals(1, graph.getNumberOfTriples());

        // delete the wrong triple
        assertThrows(GraphException.class, new Block() {
            public void execute() throws Throwable {
                graph.remove(blank2, ref1, blank1);
            }
        });
        assertEquals(1, graph.getNumberOfTriples());

        // delete a triple that never existed
        assertThrows(GraphException.class, new Block() {
            public void execute() throws Throwable {
                graph.remove(blank2, ref2, l2);
            }
        });
        assertEquals(1, graph.getNumberOfTriples());

        // and delete with a triple object
        t1 = tripleFactory.createTriple(blank2, ref1, blank1);
        assertThrows(GraphException.class, new Block() {
            public void execute() throws Throwable {
                graph.remove(t1);
            }
        });
        assertEquals(1, graph.getNumberOfTriples());

        // now clear out the graph
        assertFalse(graph.isEmpty());
        graph.remove(blank1, ref2, blank2);
        assertTrue(graph.isEmpty());
        assertEquals(0, graph.getNumberOfTriples());

        // check that we can't still remove things
        try {
            graph.remove(blank1, ref2, blank2);
            assertTrue(false);
        }
        catch (GraphException e) { /* no-op */
        }
        assertTrue(graph.isEmpty());
        assertEquals(0, graph.getNumberOfTriples());

        // Check removal using iterator
        ref1 = graph.getElementFactory().createURIReference(ref1.getURI());
        ref2 = graph.getElementFactory().createURIReference(ref2.getURI());
        graph.add(tripleFactory.createTriple(ref1, ref1, ref1));
        graph.add(tripleFactory.createTriple(ref2, ref2, ref2));

        List<Triple> list = new ArrayList<Triple>();
        list.add(tripleFactory.createTriple(ref1, ref1, ref1));
        list.add(tripleFactory.createTriple(ref2, ref2, ref2));
        graph.remove(list.iterator());

        // check that we can't still remove things
        try {
            graph.remove(ref2, ref2, ref2);
            assertTrue(false);
        }
        catch (GraphException e) { /* no-op */
        }

        assertTrue(graph.isEmpty());
        assertEquals(0, graph.getNumberOfTriples());

        // Try to add nulls
        assertThrows(IllegalArgumentException.class, CANT_REMOVE_NULL_MESSAGE, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.remove(null, ref1, ref1);
            }
        });
        assertThrows(IllegalArgumentException.class, CANT_REMOVE_NULL_MESSAGE, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.remove(ref1, null, ref1);
            }
        });
        assertThrows(IllegalArgumentException.class, CANT_REMOVE_NULL_MESSAGE, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.remove(ref1, ref1, null);
            }
        });

        // Try to add any nodes
        assertThrows(IllegalArgumentException.class, CANT_REMOVE_ANY_NODE_MESSAGE,
            new AssertThrows.Block() {
                public void execute() throws Throwable {
                    graph.remove(ANY_SUBJECT_NODE, ref1, ref1);
                }
            });
        assertThrows(IllegalArgumentException.class, CANT_REMOVE_ANY_NODE_MESSAGE,
            new AssertThrows.Block() {
                public void execute() throws Throwable {
                    graph.remove(ref1, ANY_PREDICATE_NODE, ref1);
                }
            });
        assertThrows(IllegalArgumentException.class, CANT_REMOVE_ANY_NODE_MESSAGE,
            new AssertThrows.Block() {
                public void execute() throws Throwable {
                    graph.remove(ref1, ref1, ANY_OBJECT_NODE);
                }
            });
    }

    public void testRemoveIterator() throws Exception {
        // Test removing using the iterator from a find.
        setGlobalValues();
        checkRemoveIterator(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE, 0);
        setGlobalValues();
        checkRemoveIterator(ref1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE, 2);
        setGlobalValues();
        checkRemoveIterator(blank1, ANY_PREDICATE_NODE, blank2, 1);
        setGlobalValues();
        checkRemoveIterator(ref1, ref2, l2, 2);
    }

    private void checkRemoveIterator(SubjectNode subjectNode, PredicateNode predicateNode, ObjectNode objectNode,
        int expectedNumberOfTriples) throws Exception {
        addTriplesToGraph();
        ClosableIterator<Triple> iterator = graph.find(subjectNode, predicateNode, objectNode).iterator();
        try {
            graph.remove(iterator);
            assertEquals(expectedNumberOfTriples, graph.getNumberOfTriples());
        } finally {
            iterator.close();
        }
    }

    /**
     * Tests containership.
     *
     * @throws Exception A generic exception - this should cause the tests to
     *                   fail.
     */
    public void testContains() throws Exception {
        // add some test data
        addTriplesToGraph();
        addFullTriplesToGraph();

        // test containership
        assertTrue(graph.contains(blank1, ref1, blank2));
        // test with existing and built triples
        assertTrue(graph.contains(t1));
        t1 = tripleFactory.createTriple(blank2, ref2, blank1);
        assertTrue(graph.contains(t1));

        // test non containership
        assertFalse(graph.contains(blank1, ref1, blank1));
        t1 = tripleFactory.createTriple(blank2, ref2, ref1);
        assertFalse(graph.contains(t1));

        // test containership after removal
        graph.remove(blank1, ref1, blank2);
        assertFalse(graph.contains(blank1, ref1, blank2));
        t1 = tripleFactory.createTriple(blank1, ref1, blank2);
        assertFalse(graph.contains(t1));

        // put it back in and test again
        graph.add(blank1, ref1, blank2);
        assertTrue(graph.contains(blank1, ref1, blank2));
        assertTrue(graph.contains(t1));

        // AnySubjectNode in contains.
        Graph newGraph = newGraph();
        assertFalse(newGraph.contains(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE));

        // Add a statement
        GraphElementFactory newElementFactory = newGraph.getElementFactory();
        blank1 = newElementFactory.createBlankNode();
        blank2 = newElementFactory.createBlankNode();
        ref1 = newElementFactory.createURIReference(create("http://something/here"));
        t1 = tripleFactory.createTriple(blank1, ref1, blank2);
        newGraph.add(t1);

        // Check for existance
        assertTrue(newGraph.contains(ANY_SUBJECT_NODE, ref1, blank2));
        assertTrue(newGraph.contains(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, blank2));
        assertTrue(newGraph.contains(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE));
        assertTrue(newGraph.contains(blank1, ANY_PREDICATE_NODE, blank2));
        assertTrue(newGraph.contains(blank1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE));
        assertTrue(newGraph.contains(blank1, ref1, ANY_OBJECT_NODE));
        assertTrue(newGraph.contains(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE));

        // Check non-existance
        assertFalse(newGraph.contains(ANY_SUBJECT_NODE, ref2, blank1));
        assertFalse(newGraph.contains(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, blank1));
        assertFalse(newGraph.contains(blank2, ANY_PREDICATE_NODE, blank1));
        assertFalse(newGraph.contains(blank2, ANY_PREDICATE_NODE, ANY_OBJECT_NODE));
        assertFalse(newGraph.contains(blank2, ref2, ANY_OBJECT_NODE));

        // Try to test for containing nulls
        assertThrows(IllegalArgumentException.class, CONTAIN_CANT_USE_NULLS, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.contains(null, ref1, ref1);
            }
        });
        assertThrows(IllegalArgumentException.class, CONTAIN_CANT_USE_NULLS, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.contains(ref1, null, ref1);
            }
        });
        assertThrows(IllegalArgumentException.class, CONTAIN_CANT_USE_NULLS, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.contains(ref1, ref1, null);
            }
        });
    }

    /**
     * Tests finding.
     *
     * @throws Exception A generic exception - this should cause the tests to
     *                   fail.
     */
    public void testFinding() throws Exception {
        graph.add(blank1, ref1, blank2);
        graph.add(blank1, ref1, l1);
        graph.add(blank1, ref2, blank2);
        graph.add(blank1, ref1, l2);
        graph.add(blank2, ref1, blank2);
        graph.add(blank2, ref2, blank2);
        graph.add(blank2, ref1, l1);
        graph.add(blank2, ref1, l2);

        // look for the first triple and check that one is returned
        ClosableIterator<Triple> it = graph.find(blank1, ref1, blank2).iterator();
        assertTrue(it.hasNext());
        it.close();

        // look for a non-existent triple
        it = graph.find(ref1, ref1, blank1).iterator();
        assertFalse(it.hasNext());
        it.close();

        // look for doubles and check that there is data there
        it = graph.find(blank1, ref1, ANY_OBJECT_NODE).iterator();
        assertTrue(it.hasNext());
        it.close();
        it = graph.find(blank1, ANY_PREDICATE_NODE, blank2).iterator();
        assertTrue(it.hasNext());
        it.close();
        it = graph.find(ANY_SUBJECT_NODE, ref1, blank2).iterator();
        assertTrue(it.hasNext());
        it.close();

        // look for a non-existent double
        it = graph.find(ref1, ref1, ANY_OBJECT_NODE).iterator();
        assertFalse(it.hasNext());
        it.close();
        it = graph.find(ref1, ANY_PREDICATE_NODE, blank2).iterator();
        assertFalse(it.hasNext());
        it.close();
        it = graph.find(ANY_SUBJECT_NODE, ref3, blank2).iterator();
        assertFalse(it.hasNext());
        it.close();

        // look for singles
        it = graph.find(blank1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).iterator();
        assertTrue(it.hasNext());
        it.close();
        it = graph.find(ANY_SUBJECT_NODE, ref1, ANY_OBJECT_NODE).iterator();
        assertTrue(it.hasNext());
        it.close();
        it = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, l1).iterator();
        assertTrue(it.hasNext());
        it.close();

        // look for non-existent singles
        it = graph.find(ref1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).iterator();
        assertFalse(it.hasNext());
        it.close();
        it = graph.find(ANY_SUBJECT_NODE, ref3, ANY_OBJECT_NODE).iterator();
        assertFalse(it.hasNext());
        it.close();
        it = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ref1).iterator();
        assertFalse(it.hasNext());
        it.close();

        // do it all again with triples

        // look for the first triple and check that one is returned
        Triple t = tripleFactory.createTriple(blank1, ref1, blank2);
        it = graph.find(t).iterator();
        assertTrue(it.hasNext());
        it.close();

        // look for a non-existent triple
        t = tripleFactory.createTriple(ref1, ref1, blank1);
        it = graph.find(t).iterator();
        assertFalse(it.hasNext());
        it.close();

        // look for doubles and check that there is data there
        t = tripleFactory.createTriple(blank1, ref1, ANY_OBJECT_NODE);
        it = graph.find(t).iterator();
        assertTrue(it.hasNext());
        it.close();
        t = tripleFactory.createTriple(blank1, ANY_PREDICATE_NODE, blank2);
        it = graph.find(t).iterator();
        assertTrue(it.hasNext());
        it.close();
        t = tripleFactory.createTriple(ANY_SUBJECT_NODE, ref1, blank2);
        it = graph.find(t).iterator();
        assertTrue(it.hasNext());
        it.close();

        // look for a non-existent double
        t = tripleFactory.createTriple(ref1, ref1, ANY_OBJECT_NODE);
        it = graph.find(t).iterator();
        assertFalse(it.hasNext());
        it.close();
        t = tripleFactory.createTriple(ref1, ANY_PREDICATE_NODE, blank2);
        it = graph.find(t).iterator();
        assertFalse(it.hasNext());
        it.close();
        t = tripleFactory.createTriple(ANY_SUBJECT_NODE, ref3, blank2);
        it = graph.find(t).iterator();
        assertFalse(it.hasNext());
        it.close();

        // look for singles
        t = tripleFactory.createTriple(blank1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        it = graph.find(t).iterator();
        assertTrue(it.hasNext());
        it.close();
        t = tripleFactory.createTriple(ANY_SUBJECT_NODE, ref1, ANY_OBJECT_NODE);
        it = graph.find(t).iterator();
        assertTrue(it.hasNext());
        it.close();
        t = tripleFactory.createTriple(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, l1);
        it = graph.find(t).iterator();
        assertTrue(it.hasNext());
        it.close();

        // look for non-existent singles
        t = tripleFactory.createTriple(ref1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        it = graph.find(t).iterator();
        assertFalse(it.hasNext());
        it.close();
        t = tripleFactory.createTriple(ANY_SUBJECT_NODE, ref3, ANY_OBJECT_NODE);
        it = graph.find(t).iterator();
        assertFalse(it.hasNext());
        it.close();
        t = tripleFactory.createTriple(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ref1);
        it = graph.find(t).iterator();
        assertFalse(it.hasNext());
        it.close();

        // Try to test for finding nulls
        assertThrows(IllegalArgumentException.class, FIND_CANT_USE_NULLS, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.find(null, ref1, ref1);
            }
        });
        assertThrows(IllegalArgumentException.class, FIND_CANT_USE_NULLS, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.find(ref1, null, ref1);
            }
        });
        assertThrows(IllegalArgumentException.class, FIND_CANT_USE_NULLS, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.find(ref1, ref1, null);
            }
        });
    }

    /**
     * Tests iteration over a found set.
     *
     * @throws Exception A generic exception - this should cause the tests to fail.
     */
    public void testIteration() throws Exception {
        GraphElementFactory factory = graph.getElementFactory();

        //create nodes
        BlankNode bNode1 = factory.createBlankNode();
        BlankNode bNode2 = factory.createBlankNode();
        URIReference testUri1 = factory.createURIReference(new URI("http://tucana.org/tucana#testUri1"));
        URIReference testUri2 = factory.createURIReference(new URI("http://tucana.org/tucana#testUri2"));
        Literal literal1 = factory.createLiteral("literal1");
        Literal literal2 = factory.createLiteral("literal2");

        //create some statements
        Triple[] triples = new Triple[16];
        triples[0] = tripleFactory.createTriple(bNode1, testUri1, literal1);
        triples[1] = tripleFactory.createTriple(bNode1, testUri1, literal2);
        triples[2] = tripleFactory.createTriple(bNode1, testUri2, literal1);
        triples[3] = tripleFactory.createTriple(bNode1, testUri2, literal2);
        triples[4] = tripleFactory.createTriple(bNode2, testUri1, literal1);
        triples[5] = tripleFactory.createTriple(bNode2, testUri1, literal2);
        triples[6] = tripleFactory.createTriple(bNode2, testUri2, literal1);
        triples[7] = tripleFactory.createTriple(bNode2, testUri2, literal2);
        triples[8] = tripleFactory.createTriple(bNode1, testUri1, bNode2);
        triples[9] = tripleFactory.createTriple(bNode1, testUri2, bNode2);
        triples[10] = tripleFactory.createTriple(bNode1, testUri1, testUri2);
        triples[11] = tripleFactory.createTriple(bNode1, testUri2, testUri1);
        triples[12] = tripleFactory.createTriple(testUri1, testUri2, bNode1);
        triples[13] = tripleFactory.createTriple(testUri2, testUri1, bNode1);
        triples[14] = tripleFactory.createTriple(testUri1, testUri2, bNode2);
        triples[15] = tripleFactory.createTriple(testUri2, testUri1, bNode2);

        //add them
        for (Triple triple : triples) {
            graph.add(triple);
        }

        //query them and put contents of iterator in a collection for checking
        //(iterator may return results in a different order)
        Set<Triple> statements = new HashSet<Triple>();
        ClosableIterable<Triple> iterable = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        assertTrue("ClosableIterator is returning false for hasNext().", iterable.iterator().hasNext());
        for (Triple triple : iterable) {
            statements.add(triple);
        }
        iterable.iterator().close();

        //check that the iterator contained the correct number of statements
        assertEquals("ClosableIterator is incomplete.", graph.getNumberOfTriples(), statements.size());

        //check the the collection contains all the original triples
        for (Triple triple1 : triples) {
            if (!statements.contains(triple1)) {
                fail("Iterator did not contain triple: " + triple1 + ".");
            }
        }
    }

    /**
     * Tests iterative removal.
     *
     * @throws Exception A generic exception - this should cause the tests to fail.
     */
    public void testIterativeRemoval() throws Exception {
        // TODO AN Add a test for fulliterative add.
        // add some test data
        checkFullIteratorRemoval(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE, 6);
        setGlobalValues();
        checkFullIteratorRemoval(blank1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE, 2);
        setGlobalValues();
        checkFullIteratorRemoval(blank2, ref1, ANY_OBJECT_NODE, 2);
        setGlobalValues();
        checkFullIteratorRemoval(ANY_SUBJECT_NODE, ref2, ANY_OBJECT_NODE, 3);
        setGlobalValues();
        checkFullIteratorRemoval(blank1, ANY_PREDICATE_NODE, blank2, 2);
        setGlobalValues();
        checkFullIteratorRemoval(ref1, ref2, l2, 1);
    }

    /**
     * Test blank node semantics across graphs - throws exception when adding blank node across graphs. Throws
     * an exception when the node ids exist in the graph but return different blank nodes.
     */
    public void testBlankNodesAcrossGraphs() throws Exception {
        final Graph newGraph = newGraph();
        GraphElementFactory graphElementFactory = newGraph.getElementFactory();
        URI newURI = new URI("http://namespace#somevalue");
        final URIReference newRes = graphElementFactory.createURIReference(newURI);
        newGraph.add(newRes, newRes, newRes);
        assertThrows(ExternalBlankNodeException.class, FAILED_TO_ADD_TRIPLE, new Block() {
            public void execute() throws Throwable {
                newGraph.add(blank1, newRes, newRes);
            }
        });
        assertThrows(ExternalBlankNodeException.class, FAILED_TO_ADD_TRIPLE, new Block() {
            public void execute() throws Throwable {
                newGraph.add(blank2, newRes, newRes);
            }
        });
    }

    public void testResourceIteratorSimple() throws Exception {
        final ClosableIterator<? extends Node> resources = graph.findNodes(RESOURCE_TYPE).iterator();
        try {
            boolean b = resources.hasNext();
            assertFalse("Should be no resources for empty graph", b);
            assertThrows(NoSuchElementException.class, new AssertThrows.Block() {
                public void execute() throws Throwable {
                    resources.next();
                }
            });
        } finally {
            resources.close();
        }
    }

    public void testBlankNodeTypeIterator() throws Exception {
        addTestNodes();
        assertEquals("Incorrect number of blank nodes", 2, getNumberOfBlankNodes());
        graph.remove(blank1, ref1, blank2);
        graph.remove(blank1, ref2, blank2);
        graph.remove(blank1, ref1, l1);
        graph.remove(blank1, ref1, l2);
        assertEquals("Incorrect number of blank nodes", 1, getNumberOfBlankNodes());
    }

    public void testURIReferenceResourceIterator() throws Exception {
        addTestNodes();
        ClosableIterator<Resource> iterator = graph.findResources(URI_REFERENCE_TYPE).iterator();
        try {
            int counter = 0;
            while (iterator.hasNext()) {
                Resource resource = iterator.next();
                assertTrue(resource.getUnderlyingNode() instanceof URIReference);
                counter++;
            }
            assertEquals("Unexpected number of unique URIs in the subject and object position", 3, counter);
        } finally {
            iterator.close();
        }
    }

    public void testBlankNodeResourceIterator() throws Exception {
        addTestNodes();
        ClosableIterator<Resource> iterator = graph.findResources(BNODE_TYPE).iterator();
        try {
            int counter = 0;
            while (iterator.hasNext()) {
                Resource resource = iterator.next();
                assertTrue(resource.getUnderlyingNode() instanceof BlankNode);
                counter++;
            }
            assertEquals("Unexpected number of unique blank nodes in the subject and object position", 2, counter);
        } finally {
            iterator.close();
        }
    }

    public void testURIReferencesIterator() throws Exception {
        addTestNodes();
        assertEquals("Unexpected number of unique URIs", 5, countURIRefs());
        graph.remove(ref5, ref2, ref1);
        assertEquals("Unexpected number of unique URIs", 5, countURIRefs());
        graph.remove(ref1, ref3, ref5);
        graph.remove(ref5, ref5, ref5);
        assertEquals("Unexpected number of unique URIs", 4, countURIRefs());
    }

    private int countURIRefs() {
        ClosableIterable<? extends Node> iterator = graph.findNodes(URI_REFERENCE_TYPE);
        try {
            int counter = 0;
            for (Node node : iterator) {
                counter++;
            }
            return counter;
        } finally {
            iterator.iterator().close();
        }
    }

    public void testResourceIterators() throws Exception {
        addTestNodes();
        ClosableIterable<? extends Node> resources = graph.findNodes(RESOURCE_TYPE);
        try {
            int counter = 0;
            for (Node node : resources) {
                counter++;
            }
            assertEquals("Unexpected number of unique resources (Blank Nodes and URIs)", 5, counter);
        } finally {
            resources.iterator().close();
        }
    }

    public void testPredicateIterators() throws Exception {
        addTestNodes();
        ClosableIterable<? extends Node> uniquePredicates = graph.findNodes(PREDICATE_TYPE);
        try {
            int counter = 0;
            for (Node node : uniquePredicates) {
                counter++;
            }
            assertEquals("Unexpected number of unique predicates", 4, counter);
        } finally {
            uniquePredicates.iterator().close();
        }
    }

    public void testFixedUniquePredicateIterator() throws Exception {
        addTestNodes();
        checkFixedUniquePredicateIterator(graph.getElementFactory().createResource(blank2), ref1, ref2);
        checkFixedUniquePredicateIterator(graph.getElementFactory().createResource(ref5), ref2, ref3, ref5);
    }

    public void testClear() throws Exception {
        addTriplesToGraph();
        assertEquals(3, graph.getNumberOfTriples());
        assertEquals(true, graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).iterator().hasNext());
        graph.clear();
        assertEquals(0, graph.getNumberOfTriples());
        assertEquals(false, graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).iterator().hasNext());
    }

    public void testSeparateGraphs() throws Exception {
        graph.add(blank2, ref1, l2);
        assertEquals(1, graph.getNumberOfTriples());
        newGraph();
        assertEquals(1, graph.getNumberOfTriples());
    }

    private void checkInvalidRemove(ClosableIterator<Triple> ci) {
        try {
            ci.remove();
            fail("Must throw an exception.");
        } catch (IllegalStateException ise) {
            assertTrue(ise.getMessage().indexOf("Next not called or beyond end of data") != -1);
        }
    }

    private void addTriplesToGraph() throws Exception {
        graph.add(blank1, ref1, blank2);
        graph.add(blank1, ref2, blank2);
        graph.add(ref1, ref2, l2);
    }

    private void addFullTriplesToGraph() throws GraphException {
        t1 = tripleFactory.createTriple(blank2, ref1, blank1);
        graph.add(t1);
        t2 = tripleFactory.createTriple(blank2, ref2, blank1);
        graph.add(t2);
        t3 = tripleFactory.createTriple(blank2, ref1, l1);
        graph.add(t3);
    }

    private void checkFixedUniquePredicateIterator(Resource resource, PredicateNode... predicates)
        throws Exception {
        int counter = 0;
        ClosableIterable<PredicateNode> resourcePredicates = graph.findPredicates(resource);
        try {
            Set<PredicateNode> expectedPredicates = new HashSet<PredicateNode>(Arrays.asList(predicates));
            for (PredicateNode predicateNode : resourcePredicates) {
                assertTrue("Results should not have: " + predicateNode + " expected: " + expectedPredicates,
                    expectedPredicates.contains(predicateNode));
                counter++;
            }
            assertEquals("Wrong number of unique predicates", expectedPredicates.size(), counter);
        } finally {
            resourcePredicates.iterator().close();
        }
    }

    private void addTestNodes() throws GraphException {
        graph.add(blank1, ref1, blank2);
        graph.add(blank1, ref2, blank2);
        graph.add(blank1, ref1, l1);
        graph.add(blank1, ref1, l2);
        graph.add(blank2, ref1, blank2);
        graph.add(blank2, ref2, blank2);
        graph.add(blank2, ref1, l1);
        graph.add(blank2, ref1, l2);
        graph.add(blank2, ref1, l2);
        graph.add(ref1, ref1, ref1);
        graph.add(ref1, ref3, ref1);
        graph.add(ref4, ref3, ref1);
        graph.add(ref5, ref2, ref1);
        graph.add(ref1, ref3, ref5);
        graph.add(ref5, ref5, ref5);
    }

    private int getNumberOfBlankNodes() {
        ClosableIterable<? extends Node> blankNodes = graph.findNodes(new BlankNodeType());
        try {
            int counter = 0;
            for (Node node : blankNodes) {
                counter++;
            }
            return counter;
        } finally {
            blankNodes.iterator().close();
        }
    }

    private void checkFullIteratorRemoval(SubjectNode subjectNode, PredicateNode predicateNode, ObjectNode objectNode,
        int expectedFoundTriples) throws Exception {

        addTriplesToGraph();
        addFullTriplesToGraph();
        int numberOfTriplesInGraph = 6;

        // check that all is well
        assertFalse(graph.isEmpty());
        assertEquals(numberOfTriplesInGraph, graph.getNumberOfTriples());

        // get an iterator for all the elements
        ClosableIterator<Triple> ci = graph.find(subjectNode, predicateNode, objectNode).iterator();

        // Check that it throws an exception before hasNext is called.
        checkInvalidRemove(ci);

        for (int i = 0; i < expectedFoundTriples; i++) {
            // remove the element
            assertTrue("Expected to delete: " + expectedFoundTriples + " but only deleted: " + i, ci.hasNext());
            ci.next();
            ci.remove();
            assertEquals(--numberOfTriplesInGraph, graph.getNumberOfTriples());
        }

        if (numberOfTriplesInGraph == 0) {
            assertTrue(graph.isEmpty());
        }

        assertFalse(ci.hasNext());
        ci.close();

        if (numberOfTriplesInGraph == 0) {
            // check that we can't still remove things
            try {
                graph.remove(ref2, ref2, ref2);
                assertTrue(false);
            }
            catch (GraphException e) { /* no-op */
            }
        }
    }
}
