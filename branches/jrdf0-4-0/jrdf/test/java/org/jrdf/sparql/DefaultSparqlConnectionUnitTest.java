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
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.query.Answer;
import org.jrdf.query.GraphFixture;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.MockBadGraph;
import org.jrdf.query.MockGraph;
import org.jrdf.query.QueryBuilder;
import org.jrdf.util.param.ParameterTestUtil;
import org.jrdf.util.test.AssertThrows;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.util.test.ReflectTestUtil;
import org.jrdf.util.test.SparqlQueryTestUtil;
import org.jrdf.util.test.TripleTestUtil;

import java.lang.reflect.Modifier;
import java.net.URI;

/**
 * Unit test for {@link DefaultSparqlConnection}.
 *
 * @author Tom Adams
 * @version $Id$
 */
public class DefaultSparqlConnectionUnitTest extends TestCase {

    private static final MockGraph GRAPH_GOOD = GraphFixture.createGraph();
    private static final URI NO_SECURITY_DOMAIN = JrdfConnectionFactory.NO_SECURITY_DOMAIN;
    private static final String EXECUTE_QUERY_METHOD = "executeQuery";
    private static final String NULL = ParameterTestUtil.NULL_STRING;
    private static final String EMPTY_STRING = ParameterTestUtil.EMPTY_STRING;
    private static final String SINGLE_SPACE = ParameterTestUtil.SINGLE_SPACE;
    private static final String QUERY_ITQL = "select $s $p $o from <rmi://localhost/server1#> where $s $p $o ;";
    private static final String FIELD_BUILDER = "builder";

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkExtensionOf(SparqlConnection.class, DefaultSparqlConnection.class);
        ClassPropertiesTestUtil.checkConstructor(DefaultSparqlConnection.class, Modifier.PUBLIC, Graph.class,
                URI.class);
    }

    public void testNullSessionInConstructor() {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                new DefaultSparqlConnection(null, NO_SECURITY_DOMAIN);
            }
        });
    }

    public void testNullSesurityDomainInConstructor() {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                new DefaultSparqlConnection(createBadGraph(), null);
            }
        });
    }

    public void testClose() {
        new DefaultSparqlConnection(GRAPH_GOOD, NO_SECURITY_DOMAIN).close();
    }

    public void testExecuteSimpleBadQuery() throws Exception {
        SparqlConnection connection = new DefaultSparqlConnection(createBadGraph(), NO_SECURITY_DOMAIN);
        checkBadParam(connection, EXECUTE_QUERY_METHOD, NULL);
        checkBadParam(connection, EXECUTE_QUERY_METHOD, EMPTY_STRING);
        checkBadParam(connection, EXECUTE_QUERY_METHOD, SINGLE_SPACE);
    }

    public void testGraphExceptionPassthrough() {
        AssertThrows.assertThrows(GraphException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                SparqlConnection connection = new DefaultSparqlConnection(createBadGraph(), NO_SECURITY_DOMAIN);
                connection.executeQuery(SparqlQueryTestUtil.QUERY_BOOK_1_DC_TITLE);
            }
        });
    }

    public void testInvalidQueryExceptionPassthrough() {
        AssertThrows.assertThrows(InvalidQuerySyntaxException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                SparqlConnection connection = createConnection(GRAPH_GOOD, new BadQueryBuilder());
                connection.executeQuery(SparqlQueryTestUtil.QUERY_BOOK_1_DC_TITLE);
            }
        });
    }

    public void testExecuteQuery() throws InvalidQuerySyntaxException, GraphException {
        SparqlConnection connection = createConnection(GRAPH_GOOD, new MockQueryBuilder());
        Answer answer = connection.executeQuery(QUERY_ITQL);
        GraphFixture.checkAnswer(TripleTestUtil.TRIPLE_BOOK_1_DC_SUBJECT_LITERAL, answer);
    }

    private SparqlConnection createConnection(Graph graph, QueryBuilder builder) {
        SparqlConnection connection = new DefaultSparqlConnection(graph, NO_SECURITY_DOMAIN);
        ReflectTestUtil.insertFieldValue(connection, FIELD_BUILDER, builder);
        return connection;
    }

    private MockBadGraph createBadGraph() {
        return new MockBadGraph();
    }

    private void checkBadParam(SparqlConnection connection, String method, String param) throws Exception {
        ParameterTestUtil.checkBadStringParam(connection, method, param);
    }
}
