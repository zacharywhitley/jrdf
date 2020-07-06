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

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Tests ther properties of the a class - final, abstract, etc.
 *
 * @author Tom Adams
 * @author Andrew Newman
 * @version $Id$
 */
public final class ClassPropertiesTestUtil {

    public static final Class[] NO_ARG_CONSTRUCTOR = {};
    public static final int NO_MODIFIER = 0;

    private ClassPropertiesTestUtil() {
    }

    public static boolean isPublicInstance(Method method) {
        int modifiers = method.getModifiers();
        return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers);
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
        Assert.assertTrue(implementationClass.getSimpleName() + " is not an implementation of " +
            targetInterface.getSimpleName(), isImplementationOf(targetInterface, implementationClass));
        Assert.assertTrue(implementationClass.getSimpleName() + " must be final", isClassFinal(implementationClass));
    }

    public static void checkImplementationOfInterface(Class<?> targetInterface, Class implementationClass) {
        Assert.assertTrue(implementationClass.getSimpleName() + " is not an implementation of " +
            targetInterface.getSimpleName(), isImplementationOf(targetInterface, implementationClass));
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

    public static boolean isDirectlyMarkedAsSerializable(Class<?> cls) {
        Class[] interfaces = cls.getInterfaces();
        for (Class actualInterface : interfaces) {
            if (actualInterface.equals(Serializable.class)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isImplicitlyMarkedAsSerializable(Class<?> cls) {
        Collection<Class<?>> parentClasses = new ArrayList<Class<?>>();
        parentClasses.addAll(findParentClasses(cls));
        for (Class<?> parent : parentClasses) {
            if (parent.equals(Serializable.class)) {
                return true;
            }
        }
        return false;
    }

    public static void checkMarkedAsSerializable(Class<?> cls) {
        Assert.assertTrue(cls.getSimpleName() + " implement/extend java.io.Serializable",
            isDirectlyMarkedAsSerializable(cls));
    }

    public static void checkConstructor(Class cls, int expectedModifier, Class... parameters) {
        Constructor constructor = tryGetConstructor(cls, parameters);
        Assert.assertTrue(null != constructor);
        int constructorModifier = constructor.getModifiers();
        Assert.assertTrue("Expected modifier: " + Modifier.toString(expectedModifier) + "(" + expectedModifier + ")" +
            " but was: " + Modifier.toString(constructorModifier) + "(" + constructorModifier + ")",
            expectedModifier == constructorModifier);
    }

    public static <T> Constructor<T> tryGetConstructor(Class<T> cls, ParamSpec params) {
        Constructor constructor = tryGetConstructor(cls, params.getTypes());
        constructor.setAccessible(true);
        return constructor;
    }

    public static Constructor tryGetConstructor(Class<?> cls, Class... parameters) {
        try {
            return cls.getDeclaredConstructor(parameters);
        } catch (NoSuchMethodException e) {
            assertParameters(cls, parameters);
            throw new RuntimeException(e);
        }
    }

    public static Collection<? extends Class<?>> findParentClasses(Class<?> cls) {
        Collection<Class<?>> parentClasses = new ArrayList<Class<?>>();
        Class[] parents = cls.getInterfaces();
        for (Class parent : parents) {
            parentClasses.addAll(findParentClasses(parent));
        }
        Class<?> superclass = cls.getSuperclass();
        if (null != superclass && !superclass.equals(Object.class)) {
            parentClasses.addAll(findParentClasses(superclass));
        }

        parentClasses.add(cls);
        return parentClasses;
    }

    private static void assertParameters(Class cls, Class... parameters) {
        if (parameters != null) {
            Assert.fail("No constructor for: " + cls + " found with types: " + Arrays.asList(parameters));
        } else {
            Assert.fail("No nullary constructor found for: " + cls);
        }
    }
}
