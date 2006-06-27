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
import org.easymock.IMocksControl;
import org.jrdf.connection.JrdfConnectionFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.JrdfQueryExecutor;
import org.jrdf.query.JrdfQueryExecutorFactory;
import org.jrdf.query.Query;
import org.jrdf.query.QueryBuilder;
import org.jrdf.query.relation.Relation;
import org.jrdf.util.param.ParameterTestUtil;
import org.jrdf.util.test.AssertThrows;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.util.test.MockFactory;
import org.jrdf.util.test.MockTestUtil;
import org.jrdf.util.test.ReflectTestUtil;
import org.jrdf.util.test.SparqlQueryTestUtil;
import org.jrdf.util.test.ArgumentTestUtil;
import org.jrdf.util.test.ParameterDefinition;

import java.lang.reflect.Modifier;
import java.net.URL;

/**
 * Unit test for {@link SparqlConnectionImpl}.
 *
 * @author Tom Adams
 * @version $Id$
 */
public class SparqlConnectionImplUnitTest extends TestCase {

    private static final URL NO_SECURITY_DOMAIN = JrdfConnectionFactory.NO_SECURITY_DOMAIN_URL;
    private static final String NULL = ParameterTestUtil.NULL_STRING;
    private static final String EMPTY_STRING = ParameterTestUtil.EMPTY_STRING;
    private static final String SINGLE_SPACE = ParameterTestUtil.SINGLE_SPACE;
    private static final String QUERY_ITQL = "select $s $p $o from <rmi://localhost/server1#> where $s $p $o ;";
    private static final String FIELD_BUILDER = "builder";
    private static final JrdfQueryExecutorFactory EXECUTOR_FACTORY
            = MockTestUtil.createMock(JrdfQueryExecutorFactory.class);
    private static final QueryBuilder BUILDER = MockTestUtil.createMock(QueryBuilder.class);
    private static final Relation ANSWER = MockTestUtil.createMock(Relation.class);
    private MockFactory factory;
    private static final String TEST_QUERY_1 = SparqlQueryTestUtil.QUERY_BOOK_1_DC_TITLE;
    private static final Query QUERY = MockTestUtil.createMock(Query.class);
    private static final Graph GRAPH = MockTestUtil.createMock(Graph.class);
    private static final String[] PARAMETER_NAMES = {"graph", "queryText"};
    private static final Class[] PARAMETER_TYPES = {Graph.class, String.class};
    private static final ParameterDefinition PARAM_DEFINITION = new ParameterDefinition(PARAMETER_NAMES,
            PARAMETER_TYPES);
    private static final String METHOD_NAME = "executeQuery";

    public void setUp() {
        factory = new MockFactory();
    }

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkExtensionOf(SparqlConnection.class, SparqlConnectionImpl.class);
        ClassPropertiesTestUtil.checkConstructor(SparqlConnectionImpl.class, Modifier.PUBLIC, URL.class,
                QueryBuilder.class, JrdfQueryExecutorFactory.class);
    }

    public void testNullSecurityDomainInConstructor() {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                new SparqlConnectionImpl(null, BUILDER, EXECUTOR_FACTORY);
            }
        });
    }

    public void testNullBuilder() {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                new SparqlConnectionImpl(NO_SECURITY_DOMAIN, null, EXECUTOR_FACTORY);
            }
        });
    }

    public void testNullQueryExecutor() {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                new SparqlConnectionImpl(NO_SECURITY_DOMAIN, BUILDER, null);
            }
        });
    }

    public void testCreateSparqlConnectionNullContract() {
        SparqlConnection sparqlConnection = createSparqlConnection();
        ArgumentTestUtil.checkMethodNullAssertions(PARAM_DEFINITION, sparqlConnection, METHOD_NAME);
    }


    public void testExecuteSimpleBadQuery() throws Exception {
        SparqlConnection connection = createSparqlConnection();
        checkBadParam(connection, NULL);
        checkBadParam(connection, EMPTY_STRING);
        checkBadParam(connection, SINGLE_SPACE);
    }

    public void testExecuteQuery() throws Exception {
        QueryBuilder builder = createBuilder(QUERY_ITQL, QUERY);
        JrdfQueryExecutor executor = createExecutor(QUERY, ANSWER);
        JrdfQueryExecutorFactory executorFactory = createExecutorFactory(executor, GRAPH);
        SparqlConnection connection = new SparqlConnectionImpl(NO_SECURITY_DOMAIN, builder, executorFactory);
        factory.replay();
        Relation answer = connection.executeQuery(GRAPH, QUERY_ITQL);
        factory.verify();
        assertEquals(ANSWER, answer);
    }

    public void testGraphExceptionPassthrough() throws Exception {
        QueryBuilder builder = createBuilder(TEST_QUERY_1, QUERY);
        JrdfQueryExecutor executor = createExecutorThrowsGraphException(QUERY);
        JrdfQueryExecutorFactory executorFactory = createExecutorFactory(executor, GRAPH);
        final SparqlConnection connection = new SparqlConnectionImpl(NO_SECURITY_DOMAIN, builder, executorFactory);
        AssertThrows.assertThrows(GraphException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                factory.replay();
                connection.executeQuery(GRAPH, TEST_QUERY_1);
                factory.verify();
            }
        });
    }

    public void testInvalidQueryExceptionPassthrough() {
        AssertThrows.assertThrows(InvalidQuerySyntaxException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                QueryBuilder builder = createBuilderThrowsException(GRAPH, QUERY_ITQL);
                SparqlConnection connection = createConnection(builder);
                factory.replay();
                connection.executeQuery(GRAPH, QUERY_ITQL);
                factory.verify();
            }
        });
    }

    private SparqlConnection createConnection(QueryBuilder builder) {
        SparqlConnection connection = createSparqlConnection();
        ReflectTestUtil.insertFieldValue(connection, FIELD_BUILDER, builder);
        return connection;
    }

    private void checkBadParam(final SparqlConnection connection, final String param) throws Exception {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                connection.executeQuery(GRAPH, param);
            }
        });
    }

    @SuppressWarnings({ "unchecked" })
    private QueryBuilder createBuilder(String queryString, Query query) throws Exception {
        IMocksControl control = factory.createControl();
        QueryBuilder builder = control.createMock(QueryBuilder.class);
        builder.buildQuery(GRAPH, queryString);
        control.andReturn(query);
        return builder;
    }


    @SuppressWarnings({ "unchecked" })
    private JrdfQueryExecutorFactory createExecutorFactory(JrdfQueryExecutor executor, Graph graph) {
        IMocksControl control = factory.createControl();
        JrdfQueryExecutorFactory executorFactory = control.createMock(JrdfQueryExecutorFactory.class);
        executorFactory.getExecutor(graph);
        control.andReturn(executor);
        return executorFactory;
    }

    @SuppressWarnings({ "unchecked" })
    private JrdfQueryExecutor createExecutor(Query query, Relation answer) throws Exception {
        IMocksControl control = factory.createControl();
        JrdfQueryExecutor executor = control.createMock(JrdfQueryExecutor.class);
        executor.executeQuery(query);
        control.andReturn(answer);
        return executor;
    }

    private JrdfQueryExecutor createExecutorThrowsGraphException(Query query) throws Exception {
        IMocksControl control = factory.createControl();
        JrdfQueryExecutor executor = control.createMock(JrdfQueryExecutor.class);
        executor.executeQuery(query);
        control.andThrow(new GraphException(""));
        return executor;
    }

    private QueryBuilder createBuilderThrowsException(Graph graph, String queryText) throws Exception {
        IMocksControl control = factory.createControl();
        QueryBuilder builder = control.createMock(QueryBuilder.class);
        builder.buildQuery(graph, queryText);
        control.andThrow(new InvalidQuerySyntaxException(""));
        return builder;
    }

    private SparqlConnectionImpl createSparqlConnection() {
        return new SparqlConnectionImpl(NO_SECURITY_DOMAIN, BUILDER, EXECUTOR_FACTORY);
    }
}
