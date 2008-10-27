package org.jrdf.query.relation.operation.mem.logic;

import org.jrdf.query.expression.BoundOperator;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.ExpressionVisitorAdapter;
import org.jrdf.query.expression.Operator;
import org.jrdf.query.expression.logic.EqualsExpression;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.expression.logic.LogicalNotExpression;
import org.jrdf.query.expression.logic.LogicalAndExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleFactory;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.mem.AVPOperation;
import org.jrdf.query.relation.mem.EqAVPOperation;
import org.jrdf.query.relation.operation.BooleanEvaluator;

import java.util.Map;

public class SimpleBooleanEvaluator extends ExpressionVisitorAdapter implements BooleanEvaluator {
    private RelationFactory relationFactory;
    private TupleFactory tupleFactory;
    private Tuple tuple;
    private boolean contradiction;
    private Object operatorValue;

    public SimpleBooleanEvaluator(RelationFactory relationFactory, TupleFactory tupleFactory) {
        this.relationFactory = relationFactory;
        this.tupleFactory = tupleFactory;
    }

    // TODO only bound operator now.
    public <V extends ExpressionVisitor> void visitOperator(Operator<V> operator) {
        final Map<Attribute, ValueOperation> avp = operator.getAttributeValuePair();
        // assuming single avp
        Attribute attribute = avp.keySet().iterator().next();
        final ValueOperation valueOperation = tuple.getValueOperation(attribute);
        if (BoundOperator.class.isAssignableFrom(operator.getClass())) {
            contradiction = (valueOperation == null);
        }
    }

    public <V extends ExpressionVisitor> void visitLogicalAnd(LogicalAndExpression<V> andExpression) {
        andExpression.getLhs().accept((V) this);
        boolean lhsBoolean = contradiction;
        andExpression.getRhs().accept((V) this);
        contradiction = lhsBoolean || contradiction;
    }

    public <V extends ExpressionVisitor> void visitLogicalNot(LogicalNotExpression<V> notExpression) {
        notExpression.getExpression().accept((V) this);
        contradiction = !contradiction;
    }

    public <V extends ExpressionVisitor> void visitEqualsExpression(EqualsExpression<V> equalsExpression) {
        final Map<Attribute, ValueOperation> lhs = equalsExpression.getLhs();
        final Map<Attribute, ValueOperation> rhs = equalsExpression.getRhs();
        final Attribute lhsAttr = lhs.keySet().iterator().next();
        contradiction = determineEquality(lhsAttr, lhs, rhs, lhsAttr);
    }

    private boolean determineEquality(Attribute attribute, Map<Attribute, ValueOperation> lhs,
                                      Map<Attribute, ValueOperation> rhs, Attribute lhsAttr) {
        final ValueOperation lhsVO = lhs.get(lhsAttr);
        final ValueOperation rhsVO = rhs.get(lhsAttr);
        final AVPOperation op1 = lhsVO.getOperation();
        final AVPOperation op2 = rhsVO.getOperation();
        if (!op1.equals(EqAVPOperation.EQUALS)) {
            return op1.addAttributeValuePair(attribute, tuple.getAttributeValues(), lhsVO, rhsVO);
        } else {
            return op2.addAttributeValuePair(attribute, tuple.getAttributeValues(), lhsVO, rhsVO);
        }
    }

    public boolean evaluate(Tuple tuple, LogicExpression expression) {
        this.tuple = tuple;
        expression.accept(this);
        return !contradiction;
    }
}