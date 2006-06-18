/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2005 The JRDF Project.  All rights reserved.
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

package org.jrdf.sparql;

import junit.framework.TestCase;
import org.jrdf.connection.JrdfConnectionFactory;
import org.jrdf.util.test.SparqlQueryTestUtil;
import org.jrdf.util.test.TripleTestUtil;
import org.jrdf.util.test.MockTestUtil;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.TripleFactoryException;
import org.jrdf.graph.Triple;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.URIReference;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Literal;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.Answer;
import org.jrdf.query.relation.AttributeValuePairComparator;
import org.jrdf.TestJRDFFactory;

import java.net.URI;
import java.util.List;

/**
 * Integration test for {@link SparqlConnectionImpl}.
 */
public final class DefaultSparqlConnectionIntegrationTest extends TestCase {

    // FIXME TJA: Add test that ensures that createConnection() returns a new connection each time.

    private static final URI NO_SECURITY_DOMAIN = JrdfConnectionFactory.NO_SECURITY_DOMAIN;
    private static final String QUERY_SHOULD_RETURN_ONE_SOLUTION = SparqlQueryTestUtil.QUERY_BOOK_1_DC_TITLE;
    private static final String QUERY_SHOULD_RETURN_NOTHING = SparqlQueryTestUtil.QUERY_BOOK_2_DC_TITLE;
    private static final URI URI_SUBJECT = TripleTestUtil.URI_BOOK_1;
    private static final URI URI_PREDICATE = TripleTestUtil.URI_DC_TITLE;
    private static final String LITERAL_TITLE = TripleTestUtil.LITERAL_BOOK_TITLE;
    private static final AttributeValuePairComparator avpComparator =
            MockTestUtil.createFromInterface(AttributeValuePairComparator.class);

    public void testCreateSparqlConnection() {
        checkConnectionReturnsOneSolution(createRawConnection());
        checkConnectionReturnsOneSolution(createConnectionFromFactory());
        checkConnectionReturnsNoSolutions(createRawConnection());
    }

    private SparqlConnection createRawConnection() {
        return new SparqlConnectionImpl(createGraph(), NO_SECURITY_DOMAIN, avpComparator);
    }

    private SparqlConnection createConnectionFromFactory() {
        return new JrdfConnectionFactory().createSparqlConnection(createGraph(), NO_SECURITY_DOMAIN, avpComparator);
    }

    private void checkConnectionReturnsOneSolution(SparqlConnection connection) {
        Answer answer = executeQuery(connection, QUERY_SHOULD_RETURN_ONE_SOLUTION);
        checkFirstRowOfAnswer(answer);
    }

    private void checkConnectionReturnsNoSolutions(SparqlConnection connection) {
        Answer answer = executeQuery(connection, QUERY_SHOULD_RETURN_NOTHING);
        List<Triple> solutions = answer.getSolutions();
        assertTrue(solutions.isEmpty());
    }

    private Answer executeQuery(SparqlConnection connection, String query) {
        try {
            return connection.executeQuery(query);
        } catch (InvalidQuerySyntaxException e) {
            throw new RuntimeException(e);
        } catch (GraphException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkFirstRowOfAnswer(Answer answer) {
        List<Triple> solutions = answer.getSolutions();
        Triple triple = solutions.iterator().next();
        checkSubject(triple);
        checkPredicate(triple);
        checkLiteralObject(triple);
    }

    // TODO AN Why do we need to call toString should they be equal?
    private void checkSubject(Triple triple) {
        assertEquals(URI_SUBJECT.toString(), triple.getSubject().toString());
    }

    // TODO AN Why do we need to call toString should they be equal?
    private void checkPredicate(Triple triple) {
        assertEquals(URI_PREDICATE.toString(), triple.getPredicate().toString());
    }

    private void checkLiteralObject(Triple triple) {
        Literal object = (Literal) triple.getObject();
        assertEquals(LITERAL_TITLE, object.getLexicalForm());
    }

    private Graph createGraph() {
        Graph graph = TestJRDFFactory.getNewGraph();
        populateGraph(graph);
        return graph;
    }

    private void populateGraph(Graph graph) {
        try {
            graph.add(createTriple(graph));
        } catch (GraphException e) {
            // FIXME TJA: Remove stack trace.
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Triple createTriple(Graph graph) {
        SubjectNode subject = createResource(graph, URI_SUBJECT);
        PredicateNode predicate = createResource(graph, URI_PREDICATE);
        ObjectNode object = createLiteral(graph, LITERAL_TITLE);
        return createTriple(graph, subject, predicate, object);
    }

    private Triple createTriple(Graph graph, SubjectNode subject, PredicateNode predicate, ObjectNode object) {
        try {
            return graph.getTripleFactory().createTriple(subject, predicate, object);
        } catch (TripleFactoryException e) {
            throw new RuntimeException(e);
        }
    }

    private URIReference createResource(Graph graph, URI uri) {
        try {
            return graph.getElementFactory().createResource(uri);
        } catch (GraphElementFactoryException e) {
            throw new RuntimeException(e);
        }
    }

    private ObjectNode createLiteral(Graph graph, String literal) {
        try {
            return graph.getElementFactory().createLiteral(literal);
        } catch (GraphElementFactoryException e) {
            throw new RuntimeException(e);
        }
    }
}
