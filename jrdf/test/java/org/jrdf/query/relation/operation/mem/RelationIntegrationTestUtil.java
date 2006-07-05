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

import org.jrdf.TestJRDFFactory;
import org.jrdf.graph.URIReference;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.AttributeValuePairComparator;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.RelationComparator;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleComparator;
import org.jrdf.query.relation.TupleFactory;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.PositionName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.mem.AttributeImpl;
import org.jrdf.query.relation.mem.AttributeValuePairImpl;
import org.jrdf.query.relation.mem.TupleFactoryImpl;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.query.relation.type.SubjectPredicateNodeType;
import org.jrdf.query.relation.type.SubjectObjectNodeType;
import org.jrdf.util.test.NodeTestUtil;
import org.jrdf.vocabulary.RDF;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class description goes here.
 *
 * @author Andrew Newman
 * @version $Revision:$
 */
public class RelationIntegrationTestUtil {
    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final RelationFactory RELATION_FACTORY = FACTORY.getNewRelationFactory();

    public static final AttributeName POS_FOO1 = new PositionName("foo1");
    public static final AttributeName POS_FOO2 = new PositionName("foo2");
    public static final AttributeName POS_FOO3 = new PositionName("foo3");
    public static final AttributeName POS_FOO4 = new PositionName("foo4");
    public static final AttributeName POS_FOO5 = new PositionName("foo5");
    public static final AttributeName VAR_BAR1 = new VariableName("bar1");
    public static final AttributeName VAR_BAR2 = new VariableName("bar2");
    public static final AttributeName POS_BAR3 = new PositionName("bar3");
    public static final Attribute POS_FOO1_SUBJECT = new AttributeImpl(POS_FOO1, new SubjectNodeType());
    public static final Attribute POS_FOO2_PREDICATE = new AttributeImpl(POS_FOO2, new PredicateNodeType());
    public static final Attribute POS_FOO3_OBJECT = new AttributeImpl(POS_FOO3, new ObjectNodeType());
    public static final Attribute POS_FOO4_PREDICATE = new AttributeImpl(POS_FOO4, new PredicateNodeType());
    public static final Attribute POS_FOO5_OBJECT = new AttributeImpl(POS_FOO5, new ObjectNodeType());
    public static final Attribute VAR_BAR1_SUBJECT = new AttributeImpl(VAR_BAR1, new SubjectNodeType());
    public static final Attribute VAR_BAR1_PREDICATE = new AttributeImpl(VAR_BAR1, new PredicateNodeType());
    public static final Attribute VAR_BAR1_OBJECT = new AttributeImpl(VAR_BAR1, new ObjectNodeType());
    public static final Attribute VAR_BAR1_SUBJECTPREDICATE = new AttributeImpl(VAR_BAR1, new SubjectPredicateNodeType());
    public static final Attribute VAR_BAR1_SUBJECTOBJECT = new AttributeImpl(VAR_BAR1, new SubjectObjectNodeType());
    public static final Attribute VAR_BAR2_PREDICATE = new AttributeImpl(VAR_BAR2, new PredicateNodeType());
    public static final Attribute POS_BAR3_OBJECT = new AttributeImpl(POS_BAR3, new ObjectNodeType());
    public static final URIReference RESOURCE_1 = NodeTestUtil.createResource(RDF.ALT);
    public static final URIReference RESOURCE_2 = NodeTestUtil.createResource(RDF.BAG);
    public static final URIReference RESOURCE_3 = NodeTestUtil.createResource(RDF.FIRST);
    public static final URIReference RESOURCE_4 = NodeTestUtil.createResource(RDF.LI);
    public static final URIReference RESOURCE_5 = NodeTestUtil.createResource(RDF.SUBJECT);
    public static final URIReference RESOURCE_6 = NodeTestUtil.createResource(RDF.PREDICATE);

    public static final AttributeValuePair POS_FOO1_SUBJECT_R1 =
            new AttributeValuePairImpl(POS_FOO1_SUBJECT, RESOURCE_1);
    public static final AttributeValuePair POS_FOO1_SUBJECT_R3 =
            new AttributeValuePairImpl(POS_FOO1_SUBJECT, RESOURCE_3);

    public static final AttributeValuePair POS_FOO2_PREDICATE_R2 =
            new AttributeValuePairImpl(POS_FOO2_PREDICATE, RESOURCE_2);
    public static final AttributeValuePair POS_FOO2_PREDICATE_R4 =
            new AttributeValuePairImpl(POS_FOO2_PREDICATE, RESOURCE_4);
    public static final AttributeValuePair POS_FOO4_PREDICATE_R2 =
            new AttributeValuePairImpl(POS_FOO4_PREDICATE, RESOURCE_2);
    public static final AttributeValuePair POS_FOO4_PREDICATE_R3 =
            new AttributeValuePairImpl(POS_FOO4_PREDICATE, RESOURCE_3);
    public static final AttributeValuePair POS_FOO4_PREDICATE_R5 =
            new AttributeValuePairImpl(POS_FOO4_PREDICATE, RESOURCE_5);
    public static final AttributeValuePair POS_FOO5_OBJECT_R6 =
            new AttributeValuePairImpl(POS_FOO5_OBJECT, RESOURCE_6);

    public static final AttributeValuePair POS_FOO3_OBJECT_R3 =
            new AttributeValuePairImpl(POS_FOO3_OBJECT, RESOURCE_3);
    public static final AttributeValuePair POS_FOO3_OBJECT_R4 =
            new AttributeValuePairImpl(POS_FOO3_OBJECT, RESOURCE_4);
    public static final AttributeValuePair POS_FOO3_OBJECT_R5 =
            new AttributeValuePairImpl(POS_FOO3_OBJECT, RESOURCE_4);
    public static final AttributeValuePair POS_FOO5_OBJECT_R4 =
            new AttributeValuePairImpl(POS_FOO5_OBJECT, RESOURCE_4);

    public static final AttributeValuePair VAR_BAR1_SUBJECT_R3 =
            new AttributeValuePairImpl(VAR_BAR1_SUBJECT, RESOURCE_3);
    public static final AttributeValuePair VAR_BAR1_SUBJECT_R4 =
            new AttributeValuePairImpl(VAR_BAR1_SUBJECT, RESOURCE_4);
    public static final AttributeValuePair VAR_BAR1_PREDICATE_R3 =
            new AttributeValuePairImpl(VAR_BAR1_PREDICATE, RESOURCE_3);
    public static final AttributeValuePair VAR_BAR1_PREDICATE_R4 =
            new AttributeValuePairImpl(VAR_BAR1_PREDICATE, RESOURCE_4);
    public static final AttributeValuePair VAR_BAR2_PREDICATE_R4 =
            new AttributeValuePairImpl(VAR_BAR2_PREDICATE, RESOURCE_4);
    public static final AttributeValuePair VAR_BAR1_OBJECT_R3 =
            new AttributeValuePairImpl(VAR_BAR1_OBJECT, RESOURCE_3);
    public static final AttributeValuePair VAR_BAR1_OBJECT_R4 =
            new AttributeValuePairImpl(VAR_BAR1_OBJECT, RESOURCE_4);
    public static final AttributeValuePair POS_BAR3_OBJECT_R1 =
            new AttributeValuePairImpl(POS_BAR3_OBJECT, RESOURCE_1);

    public static final AttributeValuePair VAR_BAR1_SUBJECTPREDICATE_R3 =
            new AttributeValuePairImpl(VAR_BAR1_SUBJECTPREDICATE, RESOURCE_3);
    public static final AttributeValuePair VAR_BAR1_SUBJECTPREDICATE_R4 =
            new AttributeValuePairImpl(VAR_BAR1_SUBJECTPREDICATE, RESOURCE_4);
    public static final AttributeValuePair VAR_BAR1_SUBJECTOBJECT_R3 =
            new AttributeValuePairImpl(VAR_BAR1_SUBJECTOBJECT, RESOURCE_3);
    public static final AttributeValuePair VAR_BAR1_SUBJECTOBJECT_R4 =
            new AttributeValuePairImpl(VAR_BAR1_SUBJECTOBJECT, RESOURCE_4);

    public static Set<Tuple> createASingleTuple(AttributeValuePair... attributeValuePairs) {
        AttributeValuePairComparator avpComparator = FACTORY.getNewAttributeValuePairComparator();
        Set<AttributeValuePair> values = new TreeSet<AttributeValuePair>(avpComparator);
        for (AttributeValuePair attributeValuePair : attributeValuePairs) {
            values.add(attributeValuePair);
        }

        TupleComparator tupleComparator = FACTORY.getNewTupleComparator();
        Set<Tuple> tuples = new TreeSet<Tuple>(tupleComparator);
        TupleFactory tf = new TupleFactoryImpl(FACTORY.getNewAttributeValuePairComparator());
        Tuple t = tf.getTuple(values);
        tuples.add(t);
        return tuples;
    }

    public static List<Relation> createRelation(Set<Tuple>... tuple) {
        List<Relation> relations = new ArrayList<Relation>();
        for (Set<Tuple> tuples : tuple) {
            Relation relation = createRelation(tuples);
            relations.add(relation);
        }
        return relations;
    }

    public static Relation createRelation(Set<Tuple> newTuples) {
        return RELATION_FACTORY.getRelation(newTuples);
    }

    public static Set<Relation> createRelations(Relation... relations) {
        RelationComparator relationComparator = FACTORY.getNewRelationComparator();
        Set<Relation> tuples = new TreeSet<Relation>(relationComparator);
        for (Relation relation : relations) {
            tuples.add(relation);
        }
        return tuples;
    }

}
