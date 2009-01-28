/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

package org.jrdf.urql;

import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.Query;
import org.jrdf.query.answer.Answer;
import static org.jrdf.query.answer.EmptyAnswer.EMPTY_ANSWER;
import org.jrdf.query.execute.QueryEngine;
import org.jrdf.urql.builder.QueryBuilder;
import org.jrdf.util.param.ParameterTestUtil;
import static org.jrdf.util.param.ParameterTestUtil.NULL_STRING;
import org.jrdf.util.test.ArgumentTestUtil;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.AssertThrows.assertThrows;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.util.test.MockFactory;
import org.jrdf.util.test.ParameterDefinition;
import static org.jrdf.util.test.ReflectTestUtil.insertFieldValue;

import java.lang.reflect.Modifier;

public class SparqlConnectionImplUnitTest extends TestCase {
    private static final MockFactory FACTORY = new MockFactory();
    private static final String NULL = NULL_STRING;
    private static final String EMPTY_STRING = ParameterTestUtil.EMPTY_STRING;
    private static final String SINGLE_SPACE = ParameterTestUtil.SINGLE_SPACE;
    private static final String QUERY_ITQL = "select { s, p, o } from <rmi://localhost/server1#> where s p o ;";
    private static final String FIELD_BUILDER = "builder";
    private static final QueryBuilder BUILDER = FACTORY.createMock(QueryBuilder.class);
    private static final Answer ANSWER = FACTORY.createMock(Answer.class);
    private static final Graph GRAPH = FACTORY.createMock(Graph.class);
    private static final QueryEngine QUERY_ENGINE = FACTORY.createMock(QueryEngine.class);
    private static final String METHOD_NAME = "executeQuery";
    private static final Class[] PARAM_TYPES = {QueryBuilder.class, QueryEngine.class};
    private static final String[] METHOD_PARAM_NAMES = {"graph", "queryText"};
    private static final Class[] METHOD_PARAM_TYPES = {Graph.class, String.class};
    private static final ParameterDefinition PARAM_DEFINITION = new ParameterDefinition(METHOD_PARAM_NAMES,
        METHOD_PARAM_TYPES);
    private static final InvalidQuerySyntaxException INVALID_QUERY_EXCEPTION = new InvalidQuerySyntaxException("");

    public void setUp() {
        FACTORY.reset();
    }

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkExtensionOf(UrqlConnection.class, UrqlConnectionImpl.class);
        ClassPropertiesTestUtil.checkConstructor(UrqlConnectionImpl.class, Modifier.PUBLIC, QueryBuilder.class,
            QueryEngine.class);
    }

    public void testNullsInConstructor() {
        ArgumentTestUtil.checkConstructNullAssertion(UrqlConnectionImpl.class, PARAM_TYPES);
    }

    public void testCreateSparqlConnectionNullContract() {
        UrqlConnection sparqlConnection = createSparqlConnection();
        ArgumentTestUtil.checkMethodNullAssertions(sparqlConnection, METHOD_NAME, PARAM_DEFINITION);
    }

    public void testExecuteSimpleBadQuery() throws Exception {
        UrqlConnection connection = createSparqlConnection();
        checkBadParam(connection, NULL);
        checkBadParam(connection, EMPTY_STRING);
        checkBadParam(connection, SINGLE_SPACE);
    }

    public void testExecuteQuery() throws Exception {
        QueryEngine queryEngine = createQueryEngine();
        expect(GRAPH.isEmpty()).andReturn(false);
        Query query = createQuery(GRAPH, queryEngine);
        QueryBuilder builder = createBuilder(GRAPH, QUERY_ITQL, query);
        UrqlConnection connection = new UrqlConnectionImpl(builder, queryEngine);
        FACTORY.replay();
        Answer answer = connection.executeQuery(GRAPH, QUERY_ITQL);
        FACTORY.verify();
        assertEquals(ANSWER, answer);
    }

    public void testEmptyGraph() throws Exception {
        QueryEngine queryEngine = createQueryEngine();
        Graph graph = createEmptyGraph();
        Query query = FACTORY.createMock(Query.class);
        QueryBuilder builder = createBuilder(graph, QUERY_ITQL, query);
        UrqlConnection connection = new UrqlConnectionImpl(builder, queryEngine);
        FACTORY.replay();
        Answer answer = connection.executeQuery(graph, QUERY_ITQL);
        FACTORY.verify();
        assertEquals(EMPTY_ANSWER, answer);
    }

    public void testGraphExceptionPassthrough() throws Exception {
        assertThrows(GraphException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                QueryBuilder builder = createBuilderThrowsException(GRAPH, QUERY_ITQL, new GraphException(""));
                final UrqlConnection connection = createConnection(builder);
                FACTORY.replay();
                connection.executeQuery(GRAPH, QUERY_ITQL);
                FACTORY.verify();
            }
        });
    }

    public void testInvalidQueryExceptionPassthrough() {
        assertThrows(InvalidQuerySyntaxException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                QueryBuilder builder = createBuilderThrowsException(GRAPH, QUERY_ITQL, INVALID_QUERY_EXCEPTION);
                UrqlConnection connection = createConnection(builder);
                FACTORY.replay();
                connection.executeQuery(GRAPH, QUERY_ITQL);
                FACTORY.verify();
            }
        });
    }

    private Graph createEmptyGraph() throws GraphException {
        Graph graph = FACTORY.createMock(Graph.class);
        graph.isEmpty();
        expectLastCall().andReturn(true);
        return graph;
    }

    private Query createQuery(Graph graph, QueryEngine queryEngine) {
        Query query = FACTORY.createMock(Query.class);
        query.executeQuery(graph, queryEngine);
        expectLastCall().andReturn(ANSWER);
        return query;
    }

    private QueryEngine createQueryEngine() {
        return FACTORY.createMock(QueryEngine.class);
    }

    private UrqlConnection createConnection(QueryBuilder builder) {
        UrqlConnection connection = createSparqlConnection();
        insertFieldValue(connection, FIELD_BUILDER, builder);
        return connection;
    }

    private void checkBadParam(final UrqlConnection connection, final String param) throws Exception {
        assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                connection.executeQuery(GRAPH, param);
            }
        });
    }

    private QueryBuilder createBuilder(Graph graph, String queryString, Query query) throws Exception {
        QueryBuilder builder = FACTORY.createMock(QueryBuilder.class);
        builder.buildQuery(graph, queryString);
        expectLastCall().andReturn(query);
        return builder;
    }

    private QueryBuilder createBuilderThrowsException(Graph graph, String queryText, Exception e) throws Exception {
        QueryBuilder builder = FACTORY.createMock(QueryBuilder.class);
        builder.buildQuery(graph, queryText);
        expectLastCall().andThrow(e);
        return builder;
    }

    private UrqlConnectionImpl createSparqlConnection() {
        return new UrqlConnectionImpl(BUILDER, QUERY_ENGINE);
    }
}
