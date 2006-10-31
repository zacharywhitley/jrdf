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

import com.gargoylesoftware.base.testing.EqualsTester;
import junit.framework.TestCase;
import static org.jrdf.graph.AnyNode.ANY_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Node;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeValuePair;
import static org.jrdf.query.relation.mem.AttributeImplUnitTest.TEST_ATTRIBUTE_BAR_VAR;
import static org.jrdf.query.relation.mem.AttributeImplUnitTest.TEST_ATTRIBUTE_FOO_POS;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkMarkedAsSerializable;
import static org.jrdf.util.test.FieldPropertiesTestUtil.checkFieldIsOfTypeAndPrivate;
import static org.jrdf.util.test.ReflectTestUtil.checkFieldValue;

import java.lang.reflect.Modifier;

/**
 * Test for attribute value pair implementation.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public class AttributeValuePairImplUnitTest extends TestCase {
    private static final String ATTRIBUTE_NAME = "attribute";
    private static final String VALUE_NAME = "value";

    public static final AttributeValuePair TEST_ATTRIBUTE_VALUE_1 =
        new AttributeValuePairImpl(TEST_ATTRIBUTE_FOO_POS, ANY_NODE);
    public static final AttributeValuePair TEST_ATTRIBUTE_VALUE_2 =
        new AttributeValuePairImpl(TEST_ATTRIBUTE_BAR_VAR, ANY_SUBJECT_NODE);

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(AttributeValuePair.class, AttributeValuePairImpl.class);
        checkMarkedAsSerializable(AttributeValuePair.class);
        checkConstructor(AttributeValuePairImpl.class, Modifier.PUBLIC, Attribute.class, Node.class);
        checkFieldIsOfTypeAndPrivate(AttributeValuePairImpl.class, Attribute.class, ATTRIBUTE_NAME);
        checkFieldIsOfTypeAndPrivate(AttributeValuePairImpl.class, Node.class, VALUE_NAME);
    }

    public void testConstructor() {
        checkStandardConstructor(TEST_ATTRIBUTE_FOO_POS, ANY_NODE);
        checkStandardConstructor(TEST_ATTRIBUTE_BAR_VAR, ANY_SUBJECT_NODE);
    }

    public void testEquals() {
        AttributeValuePair original = TEST_ATTRIBUTE_VALUE_1;
        AttributeValuePair equal = TEST_ATTRIBUTE_VALUE_1;
        AttributeValuePair notEqual = TEST_ATTRIBUTE_VALUE_2;
        new EqualsTester(original, equal, notEqual, null);
    }
    
    private void checkStandardConstructor(Attribute attributeName, Node node) {
        AttributeValuePair attributeValuePair = new AttributeValuePairImpl(attributeName, node);
        checkFieldValue(attributeValuePair, ATTRIBUTE_NAME, attributeName);
        checkFieldValue(attributeValuePair, VALUE_NAME, node);
        assertEquals(attributeName, attributeValuePair.getAttribute());
        assertEquals(node, attributeValuePair.getValue());
    }
}
