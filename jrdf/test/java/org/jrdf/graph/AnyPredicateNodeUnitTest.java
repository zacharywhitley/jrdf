package org.jrdf.graph;

import junit.framework.TestCase;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import org.jrdf.util.test.SerializationTestUtil;
import static org.jrdf.util.test.ClassPropertiesTestUtil.NO_ARG_CONSTRUCTOR;
import java.lang.reflect.Modifier;

/**
 * Test the properties of the AnyPredicateNode.
 * @author Andrew Newman
 * @author Tom Adams
 * @version $Id$
 */
public class AnyPredicateNodeUnitTest extends TestCase {

    private static final String EXPECTED_TO_STRING = "ANY_PREDICATE";

    public void testClassProperties() throws Exception {
        ClassPropertiesTestUtil.checkImplementationOfInterfaceAndFinal(PredicateNode.class, AnyPredicateNode.class);
        ClassPropertiesTestUtil.checkConstructor(AnyPredicateNode.class, Modifier.PRIVATE, NO_ARG_CONSTRUCTOR);
        SerializationTestUtil.checkSerialization(AnyPredicateNode.ANY_PREDICATE_NODE);
    }

    public void testToString() {
        assertEquals(EXPECTED_TO_STRING, AnyPredicateNode.ANY_PREDICATE_NODE.toString());
    }
}