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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
        if (commonHeadings.size() == 1) {
            doSortMergeJoin(headings, relation1, relation2, commonHeadings.iterator().next(), result);
        } else {
            doNaturalJoin(headings, relation1.getTuples(), relation2.getTuples(), result);
        }
    }

    private void doNaturalJoin(SortedSet<Attribute> headings, Set<Tuple> tuples1, Set<Tuple> tuples2,
                                    SortedSet<Tuple> result) {
        if (tuples1.size() < tuples2.size()) {
            startDoubleLoopProcessing(headings, result, tuples1, tuples2);
        } else {
            startDoubleLoopProcessing(headings, result, tuples2, tuples1);
        }
    }

    private void doSortMergeJoin(SortedSet<Attribute> headings, Relation relation1, Relation relation2,
                                 Attribute attribute, SortedSet<Tuple> result) {
        SortedSet<Tuple> set1, set2;
        Set<Tuple> unboundTuples1 = new HashSet<Tuple>();
        Set<Tuple> unboundTuples2 = new HashSet<Tuple>();
        tupleAVComparator.setAttribute(attribute);
        set1 = separateTuples(relation1, unboundTuples1, attribute);
        set2 = separateTuples(relation2, unboundTuples2, attribute);
        if (set1.size() <= set2.size()) {
            doProperSortMergeJoin(result, set1, set2);
        } else {
            doProperSortMergeJoin(result, set2, set1);
        }
        doNaturalJoin(headings, unboundTuples1, unboundTuples2, result);
    }

    private SortedSet<Tuple> separateTuples(Relation relation, Set<Tuple> unboundTuples, Attribute attribute) {
        SortedSet<Tuple> boundTupleSet = new TreeSet<Tuple>(tupleAVComparator);
        final Set<Tuple> tuples = relation.getTuples();
        for (Tuple tuple : tuples) {
            if (tuple.getValueOperation(attribute) != null) {
                boundTupleSet.add(tuple);
            } else {
                unboundTuples.add(tuple);
            }
        }
        return boundTupleSet;
    }

    private void doProperSortMergeJoin(SortedSet<Tuple> result, SortedSet<Tuple> set1, SortedSet<Tuple> set2) {
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
                    createMatchingTuple(tuple1, tuple2);
                    result.add(tupleFactory.getTuple(resultantAttributeValues));
                    tuple2 = advanceIterator(iterator2);
                } else {
                    break;
                }
            }
            tuple1 = advanceIterator(iterator1);
        }
    }

    private void createMatchingTuple(Tuple tuple1, Tuple tuple2) {
        resultantAttributeValues = new HashMap<Attribute, ValueOperation>();
        resultantAttributeValues.putAll(tuple1.getAttributeValues());
        resultantAttributeValues.putAll(tuple2.getAttributeValues());
    }

    private Tuple advanceIterator(Iterator<Tuple> iterator) {
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return null;
        }
    }
}
