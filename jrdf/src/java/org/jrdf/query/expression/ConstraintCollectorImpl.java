package org.jrdf.query.expression;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import static org.jrdf.query.relation.mem.EqAVPOperation.EQUALS;

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

    private <V extends ExpressionVisitor> void visitOperator(Operator<V> operator) {
        Map<Attribute, ValueOperation> valuePair = operator.getAVO();
        ValueOperation valueOperation = valuePair.values().iterator().next();
        if (valueOperation.getOperation().equals(EQUALS)) {
            operators.putAll(valuePair);
        }
    }

    @Override
    public <V extends ExpressionVisitor> void visitBound(BoundOperator<V> bound) {
        visitOperator(bound);
    }

    @Override
    public <V extends ExpressionVisitor> void visitLang(LangOperator<V> lang) {
        visitOperator(lang);
    }

    @Override
    public <V extends ExpressionVisitor> void visitStr(StrOperator<V> str) {
        visitOperator(str);
    }

    @SuppressWarnings({ "unchecked" })
    public <V extends ExpressionVisitor> Map<Attribute, ValueOperation> getNext(Expression<V> expression) {
        ConstraintCollector expressionSimplifier = new ConstraintCollectorImpl();
        expression.accept((V) expressionSimplifier);
        return expressionSimplifier.getOperators();
    }
}
