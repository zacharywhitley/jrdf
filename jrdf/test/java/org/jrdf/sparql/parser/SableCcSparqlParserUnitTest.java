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

package org.jrdf.sparql.parser;

import java.util.List;
import java.lang.reflect.Modifier;

import junit.framework.TestCase;
import org.jrdf.query.ConstraintExpression;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.MockQuery;
import org.jrdf.query.Query;
import org.jrdf.query.Variable;
import org.jrdf.util.test.SparqlQueryTestUtil;
import org.jrdf.sparql.analysis.MockSparqlAnalyser;
import org.jrdf.util.param.ParameterTestUtil;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.util.test.ReflectTestUtil;
import org.jrdf.util.test.TripleTestUtil;
import static org.jrdf.util.test.ClassPropertiesTestUtil.NO_ARG_CONSTRUCTOR;

/**
 * Unit test for {@link SableCcSparqlParser}.
 * @author Tom Adams
 * @version $Revision$
 */
public final class SableCcSparqlParserUnitTest extends TestCase {

    private static final String QUERY_BOOK_1_DC_TITLE = SparqlQueryTestUtil.QUERY_BOOK_1_DC_TITLE;
    private static final ConstraintExpression CONSTRAINT_BOOK_1_DC_TITLE = SparqlQueryTestUtil.CONSTRAINT_BOOK_1_DC_TITLE;

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(SparqlParser.class, SableCcSparqlParser.class);
        ClassPropertiesTestUtil.checkConstructor(SableCcSparqlParser.class, Modifier.PUBLIC, NO_ARG_CONSTRUCTOR);
    }

    // FIXME TJA: Triangulate to force parsing of the variable list (requires non-wildcard variable projection in grammar).
    public void testProjectedVariables() {
        checkVariablesOnParsedQuery(QUERY_BOOK_1_DC_TITLE, Variable.ALL_VARIABLES);
    }

    public void testParseQueryFailsWithBadInput() {
        checkBadInput(ParameterTestUtil.NULL_STRING);
        checkBadInput(ParameterTestUtil.EMPTY_STRING);
        checkBadInput(ParameterTestUtil.SINGLE_SPACE);
    }

    public void testSingleTriplePatternExpression() {
        checkSingleConstraintExpression(QUERY_BOOK_1_DC_TITLE, CONSTRAINT_BOOK_1_DC_TITLE);
    }

    private void checkVariablesOnParsedQuery(String queryText, List<? extends Variable> expectedVariables) {
        Query query = parseQuery(queryText);
        List<? extends Variable> actualVariables = query.getProjectedVariables();
        assertEquals(expectedVariables, actualVariables);
    }

    private void checkSingleConstraintExpression(String queryString, ConstraintExpression expectedExpression) {
        Query query = parseQuery(queryString);
        ConstraintExpression actualExpression = query.getConstraintExpression();
        assertEquals(expectedExpression, actualExpression);
    }

    private void checkBadInput(String queryText) {
        try {
            parseQuery(queryText);
            fail("new DefaultSparqlParser().parseQuery(BAD_BAD_BAD) should have failed");
        } catch (IllegalArgumentException expected) {}
    }

    private Query parseQuery(String queryString) {
        try {
            return createParser().parseQuery(queryString);
        } catch (InvalidQuerySyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private SparqlParser createParser() {
        SableCcSparqlParser parser = new SableCcSparqlParser();
        insertMockAnalyser(parser);
        return parser;
    }

    private void insertMockAnalyser(SableCcSparqlParser parser) {
        MockSparqlAnalyser mockAnalyser = createMockSparqlAnalyser();
        ReflectTestUtil.insertFieldValue(parser, "analyser", mockAnalyser);
    }

    private MockSparqlAnalyser createMockSparqlAnalyser() {
        MockSparqlAnalyser mockAnalyser = new MockSparqlAnalyser();
        mockAnalyser.prepare(createQuery());
        return mockAnalyser;
    }

    private MockQuery createQuery() {
        return new MockQuery(TripleTestUtil.URI_BOOK_1, TripleTestUtil.URI_DC_TITLE);
    }
}
