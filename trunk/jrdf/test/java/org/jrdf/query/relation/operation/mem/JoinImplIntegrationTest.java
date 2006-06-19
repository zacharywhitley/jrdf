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
import org.jrdf.TestJRDFFactory;
import org.jrdf.JRDFFactory;
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
import org.jrdf.query.relation.type.BlankNodeType;
import org.jrdf.query.relation.type.LiteralType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.util.test.NodeTestUtil;
import org.jrdf.vocabulary.RDF;

import java.util.Collections;
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

    private static final AttributeName ATTRIBUTE_NAME_1 = new PositionName("foo1");
    private static final AttributeName ATTRIBUTE_NAME_2 = new PositionName("foo2");
    private static final AttributeName ATTRIBUTE_NAME_3 = new VariableName("bar1");
    private static final AttributeName ATTRIBUTE_NAME_4 = new VariableName("bar2");
    private static final Attribute ATTRIBUTE_1 = new AttributeImpl(ATTRIBUTE_NAME_1, new LiteralType());
    private static final Attribute ATTRIBUTE_2 = new AttributeImpl(ATTRIBUTE_NAME_2, new BlankNodeType());
    private static final Attribute ATTRIBUTE_3 = new AttributeImpl(ATTRIBUTE_NAME_3, new SubjectNodeType());
    private static final Attribute ATTRIBUTE_4 = new AttributeImpl(ATTRIBUTE_NAME_4, new PredicateNodeType());
    private static final URIReference RESOURCE_1 = NodeTestUtil.createResource(RDF.ALT);
    private static final URIReference RESOURCE_2 = NodeTestUtil.createResource(RDF.BAG);
    private static final URIReference RESOURCE_3 = NodeTestUtil.createResource(RDF.FIRST);
    private static final URIReference RESOURCE_4 = NodeTestUtil.createResource(RDF.LI);
    private static final AttributeValuePair ATTRIBUTE_VALUE_PAIR_1 =
        new AttributeValuePairImpl(ATTRIBUTE_1, RESOURCE_1);
    private static final AttributeValuePair ATTRIBUTE_VALUE_PAIR_2 =
        new AttributeValuePairImpl(ATTRIBUTE_2, RESOURCE_2);
    private static final AttributeValuePair ATTRIBUTE_VALUE_PAIR_3 =
        new AttributeValuePairImpl(ATTRIBUTE_3, RESOURCE_3);
    private static final AttributeValuePair ATTRIBUTE_VALUE_PAIR_4 =
        new AttributeValuePairImpl(ATTRIBUTE_4, RESOURCE_4);
    private static final org.jrdf.query.relation.operation.Join JOIN = FACTORY.getNewJoin();


    public void testRelationDEEandDUM() {
        // The JOIN of empty is DEE.
        checkRelation(RelationDEE.RELATION_DEE, Collections.<Relation>emptySet());
        // The JOIN of DEE is DEE.
        checkRelation(RelationDEE.RELATION_DEE, Collections.singleton(RelationDEE.RELATION_DEE));
        // The JOIN of DUM is DUM.
        checkRelation(RelationDUM.RELATION_DUM, Collections.singleton(RelationDUM.RELATION_DUM));
    }

    public void testCartesianProduct() {
        Set<Tuple> tuple1 = createASingleTuple(ATTRIBUTE_VALUE_PAIR_1, ATTRIBUTE_VALUE_PAIR_2);
        Set<Tuple> tuple2 = createASingleTuple(ATTRIBUTE_VALUE_PAIR_3, ATTRIBUTE_VALUE_PAIR_4);
        Set<Tuple> resultTuple = createASingleTuple(ATTRIBUTE_VALUE_PAIR_1, ATTRIBUTE_VALUE_PAIR_2,
            ATTRIBUTE_VALUE_PAIR_3, ATTRIBUTE_VALUE_PAIR_4);
        Set<Attribute> heading1 = createHeading(ATTRIBUTE_1, ATTRIBUTE_2);
        Set<Attribute> heading2 = createHeading(ATTRIBUTE_3, ATTRIBUTE_4);
        Set<Attribute> resultHeading = createHeading(ATTRIBUTE_1, ATTRIBUTE_2, ATTRIBUTE_3, ATTRIBUTE_4);
        Relation relation1 = createRelation(heading1, tuple1);
        Relation relation2 = createRelation(heading2, tuple2);
        Relation expectedResult = createRelation(resultHeading, resultTuple);

        RelationComparator relationComparator = FACTORY.getNewRelationComparator();
        Set<Relation> tuples = new TreeSet<Relation>(relationComparator);
        tuples.add(relation1);
        tuples.add(relation2);

        checkRelation(expectedResult, tuples);
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

    private Set<Attribute> createHeading(Attribute... attributes) {
        AttributeComparator attributeComparator = FACTORY.getNewAttributeComparator();
        Set<Attribute> heading = new TreeSet<Attribute>(attributeComparator);
        for (Attribute attribute : attributes) {
            heading.add(attribute);
        }
        return heading;
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

    private static Relation createRelation(Set<Attribute> newHeading, Set<Tuple> newTuples) {
        return new RelationImpl(newHeading, newTuples, ATTRIBUTE_COMPARATOR, TUPLE_COMPARATOR);
    }
}
