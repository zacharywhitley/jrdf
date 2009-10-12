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

import junit.framework.TestCase;
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
import org.jrdf.util.test.MockFactory;
import org.jrdf.util.test.ParameterDefinition;
import org.jrdf.util.test.SparqlQueryTestUtil;

import java.lang.reflect.Modifier;

/**
 * Unit test for {@link SparqlQueryBuilder}.
 *
 * @author Tom Adams
 * @version $Id: SparqlQueryBuilderUnitTest.java 921 2006-10-31 09:52:43Z newmana $
 */
public class SparqlQueryBuilderUnitTest extends TestCase {
    private static final MockFactory FACTORY = new MockFactory();
    private static final SparqlParser SPARQL_PARSER = FACTORY.createMock(SparqlParser.class);
    private static final String QUERY_GOOD = SparqlQueryTestUtil.QUERY_BOOK_1_DC_TITLE;
    private static final Graph GRAPH = FACTORY.createMock(Graph.class);
    private static final Class[] CONSTRUCTOR_PARAM_TYPES = {SparqlParser.class};
    private static final String[] PARAM_NAMES = {"graph", "queryText"};
    private static final Class[] PARAM_TYPES = {Graph.class, String.class};
    private static final ParameterDefinition BUILD_PARAM_DEFINITION = new ParameterDefinition(PARAM_NAMES, PARAM_TYPES);

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(QueryBuilder.class, SparqlQueryBuilder.class);
        checkConstructor(SparqlQueryBuilder.class, Modifier.PUBLIC, SparqlParser.class);
    }

    public void testNullsInConstructor() {
        checkConstructNullAssertion(SparqlQueryBuilder.class, CONSTRUCTOR_PARAM_TYPES);
    }

    public void testBadParams() throws Exception {
        SparqlQueryBuilder builder = new SparqlQueryBuilder(SPARQL_PARSER);
        checkMethodNullAndEmptyAssertions(builder, "buildQuery", BUILD_PARAM_DEFINITION);
    }

    public void testExceptionPassthroughFromParser() {
        AssertThrows.assertThrows(InvalidQuerySyntaxException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                SparqlParser parser = createParserThrowsException();
                QueryBuilder builder = new SparqlQueryBuilder(parser);
                FACTORY.replay();
                builder.buildQuery(GRAPH, QUERY_GOOD);
                FACTORY.verify();
            }
        });
    }

    public void testBuildQuery() throws Exception {
        FACTORY.reset();
        SparqlParser parser = createParser();
        QueryBuilder builder = new SparqlQueryBuilder(parser);
        FACTORY.replay();
        builder.buildQuery(GRAPH, QUERY_GOOD);
        FACTORY.verify();
    }

    private SparqlParser createParserThrowsException() throws Exception {
        SparqlParser parser = FACTORY.createMock(SparqlParser.class);
        parser.parseQuery(GRAPH, QUERY_GOOD);
        expectLastCall().andThrow(new InvalidQuerySyntaxException(""));
        return parser;
    }

    private SparqlParser createParser() throws Exception {
        SparqlParser parser = FACTORY.createMock(SparqlParser.class);
        parser.parseQuery(GRAPH, QUERY_GOOD);
        expectLastCall().andReturn(FACTORY.createMock(Query.class));
        return parser;
    }
}
