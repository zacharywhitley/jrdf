/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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
package org.jrdf.query.relation.mem;

import junit.framework.TestCase;
import org.jrdf.JRDFFactory;
import org.jrdf.TestJRDFFactory;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.TupleComparator;
import static org.jrdf.query.relation.mem.TupleImplUnitTest.TEST_TUPLE_3;
import static org.jrdf.query.relation.mem.TupleImplUnitTest.TEST_TUPLE_4;
import static org.jrdf.query.relation.mem.TupleImplUnitTest.TEST_TUPLE_6;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructorSetsFieldsAndFieldsPrivate;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import static org.jrdf.util.test.FieldPropertiesTestUtil.checkFieldPrivate;
import static org.jrdf.util.test.FieldPropertiesTestUtil.isFieldOfType;
import static org.jrdf.util.test.ReflectTestUtil.checkFieldValue;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class RelationImplUnitTest extends TestCase {
    private static final Tuple[] TUPLES_1 = {TEST_TUPLE_3};
    private static final Tuple[] TUPLES_2 = {TEST_TUPLE_4};
    private static final Tuple[] TUPLES_3 = {TEST_TUPLE_6};
    private static final String HEADING_NAME = "heading";
    private static final String TUPLES_NAME = "tuples";
    private static final JRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final AttributeComparator ATTRIBUTE_COMPARATOR = FACTORY.getNewAttributeComparator();
    private static final TupleComparator TUPLE_COMPARATOR = FACTORY.getNewTupleComparator();
    private static final Class[] PARAM_TYPES_1 = {Set.class, AttributeComparator.class, TupleComparator.class};
    private static final Class[] PARAM_TYPES_2 = {Set.class, Set.class, AttributeComparator.class, TupleComparator.class};
    private static final String[] PARAM_NAMES_1 = {"tuples", "attributeComparator", "tupleComparator"};
    private static final String[] PARAM_NAMES_2 = {"heading", "tuples", "attributeComparator", "tupleComparator"};

    public static final Relation TEST_RELATION_1 = createRelation(createTuple(TUPLES_1));
    public static final Relation TEST_RELATION_2 = createRelation(createTuple(TUPLES_2));
    public static final Relation TEST_RELATION_3 = createRelation(createTuple(TUPLES_3));

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(Relation.class, RelationImpl.class);
        checkConstructor(RelationImpl.class, 0, PARAM_TYPES_1);
        checkConstructor(RelationImpl.class, 0, PARAM_TYPES_2);
    }

    public void testConstructorAndFields() {
        checkConstructNullAssertion(RelationImpl.class, PARAM_TYPES_1);
        checkConstructNullAssertion(RelationImpl.class, PARAM_TYPES_2);
        checkConstructorSetsFieldsAndFieldsPrivate(RelationImpl.class, PARAM_TYPES_1, PARAM_NAMES_1);
        checkConstructorSetsFieldsAndFieldsPrivate(RelationImpl.class, PARAM_TYPES_2, PARAM_NAMES_2);
        checkFieldPrivate(RelationImpl.class, HEADING_NAME);
        isFieldOfType(RelationImpl.class, Set.class, HEADING_NAME);
    }

    public void testConstructor() {
        checkStandardConstructor(createHeading(TUPLES_1), createTuple(TUPLES_1), TEST_RELATION_1);
        checkStandardConstructor(createHeading(TUPLES_2), createTuple(TUPLES_2), TEST_RELATION_2);
    }

    private void checkStandardConstructor(Set<Attribute> heading, Set<Tuple> tuples, Relation relation) {
        checkFieldValue(relation, HEADING_NAME, heading);
        checkFieldValue(relation, TUPLES_NAME, tuples);
        assertEquals(heading, relation.getHeading());
        assertEquals(tuples, relation.getTuples());
    }

    private static Set<Attribute> createHeading(Tuple[] tuples) {
        Set<Attribute> heading = new TreeSet<Attribute>(FACTORY.getNewAttributeComparator());
        //noinspection unchecked
        for (Tuple tuple : tuples) {
            Set<AttributeValuePair> sortedAttributeValues = tuple.getSortedAttributeValues();
            for (AttributeValuePair avPair : sortedAttributeValues) {
                heading.add(avPair.getAttribute());
            }
        }
        return heading;
    }

    private static Set<Tuple> createTuple(Tuple[] tuples) {
        //noinspection unchecked
        Set<Tuple> sortedTuples = new TreeSet<Tuple>(FACTORY.getNewTupleComparator());
        sortedTuples.addAll(Arrays.asList(tuples));
        return sortedTuples;
    }

    private static Relation createRelation(Set<Tuple> newTuples) {
        return new RelationImpl(newTuples, ATTRIBUTE_COMPARATOR, TUPLE_COMPARATOR);
    }
}
