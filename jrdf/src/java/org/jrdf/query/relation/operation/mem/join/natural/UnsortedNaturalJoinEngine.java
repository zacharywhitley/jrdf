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
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleFactory;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.mem.AVPOperation;
import static org.jrdf.query.relation.mem.BoundAVPOperation.BOUND;
import static org.jrdf.query.relation.mem.EqAVPOperation.EQUALS;
import org.jrdf.query.relation.mem.RelationHelper;
import org.jrdf.query.relation.operation.mem.join.UnsortedTupleEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: liyf
 * Date: Oct 27, 2008
 * Time: 8:54:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class UnsortedNaturalJoinEngine implements UnsortedTupleEngine {
    private final TupleFactory tupleFactory;
    private final RelationHelper relationHelper;
    private Map<Attribute, ValueOperation> resultantAttributeValues;

    public UnsortedNaturalJoinEngine(TupleFactory newTupleFactory, RelationHelper newRelationHelper) {
        this.tupleFactory = newTupleFactory;
        this.relationHelper = newRelationHelper;
        this.resultantAttributeValues = new HashMap<Attribute, ValueOperation>();
    }

    public Set<Attribute> getHeading(Relation relation1, Relation relation2) {
        return relationHelper.getHeadingUnions(relation1, relation2);
    }

    public void process(Set<Attribute> headings, Set<Tuple> result, Tuple tuple1, Tuple tuple2) {
        resultantAttributeValues = new HashMap<Attribute, ValueOperation>();
        boolean contradiction = false;
        for (Attribute attribute : headings) {
            ValueOperation avp1 = tuple1.getValueOperation(attribute);
            ValueOperation avp2 = tuple2.getValueOperation(attribute);
            contradiction = compareAVPs(attribute, avp1, avp2);
            if (contradiction) {
                break;
            }
        }

        // Only add results if we have found more items to add and there wasn't a contradiction in bound values.
        if (!contradiction && !resultantAttributeValues.isEmpty()) {
            Tuple t = tupleFactory.getTuple(resultantAttributeValues);
            result.add(t);
        }
    }

    private boolean compareAVPs(Attribute attribute, ValueOperation avp1, ValueOperation avp2) {
        boolean result;
        if (avp1 == null && avp2 == null) {
            result = false;
        } else {
            if (avp1 == null) {
                result = processSingleAVP(attribute, avp2);
            } else if (avp2 == null) {
                result = processSingleAVP(attribute, avp1);
            } else {
                result = avp1NotNull(attribute, avp1, avp2);
            }
        }
        return result;
    }

    private boolean processSingleAVP(Attribute attribute, ValueOperation avp) {
        final AVPOperation avpOperation = avp.getOperation();
        if (avpOperation.equals(EQUALS)) {
            resultantAttributeValues.put(attribute, avp);
            return false;
        } else if (avpOperation.equals(BOUND)) {
            resultantAttributeValues.put(attribute, avp);
            return true;
        } else {
            return true;
        }
    }

    private boolean avp1NotNull(Attribute attribute, ValueOperation avp1, ValueOperation avp2) {
        if (avp2 == null) {
            resultantAttributeValues.put(attribute, avp1);
            return false;
        } else {
            if (!avp1.getOperation().equals(EQUALS)) {
                return avp1.getOperation().addAttributeValuePair(attribute, resultantAttributeValues, avp1, avp2);
            } else {
                return avp2.getOperation().addAttributeValuePair(attribute, resultantAttributeValues, avp2, avp1);
            }
        }
    }
}
