/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2009 The JRDF Project.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        the JRDF Project (http://jrdf.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The JRDF Project" and "JRDF" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission. For written permission, please contact
 *    newmana@users.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "JRDF"
 *    nor may "JRDF" appear in their names without prior written
 *    permission of the JRDF Project.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the JRDF Project.  For more
 * information on JRDF, please see <http://jrdf.sourceforge.net/>.
 *
 */

package org.jrdf.query.execute;

import org.jrdf.graph.AnyNode;
import org.jrdf.graph.AnyObjectNode;
import org.jrdf.graph.AnyPredicateNode;
import org.jrdf.graph.AnySubjectNode;
import org.jrdf.graph.Node;
import static org.jrdf.query.execute.ExpressionComparatorImpl.EXPRESSION_COMPARATOR;
import org.jrdf.query.expression.Ask;
import org.jrdf.query.expression.BoundOperator;
import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.EmptyConstraint;
import static org.jrdf.query.expression.EmptyConstraint.EMPTY_CONSTRAINT;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.ExpressionVisitorAdapter;
import org.jrdf.query.expression.Filter;
import org.jrdf.query.expression.LangOperator;
import org.jrdf.query.expression.Optional;
import org.jrdf.query.expression.Projection;
import org.jrdf.query.expression.SingleConstraint;
import org.jrdf.query.expression.SingleValue;
import org.jrdf.query.expression.StrOperator;
import org.jrdf.query.expression.Union;
import org.jrdf.query.expression.logic.EqualsExpression;
import org.jrdf.query.expression.logic.FalseExpression;
import static org.jrdf.query.expression.logic.FalseExpression.FALSE_EXPRESSION;
import org.jrdf.query.expression.logic.LessThanExpression;
import org.jrdf.query.expression.logic.LogicAndExpression;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.expression.logic.LogicNotExpression;
import org.jrdf.query.expression.logic.LogicOrExpression;
import org.jrdf.query.expression.logic.NEqualsExpression;
import org.jrdf.query.expression.logic.TrueExpression;
import static org.jrdf.query.expression.logic.TrueExpression.TRUE_EXPRESSION;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.mem.AVPOperation;
import static org.jrdf.query.relation.mem.EqAVPOperation.EQUALS;
import static org.jrdf.query.relation.mem.StrAVPOperation.STR;
import org.jrdf.query.relation.mem.ValueOperationImpl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * @author Yuan-Fang Li
 * @version $Id:$
 */

public class ExpressionSimplifierImpl extends ExpressionVisitorAdapter implements ExpressionSimplifier {
    private Stack<Expression> expression;
    private Set<Attribute> declaredVariables;
    private Map<Attribute, Attribute> variableMap;
    private Map<Attribute, ValueOperation> newAttributeValues;

    public ExpressionSimplifierImpl(Map<Attribute, ValueOperation> newAttributeValues,
        Map<Attribute, Attribute> variableMap, Set<Attribute> declaredVariables) {
        this.expression = new Stack<Expression>();
        this.newAttributeValues = newAttributeValues;
        this.variableMap = variableMap;
        this.declaredVariables = declaredVariables;
    }

    public ExpressionSimplifierImpl() {
        this.expression = new Stack<Expression>();
        this.newAttributeValues = new HashMap<Attribute, ValueOperation>();
        this.variableMap = new HashMap<Attribute, Attribute>();
        this.declaredVariables = new LinkedHashSet<Attribute>();
    }

    @SuppressWarnings({ "unchecked" })
    public <V extends ExpressionVisitor> Expression<V> getExpression() {
        if (!expression.empty()) {
            return expression.peek();
        }
        else {
            return null;
        }
    }

    @SuppressWarnings({ "unchecked" })
    public <V extends ExpressionVisitor> Expression<V> popExpression() {
        return expression.pop();
    }

    public <V extends ExpressionVisitor> void visitProjection(Projection<V> projection, V v) {
        System.err.println("Hello: " + projection);
        declaredVariables = projection.getAttributes();
        Expression<ExpressionVisitor> next = getNext(projection.getNextExpression(), v);
        projection.setNextExpression(next);
        expression.push(projection);
    }

    public <V extends ExpressionVisitor> void visitConjunction(Conjunction<V> conjunction, V v) {
        final Expression<V> lhs = getNext(conjunction.getLhs(), v);
        final Expression<V> rhs = getNext(conjunction.getRhs(), v);
        if (lhs == null && rhs == null) {
            expression.clear();
        } else if (lhs == null) {
            expression.push(rhs);
        } else if (rhs == null) {
            expression.push(lhs);
        } else {
            expression.push(constructNewConjunction(lhs, rhs, v));
        }
    }

    private <V extends ExpressionVisitor> Expression constructNewConjunction(Expression<V> lhs, Expression<V> rhs,
        V v) {
        Expression<V> expression = constructFilteredConjunction(lhs, rhs, v);
        if (expression == null) {
            expression = constructUnionedConjunction(lhs, rhs, v);
            if (expression == null) {
                if (EXPRESSION_COMPARATOR.compare(lhs, rhs) <= 0) {
                    expression = new Conjunction<V>(lhs, rhs);
                } else {
                    expression = new Conjunction<V>(rhs, lhs);
                }
            }
        }
        return expression;
    }

    private <V extends ExpressionVisitor> Expression<V> constructFilteredConjunction(Expression<V> lhs,
        Expression<V> rhs, V v) {
        Expression<V> expression = null;
        if (lhs instanceof Filter && rhs instanceof Filter) {
            Expression<V> llhs = ((Filter<V>) lhs).getLhs();
            Expression<V> lrhs = ((Filter<V>) rhs).getLhs();
            expression = new Conjunction<V>(llhs, lrhs);
            LogicExpression<V> andExp = new LogicAndExpression<V>(((Filter<V>) lhs).getRhs(),
                ((Filter<V>) rhs).getRhs());
            expression = new Filter<V>(expression, andExp);
        } else if (lhs instanceof Filter) {
            expression = constructConjFilter((Filter<V>) lhs, rhs);
        } else if (rhs instanceof Filter) {
            expression = constructConjFilter((Filter<V>) rhs, lhs);
        }
        return getNext(expression, v);
    }

    private <V extends ExpressionVisitor> Expression<V> constructConjFilter(Filter<V> lhs, Expression<V> rhs) {
        Expression<V> result;
        Expression<V> llhs = lhs.getLhs();
        result = new Conjunction<V>(llhs, rhs);
        result = new Filter<V>(result, lhs.getRhs());
        return result;
    }

    @SuppressWarnings( {"unchecked"} )
    private <V extends ExpressionVisitor> Expression<V> constructUnionedConjunction(Expression lhs, Expression rhs,
        V v) {
        Expression<V> expression = null;
        if (lhs instanceof Union) {
            expression = distributeConjunctionWithUnion((Union<V>) lhs, rhs, v);
        } else if (rhs instanceof Union) {
            expression = distributeConjunctionWithUnion((Union<V>) rhs, lhs, v);
        }
        return getNext(expression, v);
    }

    private <V extends ExpressionVisitor> Expression<V> distributeConjunctionWithUnion(Union<V> lhs, Expression<V> rhs,
        V v) {
        Expression<V> uLhs = lhs.getLhs();
        Expression<V> uRhs = lhs.getRhs();
        Expression newConj1 = getNext(new Conjunction<V>(rhs, uLhs), v);
        Expression newConj2 = getNext(new Conjunction<V>(rhs, uRhs), v);
        return getNext(new Union<V>(newConj1, newConj2), v);
    }

    public <V extends ExpressionVisitor> void visitUnion(Union<V> conjunction, V v) {
        final Expression<V> lhs = getNext(conjunction.getLhs(), v);
        final Expression<V> rhs = getNext(conjunction.getRhs(), v);
        if (EXPRESSION_COMPARATOR.compare(lhs, rhs) <= 0) {
            expression.push(new Union<V>(lhs, rhs));
        } else {
            expression.push(new Union<V>(rhs, lhs));
        }
    }

    public <V extends ExpressionVisitor> void visitOptional(Optional<V> optional, V v) {
        Expression lhsExp = optional.getLhs();
        lhsExp = (lhsExp == null) ? EMPTY_CONSTRAINT : lhsExp;
        Expression<V> lhs = getNext(lhsExp, v);
        Expression rhsExp = optional.getRhs();
        rhsExp = (rhsExp == null) ? EMPTY_CONSTRAINT : rhsExp;
        Expression rhs = getNext(rhsExp, v);
        expression.push(new Optional<V>(lhs, rhs));
    }

    @SuppressWarnings( {"unchecked"} )
    public <V extends ExpressionVisitor> void visitFilter(Filter<V> filter, V v) {
        LogicExpression<V> logicExpression = (LogicExpression<V>) getNext(filter.getRhs(), v);
        expression.push(getNext(filter.getLhs(), v));
        if (logicExpression != null) {
            expression.push(new Filter<V>(expression.pop(), logicExpression));
        }
    }

    public <V extends ExpressionVisitor> void visitLogicAnd(LogicAndExpression<V> andExpression, V v) {
        Expression<V> lhs = getNext(andExpression.getLhs(), v);
        Expression<V> rhs = getNext(andExpression.getRhs(), v);
        if (lhs == null && rhs == null) {
            expression.clear();
        } else if (lhs == null) {
            expression.push(rhs);
        } else if (rhs == null) {
            expression.push(lhs);
        } else {
            expression.push(andExpression);
        }
    }

    public <V extends ExpressionVisitor> void visitLogicOr(LogicOrExpression<V> orExpression, V v) {
        Expression<V> lhs = getNext(orExpression.getLhs(), v);
        Expression<V> rhs = getNext(orExpression.getRhs(), v);
        if (lhs == null && rhs == null) {
            expression.clear();
        } else if (lhs == null) {
            expression.push(rhs);
        } else if (rhs == null) {
            expression.push(lhs);
        } else {
            expression.push(orExpression);
        }
    }

    // TODO YF if query selects both variables in (?name = ?name1), this doesn't work properly as ?name1 will be lost!
    public <V extends ExpressionVisitor> void visitEqualsExpression(EqualsExpression<V> equalsExpression, V v) {
        Expression<V> lhs = getNext(equalsExpression.getLhs(), v);
        Expression<V> rhs = getNext(equalsExpression.getRhs(), v);
        final Expression<V>[] pair = reorderPairs(lhs, rhs);
        boolean changed = equateExpressions(lhs, rhs);
        if (!changed) {
            changed = equateAVOs(pair[0].getAVO(), pair[1].getAVO());
            if (changed) {
                expression.clear();
            } else {
                lhs = pair[0];
                rhs = pair[1];
                expression.push(new EqualsExpression<V>(lhs, rhs));
            }
        }
    }

    public <V extends ExpressionVisitor> void visitTrue(TrueExpression<V> trueExp, V v) {
        expression.push(trueExp);
    }

    public <V extends ExpressionVisitor> void visitFalse(FalseExpression<V> falseExp, V v) {
        expression.push(falseExp);
    }

    private <V extends ExpressionVisitor> boolean equateExpressions(Expression<V> lhs, Expression<V> rhs) {
        boolean changed = false;
        if (isTrueExp(lhs) || isFalseExp(lhs)) {
            equateRhsExp(lhs, rhs);
            changed = true;
        } else if (isTrueExp(rhs) || isFalseExp(rhs)) {
            equateRhsExp(rhs, lhs);
            changed = true;
        }
        return changed;
    }

    private <V extends ExpressionVisitor> void equateRhsExp(Expression<V> lhs, Expression<V> rhs) {
        if (isTrueExp(lhs)) {
            expression.push(rhs);
        } else if (isFalseExp(lhs)) {
            if (isTrueExp(rhs)) {
                expression.push(FALSE_EXPRESSION);
            } else if (isFalseExp(rhs)) {
                expression.push(TRUE_EXPRESSION);
            } else {
                expression.push(new LogicNotExpression<V>((LogicExpression<V>) rhs));
            }
        }
    }

    private <V extends ExpressionVisitor> boolean isTrueExp(Expression<V> exp) {
        return TRUE_EXPRESSION.equals(exp);
    }

    private <V extends ExpressionVisitor> boolean isFalseExp(Expression<V> exp) {
        return FALSE_EXPRESSION.equals(exp);
    }

    @SuppressWarnings({ "unchecked" })
    private <V extends ExpressionVisitor> Expression<V>[] reorderPairs(Expression<V> lhs, Expression<V> rhs) {
        Expression<V>[] result = new Expression[2];
        final int lhsSize = lhs.size();
        final int rhsSize = rhs.size();
        if (lhsSize >= rhsSize) {
            result[0] = lhs;
            result[1] = rhs;
        } else {
            result[0] = rhs;
            result[1] = lhs;
        }
        return result;
    }

    private boolean equateAVOs(Map<Attribute, ValueOperation> lhs, Map<Attribute, ValueOperation> rhs) {
        Attribute attribute = lhs.keySet().iterator().next();
        ValueOperation lvo = lhs.get(attribute);
        ValueOperation rvo = rhs.get(attribute);
        boolean validForEquate = isValidForEquate(lvo, rvo);
        if (validForEquate && isAnyNode(lvo.getValue())) {
            if (rvo != null) {
                updateAttributeValue(attribute, lvo, rvo);
            } else {
                // assuming a variable now
                updateVariableMap(rhs, attribute);
            }
            return true;
        }
        return false;
    }

    private boolean isValidForEquate(ValueOperation lvo, ValueOperation rvo) {
        AVPOperation lOp = lvo.getOperation();
        boolean validForEquate = lOp.equals(EQUALS) || lOp.equals(STR);
        validForEquate = validForEquate &&
            (rvo == null || rvo.getOperation().equals(EQUALS) || rvo.getOperation().equals(STR));
        return validForEquate;
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

    public <V extends ExpressionVisitor> void visitConstraint(SingleConstraint<V> constraint, V v) {
        LinkedHashMap<Attribute, ValueOperation> avo = updateAVO(constraint.getAvo(null));
        expression.push(new SingleConstraint<V>(avo));
    }

    public <V extends ExpressionVisitor> void visitEmptyConstraint(EmptyConstraint<V> constraint, V v) {
        expression.push(constraint);
    }

    // Skip logical not now.
    public <V extends ExpressionVisitor> void visitLogicNot(LogicNotExpression<V> notExpression, V v) {
//        LogicExpression<V> exp = (LogicExpression<V>) getNext(notExpression.getExpression());
//        expression = new LogicNotExpression<V>(exp);
        expression.push(notExpression);
    }

    public <V extends ExpressionVisitor> void visitBound(BoundOperator<V> bound, V v) {
        expression.push(bound);
    }

    public <V extends ExpressionVisitor> void visitLang(LangOperator<V> lang, V v) {
        LinkedHashMap<Attribute, ValueOperation> avo = updateAVPVariables(lang.getAVO());
        expression.push(new LangOperator(avo));
    }

    public <V extends ExpressionVisitor> void visitStr(StrOperator<V> str, V v) {
        LinkedHashMap<Attribute, ValueOperation> avo = updateAVPVariables(str.getAVO());
        expression.push(new StrOperator(avo));
    }

    public <V extends ExpressionVisitor> void visitSingleValue(SingleValue<V> value, V v) {
        LinkedHashMap<Attribute, ValueOperation> avo = updateAVPVariables(value.getAVO());
        expression.push(new SingleValue(avo));
    }

    private LinkedHashMap<Attribute, ValueOperation> updateAVO(Map<Attribute, ValueOperation> oldAvo) {
        LinkedHashMap<Attribute, ValueOperation> avo = updateAVPVariables(oldAvo);
        for (Attribute attribute : avo.keySet()) {
            if (newAttributeValues.get(attribute) != null) {
                avo.put(attribute, newAttributeValues.get(attribute));
            }
        }
        return avo;
    }

    public <V extends ExpressionVisitor> void visitLessThanExpression(LessThanExpression<V> lessThanExpression, V v) {
        Expression<V> lhs = getNext(lessThanExpression.getLhs(), v);
        Expression<V> rhs = getNext(lessThanExpression.getRhs(), v);
        expression.push(new LessThanExpression<V>(lhs, rhs));
    }

    public <V extends ExpressionVisitor> void visitNEqualsExpression(NEqualsExpression<V> nEqualsExpression, V v) {
        Expression<V> lhs = getNext(nEqualsExpression.getLhs(), v);
        Expression<V> rhs = getNext(nEqualsExpression.getRhs(), v);
        expression.push(new NEqualsExpression<V>(lhs, rhs));
    }

    public <V extends ExpressionVisitor> void visitAsk(Ask<V> ask, V v) {
        Expression<ExpressionVisitor> next = getNext(ask.getNextExpression(), v);
        ask.setNextExpression(next);
        expression.push(ask);
    }

    public boolean parseAgain() {
        return !variableMap.isEmpty();
    }

    private <V extends ExpressionVisitor> Expression<V> getNext(Expression<V> expression, V v) {
        if (expression == null) {
            return null;
        }
        expression.accept(v);
        return ((ExpressionSimplifier) v).getExpression();
    }
}
