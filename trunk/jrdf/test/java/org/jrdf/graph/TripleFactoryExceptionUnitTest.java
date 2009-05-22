package org.jrdf.graph;

import junit.framework.TestCase;
import static org.jrdf.util.test.ExceptionTestUtil.*;
import static org.jrdf.util.test.ExceptionTestUtil.testMessageAndThrowableConstructor;

public class TripleFactoryExceptionUnitTest extends TestCase {
    private static final Class<TripleFactoryException> CLASS = TripleFactoryException.class;

    public void testClassProperties() {
        testInheritableClassProperties(CLASS);
        testIsRuntimeExcpetion(CLASS);
    }

    public void testConstructors() {
        testMessageConstructor(CLASS);
        testThrowableConstructor(CLASS);
        testMessageAndThrowableConstructor(CLASS);
    }
}
