/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
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
import org.jrdf.query.expression.logic.LogicalAndExpression;
import org.jrdf.query.expression.logic.LogicalNotExpression;
import org.jrdf.query.expression.logic.NEqualsExpression;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public final class ExpressionComparatorImpl implements ExpressionVisitor, ExpressionComparator {
    private static final long serialVersionUID = 4884876904025513571L;
    private int result;

    /**
     * The singleton expression comparator.
     */
    public static final ExpressionComparator EXPRESSION_COMPARATOR = new ExpressionComparatorImpl();

    private ExpressionComparatorImpl() {
    }

    @SuppressWarnings("unchecked")
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

    public int getVariableEstimate() {
        return result;
    }

    public <V extends ExpressionVisitor> void visitConstraint(SingleConstraint<V> constraint) {
        result = constraint.size();
    }

    public <V extends ExpressionVisitor> void visitFilter(Filter<V> filter) {
        int lhs = getNext(filter.getLhs());
        int rhs = getNext(filter.getRhs());
        result = lhs + rhs;
    }

    public <V extends ExpressionVisitor> void visitConjunction(Conjunction<V> conjunction) {
        int lhs = getNext(conjunction.getLhs());
        int rhs = getNext(conjunction.getRhs());
        result = (int) Math.ceil((lhs + rhs) * 1.0 / 2);
    }

    public <V extends ExpressionVisitor> void visitProjection(Projection<V> projection) {
        result = getNext(projection.getNextExpression());
    }

    public <V extends ExpressionVisitor> void visitEmptyConstraint(EmptyConstraint<V> constraint) {
        result = constraint.size();
    }

    public <V extends ExpressionVisitor> void visitUnion(Union<V> union) {
        int lhs = getNext(union.getLhs());
        int rhs = getNext(union.getRhs());
        System.err.println("union lhs compare = " + lhs);
        System.err.println("union rhs compare = " + rhs);
        result = (int) Math.ceil((lhs + rhs) * 1.0 / 2);
    }

    public <V extends ExpressionVisitor> void visitOptional(Optional<V> optional) {
        int lhs = getNext(optional.getLhs());
        int rhs = getNext(optional.getRhs());
        result = (int) Math.ceil((lhs + rhs) * 1.0 / 2);
    }

    public <V extends ExpressionVisitor> void visitOperator(Operator<V> operator) {
        result = operator.size();
    }

    public <V extends ExpressionVisitor> void visitLogicalAnd(LogicalAndExpression<V> andExpression) {
        int lhs = getNext(andExpression.getLhs());
        int rhs = getNext(andExpression.getRhs());
        result = (int) Math.ceil((lhs + rhs) * 1.0 / 2) + 1;
    }

    public <V extends ExpressionVisitor> void visitLogicalNot(LogicalNotExpression<V> notExpression) {
        result = notExpression.size();
    }

    public <V extends ExpressionVisitor> void visitEqualsExpression(EqualsExpression<V> equalsExpression) {
        result = equalsExpression.size();
    }

    public <V extends ExpressionVisitor> void visitAsk(Ask<V> ask) {
        result = getNext(ask.getNextExpression());
    }

    public <V extends ExpressionVisitor> void visitLessThanExpression(LessThanExpression<V> lessThanExpression) {
        result = lessThanExpression.size();
    }

    public <V extends ExpressionVisitor> void visitNEqualsExpression(NEqualsExpression<V> nEqualsExpression) {
        result = nEqualsExpression.size();
    }

    @SuppressWarnings({ "unchecked" })
    private <V extends ExpressionVisitor> int getNext(Expression<V> expression) {
        ExpressionComparator comparator = new ExpressionComparatorImpl();
        expression.accept((V) comparator);
        return comparator.getVariableEstimate();
    }
}
