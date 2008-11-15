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

package org.jrdf.query.relation.operation.mem.join.semi;

import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import static org.jrdf.query.relation.constants.RelationDEE.RELATION_DEE;
import static org.jrdf.query.relation.constants.RelationDUM.RELATION_DUM;
import org.jrdf.query.relation.operation.DyadicJoin;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_BAR3_OBJECT_R1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R5;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO5_OBJECT_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO5_OBJECT_R6;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_PREDICATE_R1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_PREDICATE_R2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR2_PREDICATE_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createASingleTuple;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createHeading;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createRelation;

import static java.util.Collections.EMPTY_SET;
import java.util.Set;

/**
 * Tests the integration between semijoin and other classes.
 *
 * @author Andrew Newman
 * @version $Revision: 717 $
 */
@SuppressWarnings({ "unchecked" })
public class SemiJoinImplIntegrationTest extends TestCase {
    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final DyadicJoin JOIN = FACTORY.getNewSemiJoin();

    public void testTruthTableDEEandDUM() {
        checkJoin(RELATION_DUM, RELATION_DUM, RELATION_DUM);
        checkJoin(RELATION_DUM, RELATION_DUM, RELATION_DEE);
        checkJoin(RELATION_DUM, RELATION_DEE, RELATION_DUM);
        checkJoin(RELATION_DEE, RELATION_DEE, RELATION_DEE);
    }

    public void testRelationDEEandDumWithRelation() {
        Relation relation = createRelation(createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2));
        // The semijoin of R1 and DEE is R1.
        checkJoin(relation, relation, RELATION_DEE);
        // The semijoin of DEE and R1 is DEE.
        checkJoin(RELATION_DEE, RELATION_DEE, relation);
        // The natural process of DUM and R1 is R1.
        checkJoin(RELATION_DUM, relation, RELATION_DUM);
        checkJoin(RELATION_DUM, RELATION_DUM, relation);
    }

    public void testCartesianProductSemiJoin() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, VAR_BAR2_PREDICATE_R4);
        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        checkJoin(createRelation(resultTuple), createRelation(tuple1), createRelation(tuple2));
    }

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

    public void testSemiJoinOneEqualColumnOneUnequalColumn() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1, VAR_BAR2_PREDICATE_R4);
        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        checkJoin(createRelation(resultTuple), createRelation(tuple1), createRelation(tuple2));
    }

    public void testSemiJoinRhsLargerThanLhs() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1);
        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1);
        checkJoin(createRelation(resultTuple), createRelation(tuple1), createRelation(tuple2));
    }

    public void testSemiJoinLhsLargerThanRhs() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        Set<Tuple> tmpTuple = createASingleTuple(POS_FOO1_SUBJECT_R1);
        tuple1.addAll(tmpTuple);
        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1);
        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        tmpTuple = createASingleTuple(POS_FOO1_SUBJECT_R1);
        resultTuple.addAll(tmpTuple);
        checkJoin(createRelation(resultTuple), createRelation(tuple1), createRelation(tuple2));
    }

    public void testSemiJoinOneEqualColumnTwoUnequalColumns() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2, VAR_BAR2_PREDICATE_R4);
        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO3_OBJECT_R3, POS_BAR3_OBJECT_R1);
        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2, VAR_BAR2_PREDICATE_R4);
        checkJoin(createRelation(resultTuple), createRelation(tuple1), createRelation(tuple2));
    }

    public void testSemiJoinOneEqualColumnMultipleRowsOneResult() {
        Set<Tuple> tuple1 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO2_PREDICATE_R2, POS_FOO3_OBJECT_R3);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO5_OBJECT_R4);
        Set<Tuple> tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R5, POS_FOO5_OBJECT_R6);
        tuple2.addAll(tmpTuple);

        Set<Tuple> resultTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO2_PREDICATE_R2, POS_FOO3_OBJECT_R3);

        Relation relation = createRelation(resultTuple);
        checkJoin(relation, createRelation(tuple1), createRelation(tuple2));
    }

    public void testSemiJoinOneEqualColumnMultipleRowsTwoResults() {
        Set<Tuple> tuple1 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO5_OBJECT_R4);
        Set<Tuple> tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R5, POS_FOO5_OBJECT_R6);
        tuple1.addAll(tmpTuple);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO2_PREDICATE_R2, POS_FOO3_OBJECT_R3);

        Set<Tuple> resultTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO5_OBJECT_R4);
        tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R5, POS_FOO5_OBJECT_R6);
        resultTuple.addAll(tmpTuple);

        Relation relation = createRelation(resultTuple);
        checkJoin(relation, createRelation(tuple1), createRelation(tuple2));
    }

    public void testSemiJoinNoResults() {
        Set<Tuple> tuple1 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R2, POS_FOO3_OBJECT_R3);
        Set<Attribute> heading = createHeading(VAR_BAR1_SUBJECT, POS_FOO4_PREDICATE, POS_FOO3_OBJECT);
        checkJoin(createRelation(heading, EMPTY_SET), createRelation(tuple1), createRelation(tuple2));
    }

    public void testSemiJoinUnmatchedRelations() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, VAR_BAR1_PREDICATE_R2, POS_FOO3_OBJECT_R3);
        tuple1.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, VAR_BAR1_PREDICATE_R1));

        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO4_PREDICATE_R2);
        tuple2.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, POS_FOO4_PREDICATE_R3));

        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, VAR_BAR1_PREDICATE_R2, POS_FOO3_OBJECT_R3);
        resultTuple.addAll(createASingleTuple(POS_FOO1_SUBJECT_R4, VAR_BAR1_PREDICATE_R1));

        checkJoin(createRelation(resultTuple), createRelation(tuple1), createRelation(tuple2));
    }

    private void checkJoin(Relation expectedResult, Relation relation1, Relation relation2) {
        Relation relation = JOIN.join(relation1, relation2);

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
