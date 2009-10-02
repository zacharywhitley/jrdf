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

// Java packages

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.AssertThrows.assertThrows;
import static org.jrdf.util.test.matcher.GraphContainsMatcher.containsMatchingTriples;
import static org.jrdf.util.test.matcher.GraphContainsMatcher.containsTriple;
import static org.jrdf.util.test.matcher.GraphNumberOfTriplesMatcher.hasNumberOfTriples;
import org.jrdf.vocabulary.RDF;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import static java.net.URI.create;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract test case for graph implementations.
 *
 * @author <a href="mailto:pgearon@users.sourceforge.net">Paul Gearon</a>
 * @author Andrew Newman
 * @version $Revision$
 */
public abstract class AbstractTripleFactoryUnitTest {
    private static final int NUMBER_OF_TRIPLES_TO_ADD = 10;

    /**
     * Instance of a graph object.
     */
    private Graph graph;

    /**
     * Instance of a factory for the graph
     */
    protected GraphElementFactory elementFactory;

    /**
     * Instance of the triple factory for the graph.
     */
    private TripleFactory tripleFactory;

    // The following are interally used "constants"
    private BlankNode blank1;
    private BlankNode blank2;

    private URI uri1;
    private URI uri2;
    private URI uri3;
    private URIReference ref1;
    private URIReference ref2;
    private URIReference ref3;

    private static final String TEST_STR1 = "A test string";
    private static final String TEST_STR2 = "Another test string";
    private PredicateNode reifySubject;
    private PredicateNode reifyPredicate;
    private PredicateNode reifyObject;
    private PredicateNode rdfType;
    private ObjectNode rdfStatement;

    @Before
    public void createTestInstances() throws Exception {
        graph = newGraph();
        elementFactory = graph.getElementFactory();
        tripleFactory = graph.getTripleFactory();

        blank1 = elementFactory.createBlankNode();
        blank2 = elementFactory.createBlankNode();

        uri1 = new URI("http://namespace#somevalue");
        uri2 = new URI("http://namespace#someothervalue");
        uri3 = new URI("http://namespace#yetanothervalue");
        ref1 = elementFactory.createURIReference(uri1);
        ref2 = elementFactory.createURIReference(uri2);
        ref3 = elementFactory.createURIReference(uri3);

        reifySubject = getReifySubject();
        reifyPredicate = getReifyPredicate();
        reifyObject = getReifyObject();
        rdfType = getRdfType();
        rdfStatement = getRdfStatement();
        assertThat(graph.isEmpty(), is(true));
    }

    /**
     * Create a graph implementation.
     *
     * @return A new GraphImpl.
     */
    protected abstract Graph newGraph() throws Exception;

    /**
     * Get the node used for subject reification.
     *
     * @return The subject reification node.
     */
    protected abstract PredicateNode getReifySubject() throws TripleFactoryException;

    /**
     * Get the node used for predicate reification.
     *
     * @return The predicate reification node.
     */
    protected abstract PredicateNode getReifyPredicate() throws TripleFactoryException;

    /**
     * Get the node used for object reification.
     *
     * @return The object reification node.
     */
    protected abstract PredicateNode getReifyObject() throws TripleFactoryException;

    /**
     * Get the node used for rdf:type.
     *
     * @return The object rdf:type node.
     */
    protected abstract PredicateNode getRdfType() throws TripleFactoryException;

    /**
     * Get the node used for rdf:Statement.
     *
     * @return The object rdf:statement node.
     */
    protected abstract ObjectNode getRdfStatement() throws TripleFactoryException;

    /**
     * Create a concrete Collection.
     *
     * @return the new collection.
     */
    protected abstract Collection createCollection(List<ObjectNode> objects);

    /**
     * Create a concrete alternative
     *
     * @return the new alternative.
     */
    protected abstract Alternative createAlternative(List<ObjectNode> objects);

    /**
     * Create a concrete bag
     *
     * @return the new bag.
     */
    protected abstract Bag createBag(List<ObjectNode> objects);

    /**
     * Create a concrete sequence
     *
     * @return the new sequence.
     */
    protected abstract Sequence createSequence(List<ObjectNode> objects);

    @Test
    public void reificationOfNonExistentTripleWithReferenceAddsFourTriples() throws Exception {
        final URIReference reference = elementFactory.createURIReference(uri1);
        tripleFactory.reifyTriple(blank1, ref1, blank2, reference);
        checkReification(reference, 4L, blank1, ref1, blank2);
    }

    @Test
    public void reificationOfNonExistentTripleWithBlankNodeAddsFourTriples() throws Exception {
        final BlankNode node = elementFactory.createBlankNode();
        tripleFactory.reifyTriple(blank1, ref1, blank2, node);
        checkReification(node, 4L, blank1, ref1, blank2);
    }

    @Test
    public void reificationOfExistentTripleWithReferenceAddsFourTriples() throws Exception {
        final Triple triple = tripleFactory.createTriple(blank1, ref1, blank2);
        graph.add(triple);
        final SubjectNode reference = elementFactory.createURIReference(uri2);
        tripleFactory.reifyTriple(triple, reference);
        checkReification(reference, 5L, blank1, ref1, blank2);
    }

    @Test
    public void reificationOfExistentTripleWithBlankNodeAddsFourTriples() throws Exception {
        final Triple triple = tripleFactory.createTriple(blank1, ref1, blank2);
        graph.add(triple);
        final SubjectNode blankNode = elementFactory.createURIReference(uri2);
        tripleFactory.reifyTriple(triple, blankNode);
        checkReification(blankNode, 5L, blank1, ref1, blank2);
    }

    private void checkReification(final SubjectNode reificationNode, final long expectedNumberOfTriplesInGraph,
        final SubjectNode subjectNode, final PredicateNode predicateNode, final ObjectNode objectNode) {
        assertThat(graph, hasNumberOfTriples(expectedNumberOfTriplesInGraph));
        assertThat(graph, containsTriple(reificationNode, rdfType, rdfStatement));
        assertThat(graph, containsTriple(reificationNode, reifySubject, (ObjectNode) subjectNode));
        assertThat(graph, containsTriple(reificationNode, reifyPredicate, (ObjectNode) predicateNode));
        assertThat(graph, containsTriple(reificationNode, reifyObject, objectNode));
    }

    @Test
    public void reifySameTripleTwice() throws Exception {
        // test for double insertion (allowed)
        tripleFactory.reifyTriple(blank1, ref1, blank2, elementFactory.createURIReference(uri1));
        tripleFactory.reifyTriple(blank1, ref1, blank2, elementFactory.createURIReference(uri1));
        assertThat(graph, hasNumberOfTriples(4));

        BlankNode node = elementFactory.createBlankNode();
        tripleFactory.reifyTriple(blank1, ref1, blank2, node);
        tripleFactory.reifyTriple(blank1, ref1, blank2, node);
        assertThat(graph, hasNumberOfTriples(8));

        // test for double insertion (allowed)
        Triple t = tripleFactory.createTriple(blank1, ref2, blank2);
        graph.add(t);
        tripleFactory.reifyTriple(t, elementFactory.createURIReference(uri2));
        tripleFactory.reifyTriple(t, elementFactory.createURIReference(uri2));
        assertThat(graph, hasNumberOfTriples(13));

        t = tripleFactory.createTriple(blank1, ref3, blank2);
        graph.add(t);
        node = elementFactory.createBlankNode();
        tripleFactory.reifyTriple(t, node);
        tripleFactory.reifyTriple(t, node);
        assertThat(graph, hasNumberOfTriples(18));

        // test for insertion with a different reference (allowed)
        tripleFactory.reifyTriple(blank1, ref1, blank2, elementFactory.createURIReference(uri3));
        assertThat(graph, hasNumberOfTriples(22));
        tripleFactory.reifyTriple(blank1, ref1, blank2, elementFactory.createBlankNode());
        assertThat(graph, hasNumberOfTriples(26));
    }

    @Test
    public void disallowInsertionOfANewTripleWithExistingReference() throws Exception {
        tripleFactory.reifyTriple(blank1, ref1, blank2, elementFactory.createURIReference(uri1));
        testCanReifyTriple(blank2, ref1, blank1, elementFactory.createURIReference(uri1));
        assertThat(graph, hasNumberOfTriples(4));

        // test for insertion with a different reference (disallowed)
        tripleFactory.reifyTriple(blank1, ref1, blank2, elementFactory.createURIReference(uri2));
        testTripleCannotBeReified(tripleFactory.createTriple(blank1, ref2, blank2), uri2);
        assertThat(graph, hasNumberOfTriples(8));
    }

    @Test
    public void disallowInsertionOfANewTripleWithAnExistingReference() throws Exception {
        tripleFactory.reifyTriple(blank1, ref1, blank2, elementFactory.createURIReference(uri2));
        testTripleCannotBeReified(tripleFactory.createTriple(blank2, ref2, blank2), uri2);
        assertThat(graph, hasNumberOfTriples(4));
    }

    @Test
    public void collections() throws Exception {
        // Create initial statement
        SubjectNode s = elementFactory.createURIReference(new URI("http://example.org/basket"));
        PredicateNode p = elementFactory.createURIReference(new URI("http://example.org/stuff/1.0/hasFruit"));
        ObjectNode o = elementFactory.createBlankNode();

        // Add to graph
        graph.add(s, p, o);

        // Create collection object.
        List<ObjectNode> fruit = new ArrayList<ObjectNode>() {
            {
                add(elementFactory.createURIReference(create("http://example.org/banana")));
                add(elementFactory.createURIReference(create("http://example.org/kiwi")));
                add(elementFactory.createURIReference(create("http://example.org/pineapple")));
            }
        };

        PredicateNode rdfFirst = elementFactory.createURIReference(RDF.FIRST);
        PredicateNode rdfRest = elementFactory.createURIReference(RDF.REST);
        ObjectNode rdfNil = elementFactory.createURIReference(RDF.NIL);

        // Create collection and add
        Collection collection = createCollection(fruit);

        // Add the collection to the graph.
        tripleFactory.addCollection((SubjectNode) o, collection);

        // Check we've inserted it correctly.
        assertThat(graph, hasNumberOfTriples(7));
        assertThat(graph, containsTriple(s, p, o));
        assertThat(graph, containsTriple((SubjectNode) o, rdfFirst, fruit.get(0)));

        // Get all rdf:first statements
        assertThat(graph, containsMatchingTriples(ANY_SUBJECT_NODE, rdfRest, ANY_OBJECT_NODE, 3));

        // Find all three parts of the collection.
        for (ObjectNode aFruit : fruit) {
            assertThat(graph, containsTriple(ANY_SUBJECT_NODE, rdfFirst, aFruit));
        }

        // Get all rdf:rest statements
        assertThat(graph, containsMatchingTriples(ANY_SUBJECT_NODE, rdfRest, ANY_OBJECT_NODE, 3));

        // Get all rdf:rest with rdf:nil statements
        assertThat(graph, containsMatchingTriples(ANY_SUBJECT_NODE, rdfRest, rdfNil, 1));
    }

    @Test
    public void alternative() throws Exception {
        // Create initial statement
        SubjectNode s = elementFactory.createURIReference(new URI("http://example.org/favourite-bananas"));

        // Create collection object.
        List<ObjectNode> fruit = new ArrayList<ObjectNode>() {
            {
                add(elementFactory.createURIReference(create("http://example.org/banana")));
                add(elementFactory.createURIReference(create("http://example.org/cavendish")));
                add(elementFactory.createURIReference(create("http://example.org/ladyfinger")));
                add(elementFactory.createURIReference(create("http://example.org/banana")));
            }
        };

        ObjectNode rdfAlternative = elementFactory.createURIReference(RDF.ALT);

        // Create collection and add
        Alternative alt = createAlternative(fruit);

        // Add the collection to the graph.
        tripleFactory.addAlternative(s, alt);

        // Check we've inserted it correctly (banana is in twice should be removed)
        assertThat(graph, hasNumberOfTriples(4));
        assertThat(graph, containsTriple(s, rdfType, rdfAlternative));
        for (ObjectNode aFruit : fruit) {
            assertThat(graph, containsTriple(s, ANY_PREDICATE_NODE, aFruit));
        }

        // Check that it doesn't allow duplicates.
        assertThat(graph, containsMatchingTriples(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, fruit.get(0), 1));

        // Compare both alts
        Alternative alt2 = createAlternative(fruit);
        assertThat(alt, equalTo(alt2));
        SubjectNode s2 = elementFactory.createURIReference(new URI("http://example.org/favourite-bananas2"));
        tripleFactory.addAlternative(s2, alt2);
        assertThat(alt, equalTo(alt2));
    }

    @Test
    public void bag() throws Exception {
        // Create initial statement
        SubjectNode s = elementFactory.createURIReference(new URI("http://example.org/favourite-fruit"));

        // Create collection object.
        List<ObjectNode> fruit = new ArrayList<ObjectNode>() {
            {
                add(elementFactory.createURIReference(create("http://example.org/banana")));
                add(elementFactory.createURIReference(create("http://example.org/kiwi")));
                add(elementFactory.createURIReference(create("http://example.org/pineapple")));
                add(elementFactory.createURIReference(create("http://example.org/pineapple")));
                add(elementFactory.createURIReference(create("http://example.org/banana")));
            }
        };

        ObjectNode rdfBag = elementFactory.createURIReference(RDF.BAG);

        // Create collection and add
        Bag bag = createBag(fruit);

        // Add the collection to the graph.
        tripleFactory.addBag(s, bag);

        // Check we've inserted it correctly
        assertThat(graph, hasNumberOfTriples(6));
        assertThat(graph, containsTriple(s, rdfType, rdfBag));
        for (ObjectNode aFruit : fruit) {
            assertThat(graph, containsTriple(s, ANY_PREDICATE_NODE, aFruit));
        }

        // Check that it allows duplicates.
        assertThat(graph, containsMatchingTriples(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, fruit.get(2), 2));

        // Equal before being added to graph.
        Bag bag2 = createBag(fruit);
        assertThat(bag, equalTo(bag2));
        SubjectNode s2 = elementFactory.createURIReference(new URI("http://example.org/favourite-fruit2"));
        tripleFactory.addBag(s2, bag2);
        // Equal after added - Bags are still equal even with different subjects (they don't know about their subjects).
        assertThat(bag, equalTo(bag2));
    }

    @Test
    public void sequence() throws Exception {
        // Create initial statement
        SubjectNode s = elementFactory.createURIReference(new URI("http://example.org/favourite-fruit"));

        // Create collection object.
        List<ObjectNode> fruit = new ArrayList<ObjectNode>() {
            {
                add(elementFactory.createURIReference(create("http://example.org/banana")));
                add(elementFactory.createURIReference(create("http://example.org/kiwi")));
                add(elementFactory.createURIReference(create("http://example.org/pineapple")));
                add(elementFactory.createURIReference(create("http://example.org/kiwi")));
            }
        };

        PredicateNode rdfOne = elementFactory.createURIReference(new URI(RDF.BASE_URI + "_1"));
        PredicateNode rdfTwo = elementFactory.createURIReference(new URI(RDF.BASE_URI + "_2"));
        PredicateNode rdfThree = elementFactory.createURIReference(new URI(RDF.BASE_URI + "_3"));
        PredicateNode rdfFour = elementFactory.createURIReference(new URI(RDF.BASE_URI + "_4"));
        ObjectNode rdfSequence = elementFactory.createURIReference(RDF.SEQ);

        // Create collection and add
        Sequence sequence = createSequence(fruit);

        // Add the collection to the graph.
        tripleFactory.addSequence(s, sequence);

        // Check we've inserted it correctly.
        assertThat(graph, hasNumberOfTriples(5));
        assertThat(graph, containsTriple(s, rdfType, rdfSequence));
        assertThat(graph, containsTriple(s, rdfOne, fruit.get(0)));
        assertThat(graph, containsTriple(s, rdfTwo, fruit.get(1)));
        assertThat(graph, containsTriple(s, rdfThree, fruit.get(2)));
        assertThat(graph, containsTriple(s, rdfFour, fruit.get(3)));

        Sequence sequence2 = createSequence(fruit);
        assertThat(sequence, equalTo(sequence2));
        SubjectNode s2 = elementFactory.createURIReference(new URI("http://example.org/favourite-fruit2"));
        tripleFactory.addSequence(s2, sequence2);
        assertThat(sequence, equalTo(sequence2));
    }

    @Test
    public void easyToUseMethods() throws Exception {
        tripleFactory.addTriple(uri1, uri1, uri1);
        assertThat(graph, containsTriple(ref1, ref1, ref1));
        Resource resource = elementFactory.createResource(uri2);
        tripleFactory.addTriple(uri1, uri1, resource);
        assertThat(graph, containsTriple(ref1, ref1, ref2));
    }

    @Test
    public void multipleAddtion() throws Exception {
        for (int i = 0; i < NUMBER_OF_TRIPLES_TO_ADD; i++) {
            tripleFactory.addTriple(create("http://subject/" + i), create("http://predicate/" + i),
                create("http://object/" + i));
        }
        assertThat(graph, hasNumberOfTriples(NUMBER_OF_TRIPLES_TO_ADD));
    }

    private void testCanReifyTriple(final SubjectNode subject, final PredicateNode predicate, final ObjectNode object,
        final SubjectNode r) {
        assertThrows(AlreadyReifiedException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                tripleFactory.reifyTriple(subject, predicate, object, r);
            }
        });
    }

    private void testTripleCannotBeReified(final Triple triple, final URI r) {
        assertThrows(AlreadyReifiedException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                tripleFactory.reifyTriple(triple, elementFactory.createURIReference(r));
            }
        });
    }
}
