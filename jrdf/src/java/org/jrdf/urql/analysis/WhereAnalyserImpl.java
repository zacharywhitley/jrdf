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

package org.jrdf.urql.analysis;

import org.jrdf.graph.Graph;
import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.Constraint;
import static org.jrdf.query.expression.EmptyConstraint.EMPTY_CONSTRAINT;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.Filter;
import org.jrdf.query.expression.Operator;
import org.jrdf.query.expression.Optional;
import org.jrdf.query.expression.SingleConstraint;
import org.jrdf.query.expression.Union;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.urql.builder.LiteralBuilderImpl;
import org.jrdf.urql.builder.TripleBuilder;
import org.jrdf.urql.parser.analysis.DepthFirstAdapter;
import org.jrdf.urql.parser.node.ABlockOfTriples;
import org.jrdf.urql.parser.node.AFilterPatternGraphPatternOrFilter;
import org.jrdf.urql.parser.node.AFilteredBasicGraphPatternGraphPattern;
import org.jrdf.urql.parser.node.AGraphPatternOrFilterGraphPatternOperationPattern;
import org.jrdf.urql.parser.node.AGroupOrUnionGraphPattern;
import org.jrdf.urql.parser.node.AOptionalGraphPattern;
import org.jrdf.urql.parser.node.ATriple;
import org.jrdf.urql.parser.node.Node;
import org.jrdf.urql.parser.node.PGroupGraphPattern;
import org.jrdf.urql.parser.node.PMoreTriples;
import org.jrdf.urql.parser.node.POperationPattern;
import org.jrdf.urql.parser.node.PUnionGraphPattern;
import org.jrdf.urql.parser.parser.ParserException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Default implementation of {@link org.jrdf.urql.analysis.SparqlAnalyser}.
 */
//TODO This is a mess fix me please!!!!
public final class WhereAnalyserImpl extends DepthFirstAdapter implements WhereAnalyser {
    private TripleBuilder tripleBuilder;
    private Graph graph;
    private VariableCollector collector;
    private Expression<ExpressionVisitor> expression;
    private ParserException exception;

    public WhereAnalyserImpl(TripleBuilder tripleBuilder, Graph graph, VariableCollector collector) {
        this.tripleBuilder = tripleBuilder;
        this.graph = graph;
        this.collector = collector;
    }

    public Expression<ExpressionVisitor> getExpression() throws ParserException {
        if (exception != null) {
            throw exception;
        }
        return expression;
    }

    @Override
    public void caseAFilteredBasicGraphPatternGraphPattern(AFilteredBasicGraphPatternGraphPattern node) {
        Expression<ExpressionVisitor> lhs = getExpression((Node) node.getFilteredBasicGraphPattern().clone());
        if (node.getOperationPattern() != null) {
            expression = lhs;
            LinkedList<POperationPattern> list = node.getOperationPattern();
            for (POperationPattern pOperationPattern : list) {
                Expression<ExpressionVisitor> rhs = getExpression((Node) pOperationPattern.clone());
                handleExpressions(expression, rhs);
            }
        } else {
            super.caseAFilteredBasicGraphPatternGraphPattern(node);
        }
    }

    private void handleExpressions(Expression<ExpressionVisitor> lhs, Expression<ExpressionVisitor> rhs) {
        if (lhs != null) {
            if (Optional.class.isAssignableFrom(rhs.getClass())) {
                handleOptional(lhs, rhs);
            } else if (Conjunction.class.isAssignableFrom(rhs.getClass())) {
                Conjunction<ExpressionVisitor> conjunction = (Conjunction<ExpressionVisitor>) rhs;
                if (conjunction.getLhs() != null) {
                    expression = new Conjunction<ExpressionVisitor>(lhs, rhs);
                } else {
                    conjunction.setLhs(lhs);
                    expression = rhs;
                }
            } else if (Constraint.class.isAssignableFrom(rhs.getClass()) &&
                (Constraint.class.isAssignableFrom(lhs.getClass()))) {
                expression = new Conjunction<ExpressionVisitor>(lhs, rhs);
            } else if (LogicExpression.class.isAssignableFrom(rhs.getClass()) ||
                Operator.class.isAssignableFrom(rhs.getClass())) {
                expression = new Filter<ExpressionVisitor>(lhs, rhs);
            } else {
                expression = new Conjunction<ExpressionVisitor>(lhs, rhs);
            }
        } else {
            expression = rhs;
        }
    }


    @Override
    public void caseATriple(ATriple node) {
        try {
            node.apply(tripleBuilder);
            LinkedHashMap<Attribute, ValueOperation> map = tripleBuilder.getTriples();
            collector.addConstraints(map);
            expression = new SingleConstraint<ExpressionVisitor>(map);
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseABlockOfTriples(ABlockOfTriples node) {
        if (node.getMoreTriples().size() != 0) {
            Expression<ExpressionVisitor> lhs = getExpression((Node) node.getTriple().clone());
            LinkedList<PMoreTriples> moreTriples = node.getMoreTriples();
            List<Expression<ExpressionVisitor>> expressions = new ArrayList<Expression<ExpressionVisitor>>();
            for (PMoreTriples pMoreTriples : moreTriples) {
                Expression<ExpressionVisitor> rhs = getExpression((Node) pMoreTriples.clone());
                expressions.add(rhs);
            }
            Expression<ExpressionVisitor> lhsSide = lhs;
            for (Expression<ExpressionVisitor> currentExpression : expressions) {
                lhsSide = new Conjunction<ExpressionVisitor>(lhsSide, currentExpression);
            }
            expression = lhsSide;
        } else {
            super.caseABlockOfTriples(node);
        }
    }

    @Override
    public void caseAGraphPatternOrFilterGraphPatternOperationPattern(
        AGraphPatternOrFilterGraphPatternOperationPattern node) {
        Expression<ExpressionVisitor> lhs = getExpression((Node) node.getGraphPatternOrFilter().clone());
        Expression<ExpressionVisitor> rhs = getExpression((Node) node.getFilteredBasicGraphPattern().clone());
        if (lhs != null && rhs != null) {
            expression = handleExistingLhsRhs(rhs, lhs);
        } else if (lhs != null) {
            expression = lhs;
        } else if (rhs != null) {
            expression = rhs;
        }
    }

    @Override
    public void caseAFilterPatternGraphPatternOrFilter(AFilterPatternGraphPatternOrFilter node) {
        try {
            FilterAnalyser analyser = new FilterAnalyserImpl(new LiteralBuilderImpl(graph.getElementFactory(),
                    tripleBuilder.getPrefixMap()), collector);
            node.apply(analyser);
            expression = analyser.getExpression();
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAGroupOrUnionGraphPattern(AGroupOrUnionGraphPattern node) {
        if (node.getUnionGraphPattern() != null) {
            Expression<ExpressionVisitor> lhs = getExpressionWithEmptyConstraint(
                (Node) node.getGroupGraphPattern().clone());
            LinkedList<PUnionGraphPattern> unionGraphPattern = node.getUnionGraphPattern();
            List<Expression<ExpressionVisitor>> expressions = new ArrayList<Expression<ExpressionVisitor>>();
            for (PUnionGraphPattern pUnionGraphPattern : unionGraphPattern) {
                Expression<ExpressionVisitor> rhs = getExpressionWithEmptyConstraint((Node) pUnionGraphPattern.clone());
                expressions.add(rhs);
            }
            Expression<ExpressionVisitor> lhsSide = lhs;
            for (Expression<ExpressionVisitor> currentExpression : expressions) {
                lhsSide = new Union<ExpressionVisitor>(lhsSide, currentExpression);
            }
            expression = lhsSide;
        } else {
            super.caseAGroupOrUnionGraphPattern(node);
        }
    }

    @Override
    public void caseAOptionalGraphPattern(AOptionalGraphPattern node) {
        Expression<ExpressionVisitor> rhs = getExpression((PGroupGraphPattern) node.getGroupGraphPattern().clone());
        expression = new Optional<ExpressionVisitor>(rhs);
    }

    private void handleOptional(Expression<ExpressionVisitor> lhs, Expression<ExpressionVisitor> rhs) {
        Optional<ExpressionVisitor> rhsOptional = (Optional<ExpressionVisitor>) rhs;
        if (rhsOptional.getLhs() != null) {
            expression = new Conjunction<ExpressionVisitor>(lhs, rhsOptional);
        } else {
            rhsOptional.setLhs(lhs);
            expression = rhsOptional;
        }
    }

    private Expression<ExpressionVisitor> handleExistingLhsRhs(Expression<ExpressionVisitor> lhs,
        Expression<ExpressionVisitor> rhs) {
        // TODO impossible for both sides to be Optional
        if (lhs instanceof Optional && rhs instanceof Optional) {
            return joinTwoOptionals(lhs, rhs);
        } else if (rhs instanceof Optional) {
            return joinExpressionAndOptional(lhs, rhs);
        } else {
            return new Conjunction<ExpressionVisitor>(lhs, rhs);
        }
    }

    private Expression<ExpressionVisitor> joinTwoOptionals(Expression<ExpressionVisitor> lhs,
        Expression<ExpressionVisitor> rhs) {
        Optional<ExpressionVisitor> lhsOptional = (Optional<ExpressionVisitor>) lhs;
        Optional<ExpressionVisitor> rhsOptional = (Optional<ExpressionVisitor>) rhs;
        if (lhsOptional.getLhs() == null && rhsOptional.getLhs() == null) {
            return new Optional<ExpressionVisitor>(lhsOptional.getRhs(), rhsOptional.getRhs());
        } else if (rhsOptional.getLhs() == null) {
            rhsOptional.setLhs(lhs);
            return rhsOptional;
        } else if (lhsOptional.getLhs() == null) {
            lhsOptional.setLhs(rhs);
            return lhsOptional;
        } else {
            return new Conjunction<ExpressionVisitor>(lhs, rhs);
        }
    }

    private Expression<ExpressionVisitor> joinExpressionAndOptional(Expression<ExpressionVisitor> lhs,
        Expression<ExpressionVisitor> rhs) {
        Optional<ExpressionVisitor> rhsOptional = (Optional<ExpressionVisitor>) rhs;
        if (rhsOptional.getLhs() == null) {
            rhsOptional.setLhs(lhs);
            return rhsOptional;
        } else {
            return createNewOptional(lhs, rhsOptional);
        }
    }

    private Optional<ExpressionVisitor> createNewOptional(Expression<ExpressionVisitor> lhs,
        Optional<ExpressionVisitor> rhsOptional) {
        Optional<ExpressionVisitor> optional = new Optional<ExpressionVisitor>(lhs, rhsOptional.getLhs());
        return new Optional<ExpressionVisitor>(optional, rhsOptional.getRhs());
    }

    private Expression<ExpressionVisitor> getExpressionWithEmptyConstraint(Node node) {
        Expression<ExpressionVisitor> result = getExpression(node);
        if (result == null) {
            return EMPTY_CONSTRAINT;
        } else {
            return result;
        }
    }

    private Expression<ExpressionVisitor> getExpression(Node node) {
        try {
            WhereAnalyser analyser = new WhereAnalyserImpl(tripleBuilder, graph, collector);
            node.apply(analyser);
            return analyser.getExpression();
        } catch (ParserException e) {
            exception = e;
            return null;
        }
    }

}
