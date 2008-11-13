package org.jrdf.query.relation.operation.mem.logic;

import junit.framework.TestCase;
import org.jrdf.graph.AnyNode;
import org.jrdf.graph.NodeComparator;
import org.jrdf.query.expression.logic.LessThanExpression;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.mem.ComparatorFactoryImpl;
import static org.jrdf.query.relation.mem.EqAVPOperation.EQUALS;
import org.jrdf.query.relation.mem.ValueOperationImpl;
import org.jrdf.query.relation.operation.BooleanEvaluator;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_LITERAL;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_LITERAL_L1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_LITERAL_L2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_LITERAL_L3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_FOO1_LITERAL_L1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_FOO1_LITERAL_L2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createTuple;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuan-Fang Li
 * @version $Id:$
 */

public class SimpleBooleanEvaluatorUnitTest extends TestCase {
    private static final NodeComparator NODE_COMPARATOR = new ComparatorFactoryImpl().createNodeComparator();
    private static final Tuple TEST_VARBAR_LITERAL_TUPLE_1 = createTuple(VAR_BAR1_LITERAL_L1);
    private static final Tuple TEST_VARBAR_LITERAL_TUPLE_2 = createTuple(VAR_BAR1_LITERAL_L2);
    private static final Tuple TEST_VARBAR_LITERAL_TUPLE_3 = createTuple(VAR_BAR1_LITERAL_L3);
    private static final Tuple TEST_VARFOO_LITERAL_TUPLE_2 = createTuple(VAR_FOO1_LITERAL_L1);
    private static final Tuple TEST_VARFOO_LITERAL_TUPLE_4 = createTuple(VAR_FOO1_LITERAL_L2);
    private static final Tuple TEST_TUPLE_1_2 = createTuple(VAR_BAR1_LITERAL_L1, VAR_FOO1_LITERAL_L1);
    private static final Tuple TEST_TUPLE_1_3 = createTuple(VAR_BAR1_LITERAL_L1, VAR_BAR1_LITERAL_L2);
    private static final Tuple TEST_TUPLE_3_1 = createTuple(VAR_BAR1_LITERAL_L2, VAR_BAR1_LITERAL_L1);

    private BooleanEvaluator evaluator;
    private LogicExpression expression;

    @Override
    protected void setUp() throws Exception {
        evaluator = new SimpleBooleanEvaluator(NODE_COMPARATOR);
    }

    public void testLessThanExpression() {
        Map<Attribute, ValueOperation> avo = new HashMap<Attribute, ValueOperation>();
        ValueOperation vo = new ValueOperationImpl(AnyNode.ANY_NODE, EQUALS);
        avo.put(VAR_BAR1_LITERAL, vo);
        expression = new LessThanExpression(avo, VAR_BAR1_LITERAL_L2);
        boolean result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_1, expression);
        assertTrue(result);
        expression = new LessThanExpression(avo, VAR_BAR1_LITERAL_L2);
        result = evaluator.evaluate(TEST_VARBAR_LITERAL_TUPLE_3, expression);
        assertFalse(result);
    }
}
