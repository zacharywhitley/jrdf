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

package org.jrdf.sparql.parser;

import org.jrdf.TestJRDFFactory;
import org.jrdf.graph.Graph;
import org.jrdf.graph.Literal;
import org.jrdf.query.AskQueryImpl;
import org.jrdf.query.InvalidQuerySyntaxException;
import org.jrdf.query.Query;
import org.jrdf.query.SelectQueryImpl;
import org.jrdf.query.expression.Ask;
import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.Filter;
import org.jrdf.query.expression.Optional;
import org.jrdf.query.expression.Projection;
import org.jrdf.query.expression.Union;
import org.jrdf.query.expression.logic.EqualsExpression;
import org.jrdf.query.expression.logic.LogicAndExpression;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.expression.logic.LogicNotExpression;
import org.jrdf.query.expression.logic.LogicOrExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.mem.SortedAttributeFactory;
import org.jrdf.query.relation.mem.SortedAttributeFactoryImpl;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.util.test.AssertThrows;
import org.jrdf.util.test.ReflectTestUtil;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.URI;

import static java.net.URI.create;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jrdf.query.expression.EmptyConstraint.EMPTY_CONSTRAINT;
import static org.jrdf.query.expression.logic.FalseExpression.FALSE_EXPRESSION;
import static org.jrdf.query.expression.logic.TrueExpression.TRUE_EXPRESSION;
import static org.jrdf.sparql.parser.OperatorTestUtil.bound;
import static org.jrdf.sparql.parser.OperatorTestUtil.lang;
import static org.jrdf.sparql.parser.OperatorTestUtil.literal;
import static org.jrdf.sparql.parser.OperatorTestUtil.not;
import static org.jrdf.sparql.parser.OperatorTestUtil.str;
import static org.jrdf.sparql.parser.OperatorTestUtil.var;
import static org.jrdf.util.test.AssertThrows.assertThrows;
import static org.jrdf.util.test.NodeTestUtil.createLiteral;
import static org.jrdf.util.test.SparqlQueryTestUtil.ANY_SPO;
import static org.jrdf.util.test.SparqlQueryTestUtil.BOOK_1_DC_TITLE_ID_1;
import static org.jrdf.util.test.SparqlQueryTestUtil.BOOK_2_DC_TITLE_ID_1;
import static org.jrdf.util.test.SparqlQueryTestUtil.BOOK_2_DC_TITLE_ID_2;
import static org.jrdf.util.test.SparqlQueryTestUtil.BOOK_3_DC_TITLE_ID_3;
import static org.jrdf.util.test.TripleTestUtil.FOAF_MBOX;
import static org.jrdf.util.test.TripleTestUtil.FOAF_NAME;
import static org.jrdf.util.test.TripleTestUtil.FOAF_NICK;
import static org.jrdf.util.test.TripleTestUtil.triple;
import static org.jrdf.vocabulary.XSD.BOOLEAN;
import static org.jrdf.vocabulary.XSD.DATE_TIME;
import static org.jrdf.vocabulary.XSD.DECIMAL;
import static org.jrdf.vocabulary.XSD.DOUBLE;
import static org.jrdf.vocabulary.XSD.INTEGER;
import static org.jrdf.vocabulary.XSD.STRING;

public final class SableCcSparqlParserIntegrationTest {
    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final Graph GRAPH = FACTORY.getGraph();
    private static final URI PURL_DATE = create("http://purl.org/dc/elements/1.1/date");
    private static final Literal LITERAL = createLiteral("The Pragmatic Programmer");
    private static final Expression BOOK1_AND_2_CONJUNCTION
        = new Conjunction(BOOK_1_DC_TITLE_ID_1, BOOK_2_DC_TITLE_ID_2);
    private static final Expression BOOK1_AND_2_AND_3_CONJUNCTION
        = new Conjunction(BOOK1_AND_2_CONJUNCTION, BOOK_3_DC_TITLE_ID_3);
    private static final Expression BOOK1_AND_2_UNION
        = new Union(BOOK_1_DC_TITLE_ID_1, BOOK_2_DC_TITLE_ID_2);
    private static final Expression BOOK1_AND_2_AND_3_UNION
        = new Union(BOOK1_AND_2_UNION, BOOK_3_DC_TITLE_ID_3);
    private static final Expression BOOK1_AND_2_AND_EMPTY_UNION
        = new Union(BOOK1_AND_2_UNION, EMPTY_CONSTRAINT);
    private static final Expression EMPTY_AND_BOOK1_AND_2_UNION = new Union(
        new Union(EMPTY_CONSTRAINT, BOOK_1_DC_TITLE_ID_1), BOOK_2_DC_TITLE_ID_2);
    private static final Expression FOAF_NAME_EXP_1 = triple("x", FOAF_NAME, "name", 1);
    private static final Expression FOAF_NICK_EXP_2 = triple("x", FOAF_NICK, "nick", 2);
    private static final Expression FOAF_ALIAS_EXP_2 = triple("x", FOAF_NICK, "alias", 2);
    private static final Expression FOAF_MBOX_EXP_3 = triple("x", FOAF_MBOX, "mbox", 3);
    private static final Expression FOAF_ALIAS_EXP_3 = triple("x", FOAF_MBOX, "alias", 3);
    private static final Expression FOAF_NAME_EXP_3 = triple("x", FOAF_NAME, "name", 3);
    private static final Expression FOAF_MBOX_EXP_4 = triple("x", FOAF_MBOX, "mbox", 4);
    private static final Expression DC_DATE_EXP_2 = triple("x", PURL_DATE, "date", 2);
    private QueryParser parser;

    @Before
    public void setUp() throws Exception {
        AttributeComparator newAttributeComparator = FACTORY.getNewAttributeComparator();
        SortedAttributeFactory newSortedAttributeFactory = new SortedAttributeFactoryImpl(newAttributeComparator, 1);
        parser = new SableCcSparqllParser(FACTORY.getNewParserFactory(), FACTORY.getNewGraphRelationFactory(),
            newSortedAttributeFactory);
    }

    @Test
    public void singleConstraint() throws Exception {
        checkConstraintExpression("SELECT * " +
            "WHERE  { <http://example.org/book/book1> <http://purl.org/dc/elements/1.1/title> ?title }",
            BOOK_1_DC_TITLE_ID_1);
    }

    @Test
    public void singleConstraint2() throws Exception {
        checkConstraintExpression("SELECT * " +
            "WHERE  { <http://example.org/book/book2> <http://purl.org/dc/elements/1.1/title> ?title }",
            BOOK_2_DC_TITLE_ID_1);
    }

    @Test
    public void prefix() throws Exception {
        checkConstraintExpression("PREFIX examplebook: <http://example.org/book/> \n" +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> \n" +
            "SELECT * " +
            "WHERE { examplebook:book1 dc:title ?title . examplebook:book2 dc:title ?title }",
            BOOK1_AND_2_CONJUNCTION);
    }

    @Test
    public void twoConstraints() throws Exception {
        checkConstraintExpression("SELECT * " +
            "WHERE  { <http://example.org/book/book1> <http://purl.org/dc/elements/1.1/title> ?title . " +
            "<http://example.org/book/book2> <http://purl.org/dc/elements/1.1/title> ?title }",
            BOOK1_AND_2_CONJUNCTION);
    }

    @Test
    public void twoConstraints2() throws Exception {
        checkConstraintExpression("SELECT * WHERE { ?s ?p ?o . {} } ", new Conjunction(ANY_SPO, EMPTY_CONSTRAINT));
    }

    @Test
    public void twoNestConstraintsInnerRight() throws Exception {
        checkConstraintExpression("SELECT * \n" +
            "WHERE { <http://example.org/book/book1> <http://purl.org/dc/elements/1.1/title> ?title . " +
            "{<http://example.org/book/book2> <http://purl.org/dc/elements/1.1/title> ?title }}",
            BOOK1_AND_2_CONJUNCTION);
    }

    @Test
    public void treeConstraints() throws Exception {
        checkConstraintExpression("SELECT * " +
            "WHERE  { <http://example.org/book/book1> <http://purl.org/dc/elements/1.1/title> ?title . " +
            "<http://example.org/book/book2> <http://purl.org/dc/elements/1.1/title> ?title . " +
            "<http://example.org/book/book3> <http://purl.org/dc/elements/1.1/title> ?title }",
            BOOK1_AND_2_AND_3_CONJUNCTION);
    }

    @Test
    public void unionConstraint() throws Exception {
        checkConstraintExpression("SELECT * \n" +
            "WHERE {{ <http://example.org/book/book1> <http://purl.org/dc/elements/1.1/title> ?title} \n" +
            "UNION {<http://example.org/book/book2> <http://purl.org/dc/elements/1.1/title> ?title }}",
            BOOK1_AND_2_UNION);
    }

    @Test
    public void testUnionConstraint2() throws Exception {
        checkConstraintExpression("SELECT * \n" +
            "WHERE {{ <http://example.org/book/book1> <http://purl.org/dc/elements/1.1/title> ?title} \n" +
            "UNION {<http://example.org/book/book2> <http://purl.org/dc/elements/1.1/title> ?title } \n" +
            "UNION {<http://example.org/book/book3> <http://purl.org/dc/elements/1.1/title> ?title }}",
            BOOK1_AND_2_AND_3_UNION);
    }

    @Test
    public void unionConstraint3() throws Exception {
        checkConstraintExpression("SELECT * \n" +
            "WHERE {{ <http://example.org/book/book1> <http://purl.org/dc/elements/1.1/title> ?title} \n" +
            "UNION {<http://example.org/book/book2> <http://purl.org/dc/elements/1.1/title> ?title } \n" +
            "UNION { }}", BOOK1_AND_2_AND_EMPTY_UNION);
    }

    @Test
    public void unionConstraint4() throws Exception {
        checkConstraintExpression("SELECT * \n" +
            "WHERE {{} UNION { <http://example.org/book/book1> <http://purl.org/dc/elements/1.1/title> ?title} \n" +
            "UNION {<http://example.org/book/book2> <http://purl.org/dc/elements/1.1/title> ?title } }",
            EMPTY_AND_BOOK1_AND_2_UNION);
    }

    @Test
    public void singleOptionalConstraint() throws Exception {
        Optional optional1 = new Optional(FOAF_NAME_EXP_1, FOAF_NICK_EXP_2);
        checkConstraintExpression("SELECT * " +
            "WHERE { { ?x <http://xmlns.com/foaf/0.1/name> ?name } OPTIONAL " +
            "{ ?x <http://xmlns.com/foaf/0.1/nick> ?nick } }", optional1);
    }

    @Test
    public void optionalConstraint1() throws Exception {
        Optional optional1 = new Optional(FOAF_NICK_EXP_2, FOAF_MBOX_EXP_3);
        Optional optional2 = new Optional(FOAF_NAME_EXP_1, optional1);
        checkConstraintExpression("SELECT * WHERE  { ?x <http://xmlns.com/foaf/0.1/name> ?name . \n" +
            "         OPTIONAL { ?x <http://xmlns.com/foaf/0.1/nick> ?nick\n" +
            "         OPTIONAL { ?x <http://xmlns.com/foaf/0.1/mbox> ?mbox } }\n" +
            "       }", optional2);
    }

    @Test
    public void optionalConstraint2() throws Exception {
        Optional optional1 = new Optional(FOAF_NAME_EXP_1, FOAF_ALIAS_EXP_2);
        Optional optional2 = new Optional(optional1, FOAF_ALIAS_EXP_3);
        checkConstraintExpression("SELECT * WHERE  { ?x <http://xmlns.com/foaf/0.1/name> ?name .\n" +
            "         OPTIONAL { ?x <http://xmlns.com/foaf/0.1/nick> ?alias }\n" +
            "         OPTIONAL { ?x <http://xmlns.com/foaf/0.1/mbox> ?alias }\n" +
            "       }", optional2);
    }

    @Test
    public void optionalConstraint3() throws Exception {
        Optional optional1 = new Optional(FOAF_NAME_EXP_1, FOAF_ALIAS_EXP_2);
        Optional optional2 = new Optional(optional1, FOAF_ALIAS_EXP_3);
        checkConstraintExpression("SELECT * WHERE  { { ?x <http://xmlns.com/foaf/0.1/name> ?name } .\n" +
            "         OPTIONAL { ?x <http://xmlns.com/foaf/0.1/nick> ?alias }\n" +
            "         OPTIONAL { ?x <http://xmlns.com/foaf/0.1/mbox> ?alias }\n" +
            "       }", optional2);
    }

    @Test
    public void complicatedOptional1() throws Exception {
        Optional optional1 = new Optional(FOAF_NAME_EXP_1, FOAF_NICK_EXP_2);
        Optional optional2 = new Optional(FOAF_NAME_EXP_3, FOAF_MBOX_EXP_4);
        Conjunction expectedExpression = new Conjunction(optional1, optional2);
        checkConstraintExpression("SELECT * WHERE  { \n" +
            "  { ?x <http://xmlns.com/foaf/0.1/name> ?name " +
            "OPTIONAL { ?x <http://xmlns.com/foaf/0.1/nick> ?nick }} .\n" +
            "  { ?x <http://xmlns.com/foaf/0.1/name> ?name " +
            "OPTIONAL { ?x <http://xmlns.com/foaf/0.1/mbox> ?mbox }}\n" +
            "}", expectedExpression);
    }

    @Test
    public void complicatedOptional2() throws Exception {
        Optional optional1 = new Optional(FOAF_NAME_EXP_1, FOAF_NICK_EXP_2);
        Optional optional2 = new Optional(FOAF_NAME_EXP_3, FOAF_MBOX_EXP_4);
        Optional expectedExpression = new Optional(optional1, optional2);
        checkConstraintExpression("SELECT * WHERE  { \n" +
            "  { ?x <http://xmlns.com/foaf/0.1/name> ?name " +
            "OPTIONAL { ?x <http://xmlns.com/foaf/0.1/nick> ?nick }} " +
            "OPTIONAL { ?x <http://xmlns.com/foaf/0.1/name> ?name " +
            "OPTIONAL { ?x <http://xmlns.com/foaf/0.1/mbox> ?mbox }}\n" +
            "}", expectedExpression);
    }

    @Test
    public void literalQuote() throws Exception {
        Expression spPrag1 = triple("s", "p", LITERAL, 1);
        checkConstraintExpression("SELECT * WHERE { ?s ?p 'The Pragmatic Programmer' } ", spPrag1);
        Expression spPrag2 = triple("s", "p", LITERAL, 2);
        checkConstraintExpression("SELECT * WHERE { ?s ?p \"The Pragmatic Programmer\" } ", spPrag2);
    }

    @Test
    public void literalWithLanguage() throws Exception {
        Expression spHello1 = triple("s", "p", createLiteral("hello", "en"), 1);
        checkConstraintExpression("SELECT * WHERE { ?s ?p 'hello'@en }", spHello1);
        Expression spHello2 = triple("s", "p", createLiteral("hello", "en"), 2);
        checkConstraintExpression("SELECT * WHERE { ?s ?p \"hello\"@en }", spHello2);
    }

    @Test
    public void literalWithDatatype() throws Exception {
        Literal one = createLiteral("1", INTEGER);
        Literal helloString = createLiteral("hello", STRING);
        Expression spHello1 = triple("s", "p", one, 1);
        Expression spHello2 = triple("s", "p", helloString, 2);
        Expression spHello3 = triple("s", "p", helloString, 3);
        checkConstraintExpression("SELECT * WHERE { ?s ?p '1'^^<http://www.w3.org/2001/XMLSchema#integer> }", spHello1);
        checkConstraintExpression("SELECT * WHERE { ?s ?p 'hello'^^<http://www.w3.org/2001/XMLSchema#string> }",
            spHello2);
        checkConstraintExpression("PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>" +
            " SELECT * WHERE { ?s ?p 'hello'^^xsd:string }", spHello3);
    }

    @Test
    public void testLiteralWithIncorrectPrefix() {
        String message = "Unable to parse query syntax token: xsd ";
        assertThrows(InvalidQuerySyntaxException.class, message, new AssertThrows.Block() {
            public void execute() throws Throwable {
                parseQuery(" SELECT * WHERE { ?s ?p 'hello'^^xsd:string }");
            }
        });
    }

    @Test
    public void unsignedNumericIntegerLiteral() throws Exception {
        Literal one = createLiteral("1", INTEGER);
        Literal posTwo = createLiteral("+02", INTEGER);
        Expression spOne = triple("s", "p", one, 1);
        Expression spPosTwo = triple("s", "p", posTwo, 2);
        checkConstraintExpression("SELECT * WHERE { ?s ?p 1 }", spOne);
        checkConstraintExpression("SELECT * WHERE { ?s ?p +02 }", spPosTwo);
    }

    @Test
    public void unsignedNumericDecimalLiteral() throws Exception {
        Literal onePointThree = createLiteral("1.3", DECIMAL);
        Literal onePointThreeZeroZeroZero = createLiteral("1.300", DECIMAL);
        Literal negOnePointThreeZeroZeroZero = createLiteral("-1.300", DECIMAL);
        Expression spOnePointThree = triple("s", "p", onePointThree, 1);
        Expression spOnePointThreeZeroZeroZero = triple("s", "p",
            onePointThreeZeroZeroZero, 2);
        Expression spNegOnePointThreeZeroZeroZero = triple("s", "p",
            negOnePointThreeZeroZeroZero, 3);
        checkConstraintExpression("SELECT * WHERE { ?s ?p 1.3 }", spOnePointThree);
        checkConstraintExpression("SELECT * WHERE { ?s ?p 1.300 }", spOnePointThreeZeroZeroZero);
        checkConstraintExpression("SELECT * WHERE { ?s ?p -1.300 }", spNegOnePointThreeZeroZeroZero);
    }

    @Test
    public void unsignedNumericDoubleLiteral() throws Exception {
        Literal doubleLiteral = createLiteral("1.3e6", DOUBLE);
        Literal signedDoubleLiteral = createLiteral("+1.3E06", DOUBLE);
        Expression spDouble = triple("s", "p", doubleLiteral, 1);
        Expression spSignedDouble = triple("s", "p", signedDoubleLiteral, 2);
        checkConstraintExpression("SELECT * WHERE { ?s ?p 1.3e6 }", spDouble);
        checkConstraintExpression("SELECT * WHERE { ?s ?p +1.3E06 }", spSignedDouble);
    }

    @Test
    public void booleanLiteral() throws Exception {
        final Literal trueLiteral = createLiteral("true", BOOLEAN);
        final Expression spTrue = triple("s", "p", trueLiteral, 1);
        checkConstraintExpression("SELECT * WHERE { ?s ?p true }", spTrue);
    }

    @Test
    public void booleanLiteralFalse() throws Exception {
        final Literal falseLiteral = createLiteral("false", BOOLEAN);
        final Expression spFalse = triple("s", "p", falseLiteral, 1);
        checkConstraintExpression("SELECT * WHERE { ?s ?p false }", spFalse);
    }

    @Test
    public void filterWithGreaterThan() throws Exception {
        Expression spo = triple("s", "p", "o");
        Expression urnCreated = triple("s", create("urn:created"), "date", 2);
        LogicExpression dateGtDate = var("date").gt(createLiteral("2010-03-12T22:36:40Z", DATE_TIME));
        Expression expression = new Filter(new Conjunction(spo, urnCreated), dateGtDate);
        checkConstraintExpression(
            "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> " +
            "SELECT * " +
            "WHERE { ?s ?p ?o . " +
            "?s <urn:created> ?date ." +
            "FILTER ( ?date > \"2010-03-12T22:36:40Z\"^^xsd:dateTime ) }", expression);
    }

    @Test
    public void filterInTheMiddle() throws Exception {
        final Expression expression1 = triple("s", create("urn:time"), "time", 1);
        final Expression expression2 = triple("s", create("urn:variable"), "variable", 2);
        final LogicExpression timeLtValue = var("time").lt(createLiteral("1248040800000", DATE_TIME));
        final Expression filter = new Filter(expression2, timeLtValue);
        final Conjunction expression = new Conjunction(expression1, filter);
        checkConstraintExpression(
            "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>\n" +
            "SELECT *\n" +
            "WHERE {\n" +
            "?s <urn:time> ?time .\n" +
            "FILTER ( ?time < \"1248040800000\"^^xsd:dateTime ) .\n" +
            "?s <urn:variable> ?variable .\n}", expression);
    }

    @Test
    public void filterUntypedLiteral() throws Exception {
        final LogicExpression strVarOEqUnknown = str(oVar("o")).eq(literal("unknown"));
        final Expression matchSpoAndFilter = new Filter(triple("s", "p", "o"), strVarOEqUnknown);
        checkConstraintExpression(
            "SELECT * " +
            "WHERE { ?s ?p ?o . " +
            "FILTER( str(?o) = \"unknown\" ) }", matchSpoAndFilter);
    }

    @Test
    public void filterWithTypeLiteral() throws Exception {
        final LogicExpression strVarOEqUnknown = str(oVar("o")).eq(literal("unknown", STRING));
        final Expression matchSpoAndFilter = new Filter(triple("s", "p", "o"), strVarOEqUnknown);
        checkConstraintExpression("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
            "SELECT *\n" +
            "WHERE { ?s ?p ?o . " +
            "FILTER(str(?o) = \"unknown\"^^xsd:string) }", matchSpoAndFilter);
    }

    @Test
    public void booleanNotStrOperators() throws Exception {
        final LogicExpression notStrVarNameEqAbc = not(str(oVar("name")).eq(literal("abc")));
        final Expression matchNameAndFilter = new Filter(FOAF_NAME_EXP_1, notStrVarNameEqAbc);
        checkConstraintExpression(
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
            "SELECT ?name\n" +
            "WHERE { ?x foaf:name ?name . " +
            "FILTER ( !(str(?name) = \"abc\")) }", matchNameAndFilter);
    }

    @Test
    public void neqExpression() throws Exception {
        final Expression matchNameAndFilter = new Filter(FOAF_NAME_EXP_1, str(oVar("name")).neq(literal("abc")));
        checkConstraintExpression("PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
            "SELECT ?name\n" +
            "WHERE { ?x foaf:name ?name .\n" +
            "FILTER ( str(?name) != \"abc\") }", matchNameAndFilter);
    }

    @Test
    public void eqLangOperator() throws Exception {
        final LogicExpression eqnExp = lang(oVar("name")).eq(literal("en"));
        final Expression matchNameAndFilter = new Filter(FOAF_NAME_EXP_1, eqnExp);
        checkConstraintExpression("PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
            "SELECT ?name\n" +
            "WHERE { ?x foaf:name ?name .\n" +
            "FILTER ( lang(?name) = \"en\") }", matchNameAndFilter);
    }

    @Test
    public void oppositeEqLangOperator() throws Exception {
        final LogicExpression eqnExp = literal("en").eq(lang(oVar("name")));
        final Expression filterExpression = new Filter(FOAF_NAME_EXP_1, eqnExp);
        checkConstraintExpression(
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
            "SELECT ?name\n" +
            "WHERE { ?x foaf:name ?name .\n" +
            "FILTER ( \"en\" = lang(?name)) }", filterExpression);
    }

    @Test
    public void booleanNotOperator() throws Exception {
        final LogicExpression boundVar = bound(oVar("name"));
        final LogicExpression notBound = new LogicNotExpression(boundVar);
        final Expression matchNameAndFilter = new Filter(FOAF_NAME_EXP_1, notBound);
        checkConstraintExpression(
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
            "SELECT ?name\n" +
            "WHERE { ?x foaf:name ?name .\n" +
            "FILTER ( !bound(?name) ) }", matchNameAndFilter);
    }

    @Test
    public void threePartOptional() throws Exception {
        final Expression optional = new Optional(FOAF_NAME_EXP_1, DC_DATE_EXP_2);
        final LogicExpression boundVar = bound(oVar("date"));
        final Expression optionalAndFilter = new Filter(optional, boundVar);
        checkConstraintExpression(
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
            "SELECT ?name\n" +
            "WHERE { ?x foaf:name ?name .\n" +
            "OPTIONAL { ?x dc:date ?date } .\n" +
            "FILTER ( bound(?date) ) }", optionalAndFilter);
    }

    @Test
    public void rareBooleanFilter() throws Exception {
        LogicExpression andExp = new LogicAndExpression(bound(sVar("x")), bound(oVar("name")));
        LogicExpression equalsExp = new EqualsExpression(andExp, TRUE_EXPRESSION);
        Expression filterExpression = new Filter(FOAF_NAME_EXP_1, equalsExp);
        checkConstraintExpression(
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
            "SELECT ?name\n" +
            "WHERE { ?x foaf:name ?name .\n" +
            "FILTER ( (bound(?x) && bound(?name)) = true) }", filterExpression);
    }

    @Test
    public void eqLangOperatorDiffVariables() throws Exception {
        LogicExpression eqnExp = lang(oVar("name")).eq(lang(oVar("nick")));
        Expression conj = new Conjunction(FOAF_NAME_EXP_1, FOAF_NICK_EXP_2);
        Expression filterExpression = new Filter(conj, eqnExp);
        checkConstraintExpression(
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
            "SELECT ?name\n" +
            "WHERE { ?x foaf:name ?name . ?x foaf:nick ?nick " +
            "FILTER ( lang(?name) = lang(?nick) ) }", filterExpression);
    }

    @Test
    public void languageTag() throws Exception {
        LogicExpression eqnExp = lang(oVar("name")).eq(literal("en"));
        Expression filterExpression = new Filter(FOAF_NAME_EXP_1, eqnExp);
        checkConstraintExpression(
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
            "SELECT ?name\n" +
            "WHERE { ?x foaf:name ?name \n" +
            "FILTER ( lang(?name) = \"en\" ) }", filterExpression);
    }

    @Test
    public void logicalAndExpression() throws Exception {
        LogicExpression equalsExpression = str(oVar("name")).eq(createLiteral("abc"));
        LogicExpression boundExpression = bound(sVar("x"));
        LogicExpression andExpression = new LogicAndExpression(equalsExpression, boundExpression);
        Expression filterExpression = new Filter(FOAF_NAME_EXP_1, andExpression);
        checkConstraintExpression(
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
            "SELECT ?name\n" +
            "WHERE { ?x foaf:name ?name .\n" +
            "FILTER ( str(?name) = \"abc\" && bound(?x) ) }", filterExpression);
    }

    @Test
    public void logicalOrExpression() throws Exception {
        LogicExpression equalsExpression = str(oVar("name")).eq(createLiteral("abc"));
        LogicExpression boundExpression = bound(sVar("x"));
        LogicExpression orExpression = new LogicOrExpression(equalsExpression, boundExpression);
        Expression filterExpression = new Filter(FOAF_NAME_EXP_1, orExpression);
        checkConstraintExpression(
            "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
            "SELECT ?name\n" +
            "WHERE { ?x foaf:name ?name .\n" +
            "FILTER ( str(?name) = \"abc\" || bound(?x) ) }", filterExpression);
    }

    @Test
    public void simpleAskQuery() throws Exception {
        Expression spPrag1 = triple("s", "p", LITERAL, 1);
        checkConstraintExpression("ASK WHERE { ?s ?p 'The Pragmatic Programmer' } ", spPrag1);
    }

    @Test
    public void trueBoolean() throws Exception {
        Expression spPrag1 = triple("s", "p", LITERAL, 1);
        Filter filter = new Filter(spPrag1, TRUE_EXPRESSION);
        checkConstraintExpression("SELECT * WHERE { ?s ?p 'The Pragmatic Programmer' FILTER (TRUE) } ", filter);
    }

    @Test
    public void falseBoolean() throws Exception {
        Expression spPrag1 = triple("s", "p", LITERAL, 1);
        Filter filter = new Filter(spPrag1, FALSE_EXPRESSION);
        checkConstraintExpression("SELECT * WHERE { ?s ?p 'The Pragmatic Programmer' FILTER (FALSE) } ", filter);
    }

    @Test
    public void prefixInFilteredAsk() throws Exception {
        LogicExpression equalsExpression = str(oVar("o")).eq(createLiteral("unknown", STRING));
        final Expression filterExpression = new Filter(triple("s", "p", "o"), equalsExpression);
        checkConstraintExpression("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
            "ASK WHERE { ?s ?p ?o . FILTER(str(?o) = \"unknown\"^^xsd:string) }", filterExpression);
    }

    private Attribute sVar(final String varName) {
        return new AttributeImpl(new VariableName(varName), new SubjectNodeType());
    }

    private Attribute oVar(final String varName) {
        return new AttributeImpl(new VariableName(varName), new ObjectNodeType());
    }

    private void checkConstraintExpression(String queryString, Expression expectedExpression) throws Exception {
        Query query = parseQuery(queryString);
        Expression actualExpression = getExpression(query);
        assertThat(actualExpression, equalTo(expectedExpression));
    }

    private Expression getExpression(Query query) {
        try {
            if (query instanceof SelectQueryImpl) {
                Expression expression = getExpressionField(query, SelectQueryImpl.class, "expression");
                return getExpressionField(expression, Projection.class, "nextExpression");
            } else if (query instanceof AskQueryImpl) {
                Expression expression = getExpressionField(query, AskQueryImpl.class, "expression");
                return getExpressionField(expression, Ask.class, "nextExpression");
            } else {
                throw new IllegalArgumentException();
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({ "unchecked" })
    private Expression getExpressionField(Object obj, Class<?> cls, String fieldName) throws IllegalAccessException {
        Field field = ReflectTestUtil.getField(cls, fieldName);
        field.setAccessible(true);
        return (Expression) field.get(obj);
    }

    private Query parseQuery(String queryString) throws InvalidQuerySyntaxException {
        return parser.parseQuery(GRAPH, queryString);
    }
}
