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


package org.jrdf.util.test;

import junit.framework.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Allows tests to be made of fields of classes.
 *
 * @author Tom Adams
 * @author Andrew Newman
 * @version $Revision$
 */
public class FieldPropertiesTestUtil {
    public static boolean containsField(Class<?> cls, String fieldName) {
        try {
            cls.getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    public static void checkContainsField(Class<?> cls, String fieldName) {
        Assert.assertTrue(cls.getName() + " must contain the field " + fieldName, containsField(cls, fieldName));
    }

    public static boolean isFieldFinal(Class<?> cls, String fieldName) {
        return hasModifier(cls, fieldName, Modifier.FINAL);
    }

    public static void checkFieldFinal(Class<?> cls, String fieldName) {
        checkFieldHasModifier(cls, fieldName, Modifier.FINAL);
    }

    public static boolean isFieldStatic(Class<?> cls, String fieldName) {
        return hasModifier(cls, fieldName, Modifier.STATIC);
    }

    public static void checkFieldStatic(Class<?> cls, String fieldName) {
        checkFieldHasModifier(cls, fieldName, Modifier.STATIC);
    }

    public static void checkFieldPrivate(Class<?> cls, String fieldName) {
        checkFieldHasModifier(cls, fieldName, Modifier.PRIVATE);
    }

    public static void checkFieldPublicConstant(Class<?> cls, String fieldName) {
        checkFieldHasModifier(cls, fieldName, Modifier.PUBLIC);
        checkFieldHasModifier(cls, fieldName, Modifier.STATIC);
        checkFieldHasModifier(cls, fieldName, Modifier.FINAL);
    }

    public static void checkFieldPrivateConstant(Class<?> cls, String fieldName) {
        checkFieldHasModifier(cls, fieldName, Modifier.PRIVATE);
        checkFieldHasModifier(cls, fieldName, Modifier.STATIC);
        checkFieldHasModifier(cls, fieldName, Modifier.FINAL);
    }

    public static boolean isFieldOfType(Class<?> clazz, Class<?> expectedType, String fieldName) {
        Field field = ReflectTestUtil.getField(clazz, fieldName);
        return field.getType().equals(expectedType);
    }

    public static void checkFieldIsOfType(Class<?> clazz, Class<?> expectedType, String fieldName) {
        Assert.assertTrue("Field " + fieldName + " of class " + clazz.getSimpleName() + " must be of type " +
            expectedType.getSimpleName(), isFieldOfType(clazz, expectedType, fieldName));
    }

    public static void checkFieldIsOfTypeAndPrivate(Class<?> clazz, Class<?> expectedType, String fieldName) {
        checkFieldIsOfType(clazz, expectedType, fieldName);
        checkFieldPrivate(clazz, fieldName);
    }

    public static void checkFieldIsOfTypePrivateAndFinal(Class<?> clazz, Class<?> expectedType, String fieldName) {
        checkFieldIsOfType(clazz, expectedType, fieldName);
        checkFieldPrivate(clazz, fieldName);
        checkFieldFinal(clazz, fieldName);
    }

    private static void checkFieldHasModifier(Class<?> clazz, String fieldName, int modifier) {
        boolean hasModifier = hasModifier(clazz, fieldName, modifier);
        Assert.assertTrue("Field " + fieldName + " of class " + clazz.getSimpleName() + " must have modifier " +
            Modifier.toString(modifier), hasModifier);
    }

    private static boolean hasModifier(Class<?> clazz, String fieldName, int modifier) {
        Field field = ReflectTestUtil.getField(clazz, fieldName);
        return (field.getModifiers() & modifier) != 0;
    }
}
