package org.jrdf.query.execute;

import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.ExpressionVisitorAdapter;
import org.jrdf.query.expression.Filter;
import org.jrdf.query.expression.Optional;
import org.jrdf.query.expression.Projection;
import org.jrdf.query.expression.SingleConstraint;
import org.jrdf.query.expression.Union;
import org.jrdf.query.expression.EmptyConstraint;
import org.jrdf.query.expression.Operator;
import org.jrdf.query.expression.logic.EqualsExpression;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.expression.logic.LogicalAndExpression;
import org.jrdf.query.expression.logic.LogicalNotExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.mem.AVPOperation;
import org.jrdf.query.relation.mem.EqAVPOperation;

import java.util.Map;

public class ExpressionSimplifierImpl extends ExpressionVisitorAdapter implements ExpressionSimplifier {
    private Map<Attribute, ValueOperation> newAttributeValues;
    private Expression<ExpressionVisitor> expression;

    public ExpressionSimplifierImpl(Map<Attribute, ValueOperation> newAttributeValues) {
        this.newAttributeValues = newAttributeValues;
    }

    public Expression<ExpressionVisitor> getExpression() {
        return expression;
    }

    public <V extends ExpressionVisitor> void visitProjection(Projection<V> projection) {
        Expression<ExpressionVisitor> next = getNext(projection.getNextExpression());
        projection.setNextExpression(next);
        expression = (Expression) projection;
    }

    public <V extends ExpressionVisitor> void visitConjunction(Conjunction<V> conjunction) {
        final Expression<ExpressionVisitor> lhs = getNext(conjunction.getLhs());
        final Expression<ExpressionVisitor> rhs = getNext(conjunction.getRhs());
        expression = new Conjunction<ExpressionVisitor>(lhs, rhs);
    }

    public <V extends ExpressionVisitor> void visitUnion(Union<V> conjunction) {
        final Expression<ExpressionVisitor> lhs = getNext(conjunction.getLhs());
        final Expression<ExpressionVisitor> rhs = getNext(conjunction.getRhs());
        expression = new Union<ExpressionVisitor>(lhs, rhs);
    }

    public <V extends ExpressionVisitor> void visitOptional(Optional<V> optional) {
        Expression<ExpressionVisitor> lhs = getNext(optional.getLhs());
        Expression<ExpressionVisitor> rhs = getNext(optional.getRhs());
        expression = new Optional<ExpressionVisitor>(lhs, rhs);
    }

    public <V extends ExpressionVisitor> void visitFilter(Filter<V> filter) {
        LogicExpression<ExpressionVisitor> logicExpression =
            (LogicExpression< ExpressionVisitor>) getNext(filter.getRhs());
        expression = getNext(filter.getLhs());
        if (logicExpression != null) {
            expression = new Filter<ExpressionVisitor>(expression, logicExpression);
        }
    }

    public <V extends ExpressionVisitor> void visitLogicalAnd(LogicalAndExpression<V> andExpression) {
        Expression<ExpressionVisitor> lhs = getNext(andExpression.getLhs());
        Expression<ExpressionVisitor> rhs = getNext(andExpression.getRhs());
        if (lhs == null && rhs == null) {
            expression = null;
        } else if (lhs == null) {
            expression = (LogicExpression<ExpressionVisitor>) rhs;
        } else if (rhs == null) {
            expression = (LogicExpression<ExpressionVisitor>) lhs;
        } else {
            expression = (Expression) andExpression;
        }
    }

    public <V extends ExpressionVisitor> void visitEqualsExpression(EqualsExpression<V> equalsExpression) {
        Map<Attribute, ValueOperation> lhs = equalsExpression.getLhs();
        Map<Attribute, ValueOperation> rhs = equalsExpression.getRhs();
        Attribute attribute = lhs.keySet().iterator().next();
        ValueOperation valueOperation = lhs.get(attribute);
        AVPOperation operation = valueOperation.getOperation();
        if (EqAVPOperation.EQUALS.equals(operation)) {
            newAttributeValues.put(attribute, valueOperation);
        } else {
            newAttributeValues.put(attribute, rhs.get(attribute));
        }
        expression = null;
    }

    public <V extends ExpressionVisitor> void visitConstraint(SingleConstraint<V> constraint) {
        for (Attribute attribute : newAttributeValues.keySet()) {
            constraint.setAvo(attribute, newAttributeValues.get(attribute));
        }
        expression = (Expression) constraint;
    }

    public <V extends ExpressionVisitor> void visitEmptyConstraint(EmptyConstraint<V> constraint) {
        expression = (Expression) constraint;
    }

    public <V extends ExpressionVisitor> void visitLogicalNot(LogicalNotExpression<V> notExpression) {
        expression = (Expression) notExpression;
    }

    public <V extends ExpressionVisitor> void visitOperator(Operator<V> operator) {
        expression = (Expression) operator;
    }

    @SuppressWarnings({ "unchecked" })
    private <V extends ExpressionVisitor> Expression<ExpressionVisitor> getNext(Expression<V> expression) {
        ExpressionSimplifier expressionSimplifier = new ExpressionSimplifierImpl(newAttributeValues);
        expression.accept((V) expressionSimplifier);
        return expressionSimplifier.getExpression();
    }
}
