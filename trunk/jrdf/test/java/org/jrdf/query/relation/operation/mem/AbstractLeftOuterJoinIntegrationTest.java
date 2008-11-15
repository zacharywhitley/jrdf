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

package org.jrdf.query.relation.operation.mem;

import junit.framework.TestCase;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.operation.DyadicJoin;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R5;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_PREDICATE_R1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_PREDICATE_R2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECTPREDICATE_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createASingleTuple;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createRelation;

import java.util.Set;

/**
 * The common set of tests that all left outer join implementations must pass.
 *
 * @author Andrew Newman
 * @version $Revision: 729 $
 */
public abstract class AbstractLeftOuterJoinIntegrationTest extends TestCase {

    public void testLeftOuterJoin1() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, VAR_BAR1_PREDICATE_R2, POS_FOO3_OBJECT_R3);
        tuple1.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, VAR_BAR1_PREDICATE_R1));

        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO4_PREDICATE_R2);
        tuple2.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, POS_FOO4_PREDICATE_R3));

        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, VAR_BAR1_PREDICATE_R2, POS_FOO3_OBJECT_R3,
            POS_FOO4_PREDICATE_R2);
        resultTuple.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, VAR_BAR1_PREDICATE_R1, POS_FOO4_PREDICATE_R3));

        checkJoin(createRelation(resultTuple), createRelation(tuple1), createRelation(tuple2));
    }

    public void testLeftOuterJoin2() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO4_PREDICATE_R2);
        tuple1.addAll(createASingleTuple(POS_FOO1_SUBJECT_R3, POS_FOO4_PREDICATE_R3));
        tuple1.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, POS_FOO4_PREDICATE_R5));

        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO4_PREDICATE_R2, POS_FOO3_OBJECT_R3,
            VAR_BAR1_SUBJECTPREDICATE_R3);
        tuple2.addAll(createASingleTuple(POS_FOO1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4));
        tuple2.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, POS_FOO4_PREDICATE_R2));

        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO4_PREDICATE_R2, POS_FOO3_OBJECT_R3,
            VAR_BAR1_SUBJECTPREDICATE_R3);
        resultTuple.addAll(createASingleTuple(POS_FOO1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4));
        resultTuple.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, POS_FOO4_PREDICATE_R5));

        checkJoin(createRelation(resultTuple), createRelation(tuple1), createRelation(tuple2));
    }

    public void testLeftOuterJoin3() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO4_PREDICATE_R2);
        tuple1.addAll(createASingleTuple(POS_FOO1_SUBJECT_R3, POS_FOO4_PREDICATE_R3));
        tuple1.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, POS_FOO4_PREDICATE_R2));

        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO4_PREDICATE_R2, POS_FOO3_OBJECT_R3,
            VAR_BAR1_SUBJECTPREDICATE_R3);
        tuple2.addAll(createASingleTuple(POS_FOO1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4));
        tuple2.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, POS_FOO4_PREDICATE_R5));

        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO4_PREDICATE_R2, POS_FOO3_OBJECT_R3,
            VAR_BAR1_SUBJECTPREDICATE_R3);
        resultTuple.addAll(createASingleTuple(POS_FOO1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4));
        resultTuple.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, POS_FOO4_PREDICATE_R2));

        checkJoin(createRelation(resultTuple), createRelation(tuple1), createRelation(tuple2));
    }

    public abstract DyadicJoin getJoin();

    private void checkJoin(Relation expectedResult, Relation relation1, Relation relation2) {
        Relation relation = getJoin().join(relation1, relation2);

//        Set<Tuple> sortedTuples = relation.getSortedTuples();
//        Set<Tuple> sortedTuples2 = expected.getSortedTuples();
//        System.err.println("Sorted Actual tuples relation: " + relation.getSortedTuples());
//        System.err.println("Sorted Expected tuples relation: " + expected.getSortedTuples());
//        System.err.println("-------------------------------");
//        boolean isEqual = sortedTuples.equals(sortedTuples2);
//        System.err.println("Sorted Expected tuples relation1: " + isEqual);
//        System.err.println("Sorted Expected tuples relation2: " + expected.getSortedTuples().equals(
// relation.getSortedTuples()));
//        System.err.println("Sorted Expected tuples relation3: " + relation.getSortedTuples().equals(
// expected.getSortedTuples()));
        assertEquals(expectedResult, relation);
    }
}
