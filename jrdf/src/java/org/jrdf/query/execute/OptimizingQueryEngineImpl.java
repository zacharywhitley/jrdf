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

package org.jrdf.query.execute;

import org.jrdf.graph.Node;
import org.jrdf.query.QueryFactoryImpl;
import static org.jrdf.query.execute.QueryExecutionPlanner.getPlanner;
import org.jrdf.query.expression.Ask;
import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.Optional;
import org.jrdf.query.expression.Projection;
import org.jrdf.query.expression.SingleConstraint;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.operation.DyadicJoin;
import org.jrdf.query.relation.operation.NadicJoin;
import org.jrdf.query.relation.operation.Project;
import org.jrdf.query.relation.operation.Restrict;
import org.jrdf.query.relation.operation.Union;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version $Id:$
 */
public class OptimizingQueryEngineImpl extends NaiveQueryEngineImpl implements QueryEngine<Relation> {
    private static final RelationFactory RELATION_FACTORY = new QueryFactoryImpl().createRelationFactory();
    private boolean shortCircuit;
    private QueryExecutionPlanner planner;
    private ConstraintTupleCacheHandler cacheHandler;

    public OptimizingQueryEngineImpl(Project project, NadicJoin naturalJoin, Restrict restrict,
        Union union, DyadicJoin leftOuterJoin) {
        super(project, naturalJoin, restrict, union, leftOuterJoin);
        cacheHandler = new ConstraintTupleCacheHandlerImpl();
        shortCircuit = false;
        planner = getPlanner();
    }

    @Override
    public Relation visitAsk(Ask ask) {
        clearCacheHandler();
        cacheHandler = new ConstraintTupleCacheHandlerImpl();
        System.gc();
        shortCircuit = true;
        allVariables = ask.getAllVariables();
        result = getExpression(ask.getNextExpression(), shortCircuit);
        return result;
    }

    @Override
    public Relation visitProjection(Projection projection) {
        clearCacheHandler();
        cacheHandler = new ConstraintTupleCacheHandlerImpl();
        System.gc();
        return super.visitProjection(projection);
    }

    @Override
    public Relation visitConjunction(Conjunction conjunction) {
        List<Expression> operands = planner.flattenExpression(conjunction);
        resetCacheHandler(operands);
        Set<Relation> relations = planner.processAndRearrangeExpressions(conjunction, operands, this);
        return naturalJoin.join(relations);
    }

    @Override
    public Relation visitConstraint(SingleConstraint constraint) {
        long time = System.currentTimeMillis();
        Relation relation = processConstraint(constraint);
        cacheHandler.addResultToCache(constraint, relation, time);
        return relation;
    }

    @Override
    public Relation visitUnion(org.jrdf.query.expression.Union newUnion) {
        List<Expression> operands = planner.flattenExpression(newUnion);
        clearCacheHandler();
        Expression lhsExp = operands.get(0);
        Expression rhsExp = operands.get(1);
        Relation lhs = getExpression(lhsExp);
        if (shortCircuit) {
            result = (lhs.getTuples().isEmpty()) ? getExpression(rhsExp) : lhs;
        } else {
            Relation rhs = getExpression(operands.get(1));
            result = union.union(lhs, rhs);
        }
        return result;
    }

    @Override
    public Relation visitOptional(Optional optional) {
        clearCacheHandler();
        Relation lhs = getExpression(optional.getLhs());
        clearCacheHandler();
        Relation rhs = getExpression(optional.getRhs());
        return leftOuterJoin.join(lhs, rhs);
    }

    void clearCacheHandler() {
        cacheHandler.clear();
    }

    private void resetCacheHandler(List<Expression> constraintList) {
        cacheHandler.reset(result, constraintList.size());
    }

    private Relation processConstraint(SingleConstraint constraint) {
        Attribute curAttr = cacheHandler.findOneCachedAttribute(constraint);
        if (curAttr != null) {
            return doCachedConstraint(constraint, curAttr);
        } else {
            return restrict.restrict(result, constraint.getAvo(allVariables));
        }
    }

    private Relation doCachedConstraint(SingleConstraint constraint, Attribute curAttr) {
        Set<Tuple> tuples = new HashSet<Tuple>();
        Set<Node> voSet = cacheHandler.getCachedValues(curAttr.getAttributeName());
        for (Node newVO : voSet) {
            constraint.setAttributeValue(curAttr, newVO);
            Relation tmpRelation = restrict.restrict(result, constraint.getAvo(allVariables));
            tuples.addAll(tmpRelation.getTuples());
            tmpRelation = null;
        }
        return RELATION_FACTORY.getRelation(constraint.getAvo(allVariables).keySet(), tuples);
    }

    private void setShortCircuit(boolean shortCircuit) {
        this.shortCircuit = shortCircuit;
    }

    private void setCacheHandler(ConstraintTupleCacheHandler handler) {
        this.cacheHandler = handler;
    }

    @Override
    protected Relation getExpression(Expression expression) {
        QueryEngine<Relation> queryEngine = new OptimizingQueryEngineImpl(project, naturalJoin, restrict,
            union, leftOuterJoin);
        queryEngine.initialiseBaseRelation(result);
        queryEngine.setAllVariables(allVariables);
        ((OptimizingQueryEngineImpl) queryEngine).setCacheHandler(cacheHandler);
        return expression.accept(queryEngine);
    }

    protected Relation getExpression(Expression expression, boolean shortCircuit) {
        QueryEngine<Relation> queryEngine = new OptimizingQueryEngineImpl(project, naturalJoin, restrict,
            union, leftOuterJoin);
        queryEngine.initialiseBaseRelation(result);
        queryEngine.setAllVariables(allVariables);
        ((OptimizingQueryEngineImpl) queryEngine).setShortCircuit(shortCircuit);
        return expression.accept(queryEngine);
    }
}
