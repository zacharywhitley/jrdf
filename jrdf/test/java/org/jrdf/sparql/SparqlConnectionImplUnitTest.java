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
import org.jrdf.TestJRDFFactory;
import org.jrdf.connection.JrdfConnectionFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.GraphException;
import org.jrdf.graph.Triple;
import org.jrdf.query.Answer;
import org.jrdf.query.GraphFixture;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.MockGraph;
import org.jrdf.query.QueryBuilder;
import org.jrdf.query.relation.AttributeValuePairComparator;
import org.jrdf.util.param.ParameterTestUtil;
import org.jrdf.util.test.AssertThrows;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.util.test.MockFactory;
import org.jrdf.util.test.MockTestUtil;
import org.jrdf.util.test.ReflectTestUtil;
import org.jrdf.util.test.SparqlQueryTestUtil;
import org.jrdf.util.test.TripleTestUtil;

import java.lang.reflect.Modifier;
import java.net.URI;

/**
 * Unit test for {@link SparqlConnectionImpl}.
 *
 * @author Tom Adams
 * @version $Id$
 */
public class SparqlConnectionImplUnitTest extends TestCase {

    private static final MockGraph GRAPH_GOOD = GraphFixture.createGraph();
    private static final URI NO_SECURITY_DOMAIN = JrdfConnectionFactory.NO_SECURITY_DOMAIN;
    private static final String EXECUTE_QUERY_METHOD = "executeQuery";
    private static final String NULL = ParameterTestUtil.NULL_STRING;
    private static final String EMPTY_STRING = ParameterTestUtil.EMPTY_STRING;
    private static final String SINGLE_SPACE = ParameterTestUtil.SINGLE_SPACE;
    private static final String QUERY_ITQL = "select $s $p $o from <rmi://localhost/server1#> where $s $p $o ;";
    private static final String FIELD_BUILDER = "builder";
    private static final AttributeValuePairComparator avpComparator =
            TestJRDFFactory.getNewAttributeValuePairComparator();
    private static final Graph NO_EXPECTATIONS_GRAPH = MockTestUtil.createFromInterface(Graph.class);
    private MockFactory factory;

    public void setUp() {
        factory = new MockFactory();
    }

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkExtensionOf(SparqlConnection.class, SparqlConnectionImpl.class);
        ClassPropertiesTestUtil.checkConstructor(SparqlConnectionImpl.class, Modifier.PUBLIC, Graph.class,
                URI.class, AttributeValuePairComparator.class);
    }

    public void testNullSessionInConstructor() {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                new SparqlConnectionImpl(null, NO_SECURITY_DOMAIN, avpComparator);
            }
        });
    }

    public void testNullSecurityDomainInConstructor() {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                new SparqlConnectionImpl(NO_EXPECTATIONS_GRAPH, null, avpComparator);
            }
        });
    }

    public void testNullAvpComparator() {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                new SparqlConnectionImpl(NO_EXPECTATIONS_GRAPH, NO_SECURITY_DOMAIN, null);
            }
        });
    }

    public void testClose() {
        new SparqlConnectionImpl(GRAPH_GOOD, NO_SECURITY_DOMAIN, avpComparator).close();
    }

    public void testExecuteSimpleBadQuery() throws Exception {
        SparqlConnection connection = new SparqlConnectionImpl(NO_EXPECTATIONS_GRAPH, NO_SECURITY_DOMAIN, avpComparator);
        checkBadParam(connection, EXECUTE_QUERY_METHOD, NULL);
        checkBadParam(connection, EXECUTE_QUERY_METHOD, EMPTY_STRING);
        checkBadParam(connection, EXECUTE_QUERY_METHOD, SINGLE_SPACE);
    }

    public void testGraphExceptionPassthrough() throws Exception {
        final Graph graph = createGraph();
        AssertThrows.assertThrows(GraphException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                factory.replay();
                SparqlConnection connection = new SparqlConnectionImpl(graph, NO_SECURITY_DOMAIN, avpComparator);
                connection.executeQuery(SparqlQueryTestUtil.QUERY_BOOK_1_DC_TITLE);
                factory.verify();
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

    private Graph createGraph() throws Exception {
        IMocksControl control = factory.createControl();
        final Graph graph = control.createMock(Graph.class);
        Triple triple = TripleTestUtil.TRIPLE_BOOK_1_DC_TITLE_VARIABLE;
        graph.find(triple);
        control.andThrow(new GraphException(""));
        return graph;
    }

    private SparqlConnection createConnection(Graph graph, QueryBuilder builder) {
        SparqlConnection connection = new SparqlConnectionImpl(graph, NO_SECURITY_DOMAIN, avpComparator);
        ReflectTestUtil.insertFieldValue(connection, FIELD_BUILDER, builder);
        return connection;
    }

    private void checkBadParam(SparqlConnection connection, String method, String param) throws Exception {
        ParameterTestUtil.checkBadStringParam(connection, method, param);
    }
}
