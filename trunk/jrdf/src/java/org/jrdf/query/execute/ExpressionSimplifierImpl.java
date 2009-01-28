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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version $Id:$
 */
public class ExpressionSimplifierImpl extends ExpressionVisitorAdapter implements ExpressionSimplifier {
    private Expression expression;
    private Set<Attribute> declaredVariables;
    private Map<Attribute, Attribute> variableMap;
    private Map<Attribute, ValueOperation> newAttributeValues;

    public ExpressionSimplifierImpl(Map<Attribute, ValueOperation> newAttributeValues,
        Map<Attribute, Attribute> variableMap, Set<Attribute> declaredVariables) {
        this.newAttributeValues = newAttributeValues;
        this.variableMap = variableMap;
        this.declaredVariables = declaredVariables;
    }

    public ExpressionSimplifierImpl() {
        this.newAttributeValues = new HashMap<Attribute, ValueOperation>();
        this.variableMap = new HashMap<Attribute, Attribute>();
        this.declaredVariables = new LinkedHashSet<Attribute>();
    }

    public Expression getExpression() {
        return expression;
    }

    public void visitProjection(Projection projection) {
        declaredVariables = projection.getAttributes();
        Expression next = getNext(projection.getNextExpression());
        projection.setNextExpression(next);
        expression = projection;
    }

    public void visitConjunction(Conjunction conjunction) {
        final Expression lhs = getNext(conjunction.getLhs());
        final Expression rhs = getNext(conjunction.getRhs());
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

    private Expression constructNewConjunction(Expression lhs, Expression rhs) {
        Expression expression = constructFilteredConjunction(lhs, rhs);
        if (expression == null) {
            expression = constructUnionedConjunction(lhs, rhs);
            if (expression == null) {
                if (EXPRESSION_COMPARATOR.compare(lhs, rhs) <= 0) {
                    expression = new Conjunction(lhs, rhs);
                } else {
                    expression = new Conjunction(rhs, lhs);
                }
            }
        }
        return expression;
    }

    private Expression constructFilteredConjunction(Expression lhs, Expression rhs) {
        Expression expression = null;
        if (lhs instanceof Filter && rhs instanceof Filter) {
            Expression llhs = ((Filter) lhs).getLhs();
            Expression lrhs = ((Filter) rhs).getLhs();
            expression = new Conjunction(llhs, lrhs);
            LogicExpression andExp = new LogicAndExpression(((Filter) lhs).getRhs(),
                ((Filter) rhs).getRhs());
            expression = new Filter(expression, andExp);
        } else if (lhs instanceof Filter) {
            expression = constructConjFilter((Filter) lhs, rhs);
        } else if (rhs instanceof Filter) {
            expression = constructConjFilter((Filter) rhs, lhs);
        }
        return getNext(expression);
    }

    private Expression constructConjFilter(Filter lhs, Expression rhs) {
        Expression result;
        Expression llhs = lhs.getLhs();
        result = new Conjunction(llhs, rhs);
        result = new Filter(result, lhs.getRhs());
        return result;
    }

    private Expression constructUnionedConjunction(Expression lhs, Expression rhs) {
        Expression expression = null;
        if (lhs instanceof Union) {
            expression = distributeConjunctionWithUnion((Union) lhs, rhs);
        } else if (rhs instanceof Union) {
            expression = distributeConjunctionWithUnion((Union) rhs, lhs);
        }
        return getNext(expression);
    }

    private Expression distributeConjunctionWithUnion(Union lhs, Expression rhs) {
        Expression uLhs = lhs.getLhs();
        Expression uRhs = lhs.getRhs();
        Expression newConj1 = getNext(new Conjunction(rhs, uLhs));
        Expression newConj2 = getNext(new Conjunction(rhs, uRhs));
        return getNext(new Union(newConj1, newConj2));
    }

    public void visitUnion(Union conjunction) {
        final Expression lhs = getNext(conjunction.getLhs());
        final Expression rhs = getNext(conjunction.getRhs());
        if (EXPRESSION_COMPARATOR.compare(lhs, rhs) <= 0) {
            expression = new Union(lhs, rhs);
        } else {
            expression = new Union(rhs, lhs);
        }
    }

    public void visitOptional(Optional optional) {
        Expression lhsExp = optional.getLhs();
        lhsExp = (lhsExp == null) ? EMPTY_CONSTRAINT : lhsExp;
        Expression lhs = getNext(lhsExp);
        Expression rhsExp = optional.getRhs();
        rhsExp = (rhsExp == null) ? EMPTY_CONSTRAINT : rhsExp;
        Expression rhs = getNext(rhsExp);
        expression = new Optional(lhs, rhs);
    }

    public void visitFilter(Filter filter) {
        LogicExpression logicExpression = (LogicExpression) getNext(filter.getRhs());
        expression = getNext(filter.getLhs());
        if (logicExpression != null) {
            expression = new Filter(expression, logicExpression);
        }
    }

    public void visitLogicAnd(LogicAndExpression andExpression) {
        Expression lhs = getNext(andExpression.getLhs());
        Expression rhs = getNext(andExpression.getRhs());
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

    public void visitLogicOr(LogicOrExpression orExpression) {
        Expression lhs = getNext(orExpression.getLhs());
        Expression rhs = getNext(orExpression.getRhs());
        if (lhs == null && rhs == null) {
            expression = null;
        } else if (lhs == null) {
            expression = rhs;
        } else if (rhs == null) {
            expression = lhs;
        } else {
            expression = orExpression;
        }
    }

    public void visitEqualsExpression(EqualsExpression equalsExpression) {
        Expression lhs = getNext(equalsExpression.getLhs());
        Expression rhs = getNext(equalsExpression.getRhs());
        final List<Expression> pair = reorderPairs(lhs, rhs);
        boolean changed = equateExpressions(lhs, rhs);
        if (!changed) {
            changed = equateAVOs(pair.get(0).getAVO(), pair.get(1).getAVO());
            if (changed) {
                expression = null;
            } else {
                lhs = pair.get(0);
                rhs = pair.get(1);
                expression = new EqualsExpression(lhs, rhs);
            }
        }
    }

    public void visitTrue(TrueExpression trueExp) {
        expression = trueExp;
    }

    public void visitFalse(FalseExpression falseExp) {
        expression = falseExp;
    }

    private boolean equateExpressions(Expression lhs, Expression rhs) {
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

    private void equateRhsExp(Expression lhs, Expression rhs) {
        if (isTrueExp(lhs)) {
            expression = rhs;
        } else if (isFalseExp(lhs)) {
            if (isTrueExp(rhs)) {
                expression = FALSE_EXPRESSION;
            } else if (isFalseExp(rhs)) {
                expression = TRUE_EXPRESSION;
            } else {
                expression = new LogicNotExpression((LogicExpression) rhs);
            }
        }
    }

    private boolean isTrueExp(Expression exp) {
        return TRUE_EXPRESSION.equals(exp);
    }

    private boolean isFalseExp(Expression exp) {
        return FALSE_EXPRESSION.equals(exp);
    }

    private List<Expression> reorderPairs(Expression lhs, Expression rhs) {
        List<Expression> result = new ArrayList<Expression>(2);
        final int lhsSize = lhs.size();
        final int rhsSize = rhs.size();
        if (lhsSize >= rhsSize) {
            result.add(0, lhs);
            result.add(1, rhs);
        } else {
            result.add(0, rhs);
            result.add(1, lhs);
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

    public void visitConstraint(SingleConstraint constraint) {
        LinkedHashMap<Attribute, ValueOperation> avo = updateAVO(constraint.getAvo(null));
        expression = new SingleConstraint(avo);
    }

    public void visitEmptyConstraint(EmptyConstraint constraint) {
        expression = constraint;
    }

    // Skip logical not now.
    public void visitLogicNot(LogicNotExpression notExpression) {
//        LogicExpression exp = (LogicExpression) getNext(notExpression.getExpression());
//        expression = new LogicNotExpression(exp);
        expression = notExpression;
    }

    public void visitBound(BoundOperator bound) {
        expression = bound;
    }

    public void visitLang(LangOperator lang) {
        LinkedHashMap<Attribute, ValueOperation> avo = updateAVPVariables(lang.getAVO());
        expression = new LangOperator(avo);
    }

    public void visitStr(StrOperator str) {
        LinkedHashMap<Attribute, ValueOperation> avo = updateAVPVariables(str.getAVO());
        expression = new StrOperator(avo);
    }

    public void visitSingleValue(SingleValue value) {
        LinkedHashMap<Attribute, ValueOperation> avo = updateAVPVariables(value.getAVO());
        expression = new SingleValue(avo);
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

    public void visitLessThanExpression(LessThanExpression lessThanExpression) {
        Expression lhs = getNext(lessThanExpression.getLhs());
        Expression rhs = getNext(lessThanExpression.getRhs());
        expression = new LessThanExpression(lhs, rhs);
    }

    public void visitNEqualsExpression(NEqualsExpression nEqualsExpression) {
        Expression lhs = getNext(nEqualsExpression.getLhs());
        Expression rhs = getNext(nEqualsExpression.getRhs());
        expression = new NEqualsExpression(lhs, rhs);
    }

    public void visitAsk(Ask ask) {
        Expression next = getNext(ask.getNextExpression());
        ask.setNextExpression(next);
        expression = ask;
    }

    public boolean parseAgain() {
        return !variableMap.isEmpty();
    }

    private Expression getNext(Expression expression) {
        if (expression == null) {
            return null;
        }
        expression.accept(this);
        return getExpression();
    }
}
