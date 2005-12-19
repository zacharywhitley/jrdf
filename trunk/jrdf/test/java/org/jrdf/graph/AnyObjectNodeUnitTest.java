package org.jrdf.graph;

import junit.framework.TestCase;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import static org.jrdf.util.test.ClassPropertiesTestUtil.NO_ARG_CONSTRUCTOR;
import org.jrdf.util.test.SerializationTestUtil;

import java.lang.reflect.Modifier;

/**
 * Test the properties of the AnyObjectNode.
 *
 * @author Andrew Newman
 * @version $Revision$
 */
public class AnyObjectNodeUnitTest extends TestCase {

    private static final String EXPECTED_TO_STRING = "ANY_OBJECT";

    public void testClassProperties() throws Exception {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(ObjectNode.class, AnyObjectNode.class);
        ClassPropertiesTestUtil.checkConstructor(AnyObjectNode.class, Modifier.PRIVATE, NO_ARG_CONSTRUCTOR);
    }

    public void testSerialVersionUid() {
        SerializationTestUtil.checkSerialialVersionUid(AnyObjectNode.class, 8654340032080018169L);
    }

    public void testToString() {
        assertEquals(EXPECTED_TO_STRING, AnyObjectNode.ANY_OBJECT_NODE.toString());
    }
}