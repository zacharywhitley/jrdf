package org.jrdf.query.execute;

import org.jrdf.graph.AnyNode;
import org.jrdf.graph.AnyObjectNode;
import org.jrdf.graph.AnyPredicateNode;
import org.jrdf.graph.AnySubjectNode;
import org.jrdf.graph.Node;
import static org.jrdf.query.execute.ExpressionComparatorImpl.EXPRESSION_COMPARATOR;
import org.jrdf.query.expression.Ask;
import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.EmptyConstraint;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.Filter;
import org.jrdf.query.expression.Operator;
import org.jrdf.query.expression.Optional;
import org.jrdf.query.expression.Projection;
import org.jrdf.query.expression.SingleConstraint;
import org.jrdf.query.expression.Union;
import org.jrdf.query.expression.logic.EqualsExpression;
import org.jrdf.query.expression.logic.LessThanExpression;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.expression.logic.LogicalAndExpression;
import org.jrdf.query.expression.logic.LogicalNotExpression;
import org.jrdf.query.expression.logic.NEqualsExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;

import java.util.HashMap;
import java.util.Map;

public class ExpressionSimplifierImpl implements ExpressionSimplifier {
    private Map<Attribute, ValueOperation> newAttributeValues;
    private Expression expression;
    private final ExpressionComparator expressionComparator = EXPRESSION_COMPARATOR;

    public ExpressionSimplifierImpl(Map<Attribute, ValueOperation> newAttributeValues) {
        this.newAttributeValues = newAttributeValues;
    }

    public ExpressionSimplifierImpl() {
        this.newAttributeValues = new HashMap<Attribute, ValueOperation>();
    }

    public Expression<ExpressionVisitor> getExpression() {
        return expression;
    }

    public <V extends ExpressionVisitor> void visitProjection(Projection<V> projection) {
        Expression<ExpressionVisitor> next = getNext(projection.getNextExpression());
        projection.setNextExpression(next);
        expression = projection;
    }

    public <V extends ExpressionVisitor> void visitConjunction(Conjunction<V> conjunction) {
        final Expression<ExpressionVisitor> lhs = getNext(conjunction.getLhs());
        final Expression<ExpressionVisitor> rhs = getNext(conjunction.getRhs());
        if (expressionComparator.compare(lhs, rhs) <= 0) {
            expression = new Conjunction<ExpressionVisitor>(lhs, rhs);
        } else {
            expression = new Conjunction<ExpressionVisitor>(rhs, lhs);
        }
    }

    public <V extends ExpressionVisitor> void visitUnion(Union<V> conjunction) {
        final Expression<ExpressionVisitor> lhs = getNext(conjunction.getLhs());
        final Expression<ExpressionVisitor> rhs = getNext(conjunction.getRhs());
        if (expressionComparator.compare(lhs, rhs) <= 0) {
            expression = new Union<ExpressionVisitor>(lhs, rhs);
        } else {
            expression = new Union<ExpressionVisitor>(rhs, lhs);
        }
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
            expression = rhs;
        } else if (rhs == null) {
            expression = lhs;
        } else {
            expression = andExpression;
        }
    }

    public <V extends ExpressionVisitor> void visitEqualsExpression(EqualsExpression<V> equalsExpression) {
        Map<Attribute, ValueOperation> lhs = equalsExpression.getLhs();
        Map<Attribute, ValueOperation> rhs = equalsExpression.getRhs();
        Attribute attribute = lhs.keySet().iterator().next();
        ValueOperation lvo = lhs.get(attribute);
        ValueOperation rvo = rhs.get(attribute);
        if (isAnyNode(lvo.getValue()) && rvo != null) {
            newAttributeValues.put(attribute, rvo);
            expression = null;
        } else if (rvo != null && isAnyNode(rvo.getValue())) {
            newAttributeValues.put(attribute, lvo);
            expression = null;
        } else {
            expression = equalsExpression;
        }
    }

    private boolean isAnyNode(Node node) {
        return node instanceof AnySubjectNode || node instanceof AnyPredicateNode ||
            node instanceof AnyObjectNode || node instanceof AnyNode;
    }

    public <V extends ExpressionVisitor> void visitConstraint(SingleConstraint<V> constraint) {
        for (Attribute attribute : newAttributeValues.keySet()) {
            constraint.setAvo(attribute, newAttributeValues.get(attribute));
        }
        expression = constraint;
    }

    public <V extends ExpressionVisitor> void visitEmptyConstraint(EmptyConstraint<V> constraint) {
        expression = constraint;
    }

    // Skip logical not now.
    public <V extends ExpressionVisitor> void visitLogicalNot(LogicalNotExpression<V> notExpression) {
        expression = notExpression;
    }

    public <V extends ExpressionVisitor> void visitOperator(Operator<V> operator) {
        expression = operator;
    }

    public <V extends ExpressionVisitor> void visitLessThanExpression(LessThanExpression<V> lessThanExpression) {
        expression = lessThanExpression;
    }

    public <V extends ExpressionVisitor> void visitNEqualsExpression(NEqualsExpression<V> nEqualsExpression) {
        expression = nEqualsExpression;
    }

    public <V extends ExpressionVisitor> void visitAsk(Ask<V> ask) {
        Expression<ExpressionVisitor> next = getNext(ask.getNextExpression());
        ask.setNextExpression(next);
        expression = ask;
    }

    @SuppressWarnings({ "unchecked" })
    private <V extends ExpressionVisitor> Expression<ExpressionVisitor> getNext(Expression<V> expression) {
        ExpressionSimplifier expressionSimplifier = new ExpressionSimplifierImpl(newAttributeValues);
        expression.accept((V) expressionSimplifier);
        return expressionSimplifier.getExpression();
    }
}
