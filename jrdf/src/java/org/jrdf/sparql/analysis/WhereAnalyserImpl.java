/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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

package org.jrdf.sparql.analysis;

import org.jrdf.graph.Graph;
import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.Constraint;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.Optional;
import org.jrdf.query.expression.Union;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.sparql.builder.TripleBuilder;
import org.jrdf.sparql.parser.analysis.DepthFirstAdapter;
import org.jrdf.sparql.parser.node.ABlockOfTriples;
import org.jrdf.sparql.parser.node.AFilteredBasicGraphPatternGraphPattern;
import org.jrdf.sparql.parser.node.AGroupOrUnionGraphPattern;
import org.jrdf.sparql.parser.node.AOperationPattern;
import org.jrdf.sparql.parser.node.AOptionalGraphPattern;
import org.jrdf.sparql.parser.node.ATriple;
import org.jrdf.sparql.parser.node.Node;
import org.jrdf.sparql.parser.node.PGroupGraphPattern;
import org.jrdf.sparql.parser.node.PMoreTriples;
import org.jrdf.sparql.parser.node.PUnionGraphPattern;
import org.jrdf.sparql.parser.parser.ParserException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Default implementation of {@link org.jrdf.sparql.analysis.SparqlAnalyser}.
 */
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

    public Expression<ExpressionVisitor>getExpression() throws ParserException {
        if (exception != null) {
            throw exception;
        }
        return expression;
    }

    @Override
    public void caseAFilteredBasicGraphPatternGraphPattern(AFilteredBasicGraphPatternGraphPattern node) {
        if (node.getOperationPattern() != null) {
            Expression<ExpressionVisitor> lhs = getExpression((Node) node.getFilteredBasicGraphPattern().clone());
            Expression<ExpressionVisitor> rhs = getExpression((Node) node.getOperationPattern().clone());

            if (lhs != null) {
                if (rhs.getClass().isAssignableFrom(Optional.class)) {
                    handleOptional(lhs, rhs);
                } else if (rhs.getClass().isAssignableFrom(Conjunction.class)) {
                    ((Conjunction<ExpressionVisitor>) rhs).setLhs(lhs);
                    expression = rhs;
                } else if (rhs.getClass().isAssignableFrom(Constraint.class) &&
                        (lhs.getClass().isAssignableFrom(Constraint.class))) {
                    expression = new Conjunction<ExpressionVisitor>(lhs, rhs);
                }
            } else {
                expression = rhs;
            }
        } else {
            super.caseAFilteredBasicGraphPatternGraphPattern(node);
        }
    }



    @Override
    public void caseATriple(ATriple node) {
        try {
            node.apply(tripleBuilder);
            List<AttributeValuePair> attributeValuePairs = tripleBuilder.getTriples();
            collector.addConstraints(attributeValuePairs);
            expression = new Constraint<ExpressionVisitor>(attributeValuePairs);
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
    public void caseAOperationPattern(AOperationPattern node) {
        Expression<ExpressionVisitor> lhs = getExpression((Node) node.getGraphPatternNotTriples().clone());
        Expression<ExpressionVisitor> rhs = getExpression((Node) node.getGraphPattern().clone());
        if (lhs != null && rhs != null) {
            expression = handleExistingLhsRhs(lhs, rhs);
        } else if (lhs != null) {
            expression = lhs;
        } else if (rhs != null) {
            expression = rhs;
        }
    }

    @Override
    public void caseAGroupOrUnionGraphPattern(AGroupOrUnionGraphPattern node) {
        if (node.getUnionGraphPattern() != null) {
            Expression<ExpressionVisitor> lhs = getExpression((PGroupGraphPattern) node.getGroupGraphPattern().clone());
            LinkedList<PUnionGraphPattern> unionGraphPattern = node.getUnionGraphPattern();
            List<Expression<ExpressionVisitor>> expressions = new ArrayList<Expression<ExpressionVisitor>>();
            for (PUnionGraphPattern pUnionGraphPattern : unionGraphPattern) {
                Expression<ExpressionVisitor> rhs = getExpression((PUnionGraphPattern) pUnionGraphPattern.clone());
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
            expression = createNewOptional(lhs, rhsOptional);
        } else {
            rhsOptional.setLhs(lhs);
            expression = rhsOptional;
        }
    }

    private Expression<ExpressionVisitor> handleExistingLhsRhs(Expression<ExpressionVisitor> lhs,
            Expression<ExpressionVisitor> rhs) {
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
