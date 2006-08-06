/*
 * $Header$
 * $Revision: 439 $
 * $Date: 2006-01-27 06:19:29 +1000 (Fri, 27 Jan 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2006 The JRDF Project.  All rights reserved.
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

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.AttributeValuePairComparator;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleFactory;
import org.jrdf.query.relation.constants.NullaryAttributeValuePair;
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
 * 2. OuterUnionImpl the attributes and tuples together.
 * 3. Remove any attributes that are not common between the two.
 * <p/>
 * For example:
 * Relation 1 has the following statements: <1, a, foo>, <1, b, bar>,
 * <1, c, bar>, <1, c, baz>
 * Relation 2 has the following statements: <2, b, foo>, <2, c, bar>,
 * <2, f, bar>, <2, g, bar>, <2, c, baz>, <2, f, baz>
 * <p/>
 * After process:
 * <2, b, bar>, <2, c, bar>, <1, c, bar>, <1, f, bar>, <1, f, bar>, <2, c, baz>,
 * <1, c, baz>, <1, f, baz>
 * Removed:
 * <2, a, foo>, <1, a, foo>

 */
public class NaturalJoinEngine implements TupleEngine {
    private final TupleFactory tupleFactory;
    private final AttributeValuePairComparator avpComparator;
    private final RelationHelper relationHelper;

    public NaturalJoinEngine(TupleFactory tupleFactory, AttributeValuePairComparator avpComparator,
            RelationHelper relationHelper) {
        this.tupleFactory = tupleFactory;
        this.avpComparator = avpComparator;
        this.relationHelper = relationHelper;
    }

    public SortedSet<Attribute> getHeading(Relation relation1, Relation relation2) {
        return relationHelper.getHeadingUnions(relation1, relation2);
    }

    public void process(SortedSet<Attribute> headings, SortedSet<AttributeValuePair> avps1,
            SortedSet<AttributeValuePair> avps2, SortedSet<Tuple> result) {
        SortedSet<AttributeValuePair> resultantAttributeValues = new TreeSet<AttributeValuePair>(avpComparator);
        for (Attribute attribute : headings) {
            AttributeValuePair avp1 = getAttribute(avps1, attribute);
            AttributeValuePair avp2 = getAttribute(avps2, attribute);
            boolean added = addAttributeValuePair(avp1, avp2, resultantAttributeValues);

            // If we didn't find one for the current heading end early.
            if (!added) {
                break;
            }
        }

        // TODO (AN) Change this back to matching the headings and failing on both values being null when it goes
        // to outerunion and subsumption.


        // Only add results if we have found more items to add.
//        if (resultantAttributeValues.size() == headings.size()) {
        if (resultantAttributeValues.size() > 0) {
            Tuple t = tupleFactory.getTuple(resultantAttributeValues);
            result.add(t);
        }
    }

    private AttributeValuePair getAttribute(SortedSet<AttributeValuePair> actualAvps, Attribute expectedAttribute) {
        for (AttributeValuePair avp : actualAvps) {
            if (avp.getAttribute().equals(expectedAttribute)) {
                return avp;
            }
        }
        return null;
    }

    private boolean addAttributeValuePair(AttributeValuePair avp1, AttributeValuePair avp2,
            SortedSet<AttributeValuePair> resultantAttributeValues) {
        // Add if avp1 is not null and avp2 is or they are both equal.
        if (avp1 != null) {
            return avp1NotNull(avp2, avp1, resultantAttributeValues);
        } else {
            // Add if avp1 is null and avp2 is not.
            return avp1Null(avp2, resultantAttributeValues);
        }
    }

    private boolean avp1NotNull(AttributeValuePair avp2, AttributeValuePair avp1,
            SortedSet<AttributeValuePair> resultantAttributeValues) {
        if (avp2 == null) {
            addResults(avp1, resultantAttributeValues);
            return true;
        } else if (avpComparator.compare(avp1, avp2) == 0) {
            addNonNullaryAvp(avp1, avp2, resultantAttributeValues);
            return true;
        } else {
            return false;
        }
    }

    private boolean avp1Null(AttributeValuePair avp2, SortedSet<AttributeValuePair> resultantAttributeValues) {
        if (avp2 != null) {
            addResults(avp2, resultantAttributeValues);
            return true;
        } else {
            // TODO (AN) Test when both are null - added.
            //return false;
            return true;
        }
    }

    private void addNonNullaryAvp(AttributeValuePair avp1, AttributeValuePair avp2,
            SortedSet<AttributeValuePair> resultantAttributeValues) {
        if (!(avp1 instanceof NullaryAttributeValuePair)) {
            addResults(avp1, resultantAttributeValues);
        } else {
            addResults(avp2, resultantAttributeValues);
        }
    }

    private void addResults(AttributeValuePair avp, SortedSet<AttributeValuePair> resultantAttributeValues) {
        resultantAttributeValues.add(avp);
    }
}
