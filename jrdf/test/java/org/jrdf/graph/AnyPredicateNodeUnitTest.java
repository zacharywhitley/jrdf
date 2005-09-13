package org.jrdf.graph;

import com.gargoylesoftware.base.testing.TestUtil;
import junit.framework.TestCase;
import org.jrdf.util.test.ClassPropertiesTestUtil;

/**
 * Test the properties of the AnyPredicateNode.
 * @author Andrew Newman
 * @author Tom Adams
 * @version $Id$
 */
public class AnyPredicateNodeUnitTest extends TestCase {

    private static final String EXPECTED_TO_STRING = "ANY_PREDICATE";
    private static final boolean WHO_KNOWS_WHAT_THIS_MEANS = true;

    public void testClassProperties() throws Exception {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(PredicateNode.class,
                AnyPredicateNode.class);
        TestUtil.testSerialization(AnyPredicateNode.ANY_PREDICATE_NODE, WHO_KNOWS_WHAT_THIS_MEANS);
    }

    public void testToString() {
        assertEquals(EXPECTED_TO_STRING, AnyPredicateNode.ANY_PREDICATE_NODE.toString());
    }
}