package org.jrdf.query.relation.operation.mem.logic;

import junit.framework.TestCase;
import static org.jrdf.graph.AnyNode.ANY_NODE;
import org.jrdf.graph.Literal;
import org.jrdf.graph.NodeComparator;
import org.jrdf.query.expression.BoundOperator;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.LangOperator;
import org.jrdf.query.expression.SingleValue;
import org.jrdf.query.expression.StrOperator;
import org.jrdf.query.expression.logic.EqualsExpression;
import org.jrdf.query.expression.logic.LessThanExpression;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.expression.logic.NEqualsExpression;
import org.jrdf.query.expression.logic.TrueExpression;
import org.jrdf.query.expression.logic.FalseExpression;
import org.jrdf.query.expression.logic.LogicalAndExpression;
import org.jrdf.query.expression.logic.LogicalNotExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.constants.NullaryAttribute;
import static org.jrdf.query.relation.mem.BoundAVPOperation.BOUND;
import org.jrdf.query.relation.mem.ComparatorFactoryImpl;
import static org.jrdf.query.relation.mem.EqAVPOperation.EQUALS;
import static org.jrdf.query.relation.mem.LangAVPOperator.LANG;
import static org.jrdf.query.relation.mem.StrAVPOperation.STR;
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
import org.jrdf.vocabulary.XSD;

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
    private static final ValueOperation ANY_NODE_LANG_VO = new ValueOperationImpl(ANY_NODE, LANG);
    private static final ValueOperation ANY_NODE_EQUALS_VO = new ValueOperationImpl(ANY_NODE, EQUALS);
    private static final ValueOperation ANY_NODE_STR_VO = new ValueOperationImpl(ANY_NODE, STR);
    private static final ValueOperation TRUE_VO = new ValueOperationImpl(createLiteral("true", XSD.BOOLEAN), EQUALS);
    private static final ValueOperation FALSE_VO = new ValueOperationImpl(createLiteral("false", XSD.BOOLEAN), EQUALS);
    private static final Map<Attribute, ValueOperation> TRUE_AVO =
        createAVO(NullaryAttribute.NULLARY_ATTRIBUTE, TRUE_VO);
    private static final Map<Attribute, ValueOperation> FALSE_AVO =
        createAVO(NullaryAttribute.NULLARY_ATTRIBUTE, FALSE_VO);

    private static final LogicExpression<ExpressionVisitor> TRUE_EXP =
        new TrueExpression<ExpressionVisitor>(TRUE_AVO);
    private static final LogicExpression<ExpressionVisitor> FALSE_EXP =
        new FalseExpression<ExpressionVisitor>(FALSE_AVO);

    private BooleanEvaluator evaluator;
    private LogicExpression<ExpressionVisitor> expression;

    @Override
    protected void setUp() throws Exception {
        evaluator = new SimpleBooleanEvaluator(NODE_COMPARATOR);
    }

    public void testLessThanExpression() {
        SingleValue<ExpressionVisitor> valueExp =
            new SingleValue<ExpressionVisitor>(createAVO(VAR_BAR1_LITERAL, ANY_NODE_EQUALS_VO));
        SingleValue<ExpressionVisitor> valueExp1 = new SingleValue<ExpressionVisitor>(VAR_BAR1_LITERAL_L2);
        expression = new LessThanExpression<ExpressionVisitor>(valueExp, valueExp1);
        boolean result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, expression);
        assertTrue(result);
        result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_3, expression);
        assertFalse(result);
    }

    public void testBoundExpression() {
        ValueOperation vo = new ValueOperationImpl(ANY_NODE, BOUND);
        expression = new BoundOperator<ExpressionVisitor>(createAVO(VAR_BAR1_LITERAL, vo));
        boolean result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, expression);
        assertTrue(result);
        result = evaluator.evaluate(TEST_VARFOO_LITERAL_TUPLE_1, expression);
        assertFalse(result);
    }

    public void testNEqualsExpression() {
        SingleValue<ExpressionVisitor> valueExp =
            new SingleValue<ExpressionVisitor>(createAVO(VAR_BAR1_LITERAL, ANY_NODE_EQUALS_VO));
        SingleValue valueExp1 = new SingleValue(VAR_BAR1_LITERAL_L2);
        expression = new NEqualsExpression<ExpressionVisitor>(valueExp, valueExp1);
        boolean result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, expression);
        assertTrue(result);
        result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_2, expression);
        assertFalse(result);
    }

    public void testEqualsExpression() {
        Expression<ExpressionVisitor> valueExp =
            new StrOperator<ExpressionVisitor>(createAVO(VAR_FOO1_LITERAL, ANY_NODE_STR_VO));
        SingleValue<ExpressionVisitor> valueExp1 = new SingleValue<ExpressionVisitor>(VAR_FOO1_LITERAL_L1);
        expression = new EqualsExpression<ExpressionVisitor>(valueExp, valueExp1);
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
        Expression<ExpressionVisitor> valueExp =
            new LangOperator<ExpressionVisitor>(createAVO(VAR_FOO1_LITERAL, ANY_NODE_LANG_VO));

        ValueOperation vo1 = new ValueOperationImpl(createLiteral("en"), EQUALS);
        Expression<ExpressionVisitor> valueExp1 = new SingleValue<ExpressionVisitor>(createAVO(VAR_FOO1_LITERAL, vo1));
        expression = new EqualsExpression<ExpressionVisitor>(valueExp, valueExp1);
        boolean result = evaluator.evaluate(TEST_VARFOO_LITERAL_TUPLE_1, expression);
        assertFalse(result);

        valueExp1 = new SingleValue<ExpressionVisitor>(createAVO(VAR_BAR1_LITERAL, ANY_NODE_STR_VO));
        expression = new EqualsExpression<ExpressionVisitor>(valueExp, valueExp1);
        result = evaluator.evaluate(TEST_VARFOO_LITERAL_TUPLE_1, expression);
        assertFalse(result);
    }

    public void testLangTags() {
        Expression<ExpressionVisitor> langExp =
                new LangOperator<ExpressionVisitor>(createAVO(VAR_BAR1_LITERAL, ANY_NODE_LANG_VO));
        ValueOperation vo1 = new ValueOperationImpl(LITERAL_L1_LANG, EQUALS);
        SingleValue<ExpressionVisitor> valueExp1 = new SingleValue<ExpressionVisitor>(createAVO(VAR_BAR1_LITERAL, vo1));
        expression = new NEqualsExpression<ExpressionVisitor>(langExp, valueExp1);

        Tuple tuple = createTuple(VAR_BAR1_LITERAL_L1);
        boolean result = evaluator.evaluate(tuple, expression);
        assertTrue(result);

        ValueOperation vo2 = new ValueOperationImpl(LITERAL_L1_LANG2, EQUALS);
        tuple = createTuple(createAVO(VAR_BAR1_LITERAL, vo2));
        result = evaluator.evaluate(tuple, expression);
        assertFalse(result);

        vo2 = new ValueOperationImpl(LITERAL_L1_LANG1, EQUALS);
        tuple = createTuple(createAVO(VAR_BAR1_LITERAL, vo2));
        result = evaluator.evaluate(tuple, expression);
        assertTrue(result);

        expression = new EqualsExpression<ExpressionVisitor>(langExp, valueExp1);
        result = evaluator.evaluate(tuple, expression);
        assertFalse(result);
    }

    public void testTrueFalse() {
        boolean result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, TRUE_EXP);
        assertTrue(result);

        result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, FALSE_EXP);
        assertFalse(result);
    }

    public void testAndExp() {
        LogicExpression<ExpressionVisitor> andExp = new LogicalAndExpression<ExpressionVisitor>(TRUE_EXP, FALSE_EXP);
        boolean result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, andExp);
        assertFalse(result);
    }

    public void testNotExp() {
        LogicExpression<ExpressionVisitor> notExp = new LogicalNotExpression<ExpressionVisitor>(TRUE_EXP);
        boolean result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, notExp);
        assertFalse(result);

        notExp = new LogicalNotExpression<ExpressionVisitor>(FALSE_EXP);
        result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, notExp);
        assertTrue(result);
    }

    private static Map<Attribute, ValueOperation> createAVO(Attribute key, ValueOperation value) {
        Map<Attribute, ValueOperation> avo = new HashMap<Attribute, ValueOperation>();
        avo.put(key, value);
        return avo;
    }
}
