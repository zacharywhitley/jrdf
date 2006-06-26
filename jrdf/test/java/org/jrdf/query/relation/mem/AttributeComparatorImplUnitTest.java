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
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.AttributeNameComparator;
import org.jrdf.query.relation.type.Type;
import org.jrdf.query.relation.type.TypeComparator;
import org.jrdf.util.test.AssertThrows;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.util.test.MockFactory;
import org.jrdf.util.test.MockTestUtil;
import org.easymock.classextension.IMocksControl;

import java.lang.reflect.Modifier;

/**
 * Test AttributeComparator.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public class AttributeComparatorImplUnitTest extends TestCase {
    private static final Attribute ATTRIBUTE = MockTestUtil.createMock(Attribute.class);
    private static final TypeComparator NODE_COMPARATOR = MockTestUtil.createMock(TypeComparator.class);
    private static final AttributeNameComparator ATTRIBUTE_NAME_COMPARATOR
            = MockTestUtil.createMock(AttributeNameComparator.class);
    private MockFactory mockFactory;
    private static final int BEFORE = 1;
    private static final int AFTER = -1;

    public void setUp() {
        mockFactory = new MockFactory();
    }

    // TODO (AN) These next three methods could become some sort of comparator test util.
    public void testClassProperties() {
        ClassPropertiesTestUtil
                .checkImplementationOfInterfaceAndFinal(AttributeComparator.class, AttributeComparatorImpl.class);
        ClassPropertiesTestUtil.checkConstructor(AttributeComparatorImpl.class, Modifier.PUBLIC,
                TypeComparator.class, AttributeNameComparator.class);
    }

    public void testNullPointerException() {
        checkNullPointerException(createComparator(NODE_COMPARATOR, ATTRIBUTE_NAME_COMPARATOR), ATTRIBUTE, null);
        checkNullPointerException(createComparator(NODE_COMPARATOR, ATTRIBUTE_NAME_COMPARATOR), null, ATTRIBUTE);
    }

    public void testIdentity() {
        AttributeComparator comparator = createComparator(NODE_COMPARATOR, ATTRIBUTE_NAME_COMPARATOR);
        Attribute att2 = new TestAttribute();
        TestAttribute att1 = new TestAttribute(att2);
        int result = comparator.compare(att1, att2);
        assertTrue("Should return equal for att1, att2", result == 0);
        assertTrue("Should call att1.equals(att2)", att1.isEqualsCalled());

    }

    public void testNodeTypeComparator() {
        checkNodeTypeComparator(BEFORE);
        checkNodeTypeComparator(AFTER);
    }

    // TODO (AN) Ensure that it's Serializable - as the set won't be if the Comparator isn't.
    // TODO (AN) Finish testAttributeNameComparator

    private void checkNodeTypeComparator(int expectedResult) {
        Type t1 = MockTestUtil.createMock(Type.class);
        Type t2 = MockTestUtil.createMock(Type.class);
        Attribute attribute1 = createAttribute(t1);
        Attribute attribute2 = createAttribute(t2);
        TypeComparator typeComparator = createTypeComparator(t1, t2, expectedResult);
        AttributeComparator comparator = createComparator(typeComparator, ATTRIBUTE_NAME_COMPARATOR);
        mockFactory.replay();
        int result = comparator.compare(attribute1, attribute2);
        mockFactory.verify();
        mockFactory.reset();
        assertEquals(expectedResult, result);
    }

    private Attribute createAttribute(Type type) {
        IMocksControl control = mockFactory.createControl();
        Attribute att = control.createMock(Attribute.class);
        att.getType();
        control.andReturn(type);
        return att;
    }

    private TypeComparator createTypeComparator(Type t1, Type t2, int expectedResult) {
        IMocksControl control = mockFactory.createControl();
        TypeComparator typeComparator = control.createMock(TypeComparator.class);
        typeComparator.compare(t1, t2);
        control.andReturn(expectedResult);
        return typeComparator;
    }

    private AttributeComparator createComparator(TypeComparator nodeComparator,
            AttributeNameComparator attributeNameComparator) {
        return new AttributeComparatorImpl(nodeComparator, attributeNameComparator);
    }

    // TODO (AN) Duplication with other comparator tests
    private void checkNullPointerException(final AttributeComparator attComparator, final Attribute att,
            final Attribute att2) {
        AssertThrows.assertThrows(NullPointerException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                //noinspection unchecked
                attComparator.compare(att, att2);
            }
        });
    }

    private static class TestAttribute implements Attribute {
        private final Attribute expectedObject;
        private boolean correctObjectCalled;

        public TestAttribute() {
            this.expectedObject = MockTestUtil.createMock(Attribute.class);
        }

        public TestAttribute(Attribute expectedObject) {
            this.expectedObject = expectedObject;
        }

        public AttributeName getAttributeName() {
            throw new UnsupportedOperationException();
        }

        public Type getType() {
            throw new UnsupportedOperationException();
        }

        public boolean isEqualsCalled() {
            return correctObjectCalled;
        }

        public int hashCode() {
            return 1;
        }

        public boolean equals(Object obj) {
            if (expectedObject == obj) {
                correctObjectCalled = true;
            }
            return correctObjectCalled;
        }
    }
}