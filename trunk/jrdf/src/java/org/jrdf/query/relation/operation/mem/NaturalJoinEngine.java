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

package org.jrdf.query.relation.operation.mem;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleFactory;

import java.util.Set;
import java.util.HashSet;

/**
 * Class description goes here.
 */
public class NaturalJoinEngine implements JoinEngine {
    private final TupleFactory tupleFactory;

    public NaturalJoinEngine(TupleFactory tupleFactory) {
        this.tupleFactory = tupleFactory;
    }

    public void join(Set<Attribute> headings, Set<AttributeValuePair> avps1, Set<AttributeValuePair> avps2,
            Set<Tuple> result) {
        Set<AttributeValuePair> resultantAttributeValues = new HashSet<AttributeValuePair>();
        for (Attribute attribute : headings) {
            AttributeValuePair avp1 = getAttribute(avps1, attribute);
            AttributeValuePair avp2 = getAttribute(avps2, attribute);

            boolean added = addAttributeValuePair(avp1, avp2, resultantAttributeValues);

            // If we didn't find one for the current heading end early.
            if (!added) {
                break;
            }
        }

        // Only add results if they are the same size
        if (headings.size() == resultantAttributeValues.size()) {
            Tuple t = tupleFactory.getTuple(resultantAttributeValues);
            result.add(t);
        }
    }

    private AttributeValuePair getAttribute(Set<AttributeValuePair> actualAvps, Attribute expectedAttribute) {
        for (AttributeValuePair avp : actualAvps) {
            if (avp.getAttribute().equals(expectedAttribute)) {
                return avp;
            }
        }
        return null;
    }

    private boolean addAttributeValuePair(AttributeValuePair avp1, AttributeValuePair avp2,
            Set<AttributeValuePair> resultantAttributeValues) {
        boolean added = false;

        // Add if avp1 is not null and avp2 is or they are both equal.
        if (avp1 != null) {
            if (avp2 == null || avp1.equals(avp2)) {
                resultantAttributeValues.add(avp1);
                added = true;
            }
        } else {
            // Add if avp1 is null and avp2 is not.
            if (avp2 != null) {
                resultantAttributeValues.add(avp2);
                added = true;
            }
        }
        return added;
    }
}
