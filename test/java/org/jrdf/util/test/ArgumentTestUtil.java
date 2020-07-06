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

package org.jrdf.util.test;

import static org.jrdf.util.test.AssertThrows.assertThrows;
import static org.jrdf.util.test.FieldPropertiesTestUtil.checkFieldIsOfTypeAndPrivate;
import static org.jrdf.util.test.FieldPropertiesTestUtil.checkFieldIsOfTypePrivateAndFinal;
import static org.jrdf.util.test.ReflectTestUtil.callMethod;
import static org.jrdf.util.test.ReflectTestUtil.checkFieldValue;
import static org.jrdf.util.test.ReflectTestUtil.createInstanceUsingConstructor;

import java.lang.reflect.InvocationTargetException;

/**
 * Tests the contract of a method or constructor so that the methods throw IllegalArgumentException if null is passed.
 */
public class ArgumentTestUtil {
    private static final String CANNOT_BE_NULL = " cannot be null";
    private static final String CANNOT_BE_EMPTY = " cannot be the empty string";

    public static void checkConstructorSetsFieldsAndFieldsPrivate(final Class<?> clazz, final Class[] paramTypes,
        final String[] parameterNames) {
        verifyFieldsPrivate(clazz, paramTypes, parameterNames);
        checkConstructorSetsFields(paramTypes, clazz, parameterNames);
    }

    public static void checkConstructorSetsFieldsAndFieldsPrivateFinal(final Class<?> clazz, final Class[] paramTypes,
        final String[] parameterNames) {
        verifyFieldsPrivateAndFinal(clazz, paramTypes, parameterNames);
        checkConstructorSetsFields(paramTypes, clazz, parameterNames);
    }

    public static <T> void checkConstructorSetsFields(Class[] paramTypes, Class<T> clazz, String[] parameterNames) {
        Object[] args = createArgs(paramTypes, -1);
        ParamSpec spec = new ParamSpec(paramTypes, args);
        T obj = createInstanceUsingConstructor(clazz, spec);
        for (int index = 0; index < parameterNames.length; index++) {
            String finalString;
            if (parameterNames[index].startsWith("new")) {
                finalString = removeNewPrefix(parameterNames[index]);
            } else {
                finalString = parameterNames[index];
            }
            checkFieldValue(obj, finalString, args[index]);
        }
    }

    public static void checkConstructNullAssertion(final Class<?> clazz, final Class... paramTypes) {
        for (int index = 0; index < paramTypes.length; index++) {
            if (!paramTypes[index].isPrimitive()) {
                String message = "Parameter " + (index + 1) + CANNOT_BE_NULL;
                Object[] args = createArgs(paramTypes, index);
                final ParamSpec params = new ParamSpec(paramTypes, args);
                assertThrows(IllegalArgumentException.class, message, new AssertThrows.Block() {
                    public void execute() throws Throwable {
                        createInstanceAndRethrow(clazz, params);
                    }
                });
            }
        }
    }

    public static void checkMethodNullAssertions(final Object obj, final String methodName,
        final ParameterDefinition paramDefinition) {
        int numberOfParams = paramDefinition.getParameterTypes().length;
        boolean[] checkParameter = new boolean[numberOfParams];
        for (int i = 0; i < checkParameter.length; i++) {
            checkParameter[i] = true;
        }
        checkMethodNullAssertions(obj, methodName, paramDefinition, checkParameter);
    }

    public static void checkMethodNullAndEmptyAssertions(final Object obj, final String methodName,
        final ParameterDefinition paramDefinition) {
        int numberOfParams = paramDefinition.getParameterTypes().length;
        boolean[] checkParameter = new boolean[numberOfParams];
        for (int i = 0; i < checkParameter.length; i++) {
            checkParameter[i] = true;
        }
        checkMethodNullAndEmptyStringAssertions(obj, methodName, paramDefinition, checkParameter);
    }

    private static void checkMethodNullAssertions(final Object obj, final String methodName,
        final ParameterDefinition paramDefinition, final boolean[] checkParameter) {
        final Class[] parameterTypes = paramDefinition.getParameterTypes();
        for (int index = 0; index < parameterTypes.length; index++) {
            if (checkParameter[index]) {
                final Object[] args = createArgs(parameterTypes, index);
                checkThrowsIllegalArgumentException("Parameter " + (index + 1) + CANNOT_BE_NULL, obj, methodName,
                    parameterTypes, args);
            }
        }
    }

    private static void checkMethodNullAndEmptyStringAssertions(Object obj, String methodName,
        ParameterDefinition paramDefinition, boolean[] checkParameter) {
        final Class[] parameterTypes = paramDefinition.getParameterTypes();
        for (int index = 0; index < parameterTypes.length; index++) {
            if (checkParameter[index]) {
                final Object[] args = createArgs(parameterTypes, index);
                if (parameterTypes[index].equals(String.class)) {
                    checkStringIllegalArgumentException(obj, methodName, paramDefinition, index, args);
                } else {
                    checkThrowsIllegalArgumentException("Parameter " + (index + 1) + CANNOT_BE_NULL, obj, methodName,
                        parameterTypes, args);
                }
            }
        }
    }

    private static void checkStringIllegalArgumentException(Object obj, String methodName,
        ParameterDefinition paramDefinition, int index, Object[] args) {
        final Class<?>[] parameterTypes = paramDefinition.getParameterTypes();
        String message = "Parameter " + (paramDefinition.getParameterNames()[index]);
        checkThrowsIllegalArgumentException(message + CANNOT_BE_NULL, obj, methodName, parameterTypes, args);
        args[index] = "";
        checkThrowsIllegalArgumentException(message + CANNOT_BE_EMPTY, obj, methodName, parameterTypes, args);
        args[index] = " ";
        checkThrowsIllegalArgumentException(message + CANNOT_BE_EMPTY, obj, methodName, parameterTypes, args);
    }

    private static void checkThrowsIllegalArgumentException(String message, final Object obj, final String methodName,
        final Class[] parameterTypes, final Object[] args) {
        assertThrows(IllegalArgumentException.class, message, new AssertThrows.Block() {
            public void execute() throws Throwable {
                callMethod(obj, methodName, parameterTypes, args);
            }
        });
    }

    private static void createInstanceAndRethrow(Class<?> clazz, ParamSpec params) {
        try {
            createInstanceUsingConstructor(clazz, params);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof InvocationTargetException) {
                throw (IllegalArgumentException) cause.getCause();
            }
        }
    }

    private static void verifyFieldsPrivateAndFinal(Class<?> clazz, Class[] fieldTypes, String[] fieldNames) {
        for (int i = 0; i < fieldNames.length; i++) {
            String finalString;
            if (fieldNames[i].startsWith("new")) {
                finalString = removeNewPrefix(fieldNames[i]);
                checkFieldIsOfTypePrivateAndFinal(clazz, fieldTypes[i], finalString);
            } else {
                finalString = fieldNames[i];
            }
            checkFieldIsOfTypePrivateAndFinal(clazz, fieldTypes[i], finalString);
        }
    }

    private static String removeNewPrefix(String fieldName) {
        String finalString;
        String name = fieldName.substring(3, fieldName.length());
        String lowCaseFirstChar = name.substring(0, 1).toLowerCase();
        finalString = lowCaseFirstChar + name.substring(1, name.length());
        return finalString;
    }

    private static void verifyFieldsPrivate(Class<?> clazz, Class[] fieldTypes, String[] fieldNames) {
        for (int i = 0; i < fieldNames.length; i++) {
            checkFieldIsOfTypeAndPrivate(clazz, fieldTypes[i], fieldNames[i]);
        }
    }

    private static Object[] createArgs(Class<?>[] parameterTypes, int index) {
        Object[] objects = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i != index) {
                objects[i] = MockTestUtil.createMock(parameterTypes[i]);
            }
        }
        return objects;
    }
}
