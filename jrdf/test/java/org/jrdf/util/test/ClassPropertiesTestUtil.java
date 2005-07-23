package org.jrdf.util.test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import junit.framework.Assert;

public class ClassPropertiesTestUtil {

    public static boolean isPublicInstance(Method method) {
        int modifiers = method.getModifiers();
        if (!Modifier.isPublic(modifiers)) return false;
        if (Modifier.isStatic(modifiers)) return false;
        return true;
    }

    public static boolean isClassAbstract(Class cls) {
        return Modifier.isAbstract(cls.getModifiers());
    }

    public static boolean isClassFinal(Class cls) {
        return Modifier.isFinal(cls.getModifiers());
    }

    public static boolean isClassPublic(Class cls) {
        return Modifier.isPublic(cls.getModifiers());
    }

    public static boolean isClassAnInterface(Class cls) {
        return Modifier.isInterface(cls.getModifiers());
    }

    public static boolean isImplementationOf(Class targetInterface, Class cls) {
        return isExtensionOf(targetInterface, cls);
    }

    public static boolean isExtensionOf(Class<?> superClass, Class<?> subclass) {
        return superClass.isAssignableFrom(subclass);
    }

    public static boolean isMethodFinal(Method method) {
        return Modifier.isFinal(method.getModifiers());
    }

    public static void checkImplementationOfInterfaceAndFinal(Class targetInterface, Class implementationClass) {
        Assert.assertTrue(getShortName(implementationClass) + " is not an implementation of " + getShortName(targetInterface),
                isImplementationOf(targetInterface, implementationClass));
        Assert.assertTrue(getShortName(implementationClass) + " must be final", isClassFinal(implementationClass));
    }

    public static void checkExtensionOf(Class superClass, Class subClass) {
        Assert.assertTrue(getShortName(subClass) + " is not a subclass of " + getShortName(superClass), isExtensionOf(superClass, subClass));
    }

    public static void checkClassFinal(Class cls) {
        Assert.assertTrue(isClassFinal(cls));
    }

    public static void checkClassPublic(Class cls) {
        Assert.assertTrue(isClassPublic(cls));
    }

    public static void checkInstance(Class expectedImpl, Object ref) {
        Assert.assertNotNull(ref);
        Assert.assertTrue(isExtensionOf(expectedImpl, ref.getClass()));
    }

    private static String getShortName(Class cls) {
        String name = cls.getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }
}
