package org.jrdf.graph;

import junit.framework.TestCase;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import static org.jrdf.util.test.ClassPropertiesTestUtil.NO_ARG_CONSTRUCTOR;
import org.jrdf.util.test.SerializationTestUtil;

import java.lang.reflect.Modifier;

/**
 * Test the properties of the AnyNode.
 *
 * @author Andrew Newman
 * @author Tom Adams
 * @version $Id$
 */
public class AnyNodeUnitTest extends TestCase {

    private static final String EXPECTED_TO_STRING = "ANY";

    public void testClassProperties() throws Exception {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(Node.class, AnyNode.class);
        ClassPropertiesTestUtil.checkConstructor(AnyNode.class, Modifier.PRIVATE, NO_ARG_CONSTRUCTOR);
    }

    public void testSerialVersionUid() {
        SerializationTestUtil.checkSerialialVersionUid(AnyNode.class, -4846208755020186880L);
    }

    public void testToString() {
        assertEquals(EXPECTED_TO_STRING, AnyNode.ANY_NODE.toString());
    }
}