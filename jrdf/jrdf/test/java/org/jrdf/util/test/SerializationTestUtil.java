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

import com.gargoylesoftware.base.testing.TestUtil;
import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import org.jrdf.util.test.instantiate.ArnoldTheInstantiator;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Test utilities for checking serializability.
 *
 * @author Tom Adams
 * @version $Id$
 */
public final class SerializationTestUtil {

    private static final String FIELD_SERIAL_VERSION_UID = "serialVersionUID";
    private static final Class<Long> CLASS_LONG_PRIMITIVE = long.class;
    private static final boolean CHECK_BY_EQUALITY = false;

    public static void checkSerializability(Class<? extends Serializable> cls) {
        checkContainsSerialVersionUid(cls);
        canBeSerialized(cls);
    }

    public static void checkSerialialVersionUid(Class<?> cls, long expectedUid) {
        checkContainsSerialVersionUid(cls);
        checkSerialUidValue(cls, expectedUid);
    }

    // Note. This method will not attempt to serialize interfaces.
    public static void canBeSerialized(Class<?> cls) {
        if (canBeInstantiated(cls)) {
            checkSerialization(instantiate(cls));
        }
    }

    // Note. Re-throwing exceptions below as we lose the class that caused the problem.
    public static void checkSerialization(Object instanceToBeSerialized) {
        try {
            TestUtil.testSerialization(instanceToBeSerialized, CHECK_BY_EQUALITY);
        } catch (AssertionFailedError afe) {
            throw new AssertionFailedError("Class " + getClassName(instanceToBeSerialized) + ", " + afe.getMessage());
        } catch (IOException ioe) {
            throw new RuntimeException("Exception while checking serialization of " +
                getClassName(instanceToBeSerialized), ioe);
        }
    }

    private static void checkSerialUidValue(Class<?> cls, long expectedUid) {
        long actualUid = getLongFieldValue(cls, FIELD_SERIAL_VERSION_UID);
        Assert.assertEquals(expectedUid, actualUid);
    }

    // FIXME TJA: Do interfaces need serialVersionUID fields?
    private static void checkContainsSerialVersionUid(Class<?> cls) {
        FieldPropertiesTestUtil.checkContainsField(cls, FIELD_SERIAL_VERSION_UID);
        FieldPropertiesTestUtil.checkFieldPrivate(cls, FIELD_SERIAL_VERSION_UID);
        FieldPropertiesTestUtil.checkFieldStatic(cls, FIELD_SERIAL_VERSION_UID);
        FieldPropertiesTestUtil.checkFieldFinal(cls, FIELD_SERIAL_VERSION_UID);
        FieldPropertiesTestUtil.checkFieldIsOfType(cls, CLASS_LONG_PRIMITIVE, FIELD_SERIAL_VERSION_UID);
    }

    // FIXME TJA: Think about whether interfaces need a serialVersionUID
    private static boolean canBeInstantiated(Class<?> cls) {
        return !cls.isInterface() && !Modifier.isAbstract(cls.getModifiers());
    }

    private static String getClassName(Object ref) {
        return ref.getClass().getSimpleName();
    }

    private static Object instantiate(Class<?> cls) {
        return new ArnoldTheInstantiator().instantiate(cls);
    }

    public static long getLongFieldValue(Class<?> cls, String fieldName) {
        try {
            Field field = ReflectTestUtil.getField(cls, fieldName);
            field.setAccessible(true);
            return field.getLong(cls);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
