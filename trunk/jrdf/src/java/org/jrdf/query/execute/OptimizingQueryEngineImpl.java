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

import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.RelationComparator;
import org.jrdf.query.relation.mem.SimpleRelationComparatorImpl;
import org.jrdf.query.relation.operation.DyadicJoin;
import org.jrdf.query.relation.operation.NadicJoin;
import org.jrdf.query.relation.operation.Project;
import org.jrdf.query.relation.operation.Restrict;
import org.jrdf.query.relation.operation.SemiDifference;
import org.jrdf.query.relation.operation.Union;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class OptimizingQueryEngineImpl extends NaiveQueryEngineImpl implements QueryEngine {
    private final RelationComparator relationComparator = new SimpleRelationComparatorImpl();

    public OptimizingQueryEngineImpl(Project project, NadicJoin naturalJoin, Restrict restrict,
        Union union, DyadicJoin leftOuterJoin, SemiDifference diff) {
        super(project, naturalJoin, restrict, union, leftOuterJoin, diff);
    }

    @Override
    public <V extends ExpressionVisitor> void visitConjunction(Conjunction<V> conjunction) {
        Set<Expression<V>> set = new HashSet<Expression<V>>();
        SortedSet<Relation> partialResult = new TreeSet<Relation>(relationComparator);
        flattenConjunction(conjunction, set);
        Relation tempRelation = result;
        for (Expression exp : set) {
            exp.accept(this);
            partialResult.add(result);
            result = tempRelation;
        }
        result = naturalJoin.join(partialResult);
    }

    private <V extends ExpressionVisitor> void flattenConjunction(Conjunction<V> conjunction, Set<Expression<V>> set) {
        final Expression<V> lhs = conjunction.getLhs();
        final Expression<V> rhs = conjunction.getRhs();
        addExpressionToSet(lhs, set);
        addExpressionToSet(rhs, set);
    }

    private <V extends ExpressionVisitor> void addExpressionToSet(Expression<V> expression, Set<Expression<V>> set) {
        if (Conjunction.class.isAssignableFrom(expression.getClass())) {
            flattenConjunction((Conjunction) expression, set);
        } else {
            set.add(expression);
        }
    }

    @SuppressWarnings({ "unchecked" })
    protected <V extends ExpressionVisitor> Relation getExpression(Expression<V> expression) {
        QueryEngine queryEngine = new OptimizingQueryEngineImpl(project, naturalJoin, restrict,
            union, leftOuterJoin, diff);
        queryEngine.initialiseBaseRelation(result);
        queryEngine.setAllVariables(allVariables);
        expression.accept((V) queryEngine);
        return queryEngine.getResult();
    }
}
