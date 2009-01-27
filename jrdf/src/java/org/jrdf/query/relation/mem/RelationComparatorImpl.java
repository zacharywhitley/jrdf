/*
 * $Header$
 * $Revision$
 * $Date$
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
package org.jrdf.query.relation.mem;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.RelationComparator;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleComparator;

import java.util.Iterator;
import java.util.Set;

public final class RelationComparatorImpl implements RelationComparator {
    private static final long serialVersionUID = 9186461012818411817L;
    private AttributeComparator attributeComparator;
    private TupleComparator tupleComparator;

    private RelationComparatorImpl() {
    }

    public RelationComparatorImpl(AttributeComparator newAttributeComparator, TupleComparator newTupleComparator) {
        this.attributeComparator = newAttributeComparator;
        this.tupleComparator = newTupleComparator;
    }

    public int compare(Relation relation1, Relation relation2) {
        ifNullThrowException(relation1, relation2);
        int result;
        result = compareAttributes(relation1, relation2);
        if (result == 0) {
            result = compareTuples(relation1, relation2);
        }
        return result;
    }

    // TODO Tuple Refactor Duplicate of TupleComparator.
    private int compareAttributes(Relation relation1, Relation relation2) {
        int result = 0;
        Set<Attribute> sortedHeading1 = relation1.getSortedHeading();
        Set<Attribute> sortedHeading2 = relation2.getSortedHeading();
        Iterator<Attribute> iterator1 = sortedHeading1.iterator();
        Iterator<Attribute> iterator2 = sortedHeading2.iterator();
        boolean equal = true;
        while (iterator1.hasNext() && iterator2.hasNext() && equal) {
            Attribute att1 = iterator1.next();
            Attribute att2 = iterator2.next();
            result = attributeComparator.compare(att1, att2);
            equal = result == 0;
        }
        return result;
    }

    private int compareTuples(Relation relation1, Relation relation2) {
        Set<Tuple> sortedTuples1 = relation1.getSortedTuples();
        Set<Tuple> sortedTuples2 = relation2.getSortedTuples();

        int tuplesSize1 = sortedTuples1.size();
        int tuplesSize2 = sortedTuples2.size();
        if (tuplesSize1 == tuplesSize2) {
            return compareTuplesWithSameCardinality(sortedTuples1, sortedTuples2);
        } else {
            if (tuplesSize1 > tuplesSize2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    private int compareTuplesWithSameCardinality(Set<Tuple> sortedTuples1, Set<Tuple> sortedTuples2) {
        int result = 0;
        Iterator<Tuple> iterator1 = sortedTuples1.iterator();
        Iterator<Tuple> iterator2 = sortedTuples2.iterator();
        boolean equal = true;
        while (iterator1.hasNext() && iterator2.hasNext() && equal) {
            Tuple tuple1 = iterator1.next();
            Tuple tuple2 = iterator2.next();
            result = tupleComparator.compare(tuple1, tuple2);
            equal = result == 0;
        }
        return result;
    }


    private void ifNullThrowException(Relation relation1, Relation relation2) {
        if (relation1 == null || relation2 == null) {
            throw new NullPointerException();
        }
    }
}
