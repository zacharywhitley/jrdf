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

import org.jrdf.TestJRDFFactory;
import org.jrdf.graph.Node;
import org.jrdf.graph.URIReference;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.RelationFactory;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleFactory;
import org.jrdf.query.relation.ValueOperation;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.PositionName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.mem.AVPOperation;
import org.jrdf.query.relation.mem.AttributeImpl;
import static org.jrdf.query.relation.mem.EqAVPOperation.*;
import org.jrdf.query.relation.mem.NeqAVPOperation;
import org.jrdf.query.relation.mem.RelationFactoryImpl;
import org.jrdf.query.relation.mem.TupleFactoryImpl;
import org.jrdf.query.relation.mem.ValueOperationImpl;
import org.jrdf.query.relation.type.ObjectNodeType;
import org.jrdf.query.relation.type.PredicateNodeType;
import org.jrdf.query.relation.type.SubjectNodeType;
import org.jrdf.query.relation.type.SubjectObjectNodeType;
import org.jrdf.query.relation.type.SubjectPredicateNodeType;
import org.jrdf.query.relation.type.SubjectPredicateObjectNodeType;
import static org.jrdf.util.test.NodeTestUtil.*;
import org.jrdf.vocabulary.RDF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class RelationIntegrationTestUtil {
    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final RelationFactory RELATION_FACTORY = FACTORY.getNewRelationFactory();
    private static final AttributeComparator ATTRIBUTE_COMPARATOR = FACTORY.getNewAttributeComparator();

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
    public static final Attribute VAR_BAR1_SUBJECTPREDICATEOBJECT = new AttributeImpl(VAR_BAR1, new SubjectPredicateObjectNodeType());
    public static final Attribute VAR_BAR2_PREDICATE = new AttributeImpl(VAR_BAR2, new PredicateNodeType());
    public static final Attribute POS_BAR3_OBJECT = new AttributeImpl(POS_BAR3, new ObjectNodeType());

    public static final URIReference RESOURCE_1 = createResource(RDF.ALT);
    public static final URIReference RESOURCE_2 = createResource(RDF.BAG);
    public static final URIReference RESOURCE_3 = createResource(RDF.FIRST);
    public static final URIReference RESOURCE_4 = createResource(RDF.LI);
    public static final URIReference RESOURCE_5 = createResource(RDF.SUBJECT);
    public static final URIReference RESOURCE_6 = createResource(RDF.PREDICATE);

    public static final Map<Attribute, ValueOperation> POS_FOO1_SUBJECT_R1 = createAvo(POS_FOO1_SUBJECT, RESOURCE_1);
    public static final Map<Attribute, ValueOperation> POS_FOO1_SUBJECT_R3 = createAvo(POS_FOO1_SUBJECT, RESOURCE_3);
    public static final Map<Attribute, ValueOperation> POS_FOO1_SUBJECT_R4 = createAvo(POS_FOO1_SUBJECT, RESOURCE_4);

    public static final Map<Attribute, ValueOperation> POS_FOO2_PREDICATE_R2 = createAvo(POS_FOO2_PREDICATE, RESOURCE_1);
    public static final Map<Attribute, ValueOperation> POS_FOO2_PREDICATE_R4 = createAvo(POS_FOO2_PREDICATE, RESOURCE_3);
    public static final Map<Attribute, ValueOperation> POS_FOO2_PREDICATE_R6 = createAvo(POS_FOO2_PREDICATE, RESOURCE_4);
    public static final Map<Attribute, ValueOperation> POS_FOO4_PREDICATE_R2 = createAvo(POS_FOO4_PREDICATE, RESOURCE_1);
    public static final Map<Attribute, ValueOperation> POS_FOO4_PREDICATE_R3 = createAvo(POS_FOO4_PREDICATE, RESOURCE_3);
    public static final Map<Attribute, ValueOperation> POS_FOO4_PREDICATE_R5 = createAvo(POS_FOO4_PREDICATE, RESOURCE_4);
    public static final Map<Attribute, ValueOperation> POS_FOO5_OBJECT_R6 = createAvo(POS_FOO5_OBJECT, RESOURCE_6);

    public static final Map<Attribute, ValueOperation> POS_FOO3_OBJECT_R3 = createAvo(POS_FOO3_OBJECT, RESOURCE_3);
    public static final Map<Attribute, ValueOperation> POS_FOO3_OBJECT_R4 = createAvo(POS_FOO3_OBJECT, RESOURCE_4);
    public static final Map<Attribute, ValueOperation> POS_FOO3_OBJECT_R5 = createAvo(POS_FOO3_OBJECT, RESOURCE_5);
    public static final Map<Attribute, ValueOperation> POS_FOO3_OBJECT_R6 = createAvo(POS_FOO3_OBJECT, RESOURCE_6);
    public static final Map<Attribute, ValueOperation> POS_FOO5_OBJECT_R4 = createAvo(POS_FOO5_OBJECT, RESOURCE_4);

    public static final Map<Attribute, ValueOperation> VAR_BAR1_SUBJECT_R3 = createAvo(VAR_BAR1_SUBJECT, RESOURCE_3);
    public static final Map<Attribute, ValueOperation> VAR_BAR1_SUBJECT_NOT_R3 = createAvo(VAR_BAR1_SUBJECT, RESOURCE_3, NeqAVPOperation.NEQUALS);
    public static final Map<Attribute, ValueOperation> VAR_BAR1_SUBJECT_R4 = createAvo(VAR_BAR1_SUBJECT, RESOURCE_4);
    public static final Map<Attribute, ValueOperation> VAR_BAR1_SUBJECT_R5 = createAvo(VAR_BAR1_SUBJECT, RESOURCE_5);
    public static final Map<Attribute, ValueOperation> VAR_BAR1_PREDICATE_R1 = createAvo(VAR_BAR1_PREDICATE, RESOURCE_1);
    public static final Map<Attribute, ValueOperation> VAR_BAR1_PREDICATE_R2 = createAvo(VAR_BAR1_PREDICATE, RESOURCE_2);
    public static final Map<Attribute, ValueOperation> VAR_BAR1_PREDICATE_R3 = createAvo(VAR_BAR1_PREDICATE, RESOURCE_3);
    public static final Map<Attribute, ValueOperation> VAR_BAR1_PREDICATE_R4 = createAvo(VAR_BAR1_PREDICATE, RESOURCE_4);
    public static final Map<Attribute, ValueOperation> VAR_BAR2_PREDICATE_R4 = createAvo(VAR_BAR2_PREDICATE, RESOURCE_4);
    public static final Map<Attribute, ValueOperation> VAR_BAR1_OBJECT_R3 = createAvo(VAR_BAR1_OBJECT, RESOURCE_3);
    public static final Map<Attribute, ValueOperation> VAR_BAR1_OBJECT_R4 = createAvo(VAR_BAR1_OBJECT, RESOURCE_4);
    public static final Map<Attribute, ValueOperation> POS_BAR3_OBJECT_R1 = createAvo(POS_BAR3_OBJECT, RESOURCE_1);

    public static final Map<Attribute, ValueOperation> VAR_BAR1_SUBJECTPREDICATE_R3 = createAvo(VAR_BAR1_SUBJECTPREDICATE, RESOURCE_3);
    public static final Map<Attribute, ValueOperation> VAR_BAR1_SUBJECTPREDICATE_R4 = createAvo(VAR_BAR1_SUBJECTPREDICATE, RESOURCE_4);
    public static final Map<Attribute, ValueOperation> VAR_BAR1_SUBJECTPREDICATEOBJECT_R3 = createAvo(VAR_BAR1_SUBJECTPREDICATEOBJECT, RESOURCE_3);
    public static final Map<Attribute, ValueOperation> VAR_BAR1_SUBJECTPREDICATEOBJECT_R4 = createAvo(VAR_BAR1_SUBJECTPREDICATEOBJECT, RESOURCE_4);
    public static final Map<Attribute, ValueOperation> VAR_BAR1_SUBJECTOBJECT_R3 = createAvo(VAR_BAR1_SUBJECTOBJECT, RESOURCE_3);
    public static final Map<Attribute, ValueOperation> VAR_BAR1_SUBJECTOBJECT_R4 = createAvo(VAR_BAR1_SUBJECTOBJECT, RESOURCE_4);

    private static Map<Attribute, ValueOperation> createAvo(final Attribute attribute, final Node node) {
        return new HashMap<Attribute, ValueOperation>() {
            {
                put(attribute, new ValueOperationImpl(node, EQUALS));
            }
        };
    }

    private static Map<Attribute, ValueOperation> createAvo(final Attribute attribute, final Node node,
        final AVPOperation operation) {
        return new HashMap<Attribute, ValueOperation>() {
            {
                put(attribute, new ValueOperationImpl(node, operation));
            }
        };
    }

    public static Set<Tuple> createASingleTuple(Map<Attribute, ValueOperation>... avos) {
        final Set<Tuple> tuples = new TreeSet<Tuple>(FACTORY.getNewTupleComparator());
        final TupleFactory tupleFactory = new TupleFactoryImpl();
        final Map<Attribute, ValueOperation> allAvos = new HashMap<Attribute, ValueOperation>();
        for (Map<Attribute, ValueOperation> avo : avos) {
            allAvos.putAll(avo);
        }
        tuples.add(tupleFactory.getTuple(allAvos));
        return tuples;
    }

    public static List<Relation> createRelation(Set<Tuple>... tuples) {
        List<Relation> relations = new ArrayList<Relation>();
        for (Set<Tuple> tuple : tuples) {
            Relation relation = createRelation(tuple);
            relations.add(relation);
        }
        return relations;
    }

    public static Relation createRelation(Set<Tuple> newTuples) {
        return RELATION_FACTORY.getRelation(newTuples);
    }

    public static Set<Attribute> createHeading(Attribute... attributes) {
        Set<Attribute> newAttributes = new TreeSet<Attribute>(ATTRIBUTE_COMPARATOR);
        for (Attribute attribute : attributes) {
            newAttributes.add(attribute);
        }
        return newAttributes;
    }

    @SuppressWarnings({"unchecked"})
    public static Relation createEmptyRelation(Set<Attribute> newAttributes) {
        RelationFactory rf = new RelationFactoryImpl(ATTRIBUTE_COMPARATOR, FACTORY.getNewTupleComparator());
        return rf.getRelation(newAttributes, Collections.EMPTY_SET);
    }

    public static Relation createRelation(Set<Attribute> attributes, Set<Tuple> tuples) {
        AttributeComparator attributeComparator = FACTORY.getNewAttributeComparator();
        RelationFactory rf = new RelationFactoryImpl(attributeComparator, FACTORY.getNewTupleComparator());
        return rf.getRelation(attributes, tuples);
    }

    public static Set<Relation> createRelations(Relation... relations) {
        Set<Relation> tuples = new HashSet<Relation>();
        for (Relation relation : relations) {
            tuples.add(relation);
        }
        return tuples;
    }
}
