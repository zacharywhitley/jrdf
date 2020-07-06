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

package org.jrdf.sparql.builder;

import static org.easymock.EasyMock.expectLastCall;
import org.jrdf.graph.Graph;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.Query;
import org.jrdf.sparql.parser.SparqlParser;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import static org.jrdf.util.test.ArgumentTestUtil.checkMethodNullAndEmptyAssertions;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import org.jrdf.util.test.ParameterDefinition;
import org.jrdf.util.test.SparqlQueryTestUtil;
import static org.jrdf.util.test.MockTestUtil.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Modifier;

/**
 * Unit test for {@link SparqlQueryBuilder}.
 *
 * @author Tom Adams
 * @version $Id: SparqlQueryBuilderUnitTest.java 921 2006-10-31 09:52:43Z newmana $
 */
@RunWith(PowerMockRunner.class)
public class SparqlQueryBuilderUnitTest {
    private static final String QUERY_GOOD = SparqlQueryTestUtil.QUERY_BOOK_1_DC_TITLE;
    private static final Class[] CONSTRUCTOR_PARAM_TYPES = {SparqlParser.class};
    private static final String[] PARAM_NAMES = {"graph", "queryText"};
    private static final Class[] PARAM_TYPES = {Graph.class, String.class};
    private static final ParameterDefinition BUILD_PARAM_DEFINITION = new ParameterDefinition(PARAM_NAMES, PARAM_TYPES);
    @Mock private Graph graph;
    @Mock private SparqlParser sparqlParser;

    @Test
    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(QueryBuilder.class, SparqlQueryBuilder.class);
        checkConstructor(SparqlQueryBuilder.class, Modifier.PUBLIC, SparqlParser.class);
    }

    @Test
    public void testNullsInConstructor() {
        checkConstructNullAssertion(SparqlQueryBuilder.class, CONSTRUCTOR_PARAM_TYPES);
    }

    @Test
    public void testBadParams() throws Exception {
        SparqlQueryBuilder builder = new SparqlQueryBuilder(sparqlParser);
        checkMethodNullAndEmptyAssertions(builder, "buildQuery", BUILD_PARAM_DEFINITION);
    }

    @Test
    public void testExceptionPassthroughFromParser() {
        AssertThrows.assertThrows(InvalidQuerySyntaxException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                SparqlParser parser = createParserThrowsException();
                QueryBuilder builder = new SparqlQueryBuilder(parser);
                replayAll();
                builder.buildQuery(graph, QUERY_GOOD);
                verifyAll();
            }
        });
    }

    @Test
    public void testBuildQuery() throws Exception {
        SparqlParser parser = createParser();
        QueryBuilder builder = new SparqlQueryBuilder(parser);
        replayAll();
        builder.buildQuery(graph, QUERY_GOOD);
        verifyAll();
    }

    private SparqlParser createParserThrowsException() throws Exception {
        SparqlParser parser = createMock(SparqlParser.class);
        parser.parseQuery(graph, QUERY_GOOD);
        expectLastCall().andThrow(new InvalidQuerySyntaxException(""));
        return parser;
    }

    private SparqlParser createParser() throws Exception {
        SparqlParser parser = createMock(SparqlParser.class);
        parser.parseQuery(graph, QUERY_GOOD);
        expectLastCall().andReturn(createMock(Query.class));
        return parser;
    }
}
