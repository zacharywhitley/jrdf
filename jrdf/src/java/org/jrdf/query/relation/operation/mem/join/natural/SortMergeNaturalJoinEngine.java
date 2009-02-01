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

package org.jrdf.query.relation.operation.mem.join.natural;

import org.jrdf.graph.NodeComparator;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleComparator;
import org.jrdf.query.relation.TupleFactory;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.mem.RelationHelper;
import org.jrdf.query.relation.mem.TupleAttributeValueComparatorImpl;
import org.jrdf.query.relation.operation.mem.join.TupleEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * @author Yuan-Fang Li
 * @version :$
 */
public class SortMergeNaturalJoinEngine extends NaturalJoinEngine implements TupleEngine {
    private static final Set<Attribute> EMPTY_ATTRIBUTE_SET = Collections.emptySet();
    private TupleComparator tupleAVComparator;

    public SortMergeNaturalJoinEngine(TupleFactory newTupleFactory, RelationHelper newRelationHelper,
        NodeComparator nodeComparator) {
        super(newTupleFactory, newRelationHelper, nodeComparator);
        this.resultantAttributeValues = new HashMap<Attribute, ValueOperation>();
        this.tupleAVComparator = new TupleAttributeValueComparatorImpl(nodeComparator);
    }

    public void processRelations(SortedSet<Attribute> headings, Relation relation1, Relation relation2,
        SortedSet<Tuple> result) {
        SortedSet<Attribute> commonHeadings = getHeadingsIntersection(relation1, relation2);
        if (commonHeadings.size() == 1) {
            // do sort merge join
            doSortMergeJoin(headings, relation1, relation2, commonHeadings.iterator().next(), EMPTY_ATTRIBUTE_SET,
                result);
        } else if (commonHeadings.size() > 1) {
            // do multi merge join
            doMultiSortMergeJoin(headings, relation1, relation2, commonHeadings, result);
        } else {
            // do natural join
            doNaturalJoin(headings, relation1.getTuples(), relation2.getTuples(), result);
        }
    }

    private void doMultiSortMergeJoin(SortedSet<Attribute> headings, Relation rel1, Relation rel2,
        SortedSet<Attribute> commonHeadings, SortedSet<Tuple> result) {
        Attribute attr = chooseACommonHeading(headings, rel1, rel2);
        commonHeadings.remove(attr);
        doSortMergeJoin(headings, rel1, rel2, attr, commonHeadings, result);
    }

    private Attribute chooseACommonHeading(Set<Attribute> headings, Relation rel1, Relation rel2) {
        final Iterator<Attribute> iterator = headings.iterator();
        Attribute attribute = iterator.next();
        Attribute result = attribute;
        long curMin = estimateJoinCost(attribute, rel1, rel2);
        while (iterator.hasNext()) {
            attribute = iterator.next();
            long cost = estimateJoinCost(attribute, rel1, rel2);
            if (curMin > cost) {
                curMin = cost;
                result = attribute;
            }
        }
        return result;
    }

    private long estimateJoinCost(Attribute attribute, Relation rel1, Relation rel2) {
        int b1, b2, ub1, ub2;
        int size1 = rel1.getTuples().size();
        int size2 = rel2.getTuples().size();
        b1 = rel1.getTuples(attribute).size();
        b2 = rel2.getTuples(attribute).size();
        ub1 = size1 - b1;
        ub2 = size2 - b2;
        return (long) b1 + b2 + (b1 * ub2) + (b2 * ub1) + (ub1 * ub2);
    }

    private void doSortMergeJoin(SortedSet<Attribute> headings, Relation rel1, Relation rel2,
        Attribute attribute, Set<Attribute> remainingHeadings, SortedSet<Tuple> result) {
        final List<Set<Tuple>> sets1 = partitionWithAttribute(attribute, rel1);
        final List<Set<Tuple>> sets2 = partitionWithAttribute(attribute, rel2);
        final List<Tuple> list1 = sortSetOfTuples(sets1.get(0), attribute);
        final List<Tuple> list2 = sortSetOfTuples(sets2.get(0), attribute);
        if (list1.size() <= list2.size()) {
            doProperSortMergeJoin(attribute, remainingHeadings, result, list1, list2);
        } else {
            doProperSortMergeJoin(attribute, remainingHeadings, result, list2, list1);
        }
        doNaturalJoin(headings, sets1.get(0), sets2.get(1), result);
        doNaturalJoin(headings, sets2.get(0), sets1.get(1), result);
        doNaturalJoin(headings, sets1.get(1), sets2.get(1), result);
    }

    private List<Tuple> sortSetOfTuples(Set<Tuple> tuples, Attribute attribute) {
        tupleAVComparator.setAttribute(attribute);
        List<Tuple> list = new ArrayList<Tuple>(tuples);
        Collections.sort(list, tupleAVComparator);
        return list;
    }

    private void doNaturalJoin(SortedSet<Attribute> headings, Set<Tuple> tuples1, Set<Tuple> tuples2,
        SortedSet<Tuple> result) {
        if (tuples1.size() < tuples2.size()) {
            startDoubleLoopProcessing(headings, result, tuples1, tuples2);
        } else {
            startDoubleLoopProcessing(headings, result, tuples2, tuples1);
        }
    }

    /**
     * Returns an array of two sets. The first is a bound set and the second is the unbound set.
     *
     * @param attribute The attribute used to test for boundness.
     * @param rel       The relation to be partitioned.
     * @return two sets of tuples, firrst is the bound and the 2nd is the unbound.
     */
    private List<Set<Tuple>> partitionWithAttribute(Attribute attribute, Relation rel) {
        Set<Tuple> boundSet = new HashSet<Tuple>();
        Set<Tuple> unboundSet = new HashSet<Tuple>();
        for (Tuple tuple : rel.getTuples()) {
            if (tuple.getValueOperation(attribute) != null) {
                boundSet.add(tuple);
            } else if (!boundSet.contains(tuple)) {
                unboundSet.add(tuple);
            }
        }
        final ArrayList<Set<Tuple>> list = new ArrayList<Set<Tuple>>(2);
        list.add(boundSet);
        list.add(unboundSet);
        return list;
    }

    private void doProperSortMergeJoin(Attribute attribute, Set<Attribute> commonHeadings, SortedSet<Tuple> result,
        List<Tuple> list1, List<Tuple> list2) {
        tupleAVComparator.setAttribute(attribute);
        Tuple tuple1, tuple2;
        int i = 0;
        int j = 0;
        tuple1 = getTupleFromList(list1, i);
        tuple2 = getTupleFromList(list2, j);
        while (tuple1 != null && tuple2 != null) {
            int compare = tupleAVComparator.compare(tuple1, tuple2);
            if (compare == 0) {
                int[] newInds = processSameTuples(list1, list2, attribute, result, commonHeadings, i, j, tuple1);
                i = newInds[0];
                j = newInds[1];
                tuple1 = getTupleFromList(list1, i);
                tuple2 = getTupleFromList(list2, j);
            } else if (compare > 0) {
                tuple2 = getTupleFromList(list2, ++j);
            } else {
                tuple1 = getTupleFromList(list1, ++i);
            }
        }
    }

    private Tuple getTupleFromList(List<Tuple> list, int idx) {
        if (idx < list.size()) {
            return list.get(idx);
        }
        return null;
    }

    private int[] processSameTuples(List<Tuple> l1, List<Tuple> l2, Attribute attribute, SortedSet<Tuple> result,
        Set<Attribute> commonHeadings, int pos1, int pos2, Tuple pivot) {
        Tuple t1, t2;
        int newPos1 = pos1 + 1;
        int newPos2 = pos2 + 1;
        for (int i = pos1; i < l1.size(); i++) {
            t1 = l1.get(i);
            if (tupleAVComparator.compare(t1, pivot) != 0) {
                newPos1 = i;
                break;
            }
            for (int j = pos2; j < l2.size(); j++) {
                newPos2 = j;
                t2 = l2.get(j);
                if (tupleAVComparator.compare(t1, t2) != 0) {
                    break;
                }
                addToResult(attribute, result, t1, t2, commonHeadings);
            }
        }
        return new int[]{newPos1, newPos2};
    }

    private void addToResult(Attribute attribute, SortedSet<Tuple> result, Tuple tuple1, Tuple tuple2,
        Set<Attribute> commonHeadings) {
        boolean contradiction =
            avp1NotNull(attribute, tuple1.getValueOperation(attribute), tuple2.getValueOperation(attribute)) ||
                checkCommonHeadings(commonHeadings, tuple1, tuple2);

        if (!contradiction) {
            result.add(tupleFactory.getTuple(tuple1, tuple2));
        }
    }

    private boolean checkCommonHeadings(Set<Attribute> commonHeadings, Tuple tuple1, Tuple tuple2) {
        boolean contradiction = false;
        for (Attribute attribute : commonHeadings) {
            contradiction = processTuplePair(tuple1, tuple2, attribute);
            if (contradiction) {
                return contradiction;
            }
        }
        return contradiction;
    }
}
