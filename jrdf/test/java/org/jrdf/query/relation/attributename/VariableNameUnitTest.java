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
package org.jrdf.query.relation.attributename;

import com.gargoylesoftware.base.testing.EqualsTester;
import junit.framework.TestCase;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkConstructor;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal;
import static org.jrdf.util.test.FieldPropertiesTestUtil.checkFieldIsOfType;

import java.lang.reflect.Modifier;

/**
 * Stuff goes in here.
 *
 * @author Andrew Newman
 * @version $Id$
 */
public class VariableNameUnitTest extends TestCase {
    private static final String VARIABLE_FIELD_NAME = "variableName";
    private static final String VARIABLE_NAME_1 = "foo";
    private static final String VARIABLE_NAME_2 = "bar";

    public void testClassProperties() {
        checkImplementationOfInterfaceAndFinal(AttributeName.class, VariableName.class);
        ClassPropertiesTestUtil.checkMarkedAsSerializable(AttributeName.class);
        checkConstructor(VariableName.class, Modifier.PUBLIC, String.class);
        checkFieldIsOfType(VariableName.class, String.class, VARIABLE_FIELD_NAME);
    }

    public void testConstructor() {
        checkStandardConstructor(VARIABLE_NAME_1);
        checkStandardConstructor(VARIABLE_NAME_2);
    }

    public void testEquals() {
        VariableName original = new VariableName(VARIABLE_NAME_1);
        VariableName equal = new VariableName(VARIABLE_NAME_1);
        VariableName notEqual = new VariableName(VARIABLE_NAME_2);
        new EqualsTester(original, equal, notEqual, null);
    }

    private void checkStandardConstructor(String variableName) {
        AttributeName var = new VariableName(variableName);
        org.jrdf.util.test.ReflectTestUtil.checkFieldValue(var, VARIABLE_FIELD_NAME, variableName);
        assertEquals(variableName, var.getLiteral());
        assertEquals("?" + variableName, var.toString());
    }
}
