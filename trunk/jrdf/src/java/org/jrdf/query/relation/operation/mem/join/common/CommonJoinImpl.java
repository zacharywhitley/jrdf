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

package org.jrdf.query.relation.operation.mem.join.common;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleComparator;
import org.jrdf.query.relation.operation.mem.join.JoinEngine;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Common join code - gets headings and then join tuples using the tuple engine.
 */
public class CommonJoinImpl implements CommonJoin {
    private final RelationFactory relationFactory;
    private final AttributeComparator attributeComparator;
    private final TupleComparator tupleComparator;

    public CommonJoinImpl(RelationFactory relationFactory, AttributeComparator attributeComparator,
            TupleComparator tupleComparator) {
        this.relationFactory = relationFactory;
        this.attributeComparator = attributeComparator;
        this.tupleComparator = tupleComparator;
    }

    public Relation performJoin(Set<Relation> relations, JoinEngine joinEngine) {
        Iterator<Relation> iterator = relations.iterator();
        Relation relation1 = iterator.next();
        Relation relation2 = iterator.next();
        Set<Attribute> headings = getHeadingUnions(relation1, relation2);
        Set<Tuple> tuples = joinTuples(headings, relation1.getTuples(), relation2.getTuples(), joinEngine);
        Relation resultRelation = relationFactory.getRelation(tuples);

        while (iterator.hasNext()) {
            Relation nextRelation = iterator.next();
            headings = getHeadingUnions(resultRelation, nextRelation);
            tuples = joinTuples(headings, tuples, nextRelation.getTuples(), joinEngine);
            resultRelation = relationFactory.getRelation(tuples);
        }
        return resultRelation;
    }

    private Set<Attribute> getHeadingUnions(Relation... relations) {
        Set<Attribute> headings = new TreeSet<Attribute>(attributeComparator);

        for (Relation relation : relations) {
            Set<Attribute> heading = relation.getHeading();
            headings.addAll(heading);
        }

        return headings;
    }

    private Set<Tuple> joinTuples(Set<Attribute> headings, Set<Tuple> tuples1, Set<Tuple> tuples2,
            JoinEngine joinEngine) {
        Set<Tuple> result = new TreeSet<Tuple>(tupleComparator);
        for (Tuple tuple1 : tuples1) {
            for (Tuple tuple2 : tuples2) {
                joinRhs(headings, tuple1, tuple2, result, joinEngine);
            }
        }
        return result;
    }

    private void joinRhs(Set<Attribute> headings, Tuple tuple1, Tuple tuple2, Set<Tuple> result,
            JoinEngine joinEngine) {
        Set<AttributeValuePair> avps1 = tuple1.getSortedAttributeValues();
        Set<AttributeValuePair> avps2 = tuple2.getSortedAttributeValues();
        joinEngine.join(headings, avps1, avps2, result);
    }
}
