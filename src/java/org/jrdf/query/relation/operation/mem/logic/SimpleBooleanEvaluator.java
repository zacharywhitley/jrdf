package org.jrdf.query.relation.operation.mem.logic;

import org.jrdf.graph.AnyNode;
import org.jrdf.graph.Node;
import org.jrdf.graph.NodeComparator;
import org.jrdf.query.expression.BoundOperator;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.ExpressionVisitorAdapter;
import org.jrdf.query.expression.Operator;
import org.jrdf.query.expression.logic.EqualsExpression;
import org.jrdf.query.expression.logic.LessThanExpression;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.expression.logic.LogicalAndExpression;
import org.jrdf.query.expression.logic.LogicalNotExpression;
import org.jrdf.query.expression.logic.NEqualsExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.mem.AVPOperation;
import org.jrdf.query.relation.operation.BooleanEvaluator;

import java.util.Map;

public class SimpleBooleanEvaluator extends ExpressionVisitorAdapter implements BooleanEvaluator {
    private Tuple tuple;
    private boolean contradiction;
    private final NodeComparator nodeComparator;

    public SimpleBooleanEvaluator(NodeComparator nodeComparator) {
        this.nodeComparator = nodeComparator;
    }

    // TODO YF only bound operator now.
    @Override
    public <V extends ExpressionVisitor> void visitOperator(Operator<V> operator) {
        final Map<Attribute, ValueOperation> avp = operator.getAttributeValuePair();
        // assuming single avp
        Attribute attribute = avp.keySet().iterator().next();
        final ValueOperation valueOperation = tuple.getValueOperation(attribute);
        if (BoundOperator.class.isAssignableFrom(operator.getClass())) {
            contradiction = (valueOperation == null);
        }
    }

    @Override
    public <V extends ExpressionVisitor> void visitLogicalAnd(LogicalAndExpression<V> andExpression) {
        andExpression.getLhs().accept((V) this);
        boolean lhsBoolean = contradiction;
        andExpression.getRhs().accept((V) this);
        contradiction = lhsBoolean || contradiction;
    }

    @Override
    public <V extends ExpressionVisitor> void visitLogicalNot(LogicalNotExpression<V> notExpression) {
        notExpression.getExpression().accept((V) this);
        contradiction = !contradiction;
    }

    @Override
    public <V extends ExpressionVisitor> void visitEqualsExpression(EqualsExpression<V> equalsExpression) {
        final Map<Attribute, ValueOperation> lhs = equalsExpression.getLhs();
        final Map<Attribute, ValueOperation> rhs = equalsExpression.getRhs();
        final Attribute lhsAttr = lhs.keySet().iterator().next();
        final Attribute rhsAttr = rhs.keySet().iterator().next();
        contradiction = (compareValueOperation(lhsAttr, rhsAttr, lhs, rhs) != 0);
    }

    @Override
    public <V extends ExpressionVisitor> void visitNEqualsExpression(NEqualsExpression<V> nEqualsExpression) {
        final Map<Attribute, ValueOperation> lhs = nEqualsExpression.getLhs();
        final Map<Attribute, ValueOperation> rhs = nEqualsExpression.getRhs();
        final Attribute lhsAttr = lhs.keySet().iterator().next();
        final Attribute rhsAttr = rhs.keySet().iterator().next();
        contradiction = (compareValueOperation(lhsAttr, rhsAttr, lhs, rhs) == 0);
    }

    @Override
    public <V extends ExpressionVisitor> void visitLessThanExpression(LessThanExpression<V> lessThanExpression) {
        final Map<Attribute, ValueOperation> lhs = lessThanExpression.getLhs();
        final Map<Attribute, ValueOperation> rhs = lessThanExpression.getRhs();
        final Attribute lhsAttr = lhs.keySet().iterator().next();
        final Attribute rhsAttr = rhs.keySet().iterator().next();
        contradiction = compareValueOperation(lhsAttr, rhsAttr, lhs, rhs) >= 0;
    }

    private int compareValueOperation(Attribute lAttr, Attribute rAttr, Map<Attribute, ValueOperation> lhs,
                                      Map<Attribute, ValueOperation> rhs) {
        Node lNode = getNodeForAttribute(lAttr, lhs);
        Node rNode = getNodeForAttribute(rAttr, rhs);
        int result;
        if (lNode == null && rNode == null) {
            result = 0;
        } else if (lNode == null) {
            result = -1;
        } else if (rNode == null) {
            result = 1;
        } else {
            result = nodeComparator.compare(lNode, rNode);
        }
        return result;
    }

    private Node getNodeForAttribute(Attribute attribute, Map<Attribute, ValueOperation> avp) {
        Node node = avp.get(attribute).getValue();
        AVPOperation operation = avp.get(attribute).getOperation();
        ValueOperation valueOperation = tuple.getValueOperation(attribute);
        if (node.equals(AnyNode.ANY_NODE)) {
            if (valueOperation != null) {
                node = valueOperation.getValue();
            } else {
                node = null;
            }
        }
        return node;
    }

    public <V extends ExpressionVisitor> boolean evaluate(Tuple tuple, LogicExpression<V> expression) {
        this.tuple = tuple;
        expression.accept((V) this);
        return !contradiction;
    }
}