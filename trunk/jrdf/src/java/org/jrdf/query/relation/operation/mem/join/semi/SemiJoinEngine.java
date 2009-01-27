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

package org.jrdf.query.relation.operation.mem.join.semi;

import org.jrdf.graph.NodeComparator;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleFactory;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.constants.NullaryNode;
import org.jrdf.query.relation.operation.mem.join.TupleEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

/**
 *  Join two relations if they have common tuple values and projects the results back onto the first relation.
 */
public class SemiJoinEngine implements TupleEngine {
    private final TupleFactory tupleFactory;
    private final NodeComparator nodeComparator;

    public SemiJoinEngine(TupleFactory newTupleFactory, NodeComparator newNodeComparator) {
        this.tupleFactory = newTupleFactory;
        this.nodeComparator = newNodeComparator;
    }

    public SortedSet<Attribute> getHeading(Relation relation1, Relation relation2) {
        return relation1.getSortedHeading();
    }

    public SortedSet<Attribute> getHeadingsIntersection(Relation relation1, Relation relation2) {
        throw new UnsupportedOperationException();
    }

    private void process(SortedSet<Attribute> headings, SortedSet<Tuple> result, Tuple tuple1, Tuple tuple2) {
        Map<Attribute, ValueOperation> allAttributeValuePairs = new HashMap<Attribute, ValueOperation>();
        Map<Attribute, ValueOperation> lhsAttributeValuePairs = new HashMap<Attribute, ValueOperation>();
        boolean contradiction = false;
        for (Attribute attribute : headings) {
            ValueOperation avp1 = tuple1.getValueOperation(attribute);
            ValueOperation avp2 = tuple2.getValueOperation(attribute);
            contradiction = addAttributeValuePair(attribute, avp1, avp2, allAttributeValuePairs,
                lhsAttributeValuePairs);

            // If we didn't find one for the current heading end early.
            if (contradiction) {
                break;
            }
        }

        // Only add results if they are the same size
        if ((allAttributeValuePairs.size() > 0 && !contradiction)) {
            Tuple t = tupleFactory.getTuple(lhsAttributeValuePairs);
            result.add(t);
        }
    }

    private boolean addAttributeValuePair(Attribute attribute, ValueOperation avp1, ValueOperation avp2,
        Map<Attribute, ValueOperation> resultantAttributeValues,
        Map<Attribute, ValueOperation> lhsAttributeValuePairs) {

        // Add if avp1 is not null and avp2 is or they are both equal.
        if (avp1 != null) {
            return avp1NotNull(attribute, avp2, avp1, resultantAttributeValues, lhsAttributeValuePairs);
        } else {
            // Add if avp1 is null and avp2 is not.
            return avp1Null(attribute, avp2, resultantAttributeValues, lhsAttributeValuePairs);
        }
    }

    private boolean avp1NotNull(Attribute attribute, ValueOperation avp2, ValueOperation avp1,
        Map<Attribute, ValueOperation> resultantAttributeValues,
        Map<Attribute, ValueOperation> lhsAttributeValuePairs) {
        if (avp2 == null) {
            addResults(attribute, avp1, resultantAttributeValues, lhsAttributeValuePairs);
            return false;
        } else if (nodeComparator.compare(avp1.getValue(), avp2.getValue()) == 0) {
            addNonNullaryAvp(attribute, avp1, avp2, resultantAttributeValues, lhsAttributeValuePairs);
            return false;
        } else {
            return true;
        }
    }

    private boolean avp1Null(Attribute attribute, ValueOperation avp2,
        Map<Attribute, ValueOperation> resultantAttributeValues,
        Map<Attribute, ValueOperation> lhsAttributeValuePairs) {
        if (avp2 != null) {
            addResults(attribute, avp2, resultantAttributeValues, lhsAttributeValuePairs);
        }
        return false;
    }

    private void addNonNullaryAvp(Attribute attribute, ValueOperation avp1, ValueOperation avp2,
        Map<Attribute, ValueOperation> resultantAttributeValues,
        Map<Attribute, ValueOperation> lhsAttributeValuePairs) {
        if (!(avp1.getValue() instanceof NullaryNode)) {
            addResults(attribute, avp1, resultantAttributeValues, lhsAttributeValuePairs);
        } else {
            addResults(attribute, avp2, resultantAttributeValues, lhsAttributeValuePairs);
        }
    }

    private void addResults(Attribute attribute, ValueOperation avp,
        Map<Attribute, ValueOperation> resultantAttributeValues,
        Map<Attribute, ValueOperation> lhsAttributeValuePairs) {
        resultantAttributeValues.put(attribute, avp);
        lhsAttributeValuePairs.put(attribute, avp);
    }

    public void processRelations(SortedSet<Attribute> headings, Relation relation1, Relation relation2,
                                 SortedSet<Tuple> result) {
        for (Tuple tuple1 : relation1.getTuples()) {
            for (Tuple tuple2 : relation2.getTuples()) {
                process(headings, result, tuple1, tuple2);
            }
        }
    }
}