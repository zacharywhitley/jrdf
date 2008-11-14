/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
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

package org.jrdf.urql.parser;

import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import static org.jrdf.graph.AnyNode.ANY_NODE;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Literal;
import org.jrdf.graph.global.LiteralImpl;
import org.jrdf.query.AskQueryImpl;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.Query;
import org.jrdf.query.SelectQueryImpl;
import org.jrdf.query.expression.Ask;
import org.jrdf.query.expression.BoundOperator;
import org.jrdf.query.expression.Conjunction;
import static org.jrdf.query.expression.EmptyConstraint.EMPTY_CONSTRAINT;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.Filter;
import org.jrdf.query.expression.Optional;
import org.jrdf.query.expression.Projection;
import org.jrdf.query.expression.Union;
import org.jrdf.query.expression.logic.EqualsExpression;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.expression.logic.LogicalAndExpression;
import org.jrdf.query.expression.logic.LogicalNotExpression;
import org.jrdf.query.expression.logic.NEqualsExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.mem.AttributeImpl;
import static org.jrdf.query.relation.mem.BoundAVPOperation.BOUND;
import static org.jrdf.query.relation.mem.EqAVPOperation.EQUALS;
import org.jrdf.query.relation.mem.SortedAttributeFactory;
import org.jrdf.query.relation.mem.SortedAttributeFactoryImpl;
import static org.jrdf.query.relation.mem.StrAVPOperation.STR;
import org.jrdf.query.relation.mem.ValueOperationImpl;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.AssertThrows.assertThrows;
import static org.jrdf.util.test.NodeTestUtil.createLiteral;
import org.jrdf.util.test.ReflectTestUtil;
import static org.jrdf.util.test.SparqlQueryTestUtil.ANY_SPO;
import static org.jrdf.util.test.SparqlQueryTestUtil.BOOK_1_DC_TITLE_ID_1;
import static org.jrdf.util.test.SparqlQueryTestUtil.BOOK_2_DC_TITLE_ID_1;
import static org.jrdf.util.test.SparqlQueryTestUtil.BOOK_2_DC_TITLE_ID_2;
import static org.jrdf.util.test.SparqlQueryTestUtil.BOOK_3_DC_TITLE_ID_3;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_BOOK_1_AND_2;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_BOOK_1_AND_2_AND_3;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_BOOK_1_AND_2_INNER_RIGHT;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_BOOK_1_AND_2_WITH_PREFIX;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_BOOK_1_DC_TITLE;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_BOOK_1_UNION_2;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_BOOK_1_UNION_2_UNION_3;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_BOOK_1_UNION_2_UNION_EMPTY;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_BOOK_2_DC_TITLE;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_EMPTY_UNION_BOOK_1_UNION_2;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_OPTIONAL_1;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_OPTIONAL_2;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_OPTIONAL_3;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_OPTIONAL_5;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_OPTION_4;
import static org.jrdf.util.test.SparqlQueryTestUtil.QUERY_SINGLE_OPTIONAL;
import static org.jrdf.util.test.TripleTestUtil.FOAF_MBOX;
import static org.jrdf.util.test.TripleTestUtil.FOAF_NAME;
import static org.jrdf.util.test.TripleTestUtil.FOAF_NICK;
import static org.jrdf.util.test.TripleTestUtil.createConstraintExpression;
import org.jrdf.vocabulary.XSD;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public final class SableCcSparqlParserIntegrationTest extends TestCase {

    // FIXME TJA: Triangulate on variables.
    // FIXME TJA: Triangulate on expression expression.
    // FIXME TJA: Write tests to force trimming of query string.
    // FIXME TJA: Make sure that empty variable projection lists don't make it past the parser, as the
    // Projection.ALL_VARIABLES is the empty list.
    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final Graph GRAPH = FACTORY.getGraph();
    private static final Literal LITERAL = createLiteral("The Pragmatic Programmer");
    private static final Expression<ExpressionVisitor> BOOK1_AND_2_CONJUNCTION
        = new Conjunction<ExpressionVisitor>(BOOK_1_DC_TITLE_ID_1, BOOK_2_DC_TITLE_ID_2);
    private static final Expression<ExpressionVisitor> BOOK1_AND_2_AND_3_CONJUNCTION
        = new Conjunction<ExpressionVisitor>(BOOK1_AND_2_CONJUNCTION, BOOK_3_DC_TITLE_ID_3);
    private static final Expression<ExpressionVisitor> BOOK1_AND_2_UNION
        = new Union<ExpressionVisitor>(BOOK_1_DC_TITLE_ID_1, BOOK_2_DC_TITLE_ID_2);
    private static final Expression<ExpressionVisitor> BOOK1_AND_2_AND_3_UNION
        = new Union<ExpressionVisitor>(BOOK1_AND_2_UNION, BOOK_3_DC_TITLE_ID_3);
    private static final Expression<ExpressionVisitor> BOOK1_AND_2_AND_EMPTY_UNION
        = new Union<ExpressionVisitor>(BOOK1_AND_2_UNION, EMPTY_CONSTRAINT);
    private static final Expression<ExpressionVisitor> EMPTY_AND_BOOK1_AND_2_UNION = new Union<ExpressionVisitor>(
        new Union<ExpressionVisitor>(EMPTY_CONSTRAINT, BOOK_1_DC_TITLE_ID_1), BOOK_2_DC_TITLE_ID_2);
    private static final Expression<ExpressionVisitor> ALL_AND_EMPTY = new Conjunction<ExpressionVisitor>(ANY_SPO,
        EMPTY_CONSTRAINT);
    private static final Expression<ExpressionVisitor> FOAF_NAME_EXP_1 = createConstraintExpression("x", FOAF_NAME,
        "name", 1);
    private static final Expression<ExpressionVisitor> FOAF_NICK_EXP_2 = createConstraintExpression("x", FOAF_NICK,
        "nick", 2);
    private static final Expression<ExpressionVisitor> FOAF_ALIAS_EXP_2 = createConstraintExpression("x", FOAF_NICK,
        "alias", 2);
    private static final Expression<ExpressionVisitor> FOAF_MBOX_EXP_3 = createConstraintExpression("x", FOAF_MBOX,
        "mbox", 3);
    private static final Expression<ExpressionVisitor> FOAF_ALIAS_EXP_3 = createConstraintExpression("x", FOAF_MBOX,
        "alias", 3);
    private static final Expression<ExpressionVisitor> FOAF_NAME_EXP_3 = createConstraintExpression("x", FOAF_NAME,
        "name", 3);
    private static final Expression<ExpressionVisitor> FOAF_MBOX_EXP_4 = createConstraintExpression("x", FOAF_MBOX,
        "mbox", 4);
    private static final Expression<ExpressionVisitor> DC_DATE_EXP_2 = createConstraintExpression("x",
        URI.create("http://purl.org/dc/elements/1.1/date"), "date", 2);

    private static final String SELECT_WHERE_S_P_O_AND_EMPTY = "SELECT * WHERE { ?s ?p ?o . {} } ";
    private QueryParser parser;

    public void setUp() throws Exception {
        AttributeComparator newAttributeComparator = FACTORY.getNewAttributeComparator();
        SortedAttributeFactory newSortedAttributeFactory = new SortedAttributeFactoryImpl(newAttributeComparator, 1);
        parser = new SableCcSparqllParser(FACTORY.getNewParserFactory(), FACTORY.getNewGraphRelationFactory(),
            newSortedAttributeFactory);
    }

    public void testSingleConstraint() throws Exception {
        checkConstraintExpression(QUERY_BOOK_1_DC_TITLE, BOOK_1_DC_TITLE_ID_1);
    }

    public void testSingleConstraint2() throws Exception {
        checkConstraintExpression(QUERY_BOOK_2_DC_TITLE, BOOK_2_DC_TITLE_ID_1);
    }

    public void testPrefix() throws Exception {
        checkConstraintExpression(QUERY_BOOK_1_AND_2_WITH_PREFIX, BOOK1_AND_2_CONJUNCTION);
    }

    public void testTwoConstraints() throws Exception {
        checkConstraintExpression(QUERY_BOOK_1_AND_2, BOOK1_AND_2_CONJUNCTION);
    }

    public void testTwoConstraints2() throws Exception {
        checkConstraintExpression(SELECT_WHERE_S_P_O_AND_EMPTY, ALL_AND_EMPTY);
    }

    public void testTwoNestConstraintsInnerRight() throws Exception {
        checkConstraintExpression(QUERY_BOOK_1_AND_2_INNER_RIGHT, BOOK1_AND_2_CONJUNCTION);
    }

    public void testThreeConstraints() throws Exception {
        checkConstraintExpression(QUERY_BOOK_1_AND_2_AND_3, BOOK1_AND_2_AND_3_CONJUNCTION);
    }

    public void testUnionConstraint() throws Exception {
        checkConstraintExpression(QUERY_BOOK_1_UNION_2, BOOK1_AND_2_UNION);
    }

    public void testUnionConstraint2() throws Exception {
        checkConstraintExpression(QUERY_BOOK_1_UNION_2_UNION_3, BOOK1_AND_2_AND_3_UNION);
    }

    public void testUnionConstraint3() throws Exception {
        checkConstraintExpression(QUERY_BOOK_1_UNION_2_UNION_EMPTY, BOOK1_AND_2_AND_EMPTY_UNION);
    }

    public void testUnionConstraint4() throws Exception {
        checkConstraintExpression(QUERY_EMPTY_UNION_BOOK_1_UNION_2, EMPTY_AND_BOOK1_AND_2_UNION);
    }

    public void testSingleOptionalConstraint() throws Exception {
        Optional<ExpressionVisitor> optional1 = new Optional<ExpressionVisitor>(FOAF_NAME_EXP_1, FOAF_NICK_EXP_2);
        checkConstraintExpression(QUERY_SINGLE_OPTIONAL, optional1);
    }

    public void testOptionalConstraint1() throws Exception {
        Optional<ExpressionVisitor> optional1 = new Optional<ExpressionVisitor>(FOAF_NICK_EXP_2, FOAF_MBOX_EXP_3);
        Optional<ExpressionVisitor> optional2 = new Optional<ExpressionVisitor>(FOAF_NAME_EXP_1, optional1);
        checkConstraintExpression(QUERY_OPTIONAL_1, optional2);
    }

    public void testOptionalConstraint2() throws Exception {
        Optional<ExpressionVisitor> optional1 = new Optional<ExpressionVisitor>(FOAF_NAME_EXP_1, FOAF_ALIAS_EXP_2);
        Optional<ExpressionVisitor> optional2 = new Optional<ExpressionVisitor>(optional1, FOAF_ALIAS_EXP_3);
        checkConstraintExpression(QUERY_OPTIONAL_2, optional2);
    }

    public void testOptionalConstraint3() throws Exception {
        Optional<ExpressionVisitor> optional1 = new Optional<ExpressionVisitor>(FOAF_NAME_EXP_1, FOAF_ALIAS_EXP_2);
        Optional<ExpressionVisitor> optional2 = new Optional<ExpressionVisitor>(optional1, FOAF_ALIAS_EXP_3);
        checkConstraintExpression(QUERY_OPTIONAL_3, optional2);
    }

    public void testComplicatedOptional1() throws Exception {
        Optional<ExpressionVisitor> optional1 = new Optional<ExpressionVisitor>(FOAF_NAME_EXP_1, FOAF_NICK_EXP_2);
        Optional<ExpressionVisitor> optional2 = new Optional<ExpressionVisitor>(FOAF_NAME_EXP_3, FOAF_MBOX_EXP_4);
        Conjunction<ExpressionVisitor> expectedExpression = new Conjunction<ExpressionVisitor>(optional1, optional2);
        checkConstraintExpression(QUERY_OPTION_4, expectedExpression);
    }

    public void testComplicatedOptional2() throws Exception {
        Optional<ExpressionVisitor> optional1 = new Optional<ExpressionVisitor>(FOAF_NAME_EXP_1, FOAF_NICK_EXP_2);
        Optional<ExpressionVisitor> optional2 = new Optional<ExpressionVisitor>(FOAF_NAME_EXP_3, FOAF_MBOX_EXP_4);
        Optional<ExpressionVisitor> expectedExpression = new Optional<ExpressionVisitor>(optional1, optional2);
        checkConstraintExpression(QUERY_OPTIONAL_5, expectedExpression);
    }

    public void testLiteralQuote() throws Exception {
        Expression<ExpressionVisitor> spPrag1 = createConstraintExpression("s", "p", LITERAL, 1);
        checkConstraintExpression("SELECT * WHERE { ?s ?p 'The Pragmatic Programmer' } ", spPrag1);
        Expression<ExpressionVisitor> spPrag2 = createConstraintExpression("s", "p", LITERAL, 2);
        checkConstraintExpression("SELECT * WHERE { ?s ?p \"The Pragmatic Programmer\" } ", spPrag2);
    }

    public void testLiteralWithLanguage() throws Exception {
        Expression<ExpressionVisitor> spHello1 = createConstraintExpression("s", "p", createLiteral("hello", "en"), 1);
        checkConstraintExpression("SELECT * WHERE { ?s ?p 'hello'@en }", spHello1);
        Expression<ExpressionVisitor> spHello2 = createConstraintExpression("s", "p", createLiteral("hello", "en"), 2);
        checkConstraintExpression("SELECT * WHERE { ?s ?p \"hello\"@en }", spHello2);
    }

    public void testLiteralWithDatatype() throws Exception {
        Literal one = createLiteral("1", XSD.INTEGER);
        Literal helloString = createLiteral("hello", XSD.STRING);
        Expression<ExpressionVisitor> spHello1 = createConstraintExpression("s", "p", one, 1);
        Expression<ExpressionVisitor> spHello2 = createConstraintExpression("s", "p", helloString, 2);
        Expression<ExpressionVisitor> spHello3 = createConstraintExpression("s", "p", helloString, 3);
        checkConstraintExpression("SELECT * WHERE { ?s ?p '1'^^<http://www.w3.org/2001/XMLSchema#integer> }", spHello1);
        checkConstraintExpression("SELECT * WHERE { ?s ?p 'hello'^^<http://www.w3.org/2001/XMLSchema#string> }",
            spHello2);
        checkConstraintExpression("PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>" +
            " SELECT * WHERE { ?s ?p 'hello'^^xsd:string }", spHello3);
    }

    public void testLiteralWithIncorrectPrefix() {
        String message = "Unable to parse query syntax token: xsd ";
        assertThrows(InvalidQuerySyntaxException.class, message, new AssertThrows.Block() {
            public void execute() throws Throwable {
                parseQuery(" SELECT * WHERE { ?s ?p 'hello'^^xsd:string }");
            }
        });
    }

    public void testUnsignedNumericIntegerLiteral() throws Exception {
        Literal one = createLiteral("1", XSD.INTEGER);
        Literal posTwo = createLiteral("+02", XSD.INTEGER);
        Expression<ExpressionVisitor> spOne = createConstraintExpression("s", "p", one, 1);
        Expression<ExpressionVisitor> spPosTwo = createConstraintExpression("s", "p", posTwo, 2);
        checkConstraintExpression("SELECT * WHERE { ?s ?p 1 }", spOne);
        checkConstraintExpression("SELECT * WHERE { ?s ?p +02 }", spPosTwo);
    }

    public void testUnsignedNumericDecimalLiteral() throws Exception {
        Literal onePointThree = createLiteral("1.3", XSD.DECIMAL);
        Literal onePointThreeZeroZeroZero = createLiteral("1.300", XSD.DECIMAL);
        Literal negOnePointThreeZeroZeroZero = createLiteral("-1.300", XSD.DECIMAL);
        Expression<ExpressionVisitor> spOnePointThree = createConstraintExpression("s", "p", onePointThree, 1);
        Expression<ExpressionVisitor> spOnePointThreeZeroZeroZero = createConstraintExpression("s", "p",
            onePointThreeZeroZeroZero, 2);
        Expression<ExpressionVisitor> spNegOnePointThreeZeroZeroZero = createConstraintExpression("s", "p",
            negOnePointThreeZeroZeroZero, 3);
        checkConstraintExpression("SELECT * WHERE { ?s ?p 1.3 }", spOnePointThree);
        checkConstraintExpression("SELECT * WHERE { ?s ?p 1.300 }", spOnePointThreeZeroZeroZero);
        checkConstraintExpression("SELECT * WHERE { ?s ?p -1.300 }", spNegOnePointThreeZeroZeroZero);
    }

    public void testUnsignedNumericDoubleLiteral() throws Exception {
        Literal doubleLiteral = createLiteral("1.3e6", XSD.DOUBLE);
        Literal signedDoubleLiteral = createLiteral("+1.3E06", XSD.DOUBLE);
        Expression<ExpressionVisitor> spDouble = createConstraintExpression("s", "p", doubleLiteral, 1);
        Expression<ExpressionVisitor> spSignedDouble = createConstraintExpression("s", "p", signedDoubleLiteral, 2);
        checkConstraintExpression("SELECT * WHERE { ?s ?p 1.3e6 }", spDouble);
        checkConstraintExpression("SELECT * WHERE { ?s ?p +1.3E06 }", spSignedDouble);
    }

    public void testBooleanLiteral() throws Exception {
        Literal trueLiteral = createLiteral("true", XSD.BOOLEAN);
        Literal falseLiteral = createLiteral("false", XSD.BOOLEAN);
        Expression<ExpressionVisitor> spTrue = createConstraintExpression("s", "p", trueLiteral, 1);
        Expression<ExpressionVisitor> spFalse = createConstraintExpression("s", "p", falseLiteral, 2);
        checkConstraintExpression("SELECT * WHERE { ?s ?p true }", spTrue);
        checkConstraintExpression("SELECT * WHERE { ?s ?p false }", spFalse);
    }

    public void testFilter() throws Exception {
        AttributeName oVar = new VariableName("o");
        Expression<ExpressionVisitor> spoExpression = createConstraintExpression("s", "p", "o");
        Map<Attribute, ValueOperation> avo = new HashMap<Attribute, ValueOperation>();
        Attribute attribute = new AttributeImpl(oVar, new ObjectNodeType());
        ValueOperation value = new ValueOperationImpl(createLiteral("unknown"), EQUALS);
        avo.put(attribute, value);
        Map<Attribute, ValueOperation> strAvo = new HashMap<Attribute, ValueOperation>();
        ValueOperation strValue = new ValueOperationImpl(ANY_NODE, STR);
        strAvo.put(attribute, strValue);
        LogicExpression<ExpressionVisitor> eqnExpression = new EqualsExpression<ExpressionVisitor>(strAvo, avo);
        Expression<ExpressionVisitor> filterExpression = new Filter(spoExpression, eqnExpression);
        checkConstraintExpression("SELECT * WHERE { ?s ?p ?o . FILTER( str(?o) = \"unknown\" ) }", filterExpression);
    }

    public void testPrefixInFilter() throws Exception {
        AttributeName oVar = new VariableName("o");
        Expression<ExpressionVisitor> spoExpression = createConstraintExpression("s", "p", "o");
        Map<Attribute, ValueOperation> avo = new HashMap<Attribute, ValueOperation>();
        Attribute attribute = new AttributeImpl(oVar, new ObjectNodeType());
        ValueOperation value = new ValueOperationImpl(createLiteral("unknown", XSD.STRING), EQUALS);
        avo.put(attribute, value);
        Map<Attribute, ValueOperation> strAvo = new HashMap<Attribute, ValueOperation>();
        ValueOperation strValue = new ValueOperationImpl(ANY_NODE, STR);
        strAvo.put(attribute, strValue);
        LogicExpression<ExpressionVisitor> equalsExpression = new EqualsExpression(strAvo, avo);
        Expression<ExpressionVisitor> filterExpression = new Filter(spoExpression, equalsExpression);
        checkConstraintExpression("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
            "SELECT * WHERE { ?s ?p ?o . FILTER(str(?o) = \"unknown\"^^xsd:string) }", filterExpression);
    }

    public void testThreePartOptional() throws Exception {
        String queryString = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
            "SELECT ?name\n" +
            "WHERE { ?x foaf:name ?name .\n" +
            "OPTIONAL { ?x dc:date ?date } .\n" +
            "FILTER ( bound(?date) ) }";

        Expression<ExpressionVisitor> optionalExpression = new Optional<ExpressionVisitor>(FOAF_NAME_EXP_1,
            DC_DATE_EXP_2);
        AttributeName dateVar = new VariableName("date");
        Map<Attribute, ValueOperation> avo = new HashMap<Attribute, ValueOperation>();
        Attribute attribute = new AttributeImpl(dateVar, new ObjectNodeType());
        ValueOperation value = new ValueOperationImpl(ANY_NODE, BOUND);
        avo.put(attribute, value);
        LogicExpression<ExpressionVisitor> boundExpression = new BoundOperator(avo);
        Expression<ExpressionVisitor> filterExpression = new Filter(optionalExpression, boundExpression);
        checkConstraintExpression(queryString, filterExpression);
    }

    public void testBooleanNotOperator() throws Exception {
        String queryString = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
            "SELECT ?name\n" +
            "WHERE { ?x foaf:name ?name .\n" +
            "FILTER ( !bound(?name) ) }";

        AttributeName dateVar = new VariableName("name");
        Map<Attribute, ValueOperation> avo = new HashMap<Attribute, ValueOperation>();
        Attribute attribute = new AttributeImpl(dateVar, new ObjectNodeType());
        ValueOperation value = new ValueOperationImpl(ANY_NODE, BOUND);
        avo.put(attribute, value);
        LogicExpression<ExpressionVisitor> boundExpression = new BoundOperator(avo);
        LogicExpression<ExpressionVisitor> notExpression = new LogicalNotExpression(boundExpression);
        Expression<ExpressionVisitor> filterExpression = new Filter(FOAF_NAME_EXP_1, notExpression);
        checkConstraintExpression(queryString, filterExpression);
    }

    public void testBooleanNotStrOperators() throws Exception {
        String queryString = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
            "SELECT ?name\n" +
            "WHERE { ?x foaf:name ?name .\n" +
            "FILTER ( !(str(?name) = \"abc\")) }";
        AttributeName dateVar = new VariableName("name");
        Map<Attribute, ValueOperation> avo = new HashMap<Attribute, ValueOperation>();
        Attribute attribute = new AttributeImpl(dateVar, new ObjectNodeType());
        Literal abcLit = new LiteralImpl("abc");
        ValueOperation value = new ValueOperationImpl(abcLit, EQUALS);
        avo.put(attribute, value);

        AttributeName nameVar = new VariableName("name");
        Map<Attribute, ValueOperation> nameAvo = new HashMap<Attribute, ValueOperation>();
        attribute = new AttributeImpl(nameVar, new ObjectNodeType());
        value = new ValueOperationImpl(ANY_NODE, STR);
        nameAvo.put(attribute, value);


        LogicExpression<ExpressionVisitor> equalsExpression = new EqualsExpression(nameAvo, avo);
        LogicExpression<ExpressionVisitor> notExp = new LogicalNotExpression(equalsExpression);
        Expression<ExpressionVisitor> filterExpression = new Filter(FOAF_NAME_EXP_1, notExp);
        checkConstraintExpression(queryString, filterExpression);
    }

    public void testNeqExpression() throws Exception {
        String queryString = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
            "SELECT ?name\n" +
            "WHERE { ?x foaf:name ?name .\n" +
            "FILTER ( str(?name) != \"abc\") }";
        AttributeName dateVar = new VariableName("name");
        Map<Attribute, ValueOperation> avo = new HashMap<Attribute, ValueOperation>();
        Attribute attribute = new AttributeImpl(dateVar, new ObjectNodeType());
        Literal abcLit = new LiteralImpl("abc");
        ValueOperation value = new ValueOperationImpl(abcLit, EQUALS);
        avo.put(attribute, value);

        AttributeName nameVar = new VariableName("name");
        Map<Attribute, ValueOperation> nameAvo = new HashMap<Attribute, ValueOperation>();
        attribute = new AttributeImpl(nameVar, new ObjectNodeType());
        value = new ValueOperationImpl(ANY_NODE, STR);
        nameAvo.put(attribute, value);


        LogicExpression<ExpressionVisitor> neqExpression = new NEqualsExpression(nameAvo, avo);
        Expression<ExpressionVisitor> filterExpression = new Filter(FOAF_NAME_EXP_1, neqExpression);
        checkConstraintExpression(queryString, filterExpression);
    }

    public void testBooleanAndExpression() throws Exception {
        String queryString = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
            "SELECT ?name\n" +
            "WHERE { ?x foaf:name ?name .\n" +
            "FILTER ( str(?name) = \"abc\" && bound(?x) ) }";
        AttributeName nameVar = new VariableName("name");
        Map<Attribute, ValueOperation> avo = new HashMap<Attribute, ValueOperation>();
        Attribute attribute = new AttributeImpl(nameVar, new ObjectNodeType());
        Literal abcLit = new LiteralImpl("abc");
        ValueOperation value = new ValueOperationImpl(abcLit, EQUALS);
        avo.put(attribute, value);

        AttributeName xVar = new VariableName("x");
        Map<Attribute, ValueOperation> xAvo = new HashMap<Attribute, ValueOperation>();
        attribute = new AttributeImpl(xVar, new SubjectNodeType());
        value = new ValueOperationImpl(ANY_NODE, BOUND);
        xAvo.put(attribute, value);

        Map<Attribute, ValueOperation> nameAvo = new HashMap<Attribute, ValueOperation>();
        attribute = new AttributeImpl(nameVar, new ObjectNodeType());
        value = new ValueOperationImpl(ANY_NODE, STR);
        nameAvo.put(attribute, value);

        LogicExpression<ExpressionVisitor> equalsExpression = new EqualsExpression(nameAvo, avo);
        LogicExpression<ExpressionVisitor> boundExpression = new BoundOperator(xAvo);
        LogicExpression<ExpressionVisitor> andExpression = new LogicalAndExpression(equalsExpression, boundExpression);
        Expression<ExpressionVisitor> filterExpression = new Filter(FOAF_NAME_EXP_1, andExpression);
        checkConstraintExpression(queryString, filterExpression);
    }

    public void testComplexQuery() throws InvalidQuerySyntaxException {
        String queryString =
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX biopax: <http://www.biopax.org/release/biopax-level2.owl#> \n" +
                "PREFIX biomanta: <http://biomanta.sourceforge.net/2007/07/biomanta_extension_02.owl#> \n" +
                "PREFIX ncbi: <http://biomanta.sourceforge.net/2007/10/ncbi_taxo.owl#> \n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n" +
                "SELECT ?name ?id \n" +
                "WHERE { { ?x rdf:type biopax:physicalEntity . \n" +
                "        ?x biomanta:fromNCBISpecies ncbi:ncbi_taxo_4932_ind . \n" +
                "        ?x biomanta:hasPrimaryRef ?y . \n" +
                "        ?y biopax:DB ?db \n" +
                "        FILTER (str(?db) = \"uniprotkb\"^^xsd:string) } . \n" +
                "        \n" +
                "        { ?y biopax:ID ?id . \n" +
                "        FILTER (str(?id) = \"o13516\"^^xsd:string) } . \n" +
                "        ?x biomanta:hasFullName ?name }";
        Query query = parseQuery(queryString);
        Expression<ExpressionVisitor> actualExpression = getExpression(query);
    }

    public void testSimpleAskQuery() throws Exception {
        Expression<ExpressionVisitor> spPrag1 = createConstraintExpression("s", "p", LITERAL, 1);
        checkConstraintExpression("ASK WHERE { ?s ?p 'The Pragmatic Programmer' } ", spPrag1);
    }

    private void checkConstraintExpression(String queryString, Expression<ExpressionVisitor> expectedExpression) throws
        Exception {
        Query query = parseQuery(queryString);
        Expression<ExpressionVisitor> actualExpression = getExpression(query);
        assertEquals(expectedExpression, actualExpression);
    }

    private Expression<ExpressionVisitor> getExpression(Query query) {
        try {
            if (query instanceof SelectQueryImpl) {
                Expression<ExpressionVisitor> expression = getExpressionField(query, SelectQueryImpl.class,
                    "expression");
                return getExpressionField(expression, Projection.class, "nextExpression");
            } else if (query instanceof AskQueryImpl) {
                Expression<ExpressionVisitor> expression = getExpressionField(query, AskQueryImpl.class, "expression");
                return getExpressionField(expression, Ask.class, "nextExpression");
            } else {
                throw new IllegalArgumentException();
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Expression<ExpressionVisitor> getExpressionField(Object obj, Class<?> cls, String fieldName)
        throws IllegalAccessException {
        Field field = ReflectTestUtil.getField(cls, fieldName);
        field.setAccessible(true);
        return (Expression<ExpressionVisitor>) field.get(obj);
    }

    private Query parseQuery(String queryString) throws InvalidQuerySyntaxException {
        return parser.parseQuery(GRAPH, queryString);
    }
}
