/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003, 2004 The JRDF Project.  All rights reserved.
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
package org.jrdf.query.relation.mem;

import au.net.netstorm.boost.primordial.Primordial;
import au.net.netstorm.boost.test.reflect.DefaultReflectTestUtil;
import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import org.jrdf.JRDFFactory;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.AttributeValuePairComparator;
import org.jrdf.query.relation.Tuple;
import static org.jrdf.query.relation.mem.AttributeValuePairComparatorImplIntegrationTest.TEST_AVP_1;
import static org.jrdf.query.relation.mem.AttributeValuePairComparatorImplIntegrationTest.TEST_AVP_2;
import static org.jrdf.query.relation.mem.AttributeValuePairComparatorImplIntegrationTest.TEST_AVP_3;
import static org.jrdf.query.relation.mem.AttributeValuePairComparatorImplIntegrationTest.TEST_AVP_4;
import static org.jrdf.query.relation.mem.AttributeValuePairUnitTest.TEST_ATTRIBUTE_VALUE_1;
import static org.jrdf.query.relation.mem.AttributeValuePairUnitTest.TEST_ATTRIBUTE_VALUE_2;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import static org.jrdf.util.test.FieldPropertiesTestUtil.checkFieldPrivate;
import static org.jrdf.util.test.FieldPropertiesTestUtil.isFieldOfType;
import static org.jrdf.util.test.ReflectTestUtil.checkFieldValue;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Test for tuple implementation.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public class TupleImplUnitTest extends TestCase {
    private static final JRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final AttributeValuePairComparator comparator = FACTORY.getNewAttributeValuePairComparator();

    private static final AttributeValuePair[] TEST_ATTRIBUTE_VALUE_PAIRS_1 = {TEST_ATTRIBUTE_VALUE_1};
    private static final AttributeValuePair[] TEST_ATTRIBUTE_VALUE_PAIRS_2 = {TEST_ATTRIBUTE_VALUE_1,
        TEST_ATTRIBUTE_VALUE_2};
    private static final AttributeValuePair[] ATTRIBUTE_VALUE_PAIRS_1 = {TEST_AVP_1};
    private static final AttributeValuePair[] ATTRIBUTE_VALUE_PAIRS_2 = {TEST_AVP_2};
    private static final AttributeValuePair[] ATTRIBUTE_VALUE_PAIRS_3 = {TEST_AVP_3};
    private static final AttributeValuePair[] ATTRIBUTE_VALUE_PAIRS_4 = {TEST_AVP_4};
    private static final AttributeValuePair[] ATTRIBUTE_VALUE_PAIRS_1_2 = {TEST_AVP_1, TEST_AVP_2};
    private static final AttributeValuePair[] ATTRIBUTE_VALUE_PAIRS_1_3 = {TEST_AVP_1, TEST_AVP_3};
    private static final AttributeValuePair[] ATTRIBUTE_VALUE_PAIRS_3_1 = {TEST_AVP_3, TEST_AVP_1};

    private static final Set<AttributeValuePair> ATTRIBUTE_VALUE_SET_1 = createSet(TEST_ATTRIBUTE_VALUE_PAIRS_1);
    private static final Set<AttributeValuePair> ATTRIBUTE_VALUE_SET_2 = createSet(TEST_ATTRIBUTE_VALUE_PAIRS_2);
    private static final Set<AttributeValuePair> ATTRIBUTE_VALUE_SET_3 = createSet(ATTRIBUTE_VALUE_PAIRS_1);
    private static final Set<AttributeValuePair> ATTRIBUTE_VALUE_SET_4 = createSet(ATTRIBUTE_VALUE_PAIRS_2);
    private static final Set<AttributeValuePair> ATTRIBUTE_VALUE_SET_5 = createSet(ATTRIBUTE_VALUE_PAIRS_3);
    private static final Set<AttributeValuePair> ATTRIBUTE_VALUE_SET_6 = createSet(ATTRIBUTE_VALUE_PAIRS_4);
    private static final Set<AttributeValuePair> ATTRIBUTE_VALUE_SET_1_2 = createSet(ATTRIBUTE_VALUE_PAIRS_1_2);
    private static final Set<AttributeValuePair> ATTRIBUTE_VALUE_SET_1_3 = createSet(ATTRIBUTE_VALUE_PAIRS_1_3);
    private static final Set<AttributeValuePair> ATTRIBUTE_VALUE_SET_3_1 = createSet(ATTRIBUTE_VALUE_PAIRS_3_1);
    private static final String TUPLES_NAME = "attributeValues";

    public static final Tuple TEST_TUPLE_1 = new TupleImpl(ATTRIBUTE_VALUE_SET_1, comparator);
    public static final Tuple TEST_TUPLE_2 = new TupleImpl(ATTRIBUTE_VALUE_SET_2, comparator);
    public static final Tuple TEST_TUPLE_3 = new TupleImpl(ATTRIBUTE_VALUE_SET_3, comparator);
    public static final Tuple TEST_TUPLE_4 = new TupleImpl(ATTRIBUTE_VALUE_SET_4, comparator);
    public static final Tuple TEST_TUPLE_5 = new TupleImpl(ATTRIBUTE_VALUE_SET_5, comparator);
    public static final Tuple TEST_TUPLE_6 = new TupleImpl(ATTRIBUTE_VALUE_SET_6, comparator);
    public static final Tuple TEST_TUPLES_1_2 = new TupleImpl(ATTRIBUTE_VALUE_SET_1_2, comparator);
    public static final Tuple TEST_TUPLES_1_3 = new TupleImpl(ATTRIBUTE_VALUE_SET_1_3, comparator);
    public static final Tuple TEST_TUPLES_3_1 = new TupleImpl(ATTRIBUTE_VALUE_SET_3_1, comparator);

    public void testClassProperties() {
        new DefaultReflectTestUtil().isSubclassOf(Primordial.class, RelationImpl.class);
        checkImplementationOfInterfaceAndFinal(Tuple.class, TupleImpl.class);
        checkConstructor(TupleImpl.class, Modifier.PUBLIC, Set.class, AttributeValuePairComparator.class);
        checkFieldPrivate(TupleImpl.class, TUPLES_NAME);
        isFieldOfType(TupleImpl.class, TUPLES_NAME, Set.class);
    }

    public void testConstructor() {
        checkStandardConstructor(ATTRIBUTE_VALUE_SET_1);
        checkStandardConstructor(ATTRIBUTE_VALUE_SET_2);
    }

    private void checkStandardConstructor(Set<AttributeValuePair> tupleSet) {
        Tuple tuple = new TupleImpl(tupleSet, comparator);
        checkFieldValue(tuple, TUPLES_NAME, tupleSet);
        assertEquals(tupleSet, tuple.getAttributeValues());
    }

    private static Set<AttributeValuePair> createSet(AttributeValuePair[] attributeValuePair) {
        //noinspection unchecked
        return new HashSet(Arrays.asList(attributeValuePair));
    }

}
