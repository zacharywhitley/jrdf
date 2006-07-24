/*
 * $Header$
 * $Revision: 439 $
 * $Date: 2006-01-27 06:19:29 +1000 (Fri, 27 Jan 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2006 The JRDF Project.  All rights reserved.
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
 */

package org.jrdf.sparql.analysis;

import org.jrdf.graph.Graph;
import org.jrdf.query.expression.Constraint;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.Optional;
import org.jrdf.query.expression.Union;
import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.type.NodeType;
import org.jrdf.sparql.builder.TripleBuilder;
import org.jrdf.sparql.parser.analysis.DepthFirstAdapter;
import org.jrdf.sparql.parser.node.ABlockOfTriples;
import org.jrdf.sparql.parser.node.AFilteredBasicGraphPatternGraphPattern;
import org.jrdf.sparql.parser.node.AGroupOrUnionGraphPattern;
import org.jrdf.sparql.parser.node.AOptionalGraphPattern;
import org.jrdf.sparql.parser.node.ATriple;
import org.jrdf.sparql.parser.node.Node;
import org.jrdf.sparql.parser.node.PGroupGraphPattern;
import org.jrdf.sparql.parser.node.PMoreTriples;
import org.jrdf.sparql.parser.node.PUnionGraphPattern;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * Default implementation of {@link org.jrdf.sparql.analysis.SparqlAnalyser}.
 */
public final class WhereAnalyserImpl extends DepthFirstAdapter {
    private TripleBuilder tripleBuilder;
    private Graph graph;
    private VariableCollector collector;
    private Expression<ExpressionVisitor> expression;

    public WhereAnalyserImpl(TripleBuilder tripleBuilder, Graph graph, VariableCollector collector) {
        this.tripleBuilder = tripleBuilder;
        this.graph = graph;
        this.collector = collector;
    }

    public Expression<ExpressionVisitor>getExpression() {
        return expression;
    }

    @Override
    public void caseAFilteredBasicGraphPatternGraphPattern(AFilteredBasicGraphPatternGraphPattern node) {
        if (node.getOperationPattern() != null) {
            Expression<ExpressionVisitor> lhs = getExpression((Node) node.getFilteredBasicGraphPattern().clone());
            Expression<ExpressionVisitor> rhs = getExpression((Node) node.getOperationPattern().clone());
            if (rhs instanceof Optional) {
                ((Optional<ExpressionVisitor>) rhs).setLhs(lhs);
                expression = rhs;
            }
            if (rhs instanceof Union) {
                expression = rhs;
            }
        } else {
            super.caseAFilteredBasicGraphPatternGraphPattern(node);
        }
    }

    @Override
    public void caseATriple(ATriple node) {
        SortedSet<AttributeValuePair> attributeValuePairs = tripleBuilder.build(node, graph);
        collector.addVariables(attributeValuePairs);
        expression = new Constraint<ExpressionVisitor>(attributeValuePairs);
    }

    @Override
    public void caseABlockOfTriples(ABlockOfTriples node) {
        if (node.getMoreTriples().size() != 0) {
            Expression<ExpressionVisitor> lhs = getExpression((Node) node.getTriple().clone());
            LinkedList<PMoreTriples> moreTriples = node.getMoreTriples();
            List<Expression<ExpressionVisitor>> expressions = new ArrayList <Expression<ExpressionVisitor>>();
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
    public void caseAGroupOrUnionGraphPattern(AGroupOrUnionGraphPattern node) {
        if (node.getUnionGraphPattern() != null) {
            Expression<ExpressionVisitor> lhs = getExpression((PGroupGraphPattern) node.getGroupGraphPattern().clone());
            LinkedList<PUnionGraphPattern> unionGraphPattern = node.getUnionGraphPattern();
            List<Expression<ExpressionVisitor>> expressions = new ArrayList <Expression<ExpressionVisitor>>();
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

    public Set<Attribute> getAttributes(Set<AttributeName> declaredVariables) {
        Set<Attribute> newAttributes = new LinkedHashSet<Attribute>();
        Map<String, NodeType> variables = collector.getVariables();
        for (AttributeName variable : declaredVariables) {
            NodeType type = variables.get(variable.getLiteral());
            if (type == null) {
                throw new RuntimeException("Failed to find: " + variable);
            } else {
                Attribute attribute = new AttributeImpl(variable, type);
                newAttributes.add(attribute);
            }
        }
        return newAttributes;
    }

    private Expression<ExpressionVisitor> getExpression(Node node) {
        WhereAnalyserImpl analyser = new WhereAnalyserImpl(tripleBuilder, graph, collector);
        node.apply(analyser);
        return analyser.getExpression();
    }
}
