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

    public SortMergeJoinImpl(TupleEngine newNaturalJoinEngine, NodeComparator newNodeComparator,
        RelationFactory newRelationFactory, RelationHelper newRelationHelper, TupleFactory newTupleFactory) {
        this.engine = newNaturalJoinEngine;
        this.nodeComparator = newNodeComparator;
        this.relationFactory = newRelationFactory;
        this.relationHelper = newRelationHelper;
        this.tupleFactory = newTupleFactory;
    }

    public void mergeJoin(SortedSet<Attribute> headings, SortedSet<Attribute> commonHeadings,
        final PartitionedRelation relation1, final PartitionedRelation relation2, SortedSet<Tuple> result) {
        if (relation1.getBoundSet().size() <= relation2.getBoundSet().size()) {
            doProperSortMergeJoin(commonHeadings, relation1, relation2, result);
        } else {
            doProperSortMergeJoin(commonHeadings, relation2, relation1, result);
        }
        engine.processRelations(headings, relationFactory.getRelation(relation1.getBoundSet()),
            relationFactory.getRelation(relation2.getUnboundSet()), result);
        engine.processRelations(headings, relationFactory.getRelation(relation2.getBoundSet()),
            relationFactory.getRelation(relation1.getUnboundSet()), result);
        engine.processRelations(headings, relationFactory.getRelation(relation1.getUnboundSet()),
            relationFactory.getRelation(relation2.getUnboundSet()), result);
    }

    private void doProperSortMergeJoin(SortedSet<Attribute> commonHeadings, PartitionedRelation sets1,
        PartitionedRelation sets2, SortedSet<Tuple> result) {
        while (sets1.hasNext() && sets2.hasNext()) {
            if (valuesAreEqual(sets1.curretNode(), sets2.curretNode())) {
                mergeSameValues(sets1.curretNode(), commonHeadings, sets1, sets2, result);
            } else if (nodeComparator.compare(sets1.curretNode(), sets2.curretNode()) > 0) {
                sets2.next();
            } else {
                sets1.next();
            }
        }
    }

    private void mergeSameValues(Node initLhsValue, SortedSet<Attribute> commonHeadings, PartitionedRelation sets1,
        PartitionedRelation sets2, SortedSet<Tuple> result) {
        PartitionedRelation originalRhs = sets2;
        while (sets1.hasNext() && valuesAreEqual(sets1.curretNode(), initLhsValue)) {
            sets2 = originalRhs.copy();
            while (sets2.hasNext() && valuesAreEqual(sets1.curretNode(), sets2.curretNode())) {
                addToResult(commonHeadings, sets1.currentTuple(), sets2.currentTuple(), result);
                sets2.next();
            }
            sets1.next();
        }
        if (originalRhs == sets2) {
            sets2.next();
        }
    }

    private boolean valuesAreEqual(final Node v1, final Node v2) {
        return nodeComparator.compare(v1, v2) == 0;
    }

    private void addToResult(SortedSet<Attribute> commonHeadings, Tuple tuple1, Tuple tuple2, SortedSet<Tuple> result) {
        if (!relationHelper.areIncompatible(commonHeadings, tuple1, tuple2)) {
            result.add(tupleFactory.getTuple(tuple1, tuple2));
        }
    }
}
