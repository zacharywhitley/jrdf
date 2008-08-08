/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.jrdf.query.expression;

import junit.framework.TestCase;
import static org.jrdf.graph.AnyNode.ANY_NODE;
import static org.jrdf.graph.AnySubjectNode.ANY_SUBJECT_NODE;
import org.jrdf.graph.Node;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.ValueOperation;
import static org.jrdf.query.relation.mem.AttributeImplUnitTest.TEST_ATTRIBUTE_BAR_VAR;
import static org.jrdf.query.relation.mem.AttributeImplUnitTest.TEST_ATTRIBUTE_FOO_POS;
import org.jrdf.query.relation.mem.EqAVPOperation;
import org.jrdf.query.relation.mem.ValueOperationImpl;
import static org.jrdf.util.test.ArgumentTestUtil.checkConstructNullAssertion;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkInstanceImplementsInterface;
import static org.jrdf.util.test.SerializationTestUtil.checkSerialialVersionUid;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Unit test for {@link SingleConstraint}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
@SuppressWarnings({"unchecked"})
public final class SingleConstraintUnitTest extends TestCase {
    private static final LinkedHashMap<Attribute, ValueOperation> AVO_1 =
        createAvoMap(TEST_ATTRIBUTE_FOO_POS, ANY_NODE);
    private static final LinkedHashMap<Attribute, ValueOperation> AVO_2 =
        createAvoMap(TEST_ATTRIBUTE_BAR_VAR, ANY_SUBJECT_NODE);
    private static final SingleConstraint<ExpressionVisitor> CONSTRAINT_TRIPLE_1 =
        new SingleConstraint<ExpressionVisitor>(AVO_1);
    private static final SingleConstraint<ExpressionVisitor> CONSTRAINT_TRIPLE_2 =
        new SingleConstraint<ExpressionVisitor>(AVO_2);
    private static final Class<?>[] PARAM_TYPES = {LinkedHashMap.class};

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(Expression.class, SingleConstraint.class);
        checkInstanceImplementsInterface(Serializable.class, SingleConstraint.class);
        checkConstructor(SingleConstraint.class, Modifier.PUBLIC, PARAM_TYPES);
    }

    public void testSerialVersionUID() {
        checkSerialialVersionUid(SingleConstraint.class, 4538228991602138679L);
    }

    public void testNullToConstructorThrowsException() {
        checkConstructNullAssertion(SingleConstraint.class, PARAM_TYPES);
    }

    public void getAvp() {
        SingleConstraint constraint = new SingleConstraint<ExpressionVisitor>(AVO_1);
        assertEquals(AVO_1, constraint.getAvo(Collections.EMPTY_MAP));
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
        checkToStringDelegatesToTriple(AVO_1, CONSTRAINT_TRIPLE_1);
        checkToStringDelegatesToTriple(AVO_2, CONSTRAINT_TRIPLE_2);
    }

    private void checkNull() {
        checkNotEquals(CONSTRAINT_TRIPLE_1, null);
    }

    private void checkReflexive() {
        checkSameValueSameReference();
        checkSameValueDifferentReference();
    }

    private void checkSameValueSameReference() {
        SingleConstraint x = CONSTRAINT_TRIPLE_1;
        SingleConstraint y = x;
        checkEquals(x, y);
    }

    private void checkSameValueDifferentReference() {
        SingleConstraint x = new SingleConstraint<ExpressionVisitor>(AVO_1);
        SingleConstraint y = new SingleConstraint<ExpressionVisitor>(AVO_1);
        checkEquals(x, y);
    }

    private void checkDifferentClass() {
        checkNotEquals(CONSTRAINT_TRIPLE_1, AVO_1);
    }

    private void checkSymmetric() {
        SingleConstraint x = new SingleConstraint<ExpressionVisitor>(AVO_1);
        SingleConstraint y = new SingleConstraint<ExpressionVisitor>(AVO_1);
        checkEquals(x, y);
        checkEquals(y, y);
    }

    private void checkTransitive() {
        SingleConstraint x = new SingleConstraint<ExpressionVisitor>(AVO_1);
        SingleConstraint y = new SingleConstraint<ExpressionVisitor>(AVO_1);
        SingleConstraint z = new SingleConstraint<ExpressionVisitor>(AVO_1);
        checkEquals(x, y);
        checkEquals(y, z);
        checkEquals(x, z);
    }

    private void checkConsistentEquals() {
        SingleConstraint x = new SingleConstraint<ExpressionVisitor>(AVO_1);
        SingleConstraint y = new SingleConstraint<ExpressionVisitor>(AVO_1);
        checkEquals(x, y);
        checkEquals(x, y);
    }

    private void checkConsistentHashCode() {
        int hashCode1 = CONSTRAINT_TRIPLE_1.hashCode();
        int hashCode2 = CONSTRAINT_TRIPLE_1.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    private void checkEqualObjectsReturnSameHashCode() {
        SingleConstraint x = new SingleConstraint<ExpressionVisitor>(AVO_1);
        SingleConstraint y = new SingleConstraint<ExpressionVisitor>(AVO_1);
        checkEquals(x, y);
        assertEquals(x.hashCode(), y.hashCode());
    }

    private void checkUnequal() {
        checkNotEquals(CONSTRAINT_TRIPLE_1, CONSTRAINT_TRIPLE_2);
    }

    private void checkEquals(SingleConstraint x, SingleConstraint y) {
        assertEquals(x, y);
    }

    private void checkNotEquals(Object x, Object y) {
        assertFalse(x.equals(y));
    }

    private void checkToStringDelegatesToTriple(Map<Attribute, ValueOperation> avp, SingleConstraint contraint) {
        assertEquals(avp.toString(), contraint.toString());
    }

    private static LinkedHashMap<Attribute, ValueOperation> createAvoMap(Attribute attribute, Node node) {
        LinkedHashMap<Attribute, ValueOperation> avo = new LinkedHashMap<Attribute, ValueOperation>();
        avo.put(attribute, new ValueOperationImpl(node, EqAVPOperation.EQUALS));
        return avo;
    }
}
