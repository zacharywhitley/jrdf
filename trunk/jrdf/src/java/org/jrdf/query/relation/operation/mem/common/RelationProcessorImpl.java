/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

package org.jrdf.query.relation.operation.mem.common;

import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.EvaluatedRelation;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleComparator;
import static org.jrdf.query.relation.constants.RelationDEE.RELATION_DEE;
import static org.jrdf.query.relation.constants.RelationDUM.RELATION_DUM;
import org.jrdf.query.relation.operation.mem.join.TupleEngine;
import static org.jrdf.util.param.ParameterUtil.checkNotNull;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Common tuple processing code - gets headings and then processes tuples using the tuple engine.
 */
public final class RelationProcessorImpl implements RelationProcessor {
    private final RelationFactory relationFactory;
    private final TupleComparator tupleComparator;

    public RelationProcessorImpl(RelationFactory newRelationFactory, TupleComparator newTupleComparator) {
        checkNotNull(newRelationFactory, newTupleComparator);
        this.relationFactory = newRelationFactory;
        this.tupleComparator = newTupleComparator;
    }

    public EvaluatedRelation processRelations(Set<EvaluatedRelation> relations, TupleEngine tupleEngine) {
        Iterator<EvaluatedRelation> iterator = relations.iterator();
        EvaluatedRelation relation1 = iterator.next();
        EvaluatedRelation relation2 = iterator.next();
        EvaluatedRelation resultRelation = processRelationPairs(tupleEngine, relation1, relation2);
        while (iterator.hasNext()) {
            EvaluatedRelation nextRelation = iterator.next();
            resultRelation = processRelationPairs(tupleEngine, resultRelation, nextRelation);
        }

        return convertToConstants(resultRelation);
    }

    private EvaluatedRelation processRelationPairs(TupleEngine tupleEngine, EvaluatedRelation relation1,
        EvaluatedRelation relation2) {
        SortedSet<Attribute> headings = tupleEngine.getHeading(relation1, relation2);
        SortedSet<Tuple> result = new TreeSet<Tuple>(tupleComparator);
        tupleEngine.processRelations(headings, relation1, relation2, result);
        return relationFactory.getRelation(headings, result);
    }

    public EvaluatedRelation convertToConstants(EvaluatedRelation resultRelation) {
        if (resultRelation.getHeading().isEmpty()) {
            if (resultRelation.isEmpty()) {
                return RELATION_DUM;
            } else {
                return RELATION_DEE;
            }
        }
        return resultRelation;
    }
}
