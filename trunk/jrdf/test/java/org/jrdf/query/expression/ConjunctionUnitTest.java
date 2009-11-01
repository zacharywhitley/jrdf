/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
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

package org.jrdf.query.expression;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.Matchers;
import org.jrdf.graph.Node;
import org.jrdf.query.relation.Attribute;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_BAR1_SUBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_BAR1_SUBJECT_R1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.RESOURCE_1;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.RESOURCE_3;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkInstanceImplementsInterface;
import static org.jrdf.util.test.EqualsHashCodeTestUtil.assertEquality;
import static org.jrdf.util.test.EqualsHashCodeTestUtil.assertHashCode;
import org.junit.Test;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Collections;

/**
 * Conjunction test case.
 *
 * @author Andrew Newman
 * @version $Revision:$
 */
public class ConjunctionUnitTest {
    private static final SingleValue LHS = new SingleValue(POS_BAR1_SUBJECT_R1);
    private static final SingleValue RHS = new SingleValue(POS_FOO3_OBJECT_R3);
    private static final EmptyExpression EMPTY_EXPRESSION = new EmptyExpression();

    @Test
    public void classProperties() {
        checkImplementationOfInterfaceAndFinal(Expression.class, Conjunction.class);
        checkInstanceImplementsInterface(Serializable.class, Conjunction.class);
        checkConstructor(Conjunction.class, Modifier.PUBLIC, Expression.class, Expression.class);
    }

    @Test
    public void getValuesWithEmpty() {
        final BiOperandExpression conj1 = new Conjunction(EMPTY_EXPRESSION, EMPTY_EXPRESSION);
        assertThat(conj1.getValue(), equalTo(Collections.<Attribute, Node>emptyMap()));
    }

    @Test
    public void getValuesCombinesValues() {
        final BiOperandExpression conj1 = new Conjunction(LHS, RHS);
        assertThat(conj1.getValue(), Matchers.<Attribute, Node>hasEntry(POS_BAR1_SUBJECT, RESOURCE_1));
        assertThat(conj1.getValue(), Matchers.<Attribute, Node>hasEntry(POS_FOO3_OBJECT, RESOURCE_3));
    }

    @Test
    public void sameValuesAreEqual() {
        BiOperandExpression conj1 = new Conjunction(EMPTY_EXPRESSION, EMPTY_EXPRESSION);
        BiOperandExpression conj2 = new Conjunction(EMPTY_EXPRESSION, EMPTY_EXPRESSION);
        BiOperandExpression unequalConj = new Conjunction(EMPTY_EXPRESSION, null);
        assertEquality(conj1, conj2, unequalConj, null);
        assertHashCode(conj1, conj2);

        conj1 = new Conjunction(LHS, RHS);
        conj2 = new Conjunction(LHS, RHS);
        assertEquality(conj1, conj2, unequalConj, null);
        assertHashCode(conj1, conj2);
    }

    @Test
    public void sameNullValuesAreEqual() {
        final BiOperandExpression conj1 = new Conjunction(EMPTY_EXPRESSION, null);
        final BiOperandExpression conj2 = new Conjunction(EMPTY_EXPRESSION, null);
        final BiOperandExpression unequalConj = new Conjunction(EMPTY_EXPRESSION, EMPTY_EXPRESSION);
        assertEquality(conj1, conj2, unequalConj, null);
        assertHashCode(conj1, conj2);
    }

    @Test
    public void sameNullValuesAreEqual2() {
        final BiOperandExpression conj1 = new Conjunction(null, EMPTY_EXPRESSION);
        final BiOperandExpression conj2 = new Conjunction(null, EMPTY_EXPRESSION);
        final BiOperandExpression unequalConj = new Conjunction(EMPTY_EXPRESSION, EMPTY_EXPRESSION);
        assertEquality(conj1, conj2, unequalConj, null);
        assertHashCode(conj1, conj2);
    }

    @Test
    public void sameNullValuesAreEqual3() {
        final BiOperandExpression conj1 = new Conjunction(null, null);
        final BiOperandExpression conj2 = new Conjunction(null, null);
        final BiOperandExpression unequalconj = new Conjunction(EMPTY_EXPRESSION, EMPTY_EXPRESSION);
        assertEquality(conj1, conj2, unequalconj, null);
    }
}
