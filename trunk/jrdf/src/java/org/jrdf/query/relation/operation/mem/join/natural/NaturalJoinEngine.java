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

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.AttributeValuePairComparator;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleFactory;
import org.jrdf.query.relation.mem.RelationHelper;
import org.jrdf.query.relation.operation.mem.join.TupleEngine;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Combines two relations attributes if they have common tuple values.  The
 * same as AND in Algebra A.
 * <p/>
 * The general algorithm is:
 * 1. Find all matching attributes on two relations.
 * 2. Union the attributes and tuples together.
 * 3. Remove any attributes that are not common between the two.
 * <p/>
 */
public class NaturalJoinEngine implements TupleEngine {
    private final TupleFactory tupleFactory;
    private final AttributeValuePairComparator avpComparator;
    private final RelationHelper relationHelper;
    private SortedSet<AttributeValuePair> resultantAttributeValues;

    public NaturalJoinEngine(TupleFactory newTupleFactory, AttributeValuePairComparator newAvpComparator,
        RelationHelper newRelationHelper) {
        this.tupleFactory = newTupleFactory;
        this.avpComparator = newAvpComparator;
        this.relationHelper = newRelationHelper;
        this.resultantAttributeValues = new TreeSet<AttributeValuePair>(newAvpComparator);
    }

    public SortedSet<Attribute> getHeading(Relation relation1, Relation relation2) {
        return relationHelper.getHeadingUnions(relation1, relation2);
    }

    public void process(SortedSet<Attribute> headings, SortedSet<Tuple> result, Tuple tuple1, Tuple tuple2) {
        resultantAttributeValues = new TreeSet<AttributeValuePair>(avpComparator);
        boolean contradiction = false;
        for (Attribute attribute : headings) {
            AttributeValuePair avp1 = tuple1.getAttribute(attribute);
            AttributeValuePair avp2 = tuple2.getAttribute(attribute);
            contradiction = compareAvps(avp1, avp2);

            // If we didn't find one for the current heading end early.
            if (contradiction) {
                break;
            }
        }

        // Only add results if we have found more items to add and there wasn't a contradiction in bound values.
        if (!resultantAttributeValues.isEmpty() && !contradiction) {
            Tuple t = tupleFactory.getTuple(resultantAttributeValues);
            result.add(t);
        }
    }

    private boolean compareAvps(AttributeValuePair avp1, AttributeValuePair avp2) {
        if (avp1 == null) {
            if (avp2 != null) {
                resultantAttributeValues.add(avp2);
            }
            return false;
        } else {
            return avp1NotNull(avp1, avp2);
        }
    }

    private boolean avp1NotNull(AttributeValuePair avp1, AttributeValuePair avp2) {
        if (avp2 == null) {
            resultantAttributeValues.add(avp1);
            return false;
        } else {
            return avp2.addAttributeValuePair(avpComparator, resultantAttributeValues, avp1);
        }
    }
}
