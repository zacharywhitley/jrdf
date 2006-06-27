/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2005 The JRDF Project.  All rights reserved.
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
import org.jrdf.graph.URIReference;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.AttributeValuePairComparator;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.RelationComparator;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleComparator;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.PositionName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.constants.RelationDEE;
import org.jrdf.query.relation.constants.RelationDUM;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.mem.AttributeValuePairImpl;
import org.jrdf.query.relation.mem.RelationImpl;
import org.jrdf.query.relation.mem.TupleImpl;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.util.test.NodeTestUtil;
import org.jrdf.vocabulary.RDF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Tests the integration between join and other classes such as RelationDEE, RelationDUM and other
 * relations.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public class JoinImplIntegrationTest extends TestCase {
    private static final JRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final AttributeComparator ATTRIBUTE_COMPARATOR = FACTORY.getNewAttributeComparator();
    private static final TupleComparator TUPLE_COMPARATOR = FACTORY.getNewTupleComparator();

    private static final AttributeName POS_FOO1 = new PositionName("foo1");
    private static final AttributeName POS_FOO2 = new PositionName("foo2");
    private static final AttributeName POS_FOO3 = new PositionName("foo3");
    private static final AttributeName POS_FOO4 = new PositionName("foo4");
    private static final AttributeName POS_FOO5 = new PositionName("foo5");

    private static final AttributeName VAR_BAR1 = new VariableName("bar1");
    private static final AttributeName VAR_BAR2 = new VariableName("bar2");
    private static final AttributeName POS_BAR3 = new PositionName("bar3");

    private static final Attribute POS_FOO1_SUBJECT = new AttributeImpl(POS_FOO1, new SubjectNodeType());
    private static final Attribute POS_FOO2_PREDICATE = new AttributeImpl(POS_FOO2, new PredicateNodeType());
    private static final Attribute POS_FOO3_OBJECT = new AttributeImpl(POS_FOO3, new ObjectNodeType());
    private static final Attribute POS_FOO4_PREDICATE = new AttributeImpl(POS_FOO4, new PredicateNodeType());
    private static final Attribute POS_FOO5_OBJECT = new AttributeImpl(POS_FOO5, new ObjectNodeType());

    private static final Attribute VAR_BAR1_SUBJECT = new AttributeImpl(VAR_BAR1, new SubjectNodeType());
    private static final Attribute VAR_BAR2_PREDICATE = new AttributeImpl(VAR_BAR2, new PredicateNodeType());
    private static final Attribute VAR_BAR3_OBJECT = new AttributeImpl(POS_BAR3, new ObjectNodeType());

    private static final URIReference RESOURCE_1 = NodeTestUtil.createResource(RDF.ALT);
    private static final URIReference RESOURCE_2 = NodeTestUtil.createResource(RDF.BAG);
    private static final URIReference RESOURCE_3 = NodeTestUtil.createResource(RDF.FIRST);
    private static final URIReference RESOURCE_4 = NodeTestUtil.createResource(RDF.LI);
    private static final URIReference RESOURCE_5 = NodeTestUtil.createResource(RDF.SUBJECT);
    private static final URIReference RESOURCE_6 = NodeTestUtil.createResource(RDF.PREDICATE);

    private static final AttributeValuePair POS_FOO1_SUBJECT_R1 =
            new AttributeValuePairImpl(POS_FOO1_SUBJECT, RESOURCE_1);
    private static final AttributeValuePair POS_FOO1_SUBJECT_R3 =
            new AttributeValuePairImpl(POS_FOO1_SUBJECT, RESOURCE_3);
    private static final AttributeValuePair POS_FOO2_PREDICATE_R2 =
            new AttributeValuePairImpl(POS_FOO2_PREDICATE, RESOURCE_2);
    private static final AttributeValuePair POS_FOO2_PREDICATE_R4 =
            new AttributeValuePairImpl(POS_FOO2_PREDICATE, RESOURCE_4);
    private static final AttributeValuePair POS_FOO3_OBJECT_R3 =
            new AttributeValuePairImpl(POS_FOO3_OBJECT, RESOURCE_3);

    private static final AttributeValuePair VAR_BAR1_SUBJECT_R3 =
            new AttributeValuePairImpl(VAR_BAR1_SUBJECT, RESOURCE_3);
    private static final AttributeValuePair VAR_BAR2_PREDICATE_R4 =
            new AttributeValuePairImpl(VAR_BAR2_PREDICATE, RESOURCE_4);
    private static final AttributeValuePair VAR_BAR3_OBJECT_R1 =
            new AttributeValuePairImpl(VAR_BAR3_OBJECT, RESOURCE_1);

    private static final AttributeValuePair POS_FOO4_PREDICATE_R3 =
            new AttributeValuePairImpl(POS_FOO4_PREDICATE, RESOURCE_3);
    private static final AttributeValuePair POS_FOO5_OBJECT_R4 =
            new AttributeValuePairImpl(POS_FOO5_OBJECT, RESOURCE_4);

    private static final AttributeValuePair POS_FOO4_PREDICATE_R5 =
            new AttributeValuePairImpl(POS_FOO4_PREDICATE, RESOURCE_5);
    private static final AttributeValuePair POS_FOO5_OBJECT_R6 =
            new AttributeValuePairImpl(POS_FOO5_OBJECT, RESOURCE_6);


    private static final org.jrdf.query.relation.operation.Join JOIN = FACTORY.getNewJoin();
    private static final Set<Relation> EMPTY = Collections.emptySet();


    public void testRelationDEEandDUM() {
        // The JOIN of empty is DEE.
        checkRelation(RelationDEE.RELATION_DEE, EMPTY);
        // The JOIN of DEE is DEE.
        checkRelation(RelationDEE.RELATION_DEE, Collections.singleton(RelationDEE.RELATION_DEE));
        // The JOIN of DUM is DUM.
        checkRelation(RelationDUM.RELATION_DUM, Collections.singleton(RelationDUM.RELATION_DUM));
    }

    @SuppressWarnings({ "unchecked" })
    public void testCartesianProduct() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, VAR_BAR2_PREDICATE_R4);
        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2,
                VAR_BAR1_SUBJECT_R3, VAR_BAR2_PREDICATE_R4);
        checkJoin(createRelation(tuple1, tuple2), createRelation(resultTuple));
    }

    @SuppressWarnings({ "unchecked" })
    public void testCartesianProduct2() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        Set<Tuple> tmpTuple = createASingleTuple(POS_FOO1_SUBJECT_R3, POS_FOO2_PREDICATE_R4);
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
        checkJoin(createRelation(tuple1, tuple2), createRelation(resultTuple));
    }

    @SuppressWarnings({ "unchecked" })
    public void testNaturalJoin() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1, VAR_BAR2_PREDICATE_R4);
        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2,
                VAR_BAR2_PREDICATE_R4);
        checkJoin(createRelation(tuple1, tuple2), createRelation(resultTuple));
    }

    @SuppressWarnings({ "unchecked" })
    public void testNaturalJoin2() {
        Set<Tuple> tuple1 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2);
        Set<Tuple> tuple2 = createASingleTuple(POS_FOO1_SUBJECT_R1, VAR_BAR2_PREDICATE_R4);
        Set<Tuple> tuple3 = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO3_OBJECT_R3);
        Set<Tuple> tuple4 = createASingleTuple(POS_FOO1_SUBJECT_R1, VAR_BAR3_OBJECT_R1);
        Set<Tuple> resultTuple = createASingleTuple(POS_FOO1_SUBJECT_R1, POS_FOO2_PREDICATE_R2,
                VAR_BAR2_PREDICATE_R4, POS_FOO3_OBJECT_R3, VAR_BAR3_OBJECT_R1);
        checkJoin(createRelation(tuple1, tuple2, tuple3, tuple4), createRelation(resultTuple));
    }

    @SuppressWarnings({ "unchecked" })
    public void testNaturalJoin3() {
        Set<Tuple> tuple1 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO2_PREDICATE_R2, POS_FOO3_OBJECT_R3);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO5_OBJECT_R4);
        Set<Tuple> tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R5, POS_FOO5_OBJECT_R6);
        tuple2.addAll(tmpTuple);

        Set<Tuple> resultTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO2_PREDICATE_R2,
                POS_FOO3_OBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO5_OBJECT_R4);
        tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO2_PREDICATE_R2,
                POS_FOO3_OBJECT_R3, POS_FOO4_PREDICATE_R5, POS_FOO5_OBJECT_R6);
        resultTuple.addAll(tmpTuple);

        Relation relation = createRelation(resultTuple);
        checkJoin(createRelation(tuple1, tuple2), relation);
    }

    @SuppressWarnings({ "unchecked" })
    public void testNaturalJoin4() {
        Set<Tuple> tuple1 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO5_OBJECT_R4);
        Set<Tuple> tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R5, POS_FOO5_OBJECT_R6);
        tuple1.addAll(tmpTuple);
        Set<Tuple> tuple2 = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO2_PREDICATE_R2, POS_FOO3_OBJECT_R3);

        Set<Tuple> resultTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO2_PREDICATE_R2,
                POS_FOO3_OBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO5_OBJECT_R4);
        tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO2_PREDICATE_R2,
                POS_FOO3_OBJECT_R3, POS_FOO4_PREDICATE_R5, POS_FOO5_OBJECT_R6);
        resultTuple.addAll(tmpTuple);

        Relation relation = createRelation(resultTuple);
        checkJoin(createRelation(tuple1, tuple2), relation);
    }

    private Set<Tuple> createASingleTuple(AttributeValuePair... attributeValuePairs) {
        AttributeValuePairComparator avpComparator = FACTORY.getNewAttributeValuePairComparator();
        Set<AttributeValuePair> values = new TreeSet<AttributeValuePair>(avpComparator);
        for (AttributeValuePair attributeValuePair : attributeValuePairs) {
            values.add(attributeValuePair);
        }

        TupleComparator tupleComparator = FACTORY.getNewTupleComparator();
        Set<Tuple> tuples = new TreeSet<Tuple>(tupleComparator);
        Tuple t = new TupleImpl(values, FACTORY.getNewAttributeValuePairComparator());
        tuples.add(t);
        return tuples;
    }

    private void checkJoin(List<Relation> relations, Relation expectedResult) {
        Set<Relation> tuples = createRelations(relations.toArray(new Relation[]{}));
        checkRelation(expectedResult, tuples);
    }

    private List<Relation> createRelation(Set<Tuple>... tuple1) {
        List<Relation> relations = new ArrayList<Relation>();
        for (Set<Tuple> tuples : tuple1) {
            Relation relation = createRelation(tuples);
            relations.add(relation);
        }
        return relations;
    }

    private static Relation createRelation(Set<Tuple> newTuples) {
        return new RelationImpl(newTuples, ATTRIBUTE_COMPARATOR, TUPLE_COMPARATOR);
    }

    private Set<Relation> createRelations(Relation... relations) {
        RelationComparator relationComparator = FACTORY.getNewRelationComparator();
        Set<Relation> tuples = new TreeSet<Relation>(relationComparator);
        for (Relation relation : relations) {
            tuples.add(relation);
        }
        return tuples;
    }

    private void checkRelation(Relation expected, Set<Relation> actual) {
        Relation relation = JOIN.join(actual);

//        Set<Tuple> sortedTuples = relation.getSortedTuples();
//        Set<Tuple> sortedTuples2 = expected.getSortedTuples();
//        System.err.println("Sorted Actual tuples relation: " + relation.getSortedTuples());
//        System.err.println("Sorted Expected tuples relation: " + expected.getSortedTuples());
//        System.err.println("-------------------------------");
//        boolean isEqual = sortedTuples.equals(sortedTuples2);
//        System.err.println("Sorted Expected tuples relation1: " + isEqual);
//        System.err.println("Sorted Expected tuples relation2: " + expected.getSortedTuples().equals(relation.getSortedTuples()));
//        System.err.println("Sorted Expected tuples relation3: " + relation.getSortedTuples().equals(expected.getSortedTuples()));
        assertEquals(expected, relation);
    }
}
