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

import org.jrdf.graph.NodeComparator;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.EvaluatedRelation;
import org.jrdf.query.relation.Tuple;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

public class MultiSortMergeJoinImpl implements MultiSortMergeJoin {
    private SortMergeJoin sortMergeJoin;
    private NodeComparator nodeComparator;

    public MultiSortMergeJoinImpl(SortMergeJoin newSortMergeJoin, NodeComparator newNodeComparator) {
        this.sortMergeJoin = newSortMergeJoin;
        this.nodeComparator = newNodeComparator;
    }

    public void mergeJoin(SortedSet<Attribute> headings, SortedSet<Attribute> commonHeadings,
        EvaluatedRelation relation1, EvaluatedRelation relation2, SortedSet<Tuple> result) {
        Attribute attr = chooseACommonHeading(headings, relation1, relation2);
        commonHeadings.remove(attr);
        final PartitionedRelation partRelation1 = new PartitionedRelationImpl(nodeComparator, attr, relation1);
        final PartitionedRelation partRelation2 = new PartitionedRelationImpl(nodeComparator, attr, relation2);
        sortMergeJoin.mergeJoin(headings, commonHeadings, partRelation1, partRelation2, result);
    }

    private Attribute chooseACommonHeading(Set<Attribute> headings, EvaluatedRelation rel1, EvaluatedRelation rel2) {
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

    private long estimateJoinCost(Attribute attribute, EvaluatedRelation rel1, EvaluatedRelation rel2) {
        long b1, b2, ub1, ub2;
        long size1 = rel1.getTupleSize();
        long size2 = rel2.getTupleSize();
        b1 = getNumberOfBoundAttributes(attribute, rel1);
        b2 = getNumberOfBoundAttributes(attribute, rel2);
        ub1 = size1 - b1;
        ub2 = size2 - b2;
        return b1 + b2 + (b1 * ub2) + (b2 * ub1) + (ub1 * ub2);
    }

    private long getNumberOfBoundAttributes(Attribute attribute, EvaluatedRelation relation) {
        long size = 0;
        for (Tuple tuple : relation) {
            if (tuple.getValue(attribute) != null) {
                size++;
            }
        }
        return size;
    }
}
