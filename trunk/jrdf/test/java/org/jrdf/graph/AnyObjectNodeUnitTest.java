package org.jrdf.graph;

import com.gargoylesoftware.base.testing.TestUtil;
import junit.framework.TestCase;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import static org.jrdf.util.test.ClassPropertiesTestUtil.NO_ARG_CONSTRUCTOR;

import java.lang.reflect.Modifier;

/**
 * Test the properties of the AnyObjectNode.
 * @author Andrew Newman
 * @version $Revision$
 */
public class AnyObjectNodeUnitTest extends TestCase {

    private static final String EXPECTED_TO_STRING = "ANY_OBJECT";
    private static final boolean WHO_KNOWS_WHAT_THIS_MEANS = true;

    public void testClassProperties() throws Exception {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(ObjectNode.class, AnyObjectNode.class);
        ClassPropertiesTestUtil.checkConstructor(AnyObjectNode.class, Modifier.PRIVATE, NO_ARG_CONSTRUCTOR);
        TestUtil.testSerialization(AnyObjectNode.ANY_OBJECT_NODE, WHO_KNOWS_WHAT_THIS_MEANS);
    }

    public void testToString() {
        assertEquals(EXPECTED_TO_STRING, AnyObjectNode.ANY_OBJECT_NODE.toString());
    }
}