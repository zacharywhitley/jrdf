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
