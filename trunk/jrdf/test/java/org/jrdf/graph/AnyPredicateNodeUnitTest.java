package org.jrdf.graph;

import com.gargoylesoftware.base.testing.TestUtil;
import junit.framework.TestCase;
import org.jrdf.util.test.ClassPropertiesTestUtil;

/**
 * Test the properties of the AnyPredicateNode.
 *
 * @author Andrew Newman
 *
 * @version $Revision$
 */
public class AnyPredicateNodeUnitTest extends TestCase {
    public void testClassProperties() throws Exception {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(PredicateNode.class,
            AnyPredicateNode.class);
        TestUtil.testSerialization(AnyPredicateNode.ANY_PREDICATE_NODE, true);
    }
}