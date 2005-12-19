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
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.util.test.SerializationTestUtil;

import java.io.Serializable;
import java.lang.reflect.Modifier;

/**
 * Unit test for {@link DefaultVariable}.
 *
 * @author Tom Adams
 * @version $Revision$
 */
public final class DefaultVariableUnitTest extends TestCase {

    private static final String VARIABLE_NAME_1 = "title";
    private static final String VARIABLE_NAME_2 = "author";

    public void testClassProperties() {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(Serializable.class, DefaultVariable.class);
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(Variable.class, DefaultVariable.class);
        ClassPropertiesTestUtil.checkConstructor(DefaultVariable.class, Modifier.PUBLIC, String.class);
    }

    public void testConstants() {
        SerializationTestUtil.checkSerialialVersionUid(DefaultVariable.class, -2633694253531710240L);
    }

    public void testConstructorFailsWithNull() {
        try {
            new DefaultVariable(null);
            fail("new DefaultVariable(null) should have failed");
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testGetName() {
        checkName(VARIABLE_NAME_1);
        checkName(VARIABLE_NAME_2);
    }

    public void testEquals() {
//        checkNullComparisonObject();
//        checkReflexive();
//        checkDifferentClass();
//        checkSymmetric();
//        checkTransitive();
//        checkConsistentEquals();
//        checkSimpleEqual();
//        checkSimpleNotEqual();
    }

//    public void testHashCode() {
//        checkConsistentHashCode();
//        checkEqualObjectsReturnSameHashCode();
//    }

//    private void checkNullComparisonObject() {
//        checkNotEqual(EMPTY_ANSWER_1, null);
//    }

//    private void checkReflexive() {
//        checkSameValueSameReference();
//        checkSameValueDifferentReference();
//    }

//    private void checkDifferentClass() {
//        checkNotEqual(EMPTY_ANSWER_1, STRING_FORM_NO_ELEMENTS);
//    }

//    private void checkSymmetric() {
//        Answer x = new DefaultAnswer(createEmptyTripleList());
//        Answer y = new DefaultAnswer(createEmptyTripleList());
//        checkEqual(x, y);
//        checkEqual(y, y);
//    }

//    private void checkTransitive() {
//        Answer x = new DefaultAnswer(createEmptyTripleList());
//        Answer y = new DefaultAnswer(createEmptyTripleList());
//        Answer z = new DefaultAnswer(createEmptyTripleList());
//        checkEqual(x, y);
//        checkEqual(y, z);
//        checkEqual(x, z);
//    }

//    private void checkConsistentEquals() {
//        Answer x = new DefaultAnswer(createEmptyTripleList());
//        Answer y = new DefaultAnswer(createEmptyTripleList());
//        checkEqual(x, y);
//        checkEqual(x, y);
//    }

//    private void checkSameValueSameReference() {
//        Answer x = EMPTY_ANSWER_1;
//        Answer y = x;
//        checkEqual(x, y);
//    }

//    private void checkSameValueDifferentReference() {
//        Answer x = new DefaultAnswer(createEmptyTripleList());
//        Answer y = new DefaultAnswer(createEmptyTripleList());
//        checkEqual(x, y);
//    }

    private void checkName(String name) {
        Variable variable = new DefaultVariable(name);
        assertEquals(name, variable.getName());
    }
}
