package org.jrdf.query.expression;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;

import java.util.HashMap;
import java.util.Map;

public class ConstraintCollectorImpl extends ExpressionVisitorAdapter implements ConstraintCollector {
    Map<Attribute, ValueOperation> operators = new HashMap<Attribute, ValueOperation>();

    public Map<Attribute, ValueOperation> getOperators() {
        return operators;
    }

    public <V extends ExpressionVisitor> void visitConjunction(Conjunction<V> conjunction) {
        Map<Attribute, ValueOperation> lhs = getNext(conjunction.getLhs());
        Map<Attribute, ValueOperation> rhs = getNext(conjunction.getRhs());
        operators.putAll(lhs);
        operators.putAll(rhs);
    }

    public <V extends ExpressionVisitor> void visitUnion(Union<V> union) {
        Map<Attribute, ValueOperation> lhs = getNext(union.getLhs());
        Map<Attribute, ValueOperation> rhs = getNext(union.getRhs());
        operators.putAll(lhs);
        operators.putAll(rhs);
    }

    public <V extends ExpressionVisitor> void visitOptional(Optional<V> optional) {
        Map<Attribute, ValueOperation> lhs = getNext(optional.getLhs());
        Map<Attribute, ValueOperation> rhs = getNext(optional.getRhs());
        operators.putAll(lhs);
        operators.putAll(rhs);
    }

    public <V extends ExpressionVisitor> void visitProjection(Projection<V> projection) {
        Map<Attribute, ValueOperation> map = getNext(projection.getNextExpression());
        operators.putAll(map);
    }

    public <V extends ExpressionVisitor> void visitOperator(Operator<V> operator) {
        operators.putAll(operator.getAttributeValuePair());
    }

    public <V extends ExpressionVisitor> Map<Attribute, ValueOperation> getNext(Expression<V> expression) {
        ConstraintCollector expressionSimplifier = new ConstraintCollectorImpl();
        expression.accept((V) expressionSimplifier);
        return expressionSimplifier.getOperators();
    }
}
