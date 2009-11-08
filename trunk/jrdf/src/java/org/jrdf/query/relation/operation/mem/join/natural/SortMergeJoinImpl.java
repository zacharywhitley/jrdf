/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 *  ====================================================================
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
 */

package org.jrdf.query.relation.operation.mem.join.natural;

import org.jrdf.graph.Node;
import org.jrdf.graph.NodeComparator;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.EvaluatedRelation;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleFactory;
import org.jrdf.query.relation.mem.RelationHelper;
import org.jrdf.query.relation.operation.mem.join.TupleEngine;

import java.util.SortedSet;

public class SortMergeJoinImpl implements SortMergeJoin {
    private final TupleEngine engine;
    private final NodeComparator nodeComparator;
    private final RelationFactory relationFactory;
    private final RelationHelper relationHelper;
    private final TupleFactory tupleFactory;
    private int pos1;
    private int pos2;

    public SortMergeJoinImpl(TupleEngine newNaturalJoinEngine, NodeComparator newNodeComparator,
        RelationFactory newRelationFactory, RelationHelper newRelationHelper, TupleFactory newTupleFactory) {
        this.engine = newNaturalJoinEngine;
        this.nodeComparator = newNodeComparator;
        this.relationFactory = newRelationFactory;
        this.relationHelper = newRelationHelper;
        this.tupleFactory = newTupleFactory;
    }

    public void mergeJoin(SortedSet<Attribute> headings, EvaluatedRelation rel1, EvaluatedRelation rel2,
        Attribute attribute, SortedSet<Attribute> commonHeadings, SortedSet<Tuple> result) {
        final PartitionedRelation sets1 = new PartitionedRelationImpl(nodeComparator, attribute, rel1);
        final PartitionedRelation sets2 = new PartitionedRelationImpl(nodeComparator, attribute, rel2);
        if (sets1.getBoundSet().size() <= sets2.getBoundSet().size()) {
            doProperSortMergeJoin(commonHeadings, sets1, sets2, attribute, result);
        } else {
            doProperSortMergeJoin(commonHeadings, sets2, sets1, attribute, result);
        }
        engine.processRelations(headings, relationFactory.getRelation(sets1.getBoundSet()),
            relationFactory.getRelation(sets2.getUnboundSet()), result);
        engine.processRelations(headings, relationFactory.getRelation(sets2.getBoundSet()),
            relationFactory.getRelation(sets1.getUnboundSet()), result);
        engine.processRelations(headings, relationFactory.getRelation(sets1.getUnboundSet()),
            relationFactory.getRelation(sets2.getUnboundSet()), result);
    }

    private void doProperSortMergeJoin(SortedSet<Attribute> commonHeadings, PartitionedRelation sets1,
        PartitionedRelation sets2, Attribute attribute, SortedSet<Tuple> result) {
        pos1 = 0;
        pos2 = 0;
        Tuple tuple1 = sets1.getTupleFromList(pos1);
        Tuple tuple2 = sets2.getTupleFromList(pos2);
        while (tuple1 != null && tuple2 != null) {
            int compare = nodeComparator.compare(tuple1.getValue(attribute), tuple2.getValue(attribute));
            if (compare == 0) {
                processSameTuples(commonHeadings, attribute, sets1, sets2, tuple1, result);
                tuple1 = sets1.getTupleFromList(pos1);
                tuple2 = sets2.getTupleFromList(pos2);
            } else if (compare > 0) {
                tuple2 = sets2.getTupleFromList(++pos2);
            } else {
                tuple1 = sets1.getTupleFromList(++pos1);
            }
        }
    }

    private void processSameTuples(SortedSet<Attribute> commonHeadings, Attribute attribute, PartitionedRelation l1,
        PartitionedRelation l2, Tuple pivot, SortedSet<Tuple> result) {
        int newPos2 = pos2 + 1;
        boolean incrementPos1 = true;
        for (int i = pos1; i < l1.getSourceBoundSet().size(); i++) {
            Tuple t1 = l1.getSourceBoundSet().get(i);
            final Node t1Value = t1.getValue(attribute);
            if (nodeComparator.compare(t1Value, pivot.getValue(attribute)) != 0) {
                pos1 = i;
                incrementPos1 = false;
                break;
            } else {
                for (int j = pos2; j < l2.getSourceBoundSet().size(); j++) {
                    newPos2 = j;
                    Tuple t2 = l2.getSourceBoundSet().get(j);
                    if (nodeComparator.compare(t1Value, t2.getValue(attribute)) != 0) {
                        break;
                    }
                    addToResult(commonHeadings, t1, t2, result);
                }
            }
        }
        if (incrementPos1) {
            pos1++;
        }
        pos2 = newPos2;
    }

    private void addToResult(SortedSet<Attribute> commonHeadings, Tuple tuple1, Tuple tuple2, SortedSet<Tuple> result) {
        if (!relationHelper.areIncompatible(commonHeadings, tuple1, tuple2)) {
            result.add(tupleFactory.getTuple(tuple1, tuple2));
        }
    }
}
