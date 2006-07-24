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

import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import org.jrdf.graph.Graph;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.Query;
import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.Optional;
import org.jrdf.query.expression.Union;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.mem.SortedAttributeFactory;
import org.jrdf.query.relation.mem.SortedAttributeFactoryImpl;
import org.jrdf.sparql.builder.TripleBuilder;
import org.jrdf.sparql.builder.TripleBuilderImpl;
import static org.jrdf.util.test.SparqlQueryTestUtil.BOOK_1_DC_TITLE_ID_1;
import static org.jrdf.util.test.SparqlQueryTestUtil.BOOK_2_DC_TITLE_ID_1;
import static org.jrdf.util.test.SparqlQueryTestUtil.BOOK_2_DC_TITLE_ID_2;
import static org.jrdf.util.test.SparqlQueryTestUtil.BOOK_3_DC_TITLE_ID_3;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_BOOK_1_AND_2;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_BOOK_1_AND_2_AND_3;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_BOOK_1_DC_TITLE;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_BOOK_1_UNION_2;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_BOOK_1_UNION_2_UNION_3;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_BOOK_2_DC_TITLE;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_OPTIONAL_1;
import static org.jrdf.util.test.TripleTestUtil.FOAF_MBOX;
import static org.jrdf.util.test.TripleTestUtil.FOAF_NAME;
import static org.jrdf.util.test.TripleTestUtil.FOAF_NICK;
import static org.jrdf.util.test.TripleTestUtil.createConstraintExpression;

/**
 * Integration test for {@link SableCcSparqlParser}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class SableCcSparqlParserIntegrationTest extends TestCase {

    // FIXME TJA: Triangulate on variables.
    // FIXME TJA: Triangulate on expression expression.
    // FIXME TJA: Write tests to force trimming of query string.
    // FIXME TJA: Make sure that empty variable projection lists don't make it past the parser, as the Projection.ALL_VARIABLES is the empty list.

    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final Graph GRAPH = FACTORY.getNewGraph();
    private static final Expression<ExpressionVisitor> BOOK1_AND_2_CONJUNCTION
            = new Conjunction<ExpressionVisitor>(BOOK_1_DC_TITLE_ID_1, BOOK_2_DC_TITLE_ID_2);
    private static final Expression<ExpressionVisitor> BOOK1_AND_2_AND_3_CONJUNCTION
            = new Conjunction<ExpressionVisitor>(BOOK1_AND_2_CONJUNCTION, BOOK_3_DC_TITLE_ID_3);
    private static final Expression<ExpressionVisitor> BOOK1_AND_2_UNION
            = new Union<ExpressionVisitor>(BOOK_1_DC_TITLE_ID_1, BOOK_2_DC_TITLE_ID_2);
    private static final Expression<ExpressionVisitor> BOOK1_AND_2_AND_3_UNION
            = new Union<ExpressionVisitor>(BOOK1_AND_2_UNION, BOOK_3_DC_TITLE_ID_3);
    private QueryParser parser;

    public void setUp() throws Exception {
        AttributeComparator newAttributeComparator = FACTORY.getNewAttributeComparator();
        SortedAttributeFactory newSortedAttributeFactory = new SortedAttributeFactoryImpl(newAttributeComparator, 1);
        TripleBuilder builder = new TripleBuilderImpl(FACTORY.getNewSortedAttributeValuePairHelper(),
                newSortedAttributeFactory);
        parser = new SableCcSparqlParser(FACTORY.getNewParserFactory(), builder);
    }

    public void testSingleConstraint() {
        checkConstraintExpression(QUERY_BOOK_1_DC_TITLE, BOOK_1_DC_TITLE_ID_1);
    }

    public void testSingleConstraint2() {
        checkConstraintExpression(QUERY_BOOK_2_DC_TITLE, BOOK_2_DC_TITLE_ID_1);
    }

    public void testTwoConstraints() {
        checkConstraintExpression(QUERY_BOOK_1_AND_2, BOOK1_AND_2_CONJUNCTION);
    }

    public void testThreeConstraints() {
        checkConstraintExpression(QUERY_BOOK_1_AND_2_AND_3, BOOK1_AND_2_AND_3_CONJUNCTION);
    }

    public void testUnionConstraint() {
        checkConstraintExpression(QUERY_BOOK_1_UNION_2, BOOK1_AND_2_UNION);
    }

    public void testUnionConstraint2() {
        checkConstraintExpression(QUERY_BOOK_1_UNION_2_UNION_3, BOOK1_AND_2_AND_3_UNION);
    }

    public void testOptionalConstraint() throws Exception {
        Expression<ExpressionVisitor> foafName = createConstraintExpression("?x", FOAF_NAME, "?name", 1);
        Expression<ExpressionVisitor> foafNick = createConstraintExpression("?x", FOAF_NICK, "?nick", 2);
        Expression<ExpressionVisitor> foafMbox = createConstraintExpression("?x", FOAF_MBOX, "?mbox", 3);
        Optional<ExpressionVisitor> optional1 = new Optional<ExpressionVisitor>(foafNick, foafMbox);
        Optional<ExpressionVisitor> optional2 = new Optional<ExpressionVisitor>(foafName, optional1);
        checkConstraintExpression(QUERY_OPTIONAL_1, optional2);
    }

//    public void testComplicatedOptional() {
//        String query = "SELECT ?name ?mbox ?nick\n" +
//                "WHERE  { \n" +
//                "  { ?x <http://xmlns.com/foaf/0.1/name> ?name OPTIONAL { ?x <http://xmlns.com/foaf/0.1/nick> ?nick }} .\n" +
//                "  { ?x<http://xmlns.com/foaf/0.1/name> ?name OPTIONAL { ?x  <http://xmlns.com/foaf/0.1/mbox> ?mbox }}\n" +
//                "}";
//        checkConstraintExpression(query,BOOK1_AND_2_UNION);
//    }

    private void checkConstraintExpression(String queryString, Expression expectedExpression) {
        Query query = parseQuery(queryString);
        Expression<ExpressionVisitor> actualExpression = query.getConstraintExpression();
        assertEquals(expectedExpression, actualExpression);
    }

    private Query parseQuery(String queryString) {
        try {
            return parser.parseQuery(GRAPH, queryString);
        } catch (InvalidQuerySyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
