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

import junit.framework.TestCase;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import static org.jrdf.query.relation.mem.AttributeImplUnitTest.TEST_ATTRIBUTE_1;
import static org.jrdf.query.relation.mem.AttributeImplUnitTest.TEST_ATTRIBUTE_2;
import static org.jrdf.query.relation.mem.TupleImplUnitTest.TEST_TUPLE_1;
import static org.jrdf.query.relation.mem.TupleImplUnitTest.TEST_TUPLE_2;
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
public class RelationImplUnitTest extends TestCase {
    private static final Attribute[] ATTRIBUTES_1 = new Attribute[]{TEST_ATTRIBUTE_1};
    private static final Attribute[] ATTRIBUTES_2 = new Attribute[]{TEST_ATTRIBUTE_2};
    private static final Tuple[] TUPLES_1 = new Tuple[]{TEST_TUPLE_1};
    private static final Tuple[] TUPLES_2 = new Tuple[]{TEST_TUPLE_2};
    private static final String HEADING_NAME = "heading";
    private static final String TUPLES_NAME = "tuples";

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(Relation.class, RelationImpl.class);
        checkConstructor(RelationImpl.class, Modifier.PUBLIC, Set.class, Set.class);
        checkFieldPrivate(RelationImpl.class, HEADING_NAME);
        isFieldOfType(RelationImpl.class, HEADING_NAME, Set.class);
        checkFieldPrivate(RelationImpl.class, TUPLES_NAME);
        isFieldOfType(RelationImpl.class, TUPLES_NAME, Set.class);
    }

    public void testConstructor() {
        checkStandardConstructor(createHeading(ATTRIBUTES_1), createTuple(TUPLES_1));
        checkStandardConstructor(createHeading(ATTRIBUTES_2), createTuple(TUPLES_2));
    }

    private void checkStandardConstructor(Set<Attribute> heading, Set<Tuple> tuples) {
        Relation relation = new RelationImpl(heading, tuples);
        checkFieldValue(relation, HEADING_NAME, heading);
        checkFieldValue(relation, TUPLES_NAME, tuples);
        assertEquals(heading, relation.getHeading());
        assertEquals(tuples, relation.getTuples());
    }

    private Set<Attribute> createHeading(Attribute[] attributes) {
        //noinspection unchecked
        return new HashSet(Arrays.asList(attributes));
    }

    private Set<Tuple> createTuple(Tuple[] tuples) {
        //noinspection unchecked
        return new HashSet(Arrays.asList(tuples));
    }
}
