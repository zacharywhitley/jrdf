/*
 * $Header$
 * $Revision: 921 $
 * $Date: 2006-10-31 19:52:43 +1000 (Tue, 31 Oct 2006) $
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

package org.jrdf.sparql.builder;

import junit.framework.TestCase;
import org.easymock.classextension.IMocksControl;
import org.jrdf.graph.Graph;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.Query;
import org.jrdf.sparql.parser.SparqlParser;
import org.jrdf.util.param.ParameterTestUtil;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import org.jrdf.util.test.MockFactory;
import org.jrdf.util.test.SparqlQueryTestUtil;

import java.lang.reflect.Modifier;

/**
 * Unit test for {@link org.jrdf.sparql.builder.SparqlQueryBuilder}.
 *
 * @author Tom Adams
 * @version $Id: DrqlQueryBuilderUnitTest.java 921 2006-10-31 09:52:43Z newmana $
 */
public class DrqlQueryBuilderUnitTest extends TestCase {
    private static final MockFactory factory = new MockFactory();
    private static final SparqlParser Drql_PARSER = factory.createMock(SparqlParser.class);
    private static final String NULL_STRING = ParameterTestUtil.NULL_STRING;
    private static final String EMPTY_STRING = ParameterTestUtil.EMPTY_STRING;
    private static final String SINGLE_SPACE = ParameterTestUtil.SINGLE_SPACE;
    private static final String QUERY_GOOD = SparqlQueryTestUtil.QUERY_BOOK_1_DC_TITLE;
    private static final Graph GRAPH = factory.createMock(Graph.class);
    private static final String CANNOT_BE_NULL = "queryText cannot be null";
    private static final String CANNOT_BE_EMPTY = "queryText cannot be the empty string";
    private MockFactory mockFactory;
    private static final Class[] PARAM_TYPES = {SparqlParser.class};

    public void setUp() {
        mockFactory = new MockFactory();
    }

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(QueryBuilder.class, SparqlQueryBuilder.class);
        checkConstructor(SparqlQueryBuilder.class, Modifier.PUBLIC, SparqlParser.class);
    }

    public void testNullsInConstructor() {
        checkConstructNullAssertion(SparqlQueryBuilder.class, PARAM_TYPES);
    }

    public void testBadParams() throws Exception {
        SparqlQueryBuilder builder = new SparqlQueryBuilder(Drql_PARSER);
        checkBadParam(builder, NULL_STRING, CANNOT_BE_NULL);
        checkBadParam(builder, EMPTY_STRING, CANNOT_BE_EMPTY);
        checkBadParam(builder, SINGLE_SPACE, CANNOT_BE_EMPTY);
    }

    public void testExceptionPassthroughFromParser() {
        AssertThrows.assertThrows(InvalidQuerySyntaxException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                SparqlParser parser = createParserThrowsException();
                QueryBuilder builder = new SparqlQueryBuilder(parser);
                mockFactory.replay();
                builder.buildQuery(GRAPH, QUERY_GOOD);
                mockFactory.verify();
            }
        });
    }

    public void testBuildQuery() throws Exception {
        SparqlParser parser = createParser();
        QueryBuilder builder = new SparqlQueryBuilder(parser);
        mockFactory.replay();
        builder.buildQuery(GRAPH, QUERY_GOOD);
        mockFactory.verify();
    }

    private void checkBadParam(final SparqlQueryBuilder builder, final String param, String message) throws Exception {
        AssertThrows.assertThrows(IllegalArgumentException.class, message, new AssertThrows.Block() {
            public void execute() throws Throwable {
                builder.buildQuery(GRAPH, param);
            }
        });
    }

    private SparqlParser createParserThrowsException() throws Exception {
        IMocksControl control = mockFactory.createControl();
        SparqlParser parser = control.createMock(SparqlParser.class);
        parser.parseQuery(GRAPH, QUERY_GOOD);
        control.andThrow(new InvalidQuerySyntaxException(""));
        return parser;
    }

    @SuppressWarnings({"unchecked"})
    private SparqlParser createParser() throws Exception {
        IMocksControl control = mockFactory.createControl();
        SparqlParser parser = control.createMock(SparqlParser.class);
        parser.parseQuery(GRAPH, QUERY_GOOD);
        control.andReturn(factory.createMock(Query.class));
        return parser;
    }
}
