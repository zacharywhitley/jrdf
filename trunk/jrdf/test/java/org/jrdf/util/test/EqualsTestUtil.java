package org.jrdf.util.test;

import static org.jrdf.util.test.ClassPropertiesTestUtil.isClassFinal;
import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class EqualsTestUtil {
    public static void assertEquality(final Object original, final Object equalToOriginal,
            final Object sameClassButUnequal, final Object subclassWithSameValues) {
        checkParameters(original, equalToOriginal, sameClassButUnequal, subclassWithSameValues);
        //CHECKSTYLE:OFF
        assertFalse("original should not equal null", original.equals(null));
        assertFalse("original should not equal wrong class", original.equals(String.class));
        //CHECKSTYLE:ON
        assertTrue("original should equal itself", original.equals(original));
        assertEquals("original should be equal to equalToOriginal", original, equalToOriginal);
        assertInequality(original, sameClassButUnequal, "original", "sameClassButUnequal");
        assertInequality(original, subclassWithSameValues, "original", "subclassWithSameValues");
    }

    private static void checkParameters(Object original, Object equalToOriginal, Object sameClassButUnequal,
            Object subclassWithSameValues) {
        assertNotNull("original cannot be null", original);
        assertNotNull("equalToOriginal cannot not be null", equalToOriginal);
        assertTrue("original and equalToOriginal must be the same class",
                original.getClass() == equalToOriginal.getClass());

        if (sameClassButUnequal == null) {
            assertOriginalHasNoConstructorsOrSetters(original.getClass());
        } else {
            assertTrue("Original and SameClassButUnequal must be the same class",
                    original.getClass() == sameClassButUnequal.getClass());
        }

        if (isClassFinal(original.getClass())) {
            assertNull("subclassWithSameValues must be null as original is final", subclassWithSameValues);
        } else if (subclassWithSameValues == null) {
            fail("subclassWithSameValues for non-final class");
        }

        if (subclassWithSameValues != null) {
            assertTrue("subclassWithSameValues must not be the same as original",
                    original.getClass() != subclassWithSameValues);
        }
    }

    private static void assertInequality(Object obj1, Object obj2, String name1, String name2) {
        if (obj2 != null) {
            assertTrue(name1 + " equals " + name2, !obj1.equals(obj2));
            assertTrue(name2 + " equals " + name1, !obj2.equals(obj1));
        }
    }

    private static void assertOriginalHasNoConstructorsOrSetters(final Class clazz) {
        for (Constructor constructor : clazz.getConstructors()) {
            if (constructor.getParameterTypes().length != 0) {
                fail("sameClassButUnequal cannot be null because original has public constructor: " + constructor);
            }
        }
        for (Method method : clazz.getMethods()) {
            if (method.getName().startsWith("set")) {
                fail("sameClassButUnequal may not be null because original has public set methods: " + method);
            }
        }
    }
}
