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

import org.jrdf.graph.local.index.nodepool.ExternalBlankNodeException;
import org.jrdf.util.ClosableIterable;
import org.jrdf.util.ClosableIterator;
import org.jrdf.util.test.AssertThrows;
import org.jrdf.vocabulary.XSD;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.GregorianCalendar;

import static java.net.URI.create;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jrdf.graph.AnyObjectNode.ANY_OBJECT_NODE;
import static org.jrdf.graph.AnyPredicateNode.ANY_PREDICATE_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import static org.jrdf.graph.NullURI.NULL_URI;
import static org.jrdf.util.test.AssertThrows.assertThrows;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import static org.jrdf.util.test.matcher.GraphContainsMatcher.containsTriple;

/**
 * Abstract Test case for Graph Element Factories. Implementing packages should extend this class and implement the
 * {@link #newGraph}, {@link #defaultLiteralType} and {@link #getDefaultLiteralLanguage} methods.
 *
 * @author <a href="mailto:pgearon@users.sourceforge.net">Paul Gearon</a>
 * @author Andrew Newman
 * @version $Revision$
 */
public abstract class AbstractGraphElementFactoryUnitTest {

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
    private static final String TEST_STR_1 = "A test string";
    private static final String TEST_STR_2 = "Another test string";
    private static final String EMPTY_STRING = "";
    private static final String ITALIAN_LANGUAGE = "it";
    private static final URI TYPE = XSD.STRING;
    private static final URI URI_1 = create("http://namespace#somevalue");
    private static final URI URI_2 = create("http://namespace#someothervalue");
    private Literal testLiteral1;
    private Literal testLiteral2;
    private Literal newTestLiteral1;
    private Literal languageLiteral1;
    private Literal languageLiteral2;
    private Literal newLanguageLiteral1;
    private Literal typedLiteral1;
    private Literal typedLiteral2;
    private Literal newTypedLiteral1;
    private URIReference reference1;
    private URIReference reference2;
    private URIReference newReference;

    @Before
    public void setUp() throws Exception {
        graph = newGraph();
        elementFactory = graph.getElementFactory();
        tripleFactory = graph.getTripleFactory();
        testLiteral1 = elementFactory.createLiteral(TEST_STR_1);
        testLiteral2 = elementFactory.createLiteral(TEST_STR_2);
        newTestLiteral1 = elementFactory.createLiteral(TEST_STR_1);
        languageLiteral1 = elementFactory.createLiteral(TEST_STR_1, ITALIAN_LANGUAGE);
        languageLiteral2 = elementFactory.createLiteral(TEST_STR_2, ITALIAN_LANGUAGE);
        newLanguageLiteral1 = elementFactory.createLiteral(TEST_STR_1, ITALIAN_LANGUAGE);
        typedLiteral1 = elementFactory.createLiteral(TEST_STR_1, TYPE);
        typedLiteral2 = elementFactory.createLiteral(TEST_STR_2, TYPE);
        newTypedLiteral1 = elementFactory.createLiteral(TEST_STR_1, TYPE);
        reference1 = elementFactory.createURIReference(URI_1);
        reference2 = elementFactory.createURIReference(URI_2);
        newReference = elementFactory.createURIReference(URI_1);
    }

    /**
     * Create a new graph of the appropriate type.
     *
     * @return A new graph implementation object.
     * @throws Exception A generic exception - this should cause the tests to fail.
     */
    protected abstract Graph newGraph() throws Exception;

    /**
     * Return the default literal type from the implementation.
     *
     * @return The default Literal type.
     */
    protected abstract URI defaultLiteralType();

    /**
     * Get the default literal language from the implementation.
     *
     * @return The default Literal language.
     */
    public abstract String getDefaultLiteralLanguage();

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(GraphElementFactory.class, elementFactory.getClass());
    }

    @Test
    public void defaultLiteralProperties() {
        checkLiteralProperties(testLiteral1, TEST_STR_1, defaultLiteralType(), EMPTY_STRING);
        checkLiteralProperties(testLiteral2, TEST_STR_2, defaultLiteralType(), EMPTY_STRING);
        checkLiteralProperties(languageLiteral1,  TEST_STR_1, defaultLiteralType(), ITALIAN_LANGUAGE);
        checkLiteralProperties(languageLiteral2,  TEST_STR_2, defaultLiteralType(), ITALIAN_LANGUAGE);
        checkLiteralProperties(typedLiteral1, TEST_STR_1, TYPE, EMPTY_STRING);
        checkLiteralProperties(typedLiteral2, TEST_STR_2, TYPE, EMPTY_STRING);
    }

    @Test
    public void sameLiteralValuesAreEqual() {
        assertThat(testLiteral1, equalTo(newTestLiteral1));
    }

    @Test
    public void differentLiteralValuesAreUnequal() {
        assertThat(testLiteral1, not(equalTo(testLiteral2)));
    }

    @Test
    public void sameLanguageLiteralsAreEqual() {
        assertThat(languageLiteral1, equalTo(newLanguageLiteral1));
    }

    @Test
    public void differentLiteralValuesSameLanguageAreUnequal() {
        assertThat(languageLiteral1, not(equalTo(languageLiteral2)));
    }

    @Test
    public void sameLiteralValueDifferentLanguageValuesAreUnequal() {
        assertThat(languageLiteral1, not(equalTo(testLiteral1)));
    }

    @Test
    public void sameTypeLiteralsAreEqual() {
        assertThat(typedLiteral1, equalTo(newTypedLiteral1));
    }

    @Test
    public void differentLiteralValuesSameTypeAreUnequal() {
        assertThat(typedLiteral1, not(equalTo(languageLiteral2)));
    }

    @Test
    public void sameLiteralValueDifferentTypesAreUnequal() {
        assertThat(typedLiteral1, not(equalTo(elementFactory.createLiteral(TEST_STR_1, XSD.ANY_URI))));
        assertThat(typedLiteral1, not(equalTo(testLiteral1)));
    }

    public void checkLiteralProperties(Literal literal, String lexicalValue, URI datatype, String language) {
        assertThat(literal.getDatatypeURI(), equalTo(datatype));
        assertThat(literal.getLanguage(), equalTo(language));
        assertThat(literal.getLexicalForm(), equalTo(lexicalValue));
    }

    @Test
    public void twoNewBlankNodesAreAlwaysUnequal() {
        assertThat(elementFactory.createBlankNode(), not(equalTo(elementFactory.createBlankNode())));
    }

    @Test
    public void theSameInvalidURICanBeRetrieved() {
        elementFactory.createURIReference(create("invalidURI"), false);
        elementFactory.createURIReference(create("invalidURI"), false);
    }

    @Test
    public void tryValidateInvalidUri() {
        assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                elementFactory.createURIReference(create("invalidURI"), true);
            }
        });
        assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                elementFactory.createResource(create("invalidURI"), true);
            }
        });
    }

    @Test
    public void dontValidateInvalidUri() {
        elementFactory.createURIReference(create("invalidURI"), false);
        elementFactory.createResource(create("invalidURI"), false);
    }

    @Test
    public void createResourcesAndCheckEquality() {
        assertThat(reference1, not(equalTo(reference2)));
        assertThat(reference1, equalTo(newReference));
        assertThat(reference1.getURI(), equalTo(URI_1));
        assertThat(reference2.getURI(), equalTo(URI_2));
    }

    @Test
    public void mappingOfJavaTypeToRdfLiteral() throws Exception {
        assertThat(elementFactory.createLiteral("hello").getDatatypeURI(), sameInstance(NULL_URI));
        assertThat(elementFactory.createLiteral(false).getDatatypeURI(), equalTo(XSD.BOOLEAN));
        assertThat(elementFactory.createLiteral(Boolean.TRUE).getDatatypeURI(), equalTo(XSD.BOOLEAN));
        assertThat(elementFactory.createLiteral(new GregorianCalendar(1, 1, 1)).getDatatypeURI(),
            equalTo(XSD.DATE_TIME));
        assertThat(elementFactory.createLiteral(new java.sql.Date(1)).getDatatypeURI(), equalTo(XSD.DATE_TIME));
        assertThat(elementFactory.createLiteral(new java.util.Date(1)).getDatatypeURI(), equalTo(XSD.DATE_TIME));
        assertThat(elementFactory.createLiteral(new QName("foo")).getDatatypeURI(), equalTo(XSD.Q_NAME));
    }

    @Test
    public void mappingOfJavaNumericTypeToRdfLiteral() throws Exception {
        assertThat(elementFactory.createLiteral(new BigDecimal(12)).getDatatypeURI(), equalTo(XSD.DECIMAL));
        assertThat(elementFactory.createLiteral(1f).getDatatypeURI(), equalTo(XSD.FLOAT));
        assertThat(elementFactory.createLiteral(new Float(12)).getDatatypeURI(), equalTo(XSD.FLOAT));
        assertThat(elementFactory.createLiteral(1d).getDatatypeURI(), equalTo(XSD.DOUBLE));
        assertThat(elementFactory.createLiteral(new Double(12)).getDatatypeURI(), equalTo(XSD.DOUBLE));
        assertThat(elementFactory.createLiteral(new BigInteger("1")).getDatatypeURI(), equalTo(XSD.INTEGER));
        assertThat(elementFactory.createLiteral(new BigInteger("-1")).getDatatypeURI(), equalTo(XSD.INTEGER));
        assertThat(elementFactory.createLiteral(1L).getDatatypeURI(), equalTo(XSD.LONG));
        assertThat(elementFactory.createLiteral(new Long(12)).getDatatypeURI(), equalTo(XSD.LONG));
        assertThat(elementFactory.createLiteral(1).getDatatypeURI(), equalTo(XSD.INT));
        assertThat(elementFactory.createLiteral(new Integer(12)).getDatatypeURI(), equalTo(XSD.INT));
        assertThat(elementFactory.createLiteral((short) 1).getDatatypeURI(), equalTo(XSD.SHORT));
        assertThat(elementFactory.createLiteral(new Short((short) 12)).getDatatypeURI(), equalTo(XSD.SHORT));
        assertThat(elementFactory.createLiteral((byte) 1).getDatatypeURI(), equalTo(XSD.BYTE));
        assertThat(elementFactory.createLiteral(new Byte((byte) 12)).getDatatypeURI(), equalTo(XSD.BYTE));
    }

    @Test
    public void missingMappingsThatMightLeadToConfusion() throws Exception {
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        expectCreationToFail(datatypeFactory.newDuration(1000));
        expectCreationToFail(datatypeFactory.newXMLGregorianCalendar());
    }

    private void expectCreationToFail(final Object object) {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                Literal literal = elementFactory.createLiteral(object);
            }
        });
    }

    /**
     * Tests that each of the createResource methods work as expected.
     *
     * @throws Exception if query fails when it should have succeeded
     */
    @Test
    public void testCreateTriples() throws Exception {
        BlankNode blank1 = elementFactory.createBlankNode();
        BlankNode blank2 = elementFactory.createBlankNode();

        URI uri1 = new URI("http://namespace#somevalue");
        URI uri2 = new URI("http://namespace#someothervalue");
        URIReference ref1 = elementFactory.createURIReference(uri1);
        URIReference ref2 = elementFactory.createURIReference(uri2);

        Literal l1 = elementFactory.createLiteral(TEST_STR_1);

        // test ordinary creation
        Triple triple = tripleFactory.createTriple(blank1, ref1, blank2);
        assertThat(triple.getSubject(), equalTo((SubjectNode) blank1));
        assertThat(triple.getPredicate(), equalTo((PredicateNode) ref1));
        assertThat(triple.getObject(), equalTo((ObjectNode) blank2));

        // test inequality, particularly against differing blank nodes
        Triple triple2 = tripleFactory.createTriple(blank2, ref1, blank2);
        assertThat(triple2, not(equalTo(triple)));

        // test equality
        triple2 = tripleFactory.createTriple(blank1, ref1, blank2);
        assertThat(triple2, equalTo(triple));

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
        Literal l3 = elementFactory.createLiteral(TEST_STR_1);
        assertThat(ref1, equalTo(ref4));
        assertThat(ref2, equalTo(ref5));
        assertThat(l3, equalTo(l1));
        assertThat(l3.getEscapedForm(), equalTo(l1.getEscapedForm()));
        assertThat(graph, containsTriple(ref4, ref5, blank1));

        ClosableIterable<Triple> triples = graph.find(ref2, ref1, ANY_OBJECT_NODE);
        for (Triple aTriple : triples) {
            assertThat(aTriple.getObject(), equalTo((ObjectNode) l1));
            assertThat(aTriple.getObject().hashCode(), equalTo(l1.hashCode()));
            assertThat(aTriple.getObject(), equalTo((ObjectNode) l3));
            assertThat(aTriple.getObject().hashCode(), equalTo(l3.hashCode()));
        }

        assertThat(graph.find(ref2, ref1, l1).iterator().hasNext(), is(true));
        assertThat(graph.contains(ref2, ref1, l1), is(true));
        assertThat(graph.find(ref5, ref4, l3).iterator().hasNext(), is(true));
        assertThat(graph.contains(ref5, ref4, l3), is(true));
    }

    /**
     * Tests that objects are always localized before testing.
     *
     * @throws Exception if query fails when it should have succeeded
     */
    @Test
    public void testTwoGraphs() throws Exception {
        Graph g1 = newGraph();
        final Graph g2 = newGraph();

        URI uri1 = new URI("http://namespace#somevalue1");
        URI uri2 = new URI("http://namespace#somevalue2");
        URI uri3 = new URI("http://namespace#foo");

        GraphElementFactory gef1 = g1.getElementFactory();
        URIReference g1u1 = gef1.createURIReference(uri1);
        final URIReference g1u2 = gef1.createURIReference(uri2);
        URIReference g1u3 = gef1.createURIReference(uri3);
        final BlankNode g1b1 = gef1.createBlankNode();

        GraphElementFactory gef2 = g2.getElementFactory();
        Literal g2l1 = gef2.createLiteral(TEST_STR_1);
        Literal g2l2 = gef2.createLiteral(TEST_STR_2);
        URIReference g2u1 = gef2.createURIReference(uri2);

        g2.add(new TripleImpl(g1u1, g1u1, g2l1));
        assertThat(g2, containsTriple(g1u1, g1u1, g2l1));
        g2.add(g2u1, g1u1, g2l1);
        assertThat(g2, containsTriple(g2u1, g1u1, g2l1));
        g2.add(g2u1, g1u1, g2l2);
        assertThat(g2, containsTriple(g2u1, g1u1, g2l2));
        g2.add(g2u1, g1u2, g1u2);
        assertThat(g2, containsTriple(g2u1, g1u2, g1u2));

        assertThrows(ExternalBlankNodeException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                g2.add(g1b1, g1u2, g1b1);
            }
        });
        assertThat(g2, not(containsTriple(g1b1, g1u2, g1b1)));

        // Test inserting a predicate and object that come from another graph but
        // do exist.
        assertThat("Should contain the statement", g2, containsTriple(g2u1, g2u1, g2u1));

        // Test inserting a statements using objects from the correct graph and then
        // using find and contains with the same, by value, object from another.
        URIReference g2u3 = gef2.createURIReference(uri3);
        g2.add(g2u3, g2u3, g2u3);

        assertThat("Contains should work by value", g2.contains(g1u3, g1u3, g1u3), is(true));
        assertThat("Find should work by value", g2.find(g1u3, g1u3, g1u3).iterator().hasNext(), is(true));

        // Test the find(<foo>, *, *) works.
        ClosableIterator<Triple> iter = g2.find(g2u3, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).iterator();
        assertThat("Should get back at least one result", iter.hasNext(), is(true));

        // Test the find(*, *, *) works.
        iter = g2.find(ANY_SUBJECT_NODE, ANY_PREDICATE_NODE, ANY_OBJECT_NODE).iterator();
        assertThat("Should get back at least one result", iter.hasNext(), is(true));
    }
}
