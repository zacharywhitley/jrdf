/*
 * $Header$
 * $Revision$
 * $Date$
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
package org.jrdf.query.relation.mem;

import junit.framework.TestCase;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.PositionName;
import org.jrdf.query.relation.attributename.VariableName;
import org.jrdf.query.relation.type.BlankNodeType;
import org.jrdf.query.relation.type.LiteralNodeType;
import org.jrdf.query.relation.type.NodeType;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkMarkedAsSerializable;
import static org.jrdf.util.test.FieldPropertiesTestUtil.checkFieldIsOfTypeAndPrivate;
import static org.jrdf.util.test.ReflectTestUtil.checkFieldValue;
import static org.jrdf.util.test.EqualsHashCodeTestUtil.assertEquality;

import java.lang.reflect.Modifier;

/**
 * Test for attribute implementation.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public class AttributeImplUnitTest extends TestCase {
    private static final String ATTRIBUTE_NAME = "attributeName";
    private static final String TYPE_NAME = "type";
    private static final AttributeName TEST_NAME_FOO_POS = new PositionName("foo");
    private static final AttributeName TEST_NAME_BAR_VAR = new VariableName("bar");
    private static final AttributeName TEST_NAME_BAZ_VAR = new VariableName("baz");
    private static final NodeType LITERAL_TYPE = new LiteralNodeType();
    private static final NodeType BLANK_NODE_TYPE = new BlankNodeType();

    public static final Attribute TEST_ATTRIBUTE_FOO_POS = new AttributeImpl(TEST_NAME_FOO_POS, LITERAL_TYPE);
    public static final Attribute TEST_ATTRIBUTE_BAR_VAR = new AttributeImpl(TEST_NAME_BAR_VAR, BLANK_NODE_TYPE);
    public static final Attribute TEST_ATTRIBUTE_BAZ_VAR = new AttributeImpl(TEST_NAME_BAZ_VAR, BLANK_NODE_TYPE);

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(Attribute.class, AttributeImpl.class);
        checkMarkedAsSerializable(Attribute.class);
        checkConstructor(AttributeImpl.class, Modifier.PUBLIC, AttributeName.class, NodeType.class);
        checkFieldIsOfTypeAndPrivate(AttributeImpl.class, AttributeName.class, ATTRIBUTE_NAME);
        checkFieldIsOfTypeAndPrivate(AttributeImpl.class, NodeType.class, TYPE_NAME);
    }

    public void testConstructor() {
        checkStandardConstructor(TEST_NAME_FOO_POS, LITERAL_TYPE);
        checkStandardConstructor(TEST_NAME_BAR_VAR, BLANK_NODE_TYPE);
    }

    public void testEquals() {
        Attribute original = TEST_ATTRIBUTE_FOO_POS;
        Attribute equal = TEST_ATTRIBUTE_FOO_POS;
        Attribute notEqual = TEST_ATTRIBUTE_BAR_VAR;
        assertEquality(original, equal, notEqual, null);
    }

    private void checkStandardConstructor(AttributeName attributeName, NodeType type) {
        Attribute attribute = new AttributeImpl(attributeName, type);
        checkFieldValue(attribute, ATTRIBUTE_NAME, attributeName);
        checkFieldValue(attribute, TYPE_NAME, type);
        assertEquals(attributeName, attribute.getAttributeName());
        assertEquals(type, attribute.getType());
    }
}