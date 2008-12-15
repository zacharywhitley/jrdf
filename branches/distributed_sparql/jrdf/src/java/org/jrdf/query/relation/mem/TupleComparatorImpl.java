/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.query.relation.mem;

import org.jrdf.graph.Node;
import org.jrdf.graph.NodeComparator;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleComparator;
import org.jrdf.query.relation.ValueOperation;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Iterator;

public final class TupleComparatorImpl implements TupleComparator {
    private static final long serialVersionUID = 7276502975947499595L;
    private NodeComparator nodeComparator;
    private AttributeComparator attributeComparator;

    private TupleComparatorImpl() {
    }

    public TupleComparatorImpl(NodeComparator newNodeComparator, AttributeComparator newAttributeComparator) {
        this.nodeComparator = newNodeComparator;
        this.attributeComparator = newAttributeComparator;
    }

    public int compare(Tuple o1, Tuple o2) {
        ifNullThrowException(o1, o2);
        Map<Attribute, ValueOperation> attributeValues1 = o1.getAttributeValues();
        Map<Attribute, ValueOperation> attributeValues2 = o2.getAttributeValues();
        int result = compareSize(attributeValues1, attributeValues2);
        if (result == 0) {
            result = compareAttributeValues(attributeValues1,  attributeValues2);
        }

        return result;
    }

    public void setAttribute(Attribute attribute) {
        throw new UnsupportedOperationException();
    }

    private void ifNullThrowException(Tuple tuple1, Tuple tuple2) {
        if (tuple1 == null || tuple2 == null) {
            throw new NullPointerException();
        }
    }

    private int compareSize(Map<Attribute, ValueOperation> attributeValues1,
        Map<Attribute, ValueOperation> attributeValues2) {
        int result = 0;
        if (attributeValues1.size() > attributeValues2.size()) {
            result = 1;
        } else if (attributeValues1.size() < attributeValues2.size()) {
            result = -1;
        }
        return result;
    }

    // TODO YF doesn't take into consideration different AVPOperations
    private int compareAttributeValues(Map<Attribute, ValueOperation> attributeValues1,
        Map<Attribute, ValueOperation> attributeValues2) {
        int result = 0;
        for (Attribute attribute : attributeValues1.keySet()) {
            if (attributeValues2.keySet().contains(attribute)) {
                final Node value1 = attributeValues1.get(attribute).getValue();
                final Node value2 = attributeValues2.get(attribute).getValue();
                result = nodeComparator.compare(value1, value2);
                if (result != 0) {
                    break;
                }
            } else {
                result = compareAttributes(attributeValues1.keySet(), attributeValues2.keySet());
                break;
            }
        }
        return result;
    }

    // TODO Tuple Refactor Duplicate of RelationComparator.
    private int compareAttributes(Set<Attribute> attributes1, Set<Attribute> attributes2) {
        SortedSet<Attribute> sorted1 = new TreeSet<Attribute>(attributeComparator);
        sorted1.addAll(attributes1);
        SortedSet<Attribute> sorted2 = new TreeSet<Attribute>(attributeComparator);
        sorted2.addAll(attributes2);
        Iterator<Attribute> iterator1 = sorted1.iterator();
        Iterator<Attribute> iterator2 = sorted2.iterator();
        int result = 0;
        boolean equal = true;
        while (iterator1.hasNext() && iterator2.hasNext() && equal) {
            Attribute att1 = iterator1.next();
            Attribute att2 = iterator2.next();
            result = attributeComparator.compare(att1, att2);
            equal = result == 0;
        }
        return result;
    }
}
