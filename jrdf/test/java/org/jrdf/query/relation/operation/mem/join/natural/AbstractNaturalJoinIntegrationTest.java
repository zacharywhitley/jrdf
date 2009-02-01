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

package org.jrdf.query.relation.operation.mem.join.natural;

import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import static org.jrdf.query.relation.constants.RelationDEE.RELATION_DEE;
import static org.jrdf.query.relation.constants.RelationDUM.RELATION_DUM;
import org.jrdf.query.relation.operation.NadicJoin;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_BAR3_OBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_BAR3_OBJECT_R1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO1_SUBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO1_SUBJECT_B1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO1_SUBJECT_B2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO2_PREDICATE;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT_R5;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R5;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO5_OBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO5_OBJECT_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO5_OBJECT_R6;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_OBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_OBJECT_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR2_PREDICATE;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR2_PREDICATE_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createASingleTuple;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createHeading;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createRelation;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createRelations;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createTuple;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Yuan-Fang Li
 * @version : $Id: $
 */
public abstract class AbstractNaturalJoinIntegrationTest extends TestCase {
    protected static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();
    protected NadicJoin nadicJoin;
    protected static final Set<Relation> EMPTY = emptySet();
    protected static final Set<Tuple> EMPTY_SET = emptySet();

    public void testRelationDEEandDUM() {
        // The natural join of empty is DEE.
        checkRelation(RELATION_DEE, EMPTY);
        // The natural join of DEE is DEE.
        checkRelation(RELATION_DEE, singleton(RELATION_DEE));
        // The natural join of DUM is DUM.
        checkRelation(RELATION_DUM, singleton(RELATION_DUM));
    }

    public void testTruthTableDEEandDUM() {
        // The natural joins of DEE and DUM together.
        checkRelation(RELATION_DUM, createRelations(RELATION_DUM, RELATION_DUM));
        checkRelation(RELATION_DUM, createRelations(RELATION_DUM, RELATION_DEE));
        checkRelation(RELATION_DUM, createRelations(RELATION_DEE, RELATION_DUM));
        checkRelation(RELATION_DEE, createRelations(RELATION_DEE, RELATION_DEE));
    }

    public void testRelationDEEandDumWithRelation() {
        Set<Tuple> tuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        Relation relation = createRelation(tuple);
        // The natural process of DEE and R1 is R1.
        checkRelation(relation, createRelations(relation, RELATION_DEE));
        checkRelation(relation, createRelations(RELATION_DEE, relation));
        // The natural process of DUM and R1 is DUM.
        checkRelation(RELATION_DUM, createRelations(relation, RELATION_DUM));
        checkRelation(RELATION_DUM, createRelations(RELATION_DUM, relation));
    }

    public void testCartesianProduct() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, VAR_BAR2_PREDICATE_R4);
        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2, VAR_BAR1_SUBJECT_R3,
            VAR_BAR2_PREDICATE_R4);
        checkJoin(createRelation(resultTuple), createRelation(tuple1, tuple2));
    }

    public void testCartesianProduct2() {
        Set<Tuple> tmpTuple;

        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        tmpTuple = createASingleTuple(POS_FOO1_SUBJECT_R3, POS_FOO2_PREDICATE_R4);
        tuple1.addAll(tmpTuple);

        Set<Tuple> tuple2 = createASingleTuple(POS_FOO4_PREDICATE_R3, POS_FOO5_OBJECT_R4);
        tmpTuple = createASingleTuple(POS_FOO4_PREDICATE_R5, POS_FOO5_OBJECT_R6);
        tuple2.addAll(tmpTuple);

        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2, POS_FOO4_PREDICATE_R3,
            POS_FOO5_OBJECT_R4);
        tmpTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2, POS_FOO4_PREDICATE_R5,
            POS_FOO5_OBJECT_R6);
        resultTuple.addAll(tmpTuple);
        tmpTuple = createASingleTuple(POS_FOO1_SUBJECT_R3, POS_FOO2_PREDICATE_R4, POS_FOO4_PREDICATE_R3,
            POS_FOO5_OBJECT_R4);
        resultTuple.addAll(tmpTuple);
        tmpTuple = createASingleTuple(POS_FOO1_SUBJECT_R3, POS_FOO2_PREDICATE_R4, POS_FOO4_PREDICATE_R5,
            POS_FOO5_OBJECT_R6);
        resultTuple.addAll(tmpTuple);

        checkJoin(createRelation(resultTuple), createRelation(tuple1, tuple2));
    }

    public void testNaturalJoin() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1, VAR_BAR2_PREDICATE_R4);
        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2, VAR_BAR2_PREDICATE_R4);
        checkJoin(createRelation(resultTuple), createRelation(tuple1, tuple2));
    }

    public void testJoinLhsLargerThanRhs() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1);
        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        checkJoin(createRelation(resultTuple), createRelation(tuple1, tuple2));
    }

    public void testJoinRhsLargerThanLhs() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1);
        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        checkJoin(createRelation(resultTuple), createRelation(tuple1, tuple2));
    }

    public void testJoinRhsMoreTuplesThanLhs() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1);
        Set<Tuple> tmpTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        tuple1.addAll(tmpTuple);
        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1);
        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        tmpTuple = createASingleTuple(POS_FOO1_SUBJECT_R1);
        resultTuple.addAll(tmpTuple);
        checkJoin(createRelation(resultTuple), createRelation(tuple1, tuple2));
    }

    public void testJoinLhsMoreTuplesThanRhs() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1);
        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        Set<Tuple> tmpTuple = createASingleTuple(POS_FOO1_SUBJECT_R1);
        tuple2.addAll(tmpTuple);
        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        tmpTuple = createASingleTuple(POS_FOO1_SUBJECT_R1);
        resultTuple.addAll(tmpTuple);
        checkJoin(createRelation(resultTuple), createRelation(tuple1, tuple2));
    }

    public void testNaturalJoin2() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1, VAR_BAR2_PREDICATE_R4);
        Set<Tuple> tuple3 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO3_OBJECT_R3);
        Set<Tuple> tuple4 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_BAR3_OBJECT_R1);
        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2, VAR_BAR2_PREDICATE_R4,
            POS_FOO3_OBJECT_R3, POS_BAR3_OBJECT_R1);
        checkJoin(createRelation(resultTuple), createRelation(tuple1, tuple2, tuple3, tuple4));
    }

    public void testMultiJoin() {
        final Tuple tp1 = createTuple(POS_FOO1_SUBJECT_B1, POS_FOO2_PREDICATE_R2);
        final Tuple tp2 = createTuple(POS_FOO1_SUBJECT_R1, VAR_BAR2_PREDICATE_R4);
        final Tuple tp3 = createTuple(POS_FOO1_SUBJECT_R1, VAR_BAR1_OBJECT_R4);
        final Tuple tp4 = createTuple(POS_FOO1_SUBJECT_R3, VAR_BAR1_OBJECT_R4);
        Relation rel1 = createRelation(new HashSet<Tuple>(asList(tp1, tp2, tp3, tp4)));
        final Tuple tq1 = createTuple(POS_FOO1_SUBJECT_B2, POS_FOO3_OBJECT_R3);
        final Tuple tq2 = createTuple(POS_FOO1_SUBJECT_R1, POS_BAR3_OBJECT_R1);
        final Tuple tq3 = createTuple(POS_FOO1_SUBJECT_R1, POS_FOO5_OBJECT_R4);
        final Tuple tq4 = createTuple(POS_FOO1_SUBJECT_R4, POS_FOO5_OBJECT_R4);
        Relation rel2 = createRelation(new HashSet<Tuple>(asList(tq1, tq2, tq3, tq4)));
        final Tuple tr1 = createTuple(POS_FOO1_SUBJECT_R1, VAR_BAR2_PREDICATE_R4, POS_BAR3_OBJECT_R1);
        final Tuple tr2 = createTuple(POS_FOO1_SUBJECT_R1, VAR_BAR2_PREDICATE_R4, POS_FOO5_OBJECT_R4);
        final Tuple tr3 = createTuple(POS_FOO1_SUBJECT_R1, VAR_BAR1_OBJECT_R4, POS_BAR3_OBJECT_R1);
        final Tuple tr4 = createTuple(POS_FOO1_SUBJECT_R1, VAR_BAR1_OBJECT_R4, POS_FOO5_OBJECT_R4);
        Set<Tuple> resultTuple = new HashSet<Tuple>(asList(tr1, tr2, tr3, tr4));
        Set<Attribute> headings = createHeading(POS_FOO1_SUBJECT, POS_FOO2_PREDICATE, VAR_BAR2_PREDICATE ,
            POS_FOO3_OBJECT, POS_BAR3_OBJECT, POS_FOO5_OBJECT, VAR_BAR1_OBJECT);
        List<Relation> rels = asList(rel1, rel2);
        checkJoin(createRelation(headings, resultTuple), rels);
    }

    public void testNaturalJoin3() {
        Set<Tuple> tuple1 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO2_PREDICATE_R2, POS_FOO3_OBJECT_R3);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO5_OBJECT_R4);
        Set<Tuple> tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R5, POS_FOO5_OBJECT_R6);
        tuple2.addAll(tmpTuple);

        Set<Tuple> resultTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO2_PREDICATE_R2, POS_FOO3_OBJECT_R3,
            POS_FOO4_PREDICATE_R3, POS_FOO5_OBJECT_R4);
        tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO2_PREDICATE_R2, POS_FOO3_OBJECT_R3,
            POS_FOO4_PREDICATE_R5, POS_FOO5_OBJECT_R6);
        resultTuple.addAll(tmpTuple);

        Relation relation = createRelation(resultTuple);
        checkJoin(relation, createRelation(tuple1, tuple2));
    }

    public void testNaturalJoin4() {
        Set<Tuple> tuple1 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO5_OBJECT_R4);
        Set<Tuple> tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R5, POS_FOO5_OBJECT_R6);
        tuple1.addAll(tmpTuple);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO2_PREDICATE_R2, POS_FOO3_OBJECT_R3);

        Set<Tuple> resultTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO2_PREDICATE_R2, POS_FOO3_OBJECT_R3,
            POS_FOO4_PREDICATE_R3, POS_FOO5_OBJECT_R4);
        tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO2_PREDICATE_R2, POS_FOO3_OBJECT_R3,
            POS_FOO4_PREDICATE_R5, POS_FOO5_OBJECT_R6);
        resultTuple.addAll(tmpTuple);

        checkJoin(createRelation(resultTuple), createRelation(tuple1, tuple2));
    }

    public void testNaturalJoinNoResults() {
        Set<Tuple> tuple1 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R2, POS_FOO3_OBJECT_R3);
        Set<Tuple> tuple3 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R2, POS_FOO3_OBJECT_R5);
        Set<Attribute> heading = createHeading(VAR_BAR1_SUBJECT, POS_FOO4_PREDICATE, POS_FOO3_OBJECT);
        tuple2.addAll(tuple3);
        checkJoin(createRelation(heading, EMPTY_SET), createRelation(tuple1, tuple2));
    }

    private void checkJoin(Relation expectedResult, List<Relation> relations) {
        Set<Relation> tuples = createRelations(relations.toArray(new Relation[relations.size()]));
        checkRelation(expectedResult, tuples);
    }

    private void checkRelation(Relation expected, Set<Relation> actual) {
        Relation relation = nadicJoin.join(actual);

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
        assertEquals(expected, relation);
    }
}
