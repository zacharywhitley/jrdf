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

package org.jrdf.util.test;

import junit.framework.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Test utilities that use relection.
 *
 * @author Tom Adams
 * @author Andrew Newman
 * @version $Id$
 */
public final class ReflectTestUtil {

    private static final ParamSpec PARAMS_NONE = new ParamSpec();

    private ReflectTestUtil() {
    }

    public static Object getFieldValue(Object ref, String fieldName) {
        Field field = getField(ref.getClass(), fieldName);
        field.setAccessible(true);
        try {
            return field.get(ref);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void checkFieldValue(Object ref, String fieldName, Object expectedValue) {
        Object actual = getFieldValue(ref, fieldName);
        Assert.assertEquals("Field value in field: " + fieldName, expectedValue, actual);
    }

    public static void insertFieldValue(Object ref, String fieldName, Object fieldValue) {
        Field field = getField(ref.getClass(), fieldName);
        setFieldValue(ref, field, fieldValue);
    }

    public static Field[] getFields(Class<?> cls) {
        return cls.getDeclaredFields();
    }

    public static Field getField(Class<?> cls, String fieldName) {
        try {
            return cls.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(Class<T> cls) {
        return createInstanceUsingConstructor(cls, PARAMS_NONE);
    }

    public static <T> T createInstanceUsingConstructor(Class<T> cls, ParamSpec params) {
        Constructor<T> constructor = ClassPropertiesTestUtil.tryGetConstructor(cls, params);
        return invokeConstructor(cls, constructor, params);
    }

    public static Object callMethod(Object obj, String methodName, Class[] parameterTypes, Object... args)
        throws Throwable {
        Method method = tryCallMethod(obj, methodName, parameterTypes);
        return tryInvoke(method, obj, args);
    }

    private static Object tryInvoke(Method method, Object obj, Object... args) throws Throwable {
        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
                throw e.getCause();
            }
            throw e;
        }
    }

    private static void setFieldValue(Object ref, Field field, Object fieldValue) {
        field.setAccessible(true);
        try {
            field.set(ref, fieldValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T invokeConstructor(Class<T> cls, Constructor<T> constructor, ParamSpec params) {
        try {
            return constructor.newInstance(params.getParams());
        } catch (Exception e) {
            throw new RuntimeException("Invoking constructor for: " + cls, e);
        }
    }

    private static Method tryCallMethod(Object obj, String methodName, Class... parameterTypes) {
        try {
            return obj.getClass().getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
