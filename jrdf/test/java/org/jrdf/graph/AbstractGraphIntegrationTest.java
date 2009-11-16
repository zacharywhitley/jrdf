/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
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
 *
 */

package org.jrdf.graph;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.not;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.local.index.nodepool.ExternalBlankNodeException;
import org.jrdf.query.relation.type.BlankNodeType;
import static org.jrdf.query.relation.type.BlankNodeType.BNODE_TYPE;
import org.jrdf.query.relation.type.NodeType;
import static org.jrdf.query.relation.type.PredicateNodeType.PREDICATE_TYPE;
import static org.jrdf.query.relation.type.ResourceNodeType.RESOURCE_TYPE;
import static org.jrdf.query.relation.type.URIReferenceNodeType.URI_REFERENCE_TYPE;
import org.jrdf.query.relation.type.ValueNodeType;
import org.jrdf.util.ClosableIterable;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.AssertThrows.Block;
import static org.jrdf.util.test.AssertThrows.assertThrows;
import static org.jrdf.util.test.matcher.GraphContainsMatcher.containsTriple;
import static org.jrdf.util.test.matcher.GraphNumberOfTriplesMatcher.hasNumberOfTriples;
import static org.jrdf.util.test.matcher.GraphEmptyMatcher.isEmpty;
import org.junit.Before;
import org.junit.Test;

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
public abstract class AbstractGraphIntegrationTest {

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

    private static final String CANT_REMOVE_ANY_NODE_MESSAGE = "Cannot remove any node values into the graph";
    private static final String CANT_REMOVE_NULL_MESSAGE = "Cannot remove null values into the graph";
    private static final String CONTAIN_CANT_USE_NULLS = "Cannot use null values for contains";
    private static final String FIND_CANT_USE_NULLS = "Cannot use null values for finds";
    private static final String FAILED_TO_ADD_TRIPLE = "Failed to add triple.";
    private static final String FAILED_TO_REMOVE_TRIPLE = "Failed to remove nonexistent triple";

    @Before
    public void setUp() throws Exception {
        graph = newGraph();
        final GraphElementFactory elementFactory = graph.getElementFactory();
        tripleFactory = graph.getTripleFactory();

        blank1 = elementFactory.createBlankNode();
        blank2 = elementFactory.createBlankNode();

        ref1 = elementFactory.createURIReference(create("http://namespace#somevalue"));
        ref2 = elementFactory.createURIReference(create("http://namespace#someothervalue"));
        ref3 = elementFactory.createURIReference(create("http://namespace#yetanothervalue"));
        ref4 = elementFactory.createURIReference(create("http://namespace#yetanotheranothervalue"));
        ref5 = elementFactory.createURIReference(create("http://namespace#visforvalue"));

        l1 = elementFactory.createLiteral(TEST_STR1);
        l2 = elementFactory.createLiteral(TEST_STR2);
    }

    protected abstract Graph newGraph() throws Exception;

    @Test
    public void checkThatNewGraphIsEmpty() throws Exception {
        assertThat(graph, isEmpty());
    }

    @Test
    public void checkThatElementFactoryNotNull() {
        assertThat(graph.getElementFactory(), notNullValue());
    }

    @Test
    public void addToGraphUsingSubjectPredicateObjectNodes() {
        graph.add(blank1, ref1, blank2);
        assertThat(graph, hasNumberOfTriples(1L));
    }

    @Test
    public void addToGraphUsingTriple() {
        final Triple newTriple = tripleFactory.createTriple(blank2, ref1, blank2);
        graph.add(newTriple);
        assertThat(graph, hasNumberOfTriples(1L));
    }

    @Test
    public void addSameNodeTwiceToGraphUsingNodes() {
        graph.add(blank1, ref1, blank2);
        assertThat(graph, hasNumberOfTriples(1L));
        graph.add(blank1, ref1, blank2);
        assertThat(graph, hasNumberOfTriples(1L));
    }

    @Test
    public void addSameNodeTwiceToGraphUsingTriples() {
        graph.add(tripleFactory.createTriple(blank2, ref1, blank2));
        assertThat(graph, hasNumberOfTriples(1L));
        graph.add(tripleFactory.createTriple(blank2, ref1, blank2));
        assertThat(graph, hasNumberOfTriples(1L));
    }

    @Test
    public void cannotAddNullsToTheGraph() {
        checkIllegalAdd("Cannot insert null values into the graph", null, ref1, ref1);
        checkIllegalAdd("Cannot insert null values into the graph", ref1, null, ref1);
        checkIllegalAdd("Cannot insert null values into the graph", ref1, ref1, null);
    }

    @Test
    public void cannotAddAnyNodesToGraph() {
        checkIllegalAdd("Cannot insert any node values into the graph", ANY_SUBJECT_NODE, ref1, ref1);
        checkIllegalAdd("Cannot insert any node values into the graph", ref1, ANY_PREDICATE_NODE, ref1);
        checkIllegalAdd("Cannot insert any node values into the graph", ref1, ref1, ANY_OBJECT_NODE);
    }

    private void checkIllegalAdd(final String expectedMessage, final SubjectNode subject,
        final PredicateNode predicate, final ObjectNode object) {
        assertThrows(IllegalArgumentException.class, expectedMessage, new Block() {
            public void execute() throws Throwable {
                graph.add(subject, predicate, object);
            }
        });
    }

    @Test
    public void testBasicRemoval() throws Exception {
        // add some test data
        addTriplesToGraph();
        addFullTriplesToGraph();
        assertThat(graph, hasNumberOfTriples(6L));

        // delete the first statement
        graph.remove(blank1, ref1, blank2);
        assertThat(graph, hasNumberOfTriples(5L));

        // delete the last statement
        graph.remove(t3);
        assertThat(graph, hasNumberOfTriples(4L));

        // delete the next last statement with a new "triple object"
        t2 = tripleFactory.createTriple(blank2, ref2, blank1);
        graph.remove(t2);
        assertThat(graph, hasNumberOfTriples(3L));

        // delete the next last statement with a triple different to what it was built with
        graph.remove(blank2, ref1, blank1);
        assertThat(graph, hasNumberOfTriples(2L));

        // delete the next last statement with a triple different to what it was built with
        graph.remove(ref1, ref2, l2);
        assertThat(graph, hasNumberOfTriples(1L));
    }

    @Test
    public void testRemovalByIterator() throws GraphException {
        // delete using iterator
        graph.add(blank1, ref2, blank2);
        graph.add(ref1, ref1, ref1);
        graph.add(ref2, ref2, ref2);
        final List<Triple> list = new ArrayList<Triple>();
        list.add(tripleFactory.createTriple(ref1, ref1, ref1));
        list.add(tripleFactory.createTriple(ref2, ref2, ref2));
        graph.remove(list.iterator());
        assertThat(graph, hasNumberOfTriples(1L));
    }

    @Test
    public void testIllegalRemove() throws GraphException {
        graph.add(blank1, ref2, blank2);

        // check can't remove last removed triple
        graph.add(ref2, ref2, ref2);
        graph.remove(ref2, ref2, ref2);
        checkIllegalRemove(GraphException.class, FAILED_TO_REMOVE_TRIPLE, ref2, ref2, ref2);
        assertThat(graph, hasNumberOfTriples(1L));

        // delete the wrong triple
        checkIllegalRemove(GraphException.class, FAILED_TO_REMOVE_TRIPLE, blank2, ref1, blank1);
        assertThat(graph, hasNumberOfTriples(1L));

        // delete a triple that never existed
        checkIllegalRemove(GraphException.class, FAILED_TO_REMOVE_TRIPLE, blank1, ref1, l2);
        assertThat(graph, hasNumberOfTriples(1L));
        checkIllegalRemove(GraphException.class, FAILED_TO_REMOVE_TRIPLE, blank1, ref2, l2);
        assertThat(graph, hasNumberOfTriples(1L));

        // Try to add nulls
        checkIllegalRemove(IllegalArgumentException.class, CANT_REMOVE_NULL_MESSAGE, null, ref1, ref1);
        checkIllegalRemove(IllegalArgumentException.class, CANT_REMOVE_NULL_MESSAGE, ref1, null, ref1);
        checkIllegalRemove(IllegalArgumentException.class, CANT_REMOVE_NULL_MESSAGE, ref1, ref1, null);

        // Try to add any nodes
        checkIllegalRemove(IllegalArgumentException.class, CANT_REMOVE_ANY_NODE_MESSAGE, ANY_SUBJECT_NODE, ref1, ref1);
        checkIllegalRemove(IllegalArgumentException.class, CANT_REMOVE_ANY_NODE_MESSAGE, ref1, ANY_PREDICATE_NODE,
            ref1);
        checkIllegalRemove(IllegalArgumentException.class, CANT_REMOVE_ANY_NODE_MESSAGE, ref1, ref1, ANY_OBJECT_NODE);

        // now clear out the graph
        assertThat(graph.isEmpty(), is(false));
        graph.remove(blank1, ref2, blank2);
        assertThat(graph.isEmpty(), is(true));
        assertThat(graph.getNumberOfTriples(), is(0L));

        checkIllegalRemove(GraphException.class, FAILED_TO_REMOVE_TRIPLE, ref1, ref1, ref1);
        assertThat(graph.isEmpty(), is(true));
        assertThat(graph.getNumberOfTriples(), is(0L));
    }

    private void checkIllegalRemove(final Class<?> expectedException, final String expectedMessage,
        final SubjectNode subject, final PredicateNode predicate, final ObjectNode object) {
        assertThrows(expectedException, expectedMessage, new Block() {
            public void execute() throws Throwable {
                graph.remove(subject, predicate, object);
            }
        });
    }

    @Test
    public void testRemoveIteratorAll() throws Exception {
        checkRemoveIterator(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE, 0);
    }

    @Test
    public void testRemoveIteratorAllPredicateAllObject() throws Exception {
        checkRemoveIterator(ref1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE, 2);
    }

    @Test
    public void testRemoveIteratorAllSubjectAllObject() throws Exception {
        checkRemoveIterator(ANY_SUBJECT_NODE, ref2, ANY_OBJECT_NODE, 1);
    }

    @Test
    public void testRemoveIteratorAllPredicate() throws Exception {
        checkRemoveIterator(blank1, ANY_PREDICATE_NODE, blank2, 1);
    }

    @Test
    public void testRemoveIteratorSingleTriple() throws Exception {
        checkRemoveIterator(ref1, ref2, l2, 2);
    }

    private void checkRemoveIterator(final SubjectNode subjectNode, final PredicateNode predicateNode,
        final ObjectNode objectNode, final long expectedNumberOfTriples) throws Exception {
        addTriplesToGraph();
        final ClosableIterator<Triple> iterator = graph.find(subjectNode, predicateNode, objectNode).iterator();
        try {
            graph.remove(iterator);
            assertThat(graph.getNumberOfTriples(), is(expectedNumberOfTriples));
        } finally {
            iterator.close();
        }
    }

    @Test
    public void testBasicContains() throws Exception {
        // add some test data
        addTriplesToGraph();
        addFullTriplesToGraph();

        // test containership
        assertThat(graph.contains(blank1, ref1, blank2), is(true));
        assertThat(graph.contains(t1), is(true));
        assertThat(graph.contains(tripleFactory.createTriple(blank2, ref2, blank1)), is(true));

        // test non containership
        assertThat(graph.contains(blank1, ref1, blank1), is(false));
        assertThat(graph.contains(tripleFactory.createTriple(blank2, ref2, ref1)), is(false));

        // test containership after removal
        graph.remove(blank1, ref1, blank2);
        assertThat(graph.contains(blank1, ref1, blank2), is(false));
        assertThat(graph.contains(tripleFactory.createTriple(blank1, ref1, blank2)), is(false));

        // put it back in and test again
        graph.add(blank1, ref1, blank2);
        assertThat(graph.contains(blank1, ref1, blank2), is(true));
        assertThat(graph.contains(tripleFactory.createTriple(blank1, ref1, blank2)), is(true));
    }

    @Test
    public void testWildcardContains() throws Exception {
        // AnySubjectNode in contains.
        assertThat(graph, not(containsTriple(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE)));

        // Add a statement
        final GraphElementFactory elementFactory = graph.getElementFactory();
        blank1 = elementFactory.createBlankNode();
        blank2 = elementFactory.createBlankNode();
        ref1 = elementFactory.createURIReference(create("http://something/here"));
        graph.add(tripleFactory.createTriple(blank1, ref1, blank2));

        // Check for existance
        assertThat(graph, containsTriple(ANY_SUBJECT_NODE, ref1, blank2));
        assertThat(graph, containsTriple(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, blank2));
        assertThat(graph, containsTriple(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE));
        assertThat(graph, containsTriple(blank1, ANY_PREDICATE_NODE, blank2));
        assertThat(graph, containsTriple(blank1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE));
        assertThat(graph, containsTriple(blank1, ref1, ANY_OBJECT_NODE));
        assertThat(graph, containsTriple(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE));

        // Check non-existance
        assertThat(graph, not(containsTriple(ANY_SUBJECT_NODE, ref2, blank1)));
        assertThat(graph, not(containsTriple(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, blank1)));
        assertThat(graph, not(containsTriple(blank2, ANY_PREDICATE_NODE, blank1)));
        assertThat(graph, not(containsTriple(blank2, ANY_PREDICATE_NODE, ANY_OBJECT_NODE)));
        assertThat(graph, not(containsTriple(blank2, ref2, ANY_OBJECT_NODE)));
    }

    @Test
    public void testIllegalContains() {
        checkIllegalContains(null, ref1, ref1);
        checkIllegalContains(ref1, null, ref1);
        checkIllegalContains(ref1, ref1, null);
    }

    private void checkIllegalContains(final SubjectNode subject, final PredicateNode predicate,
        final ObjectNode object) {
        assertThrows(IllegalArgumentException.class, CONTAIN_CANT_USE_NULLS, new Block() {
            public void execute() throws Throwable {
                graph.contains(subject, predicate, object);
            }
        });
    }

    @Test
    public void testFinding() throws Exception {
        addTriplesWithBlankNodes();
        ClosableIterator<Triple> it;

        // look for the first triple and check that one is returned
        checkForSingleResult(blank1, ref1, blank2);

        // look for a non-existent triple
        checkForNonExistentResults(ref1, ref1, blank1);

        // look for doubles and check that there is data there
        checkForSingleResult(blank1, ref1, ANY_OBJECT_NODE);
        checkForSingleResult(blank1, ANY_PREDICATE_NODE, blank2);
        checkForSingleResult(ANY_SUBJECT_NODE, ref1, blank2);

        // look for a non-existent double
        checkForNonExistentResults(ref1, ref1, ANY_OBJECT_NODE);
        checkForNonExistentResults(ref1, ANY_PREDICATE_NODE, blank2);
        checkForNonExistentResults(ANY_SUBJECT_NODE, ref3, blank2);

        // look for singles
        checkForSingleResult(blank1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        checkForSingleResult(ANY_SUBJECT_NODE, ref1, ANY_OBJECT_NODE);
        checkForSingleResult(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, l1);

        // look for non-existent singles
        it = graph.find(ref1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).iterator();
        assertThat(it.hasNext(), is(false));
        it.close();
        it = graph.find(ANY_SUBJECT_NODE, ref3, ANY_OBJECT_NODE).iterator();
        assertThat(it.hasNext(), is(false));
        it.close();
        it = graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ref1).iterator();
        assertThat(it.hasNext(), is(false));
        it.close();

        // look for the first triple and check that one is returned
        checkForSingleResultWithTriple(blank1, ref1, blank2);

        // look for a non-existent triple
        checkForNonExistentResultsWithTriple(ref1, ref1, blank1);

        // look for doubles and check that there is data there
        checkForSingleResultWithTriple(blank1, ref1, ANY_OBJECT_NODE);
        checkForSingleResultWithTriple(blank1, ANY_PREDICATE_NODE, blank2);
        checkForSingleResultWithTriple(ANY_SUBJECT_NODE, ref1, blank2);

        // look for a non-existent double
        checkForNonExistentResultsWithTriple(ref1, ref1, ANY_OBJECT_NODE);
        checkForNonExistentResultsWithTriple(ref1, ANY_PREDICATE_NODE, blank2);
        checkForNonExistentResultsWithTriple(ANY_SUBJECT_NODE, ref3, blank2);

        // look for singles
        checkForSingleResultWithTriple(blank1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        checkForSingleResultWithTriple(ANY_SUBJECT_NODE, ref1, ANY_OBJECT_NODE);
        checkForSingleResultWithTriple(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, l1);

        // look for non-existent singles
        checkForNonExistentResultsWithTriple(ref1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        checkForNonExistentResultsWithTriple(ANY_SUBJECT_NODE, ref3, ANY_OBJECT_NODE);
        checkForNonExistentResultsWithTriple(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ref1);

        // Try to test for finding nulls
        checkForNulls(null, ref1, ref1);
        checkForNulls(ref1, null, ref1);
        checkForNulls(ref1, ref1, null);
    }

    private void checkForSingleResult(final SubjectNode subject, final PredicateNode predicate,
        final ObjectNode object) throws GraphException {
        final ClosableIterator<Triple> it = graph.find(subject, predicate, object).iterator();
        try {
            assertThat(it.hasNext(), is(true));
        } finally {
            it.close();
        }
    }

    private void checkForSingleResultWithTriple(final SubjectNode subject, final PredicateNode predicate,
        final ObjectNode object) throws GraphException {
        final Triple t = tripleFactory.createTriple(subject, predicate, object);
        final ClosableIterator<Triple> it = graph.find(t).iterator();
        try {
            assertThat(it.hasNext(), is(true));
        } finally {
            it.close();
        }
    }

    private void checkForNonExistentResults(final SubjectNode subject, final PredicateNode predicate,
        final ObjectNode object) throws GraphException {
        final ClosableIterator<Triple> it = graph.find(subject, predicate, object).iterator();
        try {
            assertThat(it.hasNext(), is(false));
        } finally {
            it.close();
        }
    }

    private void checkForNonExistentResultsWithTriple(final SubjectNode subject, final PredicateNode predicateNode,
        final ObjectNode objectNode) throws GraphException {
        final Triple t = tripleFactory.createTriple(subject, predicateNode, objectNode);
        final ClosableIterator<Triple> it = graph.find(t).iterator();
        try {
            assertThat(it.hasNext(), is(false));
        } finally {
            it.close();
        }
    }

    private void checkForNulls(final SubjectNode subject, final PredicateNode predicate, final ObjectNode object) {
        assertThrows(IllegalArgumentException.class, FIND_CANT_USE_NULLS, new Block() {
            public void execute() throws Throwable {
                graph.find(subject, predicate, object);
            }
        });
    }

    @Test
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
        try {
            assertThat("ClosableIterator is returning false for hasNext().", iterable.iterator().hasNext(), is(true));
            for (Triple triple : iterable) {
                statements.add(triple);
            }
        } finally {
            iterable.iterator().close();
        }

        //check that the iterator contained the correct number of statements
        assertThat("ClosableIterator is incomplete.", graph.getNumberOfTriples(), is((long) statements.size()));

        //check the the collection contains all the original triples
        for (Triple triple1 : triples) {
            assertThat("Iterator did not contain triple: " + triple1 + ".", statements.contains(triple1), is(true));
        }
    }

    // TODO AN Add a test for fulliterative add.
    @Test
    public void testIterativeRemoveAll() throws Exception {
        checkFullIteratorRemoval(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE, 6);
    }

    @Test
    public void testIterativeRemoveAllPredicateAllObject() throws Exception {
        checkFullIteratorRemoval(blank1, ANY_PREDICATE_NODE, ANY_OBJECT_NODE, 2);
    }

    @Test
    public void testIterativeRemoveAllObject() throws Exception {
        checkFullIteratorRemoval(blank2, ref1, ANY_OBJECT_NODE, 2);
    }

    @Test
    public void testIterativeRemoveAllSubjectAllObject() throws Exception {
        checkFullIteratorRemoval(ANY_SUBJECT_NODE, ref2, ANY_OBJECT_NODE, 3);
    }

    @Test
    public void testIterativeRemoveAllPredicate() throws Exception {
        checkFullIteratorRemoval(blank1, ANY_PREDICATE_NODE, blank2, 2);
    }

    @Test
    public void testIterativeRemoveSingleTriple() throws Exception {
        checkFullIteratorRemoval(ref1, ref2, l2, 1);
    }

    private void checkFullIteratorRemoval(final SubjectNode subjectNode, final PredicateNode predicateNode,
        final ObjectNode objectNode, final int expectedFoundTriples) throws Exception {

        addTriplesToGraph();
        addFullTriplesToGraph();
        long numberOfTriplesInGraph = 6;

        // check that all is well
        assertThat(graph.isEmpty(), is(false));
        assertThat(graph.getNumberOfTriples(), is(numberOfTriplesInGraph));

        // get an iterator for all the elements
        final ClosableIterator<Triple> ci = graph.find(subjectNode, predicateNode, objectNode).iterator();

        // Check that it throws an exception before hasNext is called.
        AssertThrows.assertThrows(IllegalStateException.class, new Block() {
            public void execute() throws Throwable {
                ci.remove();
            }
        });

        for (int i = 0; i < expectedFoundTriples; i++) {
            // remove the element
            assertThat("Expected to delete: " + expectedFoundTriples + " but only deleted: " + i, ci.hasNext(),
                is(true));
            ci.next();
            ci.remove();
            assertThat(graph.getNumberOfTriples(), is(--numberOfTriplesInGraph));
        }

        if (numberOfTriplesInGraph == 0) {
            assertThat(graph.isEmpty(), is(true));
        }

        assertThat(ci.hasNext(), is(false));
        ci.close();

        if (numberOfTriplesInGraph == 0) {
            // check that we can't still remove things
            AssertThrows.assertThrows(GraphException.class, new Block() {
                public void execute() throws Throwable {
                    graph.remove(ref2, ref2, ref2);
                }
            });
        }
    }

    /**
     * Test blank node semantics across graphs - throws exception when adding blank node across graphs. Throws
     * an exception when the node ids exist in the graph but return different blank nodes.
     */
    @Test
    public void testBlankNodesAcrossGraphs() throws Exception {
        final Graph externalGraph = newGraph();
        GraphElementFactory graphElementFactory = externalGraph.getElementFactory();
        URI newURI = new URI("http://namespace#somevalue");
        final URIReference newRes = graphElementFactory.createURIReference(newURI);
        externalGraph.add(newRes, newRes, newRes);
        checkIllegalBlankNodeAddition(externalGraph, blank1, newRes, newRes);
        checkIllegalBlankNodeAddition(externalGraph, blank2, newRes, newRes);
    }

    private void checkIllegalBlankNodeAddition(final Graph externalGraph, final SubjectNode subject,
        final PredicateNode predicate, final ObjectNode object) {
        assertThrows(ExternalBlankNodeException.class, FAILED_TO_ADD_TRIPLE, new Block() {
            public void execute() throws Throwable {
                externalGraph.add(subject, predicate, object);
            }
        });
    }

    @Test
    public void testBlankNodeTypeIterator() throws Exception {
        addTestNodes();
        assertThat(getNumberOfBlankNodes(), is(2));
        graph.remove(blank1, ref1, blank2);
        graph.remove(blank1, ref2, blank2);
        graph.remove(blank1, ref1, l1);
        graph.remove(blank1, ref1, l2);
        assertThat(getNumberOfBlankNodes(), is(1));
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

    @Test
    public void testResourceIteratorSimple() throws Exception {
        final ClosableIterator<? extends Node> resources = graph.findNodes(RESOURCE_TYPE).iterator();
        try {
            assertThat("Should be no resources for empty graph", resources.hasNext(), is(false));
            assertThrows(NoSuchElementException.class, new AssertThrows.Block() {
                public void execute() throws Throwable {
                    resources.next();
                }
            });
        } finally {
            resources.close();
        }
    }

    @Test
    public void testURIReferenceResourceIterator() throws Exception {
        checkFindResources(3, URI_REFERENCE_TYPE, URIReference.class);
    }

    @Test
    public void testBlankNodeResourceIterator() throws Exception {
        checkFindResources(2, BNODE_TYPE, BlankNode.class);
    }

    private void checkFindResources(final int expectedNumber, final ValueNodeType nodeType, final Class<?> clazz)
        throws GraphException {
        addTestNodes();
        final ClosableIterator<Resource> iterator = graph.findResources(nodeType).iterator();
        try {
            int counter = 0;
            while (iterator.hasNext()) {
                final Resource resource = iterator.next();
                assertThat(resource.getUnderlyingNode().getClass(), Matchers.typeCompatibleWith(clazz));
                counter++;
            }
            assertThat("Unexpected number of unique nodes", counter, is(expectedNumber));
        } finally {
            iterator.close();
        }
    }

    @Test
    public void testBlankNodes() throws Exception {
        addTestNodes();
        checkFindNodes(2, BNODE_TYPE);
    }

    @Test
    public void testUriReferenceNodes() throws Exception {
        addTestNodes();
        checkFindNodes(5, URI_REFERENCE_TYPE);
        graph.remove(ref5, ref2, ref1);
        checkFindNodes(5, URI_REFERENCE_TYPE);
        graph.remove(ref1, ref3, ref5);
        graph.remove(ref5, ref5, ref5);
        checkFindNodes(4, URI_REFERENCE_TYPE);
    }

    @Test
    public void testResourceNodes() throws Exception {
        addTestNodes();
        checkFindNodes(5, RESOURCE_TYPE);
    }

    @Test
    public void testPredicateNodes() throws Exception {
        addTestNodes();
        checkFindNodes(4, PREDICATE_TYPE);
    }

    private void checkFindNodes(final int expectedNumber, final NodeType nodeType) throws GraphException {
        final ClosableIterable<? extends Node> nodes = graph.findNodes(nodeType);
        try {
            int counter = 0;
            for (Node node : nodes) {
                counter++;
            }
            assertThat("Unexpected number of unique resources nodes", counter, is(expectedNumber));
        } finally {
            nodes.iterator().close();
        }
    }

    @Test
    public void testFixedUniquePredicateIterator() throws Exception {
        addTestNodes();
        checkFixedUniquePredicateIterator(graph.getElementFactory().createResource(blank2), ref1, ref2);
        checkFixedUniquePredicateIterator(graph.getElementFactory().createResource(ref5), ref2, ref3, ref5);
    }

    private void checkFixedUniquePredicateIterator(final Resource resource, final PredicateNode... predicates)
        throws Exception {
        int counter = 0;
        final ClosableIterable<PredicateNode> resourcePredicates = graph.findPredicates(resource);
        try {
            final Set<PredicateNode> expectedPredicates = new HashSet<PredicateNode>(Arrays.asList(predicates));
            for (final PredicateNode predicateNode : resourcePredicates) {
                assertThat("Results should not have: " + predicateNode + " expected: " + expectedPredicates,
                    expectedPredicates.contains(predicateNode), is(true));
                counter++;
            }
            assertThat("Wrong number of unique predicates", counter, is(expectedPredicates.size()));
        } finally {
            resourcePredicates.iterator().close();
        }
    }

    @Test
    public void testClear() throws Exception {
        addTriplesToGraph();
        assertThat(graph.getNumberOfTriples(), equalTo(3L));
        assertThat(graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).iterator().hasNext(), is(true));
        graph.clear();
        assertThat(graph.getNumberOfTriples(), equalTo(0L));
        assertThat(graph.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).iterator().hasNext(), is(false));
    }

    @Test
    public void testSeparateGraphs() throws Exception {
        graph.add(blank2, ref1, l2);
        assertThat(graph.getNumberOfTriples(), equalTo(1L));
        newGraph();
        assertThat(graph.getNumberOfTriples(), equalTo(1L));
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

    private void addTestNodes() throws GraphException {
        addTriplesWithBlankNodes();
        graph.add(ref1, ref1, ref1);
        graph.add(ref1, ref3, ref1);
        graph.add(ref4, ref3, ref1);
        graph.add(ref5, ref2, ref1);
        graph.add(ref1, ref3, ref5);
        graph.add(ref5, ref5, ref5);
    }

    private void addTriplesWithBlankNodes() throws GraphException {
        graph.add(blank1, ref1, blank2);
        graph.add(blank1, ref1, l1);
        graph.add(blank1, ref2, blank2);
        graph.add(blank1, ref1, l2);
        graph.add(blank2, ref1, blank2);
        graph.add(blank2, ref2, blank2);
        graph.add(blank2, ref1, l1);
        graph.add(blank2, ref1, l2);
    }
}
