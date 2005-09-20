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

import junit.framework.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public final class ClassPropertiesTestUtil {
    public static final Class[] NO_ARG_CONSTRUCTOR = (Class[]) null;

    private ClassPropertiesTestUtil() {}

    public static boolean isPublicInstance(Method method) {
        int modifiers = method.getModifiers();
        if (!Modifier.isPublic(modifiers)) return false;
        if (Modifier.isStatic(modifiers)) return false;
        return true;
    }

    public static boolean isClassAbstract(Class<?> cls) {
        return Modifier.isAbstract(cls.getModifiers());
    }

    public static boolean isClassFinal(Class<?> cls) {
        return Modifier.isFinal(cls.getModifiers());
    }

    public static boolean isClassPublic(Class<?> cls) {
        return Modifier.isPublic(cls.getModifiers());
    }

    public static boolean isClassAnInterface(Class<?> cls) {
        return Modifier.isInterface(cls.getModifiers());
    }

    public static boolean isImplementationOf(Class<?> targetInterface, Class<?> cls) {
        return isExtensionOf(targetInterface, cls);
    }

    public static boolean isExtensionOf(Class<?> superClass, Class<?> subclass) {
        return superClass.isAssignableFrom(subclass);
    }

    public static boolean isMethodFinal(Method method) {
        return Modifier.isFinal(method.getModifiers());
    }

    public static void checkImplementationOfInterfaceAndFinal(Class<?> targetInterface, Class implementationClass) {
        Assert.assertTrue(implementationClass.getSimpleName() + " is not an implementation of "
                + targetInterface.getSimpleName(), isImplementationOf(targetInterface, implementationClass));
        Assert.assertTrue(implementationClass.getSimpleName() + " must be final", isClassFinal(implementationClass));
    }

    public static void checkImplementationOfInterface(Class<?> targetInterface, Class implementationClass) {
        Assert.assertTrue(implementationClass.getSimpleName() + " is not an implementation of "
                + targetInterface.getSimpleName(), isImplementationOf(targetInterface, implementationClass));
    }

    public static void checkExtensionOf(Class<?> superClass, Class subClass) {
        Assert.assertTrue(subClass.getSimpleName() + " is not a subclass of " + superClass.getSimpleName(),
                isExtensionOf(superClass, subClass));
    }

    public static void checkClassFinal(Class<?> cls) {
        Assert.assertTrue(cls.getSimpleName() + " must be final", isClassFinal(cls));
    }

    public static void checkClassPublic(Class<?> cls) {
        Assert.assertTrue(cls.getSimpleName() + " must be public", isClassPublic(cls));
    }

    public static void checkInstanceExtendsClass(Class<?> superClass, Object ref) {
        Assert.assertNotNull(ref);
        checkExtensionOf(superClass, ref.getClass());
    }

    public static void checkInstanceImplementsInterface(Class<?> targetInterface, Object ref) {
        Assert.assertNotNull(ref);
        checkImplementationOfInterface(targetInterface, ref.getClass());
    }

    public static boolean isFieldFinal(Class<?> cls, String fieldName) {
        try {
            Field field = cls.getDeclaredField(fieldName);
            int modifiers = field.getModifiers();
            return Modifier.isFinal(modifiers);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isFieldStatic(Class<?> cls, String fieldName) {
        try {
            Field field = cls.getDeclaredField(fieldName);
            int modifiers = field.getModifiers();
            return Modifier.isStatic(modifiers);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void checkFieldFinal(Class<?> cls, String fieldName) {
        String mesg = "Field " + fieldName + " of class " + cls.getSimpleName() + " must be final";
        Assert.assertTrue(mesg, isFieldFinal(cls, fieldName));
    }

    public static void checkFieldStatic(Class<?> cls, String fieldName) {
        String mesg = "Field " + fieldName + " of class " + cls.getSimpleName() + " must be static";
        Assert.assertTrue(mesg, isFieldStatic(cls, fieldName));
    }

    public static void checkConstructor(Class cls, int expectedModifier, Class... parameters) {
        Constructor constructor = tryGetConstructor(cls, parameters);
        Assert.assertTrue(null != constructor);
        int constructorModifier = constructor.getModifiers();
        Assert.assertTrue("Expected modifier: " + Modifier.toString(expectedModifier) +
                " but was: " + Modifier.toString(constructorModifier), expectedModifier == constructorModifier);
    }

    private static Constructor tryGetConstructor(Class cls, Class... parameters) {
        try {
            return cls.getDeclaredConstructor(parameters);
        } catch (NoSuchMethodException e) {
            assertParameters(parameters);
            return null;
        }
    }

    private static void assertParameters(Class... parameters) {
        if (parameters != null) {
            Assert.fail("No such constructor found with types: " + Arrays.asList(parameters));
        } else {
            Assert.fail("No null constructor found");
        }
    }
}
