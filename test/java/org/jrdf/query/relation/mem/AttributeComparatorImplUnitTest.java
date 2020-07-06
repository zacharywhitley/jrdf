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

import static org.easymock.EasyMock.expectLastCall;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.attributename.AttributeName;
import org.jrdf.query.relation.attributename.AttributeNameComparator;
import org.jrdf.query.relation.type.NodeType;
import org.jrdf.query.relation.type.TypeComparator;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterface;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import static org.jrdf.util.test.ComparatorTestUtil.checkNullPointerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import static org.powermock.api.easymock.PowerMock.resetAll;
import static org.jrdf.util.test.MockTestUtil.createMock;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.Serializable;
import java.lang.reflect.Modifier;

/**
 * Test AttributeComparator.
 *
 * @author Andrew Newman
 * @version $Id$
 */
@RunWith(PowerMockRunner.class)
public class AttributeComparatorImplUnitTest {
    private static final int BEFORE = 1;
    private static final int AFTER = -1;
    @Mock private Attribute attribute;
    @Mock private TypeComparator nodeComparator;
    @Mock private AttributeNameComparator attributeNameComparator;

    // TODO (AN) Ensure that it's Serializable - as the collection won't be if the Comparator isn't.
    // TODO (AN) These next three methods could become some sort of comparator test util.
    @Test
    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(AttributeComparator.class, AttributeComparatorImpl.class);
        checkImplementationOfInterface(Serializable.class, AttributeComparator.class);
        checkConstructor(AttributeComparatorImpl.class, Modifier.PUBLIC,
            TypeComparator.class, AttributeNameComparator.class);
    }

    @Test
    public void testNullPointerException() {
        checkNullPointerException(newComparator(nodeComparator, attributeNameComparator), attribute, null);
        checkNullPointerException(newComparator(nodeComparator, attributeNameComparator), null, attribute);
    }

    @Test
    public void testIdentity() {
        AttributeComparator comparator = newComparator(nodeComparator, attributeNameComparator);
        Attribute att2 = new TestAttribute(createMock(Attribute.class));
        TestAttribute att1 = new TestAttribute(att2);
        int result = comparator.compare(att1, att2);
        assertThat("Should return equal for att1, att2", result == 0);
        assertThat("Should call att1.equals(att2)", att1.isEqualsCalled());
    }

    @Test
    public void testNodeTypeComparator() {
        checkNodeTypeComparator(BEFORE);
        checkNodeTypeComparator(AFTER);
    }

    // TODO (AN) Finish testAttributeNameComparator
    private void checkNodeTypeComparator(int expectedResult) {
        NodeType t1 = createMock(NodeType.class);
        NodeType t2 = createMock(NodeType.class);
        Attribute attribute1 = createAttribute(t1);
        Attribute attribute2 = createAttribute(t2);
        TypeComparator typeComparator = createTypeComparator(t1, t2, expectedResult);
        AttributeComparator comparator = newComparator(typeComparator, attributeNameComparator);
        replayAll();
        int result = comparator.compare(attribute1, attribute2);
        verifyAll();
        resetAll();
        assertThat(result, equalTo(expectedResult));
    }

    private Attribute createAttribute(NodeType type) {
        Attribute att = createMock(Attribute.class);
        att.getType();
        expectLastCall().andReturn(type);
        return att;
    }

    private TypeComparator createTypeComparator(NodeType t1, NodeType t2, int expectedResult) {
        TypeComparator typeComparator = createMock(TypeComparator.class);
        typeComparator.compare(t1, t2);
        expectLastCall().andReturn(expectedResult);
        return typeComparator;
    }

    private AttributeComparator newComparator(TypeComparator comparator, AttributeNameComparator nameComparator) {
        return new AttributeComparatorImpl(comparator, nameComparator);
    }

    private static class TestAttribute implements Attribute {
        private static final long serialVersionUID = 1;
        private Attribute expectedObject;
        private boolean correctObjectCalled;

        private TestAttribute() {
        }

        public TestAttribute(Attribute newExpectedObject) {
            this.expectedObject = newExpectedObject;
        }

        public AttributeName getAttributeName() {
            throw new UnsupportedOperationException();
        }

        public NodeType getType() {
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