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

import org.jrdf.graph.NodeComparator;
import static org.jrdf.query.execute.ExpressionComparatorImpl.EXPRESSION_COMPARATOR;
import org.jrdf.query.expression.Ask;
import org.jrdf.query.expression.Conjunction;
import org.jrdf.query.expression.Expression;
import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.SingleConstraint;
import org.jrdf.query.expression.Projection;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.RelationComparator;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleComparator;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.attributename.AttributeNameComparator;
import org.jrdf.query.relation.attributename.AttributeNameComparatorImpl;
import org.jrdf.query.relation.mem.AttributeComparatorImpl;
import org.jrdf.query.relation.mem.ComparatorFactory;
import org.jrdf.query.relation.mem.ComparatorFactoryImpl;
import org.jrdf.query.relation.mem.RelationFactoryImpl;
import org.jrdf.query.relation.mem.SimpleRelationComparatorImpl;
import org.jrdf.query.relation.mem.TupleComparatorImpl;
import org.jrdf.query.relation.operation.DyadicJoin;
import org.jrdf.query.relation.operation.NadicJoin;
import org.jrdf.query.relation.operation.Project;
import org.jrdf.query.relation.operation.Restrict;
import org.jrdf.query.relation.operation.Union;
import org.jrdf.query.relation.type.TypeComparator;
import org.jrdf.query.relation.type.TypeComparatorImpl;
import org.jrdf.util.NodeTypeComparator;
import org.jrdf.util.NodeTypeComparatorImpl;

import java.util.Collections;
import static java.util.Collections.swap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version $Id: $
 */
public class OptimizingQueryEngineImpl extends NaiveQueryEngineImpl implements QueryEngine {
    private static final NodeTypeComparator NODE_TYPE_COMPARATOR = new NodeTypeComparatorImpl();
    private static final TypeComparator TYPE_COMPARATOR = new TypeComparatorImpl(NODE_TYPE_COMPARATOR);
    private static final AttributeNameComparator ATTRIBUTE_NAME_COMPARATOR = new AttributeNameComparatorImpl();
    private static final AttributeComparator ATTRIBUTE_COMPARATOR = new AttributeComparatorImpl(TYPE_COMPARATOR,
        ATTRIBUTE_NAME_COMPARATOR);
    private static final ComparatorFactory COMPARATOR_FACTORY = new ComparatorFactoryImpl();
    private static final NodeComparator NODE_COMPARATOR = COMPARATOR_FACTORY.createNodeComparator();
    private static final TupleComparator TUPLE_COMPARATOR = new TupleComparatorImpl(NODE_COMPARATOR,
        ATTRIBUTE_COMPARATOR);
    private static final RelationFactory RELATION_FACTORY = new RelationFactoryImpl(ATTRIBUTE_COMPARATOR,
        TUPLE_COMPARATOR);

    private final RelationComparator relationComparator = new SimpleRelationComparatorImpl();
    private final ExpressionComparator expressionComparator = EXPRESSION_COMPARATOR;
    private boolean shortCircuit;
    private ConstraintTupleCacheHandler cacheHandler;

    public OptimizingQueryEngineImpl(Project project, NadicJoin naturalJoin, Restrict restrict,
        Union union, DyadicJoin leftOuterJoin) {
        super(project, naturalJoin, restrict, union, leftOuterJoin);
        cacheHandler = new ConstraintTupleCacheHandlerImpl();
        shortCircuit = false;
    }

    @Override
    public <V extends ExpressionVisitor> void visitAsk(Ask<V> ask) {
        shortCircuit = true;
        allVariables = ask.getAllVariables();
        result = getExpression(ask.getNextExpression(), shortCircuit);
        cacheHandler.clear();
    }

    @Override
    public <V extends ExpressionVisitor> void visitProjection(Projection<V> projection) {
        cacheHandler.clear();
        super.visitProjection(projection);
    }

    // TODO YF join those with common attributes first.
    @Override
    public <V extends ExpressionVisitor> void visitConjunction(Conjunction<V> conjunction) {
        BiOperandExpressionSimplifier simplifier = new BiOperandExpressionSimplifierImpl(expressionComparator);
        List<Expression<V>> constraintList = simplifier.flattenAndSortConjunction(conjunction, Conjunction.class);
        cacheHandler.reset(result, constraintList.size());
        List<Relation> partialResult = new LinkedList<Relation>();
        for (Expression<V> exp : constraintList) {
            Relation tempRelation = getExpression(exp);
            if (tempRelation.getTuples().isEmpty()) {
                result = tempRelation;
                return;
            }
            partialResult.add(tempRelation);
        }
        cacheHandler.clear();
        Collections.sort(partialResult, relationComparator);
        Set<Relation> partialResultSet = matchAttributes(partialResult);
        result = naturalJoin.join(partialResultSet);
    }

    private Set<Relation> matchAttributes(List<Relation> partialResults) {
        for (int i = 0; i < partialResults.size(); i++) {
            matchAttributes(partialResults, i);
        }
        return new LinkedHashSet<Relation>(partialResults);
    }

    private void matchAttributes(List<Relation> relations, int pos) {
        Relation first = relations.get(pos);
        int idx, badPos = -1;
        for (idx = pos + 1; idx < relations.size(); idx++) {
            final Relation nextRel = relations.get(idx);
            final Set<Attribute> headings = getCommonHeadings(first, nextRel);
            if (headings.size() >= 1) {
                break;
            } else if (badPos < 0) {
                badPos = idx;
            }
        }
        if (badPos > 0 && idx < relations.size()) {
            swap(relations, badPos, idx);
        }
    }

    private Set<Attribute> getCommonHeadings(Relation rel1, Relation rel2) {
        final Set<Attribute> set1 = new HashSet<Attribute>(rel1.getHeading());
        set1.retainAll(rel2.getHeading());
        return set1;
    }

    @Override
    public <V extends ExpressionVisitor> void visitConstraint(SingleConstraint<V> constraint) {
        long time = System.currentTimeMillis();
        processConstraint(constraint);
        cacheHandler.addResultToCache(constraint, result, time);
    }

    private <V extends ExpressionVisitor> void processConstraint(SingleConstraint<V> constraint) {
        Attribute curAttr = cacheHandler.findOneCachedAttribute(constraint);
        if (curAttr != null) {
            doCachedConstraint(constraint, curAttr);
        } else {
            result = restrict.restrict(result, constraint.getAvo(allVariables));
        }
    }

    private <V extends ExpressionVisitor> void doCachedConstraint(SingleConstraint<V> constraint, Attribute curAttr) {
        Set<Tuple> tuples = new HashSet<Tuple>();
        Set<ValueOperation> voSet = cacheHandler.getCachedValues(curAttr.getAttributeName());
        for (ValueOperation newVO : voSet) {
            constraint.setAvo(curAttr, newVO);
            Relation tmpRelation = restrict.restrict(result, constraint.getAvo(allVariables));
            tuples.addAll(tmpRelation.getTuples());
        }
        result = RELATION_FACTORY.getRelation(constraint.getAvo(allVariables).keySet(), tuples);
    }

    @Override
    // TODO YF PERFORMANCE too bad!
    public <V extends ExpressionVisitor> void visitUnion(org.jrdf.query.expression.Union<V> newUnion) {
        BiOperandExpressionSimplifier simplifier = new BiOperandExpressionSimplifierImpl(expressionComparator);
        List<Expression<V>> list = simplifier.flattenAndSortConjunction(newUnion, Union.class);
        Expression<V> lhsExp = list.get(0);
        Expression<V> rhsExp = list.get(1);
        Relation lhs = getExpression(lhsExp);
        if (shortCircuit) {
            result = (lhs.getTuples().isEmpty()) ? getExpression(rhsExp) : lhs;
        } else {
            Relation rhs = getExpression(list.get(1));
            result = union.union(lhs, rhs);
        }
    }

    private void setShortCircuit(boolean shortCircuit) {
        this.shortCircuit = shortCircuit;
    }

    private void setCacheHandler(ConstraintTupleCacheHandler handler) {
        this.cacheHandler = handler;
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    protected <V extends ExpressionVisitor> Relation getExpression(Expression<V> expression) {
        QueryEngine queryEngine = new OptimizingQueryEngineImpl(project, naturalJoin, restrict,
            union, leftOuterJoin);
        queryEngine.initialiseBaseRelation(result);
        queryEngine.setAllVariables(allVariables);
        ((OptimizingQueryEngineImpl) queryEngine).setCacheHandler(cacheHandler);
        expression.accept((V) queryEngine);
        return queryEngine.getResult();
    }

    @SuppressWarnings({ "unchecked" })
    protected <V extends ExpressionVisitor> Relation getExpression(Expression<V> expression, boolean shortCircuit) {
        QueryEngine queryEngine = new OptimizingQueryEngineImpl(project, naturalJoin, restrict,
            union, leftOuterJoin);
        queryEngine.initialiseBaseRelation(result);
        queryEngine.setAllVariables(allVariables);
        ((OptimizingQueryEngineImpl) queryEngine).setShortCircuit(shortCircuit);
        expression.accept((V) queryEngine);
        return queryEngine.getResult();
    }
}
