package org.jrdf.util;

/**
 * A collection of methods that are used for equality testing.  The general pattern is:
 * 1/ Test if object (obj, the object passed in) is null,
 * 2/ Test if the object is the same reference,
 * 3/ Test if different class names (prevents subclasses from being equal) OR test if they have a different super class.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public final class EqualsUtil {

    private EqualsUtil() {
    }

    /**
     * Return true if the given object is null.
     *
     * @param obj the object to test  - who was passed into the equals method.
     * @return true if the object is null.
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * Return true if the two objects are of the same reference.
     *
     * @param thisObject the object to test - whose equals method we are in.
     * @param obj the object to test - who was passed into the equals method.
     * @return true if the two objects are of the same reference.
     */
    public static boolean sameReference(Object thisObject, Object obj) {
        return thisObject == obj;
    }

    /**
     * Returns true if the two objects have classes with different values (unequal).
     *
     * @param thisObject the object to test - whose equals method we are in.
     * @param obj the object to test - who was passed into the equals method.
     * @return true if the two object are are different classes.
     */
    public static boolean differentClasses(Object thisObject, Object obj) {
        return thisObject.getClass() != obj.getClass();
    }

    /**
     * Returns true if the given object can be cast to the superclass or interface.
     *
     * @param requiredClassOrInterface the required superclass or interface.
     * @param obj the object to test - who was passed into the equals method.
     * @return true if the given object can be cast to the superclass or interface.
     */
    public static boolean hasSuperClassOrInterface(Class<?> requiredClassOrInterface, Object obj) {
        return requiredClassOrInterface.isAssignableFrom(obj.getClass());
    }
}