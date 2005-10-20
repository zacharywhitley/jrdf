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

package org.jrdf.util.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Test utilities that use relection.
 * @author Tom Adams
 * @version $Id$
 */
public final class ReflectTestUtil {

    private static final ParamSpec PARAMS_NONE = new ParamSpec();

    private ReflectTestUtil() { }

    public static void insertFieldValue(Object ref, String fieldName, Object fieldValue) {
        Field field = getField(ref, fieldName);
        setFieldValue(ref, field, fieldValue);
    }

    public static Object newInstance(Class<?> cls) {
        return createInstanceUsingConstructor(cls, PARAMS_NONE);
    }

    public static Field getField(Class<?> cls, String fieldName) {
        try {
            return cls.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static long getLongFieldValue(Class<?> cls, String fieldName) {
        Field field = getField(cls, fieldName);
        try {
            field.setAccessible(true);
            return field.getLong(cls);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object createInstanceUsingConstructor(Class<?> cls, ParamSpec params) {
        Constructor<?> constructor = getConstructor(cls, params);
        return invokeConstructor(constructor, params);
    }

    private static Field getField(Object ref, String fieldName) {
        return getField(ref.getClass(), fieldName);
    }

    private static void setFieldValue(Object ref, Field field, Object fieldValue) {
        field.setAccessible(true);
        try {
            field.set(ref, fieldValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object invokeConstructor(Constructor<?> constructor, ParamSpec params) {
        try {
            return constructor.newInstance(params.getParams());
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Constructor<?> getConstructor(Class<?> cls, ParamSpec params) {
        try {
            Constructor<?> constructor = cls.getDeclaredConstructor(params.getTypes());
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static final class ParamSpec {

        private Object[] params;
        private Class[] types;

        public ParamSpec(Object ...  params) {
            this.params = params;
            setTypes(this.params);
        }

        public ParamSpec(Object[] params, Class[] types) {
            if (params.length != types.length) {
                throw new IllegalArgumentException("params and types arrays must be of same length");
            }
            this.params = params;
            this.types = types;
        }

        public Object[] getParams() {
            return params;
        }

        public Class[] getTypes() {
            return types;
        }

        private void setTypes(Object ... params) {
            types = guessParameterTypes(params);
        }

        private static Class<?>[] guessParameterTypes(Object ... params) {
            Class<?>[] types = new Class<?>[params.length];
            for (int i = 0; i < params.length; i++) {
                types[i] = params[i].getClass();
            }
            return types;
        }
    }
}
