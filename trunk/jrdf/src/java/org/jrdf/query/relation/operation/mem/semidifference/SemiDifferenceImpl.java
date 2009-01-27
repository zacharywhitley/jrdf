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

package org.jrdf.query.relation.operation.mem.semidifference;

import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleComparator;
import static org.jrdf.query.relation.constants.RelationDEE.RELATION_DEE;
import static org.jrdf.query.relation.constants.RelationDUM.RELATION_DUM;
import org.jrdf.query.relation.operation.SemiDifference;
import org.jrdf.query.relation.operation.mem.common.RelationProcessor;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class SemiDifferenceImpl implements SemiDifference {
    private final RelationFactory relationFactory;
    private TupleComparator tupleComparator;
    private RelationProcessor relationProcessor;

    public SemiDifferenceImpl(RelationProcessor relationProcessor, RelationFactory relationFactory,
        TupleComparator tupleComparator) {
        this.relationFactory = relationFactory;
        this.tupleComparator = tupleComparator;
        this.relationProcessor = relationProcessor;
    }

    public Relation minus(Relation relation1, Relation relation2) {
        Relation result = deeOrDumOperations(relation1, relation2);
        if (result != null) {
            return result;
        }
        SortedSet<Tuple> resultTuples = new TreeSet<Tuple>(tupleComparator);
        performMinus(relation1, relation2, resultTuples);
        result = relationFactory.getRelation(resultTuples);
        return relationProcessor.convertToConstants(result);
    }

    private Relation deeOrDumOperations(Relation relation1, Relation relation2) {
        Relation result = null;
        // DUM - Anything is DUM.
        if (relation1 == RELATION_DUM) {
            result = RELATION_DUM;
            // DEE - DEE is DUM, otherwise it's DEE
        } else if (relation1 == RELATION_DEE) {
            result = relationDeeShortcuts(relation2);
            // Anything - DUM is DUM
        } else if (relation2 == RELATION_DUM) {
            result = relation1;
            // Anything - DEE is just Anything's heading
        } else if (relation2 == RELATION_DEE) {
            Set<Tuple> noTuples = Collections.emptySet();
            result = relationFactory.getRelation(relation1.getSortedHeading(), noTuples);
        }
        return result;
    }

    private Relation relationDeeShortcuts(Relation relation2) {
        if (relation2 == RELATION_DEE) {
            return RELATION_DUM;
        } else {
            return RELATION_DEE;
        }
    }

    private void performMinus(Relation relation1, Relation relation2, SortedSet<Tuple> resultTuples) {
        SortedSet<Tuple> set1 = relation1.getSortedTuples();
        SortedSet<Tuple> set2 = relation2.getSortedTuples();
        for (Tuple tuple1 : set1) {
            if (!set2.contains(tuple1)) {
                resultTuples.add(tuple1);
            }
        }
    }
}
