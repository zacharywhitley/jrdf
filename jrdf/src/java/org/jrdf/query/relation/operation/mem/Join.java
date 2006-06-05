/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2005 The JRDF Project.  All rights reserved.
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

import org.jrdf.JRDFFactory;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.constants.RelationDEE;
import org.jrdf.query.relation.mem.RelationImpl;
import org.jrdf.query.relation.mem.TupleImpl;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * A simple memory based implementation of Join.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public final class Join implements org.jrdf.query.relation.operation.Join {

    /**
     * Singleton version of Join.
     */
    public static final Join JOIN = new Join();

    /**
     * Cannot create join.
     */
    private Join() {
    }

    public Relation join(Set<Relation> relations) {
        // Is it the empty set - if so return DEE.
        if (relations.equals(Collections.<Relation>emptySet())) {
            return RelationDEE.RELATION_DEE;
        }

        // Is it just one relations - if so just return it back.
        if (relations.size() == 1) {
            return relations.iterator().next();
        }

        // Do a proper join.
        Relation result = null;

        // Get the common attributes that are being joined
        Set<Attribute> intersectionHeading = getHeadingIntersections(relations);
        Set<Tuple> resultTuples = new TreeSet<Tuple>();

        // No common attributes
        if (intersectionHeading.size() == 0) {
            // Perform cartesian product
            resultTuples = performCartesianProduct(relations);
        }

        // Perform join using common headings.

        // Union all of the headings together.
        Set<Attribute> resultHeading = getHeadingUnions(relations);

        return new RelationImpl(resultHeading, resultTuples);
    }

    private Set<Tuple> performCartesianProduct(Set<Relation> relations) {
        Iterator<Relation> iterator = relations.iterator();
        Relation relation1 = iterator.next();
        Relation relation2 = iterator.next();
        Set<Tuple> tuples = joinTuples(relation1.getTuples(), relation2.getTuples());
        while (iterator.hasNext()) {
            tuples = joinTuples(tuples, iterator.next().getTuples());
        }
        return tuples;
    }

    private Set<Tuple> joinTuples(Set<Tuple> tuples1, Set<Tuple> tuples2) {
        Set<Tuple> result = new TreeSet<Tuple>();
        for (Tuple tuple1 : tuples1) {
            Set<AttributeValuePair> resultantAttributeValues = tuple1.getAttributeValues();
            for (Tuple tuple2 : tuples2) {
                resultantAttributeValues.addAll(tuple2.getAttributeValues());
            }
            result.add(new TupleImpl(resultantAttributeValues));
        }
        return result;
    }

    private Set<Attribute> getHeadingUnions(Set<Relation> relations) {
        Set<Attribute> headings = new TreeSet<Attribute>(JRDFFactory.getNewAttributeComparator());
        for (Relation relation : relations) {
            Set<Attribute> heading = relation.getHeading();
            headings.addAll(heading);
        }
        return headings;
    }

    private Set<Attribute> getHeadingIntersections(Set<Relation> relations) {
        Set<Attribute> headings = new TreeSet<Attribute>(JRDFFactory.getNewAttributeComparator());
        Iterator<Relation> iterator = relations.iterator();
        Relation firstRelation = iterator.next();
        headings.addAll(firstRelation.getHeading());
        while (iterator.hasNext()) {
            Relation relation = iterator.next();
            Set<Attribute> heading = relation.getHeading();
            headings.retainAll(heading);
        }
        return headings;
    }
}
