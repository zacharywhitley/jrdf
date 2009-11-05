/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 *  ====================================================================
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
 */

package org.jrdf.query.relation.operation.mem.union;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.jrdf.TestJRDFFactory;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.EvaluatedRelation;
import org.jrdf.query.relation.Tuple;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO2_PREDICATE;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO2_PREDICATE_R2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECT_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createASingleTuple;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createHeading;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createRelation;
import org.jrdf.query.relation.operation.mem.join.TupleEngine;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class SubsumptionEngineIntegrationTest {
    private static final TestJRDFFactory TEST_FACTORY = TestJRDFFactory.getFactory();
    private TupleEngine engine;

    @Before
    public void createEngine() {
        engine = TEST_FACTORY.getNewSubsumptionEngine();
    }

    @Test
    public void theSameTuplesReturnNothing() {
        Set<Tuple> tuple1 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4);
        Set<Tuple> expectedResult = Collections.emptySet();
        SortedSet<Attribute> expectedHeading = createHeading(VAR_BAR1_SUBJECT, POS_FOO4_PREDICATE, POS_FOO3_OBJECT);
        checkProcess(expectedHeading, createRelation(expectedResult), createRelation(tuple1), createRelation(tuple2));
    }

    @Test
    public void oneTupleSubsumesAnother() {
        Set<Tuple> tuple1 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3);
        Set<Tuple> expectedResult = createASingleTuple(VAR_BAR1_SUBJECT_R3);
        SortedSet<Attribute> expectedHeading = createHeading(VAR_BAR1_SUBJECT, POS_FOO4_PREDICATE, POS_FOO3_OBJECT);
        checkProcess(expectedHeading, createRelation(expectedResult), createRelation(tuple1), createRelation(tuple2));
        checkProcess(expectedHeading, createRelation(expectedResult), createRelation(tuple2), createRelation(tuple1));
    }

    @Test
    public void differentTuplesWithNoSharedValuesReturnsEmpty() {
        Set<Tuple> tuple1 = createASingleTuple(VAR_BAR1_SUBJECT_R4, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3);
        Set<Tuple> expectedResult = Collections.emptySet();
        SortedSet<Attribute> expectedHeading = createHeading(VAR_BAR1_SUBJECT, POS_FOO4_PREDICATE, POS_FOO3_OBJECT);
        checkProcess(expectedHeading, createRelation(expectedResult), createRelation(tuple1), createRelation(tuple2));
        checkProcess(expectedHeading, createRelation(expectedResult), createRelation(tuple2), createRelation(tuple1));
    }

    @Test
    public void differentTuplesWithOneSharedValueReturnsEmpty() {
        Set<Tuple> tuple1 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R2);
        Set<Tuple> expectedResult = Collections.emptySet();
        SortedSet<Attribute> expectedHeading = createHeading(VAR_BAR1_SUBJECT, POS_FOO4_PREDICATE, POS_FOO3_OBJECT);
        checkProcess(expectedHeading, createRelation(expectedResult), createRelation(tuple1), createRelation(tuple2));
        checkProcess(expectedHeading, createRelation(expectedResult), createRelation(tuple2), createRelation(tuple1));
    }

    @Test
    public void secondTupleHasDifferentValueThanFirstTupleReturnsNothing() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO2_PREDICATE_R2, POS_FOO3_OBJECT_R4, POS_FOO4_PREDICATE_R3);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3);
        Set<Tuple> expectedResult = Collections.emptySet();
        checkProcess(createHeading(VAR_BAR1_SUBJECT, POS_FOO2_PREDICATE, POS_FOO3_OBJECT, POS_FOO4_PREDICATE),
            createRelation(expectedResult), createRelation(tuple1), createRelation(tuple2));
    }

    private void checkProcess(SortedSet<Attribute> expectedHeading, final EvaluatedRelation expectedResult,
        EvaluatedRelation relation1, EvaluatedRelation relation2) {
        SortedSet<Attribute> headings = engine.getHeading(relation1, relation2);
        SortedSet<Tuple> actualResult = new TreeSet<Tuple>(TEST_FACTORY.getNewTupleComparator());
        engine.processRelations(headings, relation1, relation2, actualResult);

        assertThat(headings, equalTo(expectedHeading));
        assertThat(createRelation(actualResult), equalTo(expectedResult));
    }
}
