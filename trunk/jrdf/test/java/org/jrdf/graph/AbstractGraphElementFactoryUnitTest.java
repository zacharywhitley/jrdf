/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.graph;

import junit.framework.TestCase;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.global.TripleImpl;
import org.jrdf.graph.local.iterator.ClosableIterator;
import org.jrdf.util.test.ClassPropertiesTestUtil;

import java.net.URI;

/**
 * Abstract Test case for Graph Element Factories.
 * Implementing packages should extend this class and implement the
 * {@link #newGraph}, {@link #getDefaultLiteralType} and
 * {@link #getDefaultLiteralLanguage} methods.
 *
 * @author <a href="mailto:pgearon@users.sourceforge.net">Paul Gearon</a>
 * @version $Revision$
 */
public abstract class AbstractGraphElementFactoryUnitTest extends TestCase {

    /**
     * Instance of a graph element factory.
     */
    private GraphElementFactory elementFactory;

    /**
     * Instance of a graph triple factory.
     */
    private TripleFactory tripleFactory;

    /**
     * Global graph object.
     */
    private Graph graph;

    /**
     * Create test instance.
     *
     * @throws Exception A generic exception - this should cause the tests to
     *                   fail.
     */
    public void setUp() throws Exception {
        graph = newGraph();
        elementFactory = graph.getElementFactory();
        tripleFactory = graph.getTripleFactory();
    }

    //
    // abstract methods specific to the implementation.
    //

    /**
     * Create a new graph of the appropriate type.
     *
     * @return A new graph implementation object.
     * @throws Exception A generic exception - this should cause the tests to
     *                   fail.
     */
    protected abstract Graph newGraph() throws Exception;

    /**
     * Return the default literal type from the implementation.
     *
     * @return The default Literal type.
     */
    protected abstract URI getDefaultLiteralType();

    /**
     * Get the default literal language from the implementation.
     *
     * @return The default Literal language.
     */
    public abstract String getDefaultLiteralLanguage();

    //
    // Test cases
    //

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(GraphElementFactory.class,
            elementFactory.getClass());
    }

    /**
     * Tests that each of the createLiteral methods work as expected.
     *
     * @throws Exception if query fails when it should have succeeded
     */
    public void testCreateLiterals() throws Exception {
        final String TEST_STR1 = "A test string";
        final String TEST_STR2 = "Another test string";

        // createLiteral(String lexicalValue)
        Literal l1 = elementFactory.createLiteral(TEST_STR1);
        Literal l2 = elementFactory.createLiteral(TEST_STR2);
        Literal l3 = elementFactory.createLiteral(TEST_STR1);
        assertFalse(l1.equals(l2));
        assertEquals(l1, l3);
        assertEquals(getDefaultLiteralType(), l1.getDatatypeURI());
        assertEquals("", l1.getLanguage());
        assertEquals(TEST_STR1, l1.getLexicalForm());

        // createLiteral(String lexicalValue, String languageType)
        l1 = elementFactory.createLiteral(TEST_STR1, "it");
        l2 = elementFactory.createLiteral(TEST_STR2, "it");
        l3 = elementFactory.createLiteral(TEST_STR1, "it");
        Literal l4 = elementFactory.createLiteral(TEST_STR1);
        assertFalse(l1.equals(l2));
        assertFalse(l1.equals(l4));
        assertEquals(l1, l3);
        assertEquals(getDefaultLiteralType(), l1.getDatatypeURI());
        assertEquals("it", l1.getLanguage());
        assertEquals(TEST_STR1, l1.getLexicalForm());

        // createLiteral(String lexicalValue, URI datatypeURI)
        URI type = new URI("xsd:long");
        l1 = elementFactory.createLiteral("42", type);
        l2 = elementFactory.createLiteral("0", type);
        l3 = elementFactory.createLiteral("42", type);
        l4 = elementFactory.createLiteral("42");
        assertFalse(l1.equals(l2));
        assertFalse(l1.equals(l4));
        assertEquals(l1, l3);
        assertEquals(type, l1.getDatatypeURI());
        assertEquals("", l1.getLanguage());
        assertEquals("42", l1.getLexicalForm());

    }

    /**
     * Tests that each of the createResource methods work as expected.
     *
     * @throws Exception if query fails when it should have succeeded
     */
    public void testCreateResources() throws Exception {
        // test blank node creation
        BlankNode blank1 = elementFactory.createBlankNode();
        BlankNode blank2 = elementFactory.createBlankNode();
        assertFalse(blank1.equals(blank2));

        // test named node creation
        URI uri1 = new URI("http://namespace#somevalue");
        URI uri2 = new URI("http://namespace#someothervalue");
        URIReference ref1 = elementFactory.createURIReference(uri1);
        URIReference ref2 = elementFactory.createURIReference(uri2);
        URIReference ref3 = elementFactory.createURIReference(uri1);
        assertFalse(ref1.equals(ref2));
        assertEquals(ref1, ref3);
        assertEquals(ref1.getURI(), uri1);
    }

    /**
     * Test using the other methods of resource
     */
    public void testResourceUsage() throws Exception {
        final Resource supplier = elementFactory.createResource();
        final URIReference sno = elementFactory.createURIReference(URI.create("urn:sno"));
        supplier.addValue(sno, elementFactory.convertToLiteral("sno"));
        assertEquals(1, graph.getNumberOfTriples());
        final Triple triple = new TripleImpl((SubjectNode) supplier.getUnderlyingNode(), sno,
                elementFactory.convertToLiteral("sno"));
        assertEquals(true, graph.contains(triple));
    }

    /**
     * Tests that each of the createResource methods work as expected.
     *
     * @throws Exception if query fails when it should have succeeded
     */
    public void testCreateTriples() throws Exception {

        BlankNode blank1 = elementFactory.createBlankNode();
        BlankNode blank2 = elementFactory.createBlankNode();

        URI uri1 = new URI("http://namespace#somevalue");
        URI uri2 = new URI("http://namespace#someothervalue");
        URIReference ref1 = elementFactory.createURIReference(uri1);
        URIReference ref2 = elementFactory.createURIReference(uri2);

        final String TEST_STR1 = "A test string";
        Literal l1 = elementFactory.createLiteral(TEST_STR1);

        // test ordinary creation
        Triple triple = tripleFactory.createTriple(blank1, ref1, blank2);
        assertEquals(blank1, triple.getSubject());
        assertEquals(ref1, triple.getPredicate());
        assertEquals(blank2, triple.getObject());

        // test inequality, particularly against differing blank nodes
        Triple triple2 = tripleFactory.createTriple(blank2, ref1, blank2);
        assertFalse(triple.equals(triple2));

        // test equality
        triple2 = tripleFactory.createTriple(blank1, ref1, blank2);
        assertEquals(triple, triple2);

        // test all types of statement creation
        tripleFactory.createTriple(blank1, ref1, l1);
        tripleFactory.createTriple(blank1, ref1, l1);
        tripleFactory.createTriple(ref1, ref2, l1);
        triple = tripleFactory.createTriple(ref1, ref2, blank1);

        // Test that the node exists from a newly created predicate - the same
        // as an already existing predicate
        graph.add(triple);
        graph.add(ref2, ref1, l1);

        URIReference ref4 = elementFactory.createURIReference(uri1);
        URIReference ref5 = elementFactory.createURIReference(uri2);
        Literal l3 = elementFactory.createLiteral(TEST_STR1);
        assertEquals(ref4, ref1);
        assertEquals(ref5, ref2);
        assertEquals(l1, l3);
        assertEquals(l1.getEscapedForm(), l3.getEscapedForm());
        assertTrue(graph.contains(ref4, ref5, blank1));

        ClosableIterator iter = graph.find(ref2, ref1, ANY_OBJECT_NODE);
        while (iter.hasNext()) {
            triple = (Triple) iter.next();
            assertEquals(l1, triple.getObject());
            assertTrue(l1.hashCode() == triple.getObject().hashCode());
            assertEquals(l3, triple.getObject());
            assertTrue(l3.hashCode() == triple.getObject().hashCode());
        }

        assertTrue(graph.find(ref2, ref1, l1).hasNext());
        assertTrue(graph.contains(ref2, ref1, l1));
        assertTrue(graph.find(ref5, ref4, l3).hasNext());
        assertTrue(graph.contains(ref5, ref4, l3));
    }

    /**
     * Tests that objects are always localized before testing.
     *
     * @throws Exception if query fails when it should have succeeded
     */
    public void testTwoGraphs() throws Exception {

        Graph g1 = newGraph();
        Graph g2 = newGraph();

        final String TEST_STR1 = "A test string";
        final String TEST_STR2 = "Foo 2";
        URI uri1 = new URI("http://namespace#somevalue1");
        URI uri2 = new URI("http://namespace#somevalue2");
        URI uri3 = new URI("http://namespace#foo");

        GraphElementFactory gef1 = g1.getElementFactory();
        URIReference g1u1 = gef1.createURIReference(uri1);
        URIReference g1u2 = gef1.createURIReference(uri2);
        URIReference g1u3 = gef1.createURIReference(uri3);

        GraphElementFactory gef2 = g2.getElementFactory();
        Literal g2l1 = gef2.createLiteral(TEST_STR1);
        Literal g2l2 = gef2.createLiteral(TEST_STR2);
        URIReference g2u1 = gef2.createURIReference(uri2);

        // Test inserting a subject and predicate that do not exist in g2.
        boolean isOkay = false;
        try {
            g2.add(g1u1, g1u1, g2l1);
        }
        catch (GraphException ge) {
            isOkay = true;
        }
        assertTrue("Should fail to insert node", isOkay);

        // Test inserting a predicate that does no exist in g2.
        isOkay = false;
        try {
            g2.add(g2u1, g1u1, g2l1);
        }
        catch (GraphException ge) {
            isOkay = true;
        }
        assertTrue("Should fail to insert node", isOkay);

        // Test inserting an object that does not exist g2.
        isOkay = false;
        try {
            g2.add(g2u1, g1u1, g2l2);
        }
        catch (GraphException ge) {
            isOkay = true;
        }
        assertTrue("Should fail to insert node", isOkay);

        // Test inserting a predicate and object that come from another graph but
        // do exist.
        try {
            g2.add(g2u1, g1u2, g1u2);
            assertTrue("Should contain the statemet", g2.contains(g2u1, g2u1, g2u1));
        }
        catch (GraphException ge) {
            ge.printStackTrace();
            fail("Should allow nodes to be inserted from other graphs which have the same value but different node " +
                "ids");
        }

        // Test inserting a statements using objects from the correct graph and then
        // using find and contains with the same, by value, object from another.
        URIReference g2u3 = gef2.createURIReference(uri3);
        g2.add(g2u3, g2u3, g2u3);

        assertTrue("Contains should work by value", g2.contains(g1u3, g1u3, g1u3));
        assertTrue("Find should work by value", g2.find(g1u3, g1u3, g1u3).hasNext());

        // Test the find(<foo>, *, *) works.
        ClosableIterator iter = g2.find(g2u3, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        assertTrue("Should get back at least one result", iter.hasNext());

        // Test the find(*, *, *) works.
        iter = g2.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE);
        assertTrue("Should get back at least one result", iter.hasNext());
    }
}
