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

package org.jrdf.query.relation.operation.mem.union;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleFactory;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.mem.RelationHelper;
import org.jrdf.query.relation.operation.mem.join.TupleEngine;

import java.util.Map;
import java.util.SortedSet;

public class SubsumptionEngine implements TupleEngine {
    private final TupleFactory tupleFactory;
    private final RelationHelper relationHelper;

    public SubsumptionEngine(TupleFactory tupleFactory, RelationHelper relationHelper) {
        this.tupleFactory = tupleFactory;
        this.relationHelper = relationHelper;
    }

    public SortedSet<Attribute> getHeading(Relation relation1, Relation relation2) {
        return relationHelper.getHeadingUnions(relation1, relation2);
    }

    public SortedSet<Attribute> getHeadingsIntersection(Relation relation1, Relation relation2) {
        return relationHelper.getHeadingIntersections(relation1, relation2);
    }

    /**
     * Returns the tuples to be subsumed in the result set.
     *
     * @param headings the headings of the resultant tuple.
     * @param result   the tuples to be subsumed.
     * @param tuple1
     * @param tuple2
     */
    public void process(SortedSet<Attribute> headings, SortedSet<Tuple> result, Tuple tuple1, Tuple tuple2) {
        Map<Attribute, ValueOperation> avps1 = tuple1.getAttributeValues();
        Map<Attribute, ValueOperation> avps2 = tuple2.getAttributeValues();
        int subsumes = subsumes(headings, avps1, avps2);
        if (tuple2SubsumesTuple1(subsumes)) {
            result.add(tupleFactory.getTuple(avps1));
        } else if (tuple1SubsumesTuple2(subsumes)) {
            result.add(tupleFactory.getTuple(avps2));
        }
    }

    /**
     * -1 indicates 2 subsumes 1, 1 indicates 1 subsumes 2, 0 means that they are not subsumed.
     *
     * @param headings the headings to use.
     * @param avps1    The set of avps to compare.
     * @param avps2    The set of avps to compare.
     * @return -1 indicates avps2 subsumes avps1, 1 indicates avps1 subsumes avps2 and 0 means they do not share any
     *         common values or are equal.
     */
    // TODO Tuple Refactor.
    public int subsumes(SortedSet<Attribute> headings, Map<Attribute, ValueOperation> avps1,
        Map<Attribute, ValueOperation> avps2) {

        // Don't subsume if all the values are collection.
        int noHeadings = headings.size();
        if (avps1.size() == noHeadings && avps2.size() == noHeadings) {
            return 0;
        }

        // Compare avps and look for an equal avp.
        boolean found = false;
        for (Attribute attribute : avps1.keySet()) {
            if (avps2.keySet().contains(attribute) && avps1.get(attribute).equals(avps2.get(attribute))) {
                found = true;
                break;
            }
        }

        // If found a matching avp then check if not subsumed
        if (found) {
            return areSubsumedBy(avps1, avps2);
        } else {
            return 0;
        }
    }

    /**
     * Compare the sizes of the avps1 and avps2 if not equal one is subsumed.
     *
     * @param avps1 The set of avps to compare.
     * @param avps2 The set of avps to compare.
     * @return -1 indicates avps2 subsumes avps1, 1 indicates avps1 subsumes avps2 and 0 means they do not share any
     *         common values or are equal.
     */
    private int areSubsumedBy(Map<Attribute, ValueOperation> avps1, Map<Attribute, ValueOperation> avps2) {
        if (avps1.size() > avps2.size() && onlyContainsAttributesValues(avps1, avps2)) {
            return 1;
        } else if (avps2.size() > avps1.size() && onlyContainsAttributesValues(avps2, avps1)) {
            return -1;
        }
        return 0;
    }

    private boolean onlyContainsAttributesValues(Map<Attribute, ValueOperation> avps1,
        Map<Attribute, ValueOperation> avps2) {
        boolean onlyContainsValues = false;
        for (Attribute attribute : avps2.keySet()) {
            onlyContainsValues = avps1.keySet().contains(attribute) &&
                avps1.get(attribute).equals(avps2.get(attribute));
            if (!onlyContainsValues) {
                break;
            }
        }
        return onlyContainsValues;
    }

    private boolean tuple1SubsumesTuple2(int subsumes) {
        return subsumes == 1;
    }

    private boolean tuple2SubsumesTuple1(int subsumes) {
        return subsumes == -1;
    }
}
