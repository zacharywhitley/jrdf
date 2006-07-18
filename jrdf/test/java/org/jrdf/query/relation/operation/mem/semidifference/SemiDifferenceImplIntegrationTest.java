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

package org.jrdf.query.relation.operation.mem.semidifference;

import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.operation.SemiDifference;

/**
 * Tests the integration between semijoin and other classes.
 *
 * @author Andrew Newman
 * @version $Revision: 717 $
 */
public class SemiDifferenceImplIntegrationTest extends TestCase {
    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final SemiDifference MINUS = FACTORY.getNewSemiDifference();

    public void testTruthTableDEEandDUM() {
//        checkMinus(RELATION_DUM, RELATION_DUM, RELATION_DUM);
//        checkMinus(RELATION_DUM, RELATION_DUM, RELATION_DEE);
//        checkMinus(RELATION_DEE, RELATION_DEE, RELATION_DUM);
//        checkMinus(RELATION_DUM, RELATION_DEE, RELATION_DEE);
    }

//    public void testRelationDEEandDumWithRelation() {
//        Relation relation = RelationIntegrationTestUtil.createRelation(RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R1, RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R2));
//        // The semijoin of R1 and DEE is R1.
//        checkMinus(relation, relation, RelationDEE.RELATION_DEE);
//        // The semijoin of DEE and R1 is DEE.
//        checkMinus(RelationDEE.RELATION_DEE, RelationDEE.RELATION_DEE, relation);
//        // The natural join of DUM and R1 is R1.
//        checkMinus(RelationDUM.RELATION_DUM, relation, RelationDUM.RELATION_DUM);
//        checkMinus(RelationDUM.RELATION_DUM, RelationDUM.RELATION_DUM, relation);
//    }
//
//    @SuppressWarnings({ "unchecked" })
//    public void testCartesianProductSemiJoin() {
//        Set<Tuple> tuple1 = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R1, RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R2);
//        Set<Tuple> tuple2 = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.VAR_BAR1_SUBJECT_R3, RelationIntegrationTestUtil.VAR_BAR2_PREDICATE_R4);
//        Set<Tuple> resultTuple = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R1, RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R2);
//        checkMinus(RelationIntegrationTestUtil.createRelation(resultTuple), RelationIntegrationTestUtil.createRelation(tuple1), RelationIntegrationTestUtil.createRelation(tuple2));
//    }
//
//    @SuppressWarnings({ "unchecked" })
//    public void testCartesianProduct2SemiJoin() {
//        Set<Tuple> tuple1 = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R1, RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R2);
//        Set<Tuple> tmpTuple = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R3, RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R4);
//        tuple1.addAll(tmpTuple);
//        Set<Tuple> tuple2 = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R3, RelationIntegrationTestUtil.POS_FOO5_OBJECT_R4);
//        tmpTuple = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R5, RelationIntegrationTestUtil.POS_FOO5_OBJECT_R6);
//        tuple2.addAll(tmpTuple);
//
//        Set<Tuple> resultTuple = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R1, RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R2);
//        tmpTuple = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R3, RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R4);
//        resultTuple.addAll(tmpTuple);
//        checkMinus(RelationIntegrationTestUtil.createRelation(resultTuple), RelationIntegrationTestUtil.createRelation(tuple1), RelationIntegrationTestUtil.createRelation(tuple2));
//    }
//
//    @SuppressWarnings({ "unchecked" })
//    public void testSemiJoinOneEqualColumnOneUnequalColumn() {
//        Set<Tuple> tuple1 = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R1, RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R2);
//        Set<Tuple> tuple2 = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R1, RelationIntegrationTestUtil.VAR_BAR2_PREDICATE_R4);
//        Set<Tuple> resultTuple = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R1, RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R2);
//        checkMinus(RelationIntegrationTestUtil.createRelation(resultTuple), RelationIntegrationTestUtil.createRelation(tuple1), RelationIntegrationTestUtil.createRelation(tuple2));
//    }
//
//    @SuppressWarnings({ "unchecked" })
//    public void testSemiJoinOneEqualColumnTwoUnequalColumns() {
//        Set<Tuple> tuple1 = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R1, RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R2, RelationIntegrationTestUtil.VAR_BAR2_PREDICATE_R4);
//        Set<Tuple> tuple2 = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R1, RelationIntegrationTestUtil.POS_FOO3_OBJECT_R3, RelationIntegrationTestUtil.POS_BAR3_OBJECT_R1);
//        Set<Tuple> resultTuple = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R1, RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R2, RelationIntegrationTestUtil.VAR_BAR2_PREDICATE_R4);
//        checkMinus(RelationIntegrationTestUtil.createRelation(resultTuple), RelationIntegrationTestUtil.createRelation(tuple1), RelationIntegrationTestUtil.createRelation(tuple2));
//    }
//
//    @SuppressWarnings({ "unchecked" })
//    public void testSemiJoinOneEqualColumnMultipleRowsOneResult() {
//        Set<Tuple> tuple1 = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.VAR_BAR1_SUBJECT_R3, RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R2, RelationIntegrationTestUtil.POS_FOO3_OBJECT_R3);
//        Set<Tuple> tuple2 = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.VAR_BAR1_SUBJECT_R3, RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R3, RelationIntegrationTestUtil.POS_FOO5_OBJECT_R4);
//        Set<Tuple> tmpTuple = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.VAR_BAR1_SUBJECT_R3, RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R5, RelationIntegrationTestUtil.POS_FOO5_OBJECT_R6);
//        tuple2.addAll(tmpTuple);
//
//        Set<Tuple> resultTuple = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.VAR_BAR1_SUBJECT_R3, RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R2, RelationIntegrationTestUtil.POS_FOO3_OBJECT_R3);
//
//        Relation relation = RelationIntegrationTestUtil.createRelation(resultTuple);
//        checkMinus(relation, RelationIntegrationTestUtil.createRelation(tuple1), RelationIntegrationTestUtil.createRelation(tuple2));
//    }
//
//    @SuppressWarnings({ "unchecked" })
//    public void testSemiJoinOneEqualColumnMultipleRowsTwoResults() {
//        Set<Tuple> tuple1 = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.VAR_BAR1_SUBJECT_R3, RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R3, RelationIntegrationTestUtil.POS_FOO5_OBJECT_R4);
//        Set<Tuple> tmpTuple = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.VAR_BAR1_SUBJECT_R3, RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R5, RelationIntegrationTestUtil.POS_FOO5_OBJECT_R6);
//        tuple1.addAll(tmpTuple);
//        Set<Tuple> tuple2 = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.VAR_BAR1_SUBJECT_R3, RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R2, RelationIntegrationTestUtil.POS_FOO3_OBJECT_R3);
//
//        Set<Tuple> resultTuple = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.VAR_BAR1_SUBJECT_R3, RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R3, RelationIntegrationTestUtil.POS_FOO5_OBJECT_R4);
//        tmpTuple = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.VAR_BAR1_SUBJECT_R3, RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R5, RelationIntegrationTestUtil.POS_FOO5_OBJECT_R6);
//        resultTuple.addAll(tmpTuple);
//
//        Relation relation = RelationIntegrationTestUtil.createRelation(resultTuple);
//        checkMinus(relation, RelationIntegrationTestUtil.createRelation(tuple1), RelationIntegrationTestUtil.createRelation(tuple2));
//    }
//
//    @SuppressWarnings({ "unchecked" })
//    public void testSemiJoinNoResults() {
//        Set<Tuple> tuple1 = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.VAR_BAR1_SUBJECT_R3, RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R3, RelationIntegrationTestUtil.POS_FOO3_OBJECT_R4);
//        Set<Tuple> tuple2 = RelationIntegrationTestUtil.createASingleTuple(RelationIntegrationTestUtil.VAR_BAR1_SUBJECT_R3, RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R2, RelationIntegrationTestUtil.POS_FOO3_OBJECT_R3);
//        Set<Attribute> heading = RelationIntegrationTestUtil.createHeading(RelationIntegrationTestUtil.VAR_BAR1_SUBJECT, RelationIntegrationTestUtil.POS_FOO4_PREDICATE, RelationIntegrationTestUtil.POS_FOO3_OBJECT);
//        checkMinus(RelationIntegrationTestUtil.createRelation(heading, Collections.EMPTY_SET), RelationIntegrationTestUtil.createRelation(tuple1), RelationIntegrationTestUtil.createRelation(tuple2));
//    }

    private void checkMinus(Relation expectedResult, Relation relation1, Relation relation2) {
        Relation relation = MINUS.minus(relation1, relation2);

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
