/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.query.relation.operation.mem.logic;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jrdf.graph.AnyNode.ANY_NODE;
import org.jrdf.graph.Literal;
import org.jrdf.graph.Node;
import org.jrdf.graph.NodeComparator;
import org.jrdf.query.expression.BoundOperator;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.LangOperator;
import org.jrdf.query.expression.SingleValue;
import org.jrdf.query.expression.StrOperator;
import org.jrdf.query.expression.logic.EqualsExpression;
import static org.jrdf.query.expression.logic.FalseExpression.FALSE_EXPRESSION;
import org.jrdf.query.expression.logic.LessThanExpression;
import org.jrdf.query.expression.logic.LogicAndExpression;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.expression.logic.LogicNotExpression;
import org.jrdf.query.expression.logic.LogicOrExpression;
import org.jrdf.query.expression.logic.NEqualsExpression;
import static org.jrdf.query.expression.logic.TrueExpression.TRUE_EXPRESSION;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.mem.ComparatorFactoryImpl;
import org.jrdf.query.relation.operation.BooleanEvaluator;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_LITERAL;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_LITERAL_L1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_LITERAL_L2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_LITERAL_L3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_FOO1_LITERAL;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_FOO1_LITERAL_L1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_FOO1_LITERAL_L2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createAttValue;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createTuple;
import static org.jrdf.util.test.NodeTestUtil.createLiteral;
import org.junit.Before;
import org.junit.Test;

public class BooleanEvaluatorImplUnitTest {
    private static final NodeComparator NODE_COMPARATOR = new ComparatorFactoryImpl().createNodeComparator();
    private static final Literal LITERAL_L1_LANG = createLiteral("fr");
    private static final Literal LITERAL_L1_LANG1 = createLiteral("hello", "en");
    private static final Literal LITERAL_L1_LANG2 = createLiteral("hello", "fr");
    private static final Tuple VAR_BAR1_LITERAL_HELLO_TUPLE = createTuple(VAR_BAR1_LITERAL_L1);
    private static final Tuple VAR_BAR1_LITERAL_THERE_TUPLE = createTuple(VAR_BAR1_LITERAL_L2);
    private static final Tuple VAR_BAR1_LITERAL_WORLD_TUPLE = createTuple(VAR_BAR1_LITERAL_L3);
    private static final Tuple VAR_FOO1_LITERAL_HELLO_TUPLE = createTuple(VAR_FOO1_LITERAL_L1);
    private static final Tuple VAR_FOO1_LITERAL_THERE_TUPLE = createTuple(VAR_FOO1_LITERAL_L2);
    private static final Tuple TEST_TUPLE_1_2 = createTuple(VAR_BAR1_LITERAL_L1, VAR_FOO1_LITERAL_L1);
    private static final SingleValue VAR_BAR1_ANY = new SingleValue(createAttValue(VAR_BAR1_LITERAL, ANY_NODE));
    private static final SingleValue VAR_BAR1_THERE = new SingleValue(VAR_BAR1_LITERAL_L2);
    private BooleanEvaluator<Boolean> evaluator;
    private LogicExpression expression;

    @Before
    public void createEvaluator() {
        evaluator = new BooleanEvaluatorImpl(NODE_COMPARATOR);
    }

    @Test
    public void equalsExpression() {
        // str(?bar1:literal=ANY)
        Expression valueExp = new StrOperator(createAttValue(VAR_FOO1_LITERAL, ANY_NODE));
        // ?foo1:literal=hello
        SingleValue valueExp1 = new SingleValue(VAR_FOO1_LITERAL_L1);
        expression = new EqualsExpression(valueExp, valueExp1);
        boolean result = evaluator.evaluate(VAR_FOO1_LITERAL_HELLO_TUPLE, expression);
        assertThat(result, is(true));
        result = evaluator.evaluate(VAR_FOO1_LITERAL_THERE_TUPLE, expression);
        assertThat(result, is(false));
        result = evaluator.evaluate(VAR_BAR1_LITERAL_HELLO_TUPLE, expression);
        assertThat(result, is(false));
        result = evaluator.evaluate(TEST_TUPLE_1_2, expression);
        assertThat(result, is(true));
    }

    @Test
    public void helloIsLessThanThereReturnsTrue() {
        // ?bar1:literal=ANY < ?bar1:literal=there
        expression = new LessThanExpression(VAR_BAR1_ANY, VAR_BAR1_THERE);
        // tuple={?bar1:literal=hello} => ?bar1:literal=hello < ?bar1:literal=there
        assertThat(evaluator.evaluate(VAR_BAR1_LITERAL_HELLO_TUPLE, expression), is(true));
    }

    @Test
    public void worldIsLessThanThereReturnsFalse() {
        // ?bar1:literal=ANY < ?bar1:literal=there
        expression = new LessThanExpression(VAR_BAR1_ANY, VAR_BAR1_THERE);
        // tuple={?bar1:literal=world} => ?bar1:literal=world < ?bar1:literal=there
        assertThat(evaluator.evaluate(VAR_BAR1_LITERAL_WORLD_TUPLE, expression), is(false));
    }

    @Test
    public void helloIsNotEqualToThereReturnsTrue() {
        // ?bar1:literal=ANY != ?bar1:literal=there
        expression = new NEqualsExpression(VAR_BAR1_ANY, VAR_BAR1_THERE);
        // tuple{?bar1:literal=hello} => ?bar1:literal=hello != ?bar1:literal=there
        assertThat(evaluator.evaluate(VAR_BAR1_LITERAL_HELLO_TUPLE, expression), is(true));
    }

    @Test
    public void helloIsNotEqualToHelloReturnsFalse() {
        // ?bar1:literal=ANY != ?bar1:literal=there
        expression = new NEqualsExpression(VAR_BAR1_ANY, VAR_BAR1_THERE);
        // tuple{?bar1:literal=there} => ?bar1:literal=there != ?bar1:literal=there
        assertThat(evaluator.evaluate(VAR_BAR1_LITERAL_THERE_TUPLE, expression), is(false));
    }

    @Test
    public void boundLiteralReturnsTrue() {
        // bound(?bar1:literal=ANY)
        expression = new BoundOperator(createAttValue(VAR_BAR1_LITERAL, ANY_NODE));
        // tuple{?bar1:literal=hello} => bound(?bar1:literal=hello)
        assertThat(evaluator.evaluate(VAR_BAR1_LITERAL_HELLO_TUPLE, expression), is(true));
    }

    @Test
    public void unboundLiteralReturnsFalse() {
        // bound(?bar1:literal=ANY)
        expression = new BoundOperator(createAttValue(VAR_BAR1_LITERAL, ANY_NODE));
        // tuple{?foo1:literal=hello} => bound(?bar1:literal=hello)
        assertThat(evaluator.evaluate(VAR_FOO1_LITERAL_HELLO_TUPLE, expression), is(false));
    }

    @Test
    public void langOperator() {
        // lang(?foo1:literal=ANY)
        Expression lhs = new LangOperator(createAttValue(VAR_FOO1_LITERAL, ANY_NODE));
        // ?foo1:literal=en
        Expression rhs = new SingleValue(createAttValue(VAR_FOO1_LITERAL, createLiteral("en")));
        // lang(?foo) = (?foo=en)
        expression = new EqualsExpression(lhs, rhs);
        // tuple{?foo1:literal=hello} = lang(?foo) = (?foo=en)
        boolean result = evaluator.evaluate(VAR_FOO1_LITERAL_HELLO_TUPLE, expression);
        assertThat(result, is(false));

        expression = new EqualsExpression(lhs, VAR_BAR1_ANY);
        result = evaluator.evaluate(VAR_FOO1_LITERAL_HELLO_TUPLE, expression);
        assertThat(result, is(false));
    }

    @Test
    public void langTags() {
        Expression langExp = new LangOperator(createAttValue(VAR_BAR1_LITERAL, ANY_NODE));
        Node vo1 = LITERAL_L1_LANG;
        SingleValue valueExp1 = new SingleValue(createAttValue(VAR_BAR1_LITERAL, vo1));
        expression = new NEqualsExpression(langExp, valueExp1);

        Tuple tuple = createTuple(VAR_BAR1_LITERAL_L1);
        boolean result = evaluator.evaluate(tuple, expression);
        assertThat(result, is(true));

        Node vo2 = LITERAL_L1_LANG2;
        tuple = createTuple(createAttValue(VAR_BAR1_LITERAL, vo2));
        result = evaluator.evaluate(tuple, expression);
        assertThat(result, is(false));

        vo2 = LITERAL_L1_LANG1;
        tuple = createTuple(createAttValue(VAR_BAR1_LITERAL, vo2));
        result = evaluator.evaluate(tuple, expression);
        assertThat(result, is(true));

        expression = new EqualsExpression(langExp, valueExp1);
        result = evaluator.evaluate(tuple, expression);
        assertThat(result, is(false));
    }

    @Test
    public void trueFalse() {
        boolean result = evaluator.evaluate(VAR_BAR1_LITERAL_HELLO_TUPLE, TRUE_EXPRESSION);
        assertThat(result, is(true));

        result = evaluator.evaluate(VAR_BAR1_LITERAL_HELLO_TUPLE, FALSE_EXPRESSION);
        assertThat(result, is(false));
    }

    @Test
    public void andExp() {
        LogicExpression andExp = new LogicAndExpression(TRUE_EXPRESSION, FALSE_EXPRESSION);
        boolean result = evaluator.evaluate(VAR_BAR1_LITERAL_HELLO_TUPLE, andExp);
        assertThat(result, is(false));

        andExp = new LogicAndExpression(FALSE_EXPRESSION, TRUE_EXPRESSION);
        result = evaluator.evaluate(VAR_BAR1_LITERAL_HELLO_TUPLE, andExp);
        assertThat(result, is(false));
    }

    @Test
    public void orExp() {
        LogicExpression andExp = new LogicOrExpression(TRUE_EXPRESSION, FALSE_EXPRESSION);
        boolean result = evaluator.evaluate(VAR_BAR1_LITERAL_HELLO_TUPLE, andExp);
        assertThat(result, is(true));

        andExp = new LogicOrExpression(FALSE_EXPRESSION, TRUE_EXPRESSION);
        result = evaluator.evaluate(VAR_BAR1_LITERAL_HELLO_TUPLE, andExp);
        assertThat(result, is(true));
    }

    @Test
    public void notExp() {
        LogicExpression notExp = new LogicNotExpression(TRUE_EXPRESSION);
        boolean result = evaluator.evaluate(VAR_BAR1_LITERAL_HELLO_TUPLE, notExp);
        assertThat(result, is(false));

        notExp = new LogicNotExpression(FALSE_EXPRESSION);
        result = evaluator.evaluate(VAR_BAR1_LITERAL_HELLO_TUPLE, notExp);
        assertThat(result, is(true));
    }
}
