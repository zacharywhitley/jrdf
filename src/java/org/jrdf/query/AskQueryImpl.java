/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.query;

import org.jrdf.graph.Graph;
import org.jrdf.query.answer.Answer;
import org.jrdf.query.answer.AskAnswerImpl;
import org.jrdf.query.execute.ExpressionSimplifier;
import org.jrdf.query.execute.ExpressionSimplifierImpl;
import org.jrdf.query.execute.QueryEngine;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.relation.EvaluatedRelation;
import org.jrdf.query.relation.mem.GraphRelationFactory;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

/**
 * @author Yuan-Fang Li
 * @version $Id$
 */
public class AskQueryImpl implements Query {
    private Expression expression;
    private final GraphRelationFactory graphRelationFactory;

    public AskQueryImpl(Expression newExpression, GraphRelationFactory newGraphRelationFactory) {
        checkNotNull(newExpression, newGraphRelationFactory);
        this.expression = newExpression;
        this.graphRelationFactory = newGraphRelationFactory;
    }

    public Expression getNext() {
        return expression;
    }

    public Answer executeQuery(Graph graph, QueryEngine queryEngine) {
        checkNotNull(graph, queryEngine);
        long timeStarted = System.currentTimeMillis();
        boolean result = getResult(graph, queryEngine);
        return new AskAnswerImpl(System.currentTimeMillis() - timeStarted, result);
    }

    private boolean getResult(Graph graph, QueryEngine queryEngine) {
        EvaluatedRelation entireGraph = graphRelationFactory.createRelation(graph);
        queryEngine.initialiseBaseRelation(entireGraph);
        ExpressionSimplifier<Void> simplifier = new ExpressionSimplifierImpl();
        expression.accept(simplifier);
        expression = simplifier.getExpression();
        if (simplifier.parseAgain()) {
            expression.accept(simplifier);
            expression = simplifier.getExpression();
        }
        EvaluatedRelation relation = expression.accept(queryEngine);
        return !relation.isEmpty();
    }
}
