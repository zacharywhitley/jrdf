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

package org.jrdf.query.relation.operation.mem.restrict;

import org.jrdf.query.expression.ExpressionVisitor;
import org.jrdf.query.expression.logic.LogicExpression;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.GraphRelation;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleComparator;
import org.jrdf.query.relation.TupleFactory;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.attributename.PositionName;
import org.jrdf.query.relation.operation.BooleanEvaluator;
import org.jrdf.query.relation.operation.Restrict;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The relational operation that remove tuples that don't meet a specific criteria.
 *
 * @author Andrew Newman
 * @version $Revision:$
 */
public class RestrictImpl implements Restrict {
    private final RelationFactory relationFactory;
    private final TupleFactory tupleFactory;
    private final TupleComparator tupleComparator;
    private final BooleanEvaluator evaluator;

    public RestrictImpl(RelationFactory relationFactory, TupleFactory tupleFactory,
        TupleComparator tupleComparator, BooleanEvaluator evaluator) {
        this.relationFactory = relationFactory;
        this.tupleFactory = tupleFactory;
        this.tupleComparator = tupleComparator;
        this.evaluator = evaluator;
    }

    // TODO (AN) Implement a table scan version when we can't get to a indexed/graph based relation.
    public Relation restrict(Relation relation, LinkedHashMap<Attribute, ValueOperation> avo) {
        if (relation instanceof GraphRelation) {
            return restrict((GraphRelation) relation, avo);
        } else {
            final Set<Tuple> restrictedTuples = relation.getTuples(avo);
            boolean hasValidVarName = relationHasValidVariableNames(relation);
            return createRelation(relation, restrictedTuples, hasValidVarName);
        }
    }

    public Relation restrict(GraphRelation relation, LinkedHashMap<Attribute, ValueOperation> avo) {
        Set<Tuple> restrictedTuples = relation.getTuples(avo);
        boolean hasValidVarName = relationHasValidVariableNames(relation);
        return createRelation(relation, restrictedTuples, hasValidVarName);
    }

    public <V extends ExpressionVisitor> Relation restrict(Relation relation, LogicExpression<V> expression) {
        final Set<Tuple> restrictedTuples = relation.getTuples();
        Set<Tuple> result = new TreeSet<Tuple>(tupleComparator);
        for (Tuple tuple : restrictedTuples) {
            if (evaluator.evaluate(tuple, expression)) {
                result.add(tuple);
            }
        }
        boolean hasValidVarName = relationHasValidVariableNames(relation);
        return createRelation(relation, result, hasValidVarName);
    }

    // TODO YF may not get all headings, but should be potentially faster
    private Relation createRelation(Relation relation, Set<Tuple> restrictedTuples, boolean hasValidVarName) {
        if (hasValidVarName) {
            return relationFactory.getRelation(relation.getHeading(), restrictedTuples);
        } else {
            return relationFactory.getRelation(restrictedTuples);
        }
    }

    public Relation restrict(Map<Attribute, ValueOperation> avo) {
        Tuple tuple = tupleFactory.getTuple(avo);
        SortedSet<Tuple> resultTuples = new TreeSet<Tuple>(tupleComparator);
        resultTuples.add(tuple);
        return relationFactory.getRelation(resultTuples);
    }

    private boolean relationHasValidVariableNames(Relation relation) {
        boolean hasValidVarName = false;
        for (Attribute att : relation.getHeading()) {
            if (PositionName.class.isAssignableFrom(att.getAttributeName().getClass())) {
                hasValidVarName = false;
                break;
            } else {
                hasValidVarName = true;
            }
        }
        return hasValidVarName;
    }
}
