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

import junit.framework.TestCase;
import static org.jrdf.graph.AnyNode.ANY_NODE;
import org.jrdf.graph.Literal;
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
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.mem.ComparatorFactoryImpl;
import org.jrdf.query.relation.mem.ValueOperationImpl;
import org.jrdf.query.relation.operation.BooleanEvaluator;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_LITERAL;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_LITERAL_L1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_LITERAL_L2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_LITERAL_L3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_FOO1_LITERAL;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_FOO1_LITERAL_L1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_FOO1_LITERAL_L2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createTuple;
import static org.jrdf.util.test.NodeTestUtil.createLiteral;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuan-Fang Li
 * @version $Id$
 */
public class SimpleBooleanEvaluatorUnitTest extends TestCase {
    private static final NodeComparator NODE_COMPARATOR = new ComparatorFactoryImpl().createNodeComparator();

    private static final Literal LITERAL_L1_LANG = createLiteral("fr");
    private static final Literal LITERAL_L1_LANG1 = createLiteral("hello", "en");
    private static final Literal LITERAL_L1_LANG2 = createLiteral("hello", "fr");
    private static final Tuple TEST_VARBAR_LITERAL_TUPLE_1 = createTuple(VAR_BAR1_LITERAL_L1);
    private static final Tuple TEST_VARBAR_LITERAL_TUPLE_2 = createTuple(VAR_BAR1_LITERAL_L2);
    private static final Tuple TEST_VARBAR_LITERAL_TUPLE_3 = createTuple(VAR_BAR1_LITERAL_L3);
    private static final Tuple TEST_VARFOO_LITERAL_TUPLE_1 = createTuple(VAR_FOO1_LITERAL_L1);
    private static final Tuple TEST_VARFOO_LITERAL_TUPLE_2 = createTuple(VAR_FOO1_LITERAL_L2);
    private static final Tuple TEST_TUPLE_1_2 = createTuple(VAR_BAR1_LITERAL_L1, VAR_FOO1_LITERAL_L1);
    private static final ValueOperation ANY_NODE_LANG_VO = new ValueOperationImpl(ANY_NODE);
    private static final ValueOperation ANY_NODE_EQUALS_VO = new ValueOperationImpl(ANY_NODE);
    private static final ValueOperation ANY_NODE_STR_VO = new ValueOperationImpl(ANY_NODE);

    private BooleanEvaluator evaluator;
    private LogicExpression expression;

    @Override
    protected void setUp() throws Exception {
        evaluator = new SimpleBooleanEvaluator(NODE_COMPARATOR);
    }

    public void testLessThanExpression() {
        SingleValue valueExp = new SingleValue(createAVO(VAR_BAR1_LITERAL, ANY_NODE_EQUALS_VO));
        SingleValue valueExp1 = new SingleValue(VAR_BAR1_LITERAL_L2);
        expression = new LessThanExpression(valueExp, valueExp1);
        boolean result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, expression);
        assertTrue(result);
        result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_3, expression);
        assertFalse(result);
    }

    public void testBoundExpression() {
        ValueOperation vo = new ValueOperationImpl(ANY_NODE);
        expression = new BoundOperator(createAVO(VAR_BAR1_LITERAL, vo));
        boolean result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, expression);
        assertTrue(result);
        result = evaluator.evaluate(TEST_VARFOO_LITERAL_TUPLE_1, expression);
        assertFalse(result);
    }

    public void testNEqualsExpression() {
        SingleValue valueExp = new SingleValue(createAVO(VAR_BAR1_LITERAL, ANY_NODE_EQUALS_VO));
        SingleValue valueExp1 = new SingleValue(VAR_BAR1_LITERAL_L2);
        expression = new NEqualsExpression(valueExp, valueExp1);
        boolean result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, expression);
        assertTrue(result);
        result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_2, expression);
        assertFalse(result);
    }

    public void testEqualsExpression() {
        Expression valueExp = new StrOperator(createAVO(VAR_FOO1_LITERAL, ANY_NODE_STR_VO));
        SingleValue valueExp1 = new SingleValue(VAR_FOO1_LITERAL_L1);
        expression = new EqualsExpression(valueExp, valueExp1);
        boolean result = evaluator.evaluate(TEST_VARFOO_LITERAL_TUPLE_1, expression);
        assertTrue(result);
        result = evaluator.evaluate(TEST_VARFOO_LITERAL_TUPLE_2, expression);
        assertFalse(result);
        result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, expression);
        assertFalse(result);
        result = evaluator.evaluate(TEST_TUPLE_1_2, expression);
        assertTrue(result);
    }

    public void testLangOperator() {
        Expression valueExp = new LangOperator(createAVO(VAR_FOO1_LITERAL, ANY_NODE_LANG_VO));
        ValueOperation vo1 = new ValueOperationImpl(createLiteral("en"));
        Expression valueExp1 = new SingleValue(createAVO(VAR_FOO1_LITERAL, vo1));
        expression = new EqualsExpression(valueExp, valueExp1);
        boolean result = evaluator.evaluate(TEST_VARFOO_LITERAL_TUPLE_1, expression);
        assertFalse(result);

        valueExp1 = new SingleValue(createAVO(VAR_BAR1_LITERAL, ANY_NODE_STR_VO));
        expression = new EqualsExpression(valueExp, valueExp1);
        result = evaluator.evaluate(TEST_VARFOO_LITERAL_TUPLE_1, expression);
        assertFalse(result);
    }

    public void testLangTags() {
        Expression langExp = new LangOperator(createAVO(VAR_BAR1_LITERAL, ANY_NODE_LANG_VO));
        ValueOperation vo1 = new ValueOperationImpl(LITERAL_L1_LANG);
        SingleValue valueExp1 = new SingleValue(createAVO(VAR_BAR1_LITERAL, vo1));
        expression = new NEqualsExpression(langExp, valueExp1);

        Tuple tuple = createTuple(VAR_BAR1_LITERAL_L1);
        boolean result = evaluator.evaluate(tuple, expression);
        assertTrue(result);

        ValueOperation vo2 = new ValueOperationImpl(LITERAL_L1_LANG2);
        tuple = createTuple(createAVO(VAR_BAR1_LITERAL, vo2));
        result = evaluator.evaluate(tuple, expression);
        assertFalse(result);

        vo2 = new ValueOperationImpl(LITERAL_L1_LANG1);
        tuple = createTuple(createAVO(VAR_BAR1_LITERAL, vo2));
        result = evaluator.evaluate(tuple, expression);
        assertTrue(result);

        expression = new EqualsExpression(langExp, valueExp1);
        result = evaluator.evaluate(tuple, expression);
        assertFalse(result);
    }

    public void testTrueFalse() {
        boolean result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, TRUE_EXPRESSION);
        assertTrue(result);

        result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, FALSE_EXPRESSION);
        assertFalse(result);
    }

    public void testAndExp() {
        LogicExpression andExp =
            new LogicAndExpression(TRUE_EXPRESSION, FALSE_EXPRESSION);
        boolean result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, andExp);
        assertFalse(result);

        andExp = new LogicAndExpression(FALSE_EXPRESSION, TRUE_EXPRESSION);
        result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, andExp);
        assertFalse(result);
    }

    public void testOrExp() {
        LogicExpression andExp =
            new LogicOrExpression(TRUE_EXPRESSION, FALSE_EXPRESSION);
        boolean result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, andExp);
        assertTrue(result);

        andExp = new LogicOrExpression(FALSE_EXPRESSION, TRUE_EXPRESSION);
        result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, andExp);
        assertTrue(result);
    }

    public void testNotExp() {
        LogicExpression notExp = new LogicNotExpression(TRUE_EXPRESSION);
        boolean result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, notExp);
        assertFalse(result);

        notExp = new LogicNotExpression(FALSE_EXPRESSION);
        result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, notExp);
        assertTrue(result);
    }

    private static Map<Attribute, ValueOperation> createAVO(Attribute key, ValueOperation value) {
        Map<Attribute, ValueOperation> avo = new HashMap<Attribute, ValueOperation>();
        avo.put(key, value);
        return avo;
    }
}
