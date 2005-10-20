package org.jrdf.graph;

import java.lang.reflect.Modifier;
import junit.framework.TestCase;
import org.jrdf.util.test.ClassPropertiesTestUtil;
import static org.jrdf.util.test.ClassPropertiesTestUtil.NO_ARG_CONSTRUCTOR;
import org.jrdf.util.test.SerializationTestUtil;

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
    }

    public void testSerialVersionUid() {
        SerializationTestUtil.checkSerialialVersionUid(AnyPredicateNode.class, 1764088613140821732L);
    }

    public void testToString() {
        assertEquals(EXPECTED_TO_STRING, AnyPredicateNode.ANY_PREDICATE_NODE.toString());
    }
}