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

import org.jrdf.graph.Graph;
import org.jrdf.graph.Node;
import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.Constraint;
import static org.jrdf.query.expression.EmptyConstraint.EMPTY_CONSTRAINT;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.Filter;
import org.jrdf.query.expression.Optional;
import org.jrdf.query.expression.SingleConstraint;
import org.jrdf.query.expression.Union;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.urql.builder.LiteralBuilder;
import org.jrdf.urql.builder.LiteralBuilderImpl;
import org.jrdf.urql.builder.TripleBuilder;
import org.jrdf.urql.builder.URIReferenceBuilder;
import org.jrdf.urql.builder.URIReferenceBuilderImpl;
import org.jrdf.urql.parser.analysis.DepthFirstAdapter;
import org.jrdf.urql.parser.node.ABlockOfTriples;
import org.jrdf.urql.parser.node.AFilterPatternGraphPatternOrFilter;
import org.jrdf.urql.parser.node.AFilteredBasicGraphPatternGraphPattern;
import org.jrdf.urql.parser.node.AGraphPatternOrFilterGraphPatternOperationPattern;
import org.jrdf.urql.parser.node.AGroupOrUnionGraphPattern;
import org.jrdf.urql.parser.node.AOptionalGraphPattern;
import org.jrdf.urql.parser.node.ATriple;
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
 * Default implementation of {@link SparqlAnalyser}.
 */
//TODO This is a mess fix me please!!!!
public final class WhereAnalyserImpl extends DepthFirstAdapter implements WhereAnalyser {
    private TripleBuilder tripleBuilder;
    private Graph graph;
    private VariableCollector collector;
    private Expression expression;
    private ParserException exception;

    public WhereAnalyserImpl(TripleBuilder tripleBuilder, Graph graph, VariableCollector collector) {
        this.tripleBuilder = tripleBuilder;
        this.graph = graph;
        this.collector = collector;
    }

    public Expression getExpression() throws ParserException {
        if (exception != null) {
            throw exception;
        }
        return expression;
    }

    @Override
    public void caseAFilteredBasicGraphPatternGraphPattern(AFilteredBasicGraphPatternGraphPattern node) {
        Expression lhs = getExpression((org.jrdf.urql.parser.node.Node) node.getFilteredBasicGraphPattern().clone());
        if (node.getOperationPattern() != null) {
            expression = lhs;
            LinkedList<POperationPattern> list = node.getOperationPattern();
            for (POperationPattern pOperationPattern : list) {
                Expression rhs = getExpression((org.jrdf.urql.parser.node.Node) pOperationPattern.clone());
                handleExpressions(expression, rhs);
            }
        } else {
            super.caseAFilteredBasicGraphPatternGraphPattern(node);
        }
    }

    private void handleExpressions(Expression lhs, Expression rhs) {
        if (lhs != null) {
            if (Optional.class.isAssignableFrom(rhs.getClass())) {
                handleOptional(lhs, rhs);
            } else if (Conjunction.class.isAssignableFrom(rhs.getClass())) {
                Conjunction conjunction = (Conjunction) rhs;
                if (conjunction.getLhs() != null) {
                    expression = new Conjunction(lhs, rhs);
                } else {
                    conjunction.setLhs(lhs);
                    expression = rhs;
                }
            } else if (Constraint.class.isAssignableFrom(rhs.getClass()) &&
                (Constraint.class.isAssignableFrom(lhs.getClass()))) {
                expression = new Conjunction(lhs, rhs);
            } else if (LogicExpression.class.isAssignableFrom(rhs.getClass())) {
                expression = new Filter(lhs, (LogicExpression) rhs);
            } else if (LogicExpression.class.isAssignableFrom(lhs.getClass())) {
                expression = new Filter(rhs, (LogicExpression) lhs);
            } else {
                expression = new Conjunction(lhs, rhs);
            }
        } else {
            expression = rhs;
        }
    }


    @Override
    public void caseATriple(ATriple node) {
        try {
            node.apply(tripleBuilder);
            LinkedHashMap<Attribute, Node> map = tripleBuilder.getTriples();
            collector.addConstraints(map);
            expression = new SingleConstraint(map);
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseABlockOfTriples(ABlockOfTriples node) {
        if (node.getMoreTriples().size() != 0) {
            Expression lhs = getExpression((org.jrdf.urql.parser.node.Node) node.getTriple().clone());
            LinkedList<PMoreTriples> moreTriples = node.getMoreTriples();
            List<Expression> expressions = new ArrayList<Expression>();
            for (PMoreTriples pMoreTriples : moreTriples) {
                Expression rhs = getExpression((org.jrdf.urql.parser.node.Node) pMoreTriples.clone());
                expressions.add(rhs);
            }
            Expression lhsSide = lhs;
            for (Expression currentExpression : expressions) {
                lhsSide = new Conjunction(lhsSide, currentExpression);
            }
            expression = lhsSide;
        } else {
            super.caseABlockOfTriples(node);
        }
    }

    @Override
    public void caseAGraphPatternOrFilterGraphPatternOperationPattern(
        AGraphPatternOrFilterGraphPatternOperationPattern node) {
        Expression lhs = getExpression((org.jrdf.urql.parser.node.Node) node.getGraphPatternOrFilter().clone());
        Expression rhs = getExpression((org.jrdf.urql.parser.node.Node) node.getFilteredBasicGraphPattern().clone());
        if (lhs != null && rhs != null) {
            handleExpressions(rhs, lhs);
        } else if (lhs != null) {
            expression = lhs;
        } else if (rhs != null) {
            expression = rhs;
        }
    }

    @Override
    public void caseAFilterPatternGraphPatternOrFilter(AFilterPatternGraphPatternOrFilter node) {
        try {
            final LiteralBuilder builder = new LiteralBuilderImpl(graph.getElementFactory(),
                tripleBuilder.getPrefixMap());
            final URIReferenceBuilder uriReferenceBuilder = new URIReferenceBuilderImpl(graph.getElementFactory(),
                tripleBuilder.getPrefixMap());
            FilterAnalyser analyser = new FilterAnalyserImpl(builder, collector, uriReferenceBuilder);
            node.apply(analyser);
            expression = analyser.getExpression();
        } catch (ParserException e) {
            exception = e;
        }
    }

    @Override
    public void caseAGroupOrUnionGraphPattern(AGroupOrUnionGraphPattern node) {
        if (node.getUnionGraphPattern() != null) {
            Expression lhs = getExpressionWithEmptyConstraint(
                (org.jrdf.urql.parser.node.Node) node.getGroupGraphPattern().clone());
            LinkedList<PUnionGraphPattern> unionGraphPattern = node.getUnionGraphPattern();
            List<Expression> expressions = new ArrayList<Expression>();
            for (PUnionGraphPattern pUnionGraphPattern : unionGraphPattern) {
                Expression rhs = getExpressionWithEmptyConstraint(
                    (org.jrdf.urql.parser.node.Node) pUnionGraphPattern.clone());
                expressions.add(rhs);
            }
            Expression lhsSide = lhs;
            for (Expression currentExpression : expressions) {
                lhsSide = new Union(lhsSide, currentExpression);
            }
            expression = lhsSide;
        } else {
            super.caseAGroupOrUnionGraphPattern(node);
        }
    }

    @Override
    public void caseAOptionalGraphPattern(AOptionalGraphPattern node) {
        Expression rhs = getExpression((PGroupGraphPattern) node.getGroupGraphPattern().clone());
        expression = new Optional(rhs);
    }

    private void handleOptional(Expression lhs, Expression rhs) {
        Optional rhsOptional = (Optional) rhs;
        if (rhsOptional.getLhs() != null) {
            expression = new Conjunction(lhs, rhsOptional);
        } else {
            rhsOptional.setLhs(lhs);
            expression = rhsOptional;
        }
    }

    private Expression getExpressionWithEmptyConstraint(org.jrdf.urql.parser.node.Node node) {
        Expression result = getExpression(node);
        if (result == null) {
            return EMPTY_CONSTRAINT;
        } else {
            return result;
        }
    }

    private Expression getExpression(org.jrdf.urql.parser.node.Node node) {
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
