package org.jrdf.graph;

import com.gargoylesoftware.base.testing.TestUtil;
import junit.framework.TestCase;
import org.jrdf.util.test.ClassPropertiesTestUtil;

/**
 * Test the properties of the AnyNode.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public class AnyNodeUnitTest extends TestCase {
    public void testClassProperties() throws Exception {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(Node.class,
            AnyNode.class);
        TestUtil.testSerialization(AnyObjectNode.ANY_OBJECT_NODE, true);
    }
}