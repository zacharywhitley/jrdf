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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class SortMergeNaturalJoinEngine extends NaturalJoinEngine implements TupleEngine {
    private TupleComparator tupleAVComparator;

    public SortMergeNaturalJoinEngine(TupleFactory newTupleFactory, RelationHelper newRelationHelper,
                                      NodeComparator nodeComparator) {
        super(newTupleFactory, newRelationHelper);
        this.resultantAttributeValues = new HashMap<Attribute, ValueOperation>();
        this.tupleAVComparator = new TupleAttributeValueComparatorImpl(nodeComparator);
    }

    public void processRelations(SortedSet<Attribute> headings, Relation relation1, Relation relation2,
                                 SortedSet<Tuple> result) {
        SortedSet<Attribute> commonHeadings = getHeadingsIntersection(relation1, relation2);
        Set<Tuple> unboundTuples1 = new HashSet<Tuple>();
        Set<Tuple> unboundTuples2 = new HashSet<Tuple>();
        Set<Tuple>[] boundSets = partitionTuples(commonHeadings, relation1, relation2, unboundTuples1, unboundTuples2);
        System.err.println("total tuple # = " + (relation1.getTuples().size() + relation2.getTuples().size()));
        for (Set<Tuple> tuples : boundSets) {
            System.err.println("bound # = " + tuples.size());
        }
        System.err.println("unbound1 # " + unboundTuples1.size());
        System.err.println("unbound2 # " + unboundTuples2.size());
        System.err.println("SMJOin headings: " + commonHeadings);
        if (commonHeadings.size() == 1) {
            // do sort merge join
            doSortMergeJoin(boundSets[0], boundSets[1], commonHeadings.iterator().next(), result);
        } else if (commonHeadings.size() > 1) {
            // do multi merge join
            doMultiSortMergeJoin(boundSets[0], boundSets[1],
                    commonHeadings, result);
        }
        // do natural join
        doNaturalJoin(headings, relation1.getTuples(), relation2.getTuples(), result);
    }

    private void doMultiSortMergeJoin(Set<Tuple> bound1, Set<Tuple> bound2, SortedSet<Attribute> commonHeadings,
                                      SortedSet<Tuple> result) {
        doMultiHeadingMergeJoin(bound1, bound2, commonHeadings, result);
        /*// do natural join
        long start = System.currentTimeMillis();
        doNaturalJoin(headings, unbound1, unbound2, result);
        System.err.println("natural join took " + (System.currentTimeMillis() - start));*/
    }

    private void doMultiHeadingMergeJoin(Set<Tuple> bound1, Set<Tuple> bound2, SortedSet<Attribute> commonHeadings,
                                         SortedSet<Tuple> result) {
        long start = System.currentTimeMillis();
        for (Tuple t1 : bound1) {
            for (Tuple t2 : bound2) {
                compareAndAddToResult(commonHeadings, result, t1, t2);
            }
        }
        System.err.println("Multi join took " + (System.currentTimeMillis() - start));
    }

    private void compareAndAddToResult(SortedSet<Attribute> commonHeadings, SortedSet<Tuple> result,
                                          Tuple t1, Tuple t2) {
        boolean contradiction = false;
        for (Attribute attribute : commonHeadings) {
            contradiction = this.compareAVPs(attribute, t1.getValueOperation(attribute),
                t2.getValueOperation(attribute));
            if (contradiction) {
                break;
            }
        }
        if (!contradiction) {
            result.add(tupleFactory.getTuple(t1, t2));
        }
    }

    private void doSortMergeJoin(Set<Tuple> bound1, Set<Tuple> bound2, Attribute attribute, SortedSet<Tuple> result) {
        long start = System.currentTimeMillis();
        final Set<Tuple> set1 = sortSetOfTuples(bound1, attribute);
        final Set<Tuple> set2 = sortSetOfTuples(bound2, attribute);
        System.err.println("sorting took " + (System.currentTimeMillis() - start) + " = " +
            bound1.size() + " & " + bound2.size());
        if (bound1.size() <= bound2.size()) {
            doProperSortMergeJoin(attribute, result, set1, set2);
        } else {
            doProperSortMergeJoin(attribute, result, set2, set1);
        }
        System.err.println("SMJoin took " + (System.currentTimeMillis() - start));
    }

    private Set<Tuple> sortSetOfTuples(Set<Tuple> tuples, Attribute attribute) {
        tupleAVComparator.setAttribute(attribute);
        List<Tuple> list = new ArrayList<Tuple>(tuples);
        Collections.sort(list, tupleAVComparator);
        final Set<Tuple> treeSet = new LinkedHashSet<Tuple>(list);
        treeSet.addAll(tuples);
        return treeSet;
    }

    private void doNaturalJoin(SortedSet<Attribute> headings, Set<Tuple> tuples1, Set<Tuple> tuples2,
                               SortedSet<Tuple> result) {
        if (tuples1.size() < tuples2.size()) {
            startDoubleLoopProcessing(headings, result, tuples1, tuples2);
        } else {
            startDoubleLoopProcessing(headings, result, tuples2, tuples1);
        }
    }

    private Set<Tuple>[] partitionTuples(Set<Attribute> commonHeadings, Relation rel1, Relation rel2,
                                         Set<Tuple> unboundSet1, Set<Tuple> unboundSet2) {
        Set<Tuple> boundSet1 = new HashSet<Tuple>();
        Set<Tuple> boundSet2 = new HashSet<Tuple>();
        if (commonHeadings.isEmpty()) {
            unboundSet1 = rel1.getTuples();
            unboundSet2 = rel2.getTuples();
            return (Set<Tuple>[]) new Set[] {boundSet1, boundSet2};
        } else {
            for (Attribute attribute : commonHeadings) {
                boundSet1 = partitionWithAttribute(attribute, rel1, boundSet1, unboundSet1);
                boundSet2 = partitionWithAttribute(attribute, rel2, boundSet2, unboundSet2);
            }
            return (Set<Tuple>[]) new Set[]{boundSet1, boundSet2};
        }
    }

    private Set<Tuple> partitionWithAttribute(Attribute attribute, Relation rel,
                                              Set<Tuple> boundSet, Set<Tuple> unboundSet) {
        for (Tuple tuple : rel.getTuples()) {
            if (tuple.getValueOperation(attribute) != null) {
                boundSet.add(tuple);
            } else if (!boundSet.contains(tuple)) {
                unboundSet.add(tuple);
            }
        }
        return boundSet;
    }

    private void doProperSortMergeJoin(Attribute attribute, SortedSet<Tuple> result, Set<Tuple> set1, Set<Tuple> set2) {
        final Iterator<Tuple> iterator1 = set1.iterator();
        final Iterator<Tuple> iterator2 = set2.iterator();
        Tuple tuple1 = advanceIterator(iterator1);
        Tuple tuple2 = advanceIterator(iterator2);
        while (tuple1 != null) {
            while (tuple2 != null) {
                int compare = tupleAVComparator.compare(tuple1, tuple2);
                if (compare > 0) {
                    tuple2 = advanceIterator(iterator2);
                } else if (compare == 0) {
                    addToResult(attribute, result, tuple1, tuple2);
                    tuple2 = advanceIterator(iterator2);
                } else {
                    break;
                }
            }
            tuple1 = advanceIterator(iterator1);
        }
    }

    private void addToResult(Attribute attribute, SortedSet<Tuple> result, Tuple tuple1, Tuple tuple2) {
        boolean contradiction =
            avp1NotNull(attribute, tuple1.getValueOperation(attribute), tuple2.getValueOperation(attribute));
        if (!contradiction) {
            result.add(tupleFactory.getTuple(tuple1, tuple2));
        }
    }

    private Tuple advanceIterator(Iterator<Tuple> iterator) {
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }
}
