package org.jrdf.graph;

import java.lang.reflect.Modifier;
import junit.framework.TestCase;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import static org.jrdf.util.test.ClassPropertiesTestUtil.NO_ARG_CONSTRUCTOR;
import org.jrdf.util.test.SerializationTestUtil;

/**
 * Test the properties of the AnyNode.
 * @author Andrew Newman
 * @author Tom Adams
 * @version $Id$
 */
public class AnyNodeUnitTest extends TestCase {

    private static final String EXPECTED_TO_STRING = "ANY";

    public void testClassProperties() throws Exception {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(Node.class, AnyNode.class);
        ClassPropertiesTestUtil.checkConstructor(AnyNode.class, Modifier.PRIVATE, NO_ARG_CONSTRUCTOR);
        SerializationTestUtil.checkSerialization(AnyObjectNode.ANY_OBJECT_NODE);
    }

    public void testToString() {
        assertEquals(EXPECTED_TO_STRING, AnyNode.ANY_NODE.toString());
    }
}