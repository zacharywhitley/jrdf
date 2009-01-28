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

import org.jrdf.query.expression.logic.EqualsExpression;
import org.jrdf.query.expression.logic.FalseExpression;
import org.jrdf.query.expression.logic.LessThanExpression;
import org.jrdf.query.expression.logic.LogicAndExpression;
import org.jrdf.query.expression.logic.LogicNotExpression;
import org.jrdf.query.expression.logic.LogicOrExpression;
import org.jrdf.query.expression.logic.NEqualsExpression;
import org.jrdf.query.expression.logic.TrueExpression;

/**
 * An adapter for ExpressionVisitor - allows an implementation to avoid having to implement all the methods.
 * Currently, these methods do nothing.
 *
 * @author Andrew Newman
 * @version $Revision:$
 */
public class ExpressionVisitorAdapter implements ExpressionVisitor {

    public void visitProjection(Projection projection, ExpressionVisitor expressionVisitor) {
    }

    public void visitConstraint(SingleConstraint constraint, ExpressionVisitor v) {
    }

    public void visitEmptyConstraint(EmptyConstraint constraint, ExpressionVisitor v) {
    }

    public void visitConjunction(Conjunction conjunction, ExpressionVisitor v) {
    }

    public void visitUnion(Union union, ExpressionVisitor v) {
    }

    public void visitOptional(Optional optional, ExpressionVisitor v) {
    }

    public void visitLogicAnd(LogicAndExpression andExpression, ExpressionVisitor v) {
    }

    public void visitLogicOr(LogicOrExpression orExpression, ExpressionVisitor v) {
    }

    public void visitLogicNot(LogicNotExpression notExpression, ExpressionVisitor v) {
    }

    public void visitFilter(Filter filter, ExpressionVisitor v) {
    }

    public void visitEqualsExpression(EqualsExpression equalsExpression, ExpressionVisitor v) {
    }

    public void visitLessThanExpression(LessThanExpression lessThanExpression, ExpressionVisitor v) {
    }

    public void visitNEqualsExpression(NEqualsExpression nEqualsExpression, ExpressionVisitor v) {
    }

    public void visitAsk(Ask ask, ExpressionVisitor v) {
    }

    public void visitSingleValue(SingleValue value, ExpressionVisitor v) {
    }

    public void visitStr(StrOperator str, ExpressionVisitor v) {
    }

    public void visitLang(LangOperator lang, ExpressionVisitor v) {
    }

    public void visitBound(BoundOperator bound, ExpressionVisitor v) {
    }

    public void visitTrue(TrueExpression trueExp, ExpressionVisitor v) {
    }

    public void visitFalse(FalseExpression falseExp, ExpressionVisitor v) {
    }
}
