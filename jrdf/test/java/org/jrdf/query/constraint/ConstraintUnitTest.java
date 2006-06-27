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

package org.jrdf.query.constraint;

import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import org.jrdf.query.relation.AttributeValuePair;
import org.jrdf.query.relation.AttributeValuePairComparator;
import static org.jrdf.query.relation.mem.AttributeValuePairImplUnitTest.TEST_ATTRIBUTE_VALUE_1;
import static org.jrdf.query.relation.mem.AttributeValuePairImplUnitTest.TEST_ATTRIBUTE_VALUE_2;
import org.jrdf.util.test.AssertThrows;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import org.jrdf.util.test.SerializationTestUtil;

import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Unit test for {@link Constraint}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class ConstraintUnitTest extends TestCase {
    private static final SortedSet<AttributeValuePair> AVP_1 =  createAvpSet(TEST_ATTRIBUTE_VALUE_1);
    private static final SortedSet<AttributeValuePair> AVP_2 =  createAvpSet(TEST_ATTRIBUTE_VALUE_2);
    private static final Constraint CONSTRAINT_TRIPLE_1 = new Constraint<ExpressionVisitor>(AVP_1);
    private static final Constraint CONSTRAINT_TRIPLE_2 = new Constraint<ExpressionVisitor>(AVP_2);
    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final AttributeValuePairComparator AVP_COMPARATOR = FACTORY.getNewAttributeValuePairComparator();

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(ConstraintExpression.class, Constraint.class);
        checkConstructor(Constraint.class, Modifier.PUBLIC, SortedSet.class);
    }

    public void testSerialVersionUID() {
        SerializationTestUtil.checkSerialialVersionUid(Constraint.class, 4538228991602138679L);
    }

    public void testNullToConstructorThrowsException() {
        AssertThrows.assertThrows(IllegalArgumentException.class, new AssertThrows.Block() {
            public void execute() throws Throwable {
                new Constraint<ExpressionVisitor>(null);
            }
        });
    }

    public void getAvp() {
        Constraint constraint = new Constraint<ExpressionVisitor>(AVP_1);
        assertEquals(AVP_1, constraint.getAvp());
    }

    private static SortedSet<AttributeValuePair> createAvpSet(AttributeValuePair testAttributeValue) {
        SortedSet<AttributeValuePair> set = new TreeSet<AttributeValuePair>(AVP_COMPARATOR);
        set.add(testAttributeValue);
        return set;
    }

    public void testEquals() {
        checkNull();
        checkReflexive();
        checkDifferentClass();
        checkSymmetric();
        checkTransitive();
        checkConsistentEquals();
        checkUnequal();
    }

    public void testHashCode() {
        checkConsistentHashCode();
        checkEqualObjectsReturnSameHashCode();
    }

    public void testToString() {
        checkToStringDelegatesToTriple(AVP_1, CONSTRAINT_TRIPLE_1);
        checkToStringDelegatesToTriple(AVP_2, CONSTRAINT_TRIPLE_2);
    }

    private void checkNull() {
        checkNotEquals(CONSTRAINT_TRIPLE_1, null);
    }

    private void checkReflexive() {
        checkSameValueSameReference();
        checkSameValueDifferentReference();
    }

    private void checkSameValueSameReference() {
        Constraint x = CONSTRAINT_TRIPLE_1;
        Constraint y = x;
        checkEquals(x, y);
    }

    private void checkSameValueDifferentReference() {
        Constraint x = new Constraint<ExpressionVisitor>(AVP_1);
        Constraint y = new Constraint<ExpressionVisitor>(AVP_1);
        checkEquals(x, y);
    }

    private void checkDifferentClass() {
        checkNotEquals(CONSTRAINT_TRIPLE_1, AVP_1);
    }

    private void checkSymmetric() {
        Constraint x = new Constraint<ExpressionVisitor>(AVP_1);
        Constraint y = new Constraint<ExpressionVisitor>(AVP_1);
        checkEquals(x, y);
        checkEquals(y, y);
    }

    private void checkTransitive() {
        Constraint x = new Constraint<ExpressionVisitor>(AVP_1);
        Constraint y = new Constraint<ExpressionVisitor>(AVP_1);
        Constraint z = new Constraint<ExpressionVisitor>(AVP_1);
        checkEquals(x, y);
        checkEquals(y, z);
        checkEquals(x, z);
    }

    private void checkConsistentEquals() {
        Constraint x = new Constraint<ExpressionVisitor>(AVP_1);
        Constraint y = new Constraint<ExpressionVisitor>(AVP_1);
        checkEquals(x, y);
        checkEquals(x, y);
    }

    private void checkConsistentHashCode() {
        int hashCode1 = CONSTRAINT_TRIPLE_1.hashCode();
        int hashCode2 = CONSTRAINT_TRIPLE_1.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    private void checkEqualObjectsReturnSameHashCode() {
        Constraint x = new Constraint<ExpressionVisitor>(AVP_1);
        Constraint y = new Constraint<ExpressionVisitor>(AVP_1);
        checkEquals(x, y);
        assertEquals(x.hashCode(), y.hashCode());
    }

    private void checkUnequal() {
        checkNotEquals(CONSTRAINT_TRIPLE_1, CONSTRAINT_TRIPLE_2);
    }

    private void checkEquals(Constraint x, Constraint y) {
        assertEquals(x, y);
    }

    private void checkNotEquals(Object x, Object y) {
        assertFalse(x.equals(y));
    }

    private void checkToStringDelegatesToTriple(Set<AttributeValuePair> avp, Constraint contraint) {
        assertEquals(avp.toString(), contraint.toString());
    }
}
