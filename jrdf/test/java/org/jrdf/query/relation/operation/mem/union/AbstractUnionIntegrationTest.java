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

package org.jrdf.query.relation.operation.mem.union;

import junit.framework.TestCase;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import static org.jrdf.query.relation.constants.RelationDEE.RELATION_DEE;
import static org.jrdf.query.relation.constants.RelationDUM.RELATION_DUM;
import org.jrdf.query.relation.operation.Union;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_BAR3_OBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_BAR3_OBJECT_R1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO1_SUBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO1_SUBJECT_R1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO2_PREDICATE;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_OBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_OBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR2_PREDICATE;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR2_PREDICATE_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createASingleTuple;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createHeading;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createRelation;

import java.util.Collections;
import java.util.Set;

/**
 * Tests the integration between union and other classes such as RelationDEE, RelationDUM and other relations.  Does
 * not test subsumption.
 *
 * @author Andrew Newman
 * @version $Revision: 729 $
 */
public abstract class AbstractUnionIntegrationTest extends TestCase {
    private static final Set<Tuple> EMPTY_SET = Collections.emptySet();

    public void testTruthTableDEEandDUM() {
        // The union of DEE and DUM together (basically the truth table for OR).
        checkUnion(RELATION_DUM, RELATION_DUM, RELATION_DUM);
        checkUnion(RELATION_DEE, RELATION_DUM, RELATION_DEE);
        checkUnion(RELATION_DEE, RELATION_DEE, RELATION_DUM);
        checkUnion(RELATION_DEE, RELATION_DEE, RELATION_DEE);
    }

    public void testRelationDEEandDumWithRelation() {
        Relation relation = createRelation(createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2));
        // The union of DEE and R1 is DEE (everything matches the 0-tuple).
        checkUnion(RELATION_DEE, relation, RELATION_DEE);
        checkUnion(RELATION_DEE, RELATION_DEE, relation);
        // The union of DUM and R1 is R1.
        checkUnion(relation, relation, RELATION_DUM);
        checkUnion(relation, RELATION_DUM, relation);
    }

    public void testWithNoRowsRelation() {
        Relation relation = createRelation(createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2));
        Set<Attribute> heading = createHeading(POS_FOO1_SUBJECT, POS_FOO2_PREDICATE);
        Relation emptyRelation = createRelation(heading, EMPTY_SET);
        checkUnion(relation, relation, emptyRelation);
        checkUnion(relation, emptyRelation, relation);
    }

    public void testUnionCompatibleSameTuple() {
        Set<Tuple> tuple1 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4);
        Set<Tuple> resultTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4);

        checkUnion(createRelation(resultTuple), createRelation(tuple1), createRelation(tuple2));
    }

    public void testUnionCompatibleDifferenceTuples() {
        Set<Tuple> tuple1 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R2, POS_FOO3_OBJECT_R3);

        Set<Tuple> resultTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4);
        Set<Tuple> tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R2, POS_FOO3_OBJECT_R3);
        resultTuple.addAll(tmpTuple);

        checkUnion(createRelation(resultTuple), createRelation(tuple1), createRelation(tuple2));

    }

    public void testUnionCompatibleDifferentTuplesButWithSameRows() {
        Set<Tuple> tuple1 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R2, POS_FOO3_OBJECT_R3);
        Set<Tuple> tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4);
        tuple2.addAll(tmpTuple);

        Set<Tuple> resultTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4);
        tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R2, POS_FOO3_OBJECT_R3);
        resultTuple.addAll(tmpTuple);

        checkUnion(createRelation(resultTuple), createRelation(tuple1), createRelation(tuple2));
    }

    public void testNonUnionCompatibleOneSharedAttribute() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2, VAR_BAR2_PREDICATE_R4);
        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO3_OBJECT_R3, POS_BAR3_OBJECT_R1);
        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2, VAR_BAR2_PREDICATE_R4);
        Set<Tuple> tmpTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO3_OBJECT_R3, POS_BAR3_OBJECT_R1);
        resultTuple.addAll(tmpTuple);
        Set<Attribute> heading = createHeading(POS_FOO1_SUBJECT, POS_FOO2_PREDICATE, VAR_BAR2_PREDICATE,
            POS_FOO3_OBJECT, POS_BAR3_OBJECT);
        checkUnion(createRelation(heading, resultTuple), createRelation(tuple1), createRelation(tuple2));

    }

    public void testNonUnionCompatibleNoSharedAttribute() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2, POS_FOO3_OBJECT_R3);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, VAR_BAR2_PREDICATE_R4, VAR_BAR1_OBJECT_R3);
        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2, POS_FOO3_OBJECT_R3);
        Set<Tuple> tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, VAR_BAR2_PREDICATE_R4, VAR_BAR1_OBJECT_R3);
        resultTuple.addAll(tmpTuple);
        Set<Attribute> heading = createHeading(POS_FOO1_SUBJECT, POS_FOO2_PREDICATE, POS_FOO3_OBJECT,
            VAR_BAR1_SUBJECT, VAR_BAR2_PREDICATE, VAR_BAR1_OBJECT);
        checkUnion(createRelation(heading, resultTuple), createRelation(tuple1), createRelation(tuple2));
    }

    public abstract Union getUnion();

    protected void checkUnion(Relation expectedResult, Relation relation1, Relation relation2) {
        Relation relation = getUnion().union(relation1, relation2);

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
