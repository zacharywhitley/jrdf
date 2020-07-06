package org.jrdf.graph;

import static org.jrdf.util.test.ExceptionTestUtil.*;
import static org.jrdf.util.test.ClassPropertiesTestUtil.checkExtensionOf;
import org.junit.Test;

public class TripleFactoryExceptionUnitTest {
    private static final Class<TripleFactoryException> CLASS = TripleFactoryException.class;

    @Test
    public void finalRuntimeExceptionClassProperties() {
        testInheritableClassProperties(CLASS);
        checkExtensionOf(RuntimeException.class, CLASS);
    }

    @Test
    public void supportAllThreeExceptionConstructors() {
        testMessageConstructor(CLASS);
        testThrowableConstructor(CLASS);
        testMessageAndThrowableConstructor(CLASS);
    }
}
