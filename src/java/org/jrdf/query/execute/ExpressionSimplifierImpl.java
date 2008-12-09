package org.jrdf.query.execute;

import org.jrdf.graph.AnyNode;
import static org.jrdf.graph.AnyNode.ANY_NODE;
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
import static org.jrdf.query.expression.EmptyConstraint.EMPTY_CONSTRAINT;
import org.jrdf.query.expression.logic.EqualsExpression;
import org.jrdf.query.expression.logic.LessThanExpression;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.expression.logic.LogicalAndExpression;
import org.jrdf.query.expression.logic.LogicalNotExpression;
import org.jrdf.query.expression.logic.NEqualsExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.mem.ValueOperationImpl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ExpressionSimplifierImpl implements ExpressionSimplifier {
    private Map<Attribute, ValueOperation> newAttributeValues;
    private Expression expression;
    private final ExpressionComparator expressionComparator = EXPRESSION_COMPARATOR;
    private Map<Attribute, Attribute> variableMap;
    private Set<Attribute> declaredVariables;

    public ExpressionSimplifierImpl(Map<Attribute, ValueOperation> newAttributeValues,
                                    Map<Attribute, Attribute> variableMap,
                                    Set<Attribute> declaredVariables) {
        this.newAttributeValues = newAttributeValues;
        this.variableMap = variableMap;
        this.declaredVariables = declaredVariables;
    }

    public ExpressionSimplifierImpl() {
        this.newAttributeValues = new HashMap<Attribute, ValueOperation>();
        this.variableMap = new HashMap<Attribute, Attribute>();
        this.declaredVariables = new LinkedHashSet<Attribute>();
    }

    public <V extends ExpressionVisitor> Expression<V> getExpression() {
        return expression;
    }

    public <V extends ExpressionVisitor> void visitProjection(Projection<V> projection) {
        declaredVariables = projection.getAttributes();
        Expression<ExpressionVisitor> next = getNext(projection.getNextExpression());
        projection.setNextExpression(next);
        expression = projection;
    }

    public <V extends ExpressionVisitor> void visitConjunction(Conjunction<V> conjunction) {
        final Expression<V> lhs = getNext(conjunction.getLhs());
        final Expression<V> rhs = getNext(conjunction.getRhs());
        if (lhs == null && rhs == null) {
            expression = null;
        } else if (lhs == null) {
            expression = rhs;
        } else if (rhs == null) {
            expression = lhs;
        } else {
            expression = constructNewConjunction(lhs, rhs);
        }
    }

    private <V extends ExpressionVisitor> Expression constructNewConjunction(Expression<V> lhs, Expression<V> rhs) {
        Expression<V> expression = constructFilteredConjunction(lhs, rhs);
        if (expression == null) {
            expression = constructUnionedConjunction(lhs, rhs);
            if (expression == null) {
                if (expressionComparator.compare(lhs, rhs) <= 0) {
                    expression = new Conjunction<V>(lhs, rhs);
                } else {
                    expression = new Conjunction<V>(rhs, lhs);
                }
            }
        }
        return expression;
    }

    private <V extends ExpressionVisitor> Expression<V> constructFilteredConjunction(Expression<V> lhs,
                                                                                     Expression<V> rhs) {
        Expression<V> expression = null;
        if (lhs instanceof Filter && rhs instanceof Filter) {
            Expression<V> llhs = ((Filter<V>) lhs).getLhs();
            Expression<V> lrhs = ((Filter<V>) rhs).getLhs();
            expression = new Conjunction<V>(llhs, lrhs);
            LogicExpression<V> andExp =
                new LogicalAndExpression<V>(((Filter<V>) lhs).getRhs(), ((Filter<V>) rhs).getRhs());
            expression = new Filter<V>(expression, andExp);
        } else if (lhs instanceof Filter) {
            expression = constructConjFilter((Filter<V>) lhs, rhs);
        } else if (rhs instanceof Filter) {
            expression = constructConjFilter((Filter<V>) rhs, lhs);
        }
        return getNext(expression);
    }

    private <V extends ExpressionVisitor> Expression<V> constructConjFilter(Filter<V> lhs, Expression<V> rhs) {
        Expression<V> result;
        Expression<V> llhs = lhs.getLhs();
        result = new Conjunction<V>(llhs, rhs);
        result = new Filter<V>(result, lhs.getRhs());
        return result;
    }

    private <V extends ExpressionVisitor> Expression<V> constructUnionedConjunction(Expression lhs, Expression rhs) {
        Expression<V> expression = null;
        if (lhs instanceof Union) {
            expression = distributeConjunctionWithUnion((Union<V>) lhs, rhs);
        } else if (rhs instanceof Union) {
            expression = distributeConjunctionWithUnion((Union<V>) rhs, lhs);
        }
        return getNext(expression);
    }

    private <V extends ExpressionVisitor> Expression distributeConjunctionWithUnion(Union<V> lhs, Expression<V> rhs) {
        Expression<V> uLhs = lhs.getLhs();
        Expression<V> uRhs = lhs.getRhs();
        Expression newConj1 = getNext(new Conjunction<V>(rhs, uLhs));
        Expression newConj2 = getNext(new Conjunction<V>(rhs, uRhs));
        return getNext(new Union<V>(newConj1, newConj2));
    }

    public <V extends ExpressionVisitor> void visitUnion(Union<V> conjunction) {
        final Expression<V> lhs = getNext(conjunction.getLhs());
        final Expression<V> rhs = getNext(conjunction.getRhs());
        if (expressionComparator.compare(lhs, rhs) <= 0) {
            expression = new Union<V>(lhs, rhs);
        } else {
            expression = new Union<V>(rhs, lhs);
        }
    }

    public <V extends ExpressionVisitor> void visitOptional(Optional<V> optional) {
        Expression lhsExp = optional.getLhs();
        lhsExp = (lhsExp == null) ? EMPTY_CONSTRAINT : lhsExp;
        Expression<V> lhs = getNext(lhsExp);
        Expression<? extends ExpressionVisitor> rhsExp = optional.getRhs();
        rhsExp = (rhsExp == null) ? EMPTY_CONSTRAINT : rhsExp;
        Expression rhs = getNext(rhsExp);
        expression = new Optional<V>(lhs, rhs);
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
        Expression<V> lhs = getNext(andExpression.getLhs());
        Expression<V> rhs = getNext(andExpression.getRhs());
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

    // TODO YF if query selects both variables in (?name = ?name1), this doesn't work properly as ?name1 will be lost!
    public <V extends ExpressionVisitor> void visitEqualsExpression(EqualsExpression<V> equalsExpression) {
        Map<Attribute, ValueOperation> lhs = equalsExpression.getLhs();
        Map<Attribute, ValueOperation> rhs = equalsExpression.getRhs();
        final Map<Attribute, ValueOperation>[] pair = reorderPairs(lhs, rhs);
        lhs = pair[0];
        rhs = pair[1];
        final boolean changed = equateAVPs(lhs, rhs);
        if (changed) {
            expression = null;
        } else {
            expression = equalsExpression;
        }
    }

    private Map<Attribute, ValueOperation>[] reorderPairs(Map<Attribute, ValueOperation> lhs,
                                                          Map<Attribute, ValueOperation> rhs) {
        Map<Attribute, ValueOperation>[] result = new Map[2];
        final int lhsSize = lhs.size();
        final int rhsSize = rhs.size();
        if (lhsSize < rhsSize) {
            result[0] = lhs;
            result[1] = rhs;
        } else if (rhsSize < lhsSize) {
            result[0] = rhs;
            result[1] = lhs;
        } else {
            result = reorderEqualLengthVOs(lhs, rhs, result);
        }
        return result;
    }

    private Map<Attribute, ValueOperation>[] reorderEqualLengthVOs(Map<Attribute, ValueOperation> lhs,
                                                                   Map<Attribute, ValueOperation> rhs,
                                                                   Map<Attribute, ValueOperation>[] result) {
        Attribute attribute = lhs.keySet().iterator().next();
        if (lhs.get(attribute).getValue().equals(ANY_NODE)) {
            result[0] = lhs;
            result[1] = rhs;
        } else {
            result[0] = rhs;
            result[1] = lhs;
        }
        return result;
    }

    private boolean equateAVPs(Map<Attribute, ValueOperation> lhs, Map<Attribute, ValueOperation> rhs) {
        Attribute attribute = lhs.keySet().iterator().next();
        ValueOperation lvo = lhs.get(attribute);
        ValueOperation rvo = rhs.get(attribute);
        if (isAnyNode(lvo.getValue())) {
            if (rvo != null) {
                updateAttributeValue(attribute, lvo, rvo);
            } else if (rhs.size() == 1) {
                // assuming a variable now
                updateVariableMap(rhs, attribute);
            }
            return true;
        }
        return false;
    }

    private void updateAttributeValue(Attribute attribute, ValueOperation lvo, ValueOperation rvo) {
        ValueOperation newVO = new ValueOperationImpl(rvo.getValue(), lvo.getOperation());
        final Attribute value = variableMap.get(attribute);
        newAttributeValues.put(attribute, newVO);
        if (value != null) {
            newAttributeValues.put(value, newVO);
        }
    }

    private void updateVariableMap(Map<Attribute, ValueOperation> rhs, Attribute attribute) {
        Attribute newAttr = rhs.keySet().iterator().next();
        Attribute key, value;
        if (declaredVariables.contains(attribute)) {
            key = newAttr;
            value = attribute;
        } else {
            key = attribute;
            value = newAttr;
        }
        Attribute fixPoint = variableMap.get(value);
        if (fixPoint == null) {
            fixPoint = value;
        }
        variableMap.put(key, fixPoint);
    }

    private LinkedHashMap<Attribute, ValueOperation> updateAVPVariables(Map<Attribute, ValueOperation> avp) {
        LinkedHashMap<Attribute, ValueOperation> newAVP = new LinkedHashMap<Attribute, ValueOperation>();
        for (Attribute attribute : avp.keySet()) {
            final ValueOperation vo = avp.get(attribute);
            final Attribute newAttribute = variableMap.get(attribute);
            if (newAttribute != null) {
                newAVP.put(newAttribute, vo);
            } else {
                newAVP.put(attribute, vo);
            }
        }
        return newAVP;
    }

    private boolean isAnyNode(Node node) {
        return node instanceof AnySubjectNode || node instanceof AnyPredicateNode ||
            node instanceof AnyObjectNode || node instanceof AnyNode;
    }

    public <V extends ExpressionVisitor> void visitConstraint(SingleConstraint<V> constraint) {
        LinkedHashMap<Attribute, ValueOperation> avo = updateAVPVariables(constraint.getAvo(null));
        for (Attribute attribute : avo.keySet()) {
            if (newAttributeValues.get(attribute) != null) {
                avo.put(attribute, newAttributeValues.get(attribute));
            }
        }
        expression = new SingleConstraint<V>(avo);
    }

    public <V extends ExpressionVisitor> void visitEmptyConstraint(EmptyConstraint<V> constraint) {
        expression = constraint;
    }

    // Skip logical not now.
    public <V extends ExpressionVisitor> void visitLogicalNot(LogicalNotExpression<V> notExpression) {
        LogicExpression<V> exp = (LogicExpression<V>) getNext(notExpression.getExpression());
        expression = new LogicalNotExpression<V>(exp);
    }

    public <V extends ExpressionVisitor> void visitOperator(Operator<V> operator) {
        expression = operator;
    }

    public <V extends ExpressionVisitor> void visitLessThanExpression(LessThanExpression<V> lessThanExpression) {
        final Map<Attribute, ValueOperation> lhs = updateAVPVariables(lessThanExpression.getLhs());
        final Map<Attribute, ValueOperation> rhs = updateAVPVariables(lessThanExpression.getRhs());
        expression = new LessThanExpression<V>(lhs, rhs);
    }

    public <V extends ExpressionVisitor> void visitNEqualsExpression(NEqualsExpression<V> nEqualsExpression) {
        final Map<Attribute, ValueOperation> lhs = updateAVPVariables(nEqualsExpression.getLhs());
        final Map<Attribute, ValueOperation> rhs = updateAVPVariables(nEqualsExpression.getRhs());
        expression = new NEqualsExpression<V>(lhs, rhs);
    }

    public <V extends ExpressionVisitor> void visitAsk(Ask<V> ask) {
        Expression<ExpressionVisitor> next = getNext(ask.getNextExpression());
        ask.setNextExpression(next);
        expression = ask;
    }

    public boolean parseAgain() {
        return !variableMap.isEmpty();
    }

    @SuppressWarnings({ "unchecked" })
    private <V extends ExpressionVisitor> Expression<V> getNext(Expression<V> expression) {
        if (expression != null) {
            ExpressionSimplifier expressionSimplifier =
                    new ExpressionSimplifierImpl(newAttributeValues, variableMap, declaredVariables);
            expression.accept((V) expressionSimplifier);
            return expressionSimplifier.getExpression();
        } else {
            return null;
        }
    }
}
