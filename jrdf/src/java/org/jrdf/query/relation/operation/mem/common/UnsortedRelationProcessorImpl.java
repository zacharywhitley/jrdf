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

package org.jrdf.query.relation.operation.mem.common;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleComparator;
import static org.jrdf.query.relation.constants.RelationDUM.RELATION_DUM;
import static org.jrdf.query.relation.constants.RelationDEE.RELATION_DEE;
import org.jrdf.query.relation.operation.mem.join.TupleEngine;
import org.jrdf.query.relation.operation.mem.join.UnsortedTupleEngine;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: liyf
 * Date: Oct 27, 2008
 * Time: 8:49:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class UnsortedRelationProcessorImpl implements RelationProcessor {
    private RelationFactory relationFactory;
    private TupleComparator tupleComparator;

    public UnsortedRelationProcessorImpl(RelationFactory relationFactory, TupleComparator tupleComparator) {
        checkNotNull(relationFactory, tupleComparator);
        this.relationFactory = relationFactory;
        this.tupleComparator = tupleComparator;
    }

    public Relation processRelations(Set<Relation> relations, UnsortedTupleEngine tupleEngine) {
        Iterator<Relation> iterator = relations.iterator();
        Relation relation1 = iterator.next();
        Relation relation2 = iterator.next();
        Set<Attribute> headings = tupleEngine.getHeading(relation1, relation2);
        Set<Tuple> tuples = processTuples(headings, relation1.getTuples(), relation2.getTuples(), tupleEngine);
        Relation resultRelation = relationFactory.getRelation(headings, tuples);
        while (iterator.hasNext()) {
            Relation nextRelation = iterator.next();
            headings = tupleEngine.getHeading(resultRelation, nextRelation);
            tuples = processTuples(headings, tuples, nextRelation.getTuples(), tupleEngine);
            resultRelation = relationFactory.getRelation(headings, tuples);
        }

        return convertToConstants(resultRelation);
    }

    private Set<Tuple> processTuples(Set<Attribute> headings, Set<Tuple> tuples1,
        Set<Tuple> tuples2, UnsortedTupleEngine tupleEngine) {
        Set<Tuple> result = new HashSet<Tuple>();
        for (Tuple tuple1 : tuples1) {
            for (Tuple tuple2 : tuples2) {
                tupleEngine.process(headings, result, tuple1, tuple2);
            }
        }
        return result;
    }

    public Relation processRelations(Set<Relation> relations, TupleEngine tupleEngine) {
        throw new UnsupportedOperationException();
    }

    public Relation convertToConstants(Relation resultRelation) {
        if (resultRelation.getHeading().isEmpty()) {
            if (resultRelation.getTuples().isEmpty()) {
                return RELATION_DUM;
            } else {
                return RELATION_DEE;
            }
        }
        return resultRelation;
    }
}
