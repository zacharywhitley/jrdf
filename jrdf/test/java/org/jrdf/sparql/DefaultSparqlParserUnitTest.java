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

import java.util.List;
import junit.framework.TestCase;
import org.jrdf.query.ConstraintExpression;
import org.jrdf.query.Query;
import org.jrdf.query.Variable;
import org.jrdf.sparql.parser.analysis.DepthFirstAdapter;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.util.param.ParameterTestUtil;

/**
 * Unit test for {@link DefaultSparqlParser}.
 * @author Tom Adams
 * @version $Revision$
 */
public final class DefaultSparqlParserUnitTest extends TestCase {

    private static final QueryParser PARSER = new DefaultSparqlParser();
    // FIXME TJA: Make sure that empty variable projection lists don't make it past the parser, as the Variable.ALL_VARIABLES is the empty list.
    // FIXME TJA: Triangulate on variables.
    // FIXME TJA: Triangulate on constraint expression.

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkExtensionOf(DepthFirstAdapter.class, DefaultSparqlParser.class);
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(SparqlParser.class, DefaultSparqlParser.class);
    }

    public void testParseQueryFailsWithBadInput() {
        checkBadInput(ParameterTestUtil.NULL_STRING);
        checkBadInput(ParameterTestUtil.EMPTY_STRING);
        checkBadInput(ParameterTestUtil.SINGLE_SPACE);
    }

    public void testProjectedVariables() {
        checkVariables(SparqlQueryTestUtil.QUERY_BOOK_1_DC_TITLE, Variable.ALL_VARIABLES);
        // FIXME TJA: Breadcrumb - Triangulate to force parsing of the variable list.
    }

    public void testSingleTriplePatternExpression() {
        // FIXME TJA: Breadcrumb - Back here once ConstraintTriple.equals() is implemented.
        Query query = PARSER.parseQuery(SparqlQueryTestUtil.QUERY_BOOK_1_DC_TITLE);
        ConstraintExpression actualExpression = query.getConstraintExpression();
        ConstraintExpression expectedExpression = SparqlQueryTestUtil.CONSTRAINT_BOOK_1_DC_TITLE;
        assertEquals(expectedExpression, actualExpression);
    }

    private void checkBadInput(String queryText) {
        try {
            new DefaultSparqlParser().parseQuery(queryText);
            fail("new DefaultSparqlParser().parseQuery(BAD_BAD_BAD) should have failed");
        } catch (IllegalArgumentException expected) {}
    }

    private void checkVariables(String queryText, List<? extends Variable> expectedVariables) {
        Query query = PARSER.parseQuery(queryText);
        List<? extends Variable> actualVariables = query.getProjectedVariables();
        assertEquals(expectedVariables, actualVariables);
    }
}
