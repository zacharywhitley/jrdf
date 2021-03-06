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

import org.jrdf.query.expression.Ask;
import org.jrdf.query.expression.BoundOperator;
import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.EmptyConstraint;
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
import org.jrdf.query.expression.logic.LessThanExpression;
import org.jrdf.query.expression.logic.LogicAndExpression;
import org.jrdf.query.expression.logic.LogicNotExpression;
import org.jrdf.query.expression.logic.LogicOrExpression;
import org.jrdf.query.expression.logic.NEqualsExpression;
import org.jrdf.query.expression.logic.TrueExpression;

/**
 * @author Yuan-Fang Li
 * @version :$
 */
public final class ExpressionComparatorImpl extends ExpressionVisitorAdapter<Integer>
        implements ExpressionComparator<Integer> {
    private static final long serialVersionUID = 4884876904025513571L;

    /**
     * The singleton expression comparator.
     */
    public static final ExpressionComparator<Integer> EXPRESSION_COMPARATOR = new ExpressionComparatorImpl();

    private ExpressionComparatorImpl() {
    }

    public int compare(Expression lhs, Expression rhs) {
        int lhsValue = getNext(lhs);
        int rhsValue = getNext(rhs);
        if (lhsValue == rhsValue) {
            return 0;
        } else if (lhsValue < rhsValue) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public Integer visitConstraint(SingleConstraint constraint) {
        return constraint.size();
    }

    @Override
    public Integer visitFilter(Filter filter) {
        int lhs = getNext(filter.getLhs());
        int rhs = getNext(filter.getRhs());
        return (lhs + rhs);
    }

    @Override
    public Integer visitConjunction(Conjunction conjunction) {
        int lhs = getNext(conjunction.getLhs());
        int rhs = getNext(conjunction.getRhs());
        return (int) Math.ceil((lhs + rhs) * 1.0 / 2);
    }

    @Override
    public Integer visitProjection(Projection projection) {
        return getNext(projection.getNextExpression());
    }

    @Override
    public Integer visitEmptyConstraint(EmptyConstraint constraint) {
        return constraint.size();
    }

    @Override
    public Integer visitUnion(Union union) {
        int lhs = getNext(union.getLhs());
        int rhs = getNext(union.getRhs());
        return (int) Math.ceil((lhs + rhs) * 1.0 / 2);
    }

    @Override
    public Integer visitOptional(Optional optional) {
        int lhs = getNext(optional.getLhs());
        int rhs = getNext(optional.getRhs());
        return (int) Math.ceil((lhs + rhs) * 1.0 / 2);
    }

    @Override
    public Integer visitStr(StrOperator str) {
        return str.size();
    }

    @Override
    public Integer visitLang(LangOperator lang) {
        return lang.size();
    }

    @Override
    public Integer visitBound(BoundOperator bound) {
        return bound.size();
    }

    @Override
    public Integer visitLogicAnd(LogicAndExpression andExpression) {
        int lhs = getNext(andExpression.getLhs());
        int rhs = getNext(andExpression.getRhs());
        return (int) Math.ceil((lhs + rhs) * 1.0 / 2) + 1;
    }

    @Override
    public Integer visitLogicOr(LogicOrExpression orExpression) {
        int lhs = getNext(orExpression.getLhs());
        int rhs = getNext(orExpression.getRhs());
        return (int) Math.ceil((lhs + rhs) * 1.0 / 2) + 1;
    }

    @Override
    public Integer visitLogicNot(LogicNotExpression notExpression) {
        return notExpression.size();
    }

    @Override
    public Integer visitEqualsExpression(EqualsExpression equalsExpression) {
        return equalsExpression.size();
    }

    @Override
    public Integer visitAsk(Ask ask) {
        return getNext(ask.getNextExpression());
    }

    @Override
    public Integer visitLessThanExpression(LessThanExpression lessThanExpression) {
        return lessThanExpression.size();
    }

    @Override
    public Integer visitNEqualsExpression(NEqualsExpression nEqualsExpression) {
        return nEqualsExpression.size();
    }

    @Override
    public Integer visitSingleValue(SingleValue value) {
        return value.size();
    }

    @Override
    public Integer visitTrue(TrueExpression trueExp) {
        return trueExp.size();
    }

    @Override
    public Integer visitFalse(FalseExpression falseExp) {
        return falseExp.size();
    }

    private int getNext(Expression expression) {
        return expression.accept(this);
    }
}
