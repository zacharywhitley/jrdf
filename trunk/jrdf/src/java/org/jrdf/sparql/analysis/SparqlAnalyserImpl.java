/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2005 The JRDF Project.  All rights reserved.
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
import org.jrdf.query.Query;
import org.jrdf.query.QueryImpl;
import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.Constraint;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.sparql.builder.TripleBuilder;
import org.jrdf.sparql.parser.analysis.DepthFirstAdapter;
import org.jrdf.sparql.parser.node.APatternElementsList;
import org.jrdf.sparql.parser.node.ATriple;
import org.jrdf.sparql.parser.node.Node;

import java.util.SortedSet;

/**
 * Default implementation of {@link SparqlAnalyser}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class SparqlAnalyserImpl extends DepthFirstAdapter implements SparqlAnalyser {

    // FIXME TJA: Should eventually be using a Expression builder here.
    private Query query = SparqlAnalyser.NO_QUERY;
    private TripleBuilder tripleBuilder;
    private Graph graph;
    private Expression<ExpressionVisitor> expression;


    public SparqlAnalyserImpl(TripleBuilder tripleBuilder, Graph graph) {
        this.tripleBuilder = tripleBuilder;
        this.graph = graph;
    }

    /**
     * {@inheritDoc}
     */
    public Query getQuery() {
        if (getExpression() != null && query == SparqlAnalyser.NO_QUERY) {
            query = new QueryImpl(expression);
        }
        return query;
    }

    public Expression<ExpressionVisitor> getExpression() {
        return expression;
    }

//    @Override
//    public void caseAVariableListSelectClause(AVariableListSelectClause node) {
//        LinkedList<PVariable> variables = node.getVariable();
//        Set<Attribute> attributes = new HashSet<Attribute>();
//        for (PVariable variable : variables) {
//            VariableAnalyser variableAnalyser = new VariableAnalyser();
//            variable.apply(variableAnalyser);
//            attributes.add(variableAnalyser.getAttribute());
//        }
//        Expression<ExpressionVisitor> expression = getExpression((Node) node.clone());
//        this.expression = new Projection<ExpressionVisitor>(attributes, expression);
//    }

    @Override
    public void caseATriple(ATriple tripleNode) {
        SortedSet<AttributeValuePair> attributeValuePairs = tripleBuilder.build(tripleNode, graph);
        expression = new Constraint<ExpressionVisitor>(attributeValuePairs);
    }

    @Override
    public void caseAPatternElementsList(APatternElementsList node) {
        if (node.getPatternElementsListTail() != null)  {
            Expression<ExpressionVisitor> lhs = getExpression(node.getPatternElement());
            Expression<ExpressionVisitor> rhs = getExpression(node.getPatternElementsListTail());
            expression = new Conjunction<ExpressionVisitor>(lhs, rhs);
        } else {
            super.caseAPatternElementsList(node);
        }
    }

    private Expression<ExpressionVisitor> getExpression(Node node) {
        SparqlAnalyserImpl analyser = new SparqlAnalyserImpl(tripleBuilder, graph);
        node.apply(analyser);
        return analyser.getExpression();
    }
}
