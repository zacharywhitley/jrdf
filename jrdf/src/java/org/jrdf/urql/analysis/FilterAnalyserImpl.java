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

package org.jrdf.urql.analysis;

import org.jrdf.query.expression.EmptyExpression;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.SingleValue;
import org.jrdf.query.expression.logic.EqualsExpression;
import org.jrdf.query.expression.logic.LessThanExpression;
import org.jrdf.query.expression.logic.LogicAndExpression;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.expression.logic.LogicNotExpression;
import org.jrdf.query.expression.logic.LogicOrExpression;
import org.jrdf.query.expression.logic.NEqualsExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import static org.jrdf.query.relation.constants.NullaryAttribute.NULLARY_ATTRIBUTE;
import org.jrdf.urql.builder.LiteralBuilder;
import org.jrdf.urql.builder.URIReferenceBuilder;
import org.jrdf.urql.parser.analysis.DepthFirstAdapter;
import org.jrdf.urql.parser.node.ABooleanNotUnaryExpression;
import org.jrdf.urql.parser.node.ABracketedExpressionConstraint;
import org.jrdf.urql.parser.node.ABracketedExpressionPrimaryExpression;
import org.jrdf.urql.parser.node.AConditionalAndExpression;
import org.jrdf.urql.parser.node.AConditionalOrExpression;
import org.jrdf.urql.parser.node.AEMoreNumericExpression;
import org.jrdf.urql.parser.node.AFalseBooleanLiteral;
import org.jrdf.urql.parser.node.ALtMoreNumericExpression;
import org.jrdf.urql.parser.node.AMoreValueLogical;
import org.jrdf.urql.parser.node.ANeMoreNumericExpression;
import org.jrdf.urql.parser.node.APrimaryExpressionUnaryExpression;
import org.jrdf.urql.parser.node.ARelationalExpression;
import org.jrdf.urql.parser.node.ATrueBooleanLiteral;
import org.jrdf.urql.parser.node.PMoreConditionalAndExpression;
import org.jrdf.urql.parser.node.PMoreNumericExpression;
import org.jrdf.urql.parser.node.PMoreValueLogical;
import org.jrdf.urql.parser.node.PPrimaryExpression;
import org.jrdf.urql.parser.parser.ParserException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FilterAnalyserImpl extends DepthFirstAdapter implements FilterAnalyser {
    private Expression<ExpressionVisitor> expression = new EmptyExpression<ExpressionVisitor>();
    private ParserException exception;
    private VariableCollector collector;
    private LiteralBuilder literalBuilder;
    private URIReferenceBuilder uriBuilder;
    private NumericExpressionAnalyser numericExpressionAnalyser;

    public FilterAnalyserImpl(LiteralBuilder newLiteralBuilder, VariableCollector newCollector,
                              URIReferenceBuilder newUriBuilder) {
        this.literalBuilder = newLiteralBuilder;
        this.collector = newCollector;
        this.uriBuilder = newUriBuilder;
        this.numericExpressionAnalyser = new NumericExpressionAnalyserImpl<ExpressionVisitor>(literalBuilder,
            collector, uriBuilder);
    }

    public LogicExpression<ExpressionVisitor> getExpression() throws ParserException {
        if (exception != null) {
            throw exception;
        }
        return (LogicExpression<ExpressionVisitor>) expression;
    }

    @Override
    public void caseARelationalExpression(ARelationalExpression node) {
        node.getNumericExpression().apply(this);
        final PMoreNumericExpression moreExpressions = node.getMoreNumericExpression();
        if (moreExpressions != null) {
            moreExpressions.apply(this);
        } else {
            super.caseARelationalExpression(node);
        }
    }

    @Override
    public void caseAPrimaryExpressionUnaryExpression(APrimaryExpressionUnaryExpression node) {
        try {
            PPrimaryExpression primaryExpression = node.getPrimaryExpression();
            if (primaryExpression instanceof ABracketedExpressionPrimaryExpression) {
                primaryExpression.apply(this);
            } else {
                primaryExpression.apply(numericExpressionAnalyser);
                expression = numericExpressionAnalyser.getExpression();
            }
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAMoreValueLogical(AMoreValueLogical node) {
        FilterAnalyser analyzer = new FilterAnalyserImpl(literalBuilder, collector, uriBuilder);
        node.getValueLogical().apply(analyzer);
        try {
            expression = analyzer.getExpression();
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseABooleanNotUnaryExpression(ABooleanNotUnaryExpression node) {
        try {
            final LogicExpression<ExpressionVisitor> exp;
            PPrimaryExpression primaryExpression = node.getPrimaryExpression();
            if (primaryExpression instanceof ABracketedExpressionPrimaryExpression) {
                primaryExpression.apply(this);
                exp = (LogicExpression<ExpressionVisitor>) expression;
            } else {
                primaryExpression.apply(numericExpressionAnalyser);
                exp = (LogicExpression<ExpressionVisitor>) numericExpressionAnalyser.getExpression();
            }
            expression = new LogicNotExpression<ExpressionVisitor>(exp);
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseABracketedExpressionConstraint(ABracketedExpressionConstraint node) {
        node.getBracketedExpression().apply(this);
    }

    @Override
    public void caseAConditionalAndExpression(AConditionalAndExpression node) {
        try {
            node.getValueLogical().apply(this);
            LogicExpression<ExpressionVisitor> exp1 = (LogicExpression<ExpressionVisitor>) expression;
            final LinkedList<PMoreValueLogical> list = node.getMoreValueLogical();
            for (PMoreValueLogical rhs : list) {
                rhs.apply(this);
                final LogicExpression<ExpressionVisitor> exp2 = getExpression();
                exp1 = new LogicAndExpression<ExpressionVisitor>(exp1, exp2);
            }
            expression = exp1;
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAConditionalOrExpression(AConditionalOrExpression node) {
        try {
            node.getConditionalAndExpression().apply(this);
            LogicExpression<ExpressionVisitor> exp1 = (LogicExpression<ExpressionVisitor>) expression;
            final LinkedList<PMoreConditionalAndExpression> list = node.getMoreConditionalAndExpression();
            for (PMoreConditionalAndExpression rhs : list) {
                rhs.apply(this);
                final LogicExpression<ExpressionVisitor> exp2 = getExpression();
                exp1 = new LogicOrExpression<ExpressionVisitor>(exp1, exp2);
            }
            expression = exp1;
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAEMoreNumericExpression(AEMoreNumericExpression node) {
        try {
            Expression<ExpressionVisitor> lhsExp = expression;
            node.getNumericExpression().apply(numericExpressionAnalyser);
            Expression<ExpressionVisitor> rhsExp = numericExpressionAnalyser.getExpression();
            final List<Expression<ExpressionVisitor>> expressions = tryUpdateAttribute(lhsExp, rhsExp);
            expression = new EqualsExpression<ExpressionVisitor>(expressions.get(0), expressions.get(1));
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseANeMoreNumericExpression(ANeMoreNumericExpression node) {
        try {
            Expression<ExpressionVisitor> lhsExp = expression;
            node.getNumericExpression().apply(numericExpressionAnalyser);
            Expression<ExpressionVisitor> rhsExp = numericExpressionAnalyser.getExpression();
            final List<Expression<ExpressionVisitor>> expressions = tryUpdateAttribute(lhsExp, rhsExp);
            expression = new NEqualsExpression<ExpressionVisitor>(expressions.get(0), expressions.get(1));
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseALtMoreNumericExpression(ALtMoreNumericExpression node) {
        try {
            Expression<ExpressionVisitor> lhsExp = expression;
            node.getNumericExpression().apply(numericExpressionAnalyser);
            Expression<ExpressionVisitor> rhsExp = numericExpressionAnalyser.getExpression();
            final List<Expression<ExpressionVisitor>> expressions = tryUpdateAttribute(lhsExp, rhsExp);
            expression = new LessThanExpression<ExpressionVisitor>(expressions.get(0), expressions.get(1));
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseATrueBooleanLiteral(ATrueBooleanLiteral node) {
        try {
            node.apply(numericExpressionAnalyser);
            expression = numericExpressionAnalyser.getExpression();
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAFalseBooleanLiteral(AFalseBooleanLiteral node) {
        try {
            node.apply(numericExpressionAnalyser);
            expression = numericExpressionAnalyser.getExpression();
        } catch (ParserException e) {
            exception = e;
        }
    }

    private List<Expression<ExpressionVisitor>> tryUpdateAttribute(Expression<ExpressionVisitor> lhs,
        Expression<ExpressionVisitor> rhs) {
        List<Expression<ExpressionVisitor>> result = new ArrayList<Expression<ExpressionVisitor>>(2);
        Set<Attribute> lhsAttrs = lhs.getAVO().keySet();
        Set<Attribute> rhsAttrs = rhs.getAVO().keySet();
        result.add(0, updateoneExpression(lhs, lhsAttrs, rhsAttrs));
        result.add(1, updateoneExpression(rhs, rhsAttrs, lhsAttrs));
        return result;
    }

    private Expression<ExpressionVisitor> updateoneExpression(Expression<ExpressionVisitor> lhs,
        Set<Attribute> lhsAttrs, Set<Attribute> rhsAttrs) {
        if (lhs instanceof SingleValue && lhsAttrs.contains(NULLARY_ATTRIBUTE) && rhsAttrs.size() == 1) {
            Map<Attribute, ValueOperation> map = lhs.getAVO();
            ValueOperation vo = map.get(NULLARY_ATTRIBUTE);
            map.clear();
            map.put(rhsAttrs.iterator().next(), vo);
            ((SingleValue<ExpressionVisitor>) lhs).setAVO(map);
        }
        return lhs;
    }
}
