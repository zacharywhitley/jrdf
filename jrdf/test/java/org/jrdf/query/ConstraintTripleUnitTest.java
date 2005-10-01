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

package org.jrdf.query;

import junit.framework.TestCase;
import org.jrdf.graph.Triple;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.util.test.TripleTestUtil;

import java.lang.reflect.Modifier;

/**
 * Unit test for {@link ConstraintTriple}.
 * @author Tom Adams
 * @version $Revision$
 */
public final class ConstraintTripleUnitTest extends TestCase {

    private static final Triple TRIPLE_1 = TripleTestUtil.TRIPLE_BOOK_1_DC_TITLE_VARIABLE;
    private static final Triple TRIPLE_2 = TripleTestUtil.TRIPLE_BOOK_2_DC_TITLE_VARIABLE;
    private static final ConstraintTriple CONSTRAINT_TRIPLE_1 = new ConstraintTriple(TRIPLE_1);
    private static final ConstraintTriple CONSTRAINT_TRIPLE_2 = new ConstraintTriple(TRIPLE_2);

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(ConstraintExpression.class, ConstraintTriple.class);
        ClassPropertiesTestUtil.checkConstructor(ConstraintTriple.class, Modifier.PUBLIC, Triple.class);
    }

    public void testNullToConstructorThrowsException() {
        try {
            new ConstraintTriple(null);
            fail("null to constructor should have thrown IllegalArgumentException");
        }
        catch (IllegalArgumentException expected) {}
    }

    public void testGetTriple() {
        ConstraintTriple constraintTriple = new ConstraintTriple(TRIPLE_1);
        assertEquals(TRIPLE_1, constraintTriple.getTriple());
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
        checkToStringDelegatesToTriple(TRIPLE_1, CONSTRAINT_TRIPLE_1);
        checkToStringDelegatesToTriple(TRIPLE_2, CONSTRAINT_TRIPLE_2);
    }

    private void checkNull() {
        checkNotEquals(CONSTRAINT_TRIPLE_1, null);
    }

    private void checkReflexive() {
        checkSameValueSameReference();
        checkSameValueDifferentReference();
    }

    private void checkSameValueSameReference() {
        ConstraintTriple x = CONSTRAINT_TRIPLE_1;
        ConstraintTriple y = x;
        checkEquals(x, y);
    }

    private void checkSameValueDifferentReference() {
        ConstraintTriple x = new ConstraintTriple(TRIPLE_1);
        ConstraintTriple y = new ConstraintTriple(TRIPLE_1);
        checkEquals(x, y);
    }

    private void checkDifferentClass() {
        checkNotEquals(CONSTRAINT_TRIPLE_1, TRIPLE_1);
    }

    private void checkSymmetric() {
        ConstraintTriple x = new ConstraintTriple(TRIPLE_1);
        ConstraintTriple y = new ConstraintTriple(TRIPLE_1);
        checkEquals(x, y);
        checkEquals(y, y);
    }

    private void checkTransitive() {
        ConstraintTriple x = new ConstraintTriple(TRIPLE_1);
        ConstraintTriple y = new ConstraintTriple(TRIPLE_1);
        ConstraintTriple z = new ConstraintTriple(TRIPLE_1);
        checkEquals(x, y);
        checkEquals(y, z);
        checkEquals(x, z);
    }

    private void checkConsistentEquals() {
        ConstraintTriple x = new ConstraintTriple(TRIPLE_1);
        ConstraintTriple y = new ConstraintTriple(TRIPLE_1);
        checkEquals(x, y);
        checkEquals(x, y);
    }

    private void checkConsistentHashCode() {
        int hashCode1 = CONSTRAINT_TRIPLE_1.hashCode();
        int hashCode2 = CONSTRAINT_TRIPLE_1.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    private void checkEqualObjectsReturnSameHashCode() {
        ConstraintTriple x = new ConstraintTriple(TRIPLE_1);
        ConstraintTriple y = new ConstraintTriple(TRIPLE_1);
        checkEquals(x, y);
        assertEquals(x.hashCode(), y.hashCode());
    }

    private void checkUnequal() {
        checkNotEquals(CONSTRAINT_TRIPLE_1, CONSTRAINT_TRIPLE_2);
    }

    private void checkEquals(ConstraintTriple x, ConstraintTriple y) {
        assertEquals(x, y);
    }

    private void checkNotEquals(Object x, Object y) {
        assertFalse(x.equals(y));
    }

    private void checkToStringDelegatesToTriple(Triple triple, ConstraintTriple contraintTriple) {
        assertEquals(triple.toString(), contraintTriple.toString());
    }
}
