/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003, 2004 The JRDF Project.  All rights reserved.
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

package org.jrdf.graph;

import junit.framework.TestCase;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.test.AssertThrows;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstract test case for graph implementations.
 *
 * @author <a href="mailto:pgearon@users.sourceforge.net">Paul Gearon</a>
 * @author Andrew Newman
 * @version $Revision$
 */
public abstract class AbstractGraphUnitTest extends TestCase {

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

    /**
     * Create test instance.
     *
     * @throws Exception A generic exception - this should cause the tests to
     *                   fail.
     */
    public void setUp() throws Exception {
        graph = newGraph();
        GraphElementFactory elementFactory = graph.getElementFactory();
        tripleFactory = graph.getTripleFactory();

        blank1 = elementFactory.createResource();
        blank2 = elementFactory.createResource();

        URI uri1 = new URI("http://namespace#somevalue");
        URI uri2 = new URI("http://namespace#someothervalue");
        URI uri3 = new URI("http://namespace#yetanothervalue");
        ref1 = elementFactory.createResource(uri1);
        ref2 = elementFactory.createResource(uri2);
        ref3 = elementFactory.createResource(uri3);

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
        assertTrue(graph.isEmpty());
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
        AssertThrows.assertThrows(IllegalArgumentException.class, CANT_ADD_NULL_MESSAGE, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.add(null, ref1, ref1);
            }
        });
        AssertThrows.assertThrows(IllegalArgumentException.class, CANT_ADD_NULL_MESSAGE, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.add(ref1, null, ref1);
            }
        });
        AssertThrows.assertThrows(IllegalArgumentException.class, CANT_ADD_NULL_MESSAGE, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.add(ref1, ref1, null);
            }
        });

        // Try to add any nodes
        AssertThrows.assertThrows(IllegalArgumentException.class, CANT_ADD_ANY_NODE_MESSAGE, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.add(ANY_SUBJECT_NODE, ref1, ref1);
            }
        });
        AssertThrows.assertThrows(IllegalArgumentException.class, CANT_ADD_ANY_NODE_MESSAGE, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.add(ref1, ANY_PREDICATE_NODE, ref1);
            }
        });
        AssertThrows.assertThrows(IllegalArgumentException.class, CANT_ADD_ANY_NODE_MESSAGE, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.add(ref1, ref1, ANY_OBJECT_NODE);
            }
        });
    }

    /**
     * Tests removal.
     *
     * @throws Exception A generic exception - this should cause the tests to
     *                   fail.
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
        try {
            graph.remove(blank2, ref1, blank1);
            assertTrue(false);
        }
        catch (GraphException e) { /* no-op */
        }
        assertEquals(1, graph.getNumberOfTriples());

        // delete a triple that never existed
        try {
            graph.remove(blank2, ref2, l2);
            assertTrue(false);
        }
        catch (GraphException e) { /* no-op */
        }
        assertEquals(1, graph.getNumberOfTriples());

        // and delete with a triple object
        t1 = tripleFactory.createTriple(blank2, ref1, blank1);
        try {
            graph.remove(t1);
            assertTrue(false);
        }
        catch (GraphException e) { /* no-op */
        }
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
        AssertThrows.assertThrows(IllegalArgumentException.class, CANT_REMOVE_NULL_MESSAGE, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.remove(null, ref1, ref1);
            }
        });
        AssertThrows.assertThrows(IllegalArgumentException.class, CANT_REMOVE_NULL_MESSAGE, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.remove(ref1, null, ref1);
            }
        });
        AssertThrows.assertThrows(IllegalArgumentException.class, CANT_REMOVE_NULL_MESSAGE, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.remove(ref1, ref1, null);
            }
        });

        // Try to add any nodes
        AssertThrows.assertThrows(IllegalArgumentException.class, CANT_REMOVE_ANY_NODE_MESSAGE,
            new AssertThrows.Block() {
                public void execute() throws Throwable {
                    graph.remove(ANY_SUBJECT_NODE, ref1, ref1);
                }
            });
        AssertThrows.assertThrows(IllegalArgumentException.class, CANT_REMOVE_ANY_NODE_MESSAGE,
            new AssertThrows.Block() {
                public void execute() throws Throwable {
                    graph.remove(ref1, ANY_PREDICATE_NODE, ref1);
                }
            });
        AssertThrows.assertThrows(IllegalArgumentException.class, CANT_REMOVE_ANY_NODE_MESSAGE,
            new AssertThrows.Block() {
                public void execute() throws Throwable {
                    graph.remove(ref1, ref1, ANY_OBJECT_NODE);
                }
            });
    }

    public void testRemoveIterator() throws Exception {
        // Test removing using the iterator from a find.
        checkRemoveIterator(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE, 0);
        checkRemoveIterator(ref1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE, 2);
        checkRemoveIterator(blank1, ANY_PREDICATE_NODE, blank2, 1);
        checkRemoveIterator(ref1, ref2, l2, 2);
    }

    private void checkRemoveIterator(SubjectNode subjectNode, PredicateNode predicateNode, ObjectNode objectNode,
        int expectedNumberOfTriples) throws GraphException {
        addTriplesToGraph();
        ClosableIterator<Triple> iterator = graph.find(subjectNode, predicateNode, objectNode);
        graph.remove(iterator);
        assertEquals(expectedNumberOfTriples, graph.getNumberOfTriples());
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
        blank1 = newElementFactory.createResource();
        blank2 = newElementFactory.createResource();
        ref1 = newElementFactory.createResource(URI.create("http://something/here"));
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
        AssertThrows.assertThrows(IllegalArgumentException.class, CONTAIN_CANT_USE_NULLS, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.contains(null, ref1, ref1);
            }
        });
        AssertThrows.assertThrows(IllegalArgumentException.class, CONTAIN_CANT_USE_NULLS, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.contains(ref1, null, ref1);
            }
        });
        AssertThrows.assertThrows(IllegalArgumentException.class, CONTAIN_CANT_USE_NULLS, new AssertThrows.Block() {
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
        ClosableIterator it = graph.find(blank1, ref1, blank2);
        assertTrue(it.hasNext());
        it.close();

        // look for a non-existent triple
        it = graph.find(ref1, ref1, blank1);
        assertFalse(it.hasNext());
        it.close();

        // look for doubles and check that there is data there
        it = graph.find(blank1, ref1, ANY_OBJECT_NODE);
        assertTrue(it.hasNext());
        it.close();
        it = graph.find(blank1, ANY_PREDICATE_NODE, blank2);
        assertTrue(it.hasNext());
        it.close();
        it = graph.find(ANY_SUBJECT_NODE, ref1, blank2);
        assertTrue(it.hasNext());
        it.close();

        // look for a non-existent double
        it = graph.find(ref1, ref1, ANY_OBJECT_NODE);
        assertFalse(it.hasNext());
        it.close();
        it = graph.find(ref1, ANY_PREDICATE_NODE, blank2);
        assertFalse(it.hasNext());
        it.close();
        it = graph.find(ANY_SUBJECT_NODE, ref3, blank2);
        assertFalse(it.hasNext());
        it.close();

        // look for singles
        it = graph.find(blank1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        assertTrue(it.hasNext());
        it.close();
        it = graph.find(ANY_SUBJECT_NODE, ref1, ANY_OBJECT_NODE);
        assertTrue(it.hasNext());
        it.close();
        it = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, l1);
        assertTrue(it.hasNext());
        it.close();

        // look for non-existent singles
        it = graph.find(ref1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        assertFalse(it.hasNext());
        it.close();
        it = graph.find(ANY_SUBJECT_NODE, ref3, ANY_OBJECT_NODE);
        assertFalse(it.hasNext());
        it.close();
        it = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ref1);
        assertFalse(it.hasNext());
        it.close();

        // do it all again with triples

        // look for the first triple and check that one is returned
        Triple t = tripleFactory.createTriple(blank1, ref1, blank2);
        it = graph.find(t);
        assertTrue(it.hasNext());
        it.close();

        // look for a non-existent triple
        t = tripleFactory.createTriple(ref1, ref1, blank1);
        it = graph.find(t);
        assertFalse(it.hasNext());
        it.close();

        // look for doubles and check that there is data there
        t = tripleFactory.createTriple(blank1, ref1, ANY_OBJECT_NODE);
        it = graph.find(t);
        assertTrue(it.hasNext());
        it.close();
        t = tripleFactory.createTriple(blank1, ANY_PREDICATE_NODE, blank2);
        it = graph.find(t);
        assertTrue(it.hasNext());
        it.close();
        t = tripleFactory.createTriple(ANY_SUBJECT_NODE, ref1, blank2);
        it = graph.find(t);
        assertTrue(it.hasNext());
        it.close();

        // look for a non-existent double
        t = tripleFactory.createTriple(ref1, ref1, ANY_OBJECT_NODE);
        it = graph.find(t);
        assertFalse(it.hasNext());
        it.close();
        t = tripleFactory.createTriple(ref1, ANY_PREDICATE_NODE, blank2);
        it = graph.find(t);
        assertFalse(it.hasNext());
        it.close();
        t = tripleFactory.createTriple(ANY_SUBJECT_NODE, ref3, blank2);
        it = graph.find(t);
        assertFalse(it.hasNext());
        it.close();

        // look for singles
        t = tripleFactory.createTriple(blank1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        it = graph.find(t);
        assertTrue(it.hasNext());
        it.close();
        t = tripleFactory.createTriple(ANY_SUBJECT_NODE, ref1, ANY_OBJECT_NODE);
        it = graph.find(t);
        assertTrue(it.hasNext());
        it.close();
        t = tripleFactory.createTriple(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, l1);
        it = graph.find(t);
        assertTrue(it.hasNext());
        it.close();

        // look for non-existent singles
        t = tripleFactory.createTriple(ref1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        it = graph.find(t);
        assertFalse(it.hasNext());
        it.close();
        t = tripleFactory.createTriple(ANY_SUBJECT_NODE, ref3, ANY_OBJECT_NODE);
        it = graph.find(t);
        assertFalse(it.hasNext());
        it.close();
        t = tripleFactory.createTriple(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ref1);
        it = graph.find(t);
        assertFalse(it.hasNext());
        it.close();

        // Try to test for finding nulls
        AssertThrows.assertThrows(IllegalArgumentException.class, FIND_CANT_USE_NULLS, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.find(null, ref1, ref1);
            }
        });
        AssertThrows.assertThrows(IllegalArgumentException.class, FIND_CANT_USE_NULLS, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.find(ref1, null, ref1);
            }
        });
        AssertThrows.assertThrows(IllegalArgumentException.class, FIND_CANT_USE_NULLS, new AssertThrows.Block() {
            public void execute() throws Throwable {
                graph.find(ref1, ref1, null);
            }
        });
    }

    /**
     * Tests iteration over a found set.
     *
     * @throws Exception A generic exception - this should cause the tests to
     *                   fail.
     */
    public void testIteration() throws Exception {
        GraphElementFactory factory = graph.getElementFactory();

        //create nodes
        BlankNode bNode1 = factory.createResource();
        BlankNode bNode2 = factory.createResource();
        URIReference testUri1 = factory.createResource(new URI("http://tucana.org/tucana#testUri1"));
        URIReference testUri2 = factory.createResource(new URI("http://tucana.org/tucana#testUri2"));
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

        //query them and put contents of iterator in a set for checking
        //(iterator may return results in a different order)
        Set<Triple> statements = new HashSet<Triple>();
        ClosableIterator<Triple> iter = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        assertTrue("ClosableIterator is returning false for hasNext().",
            iter.hasNext());
        while (iter.hasNext()) {
            statements.add(iter.next());
        }
        iter.close();

        //check that the iterator contained the correct number of statements
        assertEquals("ClosableIterator is incomplete.", statements.size(), graph.getNumberOfTriples());

        //check the the set contains all the original triples
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
        checkFullIteratorRemoval(blank1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE, 2);
        checkFullIteratorRemoval(blank2, ref1, ANY_OBJECT_NODE, 2);
        checkFullIteratorRemoval(ref1, ref2, l2, 1);
    }

    private void checkFullIteratorRemoval(SubjectNode subjectNode, PredicateNode predicateNode, ObjectNode objectNode,
        int expectedFoundTriples)
        throws GraphException, TripleFactoryException {

        addTriplesToGraph();
        addFullTriplesToGraph();
        int numberOfTriplesInGraph = 6;

        // check that all is well
        assertFalse(graph.isEmpty());
        assertEquals(numberOfTriplesInGraph, graph.getNumberOfTriples());

        // get an iterator for all the elements
        ClosableIterator ci = graph.find(subjectNode, predicateNode, objectNode);

        // Check that it throws an exception before hasNext is called.
        checkInvalidRemove(ci);

        for (int i = expectedFoundTriples - 1; 0 <= i; i--) {
            // remove the element
            assertTrue(ci.hasNext());
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

    private void checkInvalidRemove(ClosableIterator ci) {
        try {
            ci.remove();
            fail("Must throw an exception.");
        }
        catch (IllegalStateException ise) {
            assertTrue(ise.getMessage().indexOf("Next not called or beyond end of data") != -1);
        }
    }

    /**
     * Checks that an iterator matches a set exactly.
     * The set will be emptied and the iterator will be closed.
     *
     * @param execptedTriples the expected set of execptedTriples.
     * @param actualTriples   the iterator containing the actual execptedTriples.
     */
    private void checkSet(Set execptedTriples, ClosableIterator actualTriples) {
        while (actualTriples.hasNext()) {
            Triple t = (Triple) actualTriples.next();
            assertTrue(execptedTriples.contains(t));
            execptedTriples.remove(t);
        }
        if (!execptedTriples.isEmpty()) {
            System.out.println("execptedTriples still contains: " + execptedTriples.toString());
        }
        assertTrue(execptedTriples.isEmpty());
        actualTriples.close();
    }


    private void addTriplesToGraph() throws GraphException {
        graph.add(blank1, ref1, blank2);
        graph.add(blank1, ref2, blank2);
        graph.add(ref1, ref2, l2);
    }


    private void addFullTriplesToGraph() throws TripleFactoryException, GraphException {
        t1 = tripleFactory.createTriple(blank2, ref1, blank1);
        graph.add(t1);
        t2 = tripleFactory.createTriple(blank2, ref2, blank1);
        graph.add(t2);
        t3 = tripleFactory.createTriple(blank2, ref1, l1);
        graph.add(t3);
    }
}
