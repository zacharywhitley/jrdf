/*
 * $Header$
 * $Revision: 439 $
 * $Date: 2006-01-27 06:19:29 +1000 (Fri, 27 Jan 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2006 The JRDF Project.  All rights reserved.
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

package org.jrdf.util.test;

import static org.jrdf.util.test.MockTestUtil.*;
import static org.jrdf.util.test.ReflectTestUtil.*;

import java.lang.reflect.InvocationTargetException;

/**
 * Tests the contract of a method or constructor so that the methods throw IllegalArgumentException if null is passed.
 */
public class ArgumentTestUtil {
    private static final String PARAMETER_CANNOT_BE_NULL = " cannot be null";

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

    public static void checkConstructorSetsFields(Class[] paramTypes, Class<?> clazz, String[] parameterNames) {
        Object[] args = createArgs(paramTypes, -1);
        ParamSpec spec = new ParamSpec(paramTypes, args);
        Object obj = createInstanceUsingConstructor(clazz, spec);
        for (int index = 0; index < parameterNames.length; index++) {
            checkFieldValue(obj, parameterNames[index], args[index]);
        }
    }

    public static void checkConstructNullAssertion(final Class<?> clazz, final Class[] paramTypes) {
        for (int index = 0; index < paramTypes.length; index++) {
            Object[] args = createArgs(paramTypes, index);
            String message = "Parameter " + (index+1) + PARAMETER_CANNOT_BE_NULL;
            final ParamSpec params = new ParamSpec(paramTypes, args);
            AssertThrows.assertThrows(IllegalArgumentException.class, message, new AssertThrows.Block() {
                public void execute() throws Throwable {
                    createInstanceAndRethrow(clazz, params);
                }
            });
        }
    }

    public static void checkMethodNullAssertions(final Object obj, final String methodName,
            final ParameterDefinition paramDefinition) {
        final Class[] parameterTypes = paramDefinition.getParameterTypes();
        String[] parameterNames = paramDefinition.getParameterNames();
        for (int index = 0; index < parameterTypes.length; index++) {
            checkMethod(parameterTypes, index, parameterNames, methodName, obj);
        }
    }

    // TODO (AN) Remove duplication with previous method.
    public static void checkMethodNullAssertions(final ParameterDefinition paramDefinition, final Object obj,
            final String methodName, final boolean[] checkParameter) {
        final Class[] parameterTypes = paramDefinition.getParameterTypes();
        String[] parameterNames = paramDefinition.getParameterNames();
        for (int index = 0; index < parameterTypes.length; index++) {
            if (checkParameter[index]) {
                checkMethod(parameterTypes, index, parameterNames, methodName, obj);
            }
        }
    }

    private static void checkMethod(final Class[] parameterTypes, int index, String[] parameterNames,
            final String methodName, final Object obj) {
        final Object[] args = createArgs(parameterTypes, index);
        String message = parameterNames[index] + PARAMETER_CANNOT_BE_NULL;
        AssertThrows.assertThrows(IllegalArgumentException.class, message, new AssertThrows.Block() {
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
            FieldPropertiesTestUtil.checkFieldIsOfTypePrivateAndFinal(clazz, fieldTypes[i], fieldNames[i]);
        }
    }

    private static void verifyFieldsPrivate(Class<?> clazz, Class[] fieldTypes, String[] fieldNames) {
        for (int i = 0; i < fieldNames.length; i++) {
            FieldPropertiesTestUtil.checkFieldIsOfTypeAndPrivate(clazz, fieldTypes[i], fieldNames[i]);
        }
    }
}
