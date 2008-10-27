package org.jrdf.query.execute;

import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.ExpressionVisitorAdapter;
import org.jrdf.query.expression.Projection;
import org.jrdf.query.expression.SingleConstraint;
import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.Union;
import org.jrdf.query.expression.Optional;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;

import java.util.Map;

public class ExpressionSimplifierImpl extends ExpressionVisitorAdapter implements ExpressionSimplifier {
    private Map<Attribute, ValueOperation> newAttributeValues;

    public ExpressionSimplifierImpl(Map<Attribute, ValueOperation> newAttributeValues) {
        this.newAttributeValues = newAttributeValues;
    }

    public <V extends ExpressionVisitor> void visitProjection(Projection<V> projection) {
        getNext(projection.getNextExpression());
    }

    public <V extends ExpressionVisitor> void visitConjunction(Conjunction<V> conjunction) {
        getNext(conjunction.getLhs());
        getNext(conjunction.getRhs());
    }

    public <V extends ExpressionVisitor> void visitUnion(Union<V> conjunction) {
        getNext(conjunction.getLhs());
        getNext(conjunction.getRhs());
    }

    public <V extends ExpressionVisitor> void visitOptional(Optional<V> optional) {
        getNext(optional.getLhs());
        getNext(optional.getRhs());
    }

    public <V extends ExpressionVisitor> void visitConstraint(SingleConstraint<V> constraint) {
        for (Attribute attribute : newAttributeValues.keySet()) {
            constraint.setAvo(attribute, newAttributeValues.get(attribute));
        }
    }

    @SuppressWarnings({ "unchecked" })
    public <V extends ExpressionVisitor> void getNext(Expression<V> expression) {
        ExpressionSimplifier expressionSimplifier = new ExpressionSimplifierImpl(newAttributeValues);
        expression.accept((V) expressionSimplifier);
    }
}
