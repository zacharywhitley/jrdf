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
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * @author Yuan-Fang Li
 * @version :$
 */

public class NewSortMergeNaturalJoinEngine extends NaturalJoinEngine implements TupleEngine {
    private static final Set<Attribute> EMPTY_ATTRIBUTE_SET = Collections.emptySet();

    private TupleComparator tupleAVComparator;

    public NewSortMergeNaturalJoinEngine(TupleFactory newTupleFactory, RelationHelper newRelationHelper,
                                         NodeComparator nodeComparator) {
        super(newTupleFactory, newRelationHelper);
        this.resultantAttributeValues = new HashMap<Attribute, ValueOperation>();
        this.tupleAVComparator = new TupleAttributeValueComparatorImpl(nodeComparator);
    }

    public void processRelations(SortedSet<Attribute> headings, Relation relation1, Relation relation2,
                                 SortedSet<Tuple> result) {
        SortedSet<Attribute> commonHeadings = getHeadingsIntersection(relation1, relation2);
        System.err.println("total tuple # = " + (relation1.getTuples().size() + relation2.getTuples().size()));
        System.err.println("SMJOin headings: " + commonHeadings);
        if (commonHeadings.size() == 1) {
            // do sort merge join
            doSortMergeJoin(headings, relation1, relation2, commonHeadings.iterator().next(),
                EMPTY_ATTRIBUTE_SET, result);
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
        long start = System.currentTimeMillis();
        System.err.println("multi join start: " + rel1.getTuples().size() + " and " + rel2.getTuples().size());
        Attribute attr = chooseACommonHeading(headings, rel1, rel2);
        commonHeadings.remove(attr);
        doSortMergeJoin(headings, rel1, rel2, attr, commonHeadings, result);
        System.err.println("Multi join took " + (System.currentTimeMillis() - start));
    }

    private Attribute chooseACommonHeading(Set<Attribute> headings, Relation rel1, Relation rel2) {
        int max = 0;
        int pos = 0;
        final Attribute[] list = headings.toArray(new Attribute[headings.size()]);
        for (int i = 0; i < headings.size(); i++) {
            Attribute attr = list[i];
            int totalSize = rel1.getTuples(attr).size() + rel2.getTuples(attr).size();
            if (max < totalSize) {
                max = totalSize;
                pos = i;
            }
        }
        return list[pos];
    }

    private void doSortMergeJoin(SortedSet<Attribute> headings, Relation rel1, Relation rel2,
                                 Attribute attribute, Set<Attribute> remainingHeadings, SortedSet<Tuple> result) {
        long start = System.currentTimeMillis();
        final Set<Tuple>[] sets1 = partitionWithAttribute(attribute, rel1);
        final Set<Tuple>[] sets2 = partitionWithAttribute(attribute, rel2);
        Set<Tuple> bound1 = sets1[0];
        Set<Tuple> bound2 = sets2[0];
        Set<Tuple> unbound1 = sets1[1];
        Set<Tuple> unbound2 = sets2[1];
        System.err.println("New SM join start: " + bound1.size() + " and " + bound2.size());
        final List<Tuple> list1 = sortSetOfTuples(bound1, attribute);
        final List<Tuple> list2 = sortSetOfTuples(bound2, attribute);
        System.err.println("sorting took " + (System.currentTimeMillis() - start) + " = " +
            list1.size() + " & " + list2.size());
        if (list1.size() <= list2.size()) {
            doProperSortMergeJoin(attribute, remainingHeadings, result, list1, list2);
        } else {
            doProperSortMergeJoin(attribute, remainingHeadings, result, list2, list1);
        }
        doNaturalJoin(headings, bound1, unbound2, result);
        doNaturalJoin(headings, bound2, unbound1, result);
        doNaturalJoin(headings, unbound1, unbound2, result);
        System.err.println("SMJoin took " + (System.currentTimeMillis() - start));
    }

    private List<Tuple> sortSetOfTuples(Set<Tuple> tuples, Attribute attribute) {
        tupleAVComparator.setAttribute(attribute);
        List<Tuple> list = new ArrayList<Tuple>(tuples);
        Collections.sort(list, tupleAVComparator);
        return list;
    }

    private void doNaturalJoin(SortedSet<Attribute> headings, Set<Tuple> tuples1, Set<Tuple> tuples2,
                               SortedSet<Tuple> result) {
        long start = System.currentTimeMillis();
        System.err.println("natural join start: " + tuples1.size() + " and " + tuples2.size());
        if (tuples1.size() < tuples2.size()) {
            startDoubleLoopProcessing(headings, result, tuples1, tuples2);
        } else {
            startDoubleLoopProcessing(headings, result, tuples2, tuples1);
        }
        System.err.println("natural join took " + (System.currentTimeMillis() - start));
    }

    private Set<Tuple>[] partitionWithAttribute(Attribute attribute, Relation rel) {
        Set<Tuple> boundSet = new HashSet<Tuple>();
        Set<Tuple> unboundSet = new HashSet<Tuple>();
        for (Tuple tuple : rel.getTuples()) {
            if (tuple.getValueOperation(attribute) != null) {
                boundSet.add(tuple);
            } else if (!boundSet.contains(tuple)) {
                unboundSet.add(tuple);
            }
        }
        return (Set<Tuple> []) new Set[] {boundSet, unboundSet};
    }

    private void doProperSortMergeJoin(Attribute attribute, Set<Attribute> commonHeadings, SortedSet<Tuple> result,
                                       List<Tuple> list1, List<Tuple> list2) {
        if (list1.size() == 0 || list2.size() == 0) {
            return;
        }
        long start = System.currentTimeMillis();
        tupleAVComparator.setAttribute(attribute);
        Tuple tuple1, tuple2;
        int i = 0;
        int j = 0;
        tuple1 = list1.get(i);
        tuple2 = list2.get(j);
        while (tuple1 != null && tuple2 != null) {
            int compare = tupleAVComparator.compare(tuple1, tuple2);
            if (compare == 0) {
                int[] newInds = processSameTuples(list1, list2, attribute, result, commonHeadings, i, j, tuple1);
                i = newInds[0];
                j = newInds[1];
                tuple1 = getTupleFromList(list1, newInds[0]);
                tuple2 = getTupleFromList(list2, newInds[1]);
            } else if (compare > 0) {
                tuple2 = getTupleFromList(list2, ++j);
            } else {
                tuple1 = getTupleFromList(list1, ++i);
            }
        }
        System.err.println("actual sort merge join took " + (System.currentTimeMillis() - start) +
            " = " + result.size());
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
        return new int[] {newPos1, newPos2};
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
