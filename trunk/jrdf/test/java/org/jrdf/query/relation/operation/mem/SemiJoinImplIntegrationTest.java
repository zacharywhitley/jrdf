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

import junit.framework.TestCase;
import org.jrdf.JRDFFactory;
import org.jrdf.TestJRDFFactory;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.operation.SemiJoin;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R5;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO5_OBJECT_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO5_OBJECT_R6;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR2_PREDICATE_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createASingleTuple;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createRelation;

import java.util.Set;

/**
 * Tests the integration between join and other classes such as RelationDEE, RelationDUM and other relations.
 *
 * @author Andrew Newman
 * @version $Revision: 717 $
 */
public class SemiJoinImplIntegrationTest extends TestCase {
    private static final JRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final SemiJoin JOIN = FACTORY.getNewSemiJoin();

    @SuppressWarnings({ "unchecked" })
    public void testCartesianProductSemiJoin() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, VAR_BAR2_PREDICATE_R4);
        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        checkJoin(createRelation(resultTuple), createRelation(tuple1), createRelation(tuple2));
    }

    @SuppressWarnings({ "unchecked" })
    public void testCartesianProduct2SemiJoin() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        Set<Tuple> tmpTuple = createASingleTuple(POS_FOO1_SUBJECT_R3, POS_FOO2_PREDICATE_R4);
        tuple1.addAll(tmpTuple);
        Set<Tuple> tuple2 = createASingleTuple(POS_FOO4_PREDICATE_R3, POS_FOO5_OBJECT_R4);
        tmpTuple = createASingleTuple(POS_FOO4_PREDICATE_R5, POS_FOO5_OBJECT_R6);
        tuple2.addAll(tmpTuple);

        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        tmpTuple = createASingleTuple(POS_FOO1_SUBJECT_R3, POS_FOO2_PREDICATE_R4);
        resultTuple.addAll(tmpTuple);
        checkJoin(createRelation(resultTuple), createRelation(tuple1), createRelation(tuple2));
    }

    @SuppressWarnings({ "unchecked" })
    public void testSemiJoin() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1, VAR_BAR2_PREDICATE_R4);
        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        checkJoin(createRelation(resultTuple), createRelation(tuple1), createRelation(tuple2));
    }

//    @SuppressWarnings({ "unchecked" })
//    public void testSemiJoin2() {
//        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
//        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1, VAR_BAR2_PREDICATE_R4);
//        Set<Tuple> tuple3 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO3_OBJECT_R3);
//        Set<Tuple> tuple4 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_BAR3_OBJECT_R1);
//        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
//        checkJoin(createRelation(resultTuple), createRelation(tuple1, tuple2, tuple3, tuple4));
//    }

    @SuppressWarnings({ "unchecked" })
    public void testSemiJoin3() {
        Set<Tuple> tuple1 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO2_PREDICATE_R2, POS_FOO3_OBJECT_R3);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO5_OBJECT_R4);
        Set<Tuple> tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R5, POS_FOO5_OBJECT_R6);
        tuple2.addAll(tmpTuple);

        Set<Tuple> resultTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO2_PREDICATE_R2, POS_FOO3_OBJECT_R3);

        Relation relation = createRelation(resultTuple);
        checkJoin(relation, createRelation(tuple1), createRelation(tuple2));
    }

//    @SuppressWarnings({ "unchecked" })
//    public void testSemiJoin4() {
//        Set<Tuple> tuple1 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO5_OBJECT_R4);
//        Set<Tuple> tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R5, POS_FOO5_OBJECT_R6);
//        tuple1.addAll(tmpTuple);
//        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO2_PREDICATE_R2, POS_FOO3_OBJECT_R3);
//
//        Set<Tuple> resultTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO5_OBJECT_R4);
//        tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R5, POS_FOO5_OBJECT_R6);
//        resultTuple.addAll(tmpTuple);
//
//        Relation relation = createRelation(resultTuple);
//        checkJoin(relation, createRelation(tuple1, tuple2));
//    }

//    @SuppressWarnings({ "unchecked" })
//    public void testSemiJoin5() {
//        Set<Tuple> tuple1 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4);
//        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R2, POS_FOO3_OBJECT_R3);
//        checkJoin(createRelation(tuple1, tuple2), RelationDEE.RELATION_DEE);
//    }

    private void checkJoin(Relation expectedResult, Relation relation1, Relation relation2) {
        Relation relation = SemiJoinImplIntegrationTest.JOIN.semiJoin(relation1, relation2);

//        Set<Tuple> sortedTuples = relation.getSortedTuples();
//        Set<Tuple> sortedTuples2 = expected.getSortedTuples();
//        System.err.println("Sorted Actual tuples relation: " + relation.getSortedTuples());
//        System.err.println("Sorted Expected tuples relation: " + expected.getSortedTuples());
//        System.err.println("-------------------------------");
//        boolean isEqual = sortedTuples.equals(sortedTuples2);
//        System.err.println("Sorted Expected tuples relation1: " + isEqual);
//        System.err.println("Sorted Expected tuples relation2: " + expected.getSortedTuples().equals(relation.getSortedTuples()));
//        System.err.println("Sorted Expected tuples relation3: " + relation.getSortedTuples().equals(expected.getSortedTuples()));
        assertEquals(expectedResult, relation);
    }

}
